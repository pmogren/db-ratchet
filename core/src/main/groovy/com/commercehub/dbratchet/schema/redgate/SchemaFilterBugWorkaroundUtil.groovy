package com.commercehub.dbratchet.schema.redgate

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/16/13
 * Time: 1:51 PM
 */
@SuppressWarnings('DuplicateStringLiteral')
class SchemaFilterBugWorkaroundUtil {
    final List<String> allowedTopLevelSchemaFolders = ['Functions', 'Security', 'Stored Procedures', 'Tables', 'Views']
    final Map<String, List<String>> allowedSchemaSubFolders = ['Security': ['Schemas']]
    final Map<String, List<String>> allowedFilePatterns = ['Schemas': ['.*\\.sql'],
            'default': ['.*\\.sql', 'RedGateDatabaseInfo.xml']]



    /**
     * This method works around a bug where the /filter switch on SQLCompare does not actually apply your
     * filter. This bug is tracked under [SC-6447]
     *
     * @param schemaDir
     */
    void removeFilteredFiles(File schemaDir) {
        schemaDir.listFiles().each { dir->
            if (dir.isDirectory() && !allowedTopLevelSchemaFolders.contains(dir.name)) {
                dir.deleteDir()
            } else {
                removeSubFolders(dir)
            }
        }

        purgeUnwantedFilesFromDir(schemaDir)
    }

    private void removeSubFolders(File dir) {
        dir.listFiles().each { subDir->
            if (subDir.isDirectory() && !allowedSchemaSubFolders.get(dir.name).contains(subDir.name)) {
                subDir.deleteDir()
            } else {
                removeSubFolders(subDir)
            }
        }

        purgeUnwantedFilesFromDir(dir)
    }

    private void purgeUnwantedFilesFromDir(File dir) {
        dir.listFiles().each { file->
            if (file.isFile()) {
                boolean matchesPattern = false
                getAllowedFileNamePatternsForDir(dir).each { pattern->
                    matchesPattern = matchesPattern || file.name.matches(pattern)
                }

                if (!matchesPattern) {
                   file.delete()
                }
            }
        }
    }

    private List<String> getAllowedFileNamePatternsForDir(File dir) {
        return allowedFilePatterns.get(dir.name) ?: allowedFilePatterns.get('default')
    }
}
