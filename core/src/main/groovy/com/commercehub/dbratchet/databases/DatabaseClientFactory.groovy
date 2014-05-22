package com.commercehub.dbratchet.databases

import com.commercehub.dbratchet.databases.derby.DerbyDatabaseClient
import com.commercehub.dbratchet.databases.mssql.MSSQLDatabaseClient

/**
 * Created by jaystgelais on 5/21/14.
 */
class DatabaseClientFactory {
    private static final DatabaseClient MSSQL = new MSSQLDatabaseClient()
    private static final DatabaseClient DERBY = new DerbyDatabaseClient()
    private static final Map<String, DatabaseClient> MAP = [
            'sqlserver': MSSQL,
            'derby':     DERBY
    ]

    static DatabaseClient getDatabaseClient(String vendor) {
        return MAP.get(vendor)
    }
}
