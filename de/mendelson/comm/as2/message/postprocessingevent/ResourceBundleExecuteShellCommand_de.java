//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteShellCommand_de.java 3     4.09.20 15:34 Heller $
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
 * @version $Revision: 3 $
 */
public class ResourceBundleExecuteShellCommand_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"executing.receipt", "[Nachbearbeitung] ({0} --> {1}) Führe Ereignis nach Datenempfang aus." },
        {"executing.send", "[Nachbearbeitung] ({0} --> {1}) Führe Ereignis Datenversand aus." },
        {"executing.command", "[Nachbearbeitung] Shell Kommando: \"{0}\"." },
        {"executed.command", "[Nachbearbeitung] Das Shell Kommando wurde ausgeführt, Rückgabewert={0}." },
        {"messageid.nolonger.exist", "[Nachbearbeitung] Ein Nachbearbeitungsereignis für die Nachricht \"{0}\" kann nicht ausgeführt werden - diese Nachricht existiert nicht mehr. Überspringe Verarbeitung.." },
    };
    
}