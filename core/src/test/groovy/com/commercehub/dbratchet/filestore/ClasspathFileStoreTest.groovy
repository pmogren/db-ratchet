package com.commercehub.dbratchet.filestore

import com.commercehub.dbratchet.util.FileUtil

/**
 * Created by jgelais on 5/16/2014.
 */
class ClasspathFileStoreTest extends GroovyTestCase {
    private static final String RESOURCE_BASE = 'com/commercehub/dbratchet/filestore'
    private static final String TEST_RESOURCE = 'test-resource.txt'
    private static final String TEST_RESOURCE_CONTENT = 'This is some text.'

    void testGetFileInputStream() {
        FileStore filestore = new ClasspathFileStore()
        String text = filestore.getFileInputStream("${RESOURCE_BASE}/${TEST_RESOURCE}").text
        assert TEST_RESOURCE_CONTENT == text
    }

    @SuppressWarnings('CatchException')
    void testGetFileOutputStream() {
        FileStore filestore = new ClasspathFileStore()
        try {
            filestore.getFileOutputStream("${RESOURCE_BASE}/${TEST_RESOURCE}")
            fail('ClasspathFileStore.getFileOutputStream() should fail with UnsupportedOperationException')
        } catch (Exception ex) {
            assert ex instanceof UnsupportedOperationException
        }
    }

    @SuppressWarnings('CatchException')
    void testGetFile() {
        FileStore filestore = new ClasspathFileStore()
        try {
            filestore.getFile("${RESOURCE_BASE}/${TEST_RESOURCE}")
            fail('ClasspathFileStore.getFile() should fail with UnsupportedOperationException')
        } catch (Exception ex) {
            assert ex instanceof UnsupportedOperationException
        }
    }

    void testScanRecursivelyForFiles() {
        FileStore filestore = new ClasspathFileStore()
        List<String> list = filestore.scanRecursivelyForFiles(RESOURCE_BASE, 'test-*.txt')
        assert list.size() == 3
        assert list.contains('test-resource.txt')
        assert list.contains('test-resource1.txt')
        assert list.contains('test-resource2.txt')

        assert !filestore.scanRecursivelyForFiles('org/flywaydb/core', 'Flyway.class').isEmpty()
    }

    void testThatIUnderstandRegex() {
        assert FileUtil.convertWildcardToRegex('test-*.txt').matcher('test-resource1.txt').matches()
    }
}
