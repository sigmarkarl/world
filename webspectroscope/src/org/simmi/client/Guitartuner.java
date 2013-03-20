package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.typedarrays.client.Float32ArrayNative;

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
	
	RequestAnimationFrameCallback ra = null;
	
	@Override
	public void onModuleLoad() {
		final elemental.html.Window 		wnd = Browser.getWindow();
		Navigator 			nvg = wnd.getNavigator();
		
		nvg.webkitGetUserMedia( audio(), new NavigatorUserMediaSuccessCallback() {
			@Override
			public boolean onNavigatorUserMediaSuccessCallback(LocalMediaStream stream) {
				//AudioElement ael = (AudioElement)Browser.getDocument().querySelector("audio");
				//stuff( ael, stream );
				//MediaElementAudioSourceNode microphone = acontext.createMediaElementSource( ael );
				
				final AudioContext 				acontext = wnd.newAudioContext();
				final AudioSourceNode 			microphone = createMediaStreamSource(acontext, stream);
				final AudioDestinationNode		output = acontext.getDestination();
				
				final Float32ArrayNative		fa = Float32ArrayNative.create(2048);
				final Float32Array				fael = ok( fa );
				
				final RealtimeAnalyserNode 		rtan = acontext.createAnalyser();
				final JavaScriptAudioNode 		jsan = acontext.createJavaScriptNode(8192, 1);
				rtan.setFftSize( 2048 );
				
				final AudioBufferSourceNode 	buffer = acontext.createBufferSource();
				//asn.setLooping( true );
				//asn.connect(jsan, 0, 0);
				jsan.setOnaudioprocess( new EventListener() {
					@Override
					public void handleEvent(Event evt) {
						//wnd.getConsole().log("ok");
						AudioProcessingEvent	ape = (AudioProcessingEvent)evt;
						AudioBuffer				ab = ape.getInputBuffer();
						
						wnd.getConsole().log( ab.getChannelData(0).numberAt(0) + "  " + ab.getChannelData(0).numberAt(4000) );
						
						//asn.setBuffer( ab );
					}
				});
				microphone.connect(jsan, 0, 0);
				jsan.connect(output, 0, 0);
				
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
				
		       /* ra = new RequestAnimationFrameCallback() {
					@Override
					public boolean onRequestAnimationFrameCallback(double time) {
						if( ra != null ) {
							//rtan.getf
							//rtan.getByteFrequencyData( ua );
							//rtan.getByteTimeDomainData(ua);
							//stream.
							
							rtan.getFloatFrequencyData( fael );
							wnd.getConsole().log(" m "+fa.get(1023) );
							wnd.webkitRequestAnimationFrame( ra );
							return true;
						}
						return false;
					}
				};*/
		        //wnd.webkitRequestAnimationFrame( ra );
				
				return false;
			}
		});
	}
}
