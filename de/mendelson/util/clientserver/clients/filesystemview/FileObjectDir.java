//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/FileObjectDir.java 5     16.11.18 14:35 Heller $
package de.mendelson.util.clientserver.clients.filesystemview;

import java.net.URI;
import java.nio.file.Path;
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
 * @version $Revision: 5 $
 */
public class FileObjectDir extends FileObject {

    public static final long serialVersionUID = 1L;
    private boolean hidden = false;
    private String symbolicLinkTarget = null;
    private boolean isSymbolikLink = false;

    public FileObjectDir(URI fileURI) {
        super( fileURI );
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Paths.get(this.getFileURI()).getFileName().toString());
        if( this.isSymbolikLink){
            builder.append( " -> ");
            if( this.symbolicLinkTarget != null ){
                builder.append( this.symbolicLinkTarget);
            }
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
        if (anObject != null && anObject instanceof FileObjectDir) {
            FileObjectDir otherObject = (FileObjectDir) anObject;
            return (otherObject.getFileURI().equals(this.getFileURI()));
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    /**
     * @return the hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param hidden the hidden to set
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @return the symbolicLinkTarget
     */
    public String getSymbolicLinkTarget() {
        return symbolicLinkTarget;
    }

    /**
     * @param symbolicLinkTarget the symbolicLinkTarget to set
     */
    public void setSymbolicLinkTarget(String symbolicLinkTarget) {
        this.symbolicLinkTarget = symbolicLinkTarget;
    }

    /**
     * @return the isSymbolikLink
     */
    public boolean isSymbolikLink() {
        return isSymbolikLink;
    }

    /**
     * @param isSymbolikLink the isSymbolikLink to set
     */
    public void setIsSymbolikLink(boolean isSymbolikLink) {
        this.isSymbolikLink = isSymbolikLink;
    }
    
}
