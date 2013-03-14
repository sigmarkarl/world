package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;

import elemental.client.Browser;
import elemental.dom.LocalMediaStream;
import elemental.dom.RequestAnimationFrameCallback;
import elemental.html.AudioContext;
import elemental.html.AudioDestinationNode;
import elemental.html.AudioElement;
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
	
	public native Uint8Array ok( int bin ) /*-{
		return new Uint8Array( bin );
	}-*/;
	
	public native void anal( AudioContext acontext, LocalMediaStream stream, RealtimeAnalyserNode analyser, AudioDestinationNode adst ) /*-{
		try {
			var ss = acontext.createMediaStreamSource( stream );
			ss.connect( adst );
			//ss.connect( analyser );
			//analyser.connect( adst );
			
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
				
				final Uint8Array ua = ok( 1024 );
				
				final AudioContext acontext = wnd.newAudioContext();
				//final RealtimeAnalyserNode rtan = acontext.createAnalyser();
				//rtan.setFftSize( 1024 );
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
				
				anal( acontext, stream, null, acontext.getDestination() );
				//microphone.connect(analyser, 0,0);
				//rtan.connect(analyser, 0,0);
		        //rtan.connect(acontext.getDestination(), 0, 0);
				
		        ra = new RequestAnimationFrameCallback() {
					@Override
					public boolean onRequestAnimationFrameCallback(double time) {
						if( ra != null ) {
							//rtan.getByteFrequencyData( ua );
							//rtan.getByteTimeDomainData(ua);
							//stream.
							
							//wnd.getConsole().log(" m "+ua.numberAt( 128 ) );
							
							wnd.webkitRequestAnimationFrame( ra );
							return true;
						}
						return false;
					}
				};
				//JavaScriptAudioNode jsnode = acontext.createJavaScriptNode(2048);
		        wnd.webkitRequestAnimationFrame( ra );
				
				return false;
			}
		});
	}
}
