//$Header: /as2/de/mendelson/util/clientserver/log/search/gui/ResourceBundleDialogSearchLogfile_fr.java 3     19/01/23 9:26 Heller $
package de.mendelson.util.clientserver.log.search.gui;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize the mendelson products
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleDialogSearchLogfile_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Parcourir les fichiers journaux sur le serveur"},
        {"no.data.messageid", "**Il n''y a pas de données de journal pour le numéro de message AS2 \"{0}\" dans la période sélectionnée. Veuillez utiliser le numéro complet du message comme chaîne de recherche." },        
        {"no.data.mdnid", "**Il n''y a pas de données de journal pour le numéro MDN \"{0}\" dans la période sélectionnée. Veuillez utiliser le numéro MDN complet comme chaîne de recherche, que vous pouvez trouver dans le journal d''une transmission." },        
        {"no.data.uid", "**Il n'y a pas de données de journal pour le numéro MDN \"{0}\" dans la période sélectionnée. Veuillez utiliser le numéro MDN complet comme chaîne de recherche, que vous pouvez trouver dans le journal d''une transmission." },        
        {"label.startdate", "Début" },
        {"label.enddate", "Fin" },
        {"button.close", "Fermer" },
        {"label.search", "Journal de recherche" },
        {"label.info", "<html>Veuillez définir une période de temps, entrez un numéro de message AS2 complet ou le numéro complet d''un MDN pour trouver toutes les entrées du journal sur le serveur - puis appuyez sur le bouton \"Rechercher le journal\". Vous pouvez définir le numéro défini par l'utilisateur pour chaque transaction lorsque vous envoyez les données au serveur en cours d''exécution depuis la ligne de commande.</html>" },
        {"textfield.preset", "AS2 numéro de message, numéro MDN ou identification définie par l''utilisateur" },
        {"label.messageid", "Numéro de message" },
        {"label.mdnid", "Numéro MDN" },
        {"label.uid", "Nombre défini par l''utilisateur" },
        {"problem.serverside", "Il y avait un problème côté serveur lors de la navigation dans les fichiers journaux: [{0}] {1}" },
    };
}
