package org.ideademo.nexus.pages;

import java.io.StringReader;
import java.io.IOException;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import org.apache.tapestry5.PersistenceConstants;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Persist;


import org.apache.tapestry5.hibernate.HibernateSessionManager;

import org.apache.tapestry5.ioc.annotations.Inject;


import org.hibernate.Session;

import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;

import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.TermMatchingContext;


import org.ideademo.nexus.entities.Dap;

import org.apache.log4j.Logger;


public class Daps 
{
	 
  private static Logger logger = Logger.getLogger(Daps.class);
  private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_31); 

  
  /////////////////////////////
  //  Drives QBE Search
  @Persist (PersistenceConstants.FLASH)
  private Dap example;
  
  
  //////////////////////////////
  // Used in rendering Grid Row
  @SuppressWarnings("unused")
  @Property 
  private Dap row;

    
  @Property
  @Persist (PersistenceConstants.FLASH)
  private String searchText;

  @Inject
  private Session session;
  
  @Inject
  private HibernateSessionManager sessionManager;
  
  @Property 
  @Persist (PersistenceConstants.FLASH)
  int retrieved; 
  @Property 
  @Persist (PersistenceConstants.FLASH)
  int total;

  
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  //  Select Boxes - Enumaration values - the user-visible labels are externalized in Index.properties 
  
  // the data select box
  @Property
  @Persist (PersistenceConstants.FLASH)
  private Data data;
  /**
   * INSITU=In situ Observations
   * SATELLITE=Satellite Remote Observations
   * OBS=Observing Systems
   * SURVEYS=Surveys and PreliminaryAssessments
   * INDI=Indicator Based Research
   * REANAL=Reanalysis Products
   * DEPTH=Depth and Elevation Data
   * PROV=Data Stewardship and Provisions
   * ODATA=Other
   */
  public enum Data
  {
    INSITU, SATELLITE, OBS, SURVEYS, INDI, REANAL, DEPTH, PROV, ODATA
  }

  
  // the products select box
  @Property
  @Persist (PersistenceConstants.FLASH)
  private Products products;  
  /**
   * HIND=Hindcasts (climatologies, models)
   * FORE=Forecasts and outlooks (monthly to annual, models)
   * PROJ=Projections (intra-annual to multi-decadal, including SLR and model down-scaling)
   * MAPS=Maps (Imagery, geo-referenced data)
   * PLANS=Plans, Assessments, Studies
   * OPRD=Other
   */
  public enum Products
  {
     HIND,  FORE, PROJ, MAPS, PLANS, OPRD
  }

  
  // the services select box
  @Property
  @Persist (PersistenceConstants.FLASH)
  private Services services;
  /**
   * ENG=Engagement
   * EDU=Education
   * DSS=Viewers and Web-based Tools
   * TRA=Training and Capacity Building
   * MGMT=Management Guidance (i.e. structured decision making)
   * GUID=Regulatory/ Policy Guidance
   * OSER=Other
   */
  public enum Services
  {
     ENG, EDU, DSS, TRA, MGMT, GUID, OSER
  }

  
  // the scientific discipline select box
  @Property
  @Persist (PersistenceConstants.FLASH)
  private Discipline discipline; 
  /**
   * PHY=Physical Science
   * BIO=Ecological and Biological
   * GEO=Geological
   * CHEM=Chemical
   * SOCIO=Climate Society Interactions
   */
  public enum Discipline
  {
    PHY, BIO, GEO, CHEM, SOCIO
  }

  
  // the sector select box
  @Property
  @Persist (PersistenceConstants.FLASH)
  private Sector sector; 
  /**
   * PUBLIC=Public Health and Safety
   * INFRA=Infrastructure
   * MECO=Managed Ecosystems
   * NECO=Natural Ecosystems
   * BIOTA=Biota
   * CULT=Social and Cultural Resources
   * REC=Recreation and Tourism
   * ECORES=Economic Resources
   * CROSS=Cross Disciplinary
   * OSEC=Other
   */
  public enum Sector
  {
    PUBLIC, INFRA, MECO, NECO, BIOTA, CULT, REC, ECORES, CROSS, OSEC
  }

  
  // the regions select box
  @Property
  @Persist (PersistenceConstants.FLASH)
  private Regions regions;  // AOA = Area of Applicability
  /**
   *  INT=International
   *  NAT=National
   *  REG=Regional Or State
   *  LOC=Local/City
   *  OTH=Other/Problem Focused
   */
  public enum Regions
  {
    INT, NAT, REG, LOC, OTH
  }
  

  ///////////////////////////////////////////////////////////////
  //  Action Event Handlers 
  //
  
  Object onSelectedFromClear() 
  {
    this.searchText = "";
   
    // nullify selectors 
    data=null;
    products=null;
    services=null;
    discipline=null;
    sector=null;
    regions=null;
    this.example = null;
    return null; 
  }
  Object onSelectedFromSearch() 
  {
    return null; 
  }

  //  Data Select Box Listener 
  //  INSITU, SATELLITE, OBS, SURVEYS, INDI, REANAL, DEPTH, PROV, ODATA
  Object onValueChangedFromData(String choice)
  {	
    // if there is no example set, create one.
    if (this.example == null)  this.example = new Dap(); 
    logger.info("Dap/Data Choice = " + choice);
    clearData(example);
    if (choice == null)
    {
      // clear 
    }
    else if (choice.equalsIgnoreCase("INSITU"))
    {
      example.setInsituObservations(true);
    }
    else if (choice.equalsIgnoreCase("SATELLITE"))
    {
      example.setSatelliteRemoteObservations(true);
    }
    else if (choice.equalsIgnoreCase("OBS"))
    {
      example.setObservingSystems(true);
    }
    else if (choice.equalsIgnoreCase("SURVEYS"))
    {
      example.setSurveysAndPreliminaryAssessments(true);
    }
    else if (choice.equalsIgnoreCase("INDI"))
    {
      example.setIndicatorBasedResearch(true);
    }
    else if (choice.equalsIgnoreCase("REANAL"))
    {
      example.setReanalysisProducts(true);
    }
    else if (choice.equalsIgnoreCase("DEPTH"))
    {
      example.setDepthAndElevationData(true);
    }
    else if (choice.equalsIgnoreCase("PROV"))
    {
      example.setDataStewardshipAndProvisions(true);
    }
    else if (choice.equalsIgnoreCase("ODATA"))
    {
      example.setOtherData(true);
    }
    else
    {
      // do nothing
    }
      
    // return request.isXHR() ? editZone.getBody() : null;
    // return index;
    return null;
  }

  
  //  Products Select Box Listener 
  //  HIND,  FORE, PROJ, MAPS, PLANS, OPRD
  Object onValueChangedFromProducts(String choice)
  {	
    // if there is no example set, create one.
    if (this.example == null) this.example = new Dap(); 
    logger.info("Products Choice = " + choice);
    clearProducts(example);
    if (choice == null)
    {
      // clear 
    }
    else if (choice.equalsIgnoreCase("HIND"))
    {
      example.setHindcasts(true);
    }
    else if (choice.equalsIgnoreCase("FORE"))
    {
      example.setForecastsAndOutlooks(true);
    }
    else if (choice.equalsIgnoreCase("PROJ"))
    {
      example.setProjections(true);
    }
    else if (choice.equalsIgnoreCase("MAPS"))
    {
      example.setMaps(true);
    }
    else if (choice.equalsIgnoreCase("PLANS"))
    {
      example.setAssessments(true);
    }
    else if (choice.equalsIgnoreCase("OPRD"))
    {
      example.setOtherProducts(true);
    }
    else
    {
   	  // do nothing
    }
      
    // return request.isXHR() ? editZone.getBody() : null;
    // return index;
    return null;
  }
  
  
  //  Services Select Box Listener 
  //  ENG, EDU, DSS, TRA, MGMT, GUID, OSER
  Object onValueChangedFromServices(String choice)
  {	
    // if there is no example set, create one.
    if (this.example == null) this.example = new Dap(); 
    logger.info("Services Choice = " + choice);
    clearServices(example);
    if (choice == null)
    {
      // clear 
    }
    else if (choice.equalsIgnoreCase("ENG"))
    {
      example.setEngagement(true);
    }
    else if (choice.equalsIgnoreCase("EDU"))
    {
      example.setEducation(true);
    }
    else if (choice.equalsIgnoreCase("DSS"))
    {
      example.setViewersAndWebBasedTools(true);
    }
    else if (choice.equalsIgnoreCase("TRA"))
    {
      example.setTrainingAndCapacityBuilding(true);
    }
    else if (choice.equalsIgnoreCase("MGMT"))
    {
      example.setManagementGuidance(true);
    }
    else if (choice.equalsIgnoreCase("GUID"))
    {
      example.setPolicyGuidance(true);
    }
    else if (choice.equalsIgnoreCase("OSER"))
    {
      example.setOtherServices(true);
    }
    else
    {
      // do nothing
    }
      
    // return request.isXHR() ? editZone.getBody() : null;
    // return index;
    return null;
  }

  //  Scientific Discipline Select Box Listener 
  //  PHY, BIO, GEO, CHEM, SOCIO
  Object onValueChangedFromDiscipline(String choice)
  {	
    // if there is no example set, create one.
    if (this.example == null) this.example = new Dap(); 
    logger.info("Scientific Discipline Choice = " + choice);
    clearDisciplines(example);
    if (choice == null)
    {
      // clear 
    }
    else if (choice.equalsIgnoreCase("PHY"))
    {
      example.setPhysical(true);
    }
    else if (choice.equalsIgnoreCase("BIO"))
    {
      example.setEcologicalAndBiological(true);
    }
    else if (choice.equalsIgnoreCase("GEO"))
    {
      example.setGeological(true);
    }
    else if (choice.equalsIgnoreCase("CHEM"))
    {
      example.setChemical(true);
    }
    else if (choice.equalsIgnoreCase("SOCIO"))
    {
      example.setSocialAndEconomic(true);
    }
    else
    {
      // do nothing
    }
      
    // return request.isXHR() ? editZone.getBody() : null;
    // return index;
    return null;
  }

  // sector select box listener
  // PUBLIC, INFRA, MECO, NECO, BIOTA, CULT, REC, ECORES, CROSS, OSEC
  Object onValueChangedFromSector(String choice)
  {	
    // no example? create one.
    if (this.example == null) this.example = new Dap(); 
    logger.info("Sector Choice = " + choice);
    clearSectors(example);
    if (choice == null)
    {
      // clear 
    }
    else if (choice.equalsIgnoreCase("PUBLIC"))
    {
      example.setPublicHealth(true);
    }
    else if (choice.equalsIgnoreCase("INFRA"))
    {
      example.setInfrastructure(true);
    }
    else if (choice.equalsIgnoreCase("MECO"))
    {
      example.setManagedEcosystems(true);
    }
    else if (choice.equalsIgnoreCase("NECO"))
    {
      example.setNaturalEcosystems(true);
    }
    else if (choice.equalsIgnoreCase("BIOTA"))
    {
      example.setBiota(true);
    }
    else if (choice.equalsIgnoreCase("CULT"))
    {
      example.setCultural(true);
    }
    else if (choice.equalsIgnoreCase("REC"))
    {
      example.setRecreationAndTourism(true);
    }
    else if (choice.equalsIgnoreCase("ECORES"))
    {
      example.setEconomicResources(true);
    }
    else if (choice.equalsIgnoreCase("CROSS"))
    {
      example.setCrossDisciplinary(true);
    }
    else if (choice.equalsIgnoreCase("OSEC"))
    {
      example.setOtherSector(true);
    }
    else
    {
   	  // do nothing
    }
      
    // return request.isXHR() ? editZone.getBody() : null;
    // return index;
    return null;
  }

  
  // regions select box listener...may be hooked-up to some AJAX zone if needed (later)
  //  INT, NAT, REG, LOC, OTH
  Object onValueChangedFromRegions(String choice)
  {	
    // if there is no example set, create one.
    if (this.example == null) this.example = new Dap(); 
    logger.info("Region Choice = " + choice);
    clearRegions(example);
    if (choice == null)
    {
      // clear 
    }
    else if (choice.equalsIgnoreCase("INT"))
    {
      example.setInternational(true);
    }
    else if (choice.equalsIgnoreCase("NAT"))
    {
      example.setNational(true);
    }
    else if (choice.equalsIgnoreCase("REG"))
    {
      example.setRegionalOrState(true);
    }
    else if (choice.equalsIgnoreCase("LOC"))
    {
      example.setLocalCity(true);
    }
    else if (choice.equalsIgnoreCase("OTH"))
    {
      example.setProblemFocused(true);
    }
   else
    {
     // do nothing
    }
      
    // return request.isXHR() ? editZone.getBody() : null;
    // return index;
    return null;
  }

  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////
  //  Entity List generator - QBE, Text Search or Show All 
  //

  @SuppressWarnings("unchecked")
  public List<Dap> getList()
  {
	
   // first interpret search criteria 
   if (data != null) onValueChangedFromData(data.toString());
   if (products != null)  onValueChangedFromProducts(products.toString());
   if (services != null) onValueChangedFromServices(services.toString());
   if (discipline != null) onValueChangedFromDiscipline(discipline.toString());
   if (sector != null) onValueChangedFromSector(sector.toString());
   if (regions != null) onValueChangedFromRegions(regions.toString());

    // Get all records anyway - for showing total at bottom of presentation layer
    List <Dap> alst = session.createCriteria(Dap.class).list();
    total = alst.size();

	
    // then makes lists and sublists as per the search criteria 
    List<Dap> xlst=null; // xlst = Query by Example search List
    if(example != null)
    {
       Example ex = Example.create(example).excludeFalse().ignoreCase().enableLike(MatchMode.ANYWHERE);
       
       xlst = session.createCriteria(Dap.class).add(ex).list();
       
       
       if (xlst != null)
       {
    	   logger.info("Dap Example Search Result List Size  = " + xlst.size() );
    	   Collections.sort(xlst);
       }
       else
       {
         logger.info("Dap Example Search result did not find any results...");
       }
    }
    
    List<Dap> tlst=null;
    if (searchText != null && searchText.trim().length() > 0)
    {
      FullTextSession fullTextSession = Search.getFullTextSession(sessionManager.getSession());  
      try
      {
        fullTextSession.createIndexer().startAndWait();
       }
       catch (java.lang.InterruptedException e)
       {
         logger.warn("Lucene Indexing was interrupted by something " + e);
       }
      
       QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Dap.class ).get();
       
       // fields being covered by text search 
       TermMatchingContext onFields = qb
		        .keyword()
		        .onFields("code","name","description", "keywords","contact", "url", "objectives", "worksheet", "feedback");
       
       BooleanJunction<BooleanJunction> bool = qb.bool();
       /////// Tokenize the search string for default AND logic ///
       TokenStream stream = analyzer.tokenStream(null, new StringReader(searchText));
       CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
       try
       {
        while (stream.incrementToken()) 
         {
    	   String token = cattr.toString();
    	   logger.info("Adding search token " +  token + " to look in Daps database");
    	   bool.must(onFields.matching(token).createQuery());
         }
        stream.end(); 
        stream.close(); 
       }
       catch (IOException ioe)
       {
    	   logger.warn("Daps Text Search: Encountered problem tokenizing search term " + searchText);
    	   logger.warn(ioe);
       }
       
       /////////////  the lucene query built from non-simplistic English words 
       org.apache.lucene.search.Query luceneQuery = bool.createQuery();
       
       tlst = fullTextSession.createFullTextQuery(luceneQuery, Dap.class).list();
       if (tlst != null) 
       {
    	   logger.info("TEXT Search for " + searchText + " found " + tlst.size() + " Daps records in database");
    	   Collections.sort(tlst);
       }
       else
       {
          logger.info("TEXT Search for " + searchText + " found nothing in Daps");
       }
    }
    
    
    // organize what type of list is returned...either total, partial (subset) or intersection of various search results  
    if (example == null && (searchText == null || searchText.trim().length() == 0))
    {
    	// Everything...
    	if (alst != null && alst.size() > 0)
    	{
    	  logger.info ("Returing all " + alst.size() + " Daps records");
          Collections.sort(alst);
    	}
    	else
    	{
    	  logger.warn("No Dap records found in the database");
    	}
    	retrieved = total;
        return alst; 
    }
    else if (xlst == null && tlst != null)
    {
    	// just text search results
    	logger.info("Returing " + tlst.size() + " Daps records as a result of PURE text search (no QBE) for " + searchText);
    	retrieved = tlst.size();
    	return tlst;
    }
    else if (xlst != null && tlst == null)
    {
    	// just example query results
    	logger.info("Returning " + xlst.size() + " Daps records as a result of PURE Query-By-Example (QBE), no text string");
    	retrieved = xlst.size();
    	return xlst;
    }
    else 
    {

        ////////////////////////////////////////////
    	// get the INTERSECTION of the two lists
    	
    	// TRIVIAL: if one of them is empty, return the other
    	// if one of them is empty, return the other
    	if (xlst.size() == 0 && tlst.size() > 0)
    	{
        	logger.info("Returing " + tlst.size() + " Daps records as a result of ONLY text search, QBE pulled up ZERO records for " + searchText);
        	retrieved = tlst.size();
    		return tlst;
    	}

    	if (tlst.size() == 0 && xlst.size() > 0)
    	{
        	logger.info("Returning " + xlst.size() + " Daps records as a result of ONLY Query-By-Example (QBE), text search pulled up NOTHING for string " + searchText);
        	retrieved = xlst.size();
	        return xlst;
    	}
    	
    	
    	List <Dap> ivec = new Vector<Dap>();
    	// if both are empty, return this Empty vector. 
    	if (xlst.size() == 0 && tlst.size() == 0)
    	{
          logger.info("Neither QBE nor text search for string " + searchText +  " pulled up ANY Daps Records.");
          retrieved = 0;
    	  return ivec;
    	}
    	


    	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	// now deal with BOTH text and QBE being non-empty lists - implementing intersection by Database Primary Key -  Id
    	Iterator<Dap> xiterator = xlst.iterator();
    	while (xiterator.hasNext()) 
    	{
    		Dap x = xiterator.next();
    		Long xid = x.getId();
    		
        	Iterator<Dap> titerator = tlst.iterator();
    		while(titerator.hasNext())
    		{
        		Dap t = titerator.next();
        		Long tid = t.getId();
    			
        		if (tid == xid)
        		{
        			ivec.add(t); break;
        		}
        		
    		}
    			
    	}
    	// sort again - 
    	if (ivec.size() > 0)  Collections.sort(ivec);
    	logger.info("Returning " + ivec.size() + " Daps records from COMBINED (text, QBE) Search");
    	retrieved = ivec.size();
    	return ivec;
    }
    
  }



  ////////////////////////////////////////////////
  //  QBE Setter 
  //  

  public void setExample(Dap x) 
  {
    this.example = x;
  }

  
  
  ///////////////////////////////////////////////////////
  // private methods 
  
  private void clearSectors(Dap x)
  {
      x.setPublicHealth(false);
      x.setInfrastructure(false);
      x.setManagedEcosystems(false);
      x.setNaturalEcosystems(false);
      x.setBiota(false);
      x.setCultural(false);
      x.setRecreationAndTourism(false);
      x.setEconomicResources(false);
      x.setCrossDisciplinary(false);
      x.setOtherSector(false);
  }
  private void clearRegions(Dap x)
  {
	x.setInternational(false);
	x.setNational(false);
	x.setRegionalOrState(false);
	x.setLocalCity(false);
	x.setProblemFocused(false);
  }
  private void clearDisciplines(Dap x)
  {
    x.setPhysical(false);
    x.setEcologicalAndBiological(false);
    x.setGeological(false);
    x.setChemical(false);
    x.setSocialAndEconomic(false);
  }
  private void clearServices(Dap x)
  {
      x.setEngagement(false);
      x.setEducation(false);
      x.setViewersAndWebBasedTools(false);
      x.setTrainingAndCapacityBuilding(false);
      x.setManagementGuidance(false);
      x.setPolicyGuidance(false);
      x.setOtherServices(false);
  }
  private void clearProducts(Dap x)
  {
      x.setHindcasts(false);
      x.setForecastsAndOutlooks(false);
      x.setProjections(false);
      x.setMaps(false);
      x.setAssessments(false);
      x.setOtherProducts(false);
  }
  private void clearData(Dap x)
  {
      x.setInsituObservations(false);
      x.setSatelliteRemoteObservations(false);
      x.setObservingSystems(false);
      x.setSurveysAndPreliminaryAssessments(false);
      x.setIndicatorBasedResearch(false);
      x.setReanalysisProducts(false);
      x.setDepthAndElevationData(false);
      x.setDataStewardshipAndProvisions(false);
      x.setOtherData(false);
  }
}