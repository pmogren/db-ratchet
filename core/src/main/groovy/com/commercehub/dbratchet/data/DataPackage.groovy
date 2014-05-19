package com.commercehub.dbratchet.data

import com.commercehub.dbratchet.filestore.FileStore

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/25/13
 * Time: 1:51 PM
 */
class DataPackage {
    String name
    List<String> tables = [] as Queue<String>
    private final FileStore fileStore

    DataPackage(FileStore fileStore) {
        this.fileStore = fileStore
    }

    OutputStream getDataOutputStream() {
        return fileStore.getFileOutputStream("data/packages/${name}.xml")
    }

    InputStream getDataInputStream() {
        return fileStore.getFileInputStream("data/packages/${name}.xml")
    }
}
