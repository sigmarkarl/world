package org.simmi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simmi.shared.Saga;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

import elemental.client.Browser;
import elemental.html.Console;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Smasogur implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync 	greetingService = GWT.create(GreetingService.class);
	private final SmasagaServiceAsync 		smasagaService = GWT.create(SmasagaService.class);

	/*var s = this;
	
	function f1( evt ) {
		evt.stopPropagation();
		evt.preventDefault();
	};
	
	function f2( evt ) {
		
	};
	
	function ie( evt ) {
		f( evt );
	}
	
	function everythingelse( evt ) {
		f1( evt );
		f( evt );
	}
	
	function f( evt ) {*/

	public native void dropHandler2( JavaScriptObject table, JavaScriptObject dataTransfer ) /*-{
		var s = this;
		var files = dataTransfer.files;		
		var count = files.length;
	
		if(count > 0) {
			var file = files[0];
			var reader = new FileReader();
			reader.onload = function(e) {
				var res = e.target.result;
				s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "File loaded" );
				s.@org.simmi.client.Smasogur::fileLoad(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( file.name, res, 'simmi' );
			};
			
			s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "Loading file" );
			reader.readAsBinaryString( file );
		} else if( response.status === 'not_authorized' ) {
		    if( $wnd.console ) $wnd.console.log('not authorized');
		} else {
		    if( $wnd.console ) $wnd.console.log('not logged in');
		}
	}-*/;
	
	public native void fileLoad( JavaScriptObject file, String uid ) /*-{
		var s = this;
		if( file ) {
			var reader = new FileReader();
			reader.onload = function(e) {
				var res = e.target.result;
				s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "File loaded" );
				s.@org.simmi.client.Smasogur::fileLoad(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( file.name, res, uid );
			};
			
			s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "Loading file" );
			reader.readAsBinaryString( file );
		}
	}-*/;
	
	public native void dropHandler( JavaScriptObject table, JavaScriptObject dataTransfer ) /*-{
		var file;
		if( dataTransfer.files.length > 0 ) file = dataTransfer.files[0];
		var s = this;
		
		//this.@org.simmi.client.Smasogur::fileLoad(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)( file, "" );
		this.@org.simmi.client.Smasogur::checkLoginForFileUpload(Lcom/google/gwt/core/client/JavaScriptObject;)( file );
	}-*/;
	
	public native void checkLoginForFileUpload( JavaScriptObject file ) /*-{
		var s = this;
		try {
			if( $wnd.console ) {
				$wnd.console.log( "checking" );
				$wnd.console.log( $wnd.FB );
			}
			
			$wnd.FB.getLoginStatus( function(response) {
				if( $wnd.console ) {
					$wnd.console.log( "ok" );
					$wnd.console.log( response.status );
				}
				
				if (response.status === 'connected') {
				    // the user is logged in and connected to your
				    // app, and response.authResponse supplies
				    // the user's ID, a valid access token, a signed
				    // request, and the time the access token 
				    // and signed request each expire
				    var uid = response.authResponse.userID;
				    var accessToken = response.authResponse.accessToken;
		
					s.@org.simmi.client.Smasogur::fileLoad(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)( file, uid );
				} else if (response.status === 'not_authorized') {
				    if( $wnd.console ) $wnd.console.log('not authorized');
				} else {
				    if( $wnd.console ) $wnd.console.log('not logged in');
				    $wnd.FB.login();
				}
			
	//				if (response.session) {
	//					$wnd.console.log('ok '+response.session);
	//					
	//					var files = evt.dataTransfer.files;		
	//				var count = files.length;
	//	
	//				if(count > 0) {
	//					var file = files[0];
	//					var reader = new FileReader();
	//					reader.onload = function(e) {
	//						var res = e.target.result;
	//						s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "File loaded" );
	//						s.@org.simmi.client.Smasogur::fileLoad(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( file.name, res, response.session.uid );
	//					};
	//					
	//					s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "Loading file" );
	//					reader.readAsBinaryString( file );
	//				}
	//			} else {
	//				$wnd.console.log('null '+response.session);
	//			    $wnd.FB.login();
	//			}
			});
		} catch( e ) {
			if( $wnd.console ) $wnd.console.log( 'error '+e );
		}
	}-*/;
	
	public native void checkLogin() /*-{
		$wnd.FB.getLoginStatus( function(response) {			
			if (response.status === 'connected') {
			    var uid = response.authResponse.userID;
			    var accessToken = response.authResponse.accessToken;
	
				///...
			} else if (response.status === 'not_authorized') {
			    if( $wnd.console ) $wnd.console.log('not authorized');
			} else {
			    if( $wnd.console ) $wnd.console.log('not logged in');
			    $wnd.FB.login();
			}
		});
	}-*/;
	
	public native void loadHandler( JavaScriptObject table, JavaScriptObject ie ) /*-{
		try {
			var file;
			if( ie.files ) {
				if( ie.files.length > 0 ) file = ie.files[0];
			}
			var s = this;
			if( $wnd.console ) {
				$wnd.console.log( "checking" );
				$wnd.console.log( $wnd.FB );
			}
			
			$wnd.FB.getLoginStatus( function(response) {
				if( $wnd.console ) {
					$wnd.console.log( "ok" );
					$wnd.console.log( response.status );
				}
				
				if (response.status === 'connected') {
				    var uid = response.authResponse.userID;
				    var accessToken = response.authResponse.accessToken;
		
					if( file ) {
						var reader = new FileReader();
						reader.onload = function(e) {
							var res = e.target.result;
							s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "File loaded" );
							s.@org.simmi.client.Smasogur::fileLoad(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( file.name, res, uid );
						};
						
						s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "Loading file" );
						reader.readAsBinaryString( file );
					} else {
						
					}
				} else if (response.status === 'not_authorized') {
				    if( $wnd.console ) $wnd.console.log('not authorized');
				} else {
				    if( $wnd.console ) $wnd.console.log('not logged in');
				    $wnd.FB.login();
				}
			});
		} catch( e ) {
			if( $wnd.console ) $wnd.console.log( 'error '+e );
		}
	}-*/;
	
	/*};
	
	if( table.addEventListener ) {
		table.addEventListener( "dragenter", f1, false );
		table.addEventListener( "dragexit", f1, false );
		table.addEventListener( "dragover", f1, false );
		table.addEventListener( "drop", everythingelse, false );
	} else {
		table.attachEvent ("ondragenter", f2);
        table.attachEvent ("ondragover", f2);
        table.attachEvent ("ondragleave", f2);
        table.attachEvent ("ondrop", ie);
	}*/
	
	public com.google.gwt.dom.client.Element loginButton() {
		//<fb:login-button show-faces="true" width="200" max-rows="1"></fb:login-button>
		
		com.google.gwt.dom.client.Element elem = Document.get().createElement("fb:login-button");
		elem.setAttribute("width", "200");
		elem.setAttribute("height", "100");
		//elem.setAttribute("layout", "standard");
		//elem.setAttribute("font", "arial");
		elem.setAttribute("show-faces", "true");
		elem.setAttribute("max-rows", "1");
		//elem.setAttribute("href",Window.Location.getHref());
		elem.setId("fblogin");
		
		return elem;
	}
	
	Label status;
	public void setStatus( String statusStr ) {
		Window.setStatus( statusStr );
		if( status != null ) status.setText( statusStr );
	}
	
	SubmitButton	uploadButton;
	public void setUserId( String val ) {
		console( "about to set userid " + val );
		
		uid = val;
		//if( uploadButton != null ) uploadButton.setEnabled( uid != null && uid.length() > 1 );
	}
	
	public native String checkLoginStatus() /*-{
		var ths = this;
		$wnd.fbAsyncInit = function() {
			if( $wnd.console ) $wnd.console.log( "fbinit" );
	    	$wnd.FB.init({appId: '179166572124315', status: true, cookie: true, xfbml: true, oauth : true});
	    	if( $wnd.console ) $wnd.console.log( "fbstat" );
	    	try {
				$wnd.FB.getLoginStatus( function(response) {
					if( $wnd.console ) $wnd.console.log( "inside login response" );
					try {
						$wnd.FB.XFBML.parse();
						if( response.status === 'connected' ) {
							var uid = response.authResponse.userID;
							ths.@org.simmi.client.Smasogur::setUserId(Ljava/lang/String;)( uid );
						} else {
							ths.@org.simmi.client.Smasogur::setUserId(Ljava/lang/String;)( "" );
						}
					} catch( e ) {
						if( $wnd.console ) $wnd.console.log( e );
					}
					if( $wnd.console ) $wnd.console.log( "past login response" );
				});
			} catch( e ) {
				if( $wnd.console ) $wnd.console.log( e );
			}
	  	};
	}-*/;
	
	public native void deleteSaga( int r ) /*-{
		var s = this;
		$wnd.FB.getLoginStatus( function(response) {
			$wnd.console.log("here");
			if (response.status === 'connected') {
			    var uid = response.authResponse.userID;
			    var accessToken = response.authResponse.accessToken;
			    
			    s.@org.simmi.client.Smasogur::delete(Ljava/lang/String;I)( uid, r );
			} else if (response.status === 'not_authorized') {
			    if( $wnd.console ) $wnd.console.log('not authorized');
			} else {
			    if( $wnd.console ) $wnd.console.log('not logged in');
			    $wnd.FB.login();
			}
		});
	}-*/;
	
	public native void fbRender() /*-{
		try {
			$wnd.FB.XFBML.parse();
		} catch( e ) {
			if( $wnd.console ) $wnd.console.log( e );
		}
	}-*/;
	
	public native void gplusgo() /*-{
		$wnd.gapi.plusone.go();
	}-*/;
	
	public void delete( String uid, final int r ) {
		Saga saga = sogur.get(r);
		if( saga.getAuthor().equals(uid) ) {		
			smasagaService.deleteShortstory( sogur.get(r), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {}

				@Override
				public void onSuccess(String result) {
					data.removeRow(r);
					sogur.remove( r );
					
					table.draw( view, options );
				}
			});
		}
	}
	
	public void deleteSelection() {
		JsArray<Selection> selections = table.getSelections();
		for( int i = 0; i < selections.length(); i++ ) {
			Selection s = selections.get(i);
			int r = s.getRow();
			//console("fuck");
			if( uid != null && uid.length() > 0 ) delete( uid, r );
			//deleteSaga( r );
		}
		view = DataView.create( data );
		table.draw( view, options );
	}
	
	static Map<Integer,String>	gradeStr = new HashMap<Integer,String>();
	static {
		gradeStr.put(0, "junk");
		gradeStr.put(1, "very bad");
		gradeStr.put(2, "rather bad");
		gradeStr.put(3, "ok");
		gradeStr.put(4, "rather good");
		gradeStr.put(5, "very good");
		gradeStr.put(6, "brilliant");
	}
	
	public native void console( String str ) /*-{
		if( $wnd.console ) $wnd.console.log( str );
	}-*/;
	
	public void fileLoad( String fileName, String fileUrl, String binaryString, String uid ) {
		//final String fnameDecoded = URL.decode( fileName );
		
		console("fl "+fileUrl);
		final Saga saga = new Saga( fileName, "Unkown", uid, "Unknown", "English", "");
		setStatus( "Saving file" );
		smasagaService.saveShortStory( saga, fileName, fileUrl, binaryString, new AsyncCallback<Saga>() {
			@Override
			public void onFailure(Throwable caught) {
				console( caught.getMessage() );
			}

			@Override
			public void onSuccess(Saga smasaga) {
				//console("in fl "+result);
				
				//smasaga.setKey( result );
				sogur.add( smasaga );
				
				setStatus( "File saved" );
				int r = data.getNumberOfRows();
				data.addRow();
				data.setFormattedValue(r, 0, "<a href=\"Smasaga.jsp?smasaga="+smasaga.getKey()+"\" target=\"_blank\">"+smasaga.getName()+"</a>" );
				data.setValue(r, 1, smasaga.getAuthorSynonim());
				data.setValue(r, 2, smasaga.getLanguage());
				if( smasaga.getGradeNum() == 0 ) {
					data.setValue(r, 3, "");
				} else {
					data.setValue(r, 3, gradeStr.get( (int)Math.round( (double)smasaga.getGradeSum()/(double)smasaga.getGradeNum() ) ) );
				}
				data.setValue( r, 4, smasaga.getDate() );
				data.setValue(r, 5, smasaga.getGradeNum());
				data.setValue( r, 6, smasaga.getLove() );
				data.setValue( r, 7, smasaga.getHorror() );
				data.setValue( r, 8, smasaga.getScience() );
				data.setValue( r, 9, smasaga.getChildren() );
				data.setValue( r, 10, smasaga.getAdolescent() );
				data.setValue( r, 11, smasaga.getHistorical() );
				data.setValue( r, 12, smasaga.getTruestory() );
				data.setValue( r, 13, smasaga.getErotic() );
				data.setValue( r, 14, smasaga.getComedy() );
				data.setValue( r, 15, smasaga.getTragedy() );
				data.setValue( r, 16, smasaga.getSupernatural() );
				data.setValue( r, 17, smasaga.getCriminal() );
				data.setValue( r, 18, smasaga.getAdventure() );
				data.setValue( r, 19, smasaga.getPoem() );
				data.setValue( r, 20, smasaga.getContinue() );
				
				view = DataView.create( data );
				table.draw( view, options );
			}
		});
	}
	
	public native void click( JavaScriptObject e ) /*-{
		e.click();
	}-*/;
	
	String 		uid;
	DataTable	data;
	DataView	view;
	Table		table;
	Options		options;
	List<Saga>	sogur;
	FocusPanel	focuspanel;
	String 		fbuid = null;
	public void onModuleLoad() {
		final Console	console = Browser.getWindow().getConsole();
		final RootPanel module = RootPanel.get();
		
		Style rootstyle = module.getElement().getStyle();
		rootstyle.setMargin(0.0, Unit.PX);
		rootstyle.setPadding(0.0, Unit.PX);
		rootstyle.setBorderWidth(0.0, Unit.PX);
		
		Window.setMargin("0px");
		//Window.enableScrolling( false );
		
  	  	//final VerticalPanel vp = new VerticalPanel();
  	  	//vp.setSize("100%", "100%");
  	  	
		NodeList<com.google.gwt.dom.client.Element> nl = Document.get().getElementsByTagName("meta");
		int i;
		for( i = 0; i < nl.getLength(); i++ ) {
			com.google.gwt.dom.client.Element e = nl.getItem(i);
			String prop = e.getAttribute("property");
			if( prop.equals("erm") ) {
				//setUserId( e.getAttribute("content") );
				fbuid = e.getAttribute("content");
				if( fbuid != null ) uid = fbuid;
				break;
			}
		}
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		module.setSize(w+"px", h+"px");
		//if( fbuid == null ) module.setSize(w+"px", h+"px");
		//else module.setWidth("758px");
		
		final VerticalPanel	subvp = new VerticalPanel();
  	  	subvp.setWidth("100%");
  	    Style vstyle = subvp.getElement().getStyle();
  	    vstyle.setPadding(0.0, Unit.PX);
  	    vstyle.setMargin(0.0, Unit.PX);
  	    vstyle.setBorderWidth(0.0, Unit.PX);
  	  
  	    subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
  	    subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
  	    subvp.setSpacing( 10 );
  	  
	  	Window.addResizeHandler( new ResizeHandler() {
	  		@Override
			public void onResize(ResizeEvent event) {
	  			int w = event.getWidth();
	  			int h = event.getHeight();
	  			module.setSize(w+"px", h+"px");
				//if( fbuid == null ) module.setSize(w+"px", h+"px");
				//else module.setWidth("758px");
				subvp.setWidth("100%");
				
				if( table != null ) {
					table.setWidth( (w-20)+"px" );
				}
				//vp.setSize(event.getWidth()+"px", (event.getHeight())+"px");
				//if( focuspanel != null ) focuspanel.setWidth("1024");
			}
	  	});
  	  
	  	final SimplePanel log = new SimplePanel();
	  	log.setSize("400px", "100px");
	  	Style style = log.getElement().getStyle();
	  	style.setPadding(20.0, Unit.PX);
	  	
	  	/*SimplePanel gug = new SimplePanel();
   	  	com.google.gwt.dom.client.Element plus = Document.get().createElement("g:plusone");
 		plus.setAttribute("size", "small");
 		gug.getElement().appendChild( plus );*/
 		  
   	  	//final HorizontalPanel	sharehp = new HorizontalPanel();
   	  	//sharehp.add( log );
   	  	//sharehp.add( gug );
   	  	
		Runnable onLoadCallback = new Runnable() {
	      public void run() {
	    	  console.log("slowness inside vizualload");
	    	  
	    	  data = DataTable.create();
	    	  
	    	  /*data.addColumn( ColumnType.STRING, "Nafn");
	    	  data.addColumn( ColumnType.STRING, "HÃ¶fundur");
	    	  data.addColumn( ColumnType.STRING, "Einkunn");
	    	  data.addColumn( ColumnType.NUMBER, "Umsagnir");
	    	  data.addColumn( ColumnType.BOOLEAN, "Ã�star");
	    	  data.addColumn( ColumnType.BOOLEAN, "Hrylling");
	    	  data.addColumn( ColumnType.BOOLEAN, "VÃ­sinda");
	    	  data.addColumn( ColumnType.BOOLEAN, "Barna");
	    	  data.addColumn( ColumnType.BOOLEAN, "Unglinga");
	    	  data.addColumn( ColumnType.BOOLEAN, "SÃ¶guleg");
	    	  data.addColumn( ColumnType.BOOLEAN, "SannsÃ¶gu");
	    	  data.addColumn( ColumnType.BOOLEAN, "ErÃ³tÃ­sk");
	    	  data.addColumn( ColumnType.BOOLEAN, "Gaman");
	    	  data.addColumn( ColumnType.BOOLEAN, "Sorgar");
	    	  data.addColumn( ColumnType.BOOLEAN, "YfirnÃ¡tt");
	    	  data.addColumn( ColumnType.BOOLEAN, "GlÃ¦pa");
	    	  data.addColumn( ColumnType.BOOLEAN, "Ã†vintÃ½ra");
	    	  data.addColumn( ColumnType.BOOLEAN, "LjÃ³Ã°");
	    	  data.addColumn( ColumnType.BOOLEAN, "Framhald");*/
	    	  
	    	  data.addColumn( ColumnType.STRING, "Name");
	    	  data.addColumn( ColumnType.STRING, "Author");
	    	  data.addColumn( ColumnType.STRING, "Language");
	    	  data.addColumn( ColumnType.STRING, "Grade");
	    	  data.addColumn( ColumnType.DATE, "Date");
	    	  data.addColumn( ColumnType.NUMBER, "Comments");
	    	  data.addColumn( ColumnType.BOOLEAN, "Love");
	    	  data.addColumn( ColumnType.BOOLEAN, "Horror");
	    	  data.addColumn( ColumnType.BOOLEAN, "Science");
	    	  data.addColumn( ColumnType.BOOLEAN, "Children");
	    	  data.addColumn( ColumnType.BOOLEAN, "Adoles");
	    	  data.addColumn( ColumnType.BOOLEAN, "Historic");
	    	  data.addColumn( ColumnType.BOOLEAN, "True");
	    	  data.addColumn( ColumnType.BOOLEAN, "Erotic");
	    	  data.addColumn( ColumnType.BOOLEAN, "Comedy");
	    	  data.addColumn( ColumnType.BOOLEAN, "Tragedy");
	    	  data.addColumn( ColumnType.BOOLEAN, "Supernat");
	    	  data.addColumn( ColumnType.BOOLEAN, "Pulp");
	    	  data.addColumn( ColumnType.BOOLEAN, "Fairytail");
	    	  data.addColumn( ColumnType.BOOLEAN, "Poem");
	    	  data.addColumn( ColumnType.BOOLEAN, "Tobecont");
	    	  
	    	  options = Options.create();
	    	  int w = Window.getClientWidth();
    		  options.setWidth((w-20)+"px");
    		  
	    	  /*if( fbuid != null ) options.setWidth("758px");
	    	  else {
	    		  int w = Window.getClientWidth();
	    		  options.setWidth((w-20)+"px");
	    	  }*/
	    	  options.setHeight("360px");
	    	  options.setAllowHtml( true );
	    	  
	    	  view = DataView.create( data );
	    	  
	    	  table = new Table( view, options );
	    	  Style tstyle = table.getElement().getStyle();
	    	  tstyle.setMargin(0.0, Unit.PX);
	    	  tstyle.setBorderWidth(0.0, Unit.PX);
	    	  tstyle.setPadding(0.0, Unit.PX);
	    	  
	    	  focuspanel = new FocusPanel( table );
	    	  Style focusstyle = focuspanel.getElement().getStyle();
	    	  focusstyle.setMargin(0.0, Unit.PX);
	    	  focusstyle.setBorderWidth(0.0, Unit.PX);
	    	  focusstyle.setPadding(0.0, Unit.PX);
	    	  focuspanel.setWidth("100%");
	    	  
	    	  focuspanel.addKeyDownHandler( new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if( event.getNativeKeyCode() == KeyCodes.KEY_DELETE ) {
						deleteSelection();
					}
				}
	    	  });
	    	  
	    	  focuspanel.addDropHandler( new DropHandler() {
				@Override
				public void onDrop(DropEvent event) {
					setStatus( "Checking login status" );
					//event.stopPropagation();
					event.preventDefault();
					dropHandler( table.getElement(), event.getDataTransfer() );
				}
	    	  });
	    	  focuspanel.addDragStartHandler( new DragStartHandler() {
				@Override
				public void onDragStart(DragStartEvent event) {}
	    	  });
	    	  focuspanel.addDragEndHandler( new DragEndHandler() {
				@Override
				public void onDragEnd(DragEndEvent event) {}	    		  
	    	  });
	    	  focuspanel.addDragEnterHandler( new DragEnterHandler() {
				@Override
				public void onDragEnter(DragEnterEvent event) {}
	    	  });
	    	  focuspanel.addDragHandler( new DragHandler() {
				@Override
				public void onDrag(DragEvent event) {}
	    	  });
	    	  focuspanel.addDragOverHandler( new DragOverHandler() {
				@Override
				public void onDragOver(DragOverEvent event) {}
	    	  });
	    	  focuspanel.addDragLeaveHandler( new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {}
	    	  });
	    	  
	    	  console.log("about to load shortstories");
	    	  smasagaService.getAllShortstories( new AsyncCallback<Saga[]>() {
				@Override
				public void onSuccess(Saga[] result) {
					console.log( "slowness inside shortstories" );
					//console( "succ all load:"+result.length );
					
					sogur = new ArrayList<Saga>( Arrays.asList(result) );
					
					for( Saga smasaga : result ) {
						int r = data.getNumberOfRows();
						data.addRow();
						data.setFormattedValue( r, 0, "<a href=\"Smasaga.jsp?smasaga="+smasaga.getKey()+"\" target=\"_blank\">"+smasaga.getName()+"</a>" );
						data.setValue( r, 1, smasaga.getAuthorSynonim() );
						data.setValue(r, 2, smasaga.getLanguage());
						if( smasaga.getGradeNum() == 0 ) {
							data.setValue(r, 3, "");
						} else {
							data.setValue(r, 3, gradeStr.get( (int)Math.round( (double)smasaga.getGradeSum()/(double)smasaga.getGradeNum() ) ) );
						}
						data.setValue( r, 4, smasaga.getDate());
						data.setValue(r, 5, smasaga.getGradeNum());
						data.setValue( r, 6, smasaga.getLove() );
						data.setValue( r, 7, smasaga.getHorror() );
						data.setValue( r, 8, smasaga.getScience() );
						data.setValue( r, 9, smasaga.getChildren() );
						data.setValue( r, 10, smasaga.getAdolescent() );
						data.setValue( r, 11, smasaga.getHistorical() );
						data.setValue( r, 12, smasaga.getTruestory() );
						data.setValue( r, 13, smasaga.getErotic() );
						data.setValue( r, 14, smasaga.getComedy() );
						data.setValue( r, 15, smasaga.getTragedy() );
						data.setValue( r, 16, smasaga.getSupernatural() );
						data.setValue( r, 17, smasaga.getCriminal() );
						data.setValue( r, 18, smasaga.getAdventure() );
						data.setValue( r, 19, smasaga.getPoem() );
						data.setValue( r, 20, smasaga.getContinue() );
						
						//smasaga.getf
						//data.setValue( r, 3, smasaga.getUrl() );

						if( smasaga == result[result.length-1] ) {
							view = DataView.create( data );
							table.draw( view, options );
							console.log("done load shortstories");
						}
						console.log("iter "+r);
					}
				}
		
				@Override
				public void onFailure(Throwable caught) {}
	    	  });
	    	  //dropHandler( table.getElement() );
	    	  
	    	  //vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
	    	  //vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );	    	  
	    	  
	    	  final HTML html = new HTML();
	    	  //html.setText( "DragÃ°u skrÃ¡na meÃ° smÃ¡sÃ¶gunni Ã¾inni Ã­ tÃ¶fluna. <br>Ef Ã¾Ãº ert logguÃ°/loggaÃ°ur innÃ¡ facebook er rÃ©ttur hÃ¶fundur skrÃ¡Ã°ur. <br>ÃžÃº getur valiÃ° hÃ¶fundarnafn, nafniÃ° Ã¡ raunverulegum hÃ¶fundi Ã¾arf ekki aÃ° vera valiÃ°" );
	    	  html.setHTML( "Drag-drop the file containing your short story into the table. " +
	    			"The file can be in a format of your choice. For example pdf for text and mp3 for audiobooks.<br>" +
	    	  		"If you are logged into facebook, you are registered as the author. " +
	    	  		"You can choose you own authorname, it doesn't have to be your real name" );
	    	  html.setWidth("100%");
	    	  //html.getElement().getStyle().setMargin(20.0, Unit.PX);
	    	  
	    	  /*SimplePanel adspanel = new SimplePanel();
	    	  com.google.gwt.dom.client.Element adselem = Document.get().getElementById("ads");
	    	  adselem.removeFromParent();
	    	  adspanel.getElement().appendChild( adselem );*/
	    	  //subvp.add( adspanel );
	    	  
	    	  HTML title = new HTML("<h2>Shortstories<h2/>");
	    	  Style style = title.getElement().getStyle();
	  			style.setMargin(0.0, Unit.PX);
	  			style.setPadding(0.0, Unit.PX);
	  			style.setBorderWidth(0.0, Unit.PX);
	  		
	    	  subvp.add( title );
	    	  HTML subtitle = new HTML("<h4>Brought to you by The Basement At 5 o'Clock reading club<h4/>");
	    	  subvp.add( subtitle );
	    	  subvp.add( log );
	    	  
	    	  final FormPanel fp = new FormPanel();
	    	  fp.setAction( "/smasogur/FileUpload" );
	    	  fp.setMethod( FormPanel.METHOD_POST );
	    	  fp.setEncoding( FormPanel.ENCODING_MULTIPART );
	    	  
	    	  fp.addSubmitHandler( new SubmitHandler() {
				@Override
				public void onSubmit(SubmitEvent event) {
					console("submitted");
				}
	    	  });
	    	  
	    	  final FileUpload	file = new FileUpload();
	    	  fp.addSubmitCompleteHandler( new SubmitCompleteHandler() {
				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) {
					String subm = event.getResults();
					console("ermi " + subm + "  " + uid);
					int i = subm.lastIndexOf("http:");
					if( i == -1 ) i = subm.lastIndexOf("https:");
					if( i >= 0 ) {
						int k = subm.indexOf( "<", i+1 );
						if( k == -1 ) k = subm.length();
						fileLoad( URL.decode( file.getName() ), subm.substring(i, k).trim(), null, uid );
					}
					uploadButton.setEnabled( false );
				}
	    	  });
	    	  
	  		  file.getElement().setId("shortstory");
	  		  file.setName("shortstory");
	  		  file.addChangeHandler( new ChangeHandler() {
	  			@Override
	  			public void onChange(ChangeEvent event) {
	  				uploadButton.setEnabled( true );
	  				
	  				String filename = file.getFilename();
	  				int i = filename.lastIndexOf('/');
	  				int k = filename.lastIndexOf('\\');
	  				i = Math.max(i, k);
	  				String fname = filename.substring(i+1);
	  				fname = URL.encode( fname );
	  				file.setName( fname );
	  				/*try {
						fname = URLEncoder.encode( fname, "UTF-8" );
						file.setName( fname );
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}*/
	  				//file.get
	  				//console("erm");
	  				//loadHandler( table.getElement(), file.getElement() );
	  				//file.setName( event.);
	  			}
	  		  });
	    	  uploadButton = new SubmitButton("Upload");
	    	  uploadButton.setEnabled( false );
	    	  //if( uid == null || uid.length() < 2 ) uploadButton.setEnabled( false );
	    	  
	    	  /*uploadButton.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					//click( file.getElement() );
					fp.submit();
				}
	    	  });*/
	    	  
	    	  Button deleteButton = new Button("Delete selection");
	    	  deleteButton.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					deleteSelection();
				}
	    	  });
	    	  
	    	  HorizontalPanel	filehp = new HorizontalPanel();
	    	  filehp.setSpacing( 20 );
	    	  filehp.add( file );
	    	  filehp.add( uploadButton );
	    	  filehp.add( deleteButton );
	    	  fp.add( filehp );
	    	  
	    	  subvp.add( html );
	    	  //if( uid != null && uid.length() > 0 ) 
	    	  subvp.add( fp );
	    	  subvp.add( focuspanel );
	    	  
	    	  Anchor	a = new Anchor( "huldaeggerts@gmail.com" );
	    	  a.setHref("mailto:huldaeggers@gmail.com");
	    	  Anchor	fast = new Anchor( "http://webwormgame.appspot.com" );
	    	  fast.setHref("http://webwormgame.appspot.com");
	    	  //Anchor	conn = new Anchor( "http://webconnectron.appspot.com/Treedraw.html" );
	    	  //conn.setHref("http://webconnectron.appspot.com/Treedrdasdddfasdfaw.html");
	    	  Anchor	fblink = new Anchor( "https://apps.facebook.com/theshortstories" );
	    	  fblink.setHref("https://apps.facebook.com/theshortstories");
	    	  //HTML		kjall = new HTML("Kjallarinn klukkan fimm ehf.");
	    	  
	    	  //log.setSize("100px", "100px");
	    	  //fblogin.getStyle().setBackgroundColor("#00ffcc");
	    	  
	    	  HorizontalPanel	hp = new HorizontalPanel();
	    	  hp.setSpacing(10);
	    	  hp.add( a );
	    	  hp.add( fast );
	    	  //hp.add( conn );
	    	  hp.add( fblink );
	    	  //hp.add( kjall );
	    	  //hp.add( log );
	    	  
	    	  //String html = "<fb:login-button show-faces=\"true\" width=\"200\" max-rows=\"1\"></fb:login-button>";
	    	  /*Element el = DOM.createElement( "fb:login-button" );
	    	  el.setAttribute("show-faces", "true");
	    	  el.setAttribute("width", "200");
	    	  el.setAttribute("max-rows", "1");
	    	  
	    	  SimplePanel sp = new SimplePanel();
	    	  Element spel = sp.getElement();
	    	  
	    	  //DOM.insertChild(spel, el, 0);
	    	  DOM.appendChild(spel, el);
	    	  hp.add( sp );*/
	    	  
	    	  //subvp.add( deleteButton );
	    	  subvp.add( hp );
	    	  
	    	  status = new Label();
	    	  subvp.add( status );
	    	  
	    	  console.log("done");
	    	  //vp.add( subvp );
	      }
	    };
	    VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
		
	    console.log("ok");
	    com.google.gwt.dom.client.Element elem = loginButton();
  	  	Element fblogin = log.getElement();
  	  	fblogin.appendChild( elem );
  	  
  	  	checkLoginStatus();
  	  	String id = "facebook-jssdk";
  	  	elem = Document.get().createElement("script");
		elem.setAttribute("async", "true");
		elem.setId( id );
	 	elem.setAttribute("src", "//connect.facebook.net/en_US/all.js" );
		Document.get().getElementById("fb-root").appendChild( elem );
  	  	//205279482582
  	  
		console.log("next");
		module.add( subvp );
  	  	gplusgo();
  	  	console.log("done");
  	  
		/*final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT User");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);*/
	}
}
