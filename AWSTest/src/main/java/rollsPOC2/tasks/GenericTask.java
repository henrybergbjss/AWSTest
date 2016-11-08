package rollsPOC2.tasks;

public class GenericTask extends Task
{

    public GenericTask(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public Boolean call() throws Exception
    {
        setSuccess(true);
        return true;
    }

}
