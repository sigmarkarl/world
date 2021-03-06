package org.simmi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Smasaga implements EntryPoint {
	private final SmasagaSubserviceAsync 	smasagaService = GWT.create(SmasagaSubservice.class);

	public native void loginStatus() /*-{
		var ths = this;
		$wnd.fbAsyncInit = function() {
	    	$wnd.FB.init({appId: '179166572124315', status: true, cookie: true, xfbml: true});
	    	
	    	try {
				$wnd.FB.getLoginStatus( function(response) {
					try {
						$wnd.FB.XFBML.parse();
						
						if (response.status === 'connected') {
						    var uid = response.authResponse.userID;
						    var accessToken = response.authResponse.accessToken;
						    
							ths.@org.simmi.client.Smasaga::setUserId(Ljava/lang/String;)( uid );
							//ths.@org.simmi.client.Smasaga::setAccessToken(Ljava/lang/String;)( accessToken );
						} else if (response.status === 'not_authorized') {
						    $wnd.console.log('not authorized');
						    ths.@org.simmi.client.Smasaga::setUserId(Ljava/lang/String;)( "" );
						} else {
						    $wnd.console.log('not logged in');
						    $wnd.FB.login();
						    ths.@org.simmi.client.Smasaga::setUserId(Ljava/lang/String;)( "" );
						}
					} catch( e ) {
						$wnd.console.log( e );
					}
					$wnd.console.log( "past login response" );
				});
			} catch( e ) {
				$wnd.console.log( e );
			}
	  	};
		
		//var s = this;
		//$wnd.FB.getLoginStatus( function(response) {
		//	if (response.session) {
		//		s.@org.simmi.smasaga.client.Smasaga::login(Ljava/lang/String;)( response.session.uid );
		//	} else {
		//	    s.@org.simmi.smasaga.client.Smasaga::login(Ljava/lang/String;)( "" );
		//	}
		//});
	}-*/;
	
	String accessToken;
	public void setAccessToken( String at ) {
		accessToken = at;
	}
	
	AsyncCallback<Subsaga> asaga;
	private String uid = null;
	private String keystr = null; 
	Einkunn[] einkunnir;
	String resultKey;
	public void setUserId( String uid ) {
		this.uid = uid;
		
		//Element erm = Document.get().getElementById("ok");
		//<fb:comments href="http://smasogurnar.appspot.com/Smasaga.jsp?smasaga=agtzbWFzb2d1cm5hcnIPCxIHc21hc2FnYRirwwEM" num_posts="2" width="500"></fb:comments>
		//RootPanel fbroot = RootPanel.get("fb-root");
		
		//<fb:comments href="http://smasogurnar.appspot.com/Smasaga.jsp?smasaga=agtzbWFzb2d1cm5hcnIPCxIHc21hc2FnYRirwwEM" num_posts="2" width="500"></fb:comments>
		
		initStory( authorName );
		initGrades( einkunnir, resultKey, authorName );
	}
	
	final CheckBox love = new CheckBox("Love story");
	final CheckBox horror = new CheckBox("Horror story");
	final CheckBox child = new CheckBox("Children story");
	final CheckBox adolescent = new CheckBox("Story for adolescents");
	final CheckBox tragedy = new CheckBox("Tragedy");
	final CheckBox comedy = new CheckBox("Comedy");
	final CheckBox science = new CheckBox("Science fiction");
	final CheckBox supernatural = new CheckBox("Supernatural");
	final CheckBox historical = new CheckBox("Historical");
	final CheckBox truestory = new CheckBox("True story");
	final CheckBox erotik = new CheckBox("Erotic");
	final CheckBox criminal = new CheckBox("Pulp fiction");
	final CheckBox adventure = new CheckBox("Fairy tail");
	final CheckBox poem = new CheckBox("Poem");
	final CheckBox tobecontine = new CheckBox("To be continued");
	
	final Button	contact = new Button("Contact author");
	final TextBox	name = new TextBox();
	final TextBox	author = new TextBox();
	final TextBox	lang = new TextBox();
	
	final RadioButton	rusl = new RadioButton("Grade", "Crap");
	final RadioButton	vont = new RadioButton("Grade", "Very bad");
	final RadioButton	slaemt = new RadioButton("Grade", "Below average");
	final RadioButton	sleppur = new RadioButton("Grade", "Ok");
	final RadioButton	saemi = new RadioButton("Grade", "Above average");
	final RadioButton	gott = new RadioButton("Grade", "Very good");
	final RadioButton	snilld = new RadioButton("Grade", "Masterpiece");
	
	final TextArea urdrattur = new TextArea();
	
	final RadioButton[] buttons = {rusl, vont, slaemt, sleppur, saemi, gott, snilld};
	final TextArea umsogntext = new TextArea();
	final Button save = new Button( "Save" );
	
	final Label umlab = new Label("Comment");
	final Button	leftButt = new Button("<");
	final Button	rightButt = new Button(">");
	
	public void initStory( String authorName ) {
		if( uid != null && uid.length() > 0 ) {
			if( authorName != null && authorName.length() > 0 ) {
				Browser.getWindow().getConsole().log("authorname: "+authorName);
				contact.setEnabled( true );
			}
			
			if( authorName.equals(uid) ) {
				name.setReadOnly( false );
				author.setReadOnly( false );
				lang.setReadOnly( false );
				urdrattur.setReadOnly( false );
				
				love.setEnabled( true );
				comedy.setEnabled( true );
				tragedy.setEnabled( true );
				horror.setEnabled( true );
				erotik.setEnabled( true );
				science.setEnabled( true );
				child.setEnabled( true );
				adolescent.setEnabled( true );
				criminal.setEnabled( true );
				historical.setEnabled( true );
				truestory.setEnabled( true );
				supernatural.setEnabled( true );
				adventure.setEnabled( true );
				poem.setEnabled( true );
				tobecontine.setEnabled( true );
				
				save.setEnabled( true );
			}
			
			if( name.isReadOnly() ) {
				rusl.setEnabled( true );
				vont.setEnabled( true );
				slaemt.setEnabled( true );
				sleppur.setEnabled( true );
				saemi.setEnabled( true );
				gott.setEnabled( true );
				snilld.setEnabled( true );
				
				umsogntext.setReadOnly( false );
			}
		}
	}
	
	public List<Einkunn> initGrades( Einkunn[] einkunnir, String resultKey, String resultAuthor ) {
		final List<Einkunn> einkunnList = new ArrayList<Einkunn>();
		//Einkunn[] einkunnir = result.getGrades();
		for( Einkunn einkunn : einkunnir ) {
			if( einkunn.getUser().equals(uid) && einkunn.getStory().equals(resultKey) ) {
				einkunnList.addAll( Arrays.asList( einkunnir ) );
				break;
			}
		}
		
		if( einkunnList.size() == 0 ) {
			//Window.alert(result.getKey());
			einkunnList.add( new Einkunn(uid,resultKey,"",-1) );
			einkunnList.addAll( Arrays.asList( einkunnir ) );
		}
		
		Einkunn enk = einkunnList.get(currentGrade);
		umlab.setText("Comment ("+(currentGrade+1)+" of "+einkunnList.size()+")");
		if( enk.grade != -1 ) {
			buttons[(int)enk.grade].setValue( true );
			umsogntext.setText( enk.getComment() );
		}
		
		final String sagaOwner = resultAuthor;
		leftButt.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentGrade = (currentGrade-1)%einkunnList.size();
				
				Einkunn enk = einkunnList.get(currentGrade);
				umlab.setText("Comment ("+(currentGrade+1)+" of "+einkunnList.size()+")");
				if( enk.grade != -1 ) {
					buttons[(int)enk.grade].setValue( true );
				}
				umsogntext.setText( enk.getComment() );
				
				boolean enni = uid != null && uid.length() > 0 && currentGrade == 0 && !sagaOwner.equals(uid);
				
				rusl.setEnabled( enni );
				vont.setEnabled( enni );
				slaemt.setEnabled( enni );
				sleppur.setEnabled( enni );
				saemi.setEnabled( enni );
				gott.setEnabled( enni );
				snilld.setEnabled( enni );
				
				umsogntext.setReadOnly( !enni );
			}
		});
		rightButt.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentGrade = (currentGrade+1)%einkunnList.size();
				
				Einkunn enk = einkunnList.get(currentGrade);
				umlab.setText("Comment ("+(currentGrade+1)+" of "+einkunnList.size()+")");
				if( enk.grade != -1 ) {
					buttons[(int)enk.grade].setValue( true );
				}
				umsogntext.setText( enk.getComment() );
				
				boolean enni = uid != null && uid.length() > 0 && currentGrade == 0 && !sagaOwner.equals(uid);
				
				rusl.setEnabled( enni );
				vont.setEnabled( enni );
				slaemt.setEnabled( enni );
				sleppur.setEnabled( enni );
				saemi.setEnabled( enni );
				gott.setEnabled( enni );
				snilld.setEnabled( enni );
				
				umsogntext.setReadOnly( !enni );
			}
		});
		return einkunnList;
	}
	
	public native void sendAuthorMessage( final String authorName, final String at, final String message ) /*-{
		$wnd.FB.api( '/'+authorName+'/notifications', {
			access_token: at,
	  		template: message
		}, function( response ) {
			 if (!response || response.error) {
    			alert( 'Error occured' + response.error.message );
			 }
		});
	}-*/;
	
	public native void renderSaveToDrive( String id, String objurl, String filename ) /*-{
		$wnd.gapi.savetodrive.render( id, {
      		src: objurl,
      		filename: filename,
      		sitename: 'Sigmasoft Shortstories'
    	});
	}-*/;
	
	String oauth = "";
	int currentGrade = 0;
	String authorName = "";
	public void onModuleLoad() {
		final List<Einkunn>	einkunnList = new ArrayList<Einkunn>();
		
		Element e = DOM.getElementById( "oauth" );
		oauth = e.getAttribute("content");
		
		e = DOM.getElementById( "metaurl" );
		String urlstr = e.getAttribute("content");
		int urlind = urlstr.indexOf('=');
		keystr = urlstr.substring(urlind+1);
		
		final RootPanel rootPanel = RootPanel.get();
		Style rootstyle = rootPanel.getElement().getStyle();
		rootstyle.setMargin(0.0, Unit.PX);
		rootstyle.setPadding(0.0, Unit.PX);
		rootstyle.setBorderWidth(0.0, Unit.PX);
		
		//final VerticalPanel	vp = new VerticalPanel();
		//vp.setSize("100%", "100%");
		//vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		//vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rootPanel.setSize(w+"px", h+"px");
		
		final Grid	grid = new Grid(3, 5);
		int nw = 1024;
		/*if( w >= 1024 ) nw = 1024;
		if( w > 758 ) nw = w;
		else nw = 758;*/ 
		grid.setWidth(nw+"px");
		
		final VerticalPanel subvp = new VerticalPanel();
		subvp.setWidth( (w-25)+"px" );
		subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		//subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				rootPanel.setSize(w+"px", h+"px");
				
				int nw = 1024;
				/*if( w >= 1024 ) nw = 1024;
				if( w > 758 ) nw = w;
				else nw = 758;*/ 
				
				grid.setWidth(nw+"px");
				subvp.setSize((w-25)+"px", "600px");
				//vp.setSize(event.getWidth()+"px", (event.getHeight())+"px");
			}
		});
		
		/*final CheckBox love = new CheckBox("Ã�starsaga");
		final CheckBox horror = new CheckBox("Hryllingur");
		final CheckBox child = new CheckBox("Barnasaga");
		final CheckBox adolescent = new CheckBox("Unglingasaga");
		final CheckBox tragedy = new CheckBox("Sorgarsaga");
		final CheckBox comedy = new CheckBox("Gamansaga");
		final CheckBox science = new CheckBox("VÃ­sindaskÃ¡ldsaga");
		final CheckBox supernatural = new CheckBox("YfirnÃ¡ttÃºruleg");
		final CheckBox historical = new CheckBox("SÃ¶guleg");
		final CheckBox truestory = new CheckBox("SannsÃ¶guleg");
		final CheckBox erotik = new CheckBox("ErÃ³tÃ­sk");
		final CheckBox criminal = new CheckBox("GlÃ¦pasaga");
		final CheckBox adventure = new CheckBox("Ã†vintÃ½ri");
		final CheckBox poem = new CheckBox("LjÃ³Ã°");
		final CheckBox tobecontine = new CheckBox("Framhaldssaga");*/
		
		love.setEnabled( false );
		comedy.setEnabled( false );
		tragedy.setEnabled( false );
		horror.setEnabled( false );
		erotik.setEnabled( false );
		science.setEnabled( false );
		child.setEnabled( false );
		adolescent.setEnabled( false );
		criminal.setEnabled( false );
		historical.setEnabled( false );
		truestory.setEnabled( false );
		supernatural.setEnabled( false );
		adventure.setEnabled( false );
		poem.setEnabled( false );
		tobecontine.setEnabled( false );
		
		grid.setWidget(0, 0, love);
		grid.setWidget(0, 1, horror);
		grid.setWidget(0, 2, child);
		grid.setWidget(0, 3, adolescent);
		grid.setWidget(0, 4, tragedy);
		grid.setWidget(1, 0, comedy);
		grid.setWidget(1, 1, science);
		grid.setWidget(1, 2, supernatural);
		grid.setWidget(1, 3, historical);
		grid.setWidget(1, 4, truestory);
		grid.setWidget(2, 0, erotik);
		grid.setWidget(2, 1, criminal);
		grid.setWidget(2, 2, adventure);
		grid.setWidget(2, 3, poem);
		grid.setWidget(2, 4, tobecontine);
		
		HorizontalPanel	hp = new HorizontalPanel();
		hp.setSpacing(10);
		Label 	nameLabel = new Label("Name:");
		name.setWidth("360px");
		//Label 	authorLabel = new Label("HÃ¶fundarnafn:");
		Label 	authorLabel = new Label("Authorname:");
		author.setWidth("360px");
		
		Label 	langLabel = new Label("Language:");
		author.setWidth("120px");
		
		hp.add( nameLabel );
		hp.add( name );
		hp.add( authorLabel );
		hp.add( author );
		hp.add( langLabel );
		hp.add( lang );
		
		subvp.setSize("100%", "600px");
		
		/*SimplePanel adspanel = new SimplePanel();
   	  	com.google.gwt.dom.client.Element adselem = Document.get().getElementById("ads");
   	  	adselem.removeFromParent();
   	  	adspanel.getElement().appendChild( adselem );
   	  	subvp.add( adspanel );*/
		
		final HorizontalPanel	nhp = new HorizontalPanel();
		nhp.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		nhp.setSpacing( 5 );
		final HTML		dwnl = new HTML("Download story: ");
		final Anchor	anchor = new Anchor("Link");
		final SimplePanel	savetodrive = new SimplePanel();
		savetodrive.getElement().setId("savetodrive");
		final HTML		or = new HTML(" or ");
		
		contact.setEnabled( false );
		contact.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final TextArea ta = new TextArea();
				ta.setSize("400px", "300px");
				Button	bt = new Button("Send");
				final PopupPanel pp = new PopupPanel();
				pp.setAutoHideEnabled( true );
				
				VerticalPanel vp = new VerticalPanel();
				vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
				vp.add( ta );
				vp.add( bt );
				pp.add( vp );
				
				bt.addClickHandler( new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						pp.hide();
						
						Browser.getWindow().getConsole().log( "blehbleh " + authorName + "  " + accessToken + "  " + oauth );
						sendAuthorMessage( authorName, oauth.length() > 0 ? oauth : accessToken, ta.getText() );
						
						/*smasagaService.sendAuthorMessage( ta.getText(), new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								
							}

							@Override
							public void onFailure(Throwable caught) {
								
							}
						});*/
					}
				});
				pp.center();
			}
		});
		
		nhp.add( dwnl );
		nhp.add( anchor );
		nhp.add( or );
		nhp.add( savetodrive );
		//anchor.setHeight("75px");
		subvp.setSpacing( 10 );
		subvp.add( nhp );
		subvp.add( contact );
		subvp.add( hp );
		//subvp.add( new Label("Veldu Ã¾aÃ° sem viÃ° Ã¡") );
		subvp.add( new Label("Select what is relevant") );
		subvp.add( grid );
		
		VerticalPanel inputvp = new VerticalPanel();
		inputvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		
		//inputvp.add( new Label("ÃšrdrÃ¡ttur") );
		inputvp.add( new Label("Summary") );
		urdrattur.setSize("512px", "100px");
		inputvp.add( urdrattur );
		
		HorizontalPanel	umsogn = new HorizontalPanel();
		umsogn.setSpacing( 2 );
		/*final RadioButton	rusl = new RadioButton("Grade", "Rusl");
		final RadioButton	vont = new RadioButton("Grade", "MjÃ¶g lÃ©legt");
		final RadioButton	slaemt = new RadioButton("Grade", "Frekar lÃ©legt");
		final RadioButton	sleppur = new RadioButton("Grade", "Sleppur");
		final RadioButton	saemi = new RadioButton("Grade", "Frekar gott");
		final RadioButton	gott = new RadioButton("Grade", "MjÃ¶g gott");
		final RadioButton	snilld = new RadioButton("Grade", "Snilld");*/
		umsogn.add(rusl);
		umsogn.add(vont);
		umsogn.add(slaemt);
		umsogn.add(sleppur);
		umsogn.add(saemi);
		umsogn.add(gott);
		umsogn.add(snilld);
		
		contact.setEnabled( false );
		name.setReadOnly( true );
		author.setReadOnly( true );
		lang.setReadOnly( true );
		urdrattur.setReadOnly( true );
		
		rusl.setEnabled( false );
		vont.setEnabled( false );
		slaemt.setEnabled( false );
		sleppur.setEnabled( false );
		saemi.setEnabled( false );
		gott.setEnabled( false );
		snilld.setEnabled( false );
		
		umsogntext.setReadOnly( true );
		
		HorizontalPanel	umh = new HorizontalPanel();
		umh.setSpacing( 10 );		
		umh.add( leftButt );
		umh.add( umlab );
		umh.add( rightButt );
		
		inputvp.add( umh );
		inputvp.add( umsogn );
		umsogntext.setSize("512px", "100px");
		inputvp.add( umsogntext );
		
		SimplePanel	fbcompanel = new SimplePanel();
		
		HorizontalPanel	umcom = new HorizontalPanel();
		umcom.setSpacing( 10 );
		umcom.add( inputvp );
		umcom.add( fbcompanel );
		
		subvp.add( umcom );
		
		final AsyncCallback<String> async = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				
			}
		};
		save.setEnabled( false );
		save.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				smasagaService.updateShortstory( keystr, name.getText(), author.getText(), lang.getText(), urdrattur.getText(),
						love.getValue(), comedy.getValue(), tragedy.getValue(), horror.getValue(), 
						erotik.getValue(), science.getValue(), child.getValue(), adolescent.getValue(), 
						criminal.getValue(), historical.getValue(), truestory.getValue(), supernatural.getValue(), 
						adventure.getValue(), poem.getValue(), tobecontine.getValue(), async );
			}
		});
		subvp.add( save );
		
		HorizontalPanel	discl = new HorizontalPanel();
		//discl.setHeight("30px");
		discl.setSpacing(10);
		Anchor back = new Anchor("Front page");
		back.setHref("/");
		discl.add( back );
		HTML html = new HTML("The Basement at 5 o'Clock");
		discl.add( html );
		
		SimplePanel	span = new SimplePanel();
		discl.add( span );
		
		subvp.add( discl );
		
		//vp.add( subvp );
		
		ClickHandler gradeHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int grade = 0;
				for( RadioButton rb : buttons ) {
					if( rb.getValue() ) break;
					grade++;
				}
				
				Einkunn enk = einkunnList.get(0);
				smasagaService.updateEinkunn(enk.getKey(), umsogntext.getText(), grade, uid, enk.getStory(), new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}

					@Override
					public void onSuccess(String result) {
					
					}
				});
			}
		};
		
		for( RadioButton rb : buttons ) {
			rb.addClickHandler( gradeHandler );
		}
		
		asaga = new AsyncCallback<Subsaga>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Subsaga result) {
				love.setValue( result.getLove() );
				comedy.setValue( result.getComedy() );
				tragedy.setValue( result.getTragedy() );
				horror.setValue( result.getHorror() );
				erotik.setValue( result.getErotic() );
				science.setValue( result.getScience() );
				child.setValue( result.getChildren() );
				adolescent.setValue( result.getAdolescent() );
				criminal.setValue( result.getCriminal() );
				historical.setValue( result.getHistorical() );
				truestory.setValue( result.getTruestory() );
				supernatural.setValue( result.getSupernatural() );
				adventure.setValue( result.getAdventure() );
				poem.setValue( result.getPoem() );
				tobecontine.setValue( result.getContinue() );
				
				anchor.setText( result.getName() );
				anchor.setHref( result.getUrl() );
				anchor.setTarget("_blank");
				
				String host = Window.Location.getHost();
				String saveurl = "//"+host+"/smasogur/FileDownload?smasaga="+URL.encode( result.getUrl() );
				Browser.getWindow().getConsole().log( "saveurl " + saveurl );
				renderSaveToDrive( "savetodrive", saveurl, result.getName() );
				/*savetodrive.getElement().setAttribute("data-src", result.getUrl());
				savetodrive.getElement().setAttribute("data-filename", result.getName());
				savetodrive.getElement().setAttribute("data-sitename", "Read Short Stories");*/
				
				/*ScriptElement selem = (ScriptElement)Document.get().createElement("script");
				//selem.setAttribute("async", "true");
			 	selem.setInnerText( "var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;" +
			 			"po.src = 'https://apis.google.com/js/plusone.js';" +
			 			"var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);"
		        );
				nhp.getElement().appendChild( selem );*/
				
				name.setText( result.getName() );
				author.setText( result.getAuthorSynonim() );
				lang.setText( result.getLanguage() );
				urdrattur.setText( result.getSummary() );
				
				authorName = result.getAuthor();
				einkunnir = result.getGrades();
				resultKey = result.getKey();
				//initStory( authorName );
				//initGrades( einkunnir, resultKey, authorName );
			}
		};
		smasagaService.getShortstory( keystr, asaga );
		loginStatus();
		
		KeyDownHandler keydownhandler = new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
					smasagaService.updateShortstory( keystr, name.getText(), author.getText(), lang.getText(), urdrattur.getText(),
							love.getValue(), comedy.getValue(), tragedy.getValue(), horror.getValue(), 
							erotik.getValue(), science.getValue(), child.getValue(), adolescent.getValue(), 
							criminal.getValue(), historical.getValue(), truestory.getValue(), supernatural.getValue(), 
							adventure.getValue(), poem.getValue(), tobecontine.getValue(), async );
				}
			}
		};
		name.addKeyDownHandler( keydownhandler );
		author.addKeyDownHandler( keydownhandler );
		lang.addKeyDownHandler( keydownhandler );
		
		ClickHandler clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				smasagaService.updateShortstory( keystr, name.getText(), author.getText(), lang.getText(), urdrattur.getText(),
						love.getValue(), comedy.getValue(), tragedy.getValue(), horror.getValue(), 
						erotik.getValue(), science.getValue(), child.getValue(), adolescent.getValue(), 
						criminal.getValue(), historical.getValue(), truestory.getValue(), supernatural.getValue(), 
						adventure.getValue(), poem.getValue(), tobecontine.getValue(), async );
			}
		};
		love.addClickHandler( clickHandler );
		horror.addClickHandler( clickHandler );
		comedy.addClickHandler( clickHandler );
		tragedy.addClickHandler( clickHandler );
		erotik.addClickHandler( clickHandler );
		science.addClickHandler( clickHandler );
		child.addClickHandler( clickHandler );
		adolescent.addClickHandler( clickHandler );
		criminal.addClickHandler( clickHandler );
		historical.addClickHandler( clickHandler );
		truestory.addClickHandler( clickHandler );
		supernatural.addClickHandler( clickHandler );
		adventure.addClickHandler( clickHandler );
		poem.addClickHandler( clickHandler );
		tobecontine.addClickHandler( clickHandler );
		
		com.google.gwt.dom.client.Element elem = Document.get().createElement("fb:like");
		elem.setAttribute("width", "200");
		elem.setAttribute("layout", "standard");
		elem.setAttribute("font", "arial");
		elem.setAttribute("href",Window.Location.getHref());
		elem.setId("fblike");
		span.getElement().appendChild( elem );
		
		elem = Document.get().createElement("fb:comments");
		elem.setAttribute("width", "500");
		elem.setAttribute("layout", "standard");
		elem.setAttribute("num_posts", "2");
		elem.setAttribute("font", "arial");
		elem.setAttribute("href",Window.Location.getHref());
		elem.setId("fbcomments");
		fbcompanel.getElement().appendChild( elem );
		
  	  	elem = Document.get().createElement("script");
		elem.setAttribute("async", "true");
	 	elem.setAttribute("src", "//connect.facebook.net/en_US/all.js" );
		Document.get().getElementById("fb-root").appendChild( elem );
		
		rootPanel.add( subvp );
	}
}
