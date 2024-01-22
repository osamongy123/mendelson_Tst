//$Header: /as2/de/mendelson/util/log/panel/ResourceBundleLogConsole_de.java 2     4/06/18 1:35p Heller $ 
package de.mendelson.util.log.panel;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ResourceBundleLogConsole_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Ausgaben"},
        {"label.clear", "Löschen"},
        {"label.toclipboard", "Log in die Zwischenablage kopieren"},
        {"label.tofile", "Log in Datei schreiben"},
        {"filechooser.logfile", "Bitte wählen Sie die Datei, in die das Log geschrieben werden soll."},
        {"write.success", "Das Log wurde erfolgreich in der Datei \"{0}\" gespeichert."},
        {"write.failure", "Fehler beim Schreiben des Logs: {0}."},};

}
