package org.ideademo.nexus.services;

import org.hibernate.Session;
import org.ideademo.nexus.entities.Bib;
import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ideademo.nexus.pages.Bibs;
import org.ideademo.nexus.services.util.IOUtils;
import org.ideademo.nexus.services.util.RDFStreamResponse;

import java.io.IOException;
import java.util.List;

/**
 * This writes out the various tabs: needs, thru bibliography to respective RDF files.  
 * @author Uday
 *
 */
public class RDFGenerator 
{

@Inject
private Session session;

@Inject 
public Bibs bibs;

public List <Bib> getBibs()
{
	//return session.createCriteria(Bib.class).list();
	return bibs.getList();
}
	 
	public static void main(String [] args)
	{
		RDFGenerator rdfGenerator = new RDFGenerator();
		/*
		Bibs bibs = new Bibs();
		RDFStreamResponse response = (RDFStreamResponse) bibs.onSelectedFromRdf();
		try
		{
			IOUtils.copy(response.getStream(), System.out);	
		}
		catch (IOException ioe) 
		{
            throw new RuntimeException("error reading stream", ioe);
        } 
        */
		
		List <Bib> alst = rdfGenerator.getBibs();
		System.out.println("Size = " + alst.size());
		
	}
	
}
