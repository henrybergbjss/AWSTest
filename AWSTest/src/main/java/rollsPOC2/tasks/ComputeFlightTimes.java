package rollsPOC2.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import rollsPOC2.util.AppServices;

public class ComputeFlightTimes extends Task
{
	public ComputeFlightTimes(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public Boolean call() throws Exception
	{
	    Logger logger = Logger.getLogger(ComputeFlightTimes.class);

	    AmazonS3 s3 = AppServices.getS3Client();
    	String bucketName = "bjss-nyc-dev";
    	String key = null;
    	ObjectListing objectListing = s3.listObjects(bucketName, "test");
    	
    	for(S3ObjectSummary s3File : objectListing.getObjectSummaries())
    	{
    		//yes, we're just grabbing the last one.
    		key = s3File.getKey();
    		System.out.println(key);
    	}
		S3Object hiveResultS3File = s3.getObject(bucketName, key);
		S3ObjectInputStream s3ObjectInputStream = hiveResultS3File.getObjectContent();
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(s3ObjectInputStream));
		String nextLine = br.readLine();
		while(nextLine != null)
		{
			sb.append(nextLine);
			nextLine = br.readLine();
		}
		
		s3ObjectInputStream.close();
		br.close();
		hiveResultS3File.close();
		
		System.out.println("S3 file contents: " + sb.toString());

        setSuccess(true);
        return true;
	}

}
