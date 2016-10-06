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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author bbende
 */
public class TestEmbeddedSolrServerFactory {

    static final String CONFIGSET_DIR = "src/test/resources/configsets";

    static SolrClient solrClient;

    @BeforeClass
    public static void setupClass() {
        try {
            String targetLocation = EmbeddedSolrServerFactory.class
                    .getProtectionDomain().getCodeSource().getLocation().getFile();

            String solrHome = targetLocation + "/solr";

            solrClient = EmbeddedSolrServerFactory.create(solrHome, CONFIGSET_DIR, "exampleCollection");

            // create some test documents
            SolrInputDocument doc1 = new SolrInputDocument();
            doc1.addField("id", "1");

            SolrInputDocument doc2 = new SolrInputDocument();
            doc2.addField("id", "2");

            SolrInputDocument doc3 = new SolrInputDocument();
            doc3.addField("id", "3");

            SolrInputDocument doc4 = new SolrInputDocument();
            doc4.addField("id", "4");

            SolrInputDocument doc5 = new SolrInputDocument();
            doc5.addField("id", "5");

            // add the test data to the index
            solrClient.add(Arrays.asList(doc1, doc2, doc3, doc4, doc5));
            solrClient.commit();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @AfterClass
    public static void teardownClass() {
        try {
            solrClient.close();
        } catch (Exception e) {
        }
    }

    @Test
    public void testEmbeddedSolrServerFactory() throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        QueryResponse response = solrClient.query(solrQuery);
        Assert.assertNotNull(response);

        SolrDocumentList solrDocuments = response.getResults();
        Assert.assertNotNull(solrDocuments);
        Assert.assertEquals(5, solrDocuments.getNumFound());
    }

}
