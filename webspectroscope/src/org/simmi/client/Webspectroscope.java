package org.simmi.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.typedarrays.client.Float32ArrayNative;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.dom.LocalMediaStream;
import elemental.dom.RequestAnimationFrameCallback;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.AudioBuffer;
import elemental.html.AudioContext;
import elemental.html.AudioDestinationNode;
import elemental.html.AudioProcessingEvent;
import elemental.html.AudioSourceNode;
import elemental.html.Float32Array;
import elemental.html.JavaScriptAudioNode;
import elemental.html.Navigator;
import elemental.html.NavigatorUserMediaError;
import elemental.html.NavigatorUserMediaErrorCallback;
import elemental.html.NavigatorUserMediaSuccessCallback;
import elemental.html.RealtimeAnalyserNode;
import elemental.util.Mappable;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Webspectroscope implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public native Float32Array ok( Float32ArrayNative fa ) /*-{
		return fa;
	}-*/;
	
	public native AudioSourceNode createMediaStreamSource( AudioContext acontext, LocalMediaStream stream ) /*-{
		return acontext.createMediaStreamSource( stream );
	}-*/;
	
	public native void startRendering( AudioContext acontext, Canvas canvas ) /*-{
		return acontext.startRendering();
		//.then(function(renderedBuffer) {
		//	this.@org.simmi.client.Webspectroscope::then(Lcom/google/gwt/canvas/client/Canvas;Lelemental/html/AudioBuffer;)( canvas, renderedBuffer );
		//});
	}-*/;
	
	public void then( Canvas canvas, AudioBuffer ab ) {
		final Context2d 		context2d = canvas.getContext2d();
		
		int w = canvas.getCoordinateSpaceWidth();
		int h = canvas.getCoordinateSpaceHeight();
		context2d.clearRect(0, 0, w, h);
								
		//AudioProcessingEvent	ape = (AudioProcessingEvent)evt;
		//AudioBuffer				ab = ape.getInputBuffer();
		
		//rtan.getFloatFrequencyData( fael );
		
		Float32Array 			farray = ab.getChannelData( 0 );
		int len = farray.length();
		
		final elemental.html.Window 		wnd = Browser.getWindow();
		wnd.getConsole().log( farray.length() + "eeeerm " + ab.getChannelData(0).numberAt(0) + " channel " + ab.getChannelData(0).numberAt(4000) );
		
		context2d.setStrokeStyle("#00aa00");
		context2d.beginPath();
		context2d.moveTo(0, 240);
		for( int i = 0; i < len; i++ ) {
			double val = farray.numberAt(i); //.get(i);
			context2d.lineTo( i*w/len, 240+val*200 );
		}
		//context2d.closePath();
		context2d.stroke();
	}
	
	public native JavaScriptAudioNode createScriptProcessor( AudioContext acontext, int bufferSize, int nInputNodes, int nOutputNodes ) /*-{
		return acontext.createScriptProcessor( bufferSize, nInputNodes, nOutputNodes );
	}-*/;
	
	public native AudioContext newAudioContext(int numChannels, int length, int sampleRate) /*-{
		return new (window.AudioContext || window.webkitAudioContext)();//new OfflineAudioContext(numChannels, length, sampleRate);
	}-*/;
	
	public native Mappable audio() /*-{
		return {audio: true};
	}-*/;

	RequestAnimationFrameCallback 	ra = null;
	ImageData						imd;
	
	public void onModuleLoad() {
		final RootPanel		rp = RootPanel.get();
		
		Style st = rp.getElement().getStyle();
		st.setBorderWidth( 0.0, Unit.PX );
		st.setMargin( 0.0, Unit.PX );
		st.setPadding( 0.0, Unit.PX );
		
		final Canvas		canvas = Canvas.createIfSupported();
		
		final VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		//vp.setWidth(w+"px");
		
		canvas.setSize( w+"px", 480+"px" );
		canvas.setCoordinateSpaceWidth( w );
		canvas.setCoordinateSpaceHeight( 480 );
		final Context2d 		context2d = canvas.getContext2d();
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				canvas.setSize( w+"px", 480+"px" );
				canvas.setCoordinateSpaceWidth( w );
				
				//vp.setSize(w+"px", h+"px");
				
				imd = context2d.getImageData(0, 0, w, h);
			}
		});
		imd = context2d.getImageData(0, 0, w, h);
		context2d.clearRect(0, 0, w, h);
		
		//VerticalPanel	subvp = new VerticalPanel();
		//subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		//subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		//subvp.add( canvas );
		
		//vp.add( subvp );
		//rp.add( vp );
		
		final elemental.html.Window 		wnd = Browser.getWindow();
		final Navigator 					nvg = wnd.getNavigator();
		
		nvg.webkitGetUserMedia( audio(), new NavigatorUserMediaSuccessCallback() {
			@Override
			public boolean onNavigatorUserMediaSuccessCallback(LocalMediaStream stream) {
				//AudioElement ael = (AudioElement)Browser.getDocument().querySelector("audio");
				//stuff( ael, stream );
				//MediaElementAudioSourceNode microphone = acontext.createMediaElementSource( ael );
				
				//final OfflineAudioContext 				oacontext = new OfflineAudioContext();
				//AudioContext	acontext = (AudioContext)oacontext;
				AudioContext 					acontext = newAudioContext(1,2048,44100);
				final AudioSourceNode 			microphone = createMediaStreamSource(acontext, stream);
				final AudioDestinationNode		output = acontext.getDestination();
				
				final Float32ArrayNative		fa = Float32ArrayNative.create(4096);
				final Float32Array				fael = ok( fa );
				
				final RealtimeAnalyserNode 		rtan = acontext.createAnalyser();
				rtan.setFftSize( 4096 );
				
				microphone.connect( rtan, 0, 0 );
				rtan.connect( output, 0, 0 );
				
				/*JavaScriptAudioNode analyser = acontext.createJavaScriptNode(4096);
				analyser.setOnaudioprocess( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						int bin = rtan.getFrequencyBinCount();
						Uint8Array ua = ok( bin );
						//rtan.getFloatFrequencyData( ua );
						//rtan.getByteFrequencyData( ua );
						rtan.getByteTimeDomainData( ua );
						wnd.getConsole().log("erm "+bin+"  "+ua.numberAt( bin/2 ));
					}
				});*/
				
				//anal( acontext, stream, rtan, asn, acontext.getDestination() );
				//microphone.connect(analyser, 0,0);
				//rtan.connect(analyser, 0,0);
		        //rtan.connect(acontext.getDestination(), 0, 0);
				
				/*int w = Window.getClientWidth();
				int h = Window.getClientHeight();
				context2d.clearRect(0, 0, w, h);
				context2d.setFillStyle("#00ff00");
				context2d.fillRect(10.0, 10.0, 100.0, 100.0 );*/
				ra = new RequestAnimationFrameCallback() {
					@Override
					public boolean onRequestAnimationFrameCallback(double time) {
						if( ra != null ) {
							double w = canvas.getCoordinateSpaceWidth();
							double h = canvas.getCoordinateSpaceHeight();
							
							//rtan.getf
							//rtan.getByteFrequencyData( ua );
							//rtan.getByteTimeDomainData(ua);
							//stream.
							//rtan.getMinDecibels();
							rtan.getFloatFrequencyData( fael );
							//int len = fa.length();
							
							context2d.drawImage( canvas.getCanvasElement(), 1.0, 0.0, w-1.0, h, 0.0, 0.0, w-1.0, h );
							//ImageData imd = context2d.getImageData(w-1, 0, 1, h);
							
							for( int i = 0; i < fa.length()/2; i++ ) {
								double val = fa.get(i);
								int green = Math.min( 255, Math.max(0, -(int)(10*val+500)));
								
								String gg = green < 16 ? "0"+Integer.toString( green, 16 ) : Integer.toString( green, 16 );
								String color = "#"+gg+"ff"+gg;
								context2d.setFillStyle(color);
								
								//double hloc = i*480*2/fa.length();
								double hloc = 500.0*Math.log10(i+100.0)-1000.0;
								context2d.fillRect(w-1.0, hloc, 1.0, 1.0);
							}
							/*for( int i = 0; i < h; i++ ) {
								double val = fa.get(i);
								int green = Math.min( 255, Math.max(0, (int)val) );
								
								imd.setGreenAt( green, 0, i);
							}
							context2d.putImageData(imd, w-1, 0);
							/*context2d.setStrokeStyle("#00aa00");
							context2d.beginPath();
							context2d.moveTo(0, 240);
							for( int i = 0; i < len; i++ ) {
								double val = fa.get(i);
								context2d.lineTo( i*w/len, 240+val );
							}
							//context2d.closePath();
							context2d.stroke();*/
							
							wnd.webkitRequestAnimationFrame( ra );
							return true;
						}
						return false;
					}
				};
		        wnd.webkitRequestAnimationFrame( ra );
				
				return false;
			}
		}, new NavigatorUserMediaErrorCallback() {
			public boolean onNavigatorUserMediaErrorCallback(NavigatorUserMediaError error) {
				wnd.getConsole().log( error.toString() + "  " + error.getCode() );
				return false;
			}
		});
		
		//VerticalPanel	vp = new VerticalPanel();
		//vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		//vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		//vp.setSize("100%", "100%");
		
		//vp.setSpacing(10);
		
		HTML html = new HTML("Web Spectroscope");
		st = html.getElement().getStyle();
		st.setFontSize(24.0, Unit.PX);
		st.setPaddingTop(10.0, Unit.PX);
		st.setPaddingBottom(10.0, Unit.PX);
		vp.add( html );
		
		html = new HTML("Monitors your environmental noise. Lines in the graph mean constant noise, f.x. the computer fan.<br>The lower the lines are, the more high pitched the sound is.");
		st = html.getElement().getStyle();
		st.setPaddingTop(10.0, Unit.PX);
		st.setPaddingBottom(10.0, Unit.PX);
		vp.add( html );
		
		vp.add( canvas ); //new HTML("<applet codebase=\"http://webspectroscope.appspot.com\" archive=\"spectroscope.jar\" code=\"org.simmi.FftApplet\" width=\"1024\" height=\"512\" jnlp_href=\"webspectroscpe.jnlp\"><param name=\"jnlp_href\" value=\"webspectroscope.jnlp\"/></applet>") );
		vp.add( new HTML("<a href=\"mailto:huldaeggerts@gmail.com\">huldaeggerts@gmail.com</a> | More apps: <a href=\"http://starwarsflyingtext.appspot.com\">http://starwarsflyingtext.appspot.com</a> | <a href=\"http://nutritiondb.appspot.com\">http://nutritiondb.appspot.com</a> | <a href=\"http://suggestadate.appspot.com\">http://suggestadate.appspot.com</a> | <a href=\"http://webspectroscope.appspot.com/webspectroscope.jnlp\">Java applet</a>") );
		
		RootPanel.get("webspect").add( vp );
	}
}
