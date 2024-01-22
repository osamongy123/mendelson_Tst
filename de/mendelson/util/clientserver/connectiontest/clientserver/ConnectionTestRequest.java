//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/clientserver/ConnectionTestRequest.java 5     16/06/22 11:54 Heller $
package de.mendelson.util.clientserver.connectiontest.clientserver;

import de.mendelson.util.clientserver.connectiontest.ConnectionTest;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
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
public class ConnectionTestRequest extends ClientServerMessage implements Serializable {
    
    public static final long serialVersionUID = 1L;
    private String[] protocols = ConnectionTest.DEFAULT_TLS_PROTOCOL_LIST;
    private String host = null;
    private int port;
    private long timeout = TimeUnit.SECONDS.toMillis(2);
    /**Some additional information for the log etc*/
    private String partnerName = null;
    private int partnerRole = ConnectionTest.PARTNER_ROLE_REMOTE_PARTNER;

    /**
     * @param host Host IP to test to
     * @param port Port to test to
     * @param protocols List of TLS protocols that are allowed
     * @param partnerName Name of the partner to display in the log and result
     * @param partnerRole One of the constants defined in ConnectionTest
     */
    public ConnectionTestRequest(String host, int port, String[] protocols, String partnerName, int partnerRole) {        
        this.protocols = protocols;
        if( this.protocols == null ){
            this.protocols = new String[0];
        }
        this.host = host;
        this.port = port;
        this.partnerName = partnerName;
        this.partnerRole = partnerRole;
    }
    
    /**Performs a TLS connection test with the default TLS protocols if ssl is set.
     * To specify the used protocols use the other constructor
     * @param host
     * @param port
     * @param ssl 
     * @param partnerName Name of the partner to display in the log and result
     * @param partnerRole One of the constants defined in ConnectionTest
     */
    public ConnectionTestRequest(String host, int port, boolean ssl, String partnerName, int partnerRole) {
        if( ssl ){
            this.protocols = ConnectionTest.DEFAULT_TLS_PROTOCOL_LIST;
        }else{
            this.protocols = new String[0];
        }
        this.host = host;
        this.port = port;
        this.partnerName = partnerName;
        this.partnerRole = partnerRole;
    }

    @Override
    public String toString() {
        return ("Connection test request");
    }

    /**
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * @return an empty array if this is a non SSL request
     */
    public String[] getTLSProtocols() {
        return( this.protocols );
    }

    public boolean getSSL(){
        return( this.protocols != null && this.protocols.length > 0);
    }
    
    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
    
    /**
     * @return the partnerName - may return null
     */
    public String getPartnerName() {
        return partnerName;
    }

    /**
     * @return the partnerRole
     */
    public int getPartnerRole() {
        return partnerRole;
    }

    
}
