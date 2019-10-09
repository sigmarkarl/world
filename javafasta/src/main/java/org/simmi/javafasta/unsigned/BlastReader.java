package org.simmi.javafasta.unsigned;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.simmi.javafasta.shared.Sequence;
import org.simmi.javafasta.shared.Serifier;

public class BlastReader {
	public static void main(String[] args) {		
		try {
			FileReader	fr = new FileReader( args[0] );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			
			Map<String,Sequence>		sequences = new HashMap<String,Sequence>();
			Map<String,Sequence>		qsequences = new HashMap<String,Sequence>();
			List<Sequence>				lqsequence = new ArrayList<Sequence>();
			//List<Sequence>			lqsequence = new ArrayList<Sequence>();
			SequenceNode				qsequence;
			SequenceNode				ssequence = null;
			String	qspec = null;
			String	sspec = null;
			while( line != null ) {
				if( line.startsWith("Query=") ) {
					qspec = line.substring(7).trim();
				} else if( line.startsWith(">") ) {
					sspec = line.substring(1).trim();
					if( sequences.containsKey( sspec ) ) {
						ssequence = (SequenceNode)sequences.get( sspec );
					} else {
						ssequence = new SequenceNode( sspec, sequences, -1, -1 );
						sequences.put( sspec, ssequence );
					}
				} else if( line.startsWith(" Strand") && sspec.equals("Thermus_scotoductus_SA_01_uid62273_NC_014974") ) {
					qsequence = new SequenceNode( qspec, null, -1, -1 );
					
					String 	qstart = null;
					String	qstop = null;
					String 	sstart = null;
					String	sstop = null;
					
					String[] split;
					line = br.readLine();
					while( line != null && !line.startsWith(" Strand") && !line.startsWith("Query=") && !line.startsWith(">") ) {
						if( line.startsWith("Query") ) {
							split = line.split("[ ]+");
							if( qstart == null ) qstart = split[1];
							qstop = split[ split.length-1 ];
							qsequence.append( split[2] );
						} else if( line.startsWith("Sbjct") ) {
							split = line.split("[ ]+");
							if( sstart == null ) sstart = split[1];
							sstop = split[ split.length-1 ];
							
							if( ssequence != null ) {
								String seq = split[2];
								int start = Integer.parseInt( split[1] );
								int stop = Integer.parseInt( sstop );
								if( start > stop ) {
									for( int i = ssequence.length(); i < start; i++ ) ssequence.append("-"); 
									for( int i = stop; i < start; i++ ) {
										ssequence.setCharAt( i, Sequence.rc.get( seq.charAt(i-stop) ) );
									}
								} else {
									for( int i = ssequence.length(); i < stop; i++ ) ssequence.append("-");
									for( int i = start; i < stop; i++ ) {
										ssequence.setCharAt( i,  seq.charAt(i-start) );
									}
								}
							}
						}
						line = br.readLine();
					}
					int isstart = Integer.parseInt( sstart );
					int isstop = Integer.parseInt( sstop );
					
					qsequence.refstart = Math.min(isstart, isstop);
					qsequence.refstop = Math.max(isstart, isstop);
					
					qsequence.offset = Math.min(isstart, isstop);
					qsequence.setName( qspec+"_"+sstart+"_"+sstop );
					//lqsequence.add( qsequence );
					if( qsequences.containsKey( qsequence.getName() ) ) {
						System.err.println( qsequence.getName() );
						System.err.println();
					}
					qsequences.put(qsequence.getName(), qsequence);
					lqsequence.add( qsequence );
					continue;
				}
				line = br.readLine();
			}
			fr.close();
			
			System.err.println( qsequences.size() + "  " + lqsequence.size() );
			Serifier serifier = new Serifier();
			for( String key : qsequences.keySet() ) {
				Sequence seq = qsequences.get( key );
				serifier.addSequence( seq );
			}
			
			Set<Sequence> remset = new HashSet<Sequence>();
			for( Sequence seq : serifier.lseq ) {
				if( seq.length() < 100 ) remset.add( seq );
			}
			serifier.lseq.removeAll( remset );
			
			Collections.sort( serifier.lseq );
			System.err.println( serifier.lseq.size() );
			serifier.lseq = serifier.lseq.subList(0, 150);
			SequenceNode lastseq = (SequenceNode)serifier.lseq.get( serifier.lseq.size()-1 );
			System.err.println( lastseq.refstart + "  " + lastseq.refstop );
			
			SequenceNode startNode = new SequenceNode( null, null, -100, -100 );
			serifier.lseq.add(0, startNode);
			for( int i = 0; i < serifier.lseq.size(); i++ ) {
				SequenceNode	seqn = (SequenceNode)serifier.lseq.get(i);
				int k = i+1;
				while( k < serifier.lseq.size() ) {
					SequenceNode	nseqn = (SequenceNode)serifier.lseq.get(k);
					if( nseqn.refstart > seqn.refstop-10 ) break;
					k++;
				}
				if( k < serifier.lseq.size() ) {
					SequenceNode	nseqn = (SequenceNode)serifier.lseq.get(k);
					
					/*if( !touched.add( nseqn ) ) {
						System.err.println( serifier.lseq.indexOf( nseqn ) );
						System.err.println();
					}*/
					seqn.connections.add( nseqn );
					
					int u = k+1;
					while( u < serifier.lseq.size() ) {
						SequenceNode	nnseqn = (SequenceNode)serifier.lseq.get(u);
						if( nnseqn.refstart > nseqn.refstop-10 ) break;
						
						/*if( !touched.add( nnseqn ) ) {
							System.err.println();
						}*/
						seqn.connections.add( nnseqn );
						
						u++;
					}
				}
			}
			
			numPaths( startNode, "", 0 );
			
			//int m = 0;
			System.err.println( bestpath );
			
			String[] 		split = bestpath.split("\\+");
			Set<String> 	bestset = new HashSet<String>( Arrays.asList(split) );
			Set<Sequence> 	seq = new HashSet<Sequence>();
			for( Sequence s : serifier.lseq ) {
				if( bestset.contains(s.getName()) ) {
					seq.add( s );
				}
			}
			serifier.lseq.retainAll( seq );
			
			/*for( String path : pathstrset ) {
				System.err.println( path );
			}*/
			
			JFrame frame = new JFrame();
			frame.setSize(800, 600);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			JavaFasta jf = new JavaFasta( null, serifier, null );
			jf.initGui(frame);
			jf.updateView();

			frame.setVisible(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Set<SequenceNode>	touched = new HashSet<SequenceNode>();
	public static int paths = 0;
	public static int maxcov = 0;
	public static String bestpath;
	public static Set<String>	pathstrset = new HashSet<String>();
	public static void numPaths( SequenceNode seqn, String rest, int coverage ) {
		if( seqn.connections.isEmpty() ) {
			if( !pathstrset.add( rest ) ) {
				//System.err.println( rest );
				//System.err.println();
			}// else System.err.println( pathstrset.size() );
			//System.err.println( seqn.getName() + "  " + paths );
			if( coverage > maxcov ) {
				maxcov = coverage;
				bestpath = rest;
			}
			paths++;
		}
		
		/*if( seqn.connections.contains(seqn) ) {
			System.err.println();
		}
		
		if( !touched.add( seqn ) ) {
			System.err.println();
		}*/
		
		for( SequenceNode sn : seqn.connections ) {
			numPaths( sn, rest+"+"+sn, coverage+(sn.refstop-sn.refstart) );
		}
	}
}
