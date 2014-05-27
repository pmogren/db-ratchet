package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.databases.DatabaseVendor
import org.flywaydb.core.Flyway

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 10/10/13
 * Time: 5:14 PM
 */
class SchemaMigrator {
    private static final Map<DatabaseVendor, String> SCHEMA_VERSION_TABLE_CREATE_SCRIPTS = [
            (DatabaseVendor.MSSQL): '/org/flywaydb/core/internal/dbsupport/sqlserver/createMetaDataTable.sql',
            (DatabaseVendor.DERBY): '/org/flywaydb/core/internal/dbsupport/derby/createMetaDataTable.sql'
    ]

    Flyway flyway

    SchemaMigrator(DatabaseConfig dbConfig, SchemaConfig schemaConfig) {
        flyway = new Flyway().with {
            setDataSource(DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getJdbcUrl(dbConfig),
                    dbConfig.user, dbConfig.password)
            setLocations(schemaConfig.schemaURLAsString)
            setSqlMigrationSuffix('upgrade.sql')
            setInitVersion('000.0000.000000')
            setInitOnMigrate(true)
            setSchemas('dbo')
            it
        }
    }

    String getSchemaTableName() {
        return flyway.table
    }

    void migrate(Version version) {
        flyway.setTarget(version.toString())
        flyway.migrate()
    }

    void initializeSchemaVersionTable() {
        flyway.init()
    }

    static InputStream getSchemaVersionTableCreateScript(DatabaseVendor vendor) {
        String resourcePath = SCHEMA_VERSION_TABLE_CREATE_SCRIPTS.get(vendor)
        if (resourcePath) {
            return SchemaMigrator.getResourceAsStream(resourcePath)
        }

        return null
    }
}
