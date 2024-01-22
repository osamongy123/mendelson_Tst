//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteMoveToDir_de.java 1     4.09.20 15:33 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;
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
 * @version $Revision: 1 $
 */
public class ResourceBundleExecuteMoveToDir_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"executing.receipt", "[Nachbearbeitung] ({0} --> {1}) Führe Ereignis nach Empfang aus." },
        {"executing.send", "[Nachbearbeitung] ({0} --> {1}) Führe Ereignis nach Versand aus." },
        {"executing.targetdir", "[Nachbearbeitung] Zielverzeichnis: \"{0}\"." },
        {"executing.movetodir", "[Nachbearbeitung] Verschiebe \"{0}\" nach \"{1}\"." },
        {"executing.movetodir.success", "[Nachbearbeitung] Datei erfolgreich verschoben" },
        {"messageid.nolonger.exist", "[Nachbearbeitung] Das Ereignis für die Nachricht \"{0}\" konnte nicht ausgeführt werden - sie existiert nicht mehr. Prozess wird übersprungen.." },
    };
    
}