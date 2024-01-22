//$Header: /as4/de/mendelson/util/clientserver/connectiontest/gui/ResourceBundleDialogConnectionTestResult.java 11    18.11.20 11:42 Heller $
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
 * @version $Revision: 11 $
 */
public class ResourceBundleDialogConnectionTestResult extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Connection test result"},
        {"description." + JDialogConnectionTestResult.CONNECTION_TEST_OFTP2, 
            "The system performed an IP connection to the ip address {0}, port {1}. "
            + "The following result log shows if this connection was successful and if an OFTP2 server listens to this address. "
            + "If a TLS connection has been requested and was successful it is possible to download the certificate(s), they will be stored in your TLS keystore."},
        {"description." + JDialogConnectionTestResult.CONNECTION_TEST_AS2, 
            "The system performed an IP connection to the ip address {0}, port {1}. "
            + "The following result log shows if this connection was successful and if a HTTP server listens to this address. "
            + "Even if the test is successful it is not ensured that there listens a AS2 server - it could be a normal HTTP server. "
            + "If a TLS connection has been requested (HTTPS) and was successful it is possible to download the certificate(s), "
            + "they will be stored in your TLS keystore."},
        {"description." + JDialogConnectionTestResult.CONNECTION_TEST_AS4, 
            "The system performed an IP connection to the ip address {0}, port {1}. "
            + "The following result log shows if this connection was successful and if a HTTP server listens to this address. "
            + "Even if the test is successful it is not ensured that there listens a AS4 server - it could be a normal HTTP server. "
            + "If a TLS connection has been requested (HTTPS) and was successful it is possible to download the certificate(s), "
            + "they will be stored in your TLS keystore."},
        {"OK", "[OK]"},
        {"FAILED", "[FAILED]"},
        {"AVAILABLE", "[AVAILABLE]"},
        {"NOT_AVAILABLE", "[NOT AVAILABLE]"},
        {"header.ssl", "{0} [TLS]"},
        {"header.plain", "{0} [PLAIN]"},
        {"no.certificate.plain", "Not available (Plain connection test)"},
        {"button.viewcert", "<HTML><div style=\"text-align:center\">Import certificate(s)</div></HTML>"},
        {"button.close", "Close"},
        {"label.connection.established", "The raw IP connection has been established"},
        {"label.certificates.available.local", "The partner TLS certificates are available in your system"},
        {"label.running.oftpservice", "A running OFTP service has been found"},
        {"used.cipher", "The used cipher for this test is \"{0}\"" },           
    };

}
