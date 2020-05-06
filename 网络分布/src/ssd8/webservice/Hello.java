package ssd8.webservice;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;


/**
 * @author wben
 * 
 */
@WebService(name="hello",  portName="helloPort", targetNamespace="http://www.javaedu.com")
public class Hello {
	@WebMethod
	public String hello(String name) {
		return "Hello," + name;
	}
	
	@WebMethod
	public String sayHi(String name, int gender) {
		if(gender == 1) {
			return "Hi, Mr." + name;
		} else {
			return "Hi, Miss. " + name;
		}
	}
	
	@WebMethod
	public User findUser(String name){
		return new User(name, "1111", new Date());
	}
	
    public static void main(String[] args) {  
        Endpoint.publish("http://127.0.0.1:8001/webservice/hello", new Hello());  
        //wsimport -keep http://127.0.0.1:8001/webservice/hello?wsdl
    }  
}
