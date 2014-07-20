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
import org.apache.tapestry5.internal.TapestryInternalUtils;
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
import org.ideademo.nexus.services.util.RDFStreamResponse;





//semantic web
import com.hp.hpl.jena.rdf.model.*;

import org.ideademo.nexus.vocabulary.NXS;






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

  public StreamResponse onSelectedFromRdf() 
  {
      // Create PDF
      InputStream is = getRdfStream(getList());
      // Return response
      return new RDFStreamResponse(is,"neXusProjectsAndActivities" + System.currentTimeMillis());
  }


  private InputStream getRdfStream(List list)
  {
	  ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	  
	  Iterator<Paw> iterator = list.iterator();
  	  while(iterator.hasNext())
  	  {
  		Paw paw = iterator.next();
         Model model =  getModel(paw);
         model.write(baos, "TURTLE", "http://www.neclimateus.org/");
  	  }
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      return bais;
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

  private Model getModel(Paw paw)
  {
      Model model = ModelFactory.createDefaultModel();
      
      Resource resource = ResourceFactory.createResource("http://neclimateus.org/nexus/paw/view/"+ paw.getId());
      
      if (StringUtils.isNotBlank(paw.getName())) 
  	   {
   	   model.add (resource, NXS.Name, StringUtils.trimToEmpty(paw.getName()));
      }
      else
      {
   	   model.add (resource, NXS.Name, "No Title???");
      }
      
      if (StringUtils.isNotBlank(paw.getCode())) model.add(resource, NXS.Acronym, StringUtils.trimToEmpty(paw.getCode()));
      if (StringUtils.isNotBlank(paw.getContact())) model.add(resource, NXS.Contact, StringUtils.trimToEmpty(paw.getContact()));
      //if (contact has email, as sensed by regex) model.add(resource, NXS.Email, StringUtils.trimToEmpty(paw.getEmail()));
      if (StringUtils.isNotBlank(paw.getDescription())) model.add(resource, NXS.Description, StringUtils.trimToEmpty(paw.getDescription()));
      if (StringUtils.isNotBlank(paw.getUrl())) model.add(resource, NXS.Link, StringUtils.trimToEmpty(paw.getUrl()));
      if (StringUtils.isNotBlank(paw.getWorksheet())) model.add(resource, NXS.Worksheet, StringUtils.trimToEmpty(paw.getWorksheet()));
      if (StringUtils.isNotBlank(paw.getKeywords())) model.add(resource, NXS.Keywords, StringUtils.trimToEmpty(paw.getKeywords()));
      
      if (StringUtils.isNotBlank(paw.getOrganization())) model.add(resource, NXS.Organization, StringUtils.trimToEmpty(paw.getOrganization()));
      if (StringUtils.isNotBlank(paw.getObjectives())) model.add(resource, NXS.Objectives, StringUtils.trimToEmpty(paw.getObjectives()));
      if (StringUtils.isNotBlank(paw.getDates())) model.add(resource, NXS.Timeline, StringUtils.trimToEmpty(paw.getDates()));
      if (StringUtils.isNotBlank(paw.getResources())) model.add(resource, NXS.Resources, StringUtils.trimToEmpty(paw.getResources()));
      if (StringUtils.isNotBlank(paw.getFeedback())) model.add(resource, NXS.Feedback, StringUtils.trimToEmpty(paw.getFeedback()));
      
      // status
      if(paw.isOngoing()) model.add(resource, NXS.Status, getLabel("ongoing"));
      if(paw.isPlanned()) model.add(resource, NXS.Status, getLabel("planned"));
      if(paw.isProposed()) model.add(resource, NXS.Status, getLabel("proposed"));
      if(paw.isCompleted()) model.add(resource, NXS.Status, getLabel("completed"));
    	
      // priority
      if(paw.getHigh()) model.add(resource, NXS.Priority, getLabel("high"));
      if(paw.getMid()) model.add(resource, NXS.Priority, getLabel("mid"));
      if(paw.getLow()) model.add(resource, NXS.Priority, getLabel("low"));
      if(paw.getUnknown()) model.add(resource, NXS.Priority, getLabel("unknown"));
      
      // categories
      if(paw.isSpecific()) model.add(resource, NXS.Categories, getLabel("specific"));
      if(paw.isRetrofitted()) model.add(resource, NXS.Categories, getLabel("retrofitted"));
      if(paw.isMonitoring()) model.add(resource, NXS.Categories, getLabel("monitoring"));
      if(paw.isResearch()) model.add(resource, NXS.Categories, getLabel("research"));
      
      // focus area
      if(paw.isSustainability()) model.add(resource, NXS.Focus, getLabel("sustainability"));
      if(paw.isResilience()) model.add(resource, NXS.Focus, getLabel("resilience"));
      if(paw.isImpacts()) model.add(resource, NXS.Focus, getLabel("impacts"));
      if(paw.isExtremes()) model.add(resource, NXS.Focus, getLabel("extremes"));
      if(paw.isConservation()) model.add(resource, NXS.Focus, getLabel("conservation"));
      
      //aoa
      if (paw.isInternational()) model.add(resource, NXS.Area_of_Applicability, getLabel("international")); 
      if (paw.isCanada()) model.add(resource, NXS.Area_of_Applicability, getLabel("canada"));  
      if (paw.isNewBrunswick())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newBrunswick"));  
      }
      if (paw.isNovaScotia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("novaScotia"));  
      }
      if (paw.isQuebec())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("quebec"));  
      }
      if (paw.isPrinceEdwardIsland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("princeEdwardIsland"));  
      }
      if (paw.isNewfoundland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newfoundland"));  
      }
      if (paw.isLabrador())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("labrador"));  
      }
      if (paw.isAtlanticCanada())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("atlanticCanada"));  
      }
      if (paw.isNational())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("national"));  
      }
      if (paw.isRegionalOrState())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("regionalOrState"));  
      }
      if (paw.isGulfOfMaine())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("gulfOfMaine"));  
      }
      if (paw.isNewEngland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newEngland"));  
      }
      if (paw.isMaine())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("maine"));  
      }
      if (paw.isNewHampshire())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newHampshire"));  
      }
      if (paw.isMassachusetts())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("massachusetts"));  
      }
      if (paw.isVermont())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("vermont"));  
      }
      if (paw.isConnecticut())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("connecticut"));  
      }
      if (paw.isRhodeIsland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("rhodeIsland"));  
      }
      if (paw.isMidAtlantic())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("midAtlantic"));  
      }
      if (paw.isNewYork())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newYork"));  
      }
      if (paw.isNewJersey())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("newJersey"));  
      }
      if (paw.isPennsylvania())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("pennsylvania"));  
      }
      if (paw.isMarlyland())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("marlyland"));  
      }
      if (paw.isDelaware())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("delaware"));  
      }
      if (paw.isVirginia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("virginia"));  
      }
      if (paw.isDistrictOfColumbia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("districtOfColumbia"));  
      }
      if (paw.isCentral())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("central"));  
      }
      if (paw.isWestVirginia())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("westVirginia"));  
      }
      if (paw.isGreatLakes())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("greatLakes"));  
      }
      if (paw.isOhio())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("ohio"));  
      }
      if (paw.isSouthEast())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("southEast"));  
      }
      if (paw.isNorthCarolina())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("northCarolina"));  
      }
      if (paw.isSouthCarolina())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("southCarolina"));  
      }
      if (paw.isLocalCity())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("localCity"));  
      }
      if (paw.isProblemFocused())
      {
   	 model.add(resource, NXS.Area_of_Applicability, getLabel("problemFocused"));  
      }
      else 
      {
   	 //model.add(resource, NXS.Area_of_Applicability, "Unspecified");  
      }

      
      // sector
      
      if (paw.isPublicHealth()) model.add(resource, NXS.Sector, getLabel("publicHealth"));  
      if (paw.isEmergencyManagement()) model.add(resource, NXS.Sector, getLabel("emergencyManagement"));  
      if (paw.isIndirectClimateHazards()) model.add(resource, NXS.Sector, getLabel("indirectClimateHazards"));  
      if (paw.isVectorBorneIllness()) model.add(resource, NXS.Sector, getLabel("vectorBorneIllness"));  
      if (paw.isHeatRelated()) model.add(resource, NXS.Sector, getLabel("heatRelated"));  
      if (paw.isWaterQuality()) model.add(resource, NXS.Sector, getLabel("waterQuality"));
      
      if (paw.isInfrastructure()) model.add(resource, NXS.Sector, getLabel("infrastructure"));  
      if (paw.isEnergy()) model.add(resource, NXS.Sector, getLabel("energy"));  
      if (paw.isCommunication()) model.add(resource, NXS.Sector, getLabel("communication"));  
      if (paw.isPublicHealth()) model.add(resource, NXS.Sector, getLabel("publicHealth"));  
      if (paw.isFreshWaterResources()) model.add(resource, NXS.Sector, getLabel("freshWaterResources"));  
      if (paw.isStormWater()) model.add(resource, NXS.Sector, getLabel("stormWater"));  
      if (paw.isWastewater()) model.add(resource, NXS.Sector, getLabel("wastewater"));
      if (paw.isWaterSupply()) model.add(resource, NXS.Sector, getLabel("waterSupply"));  
      if (paw.isTransportation()) model.add(resource, NXS.Sector, getLabel("transportation"));  
      if (paw.isBuiltCoast()) model.add(resource, NXS.Sector, getLabel("builtCoast"));
      
      if (paw.isManagedEcosystems()) model.add(resource, NXS.Sector, getLabel("managedEcosystems"));  
      if (paw.isFisheries()) model.add(resource, NXS.Sector, getLabel("fisheries"));  
      if (paw.isAquaculture()) model.add(resource, NXS.Sector, getLabel("aquaculture"));  
      if (paw.isAgriculture()) model.add(resource, NXS.Sector, getLabel("agriculture"));  
      if (paw.isForests()) model.add(resource, NXS.Sector, getLabel("forests"));  
      if (paw.isOtherManagedEcosystems()) model.add(resource, NXS.Sector, getLabel("otherManagedEcosystems"));
      
      if (paw.isNaturalEcosystems()) model.add(resource, NXS.Sector, getLabel("naturalEcosystems"));  
      if (paw.isCoasts()) model.add(resource, NXS.Sector, getLabel("coasts"));  
      if (paw.isEstuaries()) model.add(resource, NXS.Sector, getLabel("estuaries"));  
      if (paw.isWetlands()) model.add(resource, NXS.Sector, getLabel("wetlands"));  
      if (paw.isOceans()) model.add(resource, NXS.Sector, getLabel("oceans"));  
      if (paw.isInland()) model.add(resource, NXS.Sector, getLabel("inland"));  
      if (paw.isBeaches()) model.add(resource, NXS.Sector, getLabel("beaches"));  

      if (paw.isBiota()) model.add(resource, NXS.Sector, getLabel("biota"));  
      if (paw.isMarine()) model.add(resource, NXS.Sector, getLabel("marine"));  
      if (paw.isTerrestrial()) model.add(resource, NXS.Sector, getLabel("terrestrial"));  
      if (paw.isEndangered()) model.add(resource, NXS.Sector, getLabel("endangered"));  
      if (paw.isCandidateSpecies()) model.add(resource, NXS.Sector, getLabel("candidateSpecies"));
      if (paw.isConcernSpecies()) model.add(resource, NXS.Sector, getLabel("concernSpecies"));
      
      if (paw.isCultural()) model.add(resource, NXS.Sector, getLabel("cultural"));
      
      if (paw.isRecreationAndTourism()) model.add(resource, NXS.Sector, getLabel("recreationAndTourism"));
      if (paw.isUrban()) model.add(resource, NXS.Sector, getLabel("urban"));
      if (paw.isIndigenousPeoples()) model.add(resource, NXS.Sector, getLabel("indigenousPeoples"));
      if (paw.isMinority()) model.add(resource, NXS.Sector, getLabel("minority"));
      
      if (paw.isEconomicResources()) model.add(resource, NXS.Sector, getLabel("economicResources"));
      if (paw.isCrossDisciplinary()) model.add(resource, NXS.Sector, getLabel("crossDisciplinary"));
      if (paw.isOtherSector()) model.add(resource, NXS.Sector, getLabel("otherSector"));
      

      if (paw.isEcv()) model.add(resource, NXS.Capability, getLabel("ecv"));
      if (paw.isImpacts()) model.add(resource, NXS.Capability, getLabel("impacts"));      
      if (paw.isVulnerabilityAssessments()) model.add(resource, NXS.Capability, getLabel("vulnerabilityAssessments"));
      if (paw.isRiskAssessments()) model.add(resource, NXS.Capability, getLabel("riskAssessments"));
      if (paw.isNeeds()) model.add(resource, NXS.Capability, getLabel("needs"));
      
      if (paw.isScenarioPlanning()) model.add(resource, NXS.Capability, getLabel("scenarioPlanning"));
      if (paw.isExperimentalImpacts()) model.add(resource, NXS.Capability, getLabel("experimentalImpacts"));
      if (paw.isMonitor()) model.add(resource, NXS.Capability, getLabel("monitor"));
      if (paw.isDownscale()) model.add(resource, NXS.Capability, getLabel("downscale"));
      if (paw.isConditions()) model.add(resource, NXS.Capability, getLabel("conditions"));
      if (paw.isForecastImpacts()) model.add(resource, NXS.Capability, getLabel("forecastImpacts"));
      if (paw.isEconomicImpacts()) model.add(resource, NXS.Capability, getLabel("economicImpacts"));
      if (paw.isPublicSecurity()) model.add(resource, NXS.Capability, getLabel("publicSecurity"));
      
      if (paw.isMitigation()) model.add(resource, NXS.Capability, getLabel("mitigation"));
      if (paw.isTranslation()) model.add(resource, NXS.Capability, getLabel("translation"));
      if (paw.isTools()) model.add(resource, NXS.Capability, getLabel("tools"));
      if (paw.isStakeholder()) model.add(resource, NXS.Capability, getLabel("stakeholder"));
      if (paw.isGuidance()) model.add(resource, NXS.Capability, getLabel("guidance"));
      if (paw.isLiteracy()) model.add(resource, NXS.Capability, getLabel("literacy"));
      if (paw.isTranslate()) model.add(resource, NXS.Capability, getLabel("translate"));
      if (paw.isImprove()) model.add(resource, NXS.Capability, getLabel("improve"));
      
      return model;
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
  
  
  private String getLabel (String varName)
  {
	   String key = varName + "-label";
	   String value = "";
	   if (messages.contains(key)) value = messages.get(key);
	   else value = TapestryInternalUtils.toUserPresentable(varName);
	   return StringUtils.trimToEmpty(value);
  }
}