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

import static org.easymock.EasyMock.expect;

import org.eclipse.jgit.lib.Config;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.FactoryModule;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;

import com.googlesource.gerrit.plugins.its.base.testutil.LoggingMockingTestCase;

public class StoryboardItsFacadeTest extends LoggingMockingTestCase {
  private Injector injector;
  private Config serverConfig;

  public void testCreateLinkForWebUiDifferentUrlAndText() {
    mockUnconnectableStoryboard();

    replayMocks();

    StoryboardItsFacade itsFacade = createStoryboardItsFacade();
    String actual = itsFacade.createLinkForWebui("Test-Url", "Test-Text");

    assertEquals("[Test-Url|Test-Text]", actual);
  }

  public void testCreateLinkForWebUiSameUrlAndText() {
    mockUnconnectableStoryboard();

    replayMocks();

    StoryboardItsFacade itsFacade = createStoryboardItsFacade();
    String actual = itsFacade.createLinkForWebui("Test-Url", "Test-Url");

    assertEquals("[Test-Url]", actual);
  }

  public void testCreateLinkForWebUiNullText() {
    mockUnconnectableStoryboard();

    replayMocks();

    StoryboardItsFacade itsFacade = createStoryboardItsFacade();
    String actual = itsFacade.createLinkForWebui("Test-Url", null);

    assertEquals("[Test-Url]", actual);
  }

  public void testCreateLinkForWebUiEmptyText() {
    mockUnconnectableStoryboard();

    replayMocks();

    StoryboardItsFacade itsFacade = createStoryboardItsFacade();
    String actual = itsFacade.createLinkForWebui("Test-Url", "");

    assertEquals("[Test-Url]", actual);
  }

  private StoryboardItsFacade createStoryboardItsFacade() {
    return injector.getInstance(StoryboardItsFacade.class);
  }

  private void mockUnconnectableStoryboard() {
    expect(serverConfig.getString("its-storyboard",  null, "url"))
    .andReturn("<no-url>").anyTimes();
    expect(serverConfig.getString("its-storyboard",  null, "username"))
    .andReturn("none").anyTimes();
    expect(serverConfig.getString("its-storyboard",  null, "password"))
    .andReturn("none").anyTimes();
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    injector = Guice.createInjector(new TestModule());
  }

  private class TestModule extends FactoryModule {
    @Override
    protected void configure() {
      serverConfig = createMock(Config.class);
      bind(Config.class).annotatedWith(GerritServerConfig.class)
          .toInstance(serverConfig);
      bind(String.class).annotatedWith(PluginName.class)
          .toInstance("its-storyboard");
    }
  }
}