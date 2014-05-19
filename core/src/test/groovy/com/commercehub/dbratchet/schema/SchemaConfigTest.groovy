package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.filestore.ClasspathFileStore
import com.commercehub.dbratchet.filestore.FileStore

/**
 * Created by jgelais on 5/19/2014.
 */
class SchemaConfigTest extends GroovyTestCase {
    SchemaConfig schemaConfig

    @Override
    void setUp() {
        super.setUp()
        schemaConfig = new SchemaConfig(new MockFileStore())
    }

    @Override
    void tearDown() {
        super.tearDown()
        schemaConfig = null
    }

    void testGetRootDir() {
        assert schemaConfig.rootDir == new File('.')
    }

    void testGetSchemaURLAsString() {
        assert schemaConfig.schemaURLAsString == "classpath:${MockFileStore.CLASS_PATH_ROOT}${Version.VERSIONS_DIR}"
    }

    void testGetVersion() {
        assert schemaConfig.version == new Version('0.2.0')
    }

    void testGetPreviousVersion() {
        assert schemaConfig.previousVersion == new Version('0.1.1')
    }

    void testGetNextPointVersion() {
        assert schemaConfig.nextPointVersion == new Version('0.2.1')
    }

    void testGetNextMinorVersion() {
        assert schemaConfig.nextMinorVersion == new Version('0.3.0')
    }

    void testGetNextMajorVersion() {
        assert schemaConfig.nextMajorVersion == new Version('1.0.0')
    }

    void testGetFileStore() {
        assert schemaConfig.fileStore instanceof MockFileStore
    }

    void testSetFileStore() {
        schemaConfig.setFileStore(new ClasspathFileStore())
        assert schemaConfig.fileStore instanceof ClasspathFileStore
    }

    void testGetVersions() {
        assert schemaConfig.versions.size() == 3
        assert schemaConfig.versions.contains(new Version('0.1.0'))
        assert schemaConfig.versions.contains(new Version('0.1.1'))
        assert schemaConfig.versions.contains(new Version('0.2.0'))
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

    static class MockFileStore implements FileStore {
        public static final String CLASS_PATH_ROOT = '/com/commercehub/dbratchet/schema/schemaconfigtest/'

        @Override
        InputStream getFileInputStream(String path) {
            return MockFileStore.getResourceAsStream("${CLASS_PATH_ROOT}$path")
        }

        @Override
        OutputStream getFileOutputStream(String path) {
            return getFile(path).newOutputStream()
        }

        @Override
        URL getFileAsResource(String path) {
            return MockFileStore.getResource("${CLASS_PATH_ROOT}$path")
        }

        @Override
        File getFile(String path) {
            if (path == '.') {
                return new File('.')
            }
            return new File(getFileAsResource(path).toURI())
        }

        @Override
        List<String> scanRecursivelyForFiles(String path, String filePattern) {
            return ['V000.0001.000000__upgrade.sql', 'V000.0001.000001__upgrade.sql', 'V000.0002.000000__upgrade.sql']
        }

        @Override
        String getFileStoreRootURLAsString() {
            return "classpath:${CLASS_PATH_ROOT}"
        }
    }
}
