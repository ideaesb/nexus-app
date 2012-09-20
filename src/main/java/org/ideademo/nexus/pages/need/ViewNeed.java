package org.ideademo.nexus.pages.need;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import org.ideademo.nexus.entities.Need;

public class ViewNeed 
{

  @PageActivationContext 
  @Property
  private Need entity;
	  
	  
  void onPrepareForRender()  {if(this.entity == null){this.entity = new Need();}}
  void onPrepareForSubmit()  {if(this.entity == null){this.entity = new Need();}}
}
