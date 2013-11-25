package com.commercehub.dbratchet.schema

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/19/13
 * Time: 3:43 PM
 */
class MissingSchemaDifferenceEngineException extends RuntimeException {
    MissingSchemaDifferenceEngineException() {
        super()
    }

    MissingSchemaDifferenceEngineException(String message) {
        super(message)
    }

    MissingSchemaDifferenceEngineException(String message, Throwable cause) {
        super(message, cause)
    }

    MissingSchemaDifferenceEngineException(Throwable cause) {
        super(cause)
    }
}
