package org.ideademo.nexus.services.util;


import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;


public class RDFStreamResponse implements StreamResponse {
    private InputStream is;
    private String filename="default";

    public RDFStreamResponse(InputStream is, String... args) {
            this.is = is;
            if (args != null) {
                    this.filename = args[0];
            }
    }

    public String getContentType() {
            return "application/rdf+xml";
    }

    public InputStream getStream() throws IOException {
            return is;
    }

    public void prepareResponse(Response arg0) {
            arg0.setHeader("Content-Disposition", "attachment; filename="
                            + filename + ".ttl");
    }
}
