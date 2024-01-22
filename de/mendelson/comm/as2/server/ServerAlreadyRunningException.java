//$Header: /as2/de/mendelson/comm/as2/server/ServerAlreadyRunningException.java 1     19.06.20 11:12 Heller $
package de.mendelson.comm.as2.server;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Exception that is thrown if it is detected that the server is already running
 * @author  S.Heller
 */
public class ServerAlreadyRunningException extends RuntimeException {

    public ServerAlreadyRunningException(String message) {
        super(message);
    }
}
