package rollsPOC2;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

public class RabbitMQTest {
	public static void main(String[] args) throws Exception
	{
//		publish();
		subscribe();
	    
	    //Weather: [{"zip":"11430","visibility":null,"city":"Jamaica","temperature":"63","state":"NY","wind":"E7"}]
		//Flight: [{"elapsedTime":370,"score":60,"scheduledFlight":[{"carrierFsCode":"DL","flightNumber":"431","departureAirportFsCode":"JFK","arrivalAirportFsCode":"LAX","stops":0,"departureTime":"2016-06-06T13:50:00.000","arrivalTime":"2016-06-06T17:00:00.000","flightEquipmentIataCode":"76W","isCodeshare":false,"isWetlease":false,"serviceType":"J","trafficRestrictions":[],"elapsedTime":370}]}]
	    /*
		String message = "[{\"elapsedTime\":370,\"score\":60,\"scheduledFlight\":[{\"carrierFsCode\":\"DL\",\"flightNumber\":\"431\",\"departureAirportFsCode\":\"JFK\",\"arrivalAirportFsCode\":\"LAX\",\"stops\":0,\"departureTime\":\"2016-06-06T13:50:00.000\",\"arrivalTime\":\"2016-06-06T17:00:00.000\",\"flightEquipmentIataCode\":\"76W\",\"isCodeshare\":false,\"isWetlease\":false,\"serviceType\":\"J\",\"trafficRestrictions\":[],\"elapsedTime\":370}]}]";
		String message2 = "{\"connections\":" + message + "}";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Lastflightout value = mapper.readValue(message2, Lastflightout.class);
		ScheduledFlight flight = value.getConnections().get(0).getScheduledFlight().get(0);
		System.out.println("via lastFlightOut");
		System.out.println("Flight: " + flight.getFlightNumber());
		System.out.println("Carrier: " + flight.getCarrierFsCode());
		System.out.println();
		System.out.println("Via parsed array of Connections");
		List<Connection> connections = mapper.readValue(message, new TypeReference<List<Connection>>(){});
		ScheduledFlight flight2 = connections.get(0).getScheduledFlight().get(0);
		System.out.println("Flight: " + flight2.getFlightNumber());
		System.out.println("Carrier: " + flight2.getCarrierFsCode());
		*/
	}

	private static void publish() throws IOException, TimeoutException {
		String QUEUE_NAME = "flight";
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("prod.demo.com");
	    factory.setUsername("test");
	    factory.setPassword("test");
	    com.rabbitmq.client.Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
	    String message = "Hello World!";
	    channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
	    System.out.println(" [x] Sent '" + message + "'");
	    channel.close();
	    connection.close();
	}

	private static void subscribe() throws IOException, TimeoutException {
		String QUEUE_NAME = "flight";
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("prod.demo.com");
	    factory.setUsername("test");
	    factory.setPassword("test");
	    com.rabbitmq.client.Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
	    GetResponse response = channel.basicGet(QUEUE_NAME, false);
	    System.out.println(" [x] Received " + response.getMessageCount() + " messages");
	    channel.close();
	    connection.close();
	}
}
