//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerProcessing.java 14    16/06/22 13:39 Heller $
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
 * @version $Revision: 14 $
 */
public class ResourceBundleAS2ServerProcessing extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        { "send.failed", "Send failed" },
        {"unable.to.process", "Unable to process on server: {0}" },
        { "server.shutdown", "The user {0} requests a server shutdown." },
        {"sync.mdn.sent", "Synchronous MDN sent as answer to message {0}."},
        {"invalid.request.from", "An invalid request has been detected. It has not been processed because it does not contain a as2-from header."},
        {"invalid.request.to", "An invalid request has been detected. It has not been processed because it does not contain a as2-to header."},
        {"invalid.request.messageid", "An invalid request has been detected. It has not been processed because it does not contain a message id header."},
        {"info.mdn.inboundfiles", "The related AS2 message for the inbound MDN could not be identified.\n[Inbound MDN file (raw): {0}]\n[Inbound MDN file (header): {1}]"},
        {"message.resend.oldtransaction", "This transaction has been manually resend with the new message id [{0}]" },
        {"message.resend.newtransaction", "This transaction is a resend of the existing message [{0}]" }, 
        {"message.resend.title", "Manual dispatch of data in new transaction" },
        {"local.station", "Local station" },
    };
    
}