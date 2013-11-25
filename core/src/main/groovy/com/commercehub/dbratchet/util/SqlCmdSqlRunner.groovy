package com.commercehub.dbratchet.util

import com.commercehub.dbratchet.DatabaseConfig

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/17/13
 * Time: 5:04 PM
 */
class SqlCmdSqlRunner implements SqlRunner {

    @Override
    boolean runScript(DatabaseConfig dbConfig, File scriptFile) {
        return runScript(dbConfig, scriptFile.absolutePath)
    }

    @Override
    boolean runScript(DatabaseConfig dbConfig, String scriptFilePath) {
        return new Cmd(getSqlCmdWithInputSwitch(dbConfig, 'i', scriptFilePath)).run()
    }

    @Override
    boolean runCommand(DatabaseConfig dbConfig, String sqlString) {
        return new Cmd(getSqlCmdWithInputSwitch(dbConfig, 'Q', sqlString), false).run()
    }

    private String getSqlCmdWithInputSwitch(DatabaseConfig dbConfig, String cmdSwitch, String value) {
        return "sqlcmd ${getDbConfigSwitches(dbConfig)} -${cmdSwitch} \"${value}\""
    }

    private String getDbConfigSwitches(DatabaseConfig dbConfig) {
        String returnStr = ''

        if (dbConfig.server) {
            returnStr += "-S ${dbConfig.server} "
        }

        if (dbConfig.database) {
            returnStr += "-d ${dbConfig.database} "
        }

        if (dbConfig.user) {
            returnStr += "-U ${dbConfig.user} "
        }

        if (dbConfig.password) {
            returnStr += "-P ${dbConfig.password} "
        }

        return returnStr
    }
}
