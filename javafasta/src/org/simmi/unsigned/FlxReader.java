package org.simmi.unsigned;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	public static String tengioff( Map<String,String> mog, int recurcount ) {
		String nstuff = "";
		if( mog != null && recurcount < 3 ) {
			for( String m : mog.keySet() ) {
				if( !nstuff.contains(m) ) {
					String q1 = mog.get(m);
					
					int li = m.lastIndexOf('_');
					String c3 = m.substring(0,li);
					if( m.substring( li ).equals("_5'") ) {
						Map<String,String> ss = mm.get(c3+"_3'");
						if( ss != null ) {
							String st = tengioff( ss, recurcount+1 );
							String[] split = st.split( "[ ]+" );
							for( String s : split ) {
								if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+s;
								else nstuff += "  ("+q1+")"+m+s;
							}
						}
					} else {
						Map<String,String> ss = mm.get(c3+"_5'");
						if( ss != null ) {
							String st = tengioff( ss, recurcount+1 );
							String[] split = st.split( "[ ]+" );
							for( String s : split ) {
								if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+s;
								else nstuff += "  ("+q1+")"+m+s;
							}
							/*if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m;
							else nstuff += " ("+q1+")"+m;
							if( st.length() > 0 ) nstuff += "(  "+st+"  )";*/
						}
					}
				}
			}
		}
		return nstuff;
	}
	
	public static String tengi( Map<String,String> mog, String c2, int recurcount ) {
		String nstuff = "";
		if( recurcount < 3 ) {
			for( String m : mog.keySet() ) {
				//if( !nstuff.contains(m) ) {
					String q1 = mog.get(m);
					
					int li = m.lastIndexOf('_');
					String c3 = m.substring(0,li);
					if( m.substring( li ).equals("_5'") ) {
						Map<String,String> ss = mm.get(c3+"_3'");
						if( ss != null ) {
							if( ss.containsKey(c2+"_5'") ) {
								String q2 = ss.get(c2+"_5'");
								
								if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+"("+q2+")";
								else nstuff += " ("+q1+")"+m+"("+q2+")";
								//break;
							} else {
								String st = tengi( ss, c2, recurcount+1 );
								if( st.length() > 0 ) {
									String[] split = st.split( "[ ]+" );
									for( String s : split ) {
										//String[] ok = s.split("\\)");
										boolean repeat = false;
										/*for( String o : ok ) {
											int k = o.indexOf("'(");
											if( k != -1 && nstuff.contains(o.substring(0,k+1)) ) {
												repeat = true;
												break;
											}
										}*/
										
										if( !s.contains(m) ) {
											if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+s;
											else nstuff += "  ("+q1+")"+m+s;
										}
									}
									
									//String q2 = ss.get(c2+"_5'");
									/*if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m;
									else nstuff += " ("+q1+")"+m;
									nstuff += st;*/
								}
							}
						}
					} else {
						Map<String,String> ss = mm.get(c3+"_5'");
						if( ss != null ) {
							if( ss.containsKey(c2+"_5'") ) {
								String q2 = ss.get(c2+"_5'");
								
								if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+"("+q2+")";
								else {
									nstuff += " ("+q1+")"+m+"("+q2+")";
								}
								//break;
							} else {
								String st = tengi( ss, c2, recurcount+1 );
								if( st.length() > 0 ) {
									String[] split = st.split( "[ ]+" );
									for( String s : split ) {
										//String[] ok = s.split("\\)");
										boolean repeat = false;
										/*for( String o : ok ) {
											int k = o.indexOf("'(");
											if( k != -1 && nstuff.contains(o.substring(0,k+1)) ) {
												repeat = true;
												break;
											}
										}*/
										
										if( !s.contains(m) ) {
											if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+s;
											else {
												nstuff += "  ("+q1+")"+m+s;
											}
										}
									}
									
									/*if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m;
									else nstuff += " ("+q1+")"+m;
									nstuff += st;*/
								}
							}
						}
					}
				//}
			}
		}
		return nstuff;
	}
	
	/*
	 * if( firstofnew != null ) {
						int i = lastW[5].indexOf('0');
						while( lastW[5].charAt(++i) == '0' ) ;
						String c1 = lastW[5].substring(i);
						
						i = firstofnew[5].indexOf('0');
						while( firstofnew[5].charAt(++i) == '0' ) ;
						String c2 = firstofnew[5].substring(i);
						
						String nstuff = null;
						if( mm.containsKey(c1+"_3'") ) {
							Map<String,String> mog = mm.get(c1+"_3'");
							nstuff = tengi( mog, c2, 0 );
							if( mog.containsKey(c2+"_5'") ) {
								String q = mog.get(c2+"_5'");
								if( nstuff.length() == 0 ) nstuff = "directconnect("+q+")";
								else nstuff += " directconnect("+q+")";
							}
						}
						
						if( nstuff != null && nstuff.length() > 0 ) {
							System.out.println( nstuff );
							String[] sp = nstuff.split(" ");
							String nsplit = sp[sp.length-1];
							int f = nsplit.indexOf(')');
							int n = nsplit.indexOf('_',f+1);
							if( n != -1 ) {
								String sub = nsplit.substring(f+1, n);
								String ctgname = sub.length() == 1 ? "contig0000"+sub : sub.length() == 2 ? "contig000"+sub : "contig00"+sub;
								seq = mseq.get(ctgname);
								touch.add( ctgname );
								if( seq != null ) {
									boolean rev = nsplit.charAt(n+1) == '3' ? true : false;
									Sequence nseq = new Sequence( seq, rev );
									nseq.setName( ctgname+"_"+(rev?"rev":"frw"));
									//nseq.group = lastN[0];
									serifier.addSequence( nseq );
								}
							}
						}
					}
					//System.out.println( join(split) );
	 */
	
	public static String contigName( String sub ) {
		return sub.length() == 1 ? "contig0000"+sub : sub.length() == 2 ? "contig000"+sub : sub.length() == 3 ? "contig00"+sub : "contig0"+sub;
	}
	
	public static void nstuffOut( String[] lastN, String[] lastW, String[] split, Sequence cseq ) {
		int i = lastW[5].indexOf('0');
		while( lastW[5].charAt(++i) == '0' ) ;
		String c1 = lastW[5].substring(i);
		
		i = lastW[0].indexOf('0');
		String sctg = "sctg_"+lastW[0].substring(i+1)+(next >= 10 ? "_00" : "_000")+next++;
		System.out.println( "   outputing " + sctg );
		Sequence sctgseq = mseq.get(sctg);
		if( sctgseq != null ) {
			cseq.append( sctgseq.sb );
		} else {
			System.out.println( "   empty " );
		}
		
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
		}
		if( mm.containsKey(c1+"_3'") ) {
			Map<String,String> mog = mm.get(c1+"_3'");
			nstuff = tengi( mog, c2, 0 );
			if( mog.containsKey(c2+"_5'") ) {
				String q = mog.get(c2+"_5'");
				if( nstuff.length() == 0 ) nstuff = "directconnect("+q+")";
				else nstuff += " directconnect("+q+")";
			}
		}
		
		/*int i = lastW[5].indexOf('0');
		while( lastW[5].charAt(++i) == '0' ) ;
		String c1 = lastW[5].substring(i);
		
		i = lastW[0].indexOf('0');
		String sctg = "sctg_"+lastW[0].substring(i+1)+(next >= 10 ? "_00" : "_000")+next++;
		Sequence sctgseq = mseq.get(sctg);
		cseq.append( sctgseq.sb );
		
		i = firstofnew[5].indexOf('0');
		while( firstofnew[5].charAt(++i) == '0' ) ;
		String c2 = firstofnew[5].substring(i);
		
		String nstuff = null;*/
		
		if( mm.containsKey(c1+"_3'") ) {
			Map<String,String> mog = mm.get(c1+"_3'");
			nstuff = tengi( mog, c2, 0 );
			if( mog.containsKey(c2+"_5'") ) {
				String q = mog.get(c2+"_5'");
				if( nstuff.length() == 0 ) nstuff = "directconnect("+q+")";
				else nstuff += " directconnect("+q+")";
			}
		}
		
		if( nstuff != null && nstuff.length() > 0 ) {
			String[] nssplit = nstuff.split("[ ]+");
			int m = 0;
			String store = "";
			for( String ns : nssplit ) {
				int maxmin = 1000;
				int l = ns.indexOf('(');
				while( l != -1 ) {
					int k = ns.indexOf(')',l+1);
					
					String nstr = ns.substring(l+1,k);
					int n = Integer.parseInt( nstr );
					maxmin = Math.min(maxmin, n);
					
					l = ns.indexOf('(',k+1);
				}
				
				if( maxmin > m || (maxmin == m && ns.length() < store.length()) ) {
					store = ns;
					m = maxmin;
				}
			}
			String oldnstuff = nstuff;
			nstuff = store;
			
			if( oldnstuff.equals(nstuff) ) System.out.println( nstuff );
			else System.out.println( nstuff + "    old    " + oldnstuff );
			
			String[] sp = nstuff.split("[ ]+");
			String nsplit = sp[sp.length-1];
			int f = nsplit.indexOf(')');
			int n = nsplit.indexOf('_',f+1);
			while( n != -1 ) {
				String sub = nsplit.substring(f+1, n);
				String ctgname = contigName( sub );
				Sequence seq = mseq.get(ctgname);
				touch.add( ctgname );
				if( seq != null ) {
					boolean rev = nsplit.charAt(n+1) == '3' ? true : false;
					Sequence nseq = new Sequence( seq, rev );
					nseq.setName( ctgname+"_"+(rev?"rev":"frw"));
					nseq.group = lastN != null ? lastN[0] : null;
					serifier.addSequence( nseq );
					
					cseq.append( nseq.sb );
				} else {
					System.err.println("errrrror");
				}
				
				f = nsplit.indexOf(')',n+1);
				n = nsplit.indexOf('_',f+1);
			}
		} else if( lastN != null ) {
			cseq.append("NNN");
			i = lastW[5].indexOf('0');
			while( lastW[5].charAt(++i) == '0' ) ;
			String c0 = lastW[5].substring(i);
			Map<String,String> mog = mm.get(c0+"_3'");
			nstuff = tengioff( mog, 0 );
			System.out.println( "<---" + nstuff );
			
			System.out.println( join(lastN) );
			
			if( split != null ) {
				i = split[5].indexOf('0');
				while( split[5].charAt(++i) == '0' ) ;
				c0 = split[5].substring(i);
				mog = mm.get(c0+"_5'");
				nstuff = tengioff( mog, 0 );
				System.out.println( "--->" + nstuff );
			}
			//seq = mseq.get(lastW[1]);
			//if( seq != null ) serifier.addSequence( seq );
		}
		
		/*if( nstuff != null && nstuff.length() > 0 ) {
			String[] nssplit = nstuff.split("[ ]+");
			int m = 0;
			String store = "";
			for( String ns : nssplit ) {
				int maxmin = 1000;
				int l = ns.indexOf('(');
				while( l != -1 ) {
					int k = ns.indexOf(')',l+1);
					
					String nstr = ns.substring(l+1,k);
					int n = Integer.parseInt( nstr );
					maxmin = Math.min(maxmin, n);
					
					l = ns.indexOf('(',k+1);
				}
				
				if( maxmin > m || (maxmin == m && ns.length() < store.length()) ) {
					store = ns;
					m = maxmin;
				}
			}
			String oldnstuff = nstuff;
			nstuff = store;
			
			if( oldnstuff.equals(nstuff) ) System.out.println( nstuff );
			else System.out.println( nstuff + "    old    " + oldnstuff );
			
			String[] sp = nstuff.split("[ ]+");
			String nsplit = sp[sp.length-1];
			int f = nsplit.indexOf(')');
			int n = nsplit.indexOf('_',f+1);
			while( n != -1 ) {
				String sub = nsplit.substring(f+1, n);
				String ctgname = contigName( sub );
				Sequence seq = mseq.get(ctgname);
				touch.add( ctgname );
				if( seq != null ) {
					boolean rev = nsplit.charAt(n+1) == '3' ? true : false;
					Sequence nseq = new Sequence( seq, rev );
					nseq.setName( ctgname+"_"+(rev?"rev":"frw"));
					//nseq.group = lastN[0];
					// serifier.addSequence( nseq );
					cseq.append( nseq.sb );
				} else {
					System.err.println("errrrror");
				}
				
				f = nsplit.indexOf(')',n+1);
				n = nsplit.indexOf('_',f+1);
			}
		}
		
		if( nstuff != null && nstuff.length() > 0 ) {
			String[] nssplit = nstuff.split("[ ]+");
			int m = 0;
			String store = "";
			for( String ns : nssplit ) {
				int maxmin = 1000;
				int l = ns.indexOf('(');
				while( l != -1 ) {
					int k = ns.indexOf(')',l+1);
					
					String nstr = ns.substring(l+1,k);
					int n = Integer.parseInt( nstr );
					maxmin = Math.min(maxmin, n);
					
					l = ns.indexOf('(',k+1);
				}
				
				if( maxmin > m || (maxmin == m && ns.length() < store.length()) ) {
					store = ns;
					m = maxmin;
				}
			}
			String oldnstuff = nstuff;
			nstuff = store;
			
			if( oldnstuff.equals(nstuff) ) System.out.println( nstuff );
			else System.out.println( nstuff + "    old    " + oldnstuff );
			
			String[] sp = nstuff.split("[ ]+");
			String nsplit = sp[sp.length-1];
			int f = nsplit.indexOf(')');
			int n = nsplit.indexOf('_',f+1);
			while( n != -1 ) {
				String sub = nsplit.substring(f+1, n);
				String ctgname = contigName( sub );
				Sequence seq = mseq.get(ctgname);
				touch.add( ctgname );
				if( seq != null ) {
					boolean rev = nsplit.charAt(n+1) == '3' ? true : false;
					Sequence nseq = new Sequence( seq, rev );
					nseq.setName( ctgname+"_"+(rev?"rev":"frw"));
					nseq.group = lastN == null ? null : lastN[0];
					serifier.addSequence( nseq );
					cseq.append( nseq.sb );
				}
				
				f = nsplit.indexOf(')',n+1);
				n = nsplit.indexOf('_',f+1);
			}
		}*/
	}
	
	public static void start( String type ) {
		touch.clear();
		serifier.clearAll();
		mm.clear();
		mseq.clear();
		
		try {
			FileReader fr;
			BufferedReader br;
			String line;
			Sequence seq;
			
			fr = new FileReader(home+type+add+"454ScaffoldContigs.fna");
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
			fr.close();
			
			fr = new FileReader(home+type+add+"454AllContigs.fna");
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
			fr.close();
			
			fr = new FileReader(home+type+add+"454ContigGraph.txt");
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
			
			int plasm = 1;
			FileWriter fw = new FileWriter( home + type + ".fna" );
			Sequence cseq = new Sequence(type+"_chromosome", null);
			
			fr = new FileReader(home+type+add+"454ContigScaffolds.txt");
			br = new BufferedReader( fr );
			
			String[] lastW = null;
			String[] lastN = null;
			String[] firstofnew = null;
			
			String[] split = null;
			line = br.readLine();
			while( line != null ) {
				split = line.split("[\t ]+");
				String scaff = split[0];
				String end = null;
				if( firstofnew != null && lastW != null && !scaff.equals(firstofnew[0]) ) {
					//next = 1;
					int i = lastW[5].indexOf('0');
					while( lastW[5].charAt(++i) == '0' ) ;
					String c0 = lastW[5].substring(i);
					Map<String,String> mog = mm.get(c0+"_3'");
					String nstuff = tengioff( mog, 0 );
					
					end =  "end---" + nstuff;
				}
				
				if( split[4].equals("W") ) {
					if( lastW != null ) {
						System.out.println( join(lastW) );
						seq = mseq.get(lastW[5]);
						touch.add( lastW[5] );
						seq.group = lastW[0];
						// lubububu if( seq != null ) serifier.addSequence( seq );
					}
					//boolean newscaff = !lastW[0].equals(split[0]);
					if( lastN != null ) {
						nstuffOut( lastN, lastW, split, cseq );
					} else if( lastW != null && !lastW[0].equals(split[0]) ) {
						nstuffOut( lastN, lastW, firstofnew, cseq );
						//next = 1;
					}/* else {
						int i = split[0].indexOf('0');
						String sctg = "sctg_"+split[0].substring(i+1)+(next >= 10 ? "_00" : "_000")+next++;
						Sequence sctgseq = mseq.get(sctg);
						if( sctgseq != null ) cseq.append( sctgseq.sb );
					}*/
					lastN = null;
					lastW = split;
				} else {
					lastN = split;
				}
				
				if( end != null ) System.out.println( end );
				
				if( firstofnew == null || !scaff.equals(firstofnew[0]) ) {
					next = 1;
					if( firstofnew != null && cseq.length() > 0 ) {
						//System.out.println("writing " + cseq.getName());
						
						cseq.setName( cseq.getName() + " ("+cseq.length()+")" );
						cseq.writeSequence(fw);
						serifier.addSequence( cseq );
						cseq = new Sequence(type+"_plasmid"+plasm++,null);
					}
					
					int i = split[5].indexOf('0');
					while( split[5].charAt(++i) == '0' ) ;
					String c0 = split[5].substring(i);
					Map<String,String> mog = mm.get(c0+"_5'");
					String nstuff = tengioff( mog, 0 );
					
					System.out.println( "beg---" + nstuff );
					
					firstofnew = split;
				}
				
				line = br.readLine();
			}
			br.close();
			
			int i = lastW[0].indexOf('0');
			String sctg = "sctg_"+lastW[0].substring(i+1)+(next >= 10 ? "_00" : "_000")+next++;
			Sequence sctgseq = mseq.get(sctg);
			if( sctgseq != null ) {
				cseq.append( sctgseq.sb );
			} else {
				System.err.println();
			}
			
			if( cseq.length() > 0 ) {
				cseq.setName( cseq.getName() + " ("+cseq.length()+")" );
				cseq.writeSequence(fw);
				serifier.addSequence( cseq );
			}
			fw.close();
			
			if( firstofnew != null ) {
				next = 1;
				/*i = lastW[5].indexOf('0');
				while( lastW[5].charAt(++i) == '0' ) ;
				String c1 = lastW[5].substring(i);
				
				i = firstofnew[5].indexOf('0');
				while( firstofnew[5].charAt(++i) == '0' ) ;
				String c2 = firstofnew[5].substring(i);
				
				String nstuff = null;
				if( mm.containsKey(c1+"_3'") ) {
					Map<String,String> mog = mm.get(c1+"_3'");
					nstuff = tengi( mog, c2, 0 );
					if( mog.containsKey(c2+"_5'") ) {
						String q = mog.get(c2+"_5'");
						if( nstuff.length() == 0 ) nstuff = "directconnect("+q+")";
						else nstuff += " directconnect("+q+")";
					}
				}*/
				
				/*if( nstuff != null && nstuff.length() > 0 ) {
					System.out.println( nstuff );
					String[] sp = nstuff.split("[ ]+");
					String nsplit = sp[sp.length-1];
					int f = nsplit.indexOf(')');
					int n = nsplit.indexOf('_',f+1);
					if( n != -1 ) {
						String sub = nsplit.substring(f+1, n);
						String ctgname = contigName( sub );
						seq = mseq.get(ctgname);
						touch.add( ctgname );
						if( seq != null ) {
							boolean rev = nsplit.charAt(n+1) == '3' ? true : false;
							Sequence nseq = new Sequence( seq, rev );
							nseq.setName( ctgname+"_"+(rev?"rev":"frw"));
							nseq.group = lastN == null ? null : lastN[0];
							serifier.addSequence( nseq );
						}
					}
				}*/
				
				nstuffOut( lastN, lastW, firstofnew, cseq );
			}
			/*for( String seqname : mseq.keySet() ) {
				if( !touch.contains(seqname) ) {
					seq = mseq.get( seqname );
					serifier.addSequence( seq );
				}
			}*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String,Sequence>				mseq = new HashMap<String,Sequence>();
	public static Map<String,Map<String,String>>	mm = new HashMap<String,Map<String,String>>();
	public static Set<String>						touch = new HashSet<String>();
	public static Serifier							serifier = new Serifier();
	public static int 								next = 1;
	
	public static String home = "/Users/sigmar/smassembly/";
	public static String type1 = null;//"eggertsoni2789_v30";
	public static String add = "/";
	public static void main(String[] args) {
		if( type1 == null ) {
			Path p = Paths.get( home );
			for( File f : p.toFile().listFiles() ) {
				if( f.isDirectory() ) start( f.getName() );
			}
		} else {
			start( type1 );
			
			JavaFasta	jf = new JavaFasta(serifier);
			JFrame		frame = new JFrame();
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.setSize(800, 600);
			jf.initGui(frame);
			jf.updateView();
			frame.setVisible( true );
		}
	}
}
