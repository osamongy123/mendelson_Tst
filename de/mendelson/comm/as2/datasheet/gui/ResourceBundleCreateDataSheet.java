//$Header: /as2/de/mendelson/comm/as2/datasheet/gui/ResourceBundleCreateDataSheet.java 5     19/01/23 11:16 Heller $ 
package de.mendelson.comm.as2.datasheet.gui;

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
 * @version $Revision: 5 $
 */
public class ResourceBundleCreateDataSheet extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Datasheet for new communication link" },
        {"button.ok", ">> Create datasheet"},
        {"button.cancel", "Cancel"},   
        {"progress", "Create PDF" },
        {"label.newpartner", "New partner - not in the system so far" },
        {"label.comment", "Comment"},
        {"label.receipturl", "Your AS2 receipt URL" },
        {"label.localpartner", "Local partner"},
        {"label.remotepartner", "Remote partner"},
        { "label.encryption", "Encryption"},
        { "label.signature", "Digital signature"},
        { "label.compression", "Data compression"},
        { "label.syncmdn", "Request sync MDN"},
        { "label.signedmdn", "Request signed MDN"},
        {"label.info", "<HTML><strong>This wizard allows to create a comminication datasheet PDF which could be send to a new partner to establish a communication link.</strong></HTML>" },
        {"file.written", "The communication datasheet PDF has been written to \"{0}\". Please send it to your new partner to establish a new communication link." },
        {"label.usedataencryption", "Use data encryption" },
        {"label.usedatasignature", "Use data signature"},
        {"label.usessl", "Use TLS"},
        {"label.usesessionauth", "Use session auth"},
        {"label.requestsignedeerp", "Request signed EERP"},
    };
}
