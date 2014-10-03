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

import static org.easymock.EasyMock.expect;

import org.eclipse.jgit.lib.Config;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.FactoryModule;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.googlesource.gerrit.plugins.hooks.testutil.LoggingMockingTestCase;

public class StoryboardItsFacadeTest extends LoggingMockingTestCase {
  private Injector injector;
  private Config serverConfig;

  public void testCreateLinkForWebUiPlain() {
    mockUnconnectablestoryboard();

    replayMocks();

    StoryboardItsFacade itsFacade = createStoryboardItsFacade();
    String actual = itsFacade.createLinkForWebui("Test-Url", "Test-Text");

    assertNotNull("Created link is null", actual);
    assertTrue("Created link does not contain url",
        actual.contains("Test-Url"));
    assertTrue("Created link does not contain text",
        actual.contains("Test-Text"));

    assertUnconnectablestoryboard();
  }

  public void testCreateLinkForWebUiUrlEqualsText() {
    mockUnconnectablestoryboard();

    replayMocks();

    StoryboardItsFacade itsFacade = createStoryboardItsFacade();
    String actual = itsFacade.createLinkForWebui("Test-Url", "Test-Url");

    assertNotNull("Created link is null", actual);
    assertEquals("Created link does not match", "Test-Url", actual);

    assertUnconnectablestoryboard();
  }

  public void testCreateLinkForWebUiUrlEqualsNull() {
    mockUnconnectablestoryboard();

    replayMocks();

    StoryboardItsFacade itsFacade = createStoryboardItsFacade();
    String actual = itsFacade.createLinkForWebui("Test-Url", null);

    assertNotNull("Created link is null", actual);
    assertEquals("Created link does not match", "Test-Url", actual);

    assertUnconnectablestoryboard();
  }

  private StoryboardItsFacade createStoryboardItsFacade() {
    return injector.getInstance(StoryboardItsFacade.class);
  }

  private void mockUnconnectablestoryboard() {
    expect(serverConfig.getString("its-storyboard",  null, "url"))
    .andReturn("<no-url>").anyTimes();
    expect(serverConfig.getString("its-storyboard",  null, "username"))
    .andReturn("none").anyTimes();
    expect(serverConfig.getString("its-storyboard",  null, "password"))
    .andReturn("none").anyTimes();
  }

  private void assertUnconnectablestoryboard() {
    assertLogMessageContains("Configuring Storyboard plugin");
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