//$Header: /as2/de/mendelson/comm/as2/cem/CEMEntry.java 9     19.09.18 14:30 Heller $
package de.mendelson.comm.as2.cem;

import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.util.MecResourceBundle;
import java.io.Serializable;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Container that stores certificate information where the respond by date requests to change a certificate
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class CEMEntry implements Serializable{

    public static final long serialVersionUID = 1L;
    
    public static final int CATEGORY_CRYPT = 1;
    public static final int CATEGORY_SIGN = 2;
    public static final int CATEGORY_SSL = 3;
    public static final int STATUS_PENDING_INT = 1;
    public static final int STATUS_REJECTED_INT = 2;
    public static final int STATUS_ACCEPTED_INT = 3;
    public static final int STATUS_EXPIRED_INT = 4;
    public static final int STATUS_REVOKED_INT = 5;
    //canceled by user
    public static final int STATUS_CANCELED_INT = 99;
    //processing error - mainly a bad MDN on a request
    public static final int STATUS_PROCESSING_ERROR_INT = 999;
    private String initiatorAS2Id = null;
    private String receiverAS2Id = null;
    private int category = CEMEntry.CATEGORY_CRYPT;
    private long respondByDate = -1L;
    private String serialId = null;
    private String requestId = null;
    private String requestMessageid = null;
    private String responseMessageid = null;
    private long requestMessageOriginated = 0L;
    private long responseMessageOriginated = 0L;
    private int cemState = CEMEntry.STATUS_PENDING_INT;
    private String issuername = null;
    private boolean processed = false;
    private long processDate = 0L;
    /**Only filled if a response has the state "Rejected"*/
    private String reasonForRejection = null;

    public boolean hasRespondByDate(){
        return( this.respondByDate != -1L);
    }

    /**Returns a localized string that represents the category*/
    public static final String getCategoryLocalized(int category) {
        MecResourceBundle rb;
        //load resource bundle
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCEM.class.getName());
            return (rb.getResourceString("category." + category));
        } catch (MissingResourceException e) {
            return ("###");
        }
    }

    /**Returns a localized string that represents the state*/
    public static final String getStateLocalized(int state, String receiver) {
        MecResourceBundle rb;
        //load resource bundle
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCEM.class.getName());
            return (rb.getResourceString("state." + state, receiver));
        } catch (MissingResourceException e) {
            return ("###");
        }
    }
    /**
     * @return the category
     */
    public int getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(int category) {
        this.category = category;
    }

    /**
     * @return the respondByDate
     */
    public long getRespondByDate() {
        return respondByDate;
    }

    /**
     * @param respondByDate the respondByDate to set
     */
    public void setRespondByDate(long respondByDate) {
        this.respondByDate = respondByDate;
    }

    /**
     * @return the serialId
     */
    public String getSerialId() {
        return serialId;
    }

    /**
     * @param serialId the serialId to set
     */
    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    /**
     * @return the initiatorAS2Id
     */
    public String getInitiatorAS2Id() {
        return initiatorAS2Id;
    }

    /**
     * @param initiatorAS2Id the initiatorAS2Id to set
     */
    public void setInitiatorAS2Id(String initiatorAS2Id) {
        this.initiatorAS2Id = initiatorAS2Id;
    }

    /**
     * @return the receiverAS2Id
     */
    public String getReceiverAS2Id() {
        return receiverAS2Id;
    }

    /**
     * @param receiverAS2Id the receiverAS2Id to set
     */
    public void setReceiverAS2Id(String receiverAS2Id) {
        this.receiverAS2Id = receiverAS2Id;
    }

    /**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return the requestMessageid
     */
    public String getRequestMessageid() {
        return requestMessageid;
    }

    /**
     * @param requestMessageid the requestMessageid to set
     */
    public void setRequestMessageid(String requestMessageid) {
        this.requestMessageid = requestMessageid;
    }

    /**
     * @return the responseMessageid
     */
    public String getResponseMessageid() {
        return responseMessageid;
    }

    /**
     * @param responseMessageid the responseMessageid to set
     */
    public void setResponseMessageid(String responseMessageid) {
        this.responseMessageid = responseMessageid;
    }

    /**
     * @return the requestMessageOriginated
     */
    public long getRequestMessageOriginated() {
        return requestMessageOriginated;
    }

    /**
     * @param requestMessageOriginated the requestMessageOriginated to set
     */
    public void setRequestMessageOriginated(long requestMessageOriginated) {
        this.requestMessageOriginated = requestMessageOriginated;
    }

    /**
     * @return the responseMessageOriginated
     */
    public long getResponseMessageOriginated() {
        return responseMessageOriginated;
    }

    /**
     * @param responseMessageOriginated the responseMessageOriginated to set
     */
    public void setResponseMessageOriginated(long responseMessageOriginated) {
        this.responseMessageOriginated = responseMessageOriginated;
    }

    /**
     * @return the cemState
     */
    public int getCemState() {
        return cemState;
    }

    /**
     * @param cemState the cemState to set
     */
    public void setCemState(int cemState) {
        this.cemState = cemState;
    }

    /**
     * @return the issuername
     */
    public String getIssuername() {
        return issuername;
    }

    /**
     * @param issuername the issuername to set
     */
    public void setIssuername(String issuername) {
        this.issuername = issuername;
    }

    /**
     * @return the processed
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * @param processed the processed to set
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * @return the processDate
     */
    public long getProcessDate() {
        return processDate;
    }

    /**
     * @param processDate the processDate to set
     */
    public void setProcessDate(long processDate) {
        this.processDate = processDate;
    }

    /**
     * @return the reasonForRejection
     */
    public String getReasonForRejection() {
        return reasonForRejection;
    }

    /**
     * @param reasonForRejection the reasonForRejection to set
     */
    public void setReasonForRejection(String reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
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
        if (anObject != null && anObject instanceof CEMEntry) {
            CEMEntry entry = (CEMEntry) anObject;
            return (entry != null && this.requestId != null && this.requestId.equals(entry.requestId)
                    && this.category == entry.category);
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.initiatorAS2Id);
        hash = 41 * hash + Objects.hashCode(this.receiverAS2Id);
        hash = 41 * hash + this.category;
        hash = 41 * hash + (int) (this.respondByDate ^ (this.respondByDate >>> 32));
        hash = 41 * hash + Objects.hashCode(this.serialId);
        hash = 41 * hash + Objects.hashCode(this.requestId);
        hash = 41 * hash + Objects.hashCode(this.requestMessageid);
        hash = 41 * hash + Objects.hashCode(this.responseMessageid);
        hash = 41 * hash + (int) (this.requestMessageOriginated ^ (this.requestMessageOriginated >>> 32));
        hash = 41 * hash + (int) (this.responseMessageOriginated ^ (this.responseMessageOriginated >>> 32));
        hash = 41 * hash + this.cemState;
        hash = 41 * hash + Objects.hashCode(this.issuername);
        hash = 41 * hash + (this.processed ? 1 : 0);
        hash = 41 * hash + (int) (this.processDate ^ (this.processDate >>> 32));
        hash = 41 * hash + Objects.hashCode(this.reasonForRejection);
        return hash;
    }
    
}
