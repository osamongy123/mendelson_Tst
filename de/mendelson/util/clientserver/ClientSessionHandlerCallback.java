//$Header: /as2/de/mendelson/util/clientserver/ClientSessionHandlerCallback.java 9     11/02/22 13:14 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Client side protocol handler classback interface
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public interface ClientSessionHandlerCallback {

    public void loginRequestedFromServer();

    public void connected(SocketAddress address);

    public void loggedOut();

    public void disconnected();

    public void messageReceivedFromServer(ClientServerMessage message);

    public void error(String message);

    public void log(Level logLevel, String message);

    public Logger getLogger();

    /**A sync request failed
     * 
     * @param request The sync request that was not successful if it was a request, might be null
     * @param response The sync request that was not successful if it was a response, might be null
     * @param throwable The exception that occurred
     */
    public void syncRequestFailed(ClientServerMessage request, ClientServerMessage response, Throwable throwable);

    public void clientIsIncompatible(String errorMessage);
}
