package com.commercehub.dbratchet

import com.commercehub.dbratchet.filestore.ClasspathFileStore
import com.commercehub.dbratchet.schema.DatabaseOperationServiceFactory
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version
import com.commercehub.dbratchet.schema.derby.DerbyDatabaseOperationService
import com.commercehub.dbratchet.util.GroovySqlRunner

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
        assert DatabaseOperationServiceFactory.getDatabaseOperationService(databaseConfig.vendor)
                .isTableInDatabase(databaseConfig, 'DBO.COURSES')
        assert DatabaseOperationServiceFactory.getDatabaseOperationService(databaseConfig.vendor)
                .isTableInDatabase(databaseConfig, 'DBO.STUDENTS')
        assert DatabaseOperationServiceFactory.getDatabaseOperationService(databaseConfig.vendor)
                .isTableInDatabase(databaseConfig, 'DBO.ENROLLMENT')
    }

    void testMigrationToSpecificVersion() {
        def databaseConfig = new MockDatabaseConfig().setDatabase('testMigrationToSpecificVersion')
        BuildOperation buildOp = new BuildOperation(databaseConfig, new Version(0,1,0),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()
        assert DatabaseOperationServiceFactory.getDatabaseOperationService(databaseConfig.vendor)
                .isTableInDatabase(databaseConfig, 'DBO.COURSES')
        assert !DatabaseOperationServiceFactory.getDatabaseOperationService(databaseConfig.vendor)
                .isTableInDatabase(databaseConfig, 'DBO.STUDENTS')
        assert !DatabaseOperationServiceFactory.getDatabaseOperationService(databaseConfig.vendor)
                .isTableInDatabase(databaseConfig, 'DBO.ENROLLMENT')

        // Now apply the final version
        buildOp = new BuildOperation(databaseConfig, new Version(0,1,1),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()
        assert DatabaseOperationServiceFactory.getDatabaseOperationService(databaseConfig.vendor)
                .isTableInDatabase(databaseConfig, 'DBO.COURSES')
        assert DatabaseOperationServiceFactory.getDatabaseOperationService(databaseConfig.vendor)
                .isTableInDatabase(databaseConfig, 'DBO.STUDENTS')
        assert DatabaseOperationServiceFactory.getDatabaseOperationService(databaseConfig.vendor)
                .isTableInDatabase(databaseConfig, 'DBO.ENROLLMENT')
    }

    void testSafetyCheck() {
        def databaseConfig = new MockDatabaseConfig().setDatabase('testSafetyCheck')
        BuildOperation buildOp = new BuildOperation(databaseConfig, new Version(0,1,0),
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.run()

        DerbyDatabaseOperationService.printTableList(databaseConfig)
        GroovySqlRunner.getSql(databaseConfig).execute('drop table "dbo"."schema_version"')
        assert !buildOp.run()
    }
}
