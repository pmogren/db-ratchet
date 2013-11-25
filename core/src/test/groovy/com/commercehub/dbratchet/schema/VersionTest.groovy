package com.commercehub.dbratchet.schema

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 11:57 AM
 */
class VersionTest extends GroovyTestCase {
    void testVersionParsing() {
        assert new Version('001.0000.000000') == new Version(1,0,0)
    }

    void testPointVersionIncrement() {
        Version v = new Version(1,0,0)
        v.incrementPointVersion()
        assert v == new Version(1,0,1)
    }

    void testMinorVersionIncrement() {
        Version v = new Version(1,0,1)
        v.incrementMinorVersion()
        assert v == new Version(1,1,0)
    }

    void testMajorVersionIncrement() {
        Version v = new Version(1,0,0)
        v.incrementMajorVersion()
        assert v == new Version(2,0,0)
    }

    void testCopyConstructor() {
        Version v1 = new Version(1,0,0)
        Version v2 = new Version(v1)
        assert v1 == v2
        assert !v1.is(v2)
    }

    void testToString() {
        Version v = new Version(1,0,10)
        assert v.toString() == '001.0000.000010'
    }

    void testEquals() {
        assert new Version(1, 0, 0) == new Version(1, 0, 0)
        assert new Version(1, 0, 0) != new Version(2, 0, 0)
        assert new Version(1, 0, 0) != new Version(1, 1, 0)
        assert new Version(1, 0, 0) != new Version(1, 0, 1)
    }

    void testEqualsContract() {
        EqualsVerifier.forClass(Version)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify()
    }
}
