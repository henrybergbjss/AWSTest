import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary;
import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;
import com.amazonaws.services.elasticmapreduce.model.ListClustersResult;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;
import com.amazonaws.services.rds.model.DBInstance;

import rollsPOC2.util.AWSHelper;

public class AWSTest {

	public static void main(String[] args) throws Exception
	{
		new AWSTest();
	}
	
	public AWSTest() throws Exception
	{
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Henry Berg\\.aws\\credentials), and is in valid format.",
                    e);
        }
        
        Instance instance = AWSHelper.createOrFindEC2Instance("IPAASDemo");
        DBInstance dbInstance = AWSHelper.createOrFindRDSInstance("IPAASDemo");
        System.out.println("Done?  I guess.");
        
//        AmazonElasticMapReduce emr = new AmazonElasticMapReduceClient(credentials);
//        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
//        emr.setRegion(usEast1);
//	    String clusterId = null;
//        ClusterSummary clusterSummary = findCluster("Treebeard", emr);
//        if(clusterSummary != null)
//        {
//        	clusterId = clusterSummary.getId();
//			System.err.printf("Cluster found with id %s, status %s\n", clusterId, clusterSummary.getStatus().getState());
//        }
//        
//	    StepFactory stepFactory = new StepFactory();
//
//        if(clusterSummary != null && clusterSummary.getStatus().getState().startsWith("TERMINAT"))
//        {
//        	while(findCluster("Treebeard", emr).getStatus().getState().equals("TERMINATING"))
//        	{
//        		System.out.println("Waiting for previous cluster to terminate");
//        		Thread.sleep(10000l);
//        	}
//        	
//        	System.out.println("Starting cluster...");
//	
//		    StepConfig enabledebugging = new StepConfig()
//		       .withName("Enable debugging")
//		       .withActionOnFailure("TERMINATE_JOB_FLOW")
//		       .withHadoopJarStep(stepFactory.newEnableDebuggingStep());
//	
//		    StepConfig installHive = new StepConfig()
//		       .withName("Install Hive")
//		       .withActionOnFailure("TERMINATE_JOB_FLOW")
//		       .withHadoopJarStep(stepFactory.newInstallHiveStep());
//	
//		    RunJobFlowRequest request = new RunJobFlowRequest()
//		    	       .withName("Treebeard")
//		    	       .withReleaseLabel("emr-4.6.0")
//		    	       .withApplications(new Application().withName("Hive"))
//		    	       .withSteps(enabledebugging)
//		    	       .withVisibleToAllUsers(true)
//		    	       .withLogUri("s3://aws-logs-800327301943-us-east-1/elasticmapreduce/")
//		    	       .withServiceRole("EMR_DefaultRole")
//		    	       .withJobFlowRole("EMR_EC2_DefaultRole")
//		    	       .withInstances(new JobFlowInstancesConfig()
//		    	           .withEc2KeyName("bjss")
//		    	           .withInstanceCount(2)
//		    	           .withKeepJobFlowAliveWhenNoSteps(true)
//		    	           .withMasterInstanceType("m3.xlarge")
//		    	           .withSlaveInstanceType("m1.large")
//		    	           .withKeepJobFlowAliveWhenNoSteps(false)
//		    	           );
//	
//		    RunJobFlowResult result = emr.runJobFlow(request);
//		    clusterId = result.getJobFlowId();
//		    System.out.printf("Started cluster with id %s\n", clusterId);
//        }
//        
//	    StepConfig createDDLTableStep = getCreateDDLTableStep(stepFactory);
//	    StepConfig runHiveQueryStep = getRunQueryStep(stepFactory);
//
//		AddJobFlowStepsResult addJobFlowStepsResult = emr.addJobFlowSteps(new AddJobFlowStepsRequest()
//	    		.withJobFlowId(clusterId)
//	    		.withSteps(createDDLTableStep, runHiveQueryStep));
//	    
//	    System.out.println("Added steps: " + addJobFlowStepsResult.getStepIds());
	}

	private StepConfig getRunQueryStep(StepFactory stepFactory) {
		// Run query
	    HadoopJarStepConfig runHiveQuery = stepFactory.newRunHiveScriptStep("s3://bjss-nyc-dev/query/query.ddl", "-d INPUT=s3://bjss-nyc-dev/data/input/inputtemp.txt -d OUTPUT=null -i s3://bjss-nyc-dev/data/input/inputtemp.txt");
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
	
	private ClusterSummary findCluster(String clusterName, AmazonElasticMapReduce emr)
	{
		ListClustersResult clusters = emr.listClusters();
		
		for(ClusterSummary clusterSummary : clusters.getClusters())
		{
			if(clusterSummary.getName().equals(clusterName))
			{
				return clusterSummary;
			}
		}
		
		return null;
	}
}
