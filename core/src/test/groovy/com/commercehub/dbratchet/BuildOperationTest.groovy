package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.databases.DatabaseVendor
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
        def databaseConfig = getDatabaseConfig('testDefaultPathForBuildOperationTest')
        BuildOperation buildOp = new BuildOperation(databaseConfig, null,
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.isConfigured()
        assert buildOp.run()

        SchemaInformationService schemaInformationService =
                DatabaseClientFactory.getDatabaseClient(databaseConfig.vendor).schemaInformationService
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'COURSES')
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'STUDENTS')
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'ENROLLMENT')
    }

    void testMigrationToSpecificVersion() {
        def databaseConfig = getDatabaseConfig('testMigrationToSpecificVersionForBuildOperationTest')
        BuildOperation buildOp = new BuildOperation(databaseConfig, new Version(0,1,0),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()

        SchemaInformationService schemaInformationService =
                DatabaseClientFactory.getDatabaseClient(databaseConfig.vendor).schemaInformationService
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'COURSES')
        assert !schemaInformationService.isTableInDatabase(databaseConfig, 'STUDENTS')
        assert !schemaInformationService.isTableInDatabase(databaseConfig, 'ENROLLMENT')

        // Now apply the final version
        buildOp = new BuildOperation(databaseConfig, new Version(0,1,1),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()

        assert schemaInformationService.isTableInDatabase(databaseConfig, 'COURSES')
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'STUDENTS')
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'ENROLLMENT')
    }

    void testSafetyCheck() {
        def databaseConfig = getDatabaseConfig('testSafetyCheckForBuildOperationTest')
        BuildOperation buildOp = new BuildOperation(databaseConfig, new Version(0,1,0),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()

        DerbySchemaInformationService.printTableList(databaseConfig)
        DatabaseClientFactory.getDatabaseClient(databaseConfig.vendor).getSql(databaseConfig)
                .execute('drop table "dbo"."schema_version"')
        assert !buildOp.run()

        BuildOperation buildOp2 = new BuildOperation(databaseConfig, new Version(0,1,0),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema2/')))
        buildOp2.setInitNonEmptyDbForced(true)
        assert buildOp2.run()
    }

    private DatabaseConfig getDatabaseConfig(String database) {
        new DatabaseConfig().setDatabase(database).setServer('memory').setVendor(DatabaseVendor.DERBY)
    }
}
