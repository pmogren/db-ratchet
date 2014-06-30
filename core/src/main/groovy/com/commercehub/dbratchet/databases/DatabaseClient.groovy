package com.commercehub.dbratchet.databases

import com.commercehub.dbratchet.DatabaseConfig
import groovy.sql.Sql

/**
 * Created by jaystgelais on 5/21/14.
 */
interface DatabaseClient {

    String getDriverClass()

    @SuppressWarnings('FactoryMethodName')
    boolean createDatabase(DatabaseConfig databaseConfig)

    boolean deleteDatabase(DatabaseConfig databaseConfig)

    DataMigrator getDataMigrator()

    SchemaInformationService getSchemaInformationService()

    Sql getSql(DatabaseConfig databaseConfig)

    String getJdbcUrl(DatabaseConfig databaseConfig)

    String getRowCountQuery()

}