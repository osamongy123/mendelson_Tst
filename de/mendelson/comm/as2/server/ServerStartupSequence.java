//$Header: /mec_as2/de/mendelson/comm/as2/server/ServerStartupSequence.java 1     13.09.10 17:20 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Additional work that could be added to the AS2 server startup
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class ServerStartupSequence {

    private Logger logger;
    private PreferencesAS2 preferences = new PreferencesAS2();

    public ServerStartupSequence(Logger logger) {
        this.logger = logger;
    }

    /**Starts conditional processes for the server startup process*/
    public void performWork() {
    }
}
