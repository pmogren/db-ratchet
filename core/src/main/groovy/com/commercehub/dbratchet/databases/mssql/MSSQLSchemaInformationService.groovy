package com.commercehub.dbratchet.databases.mssql

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.databases.SchemaInformationService
import groovy.sql.Sql

import java.sql.ResultSet

/**
 * Created by jgelais on 5/19/2014.
 */
class MSSQLSchemaInformationService implements SchemaInformationService {

    @Override
    boolean doesDatabaseExist(DatabaseConfig dbConfig) {
        return (DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig.serverConfig)
                .rows('SELECT name FROM master.sys.databases WHERE name = ?', dbConfig.database).size() > 0)
    }

    @Override
    boolean isDatabaseEmpty(DatabaseConfig dbConfig) {
        return (DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig)
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

        Sql sql = DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig)
        try {
            return !sql.rows('SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ' +
                    "'${schema}' AND TABLE_NAME = '${actualTableName}'").empty
        } finally {
            sql.close()
        }
    }

    @SuppressWarnings('DuplicateStringLiteral')
    @SuppressWarnings('DuplicateListLiteral')
    static void printTableList(DatabaseConfig dbConfig) {
        ResultSet rs = DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig).connection.metaData
                .getTables(null, null, '%', ['TABLE', 'VIEW'].toArray(new String[0]))
        while (rs.next()) {
            if (!['sys', 'INFORMATION_SCHEMA'].contains(rs.getString('TABLE_SCHEM'))) {
                println "Found table: ${rs.getString('TABLE_NAME')}"
                println "    type:     ${rs.getString('TABLE_TYPE')}"
                println "    cataolog: ${rs.getString('TABLE_CAT')}"
                println "    schema:   ${rs.getString('TABLE_SCHEM')}"
            }
        }
    }
}
