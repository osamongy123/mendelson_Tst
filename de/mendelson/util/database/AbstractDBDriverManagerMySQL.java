//$Header: /mec_as2/de/mendelson/util/database/AbstractDBDriverManagerMySQL.java 3     2/02/22 13:44 Heller $
package de.mendelson.util.database;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public abstract class AbstractDBDriverManagerMySQL implements IDBDriverManager {
@Override
    public void setTableLockExclusive(Statement b, String a[]) throws SQLException {
        throw new IllegalAccessError();
    }

  
    @Override
    public void setTableLockINSERTAndUPDATE(Statement b, String[] a) throws SQLException {
        throw new IllegalAccessError();
    }

   
    @Override
    public void setTableLockDELETE(Statement b, String[] a) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public void startTransaction(Statement b, String a) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public void commitTransaction(Statement b, String a) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public void rollbackTransaction(Statement a) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public void setTextParameterAsJavaObject(PreparedStatement a, int b, String c) throws SQLException {
        throw new IllegalAccessError();
    }

    @Override
    public String readTextStoredAsJavaObject(ResultSet a, String b) throws Exception {
        throw new IllegalAccessError();
    }

   
    @Override
    public Object readObjectStoredAsJavaObject(ResultSet a, String b) throws Exception {
        throw new IllegalAccessError();
    }

   
    @Override
    public void setObjectParameterAsJavaObject(PreparedStatement a, int b, Object c) throws Exception {
        throw new IllegalAccessError();
    }

    /**
     * Reads a binary object from the database and returns a byte array that
     * contains it. Will return null if the read data was null. Reading and
     * writing binary objects differs relating the used database system
     */
    @Override
    public byte[] readBytesStoredAsJavaObject(ResultSet a, String b) throws Exception {
        throw new IllegalAccessError();
    }


}
