package com.olafmertens.gradle.gitversion

import org.gradle.api.Project
import org.gradle.api.provider.Property

class GitVersionPluginExtension {

    public static final String EXTENSION_NAME = "gitVersion"

    private static final def DEFAULT_VERSION_TAG_PATTERN = '(\\d+\\.\\d+\\.\\d+)'
    private static final def DEFAULT_VERSION_TAG_PATTERN_GROUP = 1
    private static final def DEFAULT_VERSION_TAG_PREFIX = ''
    private static final def DEFAULT_VERSION_TAG_SUFFIX = ''
    private static final def DEFAULT_VERSION_TAG_REQUIRED = false

    private final Property<String> versionTagPattern
    private final Property<Integer> versionTagPatternGroup
    private final Property<String> versionTagPrefix
    private final Property<String> versionTagSuffix
    private final Property<Boolean> versionTagRequired

    GitVersionPluginExtension(Project project) {

        versionTagPattern = project.getObjects().property(String.class)
        versionTagPatternGroup = project.getObjects().property(Integer.class)
        versionTagPrefix = project.getObjects().property(String.class)
        versionTagSuffix = project.getObjects().property(String.class)
        versionTagRequired = project.getObjects().property(Boolean.class)

        versionTagPattern.set(DEFAULT_VERSION_TAG_PATTERN)
        versionTagPatternGroup.set(DEFAULT_VERSION_TAG_PATTERN_GROUP)
        versionTagPrefix.set(DEFAULT_VERSION_TAG_PREFIX)
        versionTagSuffix.set(DEFAULT_VERSION_TAG_SUFFIX)
        versionTagRequired.set(DEFAULT_VERSION_TAG_REQUIRED)
    }

    Property<String> getVersionTagPattern() {
        return versionTagPattern
    }

    Property<Integer> getVersionTagPatternGroup() {
        return versionTagPatternGroup
    }

    Property<String> getVersionTagPrefix() {
        return versionTagPrefix
    }

    Property<String> getVersionTagSuffix() {
        return versionTagSuffix
    }

    Property<Boolean> getVersionTagRequired() {
        return versionTagRequired
    }
}
