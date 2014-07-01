package com.commercehub.dbratchet

import com.commercehub.dbratchet.data.DataPackage
import com.commercehub.dbratchet.data.DataPackageConfig
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.filestore.FileStore
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.database.QueryDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet

import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.Level

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/25/13
 * Time: 1:50 PM
 */
class CaptureOperation implements Operation {
    final String name = 'Capture'

    DatabaseConfig dbConfig
    FileStore fileStore

    CaptureOperation(DatabaseConfig dbConfig, FileStore fileStore) {
        this.dbConfig = dbConfig
        this.fileStore = fileStore
    }

    @Override
    boolean run() {
        silenceDbUnitLogger()
        DataPackageConfig dataPackageConfig = DataPackageConfig.load(fileStore)
        dataPackageConfig.packages.each { dataPackage ->
            capturePackage(dataPackage)
        }

        return true
    }

    @Override
    boolean isConfigured() {
        return dbConfig.isValidDatabaseConfig()
    }

    private void silenceDbUnitLogger() {
        Logger logger = (Logger) LoggerFactory.getLogger('org.dbunit')
        logger.setLevel(Level.OFF)
    }

    private void capturePackage(DataPackage dataPackage) {
        IDatabaseConnection connection = databaseConnection
        QueryDataSet partialDataSet = new QueryDataSet(connection)
        dataPackage.tables.each { tableName ->
            partialDataSet.addTable("${tableName}", "SELECT * FROM ${tableName}")
        }
        FlatXmlDataSet.write(partialDataSet, dataPackage.dataOutputStream)
    }

    private IDatabaseConnection getDatabaseConnection() {
        return new DatabaseConnection(DatabaseClientFactory.getDatabaseClient(dbConfig.vendor)
                .getSql(dbConfig).connection)
    }
}
