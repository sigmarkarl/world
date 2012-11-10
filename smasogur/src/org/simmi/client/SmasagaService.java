package org.simmi.client;

import org.simmi.shared.Saga;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("SmasagaService")
public interface SmasagaService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static SmasagaServiceAsync instance;
		public static SmasagaServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(SmasagaService.class);
			}
			return instance;
		}
	}
	
	Saga[] 	getAllShortstories();
	Saga	getShortstory( String keystr );
	String 	saveShortStory(Saga saga, String filename, String fileurl, String binary);
	String 	deleteShortstory( Saga saga );
}
