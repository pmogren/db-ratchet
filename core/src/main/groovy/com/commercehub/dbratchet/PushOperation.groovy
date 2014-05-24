package com.commercehub.dbratchet

import com.commercehub.dbratchet.schema.PresentFilestoreSchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaDifferenceEngine
import com.commercehub.dbratchet.schema.SchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaConfig

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/17/13
 * Time: 12:35 PM
 */
// TODO Write Unit Test
class PushOperation  implements Operation {
    final String name = 'Push'

    private final SchemaConfig schemaConfig
    DatabaseConfig dbConfig

    PushOperation(SchemaConfig schemaConfig, DatabaseConfig dbConfig) {
        this.schemaConfig = schemaConfig
        this.dbConfig = dbConfig
    }

    @Override
    boolean run() {
        SchemaDifferenceEngineFactory sdeFactory = new PresentFilestoreSchemaDifferenceEngineFactory()
        SchemaDifferenceEngine sde = sdeFactory.getSchemaDifferenceEngine(schemaConfig)
        sde.with {
            setTargetDatabase(dbConfig)
            useFileStoreAsSource()
            pushSourceToTarget()
        }

        return true
    }

    @Override
    boolean isConfigured() {
        return dbConfig.isValidDatabaseConfig()
    }
}
