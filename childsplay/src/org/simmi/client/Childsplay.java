package org.simmi.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.typedarrays.shared.Uint8Array;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.AudioBuffer;
import elemental.html.AudioContext;
import elemental.html.AudioGainNode;
import elemental.html.AudioParam;
import elemental.html.JavaScriptAudioNode;
import elemental.html.MediaElement;
import elemental.html.MediaElementAudioSourceNode;
import elemental.html.Oscillator;
import elemental.html.RealtimeAnalyserNode;

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
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	long counter = 0;	
	public void doRandom( Context2d context, int x, int y ) {
		int w = context.getCanvas().getWidth();
		int h = context.getCanvas().getHeight();
		if( counter++ % 100 == 0 ) context.clearRect( 0, 0, w, h );
		
		String color = "#"+Integer.toString( Random.nextInt(256), 16 )+Integer.toString( Random.nextInt(256), 16 )+Integer.toString( Random.nextInt(256), 16 );
		context.setFillStyle( color );
		context.setStrokeStyle("#000000");
		int type = Random.nextInt( 3 );
		if( x == -1 ) x = Random.nextInt( w );
		if( y == -1 ) y = Random.nextInt( h );
		int r = Random.nextInt( 250 )+50;
		if( type == 0 ) {
			context.save();
			double scalex = Random.nextDouble()+0.5;
			double scaley = Random.nextDouble()+0.5;
			context.scale( scalex, scaley );
			context.beginPath();
			context.arc( x/scalex, y/scaley, r, 0, 2.0*Math.PI );
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
			double scalex = Random.nextDouble()+0.5;
			double scaley = Random.nextDouble()+0.5;
			context.scale( scalex, scaley );
			context.beginPath();
			context.moveTo(x/scalex, y/scaley-r/2.0);
			context.lineTo(x/scalex+r/2.0, y/scaley+r/2.0);
			context.lineTo(x/scalex-r/2.0, y/scaley+r/2.0);
			context.lineTo(x/scalex, y/scaley-r/2.0);
			context.restore();
			context.fill();
			context.stroke();
			context.closePath();
		}
	}
	
	public native void loadAudioIn( AudioContext acontext ) /*-{
		var s = this;
		function onError( error ) {
			$wnd.console.log( 'err ' + error.code, error );
		}
		function onSuccess( stream ) {
			var audio = $doc.querySelector('audio');
			audio.src = $wnd.URL.createObjectURL( stream );
			console.log("mmmuuu2");
			var analyser = acontext.createAnalyser();
			analyser.smoothingTimeConstant = 0.3;
        	analyser.fftSize = 1024;
        	console.log("mmmuuu3");
        	javascriptNode = acontext.createJavaScriptNode(2048, 1, 1);
        	console.log("mmmuuu4");
        	javascriptNode.onaudioprocess = function() {
		 		console.log("mmmuuu");
		        // get the average, bincount is fftsize / 2
		        var array =  new Uint8Array(analyser.frequencyBinCount);
		        analyser.getByteFrequencyData(array);
		        var average = getAverageVolume(array)
		 
		        // clear the current state
		        ctx.clearRect(0, 0, 60, 130);
		 
		        // set the fill style
		        ctx.fillStyle=gradient;
		 
		        // create the meters
		        ctx.fillRect(0,130-average,25,130);
		    }
		    
		     function getAverageVolume(array) {
		        var values = 0;
		        var average;
		 
		        var length = array.length;
		 
		        // get all the frequency amplitudes
		        for (var i = 0; i < length; i++) {
		            values += array[i];
		        }
		 
		        average = values / length;
		        return average;
		    }
			//s.@org.simmi.client.Childsplay::audioContinue(Lelemental/html/MediaElement;)( audio );
		}
		$wnd.navigator.webkitGetUserMedia( {audio: true}, onSuccess, onError );
		console.log("mmmuuu5");
	}-*/;
	
	AudioContext 	acontext = null;
	Oscillator 		osc = null;
	public void audioContinue( MediaElement me ) {
		MediaElementAudioSourceNode measn = acontext.createMediaElementSource( me );
		AudioBuffer ab = acontext.createBuffer( null, true );
		
		final Uint8Array array;
		final RealtimeAnalyserNode ran = acontext.createAnalyser();
		final JavaScriptAudioNode jsan = acontext.createJavaScriptNode(2048);
		jsan.setOnaudioprocess( new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				//ran.getFloatFrequencyData(array);
			}
		});
		//measn.connect(destination, output, input)
	}
	
	public native boolean checkAudioSupport() /*-{
		return $wnd.AudioContext || $wnd.webkitAudioContext;
	}-*/;
	
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
		
		final elemental.html.Window wnd = Browser.getWindow();
		if( checkAudioSupport() ) {
			acontext = wnd.newAudioContext();
			
			final AudioGainNode agn = acontext.createGainNode();
			osc = acontext.createOscillator();
			osc.setType( Oscillator.SINE );
			osc.connect( (AudioParam)agn, 0 );
			agn.connect( (AudioParam)acontext.getDestination(), 0 );
		} else Browser.getWindow().getConsole().log("Audio not supported");
		
		//if( acontext != null ) loadAudioIn( acontext );
		
		/*Navigator nv = wnd.getNavigator();
		Mappable mbl = new Mappable() {
			
			@Override
			public void setAt(String key, Object value) {}
			
			@Override
			public Object at(String key) {
				wnd.getConsole().log(key);
				if( key.equals("audio") ) return true;
				return false;
			}
		};
		
		NavigatorUserMediaSuccessCallback onSuccess = new NavigatorUserMediaSuccessCallback() {
			@Override
			public boolean onNavigatorUserMediaSuccessCallback(LocalMediaStream stream) {
				wnd.getConsole().log("succ");
				
				return false;
			}
		};
		NavigatorUserMediaErrorCallback onError = new NavigatorUserMediaErrorCallback() {
			@Override
			public boolean onNavigatorUserMediaErrorCallback(NavigatorUserMediaError error) {
				return false;
			}
		};*/
		//nv.webkitGetUserMedia( mbl, onSuccess, onError );
		
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
		
		String starttext = "Press mouse key or touch to start";
		final Context2d context = canvas.getContext2d();
		context.setFillStyle("#000000");
		context.setFont("30pt Calibri");
		double strw = context.measureText( starttext ).getWidth();
		context.fillText(starttext, (w-strw)/2, h/2);
		
		canvas.addTouchStartHandler( new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				Touch touch = event.getTouches().get(0);
				doRandom( context, touch.getClientX(), touch.getClientY() );
			}
		});
		
		canvas.addTouchMoveHandler( new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				
			}
		});
		
		canvas.addTouchEndHandler( new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				
			}
		});
		
		canvas.addTouchCancelHandler( new TouchCancelHandler() {
			@Override
			public void onTouchCancel(TouchCancelEvent event) {
				
			}
		});
		
		canvas.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if( osc != null ) {
					osc.getFrequency().setValue( (float)(Random.nextDouble()*500.0+100.0) );
					osc.noteOn( 0 );
				}
				doRandom( context, event.getClientX(), event.getClientY() );
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
				//osc.getFrequency().setValue( fval );
				//agn.getGain().setValue( (900.0f-fval)/800.0f );
				//osc.noteOn( 0 );
				if( osc != null ) {
					osc.getFrequency().setValue( (float)(Random.nextDouble()*500.0+100.0) );
					osc.noteOn( 0 );
				}
				doRandom( context, -1, -1 );
				prev = next;
			}
		});
		
		/*canvas.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				//osc.getFrequency().setValue( (float)(Random.nextDouble()*500.0+100.0) );
				//osc.noteOn( 0 );
				doRandom( canvas.getContext2d() );
			}
		});*/
		
		rp.add( canvas );
	}
}