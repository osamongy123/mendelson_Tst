//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleJettyStarter.java 2     2/05/22 10:22 Heller $
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
public class ResourceBundleJettyStarter extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"module.name", "[JETTY]" },
        {"httpserver.willstart", "Embedded HTTP server is starting" },
        {"httpserver.running", "Embedded HTTP server is running ({0})" },
        {"httpserver.startup.problem", "Problem during startup ({0})" },
        {"httpserver.stopped", "Embedded HTTP server stopped" },
        {"deployment.success", "[{0}] has been deployed successfully" },
        {"deployment.failed", "[{0}] has NOT been deployed: {1}" },
        {"listener.started", "Started listener {0}"},
        {"userconfiguration.readerror", "Problem reading the user configuration from {0}: {1} .. ignoring the user configuration and starting the web server using the defined defaults" },
        {"userconfiguration.reading", "Reading user defined configuration from {0}" },
        {"userconfiguration.setvar", "Setting userdefined value [{0}] to [{1}]" },
    };
}