package com.commercehub.dbratchet

import com.commercehub.dbratchet.databases.DatabaseVendor
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.filestore.FileSystemFileStore
import com.commercehub.dbratchet.schema.SchemaConfig
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Created by jaystgelais on 5/22/14.
 */
class StoreOperationTest {
    public static final String ALIAS = 'alias'

    @Rule
    @SuppressWarnings('PublicInstanceField')
    @SuppressWarnings('NonFinalPublicField')
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void testStoreAndRetrieval() {
        FileStore fileStore = new FileSystemFileStore(folder.newFolder())
        DatabaseConfig databaseConfig = new DatabaseConfig()
                .setServer('myserv')
                .setVendor(DatabaseVendor.DERBY)
                .setUser('myuser')
                .setPassword('mypassword')
        Operation storeOp = new StoreOperation(new SchemaConfig(fileStore), ALIAS, databaseConfig)
        assert storeOp.configured
        assert storeOp.run()

        ServerCredentialStore credentialStore = new ServerCredentialStore(new SchemaConfig(fileStore))
        assert credentialStore.get(ALIAS) == databaseConfig
        println credentialStore.toString()
    }
}
