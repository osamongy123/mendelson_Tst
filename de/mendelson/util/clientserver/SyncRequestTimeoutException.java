//$Header: /as2/de/mendelson/util/clientserver/SyncRequestTimeoutException.java 1     26.03.21 10:24 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Exception that is thrown if a sync request exceeds the given timeout on the server
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class SyncRequestTimeoutException extends Exception{
    
    public static final int TIMEOUT_TYPE_SEND = 1;
    public static final int TIMEOUT_TYPE_RECEIVE = 2;
    private final ClientServerMessage request;
    private final long timeout;
    private int timeoutType = TIMEOUT_TYPE_RECEIVE;
    
    public SyncRequestTimeoutException(final int TIMEOUT_TYPE, String message, ClientServerMessage request, long timeout){
        super( message );
        this.timeout = timeout;
        this.request = request;
        this.timeoutType = TIMEOUT_TYPE;
    }

    /**
     * @return the request
     */
    public ClientServerMessage getRequest() {
        return request;
    }

    /**
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * @return the timeoutType
     */
    public int getTimeoutType() {
        return timeoutType;
    }

}
