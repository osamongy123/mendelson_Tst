//$Header: /as2/de/mendelson/comm/as2/database/ResourceBundleDBServer.java 18    23/06/22 16:50 Heller $
package de.mendelson.comm.as2.database;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize the mendelson business integration - if you want
 * to localize eagle to your language, please contact us: localize@mendelson.de
 *
 * @author S.Heller
 * @version $Revision: 18 $
 */
public class ResourceBundleDBServer extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"module.name", "[DATABASE]" },
        {"database." + IDBDriverManager.DB_CONFIG, "configuration database" },
        {"database." + IDBDriverManager.DB_RUNTIME, "runtime database" },
        {"dbserver.startup", "Starting embedded DB server.." },
        {"dbserver.running.embedded", "Embedded DB server {0} is running"},
        {"dbserver.running.external", "External DB server {0} is available"},
        {"update.versioninfo", "Automatic database updater: Found database version is {0}"
            + ", the required database version is {1}."},
        {"update.progress", "Incremental updating database ..."},
        {"update.progress.version.start", "Starting {1} update to version {0}..."},
        {"update.progress.version.end", "Updated {1} to version {0}."},
        {"update.error.hsqldb", "FATAL: Impossible to update database "
            + " from version {0} to {1}.\n"
            + "Please delete the entire database by deleting"
            + " the related AS2_DB_*.* database files in the install directory.\n"
            + "After this all your user defined data will be lost."},
        {"update.error.postgres", "FATAL: Impossible to update database "
            + " from version {0} to {1}.\n"
            + "Please enter pgAdmin and delete the database. An update seems to be impossible."},
        {"update.error.mysql", "FATAL: Impossible to update database "
            + " from version {0} to {1}.\n"
            + "Please enter MySQLWorkbench and delete the database. An update seems to be impossible."},
        {"update.error.oracledb", "FATAL: Impossible to update database "
            + " from version {0} to {1}.\n"
            + "Please enter the Oracle SQL Developer and delete the database. An update seems to be impossible."},
        {"update.successfully", "{0}: Update to the necessary version has been finished successfully."},
        {"update.notfound", "For the update, the file update{0}to{1}.sql and/or "
            + "Update{0}to{1}.class must exists in the (resource)directory {2}."},
        {"upgrade.required", "An upgrade is required.\nPlease execute as2upgrade.bat or as2upgrade.sh before starting the server."},
        {"dbserver.shutdown", "Database server shut down" },
        {"info.serveridentification", "Server identification: {0}"},
        {"info.jdbc", "JDBC: {0}"},
        {"info.host", "Host: {0}"},
        {"info.clientdriver", "Client driver: {0}"},
        {"info.user", "User: {0}"},
        {"update.error.futureversion", "The system encountered a future version of the {0}. The max supported version is {1} but the found database has the version {2}. It is not possible to work with this database nor modifying it."},            
    };

}
