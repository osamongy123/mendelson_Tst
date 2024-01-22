//$Header: /as2/de/mendelson/comm/as2/clientserver/message/ExternalLogRequest.java 2     14.09.21 13:54 Heller $
package de.mendelson.comm.as2.clientserver.message;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.Serializable;
import java.util.logging.Level;

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
 * @version $Revision: 2 $
 */
public class ExternalLogRequest extends ClientServerMessage implements Serializable {

    public static final long serialVersionUID = 1L;
    private String message = null;
    private Level level = Level.INFO;
    private String messageId = "";

    public ExternalLogRequest() {
    }

    public void setLogEntry(Level level, String message, String messageId) {
        this.message = message;
        this.messageId = messageId;
        this.level = level;
    }

    @Override
    public String toString() {
        return ("Add a log entry from an external device to the system log");
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

}
