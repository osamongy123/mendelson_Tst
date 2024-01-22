//$Header: /as2/de/mendelson/util/httpconfig/server/ResourceBundleHTTPServerConfigProcessor_fr.java 13    17/01/23 11:55 Heller $
package de.mendelson.util.httpconfig.server;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 *
 * @author S.Heller
 * @version $Revision: 13 $
 */
public class ResourceBundleHTTPServerConfigProcessor_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"http.server.config.listener", "Port {0} ({1}), Relié à l''adaptateur réseau {2}"},
        {"http.server.config.keystorepath", "TLS keystore: \"{0}\""},
        {"http.server.config.clientauthentication", "Le serveur requiert une authentification client TLS: {0}"},
        {"external.ip", "IP externe: {0} / {1}"},
        {"external.ip.error", "IP externe: -Ne peut pas être détecté-"},
        {"http.receipturls", "URL de réception complète qui sont possibles dans la configuration actuelle:"},
        {"http.serverstateurl", "Affichage de l''état du serveur:"},
        {"http.deployedwars", "Fichiers war actuellement déployés dans le serveur HTTP (fonctionnalité Servlet):"},
        {"webapp._unknown", "Servlet inconnu"},
        {"webapp.as2.war", "mendelson AS2 Servlet de récepteur"},
        {"webapp.as4.war", "mendelson AS4 Servlet de récepteur"},
        {"webapp.as2api.war", "mendelson AS2 REST API"},
        {"webapp.as4api.war", "mendelson AS4 REST API"},
        {"webapp.oftp2api.war", "mendelson OFTP2 REST API"},
        {"webapp.webas2.war", "mendelson AS2 Server Web Monitoring"},
        {"webapp.as2-sample.war", "Exemples d''API AS2 mendelson"},
        {"webapp.as4-sample.war", "Exemples d''API AS4 mendelson"},
        {"info.cipher", "Les codes suivants sont pris en charge par le serveur HTTP sous-jacent.\nLes modèles supportés dépendent de votre Java VM ({1}).\nVous pouvez désactiver les différents chiffres dans le fichier de configuration\n(\"{0}\")."},
        {"info.cipher.howtochange", "Pour désactiver certains chiffres pour les connexions entrantes, veuillez modifier le fichier de configuration de votre serveur HTTP intégré ({0}) avec un éditeur de texte. Veuillez rechercher la chaîne <Set name=\"ExcludeCipherSuites\">, ajouter le chiffre à exclure et redémarrer le programme."},
        {"info.protocols", "Les protocoles suivants sont pris en charge par le serveur HTTP sous-jacent.\nLes protocoles pris en charge dépendent de votre VM Java utilisé ({1}). Le fournisseur de sécurité TLS utilisé est {2}.\nVous pouvez désactiver les protocoles individuels dans le fichier de configuration\n(\"{0}\")."},
        {"info.protocols.howtochange", "Pour désactiver certains protocoles en entrée, veuillez modifier le fichier de configuration de votre serveur HTTP intégré ({0}) avec un éditeur de texte. Veuillez rechercher la chaîne <Set name=\"ExcludeProtocols\">, ajouter le protocole à exclure et redémarrer le programme."},
    };
}
