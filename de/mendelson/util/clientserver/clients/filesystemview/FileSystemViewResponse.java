//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/FileSystemViewResponse.java 5     6.11.18 16:59 Heller $
package de.mendelson.util.clientserver.clients.filesystemview;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.util.List;
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
 * @version $Revision: 5 $
 */
public class FileSystemViewResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private FileObject parameterFile = null;
    private List<FileObject> parameterFileArray = null;
    private String parameterString = null;
    /**As this object is always created on the server side this gives additional information about the server side
     * path separator*/
    private final String serverSideFileSeparator;

    public FileSystemViewResponse(FileSystemViewRequest request) {
        super(request);
        this.serverSideFileSeparator = FileSystems.getDefault().getSeparator();
    }

    /**
     * @return the parameterFile
     */
    public FileObject getParameterFile() {
        return parameterFile;
    }

    /**
     * @param parameterFile the parameterFile to set
     */
    public void setParameterFile(FileObject parameterFile) {
        this.parameterFile = parameterFile;
    }

    /**
     * @return the parameterFileArray
     */
    public List<FileObject> getParameterFileArray() {
        return parameterFileArray;
    }

    /**
     * @param parameterFileArray the parameterFileArray to set
     */
    public void setParameterFileArray(List<FileObject> parameterFileArray) {
        this.parameterFileArray = parameterFileArray;
    }

    /**
     * @return the parameterString
     */
    public String getParameterString() {
        return parameterString;
    }

    /**
     * @param parameterString the parameterString to set
     */
    public void setParameterString(String parameterString) {
        this.parameterString = parameterString;
    }

    /**
     * @return the serverSideFileSeparator
     */
    public String getServerSideFileSeparator() {
        return serverSideFileSeparator;
    }
}
