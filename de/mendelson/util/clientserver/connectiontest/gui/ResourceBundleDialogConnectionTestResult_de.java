//$Header: /as4/de/mendelson/util/clientserver/connectiontest/gui/ResourceBundleDialogConnectionTestResult_de.java 12    18.11.20 11:42 Helle $
package de.mendelson.util.clientserver.connectiontest.gui;

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
 *
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class ResourceBundleDialogConnectionTestResult_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Ergebnis des Verbindungstests"},
        {"description." + JDialogConnectionTestResult.CONNECTION_TEST_OFTP2, 
            "Das System hat einen Verbindungstest zur Addresse {0}, Port {1} durchgeführt. "
            + "Das folgende Ergebnis zeigt, ob der Verbindungsaufbau erfolgreich war und ob an dieser "
            + "Addresse ein OFTP2 Server läuft. Wenn eine TLS Verbindung verwendet werden sollte und dies "
            + "erfolgreich möglich war, können Sie die Zertifikate Ihres Partners herunterladen und in Ihren "
            + "Keystore importieren."},
        {"description." + JDialogConnectionTestResult.CONNECTION_TEST_AS2, 
            "Das System hat einen Verbindungstest zur Addresse {0}, Port {1} durchgeführt. "
            + "Das folgende Ergebnis zeigt, ob der Verbindungsaufbau erfolgreich war und ob an dieser "
            + "Addresse ein HTTP Server läuft. Auch wenn der Test erfolgreich ist, ist nicht sichergestellt, "
            + "ob dies ein normaler HTTP Server oder ein AS2 Server ist. Wenn eine TLS Verbindung verwendet "
            + "werden sollte (HTTPS) und dies erfolgreich möglich war, können Sie die Zertifikate Ihres "
            + "Partners herunterladen und in Ihren Keystore importieren."},
        {"description." + JDialogConnectionTestResult.CONNECTION_TEST_AS4, 
            "Das System hat einen Verbindungstest zur Addresse {0}, Port {1} durchgeführt. "
            + "Das folgende Ergebnis zeigt, ob der Verbindungsaufbau erfolgreich war und ob an dieser "
            + "Addresse ein HTTP Server läuft. Auch wenn der Test erfolgreich ist, ist nicht sichergestellt, "
            + "ob dies ein normaler HTTP Server oder ein AS4 Server ist. Wenn eine TLS Verbindung verwendet "
            + "werden sollte (HTTPS) und dies erfolgreich möglich war, können Sie die Zertifikate Ihres "
            + "Partners herunterladen und in Ihren Keystore importieren."},
        {"OK", "[ERFOLGREICH]"},
        {"FAILED", "[FEHLER]"},
        {"AVAILABLE", "[VORHANDEN]"},
        {"NOT_AVAILABLE", "[NICHT VORHANDEN]"},
        {"header.ssl", "{0} [TLS Verbindung]"},
        {"header.plain", "{0} [Ungesicherte Verbindung]"},
        {"no.certificate.plain", "Nicht verfügbar (Ungesicherte Verbindung)"},
        {"button.viewcert", "<HTML><div style=\"text-align:center\">Zertifikat(e) importieren</div></HTML>"},
        {"button.close", "Schliessen"},
        {"label.connection.established", "Die einfache IP Verbindung wurde hergestellt"},
        {"label.certificates.available.local", "Die Partnerzertifikate (TLS) sind in Ihrem System verfügbar"},
        {"label.running.oftpservice", "Es wurde ein laufender OFTP Service gefunden"},
        {"used.cipher", "Für den Test wurde der folgende Verschlüsselungsalgorithmus verwendet: \"{0}\"" },          
    };

}
