//$Header: /as2/de/mendelson/util/security/encryption/ResourceBundleEncryptionAS2_fr.java 2     16.09.21 15:30 Heller $
package de.mendelson.util.security.encryption;

import de.mendelson.comm.as2.message.*;
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
 * @version $Revision: 2 $
 */
public class ResourceBundleEncryptionAS2_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {        
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_UNKNOWN, "Inconnu"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_NONE, "Non crypté"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_3DES, "3DES"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_40, "RC2-40"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_64, "RC2-64"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_128, "RC2-128"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_196, "RC2-196"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_UNKNOWN, "RC2"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_128, "AES-128"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_192, "AES-192"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_256, "AES-256"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_128_RSAES_AOEP, "AES-128 (RSAES-OAEP)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_192_RSAES_AOEP, "AES-192 (RSAES-OAEP)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_256_RSAES_AOEP, "AES-256 (RSAES-OAEP)"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_40, "RC4-40"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_56, "RC4-56"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_128, "RC4-128"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_UNKNOWN, "RC4"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_DES, "DES"},
        {"encryption." + EncryptionConstantsAS2.ENCRYPTION_UNKNOWN_ALGORITHM, "Inconnu"},
    };
}