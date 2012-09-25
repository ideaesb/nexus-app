package org.ideademo.nexus.pages.dap;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;

import org.ideademo.nexus.pages.Daps;
import org.ideademo.nexus.entities.Dap;

public class SearchDap 
{
   
   @Property
   private Dap entity;
   
   @InjectPage
   private Daps daps;
   
   
   Object onSelectedFromSearch() 
   {
 	daps.setExample(entity);
    return daps;
   }

   void onPrepareForRender()  {if(this.entity == null){this.entity = new Dap();}}
   void onPrepareForSubmit()  {if(this.entity == null){this.entity = new Dap();}}
   
}