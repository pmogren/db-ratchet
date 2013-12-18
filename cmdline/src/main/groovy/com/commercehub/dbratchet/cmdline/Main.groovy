package com.commercehub.dbratchet.cmdline

import com.commercehub.dbratchet.CaptureOperation
import com.commercehub.dbratchet.BuildOperation
import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.InitOperation
import com.commercehub.dbratchet.MigrateOperation
import com.commercehub.dbratchet.Operation
import com.commercehub.dbratchet.PublishOperation
import com.commercehub.dbratchet.PullOperation
import com.commercehub.dbratchet.PushOperation
import com.commercehub.dbratchet.ServerCredentialStore
import com.commercehub.dbratchet.StoreOperation
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/12/13
 * Time: 3:44 PM
 */
@SuppressWarnings('CatchException')
@SuppressWarnings('DuplicateMapLiteral')
@SuppressWarnings('DuplicateStringLiteral')
@SuppressWarnings('SystemExit')
class Main {
    private final SchemaConfig schemaConfig = new SchemaConfig(new File('.'))

    static main(args) {
        String command = null
        if (args.length > 0) {
            command = args[0]

        }
        Main app = new Main()
        try {
            app.run(command, args - command)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    void run(command, args) {
        Operation operation = null
        if (command == null) {
            println usageText
        } else if ('init' == (command)) {
            operation = getInitOperation(args)
        } else if ('pull' == (command)) {
            operation = getPullOperation(args)
        } else if ('push' == (command)) {
            operation = getPushOperation(args)
        } else if ('publish' == (command)) {
            operation = getPublishOperation(args)
        } else if ('build' == (command)) {
            operation = getBuildOperation(args)
        } else if ('capture' == (command)) {
            operation = getCaptureOperation(args)
        } else if ('migrate' == (command)) {
            operation = getMigrateOperation(args)
        } else if ('store' == (command)) {
            operation = getStoreOperation(args)
        }  else {
            println "Unrecognized command: ${command}"
            println usageText
        }

        if (operation) {
            if (performOperation(operation)) {
                println "${operation.name} operation completed SUCCESSFULLY."
            } else {
                System.err.println "${operation.name} operation completed with ERRORS."
                System.exit(1)
            }
        }
    }

    boolean performOperation(Operation operation) {
        if (!operation.isConfigured()) {
            System.err.println "${operation.name} operation did not run do to missing required options."
            return false
        }

        return operation.run()
    }

    Operation getInitOperation(args) {
        def cli = new CliBuilder(usage: 'bg init [options]')
        cli.with {
            t(longOpt: 'schema-type', required:false, args:1, argName:'schemaType',
                                                      'Name of the Schema Difference Engine to Use')
            h(longOpt: 'help',        required:false, 'Displays this usage message.')
        }

        def options = cli.parse(args)
        if (!options) {
            System.exit(1)
        }

        if (options.h) {
            cli.usage()
            System.exit(0)
        }

        String schemaType = (options.t) ? options.t : 'redgate'
        return new InitOperation(schemaConfig, schemaType)
    }

    Operation getPullOperation(args) {
        def cli = new CliBuilder(usage: 'bg pull [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for this set of stored credentials..')
            s(longOpt: 'server',     args:1, argName:'server',   required:false,
                    'Database server to use to pull schema from. REQUIRED.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to use to pull schema from. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            h(longOpt: 'help', required:false, 'Displays this usage message.')
        }

        def options = cli.parse(args)
        if (!options) {
            System.exit(1)
        }

        if (options.h) {
            cli.usage()
            System.exit(0)
        }

        return new PullOperation(schemaConfig, getDBConfigFromCmdLineOptions(options))
    }

    Operation getPushOperation(args) {
        def cli = new CliBuilder(usage: 'bg push [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for this set of stored credentials..')
            s(longOpt: 'server',     args:1, argName:'server',   required:false,
                    'Database server to use to push schema to. REQUIRED.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to use to push schema to. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        def options = cli.parse(args)
        if (!options) {
            System.exit(1)
        }

        if (options.h) {
            cli.usage()
            System.exit(0)
        }

        return new PushOperation(schemaConfig, getDBConfigFromCmdLineOptions(options))
    }

    Operation getPublishOperation(args) {
        def cli = new CliBuilder(usage: 'bg publish [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:false,
                    'Database server to use to push schema to. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            _(longOpt: 'major',                                  required:false,
             'Increment the MAJOR version with this publish. Default behaviour is to increment the patch/build number.')
            _(longOpt: 'minor',                                  required:false,
             'Increment the MINOR version with this publish. Default behaviour is to increment the patch/build number.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        def options = cli.parse(args)
        if (!options) {
            System.exit(1)
        }

        if (options.h) {
            cli.usage()
            System.exit(0)
        }

        PublishOperation returnOp =
            new PublishOperation(schemaConfig, getDBConfigFromCmdLineOptions(options))
        if (options.major) {
            returnOp.publishType = PublishOperation.PUBLISH_TYPE.MAJOR
        }
        if (options.minor) {
            returnOp.publishType = PublishOperation.PUBLISH_TYPE.MINOR
        }

        return returnOp
    }

    Operation getBuildOperation(args) {
        def cli = new CliBuilder(usage: 'bg build [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:false,
                    'Database server to use to create database on. REQUIRED.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to create. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            v(longOpt: 'version',    args:1, argName:'version',  required:false,
                    'Schema version to use in creating this database.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        def options = cli.parse(args)
        if (!options) {
            System.exit(1)
        }

        if (options.h) {
            cli.usage()
            System.exit(0)
        }

        Version version = null
        if (options.v) {
            version = new Version(options.v)
        }

        return new BuildOperation(getDBConfigFromCmdLineOptions(options), version)
    }

    Operation getCaptureOperation(args) {
        def cli = new CliBuilder(usage: 'bg capture [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:false,
                    'Database server to use to create database on. REQUIRED.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to create. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        def options = cli.parse(args)
        if (!options) {
            System.exit(1)
        }

        if (options.h) {
            cli.usage()
            System.exit(0)
        }

        return new CaptureOperation(getDBConfigFromCmdLineOptions(options))
    }

    Operation getMigrateOperation(args) {
        def cli = new CliBuilder(usage: 'bg capture [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:false,
                    'Database server to use to create database on. REQUIRED.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to create. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        def options = cli.parse(args)
        if (!options) {
            System.exit(1)
        }

        if (options.h) {
            cli.usage()
            System.exit(0)
        }

        return new MigrateOperation(getDBConfigFromCmdLineOptions(options))
    }

    Operation getStoreOperation(args) {
        def cli = new CliBuilder(usage: 'bg capture [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:true,
                    'Alias for this set of stored credentials. REQUIRED.')
            s(longOpt: 'server',     args:1, argName:'server',   required:true,
                    'Database server for this set of credentials. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login for this set of credentials.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password for this set of credentials.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        def options = cli.parse(args)
        if (!options) {
            System.exit(1)
        }

        if (options.h) {
            cli.usage()
            System.exit(0)
        }

        return new StoreOperation(schemaConfig, options.a, getDBConfigFromCmdLineOptions(options))
    }

    final String usageText =  '''BlackGate Database Management

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

    DatabaseConfig getDBConfigFromCmdLineOptions(OptionAccessor options) {
        DatabaseConfig dbConfig = null
        if (options.a) {
            ServerCredentialStore credStore = new ServerCredentialStore(schemaConfig)
            dbConfig = credStore.get(options.a) ?: new DatabaseConfig()
        } else {
            dbConfig = new DatabaseConfig()

            if (options.s) {
                dbConfig.setServer(options.s)
            }

            if (options.u) {
                dbConfig.setUser(options.u)
            }

            if (options.p) {
                dbConfig.setPassword(options.p)
            }
        }

        if (options.d) {
            dbConfig.setDatabase(options.d)
        }

        return dbConfig
    }
}
