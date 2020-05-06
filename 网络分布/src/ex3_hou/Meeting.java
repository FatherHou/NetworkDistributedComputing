/**
 * 
 */
package ex3_hou;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hou
 * 会议类
 */
public class Meeting implements Serializable {
	private int id;
	private String hostUser;
	private String otherUser;
	private Date start;
	private Date end;
	private String title;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the hostUser
	 */
	public String getHostUser() {
		return hostUser;
	}
	/**
	 * @param hostUser the hostUser to set
	 */
	public void setHostUser(String hostUser) {
		this.hostUser = hostUser;
	}
	/**
	 * @return the otherUser
	 */
	public String getOtherUser() {
		return otherUser;
	}
	/**
	 * @param otherUser the otherUser to set
	 */
	public void setOtherUser(String otherUser) {
		this.otherUser = otherUser;
	}
	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public Date getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(Date end) {
		this.end = end;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @param id
	 * @param hostUser
	 * @param otherUser
	 * @param start
	 * @param end
	 * @param title
	 */
	public Meeting(int id, String hostUser, String otherUser, Date start, Date end, String title) {
		super();
		this.id = id;
		this.hostUser = hostUser;
		this.otherUser = otherUser;
		this.start = start;
		this.end = end;
		this.title = title;
	}
	/**
	 * 
	 */
	public Meeting() {
		super();
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm");	//对输出的日期类型进行规范化处理
		String tempStart = df.format(start);
		String tempEnd = df.format(end);
		return "Meeting [id=" + id + ", hostUser=" + hostUser + ", otherUser=" + otherUser + ", start=" + tempStart
				+ ", end=" + tempEnd + ", title=" + title + "]";
	}
	
}
