package com.commercehub.dbratchet

import com.commercehub.dbratchet.schema.PresentFilestoreSchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaDifferenceEngine
import com.commercehub.dbratchet.schema.SchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaConfig

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 2:42 PM
 */
class PullOperation implements Operation {
    final String name = 'Pull'

    private final SchemaConfig schemaConfig
    DatabaseConfig dbConfig

    PullOperation(SchemaConfig schemaConfig, DatabaseConfig dbConfig) {
        this.schemaConfig = schemaConfig
        this.dbConfig = dbConfig
    }

    @Override
    boolean run() {
        SchemaDifferenceEngineFactory sdeFactory = new PresentFilestoreSchemaDifferenceEngineFactory()
        SchemaDifferenceEngine sde = sdeFactory.getSchemaDifferenceEngine(schemaConfig)

        sde.setSourceDatabase(dbConfig)
        sde.useFileStoreAsTarget()
        sde.pushSourceToTarget()

        return true
    }

    @Override
    boolean isConfigured() {
        if (!dbConfig.server) {
            println 'Missing SERVER'
            return false
        }

        if (!dbConfig.database) {
            println 'Missing DATABASE'
            return false
        }

        if (dbConfig.user && !dbConfig.password) {
            println 'Missing PASSWORD'
            return false
        }

        return true
    }
}
