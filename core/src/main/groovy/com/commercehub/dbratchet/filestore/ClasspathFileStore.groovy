package com.commercehub.dbratchet.filestore

import com.commercehub.dbratchet.util.ClasspathUtil
import com.commercehub.dbratchet.util.FileUtil

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by jgelais on 5/15/2014.
 */
class ClasspathFileStore implements FileStore {
    @Override
    InputStream getFileInputStream(String path) {
        return ClasspathFileStore.getResourceAsStream(path)
    }

    @Override
    OutputStream getFileOutputStream(String path) {
        throw new UnsupportedOperationException('Cannot write to resources on classpath')
    }

    @Override
    URL getFileAsResource(String path) {
        return ClasspathFileStore.getResource(path)
    }

    @Override
    File getFile(String path) {
        throw new UnsupportedOperationException('Cannot return a File reference for resources on classpath')
    }

    // TODO Write a Unit test for this
    @Override
    List<String> scanRecursivelyForFiles(String path, String filePattern) {
        def list = [] as Queue<String>
        Pattern fileNamePattern = FileUtil.convertWildcardToRegex(filePattern)
        Pattern searchPattern = Pattern.compile("${path}/.*${fileNamePattern.pattern()}")
        ClasspathUtil.getResources(searchPattern).each { resource ->
            Matcher m = fileNamePattern.matcher(resource)
            if (m.matches()) {
                list << m.group()
            }
        }
        return list
    }
}
