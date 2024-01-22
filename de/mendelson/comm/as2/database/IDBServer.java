//$Header: /as2/de/mendelson/comm/as2/database/IDBServer.java 3     14/06/22 16:02 Heller $
package de.mendelson.comm.as2.database;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface for all DB Servers
 *
 * @author S.Heller
 * @version $Revision: 3 $
 * @since build 70
 */
public interface IDBServer {


    /**Returns the product information of the database*/
    public DBServerInformation getDBServerInformation();

    /**Returns the product information of the database driver*/
    public DBClientInformation getDBClientInformation();
    
    public void ensureServerIsRunning() throws Exception;
    
    /**
     * Sends a shutdown signal to the DB. This makes only sense if the database
     * runs in an embedded thread
     */
    public void shutdown();

}
