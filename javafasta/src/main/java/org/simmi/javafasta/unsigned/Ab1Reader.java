package org.simmi.javafasta.unsigned;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
	ByteBuffer		bb;
	
	public Ab1Reader( InputStream is ) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] ba = new byte[1024];
		try {
			int r = is.read( ba );
			while( r != -1 ) {
				baos.write(ba, 0, r);
				r = is.read( ba );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.bb = ByteBuffer.wrap( baos.toByteArray() );
		init();
	}
	
	public Ab1Reader( ByteBuffer bytebuffer ) {
		this.bb = bytebuffer;
		init();
	}
	
	public void init() {
		bb.getInt();
		bb.getShort();
		
		DirEntry firstentry = new DirEntry( bb.getInt(), bb.getInt(), bb.getShort(), bb.getShort(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt() );		
		
		bb.position( firstentry.dataoffset );
		for( int i = 0; i < firstentry.numelements; i++ ) {
			DirEntry direntry = new DirEntry( bb.getInt(), bb.getInt(), bb.getShort(), bb.getShort(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt() );
			ldirentry.add( direntry );
		}
	}
	
	public String getSequence() {
		StringBuilder	seq = new StringBuilder();
		
		boolean start = false;
		for( DirEntry de : ldirentry ) {
			if( de.datasize > 4 ) {
				bb.position( de.dataoffset );
				if( de.eltype == 18 ) {
					byte b = bb.get();
					byte[] bu = new byte[ b ];
					bb.get(bu);
					String str = new String( bu );
					if( str.startsWith("ABI3") ) start = true;
				} else if( de.eltype == 19 ) {
					ByteArrayOutputStream	baos = new ByteArrayOutputStream();
					byte b = bb.get();
					while( b != 0 ) {
						baos.write( b );
						b = bb.get();
					}
					String str = baos.toString();
					if( str.startsWith("ABI3") ) start = true;
				} else {
					if( start && de.elsize == 1 ) {
						for( int i = 0; i < de.numelements; i++ ) {
							seq.append( (char)bb.get() );
						}
						break;
					}
				}
			}
		}
		
		return seq.toString();
	}
	
	public static void main(String[] args) {
		try {
			//File f = new File( "/home/horfrae/peter/5833_Peter Panthermus 2127/B02_a1_O_Peter_a1_.R_A10P1771_2011-04-15.ab1" );
			File f = new File( "/home/horfrae/peter//5833_Peter Panthermus 2127/P24_h12_O_Peter_h12_.R_A10P1771_2011-04-15.ab1" );
			System.err.println( "file size: "+f.length() );
			
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
