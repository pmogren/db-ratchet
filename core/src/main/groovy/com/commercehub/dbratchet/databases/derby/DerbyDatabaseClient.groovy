package com.commercehub.dbratchet.databases.derby

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.databases.DataMigrator
import com.commercehub.dbratchet.databases.DatabaseClient
import com.commercehub.dbratchet.databases.DbUnitDataMigrator
import com.commercehub.dbratchet.databases.SchemaInformationService
import groovy.sql.Sql

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
    DataMigrator getDataMigrator() {
        return new DbUnitDataMigrator()
    }

    @Override
    SchemaInformationService getSchemaInformationService() {
        return new DerbySchemaInformationService()
    }

    @Override
    Sql getSql(DatabaseConfig databaseConfig) {
        return Sql.newInstance(getJdbcUrl(databaseConfig), driverClass)
    }

    @Override
    String getJdbcUrl(DatabaseConfig databaseConfig) {
        return "jdbc:derby:memory:${databaseConfig.database};create=true"
    }
}
