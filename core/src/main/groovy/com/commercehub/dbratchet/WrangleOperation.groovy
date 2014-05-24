package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseClient
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.schema.PresentFilestoreSchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.SchemaDifferenceEngine
import com.commercehub.dbratchet.schema.SchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.Version

/**
 * Created by jaystgelais on 5/24/14.
 */
// TODO Write Unit Test
class WrangleOperation implements Operation {
    final String name = 'wrangle'

    private final DatabaseConfig dbConfig
    private final Version version
    private final SchemaConfig schemaConfig
    private final SchemaDifferenceEngineFactory schemaDifferenceEngineFactory
    private final DatabaseClient databaseClient

    WrangleOperation(DatabaseConfig dbConfig, Version version, SchemaConfig schemaConfig) {
        this.dbConfig = dbConfig
        this.version = version
        this.schemaConfig = schemaConfig

        databaseClient = DatabaseClientFactory.getDatabaseClient(dbConfig.vendor)
        schemaDifferenceEngineFactory = new PresentFilestoreSchemaDifferenceEngineFactory()
    }

    @Override
    boolean run() {
        DatabaseConfig comparisonDatabaseConfig = getDatabaseConfgOnServer(dbConfig.serverConfig,
                generateThrowAwayDatabaseName())
        boolean isSuccessful = new BuildOperation(comparisonDatabaseConfig, version, schemaConfig).run()
        if (!isSuccessful) {
            System.err.println 'Unable to create database transient database for ' +
                    "comparison on server [${dbConfig.server}]"
            return false
        }

        // TODO Add movement of schema_version table
        try {
            SchemaDifferenceEngine sde = schemaDifferenceEngineFactory.getSchemaDifferenceEngine(schemaConfig)
            sde.setSourceDatabase(comparisonDatabaseConfig)
            sde.useFileStoreAsTarget(dbConfig)
            sde.pushSourceToTarget()
        } finally {
            if (!databaseClient.deleteDatabase(comparisonDatabaseConfig)) {
                System.err.println 'WARNING: Unable to clean up transient database ' +
                        "[${comparisonDatabaseConfig.server}.${comparisonDatabaseConfig.database}]. " +
                        'This database will need to be cleaned up manually.'
            }
        }

        return isSuccessful
    }

    @Override
    boolean isConfigured() {
        return dbConfig.isValidDatabaseConfig()
    }

    DatabaseConfig getDatabaseConfgOnServer(DatabaseConfig databaseServerConfig, String dbName) {
        return new DatabaseConfig()
                .setVendor(databaseServerConfig.vendor)
                .setServer(dbServerConfig.server)
                .setDatabase(dbName)
                .setUser(dbServerConfig.user)
                .setPassword(dbServerConfig.password)
    }

    static String generateThrowAwayDatabaseName() {
        String uuid = UUID.randomUUID() as String
        String uniqPart = uuid.replaceAll('-', '')
        return "ratchet_${uniqPart}"
    }
}
