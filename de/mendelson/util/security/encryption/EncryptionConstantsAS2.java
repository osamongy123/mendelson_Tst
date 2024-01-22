//$Header: /as2/de/mendelson/util/security/encryption/EncryptionConstantsAS2.java 1     16.09.21 15:30 Heller $
package de.mendelson.util.security.encryption;

import java.io.Serializable;

/**
 * Keeps the constant values of the signatures
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class EncryptionConstantsAS2 implements Serializable {

    public static final long serialVersionUID = 1L;
        
    public static final int ENCRYPTION_UNKNOWN = 0;
    public static final int ENCRYPTION_NONE = 1;
    public static final int ENCRYPTION_3DES = 2;
    public static final int ENCRYPTION_RC2_40 = 3;
    public static final int ENCRYPTION_RC2_64 = 4;
    public static final int ENCRYPTION_RC2_128 = 5;
    public static final int ENCRYPTION_RC2_196 = 6;
    public static final int ENCRYPTION_RC2_UNKNOWN = 7;
    public static final int ENCRYPTION_AES_128 = 8;    
    public static final int ENCRYPTION_AES_192 = 9;    
    public static final int ENCRYPTION_AES_256 = 10;    
    public static final int ENCRYPTION_RC4_40 = 11;
    public static final int ENCRYPTION_RC4_56 = 12;
    public static final int ENCRYPTION_RC4_128 = 13;
    public static final int ENCRYPTION_RC4_UNKNOWN = 14;
    public static final int ENCRYPTION_DES = 15;
    public static final int ENCRYPTION_AES_128_RSAES_AOEP = 16;
    public static final int ENCRYPTION_AES_192_RSAES_AOEP = 17;
    public static final int ENCRYPTION_AES_256_RSAES_AOEP = 18;
    public static final int ENCRYPTION_UNKNOWN_ALGORITHM = 99;
    
}
