package org.simmi.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FrislbokServiceAsync {

	void fetchFromFacebookId(String uid, AsyncCallback<Person> callback);

	void fetchFromKeyString(String key, AsyncCallback<Person> callback);

	//void fetchFromKeySet(Set<Key> keyset, AsyncCallback<Person[]> callback);

	void fetchFromKeyStringArray(String[] keys, AsyncCallback<Person[]> callback);

	void savePerson(Person person, AsyncCallback<String> callback);

}
