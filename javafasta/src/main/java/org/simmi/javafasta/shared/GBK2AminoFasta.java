package org.simmi.javafasta.shared;

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
import java.util.*;
import java.util.stream.Stream;

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
	}

	public static void oldannos(Annotation anno, Set<String> xref) {
		if( anno.getTag() == null || anno.getTag().length() == 0 ) {
			anno.setTag(anno.getSpecies() + "_" + (anno.ori == -1 ? "comp("+anno.start+".."+anno.stop+")" : anno.start+".."+anno.stop));
		}
		if( anno.getId() == null || anno.getId().length() == 0 ) anno.setId(anno.getTag());
		if( xref.size() > 0 ) {
			String annoname = anno.getName();
			annoname += "(";
			for( String xr : xref ) {
				annoname += xr;
			}
			annoname += ")";
			anno.setName( annoname );
			xref.clear();
		}
		//if( isCDS ) anno.gene.setTag();
	}

	public static void doAnnoStuff(Annotation anno, Set<String> xref) {
		if( anno.getTag() == null || anno.getTag().length() == 0 ) {
			anno.setTag(anno.getSpecies() + "_" + (anno.ori == -1 ? "comp("+anno.start+".."+anno.stop+")" : anno.start+".."+anno.stop));
		}
		if( anno.getId() == null || anno.getId().length() == 0 ) anno.setId(anno.getTag()); //anno.ori == -1 ? "comp("+anno.start+".."+anno.stop+")" : anno.start+".."+anno.stop;
		if( anno.getName() == null || anno.getName().length() == 0 ) {
			if(anno.getNote()!=null&&anno.getNote().length()>0) anno.setName(anno.getNote());
			else anno.setName(anno.getId());
		}
		if( xref.size() > 0 ) {
			StringBuilder annoname = new StringBuilder(anno.getName());
			annoname.append("(");
			for( String xr : xref ) {
				annoname.append(xr);
			}
			annoname.append(")");
			anno.setName(annoname.toString());
			xref.clear();
		}
	}
	
	public static Map<String,List<Sequence>> handleText(Map<String,Stream<String>> filetextmap, Map<String,Path> annoset, Writer allout, Path path, String replace, boolean noseq ) throws IOException {
		Map<String,List<Sequence>>	lseq = new HashMap<>();
		
		//List<Anno>	annolist = new ArrayList<Anno>();
		for( String tag : filetextmap.keySet() ) {
			//seq = new Sequence( tag, null );
			//lseq.add( seq );
			
			Stream<String> filetext = filetextmap.get( tag );
			String locus = null;
			Iterator<String> fileit = filetext.iterator();
			Annotation		anno = null;
			
			//int k = filename.indexOf('.');
			//if( k == -1 ) k = filename.length();
			String spec = replace != null ? replace : tag.replace(".gbk", ""); //filename.substring(0, k);

			System.err.println("about to import gb " + tag);
			Set<String>	xref = new TreeSet<>();
			Sequence	strbuf = new Contig();
			while( fileit.hasNext() ) {
				//lseq.add( strbuf );
				String line = null;
				do {
                    if (line==null) line = fileit.next();
					String trimline = line.trim();
					line = null;
					
					if( trimline.startsWith("LOCUS") ) {
						locus = trimline.split("[ \t]+")[1];
					} else if( trimline.startsWith("SOURCE") ) {
						//if( trimline.length() > 7 ) spec = trimline.substring(7).trim().replace(' ','_');
						int it = tag.lastIndexOf('.');
						spec = it == -1 ? tag : tag.substring(0,it);

						strbuf.setGroup(spec);
						if( lseq.containsKey(spec) ) {
							lseq.get(spec).add(strbuf);
						} else {
							List<Sequence> nlseq = new ArrayList<>();
							nlseq.add(strbuf);
							lseq.put(spec, nlseq);
						}
					} else if( trimline.startsWith("/country") ) {
						String country = trimline.substring(10,trimline.length()-1);
						strbuf.setCountry(country);
					}

					//String[] split = trimline.split("[\t ]+");
					
					String banno = null;
					for( String annostr : annoset.keySet() ) {
						if( trimline.startsWith( annostr+"  " ) ) {
							banno = annostr;
							break;
						}
					}
					boolean isCDS = trimline.startsWith("CDS  ");
					boolean isGene = trimline.startsWith("gene  ");
					if( isCDS || isGene || trimline.startsWith("tRNA  ") || trimline.startsWith("rRNA  ") || trimline.startsWith("mRNA  ") || trimline.startsWith("misc_feature  ") ) {
						if( anno != null ) {
							doAnnoStuff(anno, xref);
							strbuf.addAnnotation( anno );
						}
						anno = null;
					}
					if( banno != null ) { //|| trimline.startsWith("gene ") ) {
						anno = banno.equals("CDS") ? new Tegeval( "gene" ) : new Annotation( banno );
						anno.setSeq(strbuf);
						
						//anno.spec = spec + (contignum > 0 ? "_contig"+(contignum+1) : "");
						//if( locus == null ) strbuf.setName( spec );
						//else strbuf.setName( locus.contains(spec) ? locus : spec+ "_"+locus );
						
						String[] split = trimline.split("[\t ]+");
						if( split.length > 1 ) {
							if( split[1].startsWith("compl") ) {
								int iof = split[1].lastIndexOf(")");
								while( iof == -1 ) {
									line = fileit.next();
									trimline = trimline+line.trim();
									split = trimline.split("[\t ]+");
									iof = split[1].lastIndexOf(")");
								}
								int osv = split[1].indexOf('(');
								String substr = split[1].substring(osv+1, iof);
								if( !substr.startsWith("join") ) {
									iof = substr.lastIndexOf(")");
									while( iof == -1 ) {
										line = fileit.next();
										trimline = trimline+line.trim();
										split = trimline.split("[\t ]+");
										substr = split[1];
										iof = substr.lastIndexOf(")");
									}
									osv = substr.indexOf('(');
									
									if( iof < osv+1 ) {
										System.err.println();
									}
									
									substr = substr.substring(osv+1, iof);
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
											break;
										}
									}
									
									if( anno != null && anno.stop-anno.start > 100000 ) {
										anno = null;
									}
								} else {
									String[] nsplit = substr.split("\\.\\.");
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
								}
							} else if( split[1].startsWith("join") ) {
								int iof = split[1].lastIndexOf(")");
								while( iof == -1 ) {
									line = fileit.next();
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
										anno.ori = 1;
									} else {
										System.err.println( nsplit[0] + " n " + nsplit[nsplit.length-1] );
										anno = null;
										break;
									}
								}
								
								if( anno != null && anno.stop-anno.start > 10000 ) {
									anno = null;
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
						String ecid = trimline.substring(12, trimline.length()-1);
						if(anno instanceof Tegeval) {
							Tegeval tegeval = (Tegeval) anno;
							tegeval.getGene().ecid = ecid;
						}
						String ec = "EC"+ecid;
						xref.add( ec );
					} else if( trimline.startsWith("/product") ) {
						if( anno != null ) {
							if( trimline.length() > 10 ) {
								int i = trimline.indexOf('"', 10);
								while( i == -1 ) {
									line = fileit.next();
									if( line != null ) {
										trimline += line.trim();
										i = trimline.indexOf('"', 10);
									} else i = trimline.length()-1;
								}
								anno.setName( trimline.substring(10,i) );
								int ecind = Math.max( anno.getName().indexOf("(EC"), anno.getName().indexOf("(COG") );
								if( ecind != -1 ) {
									anno.setName( anno.getName().substring(0,ecind).trim() );
								}
								anno.setName( anno.getName().replace("(","") );
								anno.setName( anno.getName().replace(")","") );
							}
							//annolist.add( anno );
							//anno = null;
						}
					} else if( trimline.startsWith("/note") ) {
						if( anno != null ) {
							if( trimline.length() > 7 ) {
								int i = trimline.indexOf('"', 7);
								while( i == -1 ) {
									line = fileit.next();
									if( line != null ) {
										trimline += line.trim();
										i = trimline.indexOf('"', 7);
									} else i = trimline.length()-1;
								}
								anno.setNote( trimline.substring(7,i) );
								int ecind = Math.max( anno.getNote().indexOf("(EC"), anno.getNote().indexOf("(COG") );
								if( ecind != -1 ) {
									anno.setNote( anno.getNote().substring(0,ecind).trim() );
								}
								anno.setNote( anno.getNote().replace("(","") );
								anno.setNote( anno.getNote().replace(")","") );
							}
							//annolist.add( anno );
							//anno = null;
						}
					} else if( trimline.startsWith("/protein_id") ) {
						if( anno != null ) {
							anno.setId(trimline.substring(13,trimline.length()-1));
							//annolist.add( anno );
							//anno = null;
						}
					} else if( trimline.startsWith("/gene=") ) {
						if( anno != null ) {
							anno.setGroup(trimline.substring(7,trimline.length()-1));
						}
					} else if( trimline.startsWith("/gene_synonym") ) {
						if( anno != null ) {
							anno.setGroup(trimline.substring(15,trimline.length()-1));
						}
					} else if( trimline.startsWith("/locus_tag") ) {
						if( anno != null ) {
							//if( !anno.getType().equals("tRNA") && !anno.getType().equals("rRNA") && (anno.id == null || anno.id.contains("..") ) ) {
								anno.setTag(trimline.substring(12,trimline.length()-1));
							//}
							//annolist.add( anno );
							//anno = null;
						}
					} else if( trimline.startsWith("ORIGIN") ) {
						if( anno != null ) {
							doAnnoStuff(anno, xref);
							
							/*if( anno.spec.contains("MAT4699") || anno.spec.contains("MAT4721") || anno.spec.contains("MAT4725") || anno.spec.contains("MAT4726") ) {
								anno.start--;
								anno.stop--;
							}*/
							strbuf.addAnnotation( anno );
						}
						anno = null;
						break;
					}
				} while( fileit.hasNext() );

				while( !noseq && fileit.hasNext() ) {
					line = fileit.next();
					if( line.startsWith("//") ) break;
					strbuf.append( line.replaceAll("[\t 1234567890/]+", "") );
				}

				//if( contignum > 0 && anno != null && anno.spec != null ) anno.spec += "_contig"+contignum;;
				//allout.write( ">" + spec + (contignum > 0 ? "_contig"+contignum+"\n" : "\n") );

				strbuf.setName( locus );
				//if( locus == null ) strbuf.setName( spec );
				//else strbuf.setName( locus.contains(spec) ? locus : spec+ "_"+locus );

				if( allout != null ) {
					//if( locus.contains(spec) ) allout.write( ">" + locus + "\n" );
					//else allout.write( ">" + spec + "_" + locus + "\n" );

					//if( locus == null ) strbuf.setName( spec );
					//else strbuf.setName( (locus.contains(spec) ? "" : spec + "_") + locus);

					strbuf.writeSequence( allout );
					/*for( int i = 0; i < strbuf.length(); i+=70 ) {
						allout.write( strbuf.getSubstring( i, Math.min( strbuf.length(), i+70 ), 1 ) + "\n" );
					}*/
				}
				
				strbuf = new Contig();
			}
			filetext.close();
		}
		
		/*Map<String,String> nameMap = new HashMap<>();
		Map<Path,Writer>	urifile = new HashMap<>();
		for( Sequence seq : lseq ) {
			List<Annotation> annset = seq.getAnnotations();
			if( annset != null ) for( Annotation ao : annset ) {
				if( ao.getName() != null ) {
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

						boolean amino = ao.getType().contains("CDS");
						if( ao.id != null && ao.group != null && !ao.id.contains("..") ) nameMap.put(ao.id, ao.group);
						
						String end = amino ? " # " + ao.start + " # " + ao.stop + " # " + ao.ori + " #\n" : "\n";
						if( out != null ) {
							/*if( replace != null ) {
								String rep = replace + ao.spec.substring( ao.spec.indexOf('_') );
								out.write( ">"+ao.id + " " + ao.name + " [" + rep + "]" + end );
							} else {*
							System.err.println(strbuf.getName() + " lebbi " + ao.getName());
							out.write( ">"+ao.id + " " + ao.getName() + " [" + ao.seq.getName() + "]" + end );
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
							}*
							
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
			if( !Files.exists(p) ) {
				Writer mfw = Files.newBufferedWriter(p);
				for (String a : nameMap.keySet()) {
					String gene = nameMap.get(a);
					mfw.write(a + "\t" + gene + "\n");
				}
				mfw.close();
			}
		}
		
		for( Path uri : urifile.keySet() ) {
			Writer w = urifile.get( uri );
			if( w != null ) w.close();
		}
		if( allout != null ) allout.close();*/
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
					filetext.append(line).append("\n");
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
				Map<String,URI>	map = new HashMap<>();
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
