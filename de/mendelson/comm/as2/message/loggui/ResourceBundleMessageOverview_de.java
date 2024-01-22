//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageOverview_de.java 10    4/06/18 1:35p Heller $
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
 * @version $Revision: 10 $
 */
public class ResourceBundleMessageOverview_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"header.timestamp", "Zeit" },
        {"header.localstation", "Lokale Station" },
        {"header.partner", "Partner" },                    
        {"header.messageid", "Referenznummer" },                            
        {"header.encryption", "Verschlüsselung" },
        {"header.signature", "Digitale Signatur" },
        {"header.mdn", "MDN" },   
        {"header.userdefinedid", "Id" },  
        {"header.payload", "Nutzdaten" },   
        {"header.subject", "Subject" },
        {"header.compression", "Komprimierung" },
        {"number.of.attachments", "* {0} Dateianhänge *" },
    };
    
}