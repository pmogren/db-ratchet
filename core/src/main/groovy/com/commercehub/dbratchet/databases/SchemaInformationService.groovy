package com.commercehub.dbratchet.databases

import com.commercehub.dbratchet.DatabaseConfig

/**
 * Created by jgelais on 5/19/2014.
 */
interface SchemaInformationService {
    boolean doesDatabaseExist(DatabaseConfig dbConfig, String dbName)
    boolean isDatabaseEmpty(DatabaseConfig dbConfig)
    boolean isTableInDatabase(DatabaseConfig dbConfig, String tableName)
}