//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerProcessing_de.java 13    16/06/22 13:39 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 *
 * @author S.Heller
 * @version $Revision: 13 $
 */
public class ResourceBundleAS2ServerProcessing_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"send.failed", "Versand fehlgeschlagen"},
        {"unable.to.process", "Fehler beim Verarbeiten auf dem Server: {0}"},
        {"server.shutdown", "Der Benutzer {0} fährt den Server herunter."},
        {"sync.mdn.sent", "Synchrone MDN als Antwort auf {0} versandt."},
        {"invalid.request.from", "Eine ungültige Anfrage ist eingegangen. Sie wird nicht verarbeitet, weil kein as2-from Header vorhanden ist."},
        {"invalid.request.to", "Eine ungültige Anfrage ist eingegangen. Sie wird nicht verarbeitet, weil kein as2-to Header vorhanden ist."},
        {"invalid.request.messageid", "Eine ungültige Anfrage ist eingegangen. Sie wird nicht verarbeitet, weil kein message-id Header vorhanden ist."},        
        {"info.mdn.inboundfiles", "Für die eingegangene MDN war es nicht möglich, die referenzierte AS2 Nachricht zu ermitteln.\n[Eingegangene MDN (Daten): {0}]\n[Eingegangene MDN (Header): {1}]"},
        {"message.resend.oldtransaction", "Diese Transaktion wurde erneut manuell mit der neuen Transaktionsnummer [{0}] versendet." },
        {"message.resend.newtransaction", "Diese Transaktion ist ein erneuter Versand der Transaktion [{0}]." },
        {"message.resend.title", "Manueller Versand von Daten in neuer Transaktion" },       
        {"local.station", "Lokale Station" },
    };
}