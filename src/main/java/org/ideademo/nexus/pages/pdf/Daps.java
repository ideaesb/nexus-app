package org.ideademo.nexus.pages.pdf;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.StreamResponse;


public class Daps {
	
	@InjectPage
	private org.ideademo.nexus.pages.Daps index;
	
	public StreamResponse onActivate()
    {
		return index.onSelectedFromPdf();
    }
}
