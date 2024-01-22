//$Header: /as2/de/mendelson/comm/as2/client/manualsend/ResourceBundleManualSend.java 8     19/01/23 11:19 Heller $ 
package de.mendelson.comm.as2.client.manualsend;

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
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class ResourceBundleManualSend extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"button.ok", "Ok"},
        {"button.cancel", "Cancel"},
        {"button.browse", "Browse"},
        {"label.filename", "Send file"},
        {"label.filename.hint", "File to send to your partner"},
        {"label.testdata", "Send test data"},
        {"label.partner", "Receiver"},
        {"label.localstation", "Sender:"},
        {"label.selectfile", "Please select the file to send"},
        {"title", "Send file to partner"},
        {"send.success", "The file has been enqueued to the send process successfully."},
        {"send.failed", "The file has not been enqueued to the send process because of an error."},        
    };

}
