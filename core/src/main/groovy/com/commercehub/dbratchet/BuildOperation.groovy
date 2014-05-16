package com.commercehub.dbratchet

import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.SchemaMigrator
import com.commercehub.dbratchet.schema.Version
import com.commercehub.dbratchet.util.GroovySqlRunner
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

        boolean returnVal = true

        if (!doesDatabaseExist()) {
            returnVal &= sqlRunner.runCommand(dbConfigWithoutDbName, "create database ${dbConfig.database}")
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
        return (GroovySqlRunner.getSql(dbConfig)
                .rows('SELECT * FROM sys.objects WHERE OBJECTPROPERTY(object_id, \'IsMSShipped\') = 0').size() == 0)
    }

    private boolean doesDatabaseExist() {
        return (GroovySqlRunner.getSql(dbConfigWithoutDbName)
                .rows('SELECT name FROM master.sys.databases WHERE name = ?', dbConfig.database).size() > 0)
    }

    private void doSchemaMigration(Version version) {
        SchemaMigrator schemaMigrator = new SchemaMigrator(dbConfig, schemaLocationType)
        schemaMigrator.migrate(version)
    }

    SchemaMigrator.LocationType getSchemaLocationType() {
        if (schemaConfig.isFileURI()) {
            return SchemaMigrator.LocationType.FILE
        }

        return SchemaMigrator.LocationType.CLASSPATH
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
        return !GroovySqlRunner.getSql(dbConfig)
                .rows('SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ' +
                '\'dbo\' AND TABLE_NAME = \'schema_version\'').empty
    }
}
