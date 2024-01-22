//$Header: /as2/de/mendelson/comm/as2/datasheet/DatasheetInformation.java 4     8.10.14 17:35 Heller $
package de.mendelson.comm.as2.datasheet;

import de.mendelson.comm.as2.message.AS2Message;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Container that contains information for the datasheet
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class DatasheetInformation {

    private String receiptURL = "http://as2.mendelson-e-c.com:8080/as2/HttpReceiver";
    private String comment = "Please send this document back\nto fax no +1 123 123\nor via mail to \nourcontact@ourcomany.com";
    private byte[] certVerifySignature = null;
    private byte[] certEncryptData = null;
    private byte[] certSSL = null;
    private boolean requestSyncMDN = false;
    private boolean requestSignedMDN = false;
    private int encryption = AS2Message.ENCRYPTION_3DES;
    private int signature = AS2Message.SIGNATURE_SHA1;
    private int compression = AS2Message.COMPRESSION_NONE;

    public DatasheetInformation() {
    }

    /**
     * @return the receiptURL
     */
    public String getReceiptURL() {
        return receiptURL;
    }

    /**
     * @param receiptURL the receiptURL to set
     */
    public void setReceiptURL(String receiptURL) {
        this.receiptURL = receiptURL;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the certVerifySignature
     */
    public byte[] getCertVerifySignature() {
        return certVerifySignature;
    }

    /**
     * @param certVerifySignature the certVerifySignature to set
     */
    public void setCertVerifySignature(byte[] certVerifySignature) {
        this.certVerifySignature = certVerifySignature;
    }

    /**
     * @return the certDecryptData
     */
    public byte[] getCertEncryptData() {
        return certEncryptData;
    }

    /**
     * @param certDecryptData the certDecryptData to set
     */
    public void setCertDecryptData(byte[] certDecryptData) {
        this.certEncryptData = certDecryptData;
    }

    /**
     * @return the requestSyncMDN
     */
    public boolean requestsSyncMDN() {
        return requestSyncMDN;
    }

    /**
     * @param requestSyncMDN the requestSyncMDN to set
     */
    public void setRequestSyncMDN(boolean requestSyncMDN) {
        this.requestSyncMDN = requestSyncMDN;
    }

    /**
     * @return the encryption
     */
    public int getEncryption() {
        return encryption;
    }

    /**
     * @param encryption the encryption to set
     */
    public void setEncryption(int encryption) {
        this.encryption = encryption;
    }

    /**
     * @return the signature
     */
    public int getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(int signature) {
        this.signature = signature;
    }

    /**
     * @return the certSSL
     */
    public byte[] getCertSSL() {
        return certSSL;
    }

    /**
     * @param certSSL the certSSL to set
     */
    public void setCertSSL(byte[] certSSL) {
        this.certSSL = certSSL;
    }

    /**
     * @return the requestSignedMDN
     */
    public boolean requestsSignedMDN() {
        return requestSignedMDN;
    }

    /**
     * @param requestSignedMDN the requestSignedMDN to set
     */
    public void setRequestSignedMDN(boolean requestSignedMDN) {
        this.requestSignedMDN = requestSignedMDN;
    }

    /**
     * @return the compression
     */
    public int getCompression() {
        return compression;
    }

    /**
     * @param compression the compression to set
     */
    public void setCompression(int compression) {
        this.compression = compression;
    }

   
    
}
