package com.commercehub.dbratchet

import com.commercehub.dbratchet.schema.SchemaConfig

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/22/13
 * Time: 11:52 AM
 */
class StoreOperation implements Operation {
    final String name = 'Store'

    SchemaConfig schemaConfig
    DatabaseConfig dbConfig
    String alias

    StoreOperation(SchemaConfig schemaConfig, String alias, DatabaseConfig dbConfig) {
        this.schemaConfig = schemaConfig
        this.alias = alias
        this.dbConfig = dbConfig
    }

    @Override
    boolean run() {
        ServerCredentialStore credStore = new ServerCredentialStore(schemaConfig)
        credStore.store(alias, dbConfig)
        credStore.save()

        return true
    }

    @Override
    boolean isConfigured() {
        if (!alias || alias == '') {
            return false
        }

        return dbConfig.isValidServerConfig()
    }
}
