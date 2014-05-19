package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.schema.derby.DerbyDatabaseOperationService
import com.commercehub.dbratchet.schema.mssql.MSSQLDatabaseOperationService

/**
 * Created by jgelais on 5/19/2014.
 */
class DatabaseOperationServiceFactory {
    private static final DatabaseOperationService MSSQL = new MSSQLDatabaseOperationService()
    private static final DatabaseOperationService DERBY = new DerbyDatabaseOperationService()
    private static final Map<String, DatabaseOperationService> MAP = [
            'sqlserver': MSSQL,
            'derby':     DERBY
    ]

    static DatabaseOperationService getDatabaseOperationService(String vendor) {
        return MAP.get(vendor)
    }
}
