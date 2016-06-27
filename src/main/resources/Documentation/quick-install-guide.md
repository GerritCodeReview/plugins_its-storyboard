Quick Install Guide
===================

For general instructions on how to enable and configure an its plugin
please refer to the general [configuration documentation][config-doc]
Instructions in this document are specific to the @PLUGIN@ plugin.

Install Steps:

1. Verify Storyboard [REST endpoint][rest-enabled].
2. [Configure the connection][its-connection].
3. [Associate a changes with stories][its-associate-change].
4. [Configure the actions][its-actions] that the plugin will take on a Gerrit change.
5. [Enable the @PLUGIN@ plugin][its-enable] for the Gerrit project.
6. [Install the plugin][its-install]
7. Restart Gerrit

[rest-enabled]: #rest-enabled
<a name="rest-enabled">Checking REST API availability</a>
---------------------------------------------------------

This plugin will connect to Storyboard via it's REST endpoints.
Make sure that the Storyboard REST API is up and running.

Assuming the Storyboard instance you want to connect to is at
`http://my_storyboard_instance.com/`, open

```
http://my_storyboard_instance.com/api/v1/systeminfo
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
  password=AUTH_TOKEN_FOR_ABOVE_USERNAME
```

[its-associate-change]: #its-associate-change
<a name="its-associate-change">Associating Gerrit Changes</a>
-------------------------------------------------------------

In order for @PLUGIN@ to associate a Gerrit change with
a Storyboard story, a Gerrit commentlink needs to be
defined in `etc/gerrit.config`

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
which actions.  To configure this a `etc/its/actions.config`
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
# set task status to 'review' when a patch is uploaded or when a change is restored
[rule "change_restored"]
    event-type = patchset-created,change-restored
    action = set-status REVIEW
# add the gerrit change url to the task notes when a patch is uploaded
[rule "update_task_notes"]
    event-type = patchset-created
    action = set-notes $change-url    
```

More detailed information on actions is found the [rules documentation][rules-doc]

[its-enable]: #its-enable
<a name="its-enable">Enable the Plugin</a>
-------------------------------------------------------

In order to enable the @PLUGIN@ plugin, an entry must be
added to the project.config file in refs/meta/config.
To enable the plugin for all projects a single entry can
be added to project.config in All-Projects.

Example:

```
[plugin "@PLUGIN@"]
  enabled = true
```

[its-install]: #its-install
<a name="its-install">Install the Plugin</a>
-------------------------------------------------------

In order to install the @PLUGIN@ plugin simply copy the built jar
file into the `plugins` folder.

[config-common-doc]: config-common.html
[config-doc]: config.html
[rules-doc]: config-rulebase-common.html
