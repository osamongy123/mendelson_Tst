//$Header: /as2/de/mendelson/comm/as2/cem/gui/ResourceBundleDialogSendCEM_de.java 5     6/22/18 1:48p Heller $
package de.mendelson.comm.as2.cem.gui;
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
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ResourceBundleDialogSendCEM_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Zertifikate mit Partnern austauschen (CEM)" },
        {"button.ok", "Ok" },
        {"button.cancel", "Abbrechen" },
        {"label.initiator", "Lokale Station:" },
        {"label.receiver", "Empfänger:" },
        {"label.certificate", "Zertifikat:"},
        {"label.activationdate", "Aktivierungsdatum:"},
        {"cem.request.failed", "Die CEM Anfrage konnte nicht durchgeführt werden:\n{0}" },
        {"cem.request.success", "Die CEM Anfrage wurde erfolgreich ausgeführt." },
        {"cem.request.title", "Zertifikataustausch über CEM" },
        {"cem.informed", "Es wurde versucht, die folgenden Partner via CEM zu informieren, bitte informieren Sie sich über den Erfolg in der CEM Verwaltung: {0}" },
        {"cem.not.informed", "Die folgenden Partner wurden nicht via CEM informiert, bitte führen Sie hier den Zertifikataustausch via Email oder ähnlichem durch: {0}" },
        {"partner.all", "--Alle Partner--" },
        {"partner.cem.hint", "Partnersysteme müssen CEM unterstützen, um hier enthalten zu sein" },
        {"purpose.ssl", "SSL/TLS" },
        {"purpose.encryption", "Verschlüsselung" },
        {"purpose.signature", "Digitale Signatur" },
    };
    
}