package rollsPOC2.tasks;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import flightapi.Connection;
import flightapi.Lastflightout;
import flightapi.ScheduledFlight;
import rollsPOC2.util.RabbitHelper;

public class FlightsToRabbit extends Task
{
	public FlightsToRabbit(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public Boolean call() throws Exception {
		SimpleDateFormat urlParamFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
		Date now = new Date();
		String appId = "21b8ff79";
		String appKey = "2987c799c530bd207f9a1ecdea6e2b30";
		String request = "/flex/connections/rest/v2/json/lastflightout/JFK/to/LAX/leaving_after/" + urlParamFormat.format(now) + "?appId=" + appId + "&appKey=" + appKey + "&numHours=1&maxConnections=1&includeSurface=false&payloadType=passenger&includeCodeshares=true&includeMultipleCarriers=true&maxResults=1";

		URL url = new URL("https://api.flightstats.com" + request);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Lastflightout value = mapper.readValue(url.openStream(), Lastflightout.class);

		for(Connection connection : value.getConnections())
		{
			if(connection.getScheduledFlight().size() == 1)
			{
				publishFlight(connection.getScheduledFlight().get(0));
			}
		}

		setSuccess(true);
		return true;
	}

	private void publishFlight(ScheduledFlight scheduledFlight) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String flightJson = mapper.writeValueAsString(scheduledFlight);
		System.out.println("will publish: " + flightJson);

		RabbitHelper.publish("flight", flightJson);
    }

}
