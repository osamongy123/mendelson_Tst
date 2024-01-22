//$Header: /as2/de/mendelson/util/database/AbstractDBDriverManagerHSQL.java 7     23/08/22 11:31 Heller $
package de.mendelson.util.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
 * @version $Revision: 7 $
 */
public abstract class AbstractDBDriverManagerHSQL implements IDBDriverManager {

    @Override
    public void setTableLockExclusive(Statement statement, String[] tablenames) throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder.append("LOCK TABLE ");
        for (int i = 0; i < tablenames.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            String tablename = tablenames[i];
            builder.append(tablename + " READ," + tablename + " WRITE");
        }
        statement.execute(builder.toString());
    }

    @Override
    public void setTableLockDELETE(Statement statement, String[] tablenames) throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder.append("LOCK TABLE ");
        for (int i = 0; i < tablenames.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            String tablename = tablenames[i];
            builder.append(tablename + " WRITE");
        }
        statement.execute(builder.toString());
    }

    @Override
    public void setTableLockINSERTAndUPDATE(Statement statement, String[] tablenames) throws SQLException {
        StringBuilder builder = new StringBuilder();
        builder.append("LOCK TABLE ");
        for (int i = 0; i < tablenames.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            String tablename = tablenames[i];
            builder.append(tablename + " WRITE");
        }
        statement.execute(builder.toString());
    }

    @Override
    public void startTransaction(Statement statement, String transactionName) throws SQLException {
        if (statement.getConnection().getAutoCommit()) {
            throw new SQLException("Transaction " + transactionName + " started on database connection that is in auto commit mode");
        }
        //since HSQLDB 2.7.0 it is required to add the isolation level to the START TRANSACTION command
        // - READ COMMITTED was the default isolation level before so this is just added
        statement.execute("START TRANSACTION ISOLATION LEVEL READ COMMITTED");
    }

    @Override
    public void commitTransaction(Statement statement, String transactionName) throws SQLException {
        statement.execute("COMMIT");
    }

    @Override
    public void rollbackTransaction(Statement statement) throws SQLException {
        statement.execute("ROLLBACK");
    }

    @Override
    public void setTextParameterAsJavaObject(PreparedStatement statement, int index, String text) throws SQLException {
        if (text == null) {
            statement.setNull(index, Types.JAVA_OBJECT);
        } else {
            statement.setObject(index, text);
        }
    }

    /**
     * Reads a binary object from the database and returns a byte array that
     * contains it. Will return null if the read data was null. Reading and
     * writing binary objects differs relating the used database system
     */
    @Override
    public String readTextStoredAsJavaObject(ResultSet result, String columnName) throws Exception {
        Object object = result.getObject(columnName);
        if (!result.wasNull()) {
            if (object instanceof String) {
                return (((String) object));
            } else if (object instanceof byte[]) {
                return (new String((byte[]) object));
            }
        }
        return (null);
    }

    /**
     * Reads a binary object from the database and returns a byte array that
     * contains it. Will return null if the read data was null. Reading and
     * writing binary objects differs relating the used database system
     */
    @Override
    public Object readObjectStoredAsJavaObject(ResultSet result, String columnName) throws Exception {
        Object object = result.getObject(columnName);
        if (!result.wasNull()) {
            return (object);
        } else {
            return (null);
        }

    }

    /**
     * Sets text data as parameter to a stored procedure. The handling depends
     * if the database supports java objects
     *
     */
    @Override
    public void setObjectParameterAsJavaObject(PreparedStatement statement, int index, Object obj) throws Exception {
        if (obj == null) {
            statement.setNull(index, Types.JAVA_OBJECT);
        } else {
            statement.setObject(index, obj);
        }
    }

    /**
     * Reads a binary object from the database and returns a byte array that
     * contains it. Will return null if the read data was null. Reading and
     * writing binary objects differs relating the used database system
     */
    @Override
    public byte[] readBytesStoredAsJavaObject(ResultSet result, String columnName) throws Exception {
        Object object = result.getObject(columnName);
        if (!result.wasNull()) {
            return ((byte[]) object);
        }
        return (null);
    }
    
    /**
     * Adds a limit clause to the SQL query. LIMIT is common but no used by all
     * databases - e.g. not by the oracle database
     *
     * @param query
     * @param maxRows
     */
    @Override
    public String addLimitToQuery(String query, int maxRows) {
        return (query + " LIMIT " + maxRows);
    }

}
