//$Header: /as2/de/mendelson/comm/as2/server/AS2Server.java 170   19/10/22 15:33 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.util.httpconfig.server.HTTPServerConfigInfo;
import de.mendelson.Copyright;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.AS2ShutdownThread;
import de.mendelson.comm.as2.cem.CertificateCEMController;
import de.mendelson.comm.as2.configurationcheck.ConfigurationCheckController;
import de.mendelson.comm.as2.configurationcheck.ConfigurationIssue;
import de.mendelson.comm.as2.database.DBClientInformation;
import de.mendelson.comm.as2.database.DBDriverManagerHSQL;
import de.mendelson.comm.as2.database.DBDriverManagerMySQL;
import de.mendelson.comm.as2.database.DBDriverManagerPostgreSQL;
import de.mendelson.comm.as2.database.DBDriverManagerOracleDB;
import de.mendelson.comm.as2.database.DBServerHSQL;
import de.mendelson.comm.as2.database.DBServerInformation;
import de.mendelson.comm.as2.database.DBServerMySQL;
import de.mendelson.comm.as2.database.DBServerOracle;
import de.mendelson.comm.as2.database.DBServerPostgreSQL;
import de.mendelson.comm.as2.log.DBLoggingHandler;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.send.DirPollManager;
import de.mendelson.comm.as2.sendorder.SendOrderAccessDB;
import de.mendelson.comm.as2.sendorder.SendOrderReceiver;
import de.mendelson.comm.as2.timing.CertificateExpireController;
import de.mendelson.comm.as2.timing.FileDeleteController;
import de.mendelson.comm.as2.timing.MDNReceiptController;
import de.mendelson.comm.as2.timing.MessageDeleteController;
import de.mendelson.comm.as2.timing.PostProcessingEventController;
import de.mendelson.comm.as2.timing.StatisticDeleteController;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.clientserver.log.ClientServerLoggingHandler;
import de.mendelson.util.clientserver.user.DefaultPermissionDescription;
import de.mendelson.util.log.DailySubdirFileLoggingHandler;
import de.mendelson.util.log.LogFormatter;
import de.mendelson.util.log.LogFormatterAS2;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.security.cert.KeystoreStorageImplFile;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import de.mendelson.util.systemevents.notification.SystemEventNotificationController;
import de.mendelson.util.systemevents.notification.SystemEventNotificationControllerImplAS2;
import java.io.IOException;
import java.io.Writer;
import java.net.BindException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import org.eclipse.jetty.server.Server;
import de.mendelson.comm.as2.database.IDBServer;
import de.mendelson.comm.as2.ha.ClientLogRefreshController;
import de.mendelson.comm.as2.ha.HAInstanceController;
import de.mendelson.comm.as2.ha.ServerInstanceHA;
import de.mendelson.util.clientserver.ServerHelloMessage;
import de.mendelson.util.clientserver.ServerHelloMessageGenerator;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.log.ANSI;
import de.mendelson.util.log.ConsoleHandlerStdout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.URLConnection;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

/**
 * Class to start the AS2 server
 *
 * @author S.Heller
 * @version $Revision: 170 $
 * @since build 68
 */
public class AS2Server extends AbstractAS2Server implements AS2ServerMBean, ServerHelloMessageGenerator {

    public static final String SERVER_LOGGER_NAME = "de.mendelson.as2.server";
    public static final int CLIENTSERVER_COMM_PORT = 1234;
    public static final Path LOG_DIR;

    static {
        LOG_DIR = Paths.get(System.getProperty("user.dir"), "log");
    }
    private static int transactionCounter = 0;
    private static long rawDataSent = 0;
    private static long rawDataReceived = 0;
    /**
     * Server start time in ms
     */
    private long startTime = 0;
    private final Logger logger = Logger.getLogger(SERVER_LOGGER_NAME);
    /**
     * Product preferences
     */
    private final PreferencesAS2 preferences = new PreferencesAS2();
    /**
     * DB server that is used
     */
    private IDBServer dbServer = null;
    /**
     * Localize the output
     */
    private final MecResourceBundle rb;
    private DirPollManager pollManager = null;
    private ConfigurationCheckController configCheckController = null;
    private CertificateManager certificateManagerEncSign = null;
    private CertificateManager certificateManagerSSL = null;
    private ClientServer clientserver;
    private ClientServerSessionHandlerLocalhost clientServerSessionHandler = null;
    /**
     * Sets if all clients may connect to this server or only clients from the
     * servers host
     */
    private boolean allowAllClients = false;
    private HTTPServerConfigInfo httpServerConfigInfo = null;
    private final DBServerInformation dbServerInformation = new DBServerInformation();
    private final DBClientInformation dbClientInformation = new DBClientInformation();
    /**
     * Indicates that the system is in shutdown process. No Notification etc
     * will be sent anymore in that state
     */
    public static boolean inShutdownProcess = false;
    private final IDBDriverManager dbDriverManager;
    public static final ServerPlugins PLUGINS = new ServerPlugins();
    private SendOrderReceiver sendOrderReceiver = null;
    private final ServerInstanceHA serverInstanceHA = new ServerInstanceHA();
    private final ServerStartupSequence serverStartupSequence = new ServerStartupSequence(this.logger);
    private CertificateCEMController cemController = null;
    private ModuleLockReleaseController lockReleaseController = null;
    private CertificateExpireController expireController = null;
    private PostProcessingEventController eventController = null;
    private HAInstanceController haInstanceController = null;
    private ClientLogRefreshController clientLogRefreshController = null;
    private FileDeleteController fileDeleteController = null;
    private MessageDeleteController logDeleteController = null;
    private MDNReceiptController receiptController = null;
    private StatisticDeleteController statsDeleteController = null;
    private SystemEventNotificationController notificationController = null;
    private Handler loggingHandlerSystemOut = new ConsoleHandlerStdout();

    /**
     * Creates a new AS2 server and starts it
     *
     * @param startHTTPServer Start the integrated HTTP server. Could be
     * disabled to use the receiver servlet in other servlet containers
     * @param allowAllClients Allow client-server connections from other than
     * localhost
     * @param startPlugins Starts the plugins if there are any in the system
     *
     */
    public AS2Server(boolean startHTTPServer, boolean allowAllClients, boolean startPlugins) throws Exception {
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Server.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new Exception("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.startTime = Instant.now().toEpochMilli();
        this.initializeLogger();
        this.logger.info(rb.getResourceString("server.willstart", AS2ServerVersion.getFullProductName()));
        this.logger.info(Copyright.getCopyrightMessage());
        System.setProperty("mendelson.as2.embeddedhttpserver", startHTTPServer?"TRUE":"FALSE");
        this.fireSystemEventServerStartupBegins();        
        this.allowAllClients = allowAllClients;
        PLUGINS.setStartPlugins(startPlugins);
        this.performStartupChecks();
        this.serverStartupSequence.performWork();
        dbDriverManager = getActivatedDBDriverManager();
        this.clientserver = new ClientServer(this.logger, CLIENTSERVER_COMM_PORT);
        this.clientserver.setProductName(AS2ServerVersion.getFullProductName());
        this.initializeServerInstanceHA();
        this.setupClientServerSessionHandler();
        this.start();
        //stop logging to the console here
        this.logger.removeHandler(this.loggingHandlerSystemOut);
        //start the partner poll threads
        this.pollManager.start();
    }

    private void fireSystemEventServerStartupBegins() {
        String subject = this.rb.getResourceString("server.willstart", AS2ServerVersion.getFullProductName());
        String body = this.rb.getResourceString("server.start.details",
                new Object[]{
                    AS2ServerVersion.getFullProductName(),
                    Boolean.toString(System.getProperty("mendelson.as2.embeddedhttpserver", "TRUE").equalsIgnoreCase("TRUE")),
                    Boolean.toString(this.allowAllClients),
                    AS2Tools.getDataSizeDisplay(Runtime.getRuntime().maxMemory()),
                    System.getProperty("java.version"),
                    System.getProperty("user.name")
                });
        SystemEventManagerImplAS2.newEvent(
                SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                subject, body);
    }

    /**
     * Returns the currently activated DB driver manager
     */
    public static final IDBDriverManager getActivatedDBDriverManager() {
        if (PLUGINS.isActivated(ServerPlugins.PLUGIN_POSTGRESQL)) {
            return (DBDriverManagerPostgreSQL.instance());
        } else if (PLUGINS.isActivated(ServerPlugins.PLUGIN_MYSQL)) {
            return (DBDriverManagerMySQL.instance());
        } else if (PLUGINS.isActivated(ServerPlugins.PLUGIN_ORACLE_DB)) {
            return (DBDriverManagerOracleDB.instance());
        } else {
            return (DBDriverManagerHSQL.instance());
        }
    }

    /**
     * Informs the event manager that the server is finally running - with or
     * without found configuration issues
     */
    private void fireSystemEventServerRunning(List<ConfigurationIssue> configurationIssues) {
        String subject = this.rb.getResourceString("server.started",
                String.valueOf(System.currentTimeMillis() - this.startTime));
        String body = this.rb.getResourceString("server.start.details",
                new Object[]{
                    AS2ServerVersion.getFullProductName(),
                    Boolean.toString(System.getProperty("mendelson.as2.embeddedhttpserver").equalsIgnoreCase("TRUE")),
                    Boolean.toString(this.allowAllClients),
                    AS2Tools.getDataSizeDisplay(Runtime.getRuntime().maxMemory()),
                    System.getProperty("java.version"),
                    System.getProperty("user.name")
                });
        int severity = SystemEvent.SEVERITY_INFO;
        if (!configurationIssues.isEmpty()) {
            StringBuilder issueListStr = new StringBuilder();
            for (ConfigurationIssue issue : configurationIssues) {
                issueListStr.append("*").append(issue.getSubject());
                if (issue.getDetails() != null && issue.getDetails().trim().length() > 0) {
                    issueListStr.append(" (").append(issue.getDetails()).append(")");
                }
                issueListStr.append("\n");
            }
            severity = SystemEvent.SEVERITY_WARNING;
            if (configurationIssues.size() > 1) {
                body = this.rb.getResourceString("server.started.issues", configurationIssues.size())
                        + "\n"
                        + issueListStr
                        + "\n\n"
                        + body;
            } else {
                body = this.rb.getResourceString("server.started.issue")
                        + "\n"
                        + issueListStr
                        + "\n\n"
                        + body;
            }
        }
        SystemEventManagerImplAS2.newEvent(severity,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_MAIN_SERVER_RUNNING,
                subject, body);
    }

    private void performStartupChecks() throws Exception {
        this.checkLock();
        //check if all ports are available
        AS2ServerResourceCheck resourceCheck = new AS2ServerResourceCheck();
        resourceCheck.performPortCheck();
        resourceCheck.checkCPUCores(this.logger);
        resourceCheck.checkHeap(this.logger);
        BCCryptoHelper helper = new BCCryptoHelper();
        //check if the jurisdiction policy strength package has been installed
        boolean unlimitedStrengthInstalled = helper.performUnlimitedStrengthJurisdictionPolicyTest();
        if (!unlimitedStrengthInstalled) {
            this.logger.severe(this.rb.getResourceString("fatal.limited.strength"));
            String subject
                    = this.rb.getResourceString("fatal.limited.strength");
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    subject, "");
            System.exit(1);
        }
    }

    @Override
    public void start() throws Exception {
        try {
            this.ensureRunningDBServer();
            this.startHTTPServer();
            this.certificateManagerEncSign = new CertificateManager(this.logger);
            KeystoreStorage signEncStorage = new KeystoreStorageImplFile(
                    this.preferences.get(PreferencesAS2.KEYSTORE),
                    this.preferences.get(PreferencesAS2.KEYSTORE_PASS).toCharArray(),
                    KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN,
                    KeystoreStorageImplFile.KEYSTORE_STORAGE_TYPE_PKCS12
            );
            this.certificateManagerEncSign.loadKeystoreCertificates(signEncStorage);
            this.certificateManagerSSL = new CertificateManager(this.logger);
            KeystoreStorage sslStorage = new KeystoreStorageImplFile(
                    this.preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND),
                    this.preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND_PASS).toCharArray(),
                    KeystoreStorageImplFile.KEYSTORE_USAGE_SSL,
                    KeystoreStorageImplFile.KEYSTORE_STORAGE_TYPE_JKS
            );
            this.certificateManagerSSL.loadKeystoreCertificates(sslStorage);
            this.startSendOrderReceiver();
            this.initializeAdditionalLogger();
            //start control threads
            this.receiptController
                    = new MDNReceiptController(this.clientserver, this.dbDriverManager);
            this.receiptController.startMDNCheck();
            this.logDeleteController = new MessageDeleteController(
                    this.clientserver, this.dbDriverManager);
            this.logDeleteController.startAutoDeleteControl();
            this.fileDeleteController = new FileDeleteController();
            this.fileDeleteController.startAutoDeleteControl();
            this.statsDeleteController = new StatisticDeleteController(this.dbDriverManager);
            this.statsDeleteController.startAutoDeleteControl();
            //if there is any process that may modify log entries from outside there has to be a log poll to ensure 
            //all displayed data is synchronized. In HA any other node may add entries, in REST API any call may modify the log, too
            if (PLUGINS.isActivated(ServerPlugins.PLUGIN_HA)
                    || PLUGINS.isActivated(ServerPlugins.PLUGIN_REST_API)) {
                //In HA mode the server process should check if there is a change in the number of transactions
                //and then inform all clients to refresh
                this.clientLogRefreshController
                        = new ClientLogRefreshController(
                                this.dbDriverManager,
                                this.clientserver);
                this.clientLogRefreshController.startHALogRefreshControl();
            }
            this.haInstanceController = new HAInstanceController(this, this.dbDriverManager);
            this.haInstanceController.start();
            this.eventController
                    = new PostProcessingEventController(this.clientserver,                            
                            this.certificateManagerEncSign, 
                            this.dbDriverManager);
            this.eventController.startEventExecution();
            this.pollManager = new DirPollManager(this.certificateManagerEncSign, 
                    this.clientserver, this.dbDriverManager);
            this.configCheckController = new ConfigurationCheckController(
                    this.certificateManagerEncSign,
                    this.certificateManagerSSL,                    
                    this.httpServerConfigInfo, this.pollManager,
                    this.dbDriverManager);
            this.clientServerSessionHandler.addServerProcessing(
                    new AS2ServerProcessing(this.clientserver, this.pollManager,
                            this.certificateManagerEncSign, this.certificateManagerSSL, this.dbDriverManager,
                            this.configCheckController, this.httpServerConfigInfo, this.dbServerInformation,
                            this.dbClientInformation));
            this.expireController = new CertificateExpireController(this.certificateManagerEncSign,
                    this.certificateManagerSSL);
            this.expireController.startCertExpireControl();
            this.configCheckController.start();
            this.lockReleaseController = new ModuleLockReleaseController(this.dbDriverManager);
            this.lockReleaseController.startLockReleaseControl();
            this.cemController = new CertificateCEMController(
                    this.clientserver, this.dbDriverManager, this.certificateManagerEncSign);
            this.cemController.start();
            this.notificationController = new SystemEventNotificationControllerImplAS2(
                    this.getLogger(), 
                    this.dbDriverManager);
            Runtime.getRuntime().addShutdownHook(new AS2ShutdownThread(this.dbServer));
            //listen for inbound client connects
            this.clientserver.start();
            //run the configuration one time
            List<ConfigurationIssue> configurationIssues = this.configCheckController.runOnce();
            for (ConfigurationIssue issue : configurationIssues) {
                StringBuilder issueDetails = new StringBuilder();
                issueDetails.append("(" + issue.getDetails() + ")");
                issueDetails.append("\n\n");
                issueDetails.append(this.html2txt(issue.getHintAsHTML()));
                SystemEventManagerImplAS2.newEvent(
                        SystemEvent.SEVERITY_WARNING,
                        SystemEvent.ORIGIN_SYSTEM,
                        SystemEvent.TYPE_SERVER_CONFIGURATION_CHECK,
                        issue.getSubject(), issueDetails.toString());
            }
            this.fireSystemEventServerRunning(configurationIssues);
            this.logger.info(rb.getResourceString("server.started",
                    String.valueOf(System.currentTimeMillis() - this.startTime)));
        } catch (BindException e) {
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    this.rb.getResourceString("server.startup.failed"),
                    this.rb.getResourceString("bind.exception",
                            new Object[]{e.getMessage(),
                                AS2ServerVersion.getProductName()
                            }));
            //populate the bind exception with some more information for the user
            BindException bindException = new BindException(this.rb.getResourceString("bind.exception",
                    new Object[]{e.getMessage(),
                        AS2ServerVersion.getProductName()
                    }));
            throw bindException;
        } catch (Exception e) {
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    this.rb.getResourceString("server.startup.failed"),
                    e.getMessage());
            throw e;
        }
    }

    private String html2txt(String htmlStr) {
        htmlStr = AS2Tools.replace(htmlStr, "<HTML>", "");
        htmlStr = AS2Tools.replace(htmlStr, "<br>", "\n");
        htmlStr = AS2Tools.replace(htmlStr, "</HTML>", "");
        htmlStr = AS2Tools.replace(htmlStr, "<strong>", "");
        htmlStr = AS2Tools.replace(htmlStr, "</strong>", "");
        return (htmlStr);
    }

    /**
     * Initialize the main logging interface
     */
    private void initializeLogger() {
        this.logger.setLevel(Level.ALL);
        if (this.logger.getParent() != null) {
            Handler[] handlerList = this.logger.getParent().getHandlers();
            for (Handler handler : handlerList) {
                handler.setLevel(Level.ALL);
                if (handler instanceof ConsoleHandler) {
                    handler.setFormatter(new LogFormatter(LogFormatter.FORMAT_CONSOLE));
                }
            }
        }
        Handler[] handlerList = this.logger.getHandlers();
        for (Handler handler : handlerList) {
            handler.setLevel(Level.ALL);
            if (handler instanceof ConsoleHandler) {
                handler.setFormatter(new LogFormatter(LogFormatter.FORMAT_CONSOLE));
            }
        }
        LogFormatter logformatterSystemOut = new LogFormatter(LogFormatter.FORMAT_CONSOLE);
        this.loggingHandlerSystemOut.setFormatter(logformatterSystemOut);
        this.loggingHandlerSystemOut.setLevel(Level.ALL);
        this.logger.addHandler(this.loggingHandlerSystemOut);
        this.logger.setUseParentHandlers(false);
        
    }

    /**
     * Adds additional logging targets, e.g. DB, Client-Server interface, daily
     * log to the existing logger
     */
    private void initializeAdditionalLogger() {
        //send the log info to the attached clients of the client-server framework
        logger.addHandler(new ClientServerLoggingHandler(this.clientServerSessionHandler));
        logger.addHandler(new DBLoggingHandler(dbDriverManager));
        //add file logger that logs in a daily subdir
        logger.addHandler(new DailySubdirFileLoggingHandler(
                AS2Server.LOG_DIR,
                "as2.log", new LogFormatterAS2(LogFormatter.FORMAT_LOGFILE,
                        this.dbDriverManager))
        );
    }

    private void setupClientServerSessionHandler() {
        //set up session handler for incoming client requests
        this.clientServerSessionHandler = new ClientServerSessionHandlerLocalhost(this.logger,
                new String[]{AS2ServerVersion.getFullProductName()}, this.allowAllClients,
                this.preferences.getBoolean(PreferencesAS2.COMMUNITY_EDITION) ? 1 : -1,
                new SystemEventManagerImplAS2()
        );
        this.clientServerSessionHandler.setAnonymousProcessing(new AnonymousProcessingAS2());
        this.clientserver.setSessionHandler(this.clientServerSessionHandler);
        this.clientServerSessionHandler.setProductName(AS2ServerVersion.getProductName());
        this.clientServerSessionHandler.setServerHelloMessageGenerator(this);
        this.clientServerSessionHandler.setPermissionDescription(new DefaultPermissionDescription());
    }

    @Override
    public Logger getLogger() {
        return (this.logger);
    }

    /**
     * Checks for a lock to prevent starting the server several times on the
     * same machine
     *
     */
    private void checkLock() {
        //check if lock file exists, if it exists cancel!
        Path lockFile = Paths.get(AS2ServerVersion.getProductName().replace(' ', '_') + ".lock");
        if (Files.exists(lockFile)) {
            long lastModificationTime = System.currentTimeMillis();
            try {
                lastModificationTime = Files.getLastModifiedTime(lockFile).toMillis();
            } catch (IOException e) {
                //nop
            }
            DateFormat format = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            this.logger.severe(rb.getResourceString("server.already.running",
                    new Object[]{
                        lockFile.toAbsolutePath().toString(),
                        format.format(new java.util.Date(lastModificationTime))
                    }));
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN,
                    this.rb.getResourceString("server.startup.failed"),
                    rb.getResourceString("server.already.running",
                            new Object[]{
                                lockFile.toAbsolutePath().toString(),
                                format.format(new java.util.Date(lastModificationTime))
                            }));
            throw new ServerAlreadyRunningException(rb.getResourceString("server.already.running",
                    new Object[]{
                        lockFile.toAbsolutePath().toString(),
                        format.format(new java.util.Date(lastModificationTime))
                    }));
        } else {
            //write the lock file
            Writer writer = null;
            try {
                writer = Files.newBufferedWriter(lockFile);
                writer.write("");
            } catch (Exception e) {
                this.logger.severe("Problem writing the lock file: [" + e.getClass().getName() + "]: " + e.getMessage());
                System.exit(1);
            } finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    } catch (Exception e) {
                        //nop
                    }
                }
            }

        }
    }

    /**
     * This starts the poll process that listens to the database queue for new
     * data to send
     *
     * @throws Exception
     */
    private void startSendOrderReceiver() throws Exception {
        //reset the send order state of available send orders back to waiting
        SendOrderAccessDB sendOrderAccess = new SendOrderAccessDB(
                this.dbDriverManager);
        sendOrderAccess.resetAllToWaiting();
        this.sendOrderReceiver = new SendOrderReceiver(this.clientserver, this.dbDriverManager);
        this.sendOrderReceiver.execute();
    }

    /**
     * Starts the embedded web server if requested
     */
    private Server startHTTPServer() throws Exception {
        //start the HTTP server if this is requested
        if (System.getProperty("mendelson.as2.embeddedhttpserver", "TRUE").equalsIgnoreCase("TRUE")) {
            JettyStarter starter = new JettyStarter(this.logger);
            starter.startWebserver();
            this.httpServerConfigInfo = starter.getHttpServerConfigInfo();
        } else {
            this.logger.info(rb.getResourceString("server.nohttp"));
        }
        return (null);
    }

    /**
     * Starts the database server
     */
    private void ensureRunningDBServer() throws Exception {
        //start the database server and ensure it is running
        if (PLUGINS.isActivated(ServerPlugins.PLUGIN_POSTGRESQL)) {
            this.dbServer = new DBServerPostgreSQL(this.dbDriverManager, this.dbServerInformation,
                    this.dbClientInformation);
        } else if (PLUGINS.isActivated(ServerPlugins.PLUGIN_MYSQL)) {
            this.dbServer = new DBServerMySQL(this.dbDriverManager, this.dbServerInformation,
                    this.dbClientInformation);
        } else if (PLUGINS.isActivated(ServerPlugins.PLUGIN_ORACLE_DB)) {
            this.dbServer = new DBServerOracle(this.dbDriverManager, this.dbServerInformation,
                    this.dbClientInformation);
        } else {
            this.dbServer = new DBServerHSQL(this.dbDriverManager, this.dbServerInformation,
                    this.dbClientInformation);
        }
        this.dbServer.ensureServerIsRunning();
    }

    @Override
    public int getPort() {
        return (CLIENTSERVER_COMM_PORT);
    }

    /**
     * MBean interface
     */
    @Override
    public long getUsedMemoryInBytes() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    /**
     * MBean interface
     */
    @Override
    public long getTotalMemoryInBytes() {
        return (Runtime.getRuntime().totalMemory());
    }

    /**
     * MBean interface
     */
    @Override
    public String getServerVersion() {
        return (AS2ServerVersion.getProductName() + " " + AS2ServerVersion.getVersion() + " " + AS2ServerVersion.getBuild());
    }

    /**
     * MBean interface
     */
    @Override
    public long getUptimeInMS() {
        return (System.currentTimeMillis() - this.startTime);
    }

    @Override
    public long getRawDataSentInBytesInUptime() {
        return (rawDataSent);
    }

    @Override
    public long getRawDataReceivedInBytesInUptime() {
        return (rawDataReceived);
    }

    @Override
    public long getTransactionCountInUptime() {
        return (transactionCounter);
    }

    public static synchronized void incTransactionCounter() {
        transactionCounter++;
    }

    public static synchronized void incRawSentData(long size) {
        rawDataSent += size;
    }

    public static synchronized void incRawReceivedData(long size) {
        rawDataReceived += size;
    }

    /**
     * Deletes the lock file
     */
    public static void deleteLockFile() {
        Path lockFile = Paths.get(AS2ServerVersion.getProductName().replace(' ', '_') + ".lock");
        try {
            Files.delete(lockFile);
        } catch (Exception e) {
            //nop
        }
    }

    /**
     * Returns the new calculated server HA instance. The main data should just
     * performed once as this is expensive
     */
    private void initializeServerInstanceHA() {
        this.serverInstanceHA.setProductVersion(AS2ServerVersion.getFullProductName());
        this.serverInstanceHA.setUniqueId(ServerInstance.ID);
        this.serverInstanceHA.setStartTime(this.startTime);
        this.serverInstanceHA.setOS(System.getProperty("os.name")
                + " " + System.getProperty("os.version")
                + " " + System.getProperty("os.arch"));
        this.serverInstanceHA.setNumberOfClients(this.clientserver.getSessions().size());
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.serverInstanceHA.setLocalIP(inetAddress.getHostAddress());
        } catch (Exception e) {
            //nop
        }
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        this.serverInstanceHA.setHost(runtimeBean.getName());
        //try to figure out if this instance runs on aws - then add some additional values that are conditional
        String publicIP = this.retrieveAWSValue("public-ipv4");
        this.serverInstanceHA.setPublicIP(publicIP);
        String cloudInstanceId = this.retrieveAWSValue("instance-id");
        this.serverInstanceHA.setCloudInstanceId(cloudInstanceId);
    }

    /**
     * Returns the new calculated server HA instance. The main data should just
     * performed once as this is expensive
     */
    public ServerInstanceHA getServerInstanceHA() {
        this.serverInstanceHA.setNumberOfClients(this.clientserver.getSessions().size());
        return (this.serverInstanceHA);
    }

    /**
     * Will return null if this instance does not run on aws or the value could
     * not be obtained Used keys are: instance-id (AWS instance id) public-ipv4
     * (AWS public IP of this instance)
     *
     * @return
     */
    private String retrieveAWSValue(String key) {
        String ec2Id = null;
        URLConnection ec2Connection = null;
        try {
            String inputLine;
            URL ec2MetaData = new URL("http://169.254.169.254/latest/meta-data/" + key);
            ec2Connection = ec2MetaData.openConnection();
            ec2Connection.setConnectTimeout(2000);
            ec2Connection.setReadTimeout(2000);
            ec2Connection.setAllowUserInteraction(false);
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(ec2Connection.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    ec2Id = inputLine;
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (Throwable e) {
        }
        return ec2Id;
    }

    /**
     * Makes this a ServerHelloMessageGenerator
     */
    @Override
    public List<ServerHelloMessage> generateServerHelloMessages() {
        List<ServerHelloMessage> serverHelloList = new ArrayList<ServerHelloMessage>();
        serverHelloList.add(
                new ServerHelloMessage(this.rb.getResourceString("server.hello",
                        AS2ServerVersion.getFullProductName())));
        if (AS2Server.PLUGINS.licenseWillExpire()) {
            if (AS2Server.PLUGINS.getLicenseExpiresInDays() == 1) {
                serverHelloList.add(
                        new ServerHelloMessage(this.rb.getResourceString("server.hello.licenseexpire.single",
                                new Object[]{
                                    "1",
                                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                            .format(AS2Server.PLUGINS.getLicenseExpireDate())
                                }), ServerHelloMessage.LEVEL_SEVERE));
            } else {
                serverHelloList.add(new ServerHelloMessage(this.rb.getResourceString("server.hello.licenseexpire",
                        new Object[]{
                            String.valueOf(AS2Server.PLUGINS.getLicenseExpiresInDays()),
                            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                    .format(AS2Server.PLUGINS.getLicenseExpireDate())
                        }), ServerHelloMessage.LEVEL_SEVERE));
            }
        }
        return (serverHelloList);
    }
}
