package com.commercehub.dbratchet.databases.derby

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.databases.DataMigrator
import com.commercehub.dbratchet.databases.DatabaseClient
import com.commercehub.dbratchet.databases.DbUnitDataMigrator
import com.commercehub.dbratchet.databases.SchemaInformationService
import groovy.sql.Sql

import java.sql.SQLSyntaxErrorException

/**
 * Created by jaystgelais on 5/21/14.
 */
class DerbyDatabaseClient implements DatabaseClient {
    public static final String JDBC_DRIVER_CLASS = 'org.apache.derby.jdbc.EmbeddedDriver'
    static {
        ClassLoader.systemClassLoader.loadClass(JDBC_DRIVER_CLASS)
    }

    String driverClass = JDBC_DRIVER_CLASS

    @Override
    boolean createDatabase(DatabaseConfig databaseConfig) {
        // Do nothing. We always create on connection anyway.
        return true
    }

    @Override
    DataMigrator getDataMigrator() {
        return new DbUnitDataMigrator()
    }

    @Override
    SchemaInformationService getSchemaInformationService() {
        return new DerbySchemaInformationService()
    }

    @Override
    @SuppressWarnings('EmptyCatchBlock')
    Sql getSql(DatabaseConfig databaseConfig) {
        Sql sql = Sql.newInstance(getJdbcUrl(databaseConfig), driverClass)
        try {
            sql.execute('set schema "dbo"')
        } catch (SQLSyntaxErrorException e) {
            // Do Something Smarter here than try/catch
        }
        return sql
    }

    @Override
    String getJdbcUrl(DatabaseConfig databaseConfig) {
        return "jdbc:derby:memory:${databaseConfig.database};create=true"
    }
}
