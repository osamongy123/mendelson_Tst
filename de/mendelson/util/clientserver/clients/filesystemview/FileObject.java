//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/FileObject.java 6     15.11.18 11:59 Heller $
package de.mendelson.util.clientserver.clients.filesystemview;

import java.io.Serializable;
import java.net.URI;
import java.nio.file.Paths;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 6 $
 */
public abstract class FileObject implements Serializable, Comparable {

    public static final long serialVersionUID = 1L;
    private URI fileURI;
    private String absolutePathDisplayOnServerSide;

    public FileObject(URI fileURI) {
        this.fileURI = fileURI;
        this.absolutePathDisplayOnServerSide = Paths.get(fileURI).toAbsolutePath().toString();
    }
    
    /**
     * @return the file
     */
    public URI getFileURI() {
        return this.fileURI;
    }


    @Override
    public int compareTo(Object otherObject) {
        if (otherObject == null || this.fileURI == null) {
            return (0);
        }
        if (!(otherObject instanceof FileObject)) {
            return (0);
        }
        FileObject otherFileObject = (FileObject) otherObject;
        return (Paths.get(this.fileURI).getFileName().toString()
                .compareTo(Paths.get(otherFileObject.getFileURI()).getFileName().toString()));
    }

    /**
     * @return the absolutePathDisplayOnServerSide
     */
    public String getAbsolutePathDisplayOnServerSide() {
        return absolutePathDisplayOnServerSide;
    }

}
