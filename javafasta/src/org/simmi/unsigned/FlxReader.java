package org.simmi.unsigned;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.simmi.shared.Sequence;
import org.simmi.shared.Serifier;

public class FlxReader {
	
	public static String join( String[] jj ) {
		String ret = null;
		for( String j : jj ) {
			if( ret == null ) ret = j;
			else ret += "\t"+j;
		}
		return ret;
	}
	
	public static String type = "islandicus3838_30";
	public static void main(String[] args) {
		try {
			Map<String,Sequence>	mseq = new HashMap<String,Sequence>();
			FileReader fr = new FileReader("/Users/sigmar/thermus_close/"+type+"/454ScaffoldContigs.fna");
			BufferedReader	br = new BufferedReader( fr );
			String line = br.readLine();
			Sequence seq = null;
			while( line != null ) {
				if( line.startsWith(">") ) {
					String name = line.substring(1);
					name = name.substring(0,name.indexOf(' '));
					seq = new Sequence( name, mseq );
				} else {
					seq.append(line);
				}
				line = br.readLine();
			}
			
			fr = new FileReader("/Users/sigmar/thermus_close/"+type+"/454AllContigs.fna");
			br = new BufferedReader( fr );
			line = br.readLine();
			seq = null;
			while( line != null ) {
				if( line.startsWith(">") ) {
					String name = line.substring(1);
					name = name.substring(0,name.indexOf(' '));
					seq = new Sequence( name, mseq );
				} else {
					seq.append(line);
				}
				line = br.readLine();
			}
			
			Map<String,Map<String,String>>	mm = new HashMap<String,Map<String,String>>();
			
			fr = new FileReader("/Users/sigmar/thermus_close/"+type+"/454ContigGraph.txt");
			br = new BufferedReader( fr );
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("[\t ]+");
				if( split[0].equals("C") ) {
					String one = split[1]+"_"+split[2];
					String two = split[3]+"_"+split[4];
					
					Map<String,String> l;
					if( mm.containsKey(one) ) {
						l = mm.get(one);
					} else {
						l = new HashMap<String,String>();
						mm.put(one, l);
					}
					l.put( two, split[5] );
					
					if( mm.containsKey(two) ) {
						l = mm.get(two);
					} else {
						l = new HashMap<String,String>();
						mm.put(two, l);
					}
					l.put( one, split[5] );
				}
				line = br.readLine();
			}
			br.close();
			
			Set<String>	touch = new HashSet<String>();
			Serifier	serifier = new Serifier();
			
			fr = new FileReader("/Users/sigmar/thermus_close/"+type+"/454Scaffolds.txt");
			br = new BufferedReader( fr );
			
			String[] lastW = null;
			String[] lastN = null;
			
			line = br.readLine();
			while( line != null ) {
				String[] split = line.split("[\t ]+");
				if( split[4].equals("W") ) {
					if( lastW != null ) {
						System.out.println( join(lastW) );
						seq = mseq.get(lastW[5]);
						touch.add( lastW[5] );
						seq.group = lastW[0];
						if( seq != null ) serifier.addSequence( seq );
					}
					if( lastN != null ) {
						int i = lastW[5].indexOf('0');
						while( lastW[5].charAt(++i) == '0' ) ;
						String c1 = lastW[5].substring(i);
						
						i = split[5].indexOf('0');
						while( split[5].charAt(++i) == '0' ) ;
						String c2 = split[5].substring(i);
						
						String nstuff = null;
						/*if( mm.containsKey(c1+"_5'") ) {
							Set<String> mog = mm.get(c1+"_5'");
							for( String m : mog ) {
								int li = m.lastIndexOf('_');
								String c3 = m.substring(0,li);
								if( m.substring( li ).equals("_5'") ) {
									Set<String> ss = mm.get(c3+"_3'");
									if( ss != null && ss.contains(c2+"_3'") ) {
										nstuff = m;
										break;
									}
								} else {
									Set<String> ss = mm.get(c3+"_5'");
									if( ss != null && ss.contains(c2+"_3'") ) {
										nstuff = m;
										break;
									}
								}
							}
						}*/
						if( mm.containsKey(c1+"_3'") ) {
							Map<String,String> mog = mm.get(c1+"_3'");
							for( String m : mog.keySet() ) {
								String q1 = mog.get(m);
								
								int li = m.lastIndexOf('_');
								String c3 = m.substring(0,li);
								if( m.substring( li ).equals("_5'") ) {
									Map<String,String> ss = mm.get(c3+"_3'");
									if( ss != null && ss.containsKey(c2+"_5'") ) {
										String q2 = ss.get(c2+"_5'");
										
										if( nstuff == null ) nstuff = "("+q1+")"+m+"("+q2+")";
										else nstuff += " ("+q1+")"+m+"("+q2+")";
										//break;
									}
								} else {
									Map<String,String> ss = mm.get(c3+"_5'");
									if( ss != null && ss.containsKey(c2+"_5'") ) {
										String q2 = ss.get(c2+"_5'");
										
										if( nstuff == null ) nstuff = "("+q1+")"+m+"("+q2+")";
										else nstuff += " ("+q1+")"+m+"("+q2+")";
										//break;
									}
								}
							}
						}
						
						if( nstuff != null ) {
							System.out.println( nstuff );
							String[] sp = nstuff.split(" ");
							String nsplit = sp[sp.length-1];
							int f = nsplit.indexOf(')');
							int n = nsplit.indexOf('_',f+1);
							String sub = nsplit.substring(f+1, n);
							String ctgname = sub.length() == 1 ? "contig0000"+sub : sub.length() == 2 ? "contig000"+sub : "contig00"+sub;
							seq = mseq.get(ctgname);
							touch.add( ctgname );
							if( seq != null ) {
								boolean rev = nsplit.charAt(n+1) == '3' ? true : false;
								Sequence nseq = new Sequence( seq, rev );
								nseq.setName( ctgname+"_"+(rev?"rev":"frw"));
								nseq.group = lastN[0];
								serifier.addSequence( nseq );
							}
						} else {
							System.out.println( join(lastN) );
							//seq = mseq.get(lastW[1]);
							//if( seq != null ) serifier.addSequence( seq );
						}
					}
					lastN = null;
					lastW = split;
				} else {
					lastN = split;
				}
				line = br.readLine();
			}
			br.close();
			
			for( String seqname : mseq.keySet() ) {
				if( !touch.contains(seqname) ) {
					seq = mseq.get( seqname );
					serifier.addSequence( seq );
				}
			}
			JavaFasta	jf = new JavaFasta(serifier);
			JFrame		frame = new JFrame();
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.setSize(800, 600);
			jf.initGui(frame);
			jf.updateView();
			frame.setVisible( true );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
