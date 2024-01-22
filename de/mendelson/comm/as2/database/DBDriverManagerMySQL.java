//$Header: /mec_as2/de/mendelson/comm/as2/database/DBDriverManagerMySQL.java 5     2/01/23 13:16 Heller $
package de.mendelson.comm.as2.database;

import de.mendelson.util.database.AbstractDBDriverManagerMySQL;
import de.mendelson.util.database.IDBDriverManager;
import java.sql.Connection;
import java.sql.SQLException;

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
 * @version $Revision: 5 $
 */
public class DBDriverManagerMySQL extends AbstractDBDriverManagerMySQL implements IDBDriverManager, ISQLQueryModifier {

    /**
     * keeps this as singleton for the whole server instance
     */
    private static DBDriverManagerMySQL instance;
    /**
     * Singleton for the whole application. Looks uncommon but uses the double
     * checked method for higher performance - in this case the method is not
     * needed to be synchronized
     */
    public static DBDriverManagerMySQL instance() {
        if (instance == null) {
            synchronized (DBDriverManagerMySQL.class) {
                if (instance == null) {
                    instance = new DBDriverManagerMySQL();
                }
            }
        }
        return (instance);
    }
    
    
    /**
     * Returns the version of this class
     */
    public static String getVersion() {
        return ("0");
    }

    /**
     * Setup the driver manager, initialize the connection pool
     *
     */
    @Override
    public synchronized void setupConnectionPool() {
        throw new IllegalAccessError();
    }

    /**
     * shutdown the connection pool
     */
    @Override
    public void shutdownConnectionPool() throws SQLException {
        throw new IllegalAccessError();
    }

    /**
     * Returns the DB name, depending on the system wide profile name
     */
    public String getDBName(final int DB_TYPE) {
        throw new IllegalAccessError();
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
        throw new IllegalAccessError();
    }

    /**
     * Returns a connection to the database
     *
     * @param dummy Unused parameter
     * @param DB_TYPE of the database that should be created, as defined in this
     * class
     */
    @Override
    public Connection getConnectionWithoutErrorHandling(final int DB_TYPE)
            throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public String modifyQuery(String query) {
        throw new IllegalAccessError();
    }

    /**
     * Returns some connection pool information for debug purpose
     */
    @Override
    public String getPoolInformation(int DB_TYPE) {
        throw new IllegalAccessError();
    }

    @Override
    public String addLimitToQuery(String a, int b) {
        throw new IllegalAccessError();
    }
}
