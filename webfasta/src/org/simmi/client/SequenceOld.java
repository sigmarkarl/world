package org.simmi.client;

import java.util.List;

import org.simmi.shared.Sequence;

import com.google.gwt.typedarrays.shared.Int8Array;

class SequenceOld extends Sequence {
	static Webfasta		webfasta;
	static Int8Array 	content = null;
	
	int namestart;
	int nameend;
	int seqstart;
	int seqstop;

	public SequenceOld(Webfasta wf, int nstart, int nend, int sstart, int sstop) {
		super(null, null);

		this.namestart = nstart;
		this.nameend = nend;
		this.seqstart = sstart;
		this.seqstop = sstop;
		
		this.webfasta = wf;

		this.setStart( 0 );

		byte[] bb = new byte[nameend - namestart];
		for (int i = namestart; i < nameend; i++) {
			bb[i - namestart] = content.get(i);
		}
		setName(new String(bb));
	}

	@Override
	public double getEnd() {
		return getStart() + getLength();
	}

	/*
	 * public String toString() { //content.toString() return
	 * Webfasta.this.content.subarray( namestart, nameend ); }
	 */

	public String getName() {
		return name;
	}

	public Int8Array getSubarray(int start, int end) {
		return content.subarray(start + seqstart, end + seqstart);
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
	public void delete(double dstart, double dstop) {
		int count = 0;
		for (int i = (int)(seqstart + dstop - getStart()); i < seqstop; i++) {
			int val = content.get(i);
			// Browser.getWindow().getConsole().log(
			// ""+(char)content.get(seqstart+dstop-start) );
			content.set((int)(seqstart + dstart - getStart() + count), val);
			count++;
		}
		seqstop -= dstop - dstart;
		edited = true;
	}

	@Override
	public char charAt(double i) {
		double ind = i;// - start;
		if (ind >= 0 && ind < getLength())
			return (char) content.get((int)(seqstart + ind)); // char2String.get(
														// content.charAt(
														// seqstart+i ) );

		return ' ';
	}

	@Override
	public void setCharAt(double i, char c) {
		content.set((int)(seqstart + i), c);
	}

	@Override
	public double getLength() {
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
		double start = getLength();
		double stop = getLength();
		for( int i = (int)(getLength()-1); i >= 0; i-- ) {
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
		double start = getLength();
		double stop = getLength();
		for( int i = (int)(getLength()-1); i >= 0; i-- ) {
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
		if (Webfasta.sortcol > 1) {
			int i;
			if (webfasta.xsellen > 0) {
				i = webfasta.xselloc;
				while (charAt(i) == o.charAt(i)
						&& i < webfasta.xselloc + webfasta.xsellen - 1)
					i++;
			} else {
				i = Webfasta.sortcol - 2;
			}
			return this.charAt(i) - o.charAt(i);
		} else if (Webfasta.sortcol >= 0) {
			return Webfasta.sortcol == 0 ? getName().compareTo(o.getName())
					: Double.compare(getLength(), o.getLength());
		} else {
			return isSelected() ? (o.isSelected() ? 0 : -1) : (o
					.isSelected() ? 1 : 0);
		}
	}

	/*
	 * String name; String seq; int[] aInd; List<Annotation> aList;
	 */
};
