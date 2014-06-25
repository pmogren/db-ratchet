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
@SuppressWarnings('DuplicateStringLiteral')
class SqlScriptRunner {

    static final String BASE_COUNT_QUERY = 'Select count(1) from '

    static boolean runScript(DatabaseConfig dbConfig, File scriptFile) {
        Sql sql = getSql(dbConfig)
        try {
            parseScriptIntoTransactions(scriptFile).each { sqlString ->
                sql.execute(sqlString)
            }
        } catch (Exception e) {
            System.err.println "Error running SQL Script: ${scriptFile.absolutePath}"
            System.err.println 'Because SQL scripts can manage their own transactions,' +
                    ' this script may have been partially applied.'
            e.printStackTrace()
            return false
        } finally {
            sql.close()
        }

        return true
    }

    static boolean runScript(DatabaseConfig dbConfig, String scriptFilePath) {
        runScript(dbConfig, new File(scriptFilePath))
    }

    static boolean runScript(DatabaseConfig dbConfig, InputStream scriptContents) {
        Sql sql = getSql(dbConfig)
        try {
            parseScriptIntoTransactions(scriptContents).each { sqlString ->
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
        Sql sql = getSql(dbConfig)
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

    static runCountQuery(DatabaseConfig dbConfig, String table) {
        Sql sql = getSql(dbConfig)

        try {
            return sql.firstRow(BASE_COUNT_QUERY + table)[0]
        } catch (Exception e) {
            System.err.println "Error running SQL Commmand: ${BASE_COUNT_QUERY + table}"
            e.printStackTrace()
        }

        return 0
    }

    private static Sql getSql(DatabaseConfig dbConfig) {
        return DatabaseClientFactory.getDatabaseClient(dbConfig.vendor).getSql(dbConfig)
    }

    private static List<String> parseScriptIntoTransactions(File scriptFile) {
        List<String> commandList = [] as Queue<String>
        String currentCommand = ''
        scriptFile.eachLine { line ->
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

    private static List<String> parseScriptIntoTransactions(InputStream sqlInputStream) {
        List<String> commandList = [] as Queue<String>
        String currentCommand = ''
        sqlInputStream.eachLine { line ->
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
