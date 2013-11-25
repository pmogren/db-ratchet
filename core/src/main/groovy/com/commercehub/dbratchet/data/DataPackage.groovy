package com.commercehub.dbratchet.data

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/25/13
 * Time: 1:51 PM
 */
class DataPackage {
    String name
    List<String> tables = [] as Queue<String>

    File getDataFile() {
        return new File('./data/packages', "${name}.xml")
    }
}
