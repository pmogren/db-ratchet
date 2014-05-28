package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.databases.DatabaseVendor
import com.commercehub.dbratchet.filestore.ClasspathFileStore
import org.junit.Test

/**
 * Created by jaystgelais on 5/25/14.
 */
class SchemaMigratorTest {

    @Test
    void testLocationOfFlywayInternalResources() {
        assert SchemaMigrator.getSchemaVersionTableCreateScript(DatabaseVendor.MSSQL) != null
        assert SchemaMigrator.getSchemaVersionTableCreateScript(DatabaseVendor.DERBY) != null
    }

    @Test
    void testGetSchemaTableName() {
        def databaseConfig = new DatabaseConfig().setDatabase('testGetSchemaTableNameForSchemaMigratorTest')
                .setServer('memory').setVendor(DatabaseVendor.DERBY)
        SchemaMigrator schemaMigrator = new SchemaMigrator(databaseConfig,
                new SchemaConfig(new ClasspathFileStore('/com/commercehub/dbratchet/sampleschema/')))
        assert schemaMigrator.schemaTableName == 'schema_version'
    }
}
