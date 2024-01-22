//$Header: /as2/de/mendelson/util/security/cert/clientserver/UploadRequestKeystore.java 6     12/12/22 14:18 Heller $
package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.clientserver.clients.datatransfer.UploadRequestFile;
import de.mendelson.util.security.cert.KeystoreStorageImplFile;
import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class UploadRequestKeystore extends UploadRequestFile implements Serializable {

    public static final int KEYSTORE_TYPE_SSL = KeystoreStorageImplFile.KEYSTORE_USAGE_SSL;
    public static final int KEYSTORE_TYPE_ENC_SIGN = KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN;
    
    public static final long serialVersionUID = 1L;

    private final String keystoreStorageType;
    private final int keystoreUsage;

    public UploadRequestKeystore(final int KEYSTORE_USAGE, final String KEYSTORE_STORAGE_TYPE) {
        this.keystoreStorageType = KEYSTORE_STORAGE_TYPE;
        this.keystoreUsage = KEYSTORE_USAGE;
    }

    @Override
    public String toString() {
        return ("Upload request keystore");
    }

    /**
     * @return the keystoreType
     */
    public String getKeystoreStorageType() {
        return keystoreStorageType;
    }
    
    public int getKeystoreUsage(){
        return( this.keystoreUsage);
    }
    

}
