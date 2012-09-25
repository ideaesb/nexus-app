package org.ideademo.nexus.pages.bib;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;

import org.ideademo.nexus.pages.Bibs;
import org.ideademo.nexus.entities.Bib;

public class SearchBib 
{
   
   @Property
   private Bib entity;
   
   @InjectPage
   private Bibs bibs;
   
   
   Object onSelectedFromSearch() 
   {
     bibs.setExample(entity);
     return bibs;
   }

   void onPrepareForRender()  {if(this.entity == null){this.entity = new Bib();}}
   void onPrepareForSubmit()  {if(this.entity == null){this.entity = new Bib();}}
   
}