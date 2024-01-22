//$Header: /as2/de/mendelson/util/httpconfig/server/ResourceBundleHTTPServerConfigProcessor.java 13    17/01/23 11:55 Heller $
package de.mendelson.util.httpconfig.server;

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
 * @version $Revision: 13 $
 */
public class ResourceBundleHTTPServerConfigProcessor extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"http.server.config.listener", "Port {0} ({1}) is bound to the adapter {2}"},
        {"http.server.config.keystorepath", "TLS keystore path: \"{0}\""},
        {"http.server.config.clientauthentication", "Server requires TLS client authentication: {0}"},
        {"external.ip", "External ip: {0} / {1}"},
        {"external.ip.error", "External ip: -Unable to compute-"},
        {"http.receipturls", "Full receipt URLs that are possible in the current configuration:"},
        {"http.serverstateurl", "Check the server state:"},
        {"http.deployedwars", "Currently deployed war files in the HTTP server (Servlet functionality):"},
        {"webapp._unknown", "Unknown servlet"},
        {"webapp.as2.war", "mendelson AS2 receipt servlet"},
        {"webapp.as4.war", "mendelson AS4 receipt servlet"},
        {"webapp.as2api.war", "mendelson AS2 REST API"},
        {"webapp.as4api.war", "mendelson AS4 REST API"},
        {"webapp.oftp2api.war", "mendelson OFTP2 REST API"},
        {"webapp.webas2.war", "mendelson AS2 server web monitoring"},
        {"webapp.as2-sample.war", "mendelson AS2 API samples"},
        {"webapp.as4-sample.war", "mendelson AS4 API samples"},
        {"info.cipher", "The following ciphers are supported by the underlying HTTP server for inbound connections. Which ones are supported depends on your used Java VM (it''s {1}). You can disable single ciphers in the configuration file (\"{0}\")."},
        {"info.cipher.howtochange", "To disable ciphers for inbound connections please edit your unterlaying HTTP servers config file ({0}) using a text editor. Please search for the tag <Set name=\"ExcludeCipherSuites\">, add the cipher to exclude and restart the program."},
        {"info.protocols", "The following protocols are supported by the underlying HTTP server for inbound connections. Which ones are supported depends on your used Java VM (it''s {1}). The used TLS security provider is {2}.\nYou can disable single protocols in the configuration file (\"{0}\")."},
        {"info.protocols.howtochange", "To disable TLS protocols for inbound connections please edit your unterlaying HTTP servers config file ({0}) using a text editor. Please search for the tag <Set name=\"excludeProtocols\">, add the protocol to exclude and restart the program."},
    };
}
