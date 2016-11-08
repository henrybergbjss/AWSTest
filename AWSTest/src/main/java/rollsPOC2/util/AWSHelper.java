package rollsPOC2.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.model.Application;
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary;
import com.amazonaws.services.elasticmapreduce.model.JobFlowInstancesConfig;
import com.amazonaws.services.elasticmapreduce.model.ListClustersResult;
import com.amazonaws.services.elasticmapreduce.model.ListStepsRequest;
import com.amazonaws.services.elasticmapreduce.model.ListStepsResult;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowRequest;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowResult;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.model.StepSummary;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;

public class AWSHelper
{
	public static String createOrFindEMRHiveCluster(String clusterName, boolean createWithKeepAlive) throws Exception
	{
	    String clusterId = null;
		AmazonElasticMapReduce emr = AppServices.getEMRClient();
        ClusterSummary clusterSummary = findCluster("Treebeard", emr);
        if(clusterSummary != null)
        {
        	clusterId = clusterSummary.getId();
			System.err.printf("Cluster found with id %s, status %s\n", clusterId, clusterSummary.getStatus().getState());
        }
        
        if(clusterSummary != null && clusterSummary.getStatus().getState().startsWith("TERMINAT"))
        {
        	while(findCluster("Treebeard", emr).getStatus().getState().equals("TERMINATING"))
        	{
        		System.out.println("Waiting for previous cluster to terminate");
        		Thread.sleep(10000l);
        	}
        	
        	System.out.println("Starting cluster...");
    	    StepFactory stepFactory = new StepFactory();
	
		    StepConfig enabledebugging = new StepConfig()
		       .withName("Enable debugging")
		       .withActionOnFailure("TERMINATE_JOB_FLOW")
		       .withHadoopJarStep(stepFactory.newEnableDebuggingStep());

//		    Possibly redundant with ".withApplications(new Application().withName("Hive"))"
//		    StepConfig installHive = new StepConfig()
//		       .withName("Install Hive")
//		       .withActionOnFailure("TERMINATE_JOB_FLOW")
//		       .withHadoopJarStep(stepFactory.newInstallHiveStep());
	
		    RunJobFlowRequest request = new RunJobFlowRequest()
		    	       .withName("Treebeard")
		    	       .withReleaseLabel("emr-4.6.0")
		    	       .withApplications(new Application().withName("Hive"))
		    	       .withSteps(enabledebugging)
		    	       .withVisibleToAllUsers(true)
		    	       .withLogUri("s3://aws-logs-800327301943-us-east-1/elasticmapreduce/")
		    	       .withServiceRole("EMR_DefaultRole")
		    	       .withJobFlowRole("EMR_EC2_DefaultRole")
		    	       .withInstances(new JobFlowInstancesConfig()
		    	           .withEc2KeyName("bjss")
		    	           .withInstanceCount(2)
		    	           .withMasterInstanceType("m3.xlarge")
		    	           .withSlaveInstanceType("m1.large")
		    	           .withKeepJobFlowAliveWhenNoSteps(createWithKeepAlive)
		    	           );
	
		    RunJobFlowResult createClusterResult = emr.runJobFlow(request);
		    clusterId = createClusterResult.getJobFlowId();
		    System.out.printf("Started cluster with id %s\n", clusterId);
        }
        
        return clusterId;
	}
	
	public static ClusterSummary findCluster(String clusterName, AmazonElasticMapReduce emr)
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
	
	public static Instance createOrFindEC2Instance(String instanceName) throws Exception
	{
		Instance instance = findEC2Instance(instanceName);
		if(instance == null || instance.getState().getName().equals("terminated") || instance.getState().getName().equals("shutting-down"))
		{
        	while(instance != null && instance.getState().getName().equals("shutting-down"))
        	{
        		System.out.println("Waiting for previous EC2 instance to terminate");
        		Thread.sleep(10000l);
        		instance = findEC2Instance(instanceName);
        	}
        	String userDataScript = Base64.getUrlEncoder().encodeToString(Files.readAllBytes(Paths.get(instanceName.getClass().getResource("/scripts/postinstall-script.sh").toURI())));

        	AmazonEC2Client ec2 = AppServices.getEC2Client();
			RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
					.withKeyName("bjss")
					.withImageId("ami-2d39803a")
					.withUserData(userDataScript)
					.withMinCount(1)
					.withMaxCount(1)
					.withInstanceType("t2.small")
					.withSecurityGroupIds("IPAASDemo")
					;
			
			RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
			String instanceId = runInstancesResult.getReservation().getInstances().get(0).getInstanceId();
			CreateTagsRequest createTagsRequest = new CreateTagsRequest()
					.withResources(instanceId)
					.withTags(new Tag("Name", "IPAASDemo"))
					;
			ec2.createTags(createTagsRequest);
			instance = findEC2Instance(instanceName);
        	while(instance != null && instance.getState().getName().equals("pending"))
        	{
        		System.out.println("Waiting for EC2 instance to start");
        		Thread.sleep(10000l);
        		instance = findEC2Instance(instanceName);
        	}
		}
		
		return findEC2Instance(instanceName);
	}
	
	public static Instance findEC2Instance(String instanceName)
	{
		AmazonEC2Client ec2 = AppServices.getEC2Client();
		DescribeInstancesResult result = ec2.describeInstances();
		for(Reservation reservation : result.getReservations())
        {
        	for(Instance instance : reservation.getInstances())
        	{
        		for(Tag tag : instance.getTags())
        		{
        			if(tag.getKey().equals("Name") && instanceName.equals(tag.getValue()))
        			{
        				return instance;
        			}
        		}
        	}
        }
		
		return null;
	}
	
	public static DBInstance findRDSInstance(String instanceName)
	{
		AmazonRDSClient rds = AppServices.getRDSClient();
		DescribeDBInstancesResult result = rds.describeDBInstances();
    	for(DBInstance instance : result.getDBInstances())
    	{
			if(instance.getDBName().equals(instanceName))
			{
				return instance;
			}
        }
		
		return null;
	}
	
	public static StepSummary findEMRStep(String clusterId, String stepName)
	{
		AmazonElasticMapReduce emr = AppServices.getEMRClient();
	    ListStepsResult listStepsResponse = emr.listSteps(new ListStepsRequest().withClusterId(clusterId));
	    for(StepSummary step : listStepsResponse.getSteps())
	    {
	    	if(step.getName().equals(stepName))
	    	{
	    		return step;
	    	}
	    }
	    
	    return null;
	}
	
	public static DBInstance createOrFindRDSInstance(String instanceName) throws Exception
	{
		DBInstance instance = findRDSInstance(instanceName);
		
		if(instance == null)
		{
			AmazonRDSClient client = AppServices.getRDSClient();
			CreateDBInstanceRequest request = new CreateDBInstanceRequest()
					.withDBName(instanceName)
					.withLicenseModel("postgresql-license")
					.withEngine("Postgres")
					.withEngineVersion("9.5.2")
					.withDBInstanceClass("db.t2.small")
					.withMultiAZ(false)
					.withAllocatedStorage(5)
//					.withStorageType("Magnetic")
					.withDBInstanceIdentifier(instanceName)
					.withMasterUsername("postgres")
					.withMasterUserPassword("postgres")
					.withStorageEncrypted(false)
//					.withDBSecurityGroups("IPAASDemo")
					;
			
			instance = client.createDBInstance(request);
		}

		return instance;
	}
}
