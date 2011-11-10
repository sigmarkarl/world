package org.simmi.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SmasagaSubserviceAsync {

	void getShortstory(String keystr, AsyncCallback<Subsaga> callback);

	void updateShortstory(String keystr, String name, String author, String summary,
			boolean love, boolean comedy, boolean tragedy, boolean horror,
			boolean erotik, boolean science, boolean child, boolean adolescent,
			boolean criminal, boolean historical, boolean truestory,
			boolean supernatural, boolean adventure, boolean poem, boolean tobecontine,
			AsyncCallback<String> callback);

	void updateEinkunn(String keystr, String comment, int grade, String user, String story, AsyncCallback<String> callback);
}
