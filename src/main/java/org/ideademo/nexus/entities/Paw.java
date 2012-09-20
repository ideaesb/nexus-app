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
public class Paw implements Comparable<Paw>
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
	private String organization="";
	
	@Field
	private String contact="";
	
	@Field 
	private String url="";
	
	@Field @Column (length=4096)
	private String description="";
	
	@Field @Column (length=4096)
	private String keywords="";
	
	@Field
	private String objectives="";
	
	@Field 
	private String worksheet="";
	
	@Field 
	private String dates="";
	
	@Field 
	private String resources="";
	
	@Field
	private String feedback="";
	

	/////////////////////////////////////////////
	//Status
	//
	
	private boolean ongoing = false;
	private boolean planned = false;
	private boolean proposed = false;
	private boolean completed = false;
	
	/////////////////////////////////////////////
	//Priority Level as defined by states:
	//
	
	private Boolean high = false;
	private Boolean mid = false;
	private Boolean low = false;
	private Boolean unknown = false;
	
	/////////////////////////////////////////////
	//Focus Area
	//
	
	private boolean sustainability = false;
	private boolean resilience = false;
	private boolean water = false;
	private boolean extremes = false;
	private boolean conservation = false;

	
	/////////////////////////////////////////////
	//Area of Applicability
	//
	
	private boolean international = false;
	private boolean canada = false;
	private boolean newBrunswick  = false;
	private boolean novaScotia  = false;
	private boolean quebec = false;
	private boolean princeEdwardIsland  = false;
	private boolean newfoundland  = false;
	private boolean labrador  = false;
	private boolean national  = false;
	private boolean regionalOrState  = false;
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
	
	
	/////////////////////////////////////////////
	//Sector
	//
	
	private boolean publicHealth = false;
	private boolean emergencyManagement = false;
	private boolean indirectClimateHazards = false;
	private boolean vectorBorneIllness = false;
	private boolean heatRelated = false;
	private boolean waterQuality = false;
	private boolean infrastructure = false;
	private boolean energy = false;
	private boolean communication = false;
	private boolean freshWaterResources = false;
	private boolean stormWater = false;
	private boolean wastewater = false;
	private boolean waterSupply = false;
	private boolean transportation = false;
	private boolean builtCoast = false;
	private boolean managedEcosystems = false;
	private boolean fisheries = false;
	private boolean aquaculture = false;
	private boolean agriculture = false;
	private boolean forests = false;
	private boolean otherManagedEcosystems = false;
	private boolean naturalEcosystems = false;
	private boolean coasts = false;
	private boolean estuaries = false;
	private boolean wetlands = false;
	private boolean oceans = false;
	private boolean inland = false;
	private boolean beaches = false;
	private boolean biota = false;
	private boolean marine = false;
	private boolean terrestrial = false;
	private boolean endangered= false;
	private boolean candidateSpecies = false;
	private boolean concernSpecies = false;
	private boolean cultural = false;
	private boolean recreationAndTourism = false;
	private boolean urban = false;
	private boolean indigenousPeoples = false;
	private boolean minority = false;
	private boolean economicResources = false;
	private boolean crossDisciplinary=false;
	private boolean otherSector = false;
	
	
	
	/////////////////////////////////////////////
	//Capability Area
	//
	
	private boolean ecv = false;
	private boolean impacts = false;
	private boolean vulnerabilityAssessments = false;
	private boolean riskAssessments = false;
	private boolean needs = false;
	private boolean scenarioPlanning = false;
	private boolean experimentalImpacts = false;
	private boolean monitor = false;
	private boolean downscale = false;
	private boolean conditions = false;
	private boolean forecastImpacts = false;
	private boolean economicImpacts = false;
	private boolean publicSecurity = false;
	private boolean mitigation = false;
	private boolean translation = false;
	private boolean tools = false;
	private boolean stakeholder = false;
	private boolean guidance = false;
	private boolean literacy = false;
	private boolean translate = false;
	private boolean improve = false;
	
	
	/////////////////////////////////////////////
	//PAWS categories
	//
	
	private boolean specific = false;
	private boolean retrofitted = false;
	private boolean monitoring = false;
	private boolean research = false;

	
	
	/////////////////////////////////////////////////
	//  auto generated getters and setters 
	//
	
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
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getUrl() {
		return url;
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
	public String getObjectives() {
		return objectives;
	}
	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}
	public String getWorksheet() 
	{
	  return worksheet;
	}
	public void setWorksheet(String worksheet) {
		this.worksheet = worksheet;
	}
	public String getDates() {
		return dates;
	}
	public void setDates(String dates) {
		this.dates = dates;
	}
	public String getResources() {
		return resources;
	}
	public void setResources(String resources) {
		this.resources = resources;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public boolean isOngoing() {
		return ongoing;
	}
	public void setOngoing(boolean ongoing) {
		this.ongoing = ongoing;
	}
	public boolean isPlanned() {
		return planned;
	}
	public void setPlanned(boolean planned) {
		this.planned = planned;
	}
	public boolean isProposed() {
		return proposed;
	}
	public void setProposed(boolean proposed) {
		this.proposed = proposed;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public Boolean getHigh() {
		return high;
	}
	public void setHigh(Boolean high) {
		this.high = high;
	}
	public Boolean getMid() {
		return mid;
	}
	public void setMid(Boolean mid) {
		this.mid = mid;
	}
	public Boolean getLow() {
		return low;
	}
	public void setLow(Boolean low) {
		this.low = low;
	}
	public Boolean getUnknown() {
		return unknown;
	}
	public void setUnknown(Boolean unknown) {
		this.unknown = unknown;
	}
	public boolean isSustainability() {
		return sustainability;
	}
	public void setSustainability(boolean sustainability) {
		this.sustainability = sustainability;
	}
	public boolean isResilience() {
		return resilience;
	}
	public void setResilience(boolean resilience) {
		this.resilience = resilience;
	}
	public boolean isWater() {
		return water;
	}
	public void setWater(boolean water) {
		this.water = water;
	}
	public boolean isExtremes() {
		return extremes;
	}
	public void setExtremes(boolean extremes) {
		this.extremes = extremes;
	}
	public boolean isConservation() {
		return conservation;
	}
	public void setConservation(boolean conservation) {
		this.conservation = conservation;
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
	public boolean isPublicHealth() {
		return publicHealth;
	}
	public void setPublicHealth(boolean publicHealth) {
		this.publicHealth = publicHealth;
	}
	public boolean isEmergencyManagement() {
		return emergencyManagement;
	}
	public void setEmergencyManagement(boolean emergencyManagement) {
		this.emergencyManagement = emergencyManagement;
	}
	public boolean isIndirectClimateHazards() {
		return indirectClimateHazards;
	}
	public void setIndirectClimateHazards(boolean indirectClimateHazards) {
		this.indirectClimateHazards = indirectClimateHazards;
	}
	public boolean isVectorBorneIllness() {
		return vectorBorneIllness;
	}
	public void setVectorBorneIllness(boolean vectorBorneIllness) {
		this.vectorBorneIllness = vectorBorneIllness;
	}
	public boolean isHeatRelated() {
		return heatRelated;
	}
	public void setHeatRelated(boolean heatRelated) {
		this.heatRelated = heatRelated;
	}
	public boolean isWaterQuality() {
		return waterQuality;
	}
	public void setWaterQuality(boolean waterQuality) {
		this.waterQuality = waterQuality;
	}
	public boolean isInfrastructure() {
		return infrastructure;
	}
	public void setInfrastructure(boolean infrastructure) {
		this.infrastructure = infrastructure;
	}
	public boolean isEnergy() {
		return energy;
	}
	public void setEnergy(boolean energy) {
		this.energy = energy;
	}
	public boolean isCommunication() {
		return communication;
	}
	public void setCommunication(boolean communication) {
		this.communication = communication;
	}
	public boolean isFreshWaterResources() {
		return freshWaterResources;
	}
	public void setFreshWaterResources(boolean freshWaterResources) {
		this.freshWaterResources = freshWaterResources;
	}
	public boolean isStormWater() {
		return stormWater;
	}
	public void setStormWater(boolean stormWater) {
		this.stormWater = stormWater;
	}
	public boolean isWastewater() {
		return wastewater;
	}
	public void setWastewater(boolean wastewater) {
		this.wastewater = wastewater;
	}
	public boolean isWaterSupply() {
		return waterSupply;
	}
	public void setWaterSupply(boolean waterSupply) {
		this.waterSupply = waterSupply;
	}
	public boolean isTransportation() {
		return transportation;
	}
	public void setTransportation(boolean transportation) {
		this.transportation = transportation;
	}
	public boolean isBuiltCoast() {
		return builtCoast;
	}
	public void setBuiltCoast(boolean builtCoast) {
		this.builtCoast = builtCoast;
	}
	public boolean isManagedEcosystems() {
		return managedEcosystems;
	}
	public void setManagedEcosystems(boolean managedEcosystems) {
		this.managedEcosystems = managedEcosystems;
	}
	public boolean isFisheries() {
		return fisheries;
	}
	public void setFisheries(boolean fisheries) {
		this.fisheries = fisheries;
	}
	public boolean isAquaculture() {
		return aquaculture;
	}
	public void setAquaculture(boolean aquaculture) {
		this.aquaculture = aquaculture;
	}
	public boolean isAgriculture() {
		return agriculture;
	}
	public void setAgriculture(boolean agriculture) {
		this.agriculture = agriculture;
	}
	public boolean isForests() {
		return forests;
	}
	public void setForests(boolean forests) {
		this.forests = forests;
	}
	public boolean isOtherManagedEcosystems() {
		return otherManagedEcosystems;
	}
	public void setOtherManagedEcosystems(boolean otherManagedEcosystems) {
		this.otherManagedEcosystems = otherManagedEcosystems;
	}
	public boolean isNaturalEcosystems() {
		return naturalEcosystems;
	}
	public void setNaturalEcosystems(boolean naturalEcosystems) {
		this.naturalEcosystems = naturalEcosystems;
	}
	public boolean isCoasts() {
		return coasts;
	}
	public void setCoasts(boolean coasts) {
		this.coasts = coasts;
	}
	public boolean isEstuaries() {
		return estuaries;
	}
	public void setEstuaries(boolean estuaries) {
		this.estuaries = estuaries;
	}
	public boolean isWetlands() {
		return wetlands;
	}
	public void setWetlands(boolean wetlands) {
		this.wetlands = wetlands;
	}
	public boolean isOceans() {
		return oceans;
	}
	public void setOceans(boolean oceans) {
		this.oceans = oceans;
	}
	public boolean isInland() {
		return inland;
	}
	public void setInland(boolean inland) {
		this.inland = inland;
	}
	public boolean isBeaches() {
		return beaches;
	}
	public void setBeaches(boolean beaches) {
		this.beaches = beaches;
	}
	public boolean isBiota() {
		return biota;
	}
	public void setBiota(boolean biota) {
		this.biota = biota;
	}
	public boolean isMarine() {
		return marine;
	}
	public void setMarine(boolean marine) {
		this.marine = marine;
	}
	public boolean isTerrestrial() {
		return terrestrial;
	}
	public void setTerrestrial(boolean terrestrial) {
		this.terrestrial = terrestrial;
	}
	public boolean isEndangered() {
		return endangered;
	}
	public void setEndangered(boolean endangered) {
		this.endangered = endangered;
	}
	public boolean isCandidateSpecies() {
		return candidateSpecies;
	}
	public void setCandidateSpecies(boolean candidateSpecies) {
		this.candidateSpecies = candidateSpecies;
	}
	public boolean isConcernSpecies() {
		return concernSpecies;
	}
	public void setConcernSpecies(boolean concernSpecies) {
		this.concernSpecies = concernSpecies;
	}
	public boolean isCultural() {
		return cultural;
	}
	public void setCultural(boolean cultural) {
		this.cultural = cultural;
	}
	public boolean isRecreationAndTourism() {
		return recreationAndTourism;
	}
	public void setRecreationAndTourism(boolean recreationAndTourism) {
		this.recreationAndTourism = recreationAndTourism;
	}
	public boolean isUrban() {
		return urban;
	}
	public void setUrban(boolean urban) {
		this.urban = urban;
	}
	public boolean isIndigenousPeoples() {
		return indigenousPeoples;
	}
	public void setIndigenousPeoples(boolean indigenousPeoples) {
		this.indigenousPeoples = indigenousPeoples;
	}
	public boolean isMinority() {
		return minority;
	}
	public void setMinority(boolean minority) {
		this.minority = minority;
	}
	public boolean isEconomicResources() {
		return economicResources;
	}
	public void setEconomicResources(boolean economicResources) {
		this.economicResources = economicResources;
	}
	public boolean isCrossDisciplinary() {
		return crossDisciplinary;
	}
	public void setCrossDisciplinary(boolean crossDisciplinary) {
		this.crossDisciplinary = crossDisciplinary;
	}
	public boolean isOtherSector() {
		return otherSector;
	}
	public void setOtherSector(boolean otherSector) {
		this.otherSector = otherSector;
	}
	public boolean isEcv() {
		return ecv;
	}
	public void setEcv(boolean ecv) {
		this.ecv = ecv;
	}
	public boolean isImpacts() {
		return impacts;
	}
	public void setImpacts(boolean impacts) {
		this.impacts = impacts;
	}
	public boolean isVulnerabilityAssessments() {
		return vulnerabilityAssessments;
	}
	public void setVulnerabilityAssessments(boolean vulnerabilityAssessments) {
		this.vulnerabilityAssessments = vulnerabilityAssessments;
	}
	public boolean isRiskAssessments() {
		return riskAssessments;
	}
	public void setRiskAssessments(boolean riskAssessments) {
		this.riskAssessments = riskAssessments;
	}
	public boolean isNeeds() {
		return needs;
	}
	public void setNeeds(boolean needs) {
		this.needs = needs;
	}
	public boolean isScenarioPlanning() {
		return scenarioPlanning;
	}
	public void setScenarioPlanning(boolean scenarioPlanning) {
		this.scenarioPlanning = scenarioPlanning;
	}
	public boolean isExperimentalImpacts() {
		return experimentalImpacts;
	}
	public void setExperimentalImpacts(boolean experimentalImpacts) {
		this.experimentalImpacts = experimentalImpacts;
	}
	public boolean isMonitor() {
		return monitor;
	}
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}
	public boolean isDownscale() {
		return downscale;
	}
	public void setDownscale(boolean downscale) {
		this.downscale = downscale;
	}
	public boolean isConditions() {
		return conditions;
	}
	public void setConditions(boolean conditions) {
		this.conditions = conditions;
	}
	public boolean isForecastImpacts() {
		return forecastImpacts;
	}
	public void setForecastImpacts(boolean forecastImpacts) {
		this.forecastImpacts = forecastImpacts;
	}
	public boolean isEconomicImpacts() {
		return economicImpacts;
	}
	public void setEconomicImpacts(boolean economicImpacts) {
		this.economicImpacts = economicImpacts;
	}
	public boolean isPublicSecurity() {
		return publicSecurity;
	}
	public void setPublicSecurity(boolean publicSecurity) {
		this.publicSecurity = publicSecurity;
	}
	public boolean isMitigation() {
		return mitigation;
	}
	public void setMitigation(boolean mitigation) {
		this.mitigation = mitigation;
	}
	public boolean isTranslation() {
		return translation;
	}
	public void setTranslation(boolean translation) {
		this.translation = translation;
	}
	public boolean isTools() {
		return tools;
	}
	public void setTools(boolean tools) {
		this.tools = tools;
	}
	public boolean isStakeholder() {
		return stakeholder;
	}
	public void setStakeholder(boolean stakeholder) {
		this.stakeholder = stakeholder;
	}
	public boolean isGuidance() {
		return guidance;
	}
	public void setGuidance(boolean guidance) {
		this.guidance = guidance;
	}
	public boolean isLiteracy() {
		return literacy;
	}
	public void setLiteracy(boolean literacy) {
		this.literacy = literacy;
	}
	public boolean isTranslate() {
		return translate;
	}
	public void setTranslate(boolean translate) {
		this.translate = translate;
	}
	public boolean isImprove() {
		return improve;
	}
	public void setImprove(boolean improve) {
		this.improve = improve;
	}
	public boolean isSpecific() {
		return specific;
	}
	public void setSpecific(boolean specific) {
		this.specific = specific;
	}
	public boolean isRetrofitted() {
		return retrofitted;
	}
	public void setRetrofitted(boolean retrofitted) {
		this.retrofitted = retrofitted;
	}
	public boolean isMonitoring() {
		return monitoring;
	}
	public void setMonitoring(boolean monitoring) {
		this.monitoring = monitoring;
	}
	public boolean isResearch() {
		return research;
	}
	public void setResearch(boolean research) {
		this.research = research;
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
	
	public int compareTo(Paw ao) 
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
