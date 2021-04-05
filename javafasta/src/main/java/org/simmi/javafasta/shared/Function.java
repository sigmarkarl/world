package org.simmi.javafasta.shared;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;

public class Function {
	public Function() {}
	public Function( String go ) { this.go = new SimpleStringProperty(go); }

	private SimpleStringProperty go;
	private SimpleStringProperty ec = new SimpleStringProperty();
	private SimpleStringProperty metacyc = new SimpleStringProperty();
	private SimpleStringProperty kegg = new SimpleStringProperty();
	private SimpleStringProperty ko = new SimpleStringProperty();
	private SimpleStringProperty name = new SimpleStringProperty();
	private SimpleStringProperty namespace = new SimpleStringProperty();
	private SimpleStringProperty desc = new SimpleStringProperty();
	public Set<String> isa;
	Set<String> subset;
	private Set<Gene> geneentries;
	private Set<GeneGroup>	groupentries;
	public int index;
	
	public void setKO( String ko ) {
		this.ko.set( ko );
	}
	
	public void setDesc( String desc ) {
		this.desc.set( desc );
	}
	
	public void setName( String name ) {
		this.name.set( name );
	}
	
	public void setEc( String ec ) {
		this.ec.set( ec );
	}
	
	public void setMetaCyc( String metacyc ) {
		this.metacyc.set( metacyc );
	}
	
	public void setKegg( String kegg ) {
		this.kegg.set( kegg );
	}
	
	public void setNamespace( String namespace ) {
		this.namespace.set( namespace );
	}
	
	public int getGeneCount() {
		return geneentries != null ? geneentries.size() : 0;
	}
	
	public int getGroupSize() {
		return groupentries != null ? groupentries.size() : 0;
	}
	
	public Set<Gene> getGeneentries() {
		return geneentries;
	}
	
	public Set<GeneGroup> getGeneGroups() {
		return groupentries;
	}
	
	public void addGeneentries( Collection<Gene> ge ) {
		if( geneentries == null ) {
			geneentries = new HashSet<>();
			groupentries = new HashSet<>();
		}
		geneentries.addAll( ge );
		for( Gene g : ge ) {
			groupentries.add( g.getGeneGroup() );
		}
	}
	
	public void addGroupentry( GeneGroup gg ) {
		if( groupentries == null ) groupentries = new HashSet<>();
		groupentries.add( gg );
	}
	
	public void addGroupentries( Collection<GeneGroup> ge ) {
		if( groupentries == null ) groupentries = new HashSet<>();
		groupentries.addAll( ge );
	}
	
	public int getSpeciesCount() {
		if( groupentries != null ) {
			if( groupentries.size() <= 1 ) {
				for( GeneGroup gg : groupentries ) {
					Set<String> spec = gg != null ? gg.getSpecies() : null;
					if( spec != null ) return spec.size();
				}
			} else {
				Set<String>	specset = new HashSet<String>();
				for( GeneGroup gg : groupentries ) {
					if( gg != null ) specset.addAll( gg.getSpecies() );
				}
				return specset.size();
			}
		}
		return 0;
	}
	
	public String getKo() {
		return ko.get();
	}
	
	public String getGo() {
		return go.get();
	}
	
	public String getEc() {
		return ec.get();
	}
	
	public String getKegg() {
		return kegg.get();
	}
	
	public String getMetacyc() {
		return metacyc.get();
	}
	
	public String getName() {
		return name.get();
	}
	
	public String getNamespace() {
		return namespace.get();
	}
	
	public String getDesc() {
		return desc.get();
	}
	
	@Override
	public String toString() {
		return name.get();
	}
};