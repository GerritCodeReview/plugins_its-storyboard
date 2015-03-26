Build
=====

This plugin is built with Buck.

Clone or link this plugin to the plugins directory of Gerrit's source
tree, and issue the command:

```
  buck build plugins/its-storyboard
```

The output is created in

```
  buck-out/gen/plugins/its-storyboard/its-storyboard.jar
```

This project can be imported into the Eclipse IDE:

```
  ./tools/eclipse/project.py
```

To execute the tests run:

```
  buck test --all --include its-storyboard
```
