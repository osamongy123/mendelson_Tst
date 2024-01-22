//$Header: /as2/de/mendelson/comm/as2/clientserver/message/IncomingMessageRequest.java 4     1/09/22 14:11 Heller $
package de.mendelson.comm.as2.clientserver.message;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.Serializable;
import java.util.Properties;
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
public class IncomingMessageRequest extends ClientServerMessage implements Serializable {

    public static final long serialVersionUID = 1L;
    private String contentType = null;
    private String remoteHost = null;
    private Properties header = new Properties();
    private String messageDataFilename = null;
    private boolean usesTLS = false;

    public IncomingMessageRequest() {
    }

    @Override
    public String toString() {
        return ("Incoming message response");
    }

    public void addHeader(String key, String value) {
        this.header.setProperty(key.toLowerCase(), value);
    }

    /**
     * Deletes the existing request header and sets new
     */
    public void setHeader(Properties header) {
        this.header = header;
    }

    public Properties getHeader() {
        return (this.header);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /**
     * @return the messageDataFilename
     */
    public String getMessageDataFilename() {
        return messageDataFilename;
    }

    /**
     * @param messageDataFilename the messageDataFilename to set
     */
    public void setMessageDataFilename(String messageDataFilename) {
        this.messageDataFilename = messageDataFilename;
    }

    /**
     * @return the usesTLS
     */
    public boolean usesTLS() {
        return usesTLS;
    }

    /**
     * @param usesTLS the usesTLS to set
     */
    public void setUsesTLS(boolean usesTLS) {
        this.usesTLS = usesTLS;
    }

}
