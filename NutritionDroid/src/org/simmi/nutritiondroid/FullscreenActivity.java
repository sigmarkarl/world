package org.simmi.nutritiondroid;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.simmi.nutritiondroid.util.SystemUiHider;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.Fusiontables.Query;
import com.google.api.services.fusiontables.Fusiontables.Query.SqlGet;
import com.google.api.services.fusiontables.FusiontablesRequestInitializer;
import com.google.api.services.fusiontables.model.Sqlresponse;

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

	Set<Integer>	selset = new HashSet<Integer>();
	int sortcolumn = 0;
	public class FoodInfo implements Comparable<FoodInfo> {
		Object[]	columns;
		boolean		selected = false;
		String		id;
		
		public FoodInfo( String id, String name, String group ) {
			columns = new Object[ lcolumnwidth.size() ];
			columns[0] = name;
			columns[1] = group;
			this.id = id;
		}
		
		@JavascriptInterface
		public String getId() {
			return id;
		}
		
		@JavascriptInterface
		public void setSelected( boolean sel, int ind ) {
			selected = sel;
			if( sel ) selset.add( ind );
			else selset.remove( ind );
		}
		
		@JavascriptInterface
		public boolean isSelected() {
			return selected;
		}
		
		@JavascriptInterface
		public Object getSortObject() {
			return columns[sortcolumn];
		}
		
		@JavascriptInterface
		public String stringValAt( int i ) {
			if( i < columns.length ) {
				return (String)columns[i];
			}
			return null;
		}
		
		@JavascriptInterface
		public double doubleValAt( int i ) {
			if( i < columns.length ) {
				Object val = columns[i];
				return val == null ? -1.0 : (Double)columns[i];
			}
			return -1.0;
		}
		
		@JavascriptInterface
		public Object valAt( int i ) {
			if( i < columns.length ) {
				return columns[i];
			}
			return null;
		}
		
		@JavascriptInterface
		public int getLength() {
			return columns.length;
		}

		@Override
		public int compareTo(FoodInfo o) {
			Object obj = columns[ sortcolumn ];
			Object sobj = o == null ? null : o.getSortObject();
			
			if( obj != null && sobj != null ) {
				if( obj instanceof String ) {
					return ((String)obj).compareTo( (String)sobj );
				} else if( obj instanceof Double ) {
					return ((Double)sobj).compareTo( (Double)obj );
				}
			} else if( obj == null && sobj != null ) {
				return 1;
			} else if( obj != null && sobj == null ) {
				return -1;
			}
					
			return 0;
		}
	};
	
	public class Column {
		public Column( String name, String unit, int width, String id ) {
			this.name = name;
			this.unit = unit;
			this.width = width;
			this.id = id;
		}
		
		String 	name;
		String	unit;
		int		width;
		String 	id;
		
		@JavascriptInterface
		public int getWidth() {
			return width;
		}
		
		@JavascriptInterface
		public String getId() {
			return id;
		}
		
		@JavascriptInterface
		public String getName() {
			return name;
		}
		
		@JavascriptInterface
		public String getUnit() {
			return unit;
		}
	};
	
	Map<String,String>	groupIdMap = new HashMap<String,String>();
	Map<String,Column> nutrmap = new HashMap<String,Column>();
	Map<String,FoodInfo> foodmap = new HashMap<String,FoodInfo>();
	List<FoodInfo>	lfoodinfo = new ArrayList<FoodInfo>();
	List<Column>	lcolumnwidth = new ArrayList<Column>();
	Map<String,Integer>	nutrindex = new HashMap<String,Integer>();
	
	public class NutData {
		//String[] 	split;
		//String		data;
		public NutData( String text ) {
			int s = 0;
			int i = text.indexOf( '\n' );
			FoodInfo	fi = null;
			String		currentFood = null;
			while( i != -1 ) {
				/*int t1 = text.indexOf('^', s);
				String foodShort = text.substring(s+1, t1-1);
				int t2 = text.indexOf('^', t1+1);
				String nutrShort = text.substring( t1+2, t2-1);
				int t3 = text.indexOf('^', t2+1);
				double val = Double.parseDouble( text.substring(t2+1,t3) );*/
				
				String foodShort = text.substring(s, s+5);
				String nutrShort = text.substring(s+5, s+8);
				double val = Double.parseDouble( text.substring(s+8,i) );
				
				if( !foodShort.equals(currentFood) ) {
					currentFood = foodShort;
					fi = foodmap.get( foodShort );
				}
				
				int ind = nutrindex.get( nutrShort ); //lcolumnwidth.indexOf( nutrmap.get( nutrShort ) );
				fi.columns[ind] = val;
				
				s = i+1;
				i = text.indexOf( '\n', s );
				//groupIdMap.put( idstr.substring(1, idstr.length()-1), namestr.substring(1, namestr.length()-1 ) );
			}
		}
		
		@JavascriptInterface
		public int getColumnCount() {
			return lcolumnwidth.size();
		}
		
		@JavascriptInterface
		public Column getColumn( int i ) {
			return lcolumnwidth.get( i );
		}
		
		@JavascriptInterface
		public int getGroupCount() {
			return groupIdMap.size();
		}
		
		@JavascriptInterface
		public String getGroup( int i ) {
			int k = 0;
			for( String group : groupIdMap.keySet() ) {
				if( k == i ) return group;
				k++;
			}
			
			return null;
		}
		
		@JavascriptInterface
		public int getFoodInfoCount() {
			return lfoodinfo.size();
		}
		
		@JavascriptInterface
		public FoodInfo getFoodInfo( int i ) {
			return lfoodinfo.get(i);
		}
		
		/*@JavascriptInterface
		public int getLength() {
			return split.length;
		}*/
	}
	
	
	private static String apikey = "AIzaSyD5RTPW-0W9I9K2u70muKiq-rHXL2qhjzk";
	//private GoogleService service;
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static Fusiontables fusiontables;
	
	NutData nutdata = null;
	private class fetchNutrTask extends AsyncTask<Object,Integer,Long> {
		@Override
		protected Long doInBackground(Object... arg0) {
			try {
				fetchNutrFusion();
				
				InputStream is = (InputStream)FullscreenActivity.this.getResources().openRawResource(R.raw.nut_data_trim);
				GZIPInputStream gis = new GZIPInputStream( is );
				ByteArrayOutputStream	baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[16384];
				int r = gis.read(buffer);
				while( r > 0 ) {
					baos.write(buffer, 0, r );
					r = gis.read( buffer );
				}
				baos.close();
				
				nutdata = new NutData( baos.toString() );
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	};
	
	public void fetchNutrFusion() throws IOException {
		GoogleCredential credential = new GoogleCredential.Builder()
		  .setTransport( HTTP_TRANSPORT )
		  .setJsonFactory( JSON_FACTORY )
		  //.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
		  //.setServiceAccountScopes(FusiontablesScopes.FUSIONTABLES_READONLY);
		  //.setServiceAccountPrivateKeyFromP12File( file )
		  //.setServiceAccountPrivateKey( pk )
		  .build();
		FusiontablesRequestInitializer fri = new FusiontablesRequestInitializer(apikey);
		fusiontables = new Fusiontables.Builder( HTTP_TRANSPORT, JSON_FACTORY, credential ).setApplicationName("NutritionDroid").setFusiontablesRequestInitializer(fri).build();
		final Query query = fusiontables.query();
		
		String sql = "SELECT Id,Name FROM 1ysVkwxLAO7U4F-ULp58q4P5DqcD70V_MpiKuJ4U";
		SqlGet sqlget = query.sqlGet( sql );
		
		Sqlresponse 		sqlresp = sqlget.execute();
		List<List<Object>> 	rowObjs = sqlresp.getRows();
		
		System.err.println( "rows " + rowObjs.size() );
		if( rowObjs != null ) for( List<Object> row : rowObjs ) {
			String idstr = (String)row.get(0);
			String namestr = (String)row.get(1);

			groupIdMap.put( idstr.substring(1, idstr.length()-1), namestr.substring(1, namestr.length()-1 ) );
		}
		
		sql = "SELECT Id,Unit,Name FROM 129hFkqxrJnhaRTPDS1COEGs5d2q0dBasWg9gOJM";
		sqlget = query.sqlGet( sql );
		
		sqlresp = sqlget.execute();
		rowObjs = sqlresp.getRows();
		
		System.err.println( "rows " + rowObjs.size() );
		if( rowObjs != null ) for( List<Object> row : rowObjs ) {
			String idstr = (String)row.get(0);
			String unitstr = (String)row.get(1);
			String namestr = (String)row.get(2);
			//double val = Double.parseDouble( value.stringValue() );
			
			String idShort = idstr.substring(1, idstr.length()-1);
			String unitShort = unitstr.substring(1, unitstr.length()-1);
			String nameShort = namestr.substring(1, namestr.length()-1);
			
			Column col = new Column( nameShort, unitShort, 75, idShort);
			lcolumnwidth.add( col );
			nutrmap.put( idShort, col );
		}
		
		/*fusiontables = new Fusiontables.Builder( HTTP_TRANSPORT, JSON_FACTORY, credential ).setApplicationName("NutritionDroid").setFusiontablesRequestInitializer(fri).build();
		//Query query2 = fusiontables.query();*/
		String	sql2 = "SELECT Id,GroupId,Name FROM 11kJvZY3UCjtqcA0gN8WRZwtTVGXJ_MxAto7cdUU";
		SqlGet sqlget2 = query.sqlGet( sql2 );
		Sqlresponse sqlresp2 = sqlget2.execute();
		List<List<Object>> rowObjs2 = sqlresp2.getRows();
		
		System.err.println( "rows2 " + rowObjs2.size() );
		if( rowObjs2 != null ) for( List<Object> row : rowObjs2 ) {
			String idstr = (String)row.get(0);
			String groupidstr = (String)row.get(1);
			String namestr = (String)row.get(2);
			
			if( namestr.length() > 0 && groupidstr.length() > 0 ) {
				FoodInfo fi = new FoodInfo( idstr, namestr.substring(1, namestr.length()-1), groupIdMap.get( groupidstr.substring(1, groupidstr.length()-1) ) );
				foodmap.put( idstr.substring(1, idstr.length()-1), fi );
				lfoodinfo.add( fi );
			}
		}
	}
	
	WebView myWebView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_fullscreen);
		
		myWebView = (WebView) findViewById(R.id.webView1);
		//myWebView.setWebViewClient( new WebViewClient() );
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		//webSettings.setSavePassword( false );
		//webSettings.setSaveFormData( false );
		
		//new fetchNutrTask().execute();
		
		try {			
			InputStream is = (InputStream)FullscreenActivity.this.getResources().openRawResource(R.raw.fd_group);
			BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
			String line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\^");
				
				if( split.length >= 2 ) {
					String idstr = split[0];
					String namestr = split[1];
	
					groupIdMap.put( idstr.substring(1, idstr.length()-1), namestr.substring(1, namestr.length()-1 ) );
				}
				line = br.readLine();
			}
			br.close();
			
			lcolumnwidth.add( new Column("Food", null, 300, "0") );
			lcolumnwidth.add( new Column("Group", null, 180, "0") );
			
			int i = 0;
			nutrindex.put( "Food", i++ );
			nutrindex.put( "Group", i++ );
			
			is = (InputStream)FullscreenActivity.this.getResources().openRawResource(R.raw.nutr_def);
			br = new BufferedReader( new InputStreamReader( is ) );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\^");
				
				if( split.length >= 4 ) {
					String idstr = split[0];
					String unitstr = split[1];
					String namestr = split[3];
					
					String idShort = idstr.substring(1, idstr.length()-1);
					String unitShort = unitstr.substring(1, unitstr.length()-1);
					String nameShort = namestr.substring(1, namestr.length()-1);
					
					Column col = new Column( nameShort, unitShort, 75, idShort);
					lcolumnwidth.add( col );
					nutrmap.put( idShort, col );
					
					nutrindex.put( idShort, i++ );
				}
				line = br.readLine();
			}
			br.close();
			
			is = (InputStream)FullscreenActivity.this.getResources().openRawResource(R.raw.food_des);
			br = new BufferedReader( new InputStreamReader( is ) );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\\^");
				
				if( split.length >= 3 ) {
					String idstr = split[0];
					String groupidstr = split[1];
					String namestr = split[2];
					
					if( namestr.length() > 0 && groupidstr.length() > 0 ) {
						FoodInfo fi = new FoodInfo( idstr, namestr.substring(1, namestr.length()-1), groupIdMap.get( groupidstr.substring(1, groupidstr.length()-1) ) );
						foodmap.put( idstr.substring(1, idstr.length()-1), fi );
						lfoodinfo.add( fi );
					}
				}
				line = br.readLine();
			}
			br.close();
			
			is = (InputStream)FullscreenActivity.this.getResources().openRawResource(R.raw.nut_data_trim);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[16384];
			int r = is.read(buffer);
			while( r > 0 ) {
				baos.write(buffer, 0, r );
				r = is.read( buffer );
			}
			baos.close();
			
			nutdata = new NutData( baos.toString() );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		myWebView.addJavascriptInterface( nutdata, "nutdata" );
		myWebView.loadUrl("http://192.168.1.66:8893/");
		
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
		//myWebView.loadUrl("http://130.208.252.7:8888/");
		//myWebView.loadUrl("http://192.168.1.66:8888/");

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
