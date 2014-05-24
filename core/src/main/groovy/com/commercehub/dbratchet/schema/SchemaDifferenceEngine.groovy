package com.commercehub.dbratchet.schema

import com.commercehub.dbratchet.DatabaseConfig

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/15/13
 * Time: 4:39 PM
 */
interface SchemaDifferenceEngine {
    String getName()
    void setTargetDatabase(DatabaseConfig dbConfig)
    void setSourceDatabase(DatabaseConfig dbConfig)
    void useFileStoreAsSource()
    void useFileStoreAsTarget()
    File getFileStoreDir()
    boolean pushSourceToTarget()
    void generateScriptToBuildSourceToTarget(File script)
    void initializeSchemaStore()
}