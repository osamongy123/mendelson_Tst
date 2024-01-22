//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessagePacker.java 19    6.12.18 16:11 Heller $
package de.mendelson.comm.as2.message;
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
 * @version $Revision: 19 $
 */
public class ResourceBundleAS2MessagePacker extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"message.signed", "Outbound message signed with the algorithm \"{1}\", using keystore alias \"{0}\"." },
        {"message.notsigned", "Outbound message is not signed." },                 
        {"message.encrypted", "Outbound message encrypted with the algorithm \"{1}\", using keystore alias \"{0}\"." }, 
        {"message.notencrypted", "Outbound message has not been encrypted." },     
        {"mdn.created", "Outbound MDN created for AS2 message \"{0}\", state set to [{1}]." },
        {"mdn.details", "Outbound MDN details: {0}" },
        {"message.compressed", "Outbound payload compressed from {0} to {1}." },
        {"message.compressed.unknownratio", "Outbound payload compressed." },
        {"mdn.signed", "Outbound MDN has been signed with the algorithm \"{0}\"." },
        {"mdn.notsigned", "Outbound MDN has not been signed." },
        {"mdn.creation.start", "Generating outbound MDN, setting message id to \"{0}\"."},
        {"message.creation.start", "Generating outbound AS2 message, setting message id to \"{0}\"."},
        {"signature.no.aipa", "The signing process does not use the Algorithm Identifier Protection Attribute as defined in the configuration - this is insecure!" },
    };
    
}