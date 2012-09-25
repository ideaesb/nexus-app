package org.ideademo.nexus.pages.org;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import org.ideademo.nexus.entities.Org;

public class ViewOrg 
{

  @PageActivationContext 
  @Property
  private Org entity;
	  
	  
  void onPrepareForRender()  {if(this.entity == null){this.entity = new Org();}}
  void onPrepareForSubmit()  {if(this.entity == null){this.entity = new Org();}}
}