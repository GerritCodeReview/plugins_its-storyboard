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

  public final String HTTP_AUTH_BEARER = "Bearer gq8gC8n09XFZNgrjWslYCHSfDNMV7e";
  public final String HTTP_CONTENT_TYPE = "application/json; charset=utf-8";
  public final String STORIES_XPATH = "/api/v1/stories";

  private final String sbUrl;

  public StoryboardClient(final String baseUrl) throws IOException {
    sbUrl = baseUrl;
  }

  // checks whether an issue exists
  public boolean exists(final String issueId) throws IOException {
    String json = getStory(issueId);
    if (json != null) {
      return true;
    } else {
      return false;
    }
  }

  // checks whether storyboard is available/alive
  public boolean isAvailable() throws IOException {
    //TODO: change to getVersion when that REST endpoint is available
    String json = getStory("1");
    if (json != null) {
      return true;
    } else {
      return false;
    }
  }

  // gets the storyboard version
  // TODO: replace with a get version REST api when it's available
  public String getVersion() throws IOException {
    String version = "1.2.3";
    return version;
  }

  public String getStory(final String issueId) throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    String responseJson = null;

    try {
      HttpGet httpget = new HttpGet(sbUrl+STORIES_XPATH+"/"+issueId);
      log.info("Getting issue " + issueId + "from " + httpget.getRequestLine());
      CloseableHttpResponse response = client.execute(httpget);
      try {
        StatusLine sl = response.getStatusLine();
        int responseCode = sl.getStatusCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
          // get data from the response
          InputStream inputStream = response.getEntity().getContent();
          Reader reader = new InputStreamReader(inputStream);
          int contentLength = (int) response.getEntity().getContentLength();
          char[] charArray = new char[contentLength];
          reader.read(charArray);
          responseJson = new String(charArray);
          log.info("Retrievied story: " + responseJson);
        } else {
          log.warn("Failed executing request: " + responseCode);
        }
      } finally {
        response.close();
      }
    } finally {
      client.close();
    }
    return responseJson;
  }

  // add a comment to a story
  public void addComment(final String issueId, final String comment) throws IOException {
      log.info("Posting comment with data: ({},{})", issueId, comment);
      final String commentUrl = sbUrl+STORIES_XPATH+"/"+issueId+"/comments";
      CloseableHttpClient httpclient = HttpClients.createDefault();

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

  private void performSimpleActionChainable(final int bug, final String actionName,
      final String actionValue) throws
      InvalidTransitionException {
    if ("status".equals(actionName)) {
      //TODO: check that actionValue matches one of storyboard's status then update the
      //story's status to actionValue
    } else if ("resolution".equals(actionName)) {
      //TODO: check that actionValue matches one of storyboard's resolutions then update the
      //story's resolution to actionValue
    } else {
      throw new InvalidTransitionException("Simple action " + actionName
        + " is not known");
    }
  }

  private void performChainedAction(final int bug, final String actionName,
      final String actionValue) throws
      InvalidTransitionException {
    String[] actionNames = actionName.split("/");
    String[] actionValues = actionValue.split("/");
    if (actionNames.length != actionValues.length) {
      throw new InvalidTransitionException("Number of chained actions does not"
        + " match number of action values");
    }

    int i;
    for (i=0; i<actionNames.length; i++) {
        performSimpleActionChainable(bug, actionNames[i], actionValues[i]);
    }
  }

  public void performAction(final String issueId, final String actionNameParam,
      final String actionValueParam) throws
      InvalidTransitionException {
    int bug = Integer.parseInt(issueId);

    String actionName = actionNameParam;
    if (actionName.startsWith("set-")) {
      actionName = actionName.substring(4);
    }
    actionName = actionName.replaceAll("-and-", "/");

    String actionValue = actionValueParam;
    actionValue = actionValue.replaceAll(" ", "/");

    if ("status".equals(actionName) || "resolution".equals(actionName)) {
      performSimpleActionChainable(bug, actionName, actionValue);
    } else if ("status/resolution".equals(actionName)) {
      performChainedAction(bug, actionName, actionValue);
    } else {
      throw new InvalidTransitionException("Action " + actionNameParam
          + " is not known");
    }
    //TODO: replace with REST call to update the story's status
  }

  public String getSbUrl() {
    return sbUrl;
  }

}
