package org.ideademo.nexus.pages;

import java.awt.Toolkit;
import java.io.StringReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Path;
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


import org.ideademo.nexus.services.util.PDFStreamResponse;
import org.ideademo.nexus.services.util.RDFStreamResponse;
import org.apache.tapestry5.StreamResponse;



//semantic web
import com.hp.hpl.jena.rdf.model.*;


import org.ideademo.nexus.vocabulary.NXS; // namespaces


//to get message labels
import org.apache.tapestry5.ioc.Messages;




import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;



import org.apache.log4j.Logger;
import org.ideademo.nexus.entities.Bib;


public class Bibs 
{
	 
  private static Logger logger = Logger.getLogger(Bibs.class);
  private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_31); 

  
  /////////////////////////////
  //  Drives QBE Search
  @Persist
  private Bib example;
  
  
  //////////////////////////////////////////////////////////////
  // Used in rendering within Loop just as in Grid (Table) Row
  @SuppressWarnings("unused")
  @Property 
  private Bib row;

  @PageActivationContext
  @Property
  @Persist
  private String searchText;

  @Inject
  private Session session;
  
  @Inject
  private HibernateSessionManager sessionManager;

  @Property 
  @Persist
  int retrieved; 
  @Property 
  @Persist
  int total;
  @Inject
  @Path("context:layout/images/noaa-logo.png")
  private Asset logoAsset;
  
  @Inject 
  HttpServletRequest request;
  
  @Inject
  private Messages messages;
  
  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////
  //  Entity List generator - QBE, Text Search or Show All 
  //

  @SuppressWarnings("unchecked")
  public List<Bib> getList()
  {
    //////////////////////////////////
    // first interpret search criteria
	  
    // text search string 
    logger.info("Search Text = " + searchText);
	
	
    // Get all records anyway - for showing total at bottom of presentation layer
    List <Bib> alst = session.createCriteria(Bib.class).list();
    total = alst.size();

	
    // then makes lists and sublists as per the search criteria 
    List<Bib> xlst=null; // xlst = Query by Example search List
    if(example != null)
    {
       Example ex = Example.create(example).excludeFalse().ignoreCase().enableLike(MatchMode.ANYWHERE);
       
       xlst = session.createCriteria(Bib.class).add(ex).list();
       
       
       if (xlst != null)
       {
    	   logger.info("Bib Example Search Result List Size  = " + xlst.size() );
    	   Collections.sort(xlst);
       }
       else
       {
         logger.info("Bib Example Search result did not find any results...");
       }
    }
    
    List<Bib> tlst=null;
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
      
       QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Bib.class ).get();
       
       // fields being covered by text search 
       TermMatchingContext onFields = qb
		        .keyword()
		        .onFields("name","description", "keywords", "url", "worksheet");
       
       BooleanJunction<BooleanJunction> bool = qb.bool();
       /////// Tokenize the search string for default AND logic ///
       TokenStream stream = analyzer.tokenStream(null, new StringReader(searchText));
       CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
       try
       {
        while (stream.incrementToken()) 
         {
    	   String token = cattr.toString();
    	   logger.info("Adding search token " +  token + " to look in Bibs database");
    	   bool.must(onFields.matching(token).createQuery());
         }
        stream.end(); 
        stream.close(); 
       }
       catch (IOException ioe)
       {
    	   logger.warn("Bibs Text Search: Encountered problem tokenizing search term " + searchText);
    	   logger.warn(ioe);
       }
       
       /////////////  the lucene query built from non-simplistic English words 
       org.apache.lucene.search.Query luceneQuery = bool.createQuery();
       
       ////////////////////////////////////////////////////////////////////////////
       //  Override "normal" search 
       //
       // if search terms have "BIB" then just create the old-fashioned keyword "OR logic" query - just limited to keywords field, though
       if (searchText.indexOf("BIB") > -1)
       {
    	   luceneQuery = qb
   			    .keyword()
   			    .onFields("keywords")
   			    .matching(searchText)
   			    .createQuery();  
       }
       
       tlst = fullTextSession.createFullTextQuery(luceneQuery, Bib.class).list();
       if (tlst != null) 
       {
    	   logger.info("TEXT Search for " + searchText + " found " + tlst.size() + " Bibs records in database");
    	   Collections.sort(tlst);
       }
       else
       {
          logger.info("TEXT Search for " + searchText + " found nothing in Bibs");
       }
    }
    
    
    // organize what type of list is returned...either total, partial (subset) or intersection of various search results  
    if (example == null && (searchText == null || searchText.trim().length() == 0))
    {
    	// Everything...
    	if (alst != null && alst.size() > 0)
    	{
          logger.info ("Returing all " + alst.size() + " Bibs records");
          Collections.sort(alst);
    	}
    	else
    	{
    	  logger.warn("No Bibs records found in the database");
    	}
    	retrieved = total;
        return alst; 
    }
    else if (xlst == null && tlst != null)
    {
    	// just text search results
    	logger.info("Returing " + tlst.size() + " Bibs records as a result of PURE text search (no QBE) for " + searchText);
    	retrieved = tlst.size();
    	return tlst;
    }
    else if (xlst != null && tlst == null)
    {
    	// just example query results
    	logger.info("Returning " + xlst.size() + " Bibs records as a result of PURE Query-By-Example (QBE), no text string");
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
         	logger.info("Returing " + tlst.size() + " Bibs records as a result of ONLY text search, QBE pulled up ZERO records for " + searchText);
        	retrieved = tlst.size();
    		return tlst;
    	}

    	if (tlst.size() == 0 && xlst.size() > 0)
    	{
        	logger.info("Returning " + xlst.size() + " Bibs records as a result of ONLY Query-By-Example (QBE), text search pulled up NOTHING for string " + searchText);
        	retrieved = xlst.size();
	        return xlst;
    	}
    	
    	
    	List <Bib> ivec = new Vector<Bib>();
    	// if both are empty, return this Empty vector. 
    	if (xlst.size() == 0 && tlst.size() == 0)
    	{
        	logger.info("Neither QBE nor text search for string " + searchText +  " pulled up ANY Bibs Records.");
        	retrieved = 0;
    		return ivec;
    	}
    	


    	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	// now deal with BOTH text and QBE being non-empty lists - implementing intersection by Database Primary Key -  Id
    	Iterator<Bib> xiterator = xlst.iterator();
    	while (xiterator.hasNext()) 
    	{
    		Bib x = xiterator.next();
    		Long xid = x.getId();
    		
        	Iterator<Bib> titerator = tlst.iterator();
    		while(titerator.hasNext())
    		{
        		Bib t = titerator.next();
        		Long tid = t.getId();
    			
        		if (tid == xid)
        		{
        			ivec.add(t); break;
        		}
        		
    		}
    			
    	}
    	// sort again - 
    	if (ivec.size() > 0)  Collections.sort(ivec);
    	logger.info("Returning " + ivec.size() + " Bibs records from COMBINED (text, QBE) Search");
    	retrieved = ivec.size();
    	return ivec;
    }
    
  }


  ////////////////////////////////////////////////////
  //  QBE Setter : vestigial - not doing QBE for Bibs
  //  

  public void setExample(Bib x) 
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
  
  ///////////////////////////////////////////////////////////////
  //  Action Event Handlers 
  //
  
  Object onSelectedFromSearch() 
  {
    return null; 
  }

  Object onSelectedFromClear() 
  {
    this.searchText = "";
    this.example = null; // just for sake of completeness - not doing QBE
    return null; 
  }
  
  public StreamResponse onSelectedFromPdf() 
  {
	 List<Bib> list = getList();
     String subheader = "Printing " + retrieved + " of total " + total + " records.";
     if (StringUtils.isNotBlank(searchText))
     {
   	  subheader += "  Searching for \"" + searchText + "\""; 
     }
     Document document = new Document();
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     try
     {
    	 PdfWriter writer = PdfWriter.getInstance(document, baos);
         document.open();
         document.add(getLogo());
         document.add(new Paragraph(getHeader("NExUS Citations ")));
         document.add(new Paragraph(subheader));
         document.add(Chunk.NEWLINE);document.add(Chunk.NEWLINE);
         Iterator<Bib> iterator = list.iterator();
         while (iterator.hasNext())
         {
        	 Bib b = iterator.next();
        	 document.add(getPDFTable(b));
        	 document.add(Chunk.NEWLINE);
         }
         document.close();
     }
     catch (Exception e)
     {
   	  logger.warn("Error generating PDF Doc " + e);
     }
     return getResponse ("NExUS_Citations", baos);
  }
  public StreamResponse onReturnStreamResponse(long id) 
  {
      Document document = new Document();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try
      {
    	  PdfWriter writer = PdfWriter.getInstance(document, baos);
          document.open();
          document.add(getLogo());
          document.add(new Paragraph(getHeader("NExUS Citation ")));
          document.add(Chunk.NEWLINE);document.add(Chunk.NEWLINE);
          Bib b =  (Bib) session.load(Bib.class, id);
          document.add(getPDFTable(b));
          document.close();
      }
      catch (Exception e)
      {
    	  logger.warn("Error generating PDF Doc " + e);
      }

      return getResponse ("NExUS_Citation", baos);
  }	  
  

  public StreamResponse onSelectedFromRdf() 
  {
      // Create PDF
      InputStream is = getRdfStream(getList());
      // Return response
      return new RDFStreamResponse(is,"NExUS_Citations_" + System.currentTimeMillis());
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
  
  private InputStream getRdfStream(List list)
  {
	  ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	  
	  Iterator<Bib> iterator = list.iterator();
  	  while(iterator.hasNext())
  	  {
  		Bib bib = iterator.next();
         Model model =  getModel(bib);
         model.write(baos, "TURTLE", "http://www.neclimateus.org/");
  	  }
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      return bais;
  }
  
  
  // create RDF graph element (bunch of triples )
  private Model getModel(Bib bib)
  {
      Model model = ModelFactory.createDefaultModel();
      
      Resource resource = ResourceFactory.createResource("http://neclimateus.org/nexus/bib/view/"+bib.getId()); //TO DO - label the url  

      if (StringUtils.isNotBlank(bib.getName())) 
  	   {
   	   model.add (resource, NXS.Citation, StringUtils.trimToEmpty(bib.getName()));
      }
      else
      {
   	   model.add (resource, NXS.Citation, "Citation with No Title");
      }
      
      if (StringUtils.isNotBlank(bib.getDescription())) model.add(resource, NXS.Description, StringUtils.trimToEmpty(bib.getDescription()));
      if (StringUtils.isNotBlank(bib.getUrl())) model.add(resource, NXS.Source, ResourceFactory.createResource(StringUtils.trimToEmpty(bib.getUrl())));
      if (StringUtils.isNotBlank(bib.getWorksheet())) model.add(resource, NXS.Worksheet, StringUtils.trimToEmpty(bib.getWorksheet()));
      if (StringUtils.isNotBlank(bib.getKeywords())) model.add(resource, NXS.Keywords, StringUtils.trimToEmpty(bib.getKeywords()));
      
      return model;

  }

  private PdfPTable getPDFTable(Bib bib)
  {
      // create table, 2 columns
        PdfPTable table = new PdfPTable(2);
        try
        {
          table.setWidths(new int[]{1, 4});
        }
        catch (Exception e)
        {
      	  logger.fatal("Could not setWidths???" + e );
        }
        table.setSplitRows(false);
  		String name = bib.getName();
  		String description = bib.getDescription();
  		String url = bib.getUrl();
        
        
        PdfPCell nameTitle = new PdfPCell(new Phrase("Citation")); 
        PdfPCell nameCell = new PdfPCell(new Phrase(name));
        
        nameTitle.setBackgroundColor(BaseColor.CYAN);  nameCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        
        table.addCell(nameTitle);  table.addCell(nameCell);          		          		
  		
  		if (StringUtils.isNotBlank(description))
  		{
  		  table.addCell(new PdfPCell(new Phrase("Description")));  table.addCell(new PdfPCell(new Phrase(StringUtils.trimToEmpty(description))));
  		}
  		if (StringUtils.isNotBlank(url))
  		{
    	  Anchor link = new Anchor(StringUtils.trimToEmpty(url)); link.setReference(StringUtils.trimToEmpty(url));
  		  table.addCell(new PdfPCell(new Phrase("Url")));  table.addCell(new PdfPCell(link));
  		}
     return table; 
  }
 

  private com.itextpdf.text.Image getLogo()
   {
     java.awt.Image awtImage = Toolkit.getDefaultToolkit().createImage(logoAsset.getResource().toURL());
 	  try
 	  {
	    com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(awtImage, null);
	    logo.scalePercent(50);
	    return logo;
 	  }
 	  catch (Exception e)
 	  {
 		 logger.warn("Could not generate logo " + e);
 	  }
	  
      return null;
   }
   private String getHeader(String prefix)
   {
	      DateFormat formatter = new SimpleDateFormat
                  ("EEE MMM dd HH:mm:ss zzz yyyy");
              Date date = new Date(System.currentTimeMillis());
              TimeZone eastern = TimeZone.getTimeZone("Pacific/Honolulu");
              formatter.setTimeZone(eastern);
              
      return prefix + formatter.format(date); 
   }
   
   private PDFStreamResponse getResponse (String prefix, ByteArrayOutputStream baos)
   {
     ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
     DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy.HH.mm.ss.zzz");
     return new PDFStreamResponse(bais, prefix + "_generated_on_" + formatter.format(new Date(System.currentTimeMillis())));
     //return new PDFStreamResponse(bais, prefix);
   }
  
}