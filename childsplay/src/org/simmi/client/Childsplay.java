package org.simmi.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import elemental.client.Browser;
import elemental.html.AudioContext;
import elemental.html.AudioGainNode;
import elemental.html.AudioParam;
import elemental.html.Oscillator;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Childsplay implements EntryPoint {
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
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	long counter = 0;	
	public void doRandom( Context2d context ) {
		int w = context.getCanvas().getWidth();
		int h = context.getCanvas().getHeight();
		if( ++counter % 100 == 0 ) context.clearRect( 0, 0, w, h );
		
		String color = "#"+Integer.toString( Random.nextInt(256), 16 )+Integer.toString( Random.nextInt(256), 16 )+Integer.toString( Random.nextInt(256), 16 );
		context.setFillStyle( color );
		context.setStrokeStyle("#000000");
		int type = Random.nextInt( 3 );
		int x = Random.nextInt( w );
		int y = Random.nextInt( h );
		int r = Random.nextInt( 250 )+50;
		if( type == 0 ) {
			context.save();
			context.scale( Random.nextDouble()+0.5, Random.nextDouble()+0.5 );
			context.beginPath();
			context.arc( x, y, r, 0, 2.0*Math.PI );
			context.fill();
			context.restore();
			context.stroke();
			context.closePath();
		} else if( type == 1 ) {
			double rx = r*(Random.nextDouble()+0.5);
			double ry = r*(Random.nextDouble()+0.5);
			context.fillRect(x-rx/2.0, y-ry/2.0, rx, ry);
			context.strokeRect(x-rx/2.0, y-ry/2.0, rx, ry);
		} else {
			context.save();
			context.scale( Random.nextDouble()+0.5, Random.nextDouble()+0.5 );
			context.beginPath();
			context.moveTo(x, y-r/2.0);
			context.lineTo(x+r/2.0, y+r/2.0);
			context.lineTo(x-r/2.0, y+r/2.0);
			context.lineTo(x, y-r/2.0);
			context.restore();
			context.fill();
			context.stroke();
			context.closePath();
		}
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Canvas canvas = Canvas.createIfSupported();
		final RootPanel rp = RootPanel.get();
		
		Style st = rp.getElement().getStyle();
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		st.setBorderWidth(0.0, Unit.PX);
		
		elemental.html.Window wnd = Browser.getWindow();
		AudioContext acontext = wnd.newAudioContext();
		final AudioGainNode agn = acontext.createGainNode();
		final Oscillator osc = acontext.createOscillator();
		osc.setType( Oscillator.SINE );
		osc.connect( (AudioParam)agn, 0 );
		agn.connect( (AudioParam)acontext.getDestination(), 0 );
		
		Window.enableScrolling( false );
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize( w+"px", h+"px" );
		canvas.setSize( w+"px", h+"px" );
		canvas.setCoordinateSpaceWidth( w );
		canvas.setCoordinateSpaceHeight( h );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				rp.setSize( w+"px", h+"px" );
				canvas.setSize( w+"px", h+"px" );
				canvas.setCoordinateSpaceWidth( w );
				canvas.setCoordinateSpaceHeight( h );
			}
		});
		
		float cbrt = (float)Math.pow(2.0, 1.0/3.0);
		final float[] ff = { 100.0f, 100.0f*cbrt, 150.0f, 200.0f, 200.0f*cbrt, 300.0f, 400.0f, 400.0f*cbrt, 600.0f, 800.0f };
		canvas.addKeyDownHandler( new KeyDownHandler() {
			int prev = 0;
			@Override
			public void onKeyDown(KeyDownEvent event) {
				int next = Random.nextInt( ff.length );
				while( next == prev ) {
					next = Random.nextInt( ff.length );
				}
				float fval = ff[ next ];
				osc.getFrequency().setValue( fval );
				agn.getGain().setValue( (900.0f-fval)/800.0f );
				osc.noteOn( 0 );
				doRandom( canvas.getContext2d() );
				prev = next;
			}
		});
		
		canvas.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				osc.getFrequency().setValue( (float)(Random.nextDouble()*500.0+100.0) );
				osc.noteOn( 0 );
				doRandom( canvas.getContext2d() );
			}
		});
		
		rp.add( canvas );
	}
}
