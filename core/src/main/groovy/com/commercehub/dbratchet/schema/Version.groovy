package com.commercehub.dbratchet.schema

import com.google.common.base.Objects
import com.google.common.collect.ComparisonChain

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 10:55 AM
 */
@SuppressWarnings('DuplicateStringLiteral')
@SuppressWarnings('DuplicateNumberLiteral')
final class Version implements Comparable<Version> {
    public static final String VERSIONS_DIR = 'versions'
    int majorVersion
    int minorVersion
    int pointVersion

    Version(int major, int minor, int point) {
        assignVersions(major, minor, point)
    }

    Version(Version versionToCopy) {
        this(versionToCopy.majorVersion, versionToCopy.minorVersion, versionToCopy.pointVersion)
    }

    Version(String versionString) {
        def versionParts = versionString.split('\\.')
        assignVersions(versionParts[0].toInteger(), versionParts[1].toInteger(), versionParts[2].toInteger())
    }

    private void assignVersions(int major, int minor, int point) {
        majorVersion = major
        minorVersion = minor
        pointVersion = point
    }

    String toString() {
        return "${pad(majorVersion, 3)}.${pad(minorVersion, 4)}.${pad(pointVersion, 6)}"
    }

    void incrementPointVersion() {
        pointVersion++
    }

    void incrementMinorVersion() {
        minorVersion++
        pointVersion = 0
    }

    void incrementMajorVersion() {
        majorVersion++
        minorVersion = 0
        pointVersion = 0
    }

    File getFullBuildScriptFile() {
        return new File(versionDir, fullBuildScriptName)
    }

    File getUpgradeScriptFile() {
        return new File(versionDir, upgradeScriptName)
    }

    File getRollbackScriptFile() {
        return new File(versionDir, rollbackScriptName)
    }

    String getFullBuildScriptName() {
        return "V${toString()}__fullbuild.sql"
    }

    String getUpgradeScriptName() {
        return "V${toString()}__upgrade.sql"
    }

    String getRollbackScriptName() {
        return "V${toString()}__rollback.sql"
    }

    // TODO Fix this so that it uses FileStore
    File getVersionDir() {
        File versionDir = new File( versionsDir, "${toString()}")
        versionDir.mkdirs()

        return versionDir
    }

    File getVersionsDir() {
        File versionsDir = new File(VERSIONS_DIR)
        versionsDir.mkdirs()

        return versionsDir
    }

    private String pad(int number, int width) {
        return "${number}".padLeft(width, '0')
    }

    boolean equals(o) {
        if (this.is(o)) {
            return true
        }
        if (!(o instanceof Version)) {
            return false
        }

        Version that = (Version) o
        return Objects.equal(majorVersion, that.majorVersion) &&
               Objects.equal(minorVersion, that.minorVersion) &&
               Objects.equal(pointVersion, that.pointVersion)
    }

    int hashCode() {
        return Objects.hashCode(majorVersion, minorVersion, pointVersion)
    }

    int compareTo(Version that) {
        return ComparisonChain.start()
                .compare(this.majorVersion, that.majorVersion)
                .compare(this.minorVersion, that.minorVersion)
                .compare(this.pointVersion, that.pointVersion)
                .result()
    }
}