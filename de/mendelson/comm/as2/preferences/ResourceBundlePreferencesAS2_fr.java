//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesAS2_fr.java 7     2/08/22 15:37 Heller $
package de.mendelson.comm.as2.preferences;

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
 * @version $Revision: 7 $
 */
public class ResourceBundlePreferencesAS2_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"TRUE", "allumé" },
        {"FALSE", "désactivé" },
        {"set.to", "a été réglé sur" },
        {"setting.updated", "Réglage a été mis à jour" },
        {"notification.setting.updated", "Les paramètres de notification ont été modifiés." },
        //preferences localized
        {PreferencesAS2.ASYNC_MDN_TIMEOUT, "Délai d''attente pour MDN asynchrone en min"},
        {PreferencesAS2.AUTH_PROXY_PASS, "Données d''accès au proxy HTTP (mot de passe)"},
        {PreferencesAS2.AUTH_PROXY_USE, "Utiliser les données d'accès du proxy HTTP"},
        {PreferencesAS2.AUTH_PROXY_USER, "Données d''accès proxy HTTP (utilisateur)"},
        {PreferencesAS2.AUTO_LOGDIR_DELETE, "Nettoyer automatiquement le répertoire de logs"},
        {PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN, "Nettoyer le répertoire de logs (antérieur à)"},
        {PreferencesAS2.AUTO_MSG_DELETE, "Supprimer automatiquement les anciennes transactions"},
        {PreferencesAS2.AUTO_MSG_DELETE_LOG, "Supprimer les anciennes transactions (entrée du journal)"},
        {PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN, "Supprimer les anciennes transactions (antérieures à)"},
        {PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S, "Supprimer les anciennes transactions (unité de temps en s)"},
        {PreferencesAS2.AUTO_STATS_DELETE, "Supprimer automatiquement les anciennes données statistiques"},
        {PreferencesAS2.AUTO_STATS_DELETE_OLDERTHAN, "Supprimer les statistiques (antérieures à)"},
        {PreferencesAS2.CEM, "Utiliser la CEM"},
        {PreferencesAS2.COLOR_BLINDNESS, "Prise en charge du daltonisme"},
        {PreferencesAS2.COMMUNITY_EDITION, "Édition de la communauté"},
        {PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S, "Reconnexion toutes les n secondes"},
        {PreferencesAS2.COUNTRY, "Pays"},
        {PreferencesAS2.DATASHEET_RECEIPT_URL, "URL de réception de la fiche technique"},
        {PreferencesAS2.DIR_MSG, "Répertoire de base pour les messages"},
        {PreferencesAS2.HTTP_SEND_TIMEOUT, "Délai d''envoi (HTTP/S)"},
        {PreferencesAS2.KEYSTORE, "Fichier de clé de cryptage/signature interne"},
        {PreferencesAS2.KEYSTORE_HTTPS_SEND, "Fichier de clé TLS interne"},
        {PreferencesAS2.KEYSTORE_HTTPS_SEND_PASS, "Fichier de clé TLS interne (mot de passe)"},
        {PreferencesAS2.KEYSTORE_PASS, "Fichier de clé de chiffrement/signature interne (mot de passe)"},
        {PreferencesAS2.LANGUAGE, "Langue du client"},
        {PreferencesAS2.LAST_UPDATE_CHECK, "Dernière vérification de la nouvelle version (temps unix)"},
        {PreferencesAS2.LOG_POLL_PROCESS, "Documenter le processus Poll dans le journal"},
        {PreferencesAS2.MAX_CONNECTION_RETRY_COUNT, "Nombre de tentatives de connexion"},
        {PreferencesAS2.MAX_OUTBOUND_CONNECTIONS, "Nombre maximum de connexions sortantes simultanées"},
        {PreferencesAS2.MAX_INBOUND_CONNECTIONS, "Nombre maximal de connexions entrantes simultanées"}, 
        {PreferencesAS2.PROXY_HOST, "Hôte proxy HTTP"},
        {PreferencesAS2.PROXY_PORT, "Port proxy HTTP"},
        {PreferencesAS2.PROXY_USE, "Utiliser un proxy HTTP pour la connexion sortante"},
        {PreferencesAS2.RECEIPT_PARTNER_SUBDIR, "Utiliser un sous-répertoire par partenaire"},
        {PreferencesAS2.SHOW_HTTPHEADER_IN_PARTNER_CONFIG, "Afficher la gestion des en-têtes HTTP dans le client"},
        {PreferencesAS2.SHOW_QUOTA_NOTIFICATION_IN_PARTNER_CONFIG, "Afficher le quota dans la gestion des partenaires"},
        {PreferencesAS2.WRITE_OUTBOUND_STATUS_FILE, "Créer un fichier d''état pour chaque transaction"}, 
        {PreferencesAS2.TLS_TRUST_ALL_REMOTE_SERVER_CERTIFICATES, "(TLS) Faire confiance à tous les certificats de serveur distants"},      
        {PreferencesAS2.TLS_STRICT_HOST_CHECK, "(TLS) Vérifier l''hôte"},
        {PreferencesAS2.HTTPS_LISTEN_PORT, "Port d''entrée HTTPS"},
        {PreferencesAS2.HTTP_LISTEN_PORT, "Port d''entrée HTTP"},
    };
}
