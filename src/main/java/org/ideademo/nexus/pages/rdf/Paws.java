package org.ideademo.nexus.pages.rdf;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;

public class Paws {

	@InjectPage
	private org.ideademo.nexus.pages.Paws index;
	
	public StreamResponse onActivate()
    {
		return index.onSelectedFromRdf();
    }
	
}
