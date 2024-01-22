//$Header: /mendelson_business_integration/de/mendelson/util/systemevents/ResourceBundleSystemEventManager_fr.java 5     1.09.21 11:28 Heller $
package de.mendelson.util.systemevents;

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
 * @version $Revision: 5 $
 */
public class ResourceBundleSystemEventManager_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"label.body.clientip", "Adresse IP: {0}"},
        {"label.body.processid", "Numéro de processus: {0}"},
        {"label.body.clientos", "Système d''exploitation: {0}"},
        {"label.body.clientversion", "Version du client: {0}"},
        {"label.body.details", "Détails: {0}"},
        {"label.subject.login.success", "Connexion de l''utilisateur réussie [{0}]"},
        {"label.subject.login.failed", "Échec de la connexion de l''utilisateur [{0}]"},
        {"label.subject.logoff", "Déconnexion de l''utilisateur [{0}]"},
        {"label.error.clientserver", "Problème dans la connexion client-serveur" },
        {"label.body.tlsprotocol", "Protocole TLS: {0}" },
        {"label.body.tlsciphersuite", "Chiffre TLS: {0}" },        
    };
}
