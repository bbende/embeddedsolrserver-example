/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bbende
 */
public class EmbeddedSolrServerFactory {

    public static SolrClient create(String solrHome, String coreHome, String coreName, String dataDir)
            throws IOException {

        Map<String,String> props = new HashMap<>();
        if (dataDir != null) {
            File coreDataDir = new File(dataDir + "/" + coreName);
            if (coreDataDir.exists()) {
                FileUtils.deleteDirectory(coreDataDir);
            }
            props.put("dataDir", dataDir + "/" + coreName);
        }

        final CoreContainer coreContainer = new CoreContainer(solrHome);
        coreContainer.load();

        // if instanceDirPath is a relative path then the core is created, but it writes the core.properties
        // underneath the instanceDirPath relative to Solr home, so we end up with:
        // src/test/resources/solr/src/test/resources/exampleConfig/core.properties

        // if we put toAbsolutePath() on the end then we end up with:
        // org.apache.solr.common.SolrException: Error CREATEing SolrCore 'exampleCollection': Could not create a new core in /Users/bbende/Projects/solrcore-datdir-test/src/test/resources/exampleCollectionas another core is already defined there
        // at org.apache.solr.core.CoreContainer.create(CoreContainer.java:809)

        Path instanceDirPath = Paths.get(coreHome).resolve(coreName).toAbsolutePath();
        SolrCore core = coreContainer.create(coreName, instanceDirPath, props);

        return new EmbeddedSolrServer(coreContainer, coreName);
    }

    public static void main(String[] args) throws IOException {

        String solrHome = "src/test/resources/solr";
        String coreHome = "src/test/resources";
        String coreName = "exampleCollection";

        String dataDir = EmbeddedSolrServerFactory.class
                .getProtectionDomain().getCodeSource().getLocation().getFile() + "../../target";

        System.out.println("dataDir = " + dataDir);

        try(SolrClient solrClient = EmbeddedSolrServerFactory.create(solrHome, coreHome, coreName, dataDir)) {

        }

    }

}
