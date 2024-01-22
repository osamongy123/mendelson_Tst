//$Header: /as2/de/mendelson/comm/as2/database/migration/clientserver/HSQLDBMigrationVersionMismatchException.java 1     28.07.21 9:43 Heller $
package de.mendelson.comm.as2.database.migration.clientserver;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Exception for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class HSQLDBMigrationVersionMismatchException extends Exception {

    public static final long serialVersionUID = 1L;
    private int requiredVersionConfigDB = -1;
    private int requiredVersionRuntimeDB = -1;
    private int foundVersionRuntimeDB = -1;
    private int foundVersionConfigDB = -1;
    
    public HSQLDBMigrationVersionMismatchException() {
    }

    /**
     * @return the requiredVersionConfigDB
     */
    public int getRequiredVersionConfigDB() {
        return requiredVersionConfigDB;
    }

    /**
     * @param requiredVersionConfigDB the requiredVersionConfigDB to set
     */
    public void setRequiredVersionConfigDB(int requiredVersionConfigDB) {
        this.requiredVersionConfigDB = requiredVersionConfigDB;
    }

    /**
     * @return the requiredVersionRuntimeDB
     */
    public int getRequiredVersionRuntimeDB() {
        return requiredVersionRuntimeDB;
    }

    /**
     * @param requiredVersionRuntimeDB the requiredVersionRuntimeDB to set
     */
    public void setRequiredVersionRuntimeDB(int requiredVersionRuntimeDB) {
        this.requiredVersionRuntimeDB = requiredVersionRuntimeDB;
    }

    /**
     * @return the foundVersionRuntimeDB
     */
    public int getFoundVersionRuntimeDB() {
        return foundVersionRuntimeDB;
    }

    /**
     * @param foundVersionRuntimeDB the foundVersionRuntimeDB to set
     */
    public void setFoundVersionRuntimeDB(int foundVersionRuntimeDB) {
        this.foundVersionRuntimeDB = foundVersionRuntimeDB;
    }

    /**
     * @return the foundVersionConfigDB
     */
    public int getFoundVersionConfigDB() {
        return foundVersionConfigDB;
    }

    /**
     * @param foundVersionConfigDB the foundVersionConfigDB to set
     */
    public void setFoundVersionConfigDB(int foundVersionConfigDB) {
        this.foundVersionConfigDB = foundVersionConfigDB;
    }

}
