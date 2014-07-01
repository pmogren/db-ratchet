package com.commercehub.dbratchet.databases

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.data.DataPackage

/**
 * Created by jaystgelais on 5/20/14.
 */
interface DataMigrator {
    void migratePackage(DatabaseConfig dbConfig, DataPackage dataPackage)
}
