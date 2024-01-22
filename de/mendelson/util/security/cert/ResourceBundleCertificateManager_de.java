//$Header: /as2/de/mendelson/util/security/cert/ResourceBundleCertificateManager_de.java 14    12/12/22 14:18 Heller $
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
 * @version $Revision: 14 $
 */
public class ResourceBundleCertificateManager_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"keystore.reloaded", "Die Zertifikatdatei wurde erneut eingeladen, alle Schlüssel und Zertifikate wurden aktualisiert."},
        {"alias.notfound", "Die Zertifikatdatei beinhaltet kein Zertifikat mit dem Alias \"{0}\"."},
        {"alias.hasno.privatekey", "Die Zertifikatdatei beinhaltet keinen privaten Schlüssel mit dem Alias \"{0}\"."},
        {"alias.hasno.key", "Die Zertifikatdatei beinhaltet keinen Schlüssel mit dem Alias \"{0}\"."},
        {"certificate.not.found.fingerprint", "Das Zertifikat mit dem  SHA-1 Fingerprint \"{0}\" existiert nicht."},
        {"certificate.not.found.fingerprint.withinfo", "Das Zertifikat mit dem  SHA-1 Fingerprint \"{0}\" existiert nicht im System. ({1})" },
        {"certificate.not.found.subjectdn.withinfo", "Das Zertifikat mit dem  subjectDN \"{0}\" existiert nicht im System. ({1})" },
        {"certificate.not.found.ski.withinfo", "Das Zertifikat mit dem  Subject Key Identifier \"{0}\" existiert nicht im System. ({1})" },
        {"certificate.not.found.issuerserial.withinfo", "Das Zertifikat mit dem Issuer \"{0}\" und der Seriennummer \"{1}\" ist erforderlich, das existiert jedoch nicht im System ({2})"},
        {"keystore.read.failure", "Das System kann die hinterlegten Zertifikate/Schlüssel nicht lesen. Fehlermeldung: \"{0}\". Bitte stellen Sie sicher, dass Sie das richtige Keystore Passwort gesetzt haben."},        
        {"event.certificate.added.subject", "{0}: Ein neues Zertifikat wurde hinzugefügt (Alias \"{1}\")" },
        {"event.certificate.added.body", "Ein neues Zertifikat wurde dem System hinzugefügt, mit folgenden Daten:\n\n{0}" },
        {"event.certificate.deleted.subject", "{0}: Ein Zertifikat wurde gelöscht (Alias \"{1}\")" },
        {"event.certificate.deleted.body", "Das folgende Zertifikat wurde aus dem System gelöscht:\n\n{0}" },
        {"event.certificate.modified.subject", "{0}: Der Alias eines Zertifikats wurde verändert" },
        {"event.certificate.modified.body", "Der Zertifikatalias \"{0}\" wuerde verändert zu \"{1}\"\n\n\nDies sind die Daten des Zertifikats:\n\n{2}" },
        {"keystore." + BCCryptoHelper.KEYSTORE_JKS, "SSL/TLS Keystore" },
        {"keystore." + BCCryptoHelper.KEYSTORE_PKCS12, "Verschlüsselungs-/Signaturkeystore" },        
    };
}
