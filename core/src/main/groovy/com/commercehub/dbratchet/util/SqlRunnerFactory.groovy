package com.commercehub.dbratchet.util

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/18/13
 * Time: 1:06 PM
 */
class SqlRunnerFactory {
    SqlRunner getSqlRunner() {
        return new GroovySqlRunner()
    }
}
