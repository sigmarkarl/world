package org.simmi.unsigned;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.simmi.shared.Sequence;
import org.simmi.shared.Serifier;

public class BlastReader {
	public static void main(String[] args) {
		Map<Character,Character>	rc = new HashMap<Character,Character>();
		rc.put('A', 'T');
		rc.put('C', 'G');
		rc.put('G', 'C');
		rc.put('T', 'A');
		rc.put('N', 'N');
		rc.put('a', 't');
		rc.put('c', 'g');
		rc.put('g', 'c');
		rc.put('t', 'a');
		rc.put('n', 'n');
		rc.put('-', '-');
		
		try {
			FileReader	fr = new FileReader( args[0] );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			
			Map<String,Sequence>	sequences = new HashMap<String,Sequence>();
			Sequence				qsequence;
			Sequence				ssequence = null;
			String	qspec = null;
			String	sspec = null;
			while( line != null ) {
				if( line.startsWith("Query=") ) {
					qspec = line.substring(7).trim();
				} else if( line.startsWith(">") ) {
					sspec = line.substring(1).trim();
					if( sequences.containsKey( sspec ) ) {
						ssequence = sequences.get( sspec );
					} else {
						ssequence = new Sequence( sspec, sequences );
						sequences.put( sspec, ssequence );
					}
				} else if( line.startsWith(" Strand") ) {
					qsequence = new Sequence( qspec, null );
					
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
									for( int i = ssequence.getLength(); i < start; i++ ) ssequence.append("-"); 
									for( int i = stop; i < start; i++ ) {
										ssequence.setCharAt( i, rc.get( seq.charAt(i-stop) ) );
									}
								} else {
									for( int i = ssequence.getLength(); i < stop; i++ ) ssequence.append("-");
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
					
					qsequence.start = Math.min(isstart, isstop);
					qsequence.name = qspec+"_"+qstart+"_"+qstop;
					sequences.put(qsequence.name, qsequence);
					continue;
				}
				line = br.readLine();
			}
			fr.close();
			
			Serifier serifier = new Serifier();
			for( String key : sequences.keySet() ) {
				Sequence seq = sequences.get( key );
				serifier.addSequence( seq );
			}
			
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
}
