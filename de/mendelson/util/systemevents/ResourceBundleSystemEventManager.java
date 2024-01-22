//$Header: /mendelson_business_integration/de/mendelson/util/systemevents/ResourceBundleSystemEventManager.java 5     1.09.21 11:28 Heller $
package de.mendelson.util.systemevents;

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
public class ResourceBundleSystemEventManager extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"label.body.clientip", "Client ip: {0}"},
        {"label.body.processid", "Client process id: {0}"},
        {"label.body.clientos", "Client OS: {0}"},
        {"label.body.clientversion", "Client version: {0}"},
        {"label.body.details", "Details: {0}"},
        {"label.subject.login.success", "Client login success [{0}]"},
        {"label.subject.login.failed", "Client login failed [{0}]"},
        {"label.subject.logoff", "Client logoff [{0}]"},
        {"label.error.clientserver", "Problem in the client-server connection" },
        {"label.body.tlsprotocol", "TLS protocol: {0}" },
        {"label.body.tlsciphersuite", "TLS cipher: {0}" },
    };
}
