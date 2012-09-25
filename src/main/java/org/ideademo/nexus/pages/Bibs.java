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

import org.apache.tapestry5.annotations.PageActivationContext;
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


import org.ideademo.nexus.entities.Bib;


import org.apache.log4j.Logger;


public class Bibs 
{
	 
  private static Logger logger = Logger.getLogger(Bibs.class);
  private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_31); 

  
  /////////////////////////////
  //  Drives QBE Search
  @Persist (PersistenceConstants.FLASH)
  private Bib example;
  
  
  //////////////////////////////////////////////////////////////
  // Used in rendering within Loop just as in Grid (Table) Row
  @SuppressWarnings("unused")
  @Property 
  private Bib row;

  @PageActivationContext
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
  
  

}