package org.ideademo.nexus.pages;

import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

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
import org.ideademo.nexus.services.util.PDFStreamResponse;

import org.apache.log4j.Logger;

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

  
  // the regions select box
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

  public StreamResponse onSelectedFromPdf() 
  {
      // Create PDF
      InputStream is = getPdfTable(getList());
      // Return response
      return new PDFStreamResponse(is,"neXusDataProductsAndServices" + System.currentTimeMillis());
  }

  /*
  public StreamResponse onSubmit() 
  {
      // Create PDF
      InputStream is = PDFGenerator.generatePDF("Original PDF streaming...");
      // Return response
      return new PDFStreamResponse(is,"bibs" + System.currentTimeMillis());
  }
  */
  

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

              document.add(new Paragraph("NEClimateUS.org Data Products & Servcices Report " + formatter.format(date)));
              
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
           	Iterator<Dap> iterator = list.iterator();
           	int count=0;
       		while(iterator.hasNext())
      		{
       			count++;
          		Dap dap = iterator.next();
          		
          		String name = dap.getName();
          		String description = dap.getDescription();
          		
                PdfPTable table = new PdfPTable(2);
                table.setWidths(new int[]{1, 4});
                //table.setSplitRows(false);
          	
                
                
                PdfPCell nameTitle = new PdfPCell(new Phrase("#" + count + ") Name")); 
                PdfPCell nameCell = new PdfPCell(new Phrase(name));
                
                nameTitle.setBackgroundColor(BaseColor.CYAN);  nameCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                
                table.addCell(nameTitle);  table.addCell(nameCell);          		
                if (StringUtils.isNotBlank(description))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Description")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(description))));
          		}
          		
          		
          		table.addCell(new PdfPCell(new Phrase("Type")));
          	    // compile the types list
          		com.itextpdf.text.List types = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (dap.isInsituObservations()) 
          		{
          			ListItem item = new ListItem("DATA: In situ Observations");	types.add(item);
          		}
          		if (dap.isSatelliteRemoteObservations()) 
          		{
          			ListItem item = new ListItem("DATA: Satellite Remote Observations");	types.add(item);
          		}
          		if(dap.isObservingSystems())
          		{
          			ListItem item = new ListItem("DATA: Observing Systems");	types.add(item);
          		}
          		if (dap.isSurveysAndPreliminaryAssessments())
          		{
          			ListItem item = new ListItem("DATA: Surveys and Preliminary Assessments");	types.add(item);
          		}
          		if (dap.isIndicatorBasedResearch())
          		{
          			ListItem item = new ListItem("DATA: Indicator Based Research");	types.add(item);
          		}
          		if (dap.isReanalysisProducts())
          		{
          			ListItem item = new ListItem("DATA: Reanalysis Products");	types.add(item);
          		}
          		if (dap.isDepthAndElevationData())
          		{
          			ListItem item = new ListItem("DATA: Depth and Elevation Data");	types.add(item);
          		}
          		if (dap.isDataStewardshipAndProvisions())
          		{
          			ListItem item = new ListItem("DATA: Data Stewardship and Provisions");	types.add(item);
          		}
          		if (dap.isOtherData())
          		{
          			ListItem item = new ListItem("DATA: Other");	types.add(item);
          		}
          		
          		if (dap.isHindcasts())
          		{
          			ListItem item = new ListItem("PRODUCTS: Hindcasts (climatologies, models)");	types.add(item);
          		}
          		if (dap.isForecastsAndOutlooks())
          		{
          			ListItem item = new ListItem("PRODUCTS: Forecasts and outlooks (monthly to annual, models)");	types.add(item);
          		}
          		if (dap.isProjections())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("PROJ"));	types.add(item);
          		}
          		if (dap.isMaps())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("MAPS"));	types.add(item);
          		}
          		if (dap.isAssessments())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("PLANS"));	types.add(item);
          		}
          		if (dap.isOtherProducts())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("OPRD"));	types.add(item);
          		}
          		
          		
          		if(dap.isEngagement())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("ENG"));	types.add(item);
          		}
          		if(dap.isEducation())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("EDU"));	types.add(item);
          		}
          		if(dap.isTrainingAndCapacityBuilding())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("TRA"));	types.add(item);
          		}
          		if(dap.isViewersAndWebBasedTools())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("DSS"));	types.add(item);
          		}
          		if(dap.isManagementGuidance())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("MGMT"));	types.add(item);
          		}
          		if(dap.isPolicyGuidance())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("GUID"));	types.add(item);
          		}
          		if(dap.isOtherServices())
          		{
          			ListItem item = new ListItem("PRODUCTS: " + messages.get("OSER"));	types.add(item);
          		}
          		
          		PdfPCell typesCell = new PdfPCell(); typesCell.addElement(types);
          		table.addCell(typesCell);
          		

          		
          		
          		
          		table.addCell(new PdfPCell(new Phrase("Sector")));
          	    // compile the types list
          		com.itextpdf.text.List sectors = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (dap.isPublicHealth()) 
          		{
          			ListItem item = new ListItem(messages.get("PUBLIC"));	sectors.add(item);
          		}
          		if (dap.isInfrastructure()) 
          		{
          			ListItem item = new ListItem(messages.get("INFRA"));	sectors.add(item);
          		}
          		if (dap.isManagedEcosystems()) 
          		{
          			ListItem item = new ListItem(messages.get("MECO"));	sectors.add(item);
          		}
          		if (dap.isNaturalEcosystems()) 
          		{
          			ListItem item = new ListItem(messages.get("NECO"));	sectors.add(item);
          		}
          		if (dap.isBiota()) 
          		{
          			ListItem item = new ListItem(messages.get("BIOTA")); sectors.add(item);
          		}
          		if (dap.isCultural()) 
          		{
          			ListItem item = new ListItem(messages.get("CULT"));	sectors.add(item);
          		}
          		if (dap.isEconomicResources()) 
          		{
          			ListItem item = new ListItem(messages.get("ECORES"));	sectors.add(item);
          		}
          		if (dap.isRecreationAndTourism()) 
          		{
          			ListItem item = new ListItem(messages.get("REC"));	sectors.add(item);
          		}
          		if (dap.isCrossDisciplinary()) 
          		{
          			ListItem item = new ListItem(messages.get("CROSS"));	sectors.add(item);
          		}
          		if (dap.isOtherSector()) 
          		{
          			ListItem item = new ListItem(messages.get("OSEC"));	sectors.add(item);
          		}


          		PdfPCell sectorsCell = new PdfPCell(); sectorsCell.addElement(sectors);
          		table.addCell(sectorsCell);
          		
          		
          		table.addCell(new PdfPCell(new Phrase("Focus Area")));
          	    // compile the types list
          		com.itextpdf.text.List focii = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (dap.isSustainability()) 
          		{
          			ListItem item = new ListItem(messages.get("SUSTAINABILITY"));	focii.add(item);
          		}
          		if (dap.isResilience()) 
          		{
          			ListItem item = new ListItem(messages.get("RESILIENCE"));	focii.add(item);
          		}
          		if (dap.isWater()) 
          		{
          			ListItem item = new ListItem(messages.get("WATER"));	focii.add(item);
          		}
          		if (dap.isExtremes()) 
          		{
          			ListItem item = new ListItem(messages.get("EXTREMES"));	focii.add(item);
          		}
          		if (dap.isConservation()) 
          		{
          			ListItem item = new ListItem(messages.get("CONSERVATION"));	focii.add(item);
          		}
          		PdfPCell fociiCell = new PdfPCell(); fociiCell.addElement(focii);
          		table.addCell(fociiCell);


          		
          		table.addCell(new PdfPCell(new Phrase("Region")));
          	    // compile the types list
          		com.itextpdf.text.List regions = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (dap.isInternational()) 
          		{
          			ListItem item = new ListItem(messages.get("INT"));	regions.add(item);
          		}
          		if (dap.isNational()) 
          		{
          			ListItem item = new ListItem(messages.get("NAT"));	regions.add(item);
          		}
          		if (dap.isRegionalOrState()) 
          		{
          			String msg = messages.get("REG");
          			
          			if (dap.isNewEngland()) msg += " " + messages.get("NENG"); 
          			if (dap.isMidAtlantic()) msg += " " + messages.get("MIDA"); 
          			if (dap.isCentral()) msg += " " + messages.get("CENT"); 
          			if (dap.isGreatLakes()) msg += " " + messages.get("GRTL"); 
          			if (dap.isSouthEast()) msg += " " + messages.get("STHE"); 
          			
          			ListItem item = new ListItem(msg);	regions.add(item);
          		}
          		if (dap.isLocalCity()) 
          		{
          			ListItem item = new ListItem(messages.get("LOC"));	regions.add(item);
          		}
          		if (dap.isProblemFocused()) 
          		{
          			ListItem item = new ListItem(messages.get("OTH"));	regions.add(item);
          		}
          		

          		PdfPCell regionCell = new PdfPCell(); regionCell.addElement(regions);
          		table.addCell(regionCell);
          		
          		
          		
          		if (StringUtils.isNotBlank(dap.getResources()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Resources")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(dap.getResources()))));
          		}
          		
          		if (StringUtils.isNotBlank(dap.getOrganization()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Lead Agencies")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(dap.getOrganization()))));
          		}
          		if (StringUtils.isNotBlank(dap.getContact()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Contacts")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(dap.getContact()))));
          		}
          		document.add(table);
          		document.add(Chunk.NEWLINE);
      		}
              
              
      } catch (DocumentException de) {
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
		x.setNewEngland(false);
		x.setMidAtlantic(false);
		x.setCentral(false);
		x.setGreatLakes(false);
		x.setSouthEast(false);
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