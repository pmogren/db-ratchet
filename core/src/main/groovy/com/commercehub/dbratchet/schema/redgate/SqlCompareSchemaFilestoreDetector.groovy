package com.commercehub.dbratchet.schema.redgate

import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.SchemaDifferenceEngine
import com.commercehub.dbratchet.schema.SchemaFilestoreDetector

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/19/13
 * Time: 12:59 PM
 */
class SqlCompareSchemaFilestoreDetector implements SchemaFilestoreDetector {
    @Override
    Class<? extends SchemaDifferenceEngine> getSchemaDifferenceEngineClass() {
        return SqlCompareSchemaDifferenceEngine
    }

    @Override
    boolean isPresent(SchemaConfig schemaConfig) {
        File schemaStoreDir = new File(schemaConfig.rootDir, SqlCompareSchemaDifferenceEngine.SCHEMA_DIR)
        return schemaStoreDir.exists() && schemaStoreDir.isDirectory()
    }
}
