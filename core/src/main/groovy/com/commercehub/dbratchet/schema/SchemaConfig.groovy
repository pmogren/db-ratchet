package com.commercehub.dbratchet.schema

import com.googlecode.flyway.core.util.Resource
import com.googlecode.flyway.core.util.scanner.classpath.ClassPathScanner

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 10:48 AM
 */
class SchemaConfig {
    public static final String DATA_DIR = 'data'
    public static final String FLYWAY_MIGRATION_SUFFIX = '__upgrade.sql'
    public static final String FLYWAY_MIGRATION_PREFIX = 'V'

    URI schemaRoot
    List<Version> versions

    SchemaConfig() {
        this(new File('.').toURI())
    }

    SchemaConfig(File dir) {
        this(dir.toURI())
    }

    SchemaConfig(URI uri) {
        schemaRoot = uri
        versions = scanSchemaRootForPublishedVersions()
    }

    File getRootDir() {
        if (isFileURI()) {
            return new File(schemaRoot)
        }

        throw new IllegalStateException('Cannot get schema root as a file when schema is not rooted on '
                + "the filesystem. SchemaRoot: ${schemaRoot}")
    }

    boolean isFileURI() {
        'file'.equalsIgnoreCase(schemaRoot.scheme)
    }

    private List<Version> scanSchemaRootForPublishedVersions() {
        if (isFileURI()) {
            return scanFileSystemForPublishedForVersions(new File(schemaRoot))
        }

        return scanClasspathForPublishedForVersions()
    }

    private List<Version> scanClasspathForPublishedForVersions() {
        def versions = [] as Queue<com.commercehub.dbratchet.schema.Version>

        new ClassPathScanner().scanForResources("${Version.VERSIONS_DIR}",
                FLYWAY_MIGRATION_PREFIX, FLYWAY_MIGRATION_SUFFIX).each { Resource resource->
            def versionString = resource.filename[1..(resource.filename.indexOf(FLYWAY_MIGRATION_SUFFIX) - 1)]
            versions.add(new Version(versionString))
        }

        return versions.sort()
    }

    private List<Version> scanFileSystemForPublishedForVersions(File rootDir) {
        List<Version> returnList = [] as Queue<String>
        File versionsDir = new File(rootDir, Version.VERSIONS_DIR)
        if (versionsDir.exists() && versionsDir.isDirectory()) {
            versionsDir.eachDir { dir->
                returnList.add(new Version(dir.name))
            }
        }

        return returnList.sort()
    }

    Version getVersion() {
        return (versions.isEmpty()) ? new Version(0,0,0) : versions.last()
    }

    /*
     * Suppressing this CodeNarc check here. 2 isn't so much a magic number as it is the minimum collection
     * size for there to be a next to last element in a list.
     */
    @SuppressWarnings('DuplicateNumberLiteral')
    Version getPreviousVersion() {
        if (versions.size() < 2) {
            return null
        }

        return versions.get(versions.size() - 2)
    }

    Version getNextPointVersion() {
        Version nextVersion = new Version(version)
        nextVersion.incrementPointVersion()
        versions.add(nextVersion)

        return nextVersion
    }

    Version getNextMinorVersion() {
        Version nextVersion = new Version(version)
        nextVersion.incrementMinorVersion()
        versions.add(nextVersion)

        return nextVersion
    }

    Version getNextMajorVersion() {
        Version nextVersion = new Version(version)
        nextVersion.incrementMajorVersion()
        versions.add(nextVersion)

        return nextVersion
    }
}
