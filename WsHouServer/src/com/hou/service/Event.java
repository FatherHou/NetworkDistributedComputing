/**
 * 
 */
package com.hou.service;

import java.util.Date;

/**
 * @author hou
 * 事件类
 */
public class Event {
	private int Id;
	private String creator;
	private Date start;
	private Date end;
	private String description;
	/**
	 * @return the id
	 */
	public int getId() {
		return Id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		Id = id;
	}
	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param id
	 * @param creator
	 * @param start
	 * @param end
	 * @param description
	 */
	public Event(int id, String creator, Date start, Date end, String description) {
		super();
		Id = id;
		this.creator = creator;
		this.start = start;
		this.end = end;
		this.description = description;
	}
	
	/**
	 * 
	 */
	public Event() {
		super();
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Event [Id=" + Id + ", creator=" + creator + ", start=" + start + ", end=" + end + ", description="
				+ description + "]";
	}
}
