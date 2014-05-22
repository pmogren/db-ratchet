package com.commercehub.dbratchet

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
    String getVendor() {
        return 'derby'
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
