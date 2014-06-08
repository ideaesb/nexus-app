package org.ideademo.nexus.entities;

import java.lang.Comparable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.apache.tapestry5.beaneditor.NonVisual;



// semantic web
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

import org.ideademo.nexus.vocabulary.NXS;
import org.apache.commons.lang.StringUtils;

@Entity @Indexed
public class Bib implements Comparable<Bib>  
{
	
	
  //////////////////////////////////////////
  //  Resevred indexing id 
	
  @Id @GeneratedValue @DocumentId @NonVisual
  private Long id;

  
  //////////////////////////////////////////////
  //  String fields (being a keyword for Lucene)
  //
  
  @Field
  private String name="";

  @Field @Column (length=4096)
  private String description="";

  @Field 
  private String url="";

  @Field 
  private String worksheet="";

  @Field @Column (length=4096)
  private String keywords="";
	
		
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getWorksheet() 
	{
	  return worksheet;
	}
	
	public void setWorksheet(String worksheet) {
		this.worksheet = worksheet;
	}
	
	public String getKeywords() {
		return keywords;
	}
	
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	private boolean worksheetExists = false;
	public boolean isWorksheetExists() {
		return worksheetExists;
	}
	public void setWorksheetExists(boolean worksheetExists) {
		this.worksheetExists = worksheetExists;
	}
	
	
	////////////////////////////////////////////////
	/// default/natural sort order - String  - names
	
	public int compareTo(Bib ao) 
	{
	    boolean thisIsEmpty = false;
	    boolean aoIsEmpty = false; 
	    
	    if (this.getName() == null || this.getName().trim().length() == 0) thisIsEmpty = true; 
	    if (ao.getName() == null || ao.getName().trim().length() == 0) aoIsEmpty = true;
	    
	    if (thisIsEmpty && aoIsEmpty) return 0;
	    if (thisIsEmpty && !aoIsEmpty) return -1;
	    if (!thisIsEmpty && aoIsEmpty) return 1; 
	    return this.getName().compareToIgnoreCase(ao.getName());
   }
	
   public Model getRDF()
   {
       Model model = ModelFactory.createDefaultModel();
       
       Resource bib= ResourceFactory.createResource("http://neclimateus.org/nexus/bib/view/"+id);

       if (StringUtils.isNotBlank(this.name)) 
   	   {
    	   model.add (bib, NXS.Citation, StringUtils.trimToEmpty(this.name));
       }
       else
       {
    	   model.add (bib, NXS.Citation, "Citation with No Title");
       }
       
       if (StringUtils.isNotBlank(this.description)) model.add(bib, NXS.Description, StringUtils.trimToEmpty(this.description));
       if (StringUtils.isNotBlank(this.url)) model.add(bib, NXS.Source, ResourceFactory.createResource(StringUtils.trimToEmpty(this.url)));
       if (StringUtils.isNotBlank(this.worksheet)) model.add(bib, NXS.Worksheet, StringUtils.trimToEmpty(this.worksheet));
       if (StringUtils.isNotBlank(this.keywords)) model.add(bib, NXS.Keywords, StringUtils.trimToEmpty(this.keywords));
       
       return model;

   }
}
