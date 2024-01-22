//$Header: /as2/de/mendelson/comm/as2/send/MessageHttpUploader.java 202   16/12/22 14:11 Heller $
package de.mendelson.comm.as2.send;

import de.mendelson.comm.as2.clientserver.message.IncomingMessageRequest;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageResponse;
import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.message.AS2Info;
import de.mendelson.comm.as2.message.AS2MDNInfo;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.MDNAccessDB;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.message.store.MessageStoreHandler;
import de.mendelson.comm.as2.partner.HTTPAuthentication;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerHttpHeader;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.statistic.QuotaAccessDB;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.AnonymousTextClient;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.oauth2.OAuth2Config;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.security.cert.KeystoreStorageImplFile;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.MimeUtility;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Class to allow HTTP multipart uploads
 *
 * @author S.Heller
 * @version $Revision: 202 $
 */
public class MessageHttpUploader {

    private Logger logger = null;
    private final PreferencesAS2 preferences = new PreferencesAS2();
    /**
     * localize the GUI
     */
    private final MecResourceBundle rb;
    /**
     * The header that has been built fro the request
     */
    private final Properties requestHeader = new Properties();
    /**
     * remote answer
     */
    private byte[] responseData = null;
    /**
     * remote answer
     */
    private Header[] responseHeader = null;
    /**
     * remote answer
     */
    private StatusLine responseStatusLine = null;
    private ClientServer clientserver = null;
    private IDBDriverManager dbDriverManager = null;
    //keystore data
    private KeystoreStorage certStore = null;
    private KeystoreStorage trustStore = null;
    //EDIINT faetures
    private String ediintFeatures = "multiple-attachments, CEM";

    /**
     * Creates new message uploader instance
     *
     * @param hostname Name of the host to connect to
     * @param username Name of the user that will connect to the remote ftp
     * server
     * @param password password to connect to the ftp server
     */
    public MessageHttpUploader() throws Exception {
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleHttpUploader.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Sets the capabilities of the system, defaults to "multiple-attachments,
     * CEM"
     */
    public void setEDIINTFeatures(String ediintFeatures) {
        this.ediintFeatures = ediintFeatures;
    }

    /**
     * Sets keystore parameter for TLS sending. This is only necessary if HTTPS
     * is the protocol used for the message POST
     *
     * @param truststore Truststore file
     * @param truststorePass Password for the truststore
     * @param certstore Keystore file
     * @param certstorePass Password for the keystore
     */
    public void setSSLParameter(KeystoreStorage certStore, KeystoreStorage trustStore) {
        this.certStore = certStore;
        this.trustStore = trustStore;
    }

    /**
     * Passes a logger to this class for logging purpose
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Passes a server instance to this class to refresh messages automatically
     * for logging purpose
     */
    public void setAbstractServer(ClientServer clientserver) {
        this.clientserver = clientserver;
    }

    /**
     * Pass a DB access to this class for logging purpose
     */
    public void setDBConnection(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Returns the created header for the sent data
     */
    public Properties upload(HttpConnectionParameter connectionParameter, AS2Message message,
            Partner sender, Partner receiver) throws Exception {
        NumberFormat formatter = new DecimalFormat("0.00");
        AS2Info as2Info = message.getAS2Info();
        MessageAccessDB messageAccess = null;
        MDNAccessDB mdnAccess = null;
        if (this.dbDriverManager != null && messageAccess == null && !as2Info.isMDN()) {
            messageAccess = new MessageAccessDB(this.dbDriverManager);
            messageAccess.initializeOrUpdateMessage((AS2MessageInfo) as2Info);
        } else if (this.dbDriverManager != null && as2Info.isMDN()) {
            mdnAccess = new MDNAccessDB(this.dbDriverManager);
            mdnAccess.initializeOrUpdateMDN((AS2MDNInfo) as2Info);
        }
        if (this.clientserver != null) {
            this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
        }
        long startTime = System.currentTimeMillis();
        //sets the global requestHeader
        int returnCode = this.performUpload(connectionParameter, message, sender, receiver);
        long size = message.getRawDataSize();
        long transferTime = System.currentTimeMillis() - startTime;
        float bytePerSec = (float) ((float) size * 1000f / (float) transferTime);
        float kbPerSec = (float) (bytePerSec / 1024f);
        if (returnCode == HttpServletResponse.SC_OK) {
            if (this.logger != null) {
                this.logger.log(Level.INFO,
                        this.rb.getResourceString("returncode.ok",
                                new Object[]{
                                    String.valueOf(returnCode),
                                    AS2Tools.getDataSizeDisplay(size),
                                    AS2Tools.getTimeDisplay(transferTime),
                                    formatter.format(kbPerSec),}), as2Info);
            }
        } else if (returnCode == HttpServletResponse.SC_ACCEPTED || returnCode == HttpServletResponse.SC_CREATED || returnCode == HttpServletResponse.SC_NO_CONTENT || returnCode == HttpServletResponse.SC_RESET_CONTENT || returnCode == HttpServletResponse.SC_PARTIAL_CONTENT) {
            if (this.logger != null) {
                this.logger.log(Level.INFO,
                        this.rb.getResourceString("returncode.accepted",
                                new Object[]{
                                    String.valueOf(returnCode),
                                    AS2Tools.getDataSizeDisplay(size),
                                    AS2Tools.getTimeDisplay(transferTime),
                                    formatter.format(kbPerSec),}), as2Info);
            }
        } else {
            //If the returncode is -1 here, this has been already handled by the upload routine 
            //- its a TLS Problem or a timeout problem
            if (returnCode > 0) {
                //no connection
                new SystemEventManagerImplAS2().newEventConnectionProblem(receiver, message.getAS2Info(),
                        this.rb.getResourceString("error.noconnection"),
                        this.rb.getResourceString("hint.httpcode.signals.problem",
                                String.valueOf(returnCode)));
                if (this.logger != null) {
                    this.logger.log(Level.SEVERE, this.rb.getResourceString("hint.httpcode.signals.problem",
                            String.valueOf(returnCode)), message.getAS2Info());
                }
                throw new NoConnectionException("[" + receiver.getURL() + "]: HTTP " + returnCode);
            } else {
                throw new NoConnectionException("");
            }
        }
        //store the sent data and assign the payload to the message
        if (this.dbDriverManager != null) {
            MessageStoreHandler messageStoreHandler = new MessageStoreHandler(this.dbDriverManager);
            messageStoreHandler.storeSentMessage(message, sender, receiver, this.getRequestHeader());
        }
        //perform some statistic entries
        if (this.dbDriverManager != null) {
            //inc the sent data size, this is for new connections (as2 messages, async mdn)
            AS2Server.incRawSentData(size);
            if (message.getAS2Info().isMDN()) {
                AS2MDNInfo mdnInfo = (AS2MDNInfo) message.getAS2Info();
                //ASYNC MDN sent: insert an entry into the statistic table
                QuotaAccessDB.incReceivedMessages(this.dbDriverManager, mdnInfo.getSenderId(),
                        mdnInfo.getReceiverId(), mdnInfo.getState(), mdnInfo.getRelatedMessageId());
            }
        }
        //inform the server of the result if a sync MDN has been requested
        if (!message.isMDN()) {
            AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
            if (messageInfo.requestsSyncMDN()) {
                //check if the received MDN is just empty
                if(( this.getResponseHeader() == null || this.getResponseHeader().length == 0)
                        && (this.getResponseData() == null || this.getResponseData().length == 0)){
                    throw new Exception(this.rb.getResourceString("answer.no.sync.mdn.empty"));
                }                
                //perform a check if the answer really contains a MDN or is just an empty HTTP 200 with some header data
                //this check looks for the existance of some key header values
                boolean as2FromExists = false;
                boolean as2ToExists = false;
                for (int i = 0; i < this.getResponseHeader().length; i++) {
                    String key = this.getResponseHeader()[i].getName();
                    if (key.toLowerCase().equals("as2-to")) {
                        as2ToExists = true;
                    } else if (key.toLowerCase().equals("as2-from")) {
                        as2FromExists = true;
                    }
                }
                if (!as2ToExists) {
                    StringBuilder missingHeaderList = new StringBuilder("\"AS2-TO\"");
                    if (!as2FromExists) {
                        missingHeaderList.append(", \"AS2-FROM\"");
                    }
                    String responseDataStr = null;
                    byte[] responseData = this.getResponseData();
                    if (responseData.length < 1024) {
                        responseDataStr = new String(responseData);
                    } else {
                        responseDataStr = new String(responseData, 0, 1024);
                    }
                    throw new Exception(this.rb.getResourceString("answer.no.sync.mdn",
                            new Object[]{
                                missingHeaderList.toString(),
                                responseDataStr}));
                }
                //send the data to the as2 server. It does not care if the MDN has been sync or async anymore
                AnonymousTextClient client = null;
                Path tempFile = null;
                OutputStream outStream = null;
                try {
                    client = new AnonymousTextClient();
                    client.setDisplayServerLogMessages(false);
                    client.connect("localhost", AS2Server.CLIENTSERVER_COMM_PORT, 30000);
                    IncomingMessageRequest messageRequest = new IncomingMessageRequest();
                    //create temporary file to store the data
                    tempFile = AS2Tools.createTempFile("SYNCMDN_received", ".bin");
                    outStream = Files.newOutputStream(tempFile,
                            StandardOpenOption.SYNC,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE);
                    ByteArrayInputStream memIn = new ByteArrayInputStream(this.responseData);
                    memIn.transferTo(outStream);
                    memIn.close();
                    messageRequest.setMessageDataFilename(tempFile.toAbsolutePath().toString());
                    for (int i = 0; i < this.getResponseHeader().length; i++) {
                        String key = this.getResponseHeader()[i].getName();
                        String value = this.getResponseHeader()[i].getValue();
                        messageRequest.addHeader(key.toLowerCase(), value);
                        if (key.toLowerCase().equals("content-type")) {
                            messageRequest.setContentType(value);
                        }
                    }
                    //compatibility issue: some AS2 systems do not send a as2-from in the sync case, even if
                    //this if _NOT_ RFC conform
                    //see RFC 4130, section 6.2: The AS2-To and AS2-From header fields MUST be
                    //present in all AS2 messages and AS2 MDNs whether asynchronous or synchronous in nature,
                    //except for asynchronous MDNs, which are sent using SMTP.
                    if (!as2FromExists) {
                        messageRequest.addHeader("as2-from", AS2Message.escapeFromToHeader(receiver.getAS2Identification()));
                    }
                    IncomingMessageResponse response = (IncomingMessageResponse) client.sendSyncWaitInfinite(messageRequest);
                    if (response.getException() != null) {
                        throw (response.getException());
                    }
                } catch (Throwable e) {
                    if (this.logger != null) {
                        this.logger.log(Level.SEVERE, e.getMessage(), as2Info);
                    }
                    messageAccess.setMessageState(as2Info.getMessageId(), AS2Message.STATE_STOPPED);
                } finally {
                    if (client != null && client.isConnected()) {
                        client.disconnect();
                    }
                    if (outStream != null) {
                        outStream.flush();
                        outStream.close();
                    }
                }
                if (tempFile != null) {
                    try {
                        Files.delete(tempFile);
                    } catch (Exception e) {
                        SystemEvent event = new SystemEvent(
                                SystemEvent.SEVERITY_WARNING,
                                SystemEvent.ORIGIN_SYSTEM,
                                SystemEvent.TYPE_FILE_DELETE);
                        event.setSubject(event.typeToTextLocalized());
                        event.setBody("[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
                        SystemEventManagerImplAS2.newEvent(event);
                    }
                }
            }
        }
        return (this.getRequestHeader());
    }

    /**
     * Builds a proxy object from the actual preferences, returns null if no
     * proxy is requested
     */
    public ProxyObject createProxyObjectFromPreferences() {
        if (!this.preferences.getBoolean(PreferencesAS2.PROXY_USE)) {
            //return empty proxy object, is not used
            return (null);
        }
        ProxyObject proxy = new ProxyObject();
        proxy.setHost(this.preferences.get(PreferencesAS2.PROXY_HOST));
        proxy.setPort(this.preferences.getInt(PreferencesAS2.PROXY_PORT));
        if (this.preferences.getBoolean(PreferencesAS2.AUTH_PROXY_USE)) {
            proxy.setUser(this.preferences.get(PreferencesAS2.AUTH_PROXY_USER));
            proxy.setPassword(this.preferences.get(PreferencesAS2.AUTH_PROXY_PASS).toCharArray());
        }
        return (proxy);
    }

    /**
     * Uploads the data, returns the HTTP result code
     */
    public int performUpload(HttpConnectionParameter connectionParameter, AS2Message message, Partner sender, Partner receiver) {
        return (this.performUpload(connectionParameter, message, sender, receiver, null));
    }

    /**
     * Creates a request config to be passed to the HttpPost - it contains basic
     * HTTP settings
     */
    private RequestConfig generateRequestConfig(HttpConnectionParameter connectionParameter) {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        if (connectionParameter.getConnectionTimeoutMillis() != -1) {
            requestConfigBuilder.setConnectionRequestTimeout(connectionParameter.getConnectionTimeoutMillis());
        }
        if (connectionParameter.getSoTimeoutMillis() != -1) {
            requestConfigBuilder.setSocketTimeout(connectionParameter.getConnectionTimeoutMillis());
        }
        requestConfigBuilder.setStaleConnectionCheckEnabled(connectionParameter.isStaleConnectionCheck());
        requestConfigBuilder.setExpectContinueEnabled(connectionParameter.isUseExpectContinue());
        requestConfigBuilder.setContentCompressionEnabled(false);
        if (connectionParameter.getLocalAddress() != null) {
            requestConfigBuilder.setLocalAddress(connectionParameter.getLocalAddress());
        }
        return (requestConfigBuilder.build());
    }

    /**
     * Generate Preemptive Basic Authentication header. This will create a
     * HttpClientContext object which has to be used for the POST execution
     *
     * @param credentialsProvider
     * @param authentication
     */
    private HttpClientContext generateHttpClientContextForBasicAuth(
            HttpHost targetHost,
            CredentialsProvider credentialsProvider, HTTPAuthentication authentication) {
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(authentication.getUser(), authentication.getPassword());
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());
        HttpClientContext httpClientContext = HttpClientContext.create();
        httpClientContext.setCredentialsProvider(credentialsProvider);
        httpClientContext.setAuthCache(authCache);
        return (httpClientContext);
    }

    /**
     * Adds a proxy route to the client builder
     *
     * @param proxy
     * @param clientBuilder
     * @param credentialsProvider
     * @throws Exception
     */
    private void addProxy(ProxyObject proxy, HttpClientBuilder clientBuilder, CredentialsProvider credentialsProvider,
            AS2Message message) throws Exception {
        HttpHost proxyHost = new HttpHost(proxy.getHost(), proxy.getPort(), HttpHost.DEFAULT_SCHEME_NAME);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);
        //proxy authentication
        if (proxy.getUser() != null) {
            credentialsProvider.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                    new UsernamePasswordCredentials(proxy.getUser(), String.valueOf(proxy.getPassword())));
            BasicScheme basicAuth = new BasicScheme();
            AuthCache authCache = new BasicAuthCache();
            authCache.put(proxyHost, basicAuth);
            HttpClientContext context = HttpClientContext.create();
            context.setCredentialsProvider(credentialsProvider);
            context.setAuthCache(authCache);
            clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            this.logger.log(Level.INFO,
                    this.rb.getResourceString("using.proxy.auth",
                            new Object[]{
                                proxy.getHost(),
                                String.valueOf(proxy.getPort()),
                                proxy.getUser()
                            }), message.getAS2Info());
        } else {
            this.logger.log(Level.INFO,
                    this.rb.getResourceString("using.proxy",
                            new Object[]{
                                proxy.getHost(),
                                String.valueOf(proxy.getPort())
                            }), message.getAS2Info());
        }
        clientBuilder.setRoutePlanner(routePlanner);
    }

    /**
     * Uploads the data, returns the HTTP result code
     *
     * @param receiptURL Receivers URL, might be NULL
     */
    public int performUpload(HttpConnectionParameter connectionParameter, AS2Message message,
            Partner sender, Partner receiver, URL receiptURL) {
        int statusCode = -1;
        HttpPost filePost = null;
        CloseableHttpClient httpClient = null;
        try {
            //determine the receipt URL if it is not set
            if (receiptURL == null) {
                //async MDN requested?
                if (message.isMDN()) {
                    if (this.dbDriverManager == null) {
                        throw new IllegalArgumentException("MessageHTTPUploader.performUpload(): A MDN receipt URL is not set, unable to determine where to send the MDN");
                    }
                    MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
                    AS2MessageInfo relatedMessageInfo = messageAccess.getLastMessageEntry(
                            ((AS2MDNInfo) message.getAS2Info()).getRelatedMessageId());
                    receiptURL = new URL(relatedMessageInfo.getAsyncMDNURL());
                } else {
                    receiptURL = new URL(receiver.getURL());
                }
            }           
            //create the http client
            HttpClientBuilder clientBuilder = HttpClients.custom();
            if (receiptURL.getProtocol().equalsIgnoreCase("https")) {
                clientBuilder.setSSLSocketFactory(this.generateSSLFactory(connectionParameter, message.getAS2Info()));
            }
            clientBuilder.setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE);
            HttpHost targetHost = new HttpHost(receiptURL.getHost(), receiptURL.getPort(), receiptURL.getProtocol());
            boolean credentialAuthUsed = false;
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            HttpClientContext httpClientContext = null;
            if (receiver.getAuthenticationCredentialsMessage().isEnabled()) {
                credentialAuthUsed = true;
                httpClientContext = this.generateHttpClientContextForBasicAuth(
                        targetHost, credentialsProvider, receiver.getAuthenticationCredentialsMessage());
            }
            ProxyObject proxy = connectionParameter.getProxy();
            if (proxy != null && proxy.getHost() != null) {
                credentialAuthUsed = true;
                this.addProxy(proxy, clientBuilder, credentialsProvider, message);
            }
            if (credentialAuthUsed) {
                clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
            httpClient = clientBuilder.build();
            filePost = new HttpPost(receiptURL.toExternalForm());
            filePost.setConfig(this.generateRequestConfig(connectionParameter));
            if (connectionParameter.getHttpProtocolVersion() == null) {
                //default settings: HTTP 1.1
                filePost.setProtocolVersion(HttpVersion.HTTP_1_1);
            } else if (connectionParameter.getHttpProtocolVersion().equals(HttpConnectionParameter.HTTP_1_0)) {
                filePost.setProtocolVersion(HttpVersion.HTTP_1_0);
            } else if (connectionParameter.getHttpProtocolVersion().equals(HttpConnectionParameter.HTTP_1_1)) {
                filePost.setProtocolVersion(HttpVersion.HTTP_1_1);
            }
            filePost.addHeader("as2-version", "1.2");
            filePost.addHeader("ediint-features", ediintFeatures);
            filePost.addHeader("mime-version", "1.0");
            filePost.addHeader("recipient-address", receiptURL.toExternalForm());
            filePost.addHeader("message-id", "<" + message.getAS2Info().getMessageId() + ">");
            filePost.addHeader("as2-from", AS2Message.escapeFromToHeader(sender.getAS2Identification()));
            filePost.addHeader("as2-to", AS2Message.escapeFromToHeader(receiver.getAS2Identification()));
            String originalFilename = null;
            if (message.getPayloads() != null && !message.getPayloads().isEmpty()) {
                originalFilename = message.getPayloads().get(0).getOriginalFilename();
            }
            if (originalFilename != null) {
                String subject = this.replaceSubject(message.getAS2Info().getSubject(), originalFilename);
                filePost.addHeader("subject", subject);
                //update the message infos subject with the actual content
                if (!message.isMDN()) {
                    ((AS2MessageInfo) message.getAS2Info()).setSubject(subject);
                    //refresh this in the database if it is requested
                    if (this.dbDriverManager != null) {
                        MessageAccessDB access = new MessageAccessDB(this.dbDriverManager);
                        access.updateSubject((AS2MessageInfo) message.getAS2Info());
                    }
                }
            } else {
                filePost.addHeader("subject", message.getAS2Info().getSubject());
            }
            filePost.addHeader("from", sender.getEmail());
            filePost.addHeader("connection", "close, TE");
            //the data header must be always in english locale else there would be special
            //french characters (e.g. 13 déc. 2011 16:28:56 CET) which is not allowed after 
            //RFC 4130           
            DateFormat format = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz", Locale.US);
            filePost.addHeader("date", format.format(new Date()));
            String contentType = null;
            if (message.getAS2Info().getEncryptionType() != AS2Message.ENCRYPTION_NONE) {
                contentType = "application/pkcs7-mime; smime-type=enveloped-data; name=smime.p7m";
            } else {
                contentType = message.getContentType();
            }
            filePost.addHeader("content-type", contentType);
            //MDN header, this is always the way for async MDNs
            if (message.isMDN()) {
                if (this.logger != null) {
                    this.logger.log(Level.INFO,
                            this.rb.getResourceString("sending.mdn.async",
                                    new Object[]{
                                        receiptURL
                                    }), message.getAS2Info());
                    //its a TLS connection and the system should trust all server certificates
                    if (receiptURL.getProtocol().equalsIgnoreCase("https")) {
                        if (connectionParameter.getTrustAllRemoteServerCertificates()) {
                            this.logger.log(Level.INFO,
                                    this.rb.getResourceString("trust.all.server.certificates"),
                                    message.getAS2Info());
                        }
                        if (connectionParameter.getStrictHostCheck()) {
                            this.logger.log(Level.INFO,
                                    this.rb.getResourceString("strict.hostname.check"),
                                    message.getAS2Info());
                        }
                    }
                }
                filePost.addHeader("server", message.getAS2Info().getUserAgent());
            } else {
                AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
                //outbound AS2/CEM message
                if (messageInfo.requestsSyncMDN()) {
                    if (this.logger != null) {
                        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
                            this.logger.log(Level.INFO,
                                    this.rb.getResourceString("sending.cem.sync",
                                            new Object[]{
                                                receiver.getURL()
                                            }), messageInfo);
                        } else if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_AS2) {
                            this.logger.log(Level.INFO,
                                    this.rb.getResourceString("sending.msg.sync",
                                            new Object[]{
                                                receiver.getURL()
                                            }), messageInfo);
                        }
                        //its a TLS connection and the system should trust all server certificates
                        if (receiptURL.getProtocol().equalsIgnoreCase("https")) {
                            if (connectionParameter.getTrustAllRemoteServerCertificates()) {
                                this.logger.log(Level.INFO,
                                        this.rb.getResourceString("trust.all.server.certificates"),
                                        messageInfo);
                            }
                            if (connectionParameter.getStrictHostCheck()) {
                                this.logger.log(Level.INFO,
                                        this.rb.getResourceString("strict.hostname.check"),
                                        messageInfo);
                            }
                        }
                    }
                } else {
                    //Message with ASYNC MDN request
                    if (this.logger != null) {
                        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
                            this.logger.log(Level.INFO,
                                    this.rb.getResourceString("sending.cem.async",
                                            new Object[]{
                                                receiver.getURL(),
                                                sender.getMdnURL()
                                            }), messageInfo);
                        } else if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_AS2) {
                            this.logger.log(Level.INFO,
                                    this.rb.getResourceString("sending.msg.async",
                                            new Object[]{
                                                receiver.getURL(),
                                                sender.getMdnURL()
                                            }), messageInfo);
                        }
                        //its a TLS connection and the system should trust all server certificates
                        if (receiptURL.getProtocol().equalsIgnoreCase("https")) {
                            if (connectionParameter.getTrustAllRemoteServerCertificates()) {
                                this.logger.log(Level.INFO,
                                        this.rb.getResourceString("trust.all.server.certificates"),
                                        messageInfo);
                            }
                            if (connectionParameter.getStrictHostCheck()) {
                                this.logger.log(Level.INFO,
                                        this.rb.getResourceString("strict.hostname.check"),
                                        messageInfo);
                            }
                        }
                    }
                    //The following header indicates that this requests an asnc MDN.
                    //When the header "receipt-delivery-option" is present,
                    //the header "disposition-notification-to" serves as a request
                    //for an asynchronous MDN.
                    //The header "receipt-delivery-option" must always be accompanied by
                    //the header "disposition-notification-to".
                    //When the header "receipt-delivery-option" is not present and the header
                    //"disposition-notification-to" is present, the header "disposition-notification-to"
                    //serves as a request for a synchronous MDN.
                    filePost.addHeader("receipt-delivery-option", sender.getMdnURL());
                }
                filePost.addHeader("disposition-notification-to", sender.getMdnURL());
                //request a signed MDN if this is set up in the partner configuration
                if (receiver.isSignedMDN()) {
                    filePost.addHeader("disposition-notification-options",
                            messageInfo.getDispositionNotificationOptions().getHeaderValue());
                }
                if (messageInfo.getSignType() != AS2Message.SIGNATURE_NONE) {
                    filePost.addHeader("content-disposition", "attachment; filename=\"smime.p7m\"");
                } else if (messageInfo.getSignType() == AS2Message.SIGNATURE_NONE && message.getAS2Info().getSignType() == AS2Message.ENCRYPTION_NONE) {
                    //RFC 822 mail headers must contain only US-ASCII characters. Headers that contain non US-ASCII 
                    //characters must be encoded so that they contain only US-ASCII characters. Basically, 
                    //this process involves using either BASE64 or QP to encode certain characters. 
                    //RFC 2047 describes this in detail. 
                    //test if an encoding is required
                    try {
                        String filename = message.getPayload(0).getOriginalFilename();
                        boolean filenameEncodingRequired = !MimeUtility.encodeText(filename).equals(filename);
                        if (!filenameEncodingRequired) {
                            filePost.addHeader("content-disposition", "attachment; filename=\"" + filename + "\"");
                        } else {
                            filePost.addHeader("content-disposition", "attachment; filename=\""
                                    + MimeUtility.encodeText(filename,
                                            StandardCharsets.UTF_8.displayName(), "B")
                                    + "\"");
                        }
                    } catch (UnsupportedEncodingException willnothappen) {
                        //NOP
                    }
                }
            }
            int port = receiptURL.getPort();
            if (port == -1) {
                port = receiptURL.getDefaultPort();
            }
            filePost.addHeader("host", receiptURL.getHost() + ":" + port);
            filePost.addHeader("user-agent", connectionParameter.getUserAgent());
            HttpResponse httpResponse = null;
            byte[] transferData = message.getRawData();
            //using a ByteArrayEntity because this is repeatable
            ByteArrayEntity postEntity = new ByteArrayEntity(transferData);
            postEntity.setContentType(contentType);
            filePost.setEntity(postEntity);
            //setup oauth2 header
            if(message.isMDN()){
                this.setOAuth2Header( filePost, receiver.usesOAuth2MDN(), receiver.getOAuth2MDN() );
            }else{
                this.setOAuth2Header( filePost, receiver.usesOAuth2Message(), receiver.getOAuth2Message());
            } 
            this.updateUploadHTTPHeaderWithUserDefinedHeaders(filePost, receiver);
            if (httpClientContext != null) {
                //use preemtive basic authentication
                httpResponse = httpClient.execute(targetHost, filePost, httpClientContext);
            } else {
                httpResponse = httpClient.execute(targetHost, filePost);
            }
            if (httpResponse != null) {
                this.responseData = this.readEntityData(httpResponse);
                this.responseStatusLine = httpResponse.getStatusLine();
                statusCode = this.responseStatusLine.getStatusCode();
                this.responseHeader = httpResponse.getAllHeaders();
            }
            for (Header singleHeader : filePost.getAllHeaders()) {
                if (singleHeader.getValue() != null) {
                    this.requestHeader.setProperty(singleHeader.getName(), singleHeader.getValue());
                }
            }
            //accept all 2xx answers
            //SC_ACCEPTED Status code (202) indicating that a request was accepted for processing, but was not completed.
            //SC_CREATED  Status code (201) indicating the request succeeded and created a new resource on the server.
            //SC_NO_CONTENT Status code (204) indicating that the request succeeded but that there was no new information to return.
            //SC_NON_AUTHORITATIVE_INFORMATION Status code (203) indicating that the meta information presented by the client did not originate from the server.
            //SC_OK Status code (200) indicating the request succeeded normally.
            //SC_RESET_CONTENT Status code (205) indicating that the agent SHOULD reset the document view which caused the request to be sent.
            //SC_PARTIAL_CONTENT Status code (206) indicating that the server has fulfilled the partial GET request for the resource.
            if (statusCode != HttpServletResponse.SC_OK
                    && statusCode != HttpServletResponse.SC_ACCEPTED
                    && statusCode != HttpServletResponse.SC_CREATED
                    && statusCode != HttpServletResponse.SC_NO_CONTENT
                    && statusCode != HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION
                    && statusCode != HttpServletResponse.SC_RESET_CONTENT
                    && statusCode != HttpServletResponse.SC_PARTIAL_CONTENT) {
                if (this.logger != null) {
                    this.logger.log(Level.SEVERE,
                            this.rb.getResourceString("error.httpupload",
                                    new Object[]{
                                        String.valueOf(statusCode) + " "
                                        + URLDecoder.decode(this.responseStatusLine == null ? ""
                                                : this.responseStatusLine.getReasonPhrase(), "UTF-8")
                                    }), message.getAS2Info());
                }
            }
        } catch (Exception ex) {
            if (this.logger != null) {
                StringBuilder errorMessage = new StringBuilder();
                errorMessage.append("MessageHTTPUploader.performUpload: [");
                errorMessage.append(ex.getClass().getSimpleName());
                errorMessage.append("]");
                if (ex.getMessage() != null) {
                    errorMessage.append(": ").append(ex.getMessage());
                }
                //missing certificate on one of the sides
                if (ex instanceof SSLPeerUnverifiedException) {
                    if (ex.getCause() != null) {
                        Throwable causeEx = ex.getCause();
                        errorMessage.append("[");
                        errorMessage.append(causeEx.getClass().getSimpleName());
                        if (causeEx.getMessage() != null) {
                            errorMessage.append("/").append(causeEx.getMessage());
                        }
                        errorMessage.append("]");
                    }
                    new SystemEventManagerImplAS2().newEventConnectionProblem(receiver, message.getAS2Info(),
                            errorMessage.toString(), this.rb.getResourceString("hint.SSLPeerUnverifiedException"));
                    errorMessage.append("\n").append(this.rb.getResourceString("hint.SSLPeerUnverifiedException"));
                }
                //Remote server does not answer or is not reachable, java.net exception. Same reason for both expections
                //no idea why one of them is thrown sometimes instead of the other. 
                //Perhaps it depends on the java version or the apache client version.
                if (ex instanceof ConnectTimeoutException || ex instanceof ConnectException) {
                    if (ex.getCause() != null) {
                        Throwable causeEx = ex.getCause();
                        errorMessage.append("[");
                        errorMessage.append(causeEx.getClass().getSimpleName());
                        if (causeEx.getMessage() != null) {
                            errorMessage.append("/").append(causeEx.getMessage());
                        }
                        errorMessage.append("]");
                    }
                    new SystemEventManagerImplAS2().newEventConnectionProblem(receiver, message.getAS2Info(),
                            errorMessage.toString(), this.rb.getResourceString("hint.ConnectTimeoutException"));
                    errorMessage.append("\n").append(this.rb.getResourceString("hint.ConnectTimeoutException"));
                }

                //any other generic SSL problem - no idea why both may be thrown
                if (ex instanceof SSLException || ex instanceof ClientProtocolException) {
                    if (ex.getCause() != null) {
                        Throwable causeEx = ex.getCause();
                        errorMessage.append("[");
                        errorMessage.append(causeEx.getClass().getSimpleName());
                        if (causeEx.getMessage() != null) {
                            errorMessage.append("/").append(causeEx.getMessage());
                        }
                        errorMessage.append("]");
                    }
                    new SystemEventManagerImplAS2().newEventConnectionProblem(receiver, message.getAS2Info(),
                            errorMessage.toString(), this.rb.getResourceString("hint.SSLException"));
                    errorMessage.append("\n").append(this.rb.getResourceString("hint.SSLException"));
                }
                this.logger.log(Level.SEVERE, errorMessage.toString(), message.getAS2Info());
            }
        } finally {
            if (httpClient != null) {
                //shutdown the HTTPClient to release the resources
                try {
                    httpClient.close();
                } catch (IOException e) {
                    //nop
                }
            }
        }
        return (statusCode);
    }

    private SSLConnectionSocketFactory generateSSLFactory(HttpConnectionParameter connectionParameter,
            AS2Info as2Info) throws Exception {
        String trustStoreFilename = null;
        char[] trustStorePass = null;
        char[] certStorePass = null;
        String certStoreFilename = null;
        
        //TLS key stores not set so far: take the preferences data from the server preferences
        if (this.certStore == null) {
            trustStoreFilename = Paths.get(this.preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND)).toAbsolutePath().toString();
            certStoreFilename = Paths.get(this.preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND)).toAbsolutePath().toString();
            trustStorePass = this.preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND_PASS).toCharArray();
            certStorePass = this.preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND_PASS).toCharArray();            
        }else{
            trustStoreFilename = Paths.get(this.trustStore.getOriginalKeystoreFilename()).toAbsolutePath().toString();
            certStoreFilename = Paths.get(this.certStore.getOriginalKeystoreFilename()).toAbsolutePath().toString();
            trustStorePass = this.trustStore.getKeystorePass();
            certStorePass = this.certStore.getKeystorePass();         
        }
        SSLContext sslcontext = null;
        if (connectionParameter.getTrustAllRemoteServerCertificates()) {
            SSLContextBuilder builder = SSLContexts.custom()
                    .loadTrustMaterial(new File(trustStoreFilename), trustStorePass,
                            new TrustSelfSignedStrategy());
            sslcontext = builder.build();
        } else {
            sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(new File(trustStoreFilename), trustStorePass,
                            new TrustSelfSignedStrategy())
                    .loadKeyMaterial(new File(certStoreFilename), certStorePass,
                            certStorePass)
                    .build();
        }
        // Allowed SSL/TLS protocols as client
        String[] allowedProtocols
                = new String[]{
                    "SSLv3",
                    "TLSv1",
                    "TLSv1.1",
                    "TLSv1.2",
                    "TLSv1.3"};
        SSLConnectionSocketFactory sslConnectionFactory = null;
        if (!connectionParameter.getStrictHostCheck()) {
            sslConnectionFactory = new SSLConnectionSocketFactory(sslcontext,
                    allowedProtocols,
                    null,
                    //this is the AllowAllHostnameVerifier, another verifier is the StrictHostnameVerifier
                    new NoopHostnameVerifier()) {
                @Override
                /**
                 * This is required to support SNI (Server Name Indication) -
                 * this is more a hack as it makes use of Commons BeanUtils to
                 * invoke Oracle private method via reflection
                 */
                public Socket connectSocket(
                        int connectTimeout,
                        Socket socket,
                        HttpHost host,
                        InetSocketAddress remoteAddress,
                        InetSocketAddress localAddress,
                        HttpContext context) throws IOException, ConnectTimeoutException {
                    if (socket instanceof SSLSocket) {
                        try {
                            PropertyUtils.setProperty(socket, "host", host.getHostName());
                        } catch (NoSuchMethodException ex) {
                        } catch (IllegalAccessException ex) {
                        } catch (InvocationTargetException ex) {
                        }
                    }
                    return super.connectSocket(connectTimeout, socket, host, remoteAddress,
                            localAddress, context);
                }

            };
        } else {
            sslConnectionFactory = new SSLConnectionSocketFactory(sslcontext,
                    allowedProtocols,
                    null,
                    //this is the StrictHostnameVerifier with self signed exception
                    new DefaultHostnameVerifierTrustedOnly(as2Info)) {
                @Override
                /**
                 * This is required to support SNI (Server Name Indication) -
                 * this is more a hack as it makes use of Commons BeanUtils to
                 * invoke Oracle private method via reflection
                 */
                public Socket connectSocket(
                        int connectTimeout,
                        Socket socket,
                        HttpHost host,
                        InetSocketAddress remoteAddress,
                        InetSocketAddress localAddress,
                        HttpContext context) throws IOException, ConnectTimeoutException {
                    if (socket instanceof SSLSocket) {
                        try {
                            PropertyUtils.setProperty(socket, "host", host.getHostName());
                        } catch (NoSuchMethodException ex) {
                        } catch (IllegalAccessException ex) {
                        } catch (InvocationTargetException ex) {
                        }
                    }
                    return super.connectSocket(connectTimeout, socket, host, remoteAddress,
                            localAddress, context);
                }

            };
        }
        return (sslConnectionFactory);
    }

    /**
     * Replaces the subject set for the partner. The encoding must be
     * ISO-8859-1, only printable characters, CR, LF and TAB are replaced
     */
    private String replaceSubject(String definedSubject, String originalFilename) {
        String subjectStr = this.replace(definedSubject, "${filename}", originalFilename);
        StringBuilder subjectBuilder = new StringBuilder();
        for (int i = 0; i < subjectStr.length(); i++) {
            char testChar = subjectStr.charAt(i);
            if (testChar == '\n') {
                subjectBuilder.append("\\").append("n");
            } else if (testChar == '\r') {
                subjectBuilder.append("\\").append("r");
            } else if (testChar == '\t') {
                subjectBuilder.append("\\").append("t");
            } else {
                subjectBuilder.append(testChar);
            }
        }
        return (subjectBuilder.toString());
    }

    /**Generates a authorization header for OAuth2 if this is required*/
    private void setOAuth2Header(HttpPost post, boolean useOAuth2, OAuth2Config config){
        if( useOAuth2 && config != null){
            post.setHeader("Authorization", "Bearer " + config.getAccessTokenStr());
        }
    }
    
    /**
     * Updates the passed post HTTP headers with the headers defined for the
     * receiver
     */
    private void updateUploadHTTPHeaderWithUserDefinedHeaders(HttpPost post, Partner receiver) {
        List<String> usedHeaderKeys = new ArrayList<String>();
        for (Header singleHeader : post.getAllHeaders()) {
            PartnerHttpHeader headerReplacement = receiver.getHttpHeader(singleHeader.getName());
            if (headerReplacement != null) {
                //a value to replace is set
                if (headerReplacement.getValue() != null && headerReplacement.getValue().length() > 0) {
                    post.setHeader(singleHeader.getName(), headerReplacement.getValue());
                } else {
                    //no value to replace is set: delete the header
                    post.removeHeader(singleHeader);
                }
                usedHeaderKeys.add(singleHeader.getName());
            }
        }
        //add additional user defined headers
        List<PartnerHttpHeader> additionalHeaders = receiver.getAllNonListedHttpHeader(usedHeaderKeys);
        for (PartnerHttpHeader additionalHeader : additionalHeaders) {
            //add the header if a value is set
            if (additionalHeader.getValue() != null && additionalHeader.getValue().length() > 0) {
                post.setHeader(additionalHeader.getKey(), additionalHeader.getValue());
            }
        }
    }

    /**
     * Replaces the string tag by the string replacement in the sourceString
     *
     * @param source Source string
     * @param tag	String that will be replaced
     * @param replacement String that will replace the tag
     * @return String that contains the replaced values
     */
    private String replace(String source, String tag, String replacement) {
        if (source == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        while (true) {
            int index = source.indexOf(tag);
            if (index == -1) {
                buffer.append(source);
                return (buffer.toString());
            }
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + tag.length());
        }
    }

    /**
     * Returns the version of this class
     */
    public static String getVersion() {
        String revision = "$Revision: 202 $";
        return (revision.substring(revision.indexOf(":") + 1,
                revision.lastIndexOf("$")).trim());
    }

    /**
     * Returns the response data as byte array
     */
    public byte[] getResponseData() {
        return (this.responseData);
    }

    /**
     * Reads the data of a HTTP response entity
     */
    public byte[] readEntityData(HttpResponse httpResponse) throws Exception {
        if (httpResponse == null) {
            return (null);
        }
        if (httpResponse.getEntity() == null) {
            return (null);
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        httpResponse.getEntity().writeTo(outStream);
        outStream.flush();
        outStream.close();
        return (outStream.toByteArray());
    }

    /**
     * Returns the array of response headers after the upload process has been
     * performed
     *
     * @return the responseHeader
     */
    public Header[] getResponseHeader() {
        if (this.responseHeader == null) {
            return (new Header[0]);
        }
        return (this.responseHeader);
    }

    /**
     * @return the requestHeader, this is is only valid if the send process has
     * been already performed
     */
    public Properties getRequestHeader() {
        return requestHeader;
    }

    static class PreemptiveAuth implements HttpRequestInterceptor {

        @Override
        public void process(
                final HttpRequest request,
                final HttpContext context) throws HttpException, IOException {

            AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

            // If no auth scheme avaialble yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute(
                        "preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                        ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(
                        ExecutionContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(
                            new AuthScope(
                                    targetHost.getHostName(),
                                    targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.setAuthScheme(authScheme);
                    authState.setCredentials(creds);
                }
            }

        }
    }

    private class DefaultHostnameVerifierTrustedOnly implements HostnameVerifier {

        private final AS2Info as2Info;
        
        public DefaultHostnameVerifierTrustedOnly(AS2Info as2Info){
            this.as2Info = as2Info;
        }
        
        @Override
        public boolean verify(String hostname, SSLSession session) {
            try {
                Certificate[] certificates = session.getPeerCertificates();
                if (certificates != null && certificates.length > 0) {
                    X509Certificate certificate = (X509Certificate) certificates[0];
                    if (this.isSelfSigned(certificate)) {
                        if( logger != null ){
                            logger.log(Level.INFO,
                                        rb.getResourceString("strict.hostname.check.skipped.selfsigned"),
                                        this.as2Info);
                        }
                        return (true);
                    }
                }
            } catch (Exception e) {
            }
            return (new DefaultHostnameVerifier().verify(hostname, session));
        }

        /**
         * Checks if the passed certificate is self signed. A certificate is
         * self-signed if the subject and issuer match
         */
        private boolean isSelfSigned(X509Certificate certificate) {
            try {
                X500Principal issuer = certificate.getIssuerX500Principal();
                X500Principal subject = certificate.getSubjectX500Principal();
                certificate.verify(certificate.getPublicKey());
                return issuer.equals(subject);
            } catch (Exception e) {
                return false;
            }
        }

    }

}
