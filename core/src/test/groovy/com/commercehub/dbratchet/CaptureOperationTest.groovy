package com.commercehub.dbratchet

import com.commercehub.dbratchet.filestore.ClasspathFileStore
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.filestore.MockInMemoryFileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.util.GroovySqlRunner

/**
 * Created by jaystgelais on 5/19/14.
 */
class CaptureOperationTest extends GroovyTestCase {

    void testDefaultPath() {
        DatabaseConfig databaseConfig = new MockDatabaseConfig().setDatabase('testDefaultPath')
        FileStore fileStore = new MockInMemoryFileStore()
        setupDatabaseSchema(databaseConfig)
        setupSampleData(databaseConfig)
        storeSampleDataConfig(fileStore)

        CaptureOperation captureOp = new CaptureOperation(databaseConfig, fileStore)
        assert captureOp.run()

        // TODO write some xpath assertions
        println fileStore.getFileInputStream('data/packages/dbo.Courses.xml').text
    }

    private void storeSampleDataConfig(MockInMemoryFileStore mockInMemoryFileStore) {
        String xml = getClass().getResourceAsStream('CaptureOperationTest-data-packages.xml').text
        mockInMemoryFileStore.getFileOutputStream('data/data-packages.xml').write(xml.bytes)
    }

    private void setupSampleData(DatabaseConfig databaseConfig) {
        def sqlRunner = new GroovySqlRunner()
        assert sqlRunner.runCommand(databaseConfig, 'insert into dbo.Courses values (1, \'Biology 101\', 2013, \'F\')')
        assert sqlRunner.runCommand(databaseConfig, 'insert into dbo.Courses values (2, \'Biology 101\', 2014, \'S\')')
        assert sqlRunner.runCommand(databaseConfig,
                'insert into dbo.Courses values (3, \'Chemistry 101\', 2013, \'F\')')
    }

    private void setupDatabaseSchema(DatabaseConfig databaseConfig) {
        BuildOperation buildOp = new BuildOperation(databaseConfig, null,
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()
    }
}
