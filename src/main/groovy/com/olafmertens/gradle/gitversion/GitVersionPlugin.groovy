package com.olafmertens.gradle.gitversion

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.util.GradleVersion
import org.slf4j.LoggerFactory

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class GitVersionPlugin implements Plugin<Project> {

    private static final Logger log = LoggerFactory.getLogger(GitVersionPlugin.class)

    private static final String MINIMAL_GRADLE_VERSION = '4.3'
    private static final String LOG_PREFIX = 'git-version: '
    private static final String GROUP = 'Git Version'

    private ProjectProperties projectProperties
    private GitVersionPluginExtension extension

    GitVersionPlugin() {
        if (GradleVersion.current() < GradleVersion.version(MINIMAL_GRADLE_VERSION)) {
            throw new GradleException("The git-version plugin requires gradle $MINIMAL_GRADLE_VERSION or higher!")
        }
    }

    @Override
    void apply(Project project) {

        projectProperties = new ProjectProperties(project)
        extension = project.getExtensions().create(GitVersionPluginExtension.EXTENSION_NAME,
                GitVersionPluginExtension.class, project)

        project.afterEvaluate {

            def pattern = createPattern()
            def branch = gitBranch()
            def commitHash = gitCommit()

            if (commitHash.isEmpty()) {
                log.warn('{}Cannot set project version because there is no git commit yet!', LOG_PREFIX)
            } else {
                def version = versionByGitTag(commitHash, pattern, getVersionTagPatternGroup())

                if (version != null) {
                    project.version = version

                } else if (isVersionTagRequired()) {
                    throw new GradleException("No valid git version tag found! " +
                            "Set '$ProjectProperties.VERSION_TAG_REQUIRED=false' to disable this check.")

                } else {
                    project.version = branch.isEmpty() ? commitHash : branch + '.' + commitHash
                }

                project.ext.gitHash = commitHash
                project.ext.gitBranch = branch

                log.info('{}=> {}', LOG_PREFIX, project.version)
            }
        }

        project.task('projectVersion') {
            description = 'Displays the project version.'
            group = GROUP

            doLast {
                println project.version
            }
        }
    }

    private Pattern createPattern() {
        String patternString = getVersionTagPattern()
        try {
            return ~(quote(getVersionTagPrefix()) + patternString + quote(getVersionTagSuffix()))
        } catch (PatternSyntaxException ex) {
            throw new GradleException("Invalid regex pattern in property $ProjectProperties.VERSION_TAG_PATTERN: '$patternString'!")
        }
    }

    private String getVersionTagPattern() {
        return projectProperties.getVersionTagPattern().orElseGet({ extension.getVersionTagPattern().get() })
    }

    private String getVersionTagPrefix() {
        return projectProperties.getVersionTagPrefix().orElseGet({ extension.getVersionTagPrefix().get() })
    }

    private String getVersionTagSuffix() {
        return projectProperties.getVersionTagSuffix().orElseGet({ extension.getVersionTagSuffix().get() })
    }

    private int getVersionTagPatternGroup() {
        return projectProperties.getVersionTagPatternGroup().orElseGet({ extension.getVersionTagPatternGroup().get() })
    }

    private boolean isVersionTagRequired() {
        return projectProperties.getVersionTagRequired().orElseGet({ extension.getVersionTagRequired().get() })
    }

    String quote(String s) {
        if (s == null || s.isEmpty()) {
            return ''
        } else {
            return Pattern.quote(s)
        }
    }

    String versionByGitTag(String commitHash, Pattern pattern, int patternGroup) {

        List<String> gitTags = gitTags(commitHash)

        for (String gitTag : gitTags) {
            def version = parseVersionFromTag(gitTag, pattern, patternGroup)
            if (version != null) {
                return version
            }
        }
        return null
    }

    String parseVersionFromTag(String gitTag, Pattern pattern, int patternGroup) {
        def matcher = pattern.matcher(gitTag)
        if (matcher.matches()) {
            try {
                String version = matcher.group(patternGroup)
                log.info("{}Git tag '{}' matches. (pattern: '{}', group: {})",
                        LOG_PREFIX, gitTag, pattern, patternGroup)
                return version
            } catch (IndexOutOfBoundsException ex) {
                throw new GradleException(LOG_PREFIX +
                        "Cannot parse project version from git tag '$gitTag' with regular expression '$pattern': "
                        + ex.getMessage())
            }
        }
        log.info("{}Git tag '{}' doesn't match. (pattern: '{}', group: {})",
                LOG_PREFIX, gitTag, pattern, patternGroup)
        return null
    }

    String gitBranch() {
        return 'git symbolic-ref --short HEAD'.execute().text.trim()
    }

    String gitCommit() {
        return 'git log --pretty=format:%h -1'.execute().text.trim()
    }

    List<String> gitTags(String commitHash) {
        return ('git tag --contains ' + commitHash).execute().text.readLines()
    }
}
