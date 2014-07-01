package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseClient
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.SchemaMigrator
import com.commercehub.dbratchet.schema.Version
import com.commercehub.dbratchet.util.SqlScriptRunner

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/19/13
 * Time: 3:58 PM
 */
class BuildOperation implements Operation {
    final String name = 'Build'

    DatabaseConfig dbConfig
    Version version
    SchemaConfig schemaConfig

    BuildOperation(DatabaseConfig dbConfig, Version version, SchemaConfig schemaConfig) {
        this.dbConfig = dbConfig
        this.version = version
        this.schemaConfig = schemaConfig
    }

    @Override
    boolean run() {
        version = version ?: schemaConfig.version

        println "Migrating Schema to Version: $version"

        boolean returnVal = true

        DatabaseClient databaseClient = DatabaseClientFactory.getDatabaseClient(dbConfig.vendor)
        if (!databaseClient.schemaInformationService.doesDatabaseExist(dbConfig)) {
            returnVal &= databaseClient.createDatabase(dbConfig)
            InputStream is = schemaConfig.fileStore.getFileInputStream("${SchemaConfig.SCRIPTS_DIR}/post-create.sql")
            if (is) {
                SqlScriptRunner.runScript(dbConfig, is)
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
        return dbConfig.isValidDatabaseConfig()
    }

    private boolean isSchemaVersionTableInDatabase() {
        return DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).schemaInformationService
                .isTableInDatabase(dbConfig, 'dbo.schema_version')
    }
}
