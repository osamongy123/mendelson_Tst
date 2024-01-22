//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessageParser_fr.java 32    26.08.21 11:30 Heller $
package de.mendelson.comm.as2.message;
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
 * @author S.Heller
 * @author E.Pailleau
 * @version $Revision: 32 $
 */
public class ResourceBundleAS2MessageParser_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {        
        {"mdn.answerto", "Le MDN \"{1}\" est la réponse au message AS2 \"{1}\"." },
        {"mdn.state", "L''état du MDN est [{0}]." },
        {"mdn.details", "Détails du MDN reçu ({0}): \"{1}\"" },
        {"mdn.incoming", "La transmission entrante est un MDN." },
        {"mdn.incoming.ha", "La transmission entrante est un MDN, traitée par [{0}]." }, 
        {"mdn.incoming.relationship", "La transmission entrante est un MDN [{0}]" },
        {"mdn.incoming.relationship.ha", "La transmission entrante est un MDN [{0}], traitée par [{1}]" },
        {"msg.incoming", "La transmission entrante est un message AS2 [{0}], taille du message brut: {1}." },
        {"msg.incoming.ha", "La transmission entrante est un message AS2 [{0}], taille du message brut: {1}, , traitée par [{2}]." },   
        {"msg.incoming.identproblem", "La transmission entrante est un message AS2. Il n''a pas été traitée en raison d''un problème d''identification de partenaire." },   
        {"msg.already.processed", "Un message portant le numéro [{0}] a déjà été traité" },
        {"mdn.signed", "Le MDN est signé ({0})." },
        {"mdn.unsigned.error", "Le MDN n''est pas signé. La configuration stipule que le MDN du partenaire \"{0}\" doit être signé." },
        {"mdn.signed.error", "Le MDN est signé. La configuration stipule que le MDN du partenaire \"{0}\" ne doit pas être signé." },
        {"msg.signed", "Le message AS2 est signé." },
        {"msg.encrypted", "Le message AS2 est crypté." },
        {"msg.notencrypted", "Le message AS2 n''est pas crypté." },
        {"msg.notsigned", "Le message AS2 n''est pas signé." },
        {"mdn.notsigned", "Le MDN n''est pas signé." },
        {"message.signature.ok", "La signature numérique du message AS2 a été vérifiée avec succès." },
        {"mdn.signature.ok", "La signature numérique du MDN a été vérifiée avec succès." },
        {"mdn.signature.failure", "Vérification de signature digitale du MDN échouée - {0}" },
        {"message.signature.failure", "Vérification de signature digitale du message AS2 échouée - {0}" },
        {"message.signature.using.alias", "Utilisation du certificat \"{0}\" pour vérifier la signature du message AS2." },
        {"mdn.signature.using.alias", "Utilisation du certificat \"{0}\" pour vérifier la signature du MDN." },
        {"decryption.done.alias", "Les données du message AS2 ont été décryptées avec la clef \"{0}\", l''algorithme de chiffrement est \"{1}\", l''algorithme de chiffrement cle est \"{2}\"." },
        {"mdn.unexpected.messageid", "Le MDN référence un message AS2 avec l''identifiant \"{0}\" qui est inconnu." },
        {"mdn.unexpected.state", "Le MDN référence le message AS2 avec l''identification \"{0}\", cela n''attend pas un MDN" },
        {"data.compressed.expanded", "Le contenu compressé a vu sa taille passer de {0} à {1}." },
        {"found.attachments", "Trouvé {0} contenus en pièces attachées dans le message." },
        {"decryption.inforequired", "Afin de décrypter les données une clef avec les paramètres suivants est requise:\n{0}" },
        {"decryption.infoassigned", "Une clef avec les paramètres suivants est utilisé pour décrypter les données (alias \"{0}\"):\n{1}" },
        {"signature.analyzed.digest", "L''émetteur a utilisé l''algorithme \"{0}\" pour signer le message." },
        {"signature.analyzed.digest.failed", "Le système n''a pas pu trouver l''algorithme de signature du message AS2 entrant." },
        {"filename.extraction.error", "Extraire noms de fichier originaux n''est pas possible: \"{0}\", ignoré." },
        {"contentmic.match", "Le Message Integrity Code (MIC) assortit le message AS2 envoyé." },
        {"contentmic.failure", "Le Message Integrity Code (MIC) n'assortit pas le message AS2 envoyé (requis: {0}, reçu: {1})." },
        {"found.cem", "Le message reçu est est un message d'échange de certificat (CEM)." },
        {"data.unable.to.process.content.transfer.encoding", "Les données ont été reçues qui n''ont pas pu être traitées. Le codage de transfert de contenu \"{0}\" est inconnue."},
        {"original.filename.found", "Le nom de fichier original a été défini par l''expéditeur comme \"{0}\"." },
        {"original.filename.undefined", "Le nom de fichier original n''a pas été transmis par le canal de message." },
        {"data.not.compressed", "Les données AS2 reçues sont non compressées." },
    };
    
}
