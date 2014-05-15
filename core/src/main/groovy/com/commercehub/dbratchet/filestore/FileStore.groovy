package com.commercehub.dbratchet.filestore

/**
 * Created by jgelais on 5/15/2014.
 */
interface FileStore {
    InputStream getFileInputStream(String path)
    OutputStream getFileOutputStream(String path)
    URL getFileAsResource(String path)
    File getFile(String path)
    List<String> scanRecursivelyForFiles(String path, String filePattern)
}