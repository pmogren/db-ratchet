package com.commercehub.dbratchet.data

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/25/13
 * Time: 1:51 PM
 */
class DataPackage {
    String name
    boolean isOnClassPath
    List<String> tables = [] as Queue<String>

    def getDataFile() {
        if (isOnClassPath) {
            return DataPackage.getResourceAsStream("/data/packages/${name}.xml")
        }
        return new File('./data/packages', "${name}.xml")
    }
}
