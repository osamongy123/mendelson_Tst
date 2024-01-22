//$Header: /as2/de/mendelson/util/security/signature/ResourceBundleSignatureAS2.java 1     16.09.21 14:27 Heller $
package de.mendelson.util.security.signature;

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
 * @version $Revision: 1 $
 */
public class ResourceBundleSignatureAS2 extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"signature." + SignatureConstantsAS2.SIGNATURE_UNKNOWN, "Unknown"},
        {"signature." + SignatureConstantsAS2.SIGNATURE_NONE, "No signature"},
        {"signature." + SignatureConstantsAS2.SIGNATURE_MD5, "MD5"},
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA1, "SHA-1"},
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA224, "SHA-224" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA256, "SHA-256" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA384, "SHA-384" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA512, "SHA-512" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA1_RSASSA_PSS, "SHA-1 (RSASSA-PSS)" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA224_RSASSA_PSS, "SHA-224 (RSASSA-PSS)" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA256_RSASSA_PSS, "SHA-256 (RSASSA-PSS)" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA384_RSASSA_PSS, "SHA-384 (RSASSA-PSS)" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA512_RSASSA_PSS, "SHA-512 (RSASSA-PSS)" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA3_224, "SHA3-224" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA3_256, "SHA3-256" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA3_384, "SHA3-384" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA3_512, "SHA3-512" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA3_224_RSASSA_PSS, "SHA3-224 (RSASSA-PSS)" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA3_256_RSASSA_PSS, "SHA3-256 (RSASSA-PSS)" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA3_384_RSASSA_PSS, "SHA3-384 (RSASSA-PSS)" },
        {"signature." + SignatureConstantsAS2.SIGNATURE_SHA3_512_RSASSA_PSS, "SHA3-512 (RSASSA-PSS)" },
        
    };
}