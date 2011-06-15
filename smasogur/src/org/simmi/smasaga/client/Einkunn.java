package org.simmi.smasaga.client;

import java.io.Serializable;

public class Einkunn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Einkunn() {
		
	}
	
	public Einkunn( String user, String story, String comment, long grade ) {
		this.user = user;
		this.comment = comment;
		this.story = story;
		this.grade = grade;
	}
	
	String 	key;
	String	user;
	String	story;
	String	comment;
	long	grade;
	
	public String getKey() {
		return key;
	}
	
	public void setKey( String key ) {
		this.key = key;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getComment() {
		return comment;
	}
	
	public String getStory() {
		return story;
	}
}
