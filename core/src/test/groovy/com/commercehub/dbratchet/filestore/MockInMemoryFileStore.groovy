package com.commercehub.dbratchet.filestore

/**
 * Created by jaystgelais on 5/19/14.
 */
class MockInMemoryFileStore implements FileStore {
    Map<String, ByteArrayOutputStream> storage = [:]

    @Override
    InputStream getFileInputStream(String path) {
        ByteArrayOutputStream baos = getByteArrayOutputStream(path)
        return (baos) ? new ByteArrayInputStream(baos.toByteArray()) : null
    }

    @Override
    OutputStream getFileOutputStream(String path) {
        return getByteArrayOutputStream(path)
    }

    @Override
    URL getFileAsResource(String path) {
        throw new UnsupportedOperationException()
    }

    @Override
    File getFile(String path) {
        throw new UnsupportedOperationException()
    }

    @Override
    List<String> scanRecursivelyForFiles(String path, String filePattern) {
        return []
    }

    @Override
    String getFileStoreRootURLAsString() {
        throw new UnsupportedOperationException()
    }

    @SuppressWarnings('SynchronizedMethod')
    private synchronized ByteArrayOutputStream getByteArrayOutputStream(String path) {
        if (!storage.containsKey(path)) {
            storage.put(path, new ByteArrayOutputStream())
            println "Created new BAOS for $path"
        }

        return storage.get(path)
    }
}
