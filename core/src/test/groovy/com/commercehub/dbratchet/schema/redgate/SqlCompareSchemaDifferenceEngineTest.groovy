package com.commercehub.dbratchet.schema.redgate

import com.commercehub.dbratchet.InitOperation
import com.commercehub.dbratchet.Operation
import com.commercehub.dbratchet.TestConfig
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.filestore.FileSystemFileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.Test

/**
 * Created by jgelais on 5/22/2014.
 */
class SqlCompareSchemaDifferenceEngineTest {

    @Rule
    @SuppressWarnings('PublicInstanceField')
    @SuppressWarnings('NonFinalPublicField')
    public TemporaryFolder folder = new TemporaryFolder()

    @Before
    void setup() {
        assumptions()
    }

    private void assumptions() {
        Assume.assumeTrue(TestConfig.isRedgateAvailable)
        Assume.assumeTrue(TestConfig.isMSSQLAvailable)
    }

    @Test
    void testRedgateSqlCompareInit() {
        FileStore fileStore = new FileSystemFileStore(folder.newFolder())
        Operation initOp = new InitOperation(new SchemaConfig(fileStore),
                SqlCompareSchemaDifferenceEngine.ENGINE_NAME)
        assert initOp.configured
        assert initOp.run()

        assert fileStore.getFile('redgate-schema').exists()
        assert fileStore.getFile('redgate-schema').isDirectory()
        assert fileStore.getFile('redgate-config').exists()
        assert fileStore.getFile('redgate-config').isDirectory()
        assert fileStore.getFile('redgate-config/filter.scpf').exists()
        assert fileStore.getFile('redgate-config/filter.scpf').isFile()
        assert fileStore.getFile('redgate-config/file-filter-config.groovy').exists()
        assert fileStore.getFile('redgate-config/file-filter-config.groovy').isFile()
        assert fileStore.getFile(Version.VERSIONS_DIR).exists()
        assert fileStore.getFile(Version.VERSIONS_DIR).isDirectory()
        assert fileStore.getFile(SchemaConfig.DATA_DIR).exists()
        assert fileStore.getFile(SchemaConfig.DATA_DIR).isDirectory()
        assert fileStore.getFile(SchemaConfig.SCRIPTS_DIR).exists()
        assert fileStore.getFile(SchemaConfig.SCRIPTS_DIR).isDirectory()
    }

}
