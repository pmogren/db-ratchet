package com.commercehub.dbratchet.cmdline

import com.commercehub.dbratchet.*
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version

/**
 * Created by Brett on 1/25/14.
 */
@SuppressWarnings('DuplicateMapLiteral')
@SuppressWarnings('DuplicateStringLiteral')
@SuppressWarnings('SystemExit')
class OperationManager {

    private final SchemaConfig schemaConfig = new SchemaConfig(new File('.'))

    private final Map<String, Closure> operations

    OperationManager() {
        operations = [
            'init' : getInitOperation,
            'pull' : getPullOperation,
            'push' : getPushOperation,
            'publish' : getPublishOperation,
            'build' : getBuildOperation,
            'capture' : getCaptureOperation,
            'migrate' : getMigrateOperation,
            'store' : getStoreOperation
        ]
    }

    Closure getOperationToPerform(command) {
        return operations.get(command)
    }

    def getInitOperation = { args ->
        def cli = new CliBuilder(usage: 'ratchet init [options]')
        cli.with {
            t(longOpt: 'schema-type', required:false, args:1, argName:'schemaType',
                    'Name of the Schema Difference Engine to Use')
            h(longOpt: 'help',        required:false, 'Displays this usage message.')
        }

        if (helpOptionPresent(args)) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)

        String schemaType = (options.t) ? options.t : 'redgate'
        return new InitOperation(schemaConfig, schemaType)
    }

    def getPullOperation = { args ->
        def cli = new CliBuilder(usage: 'ratchet pull [options]')
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

        if (helpOptionPresent(args)) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)
        return new PullOperation(schemaConfig, getDBConfigFromCmdLineOptions(options))
    }

    def getPushOperation = { args ->
        def cli = new CliBuilder(usage: 'ratchet push [options]')
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

        if (helpOptionPresent(args)) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)
        return new PushOperation(schemaConfig, getDBConfigFromCmdLineOptions(options))
    }

    def getPublishOperation = { args ->
        def cli = new CliBuilder(usage: 'ratchet publish [options]')
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
                    'Increment the MAJOR version with this publish. ' +
                            'Default behaviour is to increment the patch/build number.')
            _(longOpt: 'minor',                                  required:false,
                    'Increment the MINOR version with this publish. ' +
                            'Default behaviour is to increment the patch/build number.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        if (helpOptionPresent(args)) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)

        def publishType = PublishOperation.PUBLISH_TYPE.POINT

        if (options.major) {
            publishType = PublishOperation.PUBLISH_TYPE.MAJOR
        } else if (options.minor) {
            publishType = PublishOperation.PUBLISH_TYPE.MINOR
        }

        PublishOperation returnOp =
                new PublishOperation(schemaConfig, getDBConfigFromCmdLineOptions(options), publishType)

        return returnOp
    }

    def getBuildOperation = { args ->
        def cli = new CliBuilder(usage: 'ratchet build [options]')
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

        if (helpOptionPresent(args)) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)

        Version version = null
        if (options.v) {
            version = new Version(options.v)
        }

        return new BuildOperation(getDBConfigFromCmdLineOptions(options), version)
    }

    def getCaptureOperation = { args ->
        def cli = new CliBuilder(usage: 'ratchet capture [options]')
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

        if (helpOptionPresent(args)) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)
        return new CaptureOperation(getDBConfigFromCmdLineOptions(options))
    }

    def getMigrateOperation = { args ->
        def cli = new CliBuilder(usage: 'ratchet capture [options]')
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

        if (helpOptionPresent(args)) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)
        return new MigrateOperation(getDBConfigFromCmdLineOptions(options))
    }

    def getStoreOperation = { args ->
        def cli = new CliBuilder(usage: 'ratchet capture [options]')
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

        if (helpOptionPresent(args)) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)
        return new StoreOperation(schemaConfig, options.a, getDBConfigFromCmdLineOptions(options))
    }

    private OptionAccessor processOptions(cli, args) {
        def options = cli.parse(args)
        if (!options) {
            throw new InvalidOptionsException()
        }

        return options
    }

    boolean helpOptionPresent(def args) {
        def cli = new CliBuilder()
        cli.with {
            h(longOpt: 'help',        required:false, 'Displays this usage message.')
        }

        def options = cli.parse(args)

        if (options.h) {
            return true
        }

        return false
    }

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
