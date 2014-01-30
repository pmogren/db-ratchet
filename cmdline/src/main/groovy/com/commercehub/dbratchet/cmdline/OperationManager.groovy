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

    def getInitOperation = { args -> return basicOperation(args, getInitCli, processInitCli) }

    def getPullOperation = { args -> return basicOperation(args, getPullCli, processPullCli) }

    def getPushOperation = { args -> return basicOperation(args, getPushCli, processPushCli) }

    def getPublishOperation = { args -> return basicOperation(args, getPublishCli, processPublishCli) }

    def getBuildOperation = { args -> return basicOperation(args, getBuildCli, processBuildCli) }

    def getCaptureOperation = { args -> return basicOperation(args, getCaptureCli, processCaptureCli) }

    def getMigrateOperation = { args -> return basicOperation(args, getMigrateCli, processMigrateCli) }

    def getStoreOperation = { args -> return basicOperation(args, getStoreCli, processStoreCli) }

    private basicOperation(args, cmdLineParseClosure, cmdLineProcessClosure) {
        PreProcessContext ppc = preProcessCmdLineArgs(args)

        def cli = cmdLineParseClosure.call(ppc)

        if (ppc.helpPresent) {
            cli.usage()
            return
        }

        return cmdLineProcessClosure.call(processOptions(cli, args))
    }

    private PreProcessContext preProcessCmdLineArgs(def args) {
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

    private OptionAccessor processOptions(cli, args) {
        def options = cli.parse(args)
        if (!options) {
            throw new InvalidOptionsException()
        }

        return options
    }

    private final getInitCli = { PreProcessContext ppc ->
        def cli = new CliBuilder(usage: 'ratchet init [options]')
        cli.with {
            t(longOpt: 'schema-type', required:false, args:1, argName:'schemaType',
                    'Name of the Schema Difference Engine to Use')
            h(longOpt: 'help',        required:false, 'Displays this usage message.')
        }

        return cli
    }

    private final getPullCli = { PreProcessContext ppc ->
        def cli = new CliBuilder(usage: 'ratchet pull [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aliasPresent,
                    'Database server to pull schema from. REQUIRED if alias not present.')
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                    'Name of database to pull schema from. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login to use. Leave blank to use Active Directory authentication.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password to use. Leave blank to use Active Directory authentication.')
            h(longOpt: 'help', required:false, 'Displays this usage message.')
        }

        return cli
    }

    private final getPushCli = { PreProcessContext ppc ->
        def cli = new CliBuilder(usage: 'ratchet push [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aliasPresent,
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

        return cli
    }

    private final getPublishCli = { PreProcessContext ppc ->
        def cli = new CliBuilder(usage: 'ratchet publish [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aliasPresent,
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

        return cli
    }

    private final getBuildCli = { PreProcessContext ppc ->
        def cli = new CliBuilder(usage: 'ratchet build [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aliasPresent,
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

        return cli
    }

    private final getCaptureCli = { PreProcessContext ppc ->
        def cli = new CliBuilder(usage: 'ratchet capture [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aliasPresent,
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

        return cli
    }

    private final getMigrateCli = { PreProcessContext ppc ->
        def cli = new CliBuilder(usage: 'ratchet migrate [options]')
        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:!ppc.aliasPresent,
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

        return cli
    }

    private final getStoreCli = { PreProcessContext ppc ->
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

        return cli
    }

    private final processInitCli = { def options ->
        String schemaType = (options.t) ? options.t : 'redgate'
        return new InitOperation(schemaConfig, schemaType)
    }

    private final processPullCli = { def options ->
        return new PullOperation(schemaConfig, getDBConfigFromCmdLineOptions(options))
    }

    private final processPushCli = { def options ->
        return new PushOperation(schemaConfig, getDBConfigFromCmdLineOptions(options))
    }

    private final processPublishCli = { def options ->
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

    private final processBuildCli = { def options ->
        Version version = null
        if (options.v) {
            version = new Version(options.v)
        }

        return new BuildOperation(getDBConfigFromCmdLineOptions(options), version)
    }

    private final processCaptureCli = { def options ->
        return new CaptureOperation(getDBConfigFromCmdLineOptions(options))
    }

    private final processMigrateCli = { def options ->
        return new MigrateOperation(getDBConfigFromCmdLineOptions(options))
    }

    private final processStoreCli = { def options ->
        return new StoreOperation(schemaConfig, options.a, getDBConfigFromCmdLineOptions(options))
    }

    private DatabaseConfig getDBConfigFromCmdLineOptions(OptionAccessor options) {
        DatabaseConfig dbConfig

        if (options.a) {
            ServerCredentialStore credStore = new ServerCredentialStore(schemaConfig)
            dbConfig = credStore.get(options.a)
        }
        
        dbConfig = dbConfig ?: new DatabaseConfig(server: (options.s ?: null), user: (options.u ?: null),
                password: (options.p ?: null))

        dbConfig.setDatabase(options.d ?: null)

        return dbConfig
    }

    @TupleConstructor
    private class PreProcessContext {
        boolean helpPresent
        boolean aliasPresent
    }
}