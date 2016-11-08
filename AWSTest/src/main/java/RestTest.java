import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import QPXExpress.Passengers;
import QPXExpress.QPXRequest;
import QPXExpress.QPXResponse;
import QPXExpress.Request;
import QPXExpress.Slouse;

public class RestTest {

	public static void main(String[] args) {
		try {
	        String apiKey = "AIzaSyAzCdJ_XRgvbtkb3b8lm76pfNXHtQ0yz7o";
			QPXRequest requestObj = new QPXRequest();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			requestObj.setRequest(new Request()
					.withPassengers(new Passengers().withAdultCount(1))
					.withSlice(Arrays.asList(new Slouse[]{
						new Slouse()
							.withOrigin("JFK")
							.withDestination("LAX")
							.withMaxStops(0)
							.withDate(sdf.format(new Date()))
						}))
					);
			String request = "https://www.googleapis.com/qpxExpress/v1/trips/search?key=" + apiKey;
			URL url = new URL(request);
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	        // 2. Open connection
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 
	        // 3. Specify POST method
	        conn.setRequestMethod("POST");
	 
	        // 4. Set the headers
	        conn.setRequestProperty("Content-Type", "application/json");
//	        conn.setRequestProperty("Authorization", "key="+apiKey);
	 
	        conn.setDoOutput(true);
	 
            // 5. Add JSON data into POST request body 
 
            //`5.1 Use Jackson object mapper to convert Contnet object into JSON
            ObjectMapper mapper = new ObjectMapper();
 
            // 5.2 Get connection output stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
 
            // 5.3 Copy Content "JSON" into 
            String requestJson = mapper.writeValueAsString(requestObj);
            System.out.println(requestJson);
			mapper.writeValue(wr, requestJson);
 
            // 5.4 Send the request
            wr.flush();
 
            // 5.5 close
            wr.close();
 
            // 6. Get the response
            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
			QPXResponse response = mapper.readValue(conn.getResponseMessage(), QPXResponse.class);
			System.out.println(response.getTrips().getTripOption().size());

			
//			SimpleDateFormat urlParamFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
//			Date now = new Date();
//			String request = "/flex/connections/rest/v2/json/lastflightout/JFK/to/LAX/leaving_after/" + urlParamFormat.format(now) + "?appId=21b8ff79&appKey=2987c799c530bd207f9a1ecdea6e2b30&numHours=1&maxConnections=1&includeSurface=false&payloadType=passenger&includeCodeshares=true&includeMultipleCarriers=true&maxResults=1";
//
//			URL url = new URL("https://api.flightstats.com" + request);
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//			Lastflightout value = mapper.readValue(url.openStream(), Lastflightout.class);
//
//			ScheduledFlight flight = value.getConnections().get(0).getScheduledFlight().get(0);
//			System.out.println("Flight: " + flight.getFlightNumber());
//			System.out.println("Carrier: " + flight.getCarrierFsCode());
//			
//			DefaultApi da = new DefaultApi();
//
//			String departureAirport = "JFK";
//			String month = String.valueOf(now.getMonth());
//			String hour = String.valueOf(now.getHours());
//			String year = String.valueOf(now.getYear());
//			String arrivalAirport = "LAX";
//			String day = String.valueOf(now.getDay());
//			String minute = String.valueOf(now.getMinutes());
//			String includeAirports = null;
//			String excludeAirports = null;
//			String includeAirlines = null;
//			String excludeAirlines = null;
//			String numHours = "1";
//			String maxConnections = "1";
//			String includeSurface = null;
//			String payloadType = null;
//			String includeCodeshares = null;
//			String includeMultipleCarriers = null;
//			String maxResults = null;
//			String minimumConnectTime = null;
//			String extendedOptions = null;
//			String appId = "21b8ff79";
//			String appKey = "2987c799c530bd207f9a1ecdea6e2b30";
//			String format = null;
//			
//			da.getLastFlightOut(departureAirport, month, hour, year, arrivalAirport, day, minute, includeAirports, excludeAirports, includeAirlines, excludeAirlines, numHours, maxConnections, includeSurface, payloadType, includeCodeshares, includeMultipleCarriers, maxResults, minimumConnectTime, extendedOptions, appId, appKey, format);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}