package com.commercehub.dbratchet.databases

import com.commercehub.dbratchet.DatabaseConfig
import groovy.sql.Sql

/**
 * Created by jaystgelais on 5/21/14.
 */
interface DatabaseClient {
    String getDriverClass()
    DataMigrator getDataMigrator()
    SchemaInformationService getSchemaInformationService()
    Sql getSql(DatabaseConfig databaseConfig)
    String getJdbcUrl(DatabaseConfig databaseConfig)
}