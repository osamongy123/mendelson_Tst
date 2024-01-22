//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleProcessingEvent_fr.java 3     10.09.20 12:57 Heller $
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
public class ResourceBundleProcessingEvent_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"event.enqueued", "L''événement de post-traitement défini a été demandé ({0}) et sera exécuté en quelques secondes." },
        {"processtype." + ProcessingEvent.PROCESS_EXECUTE_SHELL, "Exécuter une commande shell" },
        {"processtype." + ProcessingEvent.PROCESS_MOVE_TO_DIR, "Déplacer le message vers le répertoire" },
        {"processtype." + ProcessingEvent.PROCESS_MOVE_TO_PARTNER, "Transmettre le message au partenaire" },
        {"eventtype." + ProcessingEvent.TYPE_RECEIPT_SUCCESS, "Réception" },
        {"eventtype." + ProcessingEvent.TYPE_SEND_FAILURE, "Expédition (incorrect)" },
        {"eventtype." + ProcessingEvent.TYPE_SEND_SUCCESS, "Expédition (tout droit)" },
    };
    
}