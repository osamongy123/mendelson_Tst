//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleImportKeyPKCS12_de.java 5     7/12/22 16:31 Heller $ 
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
public class ResourceBundleImportKeyPKCS12_de extends MecResourceBundle {

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
        {"button.cancel", "Abbruch"},
        {"button.browse", "Durchsuchen"},
        {"keystore.contains.nokeys", "Diese Keystore beinhaltet keine privaten Schlüssel."},
        {"label.importkey", "Dateiname"},
        {"label.importkey.hint", "Keystore Datei, die importiert werden soll (PKCS#12)"},
        {"label.keypass", "Passwort"},
        {"label.keypass.hint", "Keystore Passwort (PKCS#12)"},
        {"title", "Schlüssel aus Keystore importieren (PKCS#12 Format)"},
        {"filechooser.key.import", "Bitte wählen Sie eine PKCS#12 Keystore Datei für den Import"},
        {"multiple.keys.message", "Bitte wählen Sie den zu importierenden Schlüssel"},
        {"multiple.keys.title", "Mehrere Schlüssel enthalten"},
        {"key.import.success.message", "Der Schlüssel wurde erfolgreich importiert."},
        {"key.import.success.title", "Erfolg"},
        {"key.import.error.message", "Es trat ein Fehler während des Importprozesses auf.\n{0}"},
        {"key.import.error.title", "Fehler"},};

}
