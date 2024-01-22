//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleRenameEntry_fr.java 6     15/12/22 10:27 Heller $
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
 * @version $Revision: 6 $
 */
public class ResourceBundleRenameEntry_fr extends MecResourceBundle {

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
        {"label.newalias", "Nouvel alias"},
        {"label.newalias.hint", "L''alias à utiliser dans le futur"},
        {"label.keypairpass", "Mot de passe de la clef"},
        {"title", "Renommer un alias ({0})"},
        {"alias.exists.title", "Le renommage d''alias a échoué" },
        {"alias.exists.message", "L''alias \"{0}\" existe déjà dans ce keystore." },
    };

}
