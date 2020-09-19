package org.simmi.javafasta.unsigned;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFrame;

import org.simmi.javafasta.shared.Sequence;
import org.simmi.javafasta.shared.Annotation;
import org.simmi.javafasta.shared.Serifier;

public class FlxReader {
	public static String join( String[] jj ) {
		String ret = null;
		for( String j : jj ) {
			if( ret == null ) ret = j;
			else ret += "\t"+j;
		}
		return ret;
	}
	
	public static String tengioff( Map<String,String> mog, int recurcount, int lenthres ) {
		String nstuff = "";
		if( mog != null && recurcount < 5 ) {
			for( String m : mog.keySet() ) {
				if( !nstuff.contains(m) ) {
					String q1 = mog.get(m);
					
					int li = m.lastIndexOf('_');
					String c3 = m.substring(0,li);
					
					String ctgname = contigName( c3 );
					Sequence seq = mseq.get(ctgname);
					
					if( seq == null ) {
						System.err.println();
					}
					
					if( seq.length() < lenthres ) {
						if( m.substring( li ).equals("_5'") ) {
							Map<String,String> ss = mm.get(c3+"_3'");
							if( ss != null ) {
								String st = tengioff( ss, recurcount+1, lenthres );
								String[] split = st.split( "[ ]+" );
								for( String s : split ) {
									if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+s;
									else nstuff += "  ("+q1+")"+m+s;
								}
							} else {
								if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m;
								else nstuff += "  ("+q1+")"+m;
							}
						} else {
							Map<String,String> ss = mm.get(c3+"_5'");
							if( ss != null ) {
								String st = tengioff( ss, recurcount+1, lenthres );
								String[] split = st.split( "[ ]+" );
								for( String s : split ) {
									if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+s;
									else nstuff += "  ("+q1+")"+m+s;
								}
								/*if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m;
								else nstuff += " ("+q1+")"+m;
								if( st.length() > 0 ) nstuff += "(  "+st+"  )";*/
							} else {
								if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m;
								else nstuff += "  ("+q1+")"+m;
							}
						}
					}
				}
			}
		}
		return nstuff;
	}
	
	public static String tengi( Map<String,String> mog, String c2, String ori2, int recurcount, int lenthres ) {
		String nstuff = "";
		if( recurcount < 7 ) {
			for( String m : mog.keySet() ) {
				//if( !nstuff.contains(m) ) {
				String q1 = mog.get(m);
				
				int li = m.lastIndexOf('_');
				String c3 = m.substring(0,li);
			
				String ctgname = contigName( c3 );
				Sequence seq = mseq.get(ctgname);
				if( seq.length() < lenthres ) {
					if( m.substring( li ).equals("_5'") ) {
						Map<String,String> ss = mm.get(c3+"_3'");
						if( ss != null ) {
							if( ss.containsKey(c2+ori2) ) {
								String q2 = ss.get(c2+ori2);
								
								if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+"("+q2+")";
								else nstuff += " ("+q1+")"+m+"("+q2+")";
								//break;
							} else {
								String st = tengi( ss, c2, ori2, recurcount+1, lenthres );
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
							if( ss.containsKey(c2+ori2) ) {
								String q2 = ss.get(c2+ori2);
								
								if( nstuff.length() == 0 ) nstuff += "("+q1+")"+m+"("+q2+")";
								else {
									nstuff += " ("+q1+")"+m+"("+q2+")";
								}
								//break;
							} else {
								String st = tengi( ss, c2, ori2, recurcount+1, lenthres );
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
				}
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
	
	public static void nextSet() {
		next = 1;
	}
	
	public static int nextIncr() {
		return next++;
	}
	
	public static void nstuffOut( String[] lastN, String[] lastW, String[] split, Sequence cseq, boolean showunclosed ) {
		String lastctg = lastW[5];
		boolean lastrv = false;
		if( lastctg.endsWith("r") ) {
			lastctg = lastctg.substring(0,lastctg.length()-1);
			lastrv = true;
		}
		int i = lastctg.indexOf('0');
		while( lastctg.charAt(++i) == '0' ) ;
		String c1 = lastctg.substring(i);
		
		i = lastW[0].indexOf('0');
		String sctg = "sctg_"+lastW[0].substring(i+1)+(next >= 10 ? "_00" : "_000")+nextIncr();
		Sequence sctgseq = mseq.get(sctg);
		if( sctgseq == null ) {
			String ctg = "contig" + (c1.length() < 2 ? "0000" : c1.length() < 3 ? "000" : "00") + c1;
			sctgseq = mseq.get(ctg);
		}
		//System.out.println( "   outputing " + sctg + " " + sctgseq.length() );
		
		if( sctgseq != null ) {
			Annotation a = new Annotation(cseq,sctgseq.getName(),null,cseq.length(),cseq.length()+sctgseq.length(),1,null);
			cseq.append( sctgseq.getSequence() );
			cseq.addAnnotation(a);
			int len = Math.min(sctgseq.getSequence().length(),10);
			//System.out.println("appending2 " + sctgseq.getName() + " " + len + " " + sctgseq.sb.substring(0, len) );
		} else {
			System.out.println( "   empty " );
		}
		
		String splitctg = split[5];
		boolean splitrv = false;
		if( splitctg.endsWith("r") ) {
			splitctg = splitctg.substring(0,splitctg.length()-1);
			splitrv = true;
		}
		i = splitctg.indexOf('0');
		while( splitctg.charAt(++i) == '0' ) ;
		String c2 = splitctg.substring(i);
		
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
		
		String ori1 = lastrv ? "_5'" : "_3'";
		String ori2 = splitrv ? "_3'" : "_5'";
		
		if( mm.containsKey(c1+ori1) ) {
			Map<String,String> mog = mm.get(c1+ori1);
			nstuff = tengi( mog, c2, ori2, 0, maxlen );
			//System.err.println( c1 + ori1 + "   " + c2 + ori2 );
			if( mog.containsKey(c2+ori2) ) {
				String q = mog.get(c2+ori2);
				if( nstuff.length() == 0 ) nstuff = "directconnect("+q+")";
				else nstuff += " directconnect("+q+")";
			}
		}
		
		/*if( mm.containsKey(c1+"_3'") ) {
			Map<String,String> mog = mm.get(c1+"_3'");
			nstuff = tengi( mog, c2, 0, 10000 );
			if( mog.containsKey(c2+"_5'") ) {
				String q = mog.get(c2+"_5'");
				if( nstuff.length() == 0 ) nstuff = "directconnect("+q+")";
				else nstuff += " directconnect("+q+")";
			}
		}*/
		
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
					nseq.setGroup(lastN != null ? lastN[0] : null);
					//serifier.addSequence( nseq );
					
					if( nseq.length() > maxlen ) {
						System.err.println( cseq.length() );
					}
					Annotation a = new Annotation(cseq,nseq.getName(),null,cseq.length(),cseq.length()+nseq.length(),rev?-1:1,null);
					cseq.append( nseq.getSequence() );
					cseq.addAnnotation(a);
					
					int len = Math.min(nseq.getSequence().length(),10);
					//System.out.println("appending3 " + nseq.getName() + " " + len + " " + nseq.sb.substring(0, len) );
				} else {
					System.err.println("errrrror");
				}
				
				f = nsplit.indexOf(')',n+1);
				n = nsplit.indexOf('_',f+1);
			}
		} else if( lastN != null ) {
			cseq.append("NNN");
			//System.out.println( "appending NNN" );
			i = lastW[5].indexOf('0');
			while( lastW[5].charAt(++i) == '0' ) ;
			String c0 = lastW[5].substring(i);
			Map<String,String> mog = mm.get(c0+"_3'");
			nstuff = tengioff( mog, 0, 10000000 );
			if( showunclosed ) System.out.println( "<---" + nstuff );
			
			System.out.println( join(lastN) );
			
			if( split != null ) {
				i = split[5].indexOf('0');
				while( split[5].charAt(++i) == '0' ) ;
				c0 = split[5].substring(i);
				mog = mm.get(c0+"_5'");
				nstuff = tengioff( mog, 0, 10000000 );
				if( showunclosed ) System.out.println( "--->" + nstuff );
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
	
	public StringBuilder referenceAssembly( String home, String comp, String what, final List<Sequence> bb, final List<Sequence> allcontigs ) throws IOException, InterruptedException {
		File userhome = new File(System.getProperty("user.home") );
		
		List<Sequence> preorder = new ArrayList<Sequence>();
		ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/makeblastdb","-dbtype","nucl","-out",comp,"-title",comp);
		pb.directory( userhome );
		Process pr = pb.start();
		
		final InputStream is = pr.getInputStream();
		Thread ti = new Thread() {
			public void run() {
				try {
					int b = is.read();
					while( b != -1 ) {
						System.err.write(b);
						b = is.read();
					}
					System.err.println();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		ti.start();
		
		final InputStream es = pr.getErrorStream();
		Thread t = new Thread() {
			public void run() {
				try {
					int b = es.read();
					while( b != -1 ) {
						System.err.write(b);
						b = es.read();
					}
					System.err.println();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
				
		OutputStream o = pr.getOutputStream();
		Sequence.writeFasta( o, bb );
		o.close();
		
		pr.waitFor();
		
		//pb.
		
		pb = new ProcessBuilder("/usr/local/bin/blastn","-db",comp);
		pb.directory( userhome );
		pr = pb.start();
		final InputStream es2 = pr.getErrorStream();
		t = new Thread() {
			public void run() {
				try {
					int b = es2.read();
					while( b != -1 ) {
						System.err.write(b);
						b = es2.read();
					}
					System.err.println();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		
		final OutputStream os5 = pr.getOutputStream();
		t = new Thread() {
			public void run() {
				try {
					Sequence.writeFasta(os5, allcontigs);
					//os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		
		Map<String,Map<Integer,String>>	mtm = new HashMap<String,Map<Integer,String>>();
		String 				current = null;
		int					currentlen = 0;
		Map<Integer,String>	tm = null;
		boolean				rstrand = false;
		
		Path tmp = Paths.get(home+comp+"_on_"+what+".blastout");
		BufferedWriter bw = Files.newBufferedWriter(tmp, StandardOpenOption.CREATE);
		final InputStream is2 = pr.getInputStream();
		Reader fr = new InputStreamReader( is2 );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		bw.write(line+"\n");
		try {
		while( line != null ) {
			//System.err.println( line );
			if( line.startsWith("Query=") ) {
				current = line.substring(7,18);
				line = br.readLine();
				bw.write(line+"\n");
				line = br.readLine();
				bw.write(line+"\n");
				currentlen = Integer.parseInt(line.substring(7));
				if( currentlen < maxlen ) current = null;
			} else if( line.startsWith(">") ) {
				if( current != null ) {
					String u = line.substring(2);//.substring(2,line.indexOf('(')-1);
					if( mtm.containsKey(u) ) {
						tm = mtm.get(u);
					} else {
						tm = new TreeMap<Integer,String>();
						mtm.put(u, tm);
					}
				}
			} else if( line.contains("Strand=") ) {
				if( current != null && line.contains("Minus") ) rstrand = true;
				if( current != null ) System.err.println( line + "  " + current );
			} else if( line.startsWith("Query ") ) {
				if( tm != null ) {
					String[] split = line.split("[ ]+");
					int start1 = Integer.parseInt(split[1]);
					line = br.readLine();
					bw.write(line+"\n");
					line = br.readLine();
					bw.write(line+"\n");
					split = line.split("[ ]+");
					int start2 = Integer.parseInt(split[1]);
					
					if( rstrand ) tm.put(start2-(currentlen-start1), current+"r");
					else {
						int val = start2-start1;
						tm.put(val, current);
					}
					
					tm = null;
					current = null;
					rstrand = false;
				}
			}
			line = br.readLine();
			bw.write(line+"\n");
		}
		} catch( Exception e ) {
			e.printStackTrace();
			bw.close();
		}
		bw.close();
		
		pr.waitFor();
		
		StringBuilder sb = new StringBuilder();
		for( String k : mtm.keySet() ) {
			System.err.println("for "+k);
			tm = mtm.get(k);
			boolean first = true;
			for( int l : tm.keySet() ) {
				String 		ctg = tm.get(l);
				boolean		revc = ctg.endsWith("r");
				Sequence seq = mseq.get( revc?ctg.substring(0,ctg.length()-1):ctg );
				
				seq.setStart(l);
				if( revc ) seq.reverseComplement();
				
				if( !first ) sb.append(k+"\t0\t0\t0\tN\tscaffold\t0\t0\t0\n");
				sb.append(k+"\t"+l+"\t0\t0\tW\t"+ctg+"\t1\t"+seq.length()+"\t0\n");
				
				System.err.println( "\t"+ctg );
				first = false;
			}
		}
		System.err.println( "sb size " + sb.length() );
		
		return sb;
	}
	
	public Map<String,Map<String,String>> loadContigGraph( BufferedReader br ) throws IOException {
		Map<String,Map<String,String>> mm = new HashMap<String,Map<String,String>>();
		String line = br.readLine();
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
		return mm;
	}
	
	public void connectContigs( BufferedReader br, Sequence cseq, boolean showunclosed, Writer fw, String type ) throws IOException {
		String[] lastW = null;
		String[] lastN = null;
		String[] firstofnew = null;
		String[] split = null;
		
		int plasm = 1;
		
		String line = br.readLine();
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
				String nstuff = tengioff( mog, 0, 10000000 );
				
				end =  "end---" + nstuff;
			}
			
			if( split[4].equals("W") ) {
				if( lastW != null ) {
					System.out.println( join(lastW) );
					String ctg = lastW[5];
					if( ctg.endsWith("r") ) {
						ctg = ctg.substring(0,ctg.length()-1);
					}
					Sequence seq = mseq.get(ctg);
					touch.add( ctg );
					seq.setGroup(lastW[0]);
					// lubububu if( seq != null ) serifier.addSequence( seq );
				}
				//boolean newscaff = !lastW[0].equals(split[0]);
				if( lastN != null ) {
					nstuffOut( lastN, lastW, split, cseq, showunclosed );
				} else if( lastW != null && !lastW[0].equals(split[0]) ) {
					nstuffOut( lastN, lastW, firstofnew, cseq, showunclosed );
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
				nextSet();
				
				if( firstofnew != null && cseq.length() > 0 ) {
					Collections.sort(cseq.annset);
					cseq.writeSequence(fw);
					
					System.out.println("out len " + cseq.length());
					serifier.addSequence( cseq );
					cseq = new Sequence(type+"_plasmid"+plasm++,null);
				}
				
				boolean reverse = false;
				String ctg = split[5];
				if( ctg.endsWith("r") ) {
					ctg = ctg.substring(0,ctg.length()-1);
					reverse = true;
				}
				int i = ctg.indexOf('0');
				while( ctg.charAt(++i) == '0' ) ;
				String c0 = ctg.substring(i);
				Map<String,String> mog = reverse ? mm.get(c0+"_3") : mm.get(c0+"_5'");
				String nstuff = tengioff( mog, 0, 10000000 );
				
				System.out.println( "beg---" + nstuff );
				
				firstofnew = split;
			}
			
			line = br.readLine();
		}
		
		/*int i = lastW[0].indexOf('0');
		String sctg = "sctg_"+lastW[0].substring(i+1)+(next >= 10 ? "_00" : "_000")+nextIncr();
		Sequence sctgseq = mseq.get(sctg);
		if( sctgseq != null ) {
			cseq.append( sctgseq.sb );
		} else {
			System.err.println();
		}*/
		
		System.err.println("lubbastop");
		
		if( firstofnew != null ) {
			//nextSet();
			
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
			
			if( split[4].equals("W") ) {
				if( lastW != null ) {
					System.out.println( join(lastW) );
					Sequence seq = mseq.get(lastW[5]);
					touch.add( lastW[5] );
					if( seq != null ) seq.setGroup(lastW[0]);
					// lubububu if( seq != null ) serifier.addSequence( seq );
				}
				//boolean newscaff = !lastW[0].equals(split[0]);
				nstuffOut( lastN, lastW, firstofnew, cseq, showunclosed );
				//next = 1;
			}
			//nstuffOut( lastN, lastW, firstofnew, cseq );
		}
		
		Collections.sort(cseq.annset);
		if( cseq.length() > 0 ) {
			cseq.writeSequence(fw);
			System.out.println("out len " + cseq.length());
			serifier.addSequence( cseq );
		}
	}
	
	public void start( String home, String type, boolean showunclosed, Writer fw, String comp, List<Sequence> bb ) throws InterruptedException {
		serifier.clearAll();
		
		touch.clear();
		serifier.clearAll();
		mm.clear();
		mseq.clear();
		
		try {
			System.err.println("starting fuck");
			Reader fr;
			BufferedReader br;
			Sequence seq;
			
			File f = new File(home+type+add+"454ScaffoldContigs.fna");
			if( f.exists() ) {
				fr = new FileReader(f);
				br = new BufferedReader( fr );
				List<Sequence> lseq = Sequence.readFasta(br, mseq, true);
				br.close();
				fr.close();
			}
			
			File file = new File(home+type+add+"454AllContigs.fna");
			//byte[] allcontigs = Files.readAllBytes(file.toPath());
			fr = new FileReader(file); //new InputStreamReader( new ByteArrayInputStream(allcontigs) );
			br = new BufferedReader( fr );
			
			List<Sequence> allcontigs = Sequence.readFasta(br, mseq, true);
			br.close();
			fr.close();
			
			//List<Sequence> lseq = Sequence.readFasta( new BufferedReader( new InputStreamReader(new ByteArrayInputStream(bb)) ), mseq);
			StringBuilder sb = comp != null ? referenceAssembly( home, comp, type, bb, allcontigs ) : null;
			
			
			fr = new FileReader(home+type+add+"454ContigGraph.txt");
			br = new BufferedReader( fr );
			mm = loadContigGraph( br );
			br.close();
			
			Sequence cseq = new Sequence(type+"_chromosome", null);
			
			if( sb != null && sb.length() > 0 ) {
				fr = new StringReader( sb.toString() );
			} else {
				file = new File(home+type+add+"454ContigScaffolds.txt");
				if( !file.exists() ) {
					file = new File(home+type+add+"454Scaffolds.txt");
				}
				fr = new FileReader(file);//"454ContigScaffolds.txt");
			}
			br = new BufferedReader( fr );
			connectContigs( br, cseq, showunclosed, fw, type );
			br.close();
			
			for( String seqname : mseq.keySet() ) {
				//if( !touch.contains(seqname) ) {
					seq = mseq.get( seqname );
					seq.setName( type+"_"+seqname );
					if( comp != null && !touch.contains(seqname) && seqname.contains("contig") ) seq.writeSequence(fw);
					serifier.addSequence( seq );
				//}
			}
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
	
	public static int maxlen = 5000;
	
	//public static String home = "/Users/sigmar/smassembly/";
	//public static String type1 = "brockianus1003";
	//public static String type1 = "filiformis947";
	
	//public static String home = "/Users/sigmar/";
	//public static String type1 = "b1003ass";
	
	public static String add = "/";
	public static void main(String[] args) {
		String type1 = null;
		String home = null;
		String comp = null;
		//byte[] bb = null;
		List<Sequence> bb = null;
		if( args.length > 0 ) home = args[0];
		if( args.length > 1 ) type1 = args[1];
		if( args.length > 2 ) {
			comp = args[2]; //"brockianus338";
			Path p = Paths.get(home+comp+".fna");
			try {
				bb = Sequence.readFasta(p, mseq);
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*try {
				bb = Files.readAllBytes(p);
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
		
		FlxReader flx = new FlxReader();
		try {
			if( type1 == null ) {
				Path p = Paths.get( home );
				for( File f : p.toFile().listFiles() ) {
					FileWriter fw = new FileWriter( home + f.getName() + ".fna" );
					if( f.isDirectory() ) flx.start( home, f.getName(), true, fw, comp, bb );
				}
			} else {
				FileWriter fw = new FileWriter( home + type1 + ".fna" );
				flx.start( home, type1, false, fw, comp, bb );
				
				JavaFasta	jf = new JavaFasta(serifier);
				JFrame		frame = new JFrame();
				frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
				frame.setSize(800, 600);
				jf.initGui(frame);
				jf.updateView();
				frame.setVisible( true );
			}
		} catch( Exception e ) {
			
		}
	}
}
