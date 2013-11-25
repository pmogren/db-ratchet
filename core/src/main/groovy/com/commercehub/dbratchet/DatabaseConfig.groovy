package com.commercehub.dbratchet

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/17/13
 * Time: 11:25 AM
 */
class DatabaseConfig implements Cloneable {
    String vendor = 'sqlserver'
    String server
    String database
    String user
    String password

    String getServer() {
        return server
    }

    DatabaseConfig setServer(String server) {
        this.server = server
        return this
    }

    String getDatabase() {
        return database
    }

    DatabaseConfig setDatabase(String database) {
        this.database = database
        return this
    }

    String getUser() {
        return user
    }

    DatabaseConfig setUser(String user) {
        this.user = user
        return this
    }

    String getPassword() {
        return password
    }

    DatabaseConfig setVendor(String vendor) {
        this.vendor = vendor
        return this
    }

    DatabaseConfig setPassword(String password) {
        this.password = password
        return this
    }

    String getJdbcUrl() {
        String jdbcUrl = "jdbc:jtds:${vendor}://${server}:1433"
        if (database) {
            jdbcUrl += "/${database}"
        }

        return jdbcUrl
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new DatabaseConfig()
                        .setServer(server)
                        .setDatabase(database)
                        .setUser(user)
                        .setPassword(password)
    }
}
