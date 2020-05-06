/**
 * 
 */
package ex3_hou;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ex3_hou.MeetingInterface;

/**
 * @author hou
 * 完成接口的操作
 */
public class MeetingOperation extends UnicastRemoteObject implements MeetingInterface {

    private ArrayList<User> userList;
    private ArrayList<Meeting> meetingList;
    private int tempId;
    
	protected MeetingOperation() throws RemoteException {
		super();
        userList = new ArrayList<>();
        meetingList = new ArrayList<>();
        tempId = 1;
	}

	/**
     * 注册用户，注册成功返回 true，否则返回 false
     * @param username
     * @param password
     * @return 注册成功返回true,否则返回false
     * @throws RemoteException
     */
	@Override
	public boolean register(String username, String password) throws RemoteException {
		User tempUser = null;
        for(int i=0;i<userList.size();i++){
        	if(userList.get(i).getName().equals(username)){
        		tempUser = userList.get(i);
        	}
        }
        if (tempUser != null) {
            return false;
        }
        tempUser = new User(username, password);
        userList.add(tempUser);
        return true;
	}

    /**
     * 添加会议，当添加失败时抛出异常。
     * @param meeting
     * @param username
     * @param password
     * @return 添加成功返回0,时间有错返回1,冲突返回2
     * @throws RemoteException
     */
	@Override
	public int add(Meeting meeting, String username, String password) throws RemoteException {
		if(meeting.getStart().after(meeting.getEnd())){
			return 1;
		}else{
			int count = 0;
			for(int i=0;i<meetingList.size();i++){
				if((meeting.getStart().before(meetingList.get(i).getStart())	//加入区间的end在已存在的某个会议区间中
						||meeting.getStart().equals(meetingList.get(i).getStart()))
					&&
					((meeting.getEnd().before(meetingList.get(i).getEnd())
						&&meeting.getEnd().after(meetingList.get(i).getStart()))
						||meeting.getEnd().equals(meetingList.get(i).getEnd()))){
					count++;
				}
				if((meeting.getStart().after(meetingList.get(i).getStart())		//加入区间在已存在的某个会议区间中
						||meeting.getStart().equals(meetingList.get(i).getStart()))
					&&
					(meeting.getEnd().before(meetingList.get(i).getEnd())
						||meeting.getEnd().equals(meetingList.get(i).getEnd()))){
					count++;
				}
				if(((meeting.getStart().after(meetingList.get(i).getStart())	//加入区间的start在已存在的某个会议区间中
						&&meeting.getStart().before(meetingList.get(i).getEnd()))
						||meeting.getStart().equals(meetingList.get(i).getStart()))
					&&
					(meeting.getEnd().after(meetingList.get(i).getEnd())
						||meeting.getEnd().equals(meetingList.get(i).getEnd()))){
					count++;
				}
				if((meeting.getStart().before(meetingList.get(i).getStart())	//加入区间包含已存在的某个会议区间
						||meeting.getStart().equals(meetingList.get(i).getStart()))
					&&
					(meeting.getEnd().after(meetingList.get(i).getEnd())
						||meeting.getEnd().equals(meetingList.get(i).getEnd()))){
					count++;
				}
			}
			if(count == 0){
				meeting.setId(tempId);
				tempId++;
				meetingList.add(meeting);
				return 0;
			}else{
				return 2;
			}
		}
	}

    /**
     * 根据时间区间查找符合的会议
     * @param start
     * @param end
     * @param username
     * @param password
     * @return List<Meeting>
     * @throws RemoteException
     */
	@Override
	public List<Meeting> query(Date start, Date end, String username, String password) throws RemoteException {
		List<Meeting> sList = new ArrayList<Meeting>();
		//查询区间应查询囊括在区间中的会议
		for(int i=0;i<meetingList.size();i++){
			if(((meetingList.get(i).getStart().after(start)			//有在查询start后面的start的会议
				||meetingList.get(i).getStart().equals(start))		//有等于查询start的start的会议
					&&(meetingList.get(i).getStart().before(end)	//有在查询end前面的start的会议
					||meetingList.get(i).getStart().equals(end)))	//有等于查询end的start的会议
				||
				((meetingList.get(i).getEnd().after(start)				//有在查询start后面的end的会议
						||meetingList.get(i).getEnd().equals(start))	//有等于查询start的end的会议
							&&(meetingList.get(i).getEnd().before(end)	//有在查询end前前面的end的会议
							||meetingList.get(i).getEnd().equals(end)))){//有等于查询end的end的会议
				sList.add(meetingList.get(i));
			}
		}
		return sList;
	}

    /**
     * 根据会议ID删除用户创建的会议。如果不存在则取消操作。
     * @param meetingId
     * @param username
     * @param password
     * @return 删除成功返回0,找不到id返回1,不是当前账户的会议返回2
     * @throws RemoteException
     */
	@Override
	public int delete(int meetingId, String username, String password) throws RemoteException {
		for(int i=0;i<meetingList.size();i++){
			if(meetingList.get(i).getId() == meetingId){
				if(meetingList.get(i).getHostUser().equals(username)){
					meetingList.remove(i);
					return 0;
				}else{
					return 2;
				}
			}
		}
		return 1;
	}

    /**
     * 清除该用户创建的所有会议
     * @param username
     * @param password
     * @return 清除成功返回true,否则返回false
     * @throws RemoteException
     */
	@Override
	public void clear(String username, String password) throws RemoteException {
		for(int i=0;i<meetingList.size();i++){
			if(meetingList.get(i).getHostUser().equals(username)){
				meetingList.remove(i);
			}
		}
		
	}

    /**
     * 检查用户是否存在且密码正确
     * @param username
     * @param password
     * @return 登录成功返回 0,帐号密码不匹配返回1,未注册返回2
     * @throws RemoteException
     */
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
