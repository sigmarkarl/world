package org.simmi.client;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
		children = new HashSet<Person>();
		siblings = new HashSet<Person>();
	}
	
	public Person( String name, Date dateOfBirth, int gender ) {
		this();
		this.name = name;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
	}
	
	Person			father;
	Person			mother;
	Set<Person>		children;
	Set<Person>		siblings;
	
	public boolean isMale() {
		return true;
	}
	
	public Person getFather() {
		return father;
	}
	
	public Person getMother() {
		return mother;
	}
	
	public Set<Person> getChildren() {
		return children;
	}
	
	public void setFather( Person father ) {
		this.father = father;
		father.addChild( this );
	}
	
	public void setMother( Person mother ) {
		this.mother = mother;
		mother.addChild( this );
	}
	
	public void setParent( Person parent ) {
		if( parent.isMale() ) setFather( parent );
		else setMother( parent );
	}
	
	public void setChildren( Set<Person> children ) {
		this.children = children;
	}
	
	public boolean addChild( Person child ) {
		if( this.children.add( child ) ) {
			child.setParent( this );
			return true;
		}
		
		return false;
	}
	
	public Set<Person> getSiblings() {
		return siblings;
	}
	
	public void setSiblings( Set<Person> siblings ) {
		this.siblings = siblings;
	}
	
	public void addSibling( Person sibling ) {
		if( this.siblings.add( sibling ) ) sibling.addSibling( this );
	}
	
	public int hashCode() {
		return super.hashCode();
	}
}
