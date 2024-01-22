//$Header: /as2/de/mendelson/util/security/cert/ResourceBundleCertificateManager_fr.java 11    21.09.18 15:17 Heller $
package de.mendelson.util.security.cert;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.BCCryptoHelper;
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
 * @version $Revision: 11 $
 */
public class ResourceBundleCertificateManager_fr extends MecResourceBundle {
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"keystore.reloaded", "Les clefs privées et les certificats ont été rechargés."},
        {"alias.notfound", "Le porte-clef ne contient aucun certificat sous l''alias \"{0}\"."},
        {"alias.hasno.privatekey", "Le porte-clef ne contient aucune clef privée sous l''alias \"{0}\"."},
        {"alias.hasno.key", "Le porte-clef ne contient aucun objet sous l''alias \"{0}\"."},
        {"certificate.not.found.fingerprint", "Le certificat avec le \"{0}\" d'empreintes SHA-1 n''existe pas."},
        {"certificate.not.found.fingerprint.withinfo", "Le certificat avec le \"{0}\" d'empreintes SHA-1 n''existe pas. ({1})" },
        {"certificate.not.found.subjectdn.withinfo", "Le certificat avec le \"{0}\" subjectDN n''existe pas. ({1})" },
        {"certificate.not.found.ski.withinfo", "Le certificat avec le \"{0}\" Subject Key Identifier n''existe pas. ({1})" },
        {"certificate.not.found.issuerserial.withinfo", "Le certificat avec \"{0}/{1}\" n''existe pas. ({2})"},
        {"keystore.read.failure", "Le système est incapable de lire les certificats. Erreur: \"{0}\". S''il vous plaît vous assurer que vous utilisez le mot de passe keystore correct."},
        {"event.certificate.added.subject", "{0}:Un nouveau certificat a été ajouté (alias \"{1}\")" },
        {"event.certificate.added.body", "Un nouveau certificat a été ajouté au système avec les données suivantes:\n\n{0}" },
        {"event.certificate.deleted.subject", "{0}: Un certificat a été supprimé (alias \"{1}\")" },
        {"event.certificate.deleted.body", "Le certificat suivant a été supprimé du système:\n\n{0}" },
        {"event.certificate.modified.subject", "{0}: Un alias de certificat a été modifié" },
        {"event.certificate.modified.body", "L'alias du certificat \"{0}\" a été changé en \"{1}\"\n\n\nIl s''agit des données du certificat:\n\n{2}" },
        {"keystore." + BCCryptoHelper.KEYSTORE_JKS, "Mémoire de clés SSL/TLS" },
        {"keystore." + BCCryptoHelper.KEYSTORE_PKCS12, "Clé de chiffrement/clé de signature" },
    };
}
