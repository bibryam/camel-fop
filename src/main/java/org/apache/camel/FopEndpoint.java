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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Represents a Fop endpoint.
 */
public class FopEndpoint extends DefaultEndpoint {
    private String userConfigURL;
    private FopFactory fopFactory;

    public FopEndpoint() {
    }

    public FopEndpoint(String uri, FopComponent component) {
        super(uri, component);
        this.fopFactory = FopFactory.newInstance();
    }

    public FopEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new FopProducer(this, fopFactory);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Consumer not supported for FOP endpoint");
    }

    public boolean isSingleton() {
        return true;
    }

    public FopFactory getFopFactory() {
        return fopFactory;
    }

    public String getUserConfigURL() {
        return userConfigURL;
    }

    public void setUserConfigURL(String userConfigURL) {
        this.userConfigURL = userConfigURL;
        updateConfigurations();
    }

    private void updateConfigurations() {
        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
        Configuration cfg = null;
        try {
            cfg = cfgBuilder.buildFromFile(this.getUserConfigURL());
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
        try {
            fopFactory.setUserConfig(cfg);
        } catch (FOPException e) {
            throw new RuntimeException(e);
        }
    }
}
