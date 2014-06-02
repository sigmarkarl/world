package org.simmi.shared;

import java.awt.Color;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Sequence implements Comparable<Sequence> {
	/*public static int						max = 0;
	public static int						min = 0;s
	
	public static ArrayList<Sequence>		lseq = new ArrayList<Sequence>() {
		private static final long serialVersionUID = 1L;

		public boolean add( Sequence seq ) {
			seq.index = Sequence.lseq.size();
			return super.add( Oseq );
		}
	};
	public static Map<String,Sequence>		mseq = new HashMap<String,Sequence>();
	public static ArrayList<Annotation>	lann = new ArrayList<Annotation>();
	public static Map<String,Annotation>	mann = new HashMap<String,Annotation>();*/
	
	public static Map<Character, Color> 		aacolor = new HashMap<Character, Color>();
	static Map<Character, Character> 	sidechainpolarity = new HashMap<Character, Character>();
	static Map<Character, Integer> 		sidechaincharge = new HashMap<Character, Integer>();
	static Map<Character, Double> 		hydropathyindex = new HashMap<Character, Double>();
	static Map<Character, Double> 		aamass = new HashMap<Character, Double>();
	static Map<Character, Double> 		isoelectricpoint = new HashMap<Character, Double>();

	public static Color[] colorCodes = new Color[9];

	// abundance
	// aliphatic - aromatic
	// size
	// sortcoeff

	public static List<Erm> uff = new ArrayList<Erm>();
	public static List<Erm> uff2 = new ArrayList<Erm>();
	public static List<Erm> uff3 = new ArrayList<Erm>();
	public static List<Erm> mass = new ArrayList<Erm>();
	public static List<Erm> isoel = new ArrayList<Erm>();
	
	static Map<String,String>				amimap = new HashMap<String,String>();
	static Map<String,String>				revcom = new HashMap<String,String>();
	public static Map<Character,Character>	rc = new HashMap<Character,Character>();
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
		amimap.put("TAA","X");
		amimap.put("TAG","X");
		amimap.put("TGT","C");
		amimap.put("TGC","C");
		amimap.put("TGA","X");
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
		rc.put('N', 'N');
		rc.put('X', 'X');
		rc.put('a', 't');
		rc.put('c', 'g');
		rc.put('g', 'c');
		rc.put('t', 'a');
		rc.put('n', 'n');
		rc.put('x', 'x');
		rc.put('-', '-');
		
		aacolor.put('A', new Color(0.0f,0.0f,1.0f));
		aacolor.put('R', new Color(0.0f,1.0f,1.0f));
		aacolor.put('N', new Color(0.0f,1.0f,0.0f));
		aacolor.put('D', new Color(1.0f,1.0f,0.0f));
		aacolor.put('C', new Color(1.0f,0.0f,0.0f));
		aacolor.put('E', new Color(1.0f,0.0f,1.0f));
		aacolor.put('Q', new Color(0.5f,0.5f,0.1f));
		aacolor.put('G', new Color(0.5f,1.0f,1.0f));
		aacolor.put('H', new Color(0.5f,1.0f,0.5f));
		aacolor.put('I', new Color(1.0f,1.0f,0.5f));
		aacolor.put('L', new Color(1.0f,0.5f,0.5f));
		aacolor.put('K', new Color(1.0f,0.5f,1.0f));
		aacolor.put('M', new Color(0.0f,1.0f,0.5f));
		aacolor.put('F', new Color(0.0f,0.5f,1.0f));
		aacolor.put('P', new Color(1.0f,0.5f,0.0f));
		aacolor.put('S', new Color(0.5f,1.0f,0.0f));
		aacolor.put('T', new Color(1.0f,0.0f,0.5f));
		aacolor.put('W', new Color(0.5f,0.0f,1.0f));
		aacolor.put('Y', new Color(0.8f,0.0f,0.8f));
		aacolor.put('V', new Color(0.8f,0.8f,0.0f));
		
		sidechainpolarity.put('A', 'n');
		sidechainpolarity.put('R', 'p');
		sidechainpolarity.put('N', 'p');
		sidechainpolarity.put('D', 'p');
		sidechainpolarity.put('C', 'n');
		sidechainpolarity.put('E', 'p');
		sidechainpolarity.put('Q', 'p');
		sidechainpolarity.put('G', 'n');
		sidechainpolarity.put('H', 'p');
		sidechainpolarity.put('I', 'n');
		sidechainpolarity.put('L', 'n');
		sidechainpolarity.put('K', 'p');
		sidechainpolarity.put('M', 'n');
		sidechainpolarity.put('F', 'n');
		sidechainpolarity.put('P', 'n');
		sidechainpolarity.put('S', 'p');
		sidechainpolarity.put('T', 'p');
		sidechainpolarity.put('W', 'n');
		sidechainpolarity.put('Y', 'p');
		sidechainpolarity.put('V', 'n');

		sidechaincharge.put('A', 0);
		sidechaincharge.put('R', 1);
		sidechaincharge.put('N', 0);
		sidechaincharge.put('D', -1);
		sidechaincharge.put('C', 0);
		sidechaincharge.put('E', -1);
		sidechaincharge.put('Q', 0);
		sidechaincharge.put('G', 0);
		sidechaincharge.put('H', 0);
		sidechaincharge.put('I', 0);
		sidechaincharge.put('L', 0);
		sidechaincharge.put('K', 1);
		sidechaincharge.put('M', 0);
		sidechaincharge.put('F', 0);
		sidechaincharge.put('P', 0);
		sidechaincharge.put('S', 0);
		sidechaincharge.put('T', 0);
		sidechaincharge.put('W', 0);
		sidechaincharge.put('Y', 0);
		sidechaincharge.put('V', 0);

		hydropathyindex.put('A', 1.8);
		hydropathyindex.put('R', -4.5);
		hydropathyindex.put('N', -3.5);
		hydropathyindex.put('D', -3.5);
		hydropathyindex.put('C', 2.5);
		hydropathyindex.put('E', -3.5);
		hydropathyindex.put('Q', -3.5);
		hydropathyindex.put('G', -0.4);
		hydropathyindex.put('H', -3.2);
		hydropathyindex.put('I', 4.5);
		hydropathyindex.put('L', 3.8);
		hydropathyindex.put('K', -3.9);
		hydropathyindex.put('M', 1.9);
		hydropathyindex.put('F', 2.8);
		hydropathyindex.put('P', -1.6);
		hydropathyindex.put('S', -0.8);
		hydropathyindex.put('T', -0.7);
		hydropathyindex.put('W', -0.9);
		hydropathyindex.put('Y', -1.3);
		hydropathyindex.put('V', 4.2);

		aamass.put('A', 89.09404);
		aamass.put('C', 121.15404);
		aamass.put('D', 133.10384);
		aamass.put('E', 147.13074);
		aamass.put('F', 165.19184);
		aamass.put('G', 75.06714);
		aamass.put('H', 155.15634);
		aamass.put('I', 131.17464);
		aamass.put('K', 146.18934);
		aamass.put('L', 131.17464);
		aamass.put('M', 149.20784);
		aamass.put('N', 132.11904);
		aamass.put('O', 100.0);
		aamass.put('P', 115.13194);
		aamass.put('Q', 146.14594);
		aamass.put('R', 174.20274);
		aamass.put('S', 105.09344);
		aamass.put('T', 119.12034);
		aamass.put('U', 168.053);
		aamass.put('V', 117.14784);
		aamass.put('W', 204.22844);
		aamass.put('Y', 181.19124);

		isoelectricpoint.put('A', 6.01);
		isoelectricpoint.put('C', 5.05);
		isoelectricpoint.put('D', 2.85);
		isoelectricpoint.put('E', 3.15);
		isoelectricpoint.put('F', 5.49);
		isoelectricpoint.put('G', 6.06);
		isoelectricpoint.put('H', 7.6);
		isoelectricpoint.put('I', 6.05);
		isoelectricpoint.put('K', 9.6);
		isoelectricpoint.put('L', 6.01);
		isoelectricpoint.put('M', 5.74);
		isoelectricpoint.put('N', 5.41);
		isoelectricpoint.put('O', 21.0);
		isoelectricpoint.put('P', 6.3);
		isoelectricpoint.put('Q', 5.65);
		isoelectricpoint.put('R', 10.76);
		isoelectricpoint.put('S', 5.68);
		isoelectricpoint.put('T', 5.6);
		isoelectricpoint.put('U', 20.0);
		isoelectricpoint.put('V', 6.0);
		isoelectricpoint.put('W', 5.89);
		isoelectricpoint.put('Y', 5.64);

		for (char c : hydropathyindex.keySet()) {
			double d = hydropathyindex.get(c);
			uff.add(new Erm(d, c));
		}
		Collections.sort(uff);

		for (char c : sidechainpolarity.keySet()) {
			double d = sidechainpolarity.get(c);
			uff2.add(new Erm(d, c));
		}
		Collections.sort(uff2);

		for (char c : sidechaincharge.keySet()) {
			double d = sidechaincharge.get(c);
			uff3.add(new Erm(d, c));
		}
		Collections.sort(uff3);

		for (char c : aamass.keySet()) {
			double d = aamass.get(c);
			mass.add(new Erm(d, c));
		}
		Collections.sort(mass);

		for (char c : isoelectricpoint.keySet()) {
			double d = isoelectricpoint.get(c);
			isoel.add(new Erm(d, c));
		}
		Collections.sort(isoel);
	}
	
	public void initIndexBuffers() {
		ib = IntBuffer.allocate( sb.length() );
		int count = 0;
		for( int i = 0; i < sb.length(); i++ ) {
			ib.put( i, count );
			char c = sb.charAt(i);
			if( c != '.' && c != '-' && c != ' ' ) {
				count++;
			}
		}
		rib = IntBuffer.allocate( count );
		count = 0;
		for( int i = 0; i < sb.length(); i++ ) {
			char c = sb.charAt(i);
			if( c != '.' && c != '-' && c != ' ' ) {
				rib.put( count, i );
				count++;
			}
		}
	}
	
	public Sequence				consensus;
	public String 				name;
	//public String				group;
	public String				id;
	public StringBuilder	 	sb;
	public IntBuffer			ib = null;
	public IntBuffer			rib = null;
	public int					offset = 0;
	private int					revcomp = 0;
	int							gcp = -1;
	int							alignedlength = -1;
	int							unalignedlength = -1;
	int							substart = -1;
	int							substop = 0;
	public List<Annotation>		annset;
	public int					index = -1;
	public boolean				edited = false;
	public boolean				selected = false;
	
	public boolean isReverse() {
		return revcomp == -1;
	}
	
	public void setReverse( boolean rev ) {
		revcomp = rev ? -1 : 1;
	}
	
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
		int alen = lseq.get(0).length();
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
					out.append( seq.getCharAt(l + seq.getStart()) );
				}
				out.append("\n");
			}
			out.append("\n");
			
			u++;
		}
		
		return out.toString();
	}
	
	public static double[] entropy( List<Sequence> lseq ) {
		int total = lseq.get(0).length();
		double[] ret = new double[total];
		Map<Character,Integer>	shanmap = new HashMap<Character,Integer>();
		for( int x = 0; x < total; x++ ) {
			shanmap.clear();
			
			int count = 0;
			for( Sequence seq : lseq ) {
				char c = seq.getCharAt( x );
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
	
	public final static void distanceMatrixNumeric( List<Sequence> lseq, double[] dmat, List<Integer> idxs, boolean bootstrap, boolean cantor, double[] ent, Map<String,Integer> blosum ) {		
		int len = lseq.size();
		//double[] dmat = new double[ len*len ];
		for( int x = 0; x < len; x++ ) {
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
									char c1 = seq1.getCharAt( u-seq1.getStart() );
									char c2 = seq2.getCharAt( u-seq2.getStart() );
									
									if( c1 != c2 ) mism += 1.0/ent[u];
									//count++;
									i++;
								}
							} else {
								for( int k : idxs ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
									if( c1 != c2 ) mism += 1.0/ent[k];
									i++;
								}
							}
						} else {
							if( bootstrap ) {
								for( int k : idxs ) {
									int ir = r.nextInt( idxs.size() );
									char c1 = seq1.getCharAt( idxs.get(ir)-seq1.getStart() );
									char c2 = seq2.getCharAt( idxs.get(ir)-seq2.getStart() );
									
									if( c1 != c2 ) mism++;
								}
							} else {
								for( int k : idxs ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
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
								char c1 = seq1.getCharAt( ir-seq1.getStart() );
								char c2 = seq2.getCharAt( ir-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
									if( c1 != c2 ) mism += 1.0/ent[ir];
									count++;
								}
							}
						} else {
							if( blosum != null ) {
								/////-----
								for( int k = start; k < end; k++ ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
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
							} else {
								for( int k = start; k < end; k++ ) {
									char c1 = seq1.getCharAt( k-seq1.getStart() );
									char c2 = seq2.getCharAt( k-seq2.getStart() );
									
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
						}
					} else {
						if( bootstrap ) {
							for( int k = start; k < end; k++ ) {
								int ir = start + r.nextInt( end-start );
								char c1 = seq1.getCharAt( ir-seq1.getStart() );
								char c2 = seq2.getCharAt( ir-seq2.getStart() );
								
								if( c1 != '.' && c1 != '-' && c1 != ' ' && c1 != '\n' &&  c2 != '.' && c2 != '-' && c2 != ' ' && c2 != '\n') {
									if( c1 != c2 ) mism++;
									count++;
								}
							}
						} else {
							for( int k = start; k < end; k++ ) {
								char c1 = seq1.getCharAt( k-seq1.getStart() );
								char c2 = seq2.getCharAt( k-seq2.getStart() );
								
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
	
	public void reverseComplement() {
		for( int i = 0; i < length()/2; i++ ) {
			char c = sb.charAt(i);
			char cc = rc.containsKey( c ) ? rc.get(c) : c;
			
			char nc = sb.charAt(length()-1-i);
			char ncc = rc.containsKey( nc ) ? rc.get(nc) : nc;
			
			sb.setCharAt( i, ncc );
			sb.setCharAt( length()-1-i, cc );
		}
	}
	
	public void reverse( int start, int end ) {
		System.err.println( sb.substring(start,end) + " " + getName() );
		for( int i = start; i < start+(end-start)/2; i++ ) {
			char c = sb.charAt(i);
			int ri = end-1-i+start;
			sb.setCharAt( i, sb.charAt(ri) );
			sb.setCharAt( ri, c );
		}
	}
	
	public void reverse() {
		for( int i = 0; i < length()/2; i++ ) {
			char c = sb.charAt(i);
			sb.setCharAt( i, sb.charAt(length()-1-i) );
			sb.setCharAt( length()-1-i, c );
		}
	}
	
	public void complement( int start, int end ) {
		for( int i = start; i < end; i++ ) {
			char c = sb.charAt(i);
			char cc = rc.containsKey( c ) ? rc.get(c) : c;
			/*if( c == rc ) {
				System.err.println();
			}*/
			sb.setCharAt( i, cc );
		}
	}
		
	public void complement() {
		for( int i = 0; i < length(); i++ ) {
			char c = sb.charAt(i);
			char cc = rc.containsKey( c ) ? rc.get(c) : c;
			/*if( c == rc ) {
				System.err.println();
			}*/
			sb.setCharAt( i, cc );
		}
	}
	
	public void upperCase() {
		for( int i = 0; i < sb.length(); i++ ) {
			sb.setCharAt( i, Character.toUpperCase(sb.charAt(i)) );
		}
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
	
	public boolean isEdited() {
		return edited;
	}
	
	public String getGroup() {
		return id;
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
	
	public Sequence() {
		super();
		sb = new StringBuilder();
	}
	
	public Sequence( String id, String name, Map<String,Sequence> mseq ) {
		this( name, mseq );
		this.id = id;
	}
	
	public Sequence( String name, Map<String,Sequence> mseq ) {
		this();
		this.name = name;
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
		if( mseq != null ) {
			//int millind = name.indexOf('#');
			//if( millind == -1 ) millind = name.length();
			//String val = name.substring( 0, millind ).trim();
			mseq.put( name, this );
		}
	}
	
	public List<Annotation> getAnnotations() {
		return annset;
	}
	
	public void removeAnnotation( Annotation a ) {
		if( annset != null ) {
			annset.remove( a );
		}
	}
	
	public void addAnnotation( Annotation a ) {
		if( annset == null ) {
			annset = new ArrayList<Annotation>();
		}
		if( annset.contains(a) ) {
			System.err.println();
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
	
	public void append( CharSequence cs ) {
		sb.append( cs );
	}
	
	public void append( String str ) {
		sb.append( str );
	}
	
	public void deleteCharAt( int i ) {
		int ind = i-offset;
		if( ind >= 0 && ind < sb.length() ) {
			sb.deleteCharAt(ind);
			edited = true;
		}
	}
	
	public void delete( int dstart, int dstop ) {
		int ind = dstart-offset;
		int end = dstop-offset;
		if( ind >= 0 && end <= sb.length() ) {
			sb.delete( ind, end );
			edited = true;
		}
	}
	
	public char charAt( int i ) {
		return revcomp == -1 ? revCompCharAt( i ) : getCharAt( i );
	}
	
	public void clearCharAt( int i ) {
		int ind = i-offset;
		if( ind >= 0 && ind < sb.length() ) {
			sb.setCharAt(ind, '-');
			edited = true;
		}
	}
	
	public void setCharAt( int i, char c ) {
		int ind = i-offset;
		if( ind >= 0 && ind < sb.length() ) {
			sb.setCharAt( ind, c );
		}
	}
	
	public char getCharAt( int i ) {
		int ind = i-offset;
		if( ind >= 0 && ind < length() ) {
			return sb.charAt( ind );
		}
		
		return ' ';
	}
	
	public char revCompCharAt( int i ) {
		int ind = i-offset;
		if( ind >= 0 && ind < length() ) {
			char c = sb.charAt( length()-ind-1 );
			if( rc.containsKey(c) ) {
				return rc.get( c );
			} else {
				System.err.println( c );
			}
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
	
	public int length() {
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
		this.offset = start;
		
		if( runbl != null ) runbl.run( this ); //boundsCheck();
	}
	
	public void setEnd( int end ) {
		this.offset = end-sb.length();
		
		if( runbl != null ) runbl.run( this );
		//boundsCheck();
	}
	
	public int getStart() {
		return offset;
	}
	
	public int getEnd() {
		return offset+sb.length();
	}
	
	public int getRevComp() {
		return revcomp;
	}
	
	public void setRevComp( int rc ) {
		this.revcomp = rc;
	}
	
	public int getGCCount() {
		int ret = 0;
		if( sb.length() > 0 ) {
			for( int i = 0; i < sb.length(); i++ ) {
				char c = sb.charAt(i);
				if( c == 'G' || c == 'g' || c == 'C' || c == 'c' ) {
					ret++;
				}
			}
		}
		return ret;
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
		return offset - o.offset;
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
				String aaa = sb.substring(i, i+3).toUpperCase();
				if( aaa.contains("N") || aaa.contains("n") || aaa.contains("X") || aaa.contains("x") ) {
					ret.append( "X" );
				} else {
					String aa = amimap.get( revcom.get(aaa) );
					if( aa != null ) ret.append( i != stop-3 ? aa : (aa.equals("V") || aa.equals("L") ? "M" : aa) );
					//else break;
				}
			}
		} else {
			int end = start - 1 + 3*((stop-start)/3);
			for( int i = start-1; i < end; i+=3 ) {
				String aaa = sb.substring(i, i+3).toUpperCase();
				if( aaa.contains("N") || aaa.contains("n") || aaa.contains("X") || aaa.contains("x") ) {
					ret.append( "X" );
				} else {
					String aa = amimap.get( aaa );
					if( aa != null ) ret.append( i != start-1 ? aa : (aa.equals("V") || aa.equals("L") ? "M" : aa) );
					//else break;
				}
			}
		}
		
		/*if( ret.length() == 0 ) {
			System.err.println(" ");
		}*/
		
		return ret;
	}
	
	public String getSubstring( int start, int end, int ori ) {
		if( start < sb.length() && end <= sb.length() ) {
			if( ori == -1 ) {
				StringBuilder subsb = new StringBuilder();
				for( int i = end-1; i >= start; i-- ) {
					char c = sb.charAt(i);
					char cc = rc.containsKey(c) ? rc.get( c ) : c;
					subsb.append( cc );
				}
				return subsb.toString();
			} else return sb.substring(start, end);
		}
		return "";
	}
}