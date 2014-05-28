package com.commercehub.dbratchet

import static org.junit.Assume.assumeTrue

import com.commercehub.dbratchet.databases.DatabaseClient
import com.commercehub.dbratchet.databases.DatabaseClientFactory
import com.commercehub.dbratchet.databases.SchemaInformationService
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.filestore.FileSystemFileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.redgate.SqlCompareSchemaDifferenceEngine
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Created by jgelais on 5/28/2014.
 */
class PushOperationTest {
    static final String SAMPLE_STUDENTS_SQL = '/com/commercehub/dbratchet/schema/redgate/dbo.Students.sql'

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
        File studentsSql = fileStore.getFile('redgate-schema/Tables/dbo.Students.sql')
        if (!studentsSql.exists()) {
            if (!studentsSql.parentFile.exists()) {
                studentsSql.parentFile.mkdirs()
            }
            studentsSql.createNewFile()
        }
        populateStudentsSqlFromSample(studentsSql)
        databaseConfig = setupTransientDatabase()

        Operation pushOp = new PushOperation(schemaConfig, databaseConfig)
        assert pushOp.configured
        assert pushOp.run()

        DatabaseClient databaseClient = DatabaseClientFactory.getDatabaseClient(databaseConfig.vendor)
        SchemaInformationService schemaInformationService = databaseClient.schemaInformationService
        assert schemaInformationService.isTableInDatabase(databaseConfig, 'dbo.Students')
    }

    private populateStudentsSqlFromSample(File studentsSql) {
        OutputStream outputStream = studentsSql.newOutputStream()
        outputStream.write(PushOperation.getResourceAsStream(SAMPLE_STUDENTS_SQL).bytes)
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
