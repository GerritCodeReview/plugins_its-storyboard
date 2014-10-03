Configuring Storyboard
======================

For instructions on how to enable and configure the plugin please refer to the
its-base configuration documentation.  All the instructions neccessary to
configure the this plugin is provided in that document.


Storyboard connectivity
=======================

In order for Gerrit to connect to Storyboard,

1. [make sure that your Storyboard instance has the REST endpoint
   enabled][rest-enabled], and
2. [provide url, user and password to @PLUGIN@][gerrit-configuration].


[rest-enabled]: #rest-enabled
<a name="rest-enabled">Checking REST API availability</a>
-------------------------------------------------------

Assuming the Storyboard instance you want to connect to is at
`http://my.storyboard.instance.example.org/`, open

```
http://my.storyboard.instance.example.org/api/v1/stories
```

in your browser. If you get an response empty page without errors, the REST
interface is enabled. You can continue by [providing the needed Gerrit
configuration][gerrit-configuration].

If you get an error page then you'll need to request your Storyboard admin
to enable the REST API.

[gerrit-configuration]: #gerrit-configuration
<a name="gerrit-configuration">Gerrit configuration</a>
-------------------------------------------------------

In order for @PLUGIN@ to connect to the REST service of your
Storyboard instance, the url and credentials are required in
your site's `etc/gerrit.config` or `etc/secure.config` under
the `@PLUGIN@` section.

Example:

```
[@PLUGIN@]
  url=http://my.storyboard.instance.example.org
  username=USERNAME_TO_CONNECT_TO_STORYBOARD
  password=PASSWORD_FOR_ABOVE_USERNAME
```

[Back to @PLUGIN@ documentation index][index]

[index]: index.html
