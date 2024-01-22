//$Header: /as2/de/mendelson/comm/as2/server/AS2ServerProcessing.java 224   15/11/22 9:23 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.comm.as2.AS2Exception;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.util.httpconfig.server.HTTPServerConfigInfo;
import de.mendelson.util.clientserver.about.ServerInfoRequest;
import de.mendelson.comm.as2.api.message.CommandRequest;
import de.mendelson.comm.as2.api.server.ServersideAPICommandProcessing;
import de.mendelson.comm.as2.cem.CEMAccessDB;
import de.mendelson.comm.as2.cem.CEMEntry;
import de.mendelson.comm.as2.cem.CEMInitiator;
import de.mendelson.comm.as2.cem.CEMReceiptController;
import de.mendelson.comm.as2.cem.clientserver.CEMCancelRequest;
import de.mendelson.comm.as2.cem.clientserver.CEMDeleteRequest;
import de.mendelson.comm.as2.cem.clientserver.CEMListRequest;
import de.mendelson.comm.as2.cem.clientserver.CEMListResponse;
import de.mendelson.comm.as2.cem.clientserver.CEMSendRequest;
import de.mendelson.comm.as2.cem.clientserver.CEMSendResponse;
import de.mendelson.comm.as2.client.manualsend.ManualSendRequest;
import de.mendelson.comm.as2.client.manualsend.ManualSendResponse;
import de.mendelson.comm.as2.clientserver.message.ConfigurationCheckRequest;
import de.mendelson.comm.as2.clientserver.message.ConfigurationCheckResponse;
import de.mendelson.comm.as2.clientserver.message.DeleteMessageRequest;
import de.mendelson.comm.as2.clientserver.message.ExternalLogRequest;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageRequest;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageResponse;
import de.mendelson.util.modulelock.message.ModuleLockRequest;
import de.mendelson.util.modulelock.message.ModuleLockResponse;
import de.mendelson.comm.as2.clientserver.message.PartnerConfigurationChanged;
import de.mendelson.comm.as2.clientserver.message.PerformNotificationTestRequest;
import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.clientserver.message.RefreshTablePartnerData;
import de.mendelson.comm.as2.clientserver.message.ServerShutdown;
import de.mendelson.comm.as2.configurationcheck.ConfigurationCheckController;
import de.mendelson.comm.as2.configurationcheck.ConfigurationIssue;
import de.mendelson.comm.as2.database.DBClientInformation;
import de.mendelson.comm.as2.database.DBDriverManagerHSQL;
import de.mendelson.comm.as2.database.DBServerInformation;
import de.mendelson.comm.as2.database.migration.clientserver.HSQLDBMigrationVersionMismatchException;
import de.mendelson.comm.as2.database.migration.clientserver.HSQLDBPartnerRequest;
import de.mendelson.comm.as2.database.migration.clientserver.HSQLDBPartnerResponse;
import de.mendelson.comm.as2.ha.HAAccessDB;
import de.mendelson.comm.as2.ha.ServerInstanceHA;
import de.mendelson.comm.as2.ha.clientserver.ServerInstanceHAListRequest;
import de.mendelson.comm.as2.ha.clientserver.ServerInstanceHAListResponse;
import de.mendelson.comm.as2.log.LogAccessDB;
import de.mendelson.comm.as2.message.AS2Info;
import de.mendelson.comm.as2.message.AS2MDNCreation;
import de.mendelson.comm.as2.message.AS2MDNInfo;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2MessageParser;
import de.mendelson.comm.as2.message.DispositionNotificationOptions;
import de.mendelson.comm.as2.message.MDNAccessDB;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.message.clientserver.MessageDetailRequest;
import de.mendelson.comm.as2.message.clientserver.MessageDetailResponse;
import de.mendelson.comm.as2.message.clientserver.MessageLogRequest;
import de.mendelson.comm.as2.message.clientserver.MessageLogResponse;
import de.mendelson.comm.as2.message.clientserver.MessageOverviewRequest;
import de.mendelson.comm.as2.message.clientserver.MessageOverviewResponse;
import de.mendelson.comm.as2.message.clientserver.MessagePayloadRequest;
import de.mendelson.comm.as2.message.clientserver.MessagePayloadResponse;
import de.mendelson.comm.as2.message.clientserver.MessageRequestLastMessage;
import de.mendelson.comm.as2.message.clientserver.MessageResponseLastMessage;
import de.mendelson.comm.as2.message.postprocessingevent.ProcessingEvent;
import de.mendelson.comm.as2.message.store.MessageStoreHandler;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.util.modulelock.LockClientInformation;
import de.mendelson.util.modulelock.ModuleLock;
import de.mendelson.util.systemevents.notification.Notification;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetRequest;
import de.mendelson.util.systemevents.notification.clientserver.NotificationSetMessage;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.partner.PartnerSystem;
import de.mendelson.comm.as2.partner.PartnerSystemAccessDB;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.comm.as2.partner.clientserver.PartnerModificationRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerSystemRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerSystemResponse;
import de.mendelson.comm.as2.partner.gui.ResourceBundlePartnerConfig;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.preferences.ResourceBundlePreferences;
import de.mendelson.comm.as2.send.DirPollManager;
import de.mendelson.comm.as2.sendorder.SendOrder;
import de.mendelson.comm.as2.sendorder.SendOrderSender;
import de.mendelson.comm.as2.statistic.QuotaAccessDB;
import de.mendelson.comm.as2.statistic.ServerInteroperabilityAccessDB;
import de.mendelson.comm.as2.statistic.ServerInteroperabilityContainer;
import de.mendelson.comm.as2.statistic.StatisticAccessDB;
import de.mendelson.comm.as2.statistic.StatisticDetailEntry;
import de.mendelson.comm.as2.statistic.StatisticExport;
import de.mendelson.comm.as2.statistic.StatisticExportRequest;
import de.mendelson.comm.as2.statistic.StatisticExportResponse;
import de.mendelson.comm.as2.statistic.StatisticOverviewEntry;
import de.mendelson.comm.as2.statistic.clientserver.QuotaResetRequest;
import de.mendelson.comm.as2.statistic.clientserver.ServerInteroperabilityRequest;
import de.mendelson.comm.as2.statistic.clientserver.ServerInteroperabilityResponse;
import de.mendelson.comm.as2.statistic.clientserver.StatisticDetailRequest;
import de.mendelson.comm.as2.statistic.clientserver.StatisticDetailResponse;
import de.mendelson.comm.as2.statistic.clientserver.StatisticOverviewRequest;
import de.mendelson.comm.as2.statistic.clientserver.StatisticOverviewResponse;
import de.mendelson.comm.as2.timing.MessageDeleteController;
import de.mendelson.comm.as2.timing.ResourceBundleMessageDeleteController;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.clientserver.ClientServerProcessing;
import de.mendelson.util.clientserver.ClientServerSessionHandler;
import de.mendelson.util.clientserver.about.ServerInfoResponse;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadRequestFile;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadRequestFileLimited;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadResponseFile;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadResponseFileLimited;
import de.mendelson.util.clientserver.clients.datatransfer.UploadRequestChunk;
import de.mendelson.util.clientserver.clients.datatransfer.UploadRequestFile;
import de.mendelson.util.clientserver.clients.datatransfer.UploadResponseChunk;
import de.mendelson.util.clientserver.clients.datatransfer.UploadResponseFile;
import de.mendelson.util.clientserver.clients.fileoperation.FileDeleteRequest;
import de.mendelson.util.clientserver.clients.fileoperation.FileDeleteResponse;
import de.mendelson.util.clientserver.clients.fileoperation.FileOperationProcessing;
import de.mendelson.util.clientserver.clients.fileoperation.FileRenameRequest;
import de.mendelson.util.clientserver.clients.fileoperation.FileRenameResponse;
import de.mendelson.util.clientserver.clients.filesystemview.FileSystemViewProcessorServer;
import de.mendelson.util.clientserver.clients.filesystemview.FileSystemViewRequest;
import de.mendelson.util.clientserver.clients.preferences.ConfigurationChangedOnServerNotification;
import de.mendelson.util.clientserver.clients.preferences.ConfigurationChangedOnServerPreferences;
import de.mendelson.util.clientserver.clients.preferences.PreferencesRequest;
import de.mendelson.util.clientserver.clients.preferences.PreferencesResponse;
import de.mendelson.util.clientserver.connectiontest.ConnectionTest;
import de.mendelson.util.clientserver.connectiontest.ConnectionTestProxy;
import de.mendelson.util.clientserver.connectiontest.ConnectionTestResult;
import de.mendelson.util.clientserver.connectiontest.ResourceBundleConnectionTest;
import de.mendelson.util.clientserver.connectiontest.clientserver.ConnectionTestRequest;
import de.mendelson.util.clientserver.connectiontest.clientserver.ConnectionTestResponse;
import de.mendelson.util.clientserver.log.search.Logline;
import de.mendelson.util.clientserver.log.search.ServerSideLogfileSearch;
import de.mendelson.util.clientserver.log.search.ServerSideLogfileSearchImplAS2;
import de.mendelson.util.clientserver.log.search.ServerlogfileSearchRequest;
import de.mendelson.util.clientserver.log.search.ServerlogfileSearchResponse;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.ClientToServerLogRequest;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.httpconfig.clientserver.DisplayHTTPServerConfigurationRequest;
import de.mendelson.util.httpconfig.server.HTTPServerConfigInfoProcessor;
import de.mendelson.util.log.LoggingHandlerLogEntryArray;
import de.mendelson.util.oauth2.OAuth2Util;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.ResourceBundleCertificateManager;
import de.mendelson.util.security.cert.clientserver.RefreshKeystoreCertificates;
import de.mendelson.util.security.cert.clientserver.UploadRequestKeystore;
import de.mendelson.util.security.cert.clientserver.UploadResponseKeystore;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import de.mendelson.util.systemevents.clientserver.SystemEventSearchRequest;
import de.mendelson.util.systemevents.clientserver.SystemEventSearchResponse;
import de.mendelson.util.systemevents.notification.NotificationAccessDB;
import de.mendelson.util.systemevents.notification.NotificationAccessDBImplAS2;
import de.mendelson.util.systemevents.notification.NotificationData;
import de.mendelson.util.systemevents.notification.NotificationDataImplAS2;
import de.mendelson.util.systemevents.notification.NotificationImplAS2;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetResponse;
import de.mendelson.util.systemevents.search.ServerSideEventSearch;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.apache.mina.core.session.IoSession;
import org.jfree.data.time.SimpleTimePeriod;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * User defined processing to extend the client-server framework
 *
 * @author S.Heller
 * @version $Revision: 224 $
 * @since build 68
 */
public class AS2ServerProcessing implements ClientServerProcessing {

    private DirPollManager pollManager;
    private final CertificateManager certificateManagerEncSign;
    private final CertificateManager certificateManagerSSL;
    private final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    /**
     * ResourceBundle to localize messages of the server
     */
    private MecResourceBundle rb = null;
    private MecResourceBundle rbPartnerConfig = null;
    private MecResourceBundle rbPreferences = null;
    private MecResourceBundle rbCertificateManager = null;
    private MecResourceBundle rbConnectionTest = null;
    private MecResourceBundle rbMessageDelete = null;

    private final ClientServer clientserver;
    private final Map<String, String> uploadMap = new ConcurrentHashMap<String, String>();
    private final AtomicInteger uploadCounter = new AtomicInteger(0);
    private final FileSystemViewProcessorServer filesystemview;
    /**
     * Start time of this class, this is similar to the server startup time
     */
    private final long startupTime = System.currentTimeMillis();
    private final MessageStoreHandler messageStoreHandler;
    private final MessageAccessDB messageAccess;
    private final LogAccessDB logAccess;
    private final MDNAccessDB mdnAccess;
    private final PartnerSystemAccessDB partnerSystemAccess;
    private final PartnerAccessDB partnerAccess;
    private final ConfigurationCheckController configurationCheckController;
    private final PreferencesAS2 preferences = new PreferencesAS2();
    private final HTTPServerConfigInfo httpServerConfigInfo;
    private final ServerSideEventSearch eventSearch = new ServerSideEventSearch();
    private final ServerSideLogfileSearch logfileSearch = new ServerSideLogfileSearchImplAS2();
    private final FileOperationProcessing fileOperationProcessing = new FileOperationProcessing();
    private final DBServerInformation dbServerInformation;
    private final DBClientInformation dbClientInformation;
    private final IDBDriverManager dbDriverManager;
    private final HAAccessDB haAccess;
    private long serverProcessId = 0L;

    public AS2ServerProcessing(ClientServer clientserver, DirPollManager pollManager,
            CertificateManager certificateManagerEncSign,
            CertificateManager certificateManagerSSL,
            IDBDriverManager dbDriverManager,
            ConfigurationCheckController configurationCheckController,
            HTTPServerConfigInfo httpServerConfigInfo,
            DBServerInformation dbServerInformation,
            DBClientInformation dbClientInformation) {
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2ServerProcessing.class.getName());
            this.rbPartnerConfig = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePartnerConfig.class.getName());
            this.rbPreferences = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
            this.rbCertificateManager = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificateManager.class.getName());
            this.rbConnectionTest = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleConnectionTest.class.getName());
            this.rbMessageDelete = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMessageDeleteController.class.getName());

        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.serverProcessId = this.lookupServerProcessId();
        this.dbServerInformation = dbServerInformation;
        this.dbClientInformation = dbClientInformation;
        this.httpServerConfigInfo = httpServerConfigInfo;
        this.filesystemview = new FileSystemViewProcessorServer(this.logger);
        this.clientserver = clientserver;
        this.dbDriverManager = dbDriverManager;
        this.pollManager = pollManager;
        this.certificateManagerEncSign = certificateManagerEncSign;
        this.certificateManagerSSL = certificateManagerSSL;
        this.configurationCheckController = configurationCheckController;
        this.messageStoreHandler = new MessageStoreHandler(this.dbDriverManager);
        this.messageAccess = new MessageAccessDB(this.dbDriverManager);
        this.logAccess = new LogAccessDB(this.dbDriverManager);
        this.mdnAccess = new MDNAccessDB(this.dbDriverManager);
        this.partnerAccess = new PartnerAccessDB(this.dbDriverManager);
        this.partnerSystemAccess = new PartnerSystemAccessDB(this.dbDriverManager);
        this.haAccess = new HAAccessDB();
    }

    /**
     * Returns the process id of the server process in the system
     */
    private long lookupServerProcessId() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long pid = Long.valueOf(runtimeBean.getName().split("@")[0]);
        return (pid);
    }

    /**
     * Sets a new upload counter value, thread safe
     */
    private String incUploadRequest() {
        return (String.valueOf(this.uploadCounter.getAndAdd(1)));
    }

    @Override
    public boolean process(IoSession session, ClientServerMessage message) {
        //process signals
        try {
            if (message instanceof PartnerConfigurationChanged) {
                this.pollManager.partnerConfigurationChanged();
                this.clientserver.broadcastToClients(new RefreshTablePartnerData());
                return (true);
            } else if (message instanceof RefreshKeystoreCertificates) {
                //a certificate manager signals that there are some changes made, reload all internal keystores
                this.certificateManagerEncSign.rereadKeystoreCertificates();
                this.certificateManagerSSL.rereadKeystoreCertificates();
                return (true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.warning(this.rb.getResourceString("unable.to.process", message.toString()));
        }
        if (this.preferences.getBoolean(PreferencesAS2.COMMUNITY_EDITION)
                && !message.getPID().equals(ManagementFactory.getRuntimeMXBean().getName())) {
            return (true);
        }
        //process client requests
        try {
            if (message instanceof PreferencesRequest) {
                this.processPreferencesRequest(session, (PreferencesRequest) message);
                return (true);
            } else if (message instanceof DeleteMessageRequest) {
                this.processDeleteMessageRequest(session, (DeleteMessageRequest) message);
                return (true);
            } else if (message instanceof ManualSendRequest) {
                this.processManualSendRequest(session, (ManualSendRequest) message);
                return (true);
            } else if (message instanceof UploadRequestKeystore) {
                this.processUploadRequestKeystore(session, (UploadRequestKeystore) message);
                return (true);
            } else if (message instanceof FileRenameRequest) {
                this.processFileRenameRequest(session, (FileRenameRequest) message);
                return (true);
            } else if (message instanceof FileDeleteRequest) {
                this.processFileDeleteRequest(session, (FileDeleteRequest) message);
                return (true);
            } else if (message instanceof StatisticExportRequest) {
                this.processStatisticExportRequest(session, (StatisticExportRequest) message);
                return (true);
            } else if (message instanceof DownloadRequestFile) {
                this.processDownloadRequestFile(session, (DownloadRequestFile) message);
                return (true);
            } else if (message instanceof UploadRequestChunk) {
                this.processUploadRequestChunk(session, (UploadRequestChunk) message);
                return (true);
            } else if (message instanceof UploadRequestFile) {
                this.processUploadRequestFile(session, (UploadRequestFile) message);
                return (true);
            } else if (message instanceof FileSystemViewRequest) {
                session.write(this.filesystemview.performRequest((FileSystemViewRequest) message));
                return (true);
            } else if (message instanceof PartnerListRequest) {
                this.processPartnerListRequest(session, (PartnerListRequest) message);
                return (true);
            } else if (message instanceof PartnerModificationRequest) {
                this.processPartnerModificationMessage(session, (PartnerModificationRequest) message);
                return (true);
            } else if (message instanceof MessageOverviewRequest) {
                this.processMessageOverviewRequest(session, (MessageOverviewRequest) message);
                return (true);
            } else if (message instanceof MessageDetailRequest) {
                this.processMessageDetailRequest(session, (MessageDetailRequest) message);
                return (true);
            } else if (message instanceof MessageLogRequest) {
                this.processMessageLogRequest(session, (MessageLogRequest) message);
                return (true);
            } else if (message instanceof MessagePayloadRequest) {
                this.processMessagePayloadRequest(session, (MessagePayloadRequest) message);
                return (true);
            } else if (message instanceof NotificationGetRequest) {
                this.processNotificationGetRequest(session, (NotificationGetRequest) message);
                return (true);
            } else if (message instanceof NotificationSetMessage) {
                this.processNotificationSetRequest(session, (NotificationSetMessage) message);
                return (true);
            } else if (message instanceof PerformNotificationTestRequest) {
                this.performNotificationTest(session, (PerformNotificationTestRequest) message);
                return (true);
            } else if (message instanceof PartnerSystemRequest) {
                this.performPartnerSystemRequest(session, (PartnerSystemRequest) message);
                return (true);
            } else if (message instanceof ServerInteroperabilityRequest) {
                this.performServerInteroperabilityRequest(session, (ServerInteroperabilityRequest) message);
                return (true);
            } else if (message instanceof QuotaResetRequest) {
                this.performQuotaResetRequest(session, (QuotaResetRequest) message);
                return (true);
            } else if (message instanceof StatisticOverviewRequest) {
                this.performStatisticOverviewRequest(session, (StatisticOverviewRequest) message);
                return (true);
            } else if (message instanceof StatisticDetailRequest) {
                this.performStatisticDetailRequest(session, (StatisticDetailRequest) message);
                return (true);
            } else if (message instanceof CEMListRequest) {
                this.processCEMListRequest(session, (CEMListRequest) message);
                return (true);
            } else if (message instanceof CEMDeleteRequest) {
                this.processCEMDeleteRequest(session, (CEMDeleteRequest) message);
                return (true);
            } else if (message instanceof CEMCancelRequest) {
                this.processCEMCancelRequest(session, (CEMCancelRequest) message);
                return (true);
            } else if (message instanceof MessageRequestLastMessage) {
                this.processMessageRequestLastMessage(session, (MessageRequestLastMessage) message);
                return (true);
            } else if (message instanceof CEMSendRequest) {
                this.processCEMSendRequest(session, (CEMSendRequest) message);
                return (true);
            } else if (message instanceof ServerShutdown) {
                this.performServerShutdown(session, (ServerShutdown) message);
                return (true);
            } else if (message instanceof ModuleLockRequest) {
                this.processModuleLockRequest(session, (ModuleLockRequest) message);
                return (true);
            } else if (message instanceof ServerInfoRequest) {
                this.processServerInfoRequest(session, (ServerInfoRequest) message);
                return (true);
            } else if (message instanceof IncomingMessageRequest) {
                this.processIncomingMessageRequest(session, (IncomingMessageRequest) message);
                return (true);
            } else if (message instanceof ConnectionTestRequest) {
                this.processConnectionTestRequest(session, (ConnectionTestRequest) message);
                return (true);
            } else if (message instanceof DisplayHTTPServerConfigurationRequest) {
                this.processDisplayServerConfigurationRequest(session, (DisplayHTTPServerConfigurationRequest) message);
                return (true);
            } else if (message instanceof ServerlogfileSearchRequest) {
                this.processServerlogfileSearchRequest(session, (ServerlogfileSearchRequest) message);
                return (true);
            } else if (message instanceof HSQLDBPartnerRequest) {
                this.processHSQLDBPartnerRequest(session, (HSQLDBPartnerRequest) message);
                return (true);
            } else if (message instanceof CommandRequest) {
                this.processCommandRequest(session, (CommandRequest) message);
                return (true);
            } else if (message instanceof ConfigurationCheckRequest) {
                this.processConfigurationCheckRequest(session, (ConfigurationCheckRequest) message);
                return (true);
            } else if (message instanceof SystemEventSearchRequest) {
                this.processSystemEventSearchRequest(session, (SystemEventSearchRequest) message);
                return (true);
            } else if (message instanceof ServerInstanceHAListRequest) {
                this.processServerInstanceHAListRequest(session, (ServerInstanceHAListRequest) message);
                return (true);
            } else if (message instanceof ExternalLogRequest) {
                this.processExternalLogRequest(session, (ExternalLogRequest) message);
                return (true);
            } else if (message instanceof ClientToServerLogRequest) {
                this.processClientToServerLogRequest(session, (ClientToServerLogRequest) message);
                return (true);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            this.logger.warning(this.rb.getResourceString("unable.to.process", message.toString()));
        }
        return (false);
    }

    private void processCommandRequest(IoSession session, CommandRequest request) {
        Path requestFile = Paths.get(this.uploadMap.get(request.getUploadHash()));
        ServersideAPICommandProcessing processing = new ServersideAPICommandProcessing(this.logger,
                this.certificateManagerEncSign,
                this.certificateManagerSSL, this.pollManager, this.clientserver, this.dbDriverManager);
        String remoteAddress = session.getRemoteAddress().toString();
        String uniqueId = String.valueOf(session.getId());
        String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
        String pid = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_CLIENT_PID);
        LockClientInformation requestingClient
                = new LockClientInformation(userName, remoteAddress, uniqueId, pid);
        session.write(processing.processRequest(request, requestFile, requestingClient));
    }

    /**
     * Adds a log entry to the system - from external. This is a async request -
     * no answer is provided
     *
     *
     * @param session
     * @param request
     */
    private void processExternalLogRequest(IoSession session, ExternalLogRequest request) {
        if (request.getMessageId() != null) {
            List<AS2MessageInfo> infoList = this.messageAccess.getMessageOverview(request.getMessageId());
            if (!infoList.isEmpty()) {
                this.logger.log(request.getLevel(), request.getMessage(), infoList.get(0));
            }
        } else {
            this.logger.log(request.getLevel(), request.getMessage());
        }
    }

    /**
     * Adds a log entry to the system - from an external client. This is a async
     * request - no answer is provided
     *
     *
     * @param session
     * @param request
     */
    private void processClientToServerLogRequest(IoSession session, ClientToServerLogRequest request) {
        String remoteAddress = session.getRemoteAddress().toString();
        String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
        String originStr = "[" + userName + "@" + remoteAddress + "]";
        this.logger.log(request.getLevel(), originStr + " " + request.getMessage());
    }

    /**
     * Returns the version of the found database
     */
    private int getActualDBVersionHSQLDBMigration(Connection connection) throws Exception {
        Statement statement = null;
        int foundVersion = -1;
        ResultSet result = null;
        try {
            statement = connection.createStatement();
            statement.setEscapeProcessing(true);
            result = statement.executeQuery("SELECT MAX(actualversion) AS maxversion FROM version");
            if (result.next()) {
                //value is always in the first column
                foundVersion = result.getInt("maxversion");
            }
        } finally {
            if (result != null) {
                result.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
        return (foundVersion);
    }

    /**
     * Partner request for the database migration wizard
     */
    private void processHSQLDBPartnerRequest(IoSession session, HSQLDBPartnerRequest request) {
        HSQLDBPartnerResponse response = new HSQLDBPartnerResponse(request);
        try {
            DBDriverManagerHSQL driverManager = DBDriverManagerHSQL.instance();
            Connection configConnectionHSQLDB = null;
            Connection runtimeConnectionHSQLDB = null;
            try {
                configConnectionHSQLDB = driverManager.getConnectionFileBased(IDBDriverManager.DB_CONFIG);
                configConnectionHSQLDB.setReadOnly(true);
                runtimeConnectionHSQLDB = driverManager.getConnectionFileBased(IDBDriverManager.DB_RUNTIME);
                runtimeConnectionHSQLDB.setReadOnly(true);
                int configDBVersion = this.getActualDBVersionHSQLDBMigration(configConnectionHSQLDB);
                int runtimeDBVersion = this.getActualDBVersionHSQLDBMigration(runtimeConnectionHSQLDB);
                if (configDBVersion != AS2ServerVersion.getRequiredDBVersionConfig()
                        || runtimeDBVersion != AS2ServerVersion.getRequiredDBVersionRuntime()) {
                    HSQLDBMigrationVersionMismatchException exception = new HSQLDBMigrationVersionMismatchException();
                    exception.setRequiredVersionConfigDB(AS2ServerVersion.getRequiredDBVersionConfig());
                    exception.setRequiredVersionRuntimeDB(AS2ServerVersion.getRequiredDBVersionRuntime());
                    exception.setFoundVersionRuntimeDB(runtimeDBVersion);
                    exception.setFoundVersionConfigDB(configDBVersion);
                    throw exception;
                }
                PartnerAccessDB partnerAccess = new PartnerAccessDB(DBDriverManagerHSQL.instance());
                List<Partner> partnerList = partnerAccess.getAllPartner(PartnerAccessDB.DATA_COMPLETENESS_FULL, configConnectionHSQLDB);
                //set all DB ids to -1 as these indicies are not related to the database they should be imported in later
                for (Partner partner : partnerList) {
                    partner.setDBId(-1);
                }
                response.addPartner(partnerList);
            } finally {
                if (configConnectionHSQLDB != null) {
                    Statement shutdownStatement = null;
                    try {
                        shutdownStatement = configConnectionHSQLDB.createStatement();
                        shutdownStatement.execute("SHUTDOWN");
                    } finally {
                        if (shutdownStatement != null) {
                            shutdownStatement.close();
                        }
                    }
                    configConnectionHSQLDB.close();
                }
                if (runtimeConnectionHSQLDB != null) {
                    Statement shutdownStatement = null;
                    try {
                        shutdownStatement = runtimeConnectionHSQLDB.createStatement();
                        shutdownStatement.execute("SHUTDOWN");
                    } finally {
                        if (shutdownStatement != null) {
                            shutdownStatement.close();
                        }
                    }
                    runtimeConnectionHSQLDB.close();
                }
            }
        } catch (Throwable e) {
            response.setException(e);
        }
        //sync response
        session.write(response);
    }

    private void processServerInstanceHAListRequest(IoSession session, ServerInstanceHAListRequest request) {
        ServerInstanceHAListResponse response = new ServerInstanceHAListResponse(request);
        try {
            List<ServerInstanceHA> list = this.haAccess.getServerInstanceHA(this.dbDriverManager,
                    TimeUnit.DAYS.toSeconds(30));
            response.setList(list);
        } catch (Throwable e) {
            response.setException(e);
        }
        //sync response
        session.write(response);
    }

    private void processServerlogfileSearchRequest(IoSession session, ServerlogfileSearchRequest request) {
        ServerlogfileSearchResponse response = new ServerlogfileSearchResponse(request);
        try {
            List<Logline> resultList = this.logfileSearch.performSearch(request.getFilter());
            response.setLoglineResultList(resultList);
        } catch (Throwable e) {
            response.setException(e);
        }
        //sync response
        session.write(response);
    }

    private void processSystemEventSearchRequest(IoSession session, SystemEventSearchRequest request) {
        SystemEventSearchResponse response = new SystemEventSearchResponse(request);
        List<SystemEvent> resultList = this.eventSearch.performSearch(request.getFilter());
        response.setEventResultList(resultList);
        //sync response
        session.write(response);
    }

    /**
     * Async request from a client to display information about the server
     */
    private void processDisplayServerConfigurationRequest(IoSession session, DisplayHTTPServerConfigurationRequest request) {
        HTTPServerConfigInfoProcessor processor = new HTTPServerConfigInfoProcessor(this.httpServerConfigInfo);
        processor.processDisplayServerConfigurationRequest(session, request);
    }

    /**
     * A client requests a module lock SET, RELEASE, REFRESH or just LOCK_INFO
     *
     * @param session
     * @param moduleLockRequest
     */
    private void processModuleLockRequest(IoSession session, ModuleLockRequest moduleLockRequest) {
        ModuleLockResponse response = new ModuleLockResponse(moduleLockRequest);
        try {
            String remoteAddress = session.getRemoteAddress().toString();
            String uniqueId = String.valueOf(session.getId());
            String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
            String pid = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_CLIENT_PID);
            LockClientInformation currentClientInfo = new LockClientInformation(userName, remoteAddress, uniqueId, pid);
            if (moduleLockRequest.getType() == ModuleLockRequest.TYPE_SET) {
                LockClientInformation lockKeeper = ModuleLock.setLock(moduleLockRequest.getModuleName(),
                        currentClientInfo, this.dbDriverManager);
                response.setLockKeeper(lockKeeper);
                response.setSuccess(lockKeeper != null && lockKeeper.equals(currentClientInfo));
            } else if (moduleLockRequest.getType() == ModuleLockRequest.TYPE_RELEASE) {
                ModuleLock.releaseLock(moduleLockRequest.getModuleName(), currentClientInfo,
                        this.dbDriverManager);
            } else if (moduleLockRequest.getType() == ModuleLockRequest.TYPE_REFRESH) {
                ModuleLock.refreshLock(moduleLockRequest.getModuleName(), currentClientInfo,
                        this.dbDriverManager);
            } else if (moduleLockRequest.getType() == ModuleLockRequest.TYPE_LOCK_INFO) {
                LockClientInformation currentLockKeeper = ModuleLock.getCurrentLockKeeper(moduleLockRequest.getModuleName(),
                        this.dbDriverManager);
                response.setLockKeeper(currentLockKeeper);
            } else {
                this.logger.warning("AS2ServerProcessing.processModuleLockRequest: Undefined request type " + moduleLockRequest.getType());
            }
        } catch (Throwable e) {
            response.setException(e);
        }
        session.write(response);
    }

    /**
     * Performs a connection test
     */
    private void processConnectionTestRequest(IoSession session, ConnectionTestRequest connectionTestRequest) {
        String processOriginHost = session.getRemoteAddress().toString();
        String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
        ConnectionTestResponse response = new ConnectionTestResponse(connectionTestRequest);
        //initialize the handler
        Logger testLogger = Logger.getAnonymousLogger();
        testLogger.setUseParentHandlers(false);
        List<LoggingHandlerLogEntryArray.LogEntry> list = new ArrayList<LoggingHandlerLogEntryArray.LogEntry>();
        LoggingHandlerLogEntryArray handler = new LoggingHandlerLogEntryArray(list);
        testLogger.setLevel(Level.ALL);
        testLogger.addHandler(handler);
        int severity = SystemEvent.SEVERITY_INFO;
        try {
            ConnectionTest connectionTest = new ConnectionTest(testLogger, ConnectionTest.CONNECTION_TEST_AS2);
            if (this.preferences.getBoolean(PreferencesAS2.PROXY_USE)) {
                ConnectionTestProxy proxy = new ConnectionTestProxy();
                proxy.setAddress(this.preferences.get(PreferencesAS2.PROXY_HOST));
                proxy.setPort(this.preferences.getInt(PreferencesAS2.PROXY_PORT));
                String proxyUserName = this.preferences.get(PreferencesAS2.AUTH_PROXY_USER);
                if (proxyUserName != null && proxyUserName.trim().length() > 0) {
                    proxy.setUserName(proxyUserName);
                    proxy.setPassword(this.preferences.get(PreferencesAS2.AUTH_PROXY_PASS));
                }
                connectionTest.setProxy(proxy);
            }
            if (connectionTestRequest.getSSL()) {
                ConnectionTestResult result = connectionTest.checkConnectionTLS(
                        connectionTestRequest.getHost(),
                        connectionTestRequest.getPort(),
                        connectionTestRequest.getTimeout(),
                        this.certificateManagerSSL,
                        this.rb.getResourceString("local.station"),
                        connectionTestRequest.getPartnerName(),
                        connectionTestRequest.getPartnerRole());
                response.setResult(result);
                if (!result.isConnectionIsPossible()) {
                    severity = SystemEvent.SEVERITY_ERROR;
                }
            } else {
                ConnectionTestResult result = connectionTest.checkConnectionPlain(
                        connectionTestRequest.getHost(),
                        connectionTestRequest.getPort(),
                        connectionTestRequest.getTimeout(),
                        this.rb.getResourceString("local.station"),
                        connectionTestRequest.getPartnerName(),
                        connectionTestRequest.getPartnerRole());
                response.setResult(result);
                if (!result.isConnectionIsPossible()) {
                    severity = SystemEvent.SEVERITY_ERROR;
                }
            }
        } catch (Throwable e) {
            severity = SystemEvent.SEVERITY_ERROR;
            response.setException(e);
        }
        SystemEvent event = new SystemEvent(
                severity,
                SystemEvent.ORIGIN_USER,
                SystemEvent.TYPE_CONNECTIVITY_TEST);
        event.setUser(userName);
        event.setProcessOriginHost(processOriginHost);
        String subject = this.rbConnectionTest.getResourceString("tag", connectionTestRequest.getHost());
        if (connectionTestRequest.getPartnerName() != null) {
            subject = subject + " - " + connectionTestRequest.getPartnerName();
        }
        event.setSubject(subject);
        StringBuilder logStr = new StringBuilder();
        for (LoggingHandlerLogEntryArray.LogEntry entry : list) {
            logStr.append(entry.getMessage());
            logStr.append(System.lineSeparator());
        }
        if (response.getException() != null) {
            logStr.append(System.lineSeparator());
            logStr.append("[");
            logStr.append(response.getException().getClass().getSimpleName());
            logStr.append("]: ");
            logStr.append(response.getException().getMessage());
        }
        event.setBody(logStr.toString());
        SystemEventManagerImplAS2.newEvent(event);
        response.addLogEntries(list);
        session.write(response);
    }

    private void processConfigurationCheckRequest(IoSession session, ConfigurationCheckRequest configurationCheckRequest) {
        ConfigurationCheckResponse response = new ConfigurationCheckResponse(configurationCheckRequest);
        List<ConfigurationIssue> issueList = this.configurationCheckController.getIssues();
        for (ConfigurationIssue issue : issueList) {
            response.addIsse(issue);
        }
        session.write(response);
    }

    private void performServerShutdown(IoSession session, ServerShutdown message) {
        //log some information about who tried this
        String username = session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER).toString();
        this.logger.severe(this.rb.getResourceString("server.shutdown", username));
        Runnable shutdownThread = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                System.exit(0);
            }
        };
        Executors.newSingleThreadExecutor().submit(shutdownThread);
    }

    private void performNotificationTest(IoSession session, PerformNotificationTestRequest message) throws Exception {
        String processOriginHost = session.getRemoteAddress().toString();
        String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
        ClientServerResponse response = new ClientServerResponse(message);
        try {
            Notification notification = new NotificationImplAS2();
            NotificationData notificationData = message.getNotificationData();
            if (notificationData.usesSMTPAuthOAuth2() && notificationData.getOAuth2Config() != null) {
                OAuth2Util.refreshAccessTokenIfRequired(notificationData.getOAuth2Config());
            }
            notification.sendTest(userName, processOriginHost, notificationData);
        } catch (Exception e) {
            //send the SMTP connection trace to the log
            this.logger.severe(e.getMessage());
            response.setException(e);
        }
        session.write(response);
    }

    /**
     * Appends a chunk to a formerly sent data. If this is the first chunk an
     * entry is created in the upload map of this class
     */
    private void processUploadRequestChunk(IoSession session, UploadRequestChunk request) {
        UploadResponseChunk response = new UploadResponseChunk(request);
        OutputStream outStream = null;
        InputStream inStream = null;
        try {
            if (request.getTargetHash() == null) {
                Path tempFile = AS2Tools.createTempFile("upload_as2", ".bin");
                String newHash = this.incUploadRequest();
                this.uploadMap.put(newHash, tempFile.toAbsolutePath().toString());
                request.setTargetHash(newHash);
            }
            response.setTargetHash(request.getTargetHash());
            Path tempFile = Paths.get(this.uploadMap.get(request.getTargetHash()));
            //append to the file and create it if it does not exist so far
            outStream = Files.newOutputStream(tempFile,
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.SYNC);
            inStream = request.getDataStream();
            inStream.transferTo(outStream);
        } catch (Throwable e) {
            response.setException(e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) {
                    //nop
                }
            }
            if (outStream != null) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
        session.write(response);
    }

    private void processStatisticExportRequest(IoSession session, StatisticExportRequest request) {
        StatisticExportResponse response = new StatisticExportResponse(request);
        StatisticExport exporter = new StatisticExport(this.dbDriverManager);
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            exporter.export(outStream,
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getTimestep(), request.getLocalStation(),
                    request.getPartner());
            outStream.flush();
            response.setData(outStream.toByteArray());
        } catch (Throwable e) {
            response.setException(e);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
        //sync respond to the request
        session.write(response);
    }

    /**
     * The user deleted a transaction ion the UI
     */
    private void processDeleteMessageRequest(IoSession session, DeleteMessageRequest request) {
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        String processOriginHost = session.getRemoteAddress().toString();
        String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
        MessageDeleteController controller = new MessageDeleteController(null,
                this.dbDriverManager);
        List<AS2MessageInfo> deleteList = request.getDeleteList();
        RefreshClientMessageOverviewList refreshRequest = new RefreshClientMessageOverviewList();
        refreshRequest.setOperation(RefreshClientMessageOverviewList.OPERATION_DELETE_UPDATE);
        StringBuilder transactionDeleteLog = new StringBuilder();
        controller.deleteMessagesFromLog(deleteList, false, transactionDeleteLog);
        SystemEvent event = new SystemEvent(
                SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_USER,
                SystemEvent.TYPE_TRANSACTION_DELETE);
        event.setUser(userName);
        event.setProcessOriginHost(processOriginHost);
        event.setSubject(this.rbMessageDelete.getResourceString("transaction.deleted.user",
                String.valueOf(deleteList.size())));
        StringBuilder builder = new StringBuilder();
        for (AS2MessageInfo singleInfo : deleteList) {
            builder.append("[")
                    .append(this.rbMessageDelete.getResourceString("transaction.deleted.transactiondate",
                            dateFormat.format(singleInfo.getInitDate())))
                    .append("] (")
                    .append(singleInfo.getSenderId())
                    .append(" --> ")
                    .append(singleInfo.getReceiverId())
                    .append(") ")
                    .append(singleInfo.getMessageId())
                    .append(System.lineSeparator());
        }
        builder.append("---").append(System.lineSeparator());
        builder.append(transactionDeleteLog);
        event.setBody(builder.toString());
        SystemEventManagerImplAS2.newEvent(event);
        this.clientserver.broadcastToClients(refreshRequest);
    }

    private void processUploadRequestFile(IoSession session, UploadRequestFile request) {
        UploadResponseFile response = new UploadResponseFile(request);
        try {
            String uploadHash = request.getUploadHash();
            Path tempFile = Paths.get(this.uploadMap.get(uploadHash));
            Path targetFile = Paths.get(request.getTargetFilename());
            try {
                Files.delete(targetFile);
            } catch (Exception e) {
                //nop
            }
            Files.move(tempFile, targetFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            this.uploadMap.remove(uploadHash);
        } catch (IOException e) {
            response.setException(e);
        }
        session.write(response);
    }

    private void processUploadRequestKeystore(IoSession session, UploadRequestKeystore request) {
        String keystoreTypeForLog = this.rbCertificateManager.getResourceString("keystore." + request.getKeystoreStorageType());
        String processOriginHost = session.getRemoteAddress().toString();
        String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
        List<KeystoreCertificate> oldCertificateList = new ArrayList<KeystoreCertificate>();
        if (request.getKeystoreUsage() == UploadRequestKeystore.KEYSTORE_TYPE_SSL) {
            oldCertificateList.addAll(this.certificateManagerSSL.getKeyStoreCertificateList());
        } else {
            oldCertificateList.addAll(this.certificateManagerEncSign.getKeyStoreCertificateList());
        }
        UploadResponseKeystore response = new UploadResponseKeystore(request);
        CertificateManager newManager = null;
        try {
            String uploadHash = request.getUploadHash();
            Path tempFile = null;
            tempFile = Paths.get(this.uploadMap.get(uploadHash));
            Path targetFile = Paths.get(request.getTargetFilename());
            try {
                Files.delete(targetFile);
            } catch (Exception e) {
                //nop
            }
            Files.move(tempFile, targetFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            this.uploadMap.remove(uploadHash);
            try {
                Files.delete(tempFile);
            } catch (Exception e) {
                //nop
            }
            if (request.getKeystoreUsage() == UploadRequestKeystore.KEYSTORE_TYPE_SSL) {
                this.certificateManagerSSL.rereadKeystoreCertificates();
                newManager = this.certificateManagerSSL;
            } else if (request.getKeystoreUsage() == UploadRequestKeystore.KEYSTORE_TYPE_ENC_SIGN) {
                this.certificateManagerEncSign.rereadKeystoreCertificates();
                newManager = this.certificateManagerEncSign;
            }
        } catch (AccessDeniedException e) {
            IOException exception = new IOException("[" + e.getClass().getSimpleName() + "] "
                    + this.rbCertificateManager.getResourceString("keystore." + request.getKeystoreStorageType()));
            response.setException(exception);
        } catch (Exception e) {
            response.setException(e);
        }
        session.write(response);
        if (response.getException() == null) {
            //everything worked fine? Now check the changes and fire system events
            this.analyzeCertificateChanges(userName, processOriginHost,
                    keystoreTypeForLog, oldCertificateList, newManager.getKeyStoreCertificateList());
        }
    }

    /**
     * Checks if the user has changed certificate related things and fires
     * system events based on this analysis
     *
     */
    private void analyzeCertificateChanges(String userName, String processOriginHost,
            String keystoreTypeForLog, List<KeystoreCertificate> oldList, List<KeystoreCertificate> newList) {
        //check for added certificates and alias change
        for (KeystoreCertificate newCertificate : newList) {
            int index = oldList.indexOf(newCertificate);
            //its an add - the new certificate does not exist in the old list
            if (index == -1) {
                String subject = this.rbCertificateManager.getResourceString("event.certificate.added.subject",
                        new Object[]{
                            keystoreTypeForLog,
                            newCertificate.getAlias()
                        });
                String body = this.rbCertificateManager.getResourceString("event.certificate.added.body",
                        new Object[]{
                            newCertificate.getInfo()
                        });
                SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                        SystemEvent.ORIGIN_USER,
                        SystemEvent.TYPE_CERTIFICATE_ADD);
                event.setBody(body);
                event.setSubject(subject);
                event.setProcessOriginHost(processOriginHost);
                event.setUser(userName);
                SystemEventManagerImplAS2.newEvent(event);
            } else {
                //the certificate existed already - check if the alias has been changed
                KeystoreCertificate oldCertificate = oldList.get(index);
                if (!oldCertificate.getAlias().equals(newCertificate.getAlias())) {
                    String subject = this.rbCertificateManager.getResourceString("event.certificate.modified.subject",
                            new Object[]{
                                keystoreTypeForLog
                            });
                    String body = this.rbCertificateManager.getResourceString("event.certificate.modified.body",
                            new Object[]{
                                oldCertificate.getAlias(),
                                newCertificate.getAlias(),
                                newCertificate.getInfo()
                            });
                    SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                            SystemEvent.ORIGIN_USER,
                            SystemEvent.TYPE_CERTIFICATE_MODIFY);
                    event.setBody(body);
                    event.setSubject(subject);
                    event.setProcessOriginHost(processOriginHost);
                    event.setUser(userName);
                    SystemEventManagerImplAS2.newEvent(event);
                }
            }
        }
        //check for deleted certificates
        for (KeystoreCertificate oldCertificate : oldList) {
            if (!newList.contains(oldCertificate)) {
                String subject = this.rbCertificateManager.getResourceString("event.certificate.deleted.subject",
                        new Object[]{
                            keystoreTypeForLog,
                            oldCertificate.getAlias()
                        });
                String body = this.rbCertificateManager.getResourceString("event.certificate.deleted.body",
                        new Object[]{
                            oldCertificate.getInfo()
                        });
                SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                        SystemEvent.ORIGIN_USER,
                        SystemEvent.TYPE_CERTIFICATE_DEL);
                event.setBody(body);
                event.setSubject(subject);
                event.setProcessOriginHost(processOriginHost);
                event.setUser(userName);
                SystemEventManagerImplAS2.newEvent(event);
            }
        }
    }

    /**
     * A client performed a file rename request
     *
     */
    private void processFileRenameRequest(IoSession session, FileRenameRequest request) {
        FileRenameResponse response = new FileRenameResponse(request);
        File oldFile = new File(new File(request.getOldName()).toURI());
        File newFile = new File(new File(request.getNewName()).toURI());
        boolean success = oldFile.renameTo(newFile);
        response.setSuccess(success);
        session.write(response);
    }

    /**
     * A client performed a file delete request
     *
     */
    private void processFileDeleteRequest(IoSession session, FileDeleteRequest request) {
        FileDeleteResponse response = new FileDeleteResponse(request);
        Path fileToDelete = Paths.get(request.getFilename());
        if (Files.isDirectory(fileToDelete)) {
            try {
                this.fileOperationProcessing.deleteDirectoryWithSubdirectories(fileToDelete);
                response.setSuccess(true);
            } catch (Exception e) {
                response.setException(e);
                response.setSuccess(false);
            }
        } else {
            try {
                Files.deleteIfExists(fileToDelete);
                response.setSuccess(true);
            } catch (Exception e) {
                response.setException(e);
                response.setSuccess(false);
            }
        }
        session.write(response);
    }

    /**
     * A client performed a manual send request
     *
     * @param session
     * @param request
     */
    private void processManualSendRequest(IoSession session, ManualSendRequest request) {
        ManualSendResponse response = new ManualSendResponse(request);
        SendOrderSender orderSender = new SendOrderSender(this.dbDriverManager);
        try {
            String[] originalFilenames = null;
            Path[] sendFiles = null;
            String[] payloadContentTypes = null;
            if (request.getSendTestdata()) {
                originalFilenames = new String[]{"testdata.txt"};
                sendFiles = new Path[]{TestdataGenerator.generateTestdata()};
            } else {
                //process the received data from the client
                originalFilenames = new String[request.getFilenames().size()];
                payloadContentTypes = new String[request.getFilenames().size()];
                for (int i = 0; i < request.getFilenames().size(); i++) {
                    originalFilenames[i] = request.getFilenames().get(i);
                    payloadContentTypes[i] = request.getPayloadContentTypes().get(i);
                }
                List<String> uploadHashs = request.getUploadHashs();
                List<Path> files = new ArrayList<Path>();
                for (String uploadHash : uploadHashs) {
                    Path uploadedFile = Paths.get(this.uploadMap.get(uploadHash));
                    files.add(uploadedFile);
                }
                sendFiles = new Path[files.size()];
                for (int i = 0; i < files.size(); i++) {
                    sendFiles[i] = files.get(i);
                }
            }
            //reload the partner from the database
            Partner sender = null;
            String senderAS2Id = request.getSenderAS2Id();
            if (senderAS2Id != null) {
                sender = this.partnerAccess.getPartnerByAS2Id(senderAS2Id, PartnerAccessDB.DATA_COMPLETENESS_FULL);
            } else {
                String senderAS2Name = request.getSenderAS2Name();
                if (senderAS2Name != null) {
                    sender = this.partnerAccess.getPartnerByName(senderAS2Name, PartnerAccessDB.DATA_COMPLETENESS_FULL);
                }
            }
            Partner receiver = null;
            String receiverAS2Id = request.getReceiverAS2Id();
            if (receiverAS2Id != null) {
                receiver = this.partnerAccess.getPartnerByAS2Id(receiverAS2Id, PartnerAccessDB.DATA_COMPLETENESS_FULL);
            } else {
                String receiverAS2Name = request.getReceiverAS2Name();
                if (receiverAS2Name != null) {
                    receiver = this.partnerAccess.getPartnerByName(receiverAS2Name, PartnerAccessDB.DATA_COMPLETENESS_FULL);
                }
            }
            if (sender == null) {
                throw new Exception("Undefined message sender or message sender does not exist.");
            }
            if (receiver == null) {
                throw new Exception("Undefined message receiver or message receiver does not exist.");
            }
            AS2Message message = orderSender.send(this.certificateManagerEncSign, sender,
                    receiver, sendFiles, originalFilenames, request.getUserdefinedId(),
                    request.getSubject(), payloadContentTypes);
            if (message == null) {
                throw new Exception(this.rb.getResourceString("send.failed"));
            } else {
                response.setAS2Info((AS2MessageInfo) message.getAS2Info());
                //is this a resend? Then get the resend message id and increment the resend counter, also enter 
                //a log entry
                String resendMessageId = request.getResendMessageId();
                if (resendMessageId != null) {
                    this.messageAccess.incResendCounter(request.getResendMessageId());
                    AS2MessageInfo oldMessageInfo = this.messageAccess.getLastMessageEntry(resendMessageId);
                    if (oldMessageInfo != null) {
                        this.logger.log(Level.WARNING,
                                this.rb.getResourceString("message.resend.oldtransaction",
                                        new Object[]{
                                            message.getAS2Info().getMessageId()
                                        }), oldMessageInfo);
                    }
                    this.logger.log(Level.WARNING,
                            this.rb.getResourceString("message.resend.newtransaction",
                                    new Object[]{
                                        resendMessageId,}), message.getAS2Info());
                    String processOriginHost = session.getRemoteAddress().toString();
                    String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
                    SystemEvent event = new SystemEvent(
                            SystemEvent.SEVERITY_INFO, SystemEvent.ORIGIN_USER, SystemEvent.TYPE_TRANSACTION_RESEND);
                    event.setUser(userName);
                    event.setProcessOriginHost(processOriginHost);
                    event.setBody(resendMessageId + ": " + this.rb.getResourceString("message.resend.oldtransaction",
                            new Object[]{
                                message.getAS2Info().getMessageId()
                            }));
                    event.setSubject(this.rb.getResourceString("message.resend.title"));
                    SystemEventManagerImplAS2.newEvent(event);
                }
                this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
            }
        } catch (Exception e) {
            this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
            response.setException(e);
        }
        session.write(response);
    }

    /**
     * A client performed a preferences request
     *
     * @param session
     * @param request
     */
    private void processPreferencesRequest(IoSession session, PreferencesRequest request) {
        if (request.getType() == PreferencesRequest.TYPE_GET) {
            PreferencesResponse response = new PreferencesResponse(request);
            response.setValue(this.preferences.get(request.getKey()));
            session.write(response);
        } else if (request.getType() == PreferencesRequest.TYPE_GET_DEFAULT) {
            PreferencesResponse response = new PreferencesResponse(request);
            response.setValue(this.preferences.getDefaultValue(request.getKey()));
            session.write(response);
        } else if (request.getType() == PreferencesRequest.TYPE_SET) {
            String oldValue = this.preferences.get(request.getKey());
            if (!oldValue.equals(request.getValue())) {
                this.preferences.put(request.getKey(), request.getValue());
                String processOriginHost = session.getRemoteAddress().toString();
                String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
                this.fireEventPreferencesModified(userName, processOriginHost, request.getKey(), oldValue, request.getValue());
            }
        }
    }

    /**
     * Fires a system event if a user changed the server settings
     */
    private void fireEventPreferencesModified(String userName, String processOriginHost, String key, String oldValue, String newValue) {
        String subject = this.rbPreferences.getResourceString("event.preferences.modified.subject",
                key.toUpperCase());
        String body = this.rbPreferences.getResourceString("event.preferences.modified.body",
                new Object[]{
                    oldValue, newValue
                });
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_USER,
                SystemEvent.TYPE_SERVER_CONFIGURATION_CHANGED);
        event.setBody(body);
        event.setSubject(subject);
        event.setProcessOriginHost(processOriginHost);
        event.setUser(userName);
        SystemEventManagerImplAS2.newEvent(event);
        //do no broadcast passwords
        if (key.equals(PreferencesAS2.AUTH_PROXY_PASS)
                || key.equals(PreferencesAS2.KEYSTORE_HTTPS_SEND_PASS)
                || key.equals(PreferencesAS2.KEYSTORE_PASS)) {
            oldValue = "***";
            newValue = "***";
        }
        this.clientserver.broadcastToClients(new ConfigurationChangedOnServerPreferences(
                key, oldValue, newValue
        ));
    }

    /**
     * A client performed a download request
     *
     * @param session
     * @param request
     */
    private void processDownloadRequestFile(IoSession session, DownloadRequestFile request) {
        DownloadResponseFile response = null;
        if (request instanceof DownloadRequestFileLimited) {
            DownloadRequestFileLimited requestLimited = (DownloadRequestFileLimited) request;
            response = new DownloadResponseFileLimited(requestLimited);
            InputStream inStream = null;
            try {
                if (request.getFilename() == null) {
                    throw new FileNotFoundException();
                }
                Path downloadFile = Paths.get(requestLimited.getFilename());
                response.setFullFilename(downloadFile.toAbsolutePath().toString());
                response.setReadOnly(!Files.isWritable(downloadFile));
                response.setSize(Files.size(downloadFile));
                if (Files.size(downloadFile) < requestLimited.getMaxSize()) {
                    inStream = Files.newInputStream(Paths.get(request.getFilename()));
                    response.setData(inStream);
                    ((DownloadResponseFileLimited) response).setSizeExceeded(false);
                } else {
                    ((DownloadResponseFileLimited) response).setSizeExceeded(true);
                }
            } catch (Exception e) {
                response.setException(e);
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (Exception e) {
                        //nop
                    }
                }
            }
        } else {
            response = new DownloadResponseFile(request);
            InputStream inStream = null;
            try {
                if (request.getFilename() == null) {
                    throw new FileNotFoundException();
                }
                Path downloadFile = Paths.get(request.getFilename());
                if (!Files.exists(downloadFile)
                        || !Files.isReadable(downloadFile)
                        || !Files.isRegularFile(downloadFile)) {
                    throw new FileNotFoundException();
                }
                response.setFullFilename(downloadFile.toAbsolutePath().toString());
                response.setReadOnly(!Files.isWritable(downloadFile));
                response.setSize(Files.size(downloadFile));
                inStream = Files.newInputStream(downloadFile);
                response.setData(inStream);
            } catch (Throwable e) {
                response.setException(e);
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (Exception e) {
                        //nop
                    }
                }
            }
        }
        session.write(response);
    }

    /**
     * sync: the partner settings have been changed
     */
    private void processPartnerModificationMessage(IoSession session, PartnerModificationRequest request) {
        ClientServerResponse response = new ClientServerResponse(request);
        String processOriginHost = session.getRemoteAddress().toString();
        String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
        List<Partner> newPartner = request.getData();
        //a new connection to the database is required because the partner storage contains several tables and all this has to be transactional
        Connection configConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        String transactionName = "AS2ServerProcessing_processPartnerModificationMessage";
        try {
            configConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = configConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockDELETE(
                    transactionStatement,
                    new String[]{
                        "partner",
                        "certificates",
                        "partnerevent",
                        "httpheader",
                        "partnersystem",
                        "oauth2"
                    });            
            try {
                //first delete all partners that are in the DB but not in the new list
                List<Partner> existingPartner = this.partnerAccess.getAllPartner(
                        PartnerAccessDB.DATA_COMPLETENESS_FULL, configConnectionNoAutoCommit);
                for (int i = 0; i < existingPartner.size(); i++) {
                    if (!newPartner.contains(existingPartner.get(i))) {
                        this.partnerAccess.deletePartner(existingPartner.get(i), configConnectionNoAutoCommit);
                        this.fireEventPartnerDeleted(userName, processOriginHost, existingPartner.get(i));
                    }
                }
                //insert all NEW partners and update the existing
                for (int i = 0; i < newPartner.size(); i++) {
                    if (newPartner.get(i).getDBId() < 0) {
                        this.partnerAccess.insertPartner(newPartner.get(i), configConnectionNoAutoCommit);
                        this.fireEventPartnerAdded(userName, processOriginHost, newPartner.get(i));
                    } else {
                        this.partnerAccess.updatePartner(newPartner.get(i), configConnectionNoAutoCommit);
                        //find out old partner
                        Partner oldPartner = null;
                        for (Partner testPartner : existingPartner) {
                            if (testPartner.getDBId() == newPartner.get(i).getDBId()) {
                                oldPartner = testPartner;
                            }
                        }
                        if (oldPartner != null 
                                && !Partner.hasSameContent(
                                        oldPartner, newPartner.get(i), this.certificateManagerEncSign)) {
                            this.fireEventPartnerModified(userName, processOriginHost, oldPartner, newPartner.get(i));
                        }
                    }
                }
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Exception e) {
                try {
                    //an error occured - rollback transaction and release all locks
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Throwable e) {
            response.setException(e);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (configConnectionNoAutoCommit != null) {
                try {
                    configConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
        //sync answer
        session.write(response);
    }

    /**
     * Fires a system event if a partner has been deleted by the user in the
     * partner management
     */
    private void fireEventPartnerDeleted(String userName, String processOriginHost, Partner partner) {
        String subject = this.rbPartnerConfig.getResourceString("event.partner.deleted.subject", partner.getName());
        String body = this.rbPartnerConfig.getResourceString("event.partner.deleted.body", partner.toDisplay(this.certificateManagerEncSign));
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_USER,
                SystemEvent.TYPE_PARTNER_DEL);
        event.setBody(body);
        event.setSubject(subject);
        event.setProcessOriginHost(processOriginHost);
        event.setUser(userName);
        SystemEventManagerImplAS2.newEvent(event);
    }

    /**
     * Fires a system event if a partner has been deleted by the user in the
     * partner management
     */
    private void fireEventPartnerAdded(String userName, String processOriginHost, Partner partner) {
        String subject = this.rbPartnerConfig.getResourceString("event.partner.added.subject", partner.getName());
        String body = this.rbPartnerConfig.getResourceString("event.partner.added.body", partner.toDisplay(this.certificateManagerEncSign));
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_USER,
                SystemEvent.TYPE_PARTNER_ADD);
        event.setBody(body);
        event.setSubject(subject);
        event.setProcessOriginHost(processOriginHost);
        event.setUser(userName);
        SystemEventManagerImplAS2.newEvent(event);
    }

    /**
     * Fires a system event if a partner has been deleted by the user in the
     * partner management
     */
    private void fireEventPartnerModified(String userName, String processOriginHost, Partner oldPartner, Partner newPartner) {
        String subject = this.rbPartnerConfig.getResourceString("event.partner.modified.subject", oldPartner.getName());
        String body = this.rbPartnerConfig.getResourceString("event.partner.modified.body",
                new Object[]{
                    oldPartner.toDisplay(this.certificateManagerEncSign),
                    newPartner.toDisplay(this.certificateManagerEncSign)
                });
        SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_USER,
                SystemEvent.TYPE_PARTNER_MODIFY);
        event.setBody(body);
        event.setSubject(subject);
        event.setProcessOriginHost(processOriginHost);
        event.setUser(userName);
        SystemEventManagerImplAS2.newEvent(event);
    }

    /**
     * A client asks for partner information
     */
    private void processPartnerListRequest(IoSession session, PartnerListRequest request) {
        PartnerListResponse response = new PartnerListResponse(request);
        try {
            if (request.getListOption() == PartnerListRequest.LIST_ALL) {
                response.setList(this.partnerAccess.getAllPartner(request.getRequestedDataCompleteness()));
            } else if (request.getListOption() == PartnerListRequest.LIST_LOCALSTATION) {
                List<Partner> list = new ArrayList<Partner>();
                list.addAll(this.partnerAccess.getLocalStations(request.getRequestedDataCompleteness()));
                response.setList(list);
            } else if (request.getListOption() == PartnerListRequest.LIST_NON_LOCALSTATIONS) {
                response.setList(this.partnerAccess.getNonLocalStations(
                        request.getRequestedDataCompleteness()));
            } else if (request.getListOption() == PartnerListRequest.LIST_NON_LOCALSTATIONS_SUPPORTING_CEM) {
                List<Partner> partnerList = this.partnerAccess.getNonLocalStations(
                        request.getRequestedDataCompleteness());
                List<Partner> cemSupportingList = new ArrayList<Partner>();
                for (Partner partner : partnerList) {
                    PartnerSystem partnerSystem = this.partnerSystemAccess.getPartnerSystem(partner);
                    if (partnerSystem != null && partnerSystem.supportsCEM()) {
                        cemSupportingList.add(partner);
                    }
                }
                response.setList(cemSupportingList);
            } else if (request.getListOption() == PartnerListRequest.LIST_BY_AS2_ID) {
                List<Partner> list = new ArrayList<Partner>();
                Partner partner = this.partnerAccess.getPartner(request.getAdditionalListOptionStr());
                if (partner != null) {
                    list.add(partner);
                }
                response.setList(list);
            }
        } catch (Throwable e) {
            response.setException(e);
        }
        //sync answer
        session.write(response);
    }

    private void processMessageOverviewRequest(IoSession session, MessageOverviewRequest request) {
        MessageOverviewResponse response = new MessageOverviewResponse(request);
        if (request.getFilter() != null) {
            response.setList(this.messageAccess.getMessageOverview(request.getFilter()));
        } else {
            response.setList(this.messageAccess.getMessageOverview(request.getMessageId()));
        }
        response.setMessageSumOnServer(this.messageAccess.getMessageCount());
        //sync answer
        session.write(response);
    }

    private void processMessageLogRequest(IoSession session, MessageLogRequest request) {
        MessageLogResponse response = new MessageLogResponse(request);
        response.setList(this.logAccess.getLog(request.getMessageId()));
        //sync answer
        session.write(response);
    }

    private void processMessageDetailRequest(IoSession session, MessageDetailRequest request) {
        MessageDetailResponse response = new MessageDetailResponse(request);
        response.setList(this.messageAccess.getMessageDetails(request.getMessageId()));
        //sync answer
        session.write(response);
    }

    private void processMessagePayloadRequest(IoSession session, MessagePayloadRequest request) {
        MessagePayloadResponse response = new MessagePayloadResponse(request);
        response.setList(this.messageAccess.getPayload(request.getMessageId()));
        //sync answer
        session.write(response);
    }

    /**
     * sync
     */
    private void processNotificationGetRequest(IoSession session, NotificationGetRequest request) {
        NotificationGetResponse response = new NotificationGetResponse(request);
        NotificationAccessDB access = new NotificationAccessDBImplAS2(this.dbDriverManager);
        response.setData(access.getNotificationData());
        //sync answer
        session.write(response);
    }

    /**
     * sync
     */
    private void performPartnerSystemRequest(IoSession session, PartnerSystemRequest request) {
        PartnerSystemResponse response = new PartnerSystemResponse(request);
        if (request.getType() == PartnerSystemRequest.TYPE_LIST_ALL) {
            response.addPartnerSystems(this.partnerSystemAccess.getAllPartnerSystems());
        } else {
            PartnerSystem singleSystem = this.partnerSystemAccess.getPartnerSystem(request.getPartner());
            if (singleSystem != null) {
                List<PartnerSystem> singleList = new ArrayList<PartnerSystem>();
                singleList.add(singleSystem);
                response.addPartnerSystems(singleList);
            }
        }
        //sync answer
        session.write(response);
    }

    /**
     * async - set new notification data to the server
     */
    private void processNotificationSetRequest(IoSession session, NotificationSetMessage request) {
        String processOriginHost = session.getRemoteAddress().toString();
        String userName = (String) session.getAttribute(ClientServerSessionHandler.SESSION_ATTRIB_USER);
        NotificationAccessDB access = new NotificationAccessDBImplAS2(this.dbDriverManager);
        NotificationDataImplAS2 oldNotificationData = (NotificationDataImplAS2) access.getNotificationData();
        NotificationDataImplAS2 newNotificationData = (NotificationDataImplAS2) request.getData();
        access.updateNotification(newNotificationData);
        if (!oldNotificationData.toXML(0).equals(newNotificationData.toXML(0))) {
            SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_USER, SystemEvent.TYPE_SERVER_CONFIGURATION_CHANGED);
            event.setSubject(this.rbPreferences.getResourceString("event.notificationdata.modified.subject"));
            event.setBody(this.rbPreferences.getResourceString("event.notificationdata.modified.body",
                    new Object[]{
                        oldNotificationData.toXML(0),
                        newNotificationData.toXML(0)
                    }));
            event.setUser(userName);
            event.setProcessOriginHost(processOriginHost);
            SystemEventManagerImplAS2.newEvent(event);
            //inform all attached clients about the changes via server push message
            this.clientserver.broadcastToClients(new ConfigurationChangedOnServerNotification());
        }
    }

    private void performServerInteroperabilityRequest(IoSession session, ServerInteroperabilityRequest request) {
        ServerInteroperabilityResponse response = new ServerInteroperabilityResponse(request);
        ServerInteroperabilityAccessDB access
                = new ServerInteroperabilityAccessDB(this.dbDriverManager);
        List<ServerInteroperabilityContainer> list = access.getServer();
        response.setList(list);
        //sync answer
        session.write(response);
    }

    private void performQuotaResetRequest(IoSession session, QuotaResetRequest request) {
        QuotaAccessDB access = new QuotaAccessDB(this.dbDriverManager);
        access.resetCounter(request.getLocalStationId(), request.getPartnerId());
        //sync answer
        session.write(new ClientServerResponse(request));
    }

    private void performStatisticOverviewRequest(IoSession session, StatisticOverviewRequest request) {
        StatisticOverviewResponse response = new StatisticOverviewResponse(request);
        QuotaAccessDB access = new QuotaAccessDB(this.dbDriverManager);
        List<StatisticOverviewEntry> list = access.getStatisticOverview(request.getAS2Identification());
        response.setList(list);
        //sync answer
        session.write(response);
    }

    private void performStatisticDetailRequest(IoSession session, StatisticDetailRequest request) {
        StatisticDetailResponse response = new StatisticDetailResponse(request);
        StatisticAccessDB access = new StatisticAccessDB(this.dbDriverManager);
        List<StatisticDetailEntry> list = new ArrayList<StatisticDetailEntry>();
        for (int i = 0; i < request.getPeriods().size(); i++) {
            SimpleTimePeriod period = request.getPeriods().get(i);
            StatisticDetailEntry entry = access.getDetails(period.getStartMillis(), period.getEndMillis(),
                    request.getAS2IdentificationLocal(),
                    request.getAS2IdentificationPartner(),
                    request.getDirection(), new int[]{request.getStates().get(i).intValue()});
            entry.setSeriesName(request.getSeriesName().get(i));
            list.add(entry);
        }
        response.setList(list);
        //sync answer
        session.write(response);
    }

    /**
     * sync
     */
    private void processCEMListRequest(IoSession session, CEMListRequest request) {
        CEMListResponse response = new CEMListResponse(request);
        CEMAccessDB access = new CEMAccessDB(this.dbDriverManager);
        response.setList(access.getCEMEntries());
        //sync answer
        session.write(response);
    }

    /**
     * sync
     */
    private void processCEMDeleteRequest(IoSession session, CEMDeleteRequest request) {
        CEMEntry entry = request.getEntry();
        CEMAccessDB cemAccess = new CEMAccessDB(this.dbDriverManager);
        cemAccess.setPendingRequestsToState(entry.getInitiatorAS2Id(), entry.getReceiverAS2Id(), entry.getCategory(), entry.getRequestId(),
                CEMEntry.STATUS_CANCELED_INT);
        //remove the underlaying messages
        if (entry.getRequestMessageid() != null) {
            List<String> requestMessageList = new ArrayList<String>();
            requestMessageList.add(entry.getRequestMessageid());
            this.logAccess.deleteMessageLog(requestMessageList);
            this.messageAccess.deleteMessages(requestMessageList);
        }
        if (entry.getResponseMessageid() != null) {
            List<String> responseMessageList = new ArrayList<String>();
            responseMessageList.add(entry.getResponseMessageid());
            this.logAccess.deleteMessageLog(responseMessageList);
            this.messageAccess.deleteMessages(responseMessageList);
        }
        cemAccess.removeEntry(entry.getInitiatorAS2Id(), entry.getReceiverAS2Id(), entry.getCategory(), entry.getRequestId());
        //sync answer
        session.write(new ClientServerResponse(request));
    }

    /**
     * sync
     */
    private void processCEMCancelRequest(IoSession session, CEMCancelRequest request) {
        CEMEntry entry = request.getEntry();
        CEMAccessDB cemAccess = new CEMAccessDB(this.dbDriverManager);
        cemAccess.setPendingRequestsToState(entry.getInitiatorAS2Id(), entry.getReceiverAS2Id(), entry.getCategory(), entry.getRequestId(),
                CEMEntry.STATUS_CANCELED_INT);
        //sync answer
        session.write(new ClientServerResponse(request));
    }

    /**
     * sync
     */
    private void processMessageRequestLastMessage(IoSession session, MessageRequestLastMessage request) {
        MessageResponseLastMessage response = new MessageResponseLastMessage(request);
        response.setInfo(this.messageAccess.getLastMessageEntry(request.getMessageId()));
        //sync answer
        session.write(response);
    }

    /**
     * sync
     */
    private void processCEMSendRequest(IoSession session, CEMSendRequest request) {
        CEMSendResponse response = new CEMSendResponse(request);
        Partner initiator = request.getInitiator();
        KeystoreCertificate certificate = request.getCertificate();
        Date activationDate = request.getActivationDate();
        //set time to 0:01 of this day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(activationDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        CEMInitiator cemInitiator = new CEMInitiator(this.dbDriverManager, this.certificateManagerEncSign);
        try {
            List<Partner> informedPartnerList = cemInitiator.sendRequests(initiator,
                    request.getReceiver(),
                    certificate,
                    request.isPurposeEncryption(),
                    request.isPurposeSignature(),
                    request.isPurposeSSL(),
                    calendar.getTime());
            response.setInformedPartner(informedPartnerList);
        } catch (Throwable e) {
            response.setException(e);
        }
        //sync answer
        session.write(response);
    }

    /**
     * Returns some server info values
     */
    private void processServerInfoRequest(IoSession session, ServerInfoRequest infoRequest) {
        ServerInfoResponse response = new ServerInfoResponse(infoRequest);
        response.setProperty(ServerInfoRequest.SERVER_BUILD_DATE, AS2ServerVersion.getLastModificationDate());
        response.setProperty(ServerInfoRequest.LICENSEE, AS2Server.PLUGINS.getLicensee());
        response.setProperty(ServerInfoRequest.EXPIRE_DATE, AS2Server.PLUGINS.getLicenseExpireDateAsString());
        response.setProperty(ServerInfoRequest.SERVER_FULL_PRODUCT_NAME, AS2ServerVersion.getFullProductName());
        response.setProperty(ServerInfoRequest.SERVER_START_TIME, String.valueOf(this.startupTime));
        response.setProperty(ServerInfoRequest.SERVER_PRODUCT_NAME, AS2ServerVersion.getProductName());
        response.setProperty(ServerInfoRequest.SERVER_VERSION, AS2ServerVersion.getVersion());
        response.setProperty(ServerInfoRequest.SERVER_BUILD, AS2ServerVersion.getBuild());
        response.setProperty(ServerInfoRequest.SERVER_CPU_CORES, String.valueOf(Runtime.getRuntime().availableProcessors()));
        response.setProperty(ServerInfoRequest.SERVER_OS, System.getProperty("os.name")
                + " " + System.getProperty("os.version")
                + " " + System.getProperty("os.arch")
        );
        try {
            response.setProperty(ServerInfoRequest.JVM_DATA_MODEL, System.getProperty("sun.arch.data.model"));
        } catch (Throwable e) {
            response.setProperty(ServerInfoRequest.JVM_DATA_MODEL, "unknown");
        }
        response.setProperty(ServerInfoRequest.SERVER_VM_VERSION,
                System.getProperty("java.version")
                + " "
                + System.getProperty("sun.arch.data.model")
                + " "
                + System.getProperty("java.vendor"));
        response.setProperty(ServerInfoRequest.DB_SERVER_VERSION,
                this.dbServerInformation.getProductName() + " "
                + this.dbServerInformation.getProductVersion()
                + "@" + this.dbServerInformation.getHost()
                + " [JDBC " + this.dbServerInformation.getJDBCVersion() + "]");
        //add db client information if the database is not the embedded HSQLDB
        if (AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_MYSQL)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_POSTGRESQL)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_ORACLE_DB)) {
            String dbClient = this.dbClientInformation.getProductName() + " "
                    + this.dbClientInformation.getProductVersion();
            response.setProperty(ServerInfoRequest.DB_CLIENT_VERSION, dbClient);
        }
        if (this.httpServerConfigInfo != null) {
            response.setProperty(ServerInfoRequest.HTTP_SERVER_VERSION, "Jetty " + this.httpServerConfigInfo.getJettyHTTPServerVersion());
        } else {
            //Its possible to start the as2 system without http server
            response.setProperty(ServerInfoRequest.HTTP_SERVER_VERSION, "NONE");
        }
        response.setProperty(ServerInfoRequest.SERVER_USER, System.getProperty("user.name"));
        response.setProperty(ServerInfoRequest.SERVER_LOCALE, Locale.getDefault().toString());
        float heapGB = (float) Runtime.getRuntime().maxMemory() / (float) (1024f * 1024f * 1024f);
        response.setProperty(ServerInfoRequest.SERVER_MAX_HEAP_GB, String.format("%.2f", heapGB) + " GB");
        response.setProperty(ServerInfoRequest.SERVERSIDE_TRANSACTION_COUNT, String.valueOf(this.messageAccess.getMessageCount()));
        response.setProperty(ServerInfoRequest.PLUGINS, AS2Server.PLUGINS.getStartedPluginsAsString());
        response.setProperty(ServerInfoRequest.SERVERSIDE_PID, String.valueOf(this.serverProcessId));
        response.setProperty(ServerInfoRequest.CLIENTSIDE_PID, String.valueOf(infoRequest.getClientPID()));
        if (System.getenv("iswindowsservice") != null && System.getenv("iswindowsservice").equals("1")) {
            response.setProperty(ServerInfoRequest.SERVER_START_METHOD_WINDOWS_SERVICE, "TRUE");
        } else {
            response.setProperty(ServerInfoRequest.SERVER_START_METHOD_WINDOWS_SERVICE, "FALSE");
        }
        //check the number of poll threads
        response.setProperty(ServerInfoRequest.DIR_POLL_THREAD_COUNT, String.valueOf(this.pollManager.getPollThreadCount()));
        response.setProperty(ServerInfoRequest.DIR_POLL_THREADS_PER_MIN, String.format("%.0f", this.pollManager.getPollsPerMinute()));
        //display the instance id - this makes only sense if there are more than a single instance possible
        //or if ou are using an external database where the instance activity is logged 
        if (AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_HA)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_POSTGRESQL)
                || AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_MYSQL)) {
            response.setProperty(ServerInfoRequest.UNIQUE_INSTANCE_ID, ServerInstance.ID);
        }
        try {
            if (AS2Server.PLUGINS.isActivated(ServerPlugins.PLUGIN_HA)) {
                int foundNodes = this.haAccess.getServerInstanceHA(
                        this.dbDriverManager, TimeUnit.DAYS.toSeconds(30)).size();
                response.setProperty(ServerInfoRequest.HA_NUMBER_OF_NODES_LAST_30DAYS,
                        String.valueOf(foundNodes));
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e);
        }
        session.write(response);
    }

    /**
     * An incoming message arrives from the receipt servlet or the system itself
     * (sync answer)
     */
    private void processIncomingMessageRequest(IoSession session, IncomingMessageRequest incomingMessageRequest) {
        IncomingMessageResponse incomingMessageResponse = new IncomingMessageResponse(incomingMessageRequest);
        try {
            try {
                //inc the sent data size, this is for sync error MDN
                long size = 0;
                if (incomingMessageRequest.getHeader() != null) {
                    size += this.computeRawHeaderSize(incomingMessageRequest.getHeader());
                }
                if (incomingMessageRequest.getMessageDataFilename() != null) {
                    size += new File(incomingMessageRequest.getMessageDataFilename()).length();
                }
                //MBean counter for received data size
                AS2Server.incRawReceivedData(size);
                //fully process the inbound message
                incomingMessageResponse = this.newMessageArrived(incomingMessageRequest);
            } catch (AS2Exception as2Exception) {
                AS2MessageInfo messageInfo = (AS2MessageInfo) as2Exception.getAS2Message().getAS2Info();
                messageInfo.setUsesTLS(incomingMessageRequest.usesTLS());
                //fire a system event for a failed inbound message processing
                SystemEventManagerImplAS2 eventManager = new SystemEventManagerImplAS2();
                try {
                    eventManager.newEventTransactionError(messageInfo.getMessageId(),
                            this.dbDriverManager);
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
                }
                String foundSenderId = messageInfo.getSenderId();
                String foundReceiverId = messageInfo.getReceiverId();
                Partner as2MessageReceiver = this.partnerAccess.getPartner(foundReceiverId);
                AS2MDNCreation mdnCreation = new AS2MDNCreation(this.certificateManagerEncSign);
                mdnCreation.setLogger(this.logger);
                //partner might be null - thats ok
                Partner foundSender = this.partnerAccess.getPartner(foundSenderId);
                AS2Message mdn = mdnCreation.createMDNError(as2Exception, foundSender, foundSenderId, as2MessageReceiver, foundReceiverId);
                AS2MDNInfo mdnInfo = (AS2MDNInfo) mdn.getAS2Info();
                if (messageInfo.requestsSyncMDN()) {
                    //sync error MDN
                    incomingMessageResponse.setContentType(mdn.getContentType());
                    incomingMessageResponse.setMDNData(mdn.getRawData());
                    //build up the header for the sync response
                    Properties header = mdnCreation.buildHeaderForSyncMDN(mdn);
                    incomingMessageResponse.setHeader(header);
                    //MBean counter: inc the sent data size, this is for sync error MDN
                    AS2Server.incRawSentData(this.computeRawHeaderSize(header) + mdn.getRawDataSize());
                    Partner mdnReceiver = partnerAccess.getPartner(mdnInfo.getReceiverId());
                    Partner mdnSender = partnerAccess.getPartner(mdnInfo.getSenderId());
                    this.messageStoreHandler.storeSentMessage(mdn, mdnSender, mdnReceiver, header);
                    this.mdnAccess.initializeOrUpdateMDN(mdnInfo);
                    this.logger.log(Level.INFO,
                            this.rb.getResourceString("sync.mdn.sent",
                                    new Object[]{
                                        mdnInfo.getRelatedMessageId()
                                    }), mdnInfo);
                    this.messageAccess.setMessageState(mdnInfo.getRelatedMessageId(), AS2Message.STATE_STOPPED);
                    this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
                    session.write(incomingMessageResponse);
                    return;
                } else {
                    //async error MDN
                    Partner messageReceiver = this.partnerAccess.getPartner(mdnInfo.getReceiverId());
                    Partner messageSender = this.partnerAccess.getPartner(mdnInfo.getSenderId());
                    //async back to sender. There are ALWAYS required partners for the send order even if the as2 ids 
                    //are not founnd because the partners are required for the async MDN receipt URL and a well structured MDN
                    if (messageReceiver == null) {
                        messageReceiver = new Partner();
                        messageReceiver.setAS2Identification(mdnInfo.getReceiverId());
                        messageReceiver.setMdnURL(messageInfo.getAsyncMDNURL());
                    }
                    if (messageSender == null) {
                        messageSender = new Partner();
                        messageSender.setAS2Identification(mdnInfo.getSenderId());
                    }
                    this.addSendOrder(mdn, messageReceiver, messageSender);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            StringBuilder message = new StringBuilder("AS2ServerProcessing: [" + e.getClass().getName() + "] " + e.getMessage());
            if (e.getCause() != null) {
                message.append(" - caused by [" + e.getCause().getClass().getName() + "] " + e.getCause().getMessage());
            }
            this.logger.severe(message.toString());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
        }
        session.write(incomingMessageResponse);
    }

    /**
     * Compute the header upload size for the jmx interface
     */
    private long computeRawHeaderSize(Properties header) {
        long size = 0;
        Enumeration enumeration = header.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            //key + "="
            size += key.length() + 1;
            //value + LF
            size += header.getProperty(key).length();
        }
        return (size);
    }

    /**
     * Adds a message send order to the queue, this could also include an MDN
     *
     */
    private void addSendOrder(AS2Message message, Partner receiver, Partner sender) throws Exception {
        SendOrder order = new SendOrder();
        order.setReceiver(receiver);
        order.setMessage(message);
        order.setSender(sender);
        SendOrderSender orderSender = new SendOrderSender(this.dbDriverManager);
        orderSender.send(order);
        this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
    }

    /**
     * A communication connection indicates that a new message arrived
     */
    private IncomingMessageResponse newMessageArrived(IncomingMessageRequest requestObject) throws Throwable {
        IncomingMessageResponse responseObject = new IncomingMessageResponse(requestObject);
        //is this an AS2 request? It should have a as2-to and as2-from header
        if (requestObject.getHeader().getProperty("as2-to") == null) {
            this.logger.log(Level.SEVERE, this.rb.getResourceString("invalid.request.to"));
            responseObject.setMDNData(null);
            responseObject.setHttpReturnCode(HttpServletResponse.SC_BAD_REQUEST);
            return (responseObject);
        }
        if (requestObject.getHeader().getProperty("as2-from") == null) {
            this.logger.log(Level.SEVERE, this.rb.getResourceString("invalid.request.from"));
            responseObject.setMDNData(null);
            responseObject.setHttpReturnCode(HttpServletResponse.SC_BAD_REQUEST);
            return (responseObject);
        }
        AS2MessageParser parser = new AS2MessageParser();
        parser.setCertificateManager(this.certificateManagerEncSign, this.certificateManagerEncSign);
        parser.setDBConnection(this.dbDriverManager);
        parser.setLogger(this.logger);
        byte[] incomingMessageData = Files.readAllBytes(Paths.get(requestObject.getMessageDataFilename()));
        //store raw incoming message. If the message partners are identified successfully
        //the raw data is also written to the partner dir/raw
        String[] rawFiles = this.messageStoreHandler.storeRawIncomingData(
                incomingMessageData, requestObject.getHeader(),
                requestObject.getRemoteHost());
        String rawIncomingFile = rawFiles[0];
        String rawIncomingFileHeader = rawFiles[1];
        AS2Message message = null;
        try {
            //this will throw an exception if any of the partners are unknown or the local station
            //is not the receiver or the content MIC does not match or the signature does not match. 
            //Anyway every message should be logged
            message = parser.createMessageFromRequest(incomingMessageData,
                    requestObject.getHeader(), requestObject.getContentType());
            message.getAS2Info().setUsesTLS(requestObject.usesTLS());
            message.getAS2Info().setRawFilename(rawIncomingFile);
            message.getAS2Info().setHeaderFilename(rawIncomingFileHeader);
            message.getAS2Info().setSenderHost(requestObject.getRemoteHost());
            message.getAS2Info().setDirection(AS2MessageInfo.DIRECTION_IN);
            //found a message without message id: stop processing
            if (!message.isMDN() && message.getAS2Info().getMessageId() == null) {
                this.logger.log(Level.SEVERE, this.rb.getResourceString("invalid.request.messageid"));
                responseObject.setMDNData(null);
                responseObject.setHttpReturnCode(HttpServletResponse.SC_BAD_REQUEST);
                return (responseObject);
            }
            //its a CEM: check data integrity before returning an MDN
            if (!message.isMDN()) {
                AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
                if (requestObject.getHeader().getProperty("disposition-notification-options") != null) {
                    messageInfo.setDispositionNotificationOptions(
                            new DispositionNotificationOptions(requestObject.getHeader().getProperty("disposition-notification-options")));
                } else {
                    messageInfo.setDispositionNotificationOptions(new DispositionNotificationOptions(""));
                }
                if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
                    CEMReceiptController cemReceipt = new CEMReceiptController(
                            this.clientserver, this.dbDriverManager,
                            this.certificateManagerEncSign);
                    cemReceipt.checkInboundCEM(message);
                }
                this.messageAccess.initializeOrUpdateMessage(messageInfo);
            } else {
                //it is a MDN
                this.mdnAccess.initializeOrUpdateMDN((AS2MDNInfo) message.getAS2Info());
            }
            //inbound message was an sync or async MDN
            if (message.isMDN()) {
                AS2MDNInfo mdnInfo = (AS2MDNInfo) message.getAS2Info();
                this.messageAccess.setMessageState(mdnInfo.getRelatedMessageId(),
                        mdnInfo.getState());
                //ASYNC/SYNC MDN received: insert an entry into the statistic table that a message has been sent
                QuotaAccessDB.incSentMessages(this.dbDriverManager,
                        mdnInfo.getReceiverId(),
                        mdnInfo.getSenderId(), mdnInfo.getState(), mdnInfo.getRelatedMessageId());
            }
            this.updatePartnerSystemInfo(requestObject.getHeader());
            this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
        } catch (AS2Exception e) {
            //exec on MDN send makes no sense here because no valid filename exists
            AS2Info as2Info = e.getAS2Message().getAS2Info();
            as2Info.setUsesTLS(requestObject.usesTLS());
            as2Info.setRawFilename(rawIncomingFile);
            as2Info.setHeaderFilename(rawIncomingFileHeader);
            as2Info.setState(AS2Message.STATE_STOPPED);
            as2Info.setDirection(AS2MessageInfo.DIRECTION_IN);
            as2Info.setSenderHost(requestObject.getRemoteHost());
            if (!as2Info.isMDN()) {
                AS2MessageInfo as2MessageInfo = (AS2MessageInfo) as2Info;
                //always ensure the disposition-notification-options are set for the message else the indicator if the
                //answer should be signed or not is missing
                if (requestObject.getHeader().getProperty("disposition-notification-options") != null) {
                    as2MessageInfo.setDispositionNotificationOptions(
                            new DispositionNotificationOptions(requestObject.getHeader().getProperty("disposition-notification-options")));
                } else {
                    as2MessageInfo.setDispositionNotificationOptions(new DispositionNotificationOptions(""));
                }
                if (as2MessageInfo.getSenderId() != null && as2MessageInfo.getReceiverId() != null) {
                    //this has to be performed because of the notification                    
                    this.messageAccess.initializeOrUpdateMessage(as2MessageInfo);
                    this.messageAccess.setMessageState(as2MessageInfo.getMessageId(), AS2Message.STATE_STOPPED);
                    if (((AS2MessageInfo) as2Info).requestsSyncMDN()) {
                        //SYNC MDN received with error: insert an entry into the statistic table that a message has been sent
                        QuotaAccessDB.incReceivedMessages(this.dbDriverManager,
                                as2Info.getReceiverId(),
                                as2Info.getSenderId(),
                                as2Info.getState(),
                                as2Info.getMessageId());
                    }
                }
                throw e;
            } else {
                AS2MDNInfo mdnInfo = (AS2MDNInfo) as2Info;
                //if its a MDN set the state of the whole transaction
                AS2MessageInfo relatedMessageInfo = this.messageAccess.getLastMessageEntry(mdnInfo.getRelatedMessageId());
                if (relatedMessageInfo != null) {
                    relatedMessageInfo.setState(AS2Message.STATE_STOPPED);
                    mdnInfo.setState(AS2Message.STATE_STOPPED);
                    this.mdnAccess.initializeOrUpdateMDN(mdnInfo);
                    this.messageAccess.setMessageState(mdnInfo.getRelatedMessageId(), AS2Message.STATE_STOPPED);
                    ProcessingEvent.enqueueEventIfRequired(this.dbDriverManager, relatedMessageInfo, mdnInfo);
                    //write status file                    
                    this.messageStoreHandler.writeOutboundStatusFile(relatedMessageInfo);
                    this.logger.log(Level.SEVERE, e.getMessage(), as2Info);
                }
            }
            this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
            //dont't thow an exception here if this is an MDN already, a thrown Exception
            //will result in another MDN!
            if (as2Info.isMDN()) {
                //its a MDN
                AS2MDNInfo mdnInfo = (AS2MDNInfo) as2Info;
                //there is no related message because the original message id of the received MDN does not reference a message?
                AS2MessageInfo originalMessageInfo = this.messageAccess.getLastMessageEntry(mdnInfo.getRelatedMessageId());
                if (originalMessageInfo == null) {
                    //as the related message could not be computed it is helpful to display the location of the inbound files
                    //for further investigation
                    StringBuilder errorMessage = new StringBuilder(e.getMessage());
                    errorMessage.append("\n");
                    errorMessage.append(this.rb.getResourceString("info.mdn.inboundfiles",
                            new Object[]{rawIncomingFile, rawIncomingFileHeader}));
                    this.logger.log(Level.SEVERE, errorMessage.toString());
                } else {
                }
                //an exception occured in processing an inbound MDN, signal back an error to the sender by HTTP code.
                // This will only work for ASYNC MDN because there is a logical problem in sync MDN processing:
                //If a sync mdn could not processed it is impossible to signal this back -> sender and receiver
                //will have different states of processing. Another reason to use ASYNC MDN instead of SYNC MDN
                responseObject.setHttpReturnCode(HttpServletResponse.SC_BAD_REQUEST);
                return (responseObject);
            }
        }
        AS2Info as2Info = message.getAS2Info();
        PartnerAccessDB access = new PartnerAccessDB(this.dbDriverManager);
        Partner messageSender = access.getPartner(as2Info.getSenderId());
        Partner messageReceiver = access.getPartner(as2Info.getReceiverId());
        this.messageStoreHandler.storeParsedIncomingMessage(message, messageReceiver);
        if (!as2Info.isMDN()) {
            this.messageAccess.updateFilenames((AS2MessageInfo) as2Info);
            this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
        }
        //process MDN
        if (message.isMDN()) {
            AS2MDNInfo mdnInfo = (AS2MDNInfo) message.getAS2Info();
            AS2MessageInfo originalMessageInfo = this.messageAccess.getLastMessageEntry(mdnInfo.getRelatedMessageId());
            ProcessingEvent.enqueueEventIfRequired(this.dbDriverManager,
                    originalMessageInfo, mdnInfo);
            //write status file
            MessageStoreHandler handler = new MessageStoreHandler(this.dbDriverManager);
            handler.writeOutboundStatusFile(originalMessageInfo);
        }
        //don't answer on signals or store them
        if (!as2Info.isMDN()) {
            AS2MessageInfo messageInfo = (AS2MessageInfo) message.getAS2Info();
            Partner mdnSender = messageReceiver;
            Partner mdnReceiver = messageSender;
            AS2MDNCreation mdnCreation = new AS2MDNCreation(this.certificateManagerEncSign);
            mdnCreation.setLogger(this.logger);
            //create the MDN that the message has been received; state "processed"
            AS2Message mdn = mdnCreation.createMDNProcessed(messageInfo, mdnSender, mdnReceiver);
            AS2MessageInfo as2RelatedMessageInfo = this.messageAccess.getLastMessageEntry(((AS2MDNInfo) mdn.getAS2Info()).getRelatedMessageId());
            if (messageInfo.requestsSyncMDN()) {
                responseObject.setContentType(mdn.getContentType());
                responseObject.setMDNData(mdn.getRawData());
                //build up the header for the sync response
                Properties header = mdnCreation.buildHeaderForSyncMDN(mdn);
                responseObject.setHeader(header);
                this.messageStoreHandler.storeSentMessage(mdn, mdnSender, mdnReceiver, header);
                this.mdnAccess.initializeOrUpdateMDN((AS2MDNInfo) mdn.getAS2Info());
                //MBean counter: inc the sent data size, this is for sync success MDN
                AS2Server.incRawSentData(this.computeRawHeaderSize(header) + mdn.getRawDataSize());
                this.logger.log(Level.INFO,
                        this.rb.getResourceString("sync.mdn.sent",
                                new Object[]{
                                    ((AS2MDNInfo) mdn.getAS2Info()).getRelatedMessageId()
                                }), mdn.getAS2Info());
                //SYNC MDN sent with state "processed": insert an entry into the statistic table that a message has been received
                QuotaAccessDB.incReceivedMessages(this.dbDriverManager,
                        messageReceiver,
                        messageSender,
                        mdn.getAS2Info().getState(),
                        ((AS2MDNInfo) mdn.getAS2Info()).getRelatedMessageId());
                //on sync MDN the command object is sent back to the servlet, store the payload already as good here
                if (mdn.getAS2Info().getState() == AS2Message.STATE_FINISHED) {
                    this.messageStoreHandler.movePayloadToInbox(messageInfo.getMessageType(),
                            ((AS2MDNInfo) mdn.getAS2Info()).getRelatedMessageId(),
                            messageReceiver, messageSender);
                    //dont execute the command after receipt for CEM
                    if (as2RelatedMessageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
                        CEMReceiptController cemReceipt = new CEMReceiptController(this.clientserver,
                                this.dbDriverManager,
                                this.certificateManagerEncSign);
                        cemReceipt.processInboundCEM(as2RelatedMessageInfo);
                    } else {
                        ProcessingEvent.enqueueEventIfRequired(this.dbDriverManager,
                                as2RelatedMessageInfo, null);
                    }
                }
                this.messageAccess.setMessageState(((AS2MDNInfo) mdn.getAS2Info()).getRelatedMessageId(), mdn.getAS2Info().getState());
                this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
            } else {
                //async MDN requested, dont send MDN in this case
                //process the CEM request if it requires async MDN
                if (as2RelatedMessageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
                    CEMReceiptController cemReceipt = new CEMReceiptController(this.clientserver,
                            this.dbDriverManager,
                            this.certificateManagerEncSign);
                    cemReceipt.processInboundCEM(as2RelatedMessageInfo);
                }
                responseObject.setMDNData(null);
                //async back to sender
                this.addSendOrder(mdn, messageSender, messageReceiver);
            }
        }
        return (responseObject);
    }

    /**
     * Updates the system information for a partner
     */
    private void updatePartnerSystemInfo(Properties header) {
        try {
            PartnerAccessDB access = new PartnerAccessDB(this.dbDriverManager);
            Partner messageSender = access.getPartner(AS2MessageParser.unescapeFromToHeader(header.getProperty("as2-from")));
            if (messageSender != null) {
                PartnerSystem partnerSystem = new PartnerSystem();
                partnerSystem.setPartner(messageSender);
                if (header.getProperty("server") != null) {
                    partnerSystem.setProductName(header.getProperty("server"));
                } else if (header.getProperty("user-agent") != null) {
                    partnerSystem.setProductName(header.getProperty("user-agent"));
                }
                String version = header.getProperty("as2-version");
                if (version != null) {
                    partnerSystem.setAS2Version(version);
                    partnerSystem.setCompression(!version.equals("1.0"));
                }
                String optionalProfiles = header.getProperty("ediint-features");
                if (optionalProfiles != null) {
                    partnerSystem.setMa(optionalProfiles.contains("multiple-attachments"));
                    partnerSystem.setCEM(optionalProfiles.contains("CEM"));
                }
                this.partnerSystemAccess.insertOrUpdatePartnerSystem(partnerSystem);
            }
        } //this feature is really NOT that important to stop an inbound message
        catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
        }
    }

}
