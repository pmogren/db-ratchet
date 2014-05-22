package com.commercehub.dbratchet

import com.commercehub.dbratchet.data.DataPackageConfig
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.filestore.FileStore

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/25/13
 * Time: 3:52 PM
 */
class MigrateOperation implements Operation {
    final String name = 'Migrate'

    DatabaseConfig dbConfig
    FileStore fileStore

    MigrateOperation(DatabaseConfig dbConfig, FileStore fileStore) {
        this.dbConfig = dbConfig
        this.fileStore = fileStore
    }

    @Override
    boolean run() {
        Date startTime = new Date()
        DataPackageConfig dataPackageConfig = DataPackageConfig.load(fileStore)
        dataPackageConfig.packages.each  { dataPackage->
            DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).dataMigrator.migratePackage(dbConfig, dataPackage)
        }
        Date endTime = new Date()
        println "TOTAL Migration time =  ${endTime.time - startTime.time} milliseconds."
        return true
    }

    @Override
    boolean isConfigured() {
        if (!dbConfig.server) {
            return false
        }

        if (!dbConfig.database) {
            return false
        }

        if (dbConfig.user && !dbConfig.password) {
            return false
        }

        return true
    }
}


