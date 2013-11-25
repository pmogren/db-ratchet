package com.commercehub.dbratchet.schema

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/16/13
 * Time: 1:12 PM
 */
interface SchemaDifferenceEngineFactory {
    SchemaDifferenceEngine getSchemaDifferenceEngine(SchemaConfig schemaConfig)
}