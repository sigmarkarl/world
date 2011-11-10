package org.simmi.client;

import org.simmi.shared.Saga;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SmasagaServiceAsync {

	void getAllShortstories(AsyncCallback<Saga[]> callback);
	void saveShortStory(Saga saga, String filename, String binary, AsyncCallback<String> callback);
	void deleteShortstory(Saga saga, AsyncCallback<String> callback);
	void getShortstory(String keystr, AsyncCallback<Saga> callback);
}
