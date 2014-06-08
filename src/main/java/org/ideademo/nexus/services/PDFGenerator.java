package org.ideademo.nexus.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


import org.apache.log4j.Logger;

public class PDFGenerator 
{

	private static Logger logger = Logger.getLogger(PDFGenerator.class);


    public static InputStream generatePDF(String teststring) {

            // step 1: creation of a document-object
            Document document = new Document();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                    // step 2:
                    // we create a writer that listens to the document
                    // and directs a PDF-stream to a file
                    PdfWriter writer = PdfWriter.getInstance(document, baos);
                    // step 3: we open the document
                    document.open();
                    // step 4: we add a paragraph to the document
                    document.add(new Paragraph(teststring));
            } catch (DocumentException de) {
                    logger.fatal(de.getMessage());
            }
            // step 5: we close the document
            document.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return bais;
    }
	
	
}
