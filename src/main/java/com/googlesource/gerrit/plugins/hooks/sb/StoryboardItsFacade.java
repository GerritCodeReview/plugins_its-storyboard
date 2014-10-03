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
import java.net.URL;
import java.util.concurrent.Callable;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;

import com.googlesource.gerrit.plugins.hooks.its.ItsFacade;

public class StoryboardItsFacade implements ItsFacade {
  private static final String GERRIT_CONFIG_USERNAME = "username";
  private static final String GERRIT_CONFIG_PASSWORD = "password";
  private static final String GERRIT_CONFIG_URL = "url";

  private static final int MAX_ATTEMPTS = 3;

  private Logger log = LoggerFactory.getLogger(StoryboardItsFacade.class);

  private final String pluginName;
  private Config gerritConfig;

  private StoryboardClient client;

  @Inject
  public StoryboardItsFacade(@PluginName String pluginName,
      @GerritServerConfig Config cfg) {
    this.pluginName = pluginName;
    try {
      this.gerritConfig = cfg;
      log.info("Configuring Storyboard plugin with connection " + getUrl());
    } catch (Exception ex) {
      log.warn("Error configuring Storyboard plugin", ex);
    }
  }

  @Override
  public String name() {
    return pluginName;
  }

  @Override
  public String healthCheck(final Check check) throws IOException {
      return execute(new Callable<String>(){
        @Override
        public String call() throws Exception {
          if (check.equals(Check.ACCESS))
            return healthCheckAccess();
          else
            return healthCheckSysinfo();
        }});
  }

  @Override
  public void addComment(final String issueId, final String comment) throws IOException {

    execute(new Callable<String>(){
      @Override
      public String call() throws Exception {
        client().addComment(issueId, comment);
        return issueId;
      }});
  }

  @Override
  public void addRelatedLink(final String issueId, final URL relatedUrl, String description)
      throws IOException {
    addComment(issueId, "Related URL: " + createLinkForWebui(relatedUrl.toExternalForm(), description));
  }

  @Override
  public void performAction(final String issueId, final String actionString)
      throws IOException {

    execute(new Callable<String>(){
      @Override
      public String call() throws Exception {
        String actionName = actionString.substring(0, actionString.indexOf(" "));
        String actionValue = actionString.substring(actionString.indexOf(" ") + 1);
        doPerformAction(issueId, actionName, actionValue);
        return issueId;
      }});
  }

  private void doPerformAction(final String issueId, final String fieldName, final String fieldValue)
      throws IOException {
    client().performAction(issueId, fieldName.toLowerCase(), fieldValue);
  }

  @Override
  public boolean exists(final String issudeId) throws IOException {
    return execute(new Callable<Boolean>(){
      @Override
      public Boolean call() throws Exception {
        return client().exists(issudeId);
      }});
  }

  private StoryboardClient client() throws IOException {

    if (client == null) {
      try {
        log.info("Connecting to Storyboard at URL " + getUrl());
        client = new StoryboardClient(getUrl());
        log.info("Autenthicating as user " + getUsername());
      } catch (Exception ex) {
        log.info("Unable to connect to " + getUrl() + " as "
            + getUsername());
        throw new IOException(ex);
      }
    }

    //TODO: attempt to authenticate?
    return client;
  }

  private <P> P execute(Callable<P> function) throws IOException {

    int attempt = 0;
    while(true) {
      try {
        return function.call();
      } catch (Exception ex) {
        if (isRecoverable(ex) && ++attempt < MAX_ATTEMPTS) {
          log.debug("Call failed - retrying, attempt {} of {}", attempt, MAX_ATTEMPTS);
          //TODO: may need to authenticate or re-authenticate
          continue;
        }

        if (ex instanceof IOException)
          throw ((IOException)ex);
        else
          throw new IOException(ex);
      }
    }
  }

  private boolean isRecoverable(Exception ex) {
    return false;
  }

  private String getPassword() {
    final String pass =
        gerritConfig.getString(pluginName, null,
            GERRIT_CONFIG_PASSWORD);
    return pass;
  }

  private String getUsername() {
    final String user =
        gerritConfig.getString(pluginName, null,
            GERRIT_CONFIG_USERNAME);
    return user;
  }

  private String getUrl() {
    final String url =
        gerritConfig.getString(pluginName, null, GERRIT_CONFIG_URL);
    return url;
  }

  @Override
  public String createLinkForWebui(String url, String text) {
    String ret = url;
    if (text != null && ! text.equals(url)) {
        ret += " (" + text + ")";
    }
    return ret;
  }

  private String healthCheckAccess() throws IOException {
    String status = null;
    if (!client.isAvailable()) {
      status = "{\"status\"=\"Storyboard not available\"}";
    } else {
      status = "{\"status\"=\"ok\",\"username\"=\""+getUsername()+"\"}";
    }
    log.info("Healtheck on access result: {}", status);
    return status;
  }

  private String healthCheckSysinfo() throws IOException {
    //TODO: replace with a get storyboard version via REST api
    String version = client.getVersion();
    final String result = "{\"status\"=\"ok\",\"system\"=\"Storyboard\",\"version\"=\""+version+"\",\"url\"=\""+getUrl()+"\"}";
    log.info("Healtheck on sysinfo result: {}", result);
    return result;
  }
}
