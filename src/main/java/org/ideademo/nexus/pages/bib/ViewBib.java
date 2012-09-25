package org.ideademo.nexus.pages.bib;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import org.ideademo.nexus.entities.Bib;

public class ViewBib 
{

  @PageActivationContext 
  @Property
  private Bib entity;
	  
	  
  void onPrepareForRender()  {if(this.entity == null){this.entity = new Bib();}}
  void onPrepareForSubmit()  {if(this.entity == null){this.entity = new Bib();}}
}