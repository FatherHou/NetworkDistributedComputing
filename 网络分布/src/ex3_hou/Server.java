/**
 * 
 */
package ex3_hou;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import ex3_hou.MeetingInterface;

/**
 * @author hou
 * 服务器端
 */
public class Server {
	 public static void main(String[] args) {
	        try {
	            LocateRegistry.createRegistry(8000);	//在服务器端注册8000端口
	            MeetingInterface meetingOpera = new MeetingOperation();	//Meeting接口的实例化对象
	            Naming.rebind("//localhost:8000/MeetingInterface", meetingOpera);	//绑定实例
	        } catch (RemoteException re) {
	        	re.printStackTrace();
	        } catch (MalformedURLException me) {
				me.printStackTrace();
			}
	    }
}
