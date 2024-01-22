//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleRenameEntry.java 6     15/12/22 10:27 Heller $ 
package de.mendelson.util.security.cert.gui;

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
public class ResourceBundleRenameEntry extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"button.ok", "Ok"},
        {"button.cancel", "Cancel"},
        {"label.newalias", "New alias"},
        {"label.newalias.hint", "The alias that should be used in the future"},
        {"label.keypairpass", "Key password"},
        {"title", "Rename alias ({0})"},
        {"alias.exists.title", "Alias rename failed" },
        {"alias.exists.message", "The alias \"{0}\" does already exist in that keystore." },
    };

}
