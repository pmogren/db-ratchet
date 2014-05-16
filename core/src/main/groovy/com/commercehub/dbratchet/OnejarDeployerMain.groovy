package com.commercehub.dbratchet

import com.commercehub.dbratchet.filestore.ClasspathFileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version

/**
 * Created by jgelais on 5/9/2014.
 */
class OnejarDeployerMain {
    DatabaseConfig dbConfig
    Version version
    boolean isSchemaOnly = false
    boolean isDataOnly = false

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

        deployer.run()
    }

    void run() {
        if (!isDataOnly) {
            doBuildOperation()
        }
        if (!isSchemaOnly) {
            migrate()
        }
    }

    void migrate() {
        Operation migrateOp = new MigrateOperation(dbConfig)
        migrateOp.isDataOnClasspath = true
        migrateOp.run()
    }

    void doBuildOperation() {
        Operation buildOp = new BuildOperation(dbConfig, version,
                                               new SchemaConfig(new ClasspathFileStore()))
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
            _(longOpt: 'schema-only',                            required:false,
                    'Only build out database schema.')
            _(longOpt: 'data-only',                              required:false,
                    'Only build out control data.')
            h(longOpt: 'help',                                   required:false,
                    'Displays this usage message.')
        }

        def options = cli.parse(args)
        if (!options) {
            throw new IllegalArgumentException()
        }

        Version version = null
        if (options.v) {
            version = new Version(options.v)
        }

        def main = new com.commercehub.dbratchet.OnejarDeployerMain(getDBConfigFromCmdLineOptions(options), version)

        if (options.'schema-only') {
            main.isSchemaOnly = true
        }
        if (options.'data-only') {
            main.isDataOnly = true
        }
        if (main.isSchemaOnly && main.isDataOnly) {
            System.err.println('Cannot specify both --schema-only and --data-only')
            throw new IllegalArgumentException()
        }

        return main
    }

    private static DatabaseConfig getDBConfigFromCmdLineOptions(OptionAccessor options) {
        return new DatabaseConfig(server: (options.s ?: null), user: (options.u ?: null),
                password: (options.p ?: null), database: (options.d ?: null))
    }
}
