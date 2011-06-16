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
		String name = null;
		String keystr = request.getParameter("smasaga");
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
		<meta id="metaurl" property="og:url" content="http://smasogurnar.appspot.com/Smasaga.jsp?smasaga=<%=keystr%>" />
		<!--meta property="og:image" content="" /-->
		<meta property="og:site_name" content="Smasogur" />

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
		<script language="javascript" src="org.simmi.smasaga.Smasaga/org.simmi.smasaga.Smasaga.nocache.js"></script>
	</head>

	<!--                                           -->
	<!-- The body can have arbitrary html, or      -->
	<!-- we leave the body empty because we want   -->
	<!-- to create a completely dynamic ui         -->
	<!--                                           -->
	<body>
		<!-- OPTIONAL: include this if you want history support -->
		<iframe id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>
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
