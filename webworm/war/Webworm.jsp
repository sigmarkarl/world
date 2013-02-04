<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.gwt.user.server.Base64Utils" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Query.FilterOperator" %>

<!doctype html>
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	
	<meta property="fb:app_id" content="215097581865564" />
	<meta property="og:title" content="WebWorm" />
	<meta property="og:type" content="Game" />
	<meta id="metaurl" property="og:url" content="http://apps.facebook.com/webwormgame" />
	<!--meta property="og:image" content="" /-->
	<meta property="og:site_name" content="WebWorm" />
		
	<%
	String str = request.getParameter("signed_request");
    if( str != null ) {
    	int k = str.indexOf(".");
		byte[] bb = null;    
    	try {
    		//bb = Base64Utils.fromBase64( str );
    		bb = Base64.decodeBase64( str.substring(k+1) );
    	} catch( Exception e ) {
    		e.printStackTrace();
    	}
    	if( bb != null ) {
    		int i;
    		for( i = 0; i < bb.length; i++ ) {
    			if( bb[i] == 0 ) break;
    		}
    		if( i == bb.length ) i = 0;
	    	String val = new String( bb, i+1, bb.length-i-1 );
	    	i = val.indexOf("user_id");
	    	%><meta property="erm" content="<%=val%>" /><%
	    	if( i != -1 ) {
	    		int n = i+10;
	    		int m = val.indexOf("\"", n);
	    		if( m > n ) {%>
	    			<meta property="fbuid" content="<%=val.substring(n,m)%>" /><%
	    		}
	    	}
	    }
    } else {
		String val = "";
		
		String remhost = request.getRemoteHost();
		
		Enumeration keys = request.getParameterNames();
	    while( keys.hasMoreElements() ) {
	      String key = (String)keys.nextElement();
	      String value = request.getParameter(key);
		  if( value != null ) val += value;
  	    }
		
	    /*BufferedReader br = request.getReader();
	    String line = br.readLine();
		while( line != null ) {
		      val += line;
		      line = br.readLine();
		}*/
	    
	    if( val.length() > 0 ) {
	    	int i = val.indexOf("huldaeggerts@gmail.com");
	    	if( i != -1 ) {
	    		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Entity ent = new Entity("postur");
				ent.setProperty("value", remhost+val);
				datastore.put( ent );
				
	    		int k = i+22;
	    		char c = val.charAt(k++);
	    		while( c >= '0' && c <= '9' ) c = val.charAt(k++);
	    		
	    		Entity save = null;
	    		String uid = val.substring( i+22, k-1 );
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
				if( val.contains("Extra life" ) ) save.setProperty("extlif", true);
				if( val.contains("Quantum teleportation" ) ) save.setProperty("quatel", true);
				if( val.contains("Lorentz contraction" ) ) save.setProperty("lorcon", true);
				if( val.contains("Luck" ) ) save.setProperty("luck", true);
				if( val.contains("Dietary pill" ) ) save.setProperty("dipill", true);
				if( val.contains("Deflection" ) ) save.setProperty("deflec", true);
				if( val.contains("Critical angle" ) ) save.setProperty("criang", true);
				
				datastore.put( save );
			}
	    }
	}
    %>
		
    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link type="text/css" rel="stylesheet" href="Webworm.css">

    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>WebWorm</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" src="//apis.google.com/js/plusone.js">
  	   {parsetags: 'explicit'}
	</script>
    <script type="text/javascript" language="javascript" src="webworm/webworm.nocache.js"></script>
    <script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-24456286-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

	</script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <form action="https://www.paypal.com/cgi-bin/webscr" method="post" id="donate" style="display:none">
		<input type="hidden" name="cmd" value="_s-xclick">
		<input type="hidden" name="hosted_button_id" value="L4TC92APSZDHW">
		<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
		<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
	</form>
	
	<form target="paypal" action="https://www.paypal.com/cgi-bin/webscr" method="post" id="mondes" style="display:none">
	<input type="hidden" name="cmd" value="_s-xclick">
	<input type="hidden" name="hosted_button_id" value="GTDHG7AXUUWWE">
	<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
	<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
	</form>
	
	<form target="paypal" action="https://www.paypal.com/cgi-bin/webscr" method="post" id="lorcon" style="display:none">
	<input type="hidden" name="cmd" value="_s-xclick">
	<input type="hidden" name="hosted_button_id" value="5GSE569LBQRN4">
	<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
	<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
	</form>
	
	<form target="paypal" action="https://www.paypal.com/cgi-bin/webscr" method="post" id="quatel" style="display:none">
	<input type="hidden" name="cmd" value="_s-xclick">
	<input type="hidden" name="hosted_button_id" value="RZEJDBKH4VHJ2">
	<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
	<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
	</form>
	
	<form target="paypal" action="https://www.paypal.com/cgi-bin/webscr" method="post" id="criang" style="display:none">
	<input type="hidden" name="cmd" value="_s-xclick">
	<input type="hidden" name="hosted_button_id" value="DGR8KG2HZVPVG">
	<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
	<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
	</form>
	
	<form target="paypal" action="https://www.paypal.com/cgi-bin/webscr" method="post" id="deflec" style="display:none">
	<input type="hidden" name="cmd" value="_s-xclick">
	<input type="hidden" name="hosted_button_id" value="X3LQBSTXA686A">
	<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
	<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
	</form>
	
	<form target="paypal" action="https://www.paypal.com/cgi-bin/webscr" method="post" id="luck" style="display:none">
	<input type="hidden" name="cmd" value="_s-xclick">
	<input type="hidden" name="hosted_button_id" value="W8FC3N9EJBEQL">
	<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
	<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
	</form>
	
	<form target="paypal" action="https://www.paypal.com/cgi-bin/webscr" method="post" id="extlif" style="display:none">
	<input type="hidden" name="cmd" value="_s-xclick">
	<input type="hidden" name="hosted_button_id" value="93ULNAMGHQ9VS">
	<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
	<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
	</form>
	
	<form target="paypal" action="https://www.paypal.com/cgi-bin/webscr" method="post" id="dipill" style="display:none">
	<input type="hidden" name="cmd" value="_s-xclick">
	<input type="hidden" name="hosted_button_id" value="CT6VST75J8Q6J">
	<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_cart_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
	<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
	</form>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>
    </table>
    
     <div id="fb-root"></div>
     <div id="content" style="float: left"></div>
<!-- if( str == null ) { -->
    <div id="ads" style="float: right">
    <script type="text/javascript"><!--
		google_ad_client = "ca-pub-7204381538404733";
		google_ad_slot = "1687563636";
		google_ad_width = 160;
		google_ad_height = 600;
		//-->
	</script>
	<script type="text/javascript" src="//pagead2.googlesyndication.com/pagead/show_ads.js"></script>
    </div>
<!--} else {-->
	<div id="ads">
	<script type="text/javascript"><!--
		google_ad_client = "ca-pub-7204381538404733";
		/* Webworm FBCanvas */
		google_ad_slot = "0213973952";
		google_ad_width = 728;
		google_ad_height = 90;
		//-->
	</script>
	<script type="text/javascript" src="//pagead2.googlesyndication.com/pagead/show_ads.js"></script>
	</div>
<!--} -->

  </body>
</html>
