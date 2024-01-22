//$Header: /as2/de/mendelson/util/httpconfig/gui/ResourceBundleDisplayHTTPConfiguration.java 12    17/01/23 11:06 Heller $ 
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
 * @version $Revision: 12 $
 */
public class ResourceBundleDisplayHTTPConfiguration extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Server side HTTP configuration"},
        {"reading.configuration", "Reading HTTP configuration..."},
        {"button.ok", "Close" },
        {"label.info.configfile", "This dialog contains the server side HTTP/S configuration. The bundled HTTP server has the version <strong>jetty {0}</strong>. "
            + "You could set up the protocols and ciphers in the file \"{1}\" on the server. Base settings could be configured in the file \"{2}\" or directly via the server settings. "
            + "Please restart the server for the changes to be applied." },
        {"tab.misc", "Misc"},
        {"tab.cipher", "TLS cipher"},
        {"tab.protocols", "TLS protocols"},
        {"no.ssl.enabled", "The TLS support has not been enabled in the underlaying HTTP server.\nPlease modify the configuration file {0}\naccording to the documentation and restart the server." },        
        {"no.embedded.httpserver", "You did not start the embedded HTTP server.\nThere is no information available." },               
    };
}