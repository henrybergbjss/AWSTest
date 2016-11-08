package rollsPOC2.util;

import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;

public class RabbitHelper
{
	public static void publish(String name, String message) throws Exception
	{
	    Connection connection = AppServices.getRabbitConnectionFactory().newConnection();
	    Channel channel = connection.createChannel();
//	    channel.queueDeclare(name, true, false, false, null);
	    channel.basicPublish("", name, null, message.getBytes());
	    channel.close();
	    connection.close();
	}

	public static List<String> consume(String name, int max) throws Exception
	{
		List<String> messages = new ArrayList<String>();
		
	    Connection connection = AppServices.getRabbitConnectionFactory().newConnection();
	    Channel channel = connection.createChannel();

	    GetResponse response = channel.basicGet(name, true);
	    if(response != null)
	    {
	    	messages.add(new String(response.getBody()));
	    	while((max == 0 && response.getMessageCount() > 0) || (messages.size() < max && response != null))
	    	{
	    		response = channel.basicGet(name, true);
	    	    if(response != null)
	    	    {
	    	    	messages.add(new String(response.getBody()));
	    	    }
	    	}
	    }

	    channel.close();
	    connection.close();
		
		return messages;
	}

	public static List<String> consumeAll(String topic) throws Exception
	{
		return consume(topic, 0);
	}
}
