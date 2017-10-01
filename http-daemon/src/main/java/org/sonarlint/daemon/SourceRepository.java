/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarlint.daemon;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.protocol.HTTP.CONTENT_TYPE;

public class SourceRepository {

  private RestClient lowLevelClient;
  private RestHighLevelClient highLevelClient;

  public String load(String uuid) {
    init();
    try {
      GetResponse response = highLevelClient.get(new GetRequest("files", "file", uuid).storedFields("code"));
      return (String) response.getField("code").getValue();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot load "+uuid, e);
    }
  }

  public String store(String code) {
    init();
    String storedAs = UUID.randomUUID().toString();
    try {
      HashMap<Object, Object> document = new HashMap<>();
      document.put("code", code);
      highLevelClient.index(new IndexRequest("files", "file", storedAs).source(document));
    } catch (IOException e) {
      throw new IllegalStateException("Cannot save", e);
    }
    return storedAs;
  }

  public void init() {
    if (lowLevelClient != null) {
      return;
    }
    lowLevelClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
    try {
      highLevelClient = new RestHighLevelClient(lowLevelClient);
      createIndexIfNeeded();
    } catch (Exception e) {
      try {
        lowLevelClient.close();
      } catch (Exception e1) {
        // ignore this one
      }
      throw new IllegalStateException("Cannot initialize the connection to Elasticsearch", e);
    }
  }

  private void createIndexIfNeeded() throws IOException {
    int indexExists = lowLevelClient.performRequest("HEAD", "/files").getStatusLine().getStatusCode();

    if (indexExists == 200) {
      return;
    }

    if (indexExists == 404) {
      createIndex();
      return;
    }

    throw new IllegalStateException("Unexpected response from Elasticsearch: " + indexExists);
  }

  private void createIndex() throws IOException {
    lowLevelClient.performRequest("PUT", "/files");
    HttpEntity fileMapping = new StringEntity("{\n" +
      "      \"properties\": {\n" +
      "        \"code\": {\n" +
      "          \"type\": \"keyword\",\n" +
      "          \"index\": \"false\",\n" +
      "          \"store\": \"true\"\n" +
      "        }\n" +
      "      }\n" +
      "  }");
    lowLevelClient.performRequest("PUT", "/files/_mapping/file", Collections.emptyMap(), fileMapping, new BasicHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType()));
  }

  private void dropIndex() throws IOException {
    lowLevelClient.performRequest("DELETE", "/files");
  }
}
