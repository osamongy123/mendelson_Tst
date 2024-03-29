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
        {"no.data.messageid", "**Il n''y a pas de donn�es de journal pour le num�ro de message AS2 \"{0}\" dans la p�riode s�lectionn�e. Veuillez utiliser le num�ro complet du message comme cha�ne de recherche." },        
        {"no.data.mdnid", "**Il n''y a pas de donn�es de journal pour le num�ro MDN \"{0}\" dans la p�riode s�lectionn�e. Veuillez utiliser le num�ro MDN complet comme cha�ne de recherche, que vous pouvez trouver dans le journal d''une transmission." },        
        {"no.data.uid", "**Il n'y a pas de donn�es de journal pour le num�ro MDN \"{0}\" dans la p�riode s�lectionn�e. Veuillez utiliser le num�ro MDN complet comme cha�ne de recherche, que vous pouvez trouver dans le journal d''une transmission." },        
        {"label.startdate", "D�but" },
        {"label.enddate", "Fin" },
        {"button.close", "Fermer" },
        {"label.search", "Journal de recherche" },
        {"label.info", "<html>Veuillez d�finir une p�riode de temps, entrez un num�ro de message AS2 complet ou le num�ro complet d''un MDN pour trouver toutes les entr�es du journal sur le serveur - puis appuyez sur le bouton \"Rechercher le journal\". Vous pouvez d�finir le num�ro d�fini par l'utilisateur pour chaque transaction lorsque vous envoyez les donn�es au serveur en cours d''ex�cution depuis la ligne de commande.</html>" },
        {"textfield.preset", "AS2 num�ro de message, num�ro MDN ou identification d�finie par l''utilisateur" },
        {"label.messageid", "Num�ro de message" },
        {"label.mdnid", "Num�ro MDN" },
        {"label.uid", "Nombre d�fini par l''utilisateur" },
        {"problem.serverside", "Il y avait un probl�me c�t� serveur lors de la navigation dans les fichiers journaux: [{0}] {1}" },
    };
}
