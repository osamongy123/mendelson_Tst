//$Header: /as2/de/mendelson/util/database/IDBDriverManager.java 10    22/11/22 8:22 Heller $
package de.mendelson.util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface for all supported database drivers
 *
 * @author S.Heller
 * @version $Revision: 10 $
 */
public interface IDBDriverManager {

    public static int DB_CONFIG = 1;
    public static int DB_RUNTIME = 2;
    public static int DB_DEPRICATED = 3;
    
    /**
     * Setup the driver manager, initialize the connection pool. It's for some
     * cases important that this method could be called multiple times without setting up
     * the connection pool again (e.g. web interfaces)
     *
     */
    public void setupConnectionPool();

    /**
     * shutdown the connection pool
     */
    public void shutdownConnectionPool() throws SQLException;

    /**
     * Creates a new locale database
     *
     * @return true if it was created successfully
     * @param DB_TYPE of the database that should be created, as defined in this
     * class
     */
    public boolean createDatabase(final int DB_TYPE) throws Exception;

    /**
     * Returns a connection to the database
     *
     * @param DB_TYPE of the database that should be created, as defined in this
     * class
     */
    public Connection getConnectionWithoutErrorHandling(final int DB_TYPE)
            throws SQLException;
    
    /**
     * Returns the SQL statement that is used to lock a table on database level
     * exclusive for a transaction
     *
     * @param tablename The name of the tables to lock exclusive for all access
     * (read/write) operations
     * @return
     */
    public void setTableLockExclusive(Statement statement, String[] tablenames) throws SQLException;

    /**
     * Returns the SQL statement that is used to lock a table on database level
     * for an INSERT or UPDATE operation. The lock level should be that high that 
     * no other session could perform an update or insert operation to the same table(s)
     * meanwhile
     * 
     *
     * @param tablename The name of the tables to lock exclusive for all access
     * (read/write) operations
     * @return
     */
    public void setTableLockINSERTAndUPDATE(Statement statement, String[] tablenames) throws SQLException;

    /**
     * Returns the SQL statement that is used to lock a table on database level
     * for a DELETE operation
     *
     * @param tablename The name of the tables to lock exclusive for all access
     * (read/write) operations
     * @return
     */
    public void setTableLockDELETE(Statement statement, String[] tablenames) throws SQLException;

    /**
     * Starts a transaction. Implementations might do nothing as this concept is
     * database specific. HSQLB has no "BEGIN transaction" concept
     *
     * @param statement
     * @param transactionName
     */
    public void startTransaction(Statement statement, String transactionName) throws SQLException;

    /**
     * Commits a transaction
     *
     * @param transactionName
     */
    public void commitTransaction(Statement statement, String transactionName) throws SQLException;

    
    /**
     * Rollback a transaction
     */
    public void rollbackTransaction(Statement statement) throws SQLException;
    
    
    /**
     * Sets text data as parameter to a stored procedure. The handling depends
     * if the database supports java objects. PostgreSQL for example could not
     * deal with the JDBC type JAVA_OBJECT
     */
    public void setTextParameterAsJavaObject(PreparedStatement statement, int index, String text) throws SQLException;
    
    /**
     * Reads a binary object from the database and returns a String that
     * contains it. Will return null if the read data was null. Reading and
     * writing binary objects differs relating the used database system. 
     * PostgreSQL for example could not deal with the JDBC type JAVA_OBJECT
     */
    public String readTextStoredAsJavaObject(ResultSet result, String columnName) throws Exception;
    
    
    /**
     * Reads a binary object from the database and returns a byte array that
     * contains it. Will return null if the read data was null. Reading and
     * writing binary objects differs relating the used database system
     */
    public Object readObjectStoredAsJavaObject(ResultSet result, String columnName) throws Exception;
    
    /**
     * Sets text data as parameter to a stored procedure. The handling depends
     * if the database supports java objects
     *
     */
    public void setObjectParameterAsJavaObject(PreparedStatement statement, int index, Object obj) throws Exception;
    
    
    /**
     * Reads a binary object from the database and returns a byte array that
     * contains it. Will return null if the read data was null. Reading and
     * writing binary objects differs relating the used database system
     */
    public byte[] readBytesStoredAsJavaObject(ResultSet result, String columnName) throws Exception;
    
    /**Returns some connection pool information for debug purpose*/
    public String getPoolInformation(int DB_TYPE);
    
    /**Adds a limit clause to the SQL query. LIMIT is common but no used by all databases - e.g. not by
     * the oracle database
     * @param query
     * @param maxRows 
     */
    public String addLimitToQuery( String query, int maxRows );
    
}
