package com.commercehub.dbratchet.databases.mssql

import static org.junit.Assume.assumeTrue

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.InitOperation
import com.commercehub.dbratchet.MigrateOperation
import com.commercehub.dbratchet.Operation
import com.commercehub.dbratchet.TestConfig
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
 * Created by jgelais on 5/29/2014.
 */
class MSSQLDataMigratorTest {
    static final String SETUP_SCRIPT_1 =
            '/com/commercehub/dbratchet/schema/redgate/sample-redgate-schema-setup-script-1.sql'
    static final String SETUP_SCRIPT_2 =
            '/com/commercehub/dbratchet/schema/redgate/sample-redgate-schema-setup-script-2.sql'
    static final String SETUP_SCRIPT_3 =
            '/com/commercehub/dbratchet/schema/redgate/sample-redgate-schema-setup-script-3.sql'

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
    void testDefaultPathViaMigrateOperation() {
        FileStore fileStore = new FileSystemFileStore(folder.newFolder())
        SchemaConfig schemaConfig = new SchemaConfig(fileStore)
        initSchemaStore(schemaConfig)
        databaseConfig = setupTransientDatabase()
        SqlScriptRunner.runScript(databaseConfig, MigrateOperation.getResourceAsStream(SETUP_SCRIPT_1))
        SqlScriptRunner.runScript(databaseConfig, MigrateOperation.getResourceAsStream(SETUP_SCRIPT_2))
        SqlScriptRunner.runScript(databaseConfig, MigrateOperation.getResourceAsStream(SETUP_SCRIPT_3))
        populateFileFromResource(fileStore, 'data/data-packages.xml',
                MigrateOperation.getResource('/com/commercehub/dbratchet/schema/redgate/data-packages.xml'))
        populateFileFromResource(fileStore, 'data/packages/transcripts.xml',
                MigrateOperation.getResource('/com/commercehub/dbratchet/schema/redgate/transcripts.xml'))

        Operation migrateOp = new MigrateOperation(databaseConfig, fileStore)
        assert migrateOp.configured
        assert migrateOp.run()

        // TODO write deeper assertions
    }

    private void populateFileFromResource(FileStore fileStore, String filePath, URL resource) {
        File targetFile = fileStore.getFile(filePath)
        if (!targetFile.exists()) {
            if (!targetFile.parentFile.exists()) {
                targetFile.parentFile.mkdirs()
            }
            targetFile.createNewFile()
        }

        OutputStream outputStream = targetFile.newOutputStream()
        outputStream.write(resource.openStream().bytes)
        outputStream.flush()
        outputStream.close()
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
