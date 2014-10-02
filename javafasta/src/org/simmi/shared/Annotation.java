package org.simmi.shared;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Annotation implements Comparable<Object> {
	public Sequence			seq;
	public String			name;
	public StringBuilder	desc;
	public String			type;
	public String			group;
	public Set<String>		dbref;
	public int				start;
	public int				stop;
	public int				ori;
	public Object			color = Color.green;
	
	public Annotation() {
		
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
	
	public StringBuilder getProteinSequence() {
		return seq.getProteinSequence(start, stop, ori);
	}
	
	public void addDbRef( String val ) {
		if( dbref == null ) dbref = new HashSet<String>();
		dbref.add( val );
	}
	
	public Annotation( Sequence seq, int start, int stop, int ori, String name ) {
		this.seq = seq;
		this.name = name;
		this.start = start;
		this.stop = stop;
		this.ori = ori;
	}
	
	public Sequence getContig() {
		return seq;
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
