//$Header: /as2/de/mendelson/comm/as2/message/ByteStorageImplFile.java 12    3.08.21 16:45 Heller $
package de.mendelson.comm.as2.message;

import de.mendelson.util.AS2Tools;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Container that stores byte arrays in a temp file
 *
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class ByteStorageImplFile implements IByteStorage {

    /**
     * IByteStorage extends Serializable
     */
    public static final long serialVersionUID = 1L;
    //Use a String here to keep this serializable
    private String fullFilename = null;

    public ByteStorageImplFile() {
    }

    @Override
    /**
     * Returns the actual stored data size
     */
    public int getSize() {
        if (this.fullFilename == null) {
            return (0);
        }
        try {
            return ((int) Files.size(Paths.get(this.fullFilename)));
        } catch (IOException e) {
            return (0);
        }
    }

    @Override
    /**
     * store a byte array
     */
    public void put(byte[] data) throws Exception {
        //create the file storage
        Path tempFile = AS2Tools.createTempFile("AS2ByteStorage", ".bin");
        this.fullFilename = tempFile.toAbsolutePath().toString();
        Files.write(tempFile, data,
                StandardOpenOption.SYNC,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    @Override
    public byte[] get() throws Exception {
        if (this.fullFilename == null) {
            return (new byte[0]);
        }
        return (Files.readAllBytes(Paths.get(this.fullFilename)));
    }

    @Override
    /**
     * Returns an input stream to read directly from the underlaying buffer
     */
    public InputStream getInputStream() throws Exception {
        return (Files.newInputStream(Paths.get(this.fullFilename)));
    }

    @Override
    public void release() {
        try {
            Files.delete(Paths.get(this.fullFilename));
        } catch (IOException e) {
            //nop
        } finally {
            this.fullFilename = null;
        }
    }

}
