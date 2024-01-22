//$Header: /as2/de/mendelson/util/tables/hideablecolumns/ResourceBundleHideableColumns_fr.java 2     4/06/18 1:35p Heller $
package de.mendelson.util.tables.hideablecolumns;

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
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ResourceBundleHideableColumns_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"header.column", "Colonne"},
        {"header.visible", "Visible"},
        {"title", "Configuration de colonne"},
        {"label.info", "S''il vous plaît sélectionner les colonnes visibles ci-dessous."},
        {"header.icon", "[Image] - toujours visible"},
        {"label.ok", "Ok"},};

}
