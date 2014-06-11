package com.commercehub.dbratchet.filestore

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Created by jgelais on 5/30/2014.
 */
class FileSystemFileStoreTest {
    public static final String SAMPLE_TEXT = 'some text'
    public static final String ALTERNATE_SAMPLE_TEXT = 'some other text'

    @Rule
    @SuppressWarnings('PublicInstanceField')
    @SuppressWarnings('NonFinalPublicField')
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void testGetFileInputStream() {
        File rootDir = folder.newFolder()
        setupSampleFiles(rootDir)
        FileStore fileStore = new FileSystemFileStore(rootDir)

        assert fileStore.getFileInputStream('some-text.txt').bytes == SAMPLE_TEXT.bytes
    }

    @Test
    void testGetFileOutputStream() {
        File rootDir = folder.newFolder()
        setupSampleFiles(rootDir)
        FileStore fileStore = new FileSystemFileStore(rootDir)
        OutputStream os = fileStore.getFileOutputStream('some-other-text.txt')
        os.write(ALTERNATE_SAMPLE_TEXT.bytes)
        os.close()

        assert fileStore.getFileInputStream('some-other-text.txt').bytes == ALTERNATE_SAMPLE_TEXT.bytes
    }

    @Test
    void testScanRecursivelyForFiles() {
        File rootDir = folder.newFolder()
        setupSampleFiles(rootDir)
        FileStore fileStore = new FileSystemFileStore(rootDir)

        List<String> list = fileStore.scanRecursivelyForFiles('dir-to-scan', 'scan-file-*')
        assert list.size() == 2
        assert list.contains('scan-file-1')
        assert list.contains('scan-file-2')
    }

    private void setupSampleFiles(File rootDir) {
        File fileWithSomeText = new File(rootDir, 'some-text.txt')
        fileWithSomeText.createNewFile()
        fileWithSomeText.text = SAMPLE_TEXT

        File dirToScan = new File(rootDir, 'dir-to-scan')
        dirToScan.mkdir()

        File scanFile1 = new File(dirToScan, 'scan-file-1')
        scanFile1.createNewFile()
        File scanFile2 = new File(dirToScan, 'scan-file-2')
        scanFile2.createNewFile()
        File scanFile3 = new File(dirToScan, 'wont-match-scan-file-pattern')
        scanFile3.createNewFile()
    }
}
