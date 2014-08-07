package org.ideademo.nexus.pages.pdf;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.StreamResponse;

public class Orgs {
	
	@InjectPage
	private org.ideademo.nexus.pages.Orgs index;
	
	public StreamResponse onActivate()
    {
		return index.onSelectedFromPdf();
    }

}
