package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.schema.redgate.SqlCompareSchemaDifferenceEngine
import com.commercehub.dbratchet.schema.redgate.SqlCompareSchemaFilestoreDetector

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/19/13
 * Time: 11:42 AM
 */
class SchemaDifferenceEngineRegistry {
    private static final Map<String, Class<? extends SchemaDifferenceEngine>> ENGINE_MAP = [
            (SqlCompareSchemaDifferenceEngine.ENGINE_NAME): SqlCompareSchemaDifferenceEngine
    ]

    private static final List<SchemaFilestoreDetector> FILESTORE_DETECTORS = [
            new SqlCompareSchemaFilestoreDetector()
    ]

    static Class<? extends SchemaDifferenceEngine> getSchemaDifferenceEngineClass(String type) {
        Class<? extends SchemaDifferenceEngine> schemaDifferenceEngineClass = ENGINE_MAP.get(type)
        if (!schemaDifferenceEngineClass) {
            throw new MissingSchemaDifferenceEngineException('No SchemaDifferenceEngine ' +
                    "implementation found for ${type}")
        }
        return schemaDifferenceEngineClass
    }

    static Class<? extends SchemaDifferenceEngine> getSchemaDifferenceEngineClass(SchemaConfig schemaConfig) {
        for (SchemaFilestoreDetector detector : FILESTORE_DETECTORS) {
            if (detector.isPresent(schemaConfig)) {
                return detector.schemaDifferenceEngineClass
            }
        }
        throw new SchemaFilestoreNotFoundException()
    }
}
