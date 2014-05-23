package com.commercehub.dbratchet

import com.commercehub.dbratchet.schema.redgate.SqlCompareFactory

/**
 * Created by jaystgelais on 5/22/14.
 */
@SuppressWarnings('PropertyName')
class TestConfig {
    static final boolean isRedgateAvailable
    static final boolean isMSSQLAvailable

    static {
        isRedgateAvailable = new SqlCompareFactory().newSqlCompare()
        isMSSQLAvailable = false
    }
}
