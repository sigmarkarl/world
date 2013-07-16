<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		
		<%
		String name = "";
		String oauth = "";
		String keystr = request.getParameter("smasaga");
		String oauthpar = request.getParameter("access");
		if( oauthpar != null ) oauth = oauthpar;
		String url = request.getServerName();
		if( keystr != null ) {
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Key key = KeyFactory.stringToKey( keystr );
				Entity e = datastore.get( key );
		
				if( e != null ) {
					name = (String)e.getProperty("name");
				}
		}		

		if( name != null ) {
		%>
		
		<meta property="og:title" content="<%=name%>" />
		<meta property="og:type" content="book" />
		<meta id="metaurl" property="og:url" content="http://<%=url%>/Smasaga.jsp?smasaga=<%=keystr%>" />
		<!--meta property="og:image" content="" /-->
		<meta property="og:site_name" content="Smasogur" />
		<meta id="oauth" property="oauth" content="<%=oauth%>" />

		<!--                                                               -->
		<!-- Consider inlining CSS to reduce the number of requested files -->
		<!--                                                               -->
		<link type="text/css" rel="stylesheet" href="Smasaga.css">
	
		<!--                                           -->
		<!-- Any title is fine                         -->
		<!--                                           -->
		<title><%=name%></title>

		<!--                                           -->
		<!-- This script loads your compiled module.   -->
		<!-- If you add any GWT meta tags, they must   -->
		<!-- be added before this line.                -->
		<!--                                           -->
		<script src="https://apis.google.com/js/plusone.js">{parsetags: 'explicit'}</script>
		<script language="javascript" src="org.simmi.Smasaga/org.simmi.Smasaga.nocache.js"></script>
	</head>
	<body>
		<!-- OPTIONAL: include this if you want history support -->
		<iframe id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>
		<div id="ads" style="text-align: center">
    	<script type="text/javascript"><!--
			google_ad_client = "ca-pub-7204381538404733";
			/* Shortstories */
			google_ad_slot = "4103255287";
			google_ad_width = 728;
			google_ad_height = 90;
			//-->
		</script>
		<script type="text/javascript" src="//pagead2.googlesyndication.com/pagead/show_ads.js">
		</script>
		</div>
		<div id="fb-root"></div>
	</body>
	<%
		} else {
	%>
	</head>
	<%
		}
	%>
</html>
