package org.ideademo.nexus.pages.paw;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import org.ideademo.nexus.entities.Paw;

public class ViewPaw 
{

  @PageActivationContext 
  @Property
  private Paw entity;
	  
	  
  void onPrepareForRender()  {if(this.entity == null){this.entity = new Paw();}}
  void onPrepareForSubmit()  {if(this.entity == null){this.entity = new Paw();}}
}