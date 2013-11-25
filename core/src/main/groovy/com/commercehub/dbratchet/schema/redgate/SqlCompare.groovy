package com.commercehub.dbratchet.schema.redgate

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.util.Cmd

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 1:49 PM
 */
class SqlCompare {
    String sqlCompareCmd
    String filter
    String scriptFile
    boolean doSynch = false
    boolean doMakeScripts = false

    DatabaseConfig srcDatabaseConfig
    String srcScripts

    DatabaseConfig targetDatabaseConfig
    String targetScripts

    SqlCompare(String cmdString) {
        sqlCompareCmd = cmdString
    }

    boolean run() {
        return (new Cmd(constructCommandString())).run()
    }

    protected GString constructCommandString() {
        "${sqlCompareCmd} ${switches}"
    }

    String getSwitches() {
        String switches = ''

        if (srcDatabaseConfig) {
            if (srcDatabaseConfig.database) {
                switches += "/Database1:${srcDatabaseConfig.database} "
            }

            if (srcDatabaseConfig.server) {
                switches += "/Server1:${srcDatabaseConfig.server} "
            }

            if (srcDatabaseConfig.user) {
                switches += "/Username1:${srcDatabaseConfig.user} "
            }

            if (srcDatabaseConfig.password) {
                switches += "/Password1:${srcDatabaseConfig.password} "
            }
        }

        if (srcScripts) {
            switches += "/Scripts1:${srcScripts} "
        }

        if (targetDatabaseConfig) {
            if (targetDatabaseConfig.database) {
                switches += "/Database2:${targetDatabaseConfig.database} "
            }

            if (targetDatabaseConfig.server) {
                switches += "/Server2:${targetDatabaseConfig.server} "
            }

            if (targetDatabaseConfig.user) {
                switches += "/Username2:${targetDatabaseConfig.user} "
            }

            if (targetDatabaseConfig.password) {
                switches += "/Password2:${targetDatabaseConfig.password} "
            }
        }

        if (targetScripts) {
            if (doMakeScripts) {
                switches += "/MakeScripts:${targetScripts} "
            } else {
                switches += "/Scripts2:${targetScripts} "
            }
        }

        if (scriptFile) {
            switches += "/ScriptFile:${scriptFile} "
        }

        if (doSynch) {
            switches += '/synch '
        }

        switches +=  "/Force /Filter:${filter}"
        return switches
    }


    SqlCompare setFilter(String filter) {
        this.filter = filter
        return this
    }

    SqlCompare setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile
        return this
    }

    SqlCompare setDoSynch(boolean doSynch) {
        this.doSynch = doSynch
        return this
    }

    SqlCompare setDoMakeScripts(boolean doMakeScripts) {
        this.doMakeScripts = doMakeScripts
        return this
    }

    SqlCompare setSrcDatabaseConfig(DatabaseConfig srcDatabaseConfig) {
        this.srcDatabaseConfig = srcDatabaseConfig
        return this
    }

    SqlCompare setSrcScripts(String srcScripts) {
        this.srcScripts = srcScripts
        return this
    }

    SqlCompare setTargetDatabaseConfig(DatabaseConfig targetDatabaseConfig) {
        this.targetDatabaseConfig = targetDatabaseConfig
        return this
    }

    SqlCompare setTargetScripts(String targetScripts) {
        this.targetScripts = targetScripts
        return this
    }

}
