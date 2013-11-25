package com.commercehub.dbratchet

import com.commercehub.dbratchet.schema.SchemaConfig

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/22/13
 * Time: 12:01 PM
 */
class ServerCredentialStore {
    static final String CRED_STORE_FILE_NAME = 'serverCredentialStore.xml'

    Map<String, DatabaseConfig> credentials = [:]
    SchemaConfig schemaConfig

    ServerCredentialStore(SchemaConfig schemaConfig) {
        this.schemaConfig = schemaConfig
        load()
    }

    void store(String alias, DatabaseConfig databaseConfig) {
        credentials.put(alias, databaseConfig)
    }

    void remove(String alias) {
        credentials.remove(alias)
    }

    void save() {
        File outputFile = credentialStoreFile
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }

        def builder = new groovy.xml.MarkupBuilder(outputFile.newPrintWriter())
        builder.credentials {
            credentials.keySet().each { alias->
                DatabaseConfig dbConfig = credentials.get(alias)
                builder.'db-config'(alias: alias, server: dbConfig.server, vendor: dbConfig.vendor,
                        user: dbConfig.user, password: dbConfig.password)
            }
        }
    }

    private void load() {
        File inputFile = credentialStoreFile
        if (inputFile.exists()) {
            def xmlConfig = new XmlSlurper().parse(inputFile)
            xmlConfig.'db-config'.each { dbConfigElement->
                String alias = dbConfigElement.@alias.text()
                DatabaseConfig dbConfig = new DatabaseConfig()
                        .setServer(dbConfigElement.@server.text())
                        .setVendor(dbConfigElement.@vendor.text())
                if (dbConfigElement.@user.size() > 0) {
                    dbConfig.setUser(dbConfigElement.@user.text())
                            .setPassword(dbConfigElement.@password.text())
                }

                credentials.put(alias, dbConfig)
            }
        }
    }

    private File getCredentialStoreFile() {
        return new File(schemaConfig.rootDir, CRED_STORE_FILE_NAME)
    }

    DatabaseConfig get(String alias) {
        return credentials.get(alias)
    }
}
