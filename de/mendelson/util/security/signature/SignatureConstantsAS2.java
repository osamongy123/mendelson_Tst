//$Header: /as2/de/mendelson/util/security/signature/SignatureConstantsAS2.java 1     16.09.21 14:27 Heller $
package de.mendelson.util.security.signature;

import java.io.Serializable;

/**
 * Keeps the constant values of the signatures
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class SignatureConstantsAS2 implements Serializable {

    public static final long serialVersionUID = 1L;
        
    public static final int SIGNATURE_UNKNOWN = 0;
    public static final int SIGNATURE_NONE = 1;
    public static final int SIGNATURE_SHA1 = 2;
    public static final int SIGNATURE_MD5 = 3;
    public static final int SIGNATURE_SHA224 = 4;
    public static final int SIGNATURE_SHA256 = 5;
    public static final int SIGNATURE_SHA384 = 6;
    public static final int SIGNATURE_SHA512 = 7;
    public static final int SIGNATURE_SHA1_RSASSA_PSS = 8;
    public static final int SIGNATURE_SHA224_RSASSA_PSS = 9;
    public static final int SIGNATURE_SHA256_RSASSA_PSS = 10;
    public static final int SIGNATURE_SHA384_RSASSA_PSS = 11;
    public static final int SIGNATURE_SHA512_RSASSA_PSS = 12;
    public static final int SIGNATURE_SHA3_224 = 13;
    public static final int SIGNATURE_SHA3_256 = 14;
    public static final int SIGNATURE_SHA3_384 = 15;
    public static final int SIGNATURE_SHA3_512 = 16;    
    public static final int SIGNATURE_SHA3_224_RSASSA_PSS = 17;
    public static final int SIGNATURE_SHA3_256_RSASSA_PSS = 18;
    public static final int SIGNATURE_SHA3_384_RSASSA_PSS = 19;
    public static final int SIGNATURE_SHA3_512_RSASSA_PSS = 20;    
    
}
