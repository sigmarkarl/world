package org.simmi.javafasta.shared;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class GeneGroup {
	GenomeSet						geneset;
	public Set<Annotation>          genes = new HashSet<>();
	public Map<String, Teginfo>  	species = new TreeMap<>();
	public int                	 	groupIndex;
	int                 			groupCount = -1;
	public int						index;
	Map<Set<String>, ShareNum> 		specset;
	//int			groupGeneCount;
	Map<String,String> 				ko2name;
	Map<String,Cog>					cogmap;
	Map<String,Cog>					pfammap;
	Map<String,Set<String>>			biosystemsmap;
	BooleanProperty selected = new SimpleBooleanProperty();

	/*Map<String,String>				cazyaamap;
	Map<String,String>				cazycemap;
	Map<String,String>				cazyghmap;
	Map<String,String>				cazygtmap;
	Map<String,String>				cazyplmap;

	public void setCazyAAMap( Map<String,String> cazyaamap ) {
		this.cazyaamap = cazyaamap;
	}

	public void setCazyCEMap( Map<String,String> cazycemap ) {
		this.cazycemap = cazycemap;
	}

	public void setCazyGHMap( Map<String,String> cazyghmap ) {
		this.cazyghmap = cazyghmap;
	}

	public void setCazyGTMap( Map<String,String> cazygtmap ) {
		this.cazygtmap = cazygtmap;
	}

	public void setCazyPLMap( Map<String,String> cazyplmap ) {
		this.cazyplmap = cazyplmap;
	}*/

	public boolean isSelected() {
		return selected.get();
	}

	public BooleanProperty selectedProperty() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}

	public String toString() {
		return this.getName() + " " + genes.size() + "  " + this.getMaxLength();
	}
	
	public void setSpecSet( Map<Set<String>,ShareNum> specset ) {
		this.specset = specset;
	}

	public Map<Set<String>,ShareNum> getSpecSet() {
		return specset;
	}
	
	public boolean containsDirty() {
		for( Annotation a : genes ) {
			if( a.isDirty() ) return true;
		}
		return false;
	}
	
	public String getFasta( boolean id ) throws IOException {
		StringWriter sb = new StringWriter();
		for( Annotation a : genes ) {
			a.getGene().getFasta( sb, id );
		}
		return sb.toString();
	}
	
	public void getFasta( Writer w, boolean id ) throws IOException {
		for( Annotation a : genes ) {
			a.getGene().getFasta( w, id );
		}
	}
	
	public int getMaxCyc() {
		int max = -1;
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.getMaxCyc() > max ) max = g.getMaxCyc();
		}
		return max;
	}
	
	public int getMaxLength() {
		int max = -1;
		for( Annotation a : genes ) {
			if( a.getProteinLength() > max ) max = a.getProteinLength();
		}
		return max;
	}
	
	public Annotation getLongestSequence() {
		int max = 0;
		Annotation seltv = null;
		for( Annotation a : genes ) {
			int unalen = a.getAlignedSequence().getUnalignedLength();
			if( unalen > max ) {
				seltv = a;
				max = unalen;
			}
		}
		return seltv;
	}
	
	public Teginfo getTes( String spec ) {
		return species.get( spec );
	}
	
	public List<Annotation> getTegevals( Set<String> sortspecies ) {
		List<Annotation>	ltv = new ArrayList();
		
		for( String sp : sortspecies )
		/*for( Gene g : genes ) {
			Teginfo stv = g.species.get(sp);
			if( stv == null ) {
				//System.err.println( sp );
			} else {
				for (Tegeval tv : stv.tset) {
					ltv.add( tv );
				}
			}
		}*/
			ltv.addAll( getTegevals( sp ) );
		
		return ltv;
	}
	
	public List<Annotation> getTegevals( String specs ) {
		List<Annotation>	ltv = new ArrayList();
		
		Teginfo genes = species.get( specs );
		if( genes != null ) ltv.addAll(genes.tset);
		
		return ltv;
	}
	
	public List<Annotation> getTegevals() {
		List<Annotation>	ltv = new ArrayList();
		ltv.addAll(genes);
		return ltv;
	}
	
	public void setIndex( int i ) {
		this.index = i;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getAvgGCPerc() {
		double gc = 0.0;
		int count = 0;
		for( Annotation a : genes ) {
			gc += a.getGCPerc();
			count++;
		}
		return gc/count;
	}

	public double getAvggcp() {
		return getAvgGCPerc();
	}
	
	public double getStddevGCPerc( double avggc ) {
		double gc = 0.0;
		int count = 0;
		for( Annotation a : genes ) {
			double val = a.getGCPerc()-avggc;
			gc += val*val;
			count++;
		}
		return Math.sqrt(gc/count);
	}
	
	public Set<Function> getFunctions() {
		Set<Function>	funcset = new HashSet();
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.funcentries != null && g.funcentries.size() > 0 ) {
				//Function f = funcmap.get( go );
				funcset.addAll(g.funcentries);
			}
		}
		return funcset;
	}
	
	public String getCommonGO( boolean breakb, boolean withinfo, Set<Function> allowedFunctions ) {
		String ret = "";
		Set<String> already = new HashSet<>();
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g.funcentries != null && g.funcentries.size() > 0 ) {
				for( Function f : g.funcentries ) {
					//Function f = funcmap.get( go );
					
					if( allowedFunctions == null || allowedFunctions.contains(f) ) {
						String name = f.getGo(); //getName().replace('/', '-').replace(",", "");
						if( withinfo && f.getName() != null ) name += "-"+f.getName().replace(",", "");
							
						//System.err.println( g.getName() + "  " + go );
						if( ret.length() == 0 ) ret = name;
						else if( !already.contains(name) ) ret += ","+name;
						
						already.add( name );
					}
				}
				if( breakb ) break;
			}
		}
		return ret;
	}
	
	public String getCommonFunction( boolean breakb, Set<Function> allowedFunctions ) {
		StringBuilder ret = new StringBuilder();
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g.funcentries != null && g.funcentries.size() > 0 ) {
				for( Function f : g.funcentries ) {
					//Function f = funcmap.get( go );
					
					if( allowedFunctions == null || allowedFunctions.contains(f) ) {
						String name = f.getName().replace('/', '-').replace(",", "");
							
						//System.err.println( g.getName() + "  " + go );
						if( ret.length() == 0 ) ret.append(name);
						else ret.append(",").append(name);
					}
				}
				if( breakb ) break;
			}
		}
		return ret.toString();
	}
	
	public boolean isOnAnyPlasmid() {
		for( Annotation a : genes ) {
			Contig ctg = a.getContshort();
			if( ctg!=null && ctg.isPlasmid() ) return true;
		}
		
		return false;
	}
	
	public boolean isInAnyPhage() {
		for( Annotation a : genes ) {
			if( a.isPhage() ) return true;
		}
		
		return false;
	}
	
	public String getCommonNamespace() {
		StringBuilder ret = new StringBuilder();
		Set<String>	included = new HashSet<>();
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g.funcentries != null ) for( Function f : g.funcentries ) {
				//Function f = funcmap.get( go );
				String namespace = f.getNamespace();
				//System.err.println( g.getName() + "  " + go );
				if( !included.contains(namespace) ) {
					if( ret.length() == 0 ) ret.append(namespace);
					else ret.append(",").append(namespace);
					included.add(namespace);
				}
			}
		}
		return ret.toString();
	}
	
	public String getOrigin() {
		String ret = null;
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if(g != null) {
				ret = a.getGene().getSpecies();
				break;
			}
		}
		
		return ret;
	}
	
	public String getCommonTag() {
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if(g != null) {
				String tag = g.getTag();
				if (tag != null) return tag;
			}
		}
		return null;
	}
	
	public String getCommonId() {
		String ret = null;
		for( Annotation a : genes ) {
			String id = a.id;
			if( ret == null ) ret = id;
			else {
				boolean jsome = (ret.startsWith("J") || ret.startsWith("A") || ret.startsWith("L") || ret.startsWith("B")) && ret.charAt(4) == '0';
				boolean isome = id != null && (id.startsWith("J") || id.startsWith("A") || id.startsWith("L") || id.startsWith("B")) && id.charAt(4) == '0';
				if( ((jsome || ret.contains("contig") || ret.contains("scaffold") || ret.contains("uid")) && !ret.contains(":")) || 
						(id != null && !(isome || id.contains("contig") || id.contains("scaffold") || id.contains("uid") || id.contains("unnamed") || id.contains("hypot")) )) ret = id;
			}
		}
		return ret;
	}
	
	public String getName() {
		String ret = "";
		if(genes.size() > 0) {
			for (Annotation a : genes) {
				String name = a.getName();
				if (ret.length() == 0) ret = name;
				else {
					boolean jsome = (ret.startsWith("J") || ret.startsWith("A") || ret.startsWith("L") || ret.startsWith("B")) && (ret.length() > 4 && ret.charAt(4) == '0');
					boolean nsome = (name.startsWith("J") || name.startsWith("A") || name.startsWith("L") || name.startsWith("B")) && (name.length() > 4 && name.charAt(4) == '0');

					if ((
							(jsome || ret.startsWith("Consensus") || ret.contains("plasmid") || ret.contains("chromosome") || ret.contains("contig") || ret.contains("scaffold") || ret.contains("uid")) && !ret.contains(":")
					) ||
							!(nsome || name.contains("Consensus") || name.contains("plasmid") || name.contains("chromosome") || name.contains("contig") || name.contains("scaffold") || name.contains("uid") || name.contains("unnamed") || (ret.startsWith("Consensus") && name.contains("hypot"))))
						ret = name;
				}
			}
			int k = ret.lastIndexOf('(');
			if (k != -1) {
				ret = ret.substring(0, k);
			}

			String genename = ret;
			if (genename.contains("CRISPR")) {
				k = genename.indexOf('(');
				if (k == -1) k = genename.length();
				genename = genename.substring(0, k);
				genename = genename.replace("CRISPR-associated", "");
				genename = genename.replace("CRISPR", "");
				genename = genename.replace("helicase", "");
				genename = genename.replace("endonuclease", "");
				genename = genename.replace("Cas3-HD", "");
				genename = genename.replace("/", "");
				genename = genename.replace(",", "");
				genename = genename.replace("type I-E", "");
				genename = genename.replace("ECOLI-associated", "");
				genename = genename.replace("family", "");
				genename = genename.replace("protein", "");
				genename = genename.replace("RAMP", "");
				genename = genename.trim();
				ret = genename;
			}
		
		/*if( ret == null || ret.length() == 0 ) {
			System.err.println();
			
			for( Gene g : genes ) {
				String name = g.getName();
				if( ret == null ) ret = name;
				else if( (ret.contains("contig") || ret.contains("scaffold")) || !(name.contains("contig") || name.contains("scaffold") || name.contains("unnamed") || name.contains("hypot")) ) ret = name;
			}
		}*/
		} else {
			ret = getTegevals().stream().map(Annotation::getName).collect(Collectors.joining(","));
		}
		
		return ret;
	}
	
	public Cog getCog( Map<String,Cog> cogmap ) {
		for( Annotation a : genes ) {
			if( cogmap.containsKey( a.id ) ) {
				return cogmap.get( a.id );
			}
		}
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.cog != null ) return g.cog;
		}
		return null;
	}
	
	public String getCogname() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.name : null;
	}
	
	public String getCog() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.id : null;
	}
	
	public String getCoganno() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.annotation : null;
	}
	
	public String getCogsymbol() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.genesymbol : null;
	}

	public String getOldsymbol() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.cogsymbol : null;
	}

	public Cog getPfam( Map<String,Cog> pfammap ) {
		for( Annotation a : genes ) {
			if( pfammap.containsKey( a.id ) ) return pfammap.get( a.id );
		}
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null & g.cog != null ) return g.cog;
		}
		return null;
	}

	public String getPfamname() {
		Cog pfam = getPfam( pfammap );
		return pfam != null ? pfam.name : null;
	}

	public String getPfam() {
		Cog pfam = getCog( pfammap );
		return pfam != null ? pfam.id : null;
	}

	public String getPfamanno() {
		Cog pfam = getCog( pfammap );
		return pfam != null ? pfam.annotation : null;
	}

	public String getPfamsymbol() {
		Cog pfam = getPfam( pfammap );
		return pfam != null ? pfam.genesymbol : null;
	}
	
	public int getPresentin() {
		return getSpecies().size();
	}
	
	public String getCommonCazy( Map<String,String> cazymap ) {
		for( Annotation a : genes ) {
			if( cazymap.containsKey( a.id ) ) return cazymap.get( a.id );
		}
		return null;
	}

	public String getCazyAA() {
		Map<String,String> cazyaamap = geneset.getCazyAAMap();
		for( Annotation a : genes ) {
			if( cazyaamap.containsKey( a.id ) ) return cazyaamap.get( a.id );
		}
		return null;
	}

	public String getCazyCE() {
		Map<String,String> cazycemap = geneset.getCazyCEMap();
		for( Annotation a : genes ) {
			if( cazycemap.containsKey( a.id ) ) return cazycemap.get( a.id );
		}
		return null;
	}

	public String getCazyGH() {
		Map<String,String> cazyghmap = geneset.getCazyGHMap();
		for( Annotation a : genes ) {
			if( cazyghmap.containsKey( a.id ) ) return cazyghmap.get( a.id );
		}
		return null;
	}

	public String getCazyGT() {
		Map<String,String> cazygtmap = geneset.getCazyGTMap();
		for( Annotation a : genes ) {
			if( cazygtmap.containsKey( a.id ) ) return cazygtmap.get( a.id );
		}
		return null;
	}

	public String getCazyPL() {
		Map<String,String> cazyplmap = geneset.getCazyPLMap();
		for( Annotation a : genes ) {
			if( cazyplmap.containsKey( a.id ) ) return cazyplmap.get( a.id );
		}
		return null;
	}

	public String getKo() {
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.koid != null && g.koid.length() > 0 ) return g.koid;
		}
		return null;
	}
	
	public String getRefid() {
		return genes.stream().map(Annotation::getGene).filter(Objects::nonNull).map(g -> g.refid).filter(refid->refid != null && refid.length() > 0 && !refid.contains("scaffold") && !refid.contains("contig")).collect(Collectors.joining(","));
	}
	
	public String getUnid() {
		return genes.stream().map(Annotation::getGene).filter(Objects::nonNull).map(g->g.uniid).filter(p->p!=null&&p.length()>0).collect(Collectors.joining(","));
	}

	public String getGenid() {
		return genes.stream().map(Annotation::getGene).filter(Objects::nonNull).map(g->g.genid).filter(p->p!=null&&p.length()>0).collect(Collectors.joining(","));
	}
	
	public String getSymbol() {
		Set<String> s = new HashSet<>();
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.symbol != null ) s.add( g.symbol );
		}
		if( s.isEmpty() ) {
			return null;
		} else {
			String remstr = "";
			while( remstr != null ) {
				remstr = null;
				if( s.size() > 1 ) {
					for( String str : s ) {
						if( str.length() >= 7 ) {
							remstr = str;
							break;
						}
					}
					s.remove( remstr );
				}
			}
			String ret = s.toString();
			return ret.substring(1, ret.length()-1 );
		}
	}
	
	public String getKsymbol() {
		Set<String> s = new HashSet<>();
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			//if( g.koname != null && g.koname.length() > 0 && g.koname.length() < 7 ) {
					//if( sel == null || (g.koname != null && g.koname.length() > 0 && g.koname.length() < 7 && (sel.length() >= 7 || g.koname.length() > sel.length())) ) {
					//if( sel != null && sel.contains("dnaA") ) System.err.println( sel + "   " + g.koname );
				//sel += ", " + g.koname;
			//}
			if( g != null && g.koname != null ) s.add( g.koname );
		}
		if( s.isEmpty() ) return null;
		else {
			String remstr = "";
			while( remstr != null ) {
				remstr = null;
				if( s.size() > 1 ) {
					for( String str : s ) {
						if( str.length() >= 7 ) {
							remstr = str;
							break;
						}
					}
					s.remove( remstr );
				}
			}
			String ret = s.toString();
			return ret.substring(1, ret.length()-1 );
		}
	}
	
	public String getKoname() {
		String ko = this.getKo();
		if( ko != null ) {
			if( ko2name != null && ko2name.containsKey( ko ) ) {
				return ko2name.get(ko);
			}
		}
		return this.getSymbol();
	}
	
	public String getEc() {
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.ecid != null && g.ecid.length() > 0 ) return g.ecid;
		}
		return null;
	}
	
	public String getCommonSignalP() {
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.signalp ) return "Y";
		}
		return null;
	}
	
	public String getCommonTransM() {
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.transm ) return "Y";
		}
		return null;
	}
	
	public String getKeggid() {
		StringBuilder ret = new StringBuilder();
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.keggid != null ) {
				if( ret.length() == 0 ) ret.append(g.keggid);
				else ret.append(" ").append(g.keggid);
			}
		}
		return ret.toString();
	}
	
	public String getKeggPathway() {
		StringBuilder ret = new StringBuilder();
		for( Annotation a : genes ) {
			Gene g = a.getGene();
			if( g != null && g.keggpathway != null ) {
				if( ret.length() == 0 ) ret.append(g.keggpathway);
				else ret.append(" ").append(g.keggpathway);
			}
		}
		//if( ret == null && biosystemsmap != null ) return genes.stream().filter( g -> g.genid != null && biosystemsmap.containsKey(g.genid) ).flatMap( g -> biosystemsmap.get(g.genid).stream() ).collect(Collectors.joining(";"));
		return ret.toString();
	}
	
	public int size() {
		return genes.size();
	}
	
	public Set<String> getSpecies() {
		return species.keySet();
	}
	
	public boolean isSingluar() {
		return this.getGroupCount() == this.getGroupCoverage();
	}
    
    public Teginfo getGenes( String spec ) {
        return species.get( spec );
    }

	public void addGenes(Collection<Annotation> genes) {
		for (Annotation gene : genes) {
			addGene(gene);
		}
	}

	public void addGene( Annotation gene ) {
		if( gene.getGeneGroup() != this ) gene.setGeneGroup( this );
		else {
			if( genes.add( gene ) ) {
				String specstr = gene.getSpecies();
				if (specstr != null) {
					Teginfo tigenes;
					if (species.containsKey(specstr)) {
						tigenes = species.get(specstr);
					} else {
						tigenes = new Teginfo();
						species.put(specstr, tigenes);
					}
					tigenes.add(gene);
				}
			}
        }
	}

	public void setCogMap( Map<String,Cog> cogmap ) {
		this.cogmap = cogmap;
	}

	public Map<String,Cog> getCogMap() {
		return cogmap;
	}

	public void setKonameMap( Map<String,String> konamemap ) {
		this.ko2name = konamemap;
	}

	public Map<String,String> getKonameMap() {
		return this.ko2name;
	}

	public void setBiosystemsmap( Map<String,Set<String>> biosystems ) {
		this.biosystemsmap = biosystems;
	}

	public Map<String,Set<String>> getBiosystemsmap() {
		return this.biosystemsmap;
	}
	
	/*public void addSpecies( String species ) {
		this.species.add( species );
	}
	
	public void addSpecies( Set<String> species ) {
		this.species.addAll( species );
	}*/
	
	public GeneGroup( GenomeSet geneset, int i, Map<Set<String>,ShareNum> specset, Map<String,Cog> cogmap, Map<String,Cog> pfammap, Map<String,String> konamemap, Map<String,Set<String>> biosystemsmap ) {
		this.groupIndex = i;
		this.specset = specset;
		this.cogmap = cogmap;
		this.ko2name = konamemap;
		this.biosystemsmap = biosystemsmap;
		this.geneset = geneset;
	}

	public GeneGroup() {
		this( null,-1, null, null, null, null, null );
	}
	
	public int getGroupIndex() {
		return this.groupIndex;
	}
	
	public Integer getGroupCoverage() {
		return this.species.size();
	}
	
	public void setGroupCount( int count ) {
		this.groupCount = count;
	}
	
	public int getGroupCount() {
		if( groupCount == -1 ) {
			this.groupCount = genes.size();
		}
		return this.groupCount;
	}
	
	public int getGroupGeneCount() {
		return this.genes.size();//this.groupGeneCount;
	}
	
	public ShareNum getSharingNumber() {
		return specset.get( getSpecies() );
	}
};
