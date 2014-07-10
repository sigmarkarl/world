package org.simmi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simmi.shared.Sequence;

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
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.typedarrays.client.Float64ArrayNative;
import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.Console;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Webfasta implements EntryPoint {
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	Canvas canvas;
	Canvas tcanvas;
	Canvas ocanvas;

	Context2d context;
	Context2d tcontext;
	Context2d ocontext;

	Int8Array content = null;

	int columnHeight = 20;
	int scrollBarWidth = 20;
	int scrollBarHeight = 20;
	// int unitheight = 20;

	/*
	 * public void stuff() { canvas = Canvas.createIfSupported();
	 * 
	 * if (canvas == null) { RootPanel.get().add(new
	 * Label("Sorry, your browser doesn't support the HTML5 Canvas element"));
	 * return; }
	 * 
	 * canvas.setStyleName("mainCanvas"); canvas.setWidth(canvasWidth + "px");
	 * canvas.setCoordinateSpaceWidth(500);
	 * 
	 * canvas.setHeight(canvasHeight + "px");
	 * canvas.setCoordinateSpaceHeight(500);
	 * 
	 * context = canvas.getContext2d();
	 * 
	 * /*final Timer timer = new Timer() {
	 * 
	 * @Override public void run() { drawSomethingNew(); } };
	 * timer.scheduleRepeating(1500);*
	 * 
	 * 
	 * //context.fillText("hohohoho", 10, 10); }
	 */

	// String _keyStr =
	// "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

	Set<Integer> selset = new HashSet<Integer>();

	public boolean isNaN(char c) {
		return false;
	}

	// public method for encoding
	public String encode(String input) {
		String _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		String output = "";
		char chr1, chr2, chr3;
		int enc1, enc2, enc3, enc4;
		int i = 0;

		// input = _utf8_encode(input);

		while (i < input.length()) {
			chr1 = input.charAt(i++);
			chr2 = input.charAt(i++);
			chr3 = input.charAt(i++);

			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;

			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}

			output = output + _keyStr.charAt(enc1) + _keyStr.charAt(enc2) + _keyStr.charAt(enc3) + _keyStr.charAt(enc4);
		}

		return output;
	};

	public native String decode(String input) /*-{
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

	private native String _utf8_encode(String string) /*-{
		string = string.replace(/\r\n/g, "\n");
		var utftext = "";

		for ( var n = 0; n < string.length; n++) {
			var c = string.charCodeAt(n);

			if (c < 128) {
				utftext += String.fromCharCode(c);
			} else if ((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			} else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}

		}

		return utftext;
	}-*/;

	private native String _utf8_decode(String utftext) /*-{
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while (i < utftext.length) {

			c = utftext.charCodeAt(i);

			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			} else if ((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i + 1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			} else {
				c2 = utftext.charCodeAt(i + 1);
				c3 = utftext.charCodeAt(i + 2);
				string += String.fromCharCode(((c & 15) << 12)
						| ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}

		}

		return string;
	}-*/;

	public native void scrollEv(JavaScriptObject te) /*-{
		//$wnd.alert('hook');
		//forlfoodinfo(var key in te){
		//	if( typeof te[key] == 'function' ) console.log(key);
		//}

		//var oldfun = te.onscroll;
		var hthis = this;
		te.onscroll = function() {
			//oldfun();
			hthis.@org.simmi.client.Webfasta::draw(II)( 0, 0 );
		};

	}-*/;

	public native JavaScriptObject handleFiles(Element ie) /*-{
		if (ie.files) {
			return ie.files[0];
		} else {
			return null;
		}

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

	public native void console(String str) /*-{
		if ($wnd.console)
			$wnd.console.log(str);
	}-*/;

	public native String createObjectURL(elemental.html.Blob bb) /*-{
		return $wnd.URL.createObjectURL(bb);
	}-*/;

	class Annotation {
		public Annotation(String name, String color, int start, int stop) {
			this.name = name;
			this.color = color;
			this.start = start;
			this.stop = stop;
		}

		String name;
		String color;
		int start;
		int stop;
	};

	static int sortcol = 0;

	class SequenceOld extends Sequence {
		int namestart;
		int nameend;
		int seqstart;
		int seqstop;

		public SequenceOld(int nstart, int nend, int sstart, int sstop) {
			super(null, null);

			this.namestart = nstart;
			this.nameend = nend;
			this.seqstart = sstart;
			this.seqstop = sstop;

			this.start = 0;

			byte[] bb = new byte[nameend - namestart];
			for (int i = namestart; i < nameend; i++) {
				bb[i - namestart] = content.get(i);
			}
			setName(new String(bb));
		}

		@Override
		public int getStart() {
			return start;
		}

		@Override
		public int getEnd() {
			return start + getLength();
		}

		/*
		 * public String toString() { //content.toString() return
		 * Webfasta.this.content.subarray( namestart, nameend ); }
		 */

		public String getName() {
			return name;
		}

		public Int8Array getSubarray(int start, int end) {
			return Webfasta.this.content.subarray(start + seqstart, end
					+ seqstart);
		}

		/*
		 * public Sequence( String name, String seq ) { this.name = name;
		 * this.seq = seq; this.aInd = new int[seq.length()]; this.aList = new
		 * ArrayList<Annotation>(); }
		 */

		public void addAnnotation(Annotation a) {
			/*
			 * aList.add( a ); for( int i = a.start; i < a.stop; i++ ) { if(
			 * aInd[i] == 0 ) aInd[i] = aList.size(); else aInd[i] =
			 * aList.size()<<16; }
			 */
		}

		public int[] getAnnotationIndex() {
			return null;
		}

		public List<Annotation> getAnnotations() {
			return null;
		}

		@Override
		public void delete(int dstart, int dstop) {
			int count = 0;
			for (int i = seqstart + dstop - start; i < seqstop; i++) {
				int val = content.get(i);
				// Browser.getWindow().getConsole().log(
				// ""+(char)content.get(seqstart+dstop-start) );
				content.set(seqstart + dstart - start + count, val);
				count++;
			}
			seqstop -= dstop - dstart;
			edited = true;
		}

		@Override
		public char charAt(int i) {
			int ind = i;// - start;
			if (ind >= 0 && ind < getLength())
				return (char) content.get(seqstart + ind); // char2String.get(
															// content.charAt(
															// seqstart+i ) );

			return ' ';
		}

		@Override
		public void setCharAt(int i, char c) {
			content.set(seqstart + i, c);
		}

		@Override
		public int getLength() {
			return seqstop - seqstart;
			// return seq.length();
		}

		@Override
		public void reverse() {
			for (int i = 0; i < getLength() / 2; i++) {
				char c = charAt(i);
				setCharAt(i, charAt(getLength() - 1 - i));
				setCharAt(getLength() - 1 - i, c);
			}
		}

		@Override
		public void complement() {
			for (int i = 0; i < getLength(); i++) {
				char c = charAt(i);
				if (Sequence.complimentMap.containsKey(c)) {
					setCharAt(i, Sequence.complimentMap.get(c));
				}
			}
		}

		public void utReplace() {
			for (int i = 0; i < getLength(); i++) {
				char c = charAt(i);
				if( c == 't' ) setCharAt(i, 'u');
				else if( c == 'u' ) setCharAt(i, 't');
				else if( c == 'T' ) setCharAt(i, 'U');
				else if( c == 'U' ) setCharAt(i, 'T');
			}
		}
		
		public void caseSwap() {
			for (int i = 0; i < getLength(); i++) {
				char c = charAt(i);
				setCharAt(i, Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c) );
			}
		}
		
		public void upperCase() {
			for (int i = 0; i < getLength(); i++) {
				char c = charAt(i);
				setCharAt(i, Character.toUpperCase(c) );
			}
		}
		
		public void removeGaps() {
			int start = getLength();
			int stop = getLength();
			for( int i = getLength()-1; i >= 0; i-- ) {
				char c = charAt(i);
				if( c == '.' || c == '-' || c == ' ' || c == '*' ) start = i;
				else {
					if( stop > start ) this.delete( start, stop );
					stop = i;
				}
			}
			if( stop > start ) this.delete( start, stop );
		}
		
		public void removeAllGaps() {
			int start = getLength();
			int stop = getLength();
			for( int i = getLength()-1; i >= 0; i-- ) {
				char c = charAt(i);
				if( c == '.' || c == '-' || c == ' ' || c == '*' ) start = i;
				else {
					if( stop > start ) this.delete( start, stop );
					stop = i;
				}
			}
			if( stop > start ) this.delete( start, stop );
		}

		@Override
		public int compareTo(Sequence o) {
			if (sortcol > 1) {
				int i;
				if (xsellen > 0) {
					i = xselloc;
					while (charAt(i) == o.charAt(i)
							&& i < xselloc + xsellen - 1)
						i++;
				} else {
					i = sortcol - 2;
				}
				return this.charAt(i) - o.charAt(i);
			} else if (sortcol >= 0) {
				return sortcol == 0 ? getName().compareTo(o.getName())
						: getLength() - o.getLength();
			} else {
				return isSelected() ? (o.isSelected() ? 0 : -1) : (o
						.isSelected() ? 1 : 0);
			}
		}

		/*
		 * String name; String seq; int[] aInd; List<Annotation> aList;
		 */
	};

	public native int indexOf(String jso, char val, int start) /*-{
		return jso.indexOf(val, start);
	}-*/;

	public void fileLoad(Int8Array cont, int max) {
		prevx = Integer.MAX_VALUE;
		prevy = Integer.MAX_VALUE;

		this.content = cont;
		this.max = max;

		// val = val.subList(0, 1000);

		initTable(0);
		draw(0, 0);
	}

	public void initTable(int append) {
		/*
		 * if( append == 0 ) { data = DataTable.create();
		 * data.addColumn(ColumnType.STRING, "Name");
		 * data.addColumn(ColumnType.NUMBER, "Length"); } int start =
		 * data.getNumberOfRows();
		 * 
		 * int count = 0; for( Sequence seq : val ) { data.addRow();
		 * data.setValue( count+start, 0, seq.toString() ); data.setValue(
		 * count+start, 1, seq.length() );
		 * 
		 * count++; }
		 * 
		 * table.draw(data, createTableOptions()); draw();
		 * 
		 * Element e = table.getElement(); com.google.gwt.dom.client.Element de
		 * = e.getFirstChildElement(); de = de.getFirstChildElement(); scrollEv(
		 * de );
		 */
	}

	public int indexOf(char c) {
		return indexOf(c, 0);
	}

	public int indexOf(char c, int k) {
		for (int i = k; i < content.length(); i++) {
			if (content.get(i) == c)
				return i;
		}
		return -1;
	}

	public int getSequenceNumber() {
		return /* filtInd.size() != 0 ? filtInd.size() : */val.size();
	}

	List<Sequence> val = new ArrayList<Sequence>();
	Map<String, SequenceOld> seqmap = new HashMap<String, SequenceOld>();
	int max = 0;
	
	public int getMin() {
		return 0;
	}
	
	public int getMax() {
		return max;
	}
	
	public int getDiff() {
		return max;
	}

	public void fileLoaded(Int8Array cont) {
		int start = 0;
		if (append) {
			start = content != null ? content.length() : 0;
			Int8Array i8a = Int8ArrayNative.create(start + cont.length());
			if( content != null ) i8a.set(content);
			i8a.set(cont, start);
			this.content = i8a;
		} else {
			content = cont;
			seqmap.clear();
			max = 0;
			val = new ArrayList<Sequence>();
		}

		int r = indexOf('>', start); // indexOf( content, '>', 0);
		// String seqname = "mu";//content.substring(r+1, i);
		// int k = content.indexOf('>', i);
		while (r != -1) {
			int i = indexOf('\n', r + 1); // indexOf( content, '\n', r+1);
			// String seqname = content.substring(r+1, i);
			if (i != -1) {
				int k = indexOf('>', i + 1); // indexOf( content, '>', i+1);
				// for( int r = 0; r < split.length-1; r++ ) {
				// String s = split[r+1];

				// content.su
				/*
				 * if( seqmap.containsKey( seqname ) ) { String subseq =
				 * content.substring(i+1, r-1); Sequence seq =
				 * seqmap.get(seqname); String[] subsplit = subseq.split("\n");
				 * for( String a : subsplit ) { String[] aspl = a.split("\t");
				 * seq.addAnnotation( new Annotation(aspl[0], aspl[1],
				 * Integer.parseInt(aspl[2]), Integer.parseInt(aspl[3])) ); } }
				 * else {
				 */
				// String seqstr = subseq;//.replace("\n", "");

				int n = k == -1 ? content.length() : k - 1;
				int m = 0;
				/*
				 * for( int u = i+1; u < n; u++ ) { if( content.charAt(u) ==
				 * '\n' ) { m++; } else { //content.setCharAt(u-m,
				 * content.charAt(u)); } }
				 */

				SequenceOld seq = new SequenceOld(r + 1, i, i + 1, n - m); // new
																			// Sequence(seqname,seqstr);
				int seqlen = n - i - 1;
				// int seqlen = seqstr.length();
				// seqmap.put( seqname, seq );
				val.add(seq);
				if (seqlen > max)
					max = seqlen;

				r = k;
			} else
				r = -1;
		}

		draw(xstart, ystart);
	}

	public void resetMax() {
		max = 0;
		for (Sequence seq : val) {
			if (seq.getEnd() > max)
				max = seq.getEnd();
		}
	}

	public Node nodeRecursive(Node n) {
		NodeList<Node> nlf = n.getChildNodes();
		for (int i = 0; i < nlf.getLength(); i++) {
			Node nn = nlf.getItem(i);
			if (nn.getNodeName().toLowerCase().contains("table"))
				return n;
		}
		return null;
	}

	/*
	 * public void odraw() { if( val != null ) {
	 * ocontext.setFillStyle("#CCFFCC"); //ocontext.fillRect(20, 20, 100, 100);
	 * 
	 * //ocontext.setStrokeStyle("#CCFFCC");
	 * //ocontext.setStrokeStyle(FillStrokeStyle.TYPE_CSSCOLOR);
	 * //ocontext.setLineWidth(2.0); for( int i = 0; i < val.length; i++ ) { int
	 * y = sortind != null ? sortind.get(i) : i; String seq = val[y];
	 * //ocontext.moveTo(0, i); int x =
	 * seq.length()*ocontext.getCanvas().getClientWidth()/max;
	 * //ocontext.lineTo( x, i ); ocontext.fillRect(0, i, x, 1); } } }
	 */

	int prevx = 0;
	int prevy = 0;
	int xstart = 0;
	int ystart = 0;
	int unitheight = 16;
	int basewidth = 10;

	public void draw(int xstartLocal, int ystartLocal) {
		// Browser.getWindow().getConsole().log( ystartLocal );

		if (val != null) {
			context.setFillStyle("#222222");

			int cw = canvas.getCoordinateSpaceWidth();
			int ch = canvas.getCoordinateSpaceHeight();

			int tcw = tcanvas.getCoordinateSpaceWidth();
			int tch = tcanvas.getCoordinateSpaceHeight();

			int ax = Math.abs(xstartLocal - prevx);
			int ay = Math.abs(ystartLocal - prevy);

			if (false && ay < ch / 2 && ax < cw / 2) {
				int h = ch - ay;
				int w = cw - ax;
				int xuno = Math.max(0, xstartLocal - prevx);
				int xduo = Math.max(0, prevx - xstartLocal);
				int yuno = Math.max(0, ystartLocal - prevy);
				int yduo = Math.max(0, prevy - ystartLocal);
				context.drawImage(context.getCanvas(), xuno, yuno + unitheight,
						w, h, xduo, yduo + unitheight, w, h);
				// tcontext.drawImage(tcontext.getCanvas(), 0, yuno+baseheight,
				// tcw, h, 0, yduo+baseheight, tcw, h);
				if (xuno > xduo) {
					if (yuno > yduo) {
						drawSection(xstartLocal, ystartLocal, 0, h, w, ay);
						drawSection(xstartLocal, ystartLocal, w, 0, ax, h);
						drawSection(xstartLocal, ystartLocal, w, h, ax, ay); // ,
																				// "#0000ff"
																				// );
						drawTable(ystartLocal, h, ay);
					} else {
						drawSection(xstartLocal, ystartLocal, 0, 0, w, ay);
						drawSection(xstartLocal, ystartLocal, w, ay, ax, h);
						drawSection(xstartLocal, ystartLocal, w, 0, ax, ay);
						drawTable(ystartLocal, 0, ay);
					}
				} else {
					if (yuno > yduo) {
						drawSection(xstartLocal, ystartLocal, 0, 0, ax, h);
						drawSection(xstartLocal, ystartLocal, ax, h, w, ay);
						drawSection(xstartLocal, ystartLocal, 0, h, ax, ay);
						drawTable(ystartLocal, h, ay);
					} else {
						drawSection(xstartLocal, ystartLocal, ax, 0, w, ay);
						drawSection(xstartLocal, ystartLocal, 0, ay, ax, h);
						drawSection(xstartLocal, ystartLocal, 0, 0, ax, ay);
						drawTable(ystartLocal, 0, ay);
					}
				}
			} else {
				drawSection(xstartLocal, ystartLocal, 0, 0, cw, ch);
				drawTable(ystartLocal, 0, ch);
			}

			context.setFillStyle("#EEEEEE");
			context.fillRect(0, 0, cw, columnHeight);
			context.setFillStyle("#111111");
			for (int x = xstartLocal; x < xstartLocal + cw; x += basewidth) {
				int val = 3;
				if ((x / basewidth) % basewidth == 0)
					val = 7;
				else if ((x / 10) % 10 == 5)
					val = 5;
				context.fillRect((x - xstartLocal), unitheight - val, 1, val);
			}
			for (int x = xstartLocal; x < xstartLocal + cw; x += 100) {
				context.fillText("" + (x / basewidth), (x - xstartLocal),
						unitheight - 7);
			}

			context.setFillStyle("#FFFFFF");
			context.fillRect(cw - scrollBarWidth, columnHeight, scrollBarWidth, ch);
			context.fillRect(0, ch - scrollBarHeight, cw, scrollBarHeight);

			tcontext.setFillStyle("#FFFFFF");
			tcontext.fillRect(0, tch - scrollBarHeight, tcw, scrollBarHeight);

			context.setFillStyle("#111111");
			if (val.size() > 0 && max > 0) {
				context.fillRect(
						cw - scrollBarWidth,
						columnHeight
								+ ((ch - scrollBarHeight - columnHeight) * ystartLocal)
								/ (val.size() * unitheight - (ch
										- scrollBarHeight - columnHeight)),
						scrollBarWidth, 5.0);
				context.fillRect(((cw - scrollBarWidth) * xstartLocal)
						/ (max * basewidth - (cw - scrollBarWidth)), ch
						- scrollBarHeight, 5.0, scrollBarHeight);
			}

			if (max > 0) {
				/*
				 * ocontext.clearRect(0, 0, ocanvas.getWidth(),
				 * ocanvas.getHeight()); ocontext.setFillStyle("#CCFFCC");
				 * //ocontext.fillRect(20, 20, 100, 100);
				 * 
				 * //ocontext.setStrokeStyle("#CCFFCC");
				 * //ocontext.setStrokeStyle(FillStrokeStyle.TYPE_CSSCOLOR);
				 * //ocontext.setLineWidth(2.0);
				 * 
				 * int h = ocanvas.getHeight(); int seqh =
				 * ((double)h/(double)val.size() <= 1.0) ? 1 : 2; double stph =
				 * Math.min( h, 2.0*val.size() ); for( int i = 0; i <
				 * val.size(); i++ ) { int y = sortind != null ? sortind.get(i)
				 * : i; Sequence seq = val.get(y); //String seqstr = seq.seq;
				 * //ocontext.moveTo(0, i); int x =
				 * seq.length()*ocanvas.getWidth()/max; //ocontext.lineTo( x, i
				 * ); ocontext.fillRect(0, i*stph/(double)val.size(), x, seqh);
				 * }
				 * 
				 * for( int i = 0; i < val.size(); i++ ) { int y = sortind !=
				 * null ? sortind.get(i) : i; Sequence seq = val.get(y);
				 * List<Annotation> lann = seq.getAnnotations(); if( lann !=
				 * null ) { for( Annotation ann : seq.getAnnotations() ) {
				 * ocontext.setFillStyle( ann.color ); //int x =
				 * seq.length()*ocanvas.getWidth()/max;
				 * 
				 * long start = (long)ann.start*ocanvas.getWidth()/(long)max;
				 * long stop = Math.max( start+1,
				 * (long)ann.stop*ocanvas.getWidth()/(long)max );
				 * ocontext.fillRect( (int)start, i*stph/(double)val.size(),
				 * (int)(stop-start), seqh); } } }
				 * 
				 * ocontext.setFillStyle("#333333"); int w = (
				 * canvas.getWidth()*ocanvas.getWidth() ) / (max*10);
				 * ocontext.fillRect(xstart*ocanvas.getWidth()/(max), 0, w, 20);
				 */
			}

			prevx = xstartLocal;
			prevy = ystartLocal;
		}
	}

	public void drawTable(int ystartLocal, int yloc, int canvasHeight) {
		int tcw = tcanvas.getCoordinateSpaceWidth();
		int tch = tcanvas.getCoordinateSpaceHeight();

		int ys = Math.max(0, ((ystartLocal + yloc) / unitheight) * unitheight);
		int ye = Math.min(
				((ystartLocal + yloc + canvasHeight) / unitheight + 1)
						* unitheight, val.size() * unitheight);

		// int ymax = Math.min( ystartLocal+tch, val.size()*baseheight );

		// tcontext.setFillStyle("#ffffff");
		// tcontext.fillRect(0, ys-ystartLocal+baseheight, tcw, ye-ys);
		// tcontext.fillRect(tcw-45, ys-ystartLocal+baseheight, 45, ye-ys);

		tcontext.clearRect(0, ys - ystartLocal + unitheight, tcw, ye - ys);
		tcontext.clearRect(tcw - 45, ys - ystartLocal + unitheight, 45, ye - ys);
		for (int y = ys; y < ye; y += unitheight) {
			int i = y / unitheight;
			int yy = i * unitheight;
			Sequence seq = val.get(i);
			if (seq.isSelected()) {
				tcontext.setFillStyle("#0011AA");
				tcontext.fillRect(0.0, yy + 1.0 * unitheight - ystartLocal
						+ 2.0, tcw, unitheight);
				tcontext.setFillStyle("#ffffff");
			} else {
				tcontext.setFillStyle("#111111");
			}
			// tcontext.fillText( seq.getName(), 3.0,
			// yy+2.0*baseheight-3.0-ystartLocal );
			tcontext.fillText(seq.getLength() + "", tcw - 40.0, yy + 2.0
					* unitheight - 3.0 - ystartLocal);
		}
		tcontext.save();
		tcontext.beginPath();
		tcontext.rect(0.0, 0.0, tcw - 45.0, tch);
		tcontext.closePath();
		tcontext.clip();
		for (int y = ys; y < ye; y += unitheight) {
			int i = y / unitheight;
			int yy = i * unitheight;
			Sequence seq = val.get(i);
			if (seq.isSelected()) {
				// tcontext.setFillStyle("#0011AA");
				// tcontext.fillRect(0.0, yy+1.0*baseheight-ystartLocal+2.0,
				// tcw, baseheight);
				tcontext.setFillStyle("#ffffff");
			} else {
				tcontext.setFillStyle("#111111");
			}
			tcontext.fillText(seq.getName(), 3.0, yy + 2.0 * unitheight - 3.0
					- ystartLocal);
			// tcontext.fillText( seq.getLength()+"", tcw-40.0,
			// yy+2.0*baseheight-3.0-ystartLocal );
		}
		tcontext.restore();

		// tcontext.setFillStyle("#ffffff");
		// tcontext.fillRect(tcw-45, ys-ystartLocal+baseheight, 45, ye-ys);
		// tcontext.setFillStyle("#111111");
		/*
		 * for( int y = ys; y < ye; y+=baseheight ) { int i = y/baseheight; int
		 * yy = i*baseheight; SequenceOld seq = val.get(i); tcontext.fillText(
		 * seq.getLength()+"", tcw-40.0, yy+2.0*baseheight-3.0-ystartLocal );
		 * //tcontext.fillText( seq.getType()+"", tcw-40.0,
		 * yy+2.0*baseheight-3.0-ystartLocal ); }
		 */

		tcontext.setFillStyle("#EEEEEE");
		tcontext.fillRect(0.0, 0.0, tcw, 20.0);
		tcontext.setFillStyle("#222222");
		tcontext.fillText("Name", 3.0, 20.0 - 5.0);
		// tcontext.fillText("Type", tcw-100, 20.0-5.0);
		tcontext.fillText("Length", tcw - 40, 20.0 - 5.0);
	}

	public void drawSection(int xstartLocal, int ystartLocal, int xloc,
			int yloc, int canvasWidth, int canvasHeight) {
		drawSection(xstartLocal, ystartLocal, xloc, yloc, canvasWidth,
				canvasHeight, "#eeeeee");
	}

	public void drawSection(int xstartLocal, int ystartLocal, int xloc,
			int yloc, int canvasWidth, int canvasHeight, String strcolor) {
		context.setFillStyle(strcolor);

		// console( xstartLocal + "  " + ystartLocal );
		// console( xstart + " - " + ystart );
		// context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		// for( int y = 0; y < Math.min( val.length, canvas.getHeight()/10 );
		// y++ ) {

		int xs = Math.max(0, ((xstartLocal + xloc) / basewidth) * basewidth);
		int ys = Math.max(0, ((ystartLocal + yloc) / unitheight) * unitheight);
		int xe = ((xstartLocal + xloc + canvasWidth) / basewidth + 1) * basewidth;
		int ye = Math.min( ((ystartLocal + yloc + canvasHeight) / unitheight + 1)
						* unitheight, val.size() * unitheight);

		context.fillRect(xs - xstartLocal, ys - ystartLocal + unitheight, xe - xs, ye - ys);
		context.setFillStyle("#222222");

		if (basecolors) {
			for (int y = ys; y < ye; y += unitheight) {
				int i = y / unitheight;
				int yy = i * unitheight;
				Sequence seq = val.get(i);
				// int[] ann = seq.getAnnotationIndex();

				/*
				 * if( selset.contains(i) ) { context.setFillStyle("#DDDDFF");
				 * context.fillRect(0, ystartLocal, canvasWidth, baseheight );
				 * context.setFillStyle("#222222"); }
				 */

				for (int x = xs; x < Math.min(seq.getEnd()/*seq.getLength()*/ * basewidth, xe); x += basewidth) {
					int k = x / basewidth;
					int xx = k * basewidth;

					//if( i == 2 ) console( ""+k );
					char c = seq.charAt(k - seq.getStart());
					// if( x == xs )
					// Browser.getWindow().getConsole().log("char "+c);
					Integer baseloc = mccol.get(c);
					if (baseloc == null) {
						// String basecolor = ccol.get(c);
						// context.setFillStyle( basecolor );
						// context.fillRect( xx-xstartLocal,
						// yy-ystartLocal+baseheight, basewidth, baseheight );
						context.setFillStyle("#222222");
						context.fillText(c + "", (xx - xstartLocal), yy + 2.0
								* unitheight - 3.0 - ystartLocal);
					} else {
						context.drawImage(buffer.getCanvasElement(), baseloc,
								0, basewidth, unitheight, xx - xstartLocal, yy
										- ystartLocal + unitheight, basewidth,
								unitheight);
					}
					/*
					 * if( ann != null && ann[x] != 0 ) { Annotation a =
					 * seq.getAnnotations().get(ann[x]-1); context.setFillStyle(
					 * a.color ); context.fillRect( (x-xstart)*10,
					 * th-top+baseheight, 10, 5 );
					 * context.setFillStyle("#222222"); }
					 */
				}
			}
		} else {
			for (int y = ys; y < ye; y += unitheight) {
				int i = y / unitheight;
				int yy = i * unitheight;
				Sequence seq = val.get(i);
				// int[] ann = seq.getAnnotationIndex();

				if (selset.contains(i)) {
					context.setFillStyle("#DDDDFF");
					context.fillRect(0, ystartLocal, canvasWidth, unitheight);
					context.setFillStyle("#222222");
				}

				for (int x = xs; x < Math.min(seq.getEnd() * basewidth, xe); x += basewidth) {
					int k = x / basewidth;
					int xx = k * basewidth;

					char c = seq.charAt(k - seq.getStart());
					context.fillText(c + "", (xx - xstartLocal), yy + 2.0 * unitheight - 3.0 - ystartLocal);
					/*
					 * if( ann != null && ann[x] != 0 ) { Annotation a =
					 * seq.getAnnotations().get(ann[x]-1); context.setFillStyle(
					 * a.color ); context.fillRect( (x-xstart)*10,
					 * th-top+baseheight, 10, 5 );
					 * context.setFillStyle("#222222"); }
					 */
				}
			}
		}

		int xsn = Math.max(0, xselloc * basewidth - xstartLocal);
		// int ys = Math.max( 0, ((ystartLocal+yloc)/unitheight)*unitheight );
		int xen = Math.min(canvasWidth, (xselloc + xsellen) * basewidth
				- xstartLocal);
		// int ye = Math.min(
		// ((ystartLocal+yloc+canvasHeight)/unitheight+1)*unitheight,
		// val.size()*unitheight );

		context.setFillStyle("rgba( 30, 100, 255, 0.5)");
		if (xen - xsn > 0)
			context.fillRect(xsn, ys - ystartLocal + columnHeight, xen - xsn, ye - ys);
	}

	public native JsArrayInteger getSortInfo(JavaScriptObject t) /*-{
		var si = t.getSortInfo();
		return si.sortedIndexes;
	}-*/;

	public native void click(JavaScriptObject e) /*-{
		e.click();
	}-*/;

	public void stuff(final int append) {
		final DialogBox db = new DialogBox();
		db.setText("Open file ...");

		// HorizontalPanel vp = new HorizontalPanel();
		final FormPanel form = new FormPanel();
		form.setAction("/");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		// form.add( vp );*/
		final FileUpload file = new FileUpload();
		// file.
		// file.getElement().setAttribute("accept", "");
		file.getElement().setId("fasta");

		form.add(file);
		db.add(form);
		/*
		 * file.fireEvent( GwtEvent)
		 * 
		 * //SubmitButton submit = new SubmitButton(); //submit.setText("Open");
		 */

		file.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				/*
				 * try { console("ss"); Desktop dt =
				 * Factory.getInstance().createDesktop(); console("before");
				 * dt.openFiles(new OpenFilesHandler() { public void
				 * onOpenFiles(OpenFilesEvent oevent) { console("erm");
				 * 
				 * File[] files = oevent.getFiles(); File file = files[0];
				 * 
				 * console("toff"); Blob data = file.getBlob();
				 * 
				 * console( "" + data.getLength() );
				 * 
				 * byte[] bb = data.getBytes(); console("toff");
				 * //Webfasta.this.content = new String( bb ); fileLoaded( new
				 * String(bb), 0 ); } }, true); console("after"); } catch
				 * (Exception ex){ Window.alert(ex.toString()); }
				 */
			}
		});

		db.setAutoHideEnabled(true);
		db.center();
		// file.fireEvent( new ClickEvent(){} );
		click(file.getElement());
	}

	public native void whileStuff(int r, int viewlen, JavaScriptObject view) /*-{
		while (r != -1) {
			var i = r + 1;
			//res.indexOf( '\n', r+1);
			while (i < viewlen && view[i] != '\n')
				i++;

			//String seqname = content.substring(r+1, i);
			var u = i + 1;
			//res.indexOf( '>', i+1);

			while (u < k && view[u] != '>')
				u++;
			if (u == k)
				u = -1;

			//for( int r = 0; r < split.length-1; r++ ) {
			//String s = split[r+1];

			var n = u == -1 ? k : u - 1;
			//Sequence seq = new Sequence( r+1, i, i+1, n ); //new Sequence(seqname,seqstr);
			var seqlen = n - i - 1;

			m = 0;
			for (u = i + 1; u < n; u++) {
				//if( res[u] == '\n' ) {
				//	m++;
				//} else {
				//	res[u-m] = res[u];
				//}
				view[u] = 'O';
			}

			//s.@org.simmi.client.Webfasta::addSequence(IIII)( r+1, i, i+1, n-m );
			if (seqlen > max)
				max = seqlen;
			count += 1;

			r = u;
		}
	}-*/;

	public native elemental.html.Blob createBlob(String bb) /*-{
		var blob = new Blob([ bb ], {
			"type" : "text/plain"
		});
		return blob;
	}-*/;

	public native JavaScriptObject createFlags() /*-{
		var flags = {
			create : true
		};
		return flags;
	}-*/;

	public native void subReadOld(ArrayBuffer newview, int k) /*-{
		$wnd.console.log("subRead");

		var s = this;
		var view = new Uint8Array(newview, 0, k);

		$wnd.console.log("blobprob");
		var blob = new Blob([ view ], {
			"type" : "text/plain"
		});
		$wnd.console.log("subRead2");

		reader = new FileReader();
		reader.onload = function(e) {
			var res = e.target.result;
			//s.@org.simmi.client.Webfasta::fileLoaded(Ljava/lang/String;I)( res, 0 );
		}
		reader.readAsText(blob);
	}-*/;

	public void shorten(Int8Array view) {
		// res.
		// Uint8Array view = new Uint8ArrayImpl(res, 0, res.byteLength());

		int k = 0;
		boolean inside = false;
		for (int i = 0; i < view.length(); i++) {
			if (view.get(i) == '>') {
				inside = true;
				if (i != 0) {
					view.set(k, '\n');
					k++;
				}
			}

			int c = view.get(i);
			view.set(k, c);
			if (inside
					|| (view.get(i) != ' ' && view.get(i) != '\r' && view
							.get(i) != '\n')) {
				k++;
			}

			if (inside && view.get(i) == '\n') {
				inside = false;
			}

			// if( i % 100 == 0 ) $wnd.console.log( "erm "+i );
		}

		int r = 0;
		// res.indexOf( '>' );
		while (r < k && view.get(r) != '>') {
			r++;
		}

		if (r == k)
			r = -1;

		// int count = 0;
		// int max = 0;
		// String seqname = "mu";//content.substring(r+1, i);
		// int k = content.indexOf('>', i);

		r = -1;
		// whileStuff();

		// subRead( view.buffer(), k );
		fileLoaded(Int8ArrayNative.create(view.buffer(), 0, k));
	}

	public native void fileRead(JavaScriptObject file) /*-{
		var s = this;
		var reader = new FileReader();
		reader.onload = function(e) {
			var res = e.target.result;
			var view = new Int8Array(res, 0, res.byteLength);
			s.@org.simmi.client.Webfasta::shorten(Lcom/google/gwt/typedarrays/shared/Int8Array;)( view );
		};
		reader.readAsArrayBuffer(file);
	}-*/;

	public native boolean transferData(JavaScriptObject dataTransfer) /*-{
		var succ = false;
		var s = this;
		if (dataTransfer.files) {
			var files = dataTransfer.files;
			var count = files.length;
			if (count > 0) {
				succ = true;
				//var count = files.length;
				var file = files[0];
				s.@org.simmi.client.Webfasta::fileRead(Lcom/google/gwt/core/client/JavaScriptObject;)( file );
				//reader.readAsText( file );
			}
		}
		return succ;
	}-*/;

	/*
	 * public native void dropTarget( JavaScriptObject canvas ) /*-{ var s =
	 * this; //jso.ondrop = function() { // $wnd.alert('alert'); //
	 * s.@org.simmi.client.Pifviewer::slubb()(); //};
	 * 
	 * function f1( evt ) { evt.stopPropagation(); evt.preventDefault();
	 * 
	 * //$wnd.alert("hey"); };
	 * 
	 * function f( evt ) { evt.stopPropagation(); evt.preventDefault();
	 * 
	 * var files = evt.dataTransfer.files; var count = files.length;
	 * 
	 * if(count > 0) { var file = files[0]; var reader = new FileReader();
	 * reader.onload = function(e) { //var iv; //try { // var res =
	 * e.target.result; // iv = new Int16Array( res ); //} catch( e ) { //
	 * $wnd.alert(e); //} var res = e.target.result;
	 * //s.@org.simmi.client.Pifviewer
	 * ::loadImage(Lcom/google/gwt/dom/client/CanvasElement
	 * ;Lcom/google/gwt/core/client/JavaScriptObject;)( canvas, iv );
	 * 
	 * 
	 * var r = res.indexOf( '>' ); var count = 0; var max = 0; //String seqname
	 * = "mu";//content.substring(r+1, i); //int k = content.indexOf('>', i);
	 * while( r != -1 ) { var i = res.indexOf( '\n', r+1); //String seqname =
	 * content.substring(r+1, i); var k = res.indexOf( '>', i+1); //for( int r =
	 * 0; r < split.length-1; r++ ) { //String s = split[r+1];
	 * 
	 * var n = k == -1 ? res.length : k-1; //Sequence seq = new Sequence( r+1,
	 * i, i+1, n ); //new Sequence(seqname,seqstr); var seqlen = n-i-1;
	 * s.@org.simmi.client.Webfasta::addSequence(IIII)( r+1, i, i+1, n ); if(
	 * seqlen > max ) max = seqlen; count += 1;
	 * 
	 * r = k; }
	 * 
	 * s.@org.simmi.client.Webfasta::fileLoad(Ljava/lang/String;I)( res, max );
	 * //s.@org.simmi.client.Webfasta::fileLoaded(Ljava/lang/String;I)( res, 0
	 * ); }; //reader.readAsArrayBuffer( file ); reader.readAsText( file ); }
	 * else { var res = evt.dataTransfer.getData("Text");
	 * //$wnd.alert(evt.dataTransfer.effectAllowed);
	 * s.@org.simmi.client.Webfasta::fileLoaded(Ljava/lang/String;I)( res, 0 );
	 * } }; canvas.addEventListener("dragenter", f1, false );
	 * canvas.addEventListener("dragexit", f1, false );
	 * canvas.addEventListener("dragover", f1, false );
	 * canvas.addEventListener("drop", f, false ); }-
	 */;

	public void addSequence(int nstart, int nstop, int sstart, int sstop) {
		val.add(new SequenceOld(nstart, nstop, sstart, sstop));
	}

	public native int bb(JavaScriptObject jo, int ind) /*-{
		return jo.charCodeAt(ind);
	}-*/;

	public native int postMessage(elemental.dom.Element e, String tree) /*-{
		e.postMessage(tree);
	}-*/;

	public class Selection {
		int x;
		int y;
		int w;
		int h;
	};

	public char charAt(int x, int y) {
		if (y < val.size()) {
			Sequence seq = val.get(y);
			if (x >= seq.getStart() && x < seq.getEnd()) {
				return seq.charAt(x - seq.getStart());
			}
		}
		return ' ';
	}

	public void setCharAt(int x, int y, char c) {
		if (y < val.size()) {
			Sequence seq = val.get(y);
			if (x >= seq.getStart() && x < seq.getStart() + seq.getLength()) {
				seq.setCharAt(x - seq.getStart(), c);
			}
		}
	}

	public int getVisibleHeight() {
		return canvas.getCoordinateSpaceHeight()
				- (columnHeight + scrollBarHeight);
	}

	public String exportString64() {
		StringBuilder out = new StringBuilder();
		if (selectionList.isEmpty()) {
			for (Sequence seq : val) {
				String seqname = seq.getName();

				String res = ">" + seqname + "\n";
				for (int i = 0; i < seq.getLength(); i += 70) {
					for (int k = i; k < Math.min(seq.getLength(), i + 70); k++) {
						res += seq.charAt(k);
					}
					res += '\n';
				}

				out.append(encode(res));
			}
		}
		String encstr = out.toString();

		return encstr;
	}

	public String exportString() {
		StringBuilder out = new StringBuilder();
		if (selectionList.isEmpty()) {
			for (Sequence seq : val) {
				String seqname = seq.getName();
				out.append(">" + seqname + "\n");
				for (int i = 0; i < seq.getLength(); i += 70) {
					for (int k = i; k < Math.min(seq.getLength(), i + 70); k++) {
						out.append(seq.charAt(k));
					}
					out.append('\n');
					// out.append( seq.getSubarray(i, Math.min(seq.length(),
					// i+70) )+"\n" );
				}
				// console( "doing seq: "+seqname+"  "+out.length() );
			}
		}
		/*
		 * for( int i = 0; i < data.getNumberOfRows(); i++ ) { String str =
		 * data.getValueString(i, 0); out += ">" + str + "\n";
		 * 
		 * Sequence seq = val.get(i); //String seqstr = seq.seq; for( int k =
		 * seq.seqstart; k < seq.seqstop; k+=60 ) { out += content.substring( k,
		 * Math.min(seq.length(), k+60) ) + "\n"; } }
		 */
		String encstr = out.toString();

		return encstr;
	}

	boolean append = false;
	List<Selection> selectionList = new ArrayList<Selection>();
	Canvas buffer;
	boolean basecolors = false;
	Map<Character, String> ccol = new HashMap<Character, String>();
	Map<Character, Integer> mccol = new HashMap<Character, Integer>();
	JsArrayInteger sortind = null;
	boolean mousedown = false;
	boolean scrollx = false;
	boolean scrolly = false;
	int mousex;
	int mousey;
	boolean dragging = false;
	boolean inRuler = false;
	int xselloc = 0;
	int xsellen = 0;

	public native void line( String name, Float64ArrayNative fa, Element popup, int width, int height, String ax, String ay ) /*-{
		var arr = [ [ax, ay] ];
		//var fa = new Float64Array( arraybuf );
		for( i = 0; i < fa.length; i++ ) {
			arr[i+1] = [i, fa[i]];
		}
        var data = $wnd.google.visualization.arrayToDataTable(arr);
        var options = {};
        options['title'] = name;
        options['width'] = width;
        options['height'] = height;
		
        var chart = new $wnd.google.visualization.LineChart( popup );
        chart.draw(data, options);
	}-*/;
	
	public void handleMessage() {
		elemental.dom.Element e = Browser.getDocument().getElementById("listener");
		e.addEventListener("message", new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				MessageEvent me = (MessageEvent) evt;
				Browser.getWindow().getConsole().log("jelp");
				treestr = (String) me.getData();
				myPopup = Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback=webfasta","TreeDraw");
				/*
				 * if( myPopup != null && treestr != null ) {
				 * myPopup.postMessage( treestr, "*" ); treestr = null; }
				 */
			}
		}, true);
	}

	public void deleteSelected() {
		tcontext.clearRect(0, 0, tcontext.getCanvas().getWidth(), tcontext
				.getCanvas().getHeight());
		context.clearRect(0, 0, context.getCanvas().getWidth(), context
				.getCanvas().getHeight());

		List<Sequence> delseq = new ArrayList<Sequence>();
		for (int i = val.size() - 1; i >= 0; i--) {
			Sequence tseq = val.get(i);
			if (tseq.isSelected())
				delseq.add(tseq);
		}
		val.removeAll(delseq);

		draw(xstart, ystart);
	}
	
	public native String emuscle(  String fstr ) /*-{
		//$wnd.console.log('bleh' + fstr);
		
		var buf = new ArrayBuffer(fstr.length); // 2 bytes for each char
		var bufView = new Uint8Array(buf);
		for (var i=0; i<fstr.length; i++) {
		   bufView[i] = fstr.charCodeAt(i);
		}
		
		//$wnd.console.log('bleh2' + bufView[0] + '  ' + bufView[1]);
		
		var mbuf = $wnd.Module._malloc( buf.byteLength );
		$wnd.Module.HEAPU8.set( bufView, mbuf );
		var ptr = $wnd.Module.ccall( 'check', 'number', ['number', 'number'], [mbuf, buf.byteLength] );
		var str = $wnd.Pointer_stringify( ptr );
		return str;
	}-*/;

	public native String fetchTreeSel(elemental.html.Window myPopup) /*-{
		var ret = "erm";
		try {
			ret = myPopup.fetchSel();
		} catch (e) {
			$wnd.console.log(e);
		}
		return ret;
	}-*/;

	/*
	 * public void selectSequence( Sequence s ) { selset.add( val.indexOf( s )
	 * ); s.setSelected( true ); }
	 */

	Sequence dullseq = null;

	elemental.html.Window myPopup = null;
	String treestr = null;

	public void onModuleLoad() {
		handleMessage();

		elemental.html.Window wnd = Browser.getWindow();
		wnd.addEventListener("message", new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				MessageEvent me = (MessageEvent) evt;
				String dstr = (String) me.getData();

				Console console = Browser.getWindow().getConsole();
				console.log("okbleh");
				console.log(dstr);

				if (dstr.equals("ready")) {
					elemental.html.Window source = myPopup;// me.getSource();
					console.log(dstr + " " + source);
					console.log(source.getName());

					// evt.
					if (treestr != null) {
						source.postMessage(treestr, "*");
						treestr = null;
					}
				} else if (dstr.startsWith("propagate")) {
					int fi = dstr.indexOf('{');
					int li = dstr.indexOf('}');
					String substr = dstr.substring(fi + 1, li);
					String[] split = substr.split(",");
					Set<String> splitset = new HashSet<String>(Arrays
							.asList(split));
					for (Sequence seq : val) {
						SequenceOld so = (SequenceOld) seq;
						String name = seq.getName();
						// console.log("trying "+name);
						if (splitset.contains(name))
							so.setSelected(true);
					}
					draw(xstart, ystart);
				}
			}
		}, true);

		ccol.put('A', "#ff0000");
		ccol.put('a', "#ff0000");
		ccol.put('G', "#00ff00");
		ccol.put('g', "#00ff00");
		ccol.put('T', "#0000ff");
		ccol.put('t', "#0000ff");
		ccol.put('U', "#00ffff");
		ccol.put('u', "#00ffff");
		ccol.put('C', "#ffff00");
		ccol.put('c', "#ffff00");

		buffer = Canvas.createIfSupported();
		buffer.setCoordinateSpaceWidth(ccol.size() * basewidth);
		buffer.setCoordinateSpaceHeight(unitheight);
		Context2d ctx = buffer.getContext2d();
		int i = 0;
		for (char c : ccol.keySet()) {
			String col = ccol.get(c);
			ctx.setFillStyle(col);
			ctx.fillRect(i * basewidth, 0, basewidth, unitheight);
			ctx.setFillStyle("#000000");
			ctx.fillText(c + "", i * basewidth, unitheight - 3.0);
			mccol.put(c, i * basewidth);

			i++;
		}

		int ww = Window.getClientWidth();
		int wh = Window.getClientHeight();
		// HorizontalPanel hpanel = new HorizontalPanel();
		// hpanel.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		// hpanel.setWidth("100%");
		// int height = Math.max( 1000, wh );
		// int height = RootPanel.get().getOffsetHeight();
		// hpanel.setHeight(Math.max(1000, height)+"px");

		FormPanel fp = new FormPanel();
		fp.setSize("100%", "100%");
		final FileUpload file = new FileUpload();
		file.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				JavaScriptObject thefile = handleFiles(file.getElement());
				if (thefile != null) {
					fileRead(thefile);
				}
			}
		});
		fp.add(file);
		Style st = file.getElement().getStyle();
		st.setVisibility(Visibility.HIDDEN);

		MenuBar popup = new MenuBar(true);
		popup.addItem("Open", new Command() {
			@Override
			public void execute() {
				append = false;
				click(file.getElement());

				/*
				 * try { Factory f = Factory.getInstance(); if( f != null ) {
				 * console("11"); Desktop dt = f.createDesktop();
				 * console("before"); dt.openFiles(new OpenFilesHandler() {
				 * public void onOpenFiles(OpenFilesEvent oevent) {
				 * console("erm");
				 * 
				 * File[] files = oevent.getFiles(); File file = files[0];
				 * 
				 * console("toff"); Blob data = file.getBlob();
				 * 
				 * console( "" + data.getLength() );
				 * 
				 * byte[] bb = data.getBytes(); console("toff");
				 * //Webfasta.this.content = new String( bb ); fileLoaded( new
				 * String(bb), 0 ); } }, true); console("after"); } } catch
				 * (Exception ex){ Window.alert(ex.toString()); }
				 */

				// stuff( 0 );

				// db.hide();

				/*
				 * submit.addClickHandler( new ClickHandler() {
				 * 
				 * @Override public void onClick(ClickEvent event) {
				 * form.submit(); db.hide(); } });
				 */
				/*
				 * form.addSubmitHandler( new SubmitHandler() {
				 * 
				 * @Override public void onSubmit(SubmitEvent event) { //File f;
				 * try { tryOut( file.getElement() ); //table = new Table( data,
				 * createTableOptions() ); //table.setWidth("100%");
				 * //table.setHeight("100%");
				 * 
				 * //slp.add
				 * 
				 * //String str = new String( bb ); //System.err.println( str );
				 * //GWT.log( filename ); } catch( Exception e ) { GWT.log(
				 * "ex", e ); //e.printStackTrace(); } } });
				 */
				/*
				 * form.addSubmitCompleteHandler( new SubmitCompleteHandler() {
				 * 
				 * @Override public void onSubmitComplete(SubmitCompleteEvent
				 * event) { Window.alert("erm"); } });
				 */

				// vp.add( file );
				// vp.add( submit );
				// db.add( form );
				// db.setAutoHideEnabled( true );
				// db.center();
			}
		});
		popup.addItem("Append", new Command() {
			@Override
			public void execute() {
				append = true;
				click(file.getElement());
				// stuff( 1 );
			}
		});
		popup.addItem("Export", new Command() {
			@Override
			public void execute() {
				// DialogBox db = new DialogBox();
				// Anchor a = new Anchor("okok");

				final Console console = Browser.getWindow().getConsole();
				final String str = exportString();
				final elemental.html.Window wnd = Browser.getWindow();

				// console.log( str.length() );
				final elemental.html.Blob bb = createBlob(str);

				// final Object[] create = {"create", true};
				// String[] create = {"create", "true"};

				boolean fail = false;
				try {
					String objurl = createObjectURL(bb);
					wnd.open(objurl, "export.fasta");
					/*
					 * wnd.webkitRequestFileSystem(elemental.html.Window.TEMPORARY
					 * , 2*str.length(), new FileSystemCallback() {
					 * 
					 * @Override public boolean
					 * onFileSystemCallback(DOMFileSystem fileSystem) {
					 * console.log("in filesystem");
					 * fileSystem.getRoot().getFile("export.fasta",
					 * createFlags(), new EntryCallback() {
					 * 
					 * @Override public boolean onEntryCallback(Entry entry) {
					 * console.log("in file"); final FileEntry fe =
					 * (FileEntry)entry; fe.createWriter( new
					 * FileWriterCallback() {
					 * 
					 * @Override public boolean onFileWriterCallback(FileWriter
					 * fileWriter) { console.log("in write");
					 * 
					 * fileWriter.truncate( 1 ); //bb.getSize() );
					 * fileWriter.write( bb );
					 * 
					 * wnd.open( fe.toURL(), "export.fasta" ); return true; }
					 * }); return true; } }); return true; } });
					 */
				} catch (Exception e) {
					fail = true;
				}
				// Base64Utils.toBase64( encstr.getBytes() );
				// String bstr = encode( encstr );
				// String dataurl =
				// "data:text/plain;fileName=export.fasta;base64,"+str;
				// a.setHref( );
				// db.add( a );
				// db.center();
				if (fail) {
					String dataurl = "data:text/plain;fileName=export.fasta;base64,"
							+ exportString64();
					Window.open(dataurl, "export.fasta", "_blank");
				}
			}
		});
		popup.addItem("Export phylip", new Command() {
			@Override
			public void execute() {
				String out = Sequence.getPhylip(val, false);
				String bstr = encode(out);
				String dataurl = "data:text/plain;fileName=export.fasta;base64,"
						+ bstr;

				Window.open(dataurl, "export.phy", "");
			}
		});
		MenuBar epopup = new MenuBar(true);
		epopup.addItem("NACLMuscle (C)", new Command() {
			@Override
			public void execute() {
				elemental.dom.Element e = Browser.getDocument().getElementById("muscle");
				String fasta = exportString();
				postMessage(e, fasta);
				// Browser.getWindow().getConsole().log( e );
				// elemental.html.EmbedElement ee =
				// (elemental.html.EmbedElement)e;

				// myPopup =
				// Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback=webfasta",
				// "TreeDraw");
			}
		});
		epopup.addItem("EMuscle", new Command() {
			@Override
			public void execute() {
				String fasta = exportString();
				String ret = emuscle( fasta );
				
				Int8Array ia8 = Int8ArrayNative.create( ret.length() );
				for (int i=0; i<ret.length(); i++) {
				   ia8.set(i, ret.charAt(i));
				}
				
				shorten( ia8 );
				//Browser.getWindow().getConsole().log( ret );
				// elemental.html.EmbedElement ee =
				// (elemental.html.EmbedElement)e;

				// myPopup =
				// Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback=webfasta",
				// "TreeDraw");
			}
		});
		epopup.addSeparator();
		epopup.addItem("Delete selection", new Command() {
			@Override
			public void execute() {
				if (xsellen > 0) {
					for (Sequence s : val) {
						s.delete(xselloc, xselloc + xsellen);
					}
					resetMax();
					draw(xstart, ystart);
				}
			}
		});
		epopup.addItem("Clear sites with gaps", new Command() {
			@Override
			public void execute() {
				for (int x = 0; x < max; x++) {
					int y;
					for (y = 0; y < val.size(); y++) {
						char c = charAt(x, y);
						if (c == '-') {
							break;
						}
					}

					if (y < val.size()) {
						for (y = 0; y < val.size(); y++) {
							setCharAt(x, y, '-');
						}
					}
				}
				resetMax();
				draw(xstart, ystart);
			}
		});
		epopup.addItem("Remove gaps", new Command() {
			@Override
			public void execute() {
				for( Sequence seq : val ) {
					if( seq.isSelected() ) {
						seq.removeGaps();
					}
				}
				resetMax();
				draw(xstart, ystart);
			}
		});
		epopup.addItem("Remove all gaps", new Command() {
			@Override
			public void execute() {
				for( Sequence seq : val ) {
					if( seq.isSelected() ) {
						seq.removeAllGaps();
					}
				}
				resetMax();
				draw(xstart, ystart);
			}
		});
		epopup.addItem("Reverse", new Command() {
			@Override
			public void execute() {
				for (Sequence seq : val) {
					if (seq.isSelected()) {
						seq.reverse();
					}
				}
				draw(xstart, ystart);
			}
		});
		epopup.addItem("Complement", new Command() {
			@Override
			public void execute() {
				for (Sequence seq : val) {
					if (seq.isSelected()) {
						seq.complement();
					}
				}
				draw(xstart, ystart);
			}
		});
		epopup.addItem("UT replace", new Command() {
			@Override
			public void execute() {
				for (Sequence seq : val) {
					if (seq.isSelected()) {
						seq.utReplace();
					}
				}
				draw(xstart, ystart);
			}
		});
		epopup.addItem("Case swap", new Command() {
			@Override
			public void execute() {
				for (Sequence seq : val) {
					if (seq.isSelected()) {
						seq.caseSwap();
					}
				}
				draw(xstart, ystart);
			}
		});
		epopup.addItem("Uppercase", new Command() {
			@Override
			public void execute() {
				for (Sequence seq : val) {
					if (seq.isSelected()) {
						seq.upperCase();
					}
				}
				draw(xstart, ystart);
			}
		});
		epopup.addSeparator();
		epopup.addItem("Trim names", new Command() {
			@Override
			public void execute() {
				for (Sequence seq : val) {
					if (seq.isSelected()) {
						String name = seq.getName();
						int lu = name.lastIndexOf('_');
						int ls = name.lastIndexOf(' ');
						int lc = name.lastIndexOf(';');
						int lb = name.lastIndexOf('[');
						int m1 = Math.max(lu, ls);
						int m2 = Math.max(lc, lb);
						int m = Math.max(m1, m2);
						if (m != -1) {
							seq.setName(name.substring(0, m));
						}
					}
				}
				draw(xstart, ystart);
			}
		});
		epopup.addSeparator();
		epopup.addItem("Select All", new Command() {
			@Override
			public void execute() {
				// JsArray<Selection> jsel =
				// (JsArray<Selection>)JsArray.createArray();
				// for( int i = 0; i < data.getNumberOfRows(); i++ ) jsel.push(
				// Selection.createRowSelection(i) );
				// JsArray<Selection>
				/*
				 * JsArray<Selection> all = new JsArray<Selection>() {
				 * 
				 * };
				 */
				// table.setSelections( jsel );
				for (Sequence seq : val) {
					seq.setSelected(true);
				}
				draw(xstart, ystart);
			}
		});
		epopup.addItem("Invert selection", new Command() {
			@Override
			public void execute() {
				for (Sequence seq : val) {
					seq.setSelected(!seq.isSelected());
				}
				draw(xstart, ystart);
			}
		});
		epopup.addItem("Delete sequences", new Command() {
			@Override
			public void execute() {
				/*
				 * JsArray<Selection> ls = table.getSelections(); List<Integer>
				 * slist = new ArrayList<Integer>(); //while( ls.length() > 0 )
				 * { for( int i = 0; i < ls.length(); i++ ) { Selection sel =
				 * ls.get(i); slist.add( sel.getRow() ); //ls =
				 * table.getSelections(); } Collections.sort( slist,
				 * Collections.reverseOrder() );
				 * 
				 * for( int i : slist ) { //Arrays. if( i < val.size() )
				 * val.remove( i ); data.removeRow( i ); } table.draw( data );
				 */

				deleteSelected();
			}
		});

		canvas = Canvas.createIfSupported();
		canvas.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				mousex = event.getX();
				mousey = event.getY();

				if (mousey < columnHeight) {
					sortcol = 2 + (mousex + xstart) / basewidth;
					Collections.sort(val);
					draw(xstart, ystart);
				}
			}
		});
		canvas.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				mousedown = true;

				mousex = event.getX();
				mousey = event.getY();

				int cw = canvas.getCoordinateSpaceWidth();
				int ch = canvas.getCoordinateSpaceHeight();
				if (mousex > cw - scrollBarWidth
						|| mousey > ch - scrollBarHeight) {
					// int xstart = Webfasta.this.xstart;
					if (mousey > ch - scrollBarHeight) {
						scrollx = true;

						double xmin1 = max * basewidth - (cw - scrollBarWidth);
						double xmin2 = (xmin1 * mousex) / (cw - scrollBarWidth);
						xstart = (int) Math.max(0.0, Math.min(xmin1, xmin2));
					}

					// int ystart = Webfasta.this.ystart;
					if (mousex > cw - scrollBarWidth) {
						scrolly = true;

						double ymin1 = val.size() * unitheight
								- (ch - scrollBarHeight - columnHeight);
						double ymin2 = (ymin1 * (mousey - unitheight))
								/ (ch - scrollBarHeight - columnHeight);
						ystart = (int) Math.max(0.0, Math.min(ymin1, ymin2));
					}

					draw(xstart, ystart);
				} else if (mousey < columnHeight) {
					inRuler = true;
					
					if( event.isShiftKeyDown() && xsellen > 0 ) {
						//xselloc = (mousex + xstart) / basewidth;
						dragging = true;
						xsellen = (mousex + xstart) / basewidth - xselloc;
						draw(xstart, ystart);
					}
				}
			}
		});
		canvas.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (mousedown) {
					dragging = true;

					int x = event.getX();
					int y = event.getY();

					// Browser.getWindow().getConsole().log("what " + inRuler);

					double cw = (double) canvas.getCoordinateSpaceWidth();
					double ch = (double) canvas.getCoordinateSpaceHeight();
					if (scrollx || scrolly) {
						if (scrollx) {
							double xmin1 = max * basewidth
									- (cw - scrollBarWidth);
							double xmin2 = (xmin1 * x) / (cw - scrollBarWidth);
							xstart = (int) Math.max(0.0, Math.min(xmin1, xmin2));
						}

						if (scrolly) {
							double ymin1 = val.size() * unitheight
									- (ch - scrollBarHeight - columnHeight);
							double ymin2 = (ymin1 * (y - unitheight))
									/ (ch - scrollBarHeight - columnHeight);
							ystart = (int) Math.max(0.0, Math.min(ymin1, ymin2));
						}

						draw(xstart, ystart);
					} else if (inRuler) {
						// Browser.getWindow().getConsole().log("what");
						xselloc = (mousex + xstart) / basewidth;
						xsellen = (x + xstart) / basewidth - xselloc;

						draw(xstart, ystart);
					} else {
						double xmin1 = max * basewidth - (cw - scrollBarWidth);
						double xmin2 = Webfasta.this.xstart + (mousex - x);
						int xstart = (int) Math.max(0.0, Math.min(xmin1, xmin2));

						if (xmin2 > xmin1) {
							mousex = mousex + (int) (xmin1 - xmin2);
						}

						double ymin1 = val.size() * unitheight
								- (ch - scrollBarHeight - columnHeight);
						double ymin2 = Webfasta.this.ystart + (mousey - y);
						int ystart = (int) Math.max(0.0, Math.min(ymin1, ymin2));

						if (ymin2 > ymin1) {
							mousey = mousey + (int) (ymin1 - ymin2);
						}

						draw(xstart, ystart);
					}
				}
			}
		});
		canvas.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				int x = event.getX();
				int y = event.getY();

				if (scrollx || scrolly) {
					if (scrollx) {
						double cw = (double) canvas.getCoordinateSpaceWidth();
						double xmin1 = max * basewidth - (cw - scrollBarWidth);
						double xmin2 = (xmin1 * x) / (cw - scrollBarWidth);
						xstart = (int) Math.max(0.0, Math.min(xmin1, xmin2));
					}

					if (scrolly) {
						double ch = (double) canvas.getCoordinateSpaceHeight();
						double ymin1 = val.size() * unitheight
								- (ch - scrollBarHeight - columnHeight);
						double ymin2 = (ymin1 * (y - unitheight))
								/ (ch - scrollBarHeight - columnHeight);
						ystart = (int) Math.max(0.0, Math.min(ymin1, ymin2));
					}
				} else if (!inRuler) {
					xstart = Math.max(0,
							Math.min(max * basewidth, xstart + (mousex - x)));
					ystart = Math.max(
							0,
							Math.min(val.size() * unitheight, ystart
									+ (mousey - y)));
				}

				inRuler = false;

				mousedown = false;
				scrollx = false;
				scrolly = false;

				if (!dragging && y < columnHeight) {
					sortcol = 2 + (x + xstart) / basewidth;
					Collections.sort(val);
					// draw( xstart, ystart );
				}
				dragging = false;

				draw(xstart, ystart);
			}
		});
		canvas.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				mousedown = false;
				scrollx = false;
				scrolly = false;
			}
		});
		canvas.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				if (event.isShiftKeyDown()) {

				} else {
					ystart = Math.max(
							0,
							Math.min(getSequenceNumber() * unitheight
									- getVisibleHeight(),
									ystart + event.getDeltaY() * unitheight));
				}
				// console.log( xstart + "  " + ystart );
				draw(xstart, ystart);
			}
		});

		canvas.addKeyDownHandler(new KeyDownHandler() {
			boolean running;

			@Override
			public void onKeyDown(KeyDownEvent event) {
				int kc = event.getNativeKeyCode();
				if (kc == KeyCodes.KEY_DOWN) {
					ystart = Math.min(
							Math.max(
									0,
									val.size() * unitheight
											- canvas.getOffsetHeight()), ystart
									+ unitheight);
				} else if (kc == KeyCodes.KEY_UP) {
					ystart = Math.max(0, ystart - unitheight);
				} else if (kc == KeyCodes.KEY_LEFT) {
					xstart = Math.max(0, xstart - 10);
				} else if (kc == KeyCodes.KEY_RIGHT) {
					xstart = Math.min(
							Math.max(0,
									max * basewidth - canvas.getOffsetWidth()),
							xstart + 10);
				}
				if (kc == KeyCodes.KEY_PAGEDOWN) {
					ystart = Math.min(
							Math.max(
									0,
									val.size() * unitheight
											- canvas.getOffsetHeight()), ystart
									+ canvas.getOffsetHeight());
				} else if (kc == KeyCodes.KEY_PAGEUP) {
					ystart = Math.max(0, ystart - canvas.getOffsetHeight());
				} else if (kc == KeyCodes.KEY_HOME) {
					ystart = 0;
				} else if (kc == KeyCodes.KEY_END) {
					ystart = Math.max(0,
							val.size() * unitheight - canvas.getOffsetHeight());
				}

				if (!running) {
					running = true;
					draw(xstart, ystart);
					Timer t = new Timer() {
						public void run() {
							running = false;
						}
					};
					t.schedule(10);
				}
			}
		});

		MenuBar vpopup = new MenuBar(true);
		vpopup.addItem("Goto", new Command() {
			@Override
			public void execute() {
				DialogBox db = new DialogBox();
				db.setAutoHideEnabled(true);
				db.setText("Goto");

				final IntegerBox ib = new IntegerBox();
				ib.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						xstart = ib.getValue();
						draw(xstart, ystart);
					}
				});
				VerticalPanel vp = new VerticalPanel();
				vp.add(ib);

				HorizontalPanel hp = new HorizontalPanel();
				Button wlb = new Button("<<");
				hp.add(wlb);
				Button slb = new Button("<");
				hp.add(slb);
				Button srb = new Button(">");
				hp.add(srb);
				Button wrb = new Button(">>");
				hp.add(wrb);

				vp.add(hp);

				db.add(vp);
				db.center();
			}
		});
		vpopup.addItem("Set offset", new Command() {
			@Override
			public void execute() {
				DialogBox db = new DialogBox();
				db.setAutoHideEnabled(true);
				db.setText("Offset");

				final IntegerBox ib = new IntegerBox();
				ib.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						for( Sequence seq : val ) {
							if( seq.isSelected() ) {
								seq.setStart(ib.getValue());
							}
						}
						draw(xstart, ystart);
					}
				});
				VerticalPanel vp = new VerticalPanel();
				vp.add(ib);

				HorizontalPanel hp = new HorizontalPanel();
				/*Button wlb = new Button("<<");
				hp.add(wlb);
				Button slb = new Button("<");
				hp.add(slb);
				Button srb = new Button(">");
				hp.add(srb);
				Button wrb = new Button(">>");
				hp.add(wrb);*/

				vp.add(hp);

				db.add(vp);
				db.center();
			}
		});
		vpopup.addItem("Base colors", new Command() {
			@Override
			public void execute() {
				basecolors = !basecolors;
				draw(xstart, ystart);
			}
		});
		vpopup.addSeparator();
		vpopup.addItem("Pairwise-align wo-gaps", new Command() {
			@Override
			public void execute() {
				Sequence seq1 = null;
				Sequence seq2 = null;
				
				for( Sequence seq : val ) {
					if( seq1 == null ) seq1 = seq;
					else {
						seq2 = seq;
						break;
					}
				}				
				if( seq1 != null && seq2 != null ) {
					int max = 0;
					int i1 = 0;
					int i2 = 0;
					
					console( "starting" );
					for( int i = 0; i < seq1.getLength()+seq2.getLength()-1; i++ ) {
						int k1 = Math.max( 0, i - seq2.getLength() );
						int k2 = Math.max( 0, seq2.getLength() - i );
						int len = Math.min( seq2.getLength()-k2, seq1.getLength()-k1 );
						
						int count = 0;
						for( int x = 0; x < len; x++ ) {
							char c1 = seq1.charAt(x+k1);
							char c2 = seq2.charAt(x+k2);
							
							if( c1 == c2 ) count++;
							else {
								if( count > max ) {
									max = count;
									i1 = k1;
									i2 = k2;
								}
								
								count = 0;
							}
						}
						
						if( count > max ) {
							max = count;
							i1 = k1;
							i2 = k2;
						}
					}
					console( i1 + "  " + i2 );
				}
				
				draw(xstart, ystart);
			}
		});
		vpopup.addSeparator();
		vpopup.addItem("Clear point-mutations", new Command() {
			@Override
			public void execute() {
				Map<Character,Integer>	shanmap = new HashMap<Character,Integer>(); 
				for( int x = getMin(); x < getMax(); x++ ) {
					shanmap.clear();
					int total = val.size();
					for( int y = 0; y < total; y++ ) {
						char c = charAt(x, y);
						int val = 0;
						if( shanmap.containsKey(c) ) val = shanmap.get(c);
						shanmap.put( c, val+1 );
					}
					
					for( char c : shanmap.keySet() ) {
						int count = shanmap.get( c );
						if( count == 1 ) {
							char maxc = ' ';
							int lastcount = 0;
							for( char subc : shanmap.keySet() ) {
								int subcount = shanmap.get( subc );
								if( subcount > lastcount ) maxc = subc;
							}
							for( int y = 0; y < total; y++ ) {
								setCharAt(x, y, maxc);
							}
						}
					}
					
					/*double res = 0.0;
					for( char c : shanmap.keySet() ) {
						int val = shanmap.get(c);
						double p = (double)val/(double)total;
						res -= p*Math.log(p)/Math.log(2.0);
					}
					d[x-getMin()] = res;*/
				}
			}
		});
		vpopup.addItem("Shannon threshold", new Command() {
			@Override
			public void execute() {
				final double[] d = new double[ getDiff() ];
				Map<Character,Integer>	shanmap = new HashMap<Character,Integer>(); 
				for( int x = getMin(); x < getMax(); x++ ) {
					shanmap.clear();
					int total = val.size();
					for( int y = 0; y < total; y++ ) {
						char c = charAt(x, y);
						int val = 0;
						if( shanmap.containsKey(c) ) val = shanmap.get(c);
						shanmap.put( c, val+1 );
					}
					double res = 0.0;
					for( char c : shanmap.keySet() ) {
						int val = shanmap.get(c);
						double p = (double)val/(double)total;
						res -= p*Math.log(p)/Math.log(2.0);
					}
					d[x-getMin()] = res;
				}
				
				Browser.getWindow().getConsole().log("ermerm");
				
				final CheckBox			cb = new CheckBox("Filter blocks");
				final IntegerBox		sp = new IntegerBox();
				sp.setValue( 10 );
				//final JSpinner 	sp = new JSpinner( new SpinnerNumberModel(10, 2, 100, 1) );
				sp.setEnabled( false );
				cb.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						sp.setEnabled( event.getValue() );	
					}
				});
				
				final DoubleBox	dbox = new DoubleBox();
				dbox.setValue( 0.1 );
				final CheckBox	cbox = new CheckBox("Inverted");
				
				DialogBox db = new DialogBox( true, true );
				VerticalPanel	vp = new VerticalPanel();
				vp.setSize(400+"px", 50+"px");
				vp.add( cb );
				vp.add( sp );
				db.add( vp );
				
				vp.add( cbox );
				vp.add( dbox );
				
				Browser.getWindow().getConsole().log("mu");
				db.center();
				//Object[] message = new Object[] { cb, sp };
				//JOptionPane.showMessageDialog(parentApplet, message);
				
				db.addCloseHandler( new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						double[] td = d;
						if( cb.getValue() ) {
							int val = (Integer)sp.getValue();
							double[] old = d;
							double sum = 0.0;
							for( int k = 0; k < val; k++ ) {
								sum += old[k];
							}
							td = new double[ old.length-val ];
							for( int i = 0; i < d.length; i++ ) {
								td[i] = sum/(double)val;
								sum += -td[i]+td[i+val];
							}
						}
						double dval = dbox.getValue();
						boolean inv = cbox.getValue();
						for( int x = 0; x < td.length; x++ ) {
							if( (!inv && td[x] < dval) || (inv && td[x] > dval) ) {
								for( int y = 0; y < val.size(); y++ ) {
									setCharAt(x, y, '-');
								}
							}
						}
						
						/*Float64ArrayNative fa = Float64ArrayNative.create(td.length);
						for( int i = 0; i < td.length; i++ ) {
							fa.set(i, td[i]);
						}
						PopupPanel pp = new PopupPanel(true);
						line( "line", fa, pp.getElement(), 640, 480, "mu", "ma" );
						pp.center();*/
						
						resetMax();
						draw(xstart, ystart);
					}
				});
			}
		});
		vpopup.addItem("Draw Shannon", new Command() {
			@Override
			public void execute() {
				String command = "command";
				final double[] d = new double[ getDiff() ];
				Map<Character,Integer>	shanmap = new HashMap<Character,Integer>(); 
				for( int x = getMin(); x < getMax(); x++ ) {
					shanmap.clear();
					int total = val.size();
					for( int y = 0; y < total; y++ ) {
						char c = charAt(x, y);
						int val = 0;
						if( shanmap.containsKey(c) ) val = shanmap.get(c);
						shanmap.put( c, val+1 );
					}
					double res = 0.0;
					for( char c : shanmap.keySet() ) {
						int val = shanmap.get(c);
						double p = (double)val/(double)total;
						res -= p*Math.log(p)/Math.log(2.0);
					}
					d[x-getMin()] = res;
				}
				
				final CheckBox			cb = new CheckBox("Filter blocks");
				final IntegerBox		sp = new IntegerBox();
				sp.setValue( 10 );
				//final JSpinner 	sp = new JSpinner( new SpinnerNumberModel(10, 2, 100, 1) );
				sp.setEnabled( false );
				cb.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						sp.setEnabled( event.getValue() );	
					}
				});
				DialogBox db = new DialogBox( true, true );
				VerticalPanel	vp = new VerticalPanel();
				vp.setSize(400+"px", 50+"px");
				vp.add( cb );
				vp.add( sp );
				db.add( vp );
				db.center();
				//Object[] message = new Object[] { cb, sp };
				//JOptionPane.showMessageDialog(parentApplet, message);
				
				db.addCloseHandler( new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						double[] td = d;
						if( cb.getValue() ) {
							int val = (Integer)sp.getValue();
							double[] old = d;
							double sum = 0.0;
							for( int k = 0; k < val; k++ ) {
								sum += old[k];
							}
							td = new double[ old.length-val ];
							for( int i = 0; i < d.length; i++ ) {
								td[i] = sum/(double)val;
								sum += -td[i]+td[i+val];
							}
						}
						
						Float64ArrayNative fa = Float64ArrayNative.create(td.length);
						for( int i = 0; i < td.length; i++ ) {
							fa.set(i, td[i]);
						}
						PopupPanel pp = new PopupPanel(true);
						line( "line", fa, pp.getElement(), 640, 480, "mu", "ma" );
						pp.center();
					}
				});
			}
		});
		vpopup.addSeparator();
		vpopup.addItem("Sort by selection", new Command() {
			@Override
			public void execute() {
				sortcol = -1;
				Collections.sort(val);
				draw(xstart, ystart);
			}
		});
		vpopup.addItem("Reverse sort order", new Command() {
			@Override
			public void execute() {
				for (int i = 0; i < val.size() / 2; i++) {
					Sequence seq = val.get(i);
					val.set(i, val.get(val.size() - 1 - i));
					val.set(val.size() - 1 - i, seq);
				}
				draw(xstart, ystart);
			}
		});
		MenuBar tpopup = new MenuBar(true);
		tpopup.addItem("NJTree (JavaScript)", new Command() {
			@Override
			public void execute() {
				treestr = exportString();
				myPopup = Browser
						.getWindow()
						.open("http://webconnectron.appspot.com/Treedraw.html?callback=webfasta",
								"TreeDraw");
			}
		});
		tpopup.addItem("NACLTree (C)", new Command() {
			@Override
			public void execute() {
				elemental.dom.Element e = Browser.getDocument().getElementById("fasttree");
				String fasta = "f" + exportString();
				postMessage(e, fasta);
				// Browser.getWindow().getConsole().log( e );
				// elemental.html.EmbedElement ee =
				// (elemental.html.EmbedElement)e;

				// myPopup =
				// Browser.getWindow().open("http://webconnectron.appspot.com/Treedraw.html?callback=webfasta",
				// "TreeDraw");
			}
		});
		tpopup.addSeparator();
		tpopup.addItem("Propagate selection", new Command() {
			@Override
			public void execute() {
				String sel = "";
				for (Sequence seq : val) {
					if (seq.isSelected()) {
						if (sel.length() == 0)
							sel += seq.getName();
						else
							sel += "," + seq.getName();
					}
				}
				if (myPopup != null) {
					String propstr = "propogate{" + sel + "}";
					Browser.getWindow().getConsole().log(propstr);

					myPopup.postMessage(propstr, "*");
				}
			}
		});
		tpopup.addItem("Fetch tree selection", new Command() {
			@Override
			public void execute() {
				if (myPopup != null) {
					myPopup.postMessage("fetchsel", "*");
				}
			}
		});
		MenuBar hpopup = new MenuBar(true);
		hpopup.addItem("About", new Command() {
			@Override
			public void execute() {
				DialogBox db = new DialogBox();
				db.setText("WebFasta");
				db.setAutoHideEnabled(true);
				// SimplePanel simplepanel = new SimplePanel();
				// Text simplepanel = new Text();
				// Panel
				HTMLPanel html = new HTMLPanel("WebFasta 1.0<br>Sigmasoft LTD.");
				db.add(html);
				// db.setHTML("WebFasta 1.0<br>Sigmasoft LTD.");
				db.center();
			}
		});

		MenuBar menubar = new MenuBar();
		menubar.addItem("File", popup);
		menubar.addItem("Edit", epopup);
		menubar.addItem("View", vpopup);
		menubar.addItem("Tree", tpopup);
		menubar.addItem("Help", hpopup);
		VerticalPanel vpanel = new VerticalPanel();
		vpanel.setWidth("100%");
		// vpanel.setHeight(height+"px");
		vpanel.add(menubar);

		final SplitLayoutPanel slp = new SplitLayoutPanel();
		// slp.setWidth("100%");
		slp.setSize((ww - 160) + "px", (wh - 40) + "px");
		vpanel.add(slp);
		vpanel.add(fp);

		canvas.setWidth("100%");
		canvas.setHeight("100%");
		context = canvas.getContext2d();

		canvas.addDragHandler(new DragHandler() {
			@Override
			public void onDrag(DragEvent event) {

			}
		});
		canvas.addDragEndHandler(new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {

			}
		});
		canvas.addDragStartHandler(new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {

			}
		});
		canvas.addDragEnterHandler(new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent event) {

			}
		});
		canvas.addDragOverHandler(new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {

			}
		});
		canvas.addDragLeaveHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {

			}
		});
		canvas.addDropHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				DataTransfer dt = event.getDataTransfer();
				// dt.getData(format)
				// File f = new File();
				append = true;
				if (!transferData(dt)) {
					String cont = dt.getData("Text");
					byte[] bb = cont.getBytes();
					// FileReader method
					Int8Array i8a = Int8ArrayNative.create(bb.length);
					// for( int i = 0; i < i8a.length(); i++ ) {
					i8a.set(bb);
					// }
					shorten(i8a);
				}
			}
		});
		// dropTarget( context.getCanvas() );

		final ResizeLayoutPanel sp = new ResizeLayoutPanel();
		sp.setWidth("100%");
		sp.setHeight("100%");
		sp.add(canvas);

		final ResizeLayoutPanel tableresize = new ResizeLayoutPanel();
		tableresize.setWidth("100%");
		tableresize.setHeight("100%");

		ResizeHandler rh = new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();

				canvas.setWidth(w + "px");
				canvas.setHeight(h + "px");

				canvas.setCoordinateSpaceWidth(w*2);
				canvas.setCoordinateSpaceHeight(h*2);

				draw(xstart, ystart);

				// canvas.setWidth(100+i+"px");
				// canvas.setHeight(100+"px");

				// canvas.setCoordinateSpaceWidth( canvas.getOffsetWidth() );
				// canvas.setCoordinateSpaceHeight( canvas.getOffsetHeight() );

				/*
				 * Canvas newcanvas = Canvas.createIfSupported(); Context2d
				 * context = newcanvas.getContext2d(); //context.clearRect( 0,
				 * 0, 1000, 1000 ); context.setFillStyle("#00ff00");
				 * context.fillRect(10, 10, 100, 100); if( canvas != null ) {
				 * canvas.removeFromParent(); } sp.add( newcanvas );
				 * newcanvas.setWidth("100%"); newcanvas.setHeight("100%");
				 */

				// canvas = newcanvas;
			}
		};
		sp.addHandler(rh, ResizeEvent.getType());
		// canvas.addHandler( rh, ResizeEvent.getType() );

		final Canvas overview = Canvas.createIfSupported();
		overview.setWidth("100%");
		overview.setHeight("100%");

		overview.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (max > 0) {
					int w = (overview.getOffsetWidth() * canvas
							.getOffsetWidth()) / (max * basewidth);
					int val = ((event.getX() - w / 2) * max)
							/ overview.getOffsetWidth();
					xstart = Math.max(0, Math.min(val, max));
					draw(xstart, ystart);
				}
			}
		});

		overview.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				mousedown = true;
			}
		});

		overview.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				mousedown = false;
			}
		});

		overview.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (mousedown) { // event.getNativeButton() ==
									// NativeEvent.BUTTON_LEFT ) {
					int w = (overview.getOffsetWidth() * canvas
							.getOffsetWidth()) / (max * basewidth);
					int val = ((event.getX() - w / 2) * max)
							/ overview.getOffsetWidth();
					xstart = Math.max(0, Math.min(val, max));
					draw(xstart, ystart);
				}
			}
		});

		// overview.addMouseDownHandler( new )

		final ResizeLayoutPanel roverview = new ResizeLayoutPanel();
		roverview.add(overview);

		ocontext = overview.getContext2d();
		ocontext.setFillStyle("#0000ff");

		tcanvas = Canvas.createIfSupported();
		tcontext = tcanvas.getContext2d();

		/*
		 * tcanvas.addDragStartHandler( new DragStartHandler() {
		 * 
		 * @Override public void onDragStart(DragStartEvent event) {
		 * Browser.getWindow().getConsole().log("s"); } });
		 * tcanvas.addDragHandler( new DragHandler() {
		 * 
		 * @Override public void onDrag(DragEvent event) {
		 * Browser.getWindow().getConsole().log("d2"); } });
		 * tcanvas.addDragEndHandler( new DragEndHandler() {
		 * 
		 * @Override public void onDragEnd(DragEndEvent event) {
		 * 
		 * } }); tcanvas.addDragEnterHandler( new DragEnterHandler() {
		 * 
		 * @Override public void onDragEnter(DragEnterEvent event) {
		 * 
		 * } }); tcanvas.addDragLeaveHandler( new DragLeaveHandler() {
		 * 
		 * @Override public void onDragLeave(DragLeaveEvent event) {
		 * 
		 * } }); tcanvas.addDragOverHandler( new DragOverHandler() {
		 * 
		 * @Override public void onDragOver(DragOverEvent event) {
		 * 
		 * } }); tcanvas.addDropHandler( new DropHandler() {
		 * 
		 * @Override public void onDrop(DropEvent event) {
		 * Browser.getWindow().getConsole().log("d");
		 * 
		 * List<Sequence> seqs = new ArrayList<Sequence>(); for( Sequence s :
		 * val ) { if( s.isSelected() ) { seqs.add( s ); } } int y =
		 * event.getNativeEvent().getClientY() + ystart - columnHeight; int i =
		 * y / unitheight;
		 * 
		 * val.removeAll( seqs ); val.addAll( i, seqs ); } });
		 */

		tcanvas.addMouseDownHandler(new MouseDownHandler() {
			List<Integer> sortlist = new ArrayList<Integer>();

			@Override
			public void onMouseDown(MouseDownEvent event) {
				mousex = event.getX();
				mousey = event.getY();

				int x = mousex;
				int y = mousey;

				mousedown = true;

				if (y < columnHeight) {
					if (x < tcanvas.getCoordinateSpaceWidth() - 40) {
						sortcol = 0;
						if (sortlist.size() == 0)
							sortlist.add(0);

						if (sortlist.get(0) <= 0) {
							Collections.sort(val);
							sortlist.set(0, 1);
						} else {
							Collections.sort(val, Collections.reverseOrder());
							sortlist.set(0, -1);
						}
					} else {
						sortcol = 1;
						if (sortlist.size() == 0)
							sortlist.add(0);
						if (sortlist.size() == 1)
							sortlist.add(0);

						if (sortlist.get(1) <= 0) {
							Collections.sort(val);
							sortlist.set(1, 1);
						} else {
							Collections.sort(val, Collections.reverseOrder());
							sortlist.set(1, -1);
						}
					}
					prevx = Integer.MAX_VALUE;
					prevy = Integer.MAX_VALUE;
				} else {
					int i = (y - columnHeight + ystart) / unitheight;

					if (i >= 0 && i < val.size()) {
						Sequence seq = val.get(i);

						if (event.isControlKeyDown()) {
							seq.setSelected(!seq.isSelected());
						} else if (event.isShiftKeyDown()) {
							int k = 0;
							for (Sequence s : val) {
								if (s.isSelected()) {
									break;
								}
								k++;
							}
							for (int l = Math.min(i, k); l < Math.max(i + 1, k); l++) {
								val.get(l).setSelected(true);
							}
						} else {
							if (!seq.isSelected()) {
								for (Sequence s : val) {
									s.setSelected(s == seq);
								}
							} else {
								dullseq = seq;
							}
						}
					}
				}
				draw(xstart, ystart);
			}
		});
		tcanvas.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				Browser.getWindow().getConsole()
						.log("dull " + dullseq + " " + dragging);
				if (dragging) {
					List<Sequence> seqs = new ArrayList<Sequence>();
					for (Sequence s : val) {
						if (s.isSelected()) {
							seqs.add(s);
						}
					}
					int y = event.getY() + ystart - columnHeight;
					int i = y / unitheight;

					Sequence sq = val.get(i++);
					while (sq.isSelected()) {
						if (i == val.size()) {
							sq = null;
							break;
						}
						sq = val.get(i++);
					}
					val.removeAll(seqs);
					if (sq == null) {
						i = val.size();
					} else
						i = val.indexOf(sq);

					// Browser.getWindow().getConsole().log( i + "  " +
					// val.size() );
					val.addAll(i, seqs);

					dragging = false;
				} else if (dullseq != null) {
					Browser.getWindow().getConsole().log("rugl");
					for (Sequence s : val) {
						s.setSelected(s == dullseq);
					}
				}
				dullseq = null;
				mousedown = false;
				draw(xstart, ystart);
			}
		});
		tcanvas.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (mousedown) {
					//Browser.getWindow().getConsole().log("fucking true");
					dragging = true;
					int x = event.getX();
					int y = event.getY();
				}
			}
		});
		tableresize.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();

				tcanvas.setWidth(w + "px");
				tcanvas.setHeight(h + "px");

				tcanvas.setCoordinateSpaceWidth(w);
				tcanvas.setCoordinateSpaceHeight(h);

				draw(xstart, ystart);

				// int w = tableresize.getOffsetWidth();
				// int h = tableresize.getOffsetHeight();

				/*
				 * int th = table.getOffsetHeight(); if( first < 10 || h != th )
				 * { first++; console( h + " " + th );
				 * 
				 * options.setWidth( w+"px" ); options.setHeight( h+"px" );
				 * //options.setSortColumn(table.) //table.draw( data, options
				 * ); //table.setWidth( w ); //table.setHeight( h ); table.draw(
				 * data, options ); }
				 */

				/*
				 * Element e = table.getElement();
				 * com.google.gwt.dom.client.Element de =
				 * e.getFirstChildElement(); de = de.getFirstChildElement();
				 * scrollEv( de );
				 */
			}
		});
		tableresize.add(tcanvas);

		slp.addSouth(roverview, 100);
		slp.addWest(tableresize, 200);
		slp.add(sp);

		RootPanel rootPanel = RootPanel.get();
		st = rootPanel.getElement().getStyle();
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		st.setBorderWidth(0.0, Unit.PX);

		Window.enableScrolling(false);

		final RootPanel mainPanel = RootPanel.get("main");
		mainPanel.setSize((ww - 160) + "px", wh + "px");
		mainPanel.add(vpanel);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int height = event.getHeight();
				int width = event.getWidth();
				// slp.setHeight((height-25)+"px");
				slp.setSize((width - 160) + "px", (height - 40) + "px");

				mainPanel.setWidth((width - 160) + "px");
			}
		});

		/*
		 * //HorizontalSplitPanel hsp = new HorizontalSplitPanel();
		 * slp.setWidth("1200px"); slp.setHeight("400px"); //final
		 * HorizontalPanel horp = new HorizontalPanel(); stuff(); final
		 * ScrollPanel sp = new ScrollPanel( canvas ); //sp.setWidth("10000px");
		 * //sp.setHeight("200px");
		 * 
		 * //final ScrollPanel spheader = new ScrollPanel();
		 * //spheader.setWidth("200px"); //spheader.setHeight("200px");
		 */

		// Runnable onLoadCallback = new Runnable() {
		// public void run() {
		// Panel panel = RootPanel.get();
		// AbstractDataTable abs = new AbstractDataTable() {

		// };
		// table = new Table( createTable(), createTableOptions() );
		// table.setWidth("100%");
		// table.setHeight("100%");
		// Table table = new Table();
		// table.setTitle("erm");

		/*
		 * Event.ons table.addHandler( new ScrollHandler() {
		 * 
		 * @Override public void onScroll(ScrollEvent event) {
		 * console("scrolling"); } }, ScrollEvent.getType() );
		 */

		/*
		 * table.addSelectHandler( new SelectHandler() {
		 * 
		 * @Override public void onSelect(SelectEvent event) { draw(); } });
		 * 
		 * table.addSortHandler( new SortHandler() {
		 * 
		 * @Override public void onSort(SortEvent event) { sortind =
		 * getSortInfo( table.getJso() ); draw();
		 * 
		 * Element e = table.getElement(); com.google.gwt.dom.client.Element de
		 * = e.getFirstChildElement(); de = de.getFirstChildElement(); scrollEv(
		 * de ); } });
		 * 
		 * /*sp.addHandler( new ResizeHandler() {
		 * 
		 * @Override public void onResize(ResizeEvent event) { //fl.publish( new
		 * LogRecord( Level.INFO, "res") ); int val = 1200-event.getWidth();
		 * Window.alert(""+val); table.setWidth(val+"px"); } },
		 * ResizeEvent.getType() );
		 */

		/*
		 * slp.addHandler( new ResizeHandler() {
		 * 
		 * @Override public void onResize(ResizeEvent event) { //fl.publish( new
		 * LogRecord( Level.INFO, "res") ); int val = 1200-event.getWidth();
		 * Window.alert("me "+val); table.setWidth(val+"px"); } },
		 * ResizeEvent.getType() );
		 * 
		 * tableresize.add( table );
		 * 
		 * slp.addSouth( roverview, 100 ); slp.addWest( tableresize, 200 );
		 * slp.add( sp );
		 * 
		 * roverview.addResizeHandler( new ResizeHandler() {
		 * 
		 * @Override public void onResize(ResizeEvent event) {
		 * overview.setCoordinateSpaceWidth( overview.getOffsetWidth() );
		 * overview.setCoordinateSpaceHeight( overview.getOffsetHeight() );
		 * draw(); } } );
		 * 
		 * //console( table.getElement().getClassName() + " 1 " +
		 * table.getElement().getParentElement().getClassName() + " 2 " +
		 * table.getElement
		 * ().getParentElement().getParentElement().getClassName() );
		 * //NodeList<Element> ne =
		 * table.getElement().getElementsByTagName("table");
		 * //table.getElement().getElementsByTagName(name) //TableElement te =
		 * TableElement.as( ne.getItem(0) ); //scrollEv(
		 * (JavaScriptObject)ne.getItem(0).getParentElement().getParentElement()
		 * ); //slp.add( sp ); //horp.add(table); //horp.add(sp);
		 */
		// }
		// };

		/*
		 * final WebGLCanvas webGLCanvas = new WebGLCanvas("500px", "500px");
		 * glContext = webGLCanvas.getGlContext(); glContext.viewport(0, 0, 500,
		 * 500); RootPanel.get("gwtGL").add(webGLCanvas); start();
		 */
	}

	/*
	 * public void drawSomethingNew() { // Get random coordinates and sizing int
	 * rndX = Random.nextInt(canvasWidth); int rndY =
	 * Random.nextInt(canvasHeight); int rndWidth = Random.nextInt(canvasWidth);
	 * int rndHeight = Random.nextInt(canvasHeight);
	 * 
	 * // Get a random color and alpha transparency int rndRedColor =
	 * Random.nextInt(255); int rndGreenColor = Random.nextInt(255); int
	 * rndBlueColor = Random.nextInt(255); double rndAlpha =
	 * Random.nextDouble();
	 * 
	 * CssColor randomColor = CssColor.make("rgba(" + rndRedColor + ", " +
	 * rndGreenColor + "," + rndBlueColor + ", " + rndAlpha + ")");
	 * 
	 * context.setFillStyle(randomColor); context.fillRect( rndX, rndY,
	 * rndWidth, rndHeight); context.fill(); }
	 */
}
