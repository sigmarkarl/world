package org.simmi.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String uid, String power, AsyncCallback<String> callback) throws IllegalArgumentException;
	void highScore(String name, String uid, int hscore, int w, int h, String superpowers, String bonuspower, AsyncCallback<String> callback);
}
