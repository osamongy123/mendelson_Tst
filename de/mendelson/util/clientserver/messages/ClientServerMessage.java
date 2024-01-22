//$Header: /as2/de/mendelson/util/clientserver/messages/ClientServerMessage.java 8     31.10.18 13:55 Heller $
package de.mendelson.util.clientserver.messages;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Superclass of all messages for the client server protocol
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class ClientServerMessage implements Serializable{
       
    public static final long serialVersionUID = 1L;
    private static long referenceIdCounter = 0;
    private long referenceId = 0;
    private boolean _syncRequest = false;
    private String pid;

    public ClientServerMessage(){
        this.referenceId = getNextReferenceId();
        this.pid = ManagementFactory.getRuntimeMXBean().getName();        
    }

    /**Returns the next unique reference id, thread safe*/
    public static synchronized long getNextReferenceId(){
        referenceIdCounter++;
        return( referenceIdCounter);
    }

    public Long getReferenceId(){
        return( Long.valueOf(this.referenceId));
    }

   
    /** Internal method, do NOT use it
     * @return the _syncRequest
     */
    public boolean _isSyncRequest() {
        return _syncRequest;
    }

    /** Internal method, do NOT use it
     * @param syncRequest the _syncRequest to set
     */
    public void _setSyncRequest(boolean syncRequest) {
        this._syncRequest = syncRequest;
    }

    /**
     * @param referenceId the referenceId to set
     */
    protected void _setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }
    
    /**
     * @return the pid
     */
    public String getPID() {
        return pid;
    }

}
