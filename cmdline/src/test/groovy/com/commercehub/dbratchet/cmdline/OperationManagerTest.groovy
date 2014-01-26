package com.commercehub.dbratchet.cmdline

import com.commercehub.dbratchet.*
import com.commercehub.dbratchet.schema.Version

/**
 * Created by Brett on 1/25/14.
 */
@SuppressWarnings('MethodCount')
class OperationManagerTest extends GroovyTestCase {

    private final OperationManager manager = new OperationManager()
    private final emptyArgs = []
    private final databaseArg = ['-d', 'database']

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
        assert manager.getPullOperation.call(databaseArg) instanceof PullOperation
    }

    void testPullOperationCreationWithHelp() {
        assert manager.getPullOperation.call(['-h']) == null
    }

    void testPullOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getPullOperation.call(emptyArgs)
        }
    }

    void testPushOperationCreation() {
        assert manager.getPushOperation.call(databaseArg) instanceof PushOperation
    }

    void testPushOperationCreationWithHelp() {
        assert manager.getPushOperation.call(['-h']) == null
    }

    void testPushOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getPushOperation.call(emptyArgs)
        }
    }

    void testPublishOperationCreation() {
        Operation operation = manager.getPublishOperation.call(emptyArgs)
        assert operation instanceof PublishOperation
        assert operation.publishType == PublishOperation.PUBLISH_TYPE.POINT
    }

    void testPublishOperationCreationMajorVersion() {
        Operation operation = manager.getPublishOperation.call(['--major'])
        assert operation instanceof PublishOperation
        assert operation.publishType == PublishOperation.PUBLISH_TYPE.MAJOR
    }

    void testPublishOperationCreationMinorVersion() {
        Operation operation = manager.getPublishOperation.call(['--minor'])
        assert operation instanceof PublishOperation
        assert operation.publishType == PublishOperation.PUBLISH_TYPE.MINOR
    }

    void testPublishOperationCreationWithHelp() {
        assert manager.getPublishOperation.call(['-h']) == null
    }

    void testBuildOperationCreation() {
        assert manager.getBuildOperation.call(databaseArg) instanceof BuildOperation
    }

    void testBuildOperationCreationWithVersion() {
        Operation operation = manager.getBuildOperation.call(['-d', 'database', '-v', '0.0.0'])
        assert operation instanceof BuildOperation
        assert operation.version == new Version(0,0,0)

    }

    void testBuildOperationCreationWithHelp() {
        assert manager.getBuildOperation.call(['-h']) == null
    }

    void testBuildOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getBuildOperation.call(emptyArgs)
        }
    }

    void testCaptureOperationCreation() {
        assert manager.getCaptureOperation.call(databaseArg) instanceof CaptureOperation
    }

    void testCaptureOperationCreationWithHelp() {
        assert manager.getCaptureOperation.call(['-h']) == null
    }

    void testCaptureOperationCreationEmptyArgs() {
        shouldFail(InvalidOptionsException) {
            manager.getCaptureOperation.call(emptyArgs)
        }
    }

    void testMigrateOperationCreation() {
        assert manager.getMigrateOperation.call(databaseArg) instanceof MigrateOperation
    }

    void testMigrateOperationCreationWithHelp() {
        assert manager.getMigrateOperation.call(['-h']) == null
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

    void testGetDBConfigFromCmdLineOptionsWithServer() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            s(longOpt: 'server',     args:1, argName:'server',   required:true,
                'Database server for this set of credentials. REQUIRED.')
        }

        OptionAccessor optionAccessor = cli.parse(['-s', 'server'])

        assert manager.getDBConfigFromCmdLineOptions(optionAccessor).server == 'server'
    }

    void testGetDBConfigFromCmdLineOptionsWithUser() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            u(longOpt: 'user',       args:1, argName:'user',     required:false,
                'Database server login for this set of credentials.')
        }

        OptionAccessor optionAccessor = cli.parse(['-u', 'user'])

        assert manager.getDBConfigFromCmdLineOptions(optionAccessor).user == 'user'
    }

    void testGetDBConfigFromCmdLineOptionsWithPassword() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            p(longOpt: 'password',   args:1, argName:'password', required:false,
                'Database server password for this set of credentials.')
        }

        OptionAccessor optionAccessor = cli.parse(['-p', 'password'])

        assert manager.getDBConfigFromCmdLineOptions(optionAccessor).password == 'password'
    }

    void testGetDBConfigFromCmdLineOptionsWithDatabase() {
        CliBuilder cli = new CliBuilder()

        cli.with {
            d(longOpt: 'database',   args:1, argName:'database', required:true,
                'Name of database to create. REQUIRED.')
        }

        OptionAccessor optionAccessor = cli.parse(['-d', 'database'])

        assert manager.getDBConfigFromCmdLineOptions(optionAccessor).database == 'database'
    }
}
