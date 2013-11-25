package com.commercehub.dbratchet.testutil

import com.commercehub.dbratchet.schema.redgate.SqlCompare
import com.commercehub.dbratchet.schema.redgate.SqlCompareFactory

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/4/13
 * Time: 4:55 PM
 */
class MockSqlCompareFactory extends SqlCompareFactory {
    List<String> loggedSQLCompareCommands = [] as List<String>
    SqlCompare newSqlCompare() {
        return new MockSqlCompare(this)
    }

    void logCommand(String cmd) {
        loggedSQLCompareCommands.add(cmd)
    }
}
