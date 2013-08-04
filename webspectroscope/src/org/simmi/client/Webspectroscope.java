package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		VerticalPanel	vp = new VerticalPanel();
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		vp.setSize("100%", "100%");
		
		vp.add( new HTML("<h1>Web Spectroscope</h1>") );
		vp.add( new HTML("Monitors your environmental noise. Lines in the graph mean constant noise, f.x. the computer fan.<br>The lower the lines are, the more high pitched the sound is.") );
		vp.add( new HTML("<applet codebase=\"http://webspectroscope.appspot.com\" archive=\"spectroscope.jar\" code=\"org.simmi.FftApplet\" width=\"1024\" height=\"512\" jnlp_href=\"webspectroscpe.jnlp\"><param name=\"jnlp_href\" value=\"webspectroscope.jnlp\"/></applet>") );
		vp.add( new HTML("<a href=\"mailto:huldaeggerts@gmail.com\">huldaeggerts@gmail.com</a> | More apps: <a href=\"http://websimlab.appspot.com\">http://websimlab.appspot.com</a> | <a href=\"http://webconnectron.appspot.com\">http://webconnectron.appspot.com</a> | <a href=\"http://nutritiondb.appspot.com\">http://nutritiondb.appspot.com</a> | <a href=\"http://fasteignaverd.appspot.com\">http://fasteignaverd.appspot.com</a>") );
		
		RootPanel.get("webspect").add( vp );
	}
}
