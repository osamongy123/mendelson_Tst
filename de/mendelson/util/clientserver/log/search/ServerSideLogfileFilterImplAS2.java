//$Header: /as2/de/mendelson/util/clientserver/log/search/ServerSideLogfileFilterImplAS2.java 1     7.12.18 12:25 Heller $
package de.mendelson.util.clientserver.log.search;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Filter the client could define to perform a server side system log file search
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class ServerSideLogfileFilterImplAS2 extends ServerSideLogfileFilter implements Serializable {

    public static final long serialVersionUID = 1L;

    private String messageId = null;
    private String mdnId = null;
    private String userdefinedId = null;

    public ServerSideLogfileFilterImplAS2() {
    }

    /**
     * @return the userdefinedId
     */
    public String getUserdefinedId() {
        return userdefinedId;
    }

    /**
     * @param userdefinedId the userdefinedId to set
     */
    public void setUserdefinedId(String userdefinedId) {
        this.userdefinedId = userdefinedId;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the mdnId
     */
    public String getMDNId() {
        return mdnId;
    }

    /**
     * @param mdnId the mdnId to set
     */
    public void setMDNId(String mdnId) {
        this.mdnId = mdnId;
    }
    
    
}
