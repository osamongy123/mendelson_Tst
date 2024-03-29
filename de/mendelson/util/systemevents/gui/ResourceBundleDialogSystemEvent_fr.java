//$Header: /as2/de/mendelson/util/systemevents/gui/ResourceBundleDialogSystemEvent_fr.java 9     5/01/23 12:24 Heller $
package de.mendelson.util.systemevents.gui;

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
 * @version $Revision: 9 $
 */
public class ResourceBundleDialogSystemEvent_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Visualiseur d''�v�nements syst�me"},
        {"label.user", "Propri�taire"},
        {"label.host", "H�te"},
        {"label.id", "Id de l''�v�nement"},
        {"label.date", "Date"},
        {"label.type", "Type"},
        {"label.category", "Cat�gorie"},
        {"header.timestamp", "Horodatage"},
        {"header.type", "Type"},
        {"header.category", "Cat�gorie"},
        {"user.server.process", "Processus serveur" },
        {"label.startdate", "D�but" },
        {"label.enddate", "Fin" },
        {"no.data", "Aucun �v�nement syst�me ne correspond � la s�lection de date/type en cours." },  
        {"label.freetext", "Rechercher du texte" },
        {"label.freetext.hint", "Identifiant complet de l''�v�nement ou recherche textuelle dans le corps et le sujet" },
        {"category.all", "-- Tous --" },      
        {"label.close", "Fermer" },
        {"label.search", "<HTML><div style=\"text-align:center\">Recherche par �v�nement</div></HTML>" },
        {"label.resetfilter", "R�initialiser le filtre" },
    };
}
