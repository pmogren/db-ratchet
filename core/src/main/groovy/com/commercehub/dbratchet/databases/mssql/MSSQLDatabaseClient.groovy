package com.commercehub.dbratchet.databases.mssql

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.databases.DataMigrator
import com.commercehub.dbratchet.databases.DatabaseClient
import com.commercehub.dbratchet.databases.SchemaInformationService
import com.commercehub.dbratchet.util.SqlScriptRunner
import groovy.sql.Sql

/**
 * Created by jaystgelais on 5/21/14.
 */
class MSSQLDatabaseClient implements DatabaseClient {
    public static final String JDBC_DRIVER_CLASS = 'net.sourceforge.jtds.jdbc.Driver'
    static {
        ClassLoader.systemClassLoader.loadClass(JDBC_DRIVER_CLASS)
    }

    String driverClass = JDBC_DRIVER_CLASS

    @Override
    boolean createDatabase(DatabaseConfig databaseConfig) {
        return SqlScriptRunner.runCommand(databaseConfig.serverConfig, "create database ${databaseConfig.database}")
    }

    @Override
    boolean deleteDatabase(DatabaseConfig databaseConfig) {
        return SqlScriptRunner.runCommand(databaseConfig.serverConfig, "drop database ${databaseConfig.database}")
    }

    @Override
    DataMigrator getDataMigrator() {
        return new MSSQLDataMigrator()
    }

    @Override
    SchemaInformationService getSchemaInformationService() {
        return new MSSQLSchemaInformationService()
    }

    @Override
    Sql getSql(DatabaseConfig databaseConfig) {
        if (databaseConfig.user) {
            return Sql.newInstance(getJdbcUrl(databaseConfig), databaseConfig.user,
                    databaseConfig.password, driverClass)
        }

        return Sql.newInstance(getJdbcUrl(databaseConfig), driverClass)
    }

    @Override
    String getJdbcUrl(DatabaseConfig databaseConfig) {
        String jdbcUrl = "jdbc:jtds:sqlserver://${databaseConfig.server}:1433"
        if (databaseConfig.database) {
            jdbcUrl += "/${databaseConfig.database}"
        }

        return jdbcUrl
    }
}
