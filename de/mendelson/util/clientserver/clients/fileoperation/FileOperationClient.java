//$Header: /as2/de/mendelson/util/clientserver/clients/fileoperation/FileOperationClient.java 3     13.11.18 11:58 Heller $
package de.mendelson.util.clientserver.clients.fileoperation;

import de.mendelson.util.clientserver.BaseClient;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Performs file operations on the server side and returns a result
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class FileOperationClient {

    private final BaseClient baseClient;
    /**
     * Stores the last exception if there was any on the server side to get
     * additional information if something failed
     */
    private Throwable lastException = null;

    public FileOperationClient(BaseClient baseClient) {
        this.baseClient = baseClient;
    }

    /**
     * Rename a file on the server
     */
    public boolean rename(String oldName, String newName) {
        this.lastException = null;
        FileRenameRequest request = new FileRenameRequest();
        request.setOldName(oldName);
        request.setNewName(newName);
        FileRenameResponse response = (FileRenameResponse) this.baseClient.sendSync(request);
        if (response != null) {
            this.lastException = response.getException();
            return (response.getSuccess());
        } else {
            return (false);
        }
    }

    /**
     * Delete a file on the server
     */
    public boolean delete(String filename) {
        this.lastException = null;
        FileDeleteRequest request = new FileDeleteRequest();
        request.setFilename(filename);
        FileDeleteResponse response = (FileDeleteResponse) this.baseClient.sendSync(request);
        if (response != null) {
            this.lastException = response.getException();
            return (response.getSuccess());
        } else {
            return (false);
        }
    }

    /**
     * @return the lastException
     */
    public Throwable getLastException() {
        return (this.lastException);
    }

}
