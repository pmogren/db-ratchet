package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseClient
import com.commercehub.dbratchet.databases.DatabaseClientFactory
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
    private final DatabaseConfig dbServerConfig
    private final SchemaDifferenceEngineFactory schemaDifferenceEngineFactory
    private final PUBLISH_TYPE publishType
    private final DatabaseClient databaseClient

    PublishOperation(SchemaConfig schemaConfig, DatabaseConfig dbServerConfig) {
        this(schemaConfig, dbServerConfig, PUBLISH_TYPE.POINT)
    }

    PublishOperation(SchemaConfig schemaConfig, DatabaseConfig dbServerConfig, PUBLISH_TYPE publishType) {
        this.schemaConfig = schemaConfig
        this.dbServerConfig = dbServerConfig
        this.publishType = publishType

        databaseClient = DatabaseClientFactory.getDatabaseClient(dbServerConfig.vendor)
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
            File outputScriptFile = schemaConfig.getVersionFullBuildScriptFile(version)
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
        DatabaseConfig dbConfig = getDatabaseConfgOnServer(dbServerConfig, generateThrowAwayDatabaseName())
        boolean hasDatabaseBeenCreated = databaseClient.createDatabase(dbConfig)
        if (!hasDatabaseBeenCreated) {
            System.err.println "Unable to create database [${dbConfig.database}] on server [${dbConfig.server}]"
            return false
        }

        try {
            comparisonAction.call(dbConfig)
        } finally {
            if (hasDatabaseBeenCreated) {
                if (!databaseClient.deleteDatabase(dbConfig)) {
                    System.err.println 'ERROR: Unable to clean up database ' +
                            "[${dbConfig.server}.${dbConfig.database}]. " +
                            'Please make sure to clean this up manually'
                }
            }
        }

        return returnVal
    }

    boolean generateUpgradeScript(Version version, Version previousVersion) {
        return performComparisonWithTransientDB { dbConfigWithDatabase->
            applyFullBuildScriptToDatabase(previousVersion, dbConfigWithDatabase)
            File outputScriptFile = schemaConfig.getVersionUpgradeScriptFile(version)
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
            File outputScriptFile = schemaConfig.getVersionRollbackScriptFile(version)
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
        File outputScriptFile = schemaConfig.getVersionUpgradeScriptFile(version)
        outputScriptFile.createNewFile()
        outputScriptFile.text = schemaConfig.getVersionFullBuildScriptFile(version).text

        return true
    }

    void applyFullBuildScriptToDatabase(Version version, DatabaseConfig dbConfigWithDatabase) {
        SqlScriptRunner.runScript(dbConfigWithDatabase, schemaConfig.getVersionFullBuildScriptFile(version))
    }

    DatabaseConfig getDatabaseConfgOnServer(DatabaseConfig databaseServerConfig, String dbName) {
        return new DatabaseConfig()
                .setVendor(databaseServerConfig.vendor)
                .setServer(dbServerConfig.server)
                .setDatabase(dbName)
                .setUser(dbServerConfig.user)
                .setPassword(dbServerConfig.password)
    }

    static String generateThrowAwayDatabaseName() {
        String uuid = UUID.randomUUID() as String
        String uniqPart = uuid.replaceAll('-', '')
        return "ratchet_${uniqPart}"
    }

    @Override
    boolean isConfigured() {
        return dbServerConfig.isValidServerConfig()
    }
}
