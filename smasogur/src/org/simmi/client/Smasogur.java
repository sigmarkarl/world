package org.simmi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;

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
	private final SmasagaServiceAsync 	smasagaService = GWT.create(SmasagaService.class);

	public native int dropHandler( JavaScriptObject table ) /*-{
		var s = this;
		
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
		
		function f( evt ) {	
			s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "Checking login status" );
			try {
				$wnd.FB.getLoginStatus( function(response) {
	  				if (response.session) {
	   					var files = evt.dataTransfer.files;		
						var count = files.length;
			
						if(count > 0) {
							var file = files[0];
							var reader = new FileReader();
							reader.onload = function(e) {
								var res = e.target.result;
								s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "File loaded" );
								s.@org.simmi.client.Smasogur::fileLoad(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( file.name, res, response.session.uid );
							};
							
							s.@org.simmi.client.Smasogur::setStatus(Ljava/lang/String;)( "Loading file" );
							reader.readAsBinaryString( file );
						}
					} else {
					    $wnd.FB.login();
					}
				});
			} catch( e ) {
				$wnd.alert( e );
			}
		};
		
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
		}
		
		return 0;
	}-*/;
	
	Element	fblogin;
	public void loginButton() {
		//<fb:login-button show-faces="true" width="200" max-rows="1"></fb:login-button>
		
		com.google.gwt.dom.client.Element elem = Document.get().createElement("fb:login-button");
		elem.setAttribute("width", "200");
		//elem.setAttribute("layout", "standard");
		//elem.setAttribute("font", "arial");
		elem.setAttribute("show-faces", "true");
		elem.setAttribute("max-rows", "1");
		//elem.setAttribute("href",Window.Location.getHref());
		elem.setId("fblogin");
		fblogin.appendChild( elem );
		
		uid = checkLoginStatus();
	}
	
	Label status;
	public void setStatus( String statusStr ) {
		Window.setStatus( statusStr );
		if( status != null ) status.setText( statusStr );
	}
	
	public native String checkLoginStatus() /*-{
		$wnd.FB.getLoginStatus( function(response) {
			$wnd.FB.XFBML.parse();
			if (response.session) {
				return response.session.uid;
			} else {
				return "";
			    //$wnd.FB.login();
			}
		});
	}-*/;
	
	public native void deleteSaga( int r ) /*-{
		var s = this;
		$wnd.FB.getLoginStatus( function(response) {
			if (response.session) {
				s.@org.simmi.client.Smasogur::delete(Ljava/lang/String;I)( response.session.uid, r );
			} else {
			    $wnd.FB.login();
			}
		});
	}-*/;
	
	public native void fbRender() /*-{
		try {
			$wnd.FB.XFBML.parse();
		} catch( e ) {
			$wnd.console.log( e );
		}
	}-*/;
	
	public void delete( String uid, int r ) {
		Saga saga = sogur.get(r);
		if( saga.getAuthor().equals(uid) ) {
			data.removeRow(r);
		
			smasagaService.deleteShortstory( sogur.get(r), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {}

				@Override
				public void onSuccess(String result) {}
			});
			
			sogur.remove( r );
		}
	}
	
	static Map<Integer,String>	gradeStr = new HashMap<Integer,String>();
	static {
		gradeStr.put(0, "rusl");
		gradeStr.put(1, "mjög lélegt");
		gradeStr.put(2, "frekar lélegt");
		gradeStr.put(3, "sleppur");
		gradeStr.put(4, "frekar gott");
		gradeStr.put(5, "mjög gott");
		gradeStr.put(6, "snilld");
	}
	
	public void fileLoad( String fileName, String binaryString, String uid ) {
		final Saga smasaga = new Saga( fileName, "Óþekkt", uid, "Óþekktur", "");
		
		setStatus( "Saving file" );
		smasagaService.saveShortStory( smasaga, fileName, binaryString, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				smasaga.setKey( result );
				
				setStatus( "File saved" );
				int r = data.getNumberOfRows();
				data.addRow();
				data.setFormattedValue(r, 0, "<a href=\"Smasaga.jsp?smasaga="+smasaga.getKey()+"\">"+smasaga.getName()+"</a>" );
				data.setValue(r, 1, smasaga.getAuthorSynonim());
				
				if( smasaga.getGradeNum() == 0 ) {
					data.setValue(r, 2, "");
				} else {
					data.setValue(r, 2, gradeStr.get( (int)Math.round( (double)smasaga.getGradeSum()/(double)smasaga.getGradeNum() ) ) );
				}
				data.setValue(r, 3, smasaga.getGradeNum());
				
				data.setValue( r, 4, smasaga.getLove() );
				data.setValue( r, 5, smasaga.getHorror() );
				data.setValue( r, 6, smasaga.getScience() );
				data.setValue( r, 7, smasaga.getChildren() );
				data.setValue( r, 8, smasaga.getAdolescent() );
				data.setValue( r, 9, smasaga.getHistorical() );
				data.setValue( r, 10, smasaga.getTruestory() );
				data.setValue( r, 11, smasaga.getErotic() );
				data.setValue( r, 12, smasaga.getComedy() );
				data.setValue( r, 13, smasaga.getTragedy() );
				data.setValue( r, 14, smasaga.getSupernatural() );
				data.setValue( r, 15, smasaga.getCriminal() );
				data.setValue( r, 16, smasaga.getAdventure() );
				data.setValue( r, 17, smasaga.getPoem() );
				data.setValue( r, 18, smasaga.getContinue() );
				
				view = DataView.create( data );
				table.draw( view, options );
			}
		});
	}
	
	String 		uid;
	DataTable	data;
	DataView	view;
	Table		table;
	Options		options;
	List<Saga>	sogur;
	FocusPanel	focuspanel;
	public void onModuleLoad() {
		final RootPanel module = RootPanel.get("module");
  	  	final VerticalPanel vp = new VerticalPanel();
  	  	
	  	Window.addResizeHandler( new ResizeHandler() {
	  		@Override
			public void onResize(ResizeEvent event) {
				module.setSize(event.getWidth()+"px", event.getHeight()+"px");
				vp.setSize(event.getWidth()+"px", (event.getHeight())+"px");
				//if( focuspanel != null ) focuspanel.setWidth("1024");
			}
	  	});
  	  
		Runnable onLoadCallback = new Runnable() {
	      public void run() {
	    	  data = DataTable.create();
	    	  
	    	  data.addColumn( ColumnType.STRING, "Nafn");
	    	  data.addColumn( ColumnType.STRING, "Höfundur");
	    	  data.addColumn( ColumnType.STRING, "Einkunn");
	    	  data.addColumn( ColumnType.NUMBER, "Umsagnir");
	    	  data.addColumn( ColumnType.BOOLEAN, "Ástar");
	    	  data.addColumn( ColumnType.BOOLEAN, "Hrylling");
	    	  data.addColumn( ColumnType.BOOLEAN, "Vísinda");
	    	  data.addColumn( ColumnType.BOOLEAN, "Barna");
	    	  data.addColumn( ColumnType.BOOLEAN, "Unglinga");
	    	  data.addColumn( ColumnType.BOOLEAN, "Söguleg");
	    	  data.addColumn( ColumnType.BOOLEAN, "Sannsögu");
	    	  data.addColumn( ColumnType.BOOLEAN, "Erótísk");
	    	  data.addColumn( ColumnType.BOOLEAN, "Gaman");
	    	  data.addColumn( ColumnType.BOOLEAN, "Sorgar");
	    	  data.addColumn( ColumnType.BOOLEAN, "Yfirnátt");
	    	  data.addColumn( ColumnType.BOOLEAN, "Glæpa");
	    	  data.addColumn( ColumnType.BOOLEAN, "Ævintýra");
	    	  data.addColumn( ColumnType.BOOLEAN, "Ljóð");
	    	  data.addColumn( ColumnType.BOOLEAN, "Framhald");
	    	  
	    	  options = Options.create();
	    	  options.setWidth("1024px");
	    	  options.setHeight("600px");
	    	  options.setAllowHtml( true );
	    	  
	    	  view = DataView.create( data );
	    	  table = new Table( view, options );
	    	  
	    	  focuspanel = new FocusPanel( table );
	    	  focuspanel.setWidth("1024px");
	    	  
	    	  focuspanel.addKeyDownHandler( new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if( event.getNativeKeyCode() == KeyCodes.KEY_DELETE ) {
						JsArray<Selection> selections = table.getSelections();
						for( int i = 0; i < selections.length(); i++ ) {
							Selection s = selections.get(i);
							int r = s.getRow();
							deleteSaga( r );
						}
						view = DataView.create( data );
						table.draw( view, options );
					}
				}
	    	  } );
	    	  
	    	  smasagaService.getAllShortstories( new AsyncCallback<Saga[]>() {
				@Override
				public void onSuccess(Saga[] result) {
					sogur = new ArrayList<Saga>( Arrays.asList(result) );
					
					for( Saga smasaga : result ) {
						int r = data.getNumberOfRows();
						data.addRow();
						data.setFormattedValue( r, 0, "<a href=\"Smasaga.jsp?smasaga="+smasaga.getKey()+"\">"+smasaga.getName()+"</a>" );
						data.setValue( r, 1, smasaga.getAuthorSynonim() );
						if( smasaga.getGradeNum() == 0 ) {
							data.setValue(r, 2, "");
						} else {
							data.setValue(r, 2, gradeStr.get( (int)Math.round( (double)smasaga.getGradeSum()/(double)smasaga.getGradeNum() ) ) );
						}
						data.setValue(r, 3, smasaga.getGradeNum());
						
						
						data.setValue( r, 4, smasaga.getLove() );
						data.setValue( r, 5, smasaga.getHorror() );
						data.setValue( r, 6, smasaga.getScience() );
						data.setValue( r, 7, smasaga.getChildren() );
						data.setValue( r, 8, smasaga.getAdolescent() );
						data.setValue( r, 9, smasaga.getHistorical() );
						data.setValue( r, 10, smasaga.getTruestory() );
						data.setValue( r, 11, smasaga.getErotic() );
						data.setValue( r, 12, smasaga.getComedy() );
						data.setValue( r, 13, smasaga.getTragedy() );
						data.setValue( r, 14, smasaga.getSupernatural() );
						data.setValue( r, 15, smasaga.getCriminal() );
						data.setValue( r, 16, smasaga.getAdventure() );
						data.setValue( r, 17, smasaga.getPoem() );
						data.setValue( r, 18, smasaga.getContinue() );
						
						//smasaga.getf
						//data.setValue( r, 3, smasaga.getUrl() );

						view = DataView.create( data );
						table.draw( view, options );
					}
				}
		
				@Override
				public void onFailure(Throwable caught) {
					
				}
	    	  });
	    	  dropHandler( table.getElement() );
	    	  
	    	  vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
	    	  vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );	    	  
	    	  
	    	  VerticalPanel	subvp = new VerticalPanel();
	    	  subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
	    	  subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
	    	  
	    	  HTML html = new HTML();
	    	  html.setText( "Dragðu skrána með smásögunni þinni í töfluna. Ef þú ert logguð/loggaður inná facebook er réttur höfundur skráður. Þú getur valið höfundarnafn, nafnið á raunverulegum höfundi þarf ekki að vera valið" );
	    	  html.setWidth("1024px");
	    	  html.getElement().getStyle().setMargin(20.0, Unit.PX);
	    	  subvp.add( html );
	    	  subvp.add( focuspanel );
	    	  
	    	  Anchor	a = new Anchor( "huldaeggerts@gmail.com" );
	    	  a.setHref("mailto:huldaeggers@gmail.com");
	    	  Anchor	fast = new Anchor( "http://fasteignaverd.appspot.com" );
	    	  fast.setHref("http://fasteignaverd.appspot.com");
	    	  Anchor	conn = new Anchor( "http://webconnectron.appspot.com" );
	    	  conn.setHref("http://webconnectron.appspot.com");
	    	  HTML		kjall = new HTML("Kjallarinn klukkan fimm ehf.");
	    	  
	    	  SimplePanel log = new SimplePanel();
	    	  //log.setSize("100px", "100px");
	    	  fblogin = log.getElement();
	    	  //fblogin.getStyle().setBackgroundColor("#00ffcc");
	    	  
	    	  HorizontalPanel	hp = new HorizontalPanel();
	    	  hp.setSpacing(10);
	    	  hp.add( a );
	    	  hp.add( fast );
	    	  hp.add( conn );
	    	  hp.add( kjall );
	    	  hp.add( log );
	    	  
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
	    	  
	    	  subvp.add( hp );
	    	  
	    	  status = new Label();
	    	  subvp.add( status );
	    	  
	    	  vp.add( subvp );
	    	  
	    	  int w = Window.getClientWidth();
	    	  int h = Window.getClientHeight();
	    	  module.setSize(w+"px", h+"px");
	    	  vp.setSize(w+"px", (h)+"px");
	    	  
	    	  loginButton();
	    	  
	    	  module.insert( vp, 0 );
	      }
	    };
	    VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
		
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
