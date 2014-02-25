package org.simmi.shared;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

public class Serifier {
	public Serifier() {
		super();
	}
	
	public Serifier( List<Sequence> lseq ) {
		super();
		this.lseq = lseq;
	}
	
	List<Sequences>		sequences = new ArrayList<Sequences>();
	public void setSequencesList( List<Sequences> sequences ) {
		this.sequences = sequences;
	}
	
	/*public class Anno {
		int	start;
		int stop;
		boolean comp;
		String name;
		
		public Anno( int start, int stop, boolean comp, String name ) {
			if( stop > start ) {
				this.start = start;
				this.stop = stop;
				this.comp = comp;
			} else {
				this.start = stop;
				this.stop = start;
				this.comp = !comp;
			}
			this.name = name;
		}
	};*/
	
	public List<Sequence> readSequences( BufferedReader br ) throws IOException {
		List<Sequence> ret = new ArrayList<Sequence>();
		
		Sequence s = null;
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith(">") ) {
				s = new Sequence( line.substring(1), null );
				//serifier.lseq.add( s );
				ret.add( s );
			} else if( s != null ) {
				int start = 0;
				int i = line.indexOf(' ');
				while( i != -1 ) {
					String substr = line.substring(start, i);
					s.append( substr );
					start = i+1;
					i = line.indexOf(' ', start);
				}
				s.append( line.substring(start, line.length()) );
			}
			line = br.readLine();
		}
		
		return ret;
	}
	
	public String getFastTree() {
		String 				ret = "";
		File 				tmpdir = new File("c:/Users/sigmar.MATIS/");
		try {
			FileWriter fw = new FileWriter( new File(tmpdir, "tmp.fasta") );
			writeFasta( lseq, fw, null);
			fw.close();

			ProcessBuilder pb = new ProcessBuilder("FastTree.exe", "tmp.fasta");
			pb.directory(tmpdir);
			Process p = pb.start();
			InputStream is = p.getInputStream();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bb = new byte[1024];
			int r = is.read(bb);
			while (r > 0) {
				baos.write(bb, 0, r);
				r = is.read(bb);
			}
			baos.close();

			ret = baos.toString();
			/*Node n = treeutil.parseTreeRecursive( tree, false );
			treeutil.setLoc( 0 );
			n.nodeCalcMap( nmap );*/
		} catch (IOException er) {
			er.printStackTrace();
		}
		return ret;
	}
	
	String addon = "nnnttaattaattaannn";
	public void genbankFromNR( Sequences s, File blastFile, File genbankOut, boolean gbk ) throws IOException {
		Map<String,List<Annotation>>	mapan = new HashMap<String,List<Annotation>>();
		
		BufferedReader			br = Files.newBufferedReader( s.getPath(), Charset.defaultCharset() );
		Map<String,Sequence>	seqmap = new TreeMap<String,Sequence>();
		Sequence		sb = null;
		String			name = null;
		String line = br.readLine();
		
		Set<String>	pool = new HashSet<String>();
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( name != null ) {
					seqmap.put(name, sb);
					int l = name.indexOf(' ');
					if( l == -1 ) l = name.length();
					pool.add( name.substring(0,l) );
				}
				int li = line.indexOf(' ');
				if( li == -1 ) li = line.length();
				name = line.substring(1,li).replace(".fna", "");
				sb = new Sequence();
			} else {
				sb.append( line.replace(" ", "") );
			}
			line = br.readLine();
		}
		if( name != null ) {
			seqmap.put(name, sb);
		}
		br.close();
		
		FileReader	fr = new FileReader( blastFile );
		br = new BufferedReader( fr );
		line = br.readLine();
		String evalue = null;
		Annotation ann = null;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				int li = line.indexOf(' ', 7);
				if( li == -1 ) li = line.length();
				int ki = line.lastIndexOf('_', li);
				//int ki = line.indexOf('_');
				
				String cont = line.substring(7,ki).trim();
				//cont = cont.replace(".fna", "");
				if( pool.contains( cont ) ) {
					List<Annotation> lann;
					if( mapan.containsKey(cont) ) {
						lann = mapan.get( cont );
					} else {
						lann = new ArrayList<Annotation>();
						mapan.put(cont, lann);
					}
					
					String[] split = line.split("#");
					
					int start = Integer.parseInt( split[1].trim() );
					int stop = Integer.parseInt( split[2].trim() );
					int rev = Integer.parseInt( split[3].trim() );
					ann = new Annotation( sb, start-1, stop-1, rev == -1, null );
					lann.add(ann);
				} else ann = null;
				evalue = null;
				System.err.println( cont );
			} else if( line.startsWith(">") ) {
				if( ann != null && ann.name == null ) {
					String hit = line.substring(1);
					line = br.readLine();
					while( !line.startsWith("Length") && !line.startsWith("Query") ) {
						hit += line.substring(1);
						line = br.readLine();
					}
					ann.name = hit;
					
					if( line.startsWith("Query") ) continue;
				}
			} else if( line.contains("Not hits") ) {
				if( ann != null && ann.name == null ) {
					ann.name = line;
				}
			} else if( line.startsWith(" Score =") ) {
				int u = line.indexOf("Expect =");
				if( u > 0 ) {
					u += 9;
					int k = line.indexOf(',', u);
					evalue = line.substring(u, k);
					if( ann != null ) ann.name += "\t" + evalue;
				}
			}
			line = br.readLine();
		}
		br.close();
		
		mseq = seqmap;
		writeGenebank( genbankOut, gbk, s, mapan );
	}
	
	public void writeGenebank( File genbankOut, boolean gbk, Sequences s, Map<String,List<Annotation>> mapan ) throws IOException {
		int count = 0;
		for( Sequence sbld : lseq ) {
			//Sequence sbld = lseq.get(key);
			count += sbld.length();
		}
		count += (lseq.size()-1)*addon.length();
		
		if( gbk ) {
			FileWriter	fw = new FileWriter( genbankOut );
			for( Sequence sbld : lseq ) {
				String key = sbld.getName();
				int ival = s.getName().indexOf('.');
				String loc = "LOCUS       "+s.getName().substring(0, ival == -1 ? s.getName().length() : ival )+"_"+key+"                "+count+" bp    dna     linear   UNK";
				String def = "DEFINITION  [organism=Unknown] [strain=Unknown] [gcode=11] [date=6-26-2012]";
				String acc = "ACCESSION   "+s.getName()+"_Unknown";
				String keyw = "KEYWORDS    .";
				String feat = "FEATURES             Location/Qualifiers";
				fw.write( loc+"\n" );
				fw.write( def+"\n" );
				fw.write( acc+"\n" );
				fw.write( keyw+"\n" );
				fw.write( feat+"\n" );
				
				count = 1;
				//Sequence sbld = mseq.get(key);
				fw.write( "     fasta_record    "+count+".."+(count+sbld.length())+"\n" );
				fw.write( "                     /name=\""+key+"\"\n" );
				
				if( mapan.containsKey(key) ) {
					List<Annotation> lann = mapan.get(key);
					int ac = 1;
					
					if( sbld.revcomp == -1 ) {
						for( int i = lann.size()-1; i >= 0; i-- ) {
							Annotation annn = lann.get(i);
							String locstr = ((sbld.length()-annn.stop)+count)+".."+((sbld.length()-annn.start)+count);
							if( !annn.isReverse() ) fw.write( "     gene            complement("+locstr+")\n" );
							else fw.write( "     gene            "+locstr+"\n" );
							fw.write( "                     /locus_tag=\""+key+"_"+ac+"\"\n" );
							fw.write( "                     /product=\""+annn.name+"\"\n" );
							if( annn.dbref != null ) for( String val : annn.dbref ) {
								fw.write( "                     /db_xref=\""+val+"\"\n" );
							}
							ac++;
						}
					} else {
						for( Annotation annn : lann ) {
							String locstr = (annn.start+count)+".."+(annn.stop+count);
							if( annn.isReverse() ) fw.write( "     gene            complement("+locstr+")\n" );
							else fw.write( "     gene            "+locstr+"\n" );
							fw.write( "                     /locus_tag=\""+key+"_"+ac+"\"\n" );
							fw.write( "                     /product=\""+annn.name+"\"\n" );
							if( annn.dbref != null ) for( String val : annn.dbref ) {
								fw.write( "                     /db_xref=\""+val+"\"\n" );
							}
							ac++;
						}
					}
				}
				
				count += sbld.length();
				count += addon.length();

				fw.write( "ORIGIN" );
				count = 1;
				//int start = 1;
				for( int k = sbld.length(); k < sbld.length(); k++ ) {
					if( (count-1)%60 == 0 ) fw.write( String.format( "\n%10s ", Integer.toString(count) ) );
					else if( (count-1)%10 == 0 ) fw.write( " " );
					
					fw.write( sbld.revcomp == -1 ? sbld.revCompCharAt(k) : sbld.charAt(k) );
					
					count++;
				}
				
				fw.write("\n//\n");
			}
			fw.close();
		} else {
			FileWriter	fw = new FileWriter( genbankOut );
			String loc = "LOCUS       "+s.getName()+"                "+count+" bp    dna     linear   UNK";
			String def = "DEFINITION  [organism=Unknown] [strain=Unknown] [gcode=11] [date=6-26-2012]";
			String acc = "ACCESSION   "+s.getName()+"_Unknown";
			String keyw = "KEYWORDS    .";
			String feat = "FEATURES             Location/Qualifiers";
			fw.write( loc+"\n" );
			fw.write( def+"\n" );
			fw.write( acc+"\n" );
			fw.write( keyw+"\n" );
			fw.write( feat+"\n" );
			count = 1;
			for( Sequence sbld : lseq ) {
				String key = sbld.getName();
				//Sequence sbld = mseq.get(key);
				fw.write( "     fasta_record    "+count+".."+(count+sbld.length())+"\n" );
				fw.write( "                     /name=\""+key+"\"\n" );
				
				if( mapan.containsKey(key) ) {
					List<Annotation> lann = mapan.get(key);
					int ac = 1;
					/*for( Anno annn : lann ) {
						String locstr = (annn.start+count)+".."+(annn.stop+count);
						if( annn.comp ) fw.write( "     gene            complement("+locstr+")\n" );
						else fw.write( "     gene            "+locstr+"\n" );
						fw.write( "                     /locus_tag=\""+key+"_"+ac+"\"\n" );
						fw.write( "                     /product=\""+annn.name+"\"\n" );
						ac++;
					}*/
					
					if( sbld.revcomp == -1 ) {
						for( int i = lann.size()-1; i >= 0; i-- ) {
							Annotation annn = lann.get(i);
							String locstr = ((sbld.length()-annn.stop)+count)+".."+((sbld.length()-annn.start)+count);
							
							if( !annn.isReverse() ) fw.write( "     gene            complement("+locstr+")\n" );
							else fw.write( "     gene            "+locstr+"\n" );
							fw.write( "                     /locus_tag=\""+key+"_"+ac+"\"\n" );
							
							String addon = "";
							if( annn.dbref != null ) for( String val : annn.dbref ) {
								addon += "(CAZY:" + val+")";
							}
							
							fw.write( "                     /product=\""+annn.name+addon+"\"\n" );
							if( annn.dbref != null ) for( String val : annn.dbref ) {
								fw.write( "                     /db_xref=\""+val+"\"\n" );
							}
							ac++;
						}
					} else {
						for( Annotation annn : lann ) {
							String locstr = (annn.start+count)+".."+(annn.stop+count);
							
							if( annn.isReverse() ) fw.write( "     gene            complement("+locstr+")\n" );
							else fw.write( "     gene            "+locstr+"\n" );
							fw.write( "                     /locus_tag=\""+key+"_"+ac+"\"\n" );
							
							String addon = "";
							if( annn.dbref != null ) for( String val : annn.dbref ) {
								addon += "(CAZY:" + val+")";
							}
							
							fw.write( "                     /product=\""+annn.name+addon+"\"\n" );
							if( annn.dbref != null ) for( String val : annn.dbref ) {
								fw.write( "                     /db_xref=\""+val+"\"\n" );
							}
							ac++;
						}
					}
				}
				
				count += sbld.length();
				count += addon.length();
			}
			fw.write( "ORIGIN" );
			count = 1;
			int total = 0;
			//int start = 1;
			for( String key : mseq.keySet() ) {
				Sequence sbld = mseq.get(key);
				for( int k = 0; k < sbld.length(); k++ ) {
					if( (count-1)%60 == 0 ) fw.write( String.format( "\n%10s ", Integer.toString(count) ) );
					else if( (count-1)%10 == 0 ) fw.write( " " );
					
					fw.write( sbld.revcomp == -1 ? sbld.revCompCharAt(k) : sbld.charAt(k) );
					
					count++;
				}
				
				if( total < mseq.size()-1 ) {
					for( int k = 0; k < addon.length(); k++ ) {
						if( (count-1)%60 == 0 ) fw.write( String.format( "\n%10s ", Integer.toString(count) ) );
						else if( (count-1)%10 == 0 ) fw.write( " " );
						
						fw.write( addon.charAt(k) );
						
						count++;
					}	
				}
				
				total++;
			}
			fw.write("\n//");
			fw.close();
		}
	}
	
	public void genbankFromBlast( Sequences s, File blastFile, File genbankOut ) throws IOException {
		BufferedReader	br = Files.newBufferedReader(s.getPath(), Charset.defaultCharset());
		Map<String,Sequence>	seqmap = new TreeMap<String,Sequence>();
		Sequence	sb = null;
		String			name = null;
		String line = br.readLine();
		
		//Set<String>	pool = new HashSet<String>();
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( name != null ) {
					seqmap.put(name, sb);
					int l = name.indexOf(' ');
					if( l == -1 ) l = name.length();
					//pool.add( name.substring(0,l) );
				}
				int li = line.indexOf(' ');
				if( li == -1 ) li = line.length();
				name = line.substring(1,li);
				sb = new Sequence();
			} else {
				sb.append( line.replace(" ", "") );
			}
			line = br.readLine();
		}
		if( name != null ) {
			seqmap.put(name, sb);
		}
		br.close();
		
		int count = 0;
		for( String key : seqmap.keySet() ) {
			Sequence sbld = seqmap.get(key);
			count += sbld.length();
		}
		count += (seqmap.size()-1)*addon.length();
		
		Set<String>	pool = new HashSet<String>();
		Map<String,List<Annotation>>	mapan = new HashMap<String,List<Annotation>>();							
		FileReader	fr = new FileReader( blastFile );
		br = new BufferedReader( fr );
		line = br.readLine();
		String evalue = null;
		String query = null;
		int conseq_empty = 0;
		List<Annotation> lann = null;
		
		int qstart = -1;
		int qstop = -1;
		int sstart = -1;
		int sstop = -1;
		
		while( line != null ) {
			if( line.trim().length() == 0 ) conseq_empty++;
			else {
				if( line.startsWith("Query=") ) {
					int li = line.indexOf(' ', 7);
					if( li == -1 ) li = line.length();
					int ki = line.lastIndexOf('_', li);
					//int ki = line.indexOf('_');
					
					query = line.substring(7,ki).trim();
					pool.add( query );
					/*if( pool.contains( cont ) ) {
						List<Anno> lann;
						if( mapan.containsKey(cont) ) {
							lann = mapan.get( cont );
						} else {
							lann = new ArrayList<Anno>();
							mapan.put(cont, lann);
						}
						
						String[] split = line.split("#");
						
						int start = Integer.parseInt( split[1].trim() );
						int stop = Integer.parseInt( split[2].trim() );
						int rev = Integer.parseInt( split[3].trim() );
						ann = new Anno( start, stop, rev == -1, null );
						lann.add(ann);
					} else ann = null;
					evalue = null;
					System.err.println( cont );*/
				} else if( line.startsWith("Query ") ) {
					String[] split = line.split("[ ]+");
					if( qstart == -1 ) qstart = Integer.parseInt( split[1] );
					qstop = Integer.parseInt( split[3] );
				} else if( line.startsWith("Sbjct") ) {
					String[] split = line.split("[ ]+");
					if( sstart == -1 ) sstart = Integer.parseInt( split[1] );
					sstop = Integer.parseInt( split[3] );
				} else if( line.startsWith(">") ) {
					String cont = line.substring(1).trim();
					cont = cont.replaceAll(".fna", "");
					
					int li = cont.indexOf(' ');
					if( li != -1 ) {
						cont = cont.substring(0,li);
					}
					
					if( mapan.containsKey(cont) ) {
						lann = mapan.get( cont );
					} else {
						lann = new ArrayList<Annotation>();
						mapan.put(cont, lann);
					}
					
					/*if( ann != null && ann.name == null ) {
						String hit = line.substring(1);
						line = br.readLine();
						while( !line.startsWith("Length") && !line.startsWith("Query") ) {
							hit += line.substring(1);
							line = br.readLine();
						}
						ann.name = hit;
						
						if( line.startsWith("Query") ) continue;
					}*/
				} else if( line.contains("Not hits") ) {
					query = null;
				} else if( line.startsWith(" Score =") ) {
					int u = line.indexOf("Expect");
					if( u > 0 ) {
						int k = line.lastIndexOf(' ');
						evalue = line.substring(k);
					}
				}
				if( conseq_empty == 2 && qstart >= 0 ) {
					Annotation ann = new Annotation( sb, sstart, sstop, false, query+"_"+qstart+"_"+qstop+"_"+evalue );
					lann.add( ann );
					
					qstart = -1;
					qstop = -1;
					sstart = -1;
					sstop = -1;
				}
				conseq_empty = 0;
			}
			line = br.readLine();
		}
		br.close();
		
		//if( pool.size() == 0 ) dir = null;
		/*if( pool.size() > 0 ) {
			fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			if( fc.showOpenDialog( SerifyApplet.this ) == JFileChooser.APPROVE_OPTION ) {
				dir = fc.getSelectedFile();
			}
		}*/
		
		for( String phage : pool ) {
			boolean empty = true;
			for( String key : mapan.keySet() ) {
				List<Annotation> lannn = mapan.get( key );
				if( lannn != null ) {
					for( Annotation a : lannn ) {
						if( a.name.contains( phage ) ) {
							empty = false;
							break;
						}
					}
				}
			}
			
			if( !empty ) {
				File f = genbankOut;
				/*if( pool.size() > 0 ) {
					f = new File( dir, s.getName()+"_"+phage+".gb" );
				} else {
					f = dir;//fc.getSelectedFile();
				}*/
				
				if( f != null ) {
					FileWriter	fw = new FileWriter( f );
					String loc = "LOCUS       "+phage+" on "+s.getName()+"                "+count+" bp    dna     linear   UNK";
					String def = "DEFINITION  [organism=unknown] [strain=unknown] [gcode=11] [date=3-20-2012]";
					String acc = "ACCESSION   Unknown";
					String keyw = "KEYWORDS    .";
					String feat = "FEATURES             Location/Qualifiers";
					fw.write( loc+"\n" );
					fw.write( def+"\n" );
					fw.write( acc+"\n" );
					fw.write( keyw+"\n" );
					fw.write( feat+"\n" );
					count = 1;
					for( String key : seqmap.keySet() ) {
						Sequence sbld = seqmap.get(key);
						fw.write( "     fasta_record    "+count+".."+(count+sbld.length())+"\n" );
						fw.write( "                     /name=\""+key+"\"\n" );
						
						if( mapan.containsKey(key) ) {
							List<Annotation> lannn = mapan.get(key);
							int ac = 1;
							for( Annotation ann : lannn ) {
								if( ann.name.contains(phage) ) {
									String locstr = (ann.start+count)+".."+(ann.stop+count);
									if( ann.isReverse() ) fw.write( "     gene            complement("+locstr+")\n" );
									else fw.write( "     gene            "+locstr+"\n" );
									fw.write( "                     /locus_tag=\""+key+"_"+ac+"\"\n" );
									fw.write( "                     /product=\""+ann.name+"\"\n" );
									ac++;
								}
							}
						}
						
						count += sbld.length();
						count += addon.length();
					}
					fw.write( "ORIGIN" );
					count = 1;
					//int start = 1;
					int total = 0;
					for( String key : seqmap.keySet() ) {
						Sequence sbld = seqmap.get(key);
						for( int k = 0; k < sbld.length(); k++ ) {
							if( (count-1)%60 == 0 ) fw.write( String.format( "\n%10s ", Integer.toString(count) ) );
							else if( (count-1)%10 == 0 ) fw.write( " " );
							
							fw.write( sbld.charAt(k) );
							
							count++;
						}
						
						if( total < seqmap.size()-1 ) {
							for( int k = 0; k < addon.length(); k++ ) {
								if( (count-1)%60 == 0 ) fw.write( String.format( "\n%10s ", Integer.toString(count) ) );
								else if( (count-1)%10 == 0 ) fw.write( " " );
								
								fw.write( addon.charAt(k) );
								
								count++;
							}	
						}
						
						total++;
					}
					fw.write("\n//");
					fw.close();
				}
			}
		}
	}
	
	public Map<String,StringBuilder> concat( List<BufferedReader>  lrd ) throws IOException {
		final Map<String,StringBuilder>	seqmap = new HashMap<String,StringBuilder>();
		
		for( BufferedReader br : lrd ) {
			//URL url = new URL( path );
			StringBuilder	sb = null;
			//InputStream is = url.openStream();
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String subline = line.substring(1);
					if( seqmap.containsKey( subline ) ) {
						sb = seqmap.get( subline );
					} else {
						sb = new StringBuilder();
						seqmap.put( subline, sb );
					}
				} else {
					if( sb != null ) sb.append( line );
				}
				
				line = br.readLine();
			}
			br.close();
		}
		
		return seqmap;
	}
	
	public List<Sequences> getSequencesList() {
		return sequences;
	}
	
	public int						max = 0;
	public int						min = 0;
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public int getDiff() {
		return max-min;
	}
	
	public void setMin( int min ) {
		this.min = min;
	}
	
	public void setMax( int max ) {
		this.max = max;
	}
	
	public List<Sequence>		lseq = new ArrayList<Sequence>() {
		private static final long serialVersionUID = 1L;

		public boolean add( Sequence seq ) {
			seq.index = lseq.size();
			return super.add( seq );
		}
	};
	public Map<String,Sequence>		mseq = new HashMap<String,Sequence>();
	public ArrayList<Annotation>	lann = new ArrayList<Annotation>();
	public Map<String,Annotation>	mann = new HashMap<String,Annotation>();
	//int min = Sequence.min;
	//int max = Sequence.max;
	
	Sequence consensus = new Sequence("consensus","consensus", null);
	public Sequence getConsensus() {
		return consensus;
	}
	
	public void setConsensus( Sequence seq ) {
		this.consensus = seq;
	}
	
	private void writeSimplifiedCluster( BufferedWriter fos, Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap ) throws IOException {
		//OutputStreamWriter	fos = new OutputStreamWriter( os );
		for( Set<String> set : clusterMap.keySet() ) {
			Set<Map<String,Set<String>>>	mapset = clusterMap.get( set );
			fos.write( set.toString()+"\n" );
			int i = 0;
			for( Map<String,Set<String>> erm : mapset ) {
				fos.write((i++)+"\n");
				
				for( String erm2 : erm.keySet() ) {
					Set<String>	erm3 = erm.get(erm2);
					fos.write("\t"+erm2+"\n");
					fos.write("\t\t"+erm3.toString()+"\n");
				}
			}
		}
		fos.close();
	}
	
	private Map<Set<String>,Set<Map<String,Set<String>>>> initCluster( Collection<Set<String>>	total, Set<String> species ) {
		Map<Set<String>,Set<Map<String,Set<String>>>> clusterMap = new HashMap<Set<String>,Set<Map<String,Set<String>>>>();
		
		for( Set<String>	t : total ) {
			Set<String>	teg = new HashSet<String>();
			for( String et : t ) {
				Sequence seq = mseq.get( et );
				if( seq == null ) {
					System.err.println(et);
				} else {
					String e = seq.name;
					int ind = e.indexOf('[');
					//if( e.contains("_JL2_") ) ind = e.indexOf('_', ind+1);
					
					if( ind != -1 ) {
						int ind2 = e.indexOf(']', ind+1);
						String str = e.substring( ind+1, ind2 );
						
						int uid = str.indexOf("uid");
						int nuid = uid != -1 ? str.indexOf('_', uid) : str.length();
						if( nuid == -1 ) nuid = str.length();
						
						int c = str.indexOf("contig");
						if( c == -1 ) c = str.indexOf("scaffold");
						if( c == -1 ) c = str.length()+1;
						/*if( joinmap.containsKey( str ) ) {
							str = joinmap.get(str);
						}*/
						
						String tegstr = str.substring( 0, Math.min( c-1, nuid ) );
						teg.add( tegstr );
						
						species.add( tegstr );
					} else {
						ind = e.indexOf("contig");
						if( ind == -1 ) ind = e.indexOf("scaffold");
						String tegstr = e.substring(0, ind-1);
						teg.add( tegstr );
						
						species.add( tegstr );
					}
				}
			}
			
			Set<Map<String,Set<String>>>	setmap;
			if( clusterMap.containsKey( teg ) ) {
				setmap = clusterMap.get( teg );
			} else {
				setmap = new HashSet<Map<String,Set<String>>>();
				clusterMap.put( teg, setmap );
			}
			
			Map<String,Set<String>>	submap = new HashMap<String,Set<String>>();
			setmap.add( submap );
			
			for( String et : t ) {
				Sequence seq = mseq.get( et );
				if( seq != null ) {
					String e = seq.name;
					String tegstr;
					int ind = e.indexOf('[');
					//if( e.contains("_JL2_") ) ind = e.indexOf('_', ind+1);
					
					if( ind != -1 ) {
						int ind2 = e.indexOf(']', ind+1);
						String str = e.substring( ind+1, ind2 );
						
						int uid = str.indexOf("uid");
						int nuid = uid != -1 ? str.indexOf('_', uid) : str.length();
						if( nuid == -1 ) nuid = str.length();
						
						int c = str.indexOf("contig");
						if( c == -1 ) c = str.indexOf("scaffold");
						if( c == -1 ) c = str.length()+1;
						
						tegstr = str.substring( 0, Math.min( c-1, nuid) );
					} else {
						ind = e.indexOf("contig");
						if( ind == -1 ) ind = e.indexOf("scaffold");
						tegstr = e.substring(0, ind-1);
					}
					
					Set<String>	set;
					if( submap.containsKey( tegstr ) ) {
						set = submap.get(tegstr);
					} else {
						set = new HashSet<String>();
						submap.put( tegstr, set );
					}
					set.add( e );
				}
			}
		}
		
		return clusterMap;
	}
	
	private void writeClusters( BufferedWriter fos, List<Set<String>> cluster ) throws IOException {
		//OutputStreamWriter	fos = new OutputStreamWriter( os );
		for( Set<String> set : cluster ) {
			fos.write( set.toString()+"\n" );
		}
		fos.close();
	}
	
	public void writeSequence( Sequence seq, BufferedWriter fw ) throws IOException {
		fw.write(">"+seq.name+"\n");
		for( int k = 0; k < seq.sb.length(); k+=70 ) {
			int m = Math.min(seq.sb.length(), k+70);
			String substr = seq.sb.substring(k, m);
			//(seq.sb.length() == k+70 ? "")
			fw.write( substr+"\n" );
		}
	}
	
	public List<Set<String>> makeBlastCluster( final Path osf, final Path blastfile, int clustermap ) throws IOException {
		//InputStream fis = new FileInputStream( blastfile );
		BufferedReader is;
		if( blastfile.getFileName().toString().endsWith(".gz") ) {
			is = new BufferedReader( new InputStreamReader( new GZIPInputStream( Files.newInputStream(blastfile) ) ) );
		} else {
			is = Files.newBufferedReader(blastfile);
		}
		
		BufferedWriter fos;
		if( Files.isDirectory(osf) ) {
			if( sequences.size() > 0 ) {
				Sequences seqs = sequences.get(0);
				appendSequenceInJavaFasta( seqs, null, true );
				
				System.err.println(mseq.size());
			}
			
			fos = Files.newBufferedWriter( osf.resolve( "clusters.txt" ) );
		} else fos = Files.newBufferedWriter( osf );
		
		/*for( String str : mseq.keySet() ) {
			if( str.contains("scotoductus1572_scaffold00003_4 ") ) {
				System.err.println();
			}
		}*/
		
		List<Set<String>> total = makeBlastCluster( is, fos, clustermap );
		is.close();
		
		//scotoductus1572_scaffold00003_4 # 2808 # 3941 # -1 # ID=3_4;partial=00;start_type=GTG;rbs_motif=None;rbs_spacer=None;gc_cont=0.959
		//scotoductus1572_scaffold00003_4 # 2808 # 3941 # -1 #ID=3_4;partial=00;start_type=GTG;rbs_motif=None;rbs_spacer=None;gc_cont=0.959
		
		System.err.println( total.size() );
		for( Set<String>	strset : total ) {
			String name = null;
			boolean pseudoname = true;
			for( String str : strset ) {
				/*if( name == null ) {
					int si = str.indexOf(' ');
					if( si == -1 ) si = str.length();
					name = str.substring(0, si);
					
					break;
				}*/
				name = str;
				
				/*int i = str.indexOf('[');
				if( i != -1 ) {
					if( pseudoname || !str.contains("hypot") ) {
						//int si = str.indexOf(' ');
						name = str.substring(0, i-1).replace(' ', '_').replace('/', '_');
						
						pseudoname = false;
					}
				}*/
			}
			
			Path of = osf.resolve(name+".aa"); //new File( osf, name+".fna" );
			
			/*if( name.startsWith("YP_007881477.1") ) {
				System.err.println();
			}*/
			
			BufferedWriter fw = Files.newBufferedWriter(of); //new FileWriter( of );
			for( String str : strset ) {
				/*if( str.startsWith("YP_007881477.1") ) {
					for( String erm : mseq.keySet() ) {
						if( erm.startsWith("YP_007881477.1") ) {
							System.err.println( str );
							System.err.println( erm );
							System.err.println();
						}
					}
				}*/
				
				if( str.contains("arciformis4241_scaffold00003_301") ) {
					System.err.println();
				}
				
				//int millind = str.indexOf('#');
				//if( millind == -1 ) millind = str.length();
				//String shortname = str.substring( 0, millind ).trim();
				Sequence seq = mseq.get( str );
				if( seq != null ) {
					writeSequence( seq, fw );
				} else {
					System.err.println();
				}
			}
			fw.close();
		}
		
		return total;
	}
	
	public List<Set<String>> makeBlastCluster( final BufferedReader is, final BufferedWriter fos, int clustermap ) {
		List<Set<String>>	total = new ArrayList<Set<String>>();
		try {		
			if( clustermap%2 == 0 ) {
				joinBlastSets( is, null, true, total, 0.0 );
			} else {
				joinBlastSetsThermus( is, Paths.get("/Users/sigmar/check.txt"), true, total );
			}
			is.close();
			
			if( fos != null ) {
				if( clustermap/2 == 0 ) {
					Set<String>	species = new TreeSet<String>();
					Map<Set<String>,Set<Map<String,Set<String>>>>	clusterMap = initCluster( total, species );
				
					/*Set<String> curset = null;
					int max = 0;
					for( Set<String> set : clusterMap.keySet() ) {
						if( set.size() > max ) {
							curset = set;
							max = set.size();
						}
					}
					System.err.println( max );
					System.err.println( curset );*/
					//if( writeSimplifiedCluster != null ) 
					writeSimplifiedCluster( fos, clusterMap );
					//writeBlastAnalysis( clusterMap, species );
				} else {
					writeClusters( fos, total );
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return total;
	}
	
	private void joinSets( Set<String> all, List<Set<String>> total ) {		
		Set<String> cont = null;
		Set<Set<String>>	rem = new HashSet<Set<String>>();
		
		for( Set<String>	check : total ) {			
			for( String aval : all ) {
				if( check.contains(aval) ) {
					if( cont == null ) {
						cont = check;
						check.addAll( all );
					} else {
						cont.addAll( check );
						rem.add( check );
					}
					break;
				}
			}
		}
		
		for( Set<String> erm : rem ) {
			int ind = -1;
			int count = 0;
			for( Set<String> ok : total ) {
				if( ok.size() == erm.size() && ok.containsAll(erm) ) {
					ind = count;
					break;
				}
				count++;
			}
			
			if( ind != -1 ) {
				total.remove( ind );
			}
		}
		
		rem.clear();
		if( cont == null ) {
			if( total.contains( all ) ) {
				System.err.println("fuckfuckfuck");
			}
			total.add( all );
		}
		
		/*Set<String>	erm = new HashSet<String>();
		for( Set<String> ss : total ) {
			for( String s : ss ) {
				if( erm.contains( s ) ) {
					break;
				}
			}
			erm.addAll( ss );
		}*/
	}
	
	public void joinBlastSetsThermus( BufferedReader br, Path write, boolean union, List<Set<String>> total ) throws IOException {		
		//File file = null;
		BufferedWriter fw = null;
		if( write != null ) {
			//File file = new File( write );
			if( Files.isDirectory(write) ) {
				fw = Files.newBufferedWriter( write.resolve("clusters.txt" ) );
			} else {
				fw = Files.newBufferedWriter(write); //new FileWriter("/home/sigmar/blastcluster.txt");
			}
		}
		//BufferedReader	br = new BufferedReader( new InputStreamReader( is ) );
		
		Set<String>	all = null;
		String line = br.readLine();
		int cnt = 0;
		while( line != null ) {
			if( line.startsWith("Query=") ) {
				String trim = line.substring(7);
				line = br.readLine();
				while( line != null && !line.startsWith("Length") ) {
					trim += " "+line;
					line = br.readLine();
				}
				
				/*if( trim.contains("arciformis4241_scaffold00003_301") ) {
					System.err.println();
				}
				/*if( trim.contains("scotoductus1572_scaffold00003_4") ) {
					System.err.println();
				}*/
				
				if( all != null && all.size() > 0 && union ) joinSets( all, total );
				all = new HashSet<String>();
				
				int i = trim.indexOf(' ');
				if( i == -1 ) i = trim.length();
				String astr = trim.substring(0, i);
				
				if( astr.contains("..") ) {
					int k = trim.indexOf('[');
					int u = trim.indexOf(']', k+1);
					astr = trim.substring(k+1,u)+"_"+astr;
				}
				
				all.add( astr );
			} else if( line.startsWith("Sequences prod") ) {
				line = br.readLine();
				while( line != null && !line.startsWith("Query=") ) {
					String trim = line;
					
					/*if( trim.contains("1572") ) {
						System.err.println();
					}*/
					if( trim.startsWith(">") ) {
					//if( trim.startsWith("o.prof") || trim.startsWith("m.hydro") || trim.startsWith("mt.silv") || trim.startsWith("mt.ruber") || trim.startsWith("t.RLM") || trim.startsWith("t.spCCB") || trim.startsWith("t.arci") || trim.startsWith("t.scoto") || trim.startsWith("t.antr") || trim.startsWith("t.aqua") || trim.startsWith("t.t") || trim.startsWith("t.egg") || trim.startsWith("t.island") || trim.startsWith("t.oshi") || trim.startsWith("t.brock") || trim.startsWith("t.fili") || trim.startsWith("t.igni") || trim.startsWith("t.kawa") ) {
						trim = trim.substring(2);
						
						line = br.readLine();
						while( line != null && !line.startsWith("Length") ) {
							trim += line;
							line = br.readLine();
						}
						
						//int millind = trim.indexOf('#');
						//if( millind == -1 ) millind = trim.indexOf("..", 5);
						//String val = trim.substring( 0, millind ).trim();
						
						if( line != null ) {
							int len = Integer.parseInt( line.substring(7) );
							
							line = br.readLine();
							if( line != null ) line = line.trim();
							else continue;
							
							while( !line.startsWith("Identities") ) {
								line = br.readLine().trim();
							}
							
							int idx0 = line.indexOf('/');
							int idx1 = line.indexOf('(');
							int idx2 = line.indexOf('%');
							
							int percid = Integer.parseInt( line.substring(idx1+1, idx2) );
							int lenid = Integer.parseInt( line.substring(idx0+1, idx1-1) );
							//int v = val.indexOf("contig");
							
							if( percid >= 50 && lenid >= len/2 ) {
								int i = trim.indexOf(' ');
								if( i == -1 ) i = trim.length();
								String astr = trim.substring(0, i);
								
								if( astr.contains("..") ) {
									int k = trim.indexOf('[');
									int u = trim.indexOf(']', k+1);
									astr = trim.substring(k+1,u)+"_"+astr;
								}
								all.add( astr ); //.replace(".fna", "") );
							}
						} else System.err.println( trim );
						
						/*if( val.contains("SG0") ) {
							System.err.println();
						}*/
						
						//all.add( val.replace(".fna", "") );
					}
					
					if( cnt++ % 100000 == 0 ) {
						System.err.println( cnt );
					}
					
					line = br.readLine();
				}
				
				//if( fw != null ) fw.write( all.toString()+"\n" );
				
				//System.err.println( all );
				//System.err.println();
				//if( union ) joinSets( all, total );
				//else intersectSets( all, total );
				
				/*for( Set<String> set : total ) {
					
				}*/
				
				if( line == null ) break;
				else continue;
			}
			
			/*if( cnt++ % 100000 == 0 ) {
				System.err.println( cnt );
			}*/
			line = br.readLine();
		}
		if( fw != null ) {
			for( Set<String> allt : total ) {
				String allstr = allt.toString();
				fw.write( allstr+"\n" );
			}
			fw.close();
		}
		
		/*for( Set<String> all : total ) {
			String allstr = all.toString();
			if( allstr.contains("346pe_scaffold00001_513") ) {
				System.err.println();
			}
		}*/
	}
	
	public void joinBlastSets( BufferedReader br, String write, boolean union, List<Set<String>> total, double evalue ) throws IOException {
		FileWriter fw = write == null ? null : new FileWriter( write ); //new FileWriter("/home/sigmar/blastcluster.txt");
		//BufferedReader	br = new BufferedReader( new InputStreamReader( is ) );
		
		String line = br.readLine();
		int cnt = 0;
		while( line != null ) {
			if( line.startsWith("Sequences prod") ) {
				line = br.readLine();
				Set<String>	all = new HashSet<String>();
				while( line != null && !line.startsWith(">") && !line.startsWith("Lambda") && !line.startsWith("Query") && !line.startsWith("Effect") ) {
					String trim = line.trim();
					
					String[] split = trim.split("[\t ]+");
					//int v = val.indexOf("contig");
					if( split[0].length() > 0 ) {
						double val = 100.0;
						try {
							val = Double.parseDouble( split[ split.length-1 ] );
						} catch( Exception e ) {
							System.err.println( line );
							e.printStackTrace();
						}
						if( val <= evalue ) all.add( split[0] );
					}
					
					line = br.readLine();
				}
				
				if( union ) joinSets( all, total );
				//else intersectSets( all, total );
				
				if( line == null ) break;
			}
			
			/*if( cnt++ % 100000 == 0 ) {
				System.err.println( cnt );
			}*/
			line = br.readLine();
		}
		
		if( fw != null ) {
			for( Set<String> all : total ) {
				fw.write( all.toString()+"\n" );
			}
			fw.close();
		}
	}
	
	public List<Sequences> splitit( int nspin, Sequences seqs, File dir ) {
		List<Sequences>	retlseq = new ArrayList<Sequences>();
		
		try {
			//File inf = new File( new URI(seqs.getPath() ) );
			String name = seqs.getPath().getFileName().toString();
			int ind = name.lastIndexOf('.');
			
			String sff = name;
			String sf2 = "";
			if( ind != -1 ) {
				sff = name.substring(0, ind);
				sf2 = name.substring(ind+1,name.length());
			}
			
			int spin = (int)Math.ceil( (double)seqs.getNSeq()/(double)nspin );
			
			int i = 0;
			FileWriter 		fw = null;
			File			of = null;
			BufferedReader	br = Files.newBufferedReader(seqs.getPath(), Charset.defaultCharset());
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					if( i%spin == 0 ) {
						if( fw != null ) {
							fw.close();
							
							//if( applet != null ) {
								name = of.getName();
								ind = name.lastIndexOf('.');
								name = name.substring(0,ind);
								//addSequences(name, seqs.getType(), of.toURI().toString(), spin);
								Sequences nseqs = new Sequences( "", name, seqs.getType(), of.toPath(), spin );
								nseqs.setKey( "" );
								retlseq.add( nseqs );
							//}
						}
						of = new File( dir, sff + "_" + (i/spin+1) + "." + sf2 );
						fw = new FileWriter( of );
					}
					i++;
				}
				fw.write( line+"\n" );
				
				line = br.readLine();
			}
			br.close();
			if( fw != null ) {
				fw.close();
				//if( applet != null ) {
					name = of.getName();
					ind = name.lastIndexOf('.');
					name = name.substring(0,ind);
					//addSequences(name, seqs.getType(), of.toURI().toString(), i%spin);
					Sequences nseqs = new Sequences( "", name, seqs.getType(), of.toPath(), spin );
					nseqs.setKey( "" );
					retlseq.add( nseqs );
				//}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return retlseq;
	}
	
	public static Map<String,String> mapNameHit( InputStream blasti, int idfilt, boolean includePerc ) throws IOException {
		return mapNameHit( blasti, idfilt, includePerc, false );
	}
	
	public static Map<String,String> mapNameHit( InputStream blasti, int idfilt, boolean includePerc, boolean includeLen ) throws IOException {
		Map<String,String>	mapHit = new HashMap<String,String>();
		
		BufferedReader br = new BufferedReader( new InputStreamReader( blasti ) );
		String line = br.readLine();
		String current;
		while( line != null ) {
			String trim = line.trim();
			if( trim.startsWith("Query=") || trim.startsWith("Query >") ) {
				current = null;
				String name = trim.substring(6).trim();
				
				String len = "";
				line = br.readLine();
				while( !line.startsWith("Length") ) {
					line = br.readLine();
				}
				len = line.substring(7);
				
				line = br.readLine();
				while( line != null ) {
					while( line != null && (!line.startsWith(">") && !line.contains("No hits") && !line.startsWith("Query=")) ) {
						line = br.readLine();
					}
					if( line != null ) {
						if( line.startsWith("Query") ) {
							/*line = br.readLine();
							while( !line.startsWith("Length") ) {
								line = br.readLine();
							}
							len = line.substring(7);*/
							
							break;
						}
						
						if( line.startsWith(">") ) {
							String newcurrent = line.substring(1).trim();
							line = br.readLine();
							while( !line.startsWith("Length") ) {
								newcurrent += " "+line.trim();
								line = br.readLine();
							}
							
							line = br.readLine();
							while( !line.startsWith(" Identities") ) {
								line = br.readLine();
							}
							int idx = line.indexOf('(');
							int idx2 = line.indexOf("%)", idx+1);
							String idstr = line.substring(idx+1,idx2);
							
							if( Integer.parseInt( idstr ) >= idfilt ) {
								int i = name.indexOf(' ');
								if( i == -1 ) i = name.length();
								String nm = name.substring(0,i);
								
								//int k = newcurrent.lastIndexOf(';');
								//if( i == -1 ) i = newcurrent.length()-1;
								//newcurrent = newcurrent.substring(k+1);
								
								/*System.out.println(newcurrent);
								if( newcurrent.indexOf("Thermus") != -1 ) {
									System.out.println(newcurrent);
									newcurrent = newcurrent.substring( newcurrent.indexOf("Thermus"), newcurrent.indexOf("strain")-1 ).replace(' ', '_');
								}*/
								
								/*String mapstr = includePerc ? newcurrent+"_"+idstr+"%" : newcurrent;
								mapstr = includeLen ? mapstr+"_"+len : mapstr;*/
								int bil = newcurrent.indexOf('|');
								int bil2 = newcurrent.indexOf('|', bil+1);
								
								/*if( newcurrent.contains("putative signal p") ) {
									System.err.println();
								}*/
								
								String mapstr = newcurrent.substring(bil+1, bil2);
								int bilt = newcurrent.indexOf(' ', bil2+1);
								
								int spc = newcurrent.indexOf("[", bilt+1);
								if( spc == -1 ) spc = newcurrent.length();
								mapstr += " " + newcurrent.substring(bilt+1,spc);
								mapstr += "["+nm.substring(0, nm.lastIndexOf('_'))+"]";
								mapstr += name.substring(i);
								mapHit.put( nm, mapstr );
							}
						}
						
							/*if( current == null || (current.contains("Uncultured bacterium") && !newcurrent.contains("Uncultured bacterium")) || (current.contains("Uncultured") && !newcurrent.contains("Uncultured")) ) {
								mapHit.put( name, newcurrent );
								current = newcurrent;
							}*/
						line = br.readLine();
					}
				}
				if( line == null ) break;
				else if( line.startsWith("Query") ) {
					/*line = br.readLine();
					while( !line.startsWith("Length") ) {
						line = br.readLine();
					}
					len = line.substring(7);*/
					
					continue;
				}
			} else if( trim.startsWith("Query >") ) {
				String name = trim.substring(7).trim();
				
				line = br.readLine();
				line = br.readLine();
				String[] split = line.trim().split("[ ]+");
				mapHit.put(name, split[2] );
			}
			line = br.readLine();
		}
		br.close();
		
		System.err.println( mapHit.size() );
		for( String s : mapHit.keySet() ) {
			System.err.println( s + "  " + mapHit.get(s) );
			break;
		}
		
		return mapHit;
	}
	
	public int doMapHitStuff( Map<String,String> mapHit, InputStream is, OutputStream os, Set<String> filter ) throws IOException {
		return doMapHitStuff(mapHit, is, os, "_", filter);
	}
	
	public boolean checkFilter( Collection<String> filter, String maphitstr ) {
		for( String str : filter ) {
			if( maphitstr.contains( str ) ) return true;
		}
		return false;
	}
	
	public int doMapHitStuff( Map<String,String> mapHit, InputStream is, OutputStream os, String sep, Collection<String> filter ) throws IOException {
		initMaps();
		Map[] maps = {snaedis1heatmap,snaedis2heatmap,snaedis3heatmap,snaedis4heatmap,snaedis5heatmap,snaedis6heatmap,snaedis7heatmap,snaedis8heatmap};
		Map[] phmaps = {snaedis1phmap,snaedis2phmap,snaedis3phmap,snaedis4phmap,snaedis5phmap,snaedis6phmap,snaedis7phmap,snaedis8phmap};
		Map[] colormaps = {snaedis1colormap,snaedis2colormap,snaedis3colormap,snaedis4colormap,snaedis5colormap,snaedis6colormap,snaedis7colormap,snaedis8colormap};
		
		int nseq = 0;
		PrintStream pr = new PrintStream( os );
		BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
		String line = br.readLine();
		boolean include = false;
		while( line != null ) {
			if( line.startsWith(">") ) {
				String name = line.substring(1).trim();
				int i = name.indexOf(' ');
				if( i == -1 ) i = name.length();
				String nm = name.substring(0,i);
				//System.err.println( "muu "+name );
				if( mapHit.containsKey(nm) ) {
					String maphitstr = mapHit.get(nm);
					//System.err.println( maphitstr );
					
					//int li = maphitstr.lastIndexOf(';');
					//if( li != -1 ) maphitstr = maphitstr.substring(li+1);
					
					if( filter == null || checkFilter( filter, maphitstr ) ) {
						nseq++;
						
						i = line.lastIndexOf('_');
						if( i != -1 ) i = line.lastIndexOf('_', i-1);
						if( i == -1 ) i = line.length();
						//String cont = line.substring(1,i);
						
						//String newline = colorAdd( maphitstr, maps, phmaps, colormaps, cont, cont, null, false );
						//pr.println( ">" + newline + sep + name );
						if( sep != null ) pr.println( ">" + maphitstr + sep + nm ); //+ sep + mapHit.get(name) );
						else pr.println( ">" + maphitstr );
						include = true;
					} else include = false;
				} else {
					pr.println( ">" + name );
					//include = false;
				}
			} else if( include ) {
				pr.println( line );
			}
			line = br.readLine();
		}
		br.close();
		pr.close();
		
		return nseq;
	}
	
	public Sequences blastRename( Sequences seqs, String s, File f, boolean includeLen ) {
		Sequences ret = null;
		try {
			//URI uri = new URI( seqs.getPath() );
			InputStream is = Files.newInputStream( seqs.getPath(), StandardOpenOption.READ);
			
			if( seqs.getPath().endsWith(".gz") ) {
				is = new GZIPInputStream( is );
			}
			
			Map<String,String> nameHitMap = mapNameHit( new FileInputStream(s), 0, true, includeLen );
			/*System.err.println( nameHitMap.size() );
			for( String key : nameHitMap.keySet() ) {
				System.err.println( key + "    " + nameHitMap.get(key) );
				break;
			}*/
			
			//String[] filter = { "Thermus", "Meiothermus" };
			int nseq = doMapHitStuff( nameHitMap, is, new FileOutputStream(f), ";", null ); //Arrays.asList(filter) );
			
			ret = new Sequences( "", f.getName(), seqs.getType(), f.toPath(), nseq );
			//if( sapplet != null ) sapplet.addSequences( f.getName(), seqs.getType(), f.toURI().toString(), nseq );
			//else addSequences( f.getName(), seqs.getType(), f.toURI().toString(), nseq );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return ret;
	}
	
	public Sequences filtit( int nspin, Sequences seqs, File dir ) {
		Sequences ret = null;
		try {
			String name = seqs.getPath().getFileName().toString();
			int ind = name.lastIndexOf('.');
			
			String sff = name;
			String sf2 = "";
			if( ind != -1 ) {
				sff = name.substring(0, ind);
				sf2 = name.substring(ind+1,name.length());
			}
			
			//int spin = (int)Math.ceil( (double)seqs.getNSeq()/(double)nspin );
			
			StringBuilder 	include = new StringBuilder();
			String			current = null;
			int i = 0;
			
			File			of = dir.isDirectory() ? new File( dir, sff + "_lenfilt." + sf2 ) : dir;
			FileWriter 		fw = new FileWriter( of );
					
			BufferedReader 	br = Files.newBufferedReader( seqs.getPath(), Charset.defaultCharset());
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					if( include.length() >= nspin ) {
						i++;
						fw.write( current + "\n" );
						for( int k = 0; k < include.length(); k+=70 ) {
							fw.write( include.substring(k, Math.min(include.length(), k+70))+"\n" );
						}
					}
					current = line;
					include.delete(0, include.length());
				} else include.append( line );
				
				line = br.readLine();
			}
			br.close();
			if( fw != null ) {
				fw.close();
				
				name = of.getName();
				ind = name.lastIndexOf('.');
				name = name.substring(0,ind);
				Sequences nseqs = new Sequences( "", name, seqs.getType(), of.toPath(), i );
				ret = nseqs;
				/*if( applet != null ) {
					name = of.getName();
					ind = name.lastIndexOf('.');
					name = name.substring(0,ind);
					applet.addSequences(name, seqs.getType(), of.toURI().toString(), i);
				}*/
			}									
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return ret;
	}
	
	public void addSequences( Sequences seqs ) {
		sequences.add( seqs );
	}
	
	public void addSequences( String name, String type, Path path, int nseq ) {
		Sequences seqs = new Sequences( "", name, type, path, nseq );
		seqs.setKey( "" );
		addSequences( seqs );
	}
	
	public int countSequences( File fasta ) throws IOException {
		FileReader fr = new FileReader( fasta );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		int nseq = 0;
		while( line != null ) {
			if( line.startsWith(">") ) {
				nseq++;
			}
			line = br.readLine();
		}
		br.close();
		
		return nseq;
	}
	
	public void appendSequenceInJavaFasta( Sequences seqs, Map<String,Sequence> contset, boolean namefix ) {
		StringBuilder	dna = new StringBuilder();
		//Map<String,String>	idmap = new HashMap<String,String>();
		try {
			//File inf = new File( new URI(seqs.getPath()) );
			BufferedReader br = Files.newBufferedReader( seqs.getPath(), Charset.defaultCharset() );
			String cont = null;
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					/*if( line.contains("scotoductus1572_scaffold00003_4") ) {
						System.err.println();
					}*/
					
					if( cont != null ) {
						Sequence seq = new Sequence(cont, dna, null);
						int millind = cont.indexOf(' ');
						if( millind == -1 ) millind = cont.length();
						String id = cont.substring(0, millind);
						mseq.put( id, seq );
						
						addSequence(seq);
						if (seq.getAnnotations() != null)
							Collections.sort(seq.getAnnotations());
						if( contset != null ) contset.put(cont, seq);
					}
					//System.err.println( seqs.getName() );
					if( /*rr.length == 1*/ namefix ) {
						cont = line.replace( ">", "" );
						//int millind = cont.indexOf(' ');
						//if( millind == -1 ) millind = cont.length();
						//cont = cont.substring( 0, millind ).trim();
					} else cont = line.replace( ">", seqs.getName()+"_" );
					dna = new StringBuilder();
					//dna.append( line.replace( ">", ">"+seqs.getName()+"_" )+"\n" );
					//nseq++;
				} else dna.append( line.replace(" ", "") );
				line = br.readLine();
			}
			if( cont != null ) {
				Sequence seq = new Sequence(cont, dna, null);
				int millind = cont.indexOf(' ');
				if( millind == -1 ) millind = cont.length();
				mseq.put( cont.substring(0, millind), seq );
				addSequence(seq);
				if (seq.getAnnotations() != null)
					Collections.sort(seq.getAnnotations());
				if( contset != null ) contset.put(cont, seq);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//return idmap;
	}
	
	public void nameReplace( String one, String two ) {
		for( Sequence seq : lseq ) {
			seq.setName( seq.getName().replace( one, two ) );
		}
	}
	
	public void removeAllGaps( List<Sequence> seqlist ) {
		for( Sequence seq : seqlist ) {
			int i = 0;
			while( i < seq.sb.length() ) {
				if( seq.sb.charAt(i) == '-' ) seq.sb.deleteCharAt(i);
				else i++;
			}
		}
		
		checkMaxMin();
	}
	
	public void removeGaps( List<Sequence> seqlist ) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for( Sequence seq : seqlist ) {
			min = Math.min( min, seq.getStart() );
			max = Math.max( max, seq.getEnd() );
		}

		for( Sequence seq : seqlist ) {
			seq.setStart( seq.getStart()-min );
		}

		int i = 0;
		while( i < max-min ) {
			boolean rem = true;
			//char c = 0;
			int len = -1;
			while( rem && i+len < max ) {
				len++;
				for( Sequence seq : seqlist ) {
					char c2 = seq.charAt(i+len); //getCharAt(i, r);
					if( c2 != '.' && c2 != '-' && c2 != ' ' && c2 != 'X' && c2 != 'x' ) {
						rem = false;
						break;
					}
					/*if( c2 != '.' && c2 != '-' ) {
					if( c != 0 && c2 != c ) {
					rem = false;
					break;
					}
					c = c2;
					}*/
				}
			}
			if( len > 0 ) {
				for( Sequence seq : seqlist ) {
					seq.delete(i, i+len);
				}
				max-=len;
			} else {
				i++;
			}
		}

		checkMaxMin();
	}
	
	public void checkMaxMin() {
		int lmin = Integer.MAX_VALUE;
		int lmax = 0;
		
		for( Sequence s : lseq ) {
			if( s.getEnd() > lmax ) lmax = s.getEnd();
			if( s.getStart() < lmin ) lmin = s.getStart();
		}
		
		//if( lmin < getMin() ) 
		setMin( lmin );
		//if( lmax > getMax() ) 
		setMax( lmax );
	}
	
	public void addSequence( Sequence seq ) {
		lseq.add( seq );
		if( seq.getEnd() > getMax() ) setMax( seq.getEnd() );
	}
	
	public void clearAll() {
		lseq.clear();
		lann.clear();
		if( mseq != null ) mseq.clear();
		setMax( 0 );
	}
	
	public void addAnnotation( Annotation ann ) {
		lann.add( ann );
	}
	
	public List<Sequences> fastTreePrepare( List<Sequences> lseqs ) {
		//JavaFasta jf = new JavaFasta( null, this );
		//jf.initDataStructures();
		
		List<Sequences>	retlseq = new ArrayList<Sequences>();
		for( Sequences seqs : lseqs ) {
			Map<String,Sequence> contset = new HashMap<String,Sequence>();
			appendSequenceInJavaFasta( seqs, contset, true );
		
			/*for (String contig : contset.keySet()) {
				Sequence seq = contset.get(contig);
				addSequence(seq);
				if (seq.getAnnotations() != null)
					Collections.sort(seq.getAnnotations());
			}*/
			//jf.selectAll();
			nameReplace(" ", "_");
			removeGaps( lseq );
			
			Path path = seqs.getPath();
			String pathstr = path.toString();
			int i = pathstr.lastIndexOf('.');
			if( i == -1 ) pathstr += "_fixed";
			else pathstr = pathstr.substring(0,i)+".fixed"+pathstr.substring(i);
			
			i = pathstr.lastIndexOf('/');
			String fname = pathstr.substring(i+1);
			
			try {
				Writer osw = Files.newBufferedWriter(path, Charset.defaultCharset(), StandardOpenOption.WRITE);
				//OutputStreamWriter osw = new OutputStreamWriter( url.openConnection().getOutputStream() );
				writeFasta( lseq, osw, null );
				osw.close();
				
				retlseq.add( new Sequences("", fname, "", path, 0) );
				//sapplet.addSequences( fname, path );
			} catch(MalformedURLException e1) {
				e1.printStackTrace();
			} catch(IOException e1) {
				e1.printStackTrace();
			}
			
			clearAll();
		}
		
		return retlseq;
	}
	
	public Map<String,String> makeFset( String trim ) throws URISyntaxException, IOException {
		return makeFset( trim, "" );
	}
	
	public Map<String,String> makeFset( String trim, String add ) throws URISyntaxException, IOException {
		boolean nofile = false;
		
		File f = new File( trim );
		if( !f.exists() ) nofile = true;
		
		/*URL url;
		try {
			File f = new File( trim );
			if( f.exists() ) url = f.toURI().toURL();
			else url = new URL( trim );
		} catch( Exception exc ) {
			nofile = true;
		}*/
		Map<String,String> fset = new HashMap<String,String>();
		if( nofile ) {
			String[] farray = { trim };
			for( String str : farray ) {
				fset.put(str+add, null);
			}
			//fset.addAll( Arrays.asList( farray ) );
		} else {
			//File fl = new File( new URI(trim) );
			FileReader fr = new FileReader( f );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			if( trim.contains("blastout") ) {
				while( line != null ) {
					if( line.startsWith(">") ) {
						String name = line.substring(2);
						line = br.readLine();
						while( !line.contains("Length") ) {
							name += line;
							line = br.readLine();
						}
						fset.put( name, null );
					}					
					line = br.readLine();
				}
			} else if( !trim.contains("454ReadStatus") ) {
				while( line != null ) {
					/*if( line.contains("ingletons") ) {
						fset.add( line.split("[\t ]+")[0] );
					}*/							
					String[] split = line.split("\t");
					if( split.length > 1 ) fset.put( split[0], split[1] );
					else fset.put( line+add, null );
					
					line = br.readLine();
				}
			} else {
				while( line != null ) {
					/*if( line.contains("ingletons") ) {
						fset.add( line.split("[\t ]+")[0] );
					}*/								
					if( line.contains("Singleton") ) {
						String[] split = line.split("[\t ]+");
						//if( split.length > 1 ) fset.put( split[0], split[1] );
						fset.put( split[0], null );
					}
					
					line = br.readLine();
				}
			}
			br.close();
		}
		return fset;
	}
	
	private String filt( String str, int val ) {
		if( val == 1 ) {
			return str.substring( str.lastIndexOf('_')+1, str.length() );
		} else if( val == 2 ) {
			return str.substring( str.indexOf('_')+1, str.lastIndexOf('_') );
		} else if( val == 3 ) {
			return str.substring( 0, str.lastIndexOf('_') );
		} else if( val == 4 ) {
			String nstr = str.substring( str.indexOf('_')+1, str.lastIndexOf('_') );
			if( nstr.contains("geysir") ) return "geysir";
			else if( nstr.contains("reykjadalir") || nstr.contains("vondugil") || nstr.contains("hrafntinnusker") ) return "torfajokull";
			else if( nstr.contains("deildartunguhver") || nstr.contains("kleppjarnsreykir") || nstr.contains("hurdarbak") ) return "borgarfjordur";
			else return nstr;
		}
		
		return str;
	}
	
	public boolean isin( String allloc, String loc, int tval ) {
		if( tval == 4 ) {
			if( allloc.contains("reykjadalir") || allloc.contains("vondugil") || allloc.contains("hrafntinnusker") ) {
				if( loc.contains("torfajokull") ) return true;
				return false;
			} else if( allloc.contains("deildartunguhver") || allloc.contains("kleppjarnsreykir") || allloc.contains("hurdarbak") ) {
				if( loc.contains("borgarfjordur") ) return true;
				return false;
			}
		}
		return allloc.contains( loc );
	}
	
	public List<Sequence> subsample( List<Sequence> lseq, int smplnum, boolean specOnly ) {
		initMaps();
		Map[] maps = {snaedis1heatmap,snaedis2heatmap,snaedis3heatmap,snaedis4heatmap,snaedis5heatmap,snaedis6heatmap,snaedis7heatmap,snaedis8heatmap};
		Map[] phmaps = {snaedis1phmap,snaedis2phmap,snaedis3phmap,snaedis4phmap,snaedis5phmap,snaedis6phmap,snaedis7phmap,snaedis8phmap};
		Map[] colormaps = {snaedis1colormap,snaedis2colormap,snaedis3colormap,snaedis4colormap,snaedis5colormap,snaedis6colormap,snaedis7colormap,snaedis8colormap};
		
		List<Sequence>	retlist = new ArrayList<Sequence>();
		Random r = new Random();
		Map<String,List<Sequence>>	samplegroup = new HashMap<String,List<Sequence>>();
		for( Sequence s : lseq ) {
			int i = s.getName().indexOf('[');
			if( i == -1 ) i = s.getName().length();
			String substr = s.getName().substring(0,i);
			
			int i1 = s.getName().indexOf(";");
			int i2 = s.getName().indexOf("_lenfilt", i+1);
			String loc = s.getName().substring(i1+1,i2);
			String sub = filt( loc, 4 );
			
			List<Sequence>	list;
			String addon = specOnly ? "" : sub;
			if( !samplegroup.containsKey( substr+addon ) ) {
				list = new ArrayList<Sequence>();
				samplegroup.put( substr+addon, list );
			} else list = samplegroup.get( substr+addon );
			
			list.add( s );
		}
		
		for( String group : samplegroup.keySet() ) {
			List<Sequence> slist = samplegroup.get(group);
			int count = 0;
			while( slist.size() > 0 && count < smplnum ) {
				retlist.add( slist.remove( r.nextInt(slist.size()) ) );
				count++;
			}
		}
		
		for( Sequence s : retlist ) {
			int i = s.getName().indexOf('[');
			if( i == -1 ) i = s.getName().length();
			String substr = s.getName().substring(0,i);
			
			int i1 = s.getName().indexOf(";");
			int i2 = s.getName().indexOf("_lenfilt", i+1);
			//if( i == -1 ) i = s.getName().length();
			String loc = s.getName().substring(i1+1,i2);
			String sub = filt( loc, 4 );
			
			String newline = colorAdd( substr, maps, phmaps, colormaps, specOnly ? loc : sub, sub/*s.getName().substring(i1+1)*/, r, specOnly );
			
			s.setName( newline );
		}
		
		return retlist;
	}
	
	public void parse( String[] args ) throws IOException, URISyntaxException {
		List<String>	arglist = Arrays.asList(args);
		//System.err.println( arglist );
		
		initMaps();
		int i = arglist.indexOf("-in");
		File inf = null;
		if( i >= 0 ) {
			while( ++i < args.length ) {
				String next = args[i];
				if( !next.startsWith("-") ) {
					inf = new File( next );
					Sequences seqs = new Sequences( "", inf.getName(), "nucl", inf.toPath(), 0 );
					addSequences( seqs );
				} else break;
			}
		}
		
		File outf = null;
		i = arglist.indexOf("-out");
		if( i >= 0 ) {
			outf = new File( args[i+1] );
			//ex
		}
		
		i = arglist.indexOf("-huge");
		if( i >= 0 ) {
			String mappingfile = args[i+1];
			
			Map<String,String>	mapping = new HashMap<String,String>();
			
			if( mappingfile.contains("\\n") || mappingfile.contains("\\t") ) {
				String first = mappingfile;
				String next = args[i+2];
				
				first = first.replace("\\n", "\n").replace("\\t", "\t");
				next = next.replace("\\n", "\n").replace("\\t", "\t");
				mapping.put( first, next );
			} else if( args.length > i+2 && !args[i+2].startsWith("-") ) {
				mapping.put( mappingfile, args[i+2] );
			} else {
				FileReader fr = new FileReader( mappingfile );
				BufferedReader br = new BufferedReader( fr );
				String line = br.readLine();
				while( line != null ) {
					String[] split = line.split("\t");
					if( split.length > 1 ) mapping.put(split[0], split[1]);
					line = br.readLine();
				}
				br.close();
				fr.close();
			}
			
			FileWriter fw = new FileWriter( outf );
			FileReader fr = new FileReader( inf );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				for( String map : mapping.keySet() ) {
					if( map.equals("\n") ) {
						String val = mapping.get(map);
						val = val.substring(0, val.length()-1);
						line = line + val;
					} else line = line.replace( map, mapping.get(map) );
				}
				fw.write( line+"\n" );
				line = br.readLine();
			}
			br.close();
			fr.close();
			fw.close();
		}
		
		i = arglist.indexOf("-replace");
		if( i >= 0 ) {
			String mappingfile = args[i+1];
			
			Map<String,String>	mapping = new HashMap<String,String>();
			if( args.length > i+2 ) mapping.put( mappingfile, args[i+2] );
			else mapping.put( mappingfile, "" );
			
			FileWriter fw = new FileWriter( outf );
			FileReader fr = new FileReader( inf );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				for( String map : mapping.keySet() ) {
					if( map.equals("\n") ) {
						String val = mapping.get(map);
						val = val.substring(0, val.length()-1);
						line = line + val;
					} else line = line.replace( map, mapping.get(map) );
				}
				fw.write( line+"\n" );
				line = br.readLine();
			}
			br.close();
			fr.close();
			fw.close();
		}
		
		// matrix med location vs species count ur fasta file
		i = arglist.indexOf("-ermat");
		if( i >= 0 ) {
			Map<String,Map<String,Integer>>	mset = new HashMap<String,Map<String,Integer>>();
			Map<String,Map<String,Integer>>	allmset = new HashMap<String,Map<String,Integer>>();
			Map<String,Integer>				allcount = new HashMap<String,Integer>();
			Map<String,Integer>				seqcount = new HashMap<String,Integer>();
			
			//int tval = Integer.parseInt( args[i+3] );
			int tval = Integer.parseInt( args[i+1] );
			
			List<String>	allloclist = new ArrayList<String>();
			Set<String>		alllocset = new HashSet<String>();
			
			File seqf = new File( args[i+2] );
			FileReader seqfr = new FileReader( seqf );
			BufferedReader seqbr = new BufferedReader( seqfr );
			String sline = seqbr.readLine();
			while( sline != null ) {
				if( sline.startsWith(">") ) {
					int u = sline.indexOf("_lenfilt");
					String loc = sline.substring(1,u);
					alllocset.add( loc );
					loc = filt( loc, tval );
					if( !seqcount.containsKey( loc ) ) {
						seqcount.put( loc, 1 );
					} else seqcount.put( loc, seqcount.get(loc)+1 );
				}
				sline = seqbr.readLine();
			}
			seqfr.close();
			allloclist.addAll( alllocset );
			
			FileReader fr = new FileReader( inf );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String[] split = line.split(";");
					String spec = split[0];
					int id = spec.indexOf('[');
					spec = spec.substring(1, id);
					
					// genotypespec
					//spec = br.readLine();
					
					String allloc = split[1];
					id = allloc.indexOf("_lenfilt");
					allloc = allloc.substring(0, id);
					String loc = filt( allloc, tval );
					
					if( !allcount.containsKey( loc ) ) {
						allcount.put( loc, 1 );
					} else allcount.put( loc, allcount.get(loc)+1 );
					
					Map<String,Integer>	submset;
					if( !mset.containsKey(spec) ) {
						submset = new HashMap<String,Integer>();
						mset.put( spec, submset );
					} else submset = mset.get( spec );
					
					if( !submset.containsKey(loc) ) {
						submset.put( loc, 1 );
					} else {
						submset.put( loc, submset.get(loc)+1 );
					}
					
					Map<String,Integer>	suballmset;
					if( !allmset.containsKey(spec) ) {
						suballmset = new HashMap<String,Integer>();
						allmset.put( spec, suballmset );
					} else suballmset = allmset.get( spec );
					
					if( !suballmset.containsKey(allloc) ) {
						//loc = filt( loc, tval );
						//System.err.println( "allloc "+allloc + suballmset.get(allloc) );
						suballmset.put( allloc, 1 );
					} else {
						suballmset.put( allloc, suballmset.get(allloc)+1 );
					}
				}
				line = br.readLine();
			}
			br.close();
			
			Map<String,String>	namemap = new HashMap<String,String>();
			//File nseqf = new File( args[i+2] );
			FileReader nseqfr = new FileReader( inf );//nseqf );
			BufferedReader nseqbr = new BufferedReader( nseqfr );
			String nsline = nseqbr.readLine();
			while( nsline != null ) {
				if( nsline.startsWith(">") ) {
					int u = nsline.indexOf(' ');
					String spc = nsline.substring(1,u);
					// gtspec
					spc = nseqbr.readLine();
					
					//if( spc.indexOf('.') != -1 ) {
						//System.err.println( spc + "  " + mset.keySet() );
						if( mset.containsKey(spc) ) {
							int li = nsline.lastIndexOf(';');
							String strval = nsline.substring(li+1, nsline.length());
							if( strval.contains("uncult") ) {
								strval = nsline.substring(nsline.lastIndexOf(';', li-1)+1, nsline.length());
							}
							namemap.put(spc, strval);
						}
					//}
				}
				nsline = nseqbr.readLine();
			}
			nseqfr.close();
			System.err.println( namemap.keySet() );
			
			List<String>	loclist = new ArrayList<String>( allcount.keySet() );
			FileWriter fw = new FileWriter( outf );
			for( String loc : loclist ) {
				fw.write( "\t"+loc );
			}
			fw.write("\ttotal\tavg temp\tavg pH");
			for( String key : mset.keySet() ) {
				Map<String,Integer> locmap = mset.get(key);
				Map<String,Integer> alllocmap = allmset.get(key);
				
				/*System.err.println( alllocmap.size()  );
				for( String str : alllocmap.keySet() ) {
					System.err.println( str );
				}*/
				
				int count = 0;
				double temp = 0.0;
				double pH = 0.0;
				
				//fw.write( "\n"+(namemap.containsKey(key) ? namemap.get(key) + " ("+key+")" : key) );
				fw.write( "\n"+key );
				for( String loc : loclist ) {
					if( !locmap.containsKey(loc) ) fw.write( "\t0" );
					else {
						int val = locmap.get(loc);
						
						for( String allloc : alllocmap.keySet() ) {
							if( isin( allloc, loc, tval ) ) {
							//if( allloc.contains( loc ) ) {
								//System.err.println( allloc + "  " + loc );
								int nval = alllocmap.get(allloc);
								temp += nval*snaedisheatmap.get(allloc);
								pH += nval*snaedisphmap.get(allloc);
							}
						}
						
						fw.write( "\t"+val );
						count += val;
					}
				}
				fw.write( "\t"+count );
				
				fw.write( "\t"+Math.round(10.0*temp/count)/10.0 );
				fw.write( "\t"+Math.round(10.0*pH/count)/10.0 );
			}
			fw.write("\ntotal Thermaceae");
			int total = 0;
			for( String loc : loclist ) {
				int val = allcount.get(loc);
				fw.write( "\t"+val );
				total += val;
			}
			fw.write( "\t"+total );
			total = 0;
			fw.write("\ntotal sequences");
			for( String loc : loclist ) {
				//System.err.println("loc "+loc + " " + seqcount);
				int val = seqcount.containsKey(loc) ? seqcount.get(loc) : -1;
				fw.write( "\t"+val );
				total += val;
			}
			fw.write( "\t"+total );
			
			double avg = 0;
			fw.write("\ntemp");
			for( String loc : loclist ) {
				double val = 0;
				int count = 0;
				for( String allloc : allloclist) {
					//System.err.println( allloc + "  " + loc );
					//System.err.println( snaedisheatmap );
					if( isin( allloc, loc, tval ) ) {
						
						//if( allloc.contains(loc) ) {
							val += snaedisheatmap.get(allloc);
							count++;
						//}
					}
				}
				val /= count;
				
				double var = 0.0;
				for( String allloc : allloclist) {
					if( isin( allloc, loc, tval ) ) {
						double diff = val-snaedisheatmap.get(allloc);
						var = diff*diff;
					}
				}
				var /= count;
				
				fw.write( "\t"+Math.round(10.0*val)/10.0+"+-"+Math.round(10.0*Math.sqrt(var))/10.0 );
				avg += val;
			}
			fw.write( "\t"+Math.round(10.0*avg/allloclist.size())/10.0 );
			
			avg = 0;
			fw.write("\npH");
			for( String loc : loclist ) {
				double val = 0;
				int count = 0;
				for( String allloc : allloclist) {
					if( isin( allloc, loc, tval ) ) {
					//if( allloc.contains(loc) ) {
						val += snaedisphmap.get(allloc);
						count++;
					}
				}
				val /= count;
				
				double var = 0.0;
				for( String allloc : allloclist) {
					if( isin( allloc, loc, tval ) ) {
						double diff = val-snaedisphmap.get(allloc);
						var = diff*diff;
					}
				}
				var /= count;
				
				//fw.write( "\t"+Math.round(10.0*val)/10.0 );
				fw.write( "\t"+Math.round(10.0*val)/10.0+"+-"+Math.round(10.0*Math.sqrt(var))/10.0 );
				avg += val;
			}
			fw.write( "\t"+Math.round(10.0*avg/allloclist.size())/10.0 );
			fw.write( "\n" );
			fw.close();
		}
		
		i = arglist.indexOf("-matrix");
		if( i >= 0 ) {
			int sec = 1;
			Map<String,Map<String,Integer>>	mset = new HashMap<String,Map<String,Integer>>();
			Set<String>				allset = new HashSet<String>();
			for( Sequences seqs : this.sequences ) {
				BufferedReader br = Files.newBufferedReader(seqs.getPath(), Charset.defaultCharset());
				String line = br.readLine();
				while( line != null ) {
					if( line.startsWith(">") ) {
						int perci = line.indexOf('%');
						String name;
						if( perci == -1 ) {
							name = br.readLine();
						} else {
							int nind = line.lastIndexOf('_', perci);
							name = line.substring(1, nind);
						}
						
						Map<String,Integer>	locset;
						if( !mset.containsKey(name) ) {
							locset = new HashMap<String,Integer>();
							mset.put( name, locset );
						} else {
							locset = mset.get( name );
						}
						
						int semi = line.indexOf(';');
						int stri = line.indexOf('_', semi+1);
						int endi = line.lastIndexOf('_');
						endi = line.lastIndexOf('_',endi-1);
						endi = line.lastIndexOf('_',endi-1);
						
						String locname = line.substring(stri+1,endi);
						if( locset.containsKey( locname ) ) {
							locset.put( locname, locset.get(locname) | sec );
						} else {
							locset.put( locname, sec );
						}
						allset.add( locname );
					}
					line = br.readLine();
				}
				br.close();
				
				sec <<= 1;
			}
			
			List<String>	loclist = new ArrayList<String>( allset );
			FileWriter fw = new FileWriter( outf );
			for( String loc : loclist ) {
				fw.write( "\t"+loc );
			}
			for( String key : mset.keySet() ) {
				Map<String,Integer> locmap = mset.get(key);
				
				fw.write( "\n"+key );
				for( String loc : loclist ) {
					if( !locmap.containsKey(loc) ) fw.write( "\t0" );
					else {
						fw.write( "\t"+locmap.get(loc) );
					}
				}
			}
			fw.write("\n");
			fw.close();
		}
		
		i = arglist.indexOf("-fix");
		if( i >= 0 ) {
			FileWriter fw = new FileWriter( outf );
			FileReader fr = new FileReader( inf );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) fw.write( line+"\n" );
				else fw.write( line.replace(" ", "").replace('.', '-').replace('U', 'T')+"\n" );
				line = br.readLine();
			}
			fw.close();
			br.close();
			fr.close();
		}
		
		i = arglist.indexOf("-extract");
		if( i >= 0 ) {
			Map<String,String>	tagmap = new HashMap<String,String>();
			File f = new File( args[i+1] );
			FileReader fr = new FileReader( f );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				String[] split = line.split(",");
				if( split.length > 1 ) tagmap.put( split[1], split[0] );
				line = br.readLine();
			}
			br.close();
			fr.close();
			
			//int len = 0;
			//i = arglist.indexOf("-lenfilt");
			//if( i != -1 ) len = Integer.parseInt(args[i+1]);
			i = arglist.indexOf("-primer");
			String primer = args[i+1];
			extractSequences(inf, tagmap, primer, outf);
		}
		
		i = arglist.indexOf("-removegaps");
		if( i >= 0 ) {
			for( Sequences seqs : this.sequences ) {
				appendSequenceInJavaFasta( seqs, null, true);
			}
			removeGaps( lseq );
			writeFasta( lseq, new FileWriter( outf ), null );
		}
		
		i = arglist.indexOf("-removeallgaps");
		if( i >= 0 ) {
			for( Sequences seqs : this.sequences ) {
				appendSequenceInJavaFasta( seqs, null, true);
			}
			removeAllGaps( lseq );
			writeFasta( lseq, new FileWriter( outf ), null );
		}
		
		i = arglist.indexOf("-cut");
		if( i >= 0 ) {
			int cutval = Integer.parseInt(args[i+1]);
			
			for( Sequences seqs : this.sequences ) {
				appendSequenceInJavaFasta( seqs, null, true);
			}
			writeFasta( lseq, new FileWriter( outf ), new Rectangle(0,0,cutval,0) );
			/*FileWriter fw = new FileWriter(outf);
			FileReader fr = new FileReader( inf );
			trimFasta( new BufferedReader(fr), fw, makeFset(args[i+1]), false, false );
			fr.close();
			fw.close();*/
		}
		
		i = arglist.indexOf("-trim");
		if( i >= 0 ) {
			String add = "";
			if( i+2 < args.length && !args[i+2].startsWith("-") ) {
				add = " "; //args[i+1];
			}
			FileWriter fw = new FileWriter(outf);
			FileReader fr = new FileReader( inf );
			trimFasta( new BufferedReader(fr), fw, makeFset(args[i+1], add), false, false );
			fr.close();
			fw.close();
		}
		
		i = arglist.indexOf("-ntrim");
		if( i >= 0 ) {
			String add = "";
			if( i+2 < args.length && !args[i+2].startsWith("-") ) {
				add = " "; //args[i+1];
			}
			FileWriter fw = new FileWriter(outf);
			FileReader fr = new FileReader( inf );
			trimFasta( new BufferedReader(fr), fw, makeFset(args[i+1], add), true, false );
			fr.close();
			fw.close();
		}
		
		i = arglist.indexOf("-ntrime");
		if( i >= 0 ) {
			System.err.println("doing ntrime");
			FileWriter fw = new FileWriter( outf );
			FileReader fr = new FileReader( inf );
			trimFasta( new BufferedReader(fr), fw, makeFset(args[i+1]), true, true );
			fr.close();
			fw.close();
		}
		
		i = arglist.indexOf("-appendfilename");
		if( i >= 0 ) {
			for( Sequences seqs : this.sequences ) {
				String fname = seqs.getPath().getFileName().toString();
				
				File of = new File( outf, fname );
				int k = fname.lastIndexOf('.');
				if( k == -1 ) k = fname.length();
				fname = fname.substring(0, k);
				
				FileWriter fw = new FileWriter( of );
				
				BufferedReader br = Files.newBufferedReader( seqs.getPath(), Charset.defaultCharset() );
				String line = br.readLine();
				while( line != null ) {
					if( line.startsWith(">") ) {
						fw.write( ">"+fname+"_"+line.substring(1, line.length())+"\n" );
					} else fw.write( line+"\n" );
					
					line = br.readLine();
				}
				br.close();
				fw.close();
			}
			
			/*Sequences ret = blastRename( this.sequences.get(0), args[i+1], outf, false );
			
			appendSequenceInJavaFasta(ret, null, true);
			writeFasta( lseq, new FileWriter( outf ), null);
			
			FileWriter fw = new FileWriter( outf );
			FileReader fr = new FileReader( inf );
			trimFasta( new BufferedReader(fr), fw, makeFset(args[i+1]), false, false );
			fr.close();
			fw.close();*/
		}
		
		i = arglist.indexOf("-rename");
		if( i >= 0 ) {
			/*Sequences ret = blastRename( this.sequences.get(0), args[i+1], outf, false );
			
			appendSequenceInJavaFasta(ret, null, true);
			writeFasta( lseq, new FileWriter( outf ), null);*/
			
			FileWriter fw = new FileWriter( outf );
			FileReader fr = new FileReader( inf );
			trimFasta( new BufferedReader(fr), fw, makeFset(args[i+1]), false, false );
			fr.close();
			fw.close();
		}
		
		i = arglist.indexOf("-subspecsample");
		if( i >= 0 ) {
			int smplnum = Integer.parseInt( args[i+1] );
			
			appendSequenceInJavaFasta( this.sequences.get(0), null, true);
			List<Sequence> retseq = subsample( lseq, smplnum, true );
			FileWriter fw = new FileWriter( outf );
			writeFasta( retseq, fw, null);
			fw.close();
		}
		
		i = arglist.indexOf("-subsample");
		if( i >= 0 ) {
			int smplnum = Integer.parseInt( args[i+1] );
			
			appendSequenceInJavaFasta( this.sequences.get(0), null, true);
			List<Sequence> retseq = subsample( lseq, smplnum, false );
			FileWriter fw = new FileWriter( outf );
			writeFasta( retseq, fw, null);
			fw.close();
		}
		
		i = arglist.indexOf("-blast");
		if( i >= 0 ) {
			Sequences ret = blastRename( this.sequences.get(0), args[i+1], outf, false );
			
			appendSequenceInJavaFasta(ret, null, true);
			FileWriter fw = new FileWriter( outf );
			writeFasta( lseq, fw, null);
			fw.close();
		}
		
		i = arglist.indexOf("-gbk");
		if( i >= 0 ) {
			genbankFromNR(this.sequences.get(0), new File( args[i+1] ), outf, true);
		} else {
			i = arglist.indexOf("-gb");
			if( i >= 0 ) {
				genbankFromNR(this.sequences.get(0), new File( args[i+1] ), outf, false);
				//Sequences ret = blastRename( this.sequences.get(0), args[i+1], outf, false );
				
				/*appendSequenceInJavaFasta(ret, null, true);
				FileWriter fw = new FileWriter( outf );
				writeFasta( lseq, fw, null);
				fw.close();*/
			}
		}
		
		i = arglist.indexOf("-keepblasthits");
		if( i >= 0 ) {
			FileWriter fw = new FileWriter(outf);
			FileReader fr = new FileReader( inf );
			Map<String,String> map = makeFset(args[i+1]);
			trimFasta( new BufferedReader(fr), fw, map, false, false );
			fr.close();
			fw.close();
		}
		
		i = arglist.indexOf("-fixwith");
		if( i >= 0 ) {
			FileWriter fw = new FileWriter(outf);
			FileReader fr = new FileReader( inf );
			
			Map<String,String> map = new HashMap<String,String>(); 
			FileReader fr2 = new FileReader( args[i+1] );
			BufferedReader br = new BufferedReader( fr2 );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					int k = line.lastIndexOf('[');
					if( k != -1 ) {
						map.put( line.substring(k).trim(), line.substring(1).trim() );
					}
				}
				line = br.readLine();
			}
			br.close();
			fr2.close();
			
			trimFasta( new BufferedReader(fr), fw, map, false, false );
			fr.close();
			fw.close();
		}
		
		i = arglist.indexOf("-usearch");
		if( i >= 0 ) {
			Sequences ret = blastRename( this.sequences.get(0), args[i+1], outf, false );
			
			appendSequenceInJavaFasta(ret, null, true);
			FileWriter fw = new FileWriter( outf );
			writeFasta( lseq, fw, null);
			fw.close();
		}
		
		i = arglist.indexOf("-blastwl");
		if( i >= 0 ) {
			Sequences ret = blastRename( this.sequences.get(0), args[i+1], outf, true );
			
			appendSequenceInJavaFasta(ret, null, true);
			FileWriter fw = new FileWriter( outf );
			writeFasta( lseq, fw, null);
			fw.close();
		}
		
		i = arglist.indexOf("-ft");
		if( i >= 0 ) {
			List<Sequences> retlseqs = fastTreePrepare( this.sequences );
			for( Sequences seqs : retlseqs ) {
				appendSequenceInJavaFasta( seqs, null, true);
			}
			FileWriter fw = new FileWriter( outf );
			writeFasta( lseq, fw, null);
			fw.close();
		}
		
		i = arglist.indexOf("-append");
		if( i >= 0 ) {
			boolean val = true;
			String mappingfile = null;
			if( i+1 < args.length && !args[i+1].startsWith("-") ) {
				//val = false;
				mappingfile = args[i+1];
			}
			List<Sequences> retlseqs = join( outf.toPath(), this.sequences, val, mappingfile, false );
			/*for( Sequences seqs : retlseqs ) {
				System.err.println( seqs.getName() );
				appendSequenceInJavaFasta( seqs, null, val);
			}
			writeFasta( lseq, new FileWriter( outf ), null);*/
		}
		
		i = arglist.indexOf("-join");
		if( i >= 0 ) {
			boolean val = true;
			String mappingfile = null;
			if( i+1 < args.length && !args[i+1].startsWith("-") ) {
				//val = false;
				mappingfile = args[i+1];
			}
			List<Sequences> retlseqs = join( outf.toPath(), this.sequences, val, mappingfile, true );
			/*for( Sequences seqs : retlseqs ) {
				System.err.println( seqs.getName() );
				appendSequenceInJavaFasta( seqs, null, val);
			}
			writeFasta( lseq, new FileWriter( outf ), null);*/
		}
		
		i = arglist.indexOf("-split");
		if( i >= 0 ) {
			int splnum = Integer.parseInt( args[i+1] );
			
			for( Sequences seqs : this.sequences ) {
				seqs.setNSeq( countSequences( inf ) );
				List<Sequences> retlseqs = splitit( splnum, seqs, outf == null ? new File(".") : outf );
				/*for( Sequences nseqs : retlseqs ) {
					appendSequenceInJavaFasta( nseqs, null, true);
					File noutf = new File( nseqs.getPath() );
					writeFasta( lseq, new FileWriter( noutf ), null );
				}*/
			}
		}
		
		i = arglist.indexOf("-concrand");
		if( i >= 0 ) {
			int cnum = Integer.parseInt( args[i+1] );
			Random r = new Random();
			for( int l = 0; l < 1000; l++ ) {
				List<BufferedReader>	lrd = new ArrayList<BufferedReader>();
				for( int k = 0; k < cnum; k++ ) {
					Sequences seqs = this.sequences.get( r.nextInt( this.sequences.size() ) );
					lrd.add( Files.newBufferedReader(seqs.getPath(), Charset.defaultCharset()) );
				}
				Map<String,StringBuilder> smap = concat( lrd );
				
				FileWriter fw = new FileWriter( new File( outf, "conc"+cnum+"_"+l+".fasta" ) );
				for( String key : smap.keySet() ) {
					fw.write( ">"+key+"\n" );
					StringBuilder sb = smap.get( key );
					for (int k = 0; k < sb.length(); k += 70) {
						fw.append(sb.substring(k, Math.min(k + 70, sb.length())) + "\n");
					}
				}
				fw.close();
			}
		}
		
		i = arglist.indexOf("-conc");
		if( i >= 0 ) {
			List<BufferedReader>	lrds = new ArrayList<BufferedReader>();
			for( Sequences seqs : this.sequences ) {
				lrds.add( Files.newBufferedReader( seqs.getPath(), Charset.defaultCharset()) );
			}
			Map<String,StringBuilder> smap = concat( lrds );
			
			FileWriter fw = new FileWriter( outf );
			for( String key : smap.keySet() ) {
				fw.write( ">"+key+"\n" );
				StringBuilder sb = smap.get( key );
				for (int k = 0; k < sb.length(); k += 70) {
					fw.append(sb.substring(k, Math.min(k + 70, sb.length())) + "\n");
				}
			}
			fw.close();
		}
		
		i = arglist.indexOf("-addtag");
		if( i >= 0 ) {
			String addtag = args[i+1];
		}
		
		i = arglist.indexOf("-lenfilt");
		if( i >= 0 ) {
			int lfilt = Integer.parseInt( args[i+1] );
			
			for( Sequences seqs : this.sequences ) {
				filtit(lfilt, seqs, outf);
				//seqs.setNSeq( countSequences( inf ) );
				//List<Sequences> retlseqs = splitit( splnum, seqs, outf == null ? new File(".") : outf );
				/*for( Sequences nseqs : retlseqs ) {
					appendSequenceInJavaFasta( nseqs, null, true);
					File noutf = new File( nseqs.getPath() );
					writeFasta( lseq, new FileWriter( noutf ), null );
				}*/
			}
		}
		
		i = arglist.indexOf("-clust");
		if( i >= 0 ) {
			String blastfile = args[i+1];
			int splnum = Integer.parseInt( args[i+2] );
			
			makeBlastCluster( /*inf,*/ outf.toPath(), Paths.get(blastfile), splnum );
			//for( Sequences seqs : this.sequences ) {
				//seqs.setNSeq( countSequences( inf ) );
				//List<Sequences> retlseqs = splitit( splnum, seqs, outf == null ? new File(".") : outf );
				/*for( Sequences nseqs : retlseqs ) {
					appendSequenceInJavaFasta( nseqs, null, true);
					File noutf = new File( nseqs.getPath() );
					writeFasta( lseq, new FileWriter( noutf ), null );
				}*/
			//}
		}
		
		i = arglist.indexOf("-head");
		if( i >= 0 ) {
			int count = Integer.parseInt( args[i+1] );
			//int count = Integer.parseInt( args[i+2] );
			
			//makeBlastCluster( /*inf,*/ outf.toPath(), Paths.get(blastfile), splnum );
			for( Sequences seqs : this.sequences ) {
				appendSequenceInJavaFasta(seqs, null, false);
				//seqs.setNSeq( countSequences( inf ) );
				//List<Sequences> retlseqs = splitit( splnum, seqs, outf == null ? new File(".") : outf );
				/*for( Sequences nseqs : retlseqs ) {
					appendSequenceInJavaFasta( nseqs, null, true);
					File noutf = new File( nseqs.getPath() );
					writeFasta( lseq, new FileWriter( noutf ), null );
				}*/
			}
			FileWriter fw = new FileWriter( outf );
			this.writeFasta(this.lseq, fw, null);
			fw.close();
		}
	}
	
	Map<String,String>	snaedismap;
	Map<String,String>	snaediscolormap;
	Map<String,Double>	snaedisheatmap;
	Map<String,Double>	snaedisphmap;
	
	Map<String,String>	snaedis1map;
	Map<String,String>	snaedis2map;
	Map<String,String>	snaedis3map;
	Map<String,String>	snaedis4map;
	Map<String,String>	snaedis5map;
	Map<String,String>	snaedis6map;
	Map<String,String>	snaedis7map;
	Map<String,String>	snaedis8map;

	Map<String,String>	snaedis1colormap;
	Map<String,String>	snaedis2colormap;
	Map<String,String>	snaedis3colormap;
	Map<String,String>	snaedis4colormap;
	Map<String,String>	snaedis5colormap;
	Map<String,String>	snaedis6colormap;
	Map<String,String>	snaedis7colormap;
	Map<String,String>	snaedis8colormap;
	
	Map<String,Double>	snaedis1heatmap;
	Map<String,Double>	snaedis2heatmap;
	Map<String,Double>	snaedis3heatmap;
	Map<String,Double>	snaedis4heatmap;
	Map<String,Double>	snaedis5heatmap;
	Map<String,Double>	snaedis6heatmap;
	Map<String,Double>	snaedis7heatmap;
	Map<String,Double>	snaedis8heatmap;
	
	Map<String,Double>	snaedis1phmap;
	Map<String,Double>	snaedis2phmap;
	Map<String,Double>	snaedis3phmap;
	Map<String,Double>	snaedis4phmap;
	Map<String,Double>	snaedis5phmap;
	Map<String,Double>	snaedis6phmap;
	Map<String,Double>	snaedis7phmap;
	Map<String,Double>	snaedis8phmap;
	
	public void initMaps() {		
		snaedis1map = new HashMap<String,String>();
		snaedis1map.put( "770_geysir_north_jardvegur", "ACGAGTGCGT" );
		snaedis1map.put( "770_geysir_north_vatn", "ACGCTCGACA" );
		snaedis1map.put( "771_geysir_north_jardvegur", "AGACGCACTC" );
		snaedis1map.put( "771_geysir_north_vatn", "AGCACTGTAG" );
		snaedis1map.put( "772_geysir_north_jardvegur", "ATCAGACACG" );
		snaedis1map.put( "772_geysir_north_vatn", "ATATCGCGAG" );
		snaedis1map.put( "773_geysir_west_jardvegur", "CGTGTCTCTA" );
		snaedis1map.put( "773_geysir_west_vatn", "CTCGCGTGTC" );
		snaedis1map.put( "774_geysir_west_jardvegur", "TGATACGTCT" );
		snaedis1map.put( "774_geysir_west_vatn", "TCTCTATGCG" );
		
		snaedis2map = new HashMap<String,String>();
		snaedis2map.put( "775_geysir_west_jardvegur", "ACGAGTGCGT" );
		snaedis2map.put( "775_geysir_west_vatn", "ACGCTCGACA" );
		snaedis2map.put( "776_geysir_west_jardvegur", "AGACGCACTC" );
		snaedis2map.put( "776_geysir_west_vatn", "AGCACTGTAG" );
		snaedis2map.put( "777_fludir_vatn", "ATCAGACACG" );
		snaedis2map.put( "777_fludir_lifmassi", "ATATCGCGAG" );
		snaedis2map.put( "778_fludir_jardvegur", "CGTGTCTCTA" );
		snaedis2map.put( "778_fludir_vatn", "CTCGCGTGTC" );
		snaedis2map.put( "779_fludir_jardvegur", "TGATACGTCT" );
		snaedis2map.put( "779_fludir_vatn", "TCTCTATGCG" );
		
		snaedis3map = new HashMap<String,String>();
		snaedis3map.put( "780_fludir_jardvegur", "ACGAGTGCGT" );
		snaedis3map.put( "780_fludir_vatn", "ACGCTCGACA" );
		snaedis3map.put( "781_olkelduhals_vatn", "AGACGCACTC" );
		snaedis3map.put( "781_olkelduhals_lifmassi", "AGCACTGTAG" );
		snaedis3map.put( "782_olkelduhals_lifmassi", "ATCAGACACG" );
		snaedis3map.put( "783_olkelduhals_jardvlifm", "ATATCGCGAG" );
		snaedis3map.put( "783_olkelduhals_vatn", "CGTGTCTCTA" );
		snaedis3map.put( "808_hrafntinnusker_jardvegur", "CTCGCGTGTC" );
		snaedis3map.put( "808_hrafntinnusker_vatn", "TGATACGTCT" );
		snaedis3map.put( "808_hrafntinnusker_lifmassi", "TCTCTATGCG" );
		
		snaedis4map = new HashMap<String,String>();
		snaedis4map.put( "809_hrafntinnusker_jardvegur", "ACGAGTGCGT" );
		snaedis4map.put( "809_hrafntinnusker_vatn", "ACGCTCGACA" );
		snaedis4map.put( "809_hrafntinnusker_lifmassi", "AGACGCACTC" );
		snaedis4map.put( "810_hrafntinnusker_jardvegur", "AGCACTGTAG" );
		snaedis4map.put( "810_hrafntinnusker_vatn", "ATCAGACACG" );
		snaedis4map.put( "810_hrafntinnusker_lifmassi", "ATATCGCGAG" );
		snaedis4map.put( "811_hrafntinnusker_jardvegur", "CGTGTCTCTA" );
		snaedis4map.put( "811_hrafntinnusker_vatn", "CTCGCGTGTC" );
		snaedis4map.put( "811_hrafntinnusker_lifmassi", "TGATACGTCT" );
		snaedis4map.put( "812_hrafntinnusker_jardvegur", "TCTCTATGCG" );
	
		snaedis5map = new HashMap<String,String>();
		snaedis5map.put( "812_hrafntinnusker_vatn", "ACGAGTGCGT" );
		snaedis5map.put( "813_hrafntinnusker_jardvegur", "ACGCTCGACA" );
		snaedis5map.put( "813_hrafntinnusker_vatn", "AGACGCACTC" );
		snaedis5map.put( "814_hrafntinnusker_jardvegur", "AGCACTGTAG" );
		snaedis5map.put( "814_hrafntinnusker_vatn", "ATCAGACACG" );
		snaedis5map.put( "815_reykjadalir_jardvegur", "ATATCGCGAG" );
		snaedis5map.put( "815_reykjadalir_vatn", "CGTGTCTCTA" );
		snaedis5map.put( "815_reykjadalir_lifmassi", "CTCGCGTGTC" );
		snaedis5map.put( "816_vondugil_jardvegur", "TGATACGTCT" );
		snaedis5map.put( "816_vondugil_vatn", "TCTCTATGCG" );
		
		snaedis6map = new HashMap<String,String>();
		snaedis6map.put( "817_vondugil_jardvegur", "ACGAGTGCGT" );
		snaedis6map.put( "817_vondugil_vatn", "ACGCTCGACA" );
		snaedis6map.put( "818_vondugil_jardvegur", "AGACGCACTC" );
		snaedis6map.put( "818_vondugil_vatn", "AGCACTGTAG" );
		snaedis6map.put( "819_vondugil_jardvegur", "ATCAGACACG" );
		snaedis6map.put( "819_vondugil_vatn", "ATATCGCGAG" );
		snaedis6map.put( "820_vondugil_jardvegur", "CGTGTCTCTA" );
		snaedis6map.put( "820_vondugil_vatn", "CTCGCGTGTC" );
		snaedis6map.put( "821_vondugil_jardvegur", "TGATACGTCT" );
		snaedis6map.put( "821_vondugil_vatn", "TCTCTATGCG" );
		
		snaedis7map = new HashMap<String,String>();
		snaedis7map.put( "846_hurdarbak_jardvegur", "ACGAGTGCGT" );
		snaedis7map.put( "846_hurdarbak_vatn", "ACGCTCGACA" );
		snaedis7map.put( "846_hurdarbak_lifmassi", "AGACGCACTC" );
		snaedis7map.put( "847_hurdarbak_jardvegur", "AGCACTGTAG" );
		snaedis7map.put( "847_hurdarbak_vatn", "ATCAGACACG" );
		snaedis7map.put( "848_kleppjarnsreykir_jardvegur", "ATATCGCGAG" );
		snaedis7map.put( "848_kleppjarnsreykir_vatn", "CGTGTCTCTA" );
		snaedis7map.put( "848_kleppjarnsreykir_lifmassi", "CTCGCGTGTC" );
		snaedis7map.put( "849_kleppjarnsreykir_jardvegur", "TGATACGTCT" );
		snaedis7map.put( "849_kleppjarnsreykir_vatn", "TCTCTATGCG" );
	
		snaedis8map = new HashMap<String,String>();
		snaedis8map.put( "849_kleppjarnsreykir_lifmassi", "ACGAGTGCGT" );
		snaedis8map.put( "850_kleppjarnsreykir_jardvegur", "ACGCTCGACA" );
		snaedis8map.put( "850_kleppjarnsreykir_vatn", "AGACGCACTC" );
		snaedis8map.put( "850_kleppjarnsreykir_lifmassi", "AGCACTGTAG" );
		snaedis8map.put( "851_deildartunguhver_jardvegur", "ATCAGACACG" );
		snaedis8map.put( "851_deildartunguhver_vatn", "ATATCGCGAG" );
		snaedis8map.put( "852_deildartunguhver_jardvegur", "CGTGTCTCTA" );
		snaedis8map.put( "852_deildartunguhver_vatn", "CTCGCGTGTC" );
		
		
		
		snaedis1colormap = new HashMap<String,String>();
		snaedis1colormap.put( "770_geysir_north_jardvegur", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "770_geysir_north_vatn", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "771_geysir_north_jardvegur", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "771_geysir_north_vatn", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "772_geysir_north_jardvegur", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "772_geysir_north_vatn", "0.0\t0.5\t1.0\n" );
		snaedis1colormap.put( "773_geysir_west_jardvegur", "0.0\t1.0\t0.5\n" );
		snaedis1colormap.put( "773_geysir_west_vatn", "0.0\t1.0\t0.5\n" );
		snaedis1colormap.put( "774_geysir_west_jardvegur", "0.0\t1.0\t0.5\n" );
		snaedis1colormap.put( "774_geysir_west_vatn", "0.0\t1.0\t0.5\n" );
		
		snaedis1heatmap = new HashMap<String,Double>();
		snaedis1heatmap.put( "770_geysir_north_jardvegur", 83.0 );
		snaedis1heatmap.put( "770_geysir_north_vatn", 83.0 );
		snaedis1heatmap.put( "771_geysir_north_jardvegur", 72.0 );
		snaedis1heatmap.put( "771_geysir_north_vatn", 72.0 );
		snaedis1heatmap.put( "772_geysir_north_jardvegur", 68.5 );
		snaedis1heatmap.put( "772_geysir_north_vatn", 68.5 );
		snaedis1heatmap.put( "773_geysir_west_jardvegur", 79.4 );
		snaedis1heatmap.put( "773_geysir_west_vatn", 79.4 );
		snaedis1heatmap.put( "774_geysir_west_jardvegur", 88.0 );
		snaedis1heatmap.put( "774_geysir_west_vatn", 88.0 );
		
		snaedis1phmap = new HashMap<String,Double>();
		snaedis1phmap.put( "770_geysir_north_jardvegur", 6.75 );
		snaedis1phmap.put( "770_geysir_north_vatn", 6.75 );
		snaedis1phmap.put( "771_geysir_north_jardvegur", 6.0 );
		snaedis1phmap.put( "771_geysir_north_vatn", 6.0 );
		snaedis1phmap.put( "772_geysir_north_jardvegur", 5.0 );
		snaedis1phmap.put( "772_geysir_north_vatn", 5.0 );
		snaedis1phmap.put( "773_geysir_west_jardvegur", 9.0 );
		snaedis1phmap.put( "773_geysir_west_vatn", 9.0 );
		snaedis1phmap.put( "774_geysir_west_jardvegur", 8.0 );
		snaedis1phmap.put( "774_geysir_west_vatn", 8.0 );
		
		
		
		snaedis2colormap = new HashMap<String,String>();
		snaedis2colormap.put( "775_geysir_west_jardvegur", "0.0\t1.0\t0.5\n" );
		snaedis2colormap.put( "775_geysir_west_vatn", "0.0\t1.0\t0.5\n" );
		snaedis2colormap.put( "776_geysir_west_jardvegur", "0.0\t1.0\t0.5\n" );
		snaedis2colormap.put( "776_geysir_west_vatn", "0.0\t1.0\t0.5\n" );
		snaedis2colormap.put( "777_fludir_vatn", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "777_fludir_lifmassi", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "778_fludir_jardvegur", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "778_fludir_vatn", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "779_fludir_jardvegur", "1.0\t0.0\t1.0\n" );
		snaedis2colormap.put( "779_fludir_vatn", "1.0\t0.0\t1.0\n" );
		
		snaedis2heatmap = new HashMap<String,Double>();
		snaedis2heatmap.put( "775_geysir_west_jardvegur", 83.0 );
		snaedis2heatmap.put( "775_geysir_west_vatn", 83.0 );
		snaedis2heatmap.put( "776_geysir_west_jardvegur", 88.0 );
		snaedis2heatmap.put( "776_geysir_west_vatn", 88.0 );
		snaedis2heatmap.put( "777_fludir_vatn", 63.0 );
		snaedis2heatmap.put( "777_fludir_lifmassi", 63.0 );
		snaedis2heatmap.put( "778_fludir_jardvegur", 69.4 );
		snaedis2heatmap.put( "778_fludir_vatn", 69.4 );
		snaedis2heatmap.put( "779_fludir_jardvegur", 79.1 );
		snaedis2heatmap.put( "779_fludir_vatn", 79.1 );
		
		snaedis2phmap = new HashMap<String,Double>();
		snaedis2phmap.put( "775_geysir_west_jardvegur", 9.0 );
		snaedis2phmap.put( "775_geysir_west_vatn", 9.0 );
		snaedis2phmap.put( "776_geysir_west_jardvegur", 7.0 );
		snaedis2phmap.put( "776_geysir_west_vatn", 7.0 );
		snaedis2phmap.put( "777_fludir_vatn", 8.0 );
		snaedis2phmap.put( "777_fludir_lifmassi", 8.0 );
		snaedis2phmap.put( "778_fludir_jardvegur", 8.25 );
		snaedis2phmap.put( "778_fludir_vatn", 8.25 );
		snaedis2phmap.put( "779_fludir_jardvegur", 8.0 );
		snaedis2phmap.put( "779_fludir_vatn", 8.0 );
		
		
		
		snaedis3colormap = new HashMap<String,String>();
		snaedis3colormap.put( "780_fludir_jardvegur", "1.0\t0.0\t1.0\n" );
		snaedis3colormap.put( "780_fludir_vatn", "1.0\t0.0\t1.0\n" );
		snaedis3colormap.put( "781_olkelduhals_vatn", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "781_olkelduhals_lifmassi", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "782_olkelduhals_lifmassi", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "783_olkelduhals_jardvlifm", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "783_olkelduhals_vatn", "1.0\t1.0\t0.0\n" );
		snaedis3colormap.put( "808_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis3colormap.put( "808_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis3colormap.put( "808_hrafntinnusker_lifmassi", "0.0\t0.0\t1.0\n" );
		
		snaedis3heatmap = new HashMap<String,Double>();
		snaedis3heatmap.put( "780_fludir_jardvegur", 87.6 );
		snaedis3heatmap.put( "780_fludir_vatn", 87.6 );
		snaedis3heatmap.put( "781_olkelduhals_vatn", 70.0 );
		snaedis3heatmap.put( "781_olkelduhals_lifmassi", 70.0 );
		snaedis3heatmap.put( "782_olkelduhals_lifmassi", 60.0 );
		snaedis3heatmap.put( "783_olkelduhals_jardvlifm", 70.0 );
		snaedis3heatmap.put( "783_olkelduhals_vatn", 70.0 );
		snaedis3heatmap.put( "808_hrafntinnusker_jardvegur", 72.0 );
		snaedis3heatmap.put( "808_hrafntinnusker_vatn", 72.0 );
		snaedis3heatmap.put( "808_hrafntinnusker_lifmassi", 72.0 );
		
		snaedis3phmap = new HashMap<String,Double>();
		snaedis3phmap.put( "780_fludir_jardvegur", 8.5 );
		snaedis3phmap.put( "780_fludir_vatn", 8.5 );
		snaedis3phmap.put( "781_olkelduhals_vatn", 6.5 );
		snaedis3phmap.put( "781_olkelduhals_lifmassi", 6.5 );
		snaedis3phmap.put( "782_olkelduhals_lifmassi", 6.5 );
		snaedis3phmap.put( "783_olkelduhals_jardvlifm", 6.0 );
		snaedis3phmap.put( "783_olkelduhals_vatn", 6.0 );
		snaedis3phmap.put( "808_hrafntinnusker_jardvegur", 7.0 );
		snaedis3phmap.put( "808_hrafntinnusker_vatn", 7.0 );
		snaedis3phmap.put( "808_hrafntinnusker_lifmassi", 7.0 );
		
		
		
		snaedis4colormap = new HashMap<String,String>();
		snaedis4colormap.put( "809_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "809_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "809_hrafntinnusker_lifmassi", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "810_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "810_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "810_hrafntinnusker_lifmassi", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "811_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "811_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "811_hrafntinnusker_lifmassi", "0.0\t0.0\t1.0\n" );
		snaedis4colormap.put( "812_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		
		snaedis4heatmap = new HashMap<String,Double>();
		snaedis4heatmap.put( "809_hrafntinnusker_jardvegur", 63.5 );
		snaedis4heatmap.put( "809_hrafntinnusker_vatn", 63.5 );
		snaedis4heatmap.put( "809_hrafntinnusker_lifmassi", 63.5 );
		snaedis4heatmap.put( "810_hrafntinnusker_jardvegur", 68.0 );
		snaedis4heatmap.put( "810_hrafntinnusker_vatn", 68.0 );
		snaedis4heatmap.put( "810_hrafntinnusker_lifmassi", 68.0 );
		snaedis4heatmap.put( "811_hrafntinnusker_jardvegur", 71.1 );
		snaedis4heatmap.put( "811_hrafntinnusker_vatn", 71.1 );
		snaedis4heatmap.put( "811_hrafntinnusker_lifmassi", 71.1 );
		snaedis4heatmap.put( "812_hrafntinnusker_jardvegur", 68.3 );
		
		snaedis4phmap = new HashMap<String,Double>();
		snaedis4phmap.put( "809_hrafntinnusker_jardvegur", 6.0 );
		snaedis4phmap.put( "809_hrafntinnusker_vatn", 6.0 );
		snaedis4phmap.put( "809_hrafntinnusker_lifmassi", 6.0 );
		snaedis4phmap.put( "810_hrafntinnusker_jardvegur", 6.0 );
		snaedis4phmap.put( "810_hrafntinnusker_vatn", 6.0 );
		snaedis4phmap.put( "810_hrafntinnusker_lifmassi", 6.0 );
		snaedis4phmap.put( "811_hrafntinnusker_jardvegur", 7.0 );
		snaedis4phmap.put( "811_hrafntinnusker_vatn", 7.0 );
		snaedis4phmap.put( "811_hrafntinnusker_lifmassi", 7.0 );
		snaedis4phmap.put( "812_hrafntinnusker_jardvegur", 6.0 );
		
		
		
		snaedis5colormap = new HashMap<String,String>();
		snaedis5colormap.put( "812_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "813_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "813_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "814_hrafntinnusker_jardvegur", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "814_hrafntinnusker_vatn", "0.0\t0.0\t1.0\n" );
		snaedis5colormap.put( "815_reykjadalir_jardvegur", "0.0\t1.0\t0.0\n" );
		snaedis5colormap.put( "815_reykjadalir_vatn", "0.0\t1.0\t0.0\n" );
		snaedis5colormap.put( "815_reykjadalir_lifmassi", "0.0\t1.0\t0.0\n" );
		snaedis5colormap.put( "816_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis5colormap.put( "816_vondugil_vatn", "1.0\t0.0\t0.0\n" );
	
		snaedis5heatmap = new HashMap<String,Double>();
		snaedis5heatmap.put( "812_hrafntinnusker_vatn", 68.3 );
		snaedis5heatmap.put( "813_hrafntinnusker_jardvegur", 71.5 );
		snaedis5heatmap.put( "813_hrafntinnusker_vatn", 71.5 );
		snaedis5heatmap.put( "814_hrafntinnusker_jardvegur", 71.7 );
		snaedis5heatmap.put( "814_hrafntinnusker_vatn", 71.7 );
		snaedis5heatmap.put( "815_reykjadalir_jardvegur", 54.0 );
		snaedis5heatmap.put( "815_reykjadalir_vatn", 54.0 );
		snaedis5heatmap.put( "815_reykjadalir_lifmassi", 54.0 );
		snaedis5heatmap.put( "816_vondugil_jardvegur", 78.0 );
		snaedis5heatmap.put( "816_vondugil_vatn", 78.0 );
	
		snaedis5phmap = new HashMap<String,Double>();
		snaedis5phmap.put( "812_hrafntinnusker_vatn", 6.0 );
		snaedis5phmap.put( "813_hrafntinnusker_jardvegur", 5.75 );
		snaedis5phmap.put( "813_hrafntinnusker_vatn", 5.75 );
		snaedis5phmap.put( "814_hrafntinnusker_jardvegur", 5.0 );
		snaedis5phmap.put( "814_hrafntinnusker_vatn", 5.0 );
		snaedis5phmap.put( "815_reykjadalir_jardvegur", 6.0 );
		snaedis5phmap.put( "815_reykjadalir_vatn", 6.0 );
		snaedis5phmap.put( "815_reykjadalir_lifmassi", 6.0 );
		snaedis5phmap.put( "816_vondugil_jardvegur", 9.0 );
		snaedis5phmap.put( "816_vondugil_vatn", 9.0 );
		
		
				
		snaedis6colormap = new HashMap<String,String>();
		snaedis6colormap.put( "817_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "817_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "818_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "818_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "819_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "819_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "820_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "820_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "821_vondugil_jardvegur", "1.0\t0.0\t0.0\n" );
		snaedis6colormap.put( "821_vondugil_vatn", "1.0\t0.0\t0.0\n" );
		
		snaedis6heatmap = new HashMap<String,Double>();
		snaedis6heatmap.put( "817_vondugil_jardvegur", 75.7 );
		snaedis6heatmap.put( "817_vondugil_vatn", 75.7 );
		snaedis6heatmap.put( "818_vondugil_jardvegur", 78.5 );
		snaedis6heatmap.put( "818_vondugil_vatn", 78.5 );
		snaedis6heatmap.put( "819_vondugil_jardvegur", 80.5 );
		snaedis6heatmap.put( "819_vondugil_vatn", 80.5 );
		snaedis6heatmap.put( "820_vondugil_jardvegur", 71.1 );
		snaedis6heatmap.put( "820_vondugil_vatn", 71.1 );
		snaedis6heatmap.put( "821_vondugil_jardvegur", 79.0 );
		snaedis6heatmap.put( "821_vondugil_vatn", 79.0 );
	
		snaedis6phmap = new HashMap<String,Double>();
		snaedis6phmap.put( "817_vondugil_jardvegur", 9.0 );
		snaedis6phmap.put( "817_vondugil_vatn", 9.0 );
		snaedis6phmap.put( "818_vondugil_jardvegur", 8.5 );
		snaedis6phmap.put( "818_vondugil_vatn", 8.5 );
		snaedis6phmap.put( "819_vondugil_jardvegur", 8.5 );
		snaedis6phmap.put( "819_vondugil_vatn", 8.5 );
		snaedis6phmap.put( "820_vondugil_jardvegur", 7.5 );
		snaedis6phmap.put( "820_vondugil_vatn", 7.5 );
		snaedis6phmap.put( "821_vondugil_jardvegur", 6.0 );
		snaedis6phmap.put( "821_vondugil_vatn", 6.0 );
		
		
		
		snaedis7colormap = new HashMap<String,String>();
		snaedis7colormap.put( "846_hurdarbak_jardvegur", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "846_hurdarbak_vatn", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "846_hurdarbak_lifmassi", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "847_hurdarbak_jardvegur", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "847_hurdarbak_vatn", "1.0\t0.0\t0.5\n" );
		snaedis7colormap.put( "848_kleppjarnsreykir_jardvegur", "1.0\t0.5\t0.0\n" );
		snaedis7colormap.put( "848_kleppjarnsreykir_vatn", "1.0\t0.5\t0.0\n" );
		snaedis7colormap.put( "848_kleppjarnsreykir_lifmassi", "1.0\t0.5\t0.0\n" );
		snaedis7colormap.put( "849_kleppjarnsreykir_jardvegur", "1.0\t0.5\t0.0\n" );
		snaedis7colormap.put( "849_kleppjarnsreykir_vatn", "1.0\t0.5\t0.0\n" );
		
		snaedis7heatmap = new HashMap<String,Double>();
		snaedis7heatmap.put( "846_hurdarbak_jardvegur", 80.5 );
		snaedis7heatmap.put( "846_hurdarbak_vatn", 80.5 );
		snaedis7heatmap.put( "846_hurdarbak_lifmassi", 80.5 );
		snaedis7heatmap.put( "847_hurdarbak_jardvegur", 71.5 );
		snaedis7heatmap.put( "847_hurdarbak_vatn", 71.5 );
		snaedis7heatmap.put( "848_kleppjarnsreykir_jardvegur", 70.7 );
		snaedis7heatmap.put( "848_kleppjarnsreykir_vatn", 70.7 );
		snaedis7heatmap.put( "848_kleppjarnsreykir_lifmassi", 70.7 );
		snaedis7heatmap.put( "849_kleppjarnsreykir_jardvegur", 76.8 );
		snaedis7heatmap.put( "849_kleppjarnsreykir_vatn", 76.8 );
		
		snaedis7phmap = new HashMap<String,Double>();
		snaedis7phmap.put( "846_hurdarbak_jardvegur", 8.0 );
		snaedis7phmap.put( "846_hurdarbak_vatn", 8.0 );
		snaedis7phmap.put( "846_hurdarbak_lifmassi", 8.0 );
		snaedis7phmap.put( "847_hurdarbak_jardvegur", 8.0 );
		snaedis7phmap.put( "847_hurdarbak_vatn", 8.0 );
		snaedis7phmap.put( "848_kleppjarnsreykir_jardvegur", 8.0 );
		snaedis7phmap.put( "848_kleppjarnsreykir_vatn", 8.0 );
		snaedis7phmap.put( "848_kleppjarnsreykir_lifmassi", 8.0 );
		snaedis7phmap.put( "849_kleppjarnsreykir_jardvegur", 7.5 );
		snaedis7phmap.put( "849_kleppjarnsreykir_vatn", 7.5 );
		
		
		
		snaedis8colormap = new HashMap<String,String>();
		snaedis8colormap.put( "849_kleppjarnsreykir_lifmassi", "1.0\t0.5\t0.0\n" );
		snaedis8colormap.put( "850_kleppjarnsreykir_jardvegur", "1.0\t0.5\t0.0\n" );
		snaedis8colormap.put( "850_kleppjarnsreykir_vatn", "1.0\t0.5\t0.0\n" );
		snaedis8colormap.put( "850_kleppjarnsreykir_lifmassi", "1.0\t0.5\t0.0\n" );
		snaedis8colormap.put( "851_deildartunguhver_jardvegur", "0.5\t0.0\t0.5\n" );
		snaedis8colormap.put( "851_deildartunguhver_vatn", "0.5\t0.0\t0.5\n" );
		snaedis8colormap.put( "852_deildartunguhver_jardvegur", "0.5\t0.0\t0.5\n" );
		snaedis8colormap.put( "852_deildartunguhver_vatn", "0.5\t0.0\t0.5\n" );
		
		snaedis8heatmap = new HashMap<String,Double>();
		snaedis8heatmap.put( "849_kleppjarnsreykir_lifmassi", 76.8 );
		snaedis8heatmap.put( "850_kleppjarnsreykir_jardvegur", 65.8 );
		snaedis8heatmap.put( "850_kleppjarnsreykir_vatn", 65.8 );
		snaedis8heatmap.put( "850_kleppjarnsreykir_lifmassi", 65.8 );
		snaedis8heatmap.put( "851_deildartunguhver_jardvegur", 83.3 );
		snaedis8heatmap.put( "851_deildartunguhver_vatn", 83.3 );
		snaedis8heatmap.put( "852_deildartunguhver_jardvegur", 86.1 );
		snaedis8heatmap.put( "852_deildartunguhver_vatn", 86.1 );
		
		snaedis8phmap = new HashMap<String,Double>();
		snaedis8phmap.put( "849_kleppjarnsreykir_lifmassi", 7.5 );
		snaedis8phmap.put( "850_kleppjarnsreykir_jardvegur", 8.5 );
		snaedis8phmap.put( "850_kleppjarnsreykir_vatn", 8.5 );
		snaedis8phmap.put( "850_kleppjarnsreykir_lifmassi", 8.5 );
		snaedis8phmap.put( "851_deildartunguhver_jardvegur", 8.5 );
		snaedis8phmap.put( "851_deildartunguhver_vatn", 8.5 );
		snaedis8phmap.put( "852_deildartunguhver_jardvegur", 8.5 );
		snaedis8phmap.put( "852_deildartunguhver_vatn", 8.5 );
		
		snaedismap = new HashMap<String,String>();
		snaediscolormap = new HashMap<String,String>();
		snaedisphmap = new HashMap<String,Double>();
		snaedisheatmap = new HashMap<String,Double>();
		
		Map[] maps = {snaedis1map,snaedis2map,snaedis3map,snaedis4map,snaedis5map,snaedis6map,snaedis7map,snaedis8map};
		Map[] heatmaps = {snaedis1heatmap,snaedis2heatmap,snaedis3heatmap,snaedis4heatmap,snaedis5heatmap,snaedis6heatmap,snaedis7heatmap,snaedis8heatmap};
		Map[] phmaps = {snaedis1phmap,snaedis2phmap,snaedis3phmap,snaedis4phmap,snaedis5phmap,snaedis6phmap,snaedis7phmap,snaedis8phmap};
		Map[] colormaps = {snaedis1colormap,snaedis2colormap,snaedis3colormap,snaedis4colormap,snaedis5colormap,snaedis6colormap,snaedis7colormap,snaedis8colormap};
		
		for( Map map : maps ) {
			for( Object key : map.keySet() ) {
				Object val = map.get(key);
				snaedismap.put( (String)key, (String)val );
			}
		}
		
		for( Map map : heatmaps ) {
			for( Object key : map.keySet() ) {
				Object val = map.get(key);
				snaedisheatmap.put( (String)key, (Double)val );
			}
		}
		
		for( Map map : phmaps ) {
			for( Object key : map.keySet() ) {
				Object val = map.get(key);
				snaedisphmap.put( (String)key, (Double)val );
			}
		}
		
		for( Map map : colormaps ) {
			for( Object key : map.keySet() ) {
				Object val = map.get(key);
				snaediscolormap.put( (String)key, (String)val );
			}
		}
	}
	
	public void initMapFiles() {
		int i = 0;
		Map[] maps = {snaedis1map,snaedis2map,snaedis3map,snaedis4map,snaedis5map,snaedis6map,snaedis7map,snaedis8map};
		for( Map map : maps ) {
			i++;
			try {
				FileWriter fw = new FileWriter( "/home/sigmar/"+i+".map" );
				for( Object key : map.keySet() ) {
					String keystr = (String)key;
					String valstr = (String)map.get( key );
					fw.write( valstr + "\t" + keystr + "\n" );
				}
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public StringBuilder replaceTreeColors( StringBuilder treestr ) {
		initMaps();
		Map[] maps = {snaedis1heatmap,snaedis2heatmap,snaedis3heatmap,snaedis4heatmap,snaedis5heatmap,snaedis6heatmap,snaedis7heatmap,snaedis8heatmap};
		Map[] phmaps = {snaedis1phmap,snaedis2phmap,snaedis3phmap,snaedis4phmap,snaedis5phmap,snaedis6phmap,snaedis7phmap,snaedis8phmap};
		Map[] colormaps = {snaedis1colormap,snaedis2colormap,snaedis3colormap,snaedis4colormap,snaedis5colormap,snaedis6colormap,snaedis7colormap,snaedis8colormap};
		
		int countrep = 0;
		int k = 0;
		for( Map m : maps ) {
			for( Object keyobj : m.keySet() ) {
				String key = (String)keyobj;
				int i = treestr.indexOf( key );
				while( i != -1 ) {
					double dval = (double)m.get( key );
					//double tval = (dval-5.0)/4.0;
					double tval = (dval-50.0)/40.0;
					
					int red = (int)(tval*255.0);
					int green = 0;
					int blue = (int)((1.0-tval)*255.0);
					
					String rstr = Integer.toString(red, 16);
					String gstr = Integer.toString(green, 16);
					String bstr = Integer.toString(blue, 16);
					
					String allstr = (rstr.length() == 1 ? "0"+rstr : rstr) + (gstr.length() == 1 ? "0"+gstr : gstr) + (bstr.length() == 1 ? "0"+bstr : bstr);
					
					countrep++;
					treestr.replace(i-8, i-2, allstr);
					
					/*Map colormap = colormaps[k];
					String[] csplit = ((String)colormap.get( key )).split("\t");
					
					red = (int)(Double.parseDouble(csplit[0])*255.0);
					green = (int)(Double.parseDouble(csplit[1])*255.0);
					blue = (int)(Double.parseDouble(csplit[2])*255.0);
					
					rstr = Integer.toString(red, 16);
					gstr = Integer.toString(green, 16);
					bstr = Integer.toString(blue, 16);
					
					String cstr = (rstr.length() == 1 ? "0"+rstr : rstr) + (gstr.length() == 1 ? "0"+gstr : gstr) + (bstr.length() == 1 ? "0"+bstr : bstr);*/
					
					i = treestr.indexOf( key, i+1 );
				}
			}
			k++;
		}
		System.err.println( countrep );
		return treestr;
	}
	
	static class erm implements Comparable<erm> {
		public erm( String prim, int cnt ) {
			this.primer = prim;
			this.count = cnt;
		}
		
		String	primer;
		int		count;
		
		@Override
		public int compareTo(erm o) {
			return count - o.count;
		}
	}
	
	private boolean primermatch( String theprimer, String primer, int err ) {
		int count = 0;
		
		for( int i = 0; i < theprimer.length(); i++ ) {
			char c = theprimer.charAt(i);
			if( c != 'N' ) count += (c != primer.charAt(i)) ? 1 : 0; 
		}
		
		return count <= err;
	}
	
	public void extractSequences( File f, Map<String,String> tagmap, String theprimer, File out ) {
		Map<String,FileWriter>	filemap = new HashMap<String,FileWriter>();
		Map<String,Integer>	mstr = new HashMap<String,Integer>();
		try {
			FileReader fr = new FileReader( f );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String name = line;
					line = br.readLine();
					String tag = line.substring(0,10);
					String primer = line.substring(10, 10+theprimer.length());
					if( mstr.containsKey(primer) ) {
						mstr.put( primer, mstr.get(primer)+1 );
					} else mstr.put( primer, 1 );
					
					if( tagmap.containsKey( tag ) ) {
						if( primermatch( theprimer, primer, 1 ) ) {
							FileWriter ofw;
							if( !filemap.containsKey( tag ) ) {
								File of = new File( out, tagmap.get(tag)+".fasta" );
								ofw = new FileWriter( of );
								filemap.put( tag, ofw );
							} else {
								ofw = filemap.get( tag );
							}
							ofw.write( name+"\n" );
							int cnt = 0;
							line = line.substring( 10+theprimer.length() );
							while( line != null && !line.startsWith(">") ) {
								for( int i = 0; i < line.length(); i++ ) {
									ofw.write( line.charAt(i) );
									cnt++;
									if( cnt % 70 == 0 ) ofw.write('\n');
								}
								line = br.readLine();
							}
							if( cnt % 70 != 0 ) ofw.write('\n');
						}
					} //else line = br.readLine();
				} else line = br.readLine();
			}
			br.close();
			fr.close();
			
			for( String tag : filemap.keySet() ) {
				filemap.get(tag).close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<erm>	erml = new ArrayList<erm>();
		for( String primer : mstr.keySet() ) {
			erml.add( new erm( primer, mstr.get(primer) ) );
			//System.err.println( primer + "  " + mstr.get(primer) );
		}
		Collections.sort( erml );
		
		for( erm hey : erml ) {
			System.err.println( hey.primer + "  " + hey.count );
		}
	}
	
	public List<Sequences> join( Path dest, List<Sequences> lseqs, boolean simple, String mappingfile, boolean includeFileName ) {
		List<Sequences>	retlseq = new ArrayList<Sequences>();
		
		initMaps();
		Map[] maps = {snaedis1heatmap,snaedis2heatmap,snaedis3heatmap,snaedis4heatmap,snaedis5heatmap,snaedis6heatmap,snaedis7heatmap,snaedis8heatmap};
		Map[] phmaps = {snaedis1phmap,snaedis2phmap,snaedis3phmap,snaedis4phmap,snaedis5phmap,snaedis6phmap,snaedis7phmap,snaedis8phmap};
		Map[] colormaps = {snaedis1colormap,snaedis2colormap,snaedis3colormap,snaedis4colormap,snaedis5colormap,snaedis6colormap,snaedis7colormap,snaedis8colormap};
		
		try {
			Map<String,String>	ftagmap = new HashMap<String,String>();
			
			if( mappingfile != null ) {
				File ff = new File( mappingfile );
				FileReader	fr = new FileReader( ff );
				BufferedReader	br2 = new BufferedReader( fr );
				String nline = br2.readLine();
				while( nline != null ) {
					String[] split = nline.split("\t");
					
					ftagmap.put( split[0], split[1] );
					
					nline = br2.readLine();
				}
				br2.close();
				fr.close();
			}
			
			//FileWriter fw = new FileWriter( f );
			Writer fw = Files.newBufferedWriter(dest);
			String seqtype = "nucl";
			String joinname = dest.getFileName().toString();
			int nseq = 0;
			for( Sequences s : lseqs ) {					
				seqtype = s.getType();
				//if( joinname == null ) joinname = s.getName();
				//else joinname += "_"+s.getName();
				
				BufferedReader br = Files.newBufferedReader(s.getPath(), Charset.defaultCharset());
				String line = br.readLine();
				while( line != null ) {
					if( line.startsWith(">") ) {
						if( mappingfile != null ) {							
							fw.write( line+"\n" );
							line = br.readLine();
							if( ftagmap.containsKey(s.getName()) ) fw.write( ftagmap.get(s.getName())+line+"\n" );
							else fw.write( "simmi"+line+"\n" );
						} else if( simple ) {
							if( includeFileName ) line = line.replace( ">", ">"+s.getName().replace(".fna", "")+"_" );
							fw.write( line+"\n" );
						} else {
							int pe = line.indexOf('%');
							String idstr = null;
							if( pe != -1 ) {
								int pi = line.lastIndexOf('_', pe);
								int perc = Integer.parseInt( line.substring(pi+1, pe) );
								
								int red = (int)( (perc-95.0)*200.0/5.0+50.0 );
								int green = (int)( (perc-95.0)*200.0/5.0+50.0 );
								int blue = (int)( (perc-95.0)*200.0/5.0+50.0 );
								
								String rstr = Integer.toString(red, 16);
								String gstr = Integer.toString(green, 16);
								String bstr = Integer.toString(blue, 16);
								
								idstr = (rstr.length() == 1 ? "0"+rstr : rstr) + (gstr.length() == 1 ? "0"+gstr : gstr) + (bstr.length() == 1 ? "0"+bstr : bstr);
							}
							
							//fw.write( line.replace( ">", ">"+s.getName().replace(".fna", "")+"_" )+"\n" );
							int i = s.getName().indexOf("_F_Good");
							if( i == -1 ) i = s.getName().length();
							String sub = s.getName().substring(0,i);
							
							i = line.lastIndexOf('_');
							if( i != -1 ) i = line.lastIndexOf('_', i-1);
							if( i == -1 ) i = line.length();
							String cont = line.substring(1,i);
							
							String newline = colorAdd( line, maps, phmaps, colormaps, sub, cont, null, false );
							fw.write( newline + "\n" );
						}
						nseq++;
					} else fw.write( line+"\n" );
					line = br.readLine();
				}
				br.close();
			}
			fw.close();
			
			System.err.println( nseq );
			
			Sequences seqs = new Sequences( "", joinname, seqtype, dest, nseq );
			retlseq.add( seqs );
			//SerifyApplet.this.addSequences( joinname, seqtype, f.toURI().toString(), nseq );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return retlseq;
	}
	
	Map<String,String>	statcolorMap = new HashMap<String,String>();
	public String colorAdd( String line, Map[] maps, Map[] phmaps, Map[] colormaps, String sub, String cont, Random r, boolean specOnly ) {
		String ret = line;
		//System.err.println(sub + "    " + cont);
		int k = 0;
		for( Map m : maps ) {
			//System.err.println( m );
			boolean bsub = m.containsKey(sub);
			boolean bcont = m.containsKey(cont);
			if( bsub || bcont ) {
				sub = bsub ? sub : cont;
				double dval = (double)m.get( sub );
				double tval = (dval-50.0)/40.0;
				
				int red = (int)(tval*255.0);
				int green = 0;
				int blue = (int)((1.0-tval)*255.0);
				
				String rstr = Integer.toString(red, 16);
				String gstr = Integer.toString(green, 16);
				String bstr = Integer.toString(blue, 16);
				
				String allstr = (rstr.length() == 1 ? "0"+rstr : rstr) + (gstr.length() == 1 ? "0"+gstr : gstr) + (bstr.length() == 1 ? "0"+bstr : bstr);
				
				Map phmap = phmaps[k];
				double phval = (double)phmap.get( sub );
				double tphval = (phval-5.0)/4.0;
				
				green = (int)(tphval*255.0);
				blue = 0;
				red = (int)((1.0-tphval)*255.0);
				
				rstr = Integer.toString(red, 16);
				gstr = Integer.toString(green, 16);
				bstr = Integer.toString(blue, 16);
				
				String phstr = (rstr.length() == 1 ? "0"+rstr : rstr) + (gstr.length() == 1 ? "0"+gstr : gstr) + (bstr.length() == 1 ? "0"+bstr : bstr);
				
				Map colormap = colormaps[k];
				String[] csplit;
				if( colormap.containsKey( sub ) ) {
					csplit = ((String)colormap.get( sub )).split("\t");
				} else {
					String crand = r.nextDouble()+"\t"+r.nextDouble()+"\t"+r.nextDouble();
					csplit = crand.split("\t");
					colormap.put( sub, crand );
				}
				
				red = (int)(Double.parseDouble(csplit[0])*255.0);
				green = (int)(Double.parseDouble(csplit[1])*255.0);
				blue = (int)(Double.parseDouble(csplit[2])*255.0);
				
				rstr = Integer.toString(red, 16);
				gstr = Integer.toString(green, 16);
				bstr = Integer.toString(blue, 16);
				
				String cstr = (rstr.length() == 1 ? "0"+rstr : rstr) + (gstr.length() == 1 ? "0"+gstr : gstr) + (bstr.length() == 1 ? "0"+bstr : bstr);
				//System.err.println( cstr );
				
				//ret = line+"[#"+cstr+"]"; //"+sub+"[#"+cstr+"]";
				//ret = line+"[#FFFFFF]-----[#"+allstr+"]";//;-----[#"+phstr+"]"; //"+sub+"[#"+cstr+"]";
				if( specOnly ) ret = line+"[#"+allstr+"];"+cont; //"+sub+"[#"+cstr+"]";
				else ret = line+"[#"+cstr+"];"+cont;
				//ret = line+"[#FFFFFF]-----[#"+allstr+"]-----[#"+phstr+"]"; //"+sub+"[#"+cstr+"]";
				//ret = line+"[#"+allstr+"]-----[#"+phstr+"];"+sub+"[#"+cstr+"]";
				//check = true;
				//fw.write( line+"[#"+allstr+"]-----[#"+phstr+"];"+sub+"[#"+cstr+"]\n" );
				//fw.write( line+(idstr != null ? "[#"+idstr+"];" : "")+sub+"[#"+cstr+"]\n" );
				break;
			}
			k++;
		}
		if( k == maps.length ) {
			String[] csplit;
			if( statcolorMap.containsKey( sub ) ) {
				csplit = ((String)statcolorMap.get( sub )).split("\t");
			} else {
				String crand = r.nextDouble()+"\t"+r.nextDouble()+"\t"+r.nextDouble();
				csplit = crand.split("\t");
				statcolorMap.put( sub, crand );
			}
			//colormap.put( sub, crand );
		
			int red = (int)(Double.parseDouble(csplit[0])*255.0);
			int green = (int)(Double.parseDouble(csplit[1])*255.0);
			int blue = (int)(Double.parseDouble(csplit[2])*255.0);
			
			String rstr = Integer.toString(red, 16);
			String gstr = Integer.toString(green, 16);
			String bstr = Integer.toString(blue, 16);
			
			String cstr = (rstr.length() == 1 ? "0"+rstr : rstr) + (gstr.length() == 1 ? "0"+gstr : gstr) + (bstr.length() == 1 ? "0"+bstr : bstr);
			
			ret = line+"[#"+cstr+"];"+cont+"[#"+cstr+"]"; //"+sub+"[#"+cstr+"]";
		}
		//if( !check ) fw.write( line + "\n" );//line.replace( ">", ">"+s.getName().replace(".fna", "")+"_" )+"\n" );
		return ret;
	}
	
	public void corr() {
		double meantmp = 0.0;
		double meanph = 0.0;
		for( String snaedis : snaedisheatmap.keySet() ) {
			double temp = snaedisheatmap.get( snaedis );
			double ph = snaedisphmap.get( snaedis );
			
			meantmp += temp;
			meanph += ph;
			
			System.err.println( temp + "  " + ph );
		}
		meantmp /= snaedisheatmap.size();
		meanph /= snaedisphmap.size();
		
		System.err.println( meantmp + "  " + meanph );
		
		double vartmp = 0.0;
		double varph = 0.0;
		double covar = 0.0;
		for( String snaedis : snaedisheatmap.keySet() ) {
			double temp = snaedisheatmap.get( snaedis );
			double ph = snaedisphmap.get( snaedis );
			
			double tmpdev = (temp-meantmp);
			double phdev = (ph-meanph);
			
			vartmp += tmpdev*tmpdev;
			varph += phdev*phdev;
			covar += tmpdev*phdev;
		}
		
		double corr = covar/( Math.sqrt(varph)*Math.sqrt(vartmp) );
		System.err.println( corr );
	}
	
	public void writeFasta( List<Sequence> seqlist, Writer osw, Rectangle selectedRect ) throws IOException {
		for( Sequence seq : seqlist ) {
			int val = 0;
	   		int end = seq.length();
	   		 
	   		if( selectedRect != null && selectedRect.width > 0 ) {
	   			 val = Math.max( val, selectedRect.x-seq.getStart() );
	   			 end = Math.min( end, selectedRect.x+selectedRect.width-seq.getStart() );
	   		}
	   		 
	   		if( val <= end ) osw.write( ">" + seq.name + "\n" );
	   		while( val < end ) {
	   			 osw.write( seq.sb.substring(val, Math.min( end, val+70 )) + "\n" );
	   			 val += 70;
	   		}
		}
	}
	
	public int trimFasta( BufferedReader br, Writer bw, Object filterset, boolean inverted, boolean endswith ) throws IOException {
		return trimFasta(br, bw, filterset, inverted, endswith, false);
	}
	
	public int trimFasta( BufferedReader br, Writer bw, Object filterset, boolean inverted, boolean endswith, boolean rename ) throws IOException {
		int nseq = 0;
		
		Set<String> keyset;
		if( filterset instanceof Map ) {
			keyset = (Set<String>)((Map) filterset).keySet();
		} else keyset = (Set<String>)filterset;
		System.err.println(keyset.size());
		
		String line = br.readLine();
		String seqname = null;
		while( line != null ) {
			if( line.startsWith(">") ) {
				if( inverted ) {
					seqname = line;
					for( String f : keyset ) {
						if( (endswith && line.endsWith(f)) || (!endswith && line.contains(f)) ) {
							nseq++;
							seqname = null;
							break;
						}
					}
					if( seqname != null ) {
						bw.write( seqname+"\n" );
					}
				} else {
					//System.err.println( line );
					seqname = null;
					for( String f : keyset ) {
						//System.err.println( f );
						//HM2RNR208JBGXL
						//HM2RNR208JM87F
						/*if( f.contains("HM2RNR208JM87F") && line.contains("HM2RNR208JM87F") ) {
							System.err.println( "muu " + line );
							System.err.println( "muu " + f );
							System.err.println( line.contains(f) );
						}
						if( f.contains("HM2RNR208JBGXL") && line.contains("HM2RNR208JBGXL") ) {
							System.err.println( "muu " + line );
							System.err.println( "muu " + f );
							System.err.println( line.contains(f) );
						}*/
						
						/*int k = line.indexOf('[');
						int c = line.indexOf(']');
						int u = line.indexOf("contig", k+1);
						int m = line.indexOf('_', u+1);
						if( m == -1 || m > c ) m = c; 
						String bull = line.substring(k, m)+line.substring(c,line.length());*/
						
						if( (endswith && line.endsWith(f)) || (!endswith && line.contains(f)) ) {
							Object swap = (filterset instanceof Map) ? ((Map)filterset).get(f) : null;
							
							nseq++;
							if( swap != null ) bw.write( ">"+swap/*+"_"+f*/+"\n" );
							else bw.write( line+"\n" );
							seqname = line;
							break;
						}
					}
					if( rename && seqname == null ) bw.write( line+"\n" );
					if( seqname == null ) System.err.println( "not found " + line );
				}
			} else if( rename || seqname != null ) {
				bw.write( line+"\n" );
			}
			
			line = br.readLine();
		}
		br.close();
		//bw.close();
		System.err.println( nseq );
		
		return nseq;
	}
	
	public static void main(String[] args) {
		Serifier s = new Serifier();
		
		/*try {
			Map<String,String>	map = new HashMap<String,String>();
			FileReader 		fr = new FileReader("/home/sigmar/MAT/MAT4555_GenBank.gb.aa");
			BufferedReader	br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String trim = line.trim();
					int i = trim.indexOf('[');
					map.put(trim.substring(i), trim.substring(1));
				}
				line = br.readLine();
			}
			br.close();
			
			fr = new FileReader("/home/sigmar/MAT/MAT4553_GenBank.gb.aa");
			br = new BufferedReader( fr );
			line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String trim = line.trim();
					int i = trim.indexOf('[');
					map.put(trim.substring(i), trim.substring(1));
				}
				line = br.readLine();
			}
			br.close();
			
			fr = new FileReader("/home/sigmar/MAT/MAT4685_GenBank.gb.aa");
			br = new BufferedReader( fr );
			line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String trim = line.trim();
					int i = trim.indexOf('[');
					map.put(trim.substring(i), trim.substring(1));
				}
				line = br.readLine();
			}
			br.close();
			
			fr = new FileReader("/home/sigmar/MAT/MAT4696_GenBank.gb.aa");
			br = new BufferedReader( fr );
			line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String trim = line.trim();
					int i = trim.indexOf('[');
					map.put(trim.substring(i), trim.substring(1));
				}
				line = br.readLine();
			}
			br.close();
			
			fr = new FileReader("/home/sigmar/MAT/MAT4705_GenBank.gb.aa");
			br = new BufferedReader( fr );
			line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String trim = line.trim();
					int i = trim.indexOf('[');
					map.put(trim.substring(i), trim.substring(1));
				}
				line = br.readLine();
			}
			br.close();
			
			fr = new FileReader("/home/sigmar/MAT/MAT4717_GenBank.gb.aa");
			br = new BufferedReader( fr );
			line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String trim = line.trim();
					int i = trim.indexOf('[');
					map.put(trim.substring(i), trim.substring(1));
				}
				line = br.readLine();
			}
			br.close();
			
			fr = new FileReader("/home/sigmar/MAT/MAT4784_GenBank.gb.aa");
			br = new BufferedReader( fr );
			line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String trim = line.trim();
					int i = trim.indexOf('[');
					map.put(trim.substring(i), trim.substring(1));
				}
				line = br.readLine();
			}
			br.close();
			
			fr = new FileReader("/home/sigmar/MAT/MAT493_GenBank.gbk.aa");
			br = new BufferedReader( fr );
			line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String trim = line.trim();
					int i = trim.indexOf('[');
					map.put(trim.substring(i), trim.substring(1));
				}
				line = br.readLine();
			}
			br.close();
			
			fr = new FileReader("/home/sigmar/MAT/MAT4716_GenBank_2.gbk.aa");
			br = new BufferedReader( fr );
			line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) {
					String trim = line.trim();
					int i = trim.indexOf('[');
					map.put(trim.substring(i), trim.substring(1));
				}
				line = br.readLine();
			}
			br.close();
			
			fr = new FileReader("/home/sigmar/MAT/allthermus_aligned.fsa");
			br = new BufferedReader( fr );
			FileWriter fw = new FileWriter("/home/sigmar/MAT/allthermus_new_aligned.fsa");
			s.trimFasta( br, fw, map, false, false, true );
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		try {
			s.parse( args );
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
