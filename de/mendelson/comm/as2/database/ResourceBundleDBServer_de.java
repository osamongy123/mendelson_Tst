//$Header: /as2/de/mendelson/comm/as2/database/ResourceBundleDBServer_de.java 18    23/06/22 16:50 Heller $
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
public class ResourceBundleDBServer_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"module.name", "[DATENBANK]" },
        {"database." + IDBDriverManager.DB_CONFIG, "Konfigurationsdatenbank" },
        {"database." + IDBDriverManager.DB_RUNTIME, "Laufzeitdatenbank" },
        {"dbserver.startup", "Starte integrierten DB Server.." },
        {"dbserver.running.embedded", "Integrierter DB Server {0} läuft"},
        {"dbserver.running.external", "Externer DB Server {0} ist verfügbar"},
        {"update.versioninfo", "Automatisches Datenbankupdate: Die gefundene Datenbankversion"
            + " ist {0}, die benoetigte ist {1}."},
        {"update.progress", "Inkrementelles Datenbankupdate gestartet..."},
        {"update.progress.version.start", "Beginne Update der {1} auf Version {0}..."},
        {"update.progress.version.end", "Update der {1} auf Version {0} fertig."},
        {"update.error.hsqldb", "FATAL: Es ist nicht möglich, die Datenbank von der Version {0} "
            + " zur Version {1} zu modifizieren.\n"
            + "Bitte löschen Sie alle entsprechenden AS2_DB_*.* Dateien im Installationsverzeichnis.\n"
            + "Dadurch gehen alle benutzerdefinierten Daten verloren."},
        {"update.error.postgres", "FATAL: Es ist nicht möglich, die Datenbank von der Version {0} "
            + " zur Version {1} zu modifizieren.\n"
            + "Bitte starten Sie pgAdmin und löschen die zugehörige Datenbank."},
        {"update.error.mysql", "FATAL: Es ist nicht möglich, die Datenbank von der Version {0} "
            + " zur Version {1} zu modifizieren.\n"
            + "Bitte starten Sie MySQLWorkbench und löschen die zugehörige Datenbank."},
        {"update.error.oracledb", "FATAL: Es ist nicht möglich, die Datenbank von der Version {0} "
            + " zur Version {1} zu modifizieren.\n"
            + "Bitte starten Sie den Oracle SQL Developer löschen Sie die Datenbank."},
        {"update.successfully",
            "{0}: Die Datenbank wurde erfolgreich fuer die notwendige Version modifiziert."},
        {"update.notfound", "Fuer das Update muss die Datei update{0}to{1}.sql und/oder"
            + " die Datei Update{0}to{1}.class im (Resource)Verzeichnis {2} existieren."},
        {"upgrade.required", "Ein Upgrade muss durchgeführt werden.\nBitte führen Sie die Datei as2upgrade.bat oder as2upgrade.sh aus, bevor Sie den Server starten."},
        {"dbserver.shutdown", "Datenbankserver wurde heruntergefahren" },
        {"info.serveridentification", "Server Identifikation: {0}"},
        {"info.jdbc", "JDBC: {0}"},
        {"info.host", "Host: {0}"},
        {"info.clientdriver", "Client Treiber: {0}"},
        {"info.user", "Benutzer: {0}"},
        {"update.error.futureversion", "Das System hat eine zukünftige Version der {0} vorgefunden. Die mit dieser Version unterstützte Datenbankversion ist {1} aber die gefundene Datenbank hat die Version {2}. Es ist nicht möglich, mit dieser Datenbank weiter zu arbeiten oder sie zu modifizieren."},            
    };
}
