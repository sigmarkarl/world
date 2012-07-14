package org.simmi.client;

import java.io.Serializable;

public class Subsaga implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Subsaga() {
		
	}
	
	public Subsaga( String nafn, String tegund, String hofundur, String hofundarnafn, String language, String url ) {
		this.nafn = nafn;
		this.tegund = tegund;
		this.hofundur = hofundur;
		this.hofundarnafn = hofundarnafn;
		this.language = language;
		this.url = url;
	}
	
	private String		nafn;
	private String		urdrattur;
	private String		tegund;
	private String		hofundur;
	private String		hofundarnafn;
	private String		language;
	private String		url;
	private String		key;
	private Einkunn[]	grades;
	
	private boolean				love;
	private boolean				horror;
	private boolean				children;
	private boolean				erotic;
	private boolean				adolescent;
	private boolean				science;
	private boolean				historical;
	private boolean				truestory;
	private boolean				comedy;
	private boolean				tragedy;
	private boolean				supernatural;
	private boolean				criminal;
	private boolean				adventure;
	private boolean				poem;
	private boolean				tobecontinued;
	
	public Einkunn[] getGrades() {
		return grades;
	}
	
	public void setGrades( Einkunn[] grades ) {
		this.grades = grades;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey( String key ) {
		this.key = key;
	}
	
	public String getName() {
		return nafn;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage( String language ) {
		this.language = language;
	}
	
	public String getType() {
		return tegund;
	}
	
	public String getSummary() {
		return urdrattur;
	}
	
	public void setSummary( String summary ) {
		this.urdrattur = summary;
	}
	
	public String getAuthor() {
		return hofundur;
	}
	
	public String getAuthorSynonim() {
		return hofundarnafn;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl( String url ) {
		this.url = url;
	}
	
	public boolean getScience() {
		return science;
	}
	
	public boolean getSupernatural() {
		return supernatural;
	}
	
	public boolean getLove() {
		return love;
	}
	
	public boolean getChildren() {
		return children;
	}
	
	public boolean getAdolescent() {
		return adolescent;
	}
	
	public boolean getErotic() {
		return erotic;
	}
	
	public boolean getHorror() {
		return horror;
	}
	
	public boolean getCriminal() {
		return criminal;
	}
	
	public boolean getComedy() {
		return comedy;
	}
	
	public boolean getTragedy() {
		return tragedy;
	}
	
	public boolean getHistorical() {
		return historical;
	}
	
	public boolean getTruestory() {
		return truestory;
	}
	
	public boolean getAdventure() {
		return adventure;
	}
	
	public boolean getPoem() {
		return poem;
	}
	
	public boolean getContinue() {
		return tobecontinued;
	}
	
	public void setScience( boolean science ) {
		this.science = science;
	}
	
	public void setSupernatural( boolean supernatural ) {
		this.supernatural = supernatural;
	}
	
	public void setLove( boolean love ) {
		this.love = love;
	}
	
	public void setChildren( boolean children ) {
		this.children = children;
	}
	
	public void setAdolescent( boolean adolescent ) {
		this.adolescent = adolescent;
	}
	
	public void setErotic( boolean erotic ) {
		this.erotic = erotic;
	}
	
	public void setHorror( boolean horror ) {
		this.horror = horror;
	}
	
	public void setCriminal( boolean criminal ) {
		this.criminal = criminal;
	}
	
	public void setComedy( boolean comedy ) {
		this.comedy = comedy;
	}
	
	public void setTragedy( boolean tragedy ) {
		this.tragedy = tragedy;
	}
	
	public void setHistorical( boolean historical ) {
		this.historical = historical;;
	}
	
	public void setTruestory( boolean truestory ) {
		this.truestory = truestory;
	}
	
	public void setAdventure( boolean adventure ) {
		this.adventure = adventure;
	}
	
	public void setPoem( boolean poem ) {
		this.poem = poem;
	}
	
	public void setContinue( boolean tobecontined ) {
		this.tobecontinued = tobecontined;
	}
}
