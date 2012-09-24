package org.ideademo.nexus.services;

import java.io.IOException;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;


public class RevalidateHTTPHeader implements RequestFilter 
{

  private static final String CACHE_CTRL = "Cache-Control";
  private static final String EXPIRE_DATE = "Expires";
 
  @Override
  public boolean service(Request request, Response response,  RequestHandler handler) throws IOException 
  { 
    response.setHeader(CACHE_CTRL, "no-cache, no-store, max-age=0, must-revalidate"); 
    response.setHeader(EXPIRE_DATE, "Sun, 07 Dec 1941  07:55:00 GMT"); 
    return handler.service(request, response);   
	            
  }
}
