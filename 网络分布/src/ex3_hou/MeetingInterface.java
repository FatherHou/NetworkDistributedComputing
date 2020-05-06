/**
 * 
 */
package ex3_hou;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

/**
 * @author hou
 * 定义会议接口
 */
public interface MeetingInterface extends Remote{
	
	/**
     * 注册用户，注册成功返回 true，否则返回 false
     * @param username
     * @param password
     * @return 注册成功返回true,否则返回false
     * @throws RemoteException
     */
    boolean register(String username, String password) throws RemoteException;

    /**
     * 添加会议，当添加失败时抛出异常。
     * @param meeting
     * @param username
     * @param password
     * @return 添加成功返回0,时间有错返回1,冲突返回2
     * @throws RemoteException
     */
    int add(Meeting meeting, String username, String password)
            throws RemoteException;

    /**
     * 根据时间区间查找符合的会议
     * @param start
     * @param end
     * @param username
     * @param password
     * @return List<Meeting>
     * @throws RemoteException
     */
    List<Meeting> query(Date start, Date end, String username, String password)
            throws RemoteException;

    /**
     * 根据会议ID删除用户创建的会议。如果不存在则取消操作。
     * @param meetingId
     * @param username
     * @param password
     * @return 删除成功返回0,找不到id返回1,不是当前账户的会议返回2
     * @throws RemoteException
     */
    int delete(int meetingId, String username, String password)
            throws RemoteException;

    /**
     * 清除该用户创建的所有会议
     * @param username
     * @param password
     * @return 清除成功返回true,否则返回false
     * @throws RemoteException
     */
    void clear(String username, String password)
            throws RemoteException;

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
