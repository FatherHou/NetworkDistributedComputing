/**
 * 
 */
package exam2;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hou
 * 消息类
 */
public class Message implements Serializable{
	private String sender;
	private String receiver;
	private Date date;
	private String body;
	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}
	/**
	 * @param sender the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}
	/**
	 * @return the reciever
	 */
	public String getReciever() {
		return receiver;
	}
	/**
	 * @param reciever the reciever to set
	 */
	public void setReciever(String reciever) {
		this.receiver = reciever;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	/**
	 * @param sender
	 * @param reciever
	 * @param date
	 * @param body
	 */
	public Message(String sender, String reciever, Date date, String body) {
		super();
		this.sender = sender;
		this.receiver = reciever;
		this.date = date;
		this.body = body;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");	//对输出的日期类型进行规范化处理
		String temp = df.format(date);
		return "Message { 发送者:" + sender + ", 接收者:" + receiver + ", 发送时间:" + temp + ", 内容:" + body + " }";
	}
	
}
