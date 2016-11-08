package rollsPOC2.tasks;

import javax.json.Json;
import javax.json.JsonObject;

import com.cdyne.ws.WeatherWS.Weather;
import com.cdyne.ws.WeatherWS.WeatherReturn;
import com.cdyne.ws.WeatherWS.WeatherSoap;

import rollsPOC2.util.RabbitHelper;

public class WeatherToRabbit extends Task
{
	public WeatherToRabbit(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public Boolean call() throws Exception
	{
		//sample output [{"zip":"11430","visibility":null,"city":"Jamaica","temperature":"63","state":"NY","wind":"E7"}]
		String ZIP_CODE = "10025";
    	Weather w = new Weather();
    	WeatherSoap ws = w.getWeatherSoap();
    	WeatherReturn wr = ws.getCityWeatherByZIP(ZIP_CODE);

    	if(wr.isSuccess())
    	{
    		JsonObject value = Json.createObjectBuilder()
    				.add("zip", ZIP_CODE)
    				.add("visibility", wr.getVisibility())
    				.add("city", wr.getCity())
    				.add("temperature", wr.getTemperature())
    				.add("state", wr.getState())
    				.add("wind", wr.getWind())
    				.build();
       	 
    		publishWeather(value.toString());
    	}
    	
        setSuccess(true);
    	return true;
	}

	private void publishWeather(String weather) throws Exception
	{
		System.out.println("will publish: " + weather);		
		RabbitHelper.publish("weather", weather);
	}

}
