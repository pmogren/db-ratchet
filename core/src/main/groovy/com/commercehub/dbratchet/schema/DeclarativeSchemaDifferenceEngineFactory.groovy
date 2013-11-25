package com.commercehub.dbratchet.schema

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/19/13
 * Time: 11:17 AM
 */
class DeclarativeSchemaDifferenceEngineFactory implements SchemaDifferenceEngineFactory {
    String declaredEngine

    DeclarativeSchemaDifferenceEngineFactory(String declaredEngine) {
        this.declaredEngine = declaredEngine
    }

    @Override
    SchemaDifferenceEngine getSchemaDifferenceEngine(SchemaConfig schemaConfig) {
        return SchemaDifferenceEngineRegistry.getSchemaDifferenceEngineClass(declaredEngine).newInstance(schemaConfig)
    }
}
