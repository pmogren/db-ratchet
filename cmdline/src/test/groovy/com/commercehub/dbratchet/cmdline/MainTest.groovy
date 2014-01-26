package com.commercehub.dbratchet.cmdline

import com.commercehub.dbratchet.Operation

/**
 * Created by Brett on 1/25/14.
 */
class MainTest extends GroovyTestCase {

    private final main = new Main()
    private final emptyArgs = []

    void setUp() {
        super.setUp()
        main.metaClass.printedValue = ''
        main.metaClass.println = { Object val ->  printedValue += val }
        System.err.metaClass.printedValue = ''
        System.err.metaClass.println = { String val ->  printedValue += val }
    }

    void tearDown() {
        super.tearDown()
        main.printedValue = ''
        System.err.printedValue = ''
    }

    void testRunWithNullCommand() {
        main.run(null, emptyArgs)
        assert main.printedValue == main.usageText
    }

    void testRunWithInvalidCommand() {
        def invalid = 'invalid'
        main.run(invalid, emptyArgs)
        assert main.printedValue == main.unrecognizedCommand(invalid) + main.usageText
    }

    void testRunWithValidCommand() {
        main.metaClass.operationPerformed = false
        main.metaClass.performOperation = { Operation operation -> operationPerformed = true }
        main.run('init', emptyArgs)
        assert main.operationPerformed
    }

    void testPerformOperationWithNullOperation() {
        main.performOperation(null)
        assert main.printedValue == ''
    }

    void testPerformOperationWithMisconfiguredOperation() {
        Operation operation = new MisconfiguredOperation()
        main.performOperation(operation)
        assert System.err.printedValue == main.misconfiguredOperation(operation)
    }

    void testPerformOperationWithValidOperation() {
        Operation operation = new ConfiguredOperation()
        main.performOperation(operation)
        assert main.printedValue == main.successfulOperation(operation)
    }

    void testPerformOperationWithInvalidOperation() {
        Operation operation = new InvalidConfiguredOperation()
        main.performOperation(operation)
        assert System.err.printedValue == main.unsuccessfulOperation(operation)
    }

    private abstract class TestOperation implements Operation {
        final String name = 'Test'

        @Override
        boolean run() {
            return true
        }

        @Override
        abstract boolean isConfigured()
    }

    private class MisconfiguredOperation extends TestOperation {
        @Override
        boolean isConfigured() {
            return false
        }
    }

    private class ConfiguredOperation extends TestOperation {
        @Override
        boolean isConfigured() {
            return true
        }
    }

    private class InvalidConfiguredOperation extends ConfiguredOperation {
        @Override
        boolean run() {
            return false
        }
    }


}
