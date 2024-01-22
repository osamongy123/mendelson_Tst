//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderReceiver_de.java 9     9/02/22 17:21 Heller $
package de.mendelson.comm.as2.sendorder;
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
 * @version $Revision: 9 $
 */
public class ResourceBundleSendOrderReceiver_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"async.mdn.wait", "Warte auf asynchrone MDN bis {0}." },
        {"max.retry.reached", "Die maximale Anzahl von Wiederholungsversuchen ({0}) wurde erreicht, die Transaktion wird beendet." },
        {"retry", "Versuche eine erneute Übertragung nach {0}s, Wiederholung {1}/{2}." },
        {"as2.send.disabled", "** Die Anzahl der parallelen ausgehenden Verbindungen ist auf 0 gestellt - das System wird weder MDN noch AS2 Nachrichten versenden. Bitte ändern Sie diese Einstellung in den Servereinstellungen, wenn Sie senden wollen **" },        
        {"outbound.connection.prepare.mdn", "Bereite ausgehende MDN Verbindung vor nach to \"{0}\", aktive Verbindungen: {1}/{2}." },
        {"outbound.connection.prepare.message", "Bereite ausgehende AS2 Nachrichtenverbindung vor nach \"{0}\", aktive Verbindungen: {1}/{2}." },        
        {"send.connectionsstillopen", "Sie haben die Anzahl der ausgehenden Verbindungen auf {0} reduziert, aber zur Zeit gibt es noch {1} ausgehende Verbindungen." },
    };
    
}