package com.commercehub.dbratchet

import org.junit.Test

/**
 * Created by jaystgelais on 5/24/14.
 */
class DatabaseConfigTest {

    @Test
    void testIsValidServerConfig() {
        assert !(new DatabaseConfig().isValidServerConfig())
        assert (new DatabaseConfig().setServer('someserver').isValidServerConfig())
        assert !(new DatabaseConfig().setServer('someserver').setUser('someuser').isValidServerConfig())
        assert (new DatabaseConfig().setServer('someserver').setUser('someuser').setPassword('somepassowrd')
                .isValidServerConfig())
    }

    @Test
    void testIsValidDatabaseConfig() {
        assert !(new DatabaseConfig().setServer('someserver').isValidDatabaseConfig())
        assert (new DatabaseConfig().setServer('someserver').setDatabase('mydb').isValidDatabaseConfig())
    }
}
