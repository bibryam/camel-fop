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
import org.apache.camel.util.IntrospectionSupport;
import org.apache.fop.apps.*;
import org.apache.fop.pdf.PDFEncryptionParams;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * The Fop producer.
 */
public class FopProducer extends DefaultProducer {
    public static final String CAMEL_FOP_RENDER = "CamelFop.render.";
    public static final String CAMEL_FOP_ENCRYPT = "CamelFop.encrypt.";
    private final FopFactory fopFactory;

    public FopProducer(FopEndpoint endpoint, FopFactory fopFactory) {
        super(endpoint);
        this.fopFactory = fopFactory;
    }

    public void process(Exchange exchange) throws Exception {
        FOUserAgent userAgent = fopFactory.newFOUserAgent();
        Source src = exchange.getIn().getBody(StreamSource.class);
        Map<String, Object> headers = exchange.getIn().getHeaders();

        setRenderParameters(userAgent, headers);
        setEncryptionParameters(userAgent, headers);

        OutputStream out = transform(userAgent, src);
        exchange.getOut().setBody(out);
    }

    private OutputStream transform(FOUserAgent userAgent, Source src) throws FOPException, TransformerException {
        OutputStream out = new ByteArrayOutputStream();
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();

        Result res = new SAXResult(fop.getDefaultHandler());
        transformer.transform(src, res);
        return out;
    }

    private void setEncryptionParameters(FOUserAgent userAgent, Map<String, Object> headers) throws Exception {
        Map<String, Object> encryptionParameters = IntrospectionSupport.extractProperties(headers, CAMEL_FOP_ENCRYPT);
        if (!encryptionParameters.isEmpty()) {
            PDFEncryptionParams encryptionParams = new PDFEncryptionParams();
            IntrospectionSupport.setProperties(encryptionParams, encryptionParameters);
            userAgent.getRendererOptions().put("encryption-params", encryptionParams);
        }
    }

    private void setRenderParameters(FOUserAgent userAgent, Map<String, Object> headers) throws Exception {
        Map<String, Object> parameters = IntrospectionSupport.extractProperties(headers, CAMEL_FOP_RENDER);
        if (!parameters.isEmpty()) {
            IntrospectionSupport.setProperties(userAgent, parameters);
        }
    }
}
