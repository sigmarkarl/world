package org.simmi.smasaga.client;

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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

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
						if (response.session) {
							ths.@org.simmi.smasaga.client.Smasaga::setUserId(Ljava/lang/String;)( response.session.uid );
						} else {
							ths.@org.simmi.smasaga.client.Smasaga::setUserId(Ljava/lang/String;)( "" );
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
	
	AsyncCallback<Subsaga> asaga;
	private String uid = null;
	private String keystr = null; 
	public void setUserId( String uid ) {
		this.uid = uid;
		
		//Element erm = Document.get().getElementById("ok");
		//<fb:comments href="http://smasogurnar.appspot.com/Smasaga.jsp?smasaga=agtzbWFzb2d1cm5hcnIPCxIHc21hc2FnYRirwwEM" num_posts="2" width="500"></fb:comments>
		//RootPanel fbroot = RootPanel.get("fb-root");
		
		//<fb:comments href="http://smasogurnar.appspot.com/Smasaga.jsp?smasaga=agtzbWFzb2d1cm5hcnIPCxIHc21hc2FnYRirwwEM" num_posts="2" width="500"></fb:comments>
		smasagaService.getShortstory( keystr, asaga );
	}
	
	int currentGrade = 0;
	public void onModuleLoad() {
		final List<Einkunn>	einkunnList = new ArrayList<Einkunn>();
		
		Element e = DOM.getElementById( "metaurl" );
		String urlstr = e.getAttribute("content");
		int urlind = urlstr.indexOf('=');
		keystr = urlstr.substring(urlind+1);
		
		final RootPanel rootPanel = RootPanel.get();
		Style rootstyle = rootPanel.getElement().getStyle();
		rootstyle.setMargin(0.0, Unit.PX);
		rootstyle.setPadding(0.0, Unit.PX);
		
		final VerticalPanel	vp = new VerticalPanel();
		vp.setSize("100%", "100%");
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
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
		subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
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
				subvp.setSize(nw+"px", "600px");
				//vp.setSize(event.getWidth()+"px", (event.getHeight())+"px");
			}
		});
		
		/*final CheckBox love = new CheckBox("Ástarsaga");
		final CheckBox horror = new CheckBox("Hryllingur");
		final CheckBox child = new CheckBox("Barnasaga");
		final CheckBox adolescent = new CheckBox("Unglingasaga");
		final CheckBox tragedy = new CheckBox("Sorgarsaga");
		final CheckBox comedy = new CheckBox("Gamansaga");
		final CheckBox science = new CheckBox("Vísindaskáldsaga");
		final CheckBox supernatural = new CheckBox("Yfirnáttúruleg");
		final CheckBox historical = new CheckBox("Söguleg");
		final CheckBox truestory = new CheckBox("Sannsöguleg");
		final CheckBox erotik = new CheckBox("Erótísk");
		final CheckBox criminal = new CheckBox("Glæpasaga");
		final CheckBox adventure = new CheckBox("Ævintýri");
		final CheckBox poem = new CheckBox("Ljóð");
		final CheckBox tobecontine = new CheckBox("Framhaldssaga");*/
		
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
		final CheckBox adventure = new CheckBox("Fairy tai");
		final CheckBox poem = new CheckBox("Poem");
		final CheckBox tobecontine = new CheckBox("To be continued");
		
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
		final TextBox	name = new TextBox();
		name.setWidth("360px");
		//Label 	authorLabel = new Label("Höfundarnafn:");
		Label 	authorLabel = new Label("Authorname:");
		final TextBox	author = new TextBox();
		author.setWidth("360px");
		hp.add( nameLabel );
		hp.add( name );
		hp.add( authorLabel );
		hp.add( author );
		
		subvp.setSize(nw+"px", "600px");
		
		final Anchor	anchor = new Anchor("Link");
		subvp.add( anchor );
		subvp.add( hp );
		//subvp.add( new Label("Veldu það sem við á") );
		subvp.add( new Label("Select what is relevant") );
		subvp.add( grid );
		
		VerticalPanel inputvp = new VerticalPanel();
		inputvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		
		//inputvp.add( new Label("Úrdráttur") );
		inputvp.add( new Label("Summary") );
		final TextArea urdrattur = new TextArea();
		urdrattur.setSize("512px", "100px");
		inputvp.add( urdrattur );
		
		HorizontalPanel	umsogn = new HorizontalPanel();
		umsogn.setSpacing( 2 );
		/*final RadioButton	rusl = new RadioButton("Grade", "Rusl");
		final RadioButton	vont = new RadioButton("Grade", "Mjög lélegt");
		final RadioButton	slaemt = new RadioButton("Grade", "Frekar lélegt");
		final RadioButton	sleppur = new RadioButton("Grade", "Sleppur");
		final RadioButton	saemi = new RadioButton("Grade", "Frekar gott");
		final RadioButton	gott = new RadioButton("Grade", "Mjög gott");
		final RadioButton	snilld = new RadioButton("Grade", "Snilld");*/
		final RadioButton	rusl = new RadioButton("Grade", "Crap");
		final RadioButton	vont = new RadioButton("Grade", "Very bad");
		final RadioButton	slaemt = new RadioButton("Grade", "Below average");
		final RadioButton	sleppur = new RadioButton("Grade", "Ok");
		final RadioButton	saemi = new RadioButton("Grade", "Above average");
		final RadioButton	gott = new RadioButton("Grade", "Very good");
		final RadioButton	snilld = new RadioButton("Grade", "Masterpiece");
		umsogn.add(rusl);
		umsogn.add(vont);
		umsogn.add(slaemt);
		umsogn.add(sleppur);
		umsogn.add(saemi);
		umsogn.add(gott);
		umsogn.add(snilld);
		
		final RadioButton[] buttons = {rusl, vont, slaemt, sleppur, saemi, gott, snilld};
		
		final TextArea umsogntext = new TextArea();
		
		HorizontalPanel	umh = new HorizontalPanel();
		umh.setSpacing( 10 );
		final Label umlab = new Label("Comment");
		final Button	leftButt = new Button("<");
		final Button	rightButt = new Button(">");
		
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
		
		vp.add( subvp );
		
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
				name.setText( result.getName() );
				author.setText( result.getAuthorSynonim() );
				urdrattur.setText( result.getSummary() );
				
				if( !result.getAuthor().equals(uid) ) {
					name.setReadOnly( true );
					author.setReadOnly( true );
					urdrattur.setReadOnly( true );
					
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
				}
				
				if( uid.length() == 0 || !name.isReadOnly() ) {
					rusl.setEnabled( false );
					vont.setEnabled( false );
					slaemt.setEnabled( false );
					sleppur.setEnabled( false );
					saemi.setEnabled( false );
					gott.setEnabled( false );
					snilld.setEnabled( false );
					
					umsogntext.setReadOnly( true );
				}
				
				Einkunn[] einkunnir = result.getGrades();
				for( Einkunn einkunn : einkunnir ) {
					if( einkunn.getUser().equals(uid) && einkunn.getStory().equals(result.getKey()) ) {
						einkunnList.addAll( Arrays.asList( einkunnir ) );
						break;
					}
				}
				
				if( einkunnList.size() == 0 ) {
					//Window.alert(result.getKey());
					einkunnList.add( new Einkunn(uid,result.getKey(),"",-1) );
					einkunnList.addAll( Arrays.asList( einkunnir ) );
				}
				
				Einkunn enk = einkunnList.get(currentGrade);
				umlab.setText("Comment ("+(currentGrade+1)+" of "+einkunnList.size()+")");
				if( enk.grade != -1 ) {
					buttons[(int)enk.grade].setValue( true );
					umsogntext.setText( enk.getComment() );
				}
				
				final String sagaOwner = result.getAuthor();
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
			}
		};
		loginStatus();
		
		final AsyncCallback<String> async = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				
			}
		};
		
		KeyDownHandler keydownhandler = new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
					smasagaService.updateShortstory( keystr, name.getText(), author.getText(), urdrattur.getText(),
							love.getValue(), comedy.getValue(), tragedy.getValue(), horror.getValue(), 
							erotik.getValue(), science.getValue(), child.getValue(), adolescent.getValue(), 
							criminal.getValue(), historical.getValue(), truestory.getValue(), supernatural.getValue(), 
							adventure.getValue(), poem.getValue(), tobecontine.getValue(), async );
				}
			}
		};
		name.addKeyDownHandler( keydownhandler );
		author.addKeyDownHandler( keydownhandler );
		
		ClickHandler clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				smasagaService.updateShortstory( keystr, name.getText(), author.getText(), urdrattur.getText(),
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
	 	elem.setAttribute("src", "http://connect.facebook.net/en_US/all.js" );
		Document.get().getElementById("fb-root").appendChild( elem );
		
		rootPanel.add( vp );
	}
}
