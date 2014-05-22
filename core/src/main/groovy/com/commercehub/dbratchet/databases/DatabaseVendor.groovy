package com.commercehub.dbratchet.databases

/**
 * Created by jgelais on 5/22/2014.
 */
enum DatabaseVendor {
    MSSQL('sqlserver'), DERBY('derby')

    private final static Map<String, DatabaseVendor> MAP
    static {
        MAP = [:]
        DatabaseVendor.values().each { DatabaseVendor vendor ->
            MAP.put(vendor.jdbcSubProtocol, vendor)
        }
    }
    static DatabaseVendor lookupDatabaseVendoryJdbcSubProtocol(String jdbcSubProtocol) {
        return MAP.get(jdbcSubProtocol)
    }

    final String jdbcSubProtocol

    DatabaseVendor(String jdbcSubProtocol) {
        this.jdbcSubProtocol = jdbcSubProtocol
    }

    @Override
    String toString() {
        return jdbcSubProtocol
    }
}