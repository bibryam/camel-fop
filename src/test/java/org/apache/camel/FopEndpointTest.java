/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel;

import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

public class FopEndpointTest extends CamelTestSupport {

    @Test
    public void generatePDFFromXSLFOWithSpecificText() throws Exception {
        Endpoint endpoint = context().getEndpoint("fop:pdf");
        Producer producer = endpoint.createProducer();
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(PDFHelper.decorateTextWithXSLFO("Test Content"));

        producer.process(exchange);
        PDDocument document = getDocumentFrom(exchange);
        String content = PDFHelper.extractTextFrom(document);
        assertEquals("Test Content", content);
    }

    private PDDocument getDocumentFrom(Exchange exchange) throws IOException {
        InputStream inputStream = exchange.getOut().getBody(InputStream.class);
        return PDDocument.load(inputStream);
    }

    @Test
    public void specifyCustomUserConfigurationFile() throws Exception {
        FopEndpoint customConfiguredEndpoint = context().getEndpoint("fop:pdf?userConfigURL=src/test/data/conf/testcfg.xml", FopEndpoint.class);
        float customSourceResolution = customConfiguredEndpoint.getFopFactory().getSourceResolution();
        assertEquals(96.0, customSourceResolution, 0.1);
    }

    @Test
    public void setPDFRenderingMetadataPerDocument() throws Exception {
        Endpoint endpoint = context().getEndpoint("fop:pdf");
        Producer producer = endpoint.createProducer();
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("CamelFop.render.Creator", "Test User");
        exchange.getIn().setBody(PDFHelper.decorateTextWithXSLFO("Test Content"));

        producer.process(exchange);
        PDDocument document = getDocumentFrom(exchange);
        String creator = PDFHelper.getDocumentMetadataValue(document, COSName.CREATOR);
        assertEquals("Test User", creator);
    }

    @Test
    public void encryptPDFWithUserPassword() throws Exception {
        Endpoint endpoint = context().getEndpoint("fop:pdf");
        Producer producer = endpoint.createProducer();
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("CamelFop.encrypt.userPassword", "secret");
        exchange.getIn().setBody(PDFHelper.decorateTextWithXSLFO("Test Content"));

        producer.process(exchange);
        PDDocument document = getDocumentFrom(exchange);
        assertTrue(document.isEncrypted());
    }

}
