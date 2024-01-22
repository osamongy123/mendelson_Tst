//$Header: /as2/de/mendelson/comm/as2/configurationcheck/gui/ResourceBundleConfigurationIssueDetails.java 3     26.11.20 9:21 Heller $
package de.mendelson.comm.as2.configurationcheck.gui;

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
 * @version $Revision: 3 $
 */
public class ResourceBundleConfigurationIssueDetails extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        //preferences localized
        {"title", "Configuration issue details - issue {0}/{1}"},
        {"button.close", "Close" },
        {"button.next", "Next issue >>" },
        {"button.jumpto.generic", "Go to problem" },
        {"button.jumpto.partner", "Display in partner manager" },
        {"button.jumpto.keystore", "Display in certificate manager" },        
        {"button.jumpto.config", "Display in configuration" },
    };
}
