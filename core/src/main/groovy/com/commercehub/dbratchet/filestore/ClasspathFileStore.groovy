package com.commercehub.dbratchet.filestore

import com.commercehub.dbratchet.util.ClasspathUtil
import com.commercehub.dbratchet.util.FileUtil

import java.util.regex.Pattern

/**
 * Created by jgelais on 5/15/2014.
 */
class ClasspathFileStore implements FileStore {
    String rootPath

    ClasspathFileStore() {
        this('/')
    }

    ClasspathFileStore(String rootPath) {
        this.rootPath = rootPath
    }

    @Override
    InputStream getFileInputStream(String path) {
        return ClasspathFileStore.getResourceAsStream("${rootPath}${path}")
    }

    @Override
    OutputStream getFileOutputStream(String path) {
        throw new UnsupportedOperationException('Cannot write to resources on classpath')
    }

    @Override
    File getFile(String path) {
        throw new UnsupportedOperationException('Cannot return a File reference for resources on classpath')
    }

    @Override
    List<String> scanRecursivelyForFiles(String path, String filePattern) {
        def list = [] as Queue<String>
        Pattern fileNamePattern = FileUtil.convertWildcardToRegex(filePattern)
        ClasspathUtil.getResources("${rootPath}${path}", fileNamePattern).each { resource ->
            list << new File(resource).name
        }
        return list
    }

    @Override
    String getFileStoreRootURLAsString() {
        return "classpath:${rootPath}"
    }
}
