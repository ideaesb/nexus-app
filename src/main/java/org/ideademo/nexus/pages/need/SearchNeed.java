package org.ideademo.nexus.pages.need;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;

import org.ideademo.nexus.pages.Needs;
import org.ideademo.nexus.entities.Need;

public class SearchNeed 
{
   
   @Property
   private Need entity;
   
   @InjectPage
   private Needs needs;
   
   
   Object onSelectedFromSearch() 
   {
 	needs.seExample(entity);
    return needs;
   }

   void onPrepareForRender()  {if(this.entity == null){this.entity = new Need();}}
   void onPrepareForSubmit()  {if(this.entity == null){this.entity = new Need();}}
   
}
