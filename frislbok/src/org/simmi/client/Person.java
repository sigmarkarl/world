package org.simmi.client;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Person implements IsSerializable {
	//private static final long serialVersionUID = 1L;
	
	String	name;
	int		gender;
	Date	dateOfBirth;
	String	comment;
	String	key;
	String	islbokid;
	String	islbokuser;
	String	islbokpass;
	String	facebookid;
	String	facebookusername;
	String	fbwriter;
	String	geocode;
	String	imgurl;
	
	public String getIslbokid() {
		return islbokid;
	}

	public void setIslbokid(String islbokid) {
		this.islbokid = islbokid;
	}

	public String getIslbokuser() {
		return islbokuser;
	}

	public void setIslbokuser(String islbokuser) {
		this.islbokuser = islbokuser;
	}

	public String getIslbokpass() {
		return islbokpass;
	}

	public void setIslbokpass(String islbokpass) {
		this.islbokpass = islbokpass;
	}
	
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
		//children = new HashSet<Person>();
		//siblings = new HashSet<Person>();
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
	Set<Person>		mates;
	
	public boolean isMale() {
		return gender == 1;
	}
	
	public boolean isFemale() {
		return gender == 2;
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
		if( this.father != null ) {
			father.addChildren( this.father.getChildren() );
		}
		this.father = father;
		father.addChild( this );
	}
	
	public void setMother( Person mother ) {
		if( this.mother != null ) {
			mother.addChildren( this.mother.getChildren() );
		}
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
	
	public void addChildren( Set<Person> newchildren ) {
		if( this.children == null ) this.children = new HashSet<Person>();
		if( newchildren != null ) this.children.addAll( newchildren );
	}
	
	public boolean addChild( Person child ) {
		if( children == null ) children = new HashSet<Person>();
		if( children.add( child ) ) {
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
	
	@Override
	public boolean equals( Object other ) {
		if( other!= null ) {
			Person otherPerson = (Person)other;
			return this.getIslbokid().equals(otherPerson.getIslbokid()) || this.getFacebookid().equals(otherPerson.getFacebookid()) || this.getKey().equals(otherPerson.getKey());
		}
		
		return false;
	}
	
	public void addSibling( Person sibling ) {
		if( siblings == null ) siblings = new HashSet<Person>();
		if( !this.equals(sibling) && siblings.add( sibling ) ) sibling.addSibling( this );
	}
	
	public int hashCode() {
		return super.hashCode();
	}
}
