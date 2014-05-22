package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.googlecode.flyway.core.Flyway

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 10/10/13
 * Time: 5:14 PM
 */
class SchemaMigrator {
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

    void migrate(Version version) {
        flyway.setTarget(version.toString())
        flyway.migrate()
    }
}
