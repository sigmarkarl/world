package org.simmi.shared;


public class Contig extends Sequence {
	int tk = 0;
	
	public Contig(String name ) {
		super( name, null );
		loc = 0.0;
	}
	
	/*public char revCompCharAt( int i ) {
		return seq.revCompCharAt( i );
	}*/
	
	public Contig( String name, StringBuilder sb ) {
		this( name );
		setSequenceString( sb );
	}
	
	/*@Override
	public boolean equals( Object other ) {
		return other instanceof Contig && name.equals( ((Contig)other).toString() );
	}*/

	public int 				size;
	//Sequence		seq;
	//boolean			reverse = false;
	
	@Override
	public int compareTo(Sequence o) {
		if( partof != null ) {
			return partof.indexOf( this ) - partof.indexOf( o );
		}
		return getName().compareTo( o.getName() );
	}
}