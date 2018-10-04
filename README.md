gradle-git-version
==================

This plugin automatically sets the gradle project version based on a git tag.

It uses a regular expression to parse the version string from the git tag.

If it cannot find a matching git tag, the version will be set to `<branch name>.<git hash>`.

By default a valid git version tag is expected to look like `1.10.1`, but you can change that in the settings.  


Usage
-----

Add the plugin the usual way:
```groovy
plugins {
    id 'com.olafmertens.git-version' version '0.1.1'
}
```

Don't set the version in the build script! It is set automatically.

Settings
--------

You can change the default behaviour by adding the following code to your build script:

```groovy
gitVersion {
    versionTagPrefix = 'v'
}
```

You can also set the properties via the command line as project properties.
Project properties will override the properties in the build script. 

```bash
./gradlew -PgitVersion.versionTagPrefix=v
```

The following properties are available.

Name | Type | Default value | Description
--- | --- | --- | ---
versionTagPattern | String | `'(\d+\.\d+\.\d+)'` | The regular expression pattern that a version tag has to match.
versionTagPatternGroup | Integer | `1` | The pattern match group to get the version.
versionTagPrefix | String | | Version tag prefix. The prefix will not be part of the parsed version string. It is not interpreted as a regular expression!
versionTagSuffix | String | | Version tag suffix. The suffix will not be part of the parsed version string. It is not interpreted as a regular expression!
versionTagRequired | Boolean | `false` | If `true`, the build will fail when no matching version tag has been found. 

Tasks
-----

The task `projectVersion` will display the parsed project version. 

Other goodies
-------------

The plugin also sets the properties `project.gitHash` and `project.gitBranch`.

FAQ
---

### My git repository contains multiple independent projects that are supposed to have different version numbers. What can I do?

You can set different version tag prefixes for each project:
```groovy
gitVersion {
    versionTagPrefix = 'project1_'
}
```

### My git version tag is ignored!

Run the build with the gradle parameter `--info` to see some parsing details. 