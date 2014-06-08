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

import org.apache.commons.lang.StringUtils;

import java.io.StringReader;
import java.io.IOException;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

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


import org.ideademo.nexus.entities.Paw;
import org.ideademo.nexus.services.util.PDFStreamResponse;

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

import org.apache.log4j.Logger;


public class Paws 
{
	 
  private static Logger logger = Logger.getLogger(Paws.class);
  private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_31); 

  
  /////////////////////////////
  //  Drives QBE Search
  @Persist (PersistenceConstants.FLASH)
  private Paw example;
  
  
  //////////////////////////////
  // Used in rendering Grid Row
  @SuppressWarnings("unused")
  @Property 
  private Paw row;

    
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
  
  
  // the scientific discipline select box
  @Property
  @Persist (PersistenceConstants.FLASH)
  private Category category; 
  /**
   * SPECIFIC=Climate-change Specific Projects
   * RETRO=Ecological and Biological
   * MONITOR=Monitoring
   * RESEARCH=Research
   */
  public enum Category
  {
    SPECIFIC, RETRO, MONITOR, RESEARCH
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
    category=null;
    sector=null;
    regions=null;
    this.example = null;
    return null; 
  }
  Object onSelectedFromSearch() 
  {
    return null; 
  }


  //  Category Select Box Listener 
  //  SPECIFIC, RETRO, MONITOR, RESEARCH
  Object onValueChangedFromCategory(String choice)
  {	
    // if there is no example set, create one.
    if (this.example == null) this.example = new Paw(); 
    logger.info("Category Choice = " + choice);
    clearCategories(example);
    if (choice == null)
    {
      // clear 
    }
    else if (choice.equalsIgnoreCase("SPECIFIC"))
    {
      example.setSpecific(true);
    }
    else if (choice.equalsIgnoreCase("RETRO"))
    {
      example.setRetrofitted(true);
    }
    else if (choice.equalsIgnoreCase("MONITOR"))
    {
      example.setMonitoring(true);
    }
    else if (choice.equalsIgnoreCase("RESEARCH"))
    {
      example.setResearch(true);
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
    if (this.example == null) this.example = new Paw(); 
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
    if (this.example == null) this.example = new Paw(); 
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
  public List<Paw> getList()
  {
	
   // first interpret search criteria 
   if (category != null) onValueChangedFromCategory(category.toString());
   if (sector != null) onValueChangedFromSector(sector.toString());
   if (regions != null) onValueChangedFromRegions(regions.toString());

    // Get all records anyway - for showing total at bottom of presentation layer
    List <Paw> alst = session.createCriteria(Paw.class).list();
    total = alst.size();

	
    // then makes lists and sublists as per the search criteria 
    List<Paw> xlst=null; // xlst = Query by Example search List
    if(example != null)
    {
       Example ex = Example.create(example).excludeFalse().ignoreCase().enableLike(MatchMode.ANYWHERE);
       
       xlst = session.createCriteria(Paw.class).add(ex).list();
       
       
       if (xlst != null)
       {
    	   logger.info("Paw Example Search Result List Size  = " + xlst.size() );
    	   Collections.sort(xlst);
       }
       else
       {
         logger.info("Paw Example Search result did not find any results...");
       }
    }
    
    List<Paw> tlst=null;
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
      
       QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Paw.class ).get();
       
       // fields being covered by text search 
       TermMatchingContext onFields = qb
		        .keyword()
		        .onFields("code","name","description", "keywords","contact", "url", "objectives", "worksheet", "feedback");
       
       BooleanJunction<BooleanJunction> bool = qb.bool();
       TokenStream stream = analyzer.tokenStream(null, new StringReader(searchText));
       CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
       try
       {
        while (stream.incrementToken()) 
         {
    	   String token = cattr.toString();
    	   logger.info("Adding search token " +  token + " to look in Paws database");
    	   bool.must(onFields.matching(token).createQuery());
         }
        stream.end(); 
        stream.close(); 
       }
       catch (IOException ioe)
       {
    	   logger.warn("Paws Text Search: Encountered problem tokenizing search term " + searchText);
    	   logger.warn(ioe);
       }
       
       /////////////  the lucene query built from non-simplistic English words 
       org.apache.lucene.search.Query luceneQuery = bool.createQuery();
       
       tlst = fullTextSession.createFullTextQuery(luceneQuery, Paw.class).list();
       if (tlst != null) 
       {
    	   logger.info("TEXT Search for " + searchText + " found " + tlst.size() + " Paws records in database");
    	   Collections.sort(tlst);
       }
       else
       {
          logger.info("TEXT Search for " + searchText + " found nothing in Paws");
       }
    }
    
    
    // organize what type of list is returned...either total, partial (subset) or intersection of various search results  
    if (example == null && (searchText == null || searchText.trim().length() == 0))
    {
    	// Everything...
    	if (alst != null && alst.size() > 0)
    	{
    	  logger.info ("Returing all " + alst.size() + " Paws records");
          Collections.sort(alst);
    	}
    	else
    	{
    	  logger.warn("No Paw records found in the database");
    	}
    	retrieved = total;
        return alst; 
    }
    else if (xlst == null && tlst != null)
    {
    	// just text search results
    	logger.info("Returing " + tlst.size() + " Paws records as a result of PURE text search (no QBE) for " + searchText);
    	retrieved = tlst.size();
    	return tlst;
    }
    else if (xlst != null && tlst == null)
    {
    	// just example query results
    	logger.info("Returning " + xlst.size() + " Paws records as a result of PURE Query-By-Example (QBE), no text string");
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
        	logger.info("Returing " + tlst.size() + " Paws records as a result of ONLY text search, QBE pulled up ZERO records for " + searchText);
        	retrieved = tlst.size();
    		return tlst;
    	}

    	if (tlst.size() == 0 && xlst.size() > 0)
    	{
        	logger.info("Returning " + xlst.size() + " Paws records as a result of ONLY Query-By-Example (QBE), text search pulled up NOTHING for string " + searchText);
        	retrieved = xlst.size();
	        return xlst;
    	}
    	
    	
    	List <Paw> ivec = new Vector<Paw>();
    	// if both are empty, return this Empty vector. 
    	if (xlst.size() == 0 && tlst.size() == 0)
    	{
          logger.info("Neither QBE nor text search for string " + searchText +  " pulled up ANY Paws Records.");
          retrieved = 0;
    	  return ivec;
    	}
    	


    	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	// now deal with BOTH text and QBE being non-empty lists - implementing intersection by Database Primary Key -  Id
    	Iterator<Paw> xiterator = xlst.iterator();
    	while (xiterator.hasNext()) 
    	{
    		Paw x = xiterator.next();
    		Long xid = x.getId();
    		
        	Iterator<Paw> titerator = tlst.iterator();
    		while(titerator.hasNext())
    		{
        		Paw t = titerator.next();
        		Long tid = t.getId();
    			
        		if (tid == xid)
        		{
        			ivec.add(t); break;
        		}
        		
    		}
    			
    	}
    	// sort again - 
    	if (ivec.size() > 0)  Collections.sort(ivec);
    	logger.info("Returning " + ivec.size() + " Paws records from COMBINED (text, QBE) Search");
    	retrieved = ivec.size();
    	return ivec;
    }
    
  }



  ////////////////////////////////////////////////
  //  QBE Setter 
  //  

  public void setExample(Paw x) 
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
      return new PDFStreamResponse(is,"neXusProjectsAndActivities" + System.currentTimeMillis());
  }

  
  ///////////////////////////////////////////////////////
  // private methods 
  
  

 

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

              document.add(new Paragraph("NEClimateUS.org Projects & Activties Report " + formatter.format(date)));

              
              String subheader = "Printing " + retrieved + " of total " + total + " records.";
              if (StringUtils.isNotBlank(searchText))
              {
            	  subheader += "  Searching for \"" + searchText + "\""; 
              }
              
              document.add(new Paragraph(subheader));
              
              
              // drop-downs, 
              if (category != null)
              {
            	  document.add(new Paragraph("Category: " + messages.get(category.toString())));
              }
              else
              {
            	  document.add(new Paragraph("Category: All"));
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
           	Iterator<Paw> iterator = list.iterator();
           	int count=0;
       		while(iterator.hasNext())
      		{
       			count++;
          		Paw paw = iterator.next();
          		
          		String name = paw.getName();
          		String description = paw.getDescription();
          		
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
          		
          		
          	    // compile the categories list
          		com.itextpdf.text.List categories = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (paw.isSpecific()) 
          		{
          			ListItem item = new ListItem(messages.get("SPECIFIC"));	categories.add(item);
          		}
          		if (paw.isRetrofitted()) 
          		{
          			ListItem item = new ListItem(messages.get("RETRO"));	categories.add(item);
          		}
          		if (paw.isMonitoring()) 
          		{
          			ListItem item = new ListItem(messages.get("MONITOR"));	categories.add(item);
          		}
          		if (paw.isResearch()) 
          		{
          			ListItem item = new ListItem(messages.get("RESEARCH"));	categories.add(item);
          		}
          		if(categories.size() > 0)
          		{
            		  PdfPCell catCell = new PdfPCell(); catCell.addElement(categories);
              		  table.addCell(new PdfPCell(new Phrase("Category"))); table.addCell(catCell);
          		}
          		
          	    // compile the sectors list
          		com.itextpdf.text.List sectors = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (paw.isPublicHealth()) 
          		{
          			ListItem item = new ListItem(messages.get("PUBLIC"));	sectors.add(item);
          		}
          		if (paw.isInfrastructure()) 
          		{
          			ListItem item = new ListItem(messages.get("INFRA"));	sectors.add(item);
          		}
          		if (paw.isManagedEcosystems()) 
          		{
          			ListItem item = new ListItem(messages.get("MECO"));	sectors.add(item);
          		}
          		if (paw.isNaturalEcosystems()) 
          		{
          			ListItem item = new ListItem(messages.get("NECO"));	sectors.add(item);
          		}
          		if (paw.isBiota()) 
          		{
          			ListItem item = new ListItem(messages.get("BIOTA")); sectors.add(item);
          		}
          		if (paw.isCultural()) 
          		{
          			ListItem item = new ListItem(messages.get("CULT"));	sectors.add(item);
          		}
          		if (paw.isEconomicResources()) 
          		{
          			ListItem item = new ListItem(messages.get("ECORES"));	sectors.add(item);
          		}
          		if (paw.isRecreationAndTourism()) 
          		{
          			ListItem item = new ListItem(messages.get("REC"));	sectors.add(item);
          		}
          		if (paw.isCrossDisciplinary()) 
          		{
          			ListItem item = new ListItem(messages.get("CROSS"));	sectors.add(item);
          		}
          		if (paw.isOtherSector()) 
          		{
          			ListItem item = new ListItem(messages.get("OSEC"));	sectors.add(item);
          		}

                if(sectors.size() > 0)
                {
          		  PdfPCell sectorsCell = new PdfPCell(); sectorsCell.addElement(sectors);
          		  table.addCell(new PdfPCell(new Phrase("Sector"))); table.addCell(sectorsCell);
                }

          		
          		
          		
          	    // compile the focus area list
          		com.itextpdf.text.List focii = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (paw.isSustainability()) 
          		{
          			ListItem item = new ListItem(messages.get("SUSTAINABILITY"));	focii.add(item);
          		}
          		if (paw.isResilience()) 
          		{
          			ListItem item = new ListItem(messages.get("RESILIENCE"));	focii.add(item);
          		}
          		if (paw.isWater()) 
          		{
          			ListItem item = new ListItem(messages.get("WATER"));	focii.add(item);
          		}
          		if (paw.isExtremes()) 
          		{
          			ListItem item = new ListItem(messages.get("EXTREMES"));	focii.add(item);
          		}
          		if (paw.isConservation()) 
          		{
          			ListItem item = new ListItem(messages.get("CONSERVATION"));	focii.add(item);
          		}
          		
          		if (focii.size() > 0)
          		{
          		  PdfPCell fociiCell = new PdfPCell(); fociiCell.addElement(focii);
          		  table.addCell(new PdfPCell(new Phrase("Focus Area")));
          		  table.addCell(fociiCell);
          		}

          		
          		
          		
          	    // compile the regions list
          		com.itextpdf.text.List regions = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (paw.isInternational()) 
          		{
          			ListItem item = new ListItem(messages.get("INT"));	regions.add(item);
          		}
          		if (paw.isNational()) 
          		{
          			ListItem item = new ListItem(messages.get("NAT"));	regions.add(item);
          		}
          		if (paw.isRegionalOrState()) 
          		{
          			String msg = messages.get("REG");
          			
          			if (paw.isNewEngland()) msg += " " + messages.get("NENG"); 
          			if (paw.isMidAtlantic()) msg += " " + messages.get("MIDA"); 
          			if (paw.isCentral()) msg += " " + messages.get("CENT"); 
          			if (paw.isGreatLakes()) msg += " " + messages.get("GRTL"); 
          			if (paw.isSouthEast()) msg += " " + messages.get("STHE"); 
          			
          			ListItem item = new ListItem(msg);	regions.add(item);
          		}
          		if (paw.isLocalCity()) 
          		{
          			ListItem item = new ListItem(messages.get("LOC"));	regions.add(item);
          		}
          		if (paw.isProblemFocused()) 
          		{
          			ListItem item = new ListItem(messages.get("OTH"));	regions.add(item);
          		}
          		
                if(regions.size() > 0)
                {
          		  PdfPCell regionCell = new PdfPCell(); regionCell.addElement(regions);
          		  table.addCell(new PdfPCell(new Phrase("Region")));table.addCell(regionCell);
                }
          		
                
                
          		com.itextpdf.text.List status = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
          		if (paw.isOngoing()) 
          		{
          			ListItem item = new ListItem("Ongoing");	status.add(item);
          		}
          		if (paw.isPlanned()) 
          		{
          			ListItem item = new ListItem("Planned");	status.add(item);
          		}
          		if (paw.isProposed()) 
          		{
          			ListItem item = new ListItem("Proposed");	status.add(item);
          		}
          		if (paw.isCompleted()) 
          		{
          			ListItem item = new ListItem("Completed");	status.add(item);
          		}
                if(status.size() > 0)
                {
          		  PdfPCell statusCell = new PdfPCell(); statusCell.addElement(status);
          		  table.addCell(new PdfPCell(new Phrase("Status")));table.addCell(statusCell);
                }

                
                
                
                if (StringUtils.isNotBlank(paw.getDates()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Timelines")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(paw.getDates()))));
          		}
                if (StringUtils.isNotBlank(paw.getResources()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Resources")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(paw.getResources()))));
          		}

          		
          		if (StringUtils.isNotBlank(paw.getOrganization()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Lead Agencies")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(paw.getOrganization()))));
          		}
          		if (StringUtils.isNotBlank(paw.getContact()))
          		{
          		  table.addCell(new PdfPCell(new Phrase("Contacts")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(paw.getContact()))));
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
  
  private void clearSectors(Paw x)
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
  private void clearRegions(Paw x)
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
  private void clearCategories(Paw x)
  {
    x.setSpecific(false);
    x.setRetrofitted(false);
    x.setMonitoring(false);
    x.setResearch(false);
  }
}