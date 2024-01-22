//$Header: /as2/de/mendelson/comm/as2/partner/gui/global/ResourceBundleGlobalChange.java 2     26/08/22 14:17 Heller $
package de.mendelson.comm.as2.partner.gui.global;
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
 * @version $Revision: 2 $
 */
public class ResourceBundleGlobalChange extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Global changes for all partner" },
        {"button.ok", "Close" },    
        {"button.set", "Set" },
        {"partnersetting.changed", "Changed the setting for {0} partner." },
        {"partnersetting.notchanged", "Settings have not been changed - incorrect value" },  
        {"info.text", "<HTML>With the help of this dialogue you can set parameters of all partners "
            + "to defined values at the same time. When you press \"Set\", the respective value is "
            + "overwritten for <strong>ALL</strong> partners.</HTML>" },
        {"label.dirpoll", "Perform directory poll for all partners" },
        {"label.maxpollfiles", "Maximum number of files of all partners per polling process" },
        {"label.pollinterval", "Directory poll interval of all partners" },
        
    };
    
}