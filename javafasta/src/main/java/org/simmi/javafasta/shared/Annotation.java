package org.simmi.javafasta.shared;

import scala.xml.dtd.impl.Base;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class Annotation implements Comparable<Object> {
	Sequence		seq;
	private Sequence		alignedsequence;
	private String			name;
	private String			note;
	private String			id;
	private String			tag;
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
	double					gc = -1.0;
	private double			gcskew = -1.0;
	public boolean			dirty = false;
	public double 			eval;
	public int				num;
	public boolean			backgap = false;
	public boolean			frontgap = false;
	public String	designation;
	public int indexOf = -1;
	public GeneGroup gg;

	public void setId(String id) {
		this.id = id;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getId() {
		return id;
	}

	public String getTag() {
		return tag;
	}

	public boolean isPhage() {
		return designation != null && designation.contains("phage");
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected( boolean sel ) {
		this.selected = sel;
	}

	int i;
	public Annotation() {
		i = 0;
	}

	public String getDesignation() {
		if (designation==null) {
			var desmap = getGeneGroup().geneset.getDesignationMap();
			designation = getId()!=null ? desmap.getOrDefault(getId(), "") : "";
			return designation;
		} else if (designation.length()>0) {
			return designation;
		}
		return "";
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
		this(seq,name,color,start,stop,ori,mann,true);
	}

	public Annotation( Sequence seq, String name, Object color, int start, int stop, int ori, Map<String,Annotation> mann, boolean add ) {
		this( seq, name, color, mann, add );
		this.setStart( start );
		this.setStop( stop );
		this.setOri( ori );
	}

	public Annotation( Sequence seq, String name, Object color, Map<String,Annotation>  mann ) {
		this(seq,name,color,mann,true);
	}

	public Annotation( Sequence seq, String name, Object color, Map<String,Annotation>  mann, boolean add ) {
		this.name = name;
		this.color = color;
		this.seq = seq;
		
		if( add && seq != null ) {
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
		if (gc == -1.0) {
			gc = gcPerc();
		}
		return gc;
	}

	public double calcGcSkew() {
		int pos = (start+stop)/2;
		if (seq.getGC()%2 == 1) {
			int acount = 0;
			int tcount = 0;
			for (int k = Math.max(0, pos - 2000); k < Math.min(seq.length(), pos + 2000); k++) {
				char chr = seq.charAt(k);
				if (chr == 'a' || chr == 'A') {
					acount++;
				} else if (chr == 't' || chr == 'T') {
					tcount++;
				}
				//else if( chr == 'a' || chr == 'A' ) acount++;
				//else if( chr == 't' || chr == 'T' ) tcount++;
			}

			if (acount > 0 || tcount > 0) {
				return (seq.getGC()-2) * (acount - tcount) / (double) (acount + tcount);
			}
		} else {
			int gcount = 0;
			int ccount = 0;
			for (int k = Math.max(0, pos - 2000); k < Math.min(seq.length(), pos + 2000); k++) {
				char chr = seq.charAt(k);
				if (chr == 'g' || chr == 'G') {
					gcount++;
				} else if (chr == 'c' || chr == 'C') {
					ccount++;
				}
				//else if( chr == 'a' || chr == 'A' ) acount++;
				//else if( chr == 't' || chr == 'T' ) tcount++;
			}

			if (gcount > 0 || ccount > 0) {
				return (seq.getGC()-1) * (gcount - ccount) / (double) (gcount + ccount);
			}
		}
		return 0.0;
	}

	public void setGCSkew(double gcskew) {
		this.gcskew = gcskew;
	}
	
	public double getGCSkew() {
		if (gcskew == -1.0) {
			gcskew = calcGcSkew();
		}
		return gcskew;
	}

	public double calcMinGC(Collection<Sequence> sequences) {
		return sequences.parallelStream().map(s -> s.annset).filter(Objects::nonNull).flatMapToDouble(as -> as.parallelStream().mapToDouble(Annotation::getGCPerc).min().stream()).min().getAsDouble();
	}

	public double calcMaxGC(Collection<Sequence> sequences) {
		return sequences.parallelStream().map(s -> s.annset).filter(Objects::nonNull).flatMapToDouble(as -> as.parallelStream().mapToDouble(Annotation::getGCPerc).max().stream()).max().getAsDouble();
	}

	public Color getGCColor(GeneGroup gg) {
		var seqlist = gg.genes.parallelStream().map(Annotation::getSeq).filter(Objects::nonNull).flatMap(s -> s.partof.parallelStream()).collect(Collectors.toList());
		float min = (float)calcMinGC(seqlist);
		float max = (float)calcMaxGC(seqlist);
		float diff = max-min;

		if( isDirty() ) return Color.red;
		double gcp = Math.min( Math.max( min, gc ), max );
		return new Color( (float)(max-gcp)/diff, (float)(gcp-min)/diff, 1.0f );
		
		/*double gcp = Math.min( Math.max( 0.35, gc ), 0.55 );
		return new Color( (float)(0.55-gcp)/0.2f, (float)(gcp-0.35)/0.2f, 1.0f );*/
	}

	public Color getGCColor() {
		float min = (float)calcMinGC(seq.partof);
		float max = (float)calcMaxGC(seq.partof);
		float diff = max-min;

		if( isDirty() ) return Color.red;
		double gcp = Math.min( Math.max( min, gc ), max );
		return new Color( (float)(max-gcp)/diff, (float)(gcp-min)/diff, 1.0f );

		/*double gcp = Math.min( Math.max( 0.35, gc ), 0.55 );
		return new Color( (float)(0.55-gcp)/0.2f, (float)(gcp-0.35)/0.2f, 1.0f );*/
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public Color getGCSkewColor() {
		return new Color( (float)Math.min( 1.0, Math.max( 0.0, 0.5+5.0*getGCSkew() ) ), 0.5f, (float)Math.min( 1.0, Math.max( 0.0, 0.5-5.0*getGCSkew() ) ) );
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

	public boolean isPseudo() {
		return getGene() == null && start == 0;
	}
	
	public Sequence getProteinSequence() {
		Sequence ret = seq == null ? null : seq.getProteinSequence(start, stop, ori);
		if( ret != null && this.name != null ) {
			int i = this.name.indexOf(' ');
			if( i != -1 ) ret.setName( this.name.substring(0,i) + " # " + this.start + " # " + this.stop + " # " + this.ori + " #" );
			else ret.setName( this.name );
			ret.setId(this.id);
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

	public Sequence getSeq() {
		return seq;
	}

	public void setSeq(Sequence seq) {
		this.seq = seq;
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

	public String getNote() {
		return note;
	}

	public void setNote( String note ) {
		this.note = note;
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

	public int clearPrefix(int window, List<Annotation> lann) {
		int ret = 0;
		/*int c = 0;
		if (this.getGene()!=null) {
			var t = this.getPrevious();
			while (t != null && t.getGene() == null && c < window) {
				c++;
				t = t.getPrevious();
			}
			if (c == window) {
				if (getContig().isReverse()) {
					ret = getContig().deleteAllAfter(this);
					for (var a : lann) {
						if (a.getGene() != null) {
							var ctg = a.getContig();
							if (ctg.isReverse()) ctg.deleteAllAfter(a);
							else ctg.deleteAllBefore(a);
						}
					}
				} else {
					ret = getContig().deleteAllBefore(this);
					for (var a : lann) {
						if (a.getGene() != null) {
							var ctg = a.getContig();
							if (ctg.isReverse()) ctg.deleteAllAfter(a);
							else ctg.deleteAllBefore(a);
						}
					}
				}
			}
		}*/
		return ret;
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
