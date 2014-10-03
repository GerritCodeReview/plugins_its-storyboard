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
  private Section storyboard;
  private Section storyboardComment;
  private String storyboardUrl;
  private String storyboardUsername;
  private String storyboardPassword;

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
      ui.message("A Storyboard configuration for the 'hooks-storyboard' "
          + "plugin was found.\n");
      if (ui.yesno(true, "Copy it for the '%s' plugin?", pluginName)) {
        for (String n : flags.cfg.getNames("storyboard")) {
          flags.cfg.setStringList(pluginName, null, n,
              Arrays.asList(flags.cfg.getStringList("storyboard", null, n)));
        }
        for (String n : flags.cfg.getNames(COMMENT_LINK_SECTION, "storyboard")) {
          flags.cfg.setStringList(COMMENT_LINK_SECTION, pluginName, n, Arrays
              .asList(flags.cfg.getStringList(COMMENT_LINK_SECTION,
                  "storyboard", n)));
        }

        if (ui.yesno(false,
            "Remove configuration for 'hooks-storyboard' plugin?")) {
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

  private void init() throws IOException {
    this.storyboard = sections.get(pluginName, null);
    this.storyboardComment = sections.get(COMMENT_LINK_SECTION, pluginName);


    do {
      enterStoryboardConnectivity();
    } while (storyboardUrl != null
        && (isConnectivityRequested(storyboardUrl) &&
            !isStoryboardConnectSuccessful(storyboardUrl)));

    if (storyboardUrl == null) {
      return;
    }

    ui.header("Storyboard issue-tracking association");
    storyboardComment.string("Storyboard bug number regex", "match",
        "\\([Ss][Tt][Oo][Rr][Yy][ ]*[1-9][0-9]*\\)");
    storyboardComment.set("html",
        String.format("<a href=\"%s/#!/story/$1\">story $1</a>", storyboardUrl));
    storyboardComment.select("Bug number enforced in commit message",
        "association", ItsAssociationPolicy.SUGGESTED);
  }

  public void enterStoryboardConnectivity() {
    storyboardUrl =
        storyboard.string("Storyboard URL (empty to skip)", "url", null);
    if (storyboardUrl != null) {
      storyboardUsername =
          storyboard.string("Storyboard username", "username", "");
      storyboardPassword = storyboard.password("username", "password");
    }
  }

  private boolean isStoryboardConnectSuccessful(String url) throws IOException {
    ui.message("Checking Storyboard connectivity ... ");
    StoryboardClient client = new StoryboardClient(url);
    // TODO: should check wether use can authenticate
    if (client.isAvailable()) {
      return true;
    } else {
      ui.message("*FAILED* Could not connect to Storyboard\n");
      return false;
    }
  }
}
