//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleImportKeyPEM_fr.java 4     7/12/22 16:31 Heller $
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
public class ResourceBundleImportKeyPEM_fr extends MecResourceBundle {

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
        {"label.importkey", "Fichier clef (PEM)"},
        {"label.importkey.hint", "Le contenu du fichier commence par --- BEGIN PRIVATE KEY ---"},
        {"label.importcert", "Fichier certificat (PEM)"},    
        {"label.importcert.hint", "Le contenu du fichier commence par --- BEGIN CERTIFICATE ---"},        
        {"label.alias", "Alias"},
        {"label.alias.hint", "Nouvel alias à utiliser pour cette clé"},
        {"label.keypass", "Mot de passe"},
        {"label.keypass.hint", "Mot de passe de la clé privée"},
        {"title", "Importer les clefs (format PEM)"},
        {"filechooser.cert.import", "Merci de sélectionner le fichier de certificat pour l''import"},
        {"filechooser.key.import", "Merci de sélectionner le fichier de clef pour l''import (format PEM)"},
        {"key.import.success.message", "La clef a été importée avec succès."},
        {"key.import.success.title", "Succès"},
        {"key.import.error.message", "Une erreur a eu lieu lors du processus d''import.\n{0}"},
        {"key.import.error.title", "Erreur"},};

}
