//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2Gui_fr.java 45    17/01/23 14:16 Heller $
package de.mendelson.comm.as2.client;

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
 * @author E.Pailleau
 * @version $Revision: 45 $
 */
public class ResourceBundleAS2Gui_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"menu.file", "Fichier"},
        {"menu.file.exit", "Fermer"},
        {"menu.file.partner", "Partenaire"},
        {"menu.file.datasheet", "Créer une fiche de communication"},
        {"menu.file.certificates", "Certificats"},
        {"menu.file.certificate", "Certificats"},
        {"menu.file.certificate.signcrypt", "Sign/Cryptage"},
        {"menu.file.certificate.ssl", "TLS"},
        {"menu.file.cem", "Certificat d'échange présentation (CEM)"},
        {"menu.file.cemsend", "Certificats d''échange avec des partenaires (CEM)"},
        {"menu.file.statistic", "Statistiques"},
        {"menu.file.quota", "Quota"},
        {"menu.file.serverinfo", "Affichage Configuration du serveur HTTP"},
        {"menu.file.systemevents", "Evénements système"},
        {"menu.file.searchinserverlog", "Rechercher journal"},        
        {"menu.file.preferences", "Préférences"},
        {"menu.file.send", "Envoyer un fichier à un partenaire"},
        {"menu.file.resend", "Envoyer en tant que nouvelle transaction"},
        {"menu.file.resend.multiple", "Envoyer en tant que nouvelles transactions"},
        {"menu.file.migrate.hsqldb", "Migrer de la HSQLDB"},
        {"menu.file.ha", "Exemples de haute disponibilité"},
        {"menu.help", "Aide"},
        {"menu.help.about", "A propos"},
        {"menu.help.supportrequest", "Demande de soutien"},
        {"menu.help.shop", "mendelson online shop"},
        {"menu.help.helpsystem", "Système d''aide"},
        {"menu.help.forum", "Forum"},
        {"details", "Détails du message"},
        {"filter.showfinished", "Voir les terminés"},
        {"filter.showpending", "Voir les en-cours"},
        {"filter.showstopped", "Voir les stoppés"},
        {"filter.none", "-- Aucun --"},
        {"filter.partner", "Filtrer le partenaire:"},
        {"filter.localstation", "Filtrer le station locale:"},
        {"filter.direction", "Filtrer le direction:"},
        {"filter.direction.inbound", "Entrer"},
        {"filter.direction.outbound", "Sortant"},
        {"filter", "Filtrer"},
        {"filter.use", "Utiliser le filtre de temps" },
        {"filter.from", "De:" },
        {"filter.to", "Jusqu''à:" },
        {"keyrefresh", "Recharger clés"},
        {"configurecolumns", "Colonnes" },
        {"delete.msg", "Suppression"},
        {"stoprefresh.msg", "Figer le rafraîchissement"},
        {"dialog.msg.delete.message", "Voulez-vous vraiment supprimer de manière permanente les messages sélectionnés ?"},
        {"dialog.msg.delete.title", "Suppression"},
        {"msg.delete.success.single", "{0} message a été supprimé avec succès" },
        {"msg.delete.success.multiple", "{0} messages ont été supprimés avec succès" },
        {"welcome", "Bienvenue, {0}"},
        {"fatal.error", "Erreur"},
        {"warning.refreshstopped", "Le rafraîchissement de l''interface a été arrêté."},
        {"tab.welcome", "Nouveautés et mises à jour"},
        {"tab.transactions", "Transactions"},
        {"new.version", "Une nouvelle version est disponible. Cliquez ici pour la télécharger."},
        {"new.version.logentry.1", "Une nouvelle version est disponible."},
        {"new.version.logentry.2", "Se il vous plaît visitez {0} pour le télécharger."}, 
        {"dbconnection.failed.message", "Incapable d''établir une connexion DB au serveur AS2: {0}"},
        {"dbconnection.failed.title", "Impossible de se connecter"},
        {"login.failed.client.incompatible.message", "Le serveur de rapports que ce client est incompatible. Veuillez utiliser la version du client approprié."},
        {"login.failed.client.incompatible.title", "Login rejeté"},
        {"uploading.to.server", "Téléchargement sur le serveur"},
        {"refresh.overview", "Rafraîchissant"},
        {"dialog.resend.message", "Voulez-vous vraiment de renvoyer les données de la transaction sélectionnée?"},
        {"dialog.resend.message.multiple", "Voulez-vous vraiment de renvoyer les données des {0} transactions sélectionnées?"},
        {"dialog.resend.title", "Transaction renvoyer"},        
        {"logputput.disabled", "** La sortie dans le journal a été désactivé **"},
        {"logputput.enabled", "** La sortie du journal a été activé **"},
        {"resend.failed.nopayload", "Renvoi en cas d''échec d''une nouvelle transaction: La transaction sélectionnée {0} n''a pas de données utilisateur." },
        {"server.answer.timeout.title", "Timeout dans la connexion client-serveur" },
        {"server.answer.timeout.details", "Le serveur ne répond pas dans le délai défini - la charge est-elle trop élevée?" },
    };
}