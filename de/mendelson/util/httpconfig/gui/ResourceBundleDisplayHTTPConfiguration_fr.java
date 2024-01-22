//$Header: /as2/de/mendelson/util/httpconfig/gui/ResourceBundleDisplayHTTPConfiguration_fr.java 10    17/01/23 11:06 Heller $ 
package de.mendelson.util.httpconfig.gui;

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
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class ResourceBundleDisplayHTTPConfiguration_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Configuration HTTP côté serveur"},
        {"reading.configuration", "Lire la configuration HTTP..."},
        {"button.ok", "Fermer" },
        {"label.info.configfile", "Cette boîte de dialogue vous montre la configuration HTTP/S côté serveur. Le serveur HTTP fourni a la version <strong>jetty {0}</strong>. "
            + "Vous pouvez configurer les codes et les protocoles dans le fichier \"{1}\" du serveur. Les paramètres de base peuvent être configurés dans "
            + "le fichier \"{2}\" ou directement via les paramètres du serveur. Veuillez redémarrer le serveur pour les modifications à appliquer." },
        {"tab.misc", "General"},
        {"tab.cipher", "Chiffrement TLS"},
        {"tab.protocols", "Protocoles TLS"},
        {"no.ssl.enabled", "La prise en charge SSL n''était pas activée dans le serveur HTTP sous-jacent.\nVeuillez modifier le fichier de configuration {0}\nselon la documentation et redémarrer le serveur." },        
        {"no.embedded.httpserver", "Vous n''avez pas démarré le serveur HTTP sous-jacent.\nAucune information n'est disponible." },                
    };
}