package org.ideademo.nexus.vocabulary;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.rdf.model.*;

/**
    The standard NXS vocabulary: applies to all tabs (needs, paws, daps etc) in Nexus.
*/
public class NXS {


    protected static final String uri ="http://www.neclimateus.org/";

    /** returns the URI for this schema
        @return the URI for this schema
    */
    public static String getURI()
        { return uri; }

    protected static final Resource resource( String local )
        { return ResourceFactory.createResource( uri + local ); }

    protected static final Property property( String local )
        { return ResourceFactory.createProperty( uri, local ); }

    public static Property li( int i )
        { return property( "_" + i ); }

    // from Bibliography
    public static final Property Name = property( "Name" );
    public static final Property Description = property( "Description" );
    public static final Property Url = property( "Url" );
    public static final Property Worksheet = property( "Worksheet" );
    public static final Property Keywords = property( "Keywords" );
    public static final Property Citation = property( "Citation" );
    public static final Property Source = property( "Source" );
    
    // from Orgs
    public static final Property Acronym = property( "Acronym" );
    public static final Property Contact = property( "Contact" );
    public static final Property Email   = property( "Email" );
    public static final Property Affiliations = property( "Affiliations" );
    public static final Property Homepage = property( "Homepage" );
    public static final Property Logo = property( "Logo" );

    public static final Property Organization_Type = property( "Organization_Type");
    
    // from paws
    public static final Property Organization = property( "Organization" );
    public static final Property Objectives = property( "Objectives" );
    public static final Property Resources = property( "Resources" );
    public static final Property Feedback = property( "Feedback" );
    public static final Property Timeline = property( "Timeline" );
    public static final Property Link = property( "Link" );
    
    public static final Property Status = property( "Status" );
    public static final Property Priority = property( "Priority" );
    public static final Property Categories = property( "Categories" );
    public static final Property Focus = property( "Focus" );
    public static final Property Sector = property( "Sector" );
    public static final Property Capability = property( "Capability" );

    
    
    // general boolean groupings
    public static final Property Area_of_Applicability = property( "Area_of_Applicability");
    
    // from daps
    public static final Property Discipline = property( "Discipline" );
    public static final Property Data = property( "Data" );
    public static final Property Products = property( "Products" );
    public static final Property Services = property( "Services" );
    
    //needs
    public static final Property Comments  = property( "Comments" );
    public static final Property Requestor = property( "Requestor" );
    public static final Property Bibliography  = property( "Bibliography" );
    
    public static final Property Xdata     = property( "Existing_Data_Related_to_Need" );
    public static final Property Xproducts = property( "Existing_Products_Related_to_Need" );
    public static final Property Xservices = property( "Existing_Services_Related_to_Need" );
    public static final Property Xprograms = property( "Existing_Programs_Related_to_Need" );
    public static final Property Xprojects = property( "Existing_Projects_Related_to_Need" );
    
    

    /**
        The same items of vocabulary, but at the Node level, parked inside a
        nested class so that there's a simple way to refer to them.
    */
    @SuppressWarnings("hiding") public static final class Nodes
        {
    	  // bibs
          public static final Node Name = NXS.Name.asNode();
          public static final Node Description = NXS.Description.asNode();
          public static final Node Url = NXS.Url.asNode();
          public static final Node Worksheet = NXS.Worksheet.asNode();
          public static final Node Keywords = NXS.Keywords.asNode();
          public static final Node Source = NXS.Source.asNode();
          
          //orgs
          public static final Node Acronym = NXS.Acronym.asNode();
          public static final Node Contact = NXS.Contact.asNode();
          public static final Node Email = NXS.Email.asNode();
          public static final Node Affliations = NXS.Affiliations.asNode();
          public static final Node Homepage = NXS.Homepage.asNode();
          public static final Node Logo = NXS.Logo.asNode();
          public static final Node Organization_Type = NXS.Organization_Type.asNode();
          
          
          // paws
          public static final Node Organization = NXS.Organization.asNode();
          public static final Node Objectives = NXS.Objectives.asNode();
          public static final Node Resources = NXS.Resources.asNode();
          public static final Node Feedback = NXS.Feedback.asNode();
          public static final Node Timeline = NXS.Timeline.asNode();
          public static final Node Link = NXS.Link.asNode();
          
          public static final Node Status = NXS.Status.asNode();
          public static final Node Priority = NXS.Priority.asNode();
          public static final Node Categories = NXS.Categories.asNode();
          public static final Node Focus = NXS.Focus.asNode();
          public static final Node Sector = NXS.Sector.asNode();
          public static final Node Capability = NXS.Capability.asNode();
          
          
          public static final Node Area_of_Applicability = NXS.Area_of_Applicability.asNode();
          
          // daps
          public static final Node Discipline = NXS.Discipline.asNode();
          public static final Node Data = NXS.Data.asNode();
          public static final Node Products = NXS.Products.asNode();
          public static final Node Services = NXS.Services.asNode();
          
          // needs
          public static final Node Requestor     = NXS.Requestor.asNode();
          public static final Node Comments      = NXS.Comments.asNode();
          public static final Node Bibliography  = NXS.Bibliography.asNode();
          
          public static final Node Xdata     = NXS.Xdata.asNode();
          public static final Node Xproducts = NXS.Xproducts.asNode();
          public static final Node Xservices = NXS.Xservices.asNode();
          public static final Node Xprograms = NXS.Xprograms.asNode();
          public static final Node Xprojects = NXS.Xprojects.asNode();
          
          
        }

}
