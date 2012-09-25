package org.ideademo.nexus.pages.org;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;

import org.ideademo.nexus.pages.Orgs;
import org.ideademo.nexus.entities.Org;

public class SearchOrg 
{
   
   @Property
   private Org entity;
   
   @InjectPage
   private Orgs orgs;
   
   
   Object onSelectedFromSearch() 
   {
     orgs.setExample(entity);
     return orgs;
   }

   void onPrepareForRender()  {if(this.entity == null){this.entity = new Org();}}
   void onPrepareForSubmit()  {if(this.entity == null){this.entity = new Org();}}
   
}