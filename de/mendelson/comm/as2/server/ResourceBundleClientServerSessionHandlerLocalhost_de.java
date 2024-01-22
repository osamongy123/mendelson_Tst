//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleClientServerSessionHandlerLocalhost_de.java 6     4/06/18 1:35p Heller $
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
 * @version $Revision: 6 $
 */
public class ResourceBundleClientServerSessionHandlerLocalhost_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"only.localhost.clients", "Der entfernte Server darf nur Verbindungen von localhost entgegennehmen. Um dieses Verhalten zu ändern, starten Sie ihn bitte mit der Option \"-allowallclients\"."},
        {"allowallclients.true", "**Der AS22 Server akzeptiert AS2 Client Anfragen von anderen Hosts**"},
        {"allowallclients.false", "**Der AS2 Server akzeptiert nur lokale Client Anfragen**"},
    };
}
