//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageOverview_fr.java 5     4/06/18 1:35p Heller $
package de.mendelson.comm.as2.message.loggui;
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
 * @author E.Pailleau
 * @version $Revision: 5 $
 */
public class ResourceBundleMessageOverview_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"header.timestamp", "Date et heure" },
        {"header.localstation", "Station locale" },
        {"header.partner", "Partenaire" },
        {"header.messageid", "Message id" },
        {"header.encryption", "Cryptage" },
        {"header.signature", "Signature" },
        {"header.mdn", "MDN" },      
        {"header.userdefinedid", "Id" },  
        {"header.payload", "Contenu" },
        {"header.subject", "Subject" },
        {"number.of.attachments", "* {0} Pièces attachées *" },
    };
    
}
