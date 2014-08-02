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
import org.apache.tapestry5.internal.TapestryInternalUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.Messages;


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
import org.ideademo.nexus.services.util.RDFStreamResponse;
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

//semantic web
import com.hp.hpl.jena.rdf.model.*;

import org.ideademo.nexus.vocabulary.NXS;

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
  
  public StreamResponse onSelectedFromRdf() 
  {
      // Create PDF
      InputStream is = getRdfStream(getList());
      // Return response
      return new RDFStreamResponse(is,"neXusDataProductsServices" + System.currentTimeMillis());
  }


  private InputStream getRdfStream(List list)
  {
	  ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	  
	  Iterator<Dap> iterator = list.iterator();
  	  while(iterator.hasNext())
  	  {
  	    Dap dap = iterator.next();
            Model model =  getModel(dap);
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
  private Model getModel(Dap dap)
  {
      Model model = ModelFactory.createDefaultModel();
      
      Resource resource = ResourceFactory.createResource("http://neclimateus.org/nexus/dap/view/"+ dap.getId());
      
      if (StringUtils.isNotBlank(dap.getName())) 
  	   {
   	   model.add (resource, NXS.Name, StringUtils.trimToEmpty(dap.getName()));
      }
      else
      {
   	   model.add (resource, NXS.Name, "No Title???");
      }
      
      if (StringUtils.isNotBlank(dap.getCode())) model.add(resource, NXS.Acronym, StringUtils.trimToEmpty(dap.getCode()));
      if (StringUtils.isNotBlank(dap.getContact())) model.add(resource, NXS.Contact, StringUtils.trimToEmpty(dap.getContact()));
      //if (contact has email, as sensed by regex) model.add(resource, NXS.Email, StringUtils.trimToEmpty(dap.getEmail()));
      if (StringUtils.isNotBlank(dap.getDescription())) model.add(resource, NXS.Description, StringUtils.trimToEmpty(dap.getDescription()));
      if (StringUtils.isNotBlank(dap.getUrl())) model.add(resource, NXS.Link, StringUtils.trimToEmpty(dap.getUrl()));
      if (StringUtils.isNotBlank(dap.getWorksheet())) model.add(resource, NXS.Worksheet, StringUtils.trimToEmpty(dap.getWorksheet()));
      if (StringUtils.isNotBlank(dap.getKeywords())) model.add(resource, NXS.Keywords, StringUtils.trimToEmpty(dap.getKeywords()));
      
      if (StringUtils.isNotBlank(dap.getOrganization())) model.add(resource, NXS.Organization, StringUtils.trimToEmpty(dap.getOrganization()));
      if (StringUtils.isNotBlank(dap.getObjectives())) model.add(resource, NXS.Objectives, StringUtils.trimToEmpty(dap.getObjectives()));
      if (StringUtils.isNotBlank(dap.getDates())) model.add(resource, NXS.Timeline, StringUtils.trimToEmpty(dap.getDates()));
      if (StringUtils.isNotBlank(dap.getResources())) model.add(resource, NXS.Resources, StringUtils.trimToEmpty(dap.getResources()));
      if (StringUtils.isNotBlank(dap.getFeedback())) model.add(resource, NXS.Feedback, StringUtils.trimToEmpty(dap.getFeedback()));
      
      // status
      if(dap.isOngoing()) model.add(resource, NXS.Status, getLabel("ongoing"));
      if(dap.isPlanned()) model.add(resource, NXS.Status, getLabel("planned"));
      if(dap.isProposed()) model.add(resource, NXS.Status, getLabel("proposed"));
      if(dap.isCompleted()) model.add(resource, NXS.Status, getLabel("completed"));
    	
      // priority
      if(dap.getHigh()) model.add(resource, NXS.Priority, getLabel("high"));
      if(dap.getMid()) model.add(resource, NXS.Priority, getLabel("mid"));
      if(dap.getLow()) model.add(resource, NXS.Priority, getLabel("low"));
      if(dap.getUnknown()) model.add(resource, NXS.Priority, getLabel("unknown"));
      
      // focus area
      if(dap.isSustainability()) model.add(resource, NXS.Focus, getLabel("sustainability"));
      if(dap.isResilience()) model.add(resource, NXS.Focus, getLabel("resilience"));
      if(dap.isImpacts()) model.add(resource, NXS.Focus, getLabel("impacts"));
      if(dap.isExtremes()) model.add(resource, NXS.Focus, getLabel("extremes"));
      if(dap.isConservation()) model.add(resource, NXS.Focus, getLabel("conservation"));
      
      //aoa
      if (dap.isInternational()) model.add(resource, NXS.Area_of_Applicability, getLabel("international")); 
      if (dap.isCanada()) model.add(resource, NXS.Area_of_Applicability, getLabel("canada"));  
      if (dap.isNewBrunswick())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newBrunswick"));  
      }
      if (dap.isNovaScotia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("novaScotia"));  
      }
      if (dap.isQuebec())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("quebec"));  
      }
      if (dap.isPrinceEdwardIsland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("princeEdwardIsland"));  
      }
      if (dap.isNewfoundland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newfoundland"));  
      }
      if (dap.isLabrador())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("labrador"));  
      }
      if (dap.isAtlanticCanada())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("atlanticCanada"));  
      }
      if (dap.isNational())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("national"));  
      }
      if (dap.isRegionalOrState())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("regionalOrState"));  
      }
      if (dap.isGulfOfMaine())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("gulfOfMaine"));  
      }
      if (dap.isNewEngland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newEngland"));  
      }
      if (dap.isMaine())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("maine"));  
      }
      if (dap.isNewHampshire())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newHampshire"));  
      }
      if (dap.isMassachusetts())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("massachusetts"));  
      }
      if (dap.isVermont())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("vermont"));  
      }
      if (dap.isConnecticut())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("connecticut"));  
      }
      if (dap.isRhodeIsland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("rhodeIsland"));  
      }
      if (dap.isMidAtlantic())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("midAtlantic"));  
      }
      if (dap.isNewYork())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newYork"));  
      }
      if (dap.isNewJersey())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newJersey"));  
      }
      if (dap.isPennsylvania())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("pennsylvania"));  
      }
      if (dap.isMarlyland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("marlyland"));  
      }
      if (dap.isDelaware())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("delaware"));  
      }
      if (dap.isVirginia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("virginia"));  
      }
      if (dap.isDistrictOfColumbia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("districtOfColumbia"));  
      }
      if (dap.isCentral())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("central"));  
      }
      if (dap.isWestVirginia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("westVirginia"));  
      }
      if (dap.isGreatLakes())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("greatLakes"));  
      }
      if (dap.isOhio())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("ohio"));  
      }
      if (dap.isSouthEast())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("southEast"));  
      }
      if (dap.isNorthCarolina())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("northCarolina"));  
      }
      if (dap.isSouthCarolina())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("southCarolina"));  
      }
      if (dap.isLocalCity())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("localCity"));  
      }
      if (dap.isProblemFocused())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("problemFocused"));  
      }
      else 
      {
   	 //model.add(resource, NXS.Area_of_Applicability, "Unspecified");  
      }

      
      // sector
      
      if (dap.isPublicHealth()) model.add(resource, NXS.Sector, getLabel("publicHealth"));  
      if (dap.isEmergencyManagement()) model.add(resource, NXS.Sector, getLabel("emergencyManagement"));  
      if (dap.isIndirectClimateHazards()) model.add(resource, NXS.Sector, getLabel("indirectClimateHazards"));  
      if (dap.isVectorBorneIllness()) model.add(resource, NXS.Sector, getLabel("vectorBorneIllness"));  
      if (dap.isHeatRelated()) model.add(resource, NXS.Sector, getLabel("heatRelated"));  
      if (dap.isWaterQuality()) model.add(resource, NXS.Sector, getLabel("waterQuality"));
      
      if (dap.isInfrastructure()) model.add(resource, NXS.Sector, getLabel("infrastructure"));  
      if (dap.isEnergy()) model.add(resource, NXS.Sector, getLabel("energy"));  
      if (dap.isCommunication()) model.add(resource, NXS.Sector, getLabel("communication"));  
      if (dap.isPublicHealth()) model.add(resource, NXS.Sector, getLabel("publicHealth"));  
      if (dap.isFreshWaterResources()) model.add(resource, NXS.Sector, getLabel("freshWaterResources"));  
      if (dap.isStormWater()) model.add(resource, NXS.Sector, getLabel("stormWater"));  
      if (dap.isWastewater()) model.add(resource, NXS.Sector, getLabel("wastewater"));
      if (dap.isWaterSupply()) model.add(resource, NXS.Sector, getLabel("waterSupply"));  
      if (dap.isTransportation()) model.add(resource, NXS.Sector, getLabel("transportation"));  
      if (dap.isBuiltCoast()) model.add(resource, NXS.Sector, getLabel("builtCoast"));
      
      if (dap.isManagedEcosystems()) model.add(resource, NXS.Sector, getLabel("managedEcosystems"));  
      if (dap.isFisheries()) model.add(resource, NXS.Sector, getLabel("fisheries"));  
      if (dap.isAquaculture()) model.add(resource, NXS.Sector, getLabel("aquaculture"));  
      if (dap.isAgriculture()) model.add(resource, NXS.Sector, getLabel("agriculture"));  
      if (dap.isForests()) model.add(resource, NXS.Sector, getLabel("forests"));  
      if (dap.isOtherManagedEcosystems()) model.add(resource, NXS.Sector, getLabel("otherManagedEcosystems"));
      
      if (dap.isNaturalEcosystems()) model.add(resource, NXS.Sector, getLabel("naturalEcosystems"));  
      if (dap.isCoasts()) model.add(resource, NXS.Sector, getLabel("coasts"));  
      if (dap.isEstuaries()) model.add(resource, NXS.Sector, getLabel("estuaries"));  
      if (dap.isWetlands()) model.add(resource, NXS.Sector, getLabel("wetlands"));  
      if (dap.isOceans()) model.add(resource, NXS.Sector, getLabel("oceans"));  
      if (dap.isInland()) model.add(resource, NXS.Sector, getLabel("inland"));  
      if (dap.isBeaches()) model.add(resource, NXS.Sector, getLabel("beaches"));  

      if (dap.isBiota()) model.add(resource, NXS.Sector, getLabel("biota"));  
      if (dap.isMarine()) model.add(resource, NXS.Sector, getLabel("marine"));  
      if (dap.isTerrestrial()) model.add(resource, NXS.Sector, getLabel("terrestrial"));  
      if (dap.isEndangered()) model.add(resource, NXS.Sector, getLabel("endangered"));  
      if (dap.isCandidateSpecies()) model.add(resource, NXS.Sector, getLabel("candidateSpecies"));
      if (dap.isConcernSpecies()) model.add(resource, NXS.Sector, getLabel("concernSpecies"));
      
      if (dap.isCultural()) model.add(resource, NXS.Sector, getLabel("cultural"));
      
      if (dap.isRecreationAndTourism()) model.add(resource, NXS.Sector, getLabel("recreationAndTourism"));
      if (dap.isUrban()) model.add(resource, NXS.Sector, getLabel("urban"));
      if (dap.isIndigenousPeoples()) model.add(resource, NXS.Sector, getLabel("indigenousPeoples"));
      if (dap.isMinority()) model.add(resource, NXS.Sector, getLabel("minority"));
      
      if (dap.isEconomicResources()) model.add(resource, NXS.Sector, getLabel("economicResources"));
      if (dap.isCrossDisciplinary()) model.add(resource, NXS.Sector, getLabel("crossDisciplinary"));
      if (dap.isOtherSector()) model.add(resource, NXS.Sector, getLabel("otherSector"));
      

      if (dap.isEcv()) model.add(resource, NXS.Capability, getLabel("ecv"));
      if (dap.isImpacts()) model.add(resource, NXS.Capability, getLabel("impacts"));      
      if (dap.isVulnerabilityAssessments()) model.add(resource, NXS.Capability, getLabel("vulnerabilityAssessments"));
      if (dap.isRiskAssessments()) model.add(resource, NXS.Capability, getLabel("riskAssessments"));
      if (dap.isNeeds()) model.add(resource, NXS.Capability, getLabel("needs"));
      
      if (dap.isScenarioPlanning()) model.add(resource, NXS.Capability, getLabel("scenarioPlanning"));
      if (dap.isExperimentalImpacts()) model.add(resource, NXS.Capability, getLabel("experimentalImpacts"));
      if (dap.isMonitor()) model.add(resource, NXS.Capability, getLabel("monitor"));
      if (dap.isDownscale()) model.add(resource, NXS.Capability, getLabel("downscale"));
      if (dap.isConditions()) model.add(resource, NXS.Capability, getLabel("conditions"));
      if (dap.isForecastImpacts()) model.add(resource, NXS.Capability, getLabel("forecastImpacts"));
      if (dap.isEconomicImpacts()) model.add(resource, NXS.Capability, getLabel("economicImpacts"));
      if (dap.isPublicSecurity()) model.add(resource, NXS.Capability, getLabel("publicSecurity"));
      
      if (dap.isMitigation()) model.add(resource, NXS.Capability, getLabel("mitigation"));
      if (dap.isTranslation()) model.add(resource, NXS.Capability, getLabel("translation"));
      if (dap.isTools()) model.add(resource, NXS.Capability, getLabel("tools"));
      if (dap.isStakeholder()) model.add(resource, NXS.Capability, getLabel("stakeholder"));
      if (dap.isGuidance()) model.add(resource, NXS.Capability, getLabel("guidance"));
      if (dap.isLiteracy()) model.add(resource, NXS.Capability, getLabel("literacy"));
      if (dap.isTranslate()) model.add(resource, NXS.Capability, getLabel("translate"));
      if (dap.isImprove()) model.add(resource, NXS.Capability, getLabel("improve"));
      
      // scientific discipline
      if (dap.isPhysical()) model.add(resource, NXS.Discipline, getLabel("physical"));
      if (dap.isAtmospheric()) model.add(resource, NXS.Discipline, getLabel("atmospheric"));
      if (dap.isSurfaceAtmosphere()) model.add(resource, NXS.Discipline, getLabel("surfaceAtmosphere"));
      if (dap.isUpperAir()) model.add(resource, NXS.Discipline, getLabel("upperAir"));
      if (dap.isComposition()) model.add(resource, NXS.Discipline, getLabel("composition"));
      if (dap.isCoastalAndOceanic()) model.add(resource, NXS.Discipline, getLabel("coastalAndOceanic"));
      if (dap.isSurface()) model.add(resource, NXS.Discipline, getLabel("surface"));
      if (dap.isSubSurface()) model.add(resource, NXS.Discipline, getLabel("subSurface"));
      if (dap.isEcologicalAndBiological()) model.add(resource, NXS.Discipline, getLabel("ecologicalAndBiological"));
      if (dap.isPopulation()) model.add(resource, NXS.Discipline, getLabel("population"));
      if (dap.isEcosystem()) model.add(resource, NXS.Discipline, getLabel("ecosystem"));
      if (dap.isOrganism()) model.add(resource, NXS.Discipline, getLabel("organism"));
      if (dap.isMicrobial()) model.add(resource, NXS.Discipline, getLabel("microbial"));
      if (dap.isOtherBiologicalOrEcological()) model.add(resource, NXS.Discipline, getLabel("otherBiologicalOrEcological"));
      if (dap.isGeological()) model.add(resource, NXS.Discipline, getLabel("geological"));
      if (dap.isPaleoClimate()) model.add(resource, NXS.Discipline, getLabel("paleoClimate"));
      if (dap.isPollenCounting()) model.add(resource, NXS.Discipline, getLabel("pollenCounting"));
      if (dap.isPorosity()) model.add(resource, NXS.Discipline, getLabel("porosity"));
      if (dap.isOtherGeological()) model.add(resource, NXS.Discipline, getLabel("otherGeological"));
      if (dap.isChemical()) model.add(resource, NXS.Discipline, getLabel("chemical"));
      if (dap.isPh()) model.add(resource, NXS.Discipline, getLabel("ph"));
      if (dap.isCarbonConcentration()) model.add(resource, NXS.Discipline, getLabel("carbonConcentration"));
      if (dap.isOtherChemical()) model.add(resource, NXS.Discipline, getLabel("otherChemical"));
      if (dap.isClimateSocietyInteractions()) model.add(resource, NXS.Discipline, getLabel("climateSocietyInteractions"));
      if (dap.isSocialAndEconomic()) model.add(resource, NXS.Discipline, getLabel("socialAndEconomic"));
      if (dap.isDecisionMaking()) model.add(resource, NXS.Discipline, getLabel("decisionMaking"));
      if (dap.isRiskAssessmentOrRiskManagement()) model.add(resource, NXS.Discipline, getLabel("riskAssessmentOrRiskManagement"));
      if (dap.isPolicyPlanning()) model.add(resource, NXS.Discipline, getLabel("policyPlanning"));
      if (dap.isCommunicationAndEducation()) model.add(resource, NXS.Discipline, getLabel("communicationAndEducation"));
      if (dap.isOtherClimateSocietyInteractions()) model.add(resource, NXS.Discipline, getLabel("otherClimateSocietyInteractions"));
      
      // data
      if (dap.isInsituObservations()) model.add(resource, NXS.Data, getLabel("insituObservations"));
      if (dap.isSatelliteRemoteObservations()) model.add(resource, NXS.Data, getLabel("satelliteRemoteObservations"));
      if (dap.isObservingSystems()) model.add(resource, NXS.Data, getLabel("observingSystems"));
      if (dap.isSurveysAndPreliminaryAssessments()) model.add(resource, NXS.Data, getLabel("surveysAndPreliminaryAssessments"));
      if (dap.isIndicatorBasedResearch()) model.add(resource, NXS.Data, getLabel("indicatorBasedResearch"));
      if (dap.isReanalysisProducts()) model.add(resource, NXS.Data, getLabel("reanalysisProducts"));
      if (dap.isDepthAndElevationData()) model.add(resource, NXS.Data, getLabel("depthAndElevationData"));
      if (dap.isDataStewardshipAndProvisions()) model.add(resource, NXS.Data, getLabel("dataStewardshipAndProvisions"));
      if (dap.isOtherData()) model.add(resource, NXS.Data, getLabel("otherData"));
      
      // products
      if (dap.isHindcasts()) model.add(resource, NXS.Products, getLabel("hindcasts"));
      if (dap.isForecastsAndOutlooks()) model.add(resource, NXS.Products, getLabel("forecastsAndOutlooks"));
      if (dap.isProjections()) model.add(resource, NXS.Products, getLabel("projections"));
      if (dap.isMaps()) model.add(resource, NXS.Products, getLabel("maps"));
      if (dap.isAssessments()) model.add(resource, NXS.Products, getLabel("assessments"));
      if (dap.isAdaptationPlan()) model.add(resource, NXS.Products, getLabel("adaptationPlan"));
      if (dap.isNeedsAssessment()) model.add(resource, NXS.Products, getLabel("needsAssessment"));
      if (dap.isProductCapacity()) model.add(resource, NXS.Products, getLabel("productCapacity"));
      if (dap.isProductCapabilities()) model.add(resource, NXS.Products, getLabel("productCapabilities"));
      if (dap.isCapacity()) model.add(resource, NXS.Products, getLabel("capacity"));
      if (dap.isCapabilities()) model.add(resource, NXS.Products, getLabel("capabilities"));
      if (dap.isImpactStudy()) model.add(resource, NXS.Products, getLabel("impactStudy"));
      if (dap.isRiskAndVulnerability()) model.add(resource, NXS.Products, getLabel("riskAndVulnerability"));
      if (dap.isProblemFocused()) model.add(resource, NXS.Products, getLabel("problemFocusedProduct"));
      if (dap.isClimateScience()) model.add(resource, NXS.Products, getLabel("climateScience"));
      if (dap.isOtherProducts()) model.add(resource, NXS.Products, getLabel("otherProducts"));
      
      // services
      if (dap.isEngagement()) model.add(resource, NXS.Services, getLabel("engagement"));
      if (dap.isStakeholderEngagement()) model.add(resource, NXS.Services, getLabel("stakeholderEngagement"));
      if (dap.isSectorSpecific()) model.add(resource, NXS.Services, getLabel("sectorSpecific"));
      if (dap.isRegionSpecific()) model.add(resource, NXS.Services, getLabel("regionSpecific"));
      if (dap.isPublicEngagement()) model.add(resource, NXS.Services, getLabel("publicEngagement"));
      if (dap.isEducation()) model.add(resource, NXS.Services, getLabel("education"));
      if (dap.isK12Education()) model.add(resource, NXS.Services, getLabel("k12Education"));
      if (dap.isPublicEducation()) model.add(resource, NXS.Services, getLabel("publicEducation"));
      if (dap.isTrainingAndCapacityBuilding()) model.add(resource, NXS.Services, getLabel("trainingAndCapacityBuilding"));
      if (dap.isDataSupportTools()) model.add(resource, NXS.Services, getLabel("dataSupportTools"));
      if (dap.isAdaptationAndMitigationGuidance()) model.add(resource, NXS.Services, getLabel("adaptationAndMitigationGuidance"));
      if (dap.isViewersAndWebBasedTools()) model.add(resource, NXS.Services, getLabel("viewersAndWebBasedTools"));
      if (dap.isMonitoringTools()) model.add(resource, NXS.Services, getLabel("monitoringTools"));
      if (dap.isVisualizationTools()) model.add(resource, NXS.Services, getLabel("visualizationTools"));
      if (dap.isPrioritizationTools()) model.add(resource, NXS.Services, getLabel("prioritizationTools"));
      if (dap.isManagementGuidance()) model.add(resource, NXS.Services, getLabel("managementGuidance"));
      if (dap.isPolicyGuidance()) model.add(resource, NXS.Services, getLabel("policyGuidance"));
      if (dap.isOtherServices()) model.add(resource, NXS.Services, getLabel("otherServices"));
      
      
      
      return model;
  }

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
  private String getLabel (String varName)
  {
	   String key = varName + "-label";
	   String value = "";
	   if (messages.contains(key)) value = messages.get(key);
	   else value = TapestryInternalUtils.toUserPresentable(varName);
	   return StringUtils.trimToEmpty(value);
  }
}