package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.filestore.ClasspathFileStore
import com.commercehub.dbratchet.filestore.FileStore

/**
 * Created by jgelais on 5/19/2014.
 */
class SchemaConfigTest extends GroovyTestCase {
    public static final String CLASS_PATH_ROOT = '/com/commercehub/dbratchet/sampleschema/'

    SchemaConfig schemaConfig

    @Override
    void setUp() {
        super.setUp()
        schemaConfig = new SchemaConfig(new ClasspathFileStore(CLASS_PATH_ROOT))
    }

    @Override
    void tearDown() {
        super.tearDown()
        schemaConfig = null
    }

    void testGetSchemaURLAsString() {
        assert schemaConfig.schemaURLAsString == "classpath:${CLASS_PATH_ROOT}${Version.VERSIONS_DIR}"
    }

    void testGetVersion() {
        assert schemaConfig.version == new Version('0.1.1')
    }

    void testGetPreviousVersion() {
        assert schemaConfig.previousVersion == new Version('0.1.0')
    }

    void testGetNextPointVersion() {
        assert schemaConfig.nextPointVersion == new Version('0.1.2')
    }

    void testGetNextMinorVersion() {
        assert schemaConfig.nextMinorVersion == new Version('0.2.0')
    }

    void testGetNextMajorVersion() {
        assert schemaConfig.nextMajorVersion == new Version('1.0.0')
    }

    void testGetFileStore() {
        assert schemaConfig.fileStore instanceof ClasspathFileStore
    }

    void testGetVersions() {
        assert schemaConfig.versions.size() == 2
        assert schemaConfig.versions.contains(new Version('0.1.0'))
        assert schemaConfig.versions.contains(new Version('0.1.1'))
    }

    void testSetVersions() {
        schemaConfig.setVersions([new Version('1.2.3')])
        assert schemaConfig.versions.size() == 1
        assert schemaConfig.versions.contains(new Version('1.2.3'))
    }

    void testDefaultVersionSelection() {
        schemaConfig.setVersions([])
        assert schemaConfig.version == new Version('0.0.0')
    }

    void testBehaviourWhenThereIsNoPreviousVersion() {
        schemaConfig.setVersions([new Version('0.0.1')])
        assert schemaConfig.previousVersion == null
    }
}
