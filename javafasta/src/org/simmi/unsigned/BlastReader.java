package org.simmi.unsigned;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.simmi.shared.Sequence;

public class BlastReader {
	public static void main(String[] args) {
		try {			
			FileReader	fr = new FileReader( args[0] );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			
			Map<String,Sequence>	sequences = new HashMap<String,Sequence>();
			Sequence				qsequence;
			Sequence				ssequence;
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
							qsequence.append( split[2] );
						} else if( line.startsWith("Sbjct") ) {
							split = line.split("[ ]+");
							qsequence.append( split[2] );
						}
					}
					continue;
				}
				line = br.readLine();
			}
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
