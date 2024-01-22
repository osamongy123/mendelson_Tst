//$Header: /oftp2/de/mendelson/util/clientserver/messages/ClientToServerLogRequest.java 1     13/10/22 10:56 Heller $
package de.mendelson.util.clientserver.messages;

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
 * Msg for the client server protocol. This is used if a user does something on a client
 * that is relevant for the server and should be displayed there as client related activity
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class ClientToServerLogRequest extends ClientServerMessage implements Serializable {

    public static final long serialVersionUID = 1L;
    private String message = null;
    private Level level = Level.INFO;

    public ClientToServerLogRequest() {
    }

    public void setLogEntry(Level level, String message) {
        this.message = message;
        this.level = level;
    }

    @Override
    public String toString() {
        return ("Add a log entry from a client that should be displayed in the server log");
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

}
