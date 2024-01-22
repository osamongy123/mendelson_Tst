//$Header: /as2/de/mendelson/util/clientserver/ResourceBundleClientServer_fr.java 2     28/03/22 11:09 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize the mendelson products - if you want to localize
 * eagle to your language, please contact us: localize@mendelson.de
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ResourceBundleClientServer_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"clientserver.start", "Démarrage de l''interface client-serveur {0}, écoute sur le port {1}."},
        {"clientserver.started", "{0} interface client-serveur démarrée."},
    };
}