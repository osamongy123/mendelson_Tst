//$Header: /as2/de/mendelson/util/clientserver/ClientSessionHandler.java 29    11/02/22 13:14 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.ServerLogMessage;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Client side protocol handler
 *
 * @author S.Heller
 * @version $Revision: 29 $
 */
public class ClientSessionHandler extends IoHandlerAdapter {

    public static final String SESSION_ATTRIB_LOGIN_KEY = "login_key";
    public static final String SESSION_ATTRIB_LOGIN_CERT = "login_cert";
    /**
     * Callback that is used for event notification
     */
    private ClientSessionHandlerCallback callback = null;
    /**
     * stores the sync requests
     */
    private final Map<Long, BlockingQueue<ClientServerResponse>> syncMap
            = new ConcurrentHashMap<Long, BlockingQueue<ClientServerResponse>>();
    /**
     * Indicates if the server log messages should be displayed in the client
     * log - defaults to true
     */
    private boolean displayServerLogMessages = true;

    public ClientSessionHandler(ClientSessionHandlerCallback callback) {
        this.callback = callback;
    }

    public ClientSessionHandlerCallback getCallback() {
        return (this.callback);
    }

    /**
     * Indicates if server log messages should be displayed in the client or
     * simply ignored
     */
    public void setDisplayServerLogMessages(boolean flag) {
        this.displayServerLogMessages = flag;
    }

    public boolean getDisplayServerLogMessages() {
        return (this.displayServerLogMessages);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        this.callback.connected(session.getRemoteAddress());
    }

    @Override
    public void messageSent(IoSession session, Object messageObj) {
    }

    @Override
    public void messageReceived(IoSession session, Object messageObj) throws Exception {
        if (messageObj == null) {
            this.callback.getLogger().log(Level.WARNING, "[Client-Server communication] ClientSessionHandler.messageReceived: Received null type message.");
            return;
        }
        if (!(messageObj instanceof ClientServerMessage)) {
            this.callback.getLogger().log(Level.WARNING, "[Client-Server communication] ClientSessionHandler.messageReceived: Client server message type is not supported: "
                    + messageObj.getClass().getName());
            return;
        }
        ClientServerMessage message = (ClientServerMessage) messageObj;
        //sync response: check if there was a request for this message
        if (message._isSyncRequest()) {
            ClientServerResponse response = (ClientServerResponse) message;
            if (!this.syncMap.containsKey(response.getReferenceId())) {
                Exception unreferredSyncResponseException
                        = new Exception("[Client-Server communication] The client received a unreferred sync response. Type: " + response.getClass().getName()
                                + ", reference id: " + response.getReferenceId());
                this.callback.syncRequestFailed(null, response, unreferredSyncResponseException);
            } else {
                BlockingQueue queue = this.syncMap.get(response.getReferenceId());
                queue.offer(response);
            }
        }
        if (message instanceof ServerLogMessage) {
            if (this.displayServerLogMessages) {
                //server log messages are just passed through to the client log if requested
                ServerLogMessage serverMessage = (ServerLogMessage) message;
                this.callback.getLogger().log(serverMessage.getLevel(), serverMessage.getMessage(),
                        serverMessage.getParameter());
            }
        } else {
            this.callback.messageReceivedFromServer((ClientServerMessage) message);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        this.callback.disconnected();
    }

    public ClientServerResponse waitForSyncAnswerInfinite(IoSession session, Long referenceId) throws Exception {
        ClientServerResponse response = null;
        while (response == null) {
            //wait for 5s and then repeat..
            response = this.waitForSyncAnswer(referenceId, TimeUnit.SECONDS.toMillis(5));
            if (!session.isConnected()) {
                this.callback.getLogger().log(Level.WARNING, "[Client-Server communication] ClientSessionHandler.waitForSyncAnswerInfinite: Session closed by remote host.");
                throw new Exception("[Client-Server communication] Session closed by remote host.");
            }
        }
        return (response);
    }

    /**
     * waits a specified time for an inbound message, used for sync processing
     */
    public ClientServerResponse waitForSyncAnswer(Long referenceId, long timeoutInMS) throws Exception {
        BlockingQueue<ClientServerResponse> queue = null;
        if (!this.syncMap.containsKey(referenceId)) {
            Exception unreferredSyncResponseException = new Exception("[Client-Server communication] ClientSessionHandler.waitForSyncAnswer: "
                    + "The message that should be waited for is unreferred."
                    + " Reference id: " + referenceId);
            this.callback.syncRequestFailed(null, null, unreferredSyncResponseException);
        } else {
            queue = this.syncMap.get(referenceId);
        }
        ClientServerResponse response = null;
        if (queue != null) {
            response = queue.poll(timeoutInMS, TimeUnit.MILLISECONDS);
        }
        return (response);
    }

    public void addSyncRequest(ClientServerMessage request) {
        BlockingQueue<ClientServerResponse> queue = new LinkedBlockingQueue<ClientServerResponse>(1);
        this.syncMap.put(request.getReferenceId(), queue);
    }

    public void removeSyncRequest(ClientServerMessage request) {
        if (!this.syncMap.containsKey(request.getReferenceId())) {
            Exception unreferredSyncResponseException = new Exception("[Client-Server communication] ClientSessionHandler.removeSyncRequest: "
                    + "The message to remove from the sync map does not exist. "
                    + " Reference id: " + request.getReferenceId());
            this.callback.syncRequestFailed(request, null, unreferredSyncResponseException);
        }
        this.syncMap.remove(request.getReferenceId());
    }

    /**
     * Inform the callback that a sync request failed
     *
     * @param request sync request that failed
     * @param the exception that was thrown
     */
    public void syncRequestFailed(ClientServerMessage request, ClientServerMessage response, Throwable throwable) {
        this.callback.syncRequestFailed(request, response, throwable);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
    }
}
