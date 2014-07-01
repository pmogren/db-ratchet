package com.commercehub.dbratchet.schema

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/19/13
 * Time: 12:53 PM
 */
interface SchemaFilestoreDetector {
    Class<? extends SchemaDifferenceEngine> getSchemaDifferenceEngineClass()
    boolean isPresent(SchemaConfig schemaConfig)
}
