package com.commercehub.dbratchet.schema

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 10:48 AM
 */
class SchemaConfig {
    public static final String SCHEMA_DIR = './schema'
    public static final String REDGATE_CONFIG_DIR = 'redgate-config'
    public static final String DATA_DIR = 'data'

    File rootDir
    List<Version> versions

    SchemaConfig() {
        this(new File('.'))
    }

    SchemaConfig(File dir) {
        rootDir = dir
        versions = scanFileSystemPublishedVersionDirs()
    }

    private List<Version> scanFileSystemPublishedVersionDirs() {
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
