//$Header: /as2/de/mendelson/comm/as2/datasheet/gui/ResourceBundleCreateDataSheet_de.java 5     19/01/23 11:16 Heller $ 
package de.mendelson.comm.as2.datasheet.gui;

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
 * @version $Revision: 5 $
 */
public class ResourceBundleCreateDataSheet_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Datenblatt für neue Kommunikationsanbindung"},
        {"button.ok", ">> Datenblatt erstellen"},
        {"button.cancel", "Abbrechen"},
        {"progress", "Erstelle PDF"},
        {"label.newpartner", "Neuer Partner - noch nicht im System"},
        {"label.comment", "Kommentar"},
        {"label.receipturl", "Ihre URL für den AS2 Empfang"},
        {"label.localpartner", "Lokaler Partner"},
        {"label.remotepartner", "Entfernter Partner"},
        {"label.encryption", "Verschlüsselung"},
        {"label.signature", "Digitale Signatur"},
        {"label.compression", "Datenkomprimierung"},
        {"label.syncmdn", "Synchrone MDN"},
        {"label.signedmdn", "Signierte MDN"},
        {"label.info", "<HTML><strong>Mit Hilfe dieses Dialoges können Sie ein Datenblatt erstellen, das die Anbindung eines neuen Partners erleichtert.</strong></HTML>"},
        {"file.written", "Das Datenblatt (PDF) wurde nach \"{0}\" geschrieben. Bitte senden Sie es an Ihren neuen Partner, um die Randdaten der Kommunikation auszutauschen."},
        {"label.usedataencryption", "Verwende Datenverschlüsselung"},
        {"label.usedatasignature", "Verwende signierte Daten"},
        {"label.usessl", "Verwende TLS"},
        {"label.usesessionauth", "Verwende Session Auth"},
        {"label.requestsignedeerp", "Erwarte signierte EERP"},};
}
