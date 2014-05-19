package com.commercehub.dbratchet.data

import com.commercehub.dbratchet.filestore.FileStore

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/25/13
 * Time: 3:00 PM
 */
class DataPackageConfig {
    List<DataPackage> packages = [] as Queue<DataPackage>

    void add(DataPackage dataPackage) {
        packages.add(dataPackage)
    }

    static DataPackageConfig load() {
        return load(false)
    }

    static DataPackageConfig load(FileStore fileStore) {
        DataPackageConfig dataPackageConfig = new DataPackageConfig()
        def xmlConfig = new XmlSlurper().parse(fileStore.getFileInputStream('data/data-packages.xml'))
        xmlConfig.package.each { packageElement->
            DataPackage dataPackage = new DataPackage(fileStore)
            if (packageElement.@name.size() > 0) {
                dataPackage.setName(packageElement.@name.text())
            }

            if (packageElement.@table.size() > 0) {
                dataPackage.tables.add(packageElement.@table.text())
                if (!dataPackage.name) {
                    dataPackage.setName(packageElement.@table.text())
                }
            }

            packageElement.table.each { tableElement->
                dataPackage.tables.add(tableElement.text())
            }

            dataPackageConfig.add(dataPackage)
        }
        return dataPackageConfig
    }
}
