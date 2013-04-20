package org.simmi.nutritiondroid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.simmi.nutritiondroid.util.SystemUiHider;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	public class NutData {
		String[] split;
		public NutData( String tdata ) {
			//data = tdata;
			split = tdata.split("\n");
		}
		
		@JavascriptInterface
		public String get( int i ) {
			return split[i];
		}
		
		@JavascriptInterface
		public int getLength() {
			return split.length;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_fullscreen);
		
		final WebView myWebView = (WebView) findViewById(R.id.webView1);
		//myWebView.setWebViewClient( new WebViewClient() );
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		//webSettings.setSavePassword( false );
		//webSettings.setSaveFormData( false );
		
		InputStream is = (InputStream)this.getResources().openRawResource(R.raw.nut_data_trim);
		try {
			GZIPInputStream gis = new GZIPInputStream( is );
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int r = gis.read(buffer);
			while( r > 0 ) {
				baos.write(buffer, 0, r );
				r = gis.read( buffer );
			}
			baos.close();
			
			NutData nutdata = new NutData( baos.toString() );
			myWebView.addJavascriptInterface( nutdata, "nutdata" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*Session.openActiveSession(this, true, new Session.StatusCallback() {
		    // callback when session changes state
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		    	if (session.isOpened()) {
		    		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
	    			  // callback after Graph API response with user object
	    			  @Override
	    			  public void onCompleted(GraphUser user, Response response) {
	    				  if (user != null) {
	    					  String imgurl = "https://graph.facebook.com/" + user.getId() + "/picture"; //user.getId();
	    					  myWebView.addJavascriptInterface(imgurl, "imgurl");
	    					  //TextView welcome = (TextView) findViewById(R.id.welcome);
	    					  //welcome.setText("Hello " + user.getName() + "!");
	    				  }
	    			  }
	    			});
		    	}
		    }
		});*/
		
		//myWebView.loadUrl("http://nutritiondb.appspot.com");
		myWebView.loadUrl("http://130.208.252.7:8888/");

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, myWebView, HIDER_FLAGS);
		mSystemUiHider.setup();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
