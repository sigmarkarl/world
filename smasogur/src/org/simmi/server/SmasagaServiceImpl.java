package org.simmi.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServlet;

import oauth.signpost.OAuth;

import org.apache.commons.codec.binary.Base64;
import org.simmi.client.SmasagaService;
import org.simmi.shared.Saga;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SmasagaServiceImpl extends RemoteServiceServlet implements SmasagaService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Saga[] getAllShortstories() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("smasaga");
		List<Entity> soguEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		Saga[] smasogur = new Saga[ soguEntities.size() ];
		
		Map<String,Saga>	sagaMap = new HashMap<String,Saga>();
		
		int i = 0;
		for( Entity e : soguEntities ) {
			Saga smasaga = new Saga( (String)e.getProperty("name"), (String)e.getProperty("type"), (String)e.getProperty("author"), (String)e.getProperty("authorsynonim"), (String)e.getProperty("language"), (String)e.getProperty("url") );
			Object love = e.getProperty("love");
			smasaga.setLove( love == null ? false : (Boolean)love );
			Object horror = e.getProperty("horror");
			smasaga.setHorror( horror == null ? false : (Boolean)horror );
			Object comedy = e.getProperty("comedy");
			smasaga.setComedy( comedy == null ? false : (Boolean)comedy );
			Object tragedy = e.getProperty("tragedy");
			smasaga.setTragedy( tragedy == null ? false : (Boolean)tragedy );
			Object criminal = e.getProperty("criminal");
			smasaga.setCriminal( criminal == null ? false : (Boolean)criminal );
			Object supernatural = e.getProperty("supernatural");
			smasaga.setSupernatural( supernatural == null ? false : (Boolean)supernatural );
			Object science = e.getProperty("science");
			smasaga.setScience( science == null ? false : (Boolean)science );
			Object children = e.getProperty("children");
			smasaga.setChildren( children == null ? false : (Boolean)children );
			Object adolescent = e.getProperty("adolescent");
			smasaga.setAdolescent( adolescent == null ? false : (Boolean)adolescent );
			Object erotic = e.getProperty("erotic");
			smasaga.setErotic( erotic == null ? false : (Boolean)erotic );
			Object historical = e.getProperty("historical");
			smasaga.setHistorical( historical == null ? false : (Boolean)historical );
			Object truestory = e.getProperty("truestory");
			smasaga.setTruestory( truestory == null ? false : (Boolean)truestory );
			Object adventure = e.getProperty("adventure");
			smasaga.setAdventure( adventure == null ? false : (Boolean)adventure );
			Object poem = e.getProperty("poem");
			smasaga.setPoem( poem == null ? false : (Boolean)poem );
			Object tobecontinued = e.getProperty("continue");
			smasaga.setContinue( tobecontinued == null ? false : (Boolean)tobecontinued );
			
			String sagaKey = KeyFactory.keyToString(e.getKey());
			sagaMap.put( sagaKey, smasaga );
			
			smasaga.setKey( sagaKey );			
			smasogur[i++] = smasaga;
		}
		
		query = new Query("einkunn");
		List<Entity> gradeEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		for( Entity e : gradeEntities ) {
			String 	saga = (String)e.getProperty("story");
			Long 	grade = (Long)e.getProperty("grade");
			
			if( saga != null && saga.length() > 0 ) {
				Saga smasaga = sagaMap.get(saga);
				smasaga.setGradeNum( smasaga.getGradeNum()+1 );
				smasaga.setGradeSum( (int)(smasaga.getGradeSum()+grade) );
			}
		}
		
		return smasogur;
	}
	
	public static String getParamstr( Map<String,String> map, List<String> set ) {
		String ret = "";
		
		for( String s : set ) {
			if( ret.length() == 0 ) ret += s + "=\"" + map.get(s) + "\"";
			else ret += ", " + s + "=\"" + map.get(s) + "\"";
		}
		
		return ret;
	}
	
	public static boolean dropboxSave( String target, String tokenkey, String tokensecret, String fname, byte[] contents, HttpServlet servlet ) throws IOException {
		Map<String,String>	parameters = new TreeMap<String,String>();
		//URL u;
		//fname = OAuth.percentEncode(fname);//URLEncoder.encode(fname, "UTF-8");
		//String origname = fname;
		
		servlet.log( fname );
		String fnameDecoded = URLDecoder.decode( fname, "UTF-8" );
		//fname.
		//fname = fname.replace(" ", "%20");
		//servlet.log( fname );
		parameters.put("file", fname );
		parameters.put("oauth_consumer_key", "jemmmn3c5ot8rdu");
		parameters.put("oauth_token", tokenkey );
		parameters.put("oauth_timestamp", Long.toString(System.currentTimeMillis()/1000) );
		parameters.put("oauth_signature_method", "HMAC-SHA1");
		parameters.put("oauth_nonce", Long.toString(Math.abs(new Random().nextLong())) );
		parameters.put("oauth_version", "1.0" );
		
		String paramstr = null;
		for( String par : parameters.keySet() ) {
			if( paramstr == null ) paramstr = par + "=" + parameters.get(par);
			else paramstr += "&" + par + "=" + parameters.get(par);
		}
		
		//String keyString = OAuth.percentEncode("9or8lsn165d44qv") + '&' + OAuth.percentEncode(tokensecret);
		String keyString = URLEncoder.encode("9or8lsn165d44qv", "UTF-8").replace("+", "%20") + '&' + URLEncoder.encode(tokensecret, "UTF-8").replace("+", "%20");
        byte[] keyBytes = keyString.getBytes( "UTF-8" );

        String MAC_NAME = "HmacSHA1";
        //Base64 base64 = new Base64();
        SecretKey key = new SecretKeySpec(keyBytes, MAC_NAME);
        Mac mac = null;
		try {
			mac = Mac.getInstance(MAC_NAME);
			mac.init(key);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		//String str = OAuth.percentEncode(target) + "&" + OAuth.percentEncode(paramstr);
		String str = URLEncoder.encode(target, "UTF-8").replace("+", "%20") + "&" + URLEncoder.encode(paramstr, "UTF-8").replace("+", "%20");
        //return "GET&" + str;
        String sbs = "POST&" + str; //generate( baseurl, paramstr );
        byte[] text = sbs.getBytes( "UTF-8" );
        
        System.err.println(sbs);
        servlet.log( "signature string: "+sbs );

        byte[] b = mac.doFinal(text);
        //byte[] nb = base64.encode(b);
        //String sign = new String(nb).trim();
        byte[] signb = Base64.encodeBase64(b);
        String sign = new String( signb );
        parameters.put("oauth_signature", sign );
		
		URL url = new URL(target);
		HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
		
		String dispstr = "Content-Disposition: form-data; name=\"file\"; filename=\""+fname+"\"\n\n";
		httpConnection.setRequestMethod("POST");
		httpConnection.setDoInput( true );
		httpConnection.setDoOutput( true );
		String[] incl = {"file", "oauth_token", "oauth_consumer_key", "oauth_version", "oauth_signature_method", "oauth_timestamp", "oauth_nonce", "oauth_signature"};
		String stuff = getParamstr( parameters, Arrays.asList( incl ) );
		httpConnection.setRequestProperty("Authorization", "OAuth "+stuff);
		httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=46-B_DuxmY9qurDm33PMiHpZ2dP7Lr"); //req.getContentType());
		
		String cont = "--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr\n";
		cont += dispstr;
		//cont += filecontents;
		cont += "\n--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr--\n";
		int clen = cont.length()+contents.length;
		
		httpConnection.setRequestProperty("Content-Length", ""+clen);
		httpConnection.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2" );
		httpConnection.setRequestProperty("Connection", "keep-alive");
		httpConnection.setRequestProperty("Expect", "100-Continue");
		
		OutputStream os = httpConnection.getOutputStream();
		os.write( "--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr\n".getBytes() );
		os.write( dispstr.getBytes() );
		os.write( contents );
		os.write( "\n--46-B_DuxmY9qurDm33PMiHpZ2dP7Lr--\n".getBytes() );
		
		InputStream is = httpConnection.getInputStream();
		byte[] bb = new byte[256];
		int r = is.read( bb );
		String s = "";
		while( r > 0 ) {
			s += new String( bb, 0, r );
			r = is.read( bb );
		}
		is.close();
		os.close();
		
		int resp = httpConnection.getResponseCode();
		System.out.println( s );
		System.out.println( resp );
		servlet.log( "response: "+resp+"  "+s );
		
		httpConnection.disconnect();
		
		return resp >= 200 && resp < 300;
	}
	
	public static boolean dropToBox( String fname, byte[] contents, HttpServlet servlet ) {
    	try {            
			String urlstr = "https://api.dropbox.com/0/token?email=sigmarkarl@gmail.com&password=skc.311";
			String oauth = "&oauth_consumer_key=jemmmn3c5ot8rdu";
			
			URL url = new URL(urlstr+oauth);
			InputStream is = url.openStream();
			
			byte[] bb = new byte[256];
			int r = is.read( bb );
			String s = "";
			while( r > 0 ) {
				s += new String( bb, 0, r );
				r = is.read( bb );
			}
			is.close();
			
			Map<String,String>	json = new HashMap<String,String>();
			String sub = s.substring( s.indexOf('{')+1, s.indexOf('}') );
			String[] split = sub.split(",");
			for( String sp : split ) {
				String[] subspl = sp.split(":");
				json.put( "oauth_"+subspl[0].trim().replace("\"", ""), subspl[1].trim().replace("\"", "") );
			}
			String tokenkey = json.get("oauth_token");
			String tokensecret = json.get("oauth_secret");
            String baseurl = "api-content.getdropbox.com";
			//String baseurl = "localhost:8899";
            String target = "https://"+baseurl+"/0/files/dropbox/Public";
            return dropboxSave( target, tokenkey, tokensecret, fname, contents, servlet );
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Total failure trying to access the trusted authenticator." + e;
        }
    	return false;
    }

	@Override
	public String saveShortStory(Saga saga, String filename, String binary) {
		try {
			String urlstr = "http://dl.dropbox.com/u/10024658/"+URLEncoder.encode(filename, "UTF-8");
			saga.setUrl( urlstr );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if( binary != null ) {
			byte[] bbinary = new byte[ binary.length() ];
			for( int i = 0; i < binary.length(); i++ ) {
				bbinary[i] = (byte)binary.charAt(i);
			}
			dropToBox( filename, bbinary, this );
		}
		
		Entity	smasaga = new Entity("smasaga");
		smasaga.setProperty("name", saga.getName());
		smasaga.setProperty("type", saga.getType());
		smasaga.setProperty("author", saga.getAuthor());
		smasaga.setProperty("authorsynonim", saga.getAuthorSynonim());
		smasaga.setProperty("language", saga.getLanguage());
		smasaga.setProperty("url", saga.getUrl());
		
		smasaga.setProperty("love", saga.getLove());
		smasaga.setProperty("horror", saga.getHorror());
		smasaga.setProperty("comedy", saga.getComedy());
		smasaga.setProperty("tragedy", saga.getTragedy());
		smasaga.setProperty("criminal", saga.getCriminal());
		smasaga.setProperty("supernatural", saga.getSupernatural());
		smasaga.setProperty("science", saga.getScience());
		smasaga.setProperty("children", saga.getChildren());
		smasaga.setProperty("adolescent", saga.getAdolescent());
		smasaga.setProperty("erotic", saga.getErotic());
		smasaga.setProperty("historical", saga.getHistorical());
		smasaga.setProperty("truestory", saga.getTruestory());
		smasaga.setProperty("adventure", saga.getAdventure());
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key key = datastore.put( smasaga );
		
		return KeyFactory.keyToString(key);
	}

	@Override
	public String deleteShortstory(Saga saga) {
		Key key = KeyFactory.stringToKey( saga.getKey() );
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.delete( key );
		
		return null;
	}

	@Override
	public Saga getShortstory(String keystr) {
		Key key = KeyFactory.stringToKey( keystr );
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity 	ent = datastore.get( key );
			
			String name = (String)ent.getProperty("name");
			String type = (String)ent.getProperty("type");
			String author = (String)ent.getProperty("author");
			String language = (String)ent.getProperty("language");
			String authorsynonim = (String)ent.getProperty("authorsynonim");
			String url = (String)ent.getProperty("url");
			
			Saga 	smasaga = new Saga( name, type, author, authorsynonim, language, url );
			smasaga.setLove( (Boolean)ent.getProperty("love") );
			smasaga.setHorror( (Boolean)ent.getProperty("horror") );
			smasaga.setComedy( (Boolean)ent.getProperty("comedy") );
			smasaga.setTragedy( (Boolean)ent.getProperty("tragedy") );
			smasaga.setCriminal( (Boolean)ent.getProperty("criminal") );
			smasaga.setSupernatural( (Boolean)ent.getProperty("supernatural") );
			smasaga.setScience( (Boolean)ent.getProperty("science") );
			smasaga.setChildren( (Boolean)ent.getProperty("children") );
			smasaga.setAdolescent( (Boolean)ent.getProperty("adolescent") );
			smasaga.setErotic( (Boolean)ent.getProperty("erotic") );
			smasaga.setHistorical( (Boolean)ent.getProperty("historical") );
			smasaga.setTruestory( (Boolean)ent.getProperty("truestory") );
			smasaga.setAdventure( (Boolean)ent.getProperty("adventure") );
			
			return smasaga;
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
