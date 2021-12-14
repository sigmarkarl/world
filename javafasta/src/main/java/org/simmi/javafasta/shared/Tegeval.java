package org.simmi.javafasta.shared;


public class Tegeval extends Annotation implements Teg {
	public Tegeval(Gene gene, double evalue, String contig, Sequence shortcontig, int sta, int sto, int orient) {
		this( contig, shortcontig, sta, sto, orient, true );
	}

	public Tegeval(Gene gene, double evalue, String contig, Sequence shortcontig, int sta, int sto, int orient, boolean add) {
		this( contig, shortcontig, sta, sto, orient, add );

		eval = evalue;
		this.setGene( gene );
		//dna = dnaseq;
		//setSequence(sequence);
	}
	
	public Tegeval( String contig, Sequence shortcontig, int sta, int sto, int orient, boolean add ) {
		super(shortcontig,contig,null,sta,sto,orient,null,add);
		init( contig, shortcontig, sta, sto, orient );
	}
	
	public Tegeval() {
		super();
	}

	public Tegeval(String type) {
		super(type);
		Gene gene = new Gene();
		setGene(gene);
		gene.setTegeval(this);
	}
	
	public void init( String contig, Sequence shortcontig, int sta, int sto, int orient ) {
		setName( contig );
		
		Sequence alignedsequence = getAlignedSequence();
		if( alignedsequence != null ) {
			String seqname = getName() + " # " + sta + " # " + sto + " # " + orient;
			alignedsequence.setName( seqname );
		}
		
		seq = shortcontig;
		start = sta;
		stop = sto;
		ori = orient;
		num = -1;

		gc = gcPerc(); //(double)gcCount()/(double)(stop-start);
		//gcskew = gcSkew();
		//else gc = -1.0;
		
		numCys = 0;
	}
	
	public String getCommonName() {
		return getGene().getGeneGroup().getName();
	}
	
	public String getCommonFunction() {
		return getGene().getGeneGroup().getCommonFunction(true, null);
	}
	
	public void setEval( double eval ) {
		this.eval = eval;
	}
	
	public String getContloc() {
		return this.getContshort() != null ? this.getContshort().getName() : "";
	}
	
	public String getContigName() {
		return getName();
	}
	
	/*public Tegeval setNext( Tegeval next ) {
		Tegeval old = this.next;
		this.next = next;
		return old;
	}
	
	public Tegeval setPrevious( Tegeval prev ) {
		Tegeval old = this.prev;
		this.prev = prev;
		prev.setNext( this );
		return old;
	}*/
	
	private double gcSkew() {
		int g = 0;
		int c = 0;
		//for( int i = 0; i < dna.length(); i++ ) {
		if( seq != null ) for( int i = start; i < stop; i++ ) {
			char n = /*this.ori == -1 ? contshort.revCompCharAt(i) :*/ seq.getCharAt(i);
			if( n == 'g' || n == 'G' ) g++;
			else if( n == 'c' || n == 'C' ) c++;
		}
		double gc = g+c;
		return gc == 0 ? gc : (g-c)/gc;
	}
	
	public void unresolvedGap( int i ) {
		Contig contig = getContshort();
		if(contig != null) {
			if (i >= contig.getAnnotations().size()) {
				int icheck = contig.getAnnotations().indexOf(this);
				System.err.println("whataf " + icheck);
			}
			if (i == 0) {
				Annotation tv = contig.getAnnotation(i);
				for (int m = 0; m < tv.start; m++) {
					char c = seq.getCharAt(m);
					if (c == 'n' || c == 'N') {
						//ret |= 1;
						if (this.ori == -1) frontgap = true;
						else backgap = true;

						break;
					}
				}
			} else {
				Annotation tv = contig.getAnnotation(i);
				Annotation tvp = contig.getAnnotation(i - 1);
				for (int m = tvp.stop; m < tv.start; m++) {
					char c = seq.getCharAt(m);
					if (c == 'n' || c == 'N') {
						//ret |= 1;
						if (this.ori == -1) frontgap = true;
						else backgap = true;

						break;
					}
				}
			}

			if (i == contig.getAnnotations().size() - 1) {
				Annotation tv = contig.getAnnotation(i);
				for (int m = tv.stop; m < seq.length(); m++) {
					char c = seq.getCharAt(m);
					if (c == 'n' || c == 'N') {
						//ret |= 2;
						if (this.ori == -1) backgap = true;
						else frontgap = true;

						break;
					}
				}
			} else {
				Annotation tv = contig.getAnnotation(i);
				Annotation tvn = contig.getAnnotation(i + 1);
				for (int m = tv.stop; m < tvn.start; m++) {
					char c = seq.getCharAt(m);
					if (c == 'n' || c == 'N') {
						//ret |= 2;
						if (this.ori == -1) backgap = true;
						else frontgap = true;

						break;
					}
				}
			}
		}
		//return ret;
	}
	
	//Contig 			contshort;
	//Sequence	 		seq;
	//StringBuilder 	dna;
	int 				numCys;
	//Tegeval			next;
	//Tegeval			prev;
	//boolean			

	/*public void setSequence(StringBuilder seq) {
		if (seq != null) {
			for (int i = 0; i < seq.length(); i++) {
				char c = (char) seq.charAt(i);
				if (c == 'C' || c == 'c')
					numCys++;
			}
			this.seq = seq;
		}
	}*/

	public String toString() {
		return (ori == -1 ? "-" + this.getContloc() : this.getContloc()) + " " + this.getStart() + "-" + this.getEnd();
	}
	
	public static boolean locsort = true;

	@Override
	public int compareTo(Object o) {
		/*if( o != null ) return this.getGene().toString().compareTo(o.toString());
		else return 1;*/

		if( o instanceof Tegeval ) {
			return compareTo((Tegeval) o);
		} else {
			return super.compareTo(o);
		}
	}
	
	public int compareTo(Tegeval tv) {
		if( locsort ) {
			if( getContshort() == null ) {
				if(tv.getContshort() == null) return 0;
				return 1;
			}
			if( tv.getContshort() == null ) {
				if( getContshort() == null ) return 0;
				return -1;
			}
			return super.compareTo(tv);
		} else {
			int comp = Double.compare(eval, tv.eval);
			return comp == 0 ? getSpecies().compareTo(tv.getSpecies()) : comp;
		}
	}
	
	public int compareTo(Teginfo ti) {
		return 1;
	}

	@Override
	public Annotation getBest() {
		return this;
	}
}