package rollsPOC2.tasks;

import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.model.AddJobFlowStepsRequest;
import com.amazonaws.services.elasticmapreduce.model.AddJobFlowStepsResult;
import com.amazonaws.services.elasticmapreduce.model.DescribeStepRequest;
import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;
import com.amazonaws.services.elasticmapreduce.model.ListStepsRequest;
import com.amazonaws.services.elasticmapreduce.model.ListStepsResult;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.model.StepSummary;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;

import rollsPOC2.util.AWSHelper;
import rollsPOC2.util.AppServices;

public class FlightTimeHive extends Task
{
	public FlightTimeHive(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public Boolean call() throws Exception
	{
		AmazonElasticMapReduce emr = AppServices.getEMRClient();
	    String clusterId = AWSHelper.createOrFindEMRHiveCluster("Treebeard", true);
	    StepFactory stepFactory = new StepFactory();
	    
	    StepConfig createDDLTableStep = getCreateDDLTableStep(stepFactory);
        StepSummary hasDDLTableBeenCreated = AWSHelper.findEMRStep(clusterId, createDDLTableStep.getName());
        if(hasDDLTableBeenCreated == null || !hasDDLTableBeenCreated.getStatus().getState().equals("COMPLETED"))
        {
        	System.out.println("Running table DDL job...");

        	AddJobFlowStepsResult addJobFlowStepsResult = emr.addJobFlowSteps(new AddJobFlowStepsRequest()
    	    		.withJobFlowId(clusterId)
    	    		.withSteps(createDDLTableStep));
        	
    	    System.out.println("Table DDL job submitted with steps: " + addJobFlowStepsResult.getStepIds());
        }

        StepConfig runHiveQueryStep = getRunQueryStep(stepFactory);

    	System.out.println("Starting Hive job...");

    	AddJobFlowStepsResult addJobFlowStepsResult = emr.addJobFlowSteps(new AddJobFlowStepsRequest()
	    		.withJobFlowId(clusterId)
	    		.withSteps(runHiveQueryStep));
	    
	    System.out.println("Hive job submitted with steps: " + addJobFlowStepsResult.getStepIds());
	    
	    boolean stillWaiting = true;
	    while(stillWaiting)
	    {
	    	stillWaiting = false;
		    ListStepsResult listStepsResponse = emr.listSteps(new ListStepsRequest().withClusterId(clusterId).withStepIds(addJobFlowStepsResult.getStepIds()));
		    for(StepSummary step : listStepsResponse.getSteps())
		    {
		        if(step.getStatus().getState().equals("FAILED"))
		        {
		            throw new RuntimeException("Hive job failed");
		        }
		        else if(!step.getStatus().getState().equals("COMPLETED"))
		    	{
		    		stillWaiting = true;
		    		System.out.println("Waiting for Hive job to finish");
		    	}
		    }
		    
		    Thread.sleep(10000);
	    }
	    
	    System.out.println("Hive job finished.");

        setSuccess(true);
        return true;
	}

	private StepConfig getRunQueryStep(StepFactory stepFactory) {
		// Run query
	    HadoopJarStepConfig runHiveQuery = stepFactory.newRunHiveScriptStep("s3://bjss-nyc-dev/query/query.ddl", "-d","INPUT=s3://bjss-nyc-dev/data/input/inputtemp.txt","-d","OUTPUT=null","-i","s3://bjss-nyc-dev/data/input/inputtemp.txt");
	    StepConfig runHiveQueryStep = new StepConfig("RunQuery", runHiveQuery).withActionOnFailure("CONTINUE");
	    
		return runHiveQueryStep;
	}

	private StepConfig getCreateDDLTableStep(StepFactory stepFactory)
	{
		// Create DDL table
	    StepConfig createDDLTableStep = new StepConfig()
	    		.withName("CreateTable")
	    		.withHadoopJarStep(stepFactory.newRunHiveScriptStep("s3://bjss-nyc-dev/scripts/CreateOnTimeTable.ddl", "-d OUTPUT=s3://bjss-nyc-dev/logs"))
	    		.withActionOnFailure("CONTINUE");
	    
		return createDDLTableStep;
	}
}
