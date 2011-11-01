package org.simmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class Ab1Reader {
	public class DirEntry {
		public DirEntry( int name, int number, short eltype, short elsize, int numelements, int datasize, int dataoffset, int datahandle ) {
			this.name = name;
			this.number = number;
			this.eltype = eltype;
			this.elsize = elsize;
			this.numelements = numelements;
			this.datasize = datasize;
			this.dataoffset = dataoffset;
			this.datahandle = datahandle;
		}
		
		int		name;
		int		number;
		short	eltype;
		short	elsize;
		int		numelements;
		int		datasize;
		int		dataoffset;
		int		datahandle;
	}
	
	List<DirEntry> ldirentry = new ArrayList<DirEntry>();
	
	public Ab1Reader( ByteBuffer bb ) {
		int loc = 0;
		
		int k = bb.getInt();
		short s = bb.getShort();
		
		DirEntry firstentry = new DirEntry( bb.getInt(), bb.getInt(), bb.getShort(), bb.getShort(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt() );
		loc = bb.position();
		
		System.err.println( firstentry.dataoffset + "  " + (loc+47) + "  " + firstentry.elsize + "  " + firstentry.eltype );
		
		bb.position( firstentry.dataoffset );
		for( int i = 0; i < firstentry.numelements; i++ ) {
			DirEntry direntry = new DirEntry( bb.getInt(), bb.getInt(), bb.getShort(), bb.getShort(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt() );
			ldirentry.add( direntry );
		}
		
		for( DirEntry de : ldirentry ) {
			bb.position( de.dataoffset );
			if( de.eltype == 18 ) {
				byte b = bb.get();
				byte[] bu = new byte[ b ];
				bb.get(bu);
				String str = new String( bu );
				System.err.println( str );
			} else if( de.eltype == 19 ) {
				
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			File f = new File( "/home/horfrae/peter/5833_Peter Panthermus 2127/B02_a1_O_Peter_a1_.R_A10P1771_2011-04-15.ab1" );
			ByteBuffer bb = ByteBuffer.allocate( (int)f.length() );
			FileInputStream fis = new FileInputStream( f );
			int r = fis.read( bb.array() );
			if( r == bb.limit() ) new Ab1Reader( bb );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
