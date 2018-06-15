load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)

gerrit_plugin(
    name = "its-storyboard",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: its-storyboard",
        "Gerrit-Module: com.googlesource.gerrit.plugins.its.storyboard.StoryboardModule",
        "Gerrit-ReloadMode: reload",
        "Implementation-Title: its-storyboard plugin",
        "Implementation-URL: https://gerrit.googlesource.com/plugins/its-storyboard",
    ],
    resources = glob(["src/main/**/*"]),
    deps = [
        "//plugins/its-base",
    ],
)

junit_tests(
    name = "its_storyboard_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    tags = ["its-storyboard"],
    deps = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":its-storyboard__plugin",
        "//plugins/its-base:its-base",
        "//plugins/its-base:its-base_tests-utils",
    ],
)
