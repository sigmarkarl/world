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
			int ed = li.get( li.size()-1 );
			
			/*for( int i = 1; i < li.size(); i++ ) {
				int ne = li.get(i);
				val += ne-fi;
				fi = ne;
			}
			val /= li.size()-1;*/
			
			val = (ed - fi)/(li.size()-1);
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
		    	   double prevbil = 0.0;
		    	   int t = 0;
					@Override
					public boolean onRequestAnimationFrameCallback(double time) {
						if( ra != null ) {
							if( t++ % 1 == 0 ) {
								double w = canvas.getCoordinateSpaceWidth();
								double h = canvas.getCoordinateSpaceHeight();
								
								context2d.clearRect(0.0, 0.0, w, h);
								
								double w52 = w/52.0;
								context2d.setStrokeStyle("#000000");
								for( double d = 0.0; d < w; d += w52 ) {
									context2d.beginPath();
									context2d.moveTo( d, 100.0 );
									context2d.lineTo( d+w52, 100.0 );
									context2d.lineTo( d+w52, 0.0 );
									context2d.stroke();
								}
								
								context2d.setFillStyle("#000000");
								for( double d = 0.0; d < w; d += 7.0*w52 ) {
									context2d.fillRect(d+w52-w52/8.0, 0.0, w52/2.0, 60.0);
									if( d < w-3.0*w52 ) {
										context2d.fillRect(d+3.0*w52-3.0*w52/8.0, 0.0, w52/2.0, 60.0);
										context2d.fillRect(d+4.0*w52-w52/8.0, 0.0, w52/2.0, 60.0);
										context2d.fillRect(d+6.0*w52-3.0*w52/8.0, 0.0, w52/2.0, 60.0);
										context2d.fillRect(d+7.0*w52-w52/4.0, 0.0, w52/2.0, 60.0);
									}
								}
								
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
								double avg = 0;
								for( int i = 0; i < ua.length(); i++ ) {
									avg += ua.get(i);
								}
								avg /= ua.length();
								
								//int avg = ua.get(0);
								List<Integer> li;/* = new ArrayList<Integer>();
								for( int i = 1; i < ua.length()-1; i++ ) {
									short valm1 = ua.get(i-1);
									short val = ua.get(i);
									short valp1 = ua.get(i+1);
									if( (val >= valm1 && val > valp1) || (val > valm1 && val >= valp1) ) {
										li.add( i );
										i++;
										
										if( val > max ) max = val;
									}
								}*/
								
								//max -= total;
								
								double mbil = 0.0;
								int size = 0;
								
								List<Integer>	upperlongindexes = new ArrayList<Integer>();
								List<Integer>	lowerlongindexes = new ArrayList<Integer>();
								
								int maxlocount = 0;
								int maxhicount = 0;
								int locount = 0;
								int hicount = 0;
								for( int i = 0; i < ua.length(); i++ ) {
									if( ua.get(i) - avg < 0 ) {
										locount++;
										if( hicount > 0 ) {
											if( hicount > 6*maxhicount/5 ) {
												upperlongindexes.clear();
												maxhicount = hicount;
											}
											
											if( hicount > 3*maxhicount/5 ) {
												upperlongindexes.add( i );
											}
										}
										hicount = 0;
									} else {
										hicount++;
										if( locount > 0 ) {
											if( locount > 6*maxlocount/5 ) {
												lowerlongindexes.clear();
												maxlocount = locount;
											}
											
											if( locount > 3*maxlocount/5 ) {
												lowerlongindexes.add( i );
											}
										}
										locount = 0;
									}
								}
								
								double sumu = 0;
								for( int i = 0; i < upperlongindexes.size()-1; i++ ) {
									sumu += upperlongindexes.get(i+1) - upperlongindexes.get(i);
								}
								sumu /= upperlongindexes.size()-1;
								
								double sumuv = 0;
								for( int i = 0; i < upperlongindexes.size()-1; i++ ) {
									double val = (upperlongindexes.get(i+1) - upperlongindexes.get(i) - sumu);
									sumuv += val*val;
								}
								
								double uvar = Math.sqrt(sumuv)/(upperlongindexes.size()-1);
								
								double suml = 0;
								for( int i = 0; i < lowerlongindexes.size()-1; i++ ) {
									suml += lowerlongindexes.get(i+1) - lowerlongindexes.get(i);
								}
								suml /= lowerlongindexes.size()-1;
								
								double sumlv = 0;
								for( int i = 0; i < lowerlongindexes.size()-1; i++ ) {
									double val = (lowerlongindexes.get(i+1) - lowerlongindexes.get(i) - suml);
									sumlv += val*val;
								}
								
								double lvar = Math.sqrt(sumlv)/(lowerlongindexes.size()-1);
								
								if( uvar > lvar ) li = lowerlongindexes;
								else li = upperlongindexes;
								
								double var = Math.min( uvar, lvar );
								
								li = upperlongindexes;
								size = li.size();
								if( size > 1 ) mbil = (li.get(li.size()-1) - li.get(0)) / (li.size() - 1); 
								
								/*mbil = medalBil( li, ua );
								if( li.size() > 1 ) {
									context2d.setFillStyle("#000000");
									
									boolean changed = true;
									int u = 0;
									while( changed && li.size() > 0 && u < 32 ) {
										changed = false;
										
										int k = 0;
										int fi = li.get( k++ );
										List<Integer> nli = new ArrayList<Integer>();
										while( k < li.size() ) {
											int ne = li.get(k++);
											
											short val = ua.get(fi);
											short nval = ua.get(ne);
											
											int mval = (int)(val - avg);
											int mnval = (int)(nval - avg);
											if( ne-fi <= 7*mbil/8 /*|| mval < 8*mnval/10 || mnval < 8*mval/10 || mval < 0* ) {
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
										size = li.size();
										mbil = medalBil( li, ua );
										
										context2d.fillText( Double.toString( Math.round(mbil*10.0)/10.0 ), w/2.0+u*30.0, h/2.0 );
										
										u++;
									}
								}*/
									
								boolean something = true;
								if( something ) {
									float samplerate = acontext.getSampleRate();
									double hz = 0.0;
									//prevbil = mbil;
									if( mbil > 1.0 && size > 3 && var < 1.0 && var != Double.NaN && var > 0.0 ) {
										prevbil = mbil;
									}
									if( prevbil > 1.0 ) hz = Math.round((samplerate/prevbil)*10.0)/10.0;
									
									double twq = Math.pow( 2.0, 1.0/12.0 );
									double kc = Math.round( Math.log( hz/440.0 ) / Math.log( twq ) );
									
									double attund = Math.floor( kc/12.0 );
									double start = 28.0*w52+7.0*w52*attund;
									
									double kval = kc - attund*12.0;
									context2d.setFillStyle("#aa0000");
									if( kval == 0 ) context2d.fillRect(start, 60.0, w52, 40.0);
									else if( kval == 2 ) context2d.fillRect(start+w52, 60.0, w52, 40.0);
									else if( kval == 3 ) context2d.fillRect(start+2.0*w52, 60.0, w52, 40.0);
									else if( kval == 5 ) context2d.fillRect(start+3.0*w52, 60.0, w52, 40.0);
									else if( kval == 7 ) context2d.fillRect(start+4.0*w52, 60.0, w52, 40.0);
									else if( kval == 8 ) context2d.fillRect(start+5.0*w52, 60.0, w52, 40.0);
									else if( kval == 10 ) context2d.fillRect(start+6.0*w52, 60.0, w52, 40.0);
									else if( kval == 1 ) context2d.fillRect(start+w52-w52/8.0, 0.0, w52/2.0, 60.0);
									else if( kval == 4 ) context2d.fillRect(start+3.0*w52-3.0*w52/8.0, 0.0, w52/2.0, 60.0);
									else if( kval == 6 ) context2d.fillRect(start+4.0*w52-w52/8.0, 0.0, w52/2.0, 60.0);
									else if( kval == 9 ) context2d.fillRect(start+6.0*w52-3.0*w52/8.0, 0.0, w52/2.0, 60.0);
									else if( kval == 11 ) context2d.fillRect(start+7.0*w52-w52/4.0, 0.0, w52/2.0, 60.0);
									//mbil = medalBil( li, ua );
									
									int val = 0;
									if( prevbil > 1.0 ) {
										if( hz > 1.0 ) {
											String font = context2d.getFont();
											context2d.setFont( "italic 40pt Calibri" );
											String hzstr = Double.toString( hz )+"hz " + var;
											double strw = context2d.measureText(hzstr).getWidth();
											context2d.fillText( hzstr, (w-strw)/2.0, h/2.0+30.0 );
											context2d.setFont( font );
										}
									}
									context2d.fillText( Double.toString( Math.round(prevbil*10.0)/10.0 ), w/2.0, h/2.0+30.0 );
									//context2d.fillText( u + "  " + li.size(), w/2.0, h/2.0-100 );
									//context2d.fillText( Float.toString( acontext.getSampleRate() ), w/2.0, h/3.0 );
									
									int len = ua.length();
									context2d.setStrokeStyle("#00aa00");
									context2d.beginPath();
									context2d.moveTo(0, 240);
									for( int i = 0; i < len; i++ ) {
										val = (int)(ua.get(i)-avg);
										context2d.lineTo( i*w/len, 240+val );
									}
									//context2d.closePath();
									context2d.stroke();
									
									context2d.moveTo(0, 240);
									context2d.lineTo( w, 240 );
									context2d.stroke();
									
									context2d.setStrokeStyle("#aa0000");
									context2d.beginPath();
									context2d.moveTo(0, 140);
									for( int k = 0; k < li.size(); k++ ) {
										int i = li.get(k);
										//val = (int)(ua.get( i )-avg);
										context2d.lineTo( i*w/len, 140 );
										context2d.lineTo( i*w/len, 240/*+val*/ );
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
