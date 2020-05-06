package exam2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author hou
 * 定义会议接口
 */
public interface MessageInterface extends Remote{
	
	/**
     * 注册用户，注册成功返回 true，否则返回 false
     * @param username
     * @param password
     * @return 注册成功返回true,否则返回false
     * @throws RemoteException
     */
    boolean register(String username, String password) throws RemoteException;
    
    /**
     * 显示所有注册的用户
     * @throws RemoteException
     */
    List<User> showusers() throws RemoteException;
    
    /**
     * 打印用户的所有留言
     * @param username
     * @return List<Message>
     * @throws RemoteException
     */
    List<Message> checkMessages(String username) throws RemoteException;
    
    /**
     * 给其他用户留言，并给出相应提示
     * @param username
     * @param receiver
     * @param body
     * @return 留言成功返回true,接受者不存在返回false
     * @throws RemoteException
     * @throws ParseException 
     */
    boolean leaveMessage(String username, String receiver, String body) throws RemoteException, ParseException;
    
    /**
     * 检查用户是否存在且密码正确
     * @param username
     * @param password
     * @return 登录成功返回 0,帐号密码不匹配返回1,未注册返回2
     * @throws RemoteException
     */
    int login(String username, String password)
            throws RemoteException;
}
