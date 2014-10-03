// Copyright (C) 2014 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.its.storyboard;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StoryboardClient {

  private static final Logger log = LoggerFactory.getLogger(
      StoryboardClient.class);

  public static final String STORIES_ENDPOINT = "/api/v1/stories";
  public static final String SYS_INFO_ENDPOINT = "/api/v1/systeminfo";

  private final String baseUrl;
  private final String username;
  private final String password;

  public StoryboardClient(final String baseUrl, final String username,
      String password) {
    this.baseUrl = baseUrl;
    this.username = username;
    this.password = password;
  }

  // generic method to get data from a REST endpoint
  public String getData(final String url) throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    String responseJson = null;

    try {
      HttpGet httpget = new HttpGet(url);
      log.debug("Making request for " + httpget.getRequestLine());
      CloseableHttpResponse response = client.execute(httpget);
      try {
        StatusLine sl = response.getStatusLine();
        int responseCode = sl.getStatusCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
          log.debug("Retreiving data from response " + httpget.getRequestLine());
          InputStream inputStream = response.getEntity().getContent();
          Reader reader = new InputStreamReader(inputStream);
          int contentLength = (int) response.getEntity().getContentLength();
          char[] charArray = new char[contentLength];
          reader.read(charArray);
          responseJson = new String(charArray);
          log.debug("Data retreived: " + responseJson);
        } else {
          log.error("Failed request: " + httpget.getRequestLine() +
              " with response: " + responseCode);
        }
      } finally {
        response.close();
      }
    } finally {
      client.close();
    }
    return responseJson;
  }

  // generic method to POST data with a REST endpoint
  public void postData(final String url, final String data)
      throws IOException {
    CloseableHttpClient httpclient = HttpClients.createDefault();

    try {
      HttpPost httpPost = new HttpPost(url);
      httpPost.addHeader("Authorization", "Bearer " + password);
      httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
      httpPost.setEntity(new StringEntity(data, "utf-8"));

      log.debug("Executing request " + httpPost.getRequestLine());
      CloseableHttpResponse response = httpclient.execute(httpPost);
      try {
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
          log.info("Updated " + url + " with " + data);
        } else {
          log.error("Failed to add comment, response: " + responseCode +
              " (" + response.getStatusLine().getReasonPhrase() + ")");
        }
      } finally {
        response.close();
      }
    } finally {
      httpclient.close();
    }
  }

  public String getSysInfo() throws IOException {
    return getData(this.baseUrl + SYS_INFO_ENDPOINT);
  }

  public String getStory(final String issueId) throws IOException {
    return getData(this.baseUrl + STORIES_ENDPOINT + "/" + issueId);
  }

  public void addComment(final String issueId, final String comment)
      throws IOException {
    log.debug("Posting comment with data: ({},{})", issueId, comment);
    final String url = baseUrl+STORIES_ENDPOINT+"/"+issueId+"/comments";
    final String escapedComment = comment.replace("\n", "\\n");
    final String json =
        "{\"story_id\":\"" + issueId + "\",\"content\":\"" +
        escapedComment + "\"}";

    postData(url, json);
  }
}
