package com.commercehub.dbratchet.databases.derby

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.databases.SchemaInformationService

import java.sql.ResultSet

/**
 * Created by jgelais on 5/19/2014.
 */
class DerbySchemaInformationService implements SchemaInformationService {

    @Override
    boolean doesDatabaseExist(DatabaseConfig dbConfig, String dbName) {
        // For use of Derby in Unit tests we are using the 'create=true' property on the url
        return true
    }

    @Override
    boolean isDatabaseEmpty(DatabaseConfig dbConfig) {
        return !DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig).connection.metaData
                .getTables(null, null, '%', ['TABLE', 'VIEW'].toArray(new String[0])).next()
    }

    @Override
    boolean isTableInDatabase(DatabaseConfig dbConfig, String tableName) {
        String schema = null
        String actualTableName = tableName

        def tableNameParts = tableName.split(/\./)
        if (tableNameParts.length == 2) {
            schema = tableNameParts[0]
            actualTableName = tableNameParts[1]
        }
        return DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig).connection.metaData
                .getTables(null, schema, "$actualTableName", null).next()
    }

    @SuppressWarnings('DuplicateStringLiteral')
    @SuppressWarnings('DuplicateListLiteral')
    static void printTableList(DatabaseConfig dbConfig) {
        ResultSet rs = DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig).connection.metaData
                .getTables(null, null, '%', ['TABLE', 'VIEW'].toArray(new String[0]))
        while (rs.next()) {
            println "Found table: ${rs.getString('TABLE_NAME')}"
            println "    type:     ${rs.getString('TABLE_TYPE')}"
            println "    cataolog: ${rs.getString('TABLE_CAT')}"
            println "    schema:   ${rs.getString('TABLE_SCHEM')}"
        }
    }
}
