Quick Install Guide
===================

For general instructions on how to enable and configure an its plugin
please refer to the general [configuration documentation][config-doc].
Instructions in this document are specific to the @PLUGIN@ plugin.

Install Steps:

1. [Check Storyboard REST API availability][rest-enabled]
2. [Connection Configuration][its-connection].
3. [Verify access to Storyboard][access-enabled]
4. [Associate Gerrit changes with Storyboard stories and tasks][its-associate-change].
5. [Configure the actions][its-actions] that the plugin will take on a Gerrit change update.
6. [Install the plugin][its-install]
7. [Enable the @PLUGIN@ plugin][its-enable] for the Gerrit project.
8. [Testing][testing]

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

In order for the @PLUGIN@ plugin to connect to the REST service of your
Storyboard instance, the url and credentials are required in
your Gerrit site's `etc/gerrit.config` or `etc/secure.config` under
the `@PLUGIN@` section.

Example:

```
[@PLUGIN@]
  url=https://my_storyboard_instance.com
  password=STORYBOARD_USER_AUTH_TOKEN
```

[access-enabled]: #access-enabled
<a name="access-enabled">Check Accessibility</a>
---------------------------------------------------------

This plugin uses the Storyboard REST endpoints to POST updates.  Make sure
that the STORYBOARD_USER_AUTH_TOKEN has access to update Storyboard stories
and tasks. To verify this use the [Storyboard story API] from the Gerrit sever
to post an update. If it fails to update the story you'll need to make the
necessary changes to allow access between the Gerrit and Storyboard
servers.


[its-associate-change]: #its-associate-change
<a name="its-associate-change">Associating Gerrit Changes</a>
-------------------------------------------------------------

In order for the @PLUGIN@ plugin to associate a Gerrit change with
a Storyboard story and task, a Gerrit commentlink needs to be
defined in `etc/gerrit.config`

Example:

```
[commentlink "story"]
    match = "\\b[Ss]tory:? #?(\\d+)"
    link = "http://my_storyboard_instance.com/#!/story/$1"
    html = ""
[commentLink "@PLUGIN@"]
    match = "\\b[Tt]ask:? #?(\\d+)"
    link = "task: $1"
    html = ""
```

[its-actions]: #its-actions
<a name="its-actions">Configure its actions</a>
-----------------------------------------------

The @PLUGIN@ plugin can take actions when there are updates
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

# set storyboard task status to 'review' when a patch is uploaded or when a change is restored
[rule "change-in-progress"]
    event-type = patchset-created,change-restored
    action = set-status REVIEW
```

*_NOTE_: A Gerrit restart is required to update these settings.

### <a id="task-status"></a>TaskStatus
Valid task status: TODO, REVIEW, INPROGRESS, MERGED, and INVALID

[its-install]: #its-install
<a name="its-install">Install the Plugin</a>
-------------------------------------------------------

In order to install the @PLUGIN@ plugin simply copy the built jar
file into the `plugins` folder.

[config-common-doc]: config-common.html
[config-doc]: config.html
[rules-doc]: config-rulebase-common.html

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

[testing]: #testing
<a name="testing">Testing the Plugin</a>
-------------------------------------------------------

Create a new Gerrit change with a commit message that contains a reference
to the Storyboard story and task.

Example:

```
My change to test integration

This is an example change to test storyboard integration.
Story: 123
Task: 1000
Change-Id: I3912f42c371023eb8bd048a5b17b776801b405e2
```

Make an update to the Gerrit change (abandone, restore, submit, etc..),
the @PLUGIN@ plugin should automatically update the corresponding story
and task in Storyboard.

SEE ALSO
--------
* More detailed information on actions is found in the [rules documentation][rules-doc]


[Storyboard story API]: http://docs.openstack.org/infra/storyboard/webapi/v1.html#put--v1-stories
