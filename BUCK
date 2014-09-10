gerrit_plugin(
  name = 'its-storyboard',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: its-storyboard',
    'Gerrit-Module: com.googlesource.gerrit.plugins.hooks.sb.InitStoryboard',
    'Gerrit-InitStep: com.googlesource.gerrit.plugins.hooks.sb.StoryboardModule',
    'Gerrit-ReloadMode: reload',
    'Implementation-Title: Plugin its-storyboard',
    'Implementation-URL: https://www.openstack.org',
  ],
  deps = [
    '//plugins/its-base:its-base__plugin',
  ],
)
