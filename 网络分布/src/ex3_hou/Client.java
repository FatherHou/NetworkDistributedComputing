/**
 * 
 */
package ex3_hou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * @author hou
 * 多线程客户端
 */
public class Client {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("请设置启动参数:[host] [port] register/login [username] [password]");
            return;
        }
        try {
            String url = "//" + args[0] + ":" + args[1] + "/MeetingInterface";
            MeetingInterface meetingInf = (MeetingInterface) Naming.lookup(url);	//查找URL，匹配对应服务器IP和端口
            new Handler(meetingInf).process(args);	//调用子线程实现对每个用户的操作
        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (MalformedURLException me) {
			me.printStackTrace();
		} catch (NotBoundException ne) {
			ne.printStackTrace();
		} 
    }
}
