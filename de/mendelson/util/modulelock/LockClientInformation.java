//$Header: /as2/de/mendelson/util/modulelock/LockClientInformation.java 3     1.11.18 10:28 Heller $
package de.mendelson.util.modulelock;

import java.io.Serializable;
import java.util.Objects;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores information about the client that locks a module or requests a module
 * lock
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class LockClientInformation implements Serializable {

    public static final long serialVersionUID = 1L;
    private String username;
    private String clientIP;
    private String uniqueid;
    private String pid;

    public LockClientInformation(String username, String clientIP, String uniqueid, String pid) {
        this.username = username;
        this.clientIP = clientIP;
        this.uniqueid = uniqueid;
        this.pid = pid;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the clientIP
     */
    public String getClientIP() {
        return clientIP;
    }

    /**
     * @return the unique id of the client
     */
    public String getUniqueid() {
        return uniqueid;
    }

    /**
     * Overwrite the equal method of object
     *
     * @param anObject object ot compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof LockClientInformation) {
            LockClientInformation entry = (LockClientInformation) anObject;
            return (this.uniqueid.equals(entry.uniqueid));
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.uniqueid);
        return hash;
    }
    
    /**Returns the client side process id
     * @return the pid
     */
    public String getPid() {
        return pid;
    }

    /**Sets the client side process id
     * @param pid the pid to set
     */
    public void setPid(String pid) {
        this.pid = pid;
    }

}
