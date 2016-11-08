import com.cdyne.ws.WeatherWS.Weather;
import com.cdyne.ws.WeatherWS.WeatherReturn;
import com.cdyne.ws.WeatherWS.WeatherSoap;

public class SOAPTest {

    public static void main(String args[]) throws Exception {
    	Weather w = new Weather();
    	WeatherSoap ws = w.getWeatherSoap();
    	WeatherReturn wr = ws.getCityWeatherByZIP("10025");
    	if(wr.isSuccess())
    	{
    		System.out.println("City: " + wr.getCity());
    		System.out.println("Temperature: " + wr.getTemperature());
    	}
    }

}