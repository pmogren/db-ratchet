package com.commercehub.dbratchet.cmdline

import com.commercehub.dbratchet.Operation

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 3:44 PM
 */
@SuppressWarnings('CatchException')
@SuppressWarnings('SystemExit')
class Main {

    static main(args) {
        String command = null

        if (args.length > 0) {
            command = args[0]
        }

        Main app = new Main()

        try {
            app.run(command, args - command)
        } catch (InvalidOptionsException e) {
            System.exit(1)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    void run(command, args) {
        if (command == null) {
            println usageText
        } else {
            Closure operation = new OperationManager().getOperationToPerform(command)

            if (operation) {
                performOperation(operation.call(args))
            } else {
                println unrecognizedCommand(command)
                println usageText
            }
        }
    }

    void performOperation(Operation operation) {
        if (!operation) {
            return
        }

        if (!operation.isConfigured()) {
            System.err.println misconfiguredOperation(operation)
            return
        }

        if (operation.run()) {
            println successfulOperation(operation)
        } else {
            System.err.println unsuccessfulOperation(operation)
        }
    }

    final unrecognizedCommand = { command -> "Unrecognized command: ${command}" }
    final misconfiguredOperation =
            { operation -> "${operation.name} operation did not run due to missing required options." }
    final successfulOperation = { operation -> "${operation.name} operation completed SUCCESSFULLY." }
    final unsuccessfulOperation = { operation -> "${operation.name} operation completed with ERRORS." }

    final usageText =  '''db-ratchet Database Management

commands:

 init       Creates a schema/data repository in the current working directory
            REQUIRES RedGate SQL Compare license

 push       Pushes a schema definition from this repository to a database
            REQUIRES RedGate SQL Compare license

 pull       Pulls a schema definition from a database to this repository
            REQUIRES RedGate SQL Compare license

 publish    Publishes schema definition as plain sql scripts

 build      Creates or updates a database (schema only) based on
            published scripts

 capture    Captures data packages from a database

 migrate    Migrates data packages to a database

 store      Stores database credentials for future reference.

All commands support the -h option for additional information.
'''
}
