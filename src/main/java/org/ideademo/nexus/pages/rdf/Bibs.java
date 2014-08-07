package org.ideademo.nexus.pages.rdf;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.StreamResponse;


public class Bibs {

	@InjectPage
	private org.ideademo.nexus.pages.Bibs index;
	
	public StreamResponse onActivate()
    {
		return index.onSelectedFromRdf();
    }
}
