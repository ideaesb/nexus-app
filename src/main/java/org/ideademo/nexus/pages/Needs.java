package org.ideademo.nexus.pages;

import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.IOException;


import java.text.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.StringUtils;


import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.tapestry5.Asset; 
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Persist;


import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.internal.TapestryInternalUtils;
import org.apache.tapestry5.ioc.Messages;
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
import org.ideademo.nexus.entities.Need;
import org.ideademo.nexus.services.util.PDFStreamResponse;
import org.ideademo.nexus.services.util.RDFStreamResponse;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


//semantic web
import com.hp.hpl.jena.rdf.model.*;

import org.ideademo.nexus.vocabulary.NXS;


import org.apache.log4j.Logger;


public class Needs 
{
	 
  private static Logger logger = Logger.getLogger(Needs.class);
  private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_31); 
  
  /////////////////////////////
  //  Drives QBE Search
  @Persist (PersistenceConstants.FLASH)
  private Need example;
  
  
  //////////////////////////////
  // Used in rendering Grid Row
  @SuppressWarnings("unused")
  @Property 
  private Need row;

    
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
  
  @Inject
  private Messages messages;
  
  @Inject
  @Path("context:layout/images/noaa-logo.png")
  private Asset logoAsset;
  @Inject 
  HttpServletRequest request;
  
    
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

  
  @Property
  @Persist (PersistenceConstants.FLASH)
  private Regions regions;  // AOA = Area of Applicability
  /**
   *  INT=International
   *  NAT=National
   *  REG=Regional Or State
   *  NENG=-- New England
   *  MIDA=-- Mid-Atlantic
   *  CENT=-- Central
   *  GRTL=-- Great Lakes
   *  STHE=-- South East
   *  LOC=Local/City
   *  OTH=Other/Problem Focused
   */
  public enum Regions
  {
    INT, NAT, REG, NENG, MIDA, CENT, GRTL, STHE, LOC, OTH
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
    if (this.example == null)  this.example = new Need(); 
	logger.info("Need/Data Choice = " + choice);
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
    if (this.example == null) 
    {
      this.example = new Need(); 
	}
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
    if (this.example == null) 
    {
      this.example = new Need(); 
	}
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
    if (this.example == null) 
    {
      this.example = new Need(); 
	}
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
    if (this.example == null) 
    {
      this.example = new Need(); 
	}
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
    if (this.example == null) 
    {
      logger.info("Region Select:  Example is NULL");
      this.example = new Need(); 
	}
	else
	{
	  logger.info("Region Select:  Example is NOT null");
    }
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
    else if (choice.equalsIgnoreCase("NENG"))
    {
      example.setNewEngland(true);
    }
    else if (choice.equalsIgnoreCase("MIDA"))
    {
      example.setMidAtlantic(true);
    }
    else if (choice.equalsIgnoreCase("CENT"))
    {
      example.setCentral(true);
    }
    else if (choice.equalsIgnoreCase("GRTL"))
    {
      example.setGreatLakes(true);
    }
    else if (choice.equalsIgnoreCase("STHE"))
    {
      example.setSouthEast(true);
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
  public List<Need> getList()
  {
	
	// first interpret search criteria 
	if (data != null) onValueChangedFromData(data.toString());
	if (products != null)  onValueChangedFromProducts(products.toString());
	if (services != null) onValueChangedFromServices(services.toString());
	if (discipline != null) onValueChangedFromDiscipline(discipline.toString());
	if (sector != null) onValueChangedFromSector(sector.toString());
	if (regions != null) onValueChangedFromRegions(regions.toString());

    // Get all records anyway - for showing total at bottom of presentation layer
    List <Need> alst = session.createCriteria(Need.class).list();
    total = alst.size();

	
    // then makes lists and sublists as per the search criteria 
    List<Need> xlst=null; // xlst = Query by Example search List
    if(example != null)
    {
       Example ex = Example.create(example).excludeFalse().ignoreCase().enableLike(MatchMode.ANYWHERE);
       
       xlst = session.createCriteria(Need.class).add(ex).list();
       
       
       if (xlst != null)
       {
    	   logger.info("Need Example Search Result List Size  = " + xlst.size() );
    	   Collections.sort(xlst);
       }
       else
       {
         logger.info("Need Example Search result did not find any results...");
       }
    }
    
    List<Need> tlst=null;
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
      
       QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Need.class ).get();
       
       // fields being covered by text search 
       TermMatchingContext onFields = qb
		        .keyword()
		        .onFields("code","name","description", "keywords","contact", "url", "objectives", "worksheet", "source", "requestor", "feedback", "data", "products", "services", "programs", "projects", "comments");
       
       BooleanJunction<BooleanJunction> bool = qb.bool();
       /////// Tokenize the search string for default AND logic ///
       TokenStream stream = analyzer.tokenStream(null, new StringReader(searchText));
       CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
       try
       {
        while (stream.incrementToken()) 
         {
    	   String token = cattr.toString();
    	   logger.info("Adding search token " +  token + " to look in Needs database");
    	   bool.must(onFields.matching(token).createQuery());
         }
        stream.end(); 
        stream.close(); 
       }
       catch (IOException ioe)
       {
    	   logger.warn("Needs Text Search: encountered problem tokenizing search term " + searchText);
    	   logger.warn(ioe);
       }
       
       /////////////  the lucene query built from non-simplistic English words 
       org.apache.lucene.search.Query luceneQuery = bool.createQuery();
       
       tlst = fullTextSession.createFullTextQuery(luceneQuery, Need.class).list();
       if (tlst != null) 
       {
    	   logger.info("TEXT Search for " + searchText + " found " + tlst.size() + " Needs records in database");
    	   Collections.sort(tlst);
       }
       else
       {
          logger.info("TEXT Search for " + searchText + " found nothing in Needs");
       }
    }
    
    
    // organize what type of list is returned...either total, partial (subset) or intersection of various search results  
    if (example == null && (searchText == null || searchText.trim().length() == 0))
    {
    	// Everything...
    	if (alst != null && alst.size() > 0)
    	{
    		logger.info ("Returing all " + alst.size() + " Needs records");
        	Collections.sort(alst);
    	}
    	else
    	{
    		logger.warn("No Need records found in the database");
    	}
    	retrieved = total;
        return alst; 
    }
    else if (xlst == null && tlst != null)
    {
    	// just text search results
    	logger.info("Returing " + tlst.size() + " Needs records as a result of PURE text search (no QBE) for " + searchText);
    	retrieved = tlst.size();
    	return tlst;
    }
    else if (xlst != null && tlst == null)
    {
    	// just example query results
    	logger.info("Returning " + xlst.size() + " Needs records as a result of PURE Query-By-Example (QBE), no text string");
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
        	logger.info("Returing " + tlst.size() + " Needs records as a result of ONLY text search, QBE pulled up ZERO records for " + searchText);
        	retrieved = tlst.size();
    		return tlst;
    	}

    	if (tlst.size() == 0 && xlst.size() > 0)
    	{
        	logger.info("Returning " + xlst.size() + " Needs records as a result of ONLY Query-By-Example (QBE), text search pulled up NOTHING for string " + searchText);
        	retrieved = xlst.size();
	        return xlst;
    	}
    	
    	
    	List <Need> ivec = new Vector<Need>();
    	// if both are empty, return this Empty vector. 
    	if (xlst.size() == 0 && tlst.size() == 0)
    	{
        	logger.info("Neither QBE nor text search for string " + searchText +  " pulled up ANY Needs Records.");
        	retrieved = 0;
    		return ivec;
    	}
    	


    	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	// now deal with BOTH text and QBE being non-empty lists - implementing intersection by Database Primary Key -  Id
    	Iterator<Need> xiterator = xlst.iterator();
    	while (xiterator.hasNext()) 
    	{
    		Need x = xiterator.next();
    		Long xid = x.getId();
    		
        	Iterator<Need> titerator = tlst.iterator();
    		while(titerator.hasNext())
    		{
        		Need t = titerator.next();
        		Long tid = t.getId();
    			
        		if (tid == xid)
        		{
        			ivec.add(t); break;
        		}
        		
    		}
    			
    	}
    	// sort again - 
    	if (ivec.size() > 0)  Collections.sort(ivec);
    	logger.info("Returning " + ivec.size() + " Needs records from COMBINED (text, QBE) Search");
    	retrieved = ivec.size();
    	return ivec;
    }
    
  }



  ////////////////////////////////////////////////
  //  QBE Setter 
  //  

  public void setExample(Need needExample) 
  {
    this.example = needExample;
  }

  
  public boolean getLoggedIn()
  {
	  if (StringUtils.isBlank(request.getRemoteUser()))
	  {
		  logger.info("User is NULL ");
		  return false;
	  }
	  else
	  {
		  logger.info("User is NOTTT NULL............... user = " + request.getRemoteUser());
		  return true;
	  }
  }
  public StreamResponse onSelectedFromRdf() 
  {
      // Create PDF
      InputStream is = getRdfStream(getList());
      // Return response
      return new RDFStreamResponse(is,"neXusNeeds" + System.currentTimeMillis());
  }


  public StreamResponse onSelectedFromPdf() 
  {
      // Create PDF
      InputStream is = getPdfTable(getList());
      
      // Return response
      
      
      return new PDFStreamResponse(is,"neXusNeeds" + System.currentTimeMillis());
  }

  
  ///////////////////////////////////////////////////////
  // private methods 
  
  

  private InputStream getRdfStream(List list)
  {
	  ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	  
	  Iterator<Need> iterator = list.iterator();
  	  while(iterator.hasNext())
  	  {
  	    Need need = iterator.next();
            Model model =  getModel(need);
            model.write(baos, "TURTLE", "http://www.neclimateus.org/");
  	  }
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      return bais;
  }
 

  private InputStream getPdfTable(List list) 
  {

      // step 1: creation of a document-object
      Document document = new Document();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
              // step 2:
              // we create a writer that listens to the document
              // and directs a PDF-stream to a file
              PdfWriter writer = PdfWriter.getInstance(document, baos);
              // step 3: we open the document
              document.open();
              
              java.awt.Image awtImage = Toolkit.getDefaultToolkit().createImage(logoAsset.getResource().toURL());
              if (awtImage != null)
              {
            	  com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(awtImage, null); 
            	  if (logo != null) document.add(logo);
              }
              
              
              
              DateFormat formatter = new SimpleDateFormat
                      ("EEE MMM dd HH:mm:ss zzz yyyy");
                  Date date = new Date(System.currentTimeMillis());
                  TimeZone eastern = TimeZone.getTimeZone("America/New_York");
                  formatter.setTimeZone(eastern);

              document.add(new Paragraph("NEClimateUS.org Needs Report " + formatter.format(date)));
              
              String subheader = "Printing " + retrieved + " of total " + total + " records.";
              if (StringUtils.isNotBlank(searchText))
              {
            	  subheader += "  Searching for \"" + searchText + "\""; 
              }
              
              document.add(new Paragraph(subheader));
              
              
              // drop-downs, 
              if (data != null)
              {
            	  document.add(new Paragraph("Data: " + messages.get(data.toString())));
              }
              else
              {
            	  document.add(new Paragraph("Data: All"));
              }

              if (products != null)
              {
            	  document.add(new Paragraph("Products: " + messages.get(products.toString())));
              }
              else
              {
            	  document.add(new Paragraph("Products: All"));
              }
              
              if (services != null)
              {
            	  document.add(new Paragraph("Services: " + messages.get(services.toString())));
              }
              else
              {
            	  document.add(new Paragraph("Services: All"));
              }
              
              if (discipline != null)
              {
            	  document.add(new Paragraph("Scientific Discipline: " + messages.get(discipline.toString())));
              }
              else
              {
            	  document.add(new Paragraph("Scientific Discipline: All"));
              }
              
              if (sector != null)
              {
            	  document.add(new Paragraph("Sector: " + messages.get(sector.toString())));
              }
              else
              {
            	  document.add(new Paragraph("Sector: All"));
              }
              
              if (regions != null)
              {
            	  document.add(new Paragraph("Area of Applicability: " + messages.get(regions.toString())));
              }
              else
              {
            	  document.add(new Paragraph("Area of Applicability: All"));
              }

              
              document.add(Chunk.NEWLINE);document.add(Chunk.NEWLINE);
              
              // create table, 2 columns
           	Iterator<Need> iterator = list.iterator();
           	int count=0;
       		while(iterator.hasNext())
      		{
       			count++;
          		Need need = iterator.next();
          		
          		String name = need.getName();
          		String description = need.getDescription();
          		
                PdfPTable table = new PdfPTable(2);
                table.setWidths(new int[]{1, 4});
                //table.setSplitRows(false);
          	
                
                
                PdfPCell nameTitle = new PdfPCell(new Phrase("Need #" + count)); 
                PdfPCell nameCell = new PdfPCell(new Phrase(name));
                
                nameTitle.setBackgroundColor(BaseColor.CYAN);  nameCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                
                table.addCell(nameTitle);  table.addCell(nameCell);
          		if (StringUtils.isNotBlank(description))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Description")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(description))));
          		}
          		
          		
          		
          	    // compile the types list
          		com.itextpdf.text.List types = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (need.isInsituObservations()) 
          		{
          			ListItem item = new ListItem("DATA: In situ Observations");	types.add(item);
          		}
          		if (need.isSatelliteRemoteObservations()) 
          		{
          			ListItem item = new ListItem("DATA: Satellite Remote Observations");	types.add(item);
          		}
          		if(need.isObservingSystems())
          		{
          			ListItem item = new ListItem("DATA: Observing Systems");	types.add(item);
          		}
          		if (need.isSurveysAndPreliminaryAssessments())
          		{
          			ListItem item = new ListItem("DATA: Surveys and Preliminary Assessments");	types.add(item);
          		}
          		if (need.isIndicatorBasedResearch())
          		{
          			ListItem item = new ListItem("DATA: Indicator Based Research");	types.add(item);
          		}
          		if (need.isReanalysisProducts())
          		{
          			ListItem item = new ListItem("DATA: Reanalysis Products");	types.add(item);
          		}
          		if (need.isDepthAndElevationData())
          		{
          			ListItem item = new ListItem("DATA: Depth and Elevation Data");	types.add(item);
          		}
          		if (need.isDataStewardshipAndProvisions())
          		{
          			ListItem item = new ListItem("DATA: Data Stewardship and Provisions");	types.add(item);
          		}
          		if (need.isOtherData())
          		{
          			ListItem item = new ListItem("DATA: Other");	types.add(item);
          		}
          		
          		if (need.isHindcasts())
          		{
          			ListItem item = new ListItem("PRODUCTS: Hindcasts (climatologies, models)");	types.add(item);
          		}
          		if (need.isForecastsAndOutlooks())
          		{
          			ListItem item = new ListItem("PRODUCTS: Forecasts and outlooks (monthly to annual, models)");	types.add(item);
          		}
          		if (need.isProjections())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("PROJ"));	types.add(item);
          		}
          		if (need.isMaps())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("MAPS"));	types.add(item);
          		}
          		if (need.isAssessments())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("PLANS"));	types.add(item);
          		}
          		if (need.isOtherProducts())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("OPRD"));	types.add(item);
          		}
          		
          		
          		if(need.isEngagement())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("ENG"));	types.add(item);
          		}
          		if(need.isEducation())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("EDU"));	types.add(item);
          		}
          		if(need.isTrainingAndCapacityBuilding())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("TRA"));	types.add(item);
          		}
          		if(need.isViewersAndWebBasedTools())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("DSS"));	types.add(item);
          		}
          		if(need.isManagementGuidance())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("MGMT"));	types.add(item);
          		}
          		if(need.isPolicyGuidance())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("GUID"));	types.add(item);
          		}
          		if(need.isOtherServices())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("OSER"));	types.add(item);
          		}
          		
          		if (types.size() > 0)
          		{
          		  table.addCell(new PdfPCell(new Phrase("Type")));
          		  PdfPCell typesCell = new PdfPCell(); typesCell.addElement(types);
          		  table.addCell(typesCell);
          		}
          		

          		
          	    // compile the priority list
          		com.itextpdf.text.List priority = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (need.isHigh()) 
          		{
          			ListItem item = new ListItem("High");	priority.add(item);
          		}
          		if (need.isLow()) 
          		{
          			ListItem item = new ListItem("Low");	priority.add(item);
          		}
          		if (need.isMid()) 
          		{
          			ListItem item = new ListItem("Mid");	priority.add(item);
          		}
          		if (need.isUnknown()) 
          		{
          			ListItem item = new ListItem("Unknown");	priority.add(item);
          		}
          		if (priority.size() > 0)
          		{
          		  table.addCell(new PdfPCell(new Phrase("Priority")));
          		  PdfPCell prCell = new PdfPCell(); prCell.addElement(priority);
          		  table.addCell(prCell);
          		}

          		
          		
          	    // compile the focus area list
          		com.itextpdf.text.List focii = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (need.isSustainability()) 
          		{
          			ListItem item = new ListItem(messages.get("SUSTAINABILITY"));	focii.add(item);
          		}
          		if (need.isResilience()) 
          		{
          			ListItem item = new ListItem(messages.get("RESILIENCE"));	focii.add(item);
          		}
          		if (need.isWater()) 
          		{
          			ListItem item = new ListItem(messages.get("WATER"));	focii.add(item);
          		}
          		if (need.isExtremes()) 
          		{
          			ListItem item = new ListItem(messages.get("EXTREMES"));	focii.add(item);
          		}
          		if (need.isConservation()) 
          		{
          			ListItem item = new ListItem(messages.get("CONSERVATION"));	focii.add(item);
          		}
          		
          		if (focii.size() > 0)
          		{
          		  PdfPCell fociiCell = new PdfPCell(); fociiCell.addElement(focii);
          		  table.addCell(new PdfPCell(new Phrase("Focus Area")));
          		  table.addCell(fociiCell);
          		}

          		
          		
          		
          	    // compile the disciples list
          		com.itextpdf.text.List disciple = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (need.isPhysical()) 
          		{
          			ListItem item = new ListItem(messages.get("PHY"));	disciple.add(item);
          		}
          		if (need.isEcologicalAndBiological()) 
          		{
          			ListItem item = new ListItem(messages.get("BIO"));	disciple.add(item);
          		}
          		if (need.isGeological()) 
          		{
          			ListItem item = new ListItem(messages.get("GEO"));	disciple.add(item);
          		}
          		if (need.isChemical()) 
          		{
          			ListItem item = new ListItem(messages.get("CHEM"));	disciple.add(item);
          		}
          		if (need.isClimateSocietyInteractions()) 
          		{
          			ListItem item = new ListItem(messages.get("SOCIO"));	disciple.add(item);
         		}

          		if(disciple.size() > 0)
          		{
          		  PdfPCell disciplineCell = new PdfPCell(); disciplineCell.addElement(disciple);
          		  table.addCell(new PdfPCell(new Phrase("Discipline")));  table.addCell(disciplineCell);
          		}

          		
          		
          		
          		
          		
          	    // compile the sectors list
          		com.itextpdf.text.List sectors = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (need.isPublicHealth()) 
          		{
          			ListItem item = new ListItem(messages.get("PUBLIC"));	sectors.add(item);
          		}
          		if (need.isInfrastructure()) 
          		{
          			ListItem item = new ListItem(messages.get("INFRA"));	sectors.add(item);
          		}
          		if (need.isManagedEcosystems()) 
          		{
          			ListItem item = new ListItem(messages.get("MECO"));	sectors.add(item);
          		}
          		if (need.isNaturalEcosystems()) 
          		{
          			ListItem item = new ListItem(messages.get("NECO"));	sectors.add(item);
          		}
          		if (need.isBiota()) 
          		{
          			ListItem item = new ListItem(messages.get("BIOTA")); sectors.add(item);
          		}
          		if (need.isCultural()) 
          		{
          			ListItem item = new ListItem(messages.get("CULT"));	sectors.add(item);
          		}
          		if (need.isEconomicResources()) 
          		{
          			ListItem item = new ListItem(messages.get("ECORES"));	sectors.add(item);
          		}
          		if (need.isRecreationAndTourism()) 
          		{
          			ListItem item = new ListItem(messages.get("REC"));	sectors.add(item);
          		}
          		if (need.isCrossDisciplinary()) 
          		{
          			ListItem item = new ListItem(messages.get("CROSS"));	sectors.add(item);
          		}
          		if (need.isOtherSector()) 
          		{
          			ListItem item = new ListItem(messages.get("OSEC"));	sectors.add(item);
          		}

                if(sectors.size() > 0)
                {
          		  PdfPCell sectorsCell = new PdfPCell(); sectorsCell.addElement(sectors);
          		  table.addCell(new PdfPCell(new Phrase("Sector"))); table.addCell(sectorsCell);
                }
          		
          		

          		
          		
          	    // compile the regions list
          		com.itextpdf.text.List regions = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (need.isInternational()) 
          		{
          			ListItem item = new ListItem(messages.get("INT"));	regions.add(item);
          		}
          		if (need.isNational()) 
          		{
          			ListItem item = new ListItem(messages.get("NAT"));	regions.add(item);
          		}
          		if (need.isRegionalOrState()) 
          		{
          			String msg = messages.get("REG");
          			
          			if (need.isNewEngland()) msg += " " + messages.get("NENG"); 
          			if (need.isMidAtlantic()) msg += " " + messages.get("MIDA"); 
          			if (need.isCentral()) msg += " " + messages.get("CENT"); 
          			if (need.isGreatLakes()) msg += " " + messages.get("GRTL"); 
          			if (need.isSouthEast()) msg += " " + messages.get("STHE"); 
          			
          			ListItem item = new ListItem(msg);	regions.add(item);
          		}
          		if (need.isLocalCity()) 
          		{
          			ListItem item = new ListItem(messages.get("LOC"));	regions.add(item);
          		}
          		if (need.isProblemFocused()) 
          		{
          			ListItem item = new ListItem(messages.get("OTH"));	regions.add(item);
          		}
          		
                if(regions.size() > 0)
                {
          		  PdfPCell regionCell = new PdfPCell(); regionCell.addElement(regions);
          		table.addCell(new PdfPCell(new Phrase("Region")));table.addCell(regionCell);
                }
          		
          		
          		
          		if (StringUtils.isNotBlank(need.getSource()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Source")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(need.getSource()))));
          		}
          		
          		
          		
          		
          	    // compile the "links" list
          		com.itextpdf.text.List linkz = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (StringUtils.isNotBlank(need.getData())) 
          		{
          			// need is of type data 
          			ListItem item = new ListItem("Data: " + need.getData());	linkz.add(item);
          		}
          		if (StringUtils.isNotBlank(need.getProducts())) 
          		{
          			// type products
          			ListItem item = new ListItem("Products: " + need.getProducts());	linkz.add(item);
          		}
          		if (StringUtils.isNotBlank(need.getServices())) 
          		{
          			// service
          			ListItem item = new ListItem("Services: " + need.getServices());	linkz.add(item);
          		}
          		
          		if(linkz.size() > 0)
          		{
          		  PdfPCell linkzCell = new PdfPCell(); linkzCell.addElement(linkz);
          		table.addCell(new PdfPCell(new Phrase("Links"))); table.addCell(linkzCell);
          		}
          		
          		
          		if (StringUtils.isNotBlank(need.getOrganization()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Lead Agencies")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(need.getOrganization()))));
          		}
          		if (StringUtils.isNotBlank(need.getContact()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Contacts")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(need.getContact()))));
          		}
          		document.add(table);
          		document.add(Chunk.NEWLINE);
      		}
              
              
      } 
      catch (DocumentException de) 
      {
         logger.fatal(de.getMessage());
      }
      catch (IOException ie)
      {
    	 logger.warn("Could not find NOAA logo (likely)");
    	 logger.warn(ie);
      }
      // step 5: we close the document
      document.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      return bais;
}

  
  
  
  
  
  private Model getModel(Need need)
  {
      Model model = ModelFactory.createDefaultModel();
      
      Resource resource = ResourceFactory.createResource("http://neclimateus.org/nexus/need/view/"+ need.getId());
      
      if (StringUtils.isNotBlank(need.getName())) 
      {
   	   model.add (resource, NXS.Name, StringUtils.trimToEmpty(need.getName()));
      }
      else
      {
   	   model.add (resource, NXS.Name, "No Title???");
      }
      
      if (StringUtils.isNotBlank(need.getCode())) model.add(resource, NXS.Acronym, StringUtils.trimToEmpty(need.getCode()));
      if (StringUtils.isNotBlank(need.getOrganization())) model.add(resource, NXS.Organization, StringUtils.trimToEmpty(need.getOrganization()));
      if (StringUtils.isNotBlank(need.getContact())) model.add(resource, NXS.Contact, StringUtils.trimToEmpty(need.getContact()));
      //if (contact has email, as sensed by regex) model.add(resource, NXS.Email, StringUtils.trimToEmpty(need.getEmail()));
      if (StringUtils.isNotBlank(need.getDescription())) model.add(resource, NXS.Description, StringUtils.trimToEmpty(need.getDescription()));
      if (StringUtils.isNotBlank(need.getUrl())) model.add(resource, NXS.Link, StringUtils.trimToEmpty(need.getUrl()));
      if (StringUtils.isNotBlank(need.getWorksheet())) model.add(resource, NXS.Worksheet, StringUtils.trimToEmpty(need.getWorksheet()));
      if (StringUtils.isNotBlank(need.getKeywords())) 
      {
    	  model.add(resource, NXS.Keywords, StringUtils.trimToEmpty(need.getKeywords()));
    	  
    	  // get the bibliography
    	  String [] tokens = StringUtils.split(need.getKeywords());
    	  for (int i=0; i<tokens.length; i++)
    	  {   
    		  
    		  if (tokens[i].startsWith("BIB"))
    		  {  
    		      /*
    			  // get the BIB number 
    			  int bib = NumberUtils.toInt(StringUtils.substringAfter(tokens[i], "BIB"));
    			  Resource bibResource = ResourceFactory.createResource("http://neclimateus.org/nexus/bib/view/"+ bib);
    			  */
    		     Resource bibResource = ResourceFactory.createResource("http://neclimateus.org/nexus/bibs/" + tokens[i]);
    		     model.add(resource, NXS.Bibliography, bibResource);
    		  }
    		  
    	  }
      }
      
      if (StringUtils.isNotBlank(need.getObjectives())) model.add(resource, NXS.Objectives, StringUtils.trimToEmpty(need.getObjectives()));
      if (StringUtils.isNotBlank(need.getSource())) model.add(resource, NXS.Source, StringUtils.trimToEmpty(need.getSource()));
      if (StringUtils.isNotBlank(need.getRequestor())) model.add(resource, NXS.Requestor, StringUtils.trimToEmpty(need.getRequestor()));
      if (StringUtils.isNotBlank(need.getFeedback())) model.add(resource, NXS.Feedback, StringUtils.trimToEmpty(need.getFeedback()));
      
      if (StringUtils.isNotBlank(need.getData())) model.add(resource, NXS.Xdata, StringUtils.trimToEmpty(need.getData()));
      if (StringUtils.isNotBlank(need.getProducts())) model.add(resource, NXS.Xproducts, StringUtils.trimToEmpty(need.getProducts()));
      if (StringUtils.isNotBlank(need.getServices())) model.add(resource, NXS.Xservices, StringUtils.trimToEmpty(need.getServices()));
      if (StringUtils.isNotBlank(need.getPrograms())) model.add(resource, NXS.Xprograms, StringUtils.trimToEmpty(need.getPrograms()));
      if (StringUtils.isNotBlank(need.getProjects())) model.add(resource, NXS.Xprojects, StringUtils.trimToEmpty(need.getProjects()));
      
      if (StringUtils.isNotBlank(need.getComments())) model.add(resource, NXS.Comments, StringUtils.trimToEmpty(need.getComments()));
      
      // status
      if(need.isOngoing()) model.add(resource, NXS.Status, getLabel("ongoing"));
      if(need.isPlanned()) model.add(resource, NXS.Status, getLabel("planned"));
      if(need.isProposed()) model.add(resource, NXS.Status, getLabel("proposed"));
      if(need.isCompleted()) model.add(resource, NXS.Status, getLabel("completed"));
    	
      // priority
      if(need.getHigh()) model.add(resource, NXS.Priority, getLabel("high"));
      if(need.getMid()) model.add(resource, NXS.Priority, getLabel("mid"));
      if(need.getLow()) model.add(resource, NXS.Priority, getLabel("low"));
      if(need.getUnknown()) model.add(resource, NXS.Priority, getLabel("unknown"));
      
      // focus area
      if(need.isSustainability()) model.add(resource, NXS.Focus, getLabel("sustainability"));
      if(need.isResilience()) model.add(resource, NXS.Focus, getLabel("resilience"));
      if(need.isImpacts()) model.add(resource, NXS.Focus, getLabel("impacts"));
      if(need.isExtremes()) model.add(resource, NXS.Focus, getLabel("extremes"));
      if(need.isConservation()) model.add(resource, NXS.Focus, getLabel("conservation"));
      
      //aoa
      if (need.isInternational()) model.add(resource, NXS.Area_of_Applicability, getLabel("international")); 
      if (need.isCanada()) model.add(resource, NXS.Area_of_Applicability, getLabel("canada"));  
      if (need.isNewBrunswick())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newBrunswick"));  
      }
      if (need.isNovaScotia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("novaScotia"));  
      }
      if (need.isQuebec())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("quebec"));  
      }
      if (need.isPrinceEdwardIsland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("princeEdwardIsland"));  
      }
      if (need.isNewfoundland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newfoundland"));  
      }
      if (need.isLabrador())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("labrador"));  
      }
      if (need.isAtlanticCanada())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("atlanticCanada"));  
      }
      if (need.isNational())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("national"));  
      }
      if (need.isRegionalOrState())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("regionalOrState"));  
      }
      if (need.isGulfOfMaine())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("gulfOfMaine"));  
      }
      if (need.isNewEngland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newEngland"));  
      }
      if (need.isMaine())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("maine"));  
      }
      if (need.isNewHampshire())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newHampshire"));  
      }
      if (need.isMassachusetts())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("massachusetts"));  
      }
      if (need.isVermont())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("vermont"));  
      }
      if (need.isConnecticut())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("connecticut"));  
      }
      if (need.isRhodeIsland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("rhodeIsland"));  
      }
      if (need.isMidAtlantic())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("midAtlantic"));  
      }
      if (need.isNewYork())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newYork"));  
      }
      if (need.isNewJersey())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newJersey"));  
      }
      if (need.isPennsylvania())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("pennsylvania"));  
      }
      if (need.isMarlyland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("marlyland"));  
      }
      if (need.isDelaware())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("delaware"));  
      }
      if (need.isVirginia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("virginia"));  
      }
      if (need.isDistrictOfColumbia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("districtOfColumbia"));  
      }
      if (need.isCentral())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("central"));  
      }
      if (need.isWestVirginia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("westVirginia"));  
      }
      if (need.isGreatLakes())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("greatLakes"));  
      }
      if (need.isOhio())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("ohio"));  
      }
      if (need.isSouthEast())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("southEast"));  
      }
      if (need.isNorthCarolina())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("northCarolina"));  
      }
      if (need.isSouthCarolina())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("southCarolina"));  
      }
      if (need.isLocalCity())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("localCity"));  
      }
      if (need.isProblemFocused())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("problemFocused"));  
      }
      else 
      {
   	 //model.add(resource, NXS.Area_of_Applicability, "Unspecified");  
      }

      
      // sector
      
      if (need.isPublicHealth()) model.add(resource, NXS.Sector, getLabel("publicHealth"));  
      if (need.isEmergencyManagement()) model.add(resource, NXS.Sector, getLabel("emergencyManagement"));  
      if (need.isIndirectClimateHazards()) model.add(resource, NXS.Sector, getLabel("indirectClimateHazards"));  
      if (need.isVectorBorneIllness()) model.add(resource, NXS.Sector, getLabel("vectorBorneIllness"));  
      if (need.isHeatRelated()) model.add(resource, NXS.Sector, getLabel("heatRelated"));  
      if (need.isWaterQuality()) model.add(resource, NXS.Sector, getLabel("waterQuality"));
      
      if (need.isInfrastructure()) model.add(resource, NXS.Sector, getLabel("infrastructure"));  
      if (need.isEnergy()) model.add(resource, NXS.Sector, getLabel("energy"));  
      if (need.isCommunication()) model.add(resource, NXS.Sector, getLabel("communication"));  
      if (need.isPublicHealth()) model.add(resource, NXS.Sector, getLabel("publicHealth"));  
      if (need.isFreshWaterResources()) model.add(resource, NXS.Sector, getLabel("freshWaterResources"));  
      if (need.isStormWater()) model.add(resource, NXS.Sector, getLabel("stormWater"));  
      if (need.isWastewater()) model.add(resource, NXS.Sector, getLabel("wastewater"));
      if (need.isWaterSupply()) model.add(resource, NXS.Sector, getLabel("waterSupply"));  
      if (need.isTransportation()) model.add(resource, NXS.Sector, getLabel("transportation"));  
      if (need.isBuiltCoast()) model.add(resource, NXS.Sector, getLabel("builtCoast"));
      
      if (need.isManagedEcosystems()) model.add(resource, NXS.Sector, getLabel("managedEcosystems"));  
      if (need.isFisheries()) model.add(resource, NXS.Sector, getLabel("fisheries"));  
      if (need.isAquaculture()) model.add(resource, NXS.Sector, getLabel("aquaculture"));  
      if (need.isAgriculture()) model.add(resource, NXS.Sector, getLabel("agriculture"));  
      if (need.isForests()) model.add(resource, NXS.Sector, getLabel("forests"));  
      if (need.isOtherManagedEcosystems()) model.add(resource, NXS.Sector, getLabel("otherManagedEcosystems"));
      
      if (need.isNaturalEcosystems()) model.add(resource, NXS.Sector, getLabel("naturalEcosystems"));  
      if (need.isCoasts()) model.add(resource, NXS.Sector, getLabel("coasts"));  
      if (need.isEstuaries()) model.add(resource, NXS.Sector, getLabel("estuaries"));  
      if (need.isWetlands()) model.add(resource, NXS.Sector, getLabel("wetlands"));  
      if (need.isOceans()) model.add(resource, NXS.Sector, getLabel("oceans"));  
      if (need.isInland()) model.add(resource, NXS.Sector, getLabel("inland"));  
      if (need.isBeaches()) model.add(resource, NXS.Sector, getLabel("beaches"));  

      if (need.isBiota()) model.add(resource, NXS.Sector, getLabel("biota"));  
      if (need.isMarine()) model.add(resource, NXS.Sector, getLabel("marine"));  
      if (need.isTerrestrial()) model.add(resource, NXS.Sector, getLabel("terrestrial"));  
      if (need.isEndangered()) model.add(resource, NXS.Sector, getLabel("endangered"));  
      if (need.isCandidateSpecies()) model.add(resource, NXS.Sector, getLabel("candidateSpecies"));
      if (need.isConcernSpecies()) model.add(resource, NXS.Sector, getLabel("concernSpecies"));
      
      if (need.isCultural()) model.add(resource, NXS.Sector, getLabel("cultural"));
      
      if (need.isRecreationAndTourism()) model.add(resource, NXS.Sector, getLabel("recreationAndTourism"));
      if (need.isUrban()) model.add(resource, NXS.Sector, getLabel("urban"));
      if (need.isIndigenousPeoples()) model.add(resource, NXS.Sector, getLabel("indigenousPeoples"));
      if (need.isMinority()) model.add(resource, NXS.Sector, getLabel("minority"));
      
      if (need.isEconomicResources()) model.add(resource, NXS.Sector, getLabel("economicResources"));
      if (need.isCrossDisciplinary()) model.add(resource, NXS.Sector, getLabel("crossDisciplinary"));
      if (need.isOtherSector()) model.add(resource, NXS.Sector, getLabel("otherSector"));
      

      if (need.isEcv()) model.add(resource, NXS.Capability, getLabel("ecv"));
      if (need.isImpacts()) model.add(resource, NXS.Capability, getLabel("impacts"));      
      if (need.isVulnerabilityAssessments()) model.add(resource, NXS.Capability, getLabel("vulnerabilityAssessments"));
      if (need.isRiskAssessments()) model.add(resource, NXS.Capability, getLabel("riskAssessments"));
      if (need.isNeeds()) model.add(resource, NXS.Capability, getLabel("needs"));
      
      if (need.isScenarioPlanning()) model.add(resource, NXS.Capability, getLabel("scenarioPlanning"));
      if (need.isExperimentalImpacts()) model.add(resource, NXS.Capability, getLabel("experimentalImpacts"));
      if (need.isMonitor()) model.add(resource, NXS.Capability, getLabel("monitor"));
      if (need.isDownscale()) model.add(resource, NXS.Capability, getLabel("downscale"));
      if (need.isConditions()) model.add(resource, NXS.Capability, getLabel("conditions"));
      if (need.isForecastImpacts()) model.add(resource, NXS.Capability, getLabel("forecastImpacts"));
      if (need.isEconomicImpacts()) model.add(resource, NXS.Capability, getLabel("economicImpacts"));
      if (need.isPublicSecurity()) model.add(resource, NXS.Capability, getLabel("publicSecurity"));
      
      if (need.isMitigation()) model.add(resource, NXS.Capability, getLabel("mitigation"));
      if (need.isTranslation()) model.add(resource, NXS.Capability, getLabel("translation"));
      if (need.isTools()) model.add(resource, NXS.Capability, getLabel("tools"));
      if (need.isStakeholder()) model.add(resource, NXS.Capability, getLabel("stakeholder"));
      if (need.isGuidance()) model.add(resource, NXS.Capability, getLabel("guidance"));
      if (need.isLiteracy()) model.add(resource, NXS.Capability, getLabel("literacy"));
      if (need.isTranslate()) model.add(resource, NXS.Capability, getLabel("translate"));
      if (need.isImprove()) model.add(resource, NXS.Capability, getLabel("improve"));
      
      // scientific discipline
      if (need.isPhysical()) model.add(resource, NXS.Discipline, getLabel("physical"));
      if (need.isAtmospheric()) model.add(resource, NXS.Discipline, getLabel("atmospheric"));
      if (need.isSurfaceAtmosphere()) model.add(resource, NXS.Discipline, getLabel("surfaceAtmosphere"));
      if (need.isUpperAir()) model.add(resource, NXS.Discipline, getLabel("upperAir"));
      if (need.isComposition()) model.add(resource, NXS.Discipline, getLabel("composition"));
      if (need.isCoastalAndOceanic()) model.add(resource, NXS.Discipline, getLabel("coastalAndOceanic"));
      if (need.isSurface()) model.add(resource, NXS.Discipline, getLabel("surface"));
      if (need.isSubSurface()) model.add(resource, NXS.Discipline, getLabel("subSurface"));
      if (need.isEcologicalAndBiological()) model.add(resource, NXS.Discipline, getLabel("ecologicalAndBiological"));
      if (need.isPopulation()) model.add(resource, NXS.Discipline, getLabel("population"));
      if (need.isEcosystem()) model.add(resource, NXS.Discipline, getLabel("ecosystem"));
      if (need.isOrganism()) model.add(resource, NXS.Discipline, getLabel("organism"));
      if (need.isMicrobial()) model.add(resource, NXS.Discipline, getLabel("microbial"));
      if (need.isOtherBiologicalOrEcological()) model.add(resource, NXS.Discipline, getLabel("otherBiologicalOrEcological"));
      if (need.isGeological()) model.add(resource, NXS.Discipline, getLabel("geological"));
      if (need.isPaleoClimate()) model.add(resource, NXS.Discipline, getLabel("paleoClimate"));
      if (need.isPollenCounting()) model.add(resource, NXS.Discipline, getLabel("pollenCounting"));
      if (need.isPorosity()) model.add(resource, NXS.Discipline, getLabel("porosity"));
      if (need.isOtherGeological()) model.add(resource, NXS.Discipline, getLabel("otherGeological"));
      if (need.isChemical()) model.add(resource, NXS.Discipline, getLabel("chemical"));
      if (need.isPh()) model.add(resource, NXS.Discipline, getLabel("ph"));
      if (need.isCarbonConcentration()) model.add(resource, NXS.Discipline, getLabel("carbonConcentration"));
      if (need.isOtherChemical()) model.add(resource, NXS.Discipline, getLabel("otherChemical"));
      if (need.isClimateSocietyInteractions()) model.add(resource, NXS.Discipline, getLabel("climateSocietyInteractions"));
      if (need.isSocialAndEconomic()) model.add(resource, NXS.Discipline, getLabel("socialAndEconomic"));
      if (need.isDecisionMaking()) model.add(resource, NXS.Discipline, getLabel("decisionMaking"));
      if (need.isRiskAssessmentOrRiskManagement()) model.add(resource, NXS.Discipline, getLabel("riskAssessmentOrRiskManagement"));
      if (need.isPolicyPlanning()) model.add(resource, NXS.Discipline, getLabel("policyPlanning"));
      if (need.isCommunicationAndEducation()) model.add(resource, NXS.Discipline, getLabel("communicationAndEducation"));
      if (need.isOtherClimateSocietyInteractions()) model.add(resource, NXS.Discipline, getLabel("otherClimateSocietyInteractions"));
      
      // data
      if (need.isInsituObservations()) model.add(resource, NXS.Data, getLabel("insituObservations"));
      if (need.isSatelliteRemoteObservations()) model.add(resource, NXS.Data, getLabel("satelliteRemoteObservations"));
      if (need.isObservingSystems()) model.add(resource, NXS.Data, getLabel("observingSystems"));
      if (need.isSurveysAndPreliminaryAssessments()) model.add(resource, NXS.Data, getLabel("surveysAndPreliminaryAssessments"));
      if (need.isIndicatorBasedResearch()) model.add(resource, NXS.Data, getLabel("indicatorBasedResearch"));
      if (need.isReanalysisProducts()) model.add(resource, NXS.Data, getLabel("reanalysisProducts"));
      if (need.isDepthAndElevationData()) model.add(resource, NXS.Data, getLabel("depthAndElevationData"));
      if (need.isDataStewardshipAndProvisions()) model.add(resource, NXS.Data, getLabel("dataStewardshipAndProvisions"));
      if (need.isOtherData()) model.add(resource, NXS.Data, getLabel("otherData"));
      
      // products
      if (need.isHindcasts()) model.add(resource, NXS.Products, getLabel("hindcasts"));
      if (need.isForecastsAndOutlooks()) model.add(resource, NXS.Products, getLabel("forecastsAndOutlooks"));
      if (need.isProjections()) model.add(resource, NXS.Products, getLabel("projections"));
      if (need.isMaps()) model.add(resource, NXS.Products, getLabel("maps"));
      if (need.isAssessments()) model.add(resource, NXS.Products, getLabel("assessments"));
      if (need.isAdaptationPlan()) model.add(resource, NXS.Products, getLabel("adaptationPlan"));
      if (need.isNeedsAssessment()) model.add(resource, NXS.Products, getLabel("needsAssessment"));
      if (need.isProductCapacity()) model.add(resource, NXS.Products, getLabel("productCapacity"));
      if (need.isProductCapabilities()) model.add(resource, NXS.Products, getLabel("productCapabilities"));
      if (need.isCapacity()) model.add(resource, NXS.Products, getLabel("capacity"));
      if (need.isCapabilities()) model.add(resource, NXS.Products, getLabel("capabilities"));
      if (need.isImpactStudy()) model.add(resource, NXS.Products, getLabel("impactStudy"));
      if (need.isRiskAndVulnerability()) model.add(resource, NXS.Products, getLabel("riskAndVulnerability"));
      if (need.isProblemFocused()) model.add(resource, NXS.Products, getLabel("problemFocusedProduct"));
      if (need.isClimateScience()) model.add(resource, NXS.Products, getLabel("climateScience"));
      if (need.isOtherProducts()) model.add(resource, NXS.Products, getLabel("otherProducts"));
      
      // services
      if (need.isEngagement()) model.add(resource, NXS.Services, getLabel("engagement"));
      if (need.isStakeholderEngagement()) model.add(resource, NXS.Services, getLabel("stakeholderEngagement"));
      if (need.isSectorSpecific()) model.add(resource, NXS.Services, getLabel("sectorSpecific"));
      if (need.isRegionSpecific()) model.add(resource, NXS.Services, getLabel("regionSpecific"));
      if (need.isPublicEngagement()) model.add(resource, NXS.Services, getLabel("publicEngagement"));
      if (need.isEducation()) model.add(resource, NXS.Services, getLabel("education"));
      if (need.isK12Education()) model.add(resource, NXS.Services, getLabel("k12Education"));
      if (need.isPublicEducation()) model.add(resource, NXS.Services, getLabel("publicEducation"));
      if (need.isTrainingAndCapacityBuilding()) model.add(resource, NXS.Services, getLabel("trainingAndCapacityBuilding"));
      if (need.isDataSupportTools()) model.add(resource, NXS.Services, getLabel("dataSupportTools"));
      if (need.isAdaptationAndMitigationGuidance()) model.add(resource, NXS.Services, getLabel("adaptationAndMitigationGuidance"));
      if (need.isViewersAndWebBasedTools()) model.add(resource, NXS.Services, getLabel("viewersAndWebBasedTools"));
      if (need.isMonitoringTools()) model.add(resource, NXS.Services, getLabel("monitoringTools"));
      if (need.isVisualizationTools()) model.add(resource, NXS.Services, getLabel("visualizationTools"));
      if (need.isPrioritizationTools()) model.add(resource, NXS.Services, getLabel("prioritizationTools"));
      if (need.isManagementGuidance()) model.add(resource, NXS.Services, getLabel("managementGuidance"));
      if (need.isPolicyGuidance()) model.add(resource, NXS.Services, getLabel("policyGuidance"));
      if (need.isOtherServices()) model.add(resource, NXS.Services, getLabel("otherServices"));
      
      
      
      return model;
  }

  
  private void clearSectors(Need x)
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
  private void clearRegions(Need x)
  {
	x.setInternational(false);
	x.setNational(false);
	x.setRegionalOrState(false);
		x.setNewEngland(false);
		x.setMidAtlantic(false);
		x.setCentral(false);
		x.setGreatLakes(false);
		x.setSouthEast(false);
	x.setLocalCity(false);
	x.setProblemFocused(false);
  }
  private void clearDisciplines(Need x)
  {
	  x.setPhysical(false);
	  x.setEcologicalAndBiological(false);
	  x.setGeological(false);
	  x.setChemical(false);
	  x.setSocialAndEconomic(false);
  }
  private void clearServices(Need x)
  {
      x.setEngagement(false);
      x.setEducation(false);
      x.setViewersAndWebBasedTools(false);
      x.setTrainingAndCapacityBuilding(false);
      x.setManagementGuidance(false);
      x.setPolicyGuidance(false);
      x.setOtherServices(false);
  }
  private void clearProducts(Need x)
  {
      x.setHindcasts(false);
      x.setForecastsAndOutlooks(false);
      x.setProjections(false);
      x.setMaps(false);
      x.setAssessments(false);
      x.setOtherProducts(false);
  }
  private void clearData(Need x)
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
  private String getLabel (String varName)
  {
	   String key = varName + "-label";
	   String value = "";
	   if (messages.contains(key)) value = messages.get(key);
	   else value = TapestryInternalUtils.toUserPresentable(varName);
	   return StringUtils.trimToEmpty(value);
  }
}