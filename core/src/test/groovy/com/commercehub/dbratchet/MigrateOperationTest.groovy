package com.commercehub.dbratchet

import com.commercehub.dbratchet.filestore.ClasspathFileStore
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.schema.SchemaConfig

/**
 * Created by jaystgelais on 5/20/14.
 */
class MigrateOperationTest extends GroovyTestCase {

    void testDefaultPath() {
        DatabaseConfig databaseConfig = new MockDatabaseConfig().setDatabase('testDefaultPathFoMigrationOperationTest')
        FileStore fileStore = new ClasspathFileStore('/com/commercehub/dbratchet/sampledata/')
        setupDatabaseSchema(databaseConfig)

        // TODO factor existing migration logic into MSSQL service and provide Derby version
        MigrateOperation migrateOp = new MigrateOperation(databaseConfig, fileStore)
        assert migrateOp.isConfigured()
        //assert migrateOp.run()
    }

    private void setupDatabaseSchema(DatabaseConfig databaseConfig) {
        BuildOperation buildOp = new BuildOperation(databaseConfig, null,
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.isConfigured()
        assert buildOp.run()
    }

}
