package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseVendor

/**
 * Created by jgelais on 5/19/2014.
 */
class MockDatabaseConfig extends DatabaseConfig {

    @Override
    @SuppressWarnings('GetterMethodCouldBeProperty')
    String getServer() {
        return 'memory'
    }

    @Override
    @SuppressWarnings('GetterMethodCouldBeProperty')
    DatabaseVendor getVendor() {
        return DatabaseVendor.DERBY
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new MockDatabaseConfig()
                .setServer(server)
                .setDatabase(database)
                .setUser(user)
                .setPassword(password)
                .setVendor(vendor)
    }
}
