//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleProcessingEvent_de.java 4     18.12.20 14:55 Heller $
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
 * @version $Revision: 4 $
 */
public class ResourceBundleProcessingEvent_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"event.enqueued", "Das definierte Nachbearbeitungsereignis ({0}) wurde in die Warteschlange gestellt und wird in einigen Sekunden ausgeführt." },
        {"processtype." + ProcessingEvent.PROCESS_EXECUTE_SHELL, "Kommando auf der Systemshell ausführen" },
        {"processtype." + ProcessingEvent.PROCESS_MOVE_TO_DIR, "Nachricht in Verzeichnis verschieben" },
        {"processtype." + ProcessingEvent.PROCESS_MOVE_TO_PARTNER, "Nachricht an Partner weiterleiten" },
        {"eventtype." + ProcessingEvent.TYPE_RECEIPT_SUCCESS, "Empfang" },
        {"eventtype." + ProcessingEvent.TYPE_SEND_FAILURE, "Versand (fehlerhaft)" },
        {"eventtype." + ProcessingEvent.TYPE_SEND_SUCCESS, "Versand (in Ordnung)" },
    };
    
}