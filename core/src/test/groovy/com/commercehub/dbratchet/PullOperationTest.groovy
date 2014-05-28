package com.commercehub.dbratchet

import static org.junit.Assume.assumeTrue

import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.filestore.FileSystemFileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.redgate.SqlCompareSchemaDifferenceEngine
import com.commercehub.dbratchet.util.SqlScriptRunner
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Created by jaystgelais on 5/27/14.
 */
class PullOperationTest {
    static final String SETUP_SCRIPT_1 =
            '/com/commercehub/dbratchet/schema/redgate/sample-redgate-schema-setup-script-1'

    @Rule
    @SuppressWarnings('PublicInstanceField')
    @SuppressWarnings('NonFinalPublicField')
    public TemporaryFolder folder = new TemporaryFolder()

    private DatabaseConfig databaseConfig

    @Before
    void setup() {
        assumptions()
    }

    @After
    void cleanup() {
        if (databaseConfig) {
            DatabaseClientFactory.getDatabaseClient(databaseConfig.vendor).deleteDatabase(databaseConfig)
            databaseConfig = null
        }
    }

    private void assumptions() {
        assumeTrue(TestConfig.isRedgateAvailable)
        assumeTrue(TestConfig.isMSSQLAvailable)
    }

    @Test
    void testDefaultPath() {
        FileStore fileStore = new FileSystemFileStore(folder.newFolder())
        SchemaConfig schemaConfig = new SchemaConfig(fileStore)
        initSchemaStore(schemaConfig)
        databaseConfig = setupTransientDatabase()
        SqlScriptRunner.runScript(databaseConfig, PullOperation.getResourceAsStream(SETUP_SCRIPT_1))

        Operation pullOp = new PullOperation(schemaConfig, databaseConfig)
        assert pullOp.configured
        assert pullOp.run()

        // TODO write assertions
    }

    private DatabaseConfig setupTransientDatabase() {
        DatabaseConfig transientDB = TestConfig.mssqlServerCredentials.setDatabase(generateTransientDbName())
        DatabaseClientFactory.getDatabaseClient(transientDB.vendor).createDatabase(transientDB)
        return transientDB
    }

    private String generateTransientDbName() {
        return "db_ratchet_unittest_db_${UUID.randomUUID().toString().replaceAll('-', '')}"
    }

    private void initSchemaStore(SchemaConfig schemaConfig) {
        Operation initOp = new InitOperation(schemaConfig, SqlCompareSchemaDifferenceEngine.ENGINE_NAME)
        assert initOp.configured
        assert initOp.run()
    }
}
