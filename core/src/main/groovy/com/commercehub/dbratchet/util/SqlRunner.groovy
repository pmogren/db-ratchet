package com.commercehub.dbratchet.util

import com.commercehub.dbratchet.DatabaseConfig

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/17/13
 * Time: 5:02 PM
 */
interface SqlRunner {
    boolean runScript(DatabaseConfig dbConfig, File scriptFile)
    boolean runScript(DatabaseConfig dbConfig, String scriptFilePath)
    boolean runCommand(DatabaseConfig dbConfig, String sqlString)
}