package org.simmi.server;

import java.util.List;

import org.simmi.client.Einkunn;
import org.simmi.client.SmasagaSubservice;
import org.simmi.client.Subsaga;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SmasagaSubserviceImpl extends RemoteServiceServlet implements SmasagaSubservice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Subsaga getShortstory(String keystr) {
		Key key = KeyFactory.stringToKey( keystr );
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity 	ent = datastore.get( key );
			
			String name = (String)ent.getProperty("name");
			String type = (String)ent.getProperty("type");
			String author = (String)ent.getProperty("author");
			String authorsynonim = (String)ent.getProperty("authorsynonim");
			String language = (String)ent.getProperty("language");
			String url = (String)ent.getProperty("url");
			String summary = (String)ent.getProperty("summary");
			
			Subsaga 	smasaga = new Subsaga( name, type, author, authorsynonim, language, url );
			smasaga.setSummary( summary );
			
			Object love = ent.getProperty("love");
			smasaga.setLove( love == null ? false : (Boolean)love );
			Object horror = ent.getProperty("horror");
			smasaga.setHorror( horror == null ? false : (Boolean)horror );
			Object comedy = ent.getProperty("comedy");
			smasaga.setComedy( comedy == null ? false : (Boolean)comedy );
			Object tragedy = ent.getProperty("tragedy");
			smasaga.setTragedy( tragedy == null ? false : (Boolean)tragedy );
			Object criminal = ent.getProperty("criminal");
			smasaga.setCriminal( criminal == null ? false : (Boolean)criminal );
			Object supernatural = ent.getProperty("supernatural");
			smasaga.setSupernatural( supernatural == null ? false : (Boolean)supernatural );
			Object science = ent.getProperty("science");
			smasaga.setScience( science == null ? false : (Boolean)science );
			Object children = ent.getProperty("children");
			smasaga.setChildren( children == null ? false : (Boolean)children );
			Object adolescent = ent.getProperty("adolescent");
			smasaga.setAdolescent( adolescent == null ? false : (Boolean)adolescent );
			Object erotic = ent.getProperty("erotic");
			smasaga.setErotic( erotic == null ? false : (Boolean)erotic );
			Object historical = ent.getProperty("historical");
			smasaga.setHistorical( historical == null ? false : (Boolean)historical );
			Object truestory = ent.getProperty("truestory");
			smasaga.setTruestory( truestory == null ? false : (Boolean)truestory );
			Object adventure = ent.getProperty("adventure");
			smasaga.setAdventure( adventure == null ? false : (Boolean)adventure );
			Object poem = ent.getProperty("poem");
			smasaga.setPoem( poem == null ? false : (Boolean)poem );
			Object tobecontinued = ent.getProperty("continue");
			smasaga.setContinue( tobecontinued == null ? false : (Boolean)tobecontinued );
			
			smasaga.setKey( keystr );
			
			Query query = new Query("einkunn");
			query.setFilter( new Query.FilterPredicate( "story", FilterOperator.EQUAL, keystr ) );
			List<Entity> lentity = datastore.prepare( query ).asList( FetchOptions.Builder.withDefaults() );
			
			Einkunn[] einkunnir = new Einkunn[ lentity.size() ];
			for( int i = 0; i < einkunnir.length; i++ ) {
				Entity e = lentity.get(i);
				String 	user = (String)e.getProperty("user");
				String 	comment = (String)e.getProperty("comment");
				Long grade = (Long)e.getProperty("grade");
				
				Einkunn enk = new Einkunn( user, keystr, comment, grade );
				enk.setKey( KeyFactory.keyToString(e.getKey()) );
				einkunnir[i] = enk;
			}
			smasaga.setGrades( einkunnir );
			
			return smasaga;
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public String updateShortstory(String keystr, String name, String author, String lang, String summary,
			boolean love, boolean comedy, boolean tragedy, boolean horror,
			boolean erotik, boolean science, boolean child, boolean adolescent,
			boolean criminal, boolean historical, boolean truestory,
			boolean supernatural, boolean adventure, boolean poem, boolean tobecontinue ) {
		Key key = KeyFactory.stringToKey( keystr );
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity e = datastore.get( key );
			e.setProperty("name", name);
			e.setProperty("authorsynonim", author);
			e.setProperty("language", lang);
			e.setProperty("summary", summary);
			e.setProperty("love", love);
			e.setProperty("comedy", comedy);
			e.setProperty("tragedy", tragedy);
			e.setProperty("horror", horror);
			e.setProperty("erotic", erotik);
			e.setProperty("science", science);
			e.setProperty("children", child);
			e.setProperty("adolescent", adolescent);
			e.setProperty("criminal", criminal);
			e.setProperty("historical", historical);
			e.setProperty("truestory", truestory);
			e.setProperty("supernatural", supernatural);
			e.setProperty("adventure", adventure);
			e.setProperty("poem", poem);
			e.setProperty("continue", tobecontinue);
			
			datastore.put( e );
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}

	@Override
	public String updateEinkunn(String keystr, String comment, int grade, String user, String story) {
		Key key = keystr == null || keystr.length() == 0 ? null : KeyFactory.stringToKey( keystr );
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity e = key == null ? new Entity("einkunn") : datastore.get( key );
			e.setProperty("comment", comment);
			e.setProperty("grade", grade);
			e.setProperty("user", user);
			e.setProperty("story", story);
			
			datastore.put( e );
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
}
