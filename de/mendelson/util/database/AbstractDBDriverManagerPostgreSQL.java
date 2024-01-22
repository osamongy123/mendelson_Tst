//$Header: /mec_as4/de/mendelson/util/database/AbstractDBDriverManagerPostgreSQL.java 3     6.01.22 9:25 Heller $
package de.mendelson.util.database;

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
 * Class needed to access the database
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public abstract class AbstractDBDriverManagerPostgreSQL implements IDBDriverManager {
    @Override
    public void setTableLockExclusive(Statement statement, String tablenames[]) throws SQLException {
        throw new IllegalAccessError();
    }

  
    @Override
    public void setTableLockINSERTAndUPDATE(Statement statement, String[] tablenames) throws SQLException {
        throw new IllegalAccessError();
    }

   
    @Override
    public void setTableLockDELETE(Statement statement, String[] tablenames) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public void startTransaction(Statement statement, String transactionName) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public void commitTransaction(Statement statement, String transactionName) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public void rollbackTransaction(Statement statement) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public void setTextParameterAsJavaObject(PreparedStatement statement, int index, String text) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public String readTextStoredAsJavaObject(ResultSet result, String columnName) throws Exception {
        throw new IllegalAccessError();
    }

   
    @Override
    public Object readObjectStoredAsJavaObject(ResultSet result, String columnName) throws Exception {
        throw new IllegalAccessError();
    }

   
    @Override
    public void setObjectParameterAsJavaObject(PreparedStatement statement, int index, Object obj) throws Exception {
        throw new IllegalAccessError();
    }

    /**
     * Reads a binary object from the database and returns a byte array that
     * contains it. Will return null if the read data was null. Reading and
     * writing binary objects differs relating the used database system
     */
    @Override
    public byte[] readBytesStoredAsJavaObject(ResultSet result, String columnName) throws Exception {
        throw new IllegalAccessError();
    }

}
