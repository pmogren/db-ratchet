package com.commercehub.dbratchet.data

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

    static DataPackageConfig load(boolean isDataOnClasspath) {
        DataPackageConfig dataPackageConfig = new DataPackageConfig()
        def xmlConfig = new XmlSlurper().parse(getDataPackageFile(isDataOnClasspath))
        xmlConfig.package.each { packageElement->
            DataPackage dataPackage = new DataPackage()
            dataPackage.isOnClassPath = isDataOnClasspath
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

    static getDataPackageFile(boolean isDataOnClasspath) {
        if (isDataOnClasspath) {
            return DataPackageConfig.getResourceAsStream('/data/data-packages.xml')
        }

        new File('./data/packages').mkdir()
        return new File('./data/data-packages.xml')
    }
}
