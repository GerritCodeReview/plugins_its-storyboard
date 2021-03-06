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

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.its.base.its.ItsFacade;
import java.io.IOException;
import java.net.URL;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoryboardItsFacade implements ItsFacade {
  private final Logger log = LoggerFactory.getLogger(StoryboardItsFacade.class);

  private static final String GERRIT_CONFIG_PASSWORD = "password";
  private static final String GERRIT_CONFIG_URL = "url";

  private final StoryboardClient client;

  @Inject
  public StoryboardItsFacade(@PluginName String pluginName, @GerritServerConfig Config cfg) {
    final String url = cfg.getString(pluginName, null, GERRIT_CONFIG_URL);
    final String password = cfg.getString(pluginName, null, GERRIT_CONFIG_PASSWORD);

    this.client = new StoryboardClient(url, password);
  }

  @Override
  public String healthCheck(final Check check) throws IOException {
    // This method is not used, so there is no need to implement it.
    return "unknown";
  }

  @Override
  public void addComment(final String issueId, final String comment) {

    if (!exists(issueId)) {
      log.warn("Storyboard item " + issueId + " does not exist");
      return;
    }

    try {
      client.addComment(issueId, comment);
    } catch (IOException e) {
      log.error("Error: unable to comment " + issueId);
    }
    log.info("Updated " + issueId + "with comment: " + comment);
  }

  @Override
  public void addRelatedLink(final String issueId, final URL relatedUrl, String description)
      throws IOException {
    addComment(
        issueId, "Related URL: " + createLinkForWebui(relatedUrl.toExternalForm(), description));
  }

  @Override
  public void performAction(final String issueId, final String actionString) {

    try {
      String actionName = actionString.substring(0, actionString.indexOf(" ")).toLowerCase();
      String actionValue = actionString.substring(actionString.indexOf(" ") + 1).toLowerCase();
      if (actionName.equals("set-status")) {
        if (!client.getTaskStatus(issueId).toLowerCase().equals(actionValue)) {
          log.info("Updating task " + issueId + " with status: " + actionValue);
          client.setStatus(issueId, actionValue);
        }
      }
    } catch (StringIndexOutOfBoundsException e) {
      log.error("Error: Invalid action: " + actionString);
    } catch (IOException e) {
      log.error("Error: Failed to peform action: " + actionString);
    }
  }

  @Override
  public boolean exists(final String issudeId) {
    String info = null;
    try {
      info = client.getStory(issudeId);
    } catch (IOException e) {
      log.error("Error: Storyboard is not accessible at: " + GERRIT_CONFIG_URL);
    }
    if (info != null) {
      log.debug("Story exists, info: " + info);
      return true;
    }
    return false;
  }

  @Override
  public String createLinkForWebui(String url, String text) {
    if (text != null && !text.isEmpty() && !text.equals(url)) {
      return "[" + text + "]" + "(" + url + ")";
    }
    return url;
  }
}
