//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeyPKCS12_de.java 5     7/12/22 16:31 Heller $ 
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
 * @version $Revision: 5 $
 */
public class ResourceBundleExportKeyPKCS12_de extends MecResourceBundle {

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
        {"button.cancel", "Abbrechen"},
        {"button.browse", "Durchsuchen"},
        {"keystore.contains.nokeys", "Dieser Keystore beihaltet keine privaten Schlüssel."},
        {"label.exportkey", "Dateiname"},
        {"label.exportkey.hint", "Zu erstellende export Keystore Datei (PKCS#12)"},
        {"label.keypass", "Passwort"},
        {"label.keypass.hint", "Passwort für exportierten Keystore"},
        {"title", "Schlüssel in Keystore exportieren (PKCS#12 format)"},
        {"filechooser.key.export", "Bitte wählen Sie den PKCS#12 Keystore für den Export"},
        {"key.export.success.message", "Der Schlüssel wurde erfolgreich exportiert."},
        {"key.export.success.title", "Erfolg"},
        {"key.export.error.message", "Es gab einen Fehler beim Export.\n{0}"},
        {"key.export.error.title", "Fehler"},
        {"label.alias", "Schlüssel"},
        {"key.exported.to.file", "Der Schlüssel \"{0}\" wurde in den Keystore \"{1}\" exportiert."},};

}
