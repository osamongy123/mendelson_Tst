//$Header: /as2/de/mendelson/util/clientserver/BaseClient.java 51    23/08/22 10:28 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.codec.ClientServerCodecFactory;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.LoginRequest;
import de.mendelson.util.clientserver.messages.LoginState;
import de.mendelson.util.clientserver.messages.QuitRequest;
import de.mendelson.util.clientserver.user.User;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.UnorderedThreadPoolExecutor;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Abstract client for a user
 *
 * @author S.Heller
 * @version $Revision: 51 $
 */
public class BaseClient {

    private Logger logger = Logger.getAnonymousLogger();
    private final ClientSessionHandler clientSessionHandler;
    private IoSession session = null;
    private NioSocketConnector connector;
    private ExecutorFilter executorFilter = null;
    private UnorderedThreadPoolExecutor unorderedThreadPoolExecutor = null;

    /**
     * Set default timeout for sync requests to 30s
     */
    public static final long TIMEOUT_SYNC_RECEIVE = TimeUnit.SECONDS.toMillis(60);
    public static final long TIMEOUT_SYNC_SEND = TimeUnit.SECONDS.toMillis(60);

    /**
     * User that is connected
     */
    private User user = null;

    public BaseClient(ClientSessionHandlerCallback callback) {
        this.connector = new NioSocketConnector();
        this.clientSessionHandler = new ClientSessionHandler(callback);
    }

    /**
     * Indicates if server log messages should be displayed in the client or
     * simply ignored
     */
    public void setDisplayServerLogMessages(boolean flag) {
        this.clientSessionHandler.setDisplayServerLogMessages(flag);
    }

    public boolean getDisplayServerLogMessages() {
        return (this.clientSessionHandler.getDisplayServerLogMessages());
    }

    /**
     * Logs something to the clients log
     */
    public void log(Level logLevel, String message) {
        this.logger.log(logLevel, message);
    }

    public void setLogger(Logger logger) {
        if (logger != null) {
            this.logger = logger;
        }
    }

    /**
     * Are client and server running n the same machine?
     */
    public boolean serverRunsLocal() {
        if (!this.isConnected()) {
            return (false);
        }
        InetAddress serviceAddress = ((InetSocketAddress) this.session.getServiceAddress()).getAddress();
        InetAddress localAddress = ((InetSocketAddress) this.session.getLocalAddress()).getAddress();
        return (serviceAddress.getHostAddress().equals(localAddress.getHostAddress()));
    }

    public LoginState login(String user, char[] passwd, String clientId) {
        if (!this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] BaseClient.login: "
                    + "Not connected to a server. Please connect first.");
        }
        LoginRequest login = new LoginRequest();
        login.setPasswd(passwd);
        login.setUserName(user);
        login.setClientId(clientId);
        LoginState state = (LoginState) this.sendSync(login);
        if (state.getState() == LoginState.STATE_INCOMPATIBLE_CLIENT) {
            this.clientSessionHandler.getCallback().clientIsIncompatible(state.getStateDetails());
        }
        return (state);
    }

    /**
     * checks if the client is connected
     */
    public boolean isConnected() {
        return (this.session != null && this.session.isConnected());
    }

    /**
     * checks if the client is connected and the user logged in
     */
    public boolean isConnectedAndLoggedIn() {
        return (this.session != null && this.session.isConnected() && this.user != null);
    }

    public boolean connect(InetSocketAddress hostAddress, long timeout) {
        if (this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] BaseClient.connect: "
                    + "Already connected to " + hostAddress + ". Please disconnect first.");
        }
        try {
            this.connector.setConnectTimeoutMillis(timeout);
            this.connector.setHandler(this.clientSessionHandler);
            //add SSL support
            SslFilter sslFilter = new SslFilter(this.createSSLContextClient());
            sslFilter.setEnabledProtocols(ClientServer.SERVERSIDE_ACCEPTED_TLS_PROTOCOLS);
            sslFilter.setNeedClientAuth(false);
            this.connector.getFilterChain().addFirst("TLS", sslFilter);
            //add CPU bound tasks first
            this.connector.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new ClientServerCodecFactory(this.logger, this.clientSessionHandler.getCallback())));
            //log client-server communication
            //this.connector.getFilterChain().addLast("logger", new LoggingFilter());
            //multi threaded model: allow and receive simulanously
            this.unorderedThreadPoolExecutor = new UnorderedThreadPoolExecutor();
            this.executorFilter = new ExecutorFilter(unorderedThreadPoolExecutor);
            this.connector.getFilterChain().addLast("executor", executorFilter);
            ConnectFuture connFuture = this.connector.connect(hostAddress).awaitUninterruptibly();
            if (connFuture.isConnected()) {
                this.session = connFuture.getSession();
                return (true);
            } else {
                this.connector.dispose();
                return (false);
            }
        } catch (Exception e) {
            this.log(Level.WARNING, "[Client-Server communication] "
                    + "BaseClient.connect: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create a SSL context instance. This client trust all server certificates
     * and it is therefor not ensured that the server is really the server you
     * expect.
     */
    private SSLContext createSSLContextClient() throws Exception {
        X509TrustManager trustManagerTrustAll = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates,
                    String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates,
                    String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(null, new TrustManager[]{trustManagerTrustAll}, null);
        return context;
    }

    public void broadcast(ClientServerMessage message) {
        if (!this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] "
                    + "BaseClient.broadcast: Not connected to a server. Please connect first.");
        }
        this.session.write(message);
    }

    /**
     * Sends an async message to the server and does not care for an answer
     */
    public void sendAsync(ClientServerMessage message) {
        if (!this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] BaseClient.sendAsync: "
                    + "Not connected to a server. Please connect first.");
        }
        WriteFuture future = this.session.write(message);
    }

    /**
     * Sends a sync message and throws a timeout exception if the client does
     * not answer in a proper time
     *
     * @param message
     */
    public ClientServerResponse sendSync(ClientServerMessage request) {
        return (this.sendSync(request, TIMEOUT_SYNC_RECEIVE));
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
        if (!this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] BaseClient.sendSyncWaitInfinite: "
                    + "Not connected to a server. Please connect first.");
        }
        ClientServerResponse response = null;
        request._setSyncRequest(true);
        try {
            this.clientSessionHandler.addSyncRequest(request);
            WriteFuture writeFuture = this.session.write(request);
            boolean isSent = writeFuture.await(TIMEOUT_SYNC_SEND, TimeUnit.MILLISECONDS);
            if (!isSent) {
                StringBuilder message = new StringBuilder("[Client-Server communication]")
                        .append("(").append(request.toString()).append(") ");
                message.append("Send timeout - Could not send sync request to server after " + TIMEOUT_SYNC_SEND + "ms");
                throw new TimeoutException(message.toString());
            }
            if (writeFuture.getException() != null) {
                throw (writeFuture.getException());
            }
            response = this.clientSessionHandler.waitForSyncAnswerInfinite(this.session, request.getReferenceId());
        } catch (Throwable throwable) {
            this.clientSessionHandler.syncRequestFailed(request, response, throwable);
        } finally {
            //remove the request from the map
            this.clientSessionHandler.removeSyncRequest(request);
        }
        return (response);
    }

    /**
     * Sends a sync message and throws a timeout exception if the client does
     * not answer in a proper time. The passed timeout is not the connection
     * timeout (this is set to TIMEOUT_SYNC_SEND [in ms] by default - it is the
     * sync wait timeout)
     *
     * @param message
     */
    public ClientServerResponse sendSync(ClientServerMessage request, long timeout) {
        if (!this.isConnected()) {
            throw new IllegalStateException("[Client-Server communication] "
                    + "BaseClient.sendSync: Not connected to a server. Please connect first.");
        }
        ClientServerResponse response = null;
        request._setSyncRequest(true);
        try {
            this.clientSessionHandler.addSyncRequest(request);
            WriteFuture writeFuture = this.session.write(request);
            boolean isSent = writeFuture.await(TIMEOUT_SYNC_SEND, TimeUnit.MILLISECONDS);
            if (!isSent) {
                StringBuilder message = new StringBuilder("[Client-Server communication]")
                        .append("(").append(request.toString()).append(") ")
                        .append("Send timeout - Could not send sync request to server after " + TIMEOUT_SYNC_SEND + "ms");
                SyncRequestTimeoutException exception = new SyncRequestTimeoutException(
                        SyncRequestTimeoutException.TIMEOUT_TYPE_SEND,
                        message.toString(), request, TIMEOUT_SYNC_SEND);
                throw exception;
            }
            if (writeFuture.getException() != null) {
                throw (writeFuture.getException());
            }
            response = this.clientSessionHandler.waitForSyncAnswer(request.getReferenceId(), timeout);
            if (response == null) {
                StringBuilder message = new StringBuilder("[Client-Server communication]");
                message.append("(").append(request.toString()).append(") ")
                        .append("Receipt timeout - Could not receive the sync response after " + timeout + "ms [");
                message.append(request.getClass().getSimpleName()).append("]");
                SyncRequestTimeoutException exception = new SyncRequestTimeoutException(
                        SyncRequestTimeoutException.TIMEOUT_TYPE_RECEIVE,
                        message.toString(), request, timeout);
                throw exception;
            }
        } catch (Throwable throwable) {
            this.clientSessionHandler.syncRequestFailed(request, response, throwable);
        } finally {
            //remove the request from the map
            this.clientSessionHandler.removeSyncRequest(request);
        }
        return (response);
    }

    /**
     * Performs a logout, closes the session
     */
    public void logout() {
        if (this.session != null) {
            if (this.session.isConnected()) {
                if (this.user == null) {
                    this.log(Level.WARNING, "[Client-Server communication] Logout failed, you are not logged in so far!");
                } else {
                    QuitRequest quitRequest = new QuitRequest();
                    quitRequest.setUser(this.user.getName());
                    this.session.write(quitRequest);
                    this.user = null;
                }
            }
            this.session.close(false);
        }
    }

    /**
     * Disconnects the connector
     */
    public void disconnect() {
        try {
            if (this.unorderedThreadPoolExecutor != null) {
                this.unorderedThreadPoolExecutor.shutdownNow();
            }
            if (this.executorFilter != null) {
                this.executorFilter.destroy();
            }
            if (this.connector != null && !this.connector.isDisposed()) {
                this.connector.dispose(true);
            }
        } catch (Exception e) {
            //nop
        }
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the current user or null if there is nobody logged in
     */
    public User getUser() {
        return (this.user);
    }

    /**
     * Returns the host address if the server is connected and the client is
     * logged in- else null
     *
     * @return null if the client is not connected or not logged in to the
     * server
     */
    public InetAddress getHostAddress() {
        if (this.isConnectedAndLoggedIn()) {
            InetAddress serviceAddress = ((InetSocketAddress) this.session.getServiceAddress()).getAddress();
            return (serviceAddress);
        } else {
            return (null);
        }
    }

}
