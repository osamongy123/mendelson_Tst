//$Header: /as2/de/mendelson/comm/as2/message/store/ResourceBundleMessageStoreHandler.java 10    30.07.21 10:27 Heller $
package de.mendelson.comm.as2.message.store;
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
 * @version $Revision: 10 $
 */
public class ResourceBundleMessageStoreHandler extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"message.error.stored", "The message payload has been stored to \"{0}\"." },
        {"message.error.raw.stored", "The raw outgoing message has been stored to \"{0}\"." },        
        {"dir.createerror", "Unable to create directory \"{0}\"." },  
        {"comm.success", "The AS2 communication was successful, the payload {0} has been moved to \"{1}\" ({2})." },
        {"outboundstatus.written", "The outbound status file has been written to \"{0}\"."},
    };
    
}