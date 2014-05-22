package com.commercehub.dbratchet.databases

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.data.DataPackage
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.operation.DatabaseOperation

/**
 * Created by jaystgelais on 5/21/14.
 */
class DbUnitDataMigrator implements DataMigrator {
    @Override
    void migratePackage(DatabaseConfig dbConfig, DataPackage dataPackage) {
        IDatabaseConnection con = getDatabaseConnection(dbConfig)
        IDataSet dataSet = new FlatXmlDataSet(dataPackage.dataInputStream)
        DatabaseOperation.CLEAN_INSERT.execute(con, dataSet)
        con.close()
    }

    private IDatabaseConnection getDatabaseConnection(DatabaseConfig dbConfig) {
        return new DatabaseConnection(DatabaseClientFactory.getDatabaseClient(dbConfig.vendor)
                .getSql(dbConfig).connection)
    }
}
