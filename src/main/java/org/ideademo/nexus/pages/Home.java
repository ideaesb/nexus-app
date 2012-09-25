package org.ideademo.nexus.pages;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class Home 
{
	private static Logger logger = Logger.getLogger(Home.class);
	
	public URL onActivate() 
	{
		URL url = null;

		try
		{
		  url = new URL ("../index.php");
		}
		catch (MalformedURLException me)
		{
		  logger.warn("Error in redirecting to home page " + me);
		}
		
		return url;
	}
	
	
}
