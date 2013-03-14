<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.gwt.user.server.Base64Utils" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<!doctype html>
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

	<meta property="fb:app_id" content="179166572124315" />
	<meta property="og:title" content="Shortstories" />
	<meta property="og:type" content="Short stories" />
	<meta id="metaurl" property="og:url" content="http://apps.facebook.com/theshortstories" />
	<!--meta property="og:image" content="" /-->
	<meta property="og:site_name" content="Shortstories" />
		
	<%String str = request.getParameter("signed_request");
    if( str != null ) {
    	%><meta property="erm" content="<%=str%>" /><%
		byte[] bb = null;
    	try {
    		bb = Base64Utils.fromBase64( str );
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
	    	if( i != -1 ) {
	    		int n = i+10;
	    		int m = val.indexOf("\"", n);
	    		if( m > n ) {%>
	    			<meta property="fbuid" content="<%=val.substring(n,m)%>" /><%
	    		}
	    	}
	    }
	} else {
		UserService userService = UserServiceFactory.getUserService();
    	User user = userService.getCurrentUser();
    	if (user != null) {%>
	    	<meta property="guid" content="<%=user.getUserId()%>" />
	    	<meta property="logout" content="<%=userService.createLogoutURL(request.getRequestURI())%>" /><%
      		//pageContext.setAttribute("user", user);
      	} else {%>
      		<meta property="login" content="<%=userService.createLoginURL(request.getRequestURI())%>" /><%
      	}
	}
	%>
    <link type="text/css" rel="stylesheet" href="Smasogur.css">
    <title>Short stories</title>
    <script type="text/javascript" src="https://apis.google.com/js/plusone.js">
  	   {parsetags: 'explicit'}
	</script>
    <script type="text/javascript" language="javascript" src="smasogur/smasogur.nocache.js"></script>
  </head>
  <body>
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
	<br>
	<g:plusone></g:plusone>
	</div>
	
    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>
     <div id="fb-root"></div>
  </body>
</html>
