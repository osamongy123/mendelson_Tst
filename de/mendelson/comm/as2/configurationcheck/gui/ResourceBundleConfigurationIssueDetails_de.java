//$Header: /as2/de/mendelson/comm/as2/configurationcheck/gui/ResourceBundleConfigurationIssueDetails_de.java 4     26.11.20 9:21 Heller $
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
 * @version $Revision: 4 $
 */
public class ResourceBundleConfigurationIssueDetails_de extends MecResourceBundle {

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
        {"title", "Konfigurationsproblem (Details) - Problem {0}/{1}"},
        {"button.close", "Schliessen" },
        {"button.next", "Nächstes Problem >>" },
        {"button.jumpto.generic", "Zum Problem" },
        {"button.jumpto.partner", "Zur Partnerverwaltung" },
        {"button.jumpto.keystore", "Zur Zertfiikatsverwaltung" },        
        {"button.jumpto.config", "Zur Konfiguration" },
    };
}
