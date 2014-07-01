package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseVendor

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/17/13
 * Time: 11:25 AM
 */
class DatabaseConfig implements Cloneable {
    DatabaseVendor vendor = DatabaseVendor.MSSQL
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

    DatabaseConfig setVendor(DatabaseVendor vendor) {
        this.vendor = vendor
        return this
    }

    DatabaseConfig setPassword(String password) {
        this.password = password
        return this
    }

    DatabaseConfig getServerConfig() {
        return ((DatabaseConfig) clone()).setDatabase(null)
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new DatabaseConfig()
                        .setServer(server)
                        .setDatabase(database)
                        .setUser(user)
                        .setPassword(password)
                        .setVendor(vendor)
    }

    boolean isValidServerConfig() {
       return server && (!user || (user && password))
    }

    boolean isValidDatabaseConfig() {
        return isValidServerConfig() && database
    }

    @Override
    String toString() {
        return "server: $server | vendor: $vendor | database: $database | user: $user | password: $password"
    }

    @Override
    @SuppressWarnings('IfStatementBraces')
    @SuppressWarnings('IfStatementCouldBeTernary')
    @SuppressWarnings('UnnecessaryIfStatement')
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        DatabaseConfig that = (DatabaseConfig) o

        if (database != that.database) return false
        if (password != that.password) return false
        if (server != that.server) return false
        if (user != that.user) return false
        if (vendor != that.vendor) return false

        return true
    }

    @Override
    @SuppressWarnings('DuplicateNumberLiteral')
    int hashCode() {
        int result
        result = (vendor != null ? vendor.hashCode() : 0)
        result = 31 * result + (server != null ? server.hashCode() : 0)
        result = 31 * result + (database != null ? database.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (password != null ? password.hashCode() : 0)
        return result
    }
}
