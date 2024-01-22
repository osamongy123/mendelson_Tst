//$Header: /as2/de/mendelson/util/tables/hideablecolumns/ResourceBundleHideableColumns_de.java 4     4/06/18 1:35p Heller $
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
 * @version $Revision: 4 $
 */
public class ResourceBundleHideableColumns_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"header.column", "Spalte"},
        {"header.visible", "Sichtbar"},
        {"title", "Konfiguration der Spalten"},
        {"label.info", "Bitte selektieren Sie hier die sichtbaren Spalten."},
        {"header.icon", "[Status Icon] - immer sichbar"},
        {"label.ok", "Ok"},};

}
