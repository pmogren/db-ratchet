package com.commercehub.dbratchet.util

import com.commercehub.dbratchet.DatabaseConfig
import groovy.sql.Sql

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/17/13
 * Time: 5:05 PM
 */
@SuppressWarnings('CatchException')
class GroovySqlRunner implements SqlRunner {
    public static final String JDBC_DRIVER_CLASS = 'net.sourceforge.jtds.jdbc.Driver'
    static {
        ClassLoader.systemClassLoader.loadClass(JDBC_DRIVER_CLASS)
    }

    @Override
    boolean runScript(DatabaseConfig dbConfig, File scriptFile) {
        Sql sql = getSql(dbConfig)
        try {
            parseScriptIntoTransactions(scriptFile).each { sqlString->
                sql.execute(sqlString)
            }
        } catch (Exception e) {
            System.err.println "Error running SQL Script: ${scriptFile}"
            System.err.println 'Because SQL scripts can manage their own transactions,' +
                    ' this script may have been partially applied.'
            e.printStackTrace()
            return false
        } finally {
            sql.close()
        }

        return true
    }

    @Override
    boolean runScript(DatabaseConfig dbConfig, String scriptFilePath) {
        runScript(dbConfig, new File(scriptFilePath))
    }

    @Override
    boolean runCommand(DatabaseConfig dbConfig, String sqlString) {
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

    static Sql getSql(DatabaseConfig dbConfig) {
        if (dbConfig.user) {
            return Sql.newInstance(dbConfig.jdbcUrl, dbConfig.user, dbConfig.password, JDBC_DRIVER_CLASS)
        }

        return Sql.newInstance(dbConfig.jdbcUrl, JDBC_DRIVER_CLASS)
    }

   def parseScriptIntoTransactions(File sqlFile) {
        def commandList = []
        String currentCommand = ''
        sqlFile.eachLine { line->
            if ('GO' == (line)) {
                commandList.add(currentCommand)
                currentCommand = ''
            } else {
                currentCommand = "${currentCommand}${line}\n"
            }
        }

        return commandList
    }
}
