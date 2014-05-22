package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.databases.SchemaInformationService
import com.commercehub.dbratchet.filestore.ClasspathFileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version
import com.commercehub.dbratchet.databases.derby.DerbySchemaInformationService

/**
 * Created by jgelais on 5/19/2014.
 */
class BuildOperationTest extends GroovyTestCase {
    void testDefaultPath() {
        def databaseConfig = new MockDatabaseConfig().setDatabase('testDefaultPath')
        BuildOperation buildOp = new BuildOperation(databaseConfig, null,
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.isConfigured()
        assert buildOp.run()

        SchemaInformationService schemaInformationService =
                DatabaseClientFactory.getDatabaseClient(databaseConfig.vendor).schemaInformationService
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'DBO.COURSES')
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'DBO.STUDENTS')
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'DBO.ENROLLMENT')
    }

    void testMigrationToSpecificVersion() {
        def databaseConfig = new MockDatabaseConfig().setDatabase('testMigrationToSpecificVersion')
        BuildOperation buildOp = new BuildOperation(databaseConfig, new Version(0,1,0),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()

        SchemaInformationService schemaInformationService =
                DatabaseClientFactory.getDatabaseClient(databaseConfig.vendor).schemaInformationService
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'DBO.COURSES')
        assert !schemaInformationService.isTableInDatabase(databaseConfig, 'DBO.STUDENTS')
        assert !schemaInformationService.isTableInDatabase(databaseConfig, 'DBO.ENROLLMENT')

        // Now apply the final version
        buildOp = new BuildOperation(databaseConfig, new Version(0,1,1),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()

        assert schemaInformationService.isTableInDatabase(databaseConfig, 'DBO.COURSES')
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'DBO.STUDENTS')
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'DBO.ENROLLMENT')
    }

    void testSafetyCheck() {
        def databaseConfig = new MockDatabaseConfig().setDatabase('testSafetyCheck')
        BuildOperation buildOp = new BuildOperation(databaseConfig, new Version(0,1,0),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()

        DerbySchemaInformationService.printTableList(databaseConfig)
        DatabaseClientFactory.getDatabaseClient(databaseConfig.vendor).getSql(databaseConfig)
                .execute('drop table "dbo"."schema_version"')
        assert !buildOp.run()
    }
}
