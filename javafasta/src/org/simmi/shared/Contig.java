package org.simmi.shared;

import java.util.Collections;
import java.util.List;

public class Contig extends Sequence {
	int tk = 0;
	
	public Contig(String name ) {
		super( name, null );
		loc = 0.0;
	}
	
	public void add( Tegeval tv ) {
		if( name.contains("scotoductus2101_scaffold00007") ) {
			System.err.println( "adding " + tv.name + " " + (tk++) + " .........................................................");
			System.err.print(" ");
		}
		
		addAnnotation( tv );
		//addAnnotation( tv );
	}
	
	public void deleteAfter( Tegeval cur ) {
		int i = annset.indexOf( cur );
		if( i != -1 && i < annset.size() && ((Tegeval)annset.get(i+1)).getGene() == null ) {
			annset.remove( i+1 );
		}
	}
	
	public void deleteBefore( Tegeval cur ) {
		int i = annset.indexOf( cur );
		if( i > 0 && ((Tegeval)annset.get(i-1)).getGene() == null )
			annset.remove( i-1 );
	}
	
	public void injectAfter( Tegeval cur, Tegeval tv ) {
		int i = annset.indexOf( cur );
		if( i != -1 ) {
			addAnnotation(i+1, tv);
		}
	}
	
	public void injectBefore( Tegeval cur, Tegeval tv ) {
		int i = annset.indexOf( cur );
		if( i != -1 ) {
			addAnnotation( i, tv );
		}
	}
	
	public boolean isPlasmid() {
		return plasmid;
	}
	
	public List<Annotation> getTegevalsList() {
		return annset;
	}
	
	public boolean isChromosome() {
		return this.length() > 1500000;
	}
	
	public void sortLocs() {
		if( annset != null ) {
			Collections.sort( annset );
			int i = 0;
			//Tegeval prev = null;
			for( Annotation tv : annset ) {
				((Tegeval)tv).setNum( i++ );
				//if( prev != null ) tv.setPrevious( prev );
				//prev = tv;
			}
		}
	}
	
	/*public char revCompCharAt( int i ) {
		return seq.revCompCharAt( i );
	}*/
	
	public int getGeneCount() {
		if( annset != null ) {
			return annset.size();
		}
		return 0;
	}
	
	public int getNumberOfSubContigs() {
		int count = 0;
		int i = sb.indexOf("NNNNN");
		if( i != -1 ) count++;
		
		while( i != -1 ) {
			int k = sb.indexOf("NNNNN", i+5);
			if( k > i+100 ) {
				count++;
			}
			i = k;
		}
		return count;
	}
	
	public Contig( String name, StringBuilder sb ) {
		this( name );
		setSequenceString( sb );
	}
	
	public Tegeval getNext( Tegeval from ) {
		int i = annset != null ? annset.indexOf( from ) : -1;
		if( i != -1 ) {
			if( isReverse() ) {
				if( i > 0 ) return (Tegeval)annset.get( i-1 );
			} else {
				 if( i < annset.size()-1 ) {
					 Tegeval ret = (Tegeval)annset.get( i+1 );
					 return ret;
				 }
			}
		}
		//if( from.getGene().getSpecies().contains("140") ) {
		//	System.err.println( from.getGene().getSpecies() + " bobbou " + from.getGene() );
		//}
		return null;
	}
	
	public Tegeval getPrev( Tegeval from ) {
		int i = annset != null ? annset.indexOf( from ) : -1;
		if( i != -1 ) {
			if( isReverse() ) {
				if( i < annset.size()-1  ) return (Tegeval)annset.get( i+1 );
			} else {
				if( i > 0 ) return (Tegeval)annset.get( i-1 );
			}
		}
		System.err.println( from.getGene().getSpecies() + "  " + from.getGene() );
		return null;
	}
	
	/*@Override
	public boolean equals( Object other ) {
		return other instanceof Contig && name.equals( ((Contig)other).toString() );
	}*/
	
	public StringBuilder getSequenceString() {
		return getStringBuilder();
	}
	
	public Tegeval getFirst() {
		if( annset != null ) return (Tegeval)(isReverse() ? annset.get(annset.size()-1) : annset.get(0));
		return null;
	}
	
	public Tegeval getLast() {
		if( annset != null ) return (Tegeval)(isReverse() ? annset.get(0) : annset.get(annset.size()-1));
		return null;
	}
	
	public Tegeval getIndex( int i ) {
		Tegeval first = getFirst();
	
		int k = 0;
		while( first != null && k < i ) {
			first = first.getNext();
			k++;
		}
		
		return first;
	}

	public double 			loc;
	public int 			size;
	//Sequence		seq;
	//boolean			reverse = false;
	public Contig			next;
	public Contig			prev;
	//List<Tegeval>	annset;
	public List<Contig>	partof;
	public boolean			plasmid;
	
	public Tegeval getEndTegeval() {
		if( annset != null ) return (Tegeval)annset.get( annset.size()-1 );
		return null;
	}
	
	public Tegeval getStartTegeval() {
		if( annset != null ) return (Tegeval)annset.get( 0 );
		return null;
	}
	
	public void setConnection( Contig contig, boolean rev, boolean forw ) {
		if( forw ) setForwardConnection( contig, rev );
		else setBackwardConnection( contig, rev );
	}
	
	public void setForwardConnection( Contig contig, boolean rev ) {
		this.next = contig;
		if( rev ) {
			contig.next = this;
			
			/*if( this.getEnd() != null ) this.getEnd().next = contig.getEnd();
			if( contig.getEnd() != null ) contig.getEnd().next = this.getEnd();
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}*/
		} else {
			contig.prev = this;
			
			/*if( this.getEnd() != null ) this.getEnd().next = contig.getStart();
			if( contig.getStart() != null ) contig.getStart().prev = this.getEnd();
			
			if( this.isReverse() != contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.prev : nextc.next;
				}
			}*/
		}
	}
	
	public void setBackwardConnection( Contig contig, boolean rev ) {
		this.prev = contig;
		if( rev ) {
			contig.next = this;
			
			/*this.getStart().prev = contig.getEnd();
			if( contig.getEnd() != null ) contig.getEnd().next = this.getStart();
			
			if( this.isReverse() != contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.next : nextc.prev;
				}
			}*/
		} else {
			contig.prev = this;
			
			/*this.getStart().prev = contig.getStart();
			if( contig.getStart() != null ) contig.getStart().prev = this.getStart();
			
			if( this.isReverse() == contig.isReverse() ) {
				Contig nextc = contig;
				while( nextc != null ) {
					nextc.setReverse( !nextc.isReverse() );
					nextc = nextc.isReverse() ? nextc.next : nextc.prev;
				}
			}*/
		}
	}
	
	public Contig getNextContig() {
		return next;
	}
	
	public Contig getPrevContig() {
		return prev;
	}
	
	@Override
	public int compareTo(Sequence o) {
		if( partof != null ) {
			return partof.indexOf( this ) - partof.indexOf( o );
		}
		return getName().compareTo( o.getName() );
	}
}