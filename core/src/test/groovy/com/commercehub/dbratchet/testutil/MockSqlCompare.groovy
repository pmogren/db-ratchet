package com.commercehub.dbratchet.testutil

import com.commercehub.dbratchet.schema.redgate.SqlCompare

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/4/13
 * Time: 5:00 PM
 */
class MockSqlCompare extends SqlCompare {
    MockSqlCompareFactory parentFactory

    MockSqlCompare(MockSqlCompareFactory factory) {
        super('')
        parentFactory = factory
    }

    boolean run() {
        parentFactory.logCommand(constructCommandString())
        return true
    }

}
