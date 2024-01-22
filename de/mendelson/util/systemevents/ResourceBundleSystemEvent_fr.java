//$Header: /as2/de/mendelson/util/systemevents/ResourceBundleSystemEvent_fr.java 24    24/02/22 17:04 Heller $
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
 * @version $Revision: 24 $
 */
public class ResourceBundleSystemEvent_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"type." + SystemEvent.TYPE_CERTIFICATE_ADD, "Certificat (ajouter)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_ANY, "Certificat"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_DEL, "Certificat (supprimer)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_EXCHANGE_ANY, "Certificat (échange)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_EXCHANGE_REQUEST_RECEIVED, "Certificat (échange demande entrante)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_EXPIRE, "Certificat (expire)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_MODIFY, "Certificat (alias modifié)"},
        {"type." + SystemEvent.TYPE_CONNECTIVITY_ANY, "Connectivité"},
        {"type." + SystemEvent.TYPE_CONNECTIVITY_TEST, "Test de connexion"},
        {"type." + SystemEvent.TYPE_DATABASE_ANY, "Base de données"},
        {"type." + SystemEvent.TYPE_DATABASE_CREATION, "Base de données (Création)"},
        {"type." + SystemEvent.TYPE_DATABASE_UPDATE, "Base de données (Mise à jour)"},
        {"type." + SystemEvent.TYPE_DATABASE_INITIALIZATION, "Base de données (Initialisation)"},
        {"type." + SystemEvent.TYPE_NOTIFICATION_ANY, "Notification"},
        {"type." + SystemEvent.TYPE_NOTIFICATION_SEND_FAILED, "Envoi de la notification (échec)"},
        {"type." + SystemEvent.TYPE_NOTIFICATION_SEND_SUCCESS, "Envoi de l'avis (succès)"},
        {"type." + SystemEvent.TYPE_PARTNER_ADD, "Partenaire (ajouter)"},
        {"type." + SystemEvent.TYPE_PARTNER_DEL, "Partenaire (supprimer)"},
        {"type." + SystemEvent.TYPE_PARTNER_MODIFY, "Partenaire (modifier)"},
        {"type." + SystemEvent.TYPE_QUOTA_ANY, "Contingent"},
        {"type." + SystemEvent.TYPE_QUOTA_RECEIVE_EXCEEDED, "Contingent dépassé"},
        {"type." + SystemEvent.TYPE_QUOTA_SEND_EXCEEDED, "Contingent dépassé"},
        {"type." + SystemEvent.TYPE_QUOTA_RECEIVE_EXCEEDED, "Contingent dépassé"},
        {"type." + SystemEvent.TYPE_SERVER_CONFIGURATION_CHANGED, "Configuration modifiée"},
        {"type." + SystemEvent.TYPE_SERVER_CONFIGURATION_ANY, "Configuration"},
        {"type." + SystemEvent.TYPE_SERVER_CONFIGURATION_CHECK, "Contrôle de configuration"},
        {"type." + SystemEvent.TYPE_SERVER_COMPONENTS_ANY, "Elément de serveur"},
        {"type." + SystemEvent.TYPE_MAIN_SERVER_RUNNING, "Serveur est en cours d''exécution"},
        {"type." + SystemEvent.TYPE_MAIN_SERVER_SHUTDOWN, "Arrêt du serveur"},
        {"type." + SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN, "Démarrage du serveur"},
        {"type." + SystemEvent.TYPE_DATABASE_SERVER_STARTUP_BEGIN, "Démarrage du serveur de base de données"},
        {"type." + SystemEvent.TYPE_DATABASE_SERVER_RUNNING, "Serveur de base de données en cours d'exécution"},
        {"type." + SystemEvent.TYPE_DATABASE_SERVER_SHUTDOWN, "Arrêt du serveur de base de données"},
        {"type." + SystemEvent.TYPE_HTTP_SERVER_STARTUP_BEGIN, "Démarrage du serveur HTTP"},
        {"type." + SystemEvent.TYPE_HTTP_SERVER_RUNNING, "Serveur HTTP en cours d'exécution"},
        {"type." + SystemEvent.TYPE_HTTP_SERVER_SHUTDOWN, "Arrêt du serveur HTTP"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_STARTUP_BEGIN, "Démarrage du serveur TRFC"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_RUNNING, "Serveur TRFC en cours d'exécution"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_SHUTDOWN, "Arrêt du serveur TRFC"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_STATE, "Statut du serveur TRFC"},
        {"type." + SystemEvent.TYPE_SCHEDULER_SERVER_STARTUP_BEGIN, "Démarrage de l'ordonnanceur"},      
        {"type." + SystemEvent.TYPE_SCHEDULER_SERVER_RUNNING, "Planificateur en cours d''exécution"},      
        {"type." + SystemEvent.TYPE_SCHEDULER_SERVER_SHUTDOWN, "Arrêt de l''ordonnanceur"},  
        {"type." + SystemEvent.TYPE_TRANSACTION_ANY, "Transaction"},
        {"type." + SystemEvent.TYPE_TRANSACTION_ERROR, "Transaction (erreur)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_REJECTED_RESEND, "Transaction (réexpédition rejetée)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_DUPLICATE_MESSAGE, "Transaction (double message)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_DELETE, "Transaction (supprimer)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_CANCEL, "Transaction (annuler)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_RESEND, "Transaction (renvoyer)"},
        {"type." + SystemEvent.TYPE_PROCESSING_ANY, "Traitement des données"},
        {"type." + SystemEvent.TYPE_PRE_PROCESSING, "Prétraitement"},
        {"type." + SystemEvent.TYPE_POST_PROCESSING, "Post-traitement"},
        {"type." + SystemEvent.TYPE_LICENSE_ANY, "Licence"},
        {"type." + SystemEvent.TYPE_LICENSE_EXPIRE, "License expiration"},
        {"type." + SystemEvent.TYPE_LICENSE_UPDATE, "License actualisation"},
        {"type." + SystemEvent.TYPE_FILE_OPERATION_ANY, "Opération sur fichier"},
        {"type." + SystemEvent.TYPE_FILE_DELETE, "Fichier (supprimer)"},
        {"type." + SystemEvent.TYPE_FILE_MOVE, "Fichier (déplacer)"},
        {"type." + SystemEvent.TYPE_FILE_COPY, "Fichier (copie)"},
        {"type." + SystemEvent.TYPE_MKDIR, "Répertoire (créer)"},
        {"type." + SystemEvent.TYPE_DIRECTORY_MONITORING_STATE_CHANGED, "Surveillance du répertoire (statut modifié)"},
        {"type." + SystemEvent.TYPE_CLIENT_ANY, "Interface utilisateur"},
        {"type." + SystemEvent.TYPE_CLIENT_LOGIN_FAILURE, "Connexion utilisateur (échec)"},
        {"type." + SystemEvent.TYPE_CLIENT_LOGIN_SUCCESS, "Connexion utilisateur (succès)"},
        {"type." + SystemEvent.TYPE_CLIENT_LOGOFF, "Déconnexion utilisateur"},
        {"type." + SystemEvent.TYPE_OTHER, "Autre"},
        {"type." + SystemEvent.TYPE_PORT_LISTENER, "L''auditeur du port"},
        {"origin." + SystemEvent.ORIGIN_SYSTEM, "Système" },
        {"origin." + SystemEvent.ORIGIN_TRANSACTION, "Transaction" },
        {"origin." + SystemEvent.ORIGIN_USER, "Utilisateur" },
        {"severity." + SystemEvent.SEVERITY_ERROR, "Erreur"},
        {"severity." + SystemEvent.SEVERITY_WARNING, "Avertissement"},
        {"severity." + SystemEvent.SEVERITY_INFO, "Info"},
        {"category." + SystemEvent.CATEGORY_LICENSE, "Déclenchement" },
        {"category." + SystemEvent.CATEGORY_CERTIFICATE, "Brevet" },
        {"category." + SystemEvent.CATEGORY_CONFIGURATION, "Konfiguration" },
        {"category." + SystemEvent.CATEGORY_CONNECTIVITY, "Liaison" },
        {"category." + SystemEvent.CATEGORY_DATABASE, "Banque de données" },
        {"category." + SystemEvent.CATEGORY_NOTIFICATION, "Notification" },
        {"category." + SystemEvent.CATEGORY_OTHER, "Autre" },
        {"category." + SystemEvent.CATEGORY_PROCESSING, "Traitement des données" },
        {"category." + SystemEvent.CATEGORY_QUOTA, "Conditionnel" },
        {"category." + SystemEvent.CATEGORY_SERVER_COMPONENTS, "Composant Serveur" },
        {"category." + SystemEvent.CATEGORY_TRANSACTION, "Transaction"},
        {"category." + SystemEvent.CATEGORY_FILE_OPERATION, "Opération sur fichier" },
        {"category." + SystemEvent.CATEGORY_CLIENT_OPERATION, "Opération interface utilisateur" },
    };
}
