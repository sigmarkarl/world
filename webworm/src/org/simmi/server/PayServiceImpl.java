package org.simmi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PayServiceImpl extends HttpServlet {
	public void doPost( HttpServletRequest req, HttpServletResponse resp ) throws IOException {
		Map m = req.getParameterMap();
		StringBuilder sb = new StringBuilder();
		
		for( Object key : m.keySet() ) {
			String[] res = (String[])m.get( key );
			sb.append( key + " " + res[0] + "\n" );
		}
		
		String[] str = (String[])m.get("signed_request");
		String s = str[0];
		
		str = (String[])m.get("method");
		String method = str[0];
		this.getServletContext().log( "hey " + sb.toString() );
		
		String rec = null;
		str = (String[])m.get("receiver");
		if( str != null ) rec = str[0];
		
		String oi = null;
		str = (String[])m.get("order_info");
		if( str != null ) oi = str[0];
		
		String od = null;
		String iid = null;
		str = (String[])m.get("order_details");
		if( str != null ) {
			od = str[0];
			
			int i = od.indexOf("receiver\":");
			if( i != -1 && rec == null ) {
				int val = od.indexOf( ',', i+10 );
				rec = od.substring(i+10,val);
			}
			
			i = od.indexOf("item_id\":\"");
			if( i != -1 ) {
				int val = od.indexOf( '"', i+10 );
				iid = od.substring(i+10,val);
			}
		}
		
		str = (String[])m.get("order_id");
		String id = str[0];
		
		if( method.equals("payments_get_items") ) respi( resp, s, rec, oi );
		else if( method.equals("payments_status_update") ) respstatup( resp, s, rec, id, rec, iid );
	}
	
	public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException {		
		respi( resp, null, null, null );
	}
	
	private void respstatup( HttpServletResponse resp, String s, String rec, String orderId, String uid, String iid ) throws IOException {
		this.getServletContext().log( "hey stat" );
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity save = null;
		Query query = new Query("superpower");
		query.setFilter( new Query.FilterPredicate( "uid", FilterOperator.EQUAL, uid ) );
		List<Entity> powerEntities = datastore.prepare( query ).asList(FetchOptions.Builder.withDefaults());
		
		for( Entity e : powerEntities ) {
			save = e;
			break;
		}
		
		if( save == null ) {
			save = new Entity("superpower");
		}
		
		save.setProperty("uid", uid);
		save.setProperty(iid, true);
		/*if( val.contains("Extra life" ) ) save.setProperty("extlif", true);
		if( val.contains("Quantum teleportation" ) ) save.setProperty("quatel", true);
		if( val.contains("Lorentz contraction" ) ) save.setProperty("lorcon", true);
		if( val.contains("Luck" ) ) save.setProperty("luck", true);
		if( val.contains("Dietary pill" ) ) save.setProperty("dipill", true);
		if( val.contains("Deflection" ) ) save.setProperty("deflec", true);
		if( val.contains("Critical angle" ) ) save.setProperty("criang", true);*/
		
		datastore.put( save );

		//resp.setHeader("X-Frame-Options", "GOFORIT");
		PrintWriter pw = resp.getWriter();
		
		/*{
			  "content":{
			              "status":"settled",
			              "order_id":9006195253076
			            },
			  "method":"payments_status_update"
			}*/
		
		pw.println( "{" );
		pw.println( "\"content\":{" );
		pw.println( "\"status\":\"settled\"," );
		pw.println( "\"order_id\":"+orderId );
		pw.println( "}," );
		pw.println( "\"method\":\"payments_status_update\"" );
		pw.println( "}" );
		pw.close();
	}
	
	private void respi( HttpServletResponse resp, String s, String rec, String orderInfo ) throws IOException {
		resp.setHeader("X-Frame-Options", "GOFORIT");
		PrintWriter pw = resp.getWriter();
		
		/*pw.println("<html>"
		pw.println("<head>");
		pw.println("<meta property=\"signed_reqest\" content=\""+s+"\" />");
		pw.println("<script type=\"text/javascript\" language=\"javascript\" src=\"/org.simmi.Facebooktree/org.simmi.Facebooktree.nocache.js\"></script>" );
		pw.println("</head>");
		pw.println("<body>");
		pw.println("</body>");
		pw.println("</html>");*/
		
		InputStream is = null;
		if( orderInfo.contains("lorcon") ) is = this.getServletContext().getResourceAsStream("/lorcon.json");
		else if( orderInfo.contains("quatel") ) is = this.getServletContext().getResourceAsStream("/quatel.json");
		else if( orderInfo.contains("criang") ) is = this.getServletContext().getResourceAsStream("/criang.json");
		else if( orderInfo.contains("deflec") ) is = this.getServletContext().getResourceAsStream("/deflec.json");
		else if( orderInfo.contains("luck") ) is = this.getServletContext().getResourceAsStream("/luck.json");
		else if( orderInfo.contains("dipill") ) is = this.getServletContext().getResourceAsStream("/dipill.json");
		else if( orderInfo.contains("extlif") ) is = this.getServletContext().getResourceAsStream("/extlif.json");
		else if( orderInfo.contains("mondes") ) is = this.getServletContext().getResourceAsStream("/mondes.json");
		
		if( is != null ) {
			BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
			
			String line = br.readLine();
			while( line != null ) {
				pw.println( line );
				line = br.readLine();
			}
			pw.close();
		}
	}
}