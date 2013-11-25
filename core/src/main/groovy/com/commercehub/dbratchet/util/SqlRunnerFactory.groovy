package com.commercehub.dbratchet.util

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/18/13
 * Time: 1:06 PM
 */
class SqlRunnerFactory {
    boolean isSqlCmdAvailable

    SqlRunnerFactory() {
        isSqlCmdAvailable = checkForSqlCmd()
    }

    SqlRunner getSqlRunner() {
        if (isSqlCmdAvailable) {
            return new SqlCmdSqlRunner()
        }

        return new GroovySqlRunner()
    }

    private boolean checkForSqlCmd() {
        return new Cmd('sqlcmd /?', false).run()
    }
}
