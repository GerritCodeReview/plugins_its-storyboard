load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
)

gerrit_plugin(
    name = "its-storyboard",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/**/*"]),
    manifest_entries = [
        "Gerrit-PluginName: its-storyboard",
        "Gerrit-Module: com.googlesource.gerrit.plugins.its.storyboard.StoryboardModule",
        "Gerrit-ReloadMode: reload",
        "Gerrit-ApiType: plugin",
        "Implementation-Title: its-storyboard plugin",
        "Implementation-URL: https://gerrit.googlesource.com/plugins/its-storyboard",
    ],
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
