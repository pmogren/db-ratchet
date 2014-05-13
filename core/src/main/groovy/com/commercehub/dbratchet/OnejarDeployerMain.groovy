package com.commercehub.dbratchet

import com.commercehub.dbratchet.schema.Version

/**
 * Created by jgelais on 5/9/2014.
 */
class OnejarDeployerMain {
    DatabaseConfig dbConfig
    Version version

    private OnejarDeployerMain(DatabaseConfig dbConfig, Version version) {
        this.dbConfig = dbConfig
        this.version = version
    }

    @SuppressWarnings('SystemExit')
    static main(args) {
        OnejarDeployerMain deployer
        try {
            deployer = initializeDeployer(args)
        } catch (IllegalArgumentException iaex) {
            System.exit(1)
        }

        deployer.doBuildOperation()
        deployer.migrate()
    }

    void migrate() {
        Operation migrateOp = new MigrateOperation(dbConfig)
        migrateOp.run()
    }

    void doBuildOperation() {
        Operation buildOp = new BuildOperation(dbConfig, version)
        buildOp.run()
    }

    @SuppressWarnings('DuplicateStringLiteral')
    static OnejarDeployerMain initializeDeployer(def args) {
        def cli = new CliBuilder(usage: 'java -jar <deployment-jar>.jar [options]')
        cli.with {
            s(longOpt: 'server',     args:1, argName:'server',   required:true,
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

        def options = cli.parse(args)
        if (!options) {
            cli.usage()
            throw new IllegalArgumentException()
        }

        Version version = null
        if (options.v) {
            version = new Version(options.v)
        }

        return new OnejarDeployerMain(getDBConfigFromCmdLineOptions(options), version)
    }

    private static DatabaseConfig getDBConfigFromCmdLineOptions(OptionAccessor options) {
        return new DatabaseConfig(server: (options.s ?: null), user: (options.u ?: null),
                password: (options.p ?: null), database: (options.d ?: null))
    }
}
