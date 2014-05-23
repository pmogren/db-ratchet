package com.commercehub.dbratchet.schema.redgate

import com.commercehub.dbratchet.util.Cmd

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 2:14 PM
 */
class SqlCompareFactory {
    private static final String SQL_COMPARE_EXE = 'SQLCompare.exe'
    private static final String SQLCOMPARE_HOME = 'SQLCOMPARE_HOME'
    private static final String DEFAULT_INSTALL_LOCATION = 'C:\\Program Files (x86)\\Red Gate\\SQL Compare 10\\'

    String sqlCompareCmd
    String sqlCompareHome

    SqlCompareFactory() {
        sqlCompareCmd = locateSqlCompareCmd()
    }

    SqlCompare newSqlCompare() {
        return (sqlCompareCmd) ? new SqlCompare(sqlCompareCmd) : null
    }

    String locateSqlCompareCmd() {
        if (isSqlCompareOnPath()) {
            return SQL_COMPARE_EXE
        }

        if (isSqlCompareHomeEnvVariableSet()) {
            return "${sqlCompareHome}${File.separator}${SQL_COMPARE_EXE}"
        }

        if (isSqlCompareAtDefaultInstallLocation()) {
            return "${DEFAULT_INSTALL_LOCATION}${SQL_COMPARE_EXE}"
        }

        return null
    }

    boolean isSqlCompareOnPath() {
        return isSqlCompareHere('')
    }

    boolean isSqlCompareHomeEnvVariableSet() {
        sqlCompareHome = System.getenv(SQLCOMPARE_HOME)
        if (!sqlCompareHome) {
            return false
        }
        return isSqlCompareHere("${sqlCompareHome}${File.separator}")
    }

    boolean isSqlCompareAtDefaultInstallLocation() {
        return isSqlCompareHere(DEFAULT_INSTALL_LOCATION)
    }

    boolean isSqlCompareHere(String location) {
        return (new Cmd("${location}${SQL_COMPARE_EXE} /help", false)).run()
    }
}
