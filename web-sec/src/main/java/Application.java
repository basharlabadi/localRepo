import javax.xml.ws.Endpoint;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ws.ECustomerWS;


public class Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("application-context.xml");
		ctx.getBean("ECustomerRestSvc");
		System.out.print("bashar");
		
		
	   //this is to test soap handler
	   Endpoint.publish("http://localhost:8888/ws/server", new ECustomerWS());
	   System.out.println("Service is published!");
	}

}
