//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/FileObjectRoot.java 4     15.11.18 17:00 Heller $
package de.mendelson.util.clientserver.clients.filesystemview;

import java.net.URI;
import javax.swing.Icon;

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
 * @version $Revision: 4 $
 */
public class FileObjectRoot extends FileObject {

    public static final long serialVersionUID = 1L;
    /**
     * A special name for this root - e.g. a drive name, might be null if no
     * name exists
     */
    private final String name;
    /**Server side icon for the root - might be null*/
    private final Icon serversideIcon;

    public FileObjectRoot(URI fileURI, String name, Icon serversideIcon) {
        super(fileURI);
        this.name = name;
        this.serversideIcon = serversideIcon;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getAbsolutePathDisplayOnServerSide());
        if( this.name != null && this.name.trim().length() > 0 ){
            builder.append( " [");
            builder.append( this.name);
            builder.append( "]");
        }
        return( builder.toString());
    }

    /**
     * Overwrite the equal method of object
     *
     * @param anObject object to compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof FileObjectRoot) {
            FileObjectRoot otherObject = (FileObjectRoot) anObject;
            return (otherObject.getFileURI().equals(this.getFileURI()));
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    /**
     * @return the serversideIcon
     */
    public Icon getServersideIcon() {
        return serversideIcon;
    }

}
