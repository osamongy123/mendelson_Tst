//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleMDNParser_de.java 4     4/06/18 1:35p Heller $
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
 * @version $Revision: 4 $
 */
public class ResourceBundleMDNParser_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"invalid.mdn.nocontenttype", "Eingehende MDN ist ungültig: Kein content-type definiert." },
        {"structure.failure.mdn", "Eine eingehende MDN wurde erkannt. Leider ist die Struktur der MDN fehlerhaft (\"{0}\"), sodass sie nicht verarbeitet werden konnte. Die zugehörige Transaktion hat Ihren Status nicht verändert." },
    };
    
}