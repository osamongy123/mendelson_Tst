//$Header: /as4/de/mendelson/util/clientserver/connectiontest/gui/ResourceBundleDialogConnectionTestResult_fr.java 3     18.11.20 11:42 Helle $
package de.mendelson.util.clientserver.connectiontest.gui;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleDialogConnectionTestResult_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Résultat du test de connexion"},
        {"description." + JDialogConnectionTestResult.CONNECTION_TEST_OFTP2, 
            "Le système a effectué un test de connexion à l'adresse {0}, port {1}. "
            + "Le résultat suivant indique si la connexion a réussi et si un serveur OFTP2 "
            + "fonctionne à cette adresse. Si une connexion TLS doit être utilisée et que cela "
            + "était possible, vous pouvez télécharger les certificats de votre partenaire et les "
            + "importer dans votre keystore."},
        {"description." + JDialogConnectionTestResult.CONNECTION_TEST_AS2, 
            "Le système a effectué un test de connexion à l''adresse {0}, port {1}. "
            + "Le résultat suivant indique si la connexion a réussi et si un serveur "
            + "HTTP fonctionne à cette adresse. Même si le test est réussi, il n''est "
            + "pas certain qu''il s'agisse d''un serveur HTTP normal ou d''un serveur "
            + "AS2. Si une connexion TLS doit être utilisée (HTTPS) et que cela a été "
            + "possible avec succès, vous pouvez télécharger les certificats de votre "
            + "partenaire et les importer dans votre keystore."},
        {"description." + JDialogConnectionTestResult.CONNECTION_TEST_AS4, 
            "Le système a effectué un test de connexion à l''adresse {0}, port {1}. "
            + "Le résultat suivant indique si la connexion a réussi et si un serveur "
            + "HTTP fonctionne à cette adresse. Même si le test est réussi, il n''est "
            + "pas certain qu''il s'agisse d''un serveur HTTP normal ou d''un serveur "
            + "AS4. Si une connexion TLS doit être utilisée (HTTPS) et que cela a été "
            + "possible avec succès, vous pouvez télécharger les certificats de votre "
            + "partenaire et les importer dans votre keystore."},
        {"OK", "[RÉUSSIEUX]"},
        {"FAILED", "[ERREUR]"},
        {"AVAILABLE", "[AVANT-PROPOS]"},
        {"NOT_AVAILABLE", "[NON-EXISTANT]"},
        {"header.ssl", "{0} [Raccordement TLS]"},
        {"header.plain", "{0} [Connexion non sécurisée]"},
        {"no.certificate.plain", "Non disponible (connexion non sécurisée)"},
        {"button.viewcert", "<HTML><div style=\"text-align:center\">Certificat(s) d''importation</div></HTML>"},
        {"button.close", "Fermer"},
        {"label.connection.established", "La simple connexion IP a été établie"},
        {"label.certificates.available.local", "Les certificats partenaires (TLS) sont disponibles dans votre système."},
        {"label.running.oftpservice", "Un service OFTP en cours d''exécution a été trouvé."},
        {"used.cipher", "L''algorithme de cryptage suivant a été utilisé pour le test: \"{0}\"" },          
    };

}
