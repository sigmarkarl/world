package org.simmi.client;

import java.io.Serializable;
import java.util.Date;

public class Person implements Serializable {
	private static final long serialVersionUID = 1L;
	
	String	name;
	int		gender;
	Date	dateOfBirth;
	String	comment;
	String	key;
	String	facebookid;
	String	facebookusername;
	String	fbwriter;
	
	public String getFacebookid() {
		return facebookid;
	}

	public void setFacebookid(String facebookid) {
		this.facebookid = facebookid;
	}
	
	public String getFacebookUsername() {
		return facebookusername;
	}

	public void setFacebookUsername(String facebookusername) {
		this.facebookusername = facebookusername;
	}

	public String getFbwriter() {
		return fbwriter;
	}

	public void setFbwriter(String fbwriter) {
		this.fbwriter = fbwriter;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGender() {
		return gender;
	}

	public void setGender( int gender) {
		this.gender = gender;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Person() {

	}
	
	public Person( String name, Date dateOfBirth, int gender ) {
		this.name = name;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
	}
	
	Person		father;
	Person		mother;
	Person[]	children;
}
