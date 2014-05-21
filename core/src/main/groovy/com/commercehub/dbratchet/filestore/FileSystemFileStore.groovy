package com.commercehub.dbratchet.filestore

import com.commercehub.dbratchet.util.FileUtil
import groovy.io.FileType

import java.util.regex.Pattern

/**
 * Created by jgelais on 5/15/2014.
 */
class FileSystemFileStore implements FileStore {
    @Override
    InputStream getFileInputStream(String path) {
        return getFile(path).newInputStream()
    }

    @Override
    OutputStream getFileOutputStream(String path) {
        File file = getFile(path)
        ensureFileExistence(file)
        return file.newDataOutputStream()
    }

    @Override
    File getFile(String path) {
        return new File(path)
    }

    @Override
    List<String> scanRecursivelyForFiles(String path, String filePattern) {
        def list = [] as Queue<String>
        File rootDir = getFile(path)

        if (!rootDir.exists()) {
            return list
        }

        if (!rootDir.isDirectory()) {
            throw new IllegalArgumentException("path [${path}] is not a directory!")
        }
        Pattern pattern = FileUtil.convertWildcardToRegex(filePattern)
        rootDir.eachFileRecurse(FileType.FILES) { file->
            if (pattern.matcher(file.name).matches()) {
                list << file.name
            }
        }

        return list
    }

    @Override
    String getFileStoreRootURLAsString() {
        return "filesystem:${getFile('.').path}/"
    }

    private void ensureFileExistence(File file) {
        if (!file.exists()) {
            File parentDir = file.parentFile
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
            file.createNewFile()
        }
    }
}
