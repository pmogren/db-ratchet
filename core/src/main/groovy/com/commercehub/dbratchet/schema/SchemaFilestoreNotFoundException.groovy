package com.commercehub.dbratchet.schema

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/19/13
 * Time: 1:05 PM
 */
class SchemaFilestoreNotFoundException extends RuntimeException {
    SchemaFilestoreNotFoundException() {
        super()
    }

    SchemaFilestoreNotFoundException(String message) {
        super(message)
    }

    SchemaFilestoreNotFoundException(String message, Throwable cause) {
        super(message, cause)
    }

    SchemaFilestoreNotFoundException(Throwable cause) {
        super(cause)
    }
}
