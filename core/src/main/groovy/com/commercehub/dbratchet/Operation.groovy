package com.commercehub.dbratchet

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 10:41 AM
 */
interface Operation {
    String getName()
    boolean run()
    boolean isConfigured()
}
