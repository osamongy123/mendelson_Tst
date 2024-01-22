//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2StatusBar_de.java 6     4.06.19 12:46 Heller $ 
package de.mendelson.comm.as2.client;

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
 * @version $Revision: 6 $
 */
public class ResourceBundleAS2StatusBar_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"count.ok", "Fehlerlose Transaktionen"},
        {"count.all.served", "Anzahl der bereitgestellten Transaktionen"},
        {"count.all.available", "Summe der Transaktionen im System"},
        {"count.pending", "Wartende Transaktionen"},
        {"count.failure", "Fehlerhafte Transaktionen"},
        {"count.selected", "Selektierte Transaktionen"},
        {"configuration.issue.single", "{0} Konfigurationsproblem"},
        {"configuration.issue.multiple", "{0} Konfigurationsprobleme"},
        {"no.configuration.issues", "Keine Konfigurationsprobleme"},};

}
