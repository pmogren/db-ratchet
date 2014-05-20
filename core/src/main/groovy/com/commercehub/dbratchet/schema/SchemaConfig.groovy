package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.filestore.FileStore

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

    FileStore fileStore
    List<Version> versions

    SchemaConfig(FileStore fileStore) {
        this.fileStore = fileStore
        versions = scanSchemaRootForPublishedVersions()
    }

    File getRootDir() {
        fileStore.getFile('.')
    }

    String getSchemaURLAsString() {
        return "${fileStore.fileStoreRootURLAsString}${Version.VERSIONS_DIR}"
    }

    private List<Version> scanSchemaRootForPublishedVersions() {
        List<Version> returnList = [] as Queue<Version>
        fileStore.scanRecursivelyForFiles(Version.VERSIONS_DIR,
                "${FLYWAY_MIGRATION_PREFIX}*${FLYWAY_MIGRATION_SUFFIX}").each { filename->
            def versionString = filename[1..(filename.indexOf(FLYWAY_MIGRATION_SUFFIX) - 1)]
            returnList.add(new Version(versionString))
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
