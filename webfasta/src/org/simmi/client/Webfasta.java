package org.simmi.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Webfasta implements EntryPoint {
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
    
	Canvas				canvas;
	Canvas				tcanvas;
	Canvas				ocanvas;
	
    Context2d 			context;
    Context2d			tcontext;
    Context2d 			ocontext;
    
    String				content = null;
	
	/*public void stuff() {
		canvas = Canvas.createIfSupported();
		
		if (canvas == null) {
            RootPanel.get().add(new Label("Sorry, your browser doesn't support the HTML5 Canvas element"));
            return;
		}
		
		canvas.setStyleName("mainCanvas");
		canvas.setWidth(canvasWidth + "px");
		canvas.setCoordinateSpaceWidth(500);
		
		canvas.setHeight(canvasHeight + "px");
		canvas.setCoordinateSpaceHeight(500);
		
		context = canvas.getContext2d();
		
		/*final Timer timer = new Timer() {
		    @Override
		    public void run() {
		        drawSomethingNew();
		    }
		};
		timer.scheduleRepeating(1500);*
		
		
		//context.fillText("hohohoho", 10, 10);
	}*/
	
	//String _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	 
	// public method for encoding
	public native String encode( String input ) /*-{
		var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = this.@org.simmi.client.Webfasta::_utf8_encode(Ljava/lang/String;)(input);
 
		while (i < input.length) {
 
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);
 
			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;
 
			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}
 
			output = output +
			_keyStr.charAt(enc1) + _keyStr.charAt(enc2) +
			_keyStr.charAt(enc3) + _keyStr.charAt(enc4);
 
		}
 
		return output;
	}-*/;
 
	public native String decode( String input ) /*-{
		var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
 
		while (i < input.length) {
 
			enc1 = _keyStr.indexOf(input.charAt(i++));
			enc2 = _keyStr.indexOf(input.charAt(i++));
			enc3 = _keyStr.indexOf(input.charAt(i++));
			enc4 = _keyStr.indexOf(input.charAt(i++));
 
			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;
 
			output = output + String.fromCharCode(chr1);
 
			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}
 
		}
 
		output = this.@org.simmi.client.Webfasta::_utf8_decode(Ljava/lang/String;)(output);
 
		return output;
	}-*/;
 
	private native String _utf8_encode( String string ) /*-{
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";
 
		for (var n = 0; n < string.length; n++) {
			var c = string.charCodeAt(n);
 
			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
 
		}
 
		return utftext;
	}-*/;
 
	private native String _utf8_decode( String utftext ) /*-{
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;
 
		while ( i < utftext.length ) {
 
			c = utftext.charCodeAt(i);
 
			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			}
			else if((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i+1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else {
				c2 = utftext.charCodeAt(i+1);
				c3 = utftext.charCodeAt(i+2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}
 
		}
 
		return string;
	}-*/;
	
	public native void scrollEv( JavaScriptObject te ) /*-{
		//$wnd.alert('hook');
		//for(var key in te){
      	//	if( typeof te[key] == 'function' ) console.log(key);
		//}
		
		//var oldfun = te.onscroll;
		var hthis = this;
		te.onscroll = function() {
			//oldfun();
			hthis.@org.simmi.client.Webfasta::draw(II)( 0, 0 );
		};
		
	}-*/;
	
	public native JavaScriptObject handleFiles( Element ie, int append ) /*-{
		return ie.files[0];
		
//		var hthis = this;
//		file = ie.files[0];
//		var reader = new FileReader();
//		reader.onload = function(e) {
//			hthis.@org.simmi.client.Webfasta::fileLoaded(Ljava/lang/String;I)(e.target.result, append);
//		};
//		reader.onerror = function(evt) {
//      		$wnd.console.log("error", evt);
//      		switch(evt.target.error.code) {
//		      case evt.target.error.NOT_FOUND_ERR:
//		        alert('File Not Found!');
//		        break;
//		      case evt.target.error.NOT_READABLE_ERR:
//		        alert('File is not readable');
//		        break;
//		      case evt.target.error.ABORT_ERR:
//		        alert('erm');
//		        break; // noop
//		      default:
//		        alert('An error occurred reading this file.');
//		    };
//      		//$wnd.console.log(e.getMessage());
//    	};
//    	$wnd.console.log('befreadastext');
//		reader.readAsText( file, "utf8" );
//		$wnd.console.log('afterreadastext');
	}-*/;
	
	public native void console( String str ) /*-{
		$wnd.console.log( str );
	}-*/;
	
	class Annotation {
		public Annotation( String name, String color, int start, int stop ) {
			this.name = name;
			this.color = color;
			this.start = start;
			this.stop = stop;
		}
		
		String	name;
		String	color;
		int		start;
		int		stop;
	};
	
	static int sortcol = 0;
	class Sequence implements Comparable<Sequence> {
		int namestart;
		int nameend;
		int seqstart;
		int seqstop;
		
		public Sequence( int nstart, int nend, int sstart, int sstop ) {
			this.namestart = nstart;
			this.nameend = nend;
			this.seqstart = sstart;
			this.seqstop = sstop;
		}
		
		public String toString() {
			return Webfasta.this.content.substring( namestart, nameend );
		}
		
		/*public Sequence( String name, String seq ) {
			this.name = name;
			this.seq = seq;
			this.aInd = new int[seq.length()];
			this.aList = new ArrayList<Annotation>();
		}*/
		
		public void addAnnotation( Annotation a ) {
			/*aList.add( a );
			for( int i = a.start; i < a.stop; i++ ) {
				if( aInd[i] == 0 ) aInd[i] = aList.size();
				else aInd[i] = aList.size()<<16;
			}*/
		}
		
		public int[] getAnnotationIndex() {
			return null;
		}
		
		public List<Annotation> getAnnotations() {
			return null;
		}
		
		public String charAt( int i ) {
			return content.charAt( seqstart+i )+""; //char2String.get( content.charAt( seqstart+i ) );
		}
		
		public int length() {
			return seqstop-seqstart;
			//return seq.length();
		}

		@Override
		public int compareTo(Sequence o) {
			return sortcol == 0 ? toString().compareTo(o.toString()) : length()-o.length();
		}
		
		/*String				name;
		String				seq;
		int[]				aInd;
		List<Annotation>	aList;*/
	};
	
	public native int indexOf( String jso, char val, int start ) /*-{
		return jso.indexOf( val, start );
	}-*/;
	
	public void fileLoad( String cont, int max ) {
		prevx = Integer.MAX_VALUE;
		prevy = Integer.MAX_VALUE;
		
		this.content = cont;
		this.max = max;
		
		//val = val.subList(0, 1000);
		
		initTable( 0 );
		draw( 0, 0 );
	}
	
	public void initTable( int append ) {
		/*if( append == 0 ) {
			data = DataTable.create();
	    	data.addColumn(ColumnType.STRING, "Name");
	    	data.addColumn(ColumnType.NUMBER, "Length");
		}
		int start = data.getNumberOfRows();
		
		int count = 0;
		for( Sequence seq : val ) {
			data.addRow();
			data.setValue( count+start, 0, seq.toString() );
			data.setValue( count+start, 1, seq.length() );
			
			count++;
		}
		
		table.draw(data, createTableOptions());
		draw();
		
		Element e = table.getElement();
        com.google.gwt.dom.client.Element de = e.getFirstChildElement();
        de = de.getFirstChildElement();
        scrollEv( de );*/
	}
	
	List<Sequence> 			val = new ArrayList<Sequence>();
	Map<String,Sequence>	seqmap = new HashMap<String,Sequence>();
	int						max = 0;
	public void fileLoaded( String cont, int append ) {
		console( "er "+cont.length() );
		
		this.content = cont;//.replace("\n", "");
		seqmap.clear();
		max = 0;
				
		if( append == 0 ) {
		//	data = DataTable.create();
	    //	data.addColumn(ColumnType.STRING, "Name");
	    //	data.addColumn(ColumnType.NUMBER, "Length");
		}
		int start = 0;//data.getNumberOfRows();
		//data.addRows( split.length-1 );
		int count = 0;
		val = new ArrayList<Sequence>();// split.length-1 );
		
		//content.indexOf(ch)
		int r = content.indexOf(">"); //indexOf( content, '>', 0);
		//String seqname = "mu";//content.substring(r+1, i);
		//int k = content.indexOf('>', i);
		while( r != -1 ) {
			int i = content.indexOf("\n", r+1); //indexOf( content, '\n', r+1);
			//String seqname = content.substring(r+1, i);
			int k = content.indexOf(">", i+1); //indexOf( content, '>', i+1);
			//for( int r = 0; r < split.length-1; r++ ) {
				//String s = split[r+1];
			
			//content.su
			/*if( seqmap.containsKey( seqname ) ) {
				String subseq = content.substring(i+1, r-1);
				Sequence seq = seqmap.get(seqname);
				String[] subsplit = subseq.split("\n");
				for( String a : subsplit ) {
					String[] aspl = a.split("\t");
					seq.addAnnotation( new Annotation(aspl[0], aspl[1], Integer.parseInt(aspl[2]), Integer.parseInt(aspl[3])) );
				}
			} else {*/
				//String seqstr = subseq;//.replace("\n", "");
				
			int n = k == -1 ? content.length() : k-1;
			int m = 0;
			/*for( int u = i+1; u < n; u++ ) {
				if( content.charAt(u) == '\n' ) {
					m++;
				} else {
					//content.setCharAt(u-m, content.charAt(u));
				}
			}*/
			
			Sequence seq = new Sequence( r+1, i, i+1, n-m ); //new Sequence(seqname,seqstr);
			int seqlen = n-i-1;
			//int seqlen = seqstr.length();
			//seqmap.put( seqname, seq );
			val.add( seq );
			if( seqlen > max ) max = seqlen;
			//data.addRow();
			//data.setValue( count+start, 0, seqname );
			//data.setValue( count+start, 1, seqlen );
			count++;
			
			r = k;
		}
		
		//table.setWidth("200px");
		//table.setHeight("200px");
		//table.draw(data, createTableOptions());
		draw( xstart, ystart );
		
		/*Element e = table.getElement();
        com.google.gwt.dom.client.Element de = e.getFirstChildElement();
        de = de.getFirstChildElement();
        scrollEv( de );*/
	}
	
	public Node nodeRecursive( Node n ) {
		NodeList<Node> nlf = n.getChildNodes();
		for( int i = 0; i < nlf.getLength(); i++ ) {
			Node nn = nlf.getItem(i);
			if( nn.getNodeName().toLowerCase().contains("table") ) return n;
		}
		return null;
	}
	
	/*public void odraw() {
		if( val != null ) {
			ocontext.setFillStyle("#CCFFCC");
			//ocontext.fillRect(20, 20, 100, 100);
			
			//ocontext.setStrokeStyle("#CCFFCC");
			//ocontext.setStrokeStyle(FillStrokeStyle.TYPE_CSSCOLOR);
			//ocontext.setLineWidth(2.0);
			for( int i = 0; i < val.length; i++ ) {		
				int y = sortind != null ? sortind.get(i) : i;
				String seq = val[y];
				//ocontext.moveTo(0, i);
				int x = seq.length()*ocontext.getCanvas().getClientWidth()/max;
				//ocontext.lineTo( x, i );
				ocontext.fillRect(0, i, x, 1);
			}
		}
	}*/
	
	int prevx = 0;
	int prevy = 0;
	int xstart = 0;
	int ystart = 0;
	int baseheight = 16;
	int basewidth = 10;
	public void draw( int xstartLocal, int ystartLocal ) {
		if( val != null ) {			
			context.setFillStyle("#222222");
			
			int cw = canvas.getCoordinateSpaceWidth();
			int ch = canvas.getCoordinateSpaceHeight()-baseheight;
			
			int tcw = tcanvas.getCoordinateSpaceWidth();
			//int tch = tcanvas.getCoordinateSpaceHeight();
			
			int ax = Math.abs(xstartLocal-prevx);
			int ay = Math.abs(ystartLocal-prevy);
			
			if( ay < ch/2 && ax < cw/2 ) {
				int h = ch-ay;
				int w = cw-ax;
				int xuno = Math.max(0,xstartLocal-prevx);
				int xduo = Math.max(0,prevx-xstartLocal);
				int yuno = Math.max(0,ystartLocal-prevy);
				int yduo = Math.max(0,prevy-ystartLocal);
				context.drawImage(context.getCanvas(), xuno, yuno+baseheight, w, h, xduo, yduo+baseheight, w, h);
				/*tcontext.drawImage(tcontext.getCanvas(), 0, yuno+baseheight, tcw, h, 0, yduo+baseheight, tcw, h);
				if( xuno > xduo ) {
					if( yuno > yduo ) {
						drawSection( xstartLocal, ystartLocal, 0, h, w, ay );
						drawSection( xstartLocal, ystartLocal, w, 0, ax, h );
						drawSection( xstartLocal, ystartLocal, w, h, ax, ay ); //, "#0000ff" );
						drawTable( ystartLocal, h, ay );
					} else {
						drawSection( xstartLocal, ystartLocal, 0, 0, w, ay );
						drawSection( xstartLocal, ystartLocal, w, ay, ax, h );
						drawSection( xstartLocal, ystartLocal, w, 0, ax, ay );
						drawTable( ystartLocal, 0, ay );
					}
				} else {
					if( yuno > yduo ) {
						drawSection( xstartLocal, ystartLocal, 0, 0, ax, h );
						drawSection( xstartLocal, ystartLocal, ax, h, w, ay );
						drawSection( xstartLocal, ystartLocal, 0, h, ax, ay );
						drawTable( ystartLocal, h, ay );
					} else {
						drawSection( xstartLocal, ystartLocal, ax, 0, w, ay );
						drawSection( xstartLocal, ystartLocal, 0, ay, ax, h );
						drawSection( xstartLocal, ystartLocal, 0, 0, ax, ay );
						drawTable( ystartLocal, 0, ay );
					}
				}*/
			} else {
				drawSection( xstartLocal, ystartLocal, 0, 0, cw, ch );
				drawTable( ystartLocal, 0, ch );
			}
			
			context.setFillStyle("#EEEEEE");
			context.fillRect(0, 0, cw, 20.0);
			context.setFillStyle("#111111");
			for( int x = xstartLocal; x < xstartLocal+cw; x+=basewidth ) {
				int val = 3;
				if( (x/basewidth)%basewidth == 0 ) val = 7;
				else if( (x/10)%10 == 5 ) val = 5;
				context.fillRect( (x-xstartLocal), baseheight-val, 1, val );
			}
			for( int x = xstartLocal; x < xstartLocal+cw; x+=100 ) {
				context.fillText( ""+(x/basewidth), (x-xstartLocal), baseheight-7 );
			}
			
			if( max > 0 ) {
				/*ocontext.clearRect(0, 0, ocanvas.getWidth(), ocanvas.getHeight());
				ocontext.setFillStyle("#CCFFCC");
				//ocontext.fillRect(20, 20, 100, 100);
				
				//ocontext.setStrokeStyle("#CCFFCC");
				//ocontext.setStrokeStyle(FillStrokeStyle.TYPE_CSSCOLOR);
				//ocontext.setLineWidth(2.0);
				
				int h = ocanvas.getHeight();
				int seqh = ((double)h/(double)val.size() <= 1.0) ? 1 : 2;
				double stph = Math.min( h, 2.0*val.size() );
				for( int i = 0; i < val.size(); i++ ) {		
					int y = sortind != null ? sortind.get(i) : i;
					Sequence seq = val.get(y);
					//String seqstr = seq.seq;
					//ocontext.moveTo(0, i);
					int x = seq.length()*ocanvas.getWidth()/max;
					//ocontext.lineTo( x, i );
					ocontext.fillRect(0, i*stph/(double)val.size(), x, seqh);
				}
				
				for( int i = 0; i < val.size(); i++ ) {		
					int y = sortind != null ? sortind.get(i) : i;
					Sequence seq = val.get(y);
					List<Annotation> lann = seq.getAnnotations();
					if( lann != null ) {
						for( Annotation ann : seq.getAnnotations() ) {
							ocontext.setFillStyle( ann.color );
							//int x = seq.length()*ocanvas.getWidth()/max;
							
							long start = (long)ann.start*ocanvas.getWidth()/(long)max;
							long stop = Math.max( start+1, (long)ann.stop*ocanvas.getWidth()/(long)max );
							ocontext.fillRect( (int)start, i*stph/(double)val.size(), (int)(stop-start), seqh);
						}
					}
				}
				
				ocontext.setFillStyle("#333333");
				int w = ( canvas.getWidth()*ocanvas.getWidth() ) / (max*10);
				ocontext.fillRect(xstart*ocanvas.getWidth()/(max), 0, w, 20);*/
			}
			
			prevx = xstartLocal;
			prevy = ystartLocal;
		}
	}
	
	public void drawTable( int ystartLocal, int yloc, int canvasHeight ) {
		int tcw = tcanvas.getCoordinateSpaceWidth();
		//int tch = tcanvas.getCoordinateSpaceHeight();
		
		int ys = Math.max( 0, ((ystartLocal+yloc)/baseheight)*baseheight );
		int ye = Math.min( ((ystartLocal+yloc+canvasHeight)/baseheight+1)*baseheight, val.size()*baseheight );
		
		//int ymax = Math.min( ystartLocal+tch, val.size()*baseheight );
		
		tcontext.setFillStyle("#ffffff");
		tcontext.fillRect(0, ys-ystartLocal+baseheight, tcw, ye-ys);
		tcontext.setFillStyle("#111111");
		for( int y = ys; y < ye; y+=baseheight ) {
			int i = y/baseheight;
			int yy = i*baseheight;
			Sequence seq = val.get(i);
			tcontext.fillText( seq.toString(), 3.0, yy+2.0*baseheight-3.0-ystartLocal );
		}
		
		tcontext.setFillStyle("#ffffff");
		tcontext.fillRect(tcw-45, ys-ystartLocal+baseheight, 45, ye-ys);
		tcontext.setFillStyle("#111111");
		for( int y = ys; y < ye; y+=baseheight ) {
			int i = y/baseheight;
			int yy = i*baseheight;
			Sequence seq = val.get(i);
			tcontext.fillText( seq.length()+"", tcw-40.0, yy+2.0*baseheight-3.0-ystartLocal );
		}
		
		tcontext.setFillStyle("#EEEEEE");
		tcontext.fillRect(0.0, 0.0, tcw, 20.0);
		tcontext.setFillStyle("#222222");
		tcontext.fillText("Name", 3.0, 20.0-5.0);
		tcontext.fillText("Length", tcw-40, 20.0-5.0);
	}
	
	public void drawSection( int xstartLocal, int ystartLocal, int xloc, int yloc, int canvasWidth, int canvasHeight ) {
		drawSection( xstartLocal, ystartLocal, xloc, yloc, canvasWidth, canvasHeight, "#eeeeee" );
	}
	
	public void drawSection( int xstartLocal, int ystartLocal, int xloc, int yloc, int canvasWidth, int canvasHeight, String strcolor ) {
		Set<Integer>	selset = new HashSet<Integer>();
		
		context.setFillStyle(strcolor);
		
		//console( xstartLocal + "  " + ystartLocal );
		//console( xstart + " - " + ystart );
		//context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		//for( int y = 0; y < Math.min( val.length, canvas.getHeight()/10 ); y++ ) {
		
		int xs = Math.max( 0, ((xstartLocal+xloc)/basewidth)*basewidth );
		int ys = Math.max( 0, ((ystartLocal+yloc)/baseheight)*baseheight );
		int xe = ((xstartLocal+xloc+canvasWidth)/basewidth+1)*basewidth;
		int ye = Math.min( ((ystartLocal+yloc+canvasHeight)/baseheight+1)*baseheight, val.size()*baseheight );
		
		context.fillRect( xs-xstartLocal, ys-ystartLocal+baseheight, xe-xs, ye-ys );
		
		context.setFillStyle("#222222");
		for( int y = ys; y < ye; y+=baseheight ) {
			int i = y/baseheight;
			int yy = i*baseheight;
			Sequence seq = val.get(i);
			//int[]	ann = seq.getAnnotationIndex();
				
			if( selset.contains(i) ) {
				context.setFillStyle("#DDDDFF");
				context.fillRect(0, ystartLocal, canvasWidth, baseheight );
				context.setFillStyle("#222222");
			}
			
			for( int x = xs; x < Math.min( seq.length()*basewidth, xe ); x+=basewidth ) {
				int k = x/basewidth;
				int xx = k*basewidth;
				context.fillText(seq.charAt(k), (xx-xstartLocal), yy+2.0*baseheight-3.0-ystartLocal );
				/*if( ann != null && ann[x] != 0 ) {
					Annotation a = seq.getAnnotations().get(ann[x]-1);
					context.setFillStyle( a.color );
					context.fillRect( (x-xstart)*10, th-top+baseheight, 10, 5 );
					context.setFillStyle("#222222");
				}*/
			}
		}
	}
	
	public native JsArrayInteger getSortInfo( JavaScriptObject t ) /*-{		
		var si = t.getSortInfo();
		return si.sortedIndexes;
	}-*/;
	
	public native void click( JavaScriptObject e ) /*-{
		e.click();
	}-*/;
	
	public void stuff( final int append ) {
		final DialogBox	db = new DialogBox();
		db.setText("Open file ...");
		
		//HorizontalPanel	vp = new HorizontalPanel();
		final FormPanel	form = new FormPanel();
		form.setAction("/");
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);
	    
		//form.add( vp );*/
		final FileUpload	file = new FileUpload();
		//file.
		//file.getElement().setAttribute("accept", "");
		file.getElement().setId("fasta");

		form.add( file );
		db.add( form );
		/*file.fireEvent( GwtEvent)
		
		//SubmitButton	submit = new SubmitButton();	
		//submit.setText("Open");*/
		
		file.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				/*try {
					console("ss");
					Desktop dt = Factory.getInstance().createDesktop();
					console("before");
					dt.openFiles(new OpenFilesHandler() {
						public void onOpenFiles(OpenFilesEvent oevent) {
							console("erm");
							
							File[] files = oevent.getFiles();
							File file = files[0];
							
							console("toff");
							Blob data = file.getBlob();

							console( "" + data.getLength() );
							
							byte[] bb = data.getBytes();
							console("toff");
							//Webfasta.this.content = new String( bb );
							fileLoaded( new String(bb), 0 );
						}
					}, true);
					console("after");
				} catch (Exception ex){
					Window.alert(ex.toString());
				}*/
			}
		});
		
		db.setAutoHideEnabled( true );
		db.center();
		//file.fireEvent( new ClickEvent(){} );
		click( file.getElement() );
	}
	
	public native void transferData( JavaScriptObject dataTransfer ) /*-{
		var s = this;
		var files = dataTransfer.files;
		var count = files.length;
		
		if(count > 0) {
			var file = files[0];
			var reader = new FileReader();
			reader.onload = function(e) {
				var res = e.target.result;
				var view = new Uint8Array( res );
				
				var r = 0;
				//res.indexOf( '>' );
				while( r < view.length && view[r] != '>' ) {
					r++;
					
					if( r % 10000 == 0 ) $wnd.console.log( r );
				}
				
				if( r == view.length ) r = -1;
				$wnd.console.log( view.length );
				
				var count = 0;
				var max = 0;
				//String seqname = "mu";//content.substring(r+1, i);
				//int k = content.indexOf('>', i);
				while( r != -1 ) {
					var i = r+1;
					//res.indexOf( '\n', r+1);
					while( i < view.length && view[i] != '\n' ) i++;
					
					//String seqname = content.substring(r+1, i);
					var k = i+1;
					//res.indexOf( '>', i+1);

					while( k < view.length && view[k] != '>' ) k++;
					if( k == view.length ) k = -1;
					
					//for( int r = 0; r < split.length-1; r++ ) {
					//String s = split[r+1];
						
					var n = k == -1 ? view.length : k-1;
					//Sequence seq = new Sequence( r+1, i, i+1, n ); //new Sequence(seqname,seqstr);
					var seqlen = n-i-1;
					
					m = 0;
					for( u = i+1; u < n; u++ ) {
						//if( res[u] == '\n' ) {
						//	m++;
						//} else {
						//	res[u-m] = res[u];
						//}
						view[u] = 'O';
					}
					
					//s.@org.simmi.client.Webfasta::addSequence(IIII)( r+1, i, i+1, n-m );
					if( seqlen > max ) max = seqlen;
					count += 1;
					
					r = k;
				}
				
				$wnd.console.log('er2');

				var blobBuilder = new BlobBuilder();
				blobBuilder.append( res );
				reader = new FileReader();
				reader.onload = function(e) {
					var res2 = e.target.result;
					s.@org.simmi.client.Webfasta::fileLoaded(Ljava/lang/String;I)( res2, 0 );
				}
				reader.readAsArrayBuffer( blobBuilder.getBlob() );
				//s.@org.simmi.client.Webfasta::fileLoad(Ljava/lang/String;I)( view, max );
			};
			reader.readAsArrayBuffer( file );
			//reader.readAsText( file );
		} else {
			var res = evt.dataTransfer.getData("Text");
			//$wnd.alert(evt.dataTransfer.effectAllowed);
			s.@org.simmi.client.Webfasta::fileLoaded(Ljava/lang/String;I)( res, 0 );
		}
	}-*/;
	
	/*public native void dropTarget( JavaScriptObject canvas ) /*-{
		var s = this;
		//jso.ondrop = function() {
		//	$wnd.alert('alert');
		//	s.@org.simmi.client.Pifviewer::slubb()();
		//};
		
		function f1( evt ) {
			evt.stopPropagation();
			evt.preventDefault();
			
			//$wnd.alert("hey");
		};
		
		function f( evt ) {
			evt.stopPropagation();
			evt.preventDefault();
	
			var files = evt.dataTransfer.files;		
			var count = files.length;
		
			if(count > 0) {
				var file = files[0];
				var reader = new FileReader();
				reader.onload = function(e) {
					//var iv;
					//try {
					//	var res = e.target.result;
					//	iv = new Int16Array( res );
					//} catch( e ) {
					//	$wnd.alert(e);
					//}
					var res = e.target.result;
					//s.@org.simmi.client.Pifviewer::loadImage(Lcom/google/gwt/dom/client/CanvasElement;Lcom/google/gwt/core/client/JavaScriptObject;)( canvas, iv );
					
					
					var r = res.indexOf( '>' );
					var count = 0;
					var max = 0;
					//String seqname = "mu";//content.substring(r+1, i);
					//int k = content.indexOf('>', i);
					while( r != -1 ) {
						var i = res.indexOf( '\n', r+1);
						//String seqname = content.substring(r+1, i);
						var k = res.indexOf( '>', i+1);
						//for( int r = 0; r < split.length-1; r++ ) {
						//String s = split[r+1];
							
						var n = k == -1 ? res.length : k-1;
						//Sequence seq = new Sequence( r+1, i, i+1, n ); //new Sequence(seqname,seqstr);
						var seqlen = n-i-1;
						s.@org.simmi.client.Webfasta::addSequence(IIII)( r+1, i, i+1, n );
						if( seqlen > max ) max = seqlen;
						count += 1;
						
						r = k;
					}
					
					s.@org.simmi.client.Webfasta::fileLoad(Ljava/lang/String;I)( res, max );
					//s.@org.simmi.client.Webfasta::fileLoaded(Ljava/lang/String;I)( res, 0 );
				};
				//reader.readAsArrayBuffer( file );			
				reader.readAsText( file );
			} else {
				var res = evt.dataTransfer.getData("Text");
				//$wnd.alert(evt.dataTransfer.effectAllowed);
				s.@org.simmi.client.Webfasta::fileLoaded(Ljava/lang/String;I)( res, 0 );
			}
		};
		canvas.addEventListener("dragenter", f1, false );
		canvas.addEventListener("dragexit", f1, false );
		canvas.addEventListener("dragover", f1, false );
		canvas.addEventListener("drop", f, false );
	}-*/;
	
	public void addSequence( int nstart, int nstop, int sstart, int sstop ) {
		val.add( new Sequence( nstart, nstop, sstart, sstop ) );
	}
	
	public native int bb( JavaScriptObject jo, int ind ) /*-{
		return jo.charCodeAt(ind);
	}-*/;

	JsArrayInteger			sortind = null;
	boolean					mousedown = false;
	int						mousex;
	int						mousey;
	public void onModuleLoad() {		
		//HorizontalPanel	hpanel = new HorizontalPanel();
		//hpanel.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		//hpanel.setWidth("100%");
		int height = Math.max( 1000, Window.getClientHeight() );
		//int height = RootPanel.get().getOffsetHeight();
		//hpanel.setHeight(Math.max(1000, height)+"px");
		
		MenuBar	popup = new MenuBar(true);
		popup.addItem("Open", new Command() {
			@Override
			public void execute() {
				/*try {
					Factory f = Factory.getInstance();
					if( f != null ) {
						console("11");
						Desktop dt = f.createDesktop();
						console("before");
						dt.openFiles(new OpenFilesHandler() {
							public void onOpenFiles(OpenFilesEvent oevent) {
								console("erm");
								
								File[] files = oevent.getFiles();
								File file = files[0];
								
								console("toff");
								Blob data = file.getBlob();
		
								console( "" + data.getLength() );
								
								byte[] bb = data.getBytes();
								console("toff");
								//Webfasta.this.content = new String( bb );
								fileLoaded( new String(bb), 0 );
							}
						}, true);
						console("after");
					}
				} catch (Exception ex){
					Window.alert(ex.toString());
				}*/
				
				//stuff( 0 );
				
				//db.hide();
				
				/*submit.addClickHandler( new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						form.submit();
						db.hide();
					}
				});*/
				/*form.addSubmitHandler( new SubmitHandler() {
					@Override
					public void onSubmit(SubmitEvent event) {
						//File f;
						try {
							tryOut( file.getElement() );
							//table = new Table( data, createTableOptions() );
						    //table.setWidth("100%");
						    //table.setHeight("100%");
						    
						    //slp.add
							
							//String str = new String( bb );
							//System.err.println( str );
							//GWT.log( filename );
						} catch( Exception e ) {
							GWT.log( "ex", e );
							//e.printStackTrace();
						}
					}
				});*/
				/*form.addSubmitCompleteHandler( new SubmitCompleteHandler() {
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						Window.alert("erm");
					}
				});*/
				
				//vp.add( file );
				//vp.add( submit );
				//db.add( form );
				//db.setAutoHideEnabled( true );
				//db.center();
			}
		});
		popup.addItem( "Append", new Command() {
			@Override
			public void execute() {
				stuff( 1 );
			}
		});
		popup.addItem( "Export", new Command() {
			@Override
			public void execute() {
				//DialogBox db = new DialogBox();
				//Anchor	a = new Anchor("okok");
				String out = "";
				/*for( int i = 0; i < data.getNumberOfRows(); i++ ) {
					String str = data.getValueString(i, 0);
					out += ">" + str + "\n";
					
					Sequence seq = val.get(i);
					//String seqstr = seq.seq;
					for( int k = seq.seqstart; k < seq.seqstop; k+=60 ) {
						out += content.substring( k, Math.min(seq.length(), k+60) ) + "\n";
					}
				}*/
				String bstr = encode( out );
				String dataurl = "data:text/plain;fileName=export.fa;base64,"+bstr;
				//a.setHref(  );
				//db.add( a );
				//db.center();
				Window.open(dataurl, "export.fa", "");
			}
		});
		MenuBar	epopup = new MenuBar(true);
		epopup.addItem("Select All", new Command() {
			@Override
			public void execute() {
				//JsArray<Selection> jsel = (JsArray<Selection>)JsArray.createArray();
				//for( int i = 0; i < data.getNumberOfRows(); i++ ) jsel.push( Selection.createRowSelection(i) );
				//JsArray<Selection>
				/*JsArray<Selection>	all = new JsArray<Selection>() {
					
				};*/
				//table.setSelections( jsel );
				draw( xstart, ystart );
			}
		});
		epopup.addItem("Delete", new Command() {
			@Override
			public void execute() {
				/*JsArray<Selection> ls = table.getSelections();
				List<Integer>	slist = new ArrayList<Integer>();
				//while( ls.length() > 0 ) {
				for( int i = 0; i < ls.length(); i++ ) {
					Selection sel = ls.get(i);
					slist.add( sel.getRow() );
					//ls = table.getSelections();
				}
				Collections.sort( slist, Collections.reverseOrder() );
				
				for( int i : slist ) {
					//Arrays.
					if( i < val.size() ) val.remove( i );
					data.removeRow( i );
				}
				table.draw( data );*/
				draw( xstart, ystart );
			}
		});
		
		canvas = Canvas.createIfSupported();
		canvas.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				mousedown = true;
				mousex = event.getX();
				mousey = event.getY();
			}
		});
		
		canvas.addMouseMoveHandler( new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if( mousedown ) {
					int x = event.getX();
					int y = event.getY();
					
					int xmin1 = max;
					int xmin2 = Webfasta.this.xstart + (mousex-x);
					int xstart = Math.max( 0, Math.min( xmin1, xmin2 ) );
					
					if( xmin2 > xmin1 ) {
						mousex = mousex+(xmin1-xmin2);
					}
					
					int ymin1 = val.size()*baseheight-canvas.getCoordinateSpaceHeight();
					int ymin2 = Webfasta.this.ystart + (mousey-y);
					int ystart = Math.max( 0, Math.min( ymin1, ymin2 ) );
					
					if( ymin2 > ymin1 ) {
						mousey = mousey+(ymin1-ymin2);
					}
					
					draw( xstart, ystart );
				}
			}
		});
		
		canvas.addMouseUpHandler( new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				mousedown = false;
				int x = event.getX();
				int y = event.getY();
				
				xstart = Math.max( 0, Math.min( max, xstart + (mousex-x) ) );
				ystart = Math.max( 0, Math.min( val.size()*baseheight, ystart + (mousey-y) ) );
				
				draw( xstart, ystart );
			}
		});
		
		canvas.addKeyDownHandler( new KeyDownHandler() {
			boolean running;
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				int kc = event.getNativeKeyCode();
				if( kc == KeyCodes.KEY_DOWN ) {
					ystart = Math.min( Math.max( 0, val.size()*baseheight-canvas.getOffsetHeight() ), ystart+baseheight );
				} else if( kc == KeyCodes.KEY_UP ) {
					ystart = Math.max( 0, ystart-baseheight );
				} else if( kc == KeyCodes.KEY_LEFT ) {
					xstart = Math.max( 0, xstart-10 );
				} else if( kc == KeyCodes.KEY_RIGHT ) {
					xstart = Math.min( Math.max( 0, max*10-canvas.getOffsetWidth() ), xstart+10 );
				} if( kc == KeyCodes.KEY_PAGEDOWN ) {
					ystart = Math.min( Math.max( 0, val.size()*baseheight-canvas.getOffsetHeight() ), ystart+canvas.getOffsetHeight() );
				} else if( kc == KeyCodes.KEY_PAGEUP ) {
					ystart = Math.max( 0, ystart-canvas.getOffsetHeight() );
				} else if( kc == KeyCodes.KEY_HOME ) {
					ystart = 0;
				} else if( kc == KeyCodes.KEY_END ) {
					ystart = Math.max( 0, val.size()*baseheight-canvas.getOffsetHeight() );
				}
				
				if( !running ) {
					running = true;
					draw( xstart, ystart );
					Timer t = new Timer() {
						public void run() {
							running = false;
						}
					};
					t.schedule(10);
				}
			}
		});
		
		MenuBar vpopup = new MenuBar( true );
		vpopup.addItem( "Goto", new Command() {
			@Override
			public void execute() {
				DialogBox	db = new DialogBox();
				db.setAutoHideEnabled( true );
				db.setText("Goto");
				
				final IntegerBox	ib = new IntegerBox();
				ib.addChangeHandler( new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						xstart = ib.getValue();
						draw( xstart, ystart );
					}
				});
				VerticalPanel	vp = new VerticalPanel();
				vp.add( ib );
				
				HorizontalPanel	hp = new HorizontalPanel();
				Button	wlb = new Button("<<");
				hp.add(wlb);
				Button slb = new Button("<");
				hp.add(slb);
				Button	srb = new Button(">");
				hp.add(srb);
				Button wrb = new Button(">>");
				hp.add(wrb);
				
				vp.add( hp );
				
				db.add( vp );
				db.center();
			}
		});
		MenuBar	hpopup = new MenuBar(true);
		hpopup.addItem("About", new Command() {
			@Override
			public void execute() {
				DialogBox	db = new DialogBox();
				db.setText("WebFasta");
				db.setAutoHideEnabled( true );
				//SimplePanel	simplepanel = new SimplePanel();
				//Text simplepanel = new Text();
				//Panel
				HTMLPanel	html = new HTMLPanel("WebFasta 1.0<br>Sigmasoft LTD.");
				db.add( html );
				//db.setHTML("WebFasta 1.0<br>Sigmasoft LTD.");
				db.center();
			}
		});
		
		MenuBar	menubar = new MenuBar();
		menubar.addItem("File", popup);
		menubar.addItem("Edit", epopup);
		menubar.addItem("View", vpopup);
		menubar.addItem("Help", hpopup);
		VerticalPanel	vpanel = new VerticalPanel();
		vpanel.setWidth("100%");
		//vpanel.setHeight(height+"px");
		vpanel.add( menubar );
		
		final SplitLayoutPanel slp = new SplitLayoutPanel();
		slp.setWidth("100%");
		slp.setHeight((height-25)+"px");
		vpanel.add( slp );
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int height = event.getHeight();
				slp.setHeight((height-25)+"px");
			}
		});
		
		canvas.setWidth("100%");
		canvas.setHeight("100%");
		context = canvas.getContext2d();
		
		canvas.addDragHandler( new DragHandler() {
			@Override
			public void onDrag(DragEvent event) {
				
			}
		});
		canvas.addDragEndHandler( new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {
				
			}
		});
		canvas.addDragStartHandler( new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {
				
			}
		});
		canvas.addDragEnterHandler( new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent event) {
				
			}
		});
		canvas.addDragOverHandler( new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {
				
			}
		});
		canvas.addDragLeaveHandler( new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				
			}
		});
		canvas.addDropHandler( new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				DataTransfer dt = event.getDataTransfer();
				//dt.getData(format)
				//File f = new File();
				transferData( dt );
			}
		});
		//dropTarget( context.getCanvas() );
		
		final ResizeLayoutPanel	sp = new ResizeLayoutPanel();
		sp.setWidth("100%");
		sp.setHeight("100%");
		sp.add( canvas );
		
		final ResizeLayoutPanel	tableresize = new ResizeLayoutPanel();
		tableresize.setWidth("100%");
		tableresize.setHeight("100%");
		
		ResizeHandler rh = new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				canvas.setWidth( w+"px" );
				canvas.setHeight( h+"px" );
				
				canvas.setCoordinateSpaceWidth( w );
				canvas.setCoordinateSpaceHeight( h );
				
				draw( xstart, ystart );
				
				//canvas.setWidth(100+i+"px");
				//canvas.setHeight(100+"px");
				
				//canvas.setCoordinateSpaceWidth( canvas.getOffsetWidth() );
				//canvas.setCoordinateSpaceHeight( canvas.getOffsetHeight() );
				
				/*Canvas newcanvas = Canvas.createIfSupported();
				Context2d context = newcanvas.getContext2d();
				//context.clearRect( 0, 0, 1000, 1000 );
				context.setFillStyle("#00ff00");
				context.fillRect(10, 10, 100, 100);
				if( canvas != null ) {
					canvas.removeFromParent();
				}
				sp.add( newcanvas );
				newcanvas.setWidth("100%");
				newcanvas.setHeight("100%");*/
			
				//canvas = newcanvas;
			}
		};
		sp.addHandler( rh, ResizeEvent.getType() );
		//canvas.addHandler( rh, ResizeEvent.getType() );
		
		final Canvas overview = Canvas.createIfSupported();
		overview.setWidth("100%");
		overview.setHeight("100%");
		
		overview.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if( max > 0 ) {
					int w = ( overview.getOffsetWidth()*canvas.getOffsetWidth() ) / (max*10);
					int val = ( (event.getX()-w/2)*max ) / overview.getOffsetWidth();
					xstart = Math.max( 0, Math.min( val, max ) );
					draw( xstart, ystart );
				}
			}
		});
		
		overview.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				mousedown = true;
			}
		});
		
		overview.addMouseUpHandler( new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				mousedown = false;
			}
		});
		
		overview.addMouseMoveHandler( new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if( mousedown ) { //event.getNativeButton() == NativeEvent.BUTTON_LEFT ) {
					int w = ( overview.getOffsetWidth()*canvas.getOffsetWidth() ) / (max*10);
					int val = ( (event.getX()-w/2)*max ) / overview.getOffsetWidth();
					xstart = Math.max( 0, Math.min( val, max ) );
					draw( xstart, ystart );
				}
			}
		});
		
		//overview.addMouseDownHandler( new )
		
		final ResizeLayoutPanel	roverview = new ResizeLayoutPanel();
		roverview.add( overview );
		
		ocontext = overview.getContext2d();
		ocontext.setFillStyle("#0000ff");
		
		tcanvas = Canvas.createIfSupported();
		tcontext = tcanvas.getContext2d();
		
		tcanvas.addMouseDownHandler( new MouseDownHandler() {
			List<Integer>	sortlist = new ArrayList<Integer>();
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				int x = event.getX();
				int y = event.getY();
				
				if( y < baseheight) {
					if( x < tcanvas.getCoordinateSpaceWidth() - 40 ) {
						sortcol = 0;
						if( sortlist.size() == 0 ) sortlist.add(0);
						
						if( sortlist.get(0) <= 0 ) {
							Collections.sort( val );
							sortlist.set(0, 1);
						} else {
							Collections.sort( val, Collections.reverseOrder() );
							sortlist.set(0, -1);
						}
					} else {
						sortcol = 1;
						if( sortlist.size() == 0 ) sortlist.add(0);
						if( sortlist.size() == 1 ) sortlist.add(0);
						
						if( sortlist.get(1) <= 0 ) {
							Collections.sort( val );
							sortlist.set(1, 1);
						} else {
							Collections.sort( val, Collections.reverseOrder() );
							sortlist.set(1, -1);
						}
					}
					prevx = Integer.MAX_VALUE;
					prevy = Integer.MAX_VALUE;
					draw( 0, 0 );
				}
			}
		});		
		tableresize.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				tcanvas.setWidth( w+"px" );
				tcanvas.setHeight( h+"px" );
				
				tcanvas.setCoordinateSpaceWidth( w );
				tcanvas.setCoordinateSpaceHeight( h );
				
				draw( xstart, ystart );
				
				//int w = tableresize.getOffsetWidth();
				//int h = tableresize.getOffsetHeight();
				
				/*int th = table.getOffsetHeight();
				if( first < 10 || h != th ) {
					first++;
					console( h + " " + th );
					
					options.setWidth( w+"px" );
					options.setHeight( h+"px" );
					//options.setSortColumn(table.)
					//table.draw( data, options );
					//table.setWidth( w );
					//table.setHeight( h );
					table.draw( data, options );
				}*/
				
				/*Element e = table.getElement();        
		        com.google.gwt.dom.client.Element de = e.getFirstChildElement();
		        de = de.getFirstChildElement();
		        scrollEv( de );*/
			}
		});
        tableresize.add( tcanvas );
        
        slp.addSouth( roverview, 100 );
        slp.addWest( tableresize, 200 );
        slp.add( sp );
		
		RootPanel	rootPanel = RootPanel.get();
		Style st = rootPanel.getElement().getStyle();
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		st.setBorderWidth(0.0, Unit.PX);
		
		Window.enableScrolling( false );
		
		rootPanel.add( vpanel );
		
		/*//HorizontalSplitPanel hsp = new HorizontalSplitPanel();
		slp.setWidth("1200px");
		slp.setHeight("400px");
		//final HorizontalPanel	horp = new HorizontalPanel();
		stuff();
		final ScrollPanel	sp = new ScrollPanel( canvas );
		//sp.setWidth("10000px");
		//sp.setHeight("200px");
		
		//final ScrollPanel	spheader = new ScrollPanel();
		//spheader.setWidth("200px");
		//spheader.setHeight("200px");*/
		
		//Runnable onLoadCallback = new Runnable() {
		//	public void run() {
		        //Panel panel = RootPanel.get();		        
		        //AbstractDataTable abs = new AbstractDataTable() {
		        	
		        //};
		        //table = new Table( createTable(), createTableOptions() );
		        //table.setWidth("100%");
		        //table.setHeight("100%");
		        //Table table = new Table();
		        //table.setTitle("erm");
		        
		        /*Event.ons
		        table.addHandler( new ScrollHandler() {
					@Override
					public void onScroll(ScrollEvent event) {
						console("scrolling");
					}
		        }, ScrollEvent.getType() );*/
		        
		        /*table.addSelectHandler( new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						draw();
					}
		        });
		        
		        table.addSortHandler( new SortHandler() {
					@Override
					public void onSort(SortEvent event) {
						sortind = getSortInfo( table.getJso() );
						draw();
						
						Element e = table.getElement();        
				        com.google.gwt.dom.client.Element de = e.getFirstChildElement();
				        de = de.getFirstChildElement();
				        scrollEv( de );
					}
		        });
		        
		        /*sp.addHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						//fl.publish( new LogRecord( Level.INFO, "res") );
						int val = 1200-event.getWidth();
						Window.alert(""+val);
						table.setWidth(val+"px");
					}
				}, ResizeEvent.getType() );*/
		        
		        /*slp.addHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						//fl.publish( new LogRecord( Level.INFO, "res") );
						int val = 1200-event.getWidth();
						Window.alert("me "+val);
						table.setWidth(val+"px");
					}
				}, ResizeEvent.getType() );
		        
		        tableresize.add( table );
		        
		        slp.addSouth( roverview, 100 );
		        slp.addWest( tableresize, 200 );
		        slp.add( sp );
		        
		        roverview.addResizeHandler( new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						overview.setCoordinateSpaceWidth( overview.getOffsetWidth() );
						overview.setCoordinateSpaceHeight( overview.getOffsetHeight() );
						draw();
					}
		        } );
		        
		        //console( table.getElement().getClassName() + " 1 " + table.getElement().getParentElement().getClassName() + " 2 " + table.getElement().getParentElement().getParentElement().getClassName() );
		        //NodeList<Element> ne = table.getElement().getElementsByTagName("table");
		        //table.getElement().getElementsByTagName(name)
				//TableElement te = TableElement.as( ne.getItem(0) );
		        //scrollEv( (JavaScriptObject)ne.getItem(0).getParentElement().getParentElement() );
		        //slp.add( sp );
		        //horp.add(table);
				//horp.add(sp);*/
		      //}
		//};
		
		/*final WebGLCanvas webGLCanvas = new WebGLCanvas("500px", "500px");
        glContext = webGLCanvas.getGlContext();
        glContext.viewport(0, 0, 500, 500);
        RootPanel.get("gwtGL").add(webGLCanvas);
        start();*/
	}
	
	/*public void drawSomethingNew() {
        // Get random coordinates and sizing
        int rndX = Random.nextInt(canvasWidth);
        int rndY = Random.nextInt(canvasHeight);
        int rndWidth = Random.nextInt(canvasWidth);
        int rndHeight = Random.nextInt(canvasHeight);

        // Get a random color and alpha transparency
        int rndRedColor = Random.nextInt(255);
        int rndGreenColor = Random.nextInt(255);
        int rndBlueColor = Random.nextInt(255);
        double rndAlpha = Random.nextDouble();

        CssColor randomColor = CssColor.make("rgba(" + rndRedColor + ", " + rndGreenColor + "," + rndBlueColor + ", " + rndAlpha + ")");

        context.setFillStyle(randomColor);
        context.fillRect( rndX, rndY, rndWidth, rndHeight);
        context.fill();
    }*/	
}
