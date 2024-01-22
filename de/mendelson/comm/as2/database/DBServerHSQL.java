//$Header: /as2/de/mendelson/comm/as2/database/DBServerHSQL.java 21    23/11/22 16:50 Heller $
package de.mendelson.comm.as2.database;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.server.UpgradeRequiredException;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerConstants;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Class to start a dedicated SQL database server
 *
 * @author S.Heller
 * @version $Revision: 21 $
 * @since build 70
 */
public class DBServerHSQL implements IDBServer {

    public static final int DB_PORT = 3336;
    /**
     * Resourcebundle to localize messages of the DB server
     */
    private static MecResourceBundle rb;
    /**
     * Log messages
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final static String MODULE_NAME;
    /**
     * Database object
     */
    private Server server = null;
    private final DBDriverManagerHSQL dbDriverManager;
    private DBServerInformation dbServerInformation = new DBServerInformation();
    private DBClientInformation dbClientInformation = new DBClientInformation();

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDBServer.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        MODULE_NAME = rb.getResourceString("module.name");
    }

    /**
     * Start an embedded database server
     */
    public DBServerHSQL(IDBDriverManager driverManager,
            DBServerInformation dbServerInformation,
            DBClientInformation dbClientInformation) throws Exception {
        if (dbServerInformation != null) {
            this.dbServerInformation = dbServerInformation;
        }
        if (dbClientInformation != null) {
            this.dbClientInformation = dbClientInformation;
        }
        this.dbDriverManager = (DBDriverManagerHSQL) driverManager;
        //split up database if its an older version with a single DB
        this.createDeprecatedCheck();
        //check if hsqldb 2.x is used or an older version
        this.checkDBUpgradeRequired();
    }

    private void checkDBUpgradeRequired() throws UpgradeRequiredException, Exception {
        Path propertiesFileConfig = Paths.get(this.dbDriverManager.getDBName(IDBDriverManager.DB_CONFIG) + ".properties");
        Path propertiesFileRuntime = Paths.get(this.dbDriverManager.getDBName(IDBDriverManager.DB_RUNTIME) + ".properties");
        String versionConfig = "";
        String versionRuntime = "";
        if (Files.exists(propertiesFileConfig)) {
            Properties dbProperties = new Properties();
            InputStream inStream = null;
            try {
                inStream = Files.newInputStream(propertiesFileConfig);
                dbProperties.load(inStream);
            } finally {
                if (inStream != null) {
                    inStream.close();
                }
            }
            versionConfig = dbProperties.getProperty("version");
        }
        if (Files.exists(propertiesFileRuntime)) {
            Properties dbProperties = new Properties();
            InputStream inStream = null;
            try {
                inStream = Files.newInputStream(propertiesFileRuntime);
                dbProperties.load(inStream);
            } finally {
                if (inStream != null) {
                    inStream.close();
                }
            }
            versionRuntime = dbProperties.getProperty("version");
        }
        if (versionConfig.startsWith("1") || versionRuntime.startsWith("1")) {
            throw new UpgradeRequiredException(this.rb.getResourceString("upgrade.required"));
        }
    }

    /**
     * Returns the product information of the database
     */
    @Override
    public DBServerInformation getDBServerInformation() {
        return (this.dbServerInformation);
    }

    /**
     * Returns the product information of the driver
     */
    @Override
    public DBClientInformation getDBClientInformation() {
        return (this.dbClientInformation);
    }

    /**
     * Starts an internal DB server with default parameter
     */
    private String startDBServer() throws Exception {
        //The system level property hsqldb.reconfig_logging=false is required to avoid 
        //configuration of java.util.logging. Otherwise configuration takes place.
        System.setProperty("hsqldb.reconfig_logging", "false");
        SystemEventManagerImplAS2.newEvent(
                SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_DATABASE_SERVER_STARTUP_BEGIN,
                this.rb.getResourceString("dbserver.startup"),
                "");
        this.server = new Server();
        this.dbServerInformation.setHost("localhost");
        this.dbServerInformation.setProductName(this.server.getProductName());
        this.dbServerInformation.setProductVersion(this.server.getProductVersion());
        this.dbClientInformation.setProductName(this.server.getProductName());
        this.dbClientInformation.setProductVersion(this.server.getProductVersion());
        //start an internal server        
        this.server.setPort(DB_PORT);
        this.server.setDatabasePath(0, this.dbDriverManager.getDBName(IDBDriverManager.DB_CONFIG));
        this.server.setDatabaseName(0, this.dbDriverManager.getDBAlias(IDBDriverManager.DB_CONFIG));
        this.server.setDatabasePath(1, this.dbDriverManager.getDBName(IDBDriverManager.DB_RUNTIME));
        this.server.setDatabaseName(1, this.dbDriverManager.getDBAlias(IDBDriverManager.DB_RUNTIME));
        HsqlProperties hsqlProperties = new HsqlProperties();
        hsqlProperties.setProperty("hsqldb.cache_file_scale", 128);
        hsqlProperties.setProperty("hsqldb.write_delay", false);
        hsqlProperties.setProperty("hsqldb.write_delay_millis", 0);
        //Database access control: Points to a file that contains the IPs that are allowed to
        //establish connections to the database. The HSQL documentation references this as
        //"HyperSQL Network Listener ACL file"
        //In the default configuration this acl file contains
        //the lines
        //
        //allow localhost
        hsqlProperties.setProperty("server.acl", "database.acl");
        this.server.setProperties(hsqlProperties);
        ByteArrayOutputStream memStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(memStream, StandardCharsets.UTF_8));
        this.server.setLogWriter(printWriter);
        this.server.setErrWriter(printWriter);
        this.server.start();
        memStream.flush();
        memStream.close();
        this.server.setLogWriter(null);
        this.server.setErrWriter(null);
        return (memStream.toString(StandardCharsets.UTF_8.name()));
    }

    @Override
    public void ensureServerIsRunning() throws Exception {
        String startupLog = this.startDBServer();
        try {
            this.createCheck();
        } catch (Exception e) {
            this.logger.severe(MODULE_NAME + " " + e.getMessage());
            throw e;
        }
        try {
            this.defragDB(IDBDriverManager.DB_CONFIG);
        } catch (Exception e) {
            this.logger.warning(MODULE_NAME + " " + e.getMessage());
            startupLog = startupLog + "\n" + "[" + e.getClass().getSimpleName() + "]: " + e.getMessage();
        }
        try {
            this.defragDB(IDBDriverManager.DB_RUNTIME);
        } catch (Exception e) {
            this.logger.warning(MODULE_NAME + " " + e.getMessage());
            startupLog = startupLog + "\n" + "[" + e.getClass().getSimpleName() + "]: " + e.getMessage();
        }
        Connection configConnection = null;
        Connection runtimeConnection = null;
        try {
            configConnection = this.dbDriverManager.getLocalConnection(IDBDriverManager.DB_CONFIG);
            if (configConnection == null) {
                return;
            }
            Statement statement = null;
            try {
                statement = configConnection.createStatement();
                statement.execute("SET FILES SCRIPT FORMAT COMPRESSED");
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
            //check if a DB update is necessary. If so, update the DB
            this.updateDB(IDBDriverManager.DB_CONFIG);
            runtimeConnection = this.dbDriverManager.getLocalConnection(DBDriverManagerHSQL.DB_RUNTIME);
            if (runtimeConnection == null) {
                return;
            }
            statement = null;
            try {
                statement = runtimeConnection.createStatement();
                statement.execute("SET FILES SCRIPT FORMAT COMPRESSED");
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
            //check if a runtime DB update is necessary. If so, update the runtime DB
            this.updateDB(DBDriverManagerHSQL.DB_RUNTIME);
            DatabaseMetaData data = runtimeConnection.getMetaData();
            this.dbServerInformation.setJDBCVersion(data.getJDBCMajorVersion() + "." + data.getJDBCMinorVersion());
            this.logger.info(MODULE_NAME + " " + rb.getResourceString("dbserver.running.embedded",
                    new Object[]{data.getDatabaseProductName() + " " + data.getDatabaseProductVersion()}));
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_SERVER_RUNNING,
                    this.rb.getResourceString("dbserver.running.embedded",
                            new Object[]{
                                data.getDatabaseProductName()
                                + " "
                                + data.getDatabaseProductVersion()
                            }),
                    startupLog);
        } catch (Exception e) {
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_SERVER_RUNNING,
                    this.rb.getResourceString("dbserver.startup"),
                    startupLog + "\n"
                    + "[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
            this.logger.severe(MODULE_NAME + " " + "DBServer.startup: " + e.getMessage());
        } finally {
            if (configConnection != null) {
                try {
                    configConnection.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
            if (runtimeConnection != null) {
                try {
                    runtimeConnection.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
        this.dbDriverManager.setupConnectionPool();
        //wait until the server is up
        while (true) {
            try {
                Connection testConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
                testConnection.close();
                break;
            } catch (Throwable e) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    //nop
                }
            }
        }
    }

    /**
     * Performs a defragmentation of the passed database. This is necessary to
     * keep the database files small
     *
     */
    public void defragDB(final int DB_TYPE) throws Exception {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = this.dbDriverManager.getConnectionWithoutErrorHandling(DB_TYPE);
            statement = connection.createStatement();
            //Automatic Defrag at Checkpoint
            //When a checkpoint is performed, the percentage of wasted space 
            //in the .data file is calculated. If the wasted space is above 
            //the specified limit, a defrag operation is performed. The 
            //default is 0, which means no automatic checkpoint. The numeric 
            //value must be between 0 and 100 and is interpreted as a percentage 
            //of the current size of the .data file. Positive values less than 25 are converted to 25
            statement.execute("SET FILES DEFRAG 25");
            statement.execute("CHECKPOINT DEFRAG");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).warning(e.getMessage());
            }
            try {
                if (statement != null) {
                    connection.close();
                }
            } catch (Exception e) {
                Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).warning(e.getMessage());
            }
        }
    }

    /**
     * Check if db exists and create a new one if it doesnt exist
     */
    private void createCheck() throws Exception {
        if (!this.databaseExists(IDBDriverManager.DB_CONFIG)) {
            //new installation
            this.dbDriverManager.createDatabase(IDBDriverManager.DB_CONFIG);
        }
        if (!this.databaseExists(IDBDriverManager.DB_RUNTIME)) {
            //new installation
            this.dbDriverManager.createDatabase(IDBDriverManager.DB_RUNTIME);
        }
    }

    /**
     * Returns if the passed database type exists
     */
    private boolean databaseExists(int databaseType) {
        String TABLE_NAME = "TABLE_NAME";
        String[] TABLE_TYPES = {"TABLE"};
        boolean databaseFound = false;
        Connection connection = null;
        try {
            connection = this.dbDriverManager.getConnectionWithoutErrorHandling(databaseType);
            if (connection != null) {
                DatabaseMetaData metadata = connection.getMetaData();
                ResultSet tableResultRuntime = metadata.getTables(null, null, null, TABLE_TYPES);
                while (tableResultRuntime.next()) {
                    if (tableResultRuntime.getString(TABLE_NAME).equalsIgnoreCase("version")) {
                        databaseFound = true;
                    }
                }
                connection.close();
            }
        } catch (Exception e) {
            return (databaseFound);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
        return (databaseFound);
    }

    /**
     * Returns the actual db version of the passed type
     */
    private int getActualDBVersion(final int DB_TYPE) {
        int foundVersion = -1;
        Connection connection = null;
        try {
            connection = this.dbDriverManager.getLocalConnection(DB_TYPE);
            Statement statement = null;
            ResultSet result = null;
            try {
                statement = connection.createStatement();
                result = statement.executeQuery("SELECT MAX(actualversion) AS maxversion FROM version");
                if (result.next()) {
                    //value is always in the first column
                    foundVersion = result.getInt("maxversion");
                }
            } catch (Exception e) {
                SystemEventManagerImplAS2.systemFailure(e);
                Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).warning(e.getMessage());
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.systemFailure(e);
                        Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).warning(e.getMessage());
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.systemFailure(e);
                        Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).warning(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
        return (foundVersion);
    }

    /**
     * Update the database if this is necessary.
     *
     * @param connection connection to the database
     * @param DB_TYPE of the database that should be created, as defined in this
     * class MecDriverManager
     */
    private void updateDB(final int DB_TYPE) {
        int requiredDBVersion = -1;
        String dbName = null;
        if (DB_TYPE == IDBDriverManager.DB_CONFIG) {
            dbName = rb.getResourceString("database." + IDBDriverManager.DB_CONFIG);
            requiredDBVersion = AS2ServerVersion.getRequiredDBVersionConfig();
        } else if (DB_TYPE == IDBDriverManager.DB_RUNTIME) {
            dbName = rb.getResourceString("database." + IDBDriverManager.DB_RUNTIME);
            requiredDBVersion = AS2ServerVersion.getRequiredDBVersionRuntime();
        } else if (DB_TYPE != IDBDriverManager.DB_DEPRICATED) {
            throw new RuntimeException("Unknown DB type requested in DBServer:updateDB.");
        }
        int foundVersion = this.getActualDBVersion(DB_TYPE);
        //if the found version is higher than the required this is a fatal problem. The software
        //cannot work with future versions of its database. This could happen if a backup is restored
        //or the system works in HA mode and another node has modified the database
        if (foundVersion != -1 && foundVersion > requiredDBVersion) {
            this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.error.futureversion",
                    new Object[]{
                        rb.getResourceString("database." + DB_TYPE),
                        String.valueOf(requiredDBVersion),
                        String.valueOf(foundVersion)}));
            SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_UPDATE);
            event.setSubject(
                    rb.getResourceString("database." + DB_TYPE));
            event.setBody(rb.getResourceString("update.error.futureversion",
                    new Object[]{
                        rb.getResourceString("database." + DB_TYPE),
                        String.valueOf(requiredDBVersion),
                        String.valueOf(foundVersion)}));
            SystemEventManagerImplAS2.newEvent(event);
            System.exit(-1);
        }
        //check if the found version is lesser than the required version!
        if (foundVersion != -1 && foundVersion < requiredDBVersion) {
            this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.versioninfo",
                    new Object[]{
                        String.valueOf(foundVersion),
                        String.valueOf(requiredDBVersion)
                    }));
            this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.progress"));
            for (int i = foundVersion; i < requiredDBVersion; i++) {
                this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.progress.version.start",
                        new Object[]{String.valueOf(i + 1), dbName}));
                if (!this.startDBUpdate(i, DB_TYPE)) {
                    this.logger.severe(MODULE_NAME + " " + rb.getResourceString("update.error.hsqldb",
                            new Object[]{String.valueOf(i), String.valueOf(i + 1)}));
                    SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_ERROR,
                            SystemEvent.ORIGIN_SYSTEM,
                            SystemEvent.TYPE_DATABASE_UPDATE);
                    event.setSubject(
                            rb.getResourceString("database." + DB_TYPE));
                    event.setBody(rb.getResourceString("update.error.hsqldb",
                            new Object[]{String.valueOf(i), String.valueOf(i + 1)}));
                    SystemEventManagerImplAS2.newEvent(event);
                    System.exit(-1);
                }
                //set new version to the database
                this.setNewDBVersion(DB_TYPE, i + 1);
                int newActualVersion = this.getActualDBVersion(DB_TYPE);
                this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.progress.version.end",
                        new Object[]{String.valueOf(newActualVersion), dbName}));
                SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_INFO,
                        SystemEvent.ORIGIN_SYSTEM,
                        SystemEvent.TYPE_DATABASE_UPDATE);
                event.setSubject(rb.getResourceString("update.successfully", dbName));
                event.setBody(rb.getResourceString("update.progress.version.end",
                        new Object[]{String.valueOf(newActualVersion), dbName}));
                SystemEventManagerImplAS2.newEvent(event);
            }
            this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.successfully", dbName));
        }
    }

    /**
     * Sets the new DB version to the passed number if the update was
     * successfully
     *
     * @param connection DB connection to use
     * @param version new DB version the update has updated to
     */
    private void setNewDBVersion(final int DB_TYPE, int version) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = this.dbDriverManager.getConnectionWithoutErrorHandling(DB_TYPE);
            //request all connections from the database to store them
            statement = connection.prepareStatement(
                    "INSERT INTO version(actualversion,updatedate,updatecomment)VALUES(?,?,?)");
            //fill in values
            statement.setInt(1, version);
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()), Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            statement.setString(3, "by " + AS2ServerVersion.getFullProductName() + " auto updater");
            statement.executeUpdate();
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
            if( connection != null ){
                try {
                    connection.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }            
        }
    }

    /**
     * Sends a shutdown signal to the DB
     */
    @Override
    public void shutdown() {
        try {
            Connection configConnection = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            Connection runtimeConnection = this.dbDriverManager.getConnection(IDBDriverManager.DB_RUNTIME);
            configConnection.createStatement().execute("SHUTDOWN");
            configConnection.close();
            System.out.println("DB server: config DB shutdown complete.");
            runtimeConnection.createStatement().execute("SHUTDOWN");
            runtimeConnection.close();
            System.out.println("DB server: runtime DB shutdown complete.");
        } catch (Exception e) {
            System.out.println("DB server shutdown: " + e.getMessage());
        }
        try {
            this.server.signalCloseAllServerConnections();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            this.dbDriverManager.shutdownConnectionPool();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.server.shutdown();
        while (this.server.getState() != ServerConstants.SERVER_STATE_SHUTDOWN) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
        SystemEventManagerImplAS2.newEvent(
                SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_DATABASE_SERVER_SHUTDOWN,
                this.rb.getResourceString("dbserver.shutdown"),
                "");
        String shutdownMessage = this.rb.getResourceString("dbserver.shutdown");
        System.out.println(shutdownMessage);
    }

    /**
     * Start the DB update from the startVersion to the startVersion+1 -
     * transactional
     *
     * @param startVersion Start version
     * @return true if the update was successful
     * @param DB_TYPE of the database that should be created, as defined in this
     * class MecDriverManager
     */
    private boolean startDBUpdate(int startVersion, final int DB_TYPE) {
        String updateResource = null;
        if (DB_TYPE == IDBDriverManager.DB_CONFIG) {
            updateResource = SQLScriptExecutor.SCRIPT_RESOURCE_CONFIG;
        } else if (DB_TYPE == IDBDriverManager.DB_RUNTIME) {
            updateResource = SQLScriptExecutor.SCRIPT_RESOURCE_RUNTIME;
        } else if (DB_TYPE != IDBDriverManager.DB_DEPRICATED) {
            throw new RuntimeException("Unknown DB type requested in DBServer.");
        }
        //sql file to execute for the update process
        String sqlResource = updateResource + "update" + startVersion + "to" + (startVersion + 1) + ".sql";
        SQLScriptExecutor executor = new SQLScriptExecutor();
        executor.setQueryModifier((ISQLQueryModifier) this.dbDriverManager);
        Connection updateConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        String transactionName = "DB_UPDATE_" + startVersion + "_to_" + (startVersion + 1) + "_DB" + DB_TYPE;
        try {
            //defrag the DB
            this.defragDB(DB_TYPE);
            updateConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(DB_TYPE);
            updateConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = updateConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            try {
                if (executor.resourceExists(sqlResource)) {
                    executor.executeScript(updateConnectionNoAutoCommit, sqlResource);
                } else {
                    //check if a java file should be executed that changes something in
                    //the database. This will happen only if the .sql file has not been found
                    String javaUpdateClass = updateResource.replace('/', '.') + "Update" + startVersion + "to" + (startVersion + 1);
                    if (javaUpdateClass.startsWith(".")) {
                        javaUpdateClass = javaUpdateClass.substring(1);
                    }
                    try {
                        Class cl = Class.forName(javaUpdateClass);
                        IUpdater updater = (IUpdater) cl.getDeclaredConstructor().newInstance();
                        updater.startUpdate(updateConnectionNoAutoCommit);
                        if (!updater.updateWasSuccessfully()) {
                            throw new Exception("Update failed.");
                        }
                    } catch (ClassNotFoundException e) {
                        this.logger.info(MODULE_NAME + " " + "DBServer.startDBUpdate (ClassNotFoundException):" + e);
                        this.logger.info(MODULE_NAME + " " + rb.getResourceString("update.notfound",
                                new Object[]{String.valueOf(startVersion),
                                    String.valueOf(startVersion + 1),
                                    updateResource
                                }));
                        throw new Exception("Update failed.");
                    }
                }
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
                return( true );
            } catch (Throwable e) {
                try {
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.systemFailure(e);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (updateConnectionNoAutoCommit != null) {
                try {
                    updateConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
        return (false);
    }

    /**
     * Split up the DB into a config and a runtime database if this is an AS
     * version where only a single database exists (< end of 2011)
     */
    private void createDeprecatedCheck() throws Exception {
        Path deprecatedFile = Paths.get(this.dbDriverManager.getDBName(DBDriverManagerHSQL.DB_DEPRICATED) + ".script");
        Path configFile = Paths.get(this.dbDriverManager.getDBName(DBDriverManagerHSQL.DB_CONFIG) + ".script");
        Path runtimeFile = Paths.get(this.dbDriverManager.getDBName(DBDriverManagerHSQL.DB_RUNTIME) + ".script");
        //create new Database
        if (Files.exists(deprecatedFile) && !Files.exists(configFile) && !Files.exists(runtimeFile)) {
            this.logger.info("Performing database split into config/runtime database.");
            //update issue, performed on 11/2011: split up deprecated database
            this.copyDeprecatedDatabaseTo(this.dbDriverManager.getDBName(DBDriverManagerHSQL.DB_CONFIG));
            this.copyDeprecatedDatabaseTo(this.dbDriverManager.getDBName(DBDriverManagerHSQL.DB_RUNTIME));
            this.logger.info(MODULE_NAME + " " + "Database structure splitted.");
        }
    }

    /**
     * Splits up the deprecated database into 2 separate databases. The version
     * of these split databases could be any from 0 to 50.
     */
    private void copyDeprecatedDatabaseTo(String targetBase) throws IOException {
        String sourceBase = this.dbDriverManager.getDBName(DBDriverManagerHSQL.DB_DEPRICATED);
        this.copyFile(sourceBase + ".backup", targetBase + ".backup");
        this.copyFile(sourceBase + ".data", targetBase + ".data");
        this.copyFile(sourceBase + ".properties", targetBase + ".properties");
        this.copyFile(sourceBase + ".script", targetBase + ".script");
    }

    private void copyFile(String source, String target) throws IOException {
        Path sourceFile = Paths.get(source);
        Path targetFile = Paths.get(target);
        if (!Files.exists(sourceFile)) {
            return;
        }
        Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
    }
}
