package org.ideademo.nexus.entities;

import java.lang.Comparable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import org.apache.tapestry5.beaneditor.NonVisual;



@Entity @Indexed
public class Org implements Comparable<Org>
{
	
	//////////////////////////////////////////
	//Reserved indexing id 
	
	@Id @GeneratedValue @DocumentId @NonVisual
	private Long id;
	
	
	//////////////////////////////////////////////
	//String fields (being a keyword for Lucene)
	//
	
	@Field
	private String code="";

	@Field
	private String name="";
	
	@Field
	private String contact="";
	
	@Field
	private String email="";

	@Field 
	private String url="";
	
	@Field @Column (length=4096)
	private String description="";
	
	@Field @Column (length=4096)
	private String keywords="";
	
	@Field 
	private String affiliations="";
	
	@Field 
	private String worksheet="";
	
	@Field
	private String logo="";
	

	
	/////////////////////////////////////////////
	//Type of Partner
	//
	
    private boolean partner = false;
    private boolean program = false; 
	private boolean federal = false;
	private boolean province = false;
	private boolean state = false;
	private boolean local = false;
	private boolean interagency = false;
	private boolean academic = false;
	private boolean ngo = false;
	private boolean otherPartnerType = false;

	
	/////////////////////////////////////////////
	//Area of Applicability
	//
	
	private boolean international = false;
	private boolean canada = false;
	private boolean atlanticCanada = false;
	private boolean newBrunswick  = false;
	private boolean novaScotia  = false;
	private boolean quebec = false;
	private boolean princeEdwardIsland  = false;
	private boolean newfoundland  = false;
	private boolean labrador  = false;
	private boolean national  = false;
	private boolean regionalOrState  = false;
	private boolean gulfOfMaine = false;
	private boolean newEngland = false;
	private boolean maine  = false;
	private boolean newHampshire  = false;
	private boolean massachusetts  = false;
	private boolean vermont  = false;
	private boolean connecticut  = false;
	private boolean rhodeIsland  = false;
	private boolean midAtlantic  = false;
	private boolean newYork  = false;
	private boolean newJersey  = false;
	private boolean pennsylvania  = false;
	private boolean marlyland  = false;
	private boolean delaware  = false;
	private boolean virginia  = false;
	private boolean central  = false;
	private boolean districtOfColumbia = false; 
	private boolean westVirginia  = false;
	private boolean greatLakes  = false;
	private boolean ohio  = false;
	private boolean southEast  = false;
	private boolean northCarolina  = false;
	private boolean southCarolina  = false; 
	private boolean localCity  = false;
	private boolean problemFocused = false;
	

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUrl() 
	{
		if (url == null || url.trim().length() == 0)
		{
			return "";
		}
	    else if (url.toLowerCase().startsWith("http"))
		{
		  return url;
		}
	    else
	    {
	    	return "http://" + url;
	    }
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getAffiliations() {
		return affiliations;
	}
	public void setAffiliations(String affiliations) {
		this.affiliations = affiliations;
	}
	public String getWorksheet() 
	{
	  return worksheet;
	}
	public void setWorksheet(String worksheet) {
		this.worksheet = worksheet;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public boolean isPartner() {
		return partner;
	}
	public void setPartner(boolean partner) {
		this.partner = partner;
	}
	public boolean isProgram() {
		return program;
	}
	public void setProgram(boolean program) {
		this.program = program;
	}
	public boolean isFederal() {
		return federal;
	}
	public void setFederal(boolean federal) {
		this.federal = federal;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public boolean isProvince() {
		return province;
	}
	public void setProvince(boolean province) {
		this.province = province;
	}

	
	public boolean isLocal() {
		return local;
	}
	public void setLocal(boolean local) {
		this.local = local;
	}
	public boolean isInteragency() {
		return interagency;
	}
	public void setInteragency(boolean interagency) {
		this.interagency = interagency;
	}
	public boolean isAcademic() {
		return academic;
	}
	public void setAcademic(boolean academic) {
		this.academic = academic;
	}
	public boolean isNgo() {
		return ngo;
	}
	public void setNgo(boolean ngo) {
		this.ngo = ngo;
	}
	public boolean isOtherPartnerType() {
		return otherPartnerType;
	}
	public void setOtherPartnerType(boolean otherPartnerType) {
		this.otherPartnerType = otherPartnerType;
	}
	
	
	public boolean isAtlanticCanada() {
		return atlanticCanada;
	}
	public void setAtlanticCanada(boolean atlanticCanada) {
		this.atlanticCanada = atlanticCanada;
	}
	public boolean isGulfOfMaine() {
		return gulfOfMaine;
	}
	public void setGulfOfMaine(boolean gulfOfMaine) {
		this.gulfOfMaine = gulfOfMaine;
	}	
	
	public boolean isInternational() {
		return international;
	}
	public void setInternational(boolean international) {
		this.international = international;
	}
	public boolean isCanada() {
		return canada;
	}
	public void setCanada(boolean canada) {
		this.canada = canada;
	}
	public boolean isNewBrunswick() {
		return newBrunswick;
	}
	public void setNewBrunswick(boolean newBrunswick) {
		this.newBrunswick = newBrunswick;
	}
	public boolean isNovaScotia() {
		return novaScotia;
	}
	public void setNovaScotia(boolean novaScotia) {
		this.novaScotia = novaScotia;
	}
	public boolean isQuebec() {
		return quebec;
	}
	public void setQuebec(boolean quebec) {
		this.quebec = quebec;
	}
	public boolean isPrinceEdwardIsland() {
		return princeEdwardIsland;
	}
	public void setPrinceEdwardIsland(boolean princeEdwardIsland) {
		this.princeEdwardIsland = princeEdwardIsland;
	}
	public boolean isNewfoundland() {
		return newfoundland;
	}
	public void setNewfoundland(boolean newfoundland) {
		this.newfoundland = newfoundland;
	}
	public boolean isLabrador() {
		return labrador;
	}
	public void setLabrador(boolean labrador) {
		this.labrador = labrador;
	}
	public boolean isNational() {
		return national;
	}
	public void setNational(boolean national) {
		this.national = national;
	}
	public boolean isRegionalOrState() {
		return regionalOrState;
	}
	public void setRegionalOrState(boolean regionalOrState) {
		this.regionalOrState = regionalOrState;
	}
	public boolean isNewEngland() {
		return newEngland;
	}
	public void setNewEngland(boolean newEngland) {
		this.newEngland = newEngland;
	}
	public boolean isMaine() {
		return maine;
	}
	public void setMaine(boolean maine) {
		this.maine = maine;
	}
	public boolean isNewHampshire() {
		return newHampshire;
	}
	public void setNewHampshire(boolean newHampshire) {
		this.newHampshire = newHampshire;
	}
	public boolean isMassachusetts() {
		return massachusetts;
	}
	public void setMassachusetts(boolean massachusetts) {
		this.massachusetts = massachusetts;
	}
	public boolean isVermont() {
		return vermont;
	}
	public void setVermont(boolean vermont) {
		this.vermont = vermont;
	}
	public boolean isConnecticut() {
		return connecticut;
	}
	public void setConnecticut(boolean connecticut) {
		this.connecticut = connecticut;
	}
	public boolean isRhodeIsland() {
		return rhodeIsland;
	}
	public void setRhodeIsland(boolean rhodeIsland) {
		this.rhodeIsland = rhodeIsland;
	}
	public boolean isMidAtlantic() {
		return midAtlantic;
	}
	public void setMidAtlantic(boolean midAtlantic) {
		this.midAtlantic = midAtlantic;
	}
	public boolean isNewYork() {
		return newYork;
	}
	public void setNewYork(boolean newYork) {
		this.newYork = newYork;
	}
	public boolean isNewJersey() {
		return newJersey;
	}
	public void setNewJersey(boolean newJersey) {
		this.newJersey = newJersey;
	}
	public boolean isPennsylvania() {
		return pennsylvania;
	}
	public void setPennsylvania(boolean pennsylvania) {
		this.pennsylvania = pennsylvania;
	}
	public boolean isMarlyland() {
		return marlyland;
	}
	public void setMarlyland(boolean marlyland) {
		this.marlyland = marlyland;
	}
	public boolean isDelaware() {
		return delaware;
	}
	public void setDelaware(boolean delaware) {
		this.delaware = delaware;
	}
	public boolean isVirginia() {
		return virginia;
	}
	public void setVirginia(boolean virginia) {
		this.virginia = virginia;
	}
	public boolean isCentral() {
		return central;
	}
	public void setCentral(boolean central) {
		this.central = central;
	}
	public boolean isDistrictOfColumbia() {
		return districtOfColumbia;
	}
	public void setDistrictOfColumbia(boolean districtOfColumbia) {
		this.districtOfColumbia = districtOfColumbia;
	}
	public boolean isWestVirginia() {
		return westVirginia;
	}
	public void setWestVirginia(boolean westVirginia) {
		this.westVirginia = westVirginia;
	}
	public boolean isGreatLakes() {
		return greatLakes;
	}
	public void setGreatLakes(boolean greatLakes) {
		this.greatLakes = greatLakes;
	}
	public boolean isOhio() {
		return ohio;
	}
	public void setOhio(boolean ohio) {
		this.ohio = ohio;
	}
	public boolean isSouthEast() {
		return southEast;
	}
	public void setSouthEast(boolean southEast) {
		this.southEast = southEast;
	}
	public boolean isNorthCarolina() {
		return northCarolina;
	}
	public void setNorthCarolina(boolean northCarolina) {
		this.northCarolina = northCarolina;
	}
	public boolean isSouthCarolina() {
		return southCarolina;
	}
	public void setSouthCarolina(boolean southCarolina) {
		this.southCarolina = southCarolina;
	}
	public boolean isLocalCity() {
		return localCity;
	}
	public void setLocalCity(boolean localCity) {
		this.localCity = localCity;
	}
	public boolean isProblemFocused() {
		return problemFocused;
	}
	public void setProblemFocused(boolean problemFocused) {
		this.problemFocused = problemFocused;
	}

	
	
	private boolean worksheetExists = false;
	public boolean isWorksheetExists() {
		return worksheetExists;
	}
	public void setWorksheetExists(boolean worksheetExists) {
		this.worksheetExists = worksheetExists;
	}
	
	////////////////////////////////////////////////
	/// default/natural sort order - String  - names
	
	public int compareTo(Org ao) 
	{
	    boolean thisIsEmpty = false;
	    boolean aoIsEmpty = false; 
	    
	    if (this.getName() == null || this.getName().trim().length() == 0) thisIsEmpty = true; 
	    if (ao.getName() == null || ao.getName().trim().length() == 0) aoIsEmpty = true;
	    
	    if (thisIsEmpty && aoIsEmpty) return 0;
	    if (thisIsEmpty && !aoIsEmpty) return -1;
	    if (!thisIsEmpty && aoIsEmpty) return 1; 
	    return this.getName().compareToIgnoreCase(ao.getName());
   }	

	
	
}
