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
	
	static Random r = new Random();
	
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
	
	public static void distanceMatrixNumeric( List<Sequence> lseq, double[] dmat, List<Integer> idxs, boolean bootstrap, boolean cantor, double[] ent ) {		
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
	
	public void clearCharAt( int i ) {
		int ind = i-start;
		if( ind >= 0 && ind < sb.length() ) {
			sb.setCharAt(ind, '-');
			edited = true;
		}
	}
	
	public char charAt( int i ) {
		int ind = i-start;
		if( ind >= 0 && ind < sb.length() ) {
			return sb.charAt( ind );
		}
		
		return '-';
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
	
	public String getSubstring( int start, int end ) {
		return sb.substring(start, end);
	}
}