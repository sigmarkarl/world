package org.simmi.shared;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import org.simmi.shared.Contig;

public class Gene {
	public Gene(GeneGroup gg, String id, String name, String origin) {
		//super();
		
		/*if( origin != null && origin.length() == 2 ) {
			System.err.println();
		}*/
		
		this.name = name;
		if( origin == null || origin.length() < 4 ) {
			System.err.println();
		}
		this.species = origin;
		this.gg = gg;
		this.refid = id;
		this.id = id;
		// this.setAa( aa );
		
		//groupIdx = -10;
	}
	
	/*public Gene( GeneGroup gg, String id, String name, String origin, String tag ) {
		this( gg, id, name, origin );
		this.tegeval.type = tag;
	}*/
	
	public void getFasta( Appendable w, boolean id ) throws IOException {
		StringBuilder ps = tegeval.getProteinSequence();
		if( id ) w.append(">" + this.getId() + "\n");
		else w.append(">" + this.tegeval.name + "\n"); //this.getId() + " " + this.getName() + (this.idstr != null ? " (" + this.idstr + ") [" : " [") + this.tegeval.name + "]" +" # " + this.tegeval.start + " # " + this.tegeval.stop + " # " + this.tegeval.ori + " #" + "\n");
		for (int i = 0; i < ps.length(); i += 70) {
			w.append( ps.substring(i, Math.min(i + 70, ps.length())) + "\n");
		}
	}
	
	public String getFasta( boolean id ) {
		StringWriter sb = new StringWriter();
		try {
			getFasta( sb, id );
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public Contig getContig() {
		return tegeval.getContshort();
	}
	
	public void setIdStr( String idstr ) {
		this.idstr = idstr;
	}
	
	public String toString() {
		return getName();
	}
	
	public int getMaxCyc() {
		return tegeval.numCys;
	}
	
	public int getMaxLength() {
		return tegeval.getProteinLength();
	}
	
	public String getTag() {
		return tegeval.type;
	}

	public void setAa(String aa) {
		if (aa != null) {
			this.aac = aa;
		}
	}

	public String getAa() {
		return aac;
	}
	
	public void setGeneGroup( GeneGroup gg ) {
		this.gg = gg;
		
		gg.addGene( this );
		//gg.addSpecies( this.species );	
	}
	
	public GeneGroup getGeneGroup() {
		return gg;
	}
	
	public int getGroupIndex() {
		if( gg != null ) return gg.groupIndex;
		return -10;
	}
	
	public int getGroupCoverage() {
		if( gg != null ) return gg.getGroupCoverage();
		return -1;
	}
	
	public int getGroupCount() {
		if( gg != null ) return gg.getGroupCount();
		return -1;
	}
	
	public int getGroupGenCount() {
		if( gg != null ) return gg.getGroupGeneCount();
		return -1;
	}
	
	public boolean getSignalP() {
		return signalp;
	}
	
	public boolean getTransM() {
		return transm;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLongName() {
		//String longname = this.getId() + " " + this.getName() + (this.idstr != null ? " (" + this.idstr + ") [" : " [") + this.tegeval.name + "]" +" # " + this.tegeval.start + " # " + this.tegeval.stop + " # " + this.tegeval.ori;
		String longname = this.getId() + " " + this.getName() + (this.idstr != null ? "(" + this.idstr + ") [" : " [") + this.getContig().getName() + "]" +" # " + this.tegeval.start + " # " + this.tegeval.stop + " # " + this.tegeval.ori;
		
		/*if( longname.split("#").length > 6 ) {
			System.err.println();
		}*/
		return longname;
	}
	
	public String getId() {
		return id;
	}
	
	public double getGCPerc() {
		return tegeval.getGCPerc();
	}
	
	public void setTegeval( Tegeval tegeval ) {
		/*Teginfo ti;
		if( species == null ) species = new HashMap<String,Teginfo>();
		if( species.containsKey( tegeval.teg ) ) {
			ti = species.get( tegeval.teg );
		} else {
			ti = new Teginfo();
			species.put( tegeval.teg, ti );
		}*/
		this.tegeval = tegeval;
		//if( teginfo == null ) teginfo = new Teginfo();
		//teginfo.add( tegeval );
	}
	
	public String parseSpecies( String lname ) {
		int i = lname.lastIndexOf('[');
		if( i == -1 ) {
			i = Sequence.parseSpec( lname );
			
			if( i == -1 ) {
				return null;
			}
			//int u = lname.lastIndexOf('_');
			//contigstr = lname.substring(0, u);
			return lname.substring(0, i-1);
			//contloc = lname.substring(i, lname.length());
			//name = lname;
			//id = lname;
		} else {
			int n = lname.indexOf(']', i+1);
			if( n < 0 || n > lname.length() ) {
				System.err.println();
			}
			String contigstr = lname.substring(i+1, n);
			int u = lname.indexOf(' ');
			id = lname.substring(0, u);
			
			String spec = lname.substring(i+1, n);
			if( id.contains("..") ) {
				id = spec + "_" + id;
			}
			
			name = lname.substring(u+1, i).trim();
			
			u = Contig.specCheck( contigstr );
			
			String origin;
			if( u == -1 ) {
				u = Serifier.contigIndex( contigstr );
				origin = contigstr.substring(0, u-1);
				//contloc = contigstr.substring(u, contigstr.length());
			} else {
				n = contigstr.indexOf("_", u+1);
				if( n == -1 ) n = contigstr.length();
				origin = contigstr.substring(0, n);
				//contloc = n < contigstr.length() ? contigstr.substring(n+1) : "";
			}
			
			/*if( line != null ) {
				i = line.lastIndexOf('#');
				if( i != -1 ) {
					u = line.indexOf(';', i+1);
					if( u != -1 ) {
						id = line.substring(u+1, line.length());
						mu.add( id );
					}
				}
			}*/
			
			if( origin == null || origin.length() < 4 ) {
				System.err.println();
			}
			return origin;
		}
	}
	
	public String getSpecies() {
		if( species == null ) {
			species = parseSpecies( tegeval.name );
			if( species == null || species.length() < 4 ) {	
				System.err.println();
			}
			if( species == null ) {
				if( tegeval.seq == null ) {
					System.err.println( tegeval.name );
				}
				
				species = tegeval.seq.getSpec();
				
				if( species == null || species.length() < 4 ) {	
					System.err.println();
				}
			}
			/*if( species.length() == 2 ) {
				System.err.println();
			}*/
			
			if( tegeval.teg == null ) {
				tegeval.teg = species;
			}
		}
		if( species == null || species.length() < 4 ) {	
			System.err.println();
		}
		return species;
	}

	public String name;
	public String symbol;
	//String tag;
	//String origin;
	public String id;
	public String refid;
	public String idstr;
	public Cog cog;
	public Set<String> allids;
	public String genid;
	public String uniid;
	public String keggid;
	public String pdbid;
	public String koid;
	public String koname;
	public String ecid;
	public String blastspec;
	public Set<Function> funcentries;
	//Map<String, Teginfo> species;
	private String 	species;
	public Tegeval tegeval;
	private String aac;
	public int index;
	public boolean signalp = false;
	public boolean transm = false;

	GeneGroup	gg;
	// Set<String> group;
	//int groupGenCount;
	//int groupCoverage;
	//int groupIdx;
	//int groupCount;
	public double corr16s;
	public double[] corrarr;

	public double proximityGroupPreservation;
};
