package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.DatabaseConfig
import com.googlecode.flyway.core.Flyway

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 10/10/13
 * Time: 5:14 PM
 */
class SchemaMigrator {
    Flyway flyway

    SchemaMigrator(DatabaseConfig dbConfig) {
        flyway = new Flyway().with {
            setDataSource(dbConfig.jdbcUrl, dbConfig.user, dbConfig.password)
            setLocations("filesystem:${Version.VERSIONS_DIR}")
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
