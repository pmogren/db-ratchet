package com.commercehub.dbratchet.cmdline

import com.commercehub.dbratchet.*
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version
import groovy.transform.TupleConstructor

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
        PreProcessContext ppc = preProcessCmdLineArgs(args)

        def cli = new CliBuilder(usage: 'ratchet init [options]')
        cli.with {
            t(longOpt: 'schema-type', required:false, args:1, argName:'schemaType',
                    'Name of the Schema Difference Engine to Use')
            h(longOpt: 'help',        required:false, 'Displays this usage message.')
        }

        if (ppc.hPresent) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)

        String schemaType = (options.t) ? options.t : 'redgate'
        return new InitOperation(schemaConfig, schemaType)
    }

    def getPullOperation = { args ->
        PreProcessContext ppc = preProcessCmdLineArgs(args)

        def cli = new CliBuilder(usage: 'ratchet pull [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aPresent,
                    'Database server to pull schema from. REQUIRED if alias not present.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to pull schema from. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            h(longOpt: 'help', required:false, 'Displays this usage message.')
        }

        if (ppc.hPresent) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)
        return new PullOperation(schemaConfig, getDBConfigFromCmdLineOptions(options))
    }

    def getPushOperation = { args ->
        PreProcessContext ppc = preProcessCmdLineArgs(args)

        def cli = new CliBuilder(usage: 'ratchet push [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aPresent,
                    'Database server to push schema to. REQUIRED if alias not present.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to push schema to. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        if (ppc.hPresent) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)
        return new PushOperation(schemaConfig, getDBConfigFromCmdLineOptions(options))
    }

    def getPublishOperation = { args ->
        PreProcessContext ppc = preProcessCmdLineArgs(args)

        def cli = new CliBuilder(usage: 'ratchet publish [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aPresent,
                    'Database server to assist in publish operation. REQUIRED if alias not present.')
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

        if (ppc.hPresent) {
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
        PreProcessContext ppc = preProcessCmdLineArgs(args)

        def cli = new CliBuilder(usage: 'ratchet build [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aPresent,
                    'Database server to create database on. REQUIRED if alias not present.')
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

        if (ppc.hPresent) {
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
        PreProcessContext ppc = preProcessCmdLineArgs(args)

        def cli = new CliBuilder(usage: 'ratchet capture [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aPresent,
                    'Database server to capture database from. REQUIRED if alias not present.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to capture from. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        if (ppc.hPresent) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)
        return new CaptureOperation(getDBConfigFromCmdLineOptions(options))
    }

    def getMigrateOperation = { args ->
        PreProcessContext ppc = preProcessCmdLineArgs(args)

        def cli = new CliBuilder(usage: 'ratchet migrate [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aPresent,
                    'Database server to migrate database to. REQUIRED if alias not present.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to migrate to. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        if (ppc.hPresent) {
            cli.usage()
            return
        }

        def options = processOptions(cli, args)
        return new MigrateOperation(getDBConfigFromCmdLineOptions(options))
    }

    def getStoreOperation = { args ->
        PreProcessContext ppc = preProcessCmdLineArgs(args)

        def cli = new CliBuilder(usage: 'ratchet capture [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:true,
                    'Alias for this set of stored credentials. REQUIRED.')
            s(longOpt: 'server',     args:1, argName:'server',   required:true,
                    'Database server for this set of stored credentials. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login for this set of stored credentials.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password for this set of stored credentials.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        if (ppc.hPresent) {
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

    PreProcessContext preProcessCmdLineArgs(def args) {
        def cli = new CliBuilder()
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            h(longOpt: 'help',        required:false, 'Displays this usage message.')
        }

        def options = cli.parse(args)

        if (options.h) {
            return new PreProcessContext(true, false)
        } else if (options.a) {
            return new PreProcessContext(false, true)
        }

        return new PreProcessContext(false, false)
    }

    @TupleConstructor
    class PreProcessContext {
        boolean hPresent
        boolean aPresent
    }

    DatabaseConfig getDBConfigFromCmdLineOptions(OptionAccessor options) {
        DatabaseConfig dbConfig
        if (options.a) {
            ServerCredentialStore credStore = new ServerCredentialStore(schemaConfig)
            dbConfig = credStore.get(options.a) ?: new DatabaseConfig(server: (options.s ?: null),
                    user: (options.u ?: null), password: (options.p ?: null))
        } else {
            dbConfig = new DatabaseConfig(server: (options.s ?: null), user: (options.u ?: null),
                    password: (options.p ?: null))
        }

        dbConfig.setDatabase(options.d ?: null)

        return dbConfig
    }
}
