package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseVendor
import com.commercehub.dbratchet.databases.derby.DerbySchemaInformationService
import com.commercehub.dbratchet.filestore.ClasspathFileStore
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.filestore.MockInMemoryFileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.util.SqlScriptRunner

/**
 * Created by jaystgelais on 5/19/14.
 */
class CaptureOperationTest extends GroovyTestCase {

    void testDefaultPath() {
        DatabaseConfig databaseConfig = getDatabaseConfig('testDefaultPathForCaptureOperationTest')
        FileStore fileStore = new MockInMemoryFileStore()
        setupDatabaseSchema(databaseConfig)
        setupSampleData(databaseConfig)
        storeSampleDataConfig(fileStore)

        CaptureOperation captureOp = new CaptureOperation(databaseConfig, fileStore)
        assert captureOp.run()

        def dataset = new XmlSlurper().parse(fileStore.getFileInputStream('data/packages/COURSES.xml'))
        assert dataset.'COURSES'.size() == 3

        def course1 = dataset.'COURSES'.find {
            it.@COURSEID == '1'
        } [0]
        assert course1.attributes().get('NAME') == 'Biology 101'
        assert course1.attributes().get('YEAROFFERED') == '2013'
        assert course1.attributes().get('SEMESTER') == 'F'

        def course2 = dataset.'COURSES'.find {
            it.@COURSEID == '2'
        } [0]
        assert course2.attributes().get('NAME') == 'Biology 101'
        assert course2.attributes().get('YEAROFFERED') == '2014'
        assert course2.attributes().get('SEMESTER') == 'S'

        def course3 = dataset.'COURSES'.find {
            it.@COURSEID == '3'
        } [0]
        assert course3.attributes().get('NAME') == 'Chemistry 101'
        assert course3.attributes().get('YEAROFFERED') == '2013'
        assert course3.attributes().get('SEMESTER') == 'F'
    }

    private void storeSampleDataConfig(MockInMemoryFileStore mockInMemoryFileStore) {
        String xml = getClass().getResourceAsStream('CaptureOperationTest-data-packages.xml').text
        mockInMemoryFileStore.getFileOutputStream('data/data-packages.xml').write(xml.bytes)
    }

    private void setupSampleData(DatabaseConfig databaseConfig) {
        assert SqlScriptRunner.runCommand(databaseConfig,
                'insert into "dbo"."COURSES" values (1, \'Biology 101\', 2013, \'F\')')
        assert SqlScriptRunner.runCommand(databaseConfig,
                'insert into "dbo"."COURSES" values (2, \'Biology 101\', 2014, \'S\')')
        assert SqlScriptRunner.runCommand(databaseConfig,
                'insert into "dbo"."COURSES" values (3, \'Chemistry 101\', 2013, \'F\')')
    }

    private void setupDatabaseSchema(DatabaseConfig databaseConfig) {
        BuildOperation buildOp = new BuildOperation(databaseConfig, null,
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()
        DerbySchemaInformationService.printTableList(databaseConfig)
    }

    private DatabaseConfig getDatabaseConfig(String database) {
        new DatabaseConfig().setDatabase(database).setServer('memory').setVendor(DatabaseVendor.DERBY)
    }
}
