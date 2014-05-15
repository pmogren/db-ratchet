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
        return getFile(path).newDataOutputStream()
    }

    @Override
    URL getFileAsResource(String path) {
        return getFile(path).toURI().toURL()
    }

    @Override
    File getFile(String path) {
        return new File(path)
    }

    @Override
    List<String> scanRecursivelyForFiles(String path, String filePattern) {
        File rootDir = getFile(path)
        if (!rootDir.isDirectory()) {
            throw new IllegalArgumentException("path [${path}] is not a directory!")
        }
        def list = [] as Queue<String>
        Pattern pattern = FileUtil.convertWildcardToRegex(filePattern)
        rootDir.eachFileRecurse(FileType.FILES) { file->
            if (pattern.matcher(file.path).matches()) {
                list << file.path
            }
        }

        return list
    }
}
