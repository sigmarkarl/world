package org.simmi.server;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simmi.client.FrislbokService;
import org.simmi.client.Person;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FrislbokServiceImpl extends RemoteServiceServlet implements FrislbokService {
	private static final long serialVersionUID = 1L;

	@Override
	public Person fetchFromFacebookId(String uid) {
		Person retPerson = null;
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("person");
		query.addFilter("facebookid", FilterOperator.EQUAL, uid);
		PreparedQuery pq = datastore.prepare( query );
		List<Entity> personEntities = pq.asList( FetchOptions.Builder.withDefaults() );
		
		if( personEntities.size() > 0 ) {
			Entity 	e = personEntities.get(0);
			String 	name = (String)e.getProperty("name");
			Date 	dateOfBirth = (Date)e.getProperty("dateofbirth");
			Long 	gender = (Long)e.getProperty("gender");
			String	comment = (String)e.getProperty("comment");
			String	fatherKey = (String)e.getProperty("father");
			String	motherKey = (String)e.getProperty("mother");
			String	fbuser = (String)e.getProperty("fbuser");
			String	fbwriter = (String)e.getProperty("fbwriter");
			
			retPerson = new Person( name, dateOfBirth, gender.intValue() );
			retPerson.setComment( comment );
			retPerson.setKey( KeyFactory.keyToString(e.getKey()) );
			retPerson.setFacebookid( uid );
			retPerson.setFacebookUsername( fbuser );
			retPerson.setFbwriter( fbwriter );
			
			return retPerson;
		}
		
		return retPerson;
	}

	@Override
	public Person fetchFromKeyString(String key) {
		Person[] persons = fetchFromKeyStringArray( new String[] { key } );
		if( persons.length > 0 ) return persons[0];
		
		return null;
	}

	@Override
	public Person[] fetchFromKeyStringArray(String[] keys) {		
		Set<Key>	keySet = new HashSet<Key>();
		for( String keystr : keys ) keySet.add( KeyFactory.stringToKey(keystr) );
		return fetchFromKeySet( keySet );
	}	
	
	private Person[] fetchFromKeySet( Set<Key>	keyset ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Map<Key,Entity> entityMap = datastore.get( keyset );
		
		/*Query query = new Query("person");
		query.addFilter("key", FilterOperator.IN, keys);
		PreparedQuery pq = datastore.prepare( query );
		List<Entity> personEntities = pq.asList( FetchOptions.Builder.withDefaults() );*/
		
		int i = 0;
		Person[] persons = new Person[ entityMap.size() ];
		for( Key key : entityMap.keySet() ) {
			Entity e = entityMap.get(key);
			
			String 	name = (String)e.getProperty("name");
			Date 	dateOfBirth = (Date)e.getProperty("dateofbirth");
			Long 	gender = (Long)e.getProperty("gender");
			String	comment = (String)e.getProperty("comment");
			//String	fatherKey = (String)e.getProperty("father");
			//String	motherKey = (String)e.getProperty("mother");
			
			Person retPerson = new Person( name, dateOfBirth, gender.intValue() );
			retPerson.setComment( comment );
			retPerson.setKey( KeyFactory.keyToString(e.getKey()) );
			
			i++;
		}
		return persons;
	}

	@Override
	public String savePerson(Person person) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String keystr = person.getKey();
		
		Entity e;
		if( keystr == null ) {
			e = new Entity("person");			
		} else {
			try {
				e = datastore.get( KeyFactory.stringToKey( keystr ) );
			} catch (EntityNotFoundException e1) {
				e = new Entity("person");
			}
		}
		e.setProperty("name", person.getName());
		e.setProperty("dateofbirth", person.getDateOfBirth());
		e.setProperty("gender", person.getGender());
		e.setProperty("fbwriter", person.getFbwriter());
		e.setProperty("facebookid", person.getFacebookid());
		e.setProperty("facebookusername", person.getFacebookUsername());
		e.setProperty("comment", person.getComment());
		
		Person father = person.getFather();
		if( father != null && father.getKey() != null ) e.setProperty( "father", father.getKey() );
		Person mother = person.getMother();
		if( mother != null && mother.getKey() != null ) e.setProperty( "mother", mother.getKey() );
		
		Set<String>	childIds = new HashSet<String>();
		for( Person child : person.getChildren() ) childIds.add( child.getKey() );
		e.setProperty("children", childIds);
		
		Set<String>	sibIds = new HashSet<String>();
		for( Person sibling : person.getSiblings() ) sibIds.add( sibling.getKey() );
		e.setProperty("siblings", sibIds);

		datastore.put( e );
		
		return KeyFactory.keyToString( e.getKey() );
	}

	@Override
	public String savePersonArray(Person[] persons) {
		for( Person person : persons ) {
			savePerson( person );
		}
		
		return null;
	}
}
