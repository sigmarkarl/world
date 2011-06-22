package org.simmi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("FrislbokService")
public interface FrislbokService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static FrislbokServiceAsync instance;
		public static FrislbokServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(FrislbokService.class);
			}
			return instance;
		}
	}
	
	public Person fetchFromFacebookId( String uid );
	public Person fetchFromKeyString( String key );
	//public Person[] fetchFromKeySet( Set<Key> keyset );
	public Person[] fetchFromKeyStringArray(String[] keys);
	public String savePerson( Person person );
	public String savePersonArray( Person[] persons );
}
