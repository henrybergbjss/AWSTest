package rollsPOC2.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceAsync;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceAsyncClient;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.rabbitmq.client.ConnectionFactory;

import rollsPOC2.tasks.Task;

/**
 * 
 * A simple place to stash the globals
 *
 */
public class AppServices {
	private static ThreadPoolExecutor mainThreadPool;
	private static SessionFactory sessionFactory;
	private static ConnectionFactory rabbitConnectionFactory;
	private static DirectedAcyclicGraph<Task, DefaultEdge> dag;
	private static StandardServiceRegistry registry;

	public static ThreadPoolExecutor getMainThreadPool()
	{
		if(mainThreadPool == null)
		{
			mainThreadPool = new ThreadPoolExecutor(10, 50, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));
		}

		return mainThreadPool;
	}

	public static SessionFactory getSessionFactory()
	{
		if(sessionFactory == null)
		{
			// A SessionFactory is set up once for an application!
			registry = new StandardServiceRegistryBuilder()
					.configure() // configures settings from hibernate.cfg.xml
					.build();
			try {
				sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
			}
			catch (Exception e) {
				// The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
				// so destroy it manually.
				e.printStackTrace();
				StandardServiceRegistryBuilder.destroy( registry );
			}			
		}
		
		return sessionFactory;
	}
	
	public static void shutdownHibernate()
	{
        if(sessionFactory != null)
        {
            sessionFactory.close();
        }
        
	    if(registry != null)
	    {
	        StandardServiceRegistryBuilder.destroy( registry );
	    }
	}
	
	public static ConnectionFactory getRabbitConnectionFactory()
	{
		if(rabbitConnectionFactory == null)
		{
			rabbitConnectionFactory = new ConnectionFactory();
			rabbitConnectionFactory.setHost("prod.demo.com");
			rabbitConnectionFactory.setUsername("test");
			rabbitConnectionFactory.setPassword("test");
		}
		
		return rabbitConnectionFactory;
	}
	
	public static AWSCredentials getAWSCredentials()
	{
        try
        {
        	return new ProfileCredentialsProvider("default").getCredentials();
        }
        catch (Exception e)
        {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Henry Berg\\.aws\\credentials), and is in valid format.",
                    e);
        }
	}

	public static AmazonS3 getS3Client()
	{
		AWSCredentials credentials = getAWSCredentials();
		AmazonS3 s3 = new AmazonS3Client(credentials);
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		s3.setRegion(usEast1);
		
		return s3;
	}
	
	public static AmazonElasticMapReduceAsync getEMRAsyncClient()
	{
        AmazonElasticMapReduceAsync emr = new AmazonElasticMapReduceAsyncClient(getAWSCredentials());
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        emr.setRegion(usEast1);

        return emr;
	}
	
	public static AmazonElasticMapReduce getEMRClient()
	{
        AmazonElasticMapReduce emr = new AmazonElasticMapReduceClient(getAWSCredentials());
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        emr.setRegion(usEast1);

        return emr;
	}
	
	public static AmazonEC2Client getEC2Client()
	{
		AmazonEC2Client ec2 = new AmazonEC2Client(getAWSCredentials());
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        ec2.setRegion(usEast1);

        return ec2;
	}
	
	public static AmazonRDSClient getRDSClient()
	{
		AmazonRDSClient rds = new AmazonRDSClient(getAWSCredentials());
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        rds.setRegion(usEast1);

        return rds;
	}
}
