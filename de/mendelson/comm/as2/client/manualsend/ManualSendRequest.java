//$Header: /as2/de/mendelson/comm/as2/client/manualsend/ManualSendRequest.java 12    28.07.21 14:58 Heller $
package de.mendelson.comm.as2.client.manualsend;

import de.mendelson.util.clientserver.clients.datatransfer.UploadRequestFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Message for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class ManualSendRequest extends UploadRequestFile implements Serializable {

    
    public static final long serialVersionUID = 1L;
    private String senderAS2Id = null;
    //If the sender AS2 name is used this results always in a AS2 id lookup on the server side!
    //Better use the AS2 id if this is known
    private String senderAS2Name = null;
    private String receiverAS2Id;
    //If the receivers AS2 name is used this results always in a AS2 id lookup on the server side!
    //Better use the AS2 id if this is known
    private String receiverAS2Name = null;
    private List<String> filenames = new ArrayList<String>();
    private String resendMessageId = null;
    private String userdefinedId = null;
    private List<String> uploadHashs = new ArrayList<String>();
    private String subject = null;
    private boolean sendTestdata = false;
    private List<String> payloadContentTypes = new ArrayList<String>();

    @Override
    public String toString() {
        return ("Manual send request");
    }

    /**
     * @return the sender
     */
    public String getSenderAS2Id() {
        return this.senderAS2Id;
    }

    /**
     * @param sender the sender to set
     */
    public void setSenderAS2Id(String senderAS2Id) {
        this.senderAS2Id = senderAS2Id;
    }

    /**
     * @return the receiver
     */
    public String getReceiverAS2Id() {
        return this.receiverAS2Id;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiverAS2Id(String receiverAS2Id) {
        this.receiverAS2Id = receiverAS2Id;
    }

    /**
     * @return the filename
     */
    public List<String> getFilenames() {
        return (this.filenames);
    }

    /**
     * @param filename the filename of a payload
     * @param payloadContentType The content type of this payload as set in the outbound AS2 message - may be null for the
     * default value or the value defined in the receiver
     */
    public void addFilename(String filename, String payloadContentType) {
        this.filenames.add(filename);
        this.payloadContentTypes.add( payloadContentType );
    }

    /**
     * @return the resendMessageId
     */
    public String getResendMessageId() {
        return (this.resendMessageId);
    }

    /**
     * Set this message id if this is a resend of an existing message
     *
     * @param resendMessageId the resendMessageId to set
     */
    public void setResendMessageId(String resendMessageId) {
        this.resendMessageId = resendMessageId;
    }

    /**
     * @return the userdefinedId
     */
    public String getUserdefinedId() {
        return userdefinedId;
    }

    /**
     * Sets a user defined id to this transaction. If this is set the user
     * defined id could be used later to track the progress of this send
     * transmission.
     *
     * @param userdefinedId the userdefinedId to set
     */
    public void setUserdefinedId(String userdefinedId) {
        this.userdefinedId = userdefinedId;
    }

    /**
     * @return the uploadHashs
     */
    public List<String> getUploadHashs() {
        return (this.uploadHashs);
    }

    /**
     * @param uploadHashs the uploadHashs to set
     */
    public void setUploadHashs(List<String> uploadHashs) {
        this.uploadHashs.addAll(uploadHashs);
    }

    @Override
    public void setUploadHash(String singleUploadHash) {
        this.uploadHashs.add(singleUploadHash);
    }

    @Override
    public String getUploadHash() {
        throw new IllegalArgumentException("ManualSendRequest: Use the method getUploadHashs() to get the uploaded file hashs");
    }

    /**
     * Indicates that no file should be send but test data that is generated on the server
     */
    public boolean getSendTestdata() {
        return sendTestdata;
    }

   /**
     * Indicates that no file should be send but test data that is generated on the server
     */
    public void setSendTestdata(boolean sendTestdata) {
        this.sendTestdata = sendTestdata;
    }

    /**
     * @return the payloadContentType
     */
    public List<String> getPayloadContentTypes() {
        return this.payloadContentTypes;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the senderAS2Name
     */
    public String getSenderAS2Name() {
        return senderAS2Name;
    }

    /**
     * @param senderAS2Name the senderAS2Name to set
     */
    public void setSenderAS2Name(String senderAS2Name) {
        this.senderAS2Name = senderAS2Name;
    }

    /**
     * @return the receiverAS2Name
     */
    public String getReceiverAS2Name() {
        return receiverAS2Name;
    }

    /**
     * @param receiverAS2Name the receiverAS2Name to set
     */
    public void setReceiverAS2Name(String receiverAS2Name) {
        this.receiverAS2Name = receiverAS2Name;
    }

    
    
}
