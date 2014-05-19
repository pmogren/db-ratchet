package com.commercehub.dbratchet.schema.mssql

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.schema.DatabaseOperationService
import com.commercehub.dbratchet.util.GroovySqlRunner

/**
 * Created by jgelais on 5/19/2014.
 */
class MSSQLDatabaseOperationService implements DatabaseOperationService {
    public static final String JDBC_DRIVER_CLASS = 'net.sourceforge.jtds.jdbc.Driver'
    static {
        ClassLoader.systemClassLoader.loadClass(JDBC_DRIVER_CLASS)
    }

    String driverClass = JDBC_DRIVER_CLASS

    @Override
    boolean doesDatabaseExist(DatabaseConfig dbConfig, String dbName) {
        return (GroovySqlRunner.getSql(dbConfig)
                .rows('SELECT name FROM master.sys.databases WHERE name = ?', dbName).size() > 0)
    }

    @Override
    boolean isDatabaseEmpty(DatabaseConfig dbConfig) {
        return (GroovySqlRunner.getSql(dbConfig)
                .rows('SELECT * FROM sys.objects WHERE OBJECTPROPERTY(object_id, \'IsMSShipped\') = 0').size() == 0)
    }

    @Override
    boolean isTableInDatabase(DatabaseConfig dbConfig, String tableName) {
        String schema = 'dbo'
        String actualTableName = tableName

        def tableNameParts = tableName.split(/\./)
        if (tableNameParts.length == 2) {
            schema = tableNameParts[0]
            actualTableName = tableNameParts[1]
        }

        !GroovySqlRunner.getSql(dbConfig)
                .rows('SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ' +
                "'${schema}' AND TABLE_NAME = '${actualTableName}'").empty
    }
}
