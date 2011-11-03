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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class FopComponentTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Ignore
    @Test
    public void createPDFUsingXMLDataAndXSLTTransformation() throws Exception {
        resultEndpoint.expectedMessageCount(1);
        InputStream file = new FileInputStream("src/test/data/xml/data.xml");

        template.sendBody(file);
        resultEndpoint.assertIsSatisfied();
        PDDocument document = PDDocument.load("target/data/result.pdf");
        String pdfText = PDFHelper.extractTextFrom(document);
        assertTrue(pdfText.contains("Project"));
        assertTrue(pdfText.contains("John Doe"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .to("xslt:xslt/template.xsl")
                        .to("fop:pdf")
                        .setHeader(Exchange.FILE_NAME, constant("result.pdf"))
                        .to("file:target/data")
                        .to("mock:result");

            }
        };
    }
}
