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
        DataPackageConfig dataPackageConfig = new DataPackageConfig()
        new File('./data/packages').mkdir()
        def xmlConfig = new XmlSlurper().parse(new File('./data/data-packages.xml'))
        xmlConfig.package.each { packageElement->
            DataPackage dataPackage = new DataPackage()
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
