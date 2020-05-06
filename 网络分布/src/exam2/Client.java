/**
 * 
 */
package exam2;

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
            System.out.println("请设置启动参数:[host] [port]");
            return;
        }
        try {
            String url = "//" + args[0] + ":" + args[1] + "/MesseageInterface";
            MessageInterface messageInf = (MessageInterface) Naming.lookup(url);	//查找URL，匹配对应服务器IP和端口
            System.out.println("欢迎使用留言系统!");
            System.out.println("清先注册或登陆,格式为:");
            System.out.println("     register [username] [password]");
            System.out.println("     login [username] [password]\n");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
			String[] handler = null;
			while(true){
				msg = buffer.readLine();
				handler = msg.split(" ");
				if(handler[0].equals("register")){
					if(messageInf.register(handler[1], handler[2]) != true){
						System.out.println("此User已存在.请选择另一个用户名.");
					}else{
						System.out.println("User " + handler[1] + " 注册成功.");
						break;
					}
				}else if(handler[0].equals("login")){
					if(messageInf.login(handler[1], handler[2]) == 0){
						System.out.println("登录 " + handler[1] + " 成功.");
						break;
					}else if(messageInf.login(handler[1], handler[2]) == 1){
						System.out.println("帐号密码不匹配,登录失败.");
					}else if(messageInf.login(handler[1], handler[2]) == 2){
						System.out.println("请先注册,登录失败.");
					}
				}else{
					System.out.println("格式错误,格式为:login/register [username] [password]");
				}
			}
            new Handler(messageInf).process(handler);	//调用子线程实现对每个用户的操作
        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (MalformedURLException me) {
			me.printStackTrace();
		} catch (NotBoundException ne) {
			ne.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		}
    }
}
