package org.simmi.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class GBK2AminoFasta {
	public static class Anno {
		public Anno( String type ) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
		
		String 			name;
		String			gene;
		String			id;
		String			spec;
		String 			type;
		StringBuilder	contig;
		int				start;
		int				stop;
		boolean 		comp;
	};
	
	public static List<Sequence> handleText( Map<String,StringBuilder> filetextmap, Map<String,Path> annoset, Writer allout, Path path, String replace ) throws IOException {
		List<Sequence>	lseq = new ArrayList<Sequence>();
		
		//List<Anno>	annolist = new ArrayList<Anno>();
		for( String tag : filetextmap.keySet() ) {
			//seq = new Sequence( tag, null );
			//lseq.add( seq );
			
			StringBuilder filetext = filetextmap.get( tag );
			String locus = null;
			int ind = filetext.indexOf("\n");
			String line = null;
			if( ind > 0 ) {
				line = filetext.substring(0, ind);
			}
			
			Annotation		anno = null;
			
			//int k = filename.indexOf('.');
			//if( k == -1 ) k = filename.length();
			String spec = replace != null ? replace : tag.replace(".gbk", ""); //filename.substring(0, k);
			
			Set<String>	xref = new TreeSet<String>();
			//int contignum = 0;
			Sequence	strbuf = new Sequence();
			while( line!= null ) {
				lseq.add( strbuf );
				while( line != null ) {
					String trimline = line.trim();
					
					if( trimline.startsWith("LOCUS") ) {
						locus = trimline.split("[ \t]+")[1];
					}
					//String[] split = trimline.split("[\t ]+");
					
					String banno = null;
					for( String annostr : annoset.keySet() ) {
						if( trimline.startsWith( annostr+"  " ) ) {
							banno = annostr;
							break;
						}
					}
					if( trimline.startsWith("CDS  ") || trimline.startsWith("tRNA  ") || trimline.startsWith("rRNA  ") || trimline.startsWith("mRNA  ") || trimline.startsWith("misc_feature  ") ) {
						if( anno != null ) {
							if( anno.id == null || anno.id.length() == 0 ) anno.id = anno.ori == -1 ? "comp("+anno.start+".."+anno.stop+")" : anno.start+".."+anno.stop;
							if( xref.size() > 0 ) {
								anno.name += "(";
								for( String xr : xref ) {
									anno.name += xr;
								}
								anno.name += ")";
								xref.clear();
							}
							
							/*if( anno.spec.contains("MAT4699") || anno.spec.contains("MAT4721") || anno.spec.contains("MAT4725") || anno.spec.contains("MAT4726") ) {
								anno.start--;
								anno.stop--;
							}*/
							System.err.println( strbuf.getName() + " mumu " + anno.name );
							strbuf.addAnnotation( anno );
						}
						anno = null;
					}
					if( banno != null ) { //|| trimline.startsWith("gene ") ) {
						anno = new Annotation( banno );
						anno.seq = strbuf;
						
						//anno.spec = spec + (contignum > 0 ? "_contig"+(contignum+1) : "");
						strbuf.setName( locus.contains(spec) ? locus : spec+ "_"+locus );
						
						String[] split = trimline.split("[\t ]+");
						if( split.length > 1 ) {
							if( split[1].startsWith("compl") ) {
								int iof = split[1].indexOf(")");
								while( iof == -1 ) {
									int k = filetext.indexOf("\n", ind+1);
									if( ind > 0 ) line = filetext.substring(ind+1, k);
									ind = k;
									trimline = trimline+line.trim();
									split = trimline.split("[\t ]+");
									iof = split[1].indexOf(")");
								}
								int osv = split[1].lastIndexOf('(');
								String substr = split[1].substring(osv+1, iof);
								String[] nsplit = substr.split("\\.\\.");
								//if( !nsplit[0].startsWith("join")  ) {
								char c = nsplit[0].charAt(0);
								char c2 = nsplit[nsplit.length-1].charAt(0);
								if( c >= '0' && c <= '9' && c2 >= '0' && c2 <= '9' ) {
									anno.start = Integer.parseInt( nsplit[0] );
									anno.stop = Integer.parseInt( nsplit[nsplit.length-1] );
									anno.ori = -1;
								} else {
									System.err.println( nsplit[0] + " n " + nsplit[nsplit.length-1] );
									anno = null;
								}
							} else if( split[1].startsWith("join") ) {
								int iof = split[1].lastIndexOf(")");
								while( iof == -1 ) {
									int k = filetext.indexOf("\n", ind+1);
									if( ind > 0 ) line = filetext.substring(ind+1, k);
									ind = k;
									trimline = trimline+line.trim();
									split = trimline.split("[\t ]+");
									iof = split[1].lastIndexOf(")");
								}
								int osv = split[1].indexOf('(');
								
								if( iof < osv+1 ) {
									System.err.println();
								}
								
								String substr = split[1].substring(osv+1, iof);
								String[] sepstr = substr.split(",");
								
								anno.start = Integer.MAX_VALUE;
								anno.stop = Integer.MIN_VALUE;
								for( String sp : sepstr ) {
									String[] nsplit = sp.split("\\.\\.");
									//if( !nsplit[0].startsWith("join")  ) {
									char c = nsplit[0].charAt(0);
									char c2 = nsplit[nsplit.length-1].charAt(0);
									if( c >= '0' && c <= '9' && c2 >= '0' && c2 <= '9' ) {
										anno.start = Math.min( anno.start, Integer.parseInt( nsplit[0] ) );
										anno.stop = Math.max( anno.stop, Integer.parseInt( nsplit[nsplit.length-1] ) );
										anno.ori = -1;
									} else {
										System.err.println( nsplit[0] + " n " + nsplit[nsplit.length-1] );
										anno = null;
									}
								}
								
								if( anno != null && anno.stop-anno.start > 10000 ) {
									System.err.println();
								}
							} else {
								String[] nsplit;
								if( split[1].startsWith("<") ) nsplit = split[1].substring(1).split("\\.\\.");
								else nsplit = split[1].split("\\.\\.");
								if( nsplit.length > 1 ) {
									char c = nsplit[0].charAt(0);
									char c2 = nsplit[1].charAt(0);
									if( c >= '0' && c <= '9' && c2 >= '0' && c2 <= '9' ) {
										anno.start = Integer.parseInt( nsplit[0] );
										anno.stop = Integer.parseInt( nsplit[1] );
										anno.ori = 1;
									} else {
										System.err.println( nsplit[0] + " n " + nsplit[1] );
										anno = null;
									}
								} else {
									System.err.println("nono2");
									anno = null;
								}
							}
						} else {
							System.err.println("nono");
						}
					} else if( trimline.startsWith("/db_xref") ) {
						xref.add( trimline.substring(10, trimline.length()-1) );
					} else if( trimline.startsWith("/EC_number") ) {
						String ec = "EC"+trimline.substring(12, trimline.length()-1);
						xref.add( ec );
					} else if( trimline.startsWith("/product") ) {
						if( anno != null ) {
							if( trimline.length() > 10 ) {
								//System.err.println("badlfjalkdjalksdj");
								int i = trimline.indexOf('"', 10);
								while( i == -1 ) {
									int k = filetext.indexOf("\n", ind+1);
									line = null;
									if( k > 0 ) line = filetext.substring(ind+1, k);
									ind = k;
									
									if( line != null ) {
										trimline += line.trim();
										i = trimline.indexOf('"', 10);
									} else i = trimline.length()-1;
								}
								anno.name = trimline.substring(10,i);
								int ecind = Math.max( anno.name.indexOf("(EC"), anno.name.indexOf("(COG") );
								if( ecind != -1 ) {
									anno.name = anno.name.substring(0,ecind).trim();
								}
							}
							//annolist.add( anno );
							//anno = null;
						}
					} else if( trimline.startsWith("/protein_id") ) {
						if( anno != null ) {
							anno.id = trimline.substring(13,trimline.length()-1);
							//annolist.add( anno );
							//anno = null;
						}
					} else if( trimline.startsWith("/gene=") ) {
						if( anno != null ) {
							anno.group = trimline.substring(7,trimline.length()-1);
						}
					} else if( trimline.startsWith("/gene_synonym") ) {
						if( anno != null ) {
							anno.group = trimline.substring(15,trimline.length()-1);
						}
					} else if( trimline.startsWith("/locus_tag") ) {
						if( anno != null ) {
							if( !anno.getType().equals("tRNA") && !anno.getType().equals("rRNA") && (anno.id == null || anno.id.contains("..") ) ) {
								anno.id = trimline.substring(12,trimline.length()-1);
							}
							//annolist.add( anno );
							//anno = null;
						}
					} else if( trimline.startsWith("ORIGIN") ) {
						if( anno != null ) {
							if( anno.id == null || anno.id.length() == 0 ) anno.id = anno.ori == -1 ? "comp("+anno.start+".."+anno.stop+")" : anno.start+".."+anno.stop;
							if( xref.size() > 0 ) {
								anno.name += "(";
								for( String xr : xref ) {
									anno.name += xr;
								}
								anno.name += ")";
								xref.clear();
							}
							
							/*if( anno.spec.contains("MAT4699") || anno.spec.contains("MAT4721") || anno.spec.contains("MAT4725") || anno.spec.contains("MAT4726") ) {
								anno.start--;
								anno.stop--;
							}*/
							strbuf.addAnnotation( anno );
						}
						anno = null;
						break;
					}
					
					int k = filetext.indexOf("\n", ind+1);
					line = null;
					if( k > 0 ) {
						line = filetext.substring(ind+1, k);
					}
					ind = k;
				}
				
				int k = filetext.indexOf("\n", ind+1);
				line = null;
				if( k > 0 ) {
					line = filetext.substring(ind+1, k);
				}
				ind = k;
				while( line != null && !line.startsWith("//") ) {
					strbuf.append( line.replaceAll("[\t 1234567890/]+", "") );
					
					k = filetext.indexOf("\n", ind+1);
					line = null;
					if( k > 0 ) line = filetext.substring(ind+1, k);
					ind = k;
				}
				
				if( line != null ) {
					/*if( contignum == 0 ) {
						for( Anno a : annolist ) {
							a.spec += "_contig1";
						}
					}*/
					
					//contignum++;
					
					k = filetext.indexOf("\n", ind+1);
					line = null;
					if( k > 0 ) {
						line = filetext.substring(ind+1, k);
					}
					ind = k;
				}
				
				//if( contignum > 0 && anno != null && anno.spec != null ) anno.spec += "_contig"+contignum;;
				
				//allout.write( ">" + spec + (contignum > 0 ? "_contig"+contignum+"\n" : "\n") );
				
				if( allout != null ) {
					//if( locus.contains(spec) ) allout.write( ">" + locus + "\n" );
					//else allout.write( ">" + spec + "_" + locus + "\n" );
					strbuf.setName( (locus.contains(spec) ? "" : spec + "_") + locus);
					strbuf.writeSequence( allout );
					/*for( int i = 0; i < strbuf.length(); i+=70 ) {
						allout.write( strbuf.getSubstring( i, Math.min( strbuf.length(), i+70 ), 1 ) + "\n" );
					}*/
				}
				
				strbuf = new Sequence();
			}
		}
		
		Map<String,String> nameMap = new HashMap<String,String>();
		Map<Path,Writer>	urifile = new HashMap<Path,Writer>();
		for( Sequence seq : lseq ) {
			List<Annotation> annset = seq.getAnnotations();
			if( annset != null ) for( Annotation ao : annset ) {
				if( ao.name != null ) {
					Sequence	strbuf = ao.seq;
					Path uri = annoset.get( ao.getType() );
					
					if( uri != null ) {
						Writer out;
						if( !urifile.containsKey( uri ) ) {
							Writer fw = Files.newBufferedWriter(uri, StandardOpenOption.CREATE);
							urifile.put( uri, fw );
							
							out = fw;
						} else {
							out = urifile.get( uri );
						}
						
						/*if( out == null ) {
							System.err.println();
						}*/
						
						boolean amino = ao.getType().contains("CDS");
						if( ao.id != null && ao.group != null && !ao.id.contains("..") ) nameMap.put(ao.id, ao.group);
						
						String end = amino ? " # " + ao.start + " # " + ao.stop + " # " + ao.ori + " #\n" : "\n";
						if( out != null ) {
							/*if( replace != null ) {
								String rep = replace + ao.spec.substring( ao.spec.indexOf('_') );
								out.write( ">"+ao.id + " " + ao.name + " [" + rep + "]" + end );
							} else {*/
							System.err.println(strbuf.getName() + " lebbi " + ao.getName());
							out.write( ">"+ao.id + " " + ao.name + " [" + ao.seq.getName() + "]" + end );
							//}
						}
						//strbuf.
						
						//System.err.println(val);
						//String	ami = "";
						
						int t = 0;
						if( amino ) {
							int sstart = Math.max(0, ao.start-1);
							int sstop = Math.min( ao.stop, strbuf.length() );
							
							/*if( !(sstart >= 0 && sstop <= strbuf.length()) ) {
								System.err.println();
							}*/
							
							String 	val = strbuf.getSubstring( sstart, sstop, 1 );
							if( ao.ori == -1 ) {
								for( int i = val.length()-3; i >= 0; i-=3 ) {
									//ami += 
									String first = val.substring(i, i+3).toUpperCase();
									String second = Sequence.revcom.get( first );
									Character str = Sequence.amimap.get( second );
									if( str != null ) {
										if( str.equals('0') || str.equals('1') ) break;
										else if( out != null ) out.write( str );//+ " " + t + " " );
										if( (++t % 60) == 0 && out != null ) out.write("\n");
									}
								}
							} else {
								for( int i = 0; i < val.length(); i+=3 ) {
									//ami += 
									String first = val.substring( i, Math.min(val.length(), i+3) ).toUpperCase();
									Character str = Sequence.amimap.get( first );
									if( str != null ) {
										if( str.equals('0') || str.equals('1') ) break;
										else if( out != null ) out.write( str );//+ " " + t + " " );
										if( (++t % 60) == 0 && out != null ) out.write("\n");
									}
								}
							}
						} else {
							if( ao.ori == -1 ) {
								for( int i = ao.stop-1; i >= ao.start; i-- ) {
									char c = strbuf.charAt(i);
									Character bigc = Sequence.rc.get( Character.toUpperCase( c ) );
									if( bigc == null ) System.err.println( "blerr " + c );
									if( out != null ) out.write( bigc != null ? bigc : c );
									if( (++t % 60) == 0 && out != null ) out.write("\n");
								}
							} else {
								for( int i = ao.start; i < ao.stop; i+=60 ) {
									int start = i;
									int stop = Math.min( ao.stop, i+60 );
									String str = strbuf.getSubstring( start, stop, 1 );
									if( out != null ) out.write( str.toUpperCase() + (str.length() == 60 ? "\n" : "") );
								}
							}
						}
						if( out != null ) out.write("\n");
						//if( c++ > 10 ) break;
					}
				}
			}
		}
		
		if( path != null ) {
			Path p = path.getParent().resolve(path.getFileName()+".namemap"); //new File( new URI(path+".namemap") );
			//FileWriter mfw = new FileWriter( f );
			Writer mfw = Files.newBufferedWriter( p );
			for( String a : nameMap.keySet() ) {
				String gene = nameMap.get(a);
				mfw.write( a + "\t" + gene + "\n" );
			}
			mfw.close();
		}
		
		for( Path uri : urifile.keySet() ) {
			Writer w = urifile.get( uri );
			if( w != null ) w.close();
		}
		if( allout != null ) allout.close();
			//String encoded = encode( sb.toString() );
			//String dataurl = "data:text/plain;base64,"+encoded;
			//console( "dataurl length: "+dataurl.length() );
			//ta.setText( sb.toString() );
			//Window.Location.assign( dataurl );
		
		return lseq;
	}
	
	public static void main(String[] args) {
		
		/*try {
			File f = new File("/home/sigmar/ami57/ami.gb");
			FileReader fr = new FileReader( f );
			BufferedReader br = new BufferedReader( fr );
			StringBuilder filetext = new StringBuilder();
			String line = br.readLine();
			while( line != null ) {
				filetext.append( line+"\n" );
				line = br.readLine();
			}
			br.close();
			fr.close();
			
			StringBuilder sb = handleText("filename.aa", filetext.toString());
			FileWriter fw = new FileWriter("/home/sigmar/filename.aa");
			fw.write( sb.toString() );
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//ftpExtract();
/* catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String basesave = "/home/sigmar/ftpncbi/";
		//ftpExtract( basesave );
		
		try {
			File file = new File( basesave );
			File[] ff = file.listFiles();
			for( File f : ff ) {
				FileReader fr = new FileReader( f );
				BufferedReader br = new BufferedReader( fr );
				StringBuilder filetext = new StringBuilder();
				String line = br.readLine();
				while( line != null ) {
					filetext.append( line+"\n" );
					line = br.readLine();
				}
				br.close();
				fr.close();
				
				String fname = f.getName();
				String fstr = fname.substring(0, fname.length()-4);
				System.err.println( "about to "+fstr );
				
				boolean amino = false;
				String[] annoarray = {"tRNA", "rRNA"};//{"CDS", "tRNA", "rRNA", "mRNA"};
				//Arrays.asList( annoarray )
				Map<String,URI>	map = new HashMap<String,URI>();
				map.put( "tRNA", null );
				map.put( "rRNA", null );
				
				StringWriter sb = new StringWriter();
				//handleText( fstr, filetext, map, null );
				// appengine out: FileWriter fw = new FileWriter( "/home/sigmar/ncbiaas/nn2/"+fstr+(amino ? ".aa" : ".nn") );
				//fw.write( sb.toString() );
				//fw.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
