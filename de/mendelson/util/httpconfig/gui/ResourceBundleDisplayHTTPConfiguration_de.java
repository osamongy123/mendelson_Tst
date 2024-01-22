//$Header: /as2/de/mendelson/util/httpconfig/gui/ResourceBundleDisplayHTTPConfiguration_de.java 10    17/01/23 11:06 Heller $ 
package de.mendelson.util.httpconfig.gui;

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
 * @version $Revision: 10 $
 */
public class ResourceBundleDisplayHTTPConfiguration_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Serverseitige HTTP Konfiguration"},
        {"reading.configuration", "Lese HTTP Konfiguration..."},
        {"button.ok", "Schliessen" },
        {"label.info.configfile", "Dieser Dialog zeigt Ihnen die serverseitige HTTP/S Konfiguration. Der mitgelieferte HTTP Server hat die Version <strong>jetty {0}</strong>. Sie können die Chiffren und die Protokolle in der Datei \"{1}\" auf dem Server konfigurieren. "
            + "Basiseinstellungen nehmen Sie bitte in der Datei \"{2}\" oder direkt über die Servereinstellungen vor. Bitte starten Sie den Server neu, um Änderungen wirksam zu machen." },
        {"tab.misc", "Allgemein"},
        {"tab.cipher", "TLS Chiffren"},
        {"tab.protocols", "TLS Protokolle"},
        {"no.ssl.enabled", "Der TLS Support wurde im unterliegenden HTTP Server nicht eingeschaltet.\nBitte modifizieren Sie die Konfigurationsdatei {0}\nentsprechend der Dokumentation und starten Sie den Server neu." },        
        {"no.embedded.httpserver", "Sie haben den unterliegenden HTTP Server nicht gestartet.\nEs ist keine Information verfügbar." },                
    };
}