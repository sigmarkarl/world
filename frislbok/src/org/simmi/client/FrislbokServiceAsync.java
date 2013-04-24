package org.simmi.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FrislbokServiceAsync {

	void fetchFromFacebookId(String uid, AsyncCallback<Person> callback);

	void fetchFromKeyString(String key, AsyncCallback<Person> callback);

	//void fetchFromKeySet(Set<Key> keyset, AsyncCallback<Person[]> callback);

	void fetchFromKeyStringArray(String[] keys, AsyncCallback<Person[]> callback);

	void savePerson(Person person, AsyncCallback<String> callback);

	void savePersonArray(Person[] persons, AsyncCallback<String> callback);

	void login(String user, String password, AsyncCallback<String> callback);

	void islbok_get(String session, String id, AsyncCallback<String> callback);

	void islbok_children(String session, String id, AsyncCallback<String> callback);
	
	void islbok_siblings(String session, String id, AsyncCallback<String> callback);

	void fetchFromIslbokId(String islbokid, AsyncCallback<Person> callback);

	void islbok_ancestors(String session, String id, AsyncCallback<String> callback);

}
