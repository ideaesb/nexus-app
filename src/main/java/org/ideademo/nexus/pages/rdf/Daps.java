package org.ideademo.nexus.pages.rdf;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;

public class Daps {

	@InjectPage
	private org.ideademo.nexus.pages.Daps index;

	public StreamResponse onActivate()
    {
		return index.onSelectedFromRdf();
    }
	
}
