package com.commercehub.dbratchet.util

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import groovy.sql.Sql

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/17/13
 * Time: 5:05 PM
 */
@SuppressWarnings('CatchException')
class SqlScriptRunner {

    static boolean runScript(DatabaseConfig dbConfig, File scriptFile) {
        runScript(dbConfig, scriptFile.newInputStream())
    }

    static boolean runScript(DatabaseConfig dbConfig, String scriptFilePath) {
        runScript(dbConfig, new File(scriptFilePath))
    }

    static boolean runScript(DatabaseConfig dbConfig, InputStream scriptContents) {
        Sql sql = DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig)
        try {
            parseScriptIntoTransactions(scriptContents).each { sqlString->
                sql.execute(sqlString)
            }
        } catch (Exception e) {
            System.err.println 'Error running SQL Script'
            System.err.println 'Because SQL scripts can manage their own transactions,' +
                    ' this script may have been partially applied.'
            e.printStackTrace()
            return false
        } finally {
            sql.close()
        }

        return true
    }

    static boolean runCommand(DatabaseConfig dbConfig, String sqlString) {
        Sql sql = DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig)
        try {
            sql.execute(sqlString)
        } catch (Exception e) {
            System.err.println "Error running SQL Commmand: ${sqlString}"
            e.printStackTrace()
            return false
        } finally {
            sql.close()
        }

        return true
    }

    private static List<String> parseScriptIntoTransactions(InputStream sql) {
        List<String> commandList = [] as Queue<String>
        String currentCommand = ''
        sql.eachLine { line->
            if ('GO' == (line)) {
                commandList.add(currentCommand)
                currentCommand = ''
            } else {
                currentCommand = "${currentCommand}${line}\n"
            }
        }

        if (!currentCommand.isEmpty()) {
            commandList.add(currentCommand)
        }

        return commandList
    }

}