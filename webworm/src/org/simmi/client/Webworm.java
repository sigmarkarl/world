package org.simmi.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.RootPanel;

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
		context.setFillStyle("#ff0000");
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
		
		int		left;
		int		right;
		
		List<Double>	xs;
		List<Double>	ys;
		
		int 	i;
		int		l;
		
		public Worm( String c, int left, int right ) {
			r = 5.0;
			a = Math.PI/2.0;
			s = 7.0;
			l = 32;
			i = 0;
			this.c = c;
			
			xs = new ArrayList<Double>();
			ys = new ArrayList<Double>();
			
			double x = w/2.0;
			double y = h-20.0;
			xs.add(x);
			ys.add(y);
			
			this.left = left;
			this.right = right;
		}
		
		public void advance( Context2d context ) {			
			if( xs.size() == 0 ) {
				worms.remove( this );
			} else {				
				if( l < xs.size() ) {
					i = Math.min( i, xs.size()-1 );
					double tx = xs.remove( (i+1)%xs.size() );
					double ty = ys.remove( (i+1)%ys.size() );
					draw( context, tx, ty, r+1.0, "#000000", "#000000" );
				} else {
					if( keyset.contains( left ) ) {
						a += Math.PI/16.0;
					}
					
					if( keyset.contains( right ) ) {
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
						draw( context, tx, ty, r+1.0, "#000000", "#000000" );
					}
					
					dx = (x-applex);
					dy = (y-appley);
					if( Math.sqrt( dx*dx + dy*dy ) < appler+1.0 ) {
						context.beginPath();
						context.setFillStyle("#000000");
						context.setStrokeStyle("#000000");
						context.arc(applex, appley, appler-4.0, 0, 2.0*Math.PI);
						context.fill();
						context.stroke();
						context.closePath();
						
						l += 16;
						
						setApple( context );
					}
					
					ImageData id = context.getImageData(x, y, 1.0, 1.0);
					int blue = id.getBlueAt(0, 0);
					int red = id.getRedAt(0, 0);
					int green = id.getGreenAt(0, 0);
					
					if( blue != 0 || green != 0 || red != 0 ) l = 0;
					else {
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
		if( init ) {
			cv.setWidth( w+"px" );
			cv.setHeight( h+"px" );
			cv.setCoordinateSpaceWidth( w );
			cv.setCoordinateSpaceHeight( h );
		}
		
		final Context2d context = cv.getContext2d();
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
		setApple( context );
	}

	int			w, h;
	Canvas		cv;
	Timer 		timer;
	Set<Worm>	worms;
	public void onModuleLoad() {
		final RootPanel		rp = RootPanel.get();
		Style				st = rp.getElement().getStyle();
		
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		
		//FocusPanel	fp = new FocusPanel();
		//fp.setWidth( "100%" );
		//fp.setHeight( "100%" );
		cv = Canvas.createIfSupported();
		st = cv.getElement().getStyle();
		//st.setMargin(0.0, Unit.PX);
		//st.setPadding(0.0, Unit.PX);
		//cv.setWidth( "100%" );
		//cv.setHeight( "100%" );
		//fp.add( cv );
		
		worms = new HashSet<Worm>();
		w = Window.getClientWidth();
		h = Window.getClientHeight();
		rp.setWidth( w+"px" );
		rp.setHeight( h+"px" );
		updateCoordinates( cv, true );
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				w = event.getWidth();
				h = event.getHeight();
				rp.setWidth( w+"px" );
				rp.setHeight( h+"px" );
				updateCoordinates( cv, true );
			}
		});
		
		timer = new Timer() {
			@Override
			public void run() {
				if( worms.size() == 0 ) this.cancel();
				if( !pause ) {
					for( Worm w : worms ) {
						Context2d context = cv.getContext2d();
						w.advance( context );
					}
				}
			}
		};
		
		cv.addKeyDownHandler( this );
		cv.addKeyUpHandler( this );
		
		cv.setFocus( true );
		rp.add( cv );
	}
	
	Set<Integer>	keyset = new HashSet<Integer>();

	@Override
	public void onKeyUp(KeyUpEvent event) {
		keyset.remove( event.getNativeKeyCode() );
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if( event.getNativeEvent().getKeyCode() == ' ' ) {
			pause = !pause;
		} else if( event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE ) {
			timer.cancel();
			worms.clear();
		} else if( event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER ) {
			int ws = worms.size();
			if( ws == 0 ) {
				worms.add( new Worm("#00ff00", KeyCodes.KEY_LEFT, KeyCodes.KEY_RIGHT) );
				updateCoordinates(cv, false);
				
				if( timer != null ) {
					timer.scheduleRepeating( 50 );
				}
			}
			else if( ws == 1 ) worms.add( new Worm("#0000ff", 'z', 'x') );
			else worms.add( new Worm("#ff0000", ',', '.') );
		} else {
			keyset.add( event.getNativeKeyCode() );
		}
	}
}
