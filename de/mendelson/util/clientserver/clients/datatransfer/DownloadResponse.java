//$Header: /oftp2/de/mendelson/util/clientserver/clients/datatransfer/DownloadResponse.java 10    3.03.20 10:26 Heller $
package de.mendelson.util.clientserver.clients.datatransfer;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol
 * @author S.Heller
 * @version $Revision: 10 $
 */
public abstract class DownloadResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private byte[] data = null;
    private long size = 0;

    public DownloadResponse(DownloadRequest request) {
        super(request);
    }

    public void setData(InputStream inStream) throws IOException {
        this.data = inStream.readAllBytes();
    }

    public void setData(byte[] data) throws IOException {
        this.data = data;
    }

    /**
     * @return the data
     */
    public InputStream getDataStream() {
        ByteArrayInputStream inStream = new ByteArrayInputStream(this.data);
        return (inStream);
    }

    /**
     * @return the data
     */
    public byte[] getDataBytes() {
        return data;
    }

    @Override
    public String toString() {
        return ("Download response");
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }
}
