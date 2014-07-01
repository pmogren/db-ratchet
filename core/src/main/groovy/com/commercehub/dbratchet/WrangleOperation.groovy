package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseClient
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.schema.PresentFilestoreSchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.SchemaDifferenceEngine
import com.commercehub.dbratchet.schema.SchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaMigrator
import com.commercehub.dbratchet.schema.Version
import groovy.sql.GroovyRowResult
import org.flywaydb.core.api.FlywayException
import groovy.sql.DataSet
import groovy.sql.Sql

// TODO Write Unit Test
// TODO Add this to the command line application
/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/17/13
 * Time: 4:59 PM
 */
class WrangleOperation implements Operation {
    final String name = 'wrangle'

    private final DatabaseConfig dbConfig
    private final Version version
    private final SchemaConfig schemaConfig
    private final SchemaDifferenceEngineFactory schemaDifferenceEngineFactory
    private final DatabaseClient databaseClient
    private final File outputScriptFile

    WrangleOperation(DatabaseConfig dbConfig, Version version, SchemaConfig schemaConfig) {
        this(dbConfig, version, schemaConfig, null)
    }

    WrangleOperation(DatabaseConfig dbConfig, Version version, SchemaConfig schemaConfig, File outputScriptFile) {
        this.dbConfig = dbConfig
        this.version = version
        this.schemaConfig = schemaConfig
        this.outputScriptFile = outputScriptFile

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

        try {
            isSuccessful = doComparison(comparisonDatabaseConfig, dbConfig)
            if (isSuccessful) {
                isSuccessful = generateSchemaVersionLog(comparisonDatabaseConfig, dbConfig)
            }
        } finally {
            if (!databaseClient.deleteDatabase(comparisonDatabaseConfig)) {
                System.err.println 'WARNING: Unable to clean up transient database ' +
                        "[${comparisonDatabaseConfig.server}.${comparisonDatabaseConfig.database}]. " +
                        'This database will need to be cleaned up manually.'
            }
        }

        return isSuccessful
    }

    private boolean generateSchemaVersionLog(DatabaseConfig srcDatabaseConfig, DatabaseConfig targetDatabaseConfig) {
        if (outputScriptFile) {
            return scriptSchemaVersionLog(srcDatabaseConfig)
        }
        return transferSchemaVersionTable(srcDatabaseConfig, targetDatabaseConfig)
    }

    boolean scriptSchemaVersionLog(DatabaseConfig databaseConfig) {
        String tableName = getSchemaVersionTableName(databaseConfig)
        scriptCreateSchemaVersionTable(tableName)
        databaseClient.getSql(databaseConfig).dataSet(tableName).rows().each { row ->
            String insertSQL = writeInsertStatement(tableName, row)
            outputScriptFile.append("${insertSQL}\n")
        }
        return true
    }

    private void scriptCreateSchemaVersionTable(String tableName) {
        SchemaMigrator.getSchemaVersionTableCreateScript(dbConfig.vendor).eachLine { line ->
            outputScriptFile.append(line.replaceAll('\\$\\{schema\\}', 'dbo').replaceAll('\\$\\{table\\}', tableName))
        }
    }

    @SuppressWarnings('DuplicateStringLiteral')
    String writeInsertStatement(String tableName, GroovyRowResult rowResult) {
        List<String> fieldNames = rowResult.keySet().asList()
        List<String> fieldValues = [] as Queue<String>
        fieldNames.each { key ->
            fieldValues.add(escapeAndQuoteForSQL(rowResult.get(key)))
        }
        return "insert into ${tableName} (${fieldNames.join(', ')}) values (${fieldValues.join(', ')})"
    }

    String escapeAndQuoteForSQL(String value) {
        return "'${value.replaceAll('\'', '\'\'')}'"
    }

    private boolean transferSchemaVersionTable(DatabaseConfig srcDatabaseConfig, DatabaseConfig targetDatabaseConfig) {
        Sql srcSql = databaseClient.getSql(srcDatabaseConfig)
        Sql targetSql = databaseClient.getSql(targetDatabaseConfig)

        String schemaVersionTableName
        try {
            schemaVersionTableName = initializeSchemaVersionTable(targetDatabaseConfig)
        } catch (FlywayException e) {
            System.err.println 'Error initializing target database with schema_version table.'
            e.printStackTrace()
            return false
        }

        return copyTableData(schemaVersionTableName, srcSql, targetSql)
    }

    boolean copyTableData(String tableName, Sql srcSql, Sql targetSql) {
        DataSet srcData = srcSql.dataSet(tableName)
        DataSet targetData = targetSql.dataSet(tableName)
        srcData.rows().each { row ->
            targetData.add(row)
        }

        return true
    }

    String initializeSchemaVersionTable(DatabaseConfig databaseConfig) {
        def schemaMigrator = new SchemaMigrator(databaseConfig, schemaConfig)
        schemaMigrator.initializeSchemaVersionTable()
        return schemaMigrator.schemaTableName
    }

    String getSchemaVersionTableName(DatabaseConfig databaseConfig) {
        return new SchemaMigrator(databaseConfig, schemaConfig).schemaTableName
    }

    private boolean doComparison(DatabaseConfig srcDatabaseConfig, DatabaseConfig targetDatabaseConfig) {
        SchemaDifferenceEngine sde = schemaDifferenceEngineFactory.getSchemaDifferenceEngine(schemaConfig)
        sde.setSourceDatabase(srcDatabaseConfig)
        sde.setTargetDatabase(targetDatabaseConfig)
        if (outputScriptFile) {
            return sde.generateScriptToBuildSourceToTarget(outputScriptFile)
        }

        return sde.pushSourceToTarget()
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
