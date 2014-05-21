package com.commercehub.dbratchet.data

import com.commercehub.dbratchet.DatabaseConfig

/**
 * Created by jaystgelais on 5/20/14.
 */
interface DataMigrator {
    void migratePackage(DatabaseConfig dbConfig, DataPackage dataPackage)
}