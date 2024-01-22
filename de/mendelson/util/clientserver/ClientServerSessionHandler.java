//$Header: /oftp2/de/mendelson/util/clientserver/ClientServerSessionHandler.java 48    2/09/22 11:32 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.LoginRequest;
import de.mendelson.util.clientserver.messages.LoginRequired;
import de.mendelson.util.clientserver.messages.LoginState;
import de.mendelson.util.clientserver.messages.QuitRequest;
import de.mendelson.util.clientserver.messages.ServerInfo;
import de.mendelson.util.clientserver.messages.ServerLogMessage;
import de.mendelson.util.clientserver.user.PermissionDescription;
import de.mendelson.util.clientserver.user.User;
import de.mendelson.util.clientserver.user.UserAccess;
import de.mendelson.util.systemevents.SystemEventManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteToClosedSessionException;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Session handler for the server implementation
 *
 * @author S.Heller
 * @version $Revision: 48 $
 */
public class ClientServerSessionHandler extends IoHandlerAdapter {

    public static final String SESSION_ATTRIB_USER = "user";
    public static final String SESSION_ATTRIB_CLIENT_PID = "pid";
    public static final String SESSION_ATTRIB_CLIENT_IP = "ip";
    /**
     * User readable description of user permissions
     */
    private PermissionDescription permissionDescription = null;
    private Logger logger = Logger.getAnonymousLogger();
    /**
     * Synchronized structure to perform user defined processing on the server
     * depending on the incoming message object type
     */
    private final List<ClientServerProcessing> processingList = Collections.synchronizedList(new ArrayList<ClientServerProcessing>());
    /**
     * Stores the product name, this is displayed on login requests
     */
    private String productName = "";
    /**
     * Stores all sessions
     */
    private final List<IoSession> sessions = Collections.synchronizedList(new ArrayList<IoSession>());
    private PasswordValidationHandler loginHandler;
    /**
     * Allows to access the server for special messages without a required login
     */
    private AnonymousProcessing anonymousProcessing = null;
    /**
     * Generate a server hello message for a client once it is connected to the
     * server
     */
    private ServerHelloMessageGenerator serverHelloMessageGenerator = null;
    private ClientServerSessionHandlerCallback callback = null;
    private String[] validClientIds = null;
    private final int maxClients;
    private SystemEventManager eventManager;

    public ClientServerSessionHandler(Logger logger, String[] validClientIds, int maxClients, SystemEventManager eventManager) {
        if (logger != null) {
            this.logger = logger;
        }
        this.eventManager = eventManager;
        this.maxClients = maxClients;
        this.validClientIds = validClientIds;
        this.loginHandler = new PasswordValidationHandler(validClientIds);
    }

    public void setCallback(ClientServerSessionHandlerCallback callback) {
        this.callback = callback;
    }

    /**
     * Get all available sessions
     */
    public List<IoSession> getSessions() {
        List<IoSession> sessionList = new ArrayList<IoSession>();
        synchronized (this.sessions) {
            sessionList.addAll(this.sessions);
        }
        return (Collections.unmodifiableList(sessionList));
    }

    /**
     * Allows to process messages without login, e.g. server state
     */
    public void setAnonymousProcessing(AnonymousProcessing anonymousProcessing) {
        this.anonymousProcessing = anonymousProcessing;
    }

    /**
     * Add a implementation that generates a server hello message for a client
     * once it is connected to the server
     */
    public void setServerHelloMessageGenerator(ServerHelloMessageGenerator serverHelloMessageGenerator) {
        this.serverHelloMessageGenerator = serverHelloMessageGenerator;
    }

    /**
     * Logs something to the clients log - but only if the level is higher than
     * the defined loglevelThreshold
     */
    public void log(Level logLevel, String message) {
        this.logger.log(logLevel, message);
    }

    private void throwEventLoginFailed(IoSession session, LoginState loginState, LoginRequest loginRequest) {
        this.eventManager.newEventClientLoginFailure(loginState, session.getRemoteAddress(), String.valueOf(session.getId()),
                loginRequest);
    }

    /**Informs the event manager that a successful login has been performed. Finds out the TLS protocol and the cipher suite
     *      
     */
    private void throwEventLoginSuccess(IoSession session, LoginState loginState, LoginRequest loginRequest) {
        String tlsProtocol = null;
        String tlsCipherSuite = null;
        if (session.isSecured()) {
            Set<Object> keys = session.getAttributeKeys();
            for( Object key:keys){
                if( session.getAttribute(key) instanceof SSLSession){
                    SSLSession sslSession = (SSLSession)session.getAttribute(key);
                    tlsProtocol = sslSession.getProtocol();
                    tlsCipherSuite = sslSession.getCipherSuite();
                    break;
                }
            }
        }
        this.eventManager.newEventClientLoginSuccess(loginState, session.getRemoteAddress(), String.valueOf(session.getId()),
                loginRequest, tlsProtocol, tlsCipherSuite);
    }

    private void throwEventLogoff(IoSession session, String message) {
        try {
            String remoteProcessId = (String) session.getAttribute(SESSION_ATTRIB_CLIENT_PID);
            String userName = (String) session.getAttribute(SESSION_ATTRIB_USER);
            //this is tricky - if the session is closed it has no longer a remote IP - that is why it is stored
            //as session parameter
            String clientIP = (String) session.getAttribute(SESSION_ATTRIB_CLIENT_IP);
            this.eventManager.newEventClientLogoff(clientIP, userName, remoteProcessId,
                    String.valueOf(session.getId()), message);
        } catch (Exception e) {
            System.out.println("ClientServerSessionHandler.logoff: ["
                    + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
    }

    private void throwEventExceptionInClientServerProcess(IoSession session, String message) {
        try {
            String remoteProcessId = (String) session.getAttribute(SESSION_ATTRIB_CLIENT_PID);
            String userName = (String) session.getAttribute(SESSION_ATTRIB_USER);
            //this is tricky - if the session is closed it has no longer a remote IP - that is why it is stored
            //as session parameter
            String clientIP = (String) session.getAttribute(SESSION_ATTRIB_CLIENT_IP);
            this.eventManager.newEventExceptionInClientServerProcess(clientIP, userName, remoteProcessId,
                    String.valueOf(session.getId()), message);
        } catch (Exception e) {
            System.out.println("ClientServerSessionHandler.clientserver: ["
                    + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
    }

    @Override
    /**
     * The session has been opened: send a server info object This is an
     * incoming connection from a client
     */
    public void sessionOpened(IoSession session) {
        //store immediatly the remote IP address in the session - if the session is closed it is no longer 
        //available and it might be required later even if the session goes into the closed state
        session.setAttribute(SESSION_ATTRIB_CLIENT_IP, session.getRemoteAddress().toString());
        //send information about what this server is
        ServerInfo info = new ServerInfo();
        info.setProductname(this.productName);
        session.write(info);
        //request a login
        LoginRequired loginRequired = new LoginRequired();
        session.write(loginRequired);
    }

    public void setPermissionDescription(PermissionDescription permissionDescription) {
        this.permissionDescription = permissionDescription;
    }

    /**
     * Incoming message on the server site
     */
    @Override
    public void messageReceived(IoSession session, Object messageObj) {
        if (!(messageObj instanceof ClientServerMessage)) {
            return;
        }
        ClientServerMessage message = (ClientServerMessage) messageObj;
        if (message instanceof QuitRequest) {
            session.closeOnFlush();
            return;
        }
        if (this.anonymousProcessing != null && this.anonymousProcessing.processMessageWithoutLogin(session, message)) {
            this.performUserDefinedProcessing(session, message);
        } else {
            //it is a login request
            if (message instanceof LoginRequest) {
                LoginRequest loginRequest = (LoginRequest) message;
                UserAccess access = new UserAccess(this.logger);
                //validate passwd first, close session if it fails
                User definedUser = access.readUser(loginRequest.getUserName());
                if (definedUser != null && this.permissionDescription != null) {
                    definedUser.setPermissionDescription(this.permissionDescription);
                }
                User transmittedUser = new User();
                transmittedUser.setName(loginRequest.getUserName());
                int validationState = this.loginHandler.validate(definedUser, loginRequest.getPasswd(),
                        loginRequest.getClientId());
                if (validationState == PasswordValidationHandler.STATE_FAILURE) {
                    LoginState loginStateMessage = new LoginState(loginRequest);
                    if (this.serverHelloMessageGenerator != null) {
                        loginStateMessage.setServerHelloMessages(this.serverHelloMessageGenerator.generateServerHelloMessages());
                    }
                    loginStateMessage.setUser(transmittedUser);
                    loginStateMessage.setState(LoginState.STATE_AUTHENTICATION_FAILURE);
                    loginStateMessage.setStateDetails("Authentication failed: Wrong user/password combination or user does not exist");
                    this.throwEventLoginFailed(session, loginStateMessage, loginRequest);
                    session.write(loginStateMessage);
                    return;
                } else if (validationState == PasswordValidationHandler.STATE_INCOMPATIBLE_CLIENT) {
                    LoginState loginStateMessage = new LoginState(loginRequest);
                    if (this.serverHelloMessageGenerator != null) {
                        loginStateMessage.setServerHelloMessages(
                                this.serverHelloMessageGenerator.generateServerHelloMessages());
                    }
                    loginStateMessage.setUser(transmittedUser);
                    loginStateMessage.setState(LoginState.STATE_INCOMPATIBLE_CLIENT);
                    StringBuilder validClientIdStr = new StringBuilder();
                    for (String clientId : this.validClientIds) {
                        if (validClientIdStr.length() > 0) {
                            validClientIdStr.append(", ");
                        }
                        validClientIdStr.append(clientId);
                    }
                    loginStateMessage.setStateDetails("The login process to the server has failed because the client is incompatible. Please ensure that client and server have the same version. Client version: ["
                            + loginRequest.getClientId() + "], Server version: [" + validClientIdStr + "]");
                    this.throwEventLoginFailed(session, loginStateMessage, loginRequest);
                    session.write(loginStateMessage);
                    session.closeOnFlush();
                    return;
                } else if (validationState == PasswordValidationHandler.STATE_PASSWORD_REQUIRED) {
                    LoginState loginStateMessage = new LoginState(loginRequest);
                    if (this.serverHelloMessageGenerator != null) {
                        loginStateMessage.setServerHelloMessages(
                                this.serverHelloMessageGenerator.generateServerHelloMessages());
                    }
                    loginStateMessage.setUser(transmittedUser);
                    loginStateMessage.setState(LoginState.STATE_AUTHENTICATION_FAILURE_PASSWORD_REQUIRED);
                    loginStateMessage.setStateDetails("Authentication failed, password required for user [" + loginRequest.getUserName() + "]");
                    this.throwEventLoginFailed(session, loginStateMessage, loginRequest);
                    session.write(loginStateMessage);
                    return;
                }
                synchronized (this.sessions) {
                    if (this.maxClients > 0 && this.sessions.size() + 1 > this.maxClients) {
                        LoginState loginStateMessage = new LoginState(loginRequest);
                        if (this.serverHelloMessageGenerator != null) {
                            loginStateMessage.setServerHelloMessages(
                                    this.serverHelloMessageGenerator.generateServerHelloMessages());
                        }
                        loginStateMessage.setUser(transmittedUser);
                        loginStateMessage.setState(LoginState.STATE_REJECTED);
                        loginStateMessage.setStateDetails("Login request rejected.");
                        this.throwEventLoginFailed(session, loginStateMessage, loginRequest);
                        session.write(loginStateMessage);
                        return;
                    }
                }
                //user is logged in: add the user name to the session
                session.setAttribute(SESSION_ATTRIB_USER, loginRequest.getUserName());
                session.setAttribute(SESSION_ATTRIB_CLIENT_PID, loginRequest.getPID());
                //add the session to the list of available sessions
                synchronized (this.sessions) {
                    this.sessions.add(session);
                }
                //success!
                LoginState loginSuccessState = new LoginState(loginRequest);
                if( this.serverHelloMessageGenerator != null ){
                        loginSuccessState.setServerHelloMessages(
                                this.serverHelloMessageGenerator.generateServerHelloMessages());
                    }
                loginSuccessState.setState(LoginState.STATE_AUTHENTICATION_SUCCESS);
                loginSuccessState.setStateDetails("Authentication successful, user [" + definedUser.getName() + "] logged in");
                loginSuccessState.setUser(definedUser);
                this.throwEventLoginSuccess(session, loginSuccessState, loginRequest);
                session.write(loginSuccessState);
                if (this.callback != null) {
                    this.callback.clientLoggedIn(session);
                }
                return;
            }
            boolean loggedIn = session.containsAttribute(SESSION_ATTRIB_USER);
            //user not logged in so far
            if (!loggedIn) {
                LoginRequired loginRequired = new LoginRequired();
                User userObj = new User();
                if (this.permissionDescription != null) {
                    userObj.setPermissionDescription(this.permissionDescription);
                }
                loginRequired.setUser(userObj);
                session.write(loginRequired);
                session.closeOnFlush();
                return;
            }
            //here starts the user defined processing to extend the server functionality
            this.performUserDefinedProcessing(session, message);
        }
    }

    /**
     * User defined extensions for the server processing
     */
    private void performUserDefinedProcessing(IoSession session, ClientServerMessage message) {
        boolean processed = false;
        synchronized (this.processingList) {
            for (int i = 0; i < this.processingList.size(); i++) {
                processed |= this.processingList.get(i).process(session, message);
            }
        }
        if (!processed) {
            this.log(Level.WARNING, "performUserDefinedProcessing: inbound message of class ["
                    + message.getClass().getName() + "] has not been processed.");
        }
    }

    /**
     * User defined actions for messages sent by any client. The user may extend
     * the framework by implementing a ServerProcessing interface
     */
    public void addServerProcessing(ClientServerProcessing serverProcessing) {
        synchronized (this.processingList) {
            this.processingList.add(serverProcessing);
        }
    }

    /**
     * Sends a message object to all connected clients
     */
    public void broadcast(Object data) {
        synchronized (this.sessions) {
            for (IoSession session : this.sessions) {
                if (session.isConnected()) {
                    session.write(data);
                }
            }
        }
    }

    /**
     * Sends a log message to all connected clients
     */
    public void broadcastLogMessage(Level level, String message, Object[] parameter) {
        ServerLogMessage serverMessage = new ServerLogMessage();
        serverMessage.setLevel(level);
        serverMessage.setMessage(message);
        serverMessage.setParameter(parameter);
        this.broadcast(serverMessage);
    }

    /**
     * Sends a log message to all connected clients
     */
    public void broadcastLogMessage(Level level, String message) {
        this.broadcastLogMessage(level, message, null);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        String user = (String) session.getAttribute(SESSION_ATTRIB_USER);
        if (user != null) {
            synchronized (this.sessions) {
                this.sessions.remove(session);
            }
            this.throwEventLogoff(session, "");
            //this.log(Level.INFO, "Session closed for user " + user);
            if (this.callback != null) {
                this.callback.clientDisconnected(session);
            }
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        this.throwEventLogoff(session, "");
        // disconnect an idle client
        session.closeOnFlush();
        if (this.callback != null) {
            this.callback.clientDisconnected(session);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        //The method org.apache.mina.core.polling.AbstractPollingIoProcessor  seems to have a problem
        //in the method clearWriteRequestQueue(AbstractPollingIoProcessor.java:1200) in MINA 2.2.1 - this will be ignored here
        if( cause instanceof WriteToClosedSessionException){
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Exception caught in client-server interface.").append("\n");
        builder.append("[" + cause.getClass().getSimpleName() + "]: " + cause.getMessage()).append("\n\n");
        StackTraceElement[] stackTrace = cause.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            builder.append(element.getClassName() + ": " + element.getMethodName() + "\n");
        }
        this.throwEventExceptionInClientServerProcess(session, builder.toString());
        // Close connection when unexpected exception is caught.
        session.closeNow();
        if (this.callback != null) {
            this.callback.clientDisconnected(session);
        }
    }

    public int getConnectedClients() {
        synchronized (this.sessions) {
            return (this.sessions.size());
        }
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
