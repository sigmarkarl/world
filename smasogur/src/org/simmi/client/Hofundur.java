package org.simmi.client;

import java.io.Serializable;

public class Hofundur implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Hofundur() {
		
	}
	
	public Hofundur( String nafn, String user ) {
		this.nafn = nafn;
		this.user = user;
	}
	
	private String	nafn;
	private String	user;
	
	public String getName() {
		return nafn;
	}
	
	public String getUser() {
		return user;
	}
	
	public String toString() {
		return user+"_"+nafn;
	}
}
