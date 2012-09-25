package org.ideademo.nexus.pages.paw;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;

import org.ideademo.nexus.pages.Paws;
import org.ideademo.nexus.entities.Paw;

public class SearchPaw 
{
   
   @Property
   private Paw entity;
   
   @InjectPage
   private Paws paws;
   
   
   Object onSelectedFromSearch() 
   {
 	paws.setExample(entity);
    return paws;
   }

   void onPrepareForRender()  {if(this.entity == null){this.entity = new Paw();}}
   void onPrepareForSubmit()  {if(this.entity == null){this.entity = new Paw();}}
   
}