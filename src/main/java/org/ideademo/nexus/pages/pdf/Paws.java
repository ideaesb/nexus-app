package org.ideademo.nexus.pages.pdf;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.StreamResponse;

public class Paws {
	
	@InjectPage
	private org.ideademo.nexus.pages.Paws index;
	
	public StreamResponse onActivate()
    {
		return index.onSelectedFromPdf();
    }

}
