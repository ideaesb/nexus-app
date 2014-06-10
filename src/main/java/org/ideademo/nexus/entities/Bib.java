package org.ideademo.nexus.entities;

import java.lang.Comparable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.apache.tapestry5.annotations.Property;

import org.apache.tapestry5.beaneditor.NonVisual;


@Entity @Indexed
public class Bib implements Comparable<Bib>  
{
	
	
  //////////////////////////////////////////
  //  Reserved indexing id 
	
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
	
}
