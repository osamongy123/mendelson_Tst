//$Header: /as2/de/mendelson/comm/as2/clientserver/message/IncomingMessageResponse.java 3     4/06/18 12:21p Heller $
package de.mendelson.comm.as2.clientserver.message;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.Serializable;
import java.util.Properties;
import javax.servlet.http.HttpServletResponse;
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
 * @version $Revision: 3 $
 */
public class IncomingMessageResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private Properties header = new Properties();
    private byte[] mdnData = null;
    private String contentType = null;

    /**
     * Stores the http return code that is transmitted to the receipt servlet
     * and will be returned
     */
    private int httpReturnCode = HttpServletResponse.SC_OK;

    public IncomingMessageResponse(IncomingMessageRequest request) {
        super(request);
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

    /**
     * @return the httpReturnCode
     */
    public int getHttpReturnCode() {
        return httpReturnCode;
    }

    /**
     * @param httpReturnCode the httpReturnCode to set
     */
    public void setHttpReturnCode(int httpReturnCode) {
        this.httpReturnCode = httpReturnCode;
    }

    /**
     * @return the mdnData
     */
    public byte[] getMDNData() {
        return mdnData;
    }

    /**
     * @param mdnData the mdnData to set
     */
    public void setMDNData(byte[] mdnData) {
        this.mdnData = mdnData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
