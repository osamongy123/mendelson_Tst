//$Header: /as2/de/mendelson/comm/as2/message/ByteStorage.java 12    3.08.21 17:48 Heller $
package de.mendelson.comm.as2.message;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Container that stores byte arrays
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class ByteStorage implements Serializable {

    public static final long serialVersionUID = 1L;
    
    /**Switch to file storage at 20 MB data size*/
    private final int THRESHOLD = 20* 1024 * 1024;
    private IByteStorage storage = null;

    public ByteStorage() {
    }

    /**Returns the actual stored data size*/
    public int getSize() {
        if (this.storage == null) {
            return (0);
        }
        return (this.storage.getSize());
    }

    /**store a byte array*/
    public void put(byte[] data) throws Exception {
        //release an existing storage if it exists
        if (this.storage != null) {
            this.storage.release();
        }
        if (data.length > THRESHOLD) {
            this.storage = new ByteStorageImplFile();
        } else {
            this.storage = new ByteStorageImplMemory();
        }
        this.storage.put(data);
    }

    public byte[] get() throws Exception {
        if (this.storage == null) {
            return (new byte[0]);
        } else {
            return (this.storage.get());
        }
    }

    /**Returns an input stream to read directly from the underlaying buffer*/
    public InputStream getInputStream() throws Exception {
        return (this.storage.getInputStream());
    }
    
    /**Releases the allocated resources*/
    public void release(){
        if( this.storage != null ){
            this.storage.release();
        }
    }
}
