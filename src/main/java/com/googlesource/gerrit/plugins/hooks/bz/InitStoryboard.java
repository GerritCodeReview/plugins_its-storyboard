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

package com.googlesource.gerrit.plugins.hooks.bz;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.api.AllProjectsConfig;
import com.google.gerrit.pgm.init.api.AllProjectsNameOnInitProvider;
import com.google.gerrit.pgm.init.api.InitFlags;
import com.google.gerrit.pgm.init.api.Section;
import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.googlesource.gerrit.plugins.hooks.its.InitIts;
import com.googlesource.gerrit.plugins.hooks.validation.ItsAssociationPolicy;

import org.eclipse.jgit.errors.ConfigInvalidException;

import java.io.IOException;
import java.util.Arrays;

/** Initialize the GitRepositoryManager configuration section. */
@Singleton
class InitStoryboard extends InitIts {
  private final String pluginName;
  private final Section.Factory sections;
  private final InitFlags flags;
  private Section issue;
  private Section issueComment;
  private String issueUrl;
  private String issueUsername;
  private String issuePassword;

  @Inject
  InitStoryboard(@PluginName String pluginName, ConsoleUI ui,
      Section.Factory sections, AllProjectsConfig allProjectsConfig,
      AllProjectsNameOnInitProvider allProjects, InitFlags flags) {
    super(pluginName, "Storyboard", ui, allProjectsConfig, allProjects);
    this.pluginName = pluginName;
    this.sections = sections;
    this.flags = flags;
  }

  @Override
  public void run() throws IOException, ConfigInvalidException {
    super.run();

    ui.message("\n");
    ui.header("Storyboard connectivity");

    if (!pluginName.equalsIgnoreCase("storyboard")
        && !flags.cfg.getSections().contains(pluginName)
        && flags.cfg.getSections().contains("storyboard")) {
      ui.message("A Storyboard configuration for the 'hooks-storyboard' plugin was found.\n");
      if (ui.yesno(true, "Copy it for the '%s' plugin?", pluginName)) {
        for (String n : flags.cfg.getNames("storyboard")) {
          flags.cfg.setStringList(pluginName, null, n,
              Arrays.asList(flags.cfg.getStringList("storyboard", null, n)));
        }
        for (String n : flags.cfg.getNames(COMMENT_LINK_SECTION, "storyboard")) {
          flags.cfg.setStringList(COMMENT_LINK_SECTION, pluginName, n,
              Arrays.asList(flags.cfg.getStringList(COMMENT_LINK_SECTION, "storyboard", n)));
        }

        if (ui.yesno(false, "Remove configuration for 'hooks-storyboard' plugin?")) {
          flags.cfg.unsetSection("storyboard", null);
          flags.cfg.unsetSection(COMMENT_LINK_SECTION, "storyboard");
        }
      } else {
        init();
      }
    } else {
      init();
    }
  }

  private void init() {
    this.issue = sections.get(pluginName, null);
    this.issueComment = sections.get(COMMENT_LINK_SECTION, pluginName);


    do {
      enterStoryboardConnectivity();
    } while (issueUrl != null
        && (isConnectivityRequested(issueUrl) && !isStoryboardConnectSuccessful()));

    if (issueUrl == null) {
      return;
    }

    ui.header("Storyboard issue-tracking association");
    issueComment.string("Storyboard bug number regex", "match", "\\([Bb][Uu][Gg][ ]*[1-9][0-9]*\\)");
    issueComment.set("html",
        String.format("<a href=\"%s/show_bug.cgi?id=$1\">$1</a>", issueUrl));
    issueComment.select("Bug number enforced in commit message", "association",
        ItsAssociationPolicy.SUGGESTED);
  }

  public void enterStoryboardConnectivity() {
    issueUrl = issue.string("Storyboard URL (empty to skip)", "url", null);
    if (issueUrl != null) {
      issueUsername = issue.string("Storyboard username", "username", "");
      issuePassword = issue.password("username", "password");
    }
  }

  private boolean isStoryboardConnectSuccessful() {
    ui.message("Checking Storyboard connectivity ... ");
    return false;
  }
}
