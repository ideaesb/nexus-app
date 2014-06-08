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
import org.apache.tapestry5.corelib.components.Label;


//semantic web
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

import org.ideademo.nexus.vocabulary.NXS;
import org.apache.commons.lang.StringUtils;

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

	
   ///////////////////////////////
   // semantic web
	
	   public Model getRDF()
	   {
	       Model model = ModelFactory.createDefaultModel();
	       
	       Resource org = ResourceFactory.createResource("http://neclimateus.org/nexus/org/view/"+id);

	       if (StringUtils.isNotBlank(this.name)) 
	   	   {
	    	   model.add (org, NXS.Name, StringUtils.trimToEmpty(this.name));
	       }
	       else
	       {
	    	   model.add (org, NXS.Name, "Organization with no Title???");
	       }
	       
	       if (StringUtils.isNotBlank(this.code)) model.add(org, NXS.Acronym, StringUtils.trimToEmpty(this.code));
	       if (StringUtils.isNotBlank(this.contact)) model.add(org, NXS.Contact, StringUtils.trimToEmpty(this.contact));
	       if (StringUtils.isNotBlank(this.email)) model.add(org, NXS.Email, StringUtils.trimToEmpty(this.email));
	       if (StringUtils.isNotBlank(this.description)) model.add(org, NXS.Description, StringUtils.trimToEmpty(this.description));
	       if (StringUtils.isNotBlank(this.affiliations)) model.add(org, NXS.Affiliations, StringUtils.trimToEmpty(this.affiliations));
	       if (StringUtils.isNotBlank(this.url)) model.add(org, NXS.Homepage, StringUtils.trimToEmpty(this.url));
	       if (StringUtils.isNotBlank(this.logo)) model.add(org, NXS.Logo, StringUtils.trimToEmpty(this.logo));
	       if (StringUtils.isNotBlank(this.worksheet)) model.add(org, NXS.Worksheet, StringUtils.trimToEmpty(this.worksheet));
	       if (StringUtils.isNotBlank(this.keywords)) model.add(org, NXS.Keywords, StringUtils.trimToEmpty(this.keywords));
	       
	       
	       if (this.federal)
	       {
	    	  model.add(org, NXS.Organization_Type, "Federal"); // TODO - these should be labels or URIs 
	       }
	       else if (this.province)
	       {
	    	  model.add(org, NXS.Organization_Type, "Province"); // TODO - these should be labels or URIs
	       }
	       else if (this.state)
	       {
	    	  model.add(org, NXS.Organization_Type, "State"); // TODO - these should be labels or URIs
	       }
	       else if (this.local)
	       {
	    	  model.add(org, NXS.Organization_Type, "Local"); // TODO - these should be labels or URIs
	       }
	       else if (this.interagency)
	       {
	    	  model.add(org, NXS.Organization_Type, "Interagency"); // TODO - these should be labels or URIs
	       }
	       else if (this.academic)
	       {
	    	  model.add(org, NXS.Organization_Type, "Academic"); // TODO - these should be labels or URIs
	       }
	       else if (this.ngo)
	       {
	    	  model.add(org, NXS.Organization_Type, "Non-governmental Organization (NGO)"); // TODO - these should be labels or URIs
	       }
	       else if (this.otherPartnerType)
	       {
	    	  model.add(org, NXS.Organization_Type, "Other"); // TODO - these should be labels or URIs
	       }
	       else 
	       {
	    	  //model.add(org, NXS.Organization_Type, "Unspecified"); // TODO - these should be labels or URIs
	       }
	       
	      
	       
	       if (this.international)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "International"); 
	       }
	       else if (this.canada)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Canada");  
	       }
	       else if (this.newBrunswick)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "New Brunswick");  
	       }
	       else if (this.novaScotia)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Nova Scotia");  
	       }
	       else if (this.quebec)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Quebec");  
	       }
	       else if (this.princeEdwardIsland)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Princ Edward Island");  
	       }
	       else if (this.newfoundland)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Newfoundland");  
	       }
	       else if (this.labrador)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Labrador");  
	       }
	       else if (this.atlanticCanada)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Atlantic Canada");  
	       }
	       else if (this.national)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "National");  
	       }
	       else if (this.regionalOrState)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Regional Or State");  
	       }
	       else if (this.gulfOfMaine)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Gulf Of Maine");  
	       }
	       else if (this.newEngland)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "New England");  
	       }
	       else if (this.maine)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Maine");  
	       }
	       else if (this.newHampshire)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "New Hampshire");  
	       }
	       else if (this.massachusetts)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Massachusetts");  
	       }
	       else if (this.vermont)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Vermont");  
	       }
	       else if (this.connecticut)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Connecticut");  
	       }
	       else if (this.rhodeIsland)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Rhode Island");  
	       }
	       else if (this.midAtlantic)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Mid Atlantic");  
	       }
	       else if (this.newYork)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "New York");  
	       }
	       else if (this.newJersey)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "New Jersey");  
	       }
	       else if (this.pennsylvania)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Pennsylvania");  
	       }
	       else if (this.marlyland)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Maryland");  
	       }
	       else if (this.delaware)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Delaware");  
	       }
	       else if (this.virginia)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Virginia");  
	       }
	       else if (this.districtOfColumbia)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "District of Columbia");  
	       }
	       else if (this.central)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Central");  
	       }
	       else if (this.westVirginia)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "West Virginia");  
	       }
	       else if (this.greatLakes)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Great Lakes");  
	       }
	       else if (this.ohio)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Ohio");  
	       }
	       else if (this.southEast)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "South East");  
	       }
	       else if (this.northCarolina)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "North Carolina");  
	       }
	       else if (this.southCarolina)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "South Carolina");  
	       }
	       else if (this.localCity)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Local City");  
	       }
	       else if (this.problemFocused)
	       {
	    	 model.add(org, NXS.Area_of_Applicability, "Problem Focused");  
	       }
	       else 
	       {
	    	 //model.add(org, NXS.Area_of_Applicability, "Unspecified");  
	       }
     
	       
	       
	       
	       return model;

	   }	
	
}
