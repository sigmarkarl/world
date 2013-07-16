package org.simmi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("SmasagaSubservice")
public interface SmasagaSubservice extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static SmasagaSubserviceAsync instance;
		public static SmasagaSubserviceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(SmasagaSubservice.class);
			}
			return instance;
		}
	}
	
	public Subsaga getShortstory(String keystr);
	public String updateShortstory( String keystr, String name, String author, String lang, String summary, boolean love, boolean comedy, boolean tragedy, boolean horror, boolean erotik, boolean science, boolean child, boolean adolescent, boolean criminal, boolean historical, boolean truestory, boolean supernatural, boolean adventure, boolean poem, boolean tobecontine );
	public String updateEinkunn( String keystr, String comment, int grade, String user, String story );
	public String sendAuthorMessage(String text);
}
