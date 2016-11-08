package rollsPOC2.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.json.JSONObject;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import rollsPOC2.util.AppServices;
import rollsPOC2.util.RabbitHelper;

public class RabbitToS3 extends Task
{
	public RabbitToS3(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public Boolean call() throws Exception
	{
		List<String> messages = RabbitHelper.consumeAll("weather");
	    System.out.println(" [x] Received " + messages.size() + " messages");

	    if(!messages.isEmpty())
	    {
	    	JSONObject obj = new JSONObject(messages.get(0));
	    	String temperature = obj.getString("temperature");
	    	
	    	AmazonS3 s3 = AppServices.getS3Client();
	    	String bucketName = "bjss-nyc-dev";
	    	String key = "data/input/inputtemp.txt";
	    	if(!s3.doesBucketExist(bucketName))
	    	{
	    		s3.createBucket(bucketName);
	    	}
            s3.putObject(new PutObjectRequest(bucketName, key, createTemeratureInputFile(temperature)));
	    }
	    
        setSuccess(true);
	    return true;
	}
	
    private static File createTemeratureInputFile(String temperature) throws IOException
    {
        File file = File.createTempFile("temperatureInput", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("SET TEMPERATURE=" + temperature + "\n");
        writer.close();

        return file;
    }

}
