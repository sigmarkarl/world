package org.simmi.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Sequence implements Comparable<Sequence> {
	/*public static int						max = 0;
	public static int						min = 0;
	
	public static ArrayList<Sequence>		lseq = new ArrayList<Sequence>() {
		private static final long serialVersionUID = 1L;

		public boolean add( Sequence seq ) {
			seq.index = Sequence.lseq.size();
			return super.add( seq );
		}
	};
	public static Map<String,Sequence>		mseq = new HashMap<String,Sequence>();
	public static ArrayList<Annotation>	lann = new ArrayList<Annotation>();
	public static Map<String,Annotation>	mann = new HashMap<String,Annotation>();*/
	
	static Map<String,String>	amimap = new HashMap<String,String>();
	static Map<String,String>	revcom = new HashMap<String,String>();
	static Map<Character,Character>	rc = new HashMap<Character,Character>();
	static {
		amimap.put("TTT","F");
		amimap.put("TTC","F");
		amimap.put("TTA","L");
		amimap.put("TTG","L");
		amimap.put("TCT","S");
		amimap.put("TCC","S");
		amimap.put("TCA","S");
		amimap.put("TCG","S");
		amimap.put("TAT","Y");
		amimap.put("TAC","Y");
		amimap.put("TAA","1");
		amimap.put("TAG","0");
		amimap.put("TGT","C");
		amimap.put("TGC","C");
		amimap.put("TGA","0");
		amimap.put("TGG","W");
		amimap.put("CTT","L");
		amimap.put("CTC","L");
		amimap.put("CTA","L");
		amimap.put("CTG","L");
		amimap.put("CCT","P");
		amimap.put("CCC","P");
		amimap.put("CCA","P");
		amimap.put("CCG","P");
		amimap.put("CAT","H");
		amimap.put("CAC","H");
		amimap.put("CAA","Q");
		amimap.put("CAG","Q");
		amimap.put("CGT","R");
		amimap.put("CGC","R");
		amimap.put("CGA","R");
		amimap.put("CGG","R");
		amimap.put("ATT","I");
		amimap.put("ATC","I");
		amimap.put("ATA","I");
		amimap.put("ATG","M");
		amimap.put("ACT","T");
		amimap.put("ACC","T");
		amimap.put("ACA","T");
		amimap.put("ACG","T");
		amimap.put("AAT","N");
		amimap.put("AAC","N");
		amimap.put("AAA","K");
		amimap.put("AAG","K");
		amimap.put("AGT","S");
		amimap.put("AGC","S");
		amimap.put("AGA","R");
		amimap.put("AGG","R");
		amimap.put("GTT","V");
		amimap.put("GTC","V");
		amimap.put("GTA","V");
		amimap.put("GTG","V");
		amimap.put("GCT","A");
		amimap.put("GCC","A");
		amimap.put("GCA","A");
		amimap.put("GCG","A");
		amimap.put("GAT","D");
		amimap.put("GAC","D");
		amimap.put("GAA","E");
		amimap.put("GAG","E");
		amimap.put("GGT","G");
		amimap.put("GGC","G");
		amimap.put("GGA","G");
		amimap.put("GGG","G");
		
		revcom.put("TTT","AAA");
		revcom.put("TTC","GAA");
		revcom.put("TTA","TAA");
		revcom.put("TTG","CAA");
		revcom.put("TCT","AGA");
		revcom.put("TCC","GGA");
		revcom.put("TCA","TGA");
		revcom.put("TCG","CGA");
		revcom.put("TAT","ATA");
		revcom.put("TAC","GTA");
		revcom.put("TAA","TTA");
		revcom.put("TAG","CTA");
		revcom.put("TGT","ACA");
		revcom.put("TGC","GCA");
		revcom.put("TGA","TCA");
		revcom.put("TGG","CCA");
		revcom.put("CTT","AAG");
		revcom.put("CTC","GAG");
		revcom.put("CTA","TAG");
		revcom.put("CTG","CAG");
		revcom.put("CCT","AGG");
		revcom.put("CCC","GGG");
		revcom.put("CCA","TGG");
		revcom.put("CCG","CGG");
		revcom.put("CAT","ATG");
		revcom.put("CAC","GTG");
		revcom.put("CAA","TTG");
		revcom.put("CAG","CTG");
		revcom.put("CGT","ACG");
		revcom.put("CGC","GCG");
		revcom.put("CGA","TCG");
		revcom.put("CGG","CCG");
		revcom.put("ATT","AAT");
		revcom.put("ATC","GAT");
		revcom.put("ATA","TAT");
		revcom.put("ATG","CAT");
		revcom.put("ACT","AGT");
		revcom.put("ACC","GGT");
		revcom.put("ACA","TGT");
		revcom.put("ACG","CGT");
		revcom.put("AAT","ATT");
		revcom.put("AAC","GTT");
		revcom.put("AAA","TTT");
		revcom.put("AAG","CTT");
		revcom.put("AGT","ACT");
		revcom.put("AGC","GCT");
		revcom.put("AGA","TCT");
		revcom.put("AGG","CCT");
		revcom.put("GTT","AAC");
		revcom.put("GTC","GAC");
		revcom.put("GTA","TAC");
		revcom.put("GTG","CAC");
		revcom.put("GCT","AGC");
		revcom.put("GCC","GGC");
		revcom.put("GCA","TGC");
		revcom.put("GCG","CGC");
		revcom.put("GAT","ATC");
		revcom.put("GAC","GTC");
		revcom.put("GAA","TTC");
		revcom.put("GAG","CTC");
		revcom.put("GGT","ACC");
		revcom.put("GGC","GCC");
		revcom.put("GGA","TCC");
		revcom.put("GGG","CCC");
		
		rc.put('A', 'T');
		rc.put('C', 'G');
		rc.put('G', 'C');
		rc.put('T', 'A');
	}
	
	public String 				name;
	public String				id;
	public StringBuilder	 	sb;
	public int					start = 0;
	public int					revcomp = 0;
	int							gcp = -1;
	int							alignedlength = -1;
	int							unalignedlength = -1;
	int							substart = -1;
	int							substop = 0;
	public List<Annotation>		annset;
	public int					index = -1;
	public boolean				edited = false;
	public boolean				selected = false;
	
	static final Random r = new Random();
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected( boolean sel ) {
		this.selected = sel;
	}
	
	public void removeGaps() {
		
	}
	
	public static String getPhylip( List<Sequence> lseq, boolean numeric ) {
		StringBuilder out = new StringBuilder();
		
		for( Sequence seq : lseq ) {
			System.err.println( seq.getName() );
		}
		
		String erm = ""+lseq.size();
		String seqlen = "";
		for( int i = 0; i < 6-erm.length(); i++ ) {
			seqlen += " ";
		}
		seqlen += erm;
		int alen = lseq.get(0).getLength();
		seqlen += "   "+alen;
		
		out.append( seqlen+"\n" );
		
		Map<String,Integer> seqset = new HashMap<String,Integer>();
		
		int u = 0;
		int count = 0;
		for( int k = 0; k < alen; k+=50 ) {
			int seqi = 0;
			for( Sequence seq : lseq ) {
				if( u == 0 ) {
					if( !numeric ) {
						String seqname = seq.getName();
						int m = Math.min( seqname.length(), 10 );
						
						String subname = seqname.substring(0, m);
						
						int scount = 1;
						String newname;
						if( seqset.containsKey( subname ) ) {
							scount = seqset.get( subname )+1;
							//if( seqname.length() > 10 ) {
							//	subname = seqname.substring( seqname.length()-10, seqname.length() );
							//} else {
							String cstr = ""+scount;
								m = Math.min( seqname.length(), 10-cstr.length() );
								newname = seqname.substring(0,m)+cstr;
							//}
						} else newname = subname;
						seqset.put( subname, scount );
						
						out.append( newname );
						while( m < 10 ) {
							out.append(' ');
							m++;
						}
					} else {
						String sind = Integer.toString( seqi++ );
						
						int m = 0;
						while( m < 10-sind.length() ) {
							out.append('0');
							m++;
						}
						out.append( sind );
					}
				} else out.append("          ");
				
				for( int l = k; l < Math.min(k+50, alen); l++ ) {
					if( l % 10 == 0 ) {
						out.append(" ");
					}
					out.append( seq.charAt(l + seq.getStart()) );
				}
				out.append("\n");
			}
			out.append("\n");
			
			u++;
		}
		
		return out.toString();
	}
	
	public static double[] entropy( List<Sequence> lseq ) {
		int total = lseq.get(0).getLength();
		double[] ret = new double[total];
		Map<Character,Integer>	shanmap = new HashMap<Character,Integer>();
		for( int x = 0; x < total; x++ ) {
			shanmap.clear();
			
			int count = 0;
			for( Sequence seq : lseq ) {
				char c = seq.charAt( x );
				if( c != '.' && c != '-' && c != ' ' && c != '\n' ) {
					int val = 0;
					if( shanmap.containsKey(c) ) val = shanmap.get(c);
					shanmap.put( c, val+1 );
					count++;
				}
			}
			
			double res = 0.0;
			for( char c : shanmap.keySet() ) {
				int val = shanmap.get(c);
				double p = (double)val/(double)count;
				res -= p*Math.log(p);
			}
			ret[x] = res/Math.log(2.0);
		}
		return ret;
	}
	
	public final static void distanceMatrixNumeric( List<Sequence> lseq, double[] dmat, List<Integer> idxs, boolean bootstrap, boolean cantor, double[] ent ) {		
		int len = lseq.size();
		for( int x = 0; x < lseq.size(); x++ ) {
			dmat[x*len+x] = 0.0;
		}
		if( idxs != null && idxs.size() > 0 ) {
			int count = idxs.size();
			for( int x = 0; x < len-1; x++ ) {
				for( int y = x+1; y < len; y++ ) {
					Sequence seq1 = lseq.get(x);
					Sequence seq2 = lseq.get(y);
					
					//if( seq1 == seq2 ) dmat[i] = 0.0;
					//else {
						double mism = 0;
						
						if( ent != null ) {
							int i = 0;
							if( bootstrap ) {
								for( int k : idxs ) {
									int ir = r.nextInt( idxs.size() );
									int u = idxs.get(ir);
									char c1 = seq1.charAt( u-seq1.getStart() );
									char c2 = seq2.charAt( u-seq2.getStart() );
									
									if( c1 != c2 ) mism += 1.0/ent[u];
									//count++;
									i++;
								}
							} else {
								for( int k : idxs ) {
									char c1 = seq1.charAt( k-seq1.getStart() );
									char c2 = seq2.charAt( k-seq2.getStart() );
									
									if( c1 != c2 ) mism += 1.0/ent[k];
									i++;
								}
							}
						} else {
							if( bootstrap ) {
								for( int k : idxs ) {
									int ir = r.nextInt( idxs.size() );
									char c1 = seq1.charAt( idxs.get(ir)-seq1.getStart() );
									char c2 = seq2.charAt( idxs.get(ir)-seq2.getStart() );
									
									if( c1 != c2 ) mism++;
								}
							} else {
								for( int k : idxs ) {
									char c1 = seq1.charAt( k-seq1.getStart() );
									char c2 = seq2.charAt( k-seq2.getStart() );
									
									if( c1 != c2 ) mism++;
								}
							}
						}
						double d = mism/(double)count;
						if( cantor ) d = -3.0*Math.log( 1.0 - 4.0*d/3.0 )/4.0;
						dmat[x*len+y] = d;
						dmat[y*len+x] = d;
					//}
					//i++;
				}
			}
		} else {
			for( int x = 0; x < len-1; x++ ) {
				for( int y = x+1; y < len; y++ ) {
					Sequence seq1 = lseq.get(x);
					Sequence seq2 = lseq.get(y);
						
					int count = 0;
					double mism = 0;
					
					int start = Math.max( seq1.getRealStart(), seq2.getRealStart() );
					int end = Math.min( seq1.getRealStop(), seq2.getRealStop() );
					
					if( ent != null ) {
						/*if( start < 0 || end >= ent.length ) {
							System.err.println( "mu " + ent.length );
							System.err.println( start + "  " + end );
						}*/
						
						if( bootstrap ) {
							for( int k = start; k < end; k++ ) {
								int ir = start + r.nextInt( end-start );
								char c1 = seq1.charAt( ir-seq1.getStart() );
								char c2 = seq2.charAt( ir-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
									if( c1 != c2 ) mism += 1.0/ent[ir];
									count++;
								}
							}
						} else {
							for( int k = start; k < end; k++ ) {
								char c1 = seq1.charAt( k-seq1.getStart() );
								char c2 = seq2.charAt( k-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
									if( c1 != c2 ) {
										mism += 1.0/ent[k];
										/*if( ent[k] == 0.0 ) {
											System.err.println("ok");
										}*/
									}
									count++;
								}
							}
						}
					} else {
						if( bootstrap ) {
							for( int k = start; k < end; k++ ) {
								int ir = start + r.nextInt( end-start );
								char c1 = seq1.charAt( ir-seq1.getStart() );
								char c2 = seq2.charAt( ir-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
									if( c1 != c2 ) mism++;
									count++;
								}
							}
						} else {
							for( int k = start; k < end; k++ ) {
								char c1 = seq1.charAt( k-seq1.getStart() );
								char c2 = seq2.charAt( k-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
									if( c1 != c2 ) mism++;
									count++;
								}
							}
						}
					}
					double d = count == 0 ? 0.0 : mism/(double)count;
					if( cantor ) d = -3.0*Math.log( 1.0 - 4.0*d/3.0 )/4.0;
					
					dmat[x*len+y] = d;
					dmat[y*len+x] = d;
				}
			}
		}
		
		//return dmat;
	}
	
	public void reverse() {
		for( int i = 0; i < getLength()/2; i++ ) {
			char c = sb.charAt(i);
			sb.setCharAt( i, sb.charAt(getLength()-1-i) );
			sb.setCharAt( getLength()-1-i, c );
		}
	}
	
	public final static Map<Character,Character>	complimentMap = new HashMap<Character,Character>();
	static {
		complimentMap.put( 'A', 'T' );
		complimentMap.put( 'T', 'A' );
		complimentMap.put( 'G', 'C' );
		complimentMap.put( 'C', 'G' );
		complimentMap.put( 'a', 't' );
		complimentMap.put( 't', 'a' );
		complimentMap.put( 'g', 'c' );
		complimentMap.put( 'c', 'g' );
	};
	
	public void complement() {
		for( int i = 0; i < getLength(); i++ ) {
			char c = sb.charAt(i);
			sb.setCharAt( i, complimentMap.get(c) );
		}
	}
	
	public void upperCase() {
		
	}
	
	public void caseSwap() {
		
	}
	
	public void utReplace() {
		int i1 = sb.indexOf("T");
		int i2 = sb.indexOf("U");
		
		if( i1 == -1 ) i1 = sb.length();
		if( i2 == -1 ) i2 = sb.length();
		
		while( i1 < sb.length() || i2 < sb.length() ) {
			while( i1 < i2 ) {
				sb.setCharAt(i1, 'U');
				i1 = sb.indexOf("T", i1+1);
				if( i1 == -1 ) i1 = sb.length();
			}
			
			while( i2 < i1 ) {
				sb.setCharAt(i2, 'T');
				i2 = sb.indexOf("U", i2+1);
				if( i2 == -1 ) i2 = sb.length();
			}
		}
		
		i1 = sb.indexOf("t");
		i2 = sb.indexOf("u");
		
		if( i1 == -1 ) i1 = sb.length();
		if( i2 == -1 ) i2 = sb.length();
		
		while( i1 < sb.length() || i2 < sb.length() ) {
			while( i1 < i2 ) {
				sb.setCharAt(i1, 'u');
				i1 = sb.indexOf("t", i1+1);
				if( i1 == -1 ) i1 = sb.length();
			}
			
			while( i2 < i1 ) {
				sb.setCharAt(i2, 't');
				i2 = sb.indexOf("u", i2+1);
				if( i2 == -1 ) i2 = sb.length();
			}
		}
	}
	
	public class Annotation implements Comparable<Annotation> {
		public Sequence			seq;
		public String			name;
		public StringBuilder	desc;
		public String			type;
		public String			group;
		public int				start;
		public int				stop;
		public int				ori;
		public Object			color;
		
		public Annotation( Sequence seq, String name, Object color, int start, int stop, Map<String,Annotation> mann ) {
			this( seq, name, color, mann );
			this.setStart( start );
			this.setStop( stop );
		}
		
		public Annotation( Sequence seq, String name, Object color, Map<String,Annotation>  mann ) {
			this.name = name;
			this.color = color;
			this.seq = seq;
			
			if( seq != null ) {
				seq.addAnnotation( this );
			}
			mann.put( name, this );
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
		
		public void append( String astr ) {
			if( desc == null ) desc = new StringBuilder( astr );
			else desc.append( astr );
		}

		@Override
		public int compareTo(Annotation o) {
			return start - o.start;
		}
	};
	
	public boolean isEdited() {
		return edited;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId( String id ) {
		this.id = id;
	}
	
	public void setSequenceString( StringBuilder sb ) {
		this.sb = sb;
	}
	
	public Sequence( String id, String name, Map<String,Sequence> mseq ) {
		this( name, mseq );
		this.id = id;
	}
	
	public Sequence( String name, Map<String,Sequence> mseq ) {
		this.name = name;
		sb = new StringBuilder();
		if( mseq != null ) mseq.put( name, this );
	}
	
	public Sequence( String id, String name, StringBuilder sb, Map<String,Sequence> mseq ) {
		this( name, sb, mseq );
		this.id = id;
	}
	
	public Sequence( String name, StringBuilder sb, Map<String,Sequence> mseq ) {
		this.name = name;
		this.sb = sb;
		this.id = name;
		if( mseq != null ) mseq.put( name, this );
	}
	
	public List<Annotation> getAnnotations() {
		return annset;
	}
	
	public void addAnnotation( Annotation a ) {
		if( annset == null ) {
			annset = new ArrayList<Annotation>();
		}
		annset.add( a );
	}
	
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public boolean equals( Object obj ) {			
		/*boolean ret = name.equals( obj.toString() ); //super.equals( obj );
		System.err.println( "erm " + this.toString() + " " + obj.toString() + "  " + ret );
		return ret;*/			
		return super.equals( obj );
	}
	
	public StringBuilder getStringBuilder() {
		return sb;
	}
	
	public String toString() {
		return name;
	}
	
	public void append( String str ) {
		sb.append( str );
	}
	
	public void deleteCharAt( int i ) {
		int ind = i-start;
		if( ind >= 0 && ind < sb.length() ) {
			sb.deleteCharAt(ind);
			edited = true;
		}
	}
	
	public void delete( int dstart, int dstop ) {
		int ind = dstart-start;
		int end = dstop-start;
		if( ind >= 0 && end <= sb.length() ) {
			sb.delete( ind, end );
			edited = true;
		}
	}
	
	public void clearCharAt( int i ) {
		int ind = i-start;
		if( ind >= 0 && ind < sb.length() ) {
			sb.setCharAt(ind, '-');
			edited = true;
		}
	}
	
	public void setCharAt( int i, char c ) {
		int ind = i-start;
		if( ind >= 0 && ind < sb.length() ) {
			sb.setCharAt( ind, c );
		}
	}
	
	public char charAt( int i ) {
		int ind = i-start;
		if( ind >= 0 && ind < getLength() ) {
			return sb.charAt( ind );
		}
		
		return ' ';
	}
	
	public void checkLengths() {
		//int start = -1;
		//int stop = 0;
		int count = 0;
		for( int i = 0; i < sb.length(); i++ ) {
			char c = sb.charAt(i);
			if( c != '.' && c != '-' && c != ' ' ) {
				if( substart == -1 ) substart = i;
				substop = i;
				count++;
			}
		}
		alignedlength = count;
		unalignedlength = substop-substart;
	}
	
	public int getLength() {
		return sb.length();
	}
	
	public int getAlignedLength() {
		if( alignedlength == -1 ) {
			checkLengths();
		}
		return alignedlength;
	}
	
	public int getUnalignedLength() {
		if( unalignedlength == -1 ) {
			checkLengths();
		}
		return unalignedlength;
	}
	
	public int getRealStart() {
		return getStart() + substart;
	}
	
	public int getRealStop() {
		return getStart() + substop;
	}
	
	public int getRealLength() {
		return substop - substart;
	}
	
	/*public void boundsCheck() {
		if( start < min ) min = start;
		if( start+sb.length() > max ) max = start+sb.length();
	}*/
	
	public interface RunInt {
		public void run( Sequence s );
	};
	
	public static RunInt runbl = null;
	public void setStart( int start ) {
		this.start = start;
		
		if( runbl != null ) runbl.run( this ); //boundsCheck();
	}
	
	public void setEnd( int end ) {
		this.start = end-sb.length();
		
		if( runbl != null ) runbl.run( this );
		//boundsCheck();
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return start+sb.length();
	}
	
	public int getRevComp() {
		return revcomp;
	}
	
	public int getGCP() {
		if( gcp == -1 && sb.length() > 0 ) {
			gcp = 0;
			int count = 0;
			for( int i = 0; i < sb.length(); i++ ) {
				char c = sb.charAt(i);
				if( c == 'G' || c == 'g' || c == 'C' || c == 'c' ) {
					gcp++;
					count++;
				} else if( c == 'T' || c == 't' || c == 'A' || c == 'a' ) {
					count++;
				}
			}
			gcp = count > 0 ? 100*gcp/count : 0;
		}
		return gcp;
	}

	@Override
	public int compareTo(Sequence o) {
		return start - o.start;
	}
	
	public StringBuilder getProteinSequence( int start, int stop, int ori ) {
		StringBuilder ret = new StringBuilder();
		
		//if( stop > sb.length() ) {
		//if( stop != end ) {
		//	System.err.println();
		//}
		
		if( ori == -1 ) {
			int begin = stop - 1 - 3*((stop-start)/3);
			
			//String aaa = sb.substring(start-1, start+2);
			//String aa = amimap.get( aaa );
			
			//String aaa = sb.substring(stop-2, stop+1);
			//String aa = amimap.get( revcom.get(aaa) );
			
			//System.err.println( aa );
			for( int i = stop-3; i > begin; i-=3 ) {
				String aaa = sb.substring(i, i+3);
				String aa = amimap.get( revcom.get(aaa) );
				if( aa != null ) ret.append( i != stop-3 ? aa : (aa.equals("V") || aa.equals("L") ? "M" : aa) );
				//else break;
			}
		} else {
			int end = start - 1 + 3*((stop-start)/3);
			for( int i = start-1; i < end; i+=3 ) {
				String aaa = sb.substring(i, i+3);
				String aa = amimap.get( aaa );
				if( aa != null ) ret.append( i != start-1 ? aa : (aa.equals("V") || aa.equals("L") ? "M" : aa) );
				//else break;
			}
		}
		
		return ret;
	}
	
	public String getSubstring( int start, int end ) {
		return sb.substring(start, end);
	}
}