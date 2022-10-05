package org.simmi.javafasta.shared;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;
import java.util.Set;

public class Gene {
	public Gene(GeneGroup gg, String id, String name) {
		this.name = name;
		//this.tegeval.setGeneGroup(gg);
		this.refid = id;
		this.id = id;
	}

	public Gene() {}
	
	/*public Gene( GeneGroup gg, String id, String name, String origin, String tag ) {
		this( gg, id, name, origin );
		this.tegeval.type = tag;
	}*/
	
	public void getFasta( Appendable w, boolean id ) throws IOException {
		Sequence ps = tegeval.getProteinSequence();
		if(ps!=null) {
			if (id) ps.setName(this.getId()); //w.append(">" + this.getId() + "\n");
			else
				ps.setName(this.tegeval.getName()); //w.append(">" + this.tegeval.name + "\n"); //this.getId() + " " + this.getName() + (this.idstr != null ? " (" + this.idstr + ") [" : " [") + this.tegeval.name + "]" +" # " + this.tegeval.start + " # " + this.tegeval.stop + " # " + this.tegeval.ori + " #" + "\n");

			ps.writeSequence(w);
		}
		/*for (int i = 0; i < ps.length(); i += 70) {
			w.append( ps.substring(i, Math.min(i + 70, ps.length())) + "\n");
		}*/
	}

	public String getHhpred() {
		if (hhblits != null && hhblits.length() > 0) return hhblits;
		else {
			var oh = this.getGeneGroup().genes.stream().map(Annotation::getGene).filter(Objects::nonNull).map(g -> g.hhblits).filter(h -> h != null && !h.isEmpty()).findAny();
			return oh.orElse(null);
		}
	}

	public String getHhpreduni() {
		if (hhblitsuni != null && hhblitsuni.length() > 0) return hhblitsuni;
		else {
			var oh = this.getGeneGroup().genes.stream().map(Annotation::getGene).filter(Objects::nonNull).map(g -> g.hhblitsuni).filter(h -> h != null && !h.isEmpty()).findAny();
			return oh.orElse(null);
		}
	}

	public String getHhpredphrog() {
		if (hhblitsphrog != null && hhblitsphrog.length() > 0) return hhblitsphrog;
		else {
			var oh = this.getGeneGroup().genes.stream().map(Annotation::getGene).filter(Objects::nonNull).map(g -> g.hhblitsphrog).filter(h -> h != null && !h.isEmpty()).findAny();
			return oh.orElse(null);
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
	
	public void writeGeneIdFasta( Writer w ) throws IOException {
		w.write( ">"+id+"\n" );
		w.write( tegeval.getSequence()+"\n" );
	}

	public void writeGeneIdAAFasta( Writer w ) throws IOException {
		w.write( ">"+id+"\n" );
		w.write( tegeval.getProteinSequence().getStringBuilder()+"\n" );
	}
	
	public Contig getContig() {
		return ((Tegeval)tegeval).getContshort();
	}
	
	public void setIdStr( String idstr ) {
		this.idstr = idstr;
	}
	
	public String toString() {
		return getName();
	}
	
	public int getMaxCyc() {
		return ((Tegeval)tegeval).numCys;
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
	
	public GeneGroup getGeneGroup() {
		return tegeval.getGeneGroup();
	}

	public void setGeneGroup(GeneGroup gg) {
		tegeval.setGeneGroup(gg);
	}
	
	public int getGroupIndex() {
		if( tegeval.gg != null ) return tegeval.gg.groupIndex;
		return -10;
	}
	
	public int getGroupCoverage() {
		if( tegeval.gg != null ) return tegeval.gg.getGroupCoverage();
		return -1;
	}
	
	public int getGroupCount() {
		if( tegeval.gg != null ) return tegeval.gg.getGroupCount();
		return -1;
	}
	
	public int getGroupGenCount() {
		if( tegeval.gg != null ) return tegeval.gg.getGroupGeneCount();
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

	public String getDerivedName() {
		return getGeneGroup().getName();
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

	public Annotation getTegeval() {
		return tegeval;
	}

	public String getDesignation() {
		/*if (tegeval == null || tegeval.designation==null) {
			var desmap = getGeneGroup().geneset.getDesignationMap();
			tegeval.designation = getId()!=null ? desmap.getOrDefault(getId(), "") : "";
			return tegeval.designation;
		} else if (tegeval.designation.length()>0) {
			return tegeval.designation;
		}
		return "";*/
		return getGeneGroup().getDesignation();
	}

	public void setTegeval( Annotation tegeval ) {
		/*Teginfo ti;
		if( species == null ) species = new HashMap<String,Teginfo>();
		if( species.containsKey( tegeval.teg ) ) {
			ti = species.get( tegeval.teg );
		} else {
			ti = new Teginfo();
			species.put( tegeval.teg, ti );
		}*/
		if(this.tegeval != tegeval) {
			this.tegeval = tegeval;
			this.tegeval.setGene(this);
		}
		//if( teginfo == null ) teginfo = new Teginfo();
		//teginfo.add( tegeval );
	}

	public String parseSpecies(String lname) {
		int i = lname.lastIndexOf('[');
		if( i == -1 ) {
			i = Sequence.parseSpec( lname );
			
			if( i <= 0 ) {
				return tegeval.getSpecies();
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
				n = lname.length();
			}
			/*if( i+1 > n || n > lname.length() ) {
				System.err.println();
			}*/
			String contigstr = lname.substring(i+1, n);
			int u = lname.indexOf(' ');
			id = lname.substring(0, u);
			
			String spec = lname.substring(i+1, n);
			if( id.contains("..") ) {
				id = spec + "_" + id;
			}

			if( u < i ) {
				name = lname.substring(u + 1, i).trim();
			}
			
			u = Contig.specCheck( contigstr );
			
			String origin;
			if( u == -1 ) {
				u = Sequence.parseSpec( contigstr );
				if( u > 0 ) {
					origin = contigstr.substring(0, u - 1);
				} else {
					u = contigstr.indexOf('_');
					if( u == -1 ) u = contigstr.length();
					origin = contigstr.substring(0, u);
				}
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
			return origin;
		}
	}
	
	public String getSpecies() {
		if( tegeval.getSpecies() == null ) {
			String species = parseSpecies( tegeval.getName() );
			if( species == null ) {
				if( tegeval.getSeq() == null ) {
					System.err.println( tegeval.getName() );
				} else {
					species = tegeval.getSeq().getSpec();
				}
				
				/*if( species == null || species.length() < 4 ) {
					System.err.println("hey!!");
				}*/
			}
			return species;
		}
		return tegeval.getSpecies();
	}

	ShareNum noShareNum = new ShareNum(-1,-1);
	public ShareNum getSharingNumber() {
		return tegeval.gg==null ? noShareNum : tegeval.gg.getSharingNumber();
	}

	public void setRefid(String refid) {
		this.refid = refid;
	}

	public String getRefid() {
		return refid;
	}

	public void setPhrog(String phrog) {
		hhblitsphrog = phrog;
	}

	public String getPhrog() {
		return hhblitsphrog;
	}

	public String name;
	public String symbol;
	//String tag;
	//String origin;
	public String id;
	private String refid;
	public String idstr;
	public Cog cog;
	public Cog pfam;
	public String cazy;
	public String dbcan;
	public String phaster;
	public String hhblits;
	public String hhblitsuni;
	private String hhblitsphrog;
	public String pfamid;
	public Set<String> allids;
	public String genid;
	public String uniid;
	public String keggid;
	public String keggpathway;
	public String pdbid;
	public String koid;
	public String goid;
	public String koname;
	public String ecid;
	public String blastspec;
	public Set<Function> funcentries;
	//Map<String, Teginfo> species;
	private Annotation tegeval;
	private String aac;
	public int index;
	public boolean signalp = false;
	public boolean transm = false;
	// Set<String> group;
	//int groupGenCount;
	//int groupCoverage;
	//int groupIdx;
	//int groupCount;
	public double corr16s;
	public double[] corrarr;

	public double proximityGroupPreservation;
};
