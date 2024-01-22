//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleJettyStarter_de.java 2     2/05/22 10:22 Heller $
package de.mendelson.comm.as2.server;

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
 * @version $Revision: 2 $
 */
public class ResourceBundleJettyStarter_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {        
        {"module.name", "[JETTY]" },
        {"httpserver.willstart", "Integrierter HTTP Server startet" },
        {"httpserver.running", "Integrierter HTTP Server läuft ({0})" },
        {"httpserver.startup.problem", "Problem beim Start ({0})" },
        {"httpserver.stopped", "Integrierter HTTP Server gestoppt" },
        {"deployment.success", "[{0}] wurde erfolgreich bereitgestellt" },
        {"deployment.failed", "[{0}] wurde NICHT bereitgestellt: {1}" },
        {"listener.started", "Warte auf eingehende Verbindungen {0}"},
        {"userconfiguration.readerror", "Problem beim Lesen der Benutzerkonfiguration von {0}: {1} ... Ignoriere die Benutzerkonfiguration und starten den Webserver unter Verwendung der definierten Standardwerte" },
        {"userconfiguration.reading", "Lese benutzerdefinierte Konfiguration von {0}" },
        {"userconfiguration.setvar", "Setze benutzerdefinierten Wert [{0}] auf [{1}]" },
    };
}