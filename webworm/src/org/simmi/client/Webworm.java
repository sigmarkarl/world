package org.simmi.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Webworm implements EntryPoint, KeyDownHandler, KeyUpHandler {
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
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	double	applex;
	double 	appley;
	double	appler = 16.0;
	public int setApple( Context2d context ) {
		int w = context.getCanvas().getWidth();
		int h = context.getCanvas().getHeight();
		
		applex = Random.nextDouble()*(w-appler*2.0)+appler;
		appley = Random.nextDouble()*(h-appler*2.0)+appler;
		
		ImageData id = context.getImageData(applex-appler, appley-appler, 2.0*appler, 2.0*appler);
		for( int x = 0; x < id.getWidth(); x+=4 ) {
			int y;
			for( y = 0; y < id.getHeight(); y+=4 ) {
				int r = id.getRedAt(x, y);
				int g = id.getGreenAt(x, y);
				int b = id.getBlueAt(x, y);
				
				if( r != 0 || g != 0 || b != 0 ) return setApple( context );
			}
		}
		
		context.beginPath();
		context.setFillStyle("#dddddd");
		context.arc(applex, appley, appler-4.0, 0, 2.0*Math.PI);
		context.fill();
		context.closePath();
		
		return 0;
	}
	
	boolean pause = false;
	class Worm {
		double	r;
		double	a;
		double	s;
		String	c;
		int		d;
		
		int		left;
		int		right;
		int		leftv;
		int		rightv;
		
		List<Double>	xs;
		List<Double>	ys;
		
		int 	i;
		int		l;
		
		int		score;
		
		HorizontalPanel	hp = new HorizontalPanel();
		TextBox 		scorebox = new TextBox();
		//ValueBox<Integer>	scorebox = new ValueBox<Integer>();
		
		public Worm( String c, int left, int right, int leftv, int rightv ) {
			r = 5.0;
			s = 7.0;
			l = 32;
			i = 0;
			d = 32;
			score = 0;
			this.c = c;
			
			Canvas		label = Canvas.createIfSupported();
			
			label.setSize("18px", "18px");
			label.setCoordinateSpaceWidth( 18 );
			label.setCoordinateSpaceHeight( 18 );
			Context2d context = label.getContext2d();
			context.clearRect(0.0, 0.0, 20.0, 20.0);
			context.setFillStyle( c );
			context.beginPath();
			context.arc(9.0, 10.0, 6.0, 0.0, Math.PI*2.0);
			context.fill();
			context.closePath();
			scorebox.setSize("24px", "10px");
			scorebox.setPixelSize(24, 10);
			scorebox.setReadOnly( true );
			scorebox.setText("0");
			
			hp.add( label );
			hp.add( scorebox );
			
			hscore.add( hp );
			
			
			xs = new ArrayList<Double>();
			ys = new ArrayList<Double>();
			
			int w = cv.getCoordinateSpaceWidth();
			int h = cv.getCoordinateSpaceHeight();
			
			double x;
			double y;
			
			int ws = worms.size();
			if( ws == 0 ) {
				a = Math.PI/2.0;
				x = w/2.0;
				y = h-10.0;
			} else if( ws == 1 ) {
				a = 0.0;
				x = 10.0;
				y = h/2.0;
			} else {//if( ws == 2 ) {
				a = Math.PI;
				x = w-10.0;
				y = h/2.0;
			}
			
			xs.add(x);
			ys.add(y);
			
			this.left = left;
			this.right = right;
			this.leftv = leftv;
			this.rightv = rightv;
		}
		
		public void kill() {
			xs.clear();
			ys.clear();
			
			hscore.remove( hp );
			worms.remove( this );
		}
		
		public void advance( Context2d context ) {
			if( xs.size() == 0 ) {
				this.kill();
			} else {				
				if( l < xs.size() ) {
					i = Math.min( i, xs.size()-1 );
					double tx = xs.remove( (i+1)%xs.size() );
					double ty = ys.remove( (i+1)%ys.size() );
					draw( context, tx, ty, r+2.0, "#000000", "#000000" );
				} else {
					if( keyset.contains( left ) || keyset.contains( leftv ) ) {
						a += Math.PI/16.0;
					}
					
					if( keyset.contains( right ) || keyset.contains( rightv ) ) {
						a -= Math.PI/16.0;
					}
					
					double dx = s*Math.cos(a);
					double dy = s*Math.sin(a);
					double ox = getX();
					double oy = getY();
					double x = ox+dx;
					double y = oy-dy;
					
					i = (i+1)%l;
					if( xs.size() < l ) {
						xs.add( i, x );
						ys.add( i, y );
					} else {
						double	tx = xs.set( i, x );
						double	ty = ys.set( i, y );
						draw( context, tx, ty, r+2.0, "#000000", "#000000" );
					}
					
					dx = (x-applex);
					dy = (y-appley);
					if( Math.sqrt( dx*dx + dy*dy ) < appler+1.0 ) {
						context.beginPath();
						context.setFillStyle("#000000");
						context.setStrokeStyle("#000000");
						context.arc(applex, appley, appler-2.0, 0, 2.0*Math.PI);
						context.fill();
						context.stroke();
						context.closePath();
						
						score++;
						scorebox.setText( ""+score );
						l += d;
						
						setApple( context );
					}
					
					ImageData id = context.getImageData(x, y, 1.0, 1.0);
					int blue = id.getBlueAt(0, 0);
					int red = id.getRedAt(0, 0);
					int green = id.getGreenAt(0, 0);
					
					int wbound = cv.getCoordinateSpaceWidth();
					int hbound = cv.getCoordinateSpaceHeight();
					
					if( blue != 0 || green != 0 || red != 0 || x < 0 || y < 0 || x >= wbound || y >= hbound ) {
						l = 0;
					} else {
						draw( context, x, y, r, c, "#111111" );
					}
				}
			}
		}
		
		public void draw( Context2d context, double tx, double ty, double tr, String fillcolor, String strokecolor ) {
			context.setFillStyle( fillcolor );
			context.setStrokeStyle( strokecolor );
			context.beginPath();
			context.arc(tx, ty, tr, 0, 2.0*Math.PI);
			context.fill();
			context.stroke();
			context.closePath();
		}
		
		public double getX() {
			return xs.get(i);
		}
		
		public double getY() {
			return ys.get(i);
		}
	};
	
	public void updateCoordinates( Canvas cv, boolean init ) {
		final Context2d context = cv.getContext2d();
		if( init ) {
			context.getCanvas().setWidth( w-2 );
			context.getCanvas().setHeight( h-70 );
			//cv.setWidth( w+"px" );
			//cv.setHeight( h+"px" );
			cv.setCoordinateSpaceWidth( context.getCanvas().getWidth() );
			cv.setCoordinateSpaceHeight( context.getCanvas().getHeight() );
		}
		
		context.setFillStyle("#000000");
		context.fillRect(0, 0, cv.getCoordinateSpaceWidth(), cv.getCoordinateSpaceHeight());
		
		for( Worm w : worms ) {
			List<Double>	xs = w.xs;
			List<Double>	ys = w.ys;
			for( int i = 0; i < xs.size(); i++ ) {
				double x =  xs.get(i);
				double y =  ys.get(i);
				w.draw( context, x, y, w.r, w.c, "#111111" );
			}
		}
		if( !init ) setApple( context );
	}
	
	public native void console( String s ) /*-{
		$wnd.console.log( s );
	}-*/;
	
	public void drawStartMessage( Context2d context ) {
		String msg = "Press enter to add new worm";
		context.setFillStyle("#eeeeee");
		TextMetrics tm = context.measureText( msg );
		context.fillText(msg, (context.getCanvas().getWidth()-tm.getWidth())/2.0, context.getCanvas().getHeight()/2.0);
	}
	
	String	uid = null;
	public com.google.gwt.dom.client.Element loginButton() {
		//<fb:login-button show-faces="true" width="200" max-rows="1"></fb:login-button>
		
		com.google.gwt.dom.client.Element elem = Document.get().createElement("fb:login-button");
		elem.setAttribute("width", "50");
		//elem.setAttribute("show-faces", "true");
		elem.setAttribute("max-rows", "1");
		//elem.setAttribute("perms", "user_birthday,friends_birthday,user_relationships,friends_relationships" );
		elem.setId("fblogin");
		
		return elem;
	}
	
	public native void checkLoginStatus() /*-{
		var ths = this;		
		$wnd.fbAsyncInit = function() {
	    	$wnd.FB.init({appId: '215097581865564', status: true, cookie: true, xfbml: true});
	    	
	    	try {
				$wnd.FB.getLoginStatus( function(response) {
					$wnd.console.log( "inside login response" );
					try {
						$wnd.FB.XFBML.parse();
						if (response.session) {
							var uid = response.session.uid;
							ths.@org.simmi.client.Webworm::setUserId(Ljava/lang/String;)( uid );
						} else {
							ths.@org.simmi.client.Webworm::setUserId(Ljava/lang/String;)( "" );
						}
					} catch( e ) {
						$wnd.console.log( e );
					}
				});
			} catch( e ) {
				$wnd.console.log( e );
			}
	  	};
	}-*/;
	
	public void setUserId( String val ) {
		uid = val;
	}

	int				w, h;
	Canvas			cv;
	Timer 			timer;
	Set<Worm>		worms;
	HorizontalPanel	hscore;
	public void onModuleLoad() {
		final RootPanel		rp = RootPanel.get();
		Style				st = rp.getElement().getStyle();
		
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		st.setBorderWidth(0.0, Unit.PX);
		st.setBackgroundColor("#222222");
		
		
		final VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		st = vp.getElement().getStyle();
		st.setBackgroundColor( "#222222" );
		
		//FocusPanel	fp = new FocusPanel();
		//fp.setWidth( "100%" );
		//fp.setHeight( "100%" );
		cv = Canvas.createIfSupported();
		st = cv.getElement().getStyle();
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		st.setBorderWidth(0.0, Unit.PX);
		cv.setWidth("100%");
		cv.setHeight("100%");
		
		vp.setWidth( "100%" );
		vp.setHeight( "100%" );
		//fp.add( cv );
		
		worms = new HashSet<Worm>();
		w = Window.getClientWidth();
		h = Window.getClientHeight();
		rp.setWidth( w+"px" );
		rp.setHeight( h+"px" );
		//w = Window.getClientWidth();
		//h = Window.getClientHeight();
		updateCoordinates( cv, true );
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				w = event.getWidth();
				h = event.getHeight();
				rp.setWidth( w+"px" );
				rp.setHeight( h+"px" );
				
				//console( w + " " + Window.getClientWidth() + " " + h + " " + Window.getClientHeight() );
				//rp.setWidth( (w-100)+"px" );
				//rp.setHeight( (h-100)+"px" );
				updateCoordinates( cv, true );
			}
		});
		
		final TextBox	timebox = new TextBox();
		timebox.setReadOnly( true );
		timebox.setPixelSize( 32, 10 );
		timebox.setSize( "32px", "10px" );
		
		timer = new Timer() {
			long count = 0;
			@Override
			public void run() {
				if( worms.size() == 0 ) {
					this.cancel();
					
					drawStartMessage( cv.getContext2d() );
				}
				if( !pause ) {
					Context2d context = cv.getContext2d();
					for( Worm w : worms ) {
						w.advance( context );
					}
					if( (count++)%50 == 0 ) timebox.setText( ""+(count/50) );
				}
			}
			
			public void cancel() {
				super.cancel();
				count = 0;
			}
		};
		
		cv.addKeyDownHandler( this );
		cv.addKeyUpHandler( this );
		cv.setFocus( true );
		drawStartMessage( cv.getContext2d() );
		
		//Style style;
		HorizontalPanel	hp = new HorizontalPanel();
		hp.getElement().getStyle().setColor("#ffffff");
		hp.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		//hp.setHeight("10px");
		hp.setSpacing( 5 );
		
		Anchor contact = new Anchor("huldaeggerts@gmail.com");
		contact.setHref("mailto:huldaeggerts@gmail.com");
		hp.add( contact );
		
		HTML html = new HTML("|");
		hp.add( html );
		
		Anchor smas = new Anchor("smasogur.is");
		smas.setHref("http://smasogur.is");
		hp.add( smas );
	
		html = new HTML("|");
		hp.add( html );
		
		Anchor fast = new Anchor("fasteignaverd.appspot.com");
		fast.setHref("fasteignaverd.appspot.com");
		hp.add( fast );
		
		html = new HTML("|");
		hp.add( html );
		
		/*<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
		<input type="hidden" name="cmd" value="_s-xclick">
		<input type="hidden" name="hosted_button_id" value="L4TC92APSZDHW">
		<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
		<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
		</form>*/
		
		FormPanel	form = new FormPanel();
		form.setAction( "https://www.paypal.com/cgi-bin/webscr" );
		form.setMethod( FormPanel.METHOD_POST );
		
		HorizontalPanel	holder = new HorizontalPanel();
		holder.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		holder.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		
		Hidden 		cmd = new Hidden("cmd", "_s-xclick");
		Hidden		hosted_button_id = new Hidden("hosted_button_id", "L4TC92APSZDHW");
		Image		img = new Image( "https://www.paypalobjects.com/en_US/i/scr/pixel.gif" );
		img.setAltText("");
		img.setWidth("1");
		img.setHeight("1");
		
		InputElement iel = Document.get().createImageInputElement();
		iel.setName("submit");
		iel.setSrc( "https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif" );
		iel.setAlt( "PayPal - The safer, easier way to pay online!" );
		
		SimplePanel	imageinput = new SimplePanel();
		imageinput.getElement().appendChild( iel );
		
		holder.add( cmd );
		holder.add( hosted_button_id );
		holder.add( imageinput );
		holder.add( img );
		
		form.add( holder );
		hp.add( form );
		
		hscore = new HorizontalPanel();
		hscore.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		hscore.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		hscore.setWidth("400px");
		
		Label timelabel = new Label("Time:");
		timelabel.getElement().getStyle().setColor("#eeeeee");
		
		HorizontalPanel	coverpanel = new HorizontalPanel();
		coverpanel.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		coverpanel.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		coverpanel.setSpacing( 5 );
		coverpanel.setWidth("100%");
		
		HorizontalPanel timepanel = new HorizontalPanel();
		timepanel.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		timepanel.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		timepanel.setWidth("100px");
		
		timepanel.add( timelabel );
		timepanel.add( timebox );
		
		SimplePanel	sp = new SimplePanel();
		sp.getElement().appendChild( loginButton() );
		sp.setWidth("100px");
		
		coverpanel.add( timepanel );
		coverpanel.add( hscore );
		coverpanel.add( sp );
		
		Element e = Document.get().createElement("script");
		e.setAttribute("async", "true");
		e.setAttribute("src", "http://connect.facebook.net/en_US/all.js" );
		Document.get().getElementById("fb-root").appendChild(e);
		
		checkLoginStatus();
		
		vp.add( coverpanel );
		vp.add( cv );
		vp.add( hp );
		
		rp.add( vp );
	}
	
	Set<Integer>	keyset = new HashSet<Integer>();

	@Override
	public void onKeyUp(KeyUpEvent event) {
		keyset.remove( event.getNativeKeyCode() );
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int keycode = event.getNativeKeyCode();
		if( keycode == ' ' ) {
			pause = !pause;
		} else if( keycode == KeyCodes.KEY_ESCAPE ) {
			timer.cancel();
			Set<Worm>	wset = new HashSet<Worm>( worms );
			for( Worm w : wset ) {
				w.kill();
			}
			wset.clear();
			drawStartMessage( cv.getContext2d() );
		} else if( keycode == KeyCodes.KEY_ENTER ) {
			int ws = worms.size();
			if( ws == 0 ) {
				worms.add( new Worm("#00ff00", KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT, KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT ) );
				updateCoordinates(cv, false);
				
				if( timer != null ) {
					timer.scheduleRepeating( 20 );
				}
			}
			else if( ws == 1 ) {
				Worm w = null;
				for( Worm ww : worms ) {
					w = ww;
					break;
				}
				if( w.c.equals("#00ff00") ) worms.add( new Worm("#0000ff", 'z', 'x', 'Z', 'X') );
				else worms.add( new Worm("#00ff00", KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT, KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT) );
			} else {
				Worm w1 = null;
				Worm w2 = null;
				for( Worm ww : worms ) {
					if( w1 == null ) w1 = ww;
					else {
						w2 = ww;
						break;
					}
				}
				if( (w1.c.equals("#00ff00") && w2.c.equals("#ff0000")) || (w2.c.equals("#00ff00") && w1.c.equals("#ff0000")) ) {
					worms.add( new Worm("#0000ff", 'z', 'x', 'Z', 'X') );
				} else if( (w1.c.equals("#0000ff") && w2.c.equals("#ff0000")) || (w2.c.equals("#0000ff") && w1.c.equals("#ff0000")) ) {
					worms.add( new Worm("#00ff00", KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT, KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT) );
				} else {
					worms.add( new Worm("#ff0000", 'n', 'm', 'N', 'M') );
				}
			}
		} else {
			keyset.add( keycode );
		}
	}
}
