/**
 * 
 */
package com.hou.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author hou
 * 实现对事件的具体操作
 */
@WebService
public class MyService {
	
	private ArrayList<User> userList;
	private ArrayList<Event> eventList;
	private int tempId;
	    
	public MyService(){
		super();
        userList = new ArrayList<>();
        eventList = new ArrayList<>();
        tempId = 1;
	}	
	
	/**
     * 注册用户，注册成功返回 true，否则返回 false
     * @param username
     * @param password
     * @return 注册成功返回true,否则返回false
     */
	@WebMethod
	public boolean register(String username,String password){
		User tempUser = null;
        for(int i=0;i<userList.size();i++){
        	if(userList.get(i).getUserName().equals(username)){
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
     * 添加事件，当添加失败时抛出异常。
     * @param event
     * @param username
     * @param password
     * @return 添加成功返回0,时间有错返回1,冲突返回2
     */
	@WebMethod
	public int add(Event event, String username, String password){
		if(event.getStart().after(event.getEnd())){
			return 1;
		}else{
			int count = 0;
			for(int i=0;i<eventList.size();i++){
				if((event.getStart().before(eventList.get(i).getStart())	//加入区间的end在已存在的某个事件区间中
						||event.getStart().equals(eventList.get(i).getStart()))
					&&
					((event.getEnd().before(eventList.get(i).getEnd())
						&&event.getEnd().after(eventList.get(i).getStart()))
						||event.getEnd().equals(eventList.get(i).getEnd()))){
					count++;
				}
				if((event.getStart().after(eventList.get(i).getStart())		//加入区间在已存在的某个事件区间中
						||event.getStart().equals(eventList.get(i).getStart()))
					&&
					(event.getEnd().before(eventList.get(i).getEnd())
						||event.getEnd().equals(eventList.get(i).getEnd()))){
					count++;
				}
				if(((event.getStart().after(eventList.get(i).getStart())	//加入区间的start在已存在的某个事件区间中
						&&event.getStart().before(eventList.get(i).getEnd()))
						||event.getStart().equals(eventList.get(i).getStart()))
					&&
					(event.getEnd().after(eventList.get(i).getEnd())
						||event.getEnd().equals(eventList.get(i).getEnd()))){
					count++;
				}
				if((event.getStart().before(eventList.get(i).getStart())	//加入区间包含已存在的某个事件区间
						||event.getStart().equals(eventList.get(i).getStart()))
					&&
					(event.getEnd().after(eventList.get(i).getEnd())
						||event.getEnd().equals(eventList.get(i).getEnd()))){
					count++;
				}
			}
			if(count == 0){
				event.setId(tempId);
				tempId++;
				eventList.add(event);
				return 0;
			}else{
				return 2;
			}
		}
	}
	
	/**
     * 根据时间区间查找符合的事件
     * @param start
     * @param end
     * @param username
     * @param password
     * @return List<event>
     */
	@WebMethod
	public List<Event> query(Date start, Date end, String username, String password){
		List<Event> eList = new ArrayList<Event>();
		//查询区间应查询囊括在区间中的会议
		for(int i=0;i<eventList.size();i++){
			if(((eventList.get(i).getStart().after(start)			//有在查询start后面的start的事件
				||eventList.get(i).getStart().equals(start))		//有等于查询start的start的事件
					&&(eventList.get(i).getStart().before(end)	//有在查询end前面的start的事件
					||eventList.get(i).getStart().equals(end)))	//有等于查询end的start的事件
				||
				((eventList.get(i).getEnd().after(start)				//有在查询start后面的end的事件
						||eventList.get(i).getEnd().equals(start))	//有等于查询start的end的事件
							&&(eventList.get(i).getEnd().before(end)	//有在查询end前前面的end的事件
							||eventList.get(i).getEnd().equals(end)))){//有等于查询end的end的事件
				eList.add(eventList.get(i));
			}
		}
		return eList;
	}
	
    /**
     * 根据事件ID删除用户创建的事件。如果不存在则取消操作。
     * @param meetingId
     * @param username
     * @param password
     * @return 删除成功返回0,找不到id返回1,不是当前账户的会议返回2
     */
	@WebMethod
	public int delete(int eventId, String username, String password){
		for(int i=0;i<eventList.size();i++){
			if(eventList.get(i).getId() == eventId){
				if(eventList.get(i).getCreator().equals(username)){
					eventList.remove(i);
					return 0;
				}else{
					return 2;
				}
			}
		}
		return 1;
	}
	
	 /**
     * 清除该用户创建的所有事件
     * @param username
     * @param password
     * @return 清除成功返回true,否则返回false
     */
	@WebMethod
	public void clear(String username, String password){
		for(int i=0;i<eventList.size();i++){
			if(eventList.get(i).getCreator().equals(username)){
				eventList.remove(i);
			}
		}
		
	}

    /**
     * 检查用户是否存在且密码正确
     * @param username
     * @param password
     * @return 登录成功返回 0,帐号密码不匹配返回1,未注册返回2
     */
	@WebMethod
	public int login(String username, String password){
		for(int i=0;i<userList.size();i++){
			if(userList.get(i).getUserName().equals(username)){
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
