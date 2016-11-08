package rollsPOC2.util;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import rollsPOC2.tasks.Task;

public class DependencyManager extends DirectedAcyclicGraph<Task, DefaultEdge>
{
    private static DependencyManager instance;
    
    private DependencyManager()
    {
        super(DefaultEdge.class);
    }
    
    public static synchronized DependencyManager getInstance()
    {
        if(instance == null)
        {
            instance = new DependencyManager();
        }
        
        return instance;
    }
    
    public boolean isComplete()
    {
        boolean isComplete = true;
        
        for(Task vertex : getLeaves())
        {
            if(!vertex.isSuccess())
            {
                isComplete = false;
            }
        }
        
        return isComplete;
    }
    
    public Set<Task> getRoots()
    {
        HashSet<Task> roots = new HashSet<Task>();
        for(Task vertex : instance.vertexSet())
        {
            if(instance.outDegreeOf(vertex) == 0)
            {
                roots.add(vertex);
            }
        }
        
        return roots;
    }
    
    public Set<Task> getLeaves()
    {
        HashSet<Task> leaves = new HashSet<Task>();
        for(Task vertex : instance.vertexSet())
        {
            if(instance.inDegreeOf(vertex) == 0)
            {
                leaves.add(vertex);
            }
        }
        
        return leaves;
    }
    
    public Task findTask(String name)
    {
        for(Task vertex : instance.vertexSet())
        {
            if(vertex.getName().equals(name))
            {
                return vertex;
            }
        }
        
        return null;
    }
    
    public boolean addTask(Task task, String...dependencies) throws CycleFoundException
    {        
        boolean success = addVertex(task);
        if(success)
        {
            for(String dependency : dependencies)
            {
                Task depFound = findTask(dependency);
                if(depFound != null)
                {
                    addDagEdge(task, depFound);
                }
                else
                {
                    System.err.println("Ignoring dependency " + dependency + " as it was not found in the graph");
                }
            }
        }
        
        return success;
    }
}
