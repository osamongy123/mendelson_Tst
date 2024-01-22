//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteMoveToPartner_de.java 1     7.09.20 13:53 Heller $
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
public class ResourceBundleExecuteMoveToPartner_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"executing.receipt", "[Nachbearbeitung] ({0} --> {1}) Führe Ereignis nach Empfang aus." },
        {"executing.send", "[Nachbearbeitung] ({0} --> {1}) Führe Ereignis nach Versand aus." },
        {"targetpartner.does.not.exist", "[Nachbearbeitung] Der Zielpartner mit der AS2 Identifikation \"{0}\" exisitert nicht im System..überspringe Ereignisausführung" },
        {"executing.targetpartner", "[Nachbearbeitung] Zielpartner: \"{0}\"." },
        {"executing.movetopartner", "[Nachbearbeitung] Leite Nachricht aus Datei \"{0}\" an den Zielpartner \"{1}\" weiter." },
        {"executing.movetopartner.success", "[Nachbearbeitung] Der Versandauftrag wurde erfolgreich erstellt (\"{0}\")." },
        {"messageid.nolonger.exist", "[Nachbearbeitung] Das Nachbearbeitungsereignis konnte nicht ausgeführt werden - die Nachricht \"{0}\" exisitert nicht mehr im System..überspringe Ereignisausführung" },
    };
    
}