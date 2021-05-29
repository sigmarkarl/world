package org.simmi.javafasta.shared;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Annotation implements Comparable<Object> {
	public Sequence			seq;
	private Sequence		alignedsequence;
	private String			name;
	public String			id;
	public String			tag;
	public StringBuilder	desc;
	public String			type;
	private String			group;
	public Set<String>		dbref;
	public int				start;
	public int				stop;
	public int				ori;
	public Object			color = Color.green;
	private Gene			gene;
	public boolean			selected = false;
	double					gc;
	double					gcskew;
	public boolean			dirty = false;
	public double 			eval;
	public int				num;
	public boolean			backgap = false;
	public boolean			frontgap = false;
	public String	designation;
	public int indexOf = -1;
	public GeneGroup gg;
	
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

	public void setGeneGroup(GeneGroup gg) {
		this.gg = gg;
		gg.addGene(this);
	}

	public GeneGroup getGeneGroup() {
		return gg;
	}

	public int getGroupIndex() {
		if( gg != null ) return gg.groupIndex;
		return -10;
	}

	public int getGroupCoverage() {
		if( gg != null ) return gg.getGroupCoverage();
		return -1;
	}

	public int getGroupCount() {
		if( gg != null ) return gg.getGroupCount();
		return -1;
	}

	@Override
	public String toString() {
		return "name: "+name+" id: "+id+" tag: "+tag;
	}
	
	public Annotation( Annotation a ) {
		this( a, 0 );
	}
	
	public Annotation( Annotation a, int offset ) {
		start = a.start+offset;
		stop = a.stop+offset;
		ori = a.ori;
		type = a.type;
		name = a.name;
		id = a.id;
		group = a.group;
		desc = new StringBuilder( a.desc );
		gene = a.getGene();
		designation = a.designation;
		eval = a.eval;
		num = a.num;
		color = a.color;
		dbref = new HashSet<>( a.dbref );
		gc = a.gc;
		gcskew = a.gcskew;
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
		if(seq instanceof Contig) {
			Contig contig = (Contig) seq;
			return contig.getSpec();
		} return null;
	}

	public Sequence getAlignedSequence() {
		return alignedsequence;
	}
	
	public void setAlignedSequence( Sequence alseq ) {
		//if( seq != null ) System.err.println( "set aligned seq " + seq.getName() + "  " + alseq.length() );
		//else System.err.println( "seq null" );
		this.alignedsequence = alseq;
		if(alseq.id == null) alseq.id = id;
		if(seq != null) alseq.name = seq.getGroup();
		if(alseq.group == null) alseq.group = group;
	}
	
	public void writeFasta( Writer w ) throws IOException {
		w.write( ">"+name+"\n" );
		w.write( getSequence()+"\n" );
	}
	
	public Sequence getProteinSubsequence( int u, int e ) {
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
	
	public void setGene( Gene gen ) {
		if(gene!=gen) {
			gene = gen;
			gene.setTegeval(this);
		}
	}
	
	public Gene getGene() {
		return gene;
	}
	
	public Sequence getProteinSequence() {
		Sequence ret = seq == null ? null : seq.getProteinSequence(start, stop, ori);
		if( ret != null && this.name != null ) {
			int i = this.name.indexOf(' ');
			if( i != -1 ) ret.setName( this.name.substring(0,i) + " # " + this.start + " # " + this.stop + " # " + this.ori + " #" );
			else ret.setName( this.name );
		}
		return ret;
	}
	
	public void addDbRef( String val ) {
		if( dbref == null ) dbref = new HashSet<>();
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
	
	public Sequence createSequence() {
		String seqstr = getSequence();
		Sequence seq = new Sequence(this.id, this.name,null);
		seq.append(seqstr);
		return seq;
	}
	
	public String getSequence() {
		return seq.getSubstring(start, stop, ori);
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

	public void append( String a ) {
		Sequence alignedsequence = getAlignedSequence();
		if( alignedsequence == null ) {
			alignedsequence = new Sequence( getName() + " # " + start + " # " + stop + " # " + ori, null );
			setAlignedSequence( alignedsequence );
		}
		alignedsequence.append( a );
	}

	public Contig getContshort() {
		return (Contig)getContig();
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

	public String getGroup() {
		return group;
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
		if(o==null) {
			return -1;
		} else if(!(o instanceof Annotation)) {
			Teg teg = (Teg)o;
			Annotation a = teg.getBest();
			Contig cont = getContshort();
			Contig acont = a.getContshort();
			int ret = cont.compareTo(acont);
			return ret == 0 ? Integer.compare(start, a.start) : ret;
		} else {
			Annotation a = (Annotation)o;
			Contig cont = getContshort();
			Contig acont = a.getContshort();
			if(cont==null||acont==null) {
				return -1;
			}
			int ret = cont.compareTo(acont);
			if(ret == 0) {
				/*if(cont.getName().equals("NZ_CP020382")) {
					if(start==0) {
						System.err.println();
					}
					System.err.println(cont.getName() + "  " + acont.getName() + "  " + start + "   " + a.start);
				}*/

				return Integer.compare(start, a.start);
			}
			return ret;
		}
	}

	public int indexOf() {
		if(indexOf == -1) {
			indexOf = seq.annset.indexOf(this);
		}
		return indexOf;
	}
	
	@Override
	public boolean equals( Object o ) {
		return compareTo(o) == 0 && (start > 0 || this == o);
	}

	public void export(Appendable a, boolean translations, Optional<Sequence.Locus> ol, int ac, String key) throws IOException {
		if( getName() != null && !getName().contains("No hits") ) {
			String locstr = (start)+".."+(stop);
			String tid = ol.map(locus -> locus.locus_tag + "_" + (locus.locus_tag_decformat.length() > 0 ? String.format(locus.locus_tag_decformat, ac) : ac)).orElseGet(() -> id);

			a.append("     ").append(type);
			int len = type.length();
			while( len < 16 ) {
				a.append(' ');
				len++;
			}
			if( isReverse() ) a.append("complement(").append(locstr).append(")\n");
			else a.append(locstr).append("\n");

			if( tid == null ) a.append("                     /locus_tag=\"").append(key).append(String.valueOf(ac)).append("\"\n");
			else a.append("                     /locus_tag=\"").append(tid).append("\"\n");

			StringBuilder addon = new StringBuilder();
			if( dbref != null ) for( String val : dbref ) {
				addon.append("(").append(val).append(")");
			}
			a.append("                     /product=\"").append(getName()).append(String.valueOf(addon)).append("\"\n");
			if( translations ) {
				a.append( "                     /translation=\"" );
				Sequence aa = getProteinSequence();
				a.append(aa.sb.substring(0, Math.min(46, aa.length())) );
				for (int k = 46; k < aa.length(); k += 60) {
					a.append("\n");
					a.append("                     ").append(aa.sb.substring(k, Math.min(k + 60, aa.length())));
				}
				a.append( "\"\n" );
			}

			if( dbref != null ) for( String val : dbref ) {
				a.append("                     /db_xref=\"").append(val).append("\"\n");
			}
		}
	}
};
