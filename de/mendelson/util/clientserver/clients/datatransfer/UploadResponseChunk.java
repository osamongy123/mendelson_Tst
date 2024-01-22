//$Header: /as2/de/mendelson/util/clientserver/clients/datatransfer/UploadResponseChunk.java 2     4/06/18 12:21p Heller $
package de.mendelson.util.clientserver.clients.datatransfer;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.Serializable;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol: A data chunk has been received
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class UploadResponseChunk extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private String targetHash = null;
    
    public UploadResponseChunk(UploadRequestChunk request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Upload response chunk");
    }

    /**
     * @return the targetHash
     */
    public String getTargetHash() {
        return targetHash;
    }

    /**
     * @param targetHash the targetHash to set
     */
    public void setTargetHash(String targetHash) {
        this.targetHash = targetHash;
    }
}
