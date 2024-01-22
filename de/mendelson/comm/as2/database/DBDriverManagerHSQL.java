//$Header: /as2/de/mendelson/comm/as2/database/DBDriverManagerHSQL.java 27    22/11/22 10:51 Heller $
package de.mendelson.comm.as2.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.AbstractDBDriverManagerHSQL;
import de.mendelson.util.database.DebuggableConnection;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Class needed to access the database
 *
 * @author S.Heller
 * @version $Revision: 27 $
 */
public class DBDriverManagerHSQL extends AbstractDBDriverManagerHSQL implements IDBDriverManager, ISQLQueryModifier {

    public static final boolean DEBUG = false;
    private final static Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final static MecResourceBundle rb;
    private final static String MODULE_NAME;
    static{
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDBDriverManager.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        MODULE_NAME = rb.getResourceString( "module.name");
    }  
    public static final PreferencesAS2 preferences = new PreferencesAS2();
    private final static String DB_USER_NAME = "sa";
    public final static String DB_PASSWORD = "as2dbadmin";
    public static final boolean USE_CONNECTION_POOLING = true;

    private final HikariConfig configConnectionPoolConfig = new HikariConfig();
    private final HikariConfig configConnectionPoolRuntime = new HikariConfig();
    private static HikariDataSource configDatasource = null;
    private static HikariDataSource runtimeDatasource = null;    

    static {
        //register db driver
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (Throwable e) {
            throw new RuntimeException("Unable to register database driver for HSQL database - ["
                    + e.getClass().getSimpleName() + "] " + e.getMessage());
        }        
    }

    /**
     * keeps this as singleton for the whole server instance
     */
    private static DBDriverManagerHSQL instance;
    /**
     * Singleton for the whole application. Looks uncommon but uses the double
     * checked method for higher performance - in this case the method is not
     * needed to be synchronized
     */
    public static DBDriverManagerHSQL instance() {
        if (instance == null) {
            synchronized (DBDriverManagerHSQL.class) {
                if (instance == null) {
                    instance = new DBDriverManagerHSQL();
                }
            }
        }
        return (instance);
    }

    private DBDriverManagerHSQL() {
    }
    
    /**
     * Setup the driver manager, initialize the connection pool
     *
     */
    @Override
    public synchronized void setupConnectionPool() {
        if (USE_CONNECTION_POOLING && configDatasource == null) {
            this.configConnectionPoolConfig.setJdbcUrl(this.getConnectionURI("localhost", DB_CONFIG));
            this.configConnectionPoolConfig.setUsername(DB_USER_NAME);
            this.configConnectionPoolConfig.setPassword(DB_PASSWORD);
            this.configConnectionPoolConfig.setPoolName(this.getDBName(DB_CONFIG));
            configDatasource = new HikariDataSource(this.configConnectionPoolConfig);

            this.configConnectionPoolRuntime.setJdbcUrl(this.getConnectionURI("localhost", DB_RUNTIME));
            this.configConnectionPoolRuntime.setUsername(DB_USER_NAME);
            this.configConnectionPoolRuntime.setPassword(DB_PASSWORD);
            this.configConnectionPoolRuntime.setPoolName(this.getDBName(DB_RUNTIME));
            runtimeDatasource = new HikariDataSource(this.configConnectionPoolRuntime);
        }
    }

    /**
     * shutdown the connection pool
     */
    @Override
    public void shutdownConnectionPool() throws SQLException {
        if (USE_CONNECTION_POOLING) {
            configDatasource.close();
            runtimeDatasource.close();
        }
    }

    /**
     * Returns the URI to connect to
     */
    private String getConnectionURI(String host, final int DB_TYPE) {
        return ("jdbc:hsqldb:hsql://" + host + ":" + DBServerHSQL.DB_PORT + "/" + this.getDBAlias(DB_TYPE));
    }

    /**
     * Returns the DB name, depending on the system wide profile name
     */
    public String getDBName(final int DB_TYPE) {
        String name = "AS2_DB";
        if (DB_TYPE == IDBDriverManager.DB_CONFIG) {
            name = name + "_CONFIG";
        } else if (DB_TYPE == IDBDriverManager.DB_RUNTIME) {
            name = name + "_RUNTIME";
        } else if (DB_TYPE != IDBDriverManager.DB_DEPRICATED) {
            throw new RuntimeException("Unknown DB type requested in DBDriverManager.");
        }
        return (name);
    }

    /**
     * Returns the DB name for a special profile
     */
    protected String getDBAlias(final int DB_TYPE) {
        String alias = "";
        if (DB_TYPE == DBDriverManagerHSQL.DB_CONFIG) {
            alias = alias + "config";
        } else if (DB_TYPE == DBDriverManagerHSQL.DB_RUNTIME) {
            alias = alias + "runtime";
        } else if (DB_TYPE != IDBDriverManager.DB_DEPRICATED) {
            throw new RuntimeException("Unknown DB type requested in DBDriverManager.");
        }
        return (alias);
    }

    /**
     * Creates a new locale database
     *
     * @return true if it was created successfully
     * @param DB_TYPE of the database that should be created, as defined in this
     * class
     */
    @Override
    public boolean createDatabase(final int DB_TYPE) throws Exception {
        try {
            // It will be create automatically if it does not yet exist
            // the given files in the URL is the name of the database
            // "sa" is the user name and "" is the (empty) password            
            String createResource = null;
            int dbVersion = 0;
            if (DB_TYPE == DBDriverManagerHSQL.DB_CONFIG) {
                dbVersion = AS2ServerVersion.getRequiredDBVersionConfig();
                createResource = SQLScriptExecutor.SCRIPT_RESOURCE_CONFIG;
            } else if (DB_TYPE == DBDriverManagerHSQL.DB_RUNTIME) {
                dbVersion = AS2ServerVersion.getRequiredDBVersionRuntime();
                createResource = SQLScriptExecutor.SCRIPT_RESOURCE_RUNTIME;
            } else if (DB_TYPE != DBDriverManagerHSQL.DB_DEPRICATED) {
                throw new RuntimeException("Unknown DB type requested in DBDriverManager.");
            }
            logger.info(MODULE_NAME + " " + rb.getResourceString("creating.database." + DB_TYPE));
            Connection connection = null;
            try {
                connection = DriverManager.getConnection("jdbc:hsqldb:" + this.getDBName(DB_TYPE),
                        "sa", "");
                Statement statement = null;
                try {
                    statement = connection.createStatement();
                    statement.execute("ALTER USER " + DB_USER_NAME.toUpperCase() + " SET PASSWORD '" + DB_PASSWORD + "'");
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
                SQLScriptExecutor executor = new SQLScriptExecutor();
                executor.create(connection, createResource, dbVersion);
            } catch (Exception e) {
                throw new Exception(rb.getResourceString("database.creation.failed." + DB_TYPE)
                        + " [" + e.getMessage() + "]");
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
            logger.info(MODULE_NAME + " " + rb.getResourceString("database.creation.success." + DB_TYPE));
            SystemEvent event = new SystemEvent(
                    SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_CREATION);
            event.setSubject(rb.getResourceString("database.creation.success." + DB_TYPE));
            SystemEventManagerImplAS2.newEvent(event);
        } catch (Throwable e) {
            Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).severe(rb.getResourceString("database.creation.failed." + DB_TYPE)
                    + " [" + e.getMessage() + "]");
            SystemEvent event = new SystemEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DATABASE_CREATION);
            event.setSubject(
                    rb.getResourceString("database.creation.failed." + DB_TYPE));
            String message = e.getMessage();
            if (message == null) {
                message = "[" + e.getClass().getSimpleName() + "]";
            }
            event.setBody(message);
            SystemEventManagerImplAS2.newEvent(event);
            throw e;
        }
        return (true);
    }

    /**
     * Connects to a local database called "MecDriverManager.DB_NAME"
     */
    protected Connection getLocalConnection(final int DB_TYPE) {
        return (getConnection(DB_TYPE));
    }

    /**
     * Returns a connection to the database
     */
    protected Connection getConnection(final int DB_TYPE) {
        try {
            return (getConnectionWithoutErrorHandling(DB_TYPE));
        } catch (SQLException e) {
            Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).severe(e.getMessage());
        }
        return (null);
    }

    /**
     * Returns a connection to the database
     *
     * @param DB_TYPE of the database that should be created, as defined in this
     * class
     */
    @Override
    public synchronized Connection getConnectionWithoutErrorHandling(final int DB_TYPE)
            throws SQLException {
        Connection connection = null;        
        if (DB_TYPE == DB_RUNTIME) {
            if (this.runtimeDatasource != null && this.runtimeDatasource.getHikariPoolMXBean().getIdleConnections() > 0) {
                connection = this.runtimeDatasource.getConnection();
            } else {
                connection = DriverManager.getConnection(
                        getConnectionURI("localhost", DB_TYPE),
                        DB_USER_NAME, DB_PASSWORD);
            }
        } else if (DB_TYPE == DB_CONFIG) {
            if (this.configDatasource != null && this.configDatasource.getHikariPoolMXBean().getIdleConnections() > 0) {
                connection = this.configDatasource.getConnection();
            } else {
                connection = DriverManager.getConnection(
                        getConnectionURI("localhost", DB_TYPE),
                        DB_USER_NAME, DB_PASSWORD);
            }
        } else if (DB_TYPE == DB_DEPRICATED) {
            //deprecated connection: no pooling
            connection = DriverManager.getConnection(
                    getConnectionURI("localhost", DB_TYPE),
                    DB_USER_NAME, DB_PASSWORD);
        } else {
            throw new RuntimeException("Requested invalid db type in getConnectionWithoutErrorHandling");
        }
        connection.setReadOnly(false);
        connection.setAutoCommit(true);
        return (new DebuggableConnection(connection));
    }

    /**
     * Just used for data migration from the HSQLDB config database to an other
     * one
     */
    public Connection getConnectionFileBased(final int DB_TYPE) throws Exception {
        String configDBName = this.getDBName(DB_TYPE);
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:" + configDBName,
                DB_USER_NAME, DB_PASSWORD);
        return (connection);
    }

    @Override
    public String modifyQuery(String query) {
        return( query );
    }

    /**
     * Returns some connection pool information for debug purpose
     */
    @Override
    public String getPoolInformation(int DB_TYPE) {
        StringBuilder output = new StringBuilder();
        HikariDataSource datasource = null;
        if (DB_TYPE == DB_CONFIG) {
            datasource = this.configDatasource;
            output.append("[CONFIG DB]");
        } else {
            datasource = this.runtimeDatasource;
            output.append("[RUNTIME DB]");
        }
        if (!USE_CONNECTION_POOLING || datasource == null) {
            output.append("No connection pooling");
        } else {
            int activeConnections = datasource.getHikariPoolMXBean().getActiveConnections();
            int totalConnections = datasource.getHikariPoolMXBean().getTotalConnections();
            int idleConnections = datasource.getHikariPoolMXBean().getIdleConnections();
            output.append(" Total [").append(String.valueOf(totalConnections)).append("]");
            output.append(" Active [").append(String.valueOf(activeConnections)).append("]");
            output.append(" Idle [").append(String.valueOf(idleConnections)).append("]");
        }
        return (output.toString());
    }
    
}
