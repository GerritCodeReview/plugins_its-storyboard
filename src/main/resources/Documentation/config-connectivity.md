Configuring @PLUGIN@
====================

For general instructions on how to enable and configure its plugins please refer
to the [`its-base`][its-base] configuration documentation.  Instructions in this
document are specific to the @PLUGIN@ plugin.

Steps required to configure the plugin:

#. Make sure that your Storyboard instance is running and has the
    [REST endpoint enabled][rest-enabled].
#. [Configure the connection and associate the changes with stories][its-connection].
#. [Associate a changes with stories][its-associate-change].
#. [Configure the actions][its-actions] that the plugin will take on a Gerrit change.
#. [Enable the @PLUGIN@ plugin][its-enable] for the Gerrit project.


[rest-enabled]: #rest-enabled
<a name="rest-enabled">Checking REST API availability</a>
---------------------------------------------------------

Assuming the Storyboard instance you want to connect to is at
`http://my.storyboard.instance.example.org/`, open

```
http://my.storyboard.instance.example.org/api/v1/systeminfo
```

in your browser. If you get a xml response page without errors, the REST
interface is enabled.

If you get an error page then you'll need to enable the Storyboard REST API.

[its-connection]: #its-connection
<a name="its-connection">Connection Configuration</a>
-----------------------------------------------------

In order for @PLUGIN@ to connect to the REST service of your
Storyboard instance, the url and credentials are required in
your site's `etc/gerrit.config` or `etc/secure.config` under
the `@PLUGIN@` section.

Example:

```
[@PLUGIN@]
  url=https://my_storyboard_instance.com
  username=USERNAME_TO_CONNECT_TO_STORYBOARD
  token=AUTH_TOKEN_FOR_ABOVE_USERNAME
```

[its-associate-change]: #its-associate-change
<a name="its-associate-change">Associating Gerrit Changes</a>
-------------------------------------------------------------

In order for @PLUGIN@ to associate a Gerrit change with
a Storyboard story, a Gerrit commentlink needs to be
defined in gerrit.config

Example:

```
[commentLink "@PLUGIN@"]
  match = [Ss][Tt][Oo][Rr][Yy][ ]*([1-9][0-9]*)
  html = "<a href=\"https://my_storyboard_instance.com/#!/story/$1\">story $1</a>"
```

[its-actions]: #its-actions
<a name="its-actions">Configure its actions</a>
-----------------------------------------------

The @PLUGIN@ plugin can take action when there are updates
to Gerrit changes.  Users can define what events will trigger
which actions.  To configure this a $SITE/etc/its/actions.config
file is required.

Example of actions.config:

```
# Add a custom comment when a comment has been added to the associated Gerrit change.
[rule "update-comment"]
    event-type = comment-added
    action = add-velocity-comment inline $commenter-name commented on change ${its.formatLink($change-url, $subject)}
# add a comment only when a user leaves a -2 or a -1 vote on the Code-Review label on the associated Gerrit change.
[rule "comment-on-negative-vote"]
    event-type = comment-added
    approval-Code-Review = -2,-1
    action = add-comment Boo-hoo, go away!
# add a standard comment when there is a status update to the associated Gerrit change.
[rule "comment-on-status-update"]
    event-type = patchset-created,change-abandoned,change-restored,change-merged
    action = add-standard-comment
```

[its-enable]: #its-enable
<a name="its-enable">Enable the Plugin</a>
-------------------------------------------------------

In order to enable the @PLUGIN@ plugin, an entry must be
added to project.config.  To enable the plugin for all
projects a single entry can be added to project.config in
All-Projects.

Example:

```
[plugin "@PLUGIN@"]
  enabled = true
```

Note - The branch limiting feature for branches is broken.
A pending [`change`][change-60940] to fix it is in review.


[change-60940]: https://gerrit-review.googlesource.com/#/c/60940
[its-base]: https://gerrit-review.googlesource.com/#/admin/projects/plugins/its-base

[Back to @PLUGIN@ documentation index][index]
[index]: index.html
