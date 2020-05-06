/**
 * 
 */
package exam2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hou
 * 单线程客户端
 */
public class Handler {
	private MessageInterface messageInf;
	private String name;
	private String password;
	
	public Handler(MessageInterface meetingInf) {
		this.messageInf = meetingInf;
	}

	public void process(String[] args) {
		name = args[1];
		password = args[2];
		BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in)); 
		try{
		menu();	//打开菜单
		System.out.println("请输入指令: ");
		String msg = null;
		while((msg = buffer.readLine()) != null){	//按行读取用户命令
			String split[] = msg.split(" ");
			if(split[0].equals("showusers")){
				if(split.length == 1){
					List<User> tempUser = new ArrayList<User>();
					tempUser = messageInf.showusers();
					for(int i=0;i<tempUser.size();i++){
						System.out.print("User" + (i+1) + ": ");
						System.out.println(tempUser.get(i).toString());
					}
				}else{
					System.out.println("格式应为:showusers.");
				}
			}else if(split[0].equals("checkmessage")){
				if(split.length == 3){
					if(messageInf.login(split[1], split[2]) == 0){
						List<Message> tempMes = new ArrayList<Message>();
						tempMes = messageInf.checkMessages(split[1]);
						if(tempMes.size() != 0){
							for(int i=0;i<tempMes.size();i++){
								System.out.println(tempMes.get(i).toString());
							}
						}else{
							System.out.println("您没有任何留言.");
						}
					}else{
						System.out.println("用户名密码不正确.");
					}
				}else{
					System.out.println("格式应为:checkmessage [username] [password].");
				}
			}else if(split[0].equals("leavemessage")){
				if(split.length == 5){
					if(messageInf.login(split[1], split[2]) == 0){
						if(messageInf.leaveMessage(split[1], split[3], split[4]) == true){
							System.out.println("留言成功.");
						}else if(messageInf.leaveMessage(split[1], split[3], split[4]) == false){
							System.out.println("接收用户不存在,留言失败.");
						}
					}else{
						System.out.println("用户名密码不正确.");
					}
				}else{
					System.out.println("格式应为:leavemessage [username] [password] [reciever] [message_bdoy].");
				}
			}else{
				System.out.println("指令格式未识别,请输入正确的指令.");
			}
			System.out.println("\n请输入指令: ");
		}
		}catch(RemoteException re) {
			re.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (ParseException pe) {
			pe.printStackTrace();
		}
	}
	
    /**
     * 菜单功能
     * @author hou
     */
	private void menu(){
		System.out.println("RMI Menu:");
		System.out.println("     1.showusers");
		System.out.println("     2.checkmessage [username] [password]");
		System.out.println("     3.leavemessage [username] [password] [reciever] [message_bdoy]\n");
	}

}
