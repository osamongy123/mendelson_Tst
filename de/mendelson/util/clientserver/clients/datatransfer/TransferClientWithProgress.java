//$Header: /oftp2/de/mendelson/util/clientserver/clients/datatransfer/TransferClientWithProgress.java 4     17.02.21 11:49 Heller $
package de.mendelson.util.clientserver.clients.datatransfer;

import de.mendelson.util.ProgressPanel;
import de.mendelson.util.clientserver.BaseClient;
import java.io.InputStream;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Requests downloads from and sends new uploads to the server
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class TransferClientWithProgress extends TransferClient {

    private ProgressPanel progressPanel;

    public TransferClientWithProgress(BaseClient baseClient, ProgressPanel progressPanel) {
        super(baseClient);
        this.progressPanel = progressPanel;
    }

    /**Sends the data of the inputstream synced to the server and returns a unique number from the server
     * for the upload process
     * Warning: This does also transfer files with the size of 0 bytes to the server
     * Please be aware of this at the server side
     */
    public String uploadChunkedWithProgress(InputStream inStream, String display, int maxBytes) throws Throwable {
        String targetHash = null;
        int readBytes = 0;
        String uniqueId = display + String.valueOf(maxBytes) + inStream.hashCode() + System.currentTimeMillis();
        try {
            this.progressPanel.startProgress(display, uniqueId, 0, maxBytes);
            while (true) {
                byte[] data = super.copyBytesFromStream(inStream, TransferClient.CHUNK_SIZE_IN_BYTES);
                if (data != null) {                                      
                    readBytes += data.length;
                    UploadRequestChunk uploadRequest = new UploadRequestChunk();
                    uploadRequest.setData(data);
                    uploadRequest.setTargetHash(targetHash);
                    UploadResponseChunk response = (UploadResponseChunk) super.getBaseClient().sendSync(uploadRequest, TransferClient.TIMEOUT);
                    if (response != null) {
                        targetHash = response.getTargetHash();
                    }
                    //display this progress in the progress bar
                    this.progressPanel.setProgressValue(uniqueId, readBytes);
                    //special case: the transferred file has the size 0
                    if( data.length == 0){
                        break;
                    }
                } else {
                    //file seems to be transferred or stream does not exist
                    break;
                }
            }
        } finally {
            this.progressPanel.stopProgressIfExists(uniqueId);
        }
        return (targetHash);
    }
}
