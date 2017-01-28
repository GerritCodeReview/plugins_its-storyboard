Build
=====

This plugin is built with Bazel.

Clone or link this plugin to the plugins directory of Gerrit's source
tree, and issue the command:

```
  bazel build plugins/its-storyboard
```

The output is created in

```
  buck-out/gen/plugins/its-storyboard/its-storyboard.jar
  bazel-genfiles/plugins/its-storyboard/its-storyboard.jar
```

This project can be imported into the Eclipse IDE:

```
  ./tools/eclipse/project.py
```

To execute the tests run:

```
  bazel test plugins/its-storyboard:its_storyboard_tests
```
