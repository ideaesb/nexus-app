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
    
    
    public static final Property Area_of_Applicability = property( "Area_of_Applicability"); 

    /**
        The same items of vocabulary, but at the Node level, parked inside a
        nested class so that there's a simple way to refer to them.
    */
    @SuppressWarnings("hiding") public static final class Nodes
        {
          public static final Node Name = NXS.Name.asNode();
          public static final Node Description = NXS.Description.asNode();
          public static final Node Url = NXS.Url.asNode();
          public static final Node Worksheet = NXS.Worksheet.asNode();
          public static final Node Keywords = NXS.Keywords.asNode();
          public static final Node Source = NXS.Source.asNode();
          
          public static final Node Acronym = NXS.Acronym.asNode();
          public static final Node Contact = NXS.Contact.asNode();
          public static final Node Email = NXS.Email.asNode();
          public static final Node Affliations = NXS.Affiliations.asNode();
          public static final Node Homepage = NXS.Homepage.asNode();
          public static final Node Logo = NXS.Logo.asNode();
          public static final Node Organization_Type = NXS.Organization_Type.asNode();
          
          public static final Node Area_of_Applicability = NXS.Area_of_Applicability.asNode();
          
          
        }

}
