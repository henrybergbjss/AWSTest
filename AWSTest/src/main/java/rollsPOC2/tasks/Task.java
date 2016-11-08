package rollsPOC2.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import rollsPOC2.util.AppServices;
import rollsPOC2.util.DependencyManager;

public abstract class Task extends DefaultEdge implements Callable<Boolean>
{
    String name;
    boolean isSuccess;
    
	public Future<Boolean> runTask()
	{
		System.out.println("Starting task " + getClass().getName());
		
		return AppServices.getMainThreadPool().submit(this);
	}
	
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Task(String name)
    {
        this.name = name;
    }
    
    //TODO:  protected methods smell
    protected void setSuccess(boolean success)
    {
        if(success)
        {
            this.isSuccess = success;
            DirectedAcyclicGraph<Task, DefaultEdge> dag = DependencyManager.getInstance();
            for(DefaultEdge incomingEdge : dag.incomingEdgesOf(this))
            {
                try
                {
                    dag.getEdgeSource(incomingEdge).update();
                }
                catch(Exception e)
                {
                    //TODO: improve
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public void update()
    {
        DirectedAcyclicGraph<Task, DefaultEdge> dag = DependencyManager.getInstance();
        boolean allDependenciesSatisfied = true;
        for(DefaultEdge outgoingEdge : dag.outgoingEdgesOf(this))
        {
            try
            {
                if(!dag.getEdgeTarget(outgoingEdge).isSuccess())
                {
                    allDependenciesSatisfied = false;
                }
            }
            catch(Exception e)
            {
                //TODO: improve
                throw new RuntimeException(e);
            }
        }
        
        if(allDependenciesSatisfied)
        {
            runTask();
        }
    }
    
    public boolean isSuccess()
    {
        return this.isSuccess;
    }
}