package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.databases.DatabaseVendor
import com.commercehub.dbratchet.filestore.ClasspathFileStore
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import groovy.sql.Sql

/**
 * Created by jaystgelais on 5/20/14.
 */
class MigrateOperationTest extends GroovyTestCase {

    void testDefaultPath() {
        PrintStream oldErr = System.err
        ByteArrayOutputStream err = new ByteArrayOutputStream()
        System.setErr(new PrintStream(err))

        DatabaseConfig databaseConfig = getDatabaseConfig('testDefaultPathForMigrationOperationTest')
        FileStore fileStore = new ClasspathFileStore('/com/commercehub/dbratchet/sampledata/')
        setupDatabaseSchema(databaseConfig)

        MigrateOperation migrateOp = new MigrateOperation(databaseConfig, fileStore)
        assert migrateOp.isConfigured()
        assert migrateOp.run()

        Sql sql = DatabaseClientFactory.getDatabaseClient(databaseConfig.vendor).getSql(databaseConfig)
        assert sql.rows('select * from COURSES').size() > 0
        assert err.toString().trim() == '0 rows migrated for table EMPTY. Are your packages are correctly configured?'
        assert sql.rows('select * from EMPTY').size() == 0

        System.setErr(oldErr)
    }

    private void setupDatabaseSchema(DatabaseConfig databaseConfig) {
        BuildOperation buildOp = new BuildOperation(databaseConfig, null,
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert buildOp.isConfigured()
        assert buildOp.run()
    }

    private DatabaseConfig getDatabaseConfig(String database) {
        new DatabaseConfig().setDatabase(database).setServer('memory').setVendor(DatabaseVendor.DERBY)
    }

}
