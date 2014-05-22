package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.SchemaMigrator
import com.commercehub.dbratchet.schema.Version
import com.commercehub.dbratchet.util.SqlRunner
import com.commercehub.dbratchet.util.SqlRunnerFactory

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/19/13
 * Time: 3:58 PM
 */
class BuildOperation implements Operation {
    final String name = 'Build'

    DatabaseConfig dbConfig
    DatabaseConfig dbConfigWithoutDbName
    Version version
    SqlRunner sqlRunner
    SchemaConfig schemaConfig

    BuildOperation(DatabaseConfig dbConfig, Version version, SchemaConfig schemaConfig) {
        this.dbConfig = dbConfig
        dbConfigWithoutDbName = dbConfig.clone().setDatabase(null)
        this.version = version
        this.schemaConfig = schemaConfig
        this.sqlRunner = new SqlRunnerFactory().sqlRunner
    }

    @Override
    boolean run() {
        version = version ?: schemaConfig.version

        println "Migrating Schema to Version: $version"

        boolean returnVal = true

        if (!DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).schemaInformationService
                .doesDatabaseExist(dbConfigWithoutDbName, dbConfig.database)) {
            returnVal &= sqlRunner.runCommand(dbConfigWithoutDbName, "create database ${dbConfig.database}")
            InputStream is = schemaConfig.fileStore.getFileInputStream("${SchemaConfig.SCRIPTS_DIR}/post-create.sql")
            if (is) {
                sqlRunner.runScript(dbConfig, is)
            }
        }

        if (isSafeToMigrate()) {
            doSchemaMigration(version)
        } else {
            System.err.println 'Database is non-empty but does not contain schema version information. ' +
                    'Cannot build upgrade schema of this database.'
            returnVal = false
        }

        return returnVal
    }

    private boolean isSafeToMigrate() {
        return isDbSchemaEmpty() || isSchemaVersionTableInDatabase()
    }

    private boolean isDbSchemaEmpty() {
        return DatabaseClientFactory.getDatabaseClient(dbConfig.vendor)
                .schemaInformationService.isDatabaseEmpty(dbConfig)
    }

    private void doSchemaMigration(Version version) {
        SchemaMigrator schemaMigrator = new SchemaMigrator(dbConfig, schemaConfig)
        schemaMigrator.migrate(version)
    }

    @Override
    boolean isConfigured() {
        if (!dbConfig.server) {
            return false
        }

        if (!dbConfig.database) {
            return false
        }

        if (dbConfig.user && !dbConfig.password) {
            return false
        }

        return true
    }

    private boolean isSchemaVersionTableInDatabase() {
        return DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).schemaInformationService
                .isTableInDatabase(dbConfig, 'dbo.schema_version')
    }
}
