//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleImportKeyPEM.java 4     7/12/22 16:31 Heller $ 
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
 * @version $Revision: 4 $
 */
public class ResourceBundleImportKeyPEM extends MecResourceBundle {

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
        {"button.browse", "Browse"},
        {"label.importkey", "Key file (PEM)"},        
        {"label.importkey.hint", "File contents starts with --- BEGIN PRIVATE KEY ---"},
        {"label.importcert", "Certificate file"},
        {"label.importcert.hint", "File contents starts with --- BEGIN CERTIFICATE ---"},        
        {"label.alias", "Alias"},
        {"label.alias.hint", "New alias to use for this key entry"},
        {"label.keypass", "Password"},
        {"label.keypass.hint", "Key password (importing key)"},
        {"title", "Import keys (PEM format)"},
        {"filechooser.cert.import", "Please select the certificate file for the import"},
        {"filechooser.key.import", "Please select the key file for the import (PEM format)"},
        {"key.import.success.message", "The key has been imported successfully."},
        {"key.import.success.title", "Success"},
        {"key.import.error.message", "There occured an error during the import process.\n{0}"},
        {"key.import.error.title", "Error"},};

}
