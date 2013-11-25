package com.commercehub.dbratchet

import com.commercehub.dbratchet.schema.DeclarativeSchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaDifferenceEngine
import com.commercehub.dbratchet.schema.SchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 3:06 PM
 */
class InitOperation implements Operation {
    final String name = 'Init'

    private final SchemaConfig schemaConfig
    private final SchemaDifferenceEngineFactory sdeFactory

    InitOperation(SchemaConfig schemaConfig, String schemaDiffEngine) {
        this.schemaConfig = schemaConfig
        sdeFactory = new DeclarativeSchemaDifferenceEngineFactory(schemaDiffEngine)
    }

    @Override
    boolean run() {
        boolean returnVal = initSchemaStore()
        mkdir(Version.VERSIONS_DIR)
        mkdir(SchemaConfig.DATA_DIR)
        generateDataPackagesXml()
        return returnVal
    }

    @Override
    boolean isConfigured() {
        if (!sdeFactory) {
            return false
        }

        return true
    }

    boolean initSchemaStore() {
        SchemaDifferenceEngine sde = sdeFactory.getSchemaDifferenceEngine(schemaConfig)
        sde.initializeSchemaStore()

        return true
    }

    boolean generateDataPackagesXml() {
        File destFile = new File(SchemaConfig.DATA_DIR, 'data-packages.xml')
        destFile.createNewFile()

        this.getClass().getResource('/templates/data-packages.xml').withInputStream { is->
            destFile << is
        }
    }

    private static void mkdir(String dirName) {
        new File(dirName).mkdir()
    }
}
