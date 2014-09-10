gerrit_plugin(
  name = 'its-storyboard',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: its-storyboard',
    'Gerrit-Module: com.googlesource.gerrit.plugins.hooks.bz.InitStoryboard',
    'Gerrit-InitStep: com.googlesource.gerrit.plugins.hooks.bz.StoryboardModule',
    'Gerrit-ReloadMode: reload',
    'Implementation-Title: Plugin its-storyboard',
    'Implementation-URL: https://www.wikimediafoundation.org',
  ],
  deps = [
    '//plugins/its-base:its-base__plugin',
  ],
)

TEST_UTIL_SRC = glob(['src/test/java/com/googlesource/gerrit/plugins/hooks/testutil/**/*.java'])

java_library(
  name = 'its-storyboard_tests-utils',
  srcs = TEST_UTIL_SRC,
  deps = [
    '//lib:guava',
    '//lib/easymock:easymock',
    '//lib/log:impl_log4j',
    '//lib/log:log4j',
    '//lib:junit',
    '//lib/powermock:powermock-api-easymock',
    '//lib/powermock:powermock-api-support',
    '//lib/powermock:powermock-core',
    '//lib/powermock:powermock-module-junit4',
    '//lib/powermock:powermock-module-junit4-common',
  ],
)

java_test(
  name = 'its-storyboard_tests',
  srcs = glob(
    ['src/test/java/**/*.java'],
    excludes = TEST_UTIL_SRC
  ),
  labels = ['its-storyboard'],
  source_under_test = [':its-storyboard__plugin'],
  deps = [
    ':its-storyboard__plugin',
    ':its-storyboard_tests-utils',
    '//gerrit-plugin-api:lib',
    '//lib/easymock:easymock',
    '//lib:guava',
    '//lib/guice:guice',
    '//lib/jgit:jgit',
    '//lib:junit',
    '//lib/log:api',
    '//lib/log:impl_log4j',
    '//lib/log:log4j',
    '//lib/powermock:powermock-api-easymock',
    '//lib/powermock:powermock-api-support',
    '//lib/powermock:powermock-core',
    '//lib/powermock:powermock-module-junit4',
  ],
)
