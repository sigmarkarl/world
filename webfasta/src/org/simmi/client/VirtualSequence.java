package org.simmi.client;

import java.util.ArrayList;
import java.util.Map;

import org.simmi.shared.Sequence;

import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Int8Array;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Blob;
import elemental.html.FileReader;

public class VirtualSequence extends Sequence {
	static final int 			segsize = 10000;
	static Webfasta				webfasta;
	
	double 						seqlen = 0;
	int							linelen = 0;
	int							linebuf = 0;
	double						seqoffset = 0;
	ArrayList<Int8Array>	 	segments = new ArrayList<Int8Array>();
	ArrayList<Boolean>			segmentloaded = new ArrayList<Boolean>();
	private Blob				file;
	private boolean				loading = false;
	boolean						bamcons = false;
	
	public static boolean samloading = false;
	
	public VirtualSequence(String name, double seqlen, double seqstart, Blob file, double seqoffset, int linelen, int linebuf, Map<String, Sequence> mseq, boolean bamcons) {
		super(name, mseq);
		this.file = file;
		this.seqlen = seqlen;
		this.setStart( seqstart );
		this.seqoffset = seqoffset;
		this.linelen = linelen;
		this.linebuf = linebuf;
		
		this.bamcons = bamcons;
		
		double numsegments = seqlen/segsize+1;
		while( segments.size() < numsegments ) segments.add( null );
		while( segmentloaded.size() < numsegments ) segmentloaded.add( false );
	}
	
	public void setLoading( boolean ld ) {
		loading = ld;
	}

	@Override
	public double getLength() {
		return this.seqlen;
	}
	
	public native Blob slice(Blob blob, double start, double stop) /*-{	
		return blob.slice( start, stop );
	}-*/;
	
	public native FileReader newFileReader() /*-{	
		return new FileReader();
	}-*/;
	
	@Override
	public double getEnd() {
		return getStart()+seqlen;
	}
	
	@Override
	public char charAt( double i ) {
		//Browser.getWindow().getConsole().log("arg");
		if( i >= 0 ) {
			final int numseg = (int)(i/segsize);
			
			//Browser.getWindow().getConsole().log("lubb " + segments.size() + "  " + numseg);
			if( numseg < segments.size() ) {
				Int8Array strb = segments.get(numseg);
				if( strb == null ) {
					//Browser.getWindow().getConsole().log("koki " + file + "  " + loading);
					if( !loading ) {
						//Browser.getWindow().getConsole().log("lubb " + strb.byteLength());
						//strb = Int8ArrayNative.create(segsize);
						double startseg = numseg*segsize;
						double start = seqoffset+startseg;
						
						if( file != null && startseg < seqlen ) {
							loading = true;
							Blob sliceblob = slice( file, start, start+Math.min( seqlen,segsize ) );
							final FileReader reader = newFileReader();
							reader.setOnload( new EventListener() {
								@Override
								public void handleEvent(Event evt) {
									ArrayBuffer res = (ArrayBuffer)reader.getResult();
									Int8Array i8a = Int8ArrayNative.create(res);
									//Browser.getWindow().getConsole().log("bsize "+i8a.length());
									segments.set(numseg, i8a);
									loading = false;
									//webfasta.draw( webfasta.xstart, webfasta.ystart );
								}
							});
							reader.readAsArrayBuffer(sliceblob);
						}
					}
				} else {
					//Browser.getWindow().getConsole().log("bsize2 "+strb.length());
					
					int idx = (int)(i-numseg*segsize);
					if( idx >= 0 && idx < strb.length() ) 
						return (char)strb.get(idx);
					
					//else Browser.getWindow().getConsole().log("idx "+idx);
				}
				
				boolean bb = segmentloaded.get(numseg);
				if( !bb && !samloading ) {
					//Browser.getWindow().getConsole().log("saell " + loading);
					//if( prevx != xstartLocal ) {
						//Sequence selseq = null;
						//for( Sequence seq : consensusmap.keySet() ) {
							//Sequence seq = consensusmap.get( seq );
							//if( (xstartLocal+cw)/basewidth > seq.getStart() && (xstartLocal)/basewidth < seq.getEnd() ) {
								//selseq = seq;
					
					//if( !loading ) {
						//Browser.getWindow().getConsole().log("saell2");
						
					//loading = true;
					elemental.dom.Element e = Browser.getDocument().getElementById("samtools");
					
					double startseg = numseg*segsize;
					double start = startseg;
					
					String message = getName()+":"+start+"-"+(start+Math.min( seqlen,segsize ));
					
					Browser.getWindow().getConsole().log("about to " + numseg + "  " + getName() + "  " + bb + "  " + message);
					//(int)Math.max(0,((xstartLocal-5*cw)/basewidth-seq.getStart()))+"-"+(int)Math.min(seq.getEnd(),(xstartLocal+5*cw)/basewidth-seq.getStart());
					
					
					segmentloaded.set(numseg, true);
					//webfasta.postMessage(e, message);
					samloading = true;
					webfasta.postWorkerMessage( webfasta.semtoolsworker, message );
					//}
					
							//}
						//}
					//}
				}
			}
		}
		
		return ' ';
	}
	
	@Override
	public int compareTo(Sequence o) {
		if (Webfasta.sortcol > 1) {
			int i;
			if (webfasta.xsellen > 0) {
				i = webfasta.xselloc;
				while (charAt(i) == o.charAt(i) && i < webfasta.xselloc + webfasta.xsellen - 1) i++;
			} else {
				i = Webfasta.sortcol - 2;
			}
			return this.charAt(i) - o.charAt(i);
		} else if (Webfasta.sortcol >= 0) {
			return Webfasta.sortcol == 0 ? getName().compareTo(o.getName()) : Double.compare(getLength(), o.getLength());
		} else {
			return isSelected() ? (o.isSelected() ? 0 : -1) : (o.isSelected() ? 1 : 0);
		}
	}
}
