package org.apache.camel;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.BadSecurityHandlerException;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class FopHelper {

    //decryption requires additional libraries
    public static void decryptPDFN(PDDocument document, String password) throws IOException, CryptographyException, BadSecurityHandlerException {
        if (document.isEncrypted()) {
            DecryptionMaterial decryptionMaterial = new StandardDecryptionMaterial(password);
            document.openProtection(decryptionMaterial);
        } else {
            throw new RuntimeException("Document not encrypted");
        }
    }

    public static String extractTextFrom(PDDocument document) throws IOException {
        Writer output = new StringWriter();
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.writeText(document, output);
        return output.toString().trim();
    }

    public static String getDocumentMetadataValue(PDDocument document, COSName name) {
        PDDocumentInformation info = document.getDocumentInformation();
        return info.getDictionary().getString(name);
    }

    public static String decorateTextWithXSLFO(String text) {
        return "<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n" +
                "  <fo:layout-master-set>\n" +
                "    <fo:simple-page-master master-name=\"only\">\n" +
                "      <fo:region-body region-name=\"xsl-region-body\" margin=\"0.7in\"  padding=\"0\" />\n" +
                "      <fo:region-before region-name=\"xsl-region-before\" extent=\"0.7in\" />\n" +
                "        <fo:region-after region-name=\"xsl-region-after\" extent=\"0.7in\" />\n" +
                "      </fo:simple-page-master>\n" +
                "    </fo:layout-master-set>\n" +
                "    <fo:page-sequence master-reference=\"only\">\n" +
                "      <fo:flow flow-name=\"xsl-region-body\">\n" +
                "      <fo:block>" + text + "</fo:block>\n" +
                "    </fo:flow>\n" +
                "  </fo:page-sequence>\n" +
                "</fo:root>";
    }
}
