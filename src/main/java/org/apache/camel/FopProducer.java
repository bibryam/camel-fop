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

import org.apache.camel.impl.DefaultProducer;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * The Fop producer.
 */
public class FopProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(FopProducer.class);
    private FopEndpoint endpoint;

    public FopProducer(FopEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;


    }

    public void process(Exchange exchange) throws Exception {


        FopFactory fopFactory = FopFactory.newInstance();
        //OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("/Users/bilginibryam/Desktop/myfile.pdf")));

        OutputStream out = new ByteArrayOutputStream();
        try {
            // Step 3: Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            // Step 4: Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity transformer

            // Step 5: Setup input and output for XSLT transformation
            // Setup input stream
            //Source src = new StreamSource(new File("/Users/bilginibryam/Desktop/myfile.fo"));

            Source src = exchange.getIn().getBody(StreamSource.class);

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Step 6: Start XSLT transformation and FOP processing
            transformer.transform(src, res);

        } finally {
            //Clean-up
            out.close();
        }

        exchange.getOut().setBody(out);


        System.out.println(exchange.getIn().getBody());
    }

}
