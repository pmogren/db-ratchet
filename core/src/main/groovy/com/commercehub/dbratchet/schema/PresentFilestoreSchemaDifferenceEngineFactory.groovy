package com.commercehub.dbratchet.schema

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/19/13
 * Time: 1:11 PM
 */
class PresentFilestoreSchemaDifferenceEngineFactory implements SchemaDifferenceEngineFactory {
    @Override
    SchemaDifferenceEngine getSchemaDifferenceEngine(SchemaConfig schemaConfig) {
        return SchemaDifferenceEngineRegistry.getSchemaDifferenceEngineClass(schemaConfig).newInstance(schemaConfig)
    }
}
