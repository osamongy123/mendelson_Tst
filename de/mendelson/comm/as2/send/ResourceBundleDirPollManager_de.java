//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleDirPollManager_de.java 21    19/01/22 16:17 Heller $
package de.mendelson.comm.as2.send;
import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 * @author S.Heller
 * @version $Revision: 21 $
 */
public class ResourceBundleDirPollManager_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"none", "Keine" },
        {"manager.status.modified", "Die Verzeichnisüberwachung hat Verzeichnisüberwachungen verändert, es werden {0} Verzeichnisse überwacht" },
        {"poll.stopped", "Die Verzeichnisüberwachung für die Beziehung \"{0}/{1}\" wurde gestoppt." },
        {"poll.started", "Die Verzeichnisüberwachung für die Beziehung \"{0}/{1}\" wurde gestartet. Ignoriere: \"{2}\". Intervall: {3}s" },
        {"poll.stopped.notscheduled", "[Verzeichnisüberwachung] Das System versuchte die Verzeichnisüberwachung für \"{0}/{1}\" zu stoppen - es gab aber keine Überwachnung." },
        {"poll.modified", "[Verzeichnisüberwachung] Die Partnereinstellungen für die Beziehung \"{0}/{1}\" wurden verändert." },
        {"warning.noread", "[Verzeichnisüberwachung] Kein Lesezugriff möglich für die Ausgangsdatei {0}, Datei wird ignoriert."},
        {"warning.ro", "[Verzeichnisüberwachung] Die Ausgangsdatei {0} ist schreibgeschützt, diese Datei wird ignoriert." },
        {"warning.notcomplete", "[Verzeichnisüberwachung] Die Ausgangsdatei {0} ist noch nicht vollständig vorhanden, Datei wird ignoriert." },
        {"messagefile.deleted", "Die Datei \"{0}\" wurde gelöscht und der Verarbeitungswarteschlange des Servers übergeben." },
        {"processing.file", "Verarbeite die Datei \"{0}\" für die Beziehung \"{1}/{2}\"." }, 
        {"processing.file.error", "Verarbeitungsfehler der Datei \"{0}\" für die Beziehung \"{1}/{2}\": \"{3}\"." },
        {"poll.log.wait", "[Verzeichnisüberwachung] {0}->{1}: Nächster Pollprozess in {2}s ({3})" },
        {"poll.log.polling", "[Verzeichnisüberwachung] {0}->{1}: Prüfe Verzeichnis \"{2}\" auf neue Dateien"},
        {"title.list.polls.running", "Zusammenfassung der überwachten Verzeichnisse:" },
        {"title.list.polls.stopped", "Die folgenden Überwachungen wurden beendet" },
        {"title.list.polls.started", "Die folgenden Überwachungen wurden gestartet" },
    };
    
}