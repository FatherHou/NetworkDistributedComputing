/**
 * 
 */
package exam2;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hou
 * 完成接口的操作
 */
public class MessageOperation extends UnicastRemoteObject implements MessageInterface{

    private ArrayList<User> userList;
    private ArrayList<Message> messageList;
	
	protected MessageOperation() throws RemoteException {
		super();
		userList = new ArrayList<>();
	    messageList = new ArrayList<>();
	}

	@Override
	public boolean register(String username, String password) throws RemoteException {
		User tempUser = null;
        for(int i=0;i<userList.size();i++){
        	if(userList.get(i).getName().equals(username)){
        		tempUser = userList.get(i);	
        	}
        }
        if (tempUser != null) {	//检测用户名是否已经被注册
            return false;
        }
        tempUser = new User(username, password);
        userList.add(tempUser);
        return true;
	}

	@Override
	public List<User> showusers() throws RemoteException {
		return userList;
	}

	@Override
	public List<Message> checkMessages(String username) throws RemoteException {
		List<Message> nowMessage = new ArrayList<Message>();
		for(int i=0;i<messageList.size();i++){
			if(messageList.get(i).getReciever().equals(username)){	//从信息列表寻找接受者为此用户的信息
				nowMessage.add(messageList.get(i));
			}
		}
		return nowMessage;
	}

	@Override
	public boolean leaveMessage(String username, String receiver, String body) throws RemoteException, ParseException {
		Date now = new Date();
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		String dateStr = dateFormat.format(now);
		Date now2 = dateFormat.parse(dateStr);
		for(int i=0;i<userList.size();i++){
			if(userList.get(i).getName().equals(receiver)){	//检测用户列表里是否有目标用户
				Message newMes = new Message(username, receiver, now2 , body);
				messageList.add(newMes);
				return true;
			}
		}
		return false;
	}

	@Override
	public int login(String username, String password) throws RemoteException {
		for(int i=0;i<userList.size();i++){
			if(userList.get(i).getName().equals(username)){
				if(userList.get(i).getPassword().equals(password)){
					return 0;
				}else{
					return 1;
				}
			}
		}
		return 2;
	}

}
