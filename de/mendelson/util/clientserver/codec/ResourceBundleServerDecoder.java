//$Header: /oftp2/de/mendelson/util/clientserver/codec/ResourceBundleServerDecoder.java 3     9.06.20 10:40 Heller $
package de.mendelson.util.clientserver.codec;

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
 * @version $Revision: 3 $
 */
public class ResourceBundleServerDecoder extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"client.incompatible", "Unable to establish the client-server connection. "
            + "The server is not able to deserialize the client messages. "
            + "The main reason for this is different versions of client and server."},};
}