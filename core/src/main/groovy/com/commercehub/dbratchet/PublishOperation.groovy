package com.commercehub.dbratchet

import com.commercehub.dbratchet.schema.PresentFilestoreSchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaDifferenceEngine
import com.commercehub.dbratchet.schema.SchemaDifferenceEngineFactory
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version
import com.commercehub.dbratchet.util.SqlScriptRunner

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/17/13
 * Time: 4:59 PM
 */
class PublishOperation implements Operation {
    static final enum PUBLISH_TYPE { POINT, MINOR, MAJOR }

    final String name = 'Publish'

    private final SchemaConfig schemaConfig
    private final DatabaseConfig dbConfig
    private final SchemaDifferenceEngineFactory schemaDifferenceEngineFactory
    private final PUBLISH_TYPE publishType

    PublishOperation(SchemaConfig schemaConfig, DatabaseConfig dbConfig) {
        this(schemaConfig, dbConfig, PUBLISH_TYPE.POINT)
    }

    PublishOperation(SchemaConfig schemaConfig, DatabaseConfig dbConfig, PUBLISH_TYPE publishType) {
        this.schemaConfig = schemaConfig
        this.dbConfig = dbConfig
        this.publishType = publishType

        schemaDifferenceEngineFactory = new PresentFilestoreSchemaDifferenceEngineFactory()
    }

    @Override
    boolean run() {
        def returnVal = true
        Version version
        switch (publishType) {
            case PUBLISH_TYPE.POINT:
                version = schemaConfig.nextPointVersion
                break
            case PUBLISH_TYPE.MINOR:
                version = schemaConfig.nextMinorVersion
                break
            case PUBLISH_TYPE.MAJOR:
                version = schemaConfig.nextMajorVersion
                break
        }

        returnVal &= generateFullBuildScript(version)

        Version previousVersion = schemaConfig.previousVersion
        if (previousVersion) {
            returnVal &= generateUpgradeScript(version, previousVersion)
            returnVal &= generateRollbackScript(version, previousVersion)
        } else {
            returnVal &= generateUpgradeScriptFromFullBuild(version)
        }

        return returnVal
    }

    boolean generateFullBuildScript(Version version) {
        return performComparisonWithTransientDB { dbConfigWithDatabase->
            File outputScriptFile = version.fullBuildScriptFile
            outputScriptFile.createNewFile()
            SchemaDifferenceEngine sde = schemaDifferenceEngineFactory.getSchemaDifferenceEngine(schemaConfig)
            sde.with {
                setTargetDatabase(dbConfigWithDatabase)
                useFileStoreAsSource()
                generateScriptToBuildSourceToTarget(outputScriptFile)
            }

            println "Full build script saved to ${outputScriptFile.absolutePath}"
        }
    }

    boolean performComparisonWithTransientDB(Closure comparisonAction) {
        boolean returnVal = true
        String dbName = generateThrowAwayDatabaseName()
        boolean hasDatabaseBeenCreated = runCreateDatabaseCommand(dbConfig, dbName)
        if (!hasDatabaseBeenCreated) {
            System.err.println "Unable to create database [${dbName}] on server [${dbConfig.server}]"
            return false
        }

        try {
            comparisonAction.call(dbConfig.serverConfig)
        } finally {
            if (hasDatabaseBeenCreated) {
                if (!dropDatabase(dbConfig, dbName)) {
                    System.err.println "ERROR: Unable to clean up database [${dbConfig.server}.${dbName}]. " +
                            'Please make sure to clean this up manually'
                }
            }
        }

        return returnVal
    }

    boolean generateUpgradeScript(Version version, Version previousVersion) {
        return performComparisonWithTransientDB { dbConfigWithDatabase->
            applyFullBuildScriptToDatabase(previousVersion, dbConfigWithDatabase)
            File outputScriptFile = version.upgradeScriptFile
            outputScriptFile.createNewFile()
            SchemaDifferenceEngine sde = schemaDifferenceEngineFactory.getSchemaDifferenceEngine(schemaConfig)
            sde.with {
                setTargetDatabase(dbConfigWithDatabase)
                useFileStoreAsSource()
                generateScriptToBuildSourceToTarget(outputScriptFile)
            }

            println "Upgrade script saved to ${outputScriptFile.absolutePath}"
        }
    }

    boolean generateRollbackScript(Version version, Version previousVersion) {
        return performComparisonWithTransientDB { dbConfigWithDatabase->
            applyFullBuildScriptToDatabase(previousVersion, dbConfigWithDatabase)
            File outputScriptFile = version.rollbackScriptFile
            outputScriptFile.createNewFile()
            SchemaDifferenceEngine sde = schemaDifferenceEngineFactory.getSchemaDifferenceEngine(schemaConfig)
            sde.with {
                setSourceDatabase(dbConfigWithDatabase)
                useFileStoreAsTarget()
                generateScriptToBuildSourceToTarget(outputScriptFile)
            }

            println "Rollback script saved to ${outputScriptFile.absolutePath}"
        }
    }

    boolean generateUpgradeScriptFromFullBuild(Version version) {
        File outputScriptFile = version.upgradeScriptFile
        outputScriptFile.createNewFile()
        outputScriptFile.text = version.fullBuildScriptFile.text

        return true
    }

    void applyFullBuildScriptToDatabase(Version version, DatabaseConfig dbConfigWithDatabase) {
        SqlScriptRunner.runScript(dbConfigWithDatabase, version.fullBuildScriptFile)
    }

    boolean runCreateDatabaseCommand(DatabaseConfig databaseConfig, String dbName) {
        SqlScriptRunner.runCommand(databaseConfig, "create database ${dbName}")
    }

    boolean dropDatabase(DatabaseConfig databaseConfig, String dbName) {
        SqlScriptRunner.runCommand(databaseConfig, "drop database ${dbName}")
    }

    static String generateThrowAwayDatabaseName() {
        String uuid = UUID.randomUUID() as String
        String uniqPart = uuid.replaceAll('-', '')
        return "ratchet_${uniqPart}"
    }

    @Override
    boolean isConfigured() {
        if (!dbConfig.server) {
            return false
        }

        if (dbConfig.user && !dbConfig.password) {
            return false
        }

        return true
    }
}
