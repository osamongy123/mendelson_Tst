//$Header: /as2/de/mendelson/util/clientserver/ResourceBundleGUIClient.java 10    4/06/18 1:35p Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize the mendelson products - if you want to localize
 * eagle to your language, please contact us: localize@mendelson.de
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class ResourceBundleGUIClient extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        //dialog
        {"password.required", "Login failure, server requires password for user {0}"},
        {"connectionrefused.message", "Connection refused to {0}. Please ensure that the server is running."},
        {"connectionrefused.title", "Connection problem"},
        {"login.success", "Logged in as user \"{0}\""},
        {"login.failure", "Login as user \"{0}\" failed."},
        {"connection.success", "Client connected to {0}"},
        {"logout.from.server", "Logged out from server"},
        {"connection.closed", "The local client-server connection has been disconnected by the server"},
        {"connection.closed.title", "Disconnected"},
        {"connection.closed.message", "The local client-server connection has been disconnected by the server"},
        {"client.received.unprocessed.message", "A message has been received by the client that is not processed: {0}."},
        {"error", "Error: {0}"},
        {"login.failed.client.incompatible.message", "The server reports that this client is incompatible. Please use the proper client version."},
        {"login.failed.client.incompatible.title", "Login rejected"},
    };
}