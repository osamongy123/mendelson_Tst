//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/ConnectionTestResult.java 4     16/06/22 11:54 Heller $
package de.mendelson.util.clientserver.connectiontest;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.security.cert.X509Certificate;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores the results of a connection test
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ConnectionTestResult implements Serializable {
    
    
    public static final long serialVersionUID = 1L;
    private boolean connectionIsPossible = false;
    private boolean oftpServiceFound = false;
    private X509Certificate[] foundCertificates = null;
    private Exception exception = null;
    private String protocol = null;
    private InetSocketAddress testedRemoteAddress = null;
    private boolean wasSSLTest = false;
    private String usedCipherSuite = null;
    private String[] supportedCipherSuites = null;
    private String[] enabledCipherSuites = null;    
    private String senderName = null;
    private String receiverName = null;
    private int partnerRole = ConnectionTest.PARTNER_ROLE_REMOTE_PARTNER;

    public ConnectionTestResult(InetSocketAddress testedRemoteAddress, boolean wasSSLTest,
            String senderName, String receiverName, final int PARTNER_ROLE){
        this.testedRemoteAddress = testedRemoteAddress;
        this.wasSSLTest = wasSSLTest;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.partnerRole = PARTNER_ROLE;
    }    
    
    public void setUsedCipherSuite( String usedCipherSuite){
        this.usedCipherSuite = usedCipherSuite;
    }
    
    public void setSupportedCipherSuites(String[] supportedCipherSuites){
        this.supportedCipherSuites = supportedCipherSuites;        
    }
    
    public void setEnabledCipherSuites(String[] enabledCipherSuites){
        this.enabledCipherSuites = enabledCipherSuites;
    }    
    
    public int getPartnerRole(){
        return( this.partnerRole );
    }
    
    public String getSenderName(){
        return( this.senderName );
    }
    
    public String getReceiverName(){
        return( this.receiverName );
    }
    
    /**
     * @return the oftpServiceFound
     */
    public boolean isOftpServiceFound() {
        return oftpServiceFound;
    }

    /**
     * @param oftpServiceFound the oftpServiceFound to set
     */
    public void setOftpServiceFound(boolean oftpServiceFound) {
        this.oftpServiceFound = oftpServiceFound;
    }

    /**
     * @return the connectionIsPossible
     */
    public boolean isConnectionIsPossible() {
        return connectionIsPossible;
    }

    /**
     * @param connectionIsPossible the connectionIsPossible to set
     */
    public void setConnectionIsPossible(boolean connectionIsPossible) {
        this.connectionIsPossible = connectionIsPossible;
    }

    /**
     * @return the foundCertificates
     */
    public X509Certificate[] getFoundCertificates() {
        return foundCertificates;
    }

    /**
     * @param foundCertificates the foundCertificates to set
     */
    public void setFoundCertificates(X509Certificate[] foundCertificates) {
        this.foundCertificates = foundCertificates;
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the testedRemoteAddress
     */
    public InetSocketAddress getTestedRemoteAddress() {
        return testedRemoteAddress;
    }

    /**
     * @return the wasSSLTest
     */
    public boolean wasSSLTest() {
        return wasSSLTest;
    }
    
    /**
     * @return the usedCipherSuite
     */
    public String getUsedCipherSuite() {
        return usedCipherSuite;
    }

    /**
     * @return the supportedCipherSuites
     */
    public String[] getSupportedCipherSuites() {
        return supportedCipherSuites;
    }

    /**
     * @return the enabledCipherSuites
     */
    public String[] getEnabledCipherSuites() {
        return enabledCipherSuites;
    }
    

}
