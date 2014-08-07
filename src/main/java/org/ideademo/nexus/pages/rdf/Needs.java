package org.ideademo.nexus.pages.rdf;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;

public class Needs {

	@InjectPage
	private org.ideademo.nexus.pages.Needs index;

	public StreamResponse onActivate()
    {
		return index.onSelectedFromRdf();
    }

}
