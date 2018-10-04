package com.olafmertens.gradle.gitversion

import org.gradle.api.GradleException
import org.gradle.api.Project

import static com.olafmertens.gradle.gitversion.GitVersionPluginExtension.EXTENSION_NAME

class ProjectProperties {

    public static final String VERSION_TAG_PATTERN = EXTENSION_NAME + ".versionTagPattern"
    public static final String VERSION_TAG_PREFIX = EXTENSION_NAME + ".versionTagPrefix"
    public static final String VERSION_TAG_SUFFIX = EXTENSION_NAME + ".versionTagSuffix"
    public static final String VERSION_TAG_REQUIRED = EXTENSION_NAME + ".versionTagRequired"
    public static final String VERSION_TAG_PATTERN_GROUP = EXTENSION_NAME + ".versionTagPatternGroup"

    private static final STRING_CONVERTER = { String s -> s }
    private static final INTEGER_CONVERTER = { String s -> Integer.valueOf(s) }
    private static final BOOLEAN_CONVERTER = { String s -> Boolean.valueOf(s) }

    private String versionTagPattern
    private String versionTagPrefix
    private String versionTagSuffix
    private Boolean versionTagRequired
    private Integer versionTagPatternGroup

    ProjectProperties(Project project) {
        versionTagPattern = readProjectProperty(project, VERSION_TAG_PATTERN, String.class)
        versionTagPrefix = readProjectProperty(project, VERSION_TAG_PREFIX, String.class)
        versionTagSuffix = readProjectProperty(project, VERSION_TAG_SUFFIX, String.class)
        versionTagRequired = readProjectProperty(project, VERSION_TAG_REQUIRED, Boolean.class)
        versionTagPatternGroup = readProjectProperty(project, VERSION_TAG_PATTERN_GROUP, Integer.class)
    }

    private <T> T readProjectProperty(Project project, String propertyName, Class<T> type) {

        if (project.hasProperty(propertyName)) {
            String value = project.property(propertyName)
            try {
                return getValueConverter(type)(value)
            } catch (Exception ex) {
                throw new GradleException("Invalid value for project property '$propertyName'!" +
                        " Value '$value' can't be converted to $type.")
            }
        } else {
            return null
        }
    }

    private <T> Closure<T> getValueConverter(Class<T> type) {
        if (Integer.class == type) {
            return INTEGER_CONVERTER
        } else if (Boolean.class == type) {
            return BOOLEAN_CONVERTER
        } else {
            return STRING_CONVERTER
        }
    }

    Optional<String> getVersionTagPattern() {
        return Optional.ofNullable(versionTagPattern)
    }

    Optional<String> getVersionTagPrefix() {
        return Optional.ofNullable(versionTagPrefix)
    }

    Optional<String> getVersionTagSuffix() {
        return Optional.ofNullable(versionTagSuffix)
    }

    Optional<Boolean> getVersionTagRequired() {
        return Optional.ofNullable(versionTagRequired)
    }

    Optional<Integer> getVersionTagPatternGroup() {
        return Optional.ofNullable(versionTagPatternGroup)
    }
}
