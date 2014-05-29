package com.commercehub.dbratchet

import static org.junit.Assume.assumeTrue

import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.filestore.FileSystemFileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.Version
import com.commercehub.dbratchet.schema.redgate.SqlCompareSchemaDifferenceEngine
import com.commercehub.dbratchet.util.SqlScriptRunner
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Created by jgelais on 5/29/2014.
 */
class PublishOperationTest {
    static final String SETUP_SCRIPT_1 =
            '/com/commercehub/dbratchet/schema/redgate/sample-redgate-schema-setup-script-1.sql'
    static final String SETUP_SCRIPT_2 =
            '/com/commercehub/dbratchet/schema/redgate/sample-redgate-schema-setup-script-2.sql'

    @Rule
    @SuppressWarnings('PublicInstanceField')
    @SuppressWarnings('NonFinalPublicField')
    public TemporaryFolder folder = new TemporaryFolder()

    private DatabaseConfig modelDatabaseConfig

    @Before
    void setup() {
        assumptions()
    }

    @After
    void cleanup() {
        if (modelDatabaseConfig) {
            DatabaseClientFactory.getDatabaseClient(modelDatabaseConfig.vendor).deleteDatabase(modelDatabaseConfig)
            modelDatabaseConfig = null
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
        modelDatabaseConfig = setupTransientDatabase()

        setupVersion001(modelDatabaseConfig, schemaConfig)
        publishVersion(schemaConfig, modelDatabaseConfig)
        assert schemaConfig.versions.contains(new Version(0, 0, 1))

        setupVersion002(modelDatabaseConfig, schemaConfig)
        publishVersion(schemaConfig, modelDatabaseConfig, PublishOperation.PUBLISH_TYPE.MINOR)
        assert schemaConfig.versions.contains(new Version(0, 1, 0))
    }

    private void publishVersion(SchemaConfig schemaConfig, DatabaseConfig modelDatabaseConfig) {
        Operation publishOp = new PublishOperation(schemaConfig, modelDatabaseConfig.serverConfig)
        assert publishOp.configured
        assert publishOp.run()
    }

    private void publishVersion(SchemaConfig schemaConfig, DatabaseConfig modelDatabaseConfig,
                                PublishOperation.PUBLISH_TYPE publishType) {
        Operation publishOp = new PublishOperation(schemaConfig, modelDatabaseConfig.serverConfig, publishType)
        assert publishOp.configured
        assert publishOp.run()
    }

    private void setupVersion001(DatabaseConfig modelDatabaseConfig, SchemaConfig schemaConfig) {
        SqlScriptRunner.runScript(modelDatabaseConfig, PublishOperation.getResourceAsStream(SETUP_SCRIPT_1))
        pullFromModel(schemaConfig, modelDatabaseConfig)
    }

    private void setupVersion002(DatabaseConfig modelDatabaseConfig, SchemaConfig schemaConfig) {
        SqlScriptRunner.runScript(modelDatabaseConfig, PublishOperation.getResourceAsStream(SETUP_SCRIPT_2))
        pullFromModel(schemaConfig, modelDatabaseConfig)
    }

    private void pullFromModel(SchemaConfig schemaConfig, DatabaseConfig modelDatabaseConfig) {
        Operation pullOp = new PullOperation(schemaConfig, modelDatabaseConfig)
        assert pullOp.configured
        assert pullOp.run()
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
