package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseVendor
import com.commercehub.dbratchet.schema.redgate.SqlCompareFactory

/**
 * Created by jaystgelais on 5/22/14.
 */
@SuppressWarnings('PropertyName')
@SuppressWarnings('FieldName')
class TestConfig {
    static final boolean isRedgateAvailable
    static final boolean isMSSQLAvailable
    private static final DatabaseConfig mssqlServerCredentials

    static {
        isRedgateAvailable = new SqlCompareFactory().newSqlCompare()
        mssqlServerCredentials = loadMssqlServerCredentials()
        isMSSQLAvailable = mssqlServerCredentials
    }

    private static DatabaseConfig loadMssqlServerCredentials() {
        InputStream congigInputStream = TestConfig.getResourceAsStream('/mssql-config.properties')
        if (congigInputStream) {
            Properties props = new Properties()
            props.load(congigInputStream)
            String server = props.get('server')
            String user = props.get('user')
            String password = props.get('password')

            if (server && user && password) {
                return new DatabaseConfig()
                        .setVendor(DatabaseVendor.MSSQL)
                        .setServer(server)
                        .setUser(user)
                        .setPassword(password)
            }
        }

        return null
    }

    static DatabaseConfig getMssqlServerCredentials() {
        return mssqlServerCredentials.serverConfig
    }
}
