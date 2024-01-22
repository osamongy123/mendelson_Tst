//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2Message_de.java 15    7/03/18 9:35a Heller $
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
 * @version $Revision: 15 $
 */
public class ResourceBundleAS2Message_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"signature." + AS2Message.SIGNATURE_UNKNOWN, "Unbekannt" },
        {"signature." + AS2Message.SIGNATURE_NONE, "Keine Signatur" },
        {"signature." + AS2Message.SIGNATURE_MD5, "MD5" },
        {"signature." + AS2Message.SIGNATURE_SHA1, "SHA-1" },
        {"signature." + AS2Message.SIGNATURE_SHA224, "SHA-224" },
        {"signature." + AS2Message.SIGNATURE_SHA256, "SHA-256" },
        {"signature." + AS2Message.SIGNATURE_SHA384, "SHA-384" },
        {"signature." + AS2Message.SIGNATURE_SHA512, "SHA-512" },
        {"signature." + AS2Message.SIGNATURE_SHA1_RSASSA_PSS, "SHA-1 (RSASSA-PSS)" },
        {"signature." + AS2Message.SIGNATURE_SHA224_RSASSA_PSS, "SHA-224 (RSASSA-PSS)" },
        {"signature." + AS2Message.SIGNATURE_SHA256_RSASSA_PSS, "SHA-256 (RSASSA-PSS)" },
        {"signature." + AS2Message.SIGNATURE_SHA384_RSASSA_PSS, "SHA-384 (RSASSA-PSS)" },
        {"signature." + AS2Message.SIGNATURE_SHA512_RSASSA_PSS, "SHA-512 (RSASSA-PSS)" },
        {"signature." + AS2Message.SIGNATURE_SHA3_224, "SHA3-224" },
        {"signature." + AS2Message.SIGNATURE_SHA3_256, "SHA3-256" },
        {"signature." + AS2Message.SIGNATURE_SHA3_384, "SHA3-384" },
        {"signature." + AS2Message.SIGNATURE_SHA3_512, "SHA3-512" },
        {"signature." + AS2Message.SIGNATURE_SHA3_224_RSASSA_PSS, "SHA3-224 (RSASSA-PSS)" },
        {"signature." + AS2Message.SIGNATURE_SHA3_256_RSASSA_PSS, "SHA3-256 (RSASSA-PSS)" },
        {"signature." + AS2Message.SIGNATURE_SHA3_384_RSASSA_PSS, "SHA3-384 (RSASSA-PSS)" },
        {"signature." + AS2Message.SIGNATURE_SHA3_512_RSASSA_PSS, "SHA3-512 (RSASSA-PSS)" },
        {"encryption." + AS2Message.ENCRYPTION_UNKNOWN, "Unbekannt" },        
        {"encryption." + AS2Message.ENCRYPTION_NONE, "Keine Verschlüsselung" },        
        {"encryption." + AS2Message.ENCRYPTION_3DES, "3DES" },        
        {"encryption." + AS2Message.ENCRYPTION_RC2_40, "RC2-40" },        
        {"encryption." + AS2Message.ENCRYPTION_RC2_64, "RC2-64" },
        {"encryption." + AS2Message.ENCRYPTION_RC2_128, "RC2-128" },
        {"encryption." + AS2Message.ENCRYPTION_RC2_196, "RC2-196" },
        {"encryption." + AS2Message.ENCRYPTION_RC2_UNKNOWN, "RC2" },
        {"encryption." + AS2Message.ENCRYPTION_AES_128, "AES-128" },
        {"encryption." + AS2Message.ENCRYPTION_AES_192, "AES-192" },
        {"encryption." + AS2Message.ENCRYPTION_AES_256, "AES-256" },
        {"encryption." + AS2Message.ENCRYPTION_AES_128_RSAES_AOEP, "AES-128 (RSAES-OAEP)"},
        {"encryption." + AS2Message.ENCRYPTION_AES_192_RSAES_AOEP, "AES-192 (RSAES-OAEP)"},
        {"encryption." + AS2Message.ENCRYPTION_AES_256_RSAES_AOEP, "AES-256 (RSAES-OAEP)"},
        {"encryption." + AS2Message.ENCRYPTION_RC4_40, "RC4-40" },
        {"encryption." + AS2Message.ENCRYPTION_RC4_56, "RC4-56" },
        {"encryption." + AS2Message.ENCRYPTION_RC4_128, "RC4-128" },
        {"encryption." + AS2Message.ENCRYPTION_RC4_UNKNOWN, "RC4" },
        {"encryption." + AS2Message.ENCRYPTION_DES, "DES" },
        {"encryption." + AS2Message.ENCRYPTION_UNKNOWN_ALGORITHM, "Unbekannt" },
        {"compression." + AS2Message.COMPRESSION_NONE, "Keine"},
        {"compression." + AS2Message.COMPRESSION_UNKNOWN, "Unbekannt"},
        {"compression." + AS2Message.COMPRESSION_ZLIB, "ZLIB"},
        {"direction." + AS2MessageInfo.DIRECTION_IN, "Eingehend"},
        {"direction." + AS2MessageInfo.DIRECTION_OUT, "Ausgehend"},
        {"direction." + AS2MessageInfo.DIRECTION_UNKNOWN, "Unbekannt"},
    };
    
}