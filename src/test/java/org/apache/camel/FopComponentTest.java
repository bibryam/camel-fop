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
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class FopComponentTest extends CamelTestSupport {

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;



    @Test
    public void testName() throws Exception {


        Endpoint endpoint = context().getEndpoint("file:data?delete=false");
        PollingConsumer pollingConsumer = endpoint.createPollingConsumer();

        pollingConsumer.start();

        Exchange exchange = pollingConsumer.receive();
        Object body = exchange.getIn().getBody();



        File original = new File("data/result/original_result.pdf");

        assertTrue(original.equals(body));


    }

    @Ignore
    @Test
    public void testFop() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);


//        Exchange exchange = createExchangeWithOptionalInBody("Some content to be stored");
//        template.send(exchange);
//        String newNodeId = exchange.getOut().getBody(String.class);


        assertMockEndpointsSatisfied();
    }


    private Exchange createExchangeWithOptionalInBody(String body) {
        DefaultExchange exchange = new DefaultExchange(context);
        if (body != null) {
            exchange.getIn().setBody(body);
        }
        return exchange;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
              //  from("direct:start")
                    from("file:data?delete=false")
                        .to("fop:test")
                        .setHeader(Exchange.FILE_NAME, constant("result.pdf"))
                        .to("file:data")
                        .to("mock:result");
            }
        };
    }
}
