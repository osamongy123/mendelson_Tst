//$Header: /as2/de/mendelson/util/httpconfig/server/ResourceBundleHTTPServerConfigProcessor_de.java 13    17/01/23 11:55 Heller $
package de.mendelson.util.httpconfig.server;

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
public class ResourceBundleHTTPServerConfigProcessor_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"http.server.config.listener", "Port {0} ({1}) ist gebunden an den Netzwerkadapter {2}"},
        {"http.server.config.keystorepath", "TLS Keystore Pfad: \"{0}\""},
        {"http.server.config.clientauthentication", "Server benötigt TLS Client Authentication: {0}"},
        {"external.ip", "Externe IP: {0} / {1}"},
        {"external.ip.error", "Externe IP: -Kann nicht festgestellt werden-"},
        {"http.receipturls", "Vollständige Empfangs-URLs der aktuellen Konfiguration"},
        {"http.serverstateurl", "Serverstatus anzeigen:"},
        {"http.deployedwars", "Aktuell verfügbare WARs im HTTP Server (Servletfunktionalität):"},
        {"webapp._unknown", "Unbekanntes Servlet"},
        {"webapp.as2.war", "mendelson AS2 Empfangsservlet"},
        {"webapp.as4.war", "mendelson AS4 Empfangsservlet"},
        {"webapp.as2api.war", "mendelson AS2 REST API"},
        {"webapp.as4api.war", "mendelson AS4 REST API"},
        {"webapp.oftp2api.war", "mendelson OFTP2 REST API"},
        {"webapp.webas2.war", "mendelson AS2 Server Web Überwachung"},
        {"webapp.as2-sample.war", "mendelson AS2 API Beispiele"},
        {"webapp.as4-sample.war", "mendelson AS4 API Beispiele"},
        {"info.cipher", "Die folgende Chiffren werden vom unterliegenden HTTP Server eingangsseitig unterstützt.\nWelche unterstützt werden, hängt von Ihrer eingesetzten Java VM ab (aktuell {1}).\nSie können einzelne Chiffren in der Konfigurationsdatei\n\"{0}\" deaktivieren."},
        {"info.cipher.howtochange", "Um bestimmte Chiffren für eingehende Verbindungen zu deaktivieren, bearbeiten Sie bitte die Konfigurationsdatei Ihres eingebetteten HTTP Servers ({0}) mit einem Texteditor. Suchen Sie bitte nach der Zeichenkette <Set name=\"ExcludeCipherSuites\">, fügen Sie die auszuschliessende Chiffre hinzu und starten Sie das Programm neu."},
        {"info.protocols", "Die folgende Protokolle werden vom unterliegenden HTTP Server für eingehende Verbindungen unterstützt.\nWelche unterstützt werden, hängt von Ihrer eingesetzten Java VM ab (aktuell {1}). Der verwendete TLS Sicherheitsanbieter ist {2}.\nSie können einzelne Protokolle in der Konfigurationsdatei\n\"{0}\" deaktivieren."},
        {"info.protocols.howtochange", "Um bestimmte Protokolle eingangsseitig zu deaktivieren, bearbeiten Sie bitte die Konfigurationsdatei Ihres eingebetteten HTTP Servers ({0}) mit einem Texteditor. Suchen Sie bitte nach der Zeichenkette <Set name=\"ExcludeProtocols\">, fügen Sie das auszuschliessende Protokoll hinzu und starten Sie das Programm neu."},
    };
}
