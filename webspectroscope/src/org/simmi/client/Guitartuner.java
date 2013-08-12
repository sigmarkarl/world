package org.simmi.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.typedarrays.client.Float32ArrayNative;
import com.google.gwt.typedarrays.client.Uint8ArrayNative;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.dom.LocalMediaStream;
import elemental.dom.RequestAnimationFrameCallback;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.AudioBuffer;
import elemental.html.AudioBufferSourceNode;
import elemental.html.AudioContext;
import elemental.html.AudioDestinationNode;
import elemental.html.AudioElement;
import elemental.html.AudioProcessingEvent;
import elemental.html.AudioSourceNode;
import elemental.html.Float32Array;
import elemental.html.JavaScriptAudioNode;
import elemental.html.Navigator;
import elemental.html.NavigatorUserMediaSuccessCallback;
import elemental.html.RealtimeAnalyserNode;
import elemental.html.Uint8Array;
import elemental.util.Mappable;

public class Guitartuner implements EntryPoint {

	public native Mappable audio() /*-{
		return {audio: true};
	}-*/;
	
	public native void stuff( AudioElement audio, LocalMediaStream stream ) /*-{
		audio.src = $wnd.URL.createObjectURL( stream );
		audio.controls = true;
	}-*/;
	
	public native Float32Array ok( Float32ArrayNative fa ) /*-{
		return fa;
	}-*/;
	
	public native Uint8Array oku( Uint8ArrayNative ua ) /*-{
		return ua;
	}-*/;
	
	public native AudioSourceNode createMediaStreamSource( AudioContext acontext, LocalMediaStream stream ) /*-{
		return acontext.createMediaStreamSource( stream );
	}-*/;
	
	public native void anal( AudioContext acontext, LocalMediaStream stream, RealtimeAnalyserNode analyser, AudioBufferSourceNode asn, AudioDestinationNode adst ) /*-{
		try {
			var ss = acontext.createMediaStreamSource( stream );
			ss.connect( adst );
			//ss.connect( analyser );
			//analyser.connect( adst );
			
			//ss.noteOn( 0 );
			
			$wnd.console.log('jujuuff');
		} catch( e ) {
			$wnd.console.log('uff'+e);
		}
	}-*/;
	
	public double medalBil( List<Integer> li, Uint8ArrayNative ua ) {
		double val = 0.0;
		
		if( li.size() > 1 ) {
			int fi = li.get(0);
			for( int i = 1; i < li.size(); i++ ) {
				int ne = li.get(i);
				val += ne-fi;
				fi = ne;
			}
			val /= li.size()-1;
		}
		
		return val;
	}
	
	RequestAnimationFrameCallback 	ra = null;
	ImageData						imd;
	
	@Override
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
		vp.setSize(w+"px", h+"px");
		
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
				
				vp.setSize(w+"px", h+"px");
				
				imd = context2d.getImageData(0, 0, w, h);
			}
		});
		imd = context2d.getImageData(0, 0, w, h);
		context2d.clearRect(0, 0, w, h);
		
		VerticalPanel	subvp = new VerticalPanel();
		subvp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		subvp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		
		subvp.add( canvas );
		
		vp.add( subvp );
		rp.add( vp );
		
		final elemental.html.Window 		wnd = Browser.getWindow();
		final Navigator 					nvg = wnd.getNavigator();
		
		nvg.webkitGetUserMedia( audio(), new NavigatorUserMediaSuccessCallback() {
			@Override
			public boolean onNavigatorUserMediaSuccessCallback(LocalMediaStream stream) {
				//AudioElement ael = (AudioElement)Browser.getDocument().querySelector("audio");
				//stuff( ael, stream );
				//MediaElementAudioSourceNode microphone = acontext.createMediaElementSource( ael );
				
				final AudioContext 				acontext = wnd.newAudioContext();
				final AudioSourceNode 			microphone = createMediaStreamSource(acontext, stream);
				final AudioDestinationNode		output = acontext.getDestination();
				
				final Float32ArrayNative		fa = Float32ArrayNative.create(1024);
				final Float32Array				fael = ok( fa );
				
				final Uint8ArrayNative			ua = Uint8ArrayNative.create(2048);
				final Uint8Array				uael = oku( ua );
				
				final RealtimeAnalyserNode 		rtan = acontext.createAnalyser();
				final JavaScriptAudioNode 		jsan = acontext.createJavaScriptNode(2048, 1);
				rtan.setFftSize( 2048 );
				
				/*final BiquadFilterNode			filter = acontext.createBiquadFilter();
				filter.setType( BiquadFilterNode.BANDPASS );
				filter.getQ().setValue( 400.0f );
				AudioParam freq = filter.getFrequency();
				freq.setValue( 440.0f );*/
				
				//jsan.getContext().
				
				microphone.connect( rtan, 0, 0 );
				//filter.connect( rtan, 0, 0 );
				rtan.connect( output, 0, 0 );
				//jsan.connect( output, 0, 0 );
				
				//final AudioBufferSourceNode 	buffer = acontext.createBufferSource();
				//asn.setLooping( true );
				//asn.connect(jsan, 0, 0);
				
				jsan.setOnaudioprocess( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						int w = canvas.getCoordinateSpaceWidth();
						int h = canvas.getCoordinateSpaceHeight();
						context2d.clearRect(0, 0, w, h);
												
						AudioProcessingEvent	ape = (AudioProcessingEvent)evt;
						AudioBuffer				ab = ape.getInputBuffer();
						
						//rtan.getFloatFrequencyData( fael );
						
						Float32Array 			farray = ab.getChannelData( 0 );
						int len = farray.length();
						//wnd.getConsole().log( ab.getChannelData(0).numberAt(0) + "  " + ab.getChannelData(0).numberAt(4000) );
						
						context2d.setStrokeStyle("#00aa00");
						context2d.beginPath();
						context2d.moveTo(0, 240);
						for( int i = 0; i < len; i++ ) {
							double val = farray.numberAt(i); //.get(i);
							context2d.lineTo( i*w/len, 240+val*200 );
						}
						//context2d.closePath();
						context2d.stroke();
						
						//asn.setBuffer( ab );
					}
				});
				acontext.startRendering();
				
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
				
				int w = Window.getClientWidth();
				int h = Window.getClientHeight();
				context2d.clearRect(0, 0, w, h);
				context2d.setFillStyle("#00ff00");
				context2d.fillRect(10.0, 10.0, 100.0, 100.0 );
		       ra = new RequestAnimationFrameCallback() {
		    	   int t = 0;
					@Override
					public boolean onRequestAnimationFrameCallback(double time) {
						if( ra != null ) {
							if( t++ % 10 == 0 ) {
								double w = canvas.getCoordinateSpaceWidth();
								double h = canvas.getCoordinateSpaceHeight();
								
								context2d.clearRect(0.0, 0.0, w, h);
								//rtan.getf
								//rtan.getByteFrequencyData( ua );
								//rtan.getByteTimeDomainData(ua);
								//stream.
								//rtan.getMinDecibels();
								//rtan.getFloatFrequencyData( fael );
								rtan.getByteTimeDomainData( uael );
								
								/*int		maxi = 0;
								float 	max = fa.get(maxi);
								for( int i = 0; i < fa.length()/2-1; i++ ) {
									float fval = fa.get(i);
									if( fval > max ) {
										max = fval;
										maxi = i;
									}
								}*/
								
								int max = 0;
								int total = ua.get(0);
								List<Integer> li = new ArrayList<Integer>();
								for( int i = 1; i < ua.length()-1; i++ ) {
									short valm1 = ua.get(i-1);
									short val = ua.get(i);
									short valp1 = ua.get(i+1);
									if( (val >= valm1 && val > valp1) || (val > valm1 && val >= valp1) ) {
										li.add( i );
										i++;
										
										if( val > max ) max = val;
										
										total += valp1;
									}
									total += val;
								}
								total += ua.get(ua.length()-1);
								total /= ua.length();
								
								max -= total;
								
								double mbil = medalBil( li, ua );
								if( li.size() > 1 ) {
									context2d.setFillStyle("#000000");
									
									boolean changed = true;
									int u = 0;
									while( changed && u < 32 ) {
										changed = false;
										
										int k = 0;
										int fi = li.get( k++ );
										List<Integer> nli = new ArrayList<Integer>();
										while( k < li.size() ) {
											int ne = li.get(k++);
											
											short val = ua.get(fi);
											short nval = ua.get(ne);
											if( ne-fi <= 2*mbil/3 || val < 8*nval/10 || nval < 8*val/10 || val - total < 0 ) {
												changed = true;
												//short val = ua.get(fi);
												
												if( val > nval ) {
													
												} else {
													fi = ne;
												}
											} else {
												nli.add( fi );
												fi = ne;
											}
										}
										li = nli;
										mbil = medalBil( li, ua );
										
										context2d.fillText( Double.toString( Math.round(mbil*10.0)/10.0 ), w/2.0+u*30.0, h/2.0 );
										
										u++;
									}
									
									//mbil = medalBil( li, ua );
									
									int val = 0;
									context2d.fillText( Double.toString( Math.round(mbil*10.0)/10.0 ), w/2.0, h/2.0+30.0 );
									context2d.fillText( u + "  " + li.size(), w/2.0, h/2.0-100 );
									//context2d.fillText( Float.toString( acontext.getSampleRate() ), w/2.0, h/3.0 );
									
									int len = ua.length();
									context2d.setStrokeStyle("#00aa00");
									context2d.beginPath();
									context2d.moveTo(0, 240);
									for( int i = 0; i < len; i++ ) {
										val = ua.get(i)-total;
										context2d.lineTo( i*w/len, 240+val );
									}
									//context2d.closePath();
									context2d.stroke();
									
									context2d.setStrokeStyle("#aa0000");
									context2d.beginPath();
									context2d.moveTo(0, 240);
									for( int k = 0; k < li.size(); k++ ) {
										int i = li.get(k);
										val = ua.get( i )-total;
										context2d.lineTo( i*w/len, 140 );
										context2d.lineTo( i*w/len, 140+val );
										context2d.lineTo( i*w/len, 140 );
									}
									//context2d.closePath();
									context2d.stroke();
								}
								
								context2d.setFillStyle("#000000");
								context2d.fillText( "blehbleh", w/2.0, h/2.0+20 );
							}
							
							wnd.webkitRequestAnimationFrame( ra );
							return true;
						}
						return false;
					}
				};
		        wnd.webkitRequestAnimationFrame( ra );
				
				return false;
			}
		});
	}
}
