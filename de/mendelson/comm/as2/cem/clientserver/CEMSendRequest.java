//$Header: /as2/de/mendelson/comm/as2/cem/clientserver/CEMSendRequest.java 4     6/22/18 1:48p Heller $
package de.mendelson.comm.as2.cem.clientserver;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.security.cert.KeystoreCertificate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class CEMSendRequest extends ClientServerMessage implements Serializable {
   
    public static final long serialVersionUID = 1L;
    private Partner initiator = null;
    private KeystoreCertificate certificate = null;
    private Date activationDate = null;
    private List<Partner> receiver = new ArrayList<Partner>();
    private boolean purposeSSL = false;
    private boolean purposeEncryption = false;
    private boolean purposeSignature = false;

    public CEMSendRequest() {
    }

    @Override
    public String toString() {
        return ("Send a CEM");
    }

    /**
     * @return the initiator
     */
    public Partner getInitiator() {
        return initiator;
    }

    /**
     * @param initiator the initiator to set
     */
    public void setInitiator(Partner initiator) {
        this.initiator = initiator;
    }

    /**
     * @return the certificate
     */
    public KeystoreCertificate getCertificate() {
        return certificate;
    }

    /**
     * @param certificate the certificate to set
     */
    public void setCertificate(KeystoreCertificate certificate) {
        this.certificate = certificate;
    }

    /**
     * @return the activationDate
     */
    public Date getActivationDate() {
        return activationDate;
    }

    /**
     * @param activationDate the activationDate to set
     */
    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }
    
    /**
     * @return the receiver
     */
    public List<Partner> getReceiver() {
        return receiver;
    }

    /**
     * @param newReceiver the receiver to set
     */
    public void setReceiver(List<Partner> newReceiver) {
        this.receiver.clear();
        this.receiver.addAll( newReceiver );
    }
    
    /**
     * @param newReceiver the receiver to set
     */
    public void setReceiver(Partner newReceiver) {
        this.receiver.clear();
        this.receiver.add( newReceiver );
    }
    
    /**
     * @return the purposeSSL
     */
    public boolean isPurposeSSL() {
        return purposeSSL;
    }

    /**
     * @param purposeSSL the purposeSSL to set
     */
    public void setPurposeSSL(boolean purposeSSL) {
        this.purposeSSL = purposeSSL;
    }

    /**
     * @return the purposeEncryption
     */
    public boolean isPurposeEncryption() {
        return purposeEncryption;
    }

    /**
     * @param purposeEncryption the purposeEncryption to set
     */
    public void setPurposeEncryption(boolean purposeEncryption) {
        this.purposeEncryption = purposeEncryption;
    }

    /**
     * @return the purposeSignature
     */
    public boolean isPurposeSignature() {
        return purposeSignature;
    }

    /**
     * @param purposeSignature the purposeSignature to set
     */
    public void setPurposeSignature(boolean purposeSignature) {
        this.purposeSignature = purposeSignature;
    }
    
    
}
