//$Header: /as2/de/mendelson/comm/as2/partner/gui/ResourceBundlePartnerPanel_fr.java 50    19/01/23 10:00 Heller $
package de.mendelson.comm.as2.partner.gui;

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
 * @author E.Pailleau
 * @version $Revision: 50 $
 */
public class ResourceBundlePartnerPanel_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Configuration des partenaires"},
        {"label.name", "Nom"},
        {"label.name.help", "<HTML><strong>Nom</strong><br><br>"
            + "Il s''agit du nom interne du partenaire tel qu''il est utilisé dans le système. Il ne s''agit pas d''une "
            + "valeur spécifique au protocole, mais elle est utilisée pour construire tout nom de fichier ou structure "
            + "de répertoire lié à ce partenaire."
            + "</HTML>"},
        {"label.name.hint", "Nom du partenaire interne"},
        {"label.id", "AS2 id"},
        {"label.id.help", "<HTML><strong>AS2 id</strong><br><br>"
            + "L''identification unique (dans votre réseau de partenaires) utilisée dans le protocole AS2 pour identifier "
            + "ce partenaire. Vous pouvez le choisir librement - assurez-vous simplement qu''il est unique."
            + "</HTML>"},
        {"label.id.hint", "Identification des partenaires (protocole AS2)"},
        {"label.partnercomment", "Commentaire"},
        {"label.url", "URL de réception"},
        {"label.url.help", "<HTML><strong>URL de réception</strong><br><br>"
            + "Il s''agit de l'URL de votre partenaire via laquelle son système AS2 est accessible.<br>"
            + "Veuillez spécifier cette URL au format <strong>PROTOCOL://HOST:PORT/CHEMIN</strong>, où le "
            + "<strong>PROTOCOL</strong> doit être l''un des formats \"http\" ou \"https\". <strong>HOST</strong> "
            + "indique l''hôte du serveur AS2 de votre partenaire. <strong>PORT</strong> est le port de réception de "
            + "votre partenaire. Si elle n''est pas spécifiée, la valeur \"80\" sera fixée. <strong>CHEMIN</strong> est le "
            + "chemin de réception, par exemple \"/as2/HttpReceiver\".</HTML>"},
        {"label.mdnurl", "URL des MDN"},
        {"label.mdnurl.help", "<HTML><strong>URL des MDN</strong> (<strong>M</strong>essage <strong>D</strong>elivery <strong>N</strong>otification)<br><br>"
            + "C''est l''URL que votre partenaire utilisera pour le MDN asynchrone entrant vers cette station locale. Dans le cas synchrone, "
            + "cette valeur n''est pas utilisée, car le MDN est envoyé sur le canal de retour de la connexion sortante.<br>"
            + "Veuillez spécifier cette URL au format <strong>PROTOCOL://HOST:PORT/CHEMIN</strong>. <br><strong>PROTOCOLE</strong> "
            + "doit être l''un de \"http\" ou \"https\".<br><strong>HOST</strong> indique votre propre hôte de serveur AS2.<br>"
            + "<strong>PORT</strong> est le port de réception de votre système AS2. S''il n''est pas spécifié, "
            + "la valeur \"80\" sera définie.<br><strong>CHEMIN</strong> indique le "
            + "chemin de réception, par exemple \"/as2/HttpReceiver\".</HTML>"},
        {"label.signalias.key", "Clef privée (Création de signature)"},
        {"label.signalias.key.help", "<HTML><strong>Clef privée (Création de signature)</strong><br><br>"
            + "Veuillez sélectionner ici une clé privée disponible dans le gestionnaire de certificats (signature/chiffrement) du système.<br>"
            + "Avec cette clé, vous créez une signature numérique pour les messages sortants destinés à tous les partenaires distants."
            + "</HTML>"},
        {"label.cryptalias.key", "Clef privée (Décryptage)"},
        {"label.cryptalias.key.help", "<HTML><strong>Clef privée (Décryptage)</strong><br><br>"
            + "Veuillez sélectionner ici une clé privée disponible dans le gestionnaire de certificats (signature/chiffrement) du système.<br>"
            + "Si les messages entrants de n''importe quel partenaire sont cryptés pour cette station locale, cette clé est utilisée pour le décryptage."
            + "</HTML>"},
        {"label.signalias.cert", "Certificat du partenaire (Vérification de la signature)"},
        {"label.signalias.cert.help", "<HTML><strong>Certificat du partenaire (Vérification de la signature)</strong><br><br>"
            + "Veuillez sélectionner ici un certificat qui est disponible dans le gestionnaire de certificats (signature/chiffrement) du système.<br>"
            + "Si les messages entrants de ce partenaire sont signés numériquement pour une station locale, ce certificat est utilisé pour vérifier cette signature."
            + "</HTML>"},
        {"label.cryptalias.cert", "Certificat du partenaire (Cryptage)"},
        {"label.cryptalias.cert.help", "<HTML><strong>Certificat du partenaire (Cryptage)</strong><br><br>"
            + "Veuillez sélectionner ici un certificat qui est disponible dans le gestionnaire de certificats (signature/chiffrement) du système.<br>"
            + "Si vous souhaitez crypter les messages sortants à destination de ce partenaire, ce certificat sera utilisé pour crypter les données."
            + "</HTML>"},
        {"label.signtype", "Algorithme de signature numérique"},
        {"label.signtype.help", "<HTML><strong>Algorithme de signature numérique</strong><br><br>"
            + "Vous choisissez ici l''algorithme de signature avec lequel les messages sortants destinés à ce partenaire doivent être signés.<br>"
            + "Si vous avez choisi ici un algorithme de signature, un message signé est également attendu en entrée de ce partenaire - l''algorithme de signature est toutefois arbitraire."
            + "</HTML>"},
        {"label.encryptiontype", "Algorithme de chiffrement des messages"},
        {"label.encryptiontype.help", "<HTML><strong>Algorithme de chiffrement des messages</strong><br><br>"
            + "Vous choisissez ici l''algorithme de cryptage avec lequel les messages sortants à destination de ce partenaire doivent être cryptés.<br>"
            + "Si vous avez choisi ici un algorithme de cryptage, un message crypté est également attendu en entrée de ce partenaire - l''algorithme de cryptage est toutefois arbitraire."
            + "</HTML>"},
        {"label.email", "E-mail"},
        {"label.email.help", "<HTML><strong>E-mail</strong><br><br>"
            + "Cette valeur fait partie de la description du protocole AS2 mais n'est en fait actuellement pas du tout utilisée."
            + "</HTML>"},
        {"label.email.hint", "Non utilisé ou validé dans le protocole AS2"},
        {"label.localstation", "Station locale"},
        {"label.localstation.help", "<HTML><strong>Station locale</strong><br><br>"
            + "Il existe deux types de partenaires: Les stations locales et les partenaires distants. Une station locale représente votre propre système."
            + "</HTML>"},
        {"label.compression", "Compresser les messages sortants (nécessite une solution AS2 1.1 en face)"},
        {"label.usecommandonreceipt", "Sur réception de message:"},
        {"label.usecommandonsenderror", "Sur envoi échoué de message:"},
        {"label.usecommandonsendsuccess", "Sur envoi réussi de message:"},
        {"label.keepfilenameonreceipt", "Garder le nom de fichier original sur réception"},
        {"label.keepfilenameonreceipt.help", "<HTML><strong>Garder le nom de fichier original sur réception</strong><br><br>"
            + "Si cette option est activée, le système tente d''extraire le nom de fichier original des messages AS2 entrants "
            + "et d''enregistrer le fichier transmis sous ce nom afin qu''il puisse être traité en conséquence.<br>"
            + "Cette option ne fonctionnera que si l''expéditeur a ajouté les informations relatives au nom de fichier original. Si vous l''activez, veillez à ce que votre partenaire envoie des noms de fichiers uniques.</HTML>"},
        {"label.address", "Adresse"},
        {"label.notes.help", "<HTML><strong>Notes</strong><br><br>"
            + "Vous trouverez ici la possibilité de prendre des notes sur ce partenaire pour votre propre usage."
            + "</HTML>"},
        {"label.contact", "Contact"},
        {"tab.misc", "Divers"},
        {"tab.security", "Sécurité"},
        {"tab.send", "Envoi"},
        {"tab.mdn", "MDN"},
        {"tab.dirpoll", "Scrutation de répertoire"},
        {"tab.receipt", "Réception"},
        {"tab.httpauth", "Authentication HTTP"},
        {"tab.httpheader", "En-tête de HTTP"},
        {"tab.notification", "Notification"},
        {"tab.events", "Post-traitement"},
        {"tab.partnersystem", "Info"},
        {"label.subject", "Sujet du contenu"},
        {"label.subject.help", "<HTML><strong>Sujet du contenu</strong><br><br>$'{'filename} sera remplacé par le nom de fichier send.<br>Cette valeur sera transférée dans l''en-tête HTTP, il y a des restrictions! Veuillez utiliser la norme ISO-8859-1 pour l''encodage des caractères, uniquement des caractères imprimables, pas de caractères spéciaux. CR, LF et TAB sont remplacés par \"\\r\", \"\\n\" et \"\\t\".</HTML>"},
        {"label.contenttype", "Type de contenu"},
        {"label.contenttype.help", "<HTML><strong>Type de contenu</strong><br><br>"
            + "application/EDI-X12<br>"
            + "application/EDIFACT<br>"
            + "application/edi-consent<br>"
            + "application/XML<br><br>"
            + "Le RFC AS2 indique que tous les types de contenu MIME doivent être pris en charge dans l''AS2 - "
            + "mais ce n''est pas une condition obligatoire. Vous ne devez donc pas compter sur le "
            + "système de votre partenaire ou sur le traitement SMIME sous-jacent de l''AS2 de "
            + "mendelson pour gérer d''autres types de contenu que ceux décrits."
            + "</HTML>"},
        {"label.syncmdn", "Utilise des MDN synchrone"},
        {"label.syncmdn.help", "<HTML><strong>MDN synchrone</strong><br><br>"
            + "Le partenaire envoie la confirmation (MDN) sur le canal de retour de votre connexion sortante. "
            + "La connexion sortante reste ouverte pendant que le partenaire décrypte les données et vérifie "
            + "la signature. C''est la raison pour laquelle cette méthode nécessite plus de ressources que "
            + "le traitement MDN asynchrone.</HTML>"},
        {"label.asyncmdn", "Utilise des MDN asynchrone"},
        {"label.asyncmdn.help", "<HTML><strong>MDN asynchrone</strong><br><br>"
            + "Le partenaire établit une nouvelle connexion à votre système pour envoyer une confirmation pour votre "
            + "message sortant. La vérification de la signature et le décryptage des données du côté de votre partenaire "
            + "sont effectués après la fermeture de la connexion entrante. C''est la raison pour laquelle cette méthode "
            + "nécessite moins de ressources que le MDN synchrone.</HTML>"},
        {"label.signedmdn", "Utilise des MDN signés"},
        {"label.signedmdn.help", "<HTML><strong>MDN signés</strong><br><br>"
            + "Ce paramètre vous permet d''indiquer au système partenaire pour les messages AS2 sortants que vous souhaitez un accusé de réception signé (MDN).<br>"
            + "Bien que cette option semble logique au premier abord, elle est malheureusement problématique. En effet, lorsque le MDN du partenaire est reçu, la transaction est terminée. "
            + "Si la vérification de la signature du MDN est effectuée et échoue, il n''est plus possible d'informer le partenaire de ce problème. "
            + "Une interruption de la transaction n''est plus possible - la transaction est déjà terminée. La vérification de la signature du MDN en mode automatique n'a donc aucun sens. "
            + "Le protocole AS2 prescrit ici que l''application doit résoudre ce problème logique, ce qui n''est pas possible.<br>"
            + "La solution AS2 de mendelson affiche un avertissement en cas d''échec de la vérification de la signature MDN.<br><br>"
            + "Il existe encore une particularité de ce réglage : si un problème est survenu lors du traitement côté partenaire, le MDN peut toujours être non signé - indépendamment de ce réglage."
            + "</HTML>"},
        {"label.enabledirpoll", "Activer le sondage d''annuaire"},
        {"label.pollignore.help", "<HTML><strong>Activer le sondage d''annuaire</strong><br><br>"
            + "La surveillance des répertoires ira chercher à intervalles réguliers un nombre défini de fichiers dans le répertoire surveillé et les traitera. "
            + "Il faut s''assurer qu''à ce moment-là, le fichier soit "
            + "présent dans son intégralité. Si vous copiez régulièrement des fichiers dans le répertoire surveillé, il peut arriver que des chevauchements temporels se produisent, "
            + "c''est-à-dire qu''un fichier soit récupéré alors qu''il n'est pas encore entièrement disponible. "
            + "C''est pourquoi, si vous copiez les fichiers dans le répertoire surveillé à l''aide d''une opération non atomique, vous devriez choisir une extension de nom de fichier au moment du processus de copie qui sera ignorée par le processus de surveillance. Une fois que le fichier entier est dans le répertoire surveillé "
            + "répertoire, vous pouvez supprimer l''extension de nom de fichier à l''aide d''une opération atomique (move, mv, rename) et le fichier complet est récupéré. "
            + "<br>La liste des extensions de nom de fichier est une liste d''extensions séparées par des virgules, par exemple \"*.tmp, *.upload\"."
            + "</HTML>"},
        {"label.enabledirpoll.help", "<HTML><strong>Activer le sondage d''annuaire</strong><br><br>"
            + "Si vous activez cette option, le système cherchera automatiquement de nouveaux fichiers dans le répertoire de départ "
            + "pour ce partenaire. Si un nouveau fichier est trouvé, un message AS2 est généré à partir de celui-ci et envoyé au partenaire.<br>"
            + "Veuillez noter que cette méthode de surveillance du répertoire ne peut utiliser que des paramètres généraux "
            + "pour toutes les créations de messages. Si vous souhaitez définir des paramètres spécifiques pour chaque message "
            + "individuellement, veuillez utiliser le processus d''envoi via la ligne de commande.<br>"
            + "En cas de fonctionnement en cluster (HA), vous devez désactiver toutes les surveillances de répertoire, car ce "
            + "processus ne peut pas être synchronisé."
            + "</HTML>"},
        {"label.polldir", "Répertoire de scrutation"},
        {"label.pollinterval", "Intervalle de scrutation"},
        {"label.pollignore", "Ignorer les fichiers"},
        {"label.pollignore.hint", "Liste des fichiers à ignorer, séparés par virgules (caractères génériques autorisés)."},
        {"label.maxpollfiles", "Max fichiers/sondage"},
        {"label.httpauth.message", "Authentification des messages AS2 sortants"},
        {"label.httpauth.none", "Aucune"},
        {"label.httpauth.credentials.message", "Authentification HTTP basique"},
        {"label.httpauth.credentials.message.user", "Utilisateur"},
        {"label.httpauth.credentials.message.pass", "Mot de passe"},
        {"label.httpauth.oauth2.message", "OAuth2"},
        {"label.httpauth.asyncmdn", "Authentification des MDN asynchrones sortants"},
        {"label.httpauth.credentials.asyncmdn", "Authentification HTTP basique"},
        {"label.httpauth.credentials.asyncmdn.user", "Utilisateur"},
        {"label.httpauth.credentials.asyncmdn.pass", "Mot de passe"},
        {"label.httpauth.oauth2.asyncmdn", "OAuth2"},
        {"label.notify.send", "Notifier lors d''un dépassement de quota sur message envoyé"},
        {"label.notify.receive", "Notifier lors d''un dépassement de quota sur message reçu"},
        {"label.notify.sendreceive", "Notifier lors d''un dépassement de quota sur message envoyé ou reçu"},
        {"header.httpheaderkey", "Nom"},
        {"header.httpheadervalue", "Valeur"},
        {"httpheader.add", "Ajouter "},
        {"httpheader.delete", "Éliminer"},
        {"label.as2version", "Version AS2:"},
        {"label.productname", "Nom du produit:"},
        {"label.features", "Fonctionnalités:"},
        {"label.features.cem", "Certificat d'échange via CEM"},
        {"label.features.ma", "Plusieurs pièces jointes"},
        {"label.features.compression", "Compression"},
        {"partnerinfo", "Votre partenaire transmet avec chaque message AS2 quelques informations à propos de ses capacités de système AS2. Il s'agit d'une liste de fonctions qui a été transmise par votre partenaire."},
        {"partnersystem.noinfo", "Aucune information n''est disponible, qu''il y avait déjà une transaction?"},
        {"label.httpversion", "Version du protocole HTTP"},
        {"label.test.connection", "Connexion de test"},
        {"label.mdn.description", "<HTML>Le MDN (Message Delivery Notification) est la confirmation du message AS2. Cette section définit le comportement de votre partenaire pour vos messages AS2 sortants.</HTML>"},
        {"label.algorithmidentifierprotection", "<HTML>Utiliser l''attribut de protection de l''identificateur d''algorithme dans la signature (recommandé), voir RFC 6211</HTML>"},
        {"tooltip.button.editevent", "Modifier l'événement"},
        {"tooltip.button.addevent", "Créer un nouvel événement"},
        {"label.httpauthentication.credentials.help", "<HTML><strong>Authentification d''accès de base HTTP</strong><br><br>"
            + "Veuillez configurer ici l''authentification d''accès de base HTTP si celle-ci est activée du côté "
            + "de votre partenaire (définie dans la RFC 7617). Pour les demandes non authentifiées (données de "
            + "connexion incorrectes, etc.), le système du partenaire distant doit renvoyer un <strong>HTTP 401 "
            + "Unauthorized</strong> status.<br>Si la connexion à votre partenaire nécessite l''authentification du "
            + "client TLS (via des certificats), aucun réglage n''est nécessaire ici. Dans ce cas, veuillez "
            + "importer les certificats du partenaire via le gestionnaire de certificats TLS - le système se "
            + "chargera alors de l''authentification du client TLS."
            + "</HTML>"},};
}
