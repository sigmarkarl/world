package org.simmi.shared;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Annotation implements Comparable<Object> {
	public Sequence			seq;
	public String			name;
	public String			id;
	public StringBuilder	desc;
	public String			type;
	public String			group;
	public Set<String>		dbref;
	public int				start;
	public int				stop;
	public int				ori;
	public Object			color = Color.green;
	public Gene				gene;
	public boolean			selected = false;
	double					gc;
	double					gcskew;
	public boolean			dirty = false;
	public String 			teg;
	public double 			eval;
	public int				num;
	public boolean			backgap = false;
	public boolean			frontgap = false;
	public String	designation;
	
	public boolean isPhage() {
		return designation != null && designation.contains("phage");
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected( boolean sel ) {
		this.selected = sel;
	}
	
	public Annotation() {
		
	}
	
	public Annotation( String type ) {
		this.type = type;
	}
	
	public Annotation( Sequence seq, String name, Object color, int start, int stop, int ori, Map<String,Annotation> mann ) {
		this( seq, name, color, mann );
		this.setStart( start );
		this.setStop( stop );
		this.setOri( ori );
	}
	
	public Annotation( Sequence seq, String name, Object color, Map<String,Annotation>  mann ) {
		this.name = name;
		this.color = color;
		this.seq = seq;
		
		if( seq != null ) {
			seq.addAnnotation( this );
		}
		if( mann != null ) mann.put( name, this );
	}
	
	public String getSpecies() {
		return teg;
	}
	
	private Sequence		alignedsequence;
	public Sequence getAlignedSequence() {
		return alignedsequence;
	}
	
	public void setAlignedSequence( Sequence alseq ) {
		if( seq != null ) System.err.println( "set aligned seq " + seq.getName() + "  " + alseq.length() );
		else System.err.println( "seq null" );
		
		this.alignedsequence = alseq;
	}
	
	public StringBuilder getProteinSubsequence( int u, int e ) {
		return seq.getProteinSequence( start+u, start+e, ori );
	}
	
	public Color getBackFlankingGapColor() {
		//if( ori == 1 ) return new Color( 1.0f, 1.0f, 1.0f );
		
		//int i = contshort.tlist.indexOf(this);
		//if( i == 0 || i == contshort.tlist.size()-1 ) return Color.blue;
		//else if( unresolvedGap() > 0 ) return Color.red;
		return backgap ? Color.red : Color.lightGray;
	}
	
	public Color getFrontFlankingGapColor() {
		//if( ori == 1 ) return new Color( 1.0f, 1.0f, 1.0f );
		
		//int i = contshort.tlist.indexOf(this);
		//if( i == 0 || i == contshort.tlist.size()-1 ) return Color.blue;
		//else if( unresolvedGap() > 0 ) return Color.red;
		return frontgap ? Color.red : Color.lightGray;
	}
	
	public int getSequenceLength() {
		return alignedsequence == null ? 0 : alignedsequence.length();
	}
	
	public int getProteinLength() {
		return getLength()/3;
	}
	
	public int getNum() {
		return num;
	}

	public void setNum(int i) {
		num = i;
	}
	
	public double gcPerc() {
		int gc = 0;
		int total = 0;
		//for( int i = 0; i < dna.length(); i++ ) {
		if( seq != null ) for( int i = start; i < stop; i++ ) {
			char c = /*this.ori == -1 ? contshort.revCompCharAt(i) :*/ seq.getCharAt(i);
			if( c == 'g' || c == 'G' || c == 'c' || c == 'C' ) gc++;
			if( c != '-' && c != 'x' || c != 'X' ) total++;
		}
		return (double)gc/(double)total;
	}
	
	public double getGCPerc() {
		return gc;
	}
	
	public double getGCSkew() {
		return gcskew;
	}
	
	public Color getGCColor() {
		if( isDirty() ) return Color.red;
		double gcp = Math.min( Math.max( 0.5, gc ), 0.8 );
		return new Color( (float)(0.8-gcp)/0.3f, (float)(gcp-0.5)/0.3f, 1.0f );
		
		/*double gcp = Math.min( Math.max( 0.35, gc ), 0.55 );
		return new Color( (float)(0.55-gcp)/0.2f, (float)(gcp-0.35)/0.2f, 1.0f );*/
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public Color getGCSkewColor() {
		return new Color( (float)Math.min( 1.0, Math.max( 0.0, 0.5+5.0*gcskew ) ), 0.5f, (float)Math.min( 1.0, Math.max( 0.0, 0.5-5.0*gcskew ) ) );
	}
	
	public void setGene( Gene gene ) {
		this.gene = gene;
	}
	
	public Gene getGene() {
		return this.gene;
	}
	
	public StringBuilder getProteinSequence() {
		return seq.getProteinSequence(start, stop, ori);
	}
	
	public void addDbRef( String val ) {
		if( dbref == null ) dbref = new HashSet<String>();
		dbref.add( val );
	}
	
	public int getSubstringOffset( int u, int e ) {
		return seq.getSubstringOffset(start+u, start+e, ori);
	}
	
	public String getPaddedSubstring( int u, int e ) {
		return seq.getPaddedSubstring(start+u, start+e, ori);
	}
	
	public String getSubstring( int u, int e ) {
		return seq.getSubstring(start+u, start+e, ori);
	}
	
	public String getSequence() {
		return seq.getSubstring(start, stop, ori);
	}
	
	public String	designation;
	public boolean isPhage() {
		return designation != null && designation.contains("phage");
	}
	
	public Annotation( Sequence seq, int start, int stop, int ori, String name ) {
		this.seq = seq;
		this.name = name;
		this.start = start;
		this.stop = stop;
		this.ori = ori;
		
		/*if( seq != null ) {
			seq.addAnnotation( this );
		}*/
		//if( mann != null ) mann.put( name, this );
	}
	
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public Sequence getContig() {
		return seq;
	}
	
	public void setContig( Sequence contig ) {
		this.seq = contig;
	}
	
	public Annotation getNext() {
		if( seq != null ) 
			return getContig().getNext( this );
		return null;
	}
	
	public Annotation getPrevious() {
		if( seq != null ) 
			return getContig().getPrev( this );
		return null;
	}
	
	public boolean isReverse() {
		return ori == -1;
	}
	
	public boolean isGlobal() {
		return seq == null;
	}
	
	public int getLength() {
		return stop-start;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return stop;
	}
	
	public void setStart( int start ) {
		this.start = start;
	}
	
	public void setStop( int stop ) {
		this.stop = stop;
	}
	
	public void setOri( int ori ) {
		this.ori = ori;
	}
	
	public void setGroup( String group ) {
		this.group = group;
	}
	
	public void setType( String type ) {
		this.type = type;
	}
	
	public int getCoordStart() {
		return (seq != null ? seq.getStart() : 0)+start;
	}
	
	public int getCoordEnd() {
		return (seq != null ? seq.getStart() : 0)+stop;
	}
	
	public void appendDesc( String astr ) {
		if( desc == null ) desc = new StringBuilder( astr );
		else desc.append( astr );
	}

	@Override
	public int compareTo(Object o) {
		int val = start - ((Annotation)o).start;
		//if( val == 0 && start == 0 ) return this.hashCode();
		return val;
	}
	
	@Override
	public boolean equals( Object o ) {
		return compareTo(o) == 0 && (start > 0 || this == o);
	}
};
