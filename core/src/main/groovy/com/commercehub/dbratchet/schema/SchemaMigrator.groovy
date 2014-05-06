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
        flyway = new Flyway()
        flyway.setDataSource(dbConfig.jdbcUrl, dbConfig.user, dbConfig.password)
        flyway.setLocations("filesystem:${Version.VERSIONS_DIR}")
        flyway.setSqlMigrationSuffix('upgrade.sql')
        flyway.setInitVersion('000.0000.000000')
        flyway.setInitOnMigrate(true)
        flyway.setSchemas('dbo')
    }

    void migrate(Version version) {
        flyway.setTarget(version.toString())
        flyway.migrate()
    }
}
