//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageDetails_fr.java 16    1/09/22 14:19 Heller $
package de.mendelson.comm.as2.message.loggui;
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
 * @version $Revision: 16 $
 */
public class ResourceBundleMessageDetails_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        
        {"title", "Détails du message" },
        {"title.cem", "Détails du message de l'échange de certificat (CEM)" },        
        {"transactionstate.ok.send", "<HTML>Le message {0} a été envoyé avec succès au partenaire \"{1}\" - il a envoyé une confirmation correspondante.</HTML>"},
        {"transactionstate.ok.receive", "<HTML>Le message {0} a été reçu avec succès au partenaire \"{1}\". Une confirmation correspondante a été envoyée.</HTML>"},
        {"transactionstate.ok.details", "<HTML>Les données ont été transférées et la transaction a été effectuée avec succès.</HTML>" },
        {"transactionstate.error.unknown", "Une erreur inconnue s'est produite." },
        {"transactionstate.error.out", "<HTML>Vous avez transmis avec succès le message {0} à votre partenaire \"{1}\" - mais il n''a pas été en mesure de le traiter et a répondu par l''erreur [{2}]</HTML>." },
        {"transactionstate.error.in", "<HTML>Vous avez reçu avec succès le message {0} de votre partenaire \"{1}\" - mais votre système n''a pas été en mesure de le traiter et a répondu avec l''erreur [{2}]</HTML>." },
        {"transactionstate.error.unknown-trading-partner", "<HTML>Vous et votre partenaire avez des identificateurs AS2 différents pour les deux partenaires de transmission dans la configuration. Les identificateurs suivants ont été utilisés : \"{0}\" (lecteur de nouvelles), \"{1}\" (destinataire du message)</HTML>" },
        {"transactionstate.error.authentication-failed", "<HTML>Le destinataire du message n' a pas pu vérifier avec succès la signature de l''expéditeur dans les données. C''est généralement un problème de configuration, puisque l''expéditeur et le destinataire doivent utiliser le même certificat ici. Veuillez également consulter les détails du MDN qui se trouvent dans le journal - celui-ci peut contenir des informations supplémentaires.</HTML>" },
        {"transactionstate.error.decompression-failed", "<HTML>Le destinataire du message ne pouvait pas décompresser le message reçu</HTML>" },
        {"transactionstate.error.insufficient-message-security", "<HTML>Le destinataire du message s''attendait à un niveau de sécurité plus élevé pour les données reçues (par exemple, données chiffrées au lieu de données non chiffrées)</HTML>" },
        {"transactionstate.error.unexpected-processing-error", "<HTML>Ceci est un message d''erreur très générique. Pour une raison inconnue, le destinataire n''a pas pu traiter le message.</HTML>" },
        {"transactionstate.error.decryption-failed", "<HTML>Le destinataire du message n'a pas pu décrypter le message. Habituellement, c''est un problème de configuration, est-ce que l''expéditeur utilise le bon certificat de cryptage?</HTML>" },
        {"transactionstate.error.connectionrefused", "<HTML>Vous avez essayé d'atteindre le système partenaire. Soit cela a échoué, soit votre partenaire n''a pas répondu dans le délai imparti avec une confirmation.</HTML>" },
        {"transactionstate.error.connectionrefused.details", "<HTML>Cela peut être un problème d''infrastructure, votre système partenaire n'est pas en cours d''exécution ou vous avez entré la mauvaise URL dans la configuration? Si les données ont été transférées et que votre partenaire ne les a pas confirmées, avez-vous choisi une fenêtre de confirmation trop petite?</HTML>" },
        {"transactionstate.error.asyncmdnsend", "<HTML>Un message contenant une demande de MDN asynchrone a été reçu et traité avec succès, mais votre système n''a pas pu renvoyer le MDN asynchrone ou il n' a pas été accepté par le système partenaire.</HTML>" },
        {"transactionstate.error.asyncmdnsend.details", "<HTML>L''expéditeur du message AS2 transmet l''URL à laquelle il doit renvoyer le MDN - soit ce système n'est pas joignable (problème d''infrastructure ou système partenaire a échoué?) soit le système partenaire n''a pas accepté le MDN asynchrone et a répondu par un HTTP 400.</HTML>" },
        {"transactionstate.pending", "Cette transaction est en attente." },
        {"transactiondetails.outbound.insecure", "Il s''agit d''une connexion sortante non sécurisée, vous envoyez des données au partenaire \"{0}\"." },
        {"transactiondetails.outbound.secure", "Il s''agit d''une connexion sortante sécurisée, vous envoyez des données au partenaire \"{0}\"." },
        {"transactiondetails.inbound.insecure", "Il s''agit d'une connexion entrante non sécurisée, vous recevez les données du partenaire \"{0}\"." },
        {"transactiondetails.inbound.secure", "Il s''agit d'une connexion entrante sécurisée, vous recevez les données du partenaire \"{0}\"." },
        {"transactiondetails.outbound.sync", " Vous recevez la confirmation directement en réponse sur le canal arrière de votre connexion sortante (MDN synchrone)." },
        {"transactiondetails.outbound.async", " Votre partenaire établit une nouvelle connexion avec vous pour confirmation (MDN asynchrone)." },
        {"transactiondetails.inbound.sync", " Vous envoyez la confirmation directement en réponse sur le canal arrière de la connexion entrante (MDN synchrone)." },
        {"transactiondetails.inbound.async", " Vous envoyez la confirmation en établissant une nouvelle connexion au partenaire (MDN asynchrone)." },        
        {"button.ok", "Valider" },
        {"header.timestamp", "Date" },
        {"header.messageid", "Réf No" },
        {"message.raw.decrypted", "Données brutes (non décrypté)" },
        {"message.header", "Entête message" },
        {"message.payload", "Contenu transféré" },
        {"message.payload.multiple", "Contenu ({0})" },
        {"tab.log", "Log de l''instance de ce message" },
        {"header.encryption", "Cryptage" },
        {"header.signature", "Signature" },
        {"header.senderhost", "Emetteur" },
        {"header.useragent", "Serveur AS2" },
    };
    
}
