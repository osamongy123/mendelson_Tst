//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderSender.java 5     6.12.18 16:26 Heller $
package de.mendelson.comm.as2.sendorder;
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
 * @version $Revision: 5 $
 */
public class ResourceBundleSendOrderSender extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"message.packed", "Outbound AS2 message created from \"{0}\" for the receiver \"{1}\" in {3}, raw message size: {2}, user defined id: \"{4}\"" },
        {"sendoder.sendfailed", "A problem occured during processing a send order: [{0}] \"{1}\" - the data has not been transmitted to the partner." },
    };
    
}