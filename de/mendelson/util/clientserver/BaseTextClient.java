//$Header: /as2/de/mendelson/util/clientserver/BaseTextClient.java 17    11/02/22 13:14 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.console.LoggingHandlerPrintStream;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.LoginRequired;
import de.mendelson.util.clientserver.messages.ServerSideNotification;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Text Client root implementation
 *
 * @author S.Heller
 * @version $Revision: 17 $
 */
public abstract class BaseTextClient implements ClientSessionHandlerCallback {

    private Logger logger = Logger.getAnonymousLogger();
    private BaseClient baseClient = null;
    private final List<ClientsideMessageProcessor> messageProcessorList = Collections.synchronizedList(new ArrayList<ClientsideMessageProcessor>());
    private PrintStream out;
    private String host = null;

    public BaseTextClient() {
        this.out = System.out;
        this.setupLogger();
        this.baseClient = new BaseClient(this);
        this.baseClient.setLogger(this.logger);
    }

    public void setBaseClient(BaseClient baseClient) {
        this.baseClient = baseClient;
        this.baseClient.setLogger(this.logger);
    }
    
    /**
     * Indicates if server log messages should be displayed in the client or
     * simply ignored
     */
    public void setDisplayServerLogMessages(boolean flag) {
        this.baseClient.setDisplayServerLogMessages(flag);
    }

    private void setupLogger() {
        this.logger.setUseParentHandlers(false);
        //send log output to the console
        this.logger.addHandler(new LoggingHandlerPrintStream(this.out));
        this.logger.setLevel(Level.ALL);
    }

    public void addMessageProcessor(ClientsideMessageProcessor processor) {
        synchronized (this.messageProcessorList) {
            this.messageProcessorList.add(processor);
        }
    }

    public void removeMessageProcessor(ClientsideMessageProcessor processor) {
        synchronized (this.messageProcessorList) {
            this.messageProcessorList.remove(processor);
        }
    }

    /**
     * Logs something to the clients log
     */
    @Override
    public void log(Level logLevel, String message) {
        if (this.logger == null) {
            throw new RuntimeException("TextClient: No logger set.");
        }
        this.logger.log(logLevel, message);
    }

    @Override
    public Logger getLogger() {
        return (this.logger);
    }

    /**
     * Returns if the client is connected
     */
    public boolean isConnected() {
        return (this.baseClient.isConnected());
    }

    public abstract void performLogin();

    public void connect(String host, int port, long timeout) throws Exception {
        this.host = host;
        this.connect(new InetSocketAddress(host, port), timeout);
    }

    public void connect(InetSocketAddress hostAddress, long timeout) throws Exception {
        if (this.logger == null) {
            throw new RuntimeException("TextClient: No logger set.");
        }
        if (this.baseClient.getDisplayServerLogMessages()) {
            this.logger.info("Connecting to " + hostAddress);
        }
        if (!this.baseClient.connect(hostAddress, timeout)) {
            String msg = "Connection refused: " + hostAddress.toString();
            this.log(Level.WARNING, msg);
            throw new Exception(msg);
        }
    }

    public void disconnect() {
        if (this.baseClient != null) {
            this.baseClient.disconnect();
        }
    }

    /**
     * Makes this a ClientSessionCallback
     */
    @Override
    public void syncRequestFailed(ClientServerMessage request, ClientServerMessage response, Throwable throwable) {
        this.log(Level.WARNING, throwable.getMessage());
    }

    /**
     * Sends a message async to the server
     */
    public void sendAsync(ClientServerMessage message) {
        this.baseClient.sendAsync(message);
    }

    /**
     * Sends a message sync to the server and returns a response Will inform the
     * ClientSessionHandler callback (syncRequestFailed) if the sync request
     * fails
     */
    public ClientServerResponse sendSync(ClientServerMessage request, long timeout) {
        return (this.getBaseClient().sendSync(request, timeout));
    }

    /**
     * Sends a message sync to the server and returns a response Will inform the
     * ClientSessionHandler callback (syncRequestFailed) if the sync request
     * fails
     */
    public ClientServerResponse sendSync(ClientServerMessage request) {
        return (this.getBaseClient().sendSync(request));
    }

    /**
     * Sends a sync message and waits infinite for an answer. A connection
     * timeout will occur if the request could not be sent to the server anyway.
     * Once the request has been sent this will wait for an answer without any
     * timeout.
     *
     * @param message
     */
    public ClientServerResponse sendSyncWaitInfinite(ClientServerMessage request) {
        return (this.getBaseClient().sendSyncWaitInfinite(request));
    }

    @Override
    public void connected(SocketAddress socketAddress) {
        if (this.logger == null) {
            throw new RuntimeException("TextClient: No logger set.");
        }
        if (this.baseClient.getDisplayServerLogMessages()) {
            this.log(Level.INFO, "Connection established to " + socketAddress.toString());
        }
    }

    @Override
    public void loggedOut() {
        if (this.logger == null) {
            throw new RuntimeException("TextClient: No logger set.");
        }
        if (this.baseClient.getDisplayServerLogMessages()) {
            this.log(Level.INFO, "Logged out");
        }
    }

    @Override
    public void disconnected() {
        if (this.logger == null) {
            throw new RuntimeException("TextClient: No logger set.");
        }
        if (this.baseClient.getDisplayServerLogMessages()) {
            this.log(Level.INFO, "Connection closed by foreign host");
        }
    }

    /**
     * Overwrite this in the client implementation for user defined processing
     */
    @Override
    public void messageReceivedFromServer(ClientServerMessage message) {
        if (this.logger == null) {
            throw new RuntimeException("TextClient: No logger set.");
        }
        //there is no user defined processing for sync responses
        if (message._isSyncRequest()) {
            return;
        } else if (message instanceof LoginRequired) {
            this.loginRequestedFromServer();
        } else {
            //let the message process by all registered client side processors
            boolean processed = false;
            synchronized (this.messageProcessorList) {
                for (ClientsideMessageProcessor processor : this.messageProcessorList) {
                    processed |= processor.processMessageFromServer(message);
                }
            }
            if (!(message instanceof ServerSideNotification) && !processed) {
                this.log(Level.WARNING, "Unprocessed message of type "
                        + message.getClass().getName());
            }
        }
    }

    /**
     * @return the client
     */
    public BaseClient getBaseClient() {
        return (this.baseClient);
    }

    @Override
    public void error(String message) {
        if (this.logger == null) {
            throw new RuntimeException("TextClient: No logger set.");
        }
        this.log(Level.SEVERE, "Error: " + message);
    }

    /**
     * Performs a logout, closes the actual session
     */
    public void logout() {
        this.baseClient.logout();
    }

    /**
     * checks if the client is connected and the user logged in
     */
    public boolean isConnectedAndLoggedIn() {
        return (this.baseClient.isConnectedAndLoggedIn());
    }

    /**
     * Calback: the server requets a login
     */
    @Override
    public void loginRequestedFromServer() {
        this.performLogin();
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }
    
    /**This is called if there is any decoder problem between client and server which means that the client is not compatible to the server.
     * This is not a result of a compare of the client/server versions but mainly a problem that occurs already if the client tries to connect/login.
     * In this case a transfer of the client version to the server to check the versions is not even possible
     * 
     */
    @Override
    public void clientIsIncompatible( String errorMessage ){
        this.logger.severe(errorMessage);
    }
}
