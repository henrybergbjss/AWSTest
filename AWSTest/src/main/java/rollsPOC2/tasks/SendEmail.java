package rollsPOC2.tasks;

import java.util.List;

import org.hibernate.Session;

import hibtest.AirlineNotification;
import rollsPOC2.util.AppServices;

public class SendEmail extends Task
{
	public SendEmail(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public Boolean call() throws Exception {
		Session session = AppServices.getSessionFactory().openSession();
		List<AirlineNotification> result = session.createQuery( "from AirlineNotification" ).list();
		for ( AirlineNotification notification : (List<AirlineNotification>) result ) {
		    System.out.println( "Event (" + notification.getFlightNum() + ") : " + notification.getBody() );
		}
		session.close();

        setSuccess(true);
        return true;
	}
}
