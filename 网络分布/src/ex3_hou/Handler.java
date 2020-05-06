/**
 * 
 */
package ex3_hou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author hou
 * 单线程客户端
 */
public class Handler {
	private MeetingInterface meetingInf;
	private String name;
	private String password;
	
	public Handler(MeetingInterface meetingInf) {
		this.meetingInf = meetingInf;
	}

	public void process(String[] args) {
		String operation = args[2];
		name = args[3];
		password = args[4];
		BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in)); 
		try{
			if(operation.equals("register")){
				if(meetingInf.register(name, password) != true){
					System.out.println("此User已存在.");
					return;
				}else{
					System.out.println("User " + name + "注册成功.");
				}
			}else if(operation.equals("login")){
				if(meetingInf.login(name, password) == 0){
					System.out.println("登录 " + name + "成功.");
				}else if(meetingInf.login(name, password) == 1){
					System.out.println("帐号密码不匹配,登录失败.");
					return;
				}else if(meetingInf.login(name, password) == 2){
					System.out.println("请先注册,登录失败.");
					return;
				}
			}else{
				System.out.println("格式错误.");
				return;
			}
		menu();
		System.out.println("请输入指令: ");
		String msg = null;
		while((msg = buffer.readLine()) != null){
			String split[] = msg.split(" ");
			if(split[0].equals("add")){
				if(split.length == 5){
					//建立新的Meeting存储参数传递给服务器
					Meeting newMet = new Meeting(999,name,split[1],(new SimpleDateFormat("yyyy-MM-dd-HH:mm")).parse(split[2]),(new SimpleDateFormat("yyyy-MM-dd-HH:mm")).parse(split[3]),split[4]);
					if(meetingInf.add(newMet, name, password) == 0){
						System.out.println("添加会议成功.");
					}else if(meetingInf.add(newMet, name, password) == 1){
						System.out.println("添加失败,开始时间应在结束时间之后.");
					}else if(meetingInf.add(newMet, name, password) == 2){
						System.out.println("会议与已有会议冲突,添加失败.");
					}
				}else{
					System.out.println("格式应为:add [othername] [start] [end] [title].");
				}
			}else if(split[0].equals("delete")){ 
				if(split.length == 2){
					if(meetingInf.delete(Integer.parseInt(split[1]), name, password) == 0){
						System.out.println("删除会议成功.");
					}else if(meetingInf.delete(Integer.parseInt(split[1]), name, password) == 1){
						System.out.println("未找到此会议,删除失败.");
					}else if(meetingInf.delete(Integer.parseInt(split[1]), name, password) == 2){
						System.out.println("这不是你的会议,删除失败.");
					}
				}else{
					System.out.println("格式应为:delete [meetingid].");
				}
			}else if(split[0].equals("clear")){
				meetingInf.clear(name, password);
				System.out.println("清除" + name + "的会议成功.");
			}else if(split[0].equals("query")){
				if(split.length == 3){
					List<Meeting> qMet = null;
					qMet = meetingInf.query((new SimpleDateFormat("yyyy-MM-dd-HH:mm")).parse(split[1]), (new SimpleDateFormat("yyyy-MM-dd-HH:mm")).parse(split[2]), name, password);
					System.out.println("查询成功.");
					if(qMet.size()!=0){
					for(int i=0;i<qMet.size();i++){
						System.out.println(qMet.get(i).toString());
					}
					}else{
						System.out.println("所查询区间无会议");
					}
				}else{
					System.out.println("格式应为:query [start] [end].");
				}
			}else if(split[0].equals("help")){
				menu();
			}else if(split[0].equals("quit")){
				System.out.println("使用结束.");
				break;
			}else{
				System.out.println("未知指令.");
				break;
			}
			System.out.println("\n请输入指令: ");
		}
		}catch(RemoteException re) {
			re.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
	}
	
    /**
     * 菜单功能
     * @author hou
     */
	private void menu(){
		System.out.println("RMI Menu:");
		System.out.println("         1.add [otherusername] [start] [end] [title]");
		System.out.println("         2.delete [meetingid]");
		System.out.println("         3.clear");
		System.out.println("         4.query [start] [end]");
		System.out.println("         5.help");
		System.out.println("         6.quit");
		System.out.println("         (PS:日期格式 20xx-12-12-12:12)\n");
	}

}
