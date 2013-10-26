package org.simmi.shared;


public class Sequences implements Comparable<Sequences> {
	public Sequences( String user, String name, String type, String path, int nseq ) {
		this.user = user;
		this.name = name;
		this.type = type;
		this.path = path;
		this.nseq = nseq;
	}
	
	public void setKey( String key ) {
		_key = key;
	}
	
	public String getKey() {
		return _key;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getNSeq() {
		return nseq;
	}
	
	public void setNSeq( int count ) {
		nseq = count;
	}
	
	public String user;
	public String name;
	public String type;
	public String path;
	public Integer nseq;
	public String _key;
	
	@Override
	public int compareTo(Sequences o) {
		return name.compareTo( o.name );
	}
};
