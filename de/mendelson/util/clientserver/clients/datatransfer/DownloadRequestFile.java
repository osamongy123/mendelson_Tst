//$Header: /as2/de/mendelson/util/clientserver/clients/datatransfer/DownloadRequestFile.java 2     4/06/18 12:21p Heller $
package de.mendelson.util.clientserver.clients.datatransfer;

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
 * @version $Revision: 2 $
 */
public class DownloadRequestFile extends DownloadRequest implements Serializable{

    public static final long serialVersionUID = 1L;
    private String filename = null;

    @Override
    public String toString(){
        return( "Download request file" );
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    

}
