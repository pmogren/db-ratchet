package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.databases.DatabaseVendor
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
}
