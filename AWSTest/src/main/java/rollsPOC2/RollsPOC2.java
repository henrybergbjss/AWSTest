package rollsPOC2;

import java.util.concurrent.ThreadPoolExecutor;

import rollsPOC2.tasks.ComputeFlightTimes;
import rollsPOC2.tasks.FlightsToRabbit;
import rollsPOC2.tasks.GenericTask;
import rollsPOC2.tasks.RabbitToS3;
import rollsPOC2.tasks.SendEmail;
import rollsPOC2.tasks.Task;
import rollsPOC2.tasks.WeatherToRabbit;
import rollsPOC2.util.AWSHelper;
import rollsPOC2.util.AppServices;
import rollsPOC2.util.DependencyManager;

public class RollsPOC2
{
    public static void main(String[] args) throws Exception
    {
        if (args.length > 0)
        {
            if("startCluster".equals(args[0]))
            {
                AWSHelper.createOrFindEMRHiveCluster("Treebeard", true);
            }
        }
        else
        {
            new RollsPOC2().doIt();
        }
    }

    private void doIt() throws Exception
    {
        setUp();

//        ListenableGraph g = new ListenableDirectedGraph( DefaultEdge.class );

        DependencyManager dag = DependencyManager.getInstance();
        dag.addTask(new WeatherToRabbit("Weather to Rabbit"));
        dag.addTask(new FlightsToRabbit("Flights to Rabbit"));
        dag.addTask(new RabbitToS3("Rabbit to S3"), "Weather to Rabbit", "Flights to Rabbit");
        dag.addTask(new GenericTask("Flight Time Hive"), "Rabbit to S3");
        dag.addTask(new ComputeFlightTimes("Compute Flight Times"), "Flight Time Hive");
        dag.addTask(new SendEmail("Send Email"), "Compute Flight Times");

//        Task weatherToRabbitTask = new WeatherToRabbit("Weather to Rabbit");
//        Task flightsToRabbitTask = new FlightsToRabbit("Flights to Rabbit");
//        Task rabbitToS3Task = new RabbitToS3("Rabbit to S3");
//        Task flightTimeHiveTask = new GenericTask("Flight Time Hive");
//        Task computeFlightTimesTask = new ComputeFlightTimes("Compute Flight Times");
//        Task sendEmailTask = new SendEmail("Send Email");
//
//        dag.addVertex(weatherToRabbitTask);
//        dag.addVertex(flightsToRabbitTask);
//        dag.addVertex(rabbitToS3Task);
//        dag.addVertex(flightTimeHiveTask);
//        dag.addVertex(computeFlightTimesTask);
//        dag.addVertex(sendEmailTask);
//
//        dag.addDagEdge(rabbitToS3Task, weatherToRabbitTask);
//        dag.addDagEdge(rabbitToS3Task, flightsToRabbitTask);
//        dag.addDagEdge(flightTimeHiveTask, rabbitToS3Task);
//        dag.addDagEdge(computeFlightTimesTask, flightTimeHiveTask);
//        dag.addDagEdge(sendEmailTask, computeFlightTimesTask);
        
        for(Task vertex : dag.getRoots())
        {
            vertex.runTask();
        }
        
        try
        {
//            Future<Boolean> weatherToRabbitTask = new WeatherToRabbit("Weather to Rabbit").runTask();
//            Future<Boolean> flightsToRabbitTask = new FlightsToRabbit("Flights to Rabbit").runTask();
//
//            Boolean weatherToRabbitResult = weatherToRabbitTask.get(10, TimeUnit.SECONDS);
//            Boolean flightsToRabbitResult = flightsToRabbitTask.get(10, TimeUnit.SECONDS);
//
//            if (weatherToRabbitResult && flightsToRabbitResult)
//            {
//                Future<Boolean> rabbitToS3Task = new RabbitToS3("Rabbit to S3").runTask();
//                Boolean rabbitToS3Result = rabbitToS3Task.get(10, TimeUnit.SECONDS);
//
//                if (rabbitToS3Result)
//                {
//                    Future<Boolean> flightTimeHiveTask = new FlightTimeHive("Flight Time Hive").runTask();
//                    Boolean flightTimeHiveResult = flightTimeHiveTask.get(20, TimeUnit.MINUTES);
//
//                    if (flightTimeHiveResult)
//                    {
//                        Future<Boolean> computeFlightTimesTask = new ComputeFlightTimes("Compute Flight Times").runTask();
//                        Boolean computeFlightTimesResult = computeFlightTimesTask.get(10, TimeUnit.SECONDS);
//
//                        if (computeFlightTimesResult)
//                        {
//                            Future<Boolean> sendEmailTask = new SendEmail("Send Email").runTask();
//                            Boolean sendEmailResult = sendEmailTask.get(30, TimeUnit.SECONDS);
//
//                            if (sendEmailResult)
//                            {
//                                System.out.println("done");
//                            }
//                        }
//                    }
//                }
//            }
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            shutDown();
        }
    }

    private void setUp() throws Exception
    {

    }

    private void shutDown() throws Exception
    {
        while(!DependencyManager.getInstance().isComplete())
        {
            System.out.println("Waiting for dep. mgr. tasks to finish");
            Thread.sleep(10000);
        }
        ThreadPoolExecutor threadPool = AppServices.getMainThreadPool();
        System.out.println("Shutting down.  Active task count: " + threadPool.getActiveCount());
        AppServices.shutdownHibernate();
        threadPool.shutdown();
        
        while(threadPool.isTerminating())
        {
            System.out.println("waiting for shutdown.  Active task count: " + threadPool.getActiveCount());
            Thread.sleep(1000);
        }
    }

}
