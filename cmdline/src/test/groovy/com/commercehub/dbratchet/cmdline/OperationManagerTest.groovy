package com.commercehub.dbratchet.cmdline

import com.commercehub.dbratchet.BuildOperation
import com.commercehub.dbratchet.CaptureOperation
import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.InitOperation
import com.commercehub.dbratchet.MigrateOperation
import com.commercehub.dbratchet.Operation
import com.commercehub.dbratchet.PublishOperation
import com.commercehub.dbratchet.PullOperation
import com.commercehub.dbratchet.PushOperation
import com.commercehub.dbratchet.ServerCredentialStore
import com.commercehub.dbratchet.StoreOperation
import com.commercehub.dbratchet.schema.Version

/**
 * Created by Brett on 1/25/14.
 */
@SuppressWarnings('MethodCount')
class OperationManagerTest extends GroovyTestCase {

    private final OperationManager manager = new OperationManager()
    private final emptyArgs = []
    private final serverAndDatabaseArg = ['-s', 'server', '-d', 'database']

    void setUp() {
        super.setUp()
        manager.metaClass.printedValue = ''
        manager.metaClass.println = { Object val ->  printedValue += val }
    }

    void tearDown() {
        super.tearDown()
        manager.printedValue = ''
    }

    void testGetNullCommandIsNull() {
        assert manager.getOperationToPerform(null) == null
    }

    void testGetInvalidCommandIsNull() {
        assert manager.getOperationToPerform('invalid') == null
    }

    void testGetInitOperationIsNotNull() {
        assert manager.getOperationToPerform('init') != null
    }

    void testGetPullOperationIsNotNull() {
        assert manager.getOperationToPerform('pull') != null
    }

    void testGetPushOperationIsNotNull() {
        assert manager.getOperationToPerform('push') != null
    }

    void testGetPublishOperationIsNotNull() {
        assert manager.getOperationToPerform('publish') != null
    }

    void testGetBuildOperationIsNotNull() {
        assert manager.getOperationToPerform('build') != null
    }

    void testGetCaptureOperationIsNotNull() {
        assert manager.getOperationToPerform('capture') != null
    }

    void testGetMigrateOperationIsNotNull() {
        assert manager.getOperationToPerform('migrate') != null
    }

    void testGetStoreOperationIsNotNull() {
        assert manager.getOperationToPerform('store') != null
    }

    void testInitOperationCreation() {
        assert manager.getInitOperation.call(emptyArgs) instanceof InitOperation
    }

    void testInitOperationCreationWithHelp() {
        assert manager.getInitOperation.call(['-h']) == null
    }

    void testInitOperationCreationWithSchemaType() {
        assert manager.getInitOperation.call(['-t', 'type']) instanceof InitOperation
    }

    void testPullOperationCreation() {
        assert manager.getPullOperation.call(serverAndDatabaseArg) instanceof PullOperation
    }

    void testPullOperationCreationWithHelp() {
        assert manager.getPullOperation.call(['-h']) == null
    }

    void testPullOperationCreationWithAlias() {
        assert manager.getPullOperation.call(['-a', 'alias', '-d', 'database']) instanceof PullOperation
    }

    void testPullOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getPullOperation.call(emptyArgs)
        }
    }

    void testPushOperationCreation() {
        assert manager.getPushOperation.call(serverAndDatabaseArg) instanceof PushOperation
    }

    void testPushOperationCreationWithHelp() {
        assert manager.getPushOperation.call(['-h']) == null
    }

    void testPushOperationCreationWithAlias() {
        assert manager.getPushOperation.call(['-a', 'alias', '-d', 'database']) instanceof PushOperation
    }

    void testPushOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getPushOperation.call(emptyArgs)
        }
    }

    void testPublishOperationCreation() {
        Operation operation = manager.getPublishOperation.call(['-s', 'server'])
        assert operation instanceof PublishOperation
        assert operation.publishType == PublishOperation.PUBLISH_TYPE.POINT
    }

    void testPublishOperationCreationMajorVersion() {
        Operation operation = manager.getPublishOperation.call(['-s', 'server', '--major'])
        assert operation instanceof PublishOperation
        assert operation.publishType == PublishOperation.PUBLISH_TYPE.MAJOR
    }

    void testPublishOperationCreationMinorVersion() {
        Operation operation = manager.getPublishOperation.call(['-s', 'server', '--minor'])
        assert operation instanceof PublishOperation
        assert operation.publishType == PublishOperation.PUBLISH_TYPE.MINOR
    }

    void testPublishOperationCreationWithHelp() {
        assert manager.getPublishOperation.call(['-h']) == null
    }

    void testPublishOperationCreationWithAlias() {
        assert manager.getPublishOperation.call(['-a', 'alias']) instanceof PublishOperation
    }

    void testBuildOperationCreation() {
        assert manager.getBuildOperation.call(serverAndDatabaseArg) instanceof BuildOperation
    }

    void testBuildOperationCreationWithVersion() {
        Operation operation = manager.getBuildOperation.call(serverAndDatabaseArg + ['-v', '0.0.0'])
        assert operation instanceof BuildOperation
        assert operation.version == new Version(0,0,0)

    }

    void testBuildOperationCreationWithHelp() {
        assert manager.getBuildOperation.call(['-h']) == null
    }

    void testBuildOperationCreationWithAlias() {
        assert manager.getBuildOperation.call(['-a', 'alias', '-d', 'database']) instanceof BuildOperation
    }

    void testBuildOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getBuildOperation.call(emptyArgs)
        }
    }

    void testCaptureOperationCreation() {
        assert manager.getCaptureOperation.call(serverAndDatabaseArg) instanceof CaptureOperation
    }

    void testCaptureOperationCreationWithHelp() {
        assert manager.getCaptureOperation.call(['-h']) == null
    }

    void testCaptureOperationCreationWithAlias() {
        assert manager.getCaptureOperation.call(['-a', 'alias', '-d', 'database']) instanceof CaptureOperation
    }

    void testCaptureOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getCaptureOperation.call(emptyArgs)
        }
    }

    void testMigrateOperationCreation() {
        assert manager.getMigrateOperation.call(serverAndDatabaseArg) instanceof MigrateOperation
    }

    void testMigrateOperationCreationWithHelp() {
        assert manager.getMigrateOperation.call(['-h']) == null
    }

    void testMigrateOperationCreationWithAlias() {
        assert manager.getMigrateOperation.call(['-a', 'alias', '-d', 'database']) instanceof MigrateOperation
    }

    void testMigrateOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getMigrateOperation.call(emptyArgs)
        }
    }

    void testStoreOperationCreation() {
        Operation operation = manager.getStoreOperation.call(['-a', 'alias', '-s', 'server'])
        assert operation instanceof StoreOperation
        assert operation.alias == 'alias'
    }

    void testStoreOperationCreationWithHelp() {
        assert manager.getStoreOperation.call(['-h']) == null
    }

    void testStoreOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getStoreOperation.call(emptyArgs)
        }
    }

    void testGetDBConfigFromCmdLineOptionsWithAlias() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
        }

        OptionAccessor optionAccessor = cli.parse(['-a', 'alias', '-s', 'server'])

        ServerCredentialStore.metaClass.get = { String alias -> return new DatabaseConfig(server:'server') }

        assert manager.getDBConfigFromCmdLineOptions(optionAccessor).server == 'server'

        ServerCredentialStore.metaClass = null
    }

    void testGetDBConfigFromCmdLineOptionsWithUnmappedAlias() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            a(longOpt: 'alias',      args:1, argName:'alias',    required:false,
                    'Alias for a set of stored credentials.')
            s(longOpt: 'server',     args:1, argName:'server',   required:false,
                    'Database server for this set of credentials. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login for this set of credentials.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password for this set of credentials.')
            d(longOpt: 'database',   args:1, argName:'database', required:false,
                    'Name of database to create. REQUIRED.')
        }

        OptionAccessor optionAccessor =
                cli.parse(['-a', 'alias', '-s', 'server', '-u', 'user', '-p', 'password', '-d', 'database'])

        ServerCredentialStore.metaClass.get = { String alias -> return null }

        DatabaseConfig dbConfig = manager.getDBConfigFromCmdLineOptions(optionAccessor)

        assert dbConfig.server == 'server'
        assert dbConfig.user == 'user'
        assert dbConfig.password == 'password'
        assert dbConfig.database == 'database'

        ServerCredentialStore.metaClass = null
    }

    void testGetDBConfigFromCmdLineOptionsWithAllOptions() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            s(longOpt: 'server',     args:1, argName:'server',   required:false,
                    'Database server for this set of credentials. REQUIRED.')
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                    'Database server login for this set of credentials.')
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                    'Database server password for this set of credentials.')
            d(longOpt: 'database',   args:1, argName:'database', required:false,
                    'Name of database to create. REQUIRED.')
        }

        OptionAccessor optionAccessor =
                cli.parse(['-s', 'server', '-u', 'user', '-p', 'password', '-d', 'database'])

        DatabaseConfig dbConfig = manager.getDBConfigFromCmdLineOptions(optionAccessor)

        assert dbConfig.server == 'server'
        assert dbConfig.user == 'user'
        assert dbConfig.password == 'password'
        assert dbConfig.database == 'database'
    }

    void testGetDBConfigFromCmdLineOptionsWithServer() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            s(longOpt: 'server',     args:1, argName:'server',   required:true,
                'Database server for this set of credentials. REQUIRED.')
        }

        OptionAccessor optionAccessor = cli.parse(['-s', 'server'])

        DatabaseConfig dbConfig = manager.getDBConfigFromCmdLineOptions(optionAccessor)

        assert dbConfig.server == 'server'
        assert dbConfig.user == null
        assert dbConfig.password == null
        assert dbConfig.database == null
    }

    void testGetDBConfigFromCmdLineOptionsWithUser() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                'Database server login for this set of credentials.')
        }

        OptionAccessor optionAccessor = cli.parse(['-u', 'user'])

        DatabaseConfig dbConfig = manager.getDBConfigFromCmdLineOptions(optionAccessor)

        assert dbConfig.user == 'user'
        assert dbConfig.server == null
        assert dbConfig.password == null
        assert dbConfig.database == null
    }

    void testGetDBConfigFromCmdLineOptionsWithPassword() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                'Database server password for this set of credentials.')
        }

        OptionAccessor optionAccessor = cli.parse(['-p', 'password'])

        DatabaseConfig dbConfig = manager.getDBConfigFromCmdLineOptions(optionAccessor)

        assert dbConfig.password == 'password'
        assert dbConfig.server == null
        assert dbConfig.user == null
        assert dbConfig.database == null
    }

    void testGetDBConfigFromCmdLineOptionsWithDatabase() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                'Name of database to create. REQUIRED.')
        }

        OptionAccessor optionAccessor = cli.parse(['-d', 'database'])

        DatabaseConfig dbConfig = manager.getDBConfigFromCmdLineOptions(optionAccessor)

        assert dbConfig.database == 'database'
        assert dbConfig.server == null
        assert dbConfig.user == null
        assert dbConfig.password == null
    }
}
