//$Header: /as2/de/mendelson/util/clientserver/log/search/gui/ResourceBundleDialogSearchLogfile_de.java 5     19/01/23 9:26 Heller $
package de.mendelson.util.clientserver.log.search.gui;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize the mendelson products
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ResourceBundleDialogSearchLogfile_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Protokolleinträge des Servers durchsuchen"},
        {"no.data.messageid", "**Es gibt keine Protokolldaten für die AS2 Nachrichtennummer \"{0}\" in dem gewählten Zeitraum. Bitte verwenden Sie als Suchzeichenkette die vollständige Nachrichtennummer." },        
        {"no.data.mdnid", "**Es gibt keine Protokolldaten für die MDN Nummer \"{0}\" in dem gewählten Zeitraum. Bitte verwenden Sie als Suchzeichenkette die vollständige MDN Nummer, die Sie dem Log einer Übertragung entnehmen können." },        
        {"no.data.uid", "**Es gibt keine Protokolldaten für die benutzerdefinierte Nummer \"{0}\" in dem gewählten Zeitraum. Bitte wählen Sie als Suchzeichenkette die vollständige benutzerdefinierte Nummer, die Sie der Übertragung mitgegeben haben." },        
        {"label.startdate", "Anfang" },
        {"label.enddate", "Ende" },
        {"button.close", "Schliessen" },
        {"label.search", "<html><div style=\"text-align:center\">Protokoll<br>durchsuchen</div></html>" },
        {"label.info", "<html>Bitte definieren Sie einen Zeitraum, geben eine vollständige AS2 Nachrichtennummer oder die vollständige Nummer einer MDN ein, um alle Protokolleinträge dafür auf dem Server zu finden - dann drücken Sie bitte den Knopf \"Protokoll durchsuchen\". Die benutzerdefinierte Nummer können Sie für jede Transaktion definieren, wenn Sie die Daten über die Kommandozeile an den laufenden Server schicken.</html>" },
        {"textfield.preset", "AS2 Nachrichtennummer, MDN Nummer oder benutzerdefinierte Identifikation" },
        {"label.messageid", "Nachrichtennummer" },
        {"label.mdnid", "MDN Nummer" },
        {"label.uid", "Benutzerdefinierte Identifikation" },
        {"problem.serverside", "Es gab ein serverseitiges Problem beim Durchsuchen der Protokolldateien: [{0}] {1}" },
    };
}
