package org.simmi.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
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
		String			id;
		String			spec;
		String 			type;
		StringBuilder	contig;
		int				start;
		int				stop;
		boolean 		comp;
	};
	
	public static void handleText( String filename, Map<String,StringBuilder> filetextmap, Map<String,URI> annoset, Writer allout ) throws IOException {
		List<Anno>	annolist = new ArrayList<Anno>();
		for( String tag : filetextmap.keySet() ) {
			StringBuilder filetext = filetextmap.get( tag );
			String locus = null;
			int ind = filetext.indexOf("\n");
			String line = null;
			if( ind > 0 ) {
				line = filetext.substring(0, ind);
			}
			
			Anno		anno = null;
			
			//int k = filename.indexOf('.');
			//if( k == -1 ) k = filename.length();
			String spec = tag; //filename.substring(0, k);
			
			Set<String>	xref = new TreeSet<String>();
			//int contignum = 0;
			StringBuilder	strbuf = new StringBuilder();		
			while( line!= null ) {
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
							if( anno.id == null || anno.id.length() == 0 ) anno.id = anno.comp ? "comp("+anno.start+".."+anno.stop+")" : anno.start+".."+anno.stop;
							annolist.add( anno );
						}
						anno = null;
					}
					if( banno != null ) { //|| trimline.startsWith("gene ") ) {
						anno = new Anno( banno );
						anno.contig = strbuf;
						
						//anno.spec = spec + (contignum > 0 ? "_contig"+(contignum+1) : "");
						anno.spec = spec + "_"+locus;
						
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
									anno.comp = true;
								} else {
									System.err.println( nsplit[0] + " n " + nsplit[nsplit.length-1] );
									anno = null;
								}
							} else {
								String[] nsplit = split[1].split("\\.\\.");
								if( nsplit.length > 1 ) {
									char c = nsplit[0].charAt(0);
									char c2 = nsplit[1].charAt(0);
									if( c >= '0' && c <= '9' && c2 >= '0' && c2 <= '9' ) {
										anno.start = Integer.parseInt( nsplit[0] );
										anno.stop = Integer.parseInt( nsplit[1] );
										anno.comp = false;
									} else {
										System.err.println( nsplit[0] + " n " + nsplit[1] );
										anno = null;
									}
								} else {
									System.err.println("nono2");
								}
							}
						} else {
							System.err.println("nono");
						}
					} else if( trimline.startsWith("/db_xref") ) {
						xref.add( trimline.substring(10, trimline.length()-1) );
					} else if( trimline.startsWith("/product") ) {
						if( anno != null ) {
							if( trimline.length() > 10 ) {								
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
								if( xref.size() > 0 ) {
									anno.name += "(";
									for( String xr : xref ) {
										anno.name += xr;
									}
									anno.name += ")";
									xref.clear();
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
							if( anno.id == null || anno.id.length() == 0 ) anno.id = anno.comp ? "comp("+anno.start+".."+anno.stop+")" : anno.start+".."+anno.stop;
							annolist.add( anno );
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
				allout.write( ">" + spec + "_" + locus + "\n" );
				for( int i = 0; i < strbuf.length(); i+=70 ) {
					allout.write( strbuf.substring(i, Math.min( strbuf.length(), i+70) ) + "\n" );
				}
				
				strbuf = new StringBuilder();
			}
		}
			
		Map<URI,Writer>	urifile = new HashMap<URI,Writer>();
		for( Anno ao : annolist ) {
			StringBuilder	strbuf = ao.contig;
			URI uri = annoset.get( ao.getType() );
			
			Writer out;
			if( !urifile.containsKey( uri ) ) {
				Writer fw = null;// = new FileWriter( new File( uri ) );
				urifile.put( uri, fw );
				
				out = fw;
			} else {
				out = urifile.get( uri );
			}
			
			/*if( out == null ) {
				System.err.println();
			}*/
			
			boolean amino = ao.getType().contains("CDS");
			
			String end = amino ? " # " + ao.start + " # " + ao.stop + " # " + (ao.comp ? "-1" : "1") + " #\n" : "\n";
			if( out != null ) {
				out.write( ">"+ao.id + " " + ao.name + " [" + ao.spec + "]" + end );
			} else {
				System.err.println();
			}
			//strbuf.
			
			//System.err.println(val);
			//String	ami = "";
			
			int t = 0;
			if( amino ) {
				String 	val = strbuf.substring( Math.max(0, ao.start-1), ao.stop );
				if( ao.comp ) {
					for( int i = val.length()-3; i >= 0; i-=3 ) {
						//ami += 
						String first = val.substring(i, i+3).toUpperCase();
						String second = Sequence.revcom.get( first );
						String str = Sequence.amimap.get( second );
						if( str != null ) {
							if( str.equals("0") || str.equals("1") ) break;
							else if( out != null ) out.write( str );//+ " " + t + " " );
							if( (++t % 60) == 0 && out != null ) out.write("\n");
						}
					}
				} else {
					for( int i = 0; i < val.length(); i+=3 ) {
						//ami += 
						String first = val.substring( i, Math.min(val.length(), i+3) ).toUpperCase();
						String str = Sequence.amimap.get( first );
						if( str != null ) {
							if( str.equals("0") || str.equals("1") ) break;
							else if( out != null ) out.write( str );//+ " " + t + " " );
							if( (++t % 60) == 0 && out != null ) out.write("\n");
						}
					}
				}
			} else {
				if( ao.comp ) {
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
						String str = strbuf.substring( start, stop );
						if( out != null ) out.write( str.toUpperCase() + (str.length() == 60 ? "\n" : "") );
					}
				}
			}
			if( out != null ) out.write("\n");
			//if( c++ > 10 ) break;
		}
		
		for( URI uri : urifile.keySet() ) {
			Writer w = urifile.get( uri );
			if( w != null ) w.close();
		}
		allout.close();
			//String encoded = encode( sb.toString() );
			//String dataurl = "data:text/plain;base64,"+encoded;
			//console( "dataurl length: "+dataurl.length() );
			//ta.setText( sb.toString() );
			//Window.Location.assign( dataurl );
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
