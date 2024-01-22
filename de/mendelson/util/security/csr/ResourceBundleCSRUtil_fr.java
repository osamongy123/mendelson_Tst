//$Header: /as2/de/mendelson/util/security/csr/ResourceBundleCSRUtil_fr.java 5     4/06/18 1:35p Heller $
package de.mendelson.util.security.csr;
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
 * @version $Revision: 5 $
 */
public class ResourceBundleCSRUtil_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {                
        {"verification.failed", "L''opération a échoué - Vérification de la CSR created a échoué" },
        {"no.certificates.in.reply", "L''opération a échoué - Aucun certificat de la réponse de la CSR, incapable de patcher la clé" },
        {"missing.cert.in.trustchain", "L''opération a échoué - Le système n''a pas établi de la chaîne de confiance de la réponse.\nVeuillez importer le certificat avec le \nissuer\n {0} keystore du premier." },
        {"response.chain.incomplete", "L''opération a échoué - La chaîne de certificats de la réponse est incomplète" },
        {"response.verification.failed", "L''opération a échoué - Problème de vérification de la chaîne de certificats de la réponse: {0}" },
        {"response.public.key.does.not.match", "L''opération a échoué - Ce n''est pas la solution CA de cette clé." },
    };

}