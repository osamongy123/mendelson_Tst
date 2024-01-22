//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportKeyPKCS12_fr.java 4     7/12/22 16:31 Heller $
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
 * @author E.Pailleau
 * @version $Revision: 4 $
 */
public class ResourceBundleExportKeyPKCS12_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"button.ok", "Valider"},
        {"button.cancel", "Annuler"},
        {"button.browse", "Parcourir..."},
        {"keystore.contains.nokeys", "Ce porte-clef ne contient aucune clef privée."},
        {"label.exportkey", "Nom du fichier"},
        {"label.exportkey.hint", "Nom du fichier d''exportation (PKCS#12)"},
        {"label.keypass", "Mot de passe"},
        {"label.keypass.hint", "Mot de passe pour keystore exporté"},
        {"title", "Export de la clef vers le porte-clef (PKCS#12 format)"},
        {"filechooser.key.export", "Merci de sélectionner le fichier porte-clef PKCS#12 pour l''export"},
        {"key.export.success.message", "La clef a été exportée avec succès."},
        {"key.export.success.title", "Succès"},
        {"key.export.error.message", "Une erreur a eu lieu lors du processus d''export.\n{0}"},
        {"key.export.error.title", "Erreur"},
        {"label.alias", "Clef"},
        {"key.exported.to.file", "La clef \"{0}\" a été insérée dans le porte-clef \"{1}\"."},};

}
