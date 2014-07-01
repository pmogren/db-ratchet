package com.commercehub.dbratchet.util

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 10:19 AM
 */
@SuppressWarnings('CatchException')
class Cmd {
    String cmdString
    boolean showOutput = true
    String errorMessage = ''

    Cmd(String cmdString) {
        this.cmdString = cmdString
    }

    Cmd(String cmdString, boolean showOutput) {
        this.cmdString = cmdString
        this.showOutput = showOutput
    }

    boolean run() throws RuntimeException {
        int exitCode = -1
        try {
            def proc = cmdString.execute()
            if (showOutput) {
                proc.consumeProcessOutput(System.out, System.err)
            } else {
                proc.consumeProcessOutput()
            }
            proc.waitFor()

            exitCode = proc.exitValue()
            if (exitCode != 0) {
                errorMessage = "process terminated with ERROR CODE: ${proc.exitValue()}"
            }
        } catch (Exception e) {
            errorMessage = "Process execution threw the following exception: ${e}"
        }

        return (exitCode == 0)
    }
}
