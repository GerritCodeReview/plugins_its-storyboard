// Copyright (C) 2013 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.hooks.sb;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import com.googlesource.gerrit.plugins.hooks.its.InvalidTransitionException;
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

  private static final Logger log = LoggerFactory.getLogger(StoryboardClient.class);

  public static final String HTTP_CONTENT_TYPE = "application/json; charset=utf-8";
  public static final String STORIES_ENDPOINT = "/api/v1/stories";
  public static final String SYS_INFO_ENDPOINT = "/api/v1/systeminfo";

  private final String baseUrl;
  private final String username;
  private final String token;

  public StoryboardClient(final String baseUrl, final String username,
      String token) {
    this.baseUrl = baseUrl;
    this.username = username;
    this.token = token;
  }

  // checks whether an issue exists
  public boolean exists(final String issueId) throws IOException {
    String info = getStory(issueId);
    if (info != null) {
      return true;
    } else {
      return false;
    }
  }

  // checks whether storyboard is available/alive
  public boolean isAvailable() throws IOException {
    String info = getSysInfo();
    if (info != null) {
      return true;
    } else {
      return false;
    }
  }

  // generic method to get data from storyboard url endpoint
  public String getData(String url) throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    String responseJson = null;

    try {
      HttpGet httpget = new HttpGet(url);
      log.info("Making request for " + httpget.getRequestLine());
      CloseableHttpResponse response = client.execute(httpget);
      try {
        StatusLine sl = response.getStatusLine();
        int responseCode = sl.getStatusCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
          log.info("Retreiving data from response " + httpget.getRequestLine());
          InputStream inputStream = response.getEntity().getContent();
          Reader reader = new InputStreamReader(inputStream);
          int contentLength = (int) response.getEntity().getContentLength();
          char[] charArray = new char[contentLength];
          reader.read(charArray);
          responseJson = new String(charArray);
          log.info("Data retreived: " + responseJson);
        } else {
          log.error("Failed to execute request: " + responseCode);
        }
      } finally {
        response.close();
      }
    } finally {
      client.close();
    }
    return responseJson;
  }

  public String getSysInfo() throws IOException {
    return getData(this.baseUrl + SYS_INFO_ENDPOINT);
  }

  public String getStory(final String issueId) throws IOException {
    return getData(this.baseUrl + STORIES_ENDPOINT + "/" + issueId);
  }

  // add a comment to a story
  public void addComment(final String issueId, final String comment) throws IOException {
      log.info("Posting comment with data: ({},{})", issueId, comment);
      final String commentUrl = baseUrl+STORIES_ENDPOINT+"/"+issueId+"/comments";
      CloseableHttpClient httpclient = HttpClients.createDefault();
      final String HTTP_AUTH_BEARER = "Bearer " + this.token;

      try {
        HttpPost httpPost = new HttpPost(commentUrl);
        httpPost.addHeader("Authorization", HTTP_AUTH_BEARER);
        httpPost.addHeader("Content-Type", HTTP_CONTENT_TYPE);
        String escapedMessage = comment.replace("\n", "\\n");
        String json =
            "{\"story_id\":\"" + issueId + "\",\"content\":\"" + escapedMessage + "\"}";
        StringEntity params = new StringEntity(json, "utf-8");
        httpPost.setEntity(params);

        log.info("Executing request " + httpPost.getRequestLine());
        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
          int responseCode = response.getStatusLine().getStatusCode();
          if (responseCode != HttpURLConnection.HTTP_OK) {
            log.warn("Failed to add comment: " + responseCode);
          }
        } finally {
          response.close();
        }
      } finally {
        httpclient.close();
      }
  }

  public void performAction(final String issueId, final String actionNameParam,
      final String actionValueParam) throws
      InvalidTransitionException {
      //TODO: implement to update storyboard status
  }
}
