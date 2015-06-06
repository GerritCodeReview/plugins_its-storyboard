gerrit_plugin(
  name = 'its-storyboard',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: its-storyboard',
    'Gerrit-Module: com.googlesource.gerrit.plugins.its.storyboard.StoryboardModule',
    'Gerrit-ReloadMode: reload',
    'Gerrit-ApiType: plugin',
    'Implementation-Vendor: Hewlett Packard',
    'Implementation-Title: its-storyboard plugin',
    'Implementation-URL: https://gerrit.googlesource.com/plugins/its-storyboard',
  ],
  deps = [
    ':its-base_stripped',
    '//lib/httpcomponents:httpcore',
    '//lib/httpcomponents:httpclient',
    '//lib:gson',
  ],
)


def strip_jar(
    name,
    src,
    excludes = [],
    visibility = [],
  ):
  name_zip = name + '.zip'
  genrule(
    name = name_zip,
    cmd = 'cp $SRCS $OUT && zip -qd $OUT ' + ' '.join(excludes),
    srcs = [ src ],
    deps = [ src ],
    out = name_zip,
    visibility = visibility,
  )
  prebuilt_jar(
    name = name,
    binary_jar = ':' + name_zip,
    visibility = visibility,
  )

strip_jar(
  name = 'its-base_stripped',
  src = '//plugins/its-base:its-base',
  excludes = [
    'Documentation/about.md',
    'Documentation/build.md',
    'Documentation/config-connectivity.md',
    'Documentation/config-rulebase-plugin-actions.md',
  ]
)

TEST_UTIL_SRC = glob(['src/test/java/com/googlesource/gerrit/plugins/its/testutil/**/*.java'])

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
    ':its-base_stripped',
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
    '//lib/powermock:powermock-module-junit4-common',
    '//lib/powermock:powermock-reflect',
    '//lib/httpcomponents:httpclient',
    '//lib:gson',
  ],
)
