package org.simmi.server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.simmi.client.GreetingService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public String greetServer(String uid, String power) throws IllegalArgumentException {
		String result = "";
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		if( uid != null && uid.length() > 0 ) {
			Query query = new Query("superpower");
			query.addFilter( "uid", FilterOperator.EQUAL, uid );
			List<Entity> powerEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			
			Entity save = null;
			Set<String>	powers = new HashSet<String>();
			for( Entity e : powerEntities ) {
				Object criang = e.getProperty("criang");
				if( criang != null && (Boolean)criang ) powers.add( "criang" );
				
				Object quatel = e.getProperty("quatel");
				if( quatel != null && (Boolean)quatel ) powers.add( "quatel" );
				
				Object lorcon = e.getProperty("lorcon");
				if( lorcon != null && (Boolean)lorcon ) powers.add( "lorcon" );
				
				Object luck = e.getProperty("luck");
				if( luck != null && (Boolean)luck ) powers.add( "luck" );
				
				Object extlif = e.getProperty("extlif");
				if( extlif != null && (Boolean)extlif ) powers.add( "extlif" );
				
				Object dipill = e.getProperty("dipill");
				if( dipill != null && (Boolean)dipill ) powers.add( "dipill" );
				
				Object deflec = e.getProperty("deflec");
				if( deflec != null && (Boolean)deflec ) powers.add( "deflec" );
				
				save = e;
				break;
			}
			
			if( power != null && power.length() > 0 ) {
				String[] addpow = power.split("\t");
				powers.addAll( Arrays.asList(addpow) );
				
				if( save == null ) {
					save = new Entity("superpower");					
					save.setProperty("criang", powers.contains("criang"));
					save.setProperty("quatel", powers.contains("quatel"));
					save.setProperty("lorcon", powers.contains("lorcon"));
					save.setProperty("luck", powers.contains("luck"));
					save.setProperty("extlif", powers.contains("extlif"));
					save.setProperty("dipill", powers.contains("dipill"));
					save.setProperty("deflec", powers.contains("deflec"));
				} else {
					for( String pow : powers ) {
						save.setProperty(pow, true);
					}
				}
				
				save.setProperty("uid", uid);
				datastore.put( save );
			}
			
			for( String pow : powers ) {
				if( result.length() == 0 ) result = pow;
				else result += "\t"+pow;
			}
		}
		

		//Entity erm = new Entity("bug");
		//erm.setProperty("erm", result);
		//erm.setProperty("uid", uid);
		//datastore.put( erm );
		
		return result;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	@Override
	public String highScore(String name, String uid, int hscore, int w, int h, String superpowers, String bonuspower) throws IllegalArgumentException {
		String res = "";
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		if( name == null ) {
			Query query = new Query("highscore");
			query.addSort("hscore", SortDirection.DESCENDING);
			Iterator<Entity> itent = datastore.prepare(query).asIterator();
			if( itent.hasNext() ) {
				Entity e = itent.next();
				res = e.getProperty("name")+"\t"+e.getProperty("hscore")+"\t"+e.getProperty("width")+"\t"+e.getProperty("height");
				//List<Entity> powerEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
			}
		} else {
			Entity e = new Entity("highscore");
			e.setProperty("hscore", hscore);
			e.setProperty("name", name);
			e.setProperty("uid", uid);
			e.setProperty("width", w);
			e.setProperty("height", h);
			e.setProperty("superpowers", superpowers);
			datastore.put( e );
			
			if( bonuspower.length() > 0 ) {
				Query query = new Query("superpower");
				query.addFilter( "uid", FilterOperator.EQUAL, uid );
				List<Entity> powerEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
				
				Entity save = null;
				for( Entity ent : powerEntities ) {				
					save = ent;
					break;
				}
					
				if( save == null ) {
					save = new Entity("superpower");					
				}
				save.setProperty(bonuspower, true);
				save.setProperty("uid", uid);
				datastore.put( save );
			}
			
			res = name+"\t"+hscore+"\t"+w+"\t"+h;
		}
		
		return res;
	}
}