package com.commercehub.dbratchet.schema.redgate

import com.commercehub.dbratchet.DatabaseConfig
import com.commercehub.dbratchet.schema.SchemaConfig
import com.commercehub.dbratchet.schema.SchemaDifferenceEngine
import com.commercehub.dbratchet.util.FileUtil

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 11/16/13
 * Time: 12:04 PM
 */
class SqlCompareSchemaDifferenceEngine implements SchemaDifferenceEngine {
    public static final String SCHEMA_DIR = 'redgate-schema'
    public static final String CONFIG_DIR = 'redgate-config'
    public static final String FILTER_FILE_NAME = 'filter.scpf'
    public static final String FILE_FILTER_FILE_NAME = 'file-filter-config.groovy'
    public static final String ENGINE_NAME = 'redgate'
    private static final String REDGATE_DATABASE_INFO_FILE_NAME = 'RedGateDatabaseInfo.xml'

    SchemaConfig schemaConfig
    SqlCompare sqlCompare

    final String name = ENGINE_NAME
    final File fileStoreDir

    private final File configDir
    private final File filterFile
    private final File filterConfigFile

    SqlCompareSchemaDifferenceEngine(SchemaConfig config) {
        sqlCompare = new SqlCompareFactory().newSqlCompare()
        schemaConfig = config
        fileStoreDir = schemaConfig.fileStore.getFile(SCHEMA_DIR)
        configDir = schemaConfig.fileStore.getFile(CONFIG_DIR)
        filterFile = new File(configDir, FILTER_FILE_NAME)
        filterConfigFile = new File(configDir, FILE_FILTER_FILE_NAME)

        if (filterFile.exists()) {
            sqlCompare.setFilter(filterFile.absolutePath)
        }
    }

    void setTargetDatabase(DatabaseConfig dbConfig) {
        sqlCompare.targetDatabaseConfig = dbConfig
        if (sqlCompare.isDoMakeScripts()) {
            sqlCompare.setDoMakeScripts(false)
        }
    }

    void setSourceDatabase(DatabaseConfig dbConfig) {
        sqlCompare.srcDatabaseConfig = dbConfig
    }

    void useFileStoreAsSource() {
        sqlCompare.srcScripts = fileStoreDir.absolutePath
    }

    void useFileStoreAsTarget() {
        sqlCompare.targetScripts = fileStoreDir.absolutePath
        if (!isSchemaStorePopulated()) {
            sqlCompare.setDoMakeScripts(true)
        }
    }

    boolean pushSourceToTarget() {
        boolean isSuccessful = sqlCompare.setDoSynch(true).run()
        if (isSuccessful) {
            if (isInitialCapture()) {
                new SchemaFilterBugWorkaroundUtil().removeFilteredFiles(fileStoreDir)
            }
            if (isTargetFileStore()) {
                processFileFilters()
            }
        }
        return isSuccessful
    }

    boolean generateScriptToBuildSourceToTarget(File script) {
        sqlCompare.setScriptFile(script.absolutePath)
                .setDoSynch(false)
                .run()
    }

    @Override
    void initializeSchemaStore() {
        if (!fileStoreDir.exists()) {
            fileStoreDir.mkdir()
        }

        if (!configDir.exists()) {
            configDir.mkdir()
        }

        if (!filterFile.exists()) {
            filterFile.createNewFile()

            this.getClass().getResource('/templates/redgate/filter.scpf').withInputStream { is ->
                filterFile << is
            }
        }

        if (!filterConfigFile.exists()) {
            filterConfigFile.createNewFile()

            this.getClass().getResource('/templates/redgate/file-filter-config.groovy').withInputStream { is ->
                filterConfigFile << is
            }
        }
    }

    private boolean isSchemaStorePopulated() {
        (new File(fileStoreDir, REDGATE_DATABASE_INFO_FILE_NAME)).exists()
    }

    private boolean isInitialCapture() {
        return sqlCompare.doMakeScripts && sqlCompare.doSynch
    }

    private boolean isTargetFileStore() {
        return sqlCompare.targetScripts
    }

    private void processFileFilters() {
        if (filterConfigFile.exists()) {
            def config = new ConfigSlurper().parse(filterConfigFile.toURL())
            config.filters.keySet().each { path ->
                def filter = config.filters[path]
                File filteredDir = new File(fileStoreDir, path)
                if (filteredDir.exists()) {
                    if (filter.filterType == 'whitelist') {
                        doWhiteListFilter(filteredDir, filter.patterns)
                    }
                    if (filter.filterType == 'blacklist') {
                        doBlackListFilter(filteredDir, filter.patterns)
                    }
                }
            }
        }
    }

    private void doWhiteListFilter(File dir, def patterns) {
        def filesToKeep = []
        patterns.each { pattern ->
            dir.eachFileMatch(FileUtil.convertWildcardToRegex(pattern)) { file ->
                filesToKeep.add(file)
            }
        }
        dir.eachFile { file ->
            if (!filesToKeep.contains(file)) {
                file.delete()
            }
        }
    }

    private void doBlackListFilter(File dir, def patterns) {
        patterns.each { pattern ->
            dir.eachFileMatch(FileUtil.convertWildcardToRegex(pattern)) { file ->
                file.delete()
            }
        }
    }

}
