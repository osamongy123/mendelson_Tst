//$Header: /as2/de/mendelson/util/security/cert/KeystoreStorageImplFile.java 18    16/12/22 13:41 Heller $
package de.mendelson.util.security.cert;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.KeyStoreUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Keystore storage implementation that relies on a keystore file
 *
 * @author S.Heller
 * @version $Revision: 18 $
 */
public class KeystoreStorageImplFile implements KeystoreStorage {

    public static final int KEYSTORE_USAGE_SSL = 1;
    public static final int KEYSTORE_USAGE_ENC_SIGN = 2;
    public static final String KEYSTORE_STORAGE_TYPE_JKS = BCCryptoHelper.KEYSTORE_JKS;
    public static final String KEYSTORE_STORAGE_TYPE_PKCS12 = BCCryptoHelper.KEYSTORE_PKCS12;
    
    private KeyStore keystore = null;
    private final char[] keystorePass;
    private final String keystoreFilename;
    private final KeyStoreUtil keystoreUtil = new KeyStoreUtil();
    private final MecResourceBundle rb;
    private int keystoreUsage = KEYSTORE_USAGE_ENC_SIGN;
    private final String keystoreStorageType;

    /**
     * @param keystoreFilename
     * @param keystorePass
     * @param keystoreType keystore type as defined in the class BCCryptoHelper
     */
    public KeystoreStorageImplFile(String keystoreFilename, char[] keystorePass, final int KEYSTORE_USAGE,
            final String KEYSTORE_STORAGE_TYPE) throws Exception {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleKeystoreStorage.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.keystoreFilename = keystoreFilename;
        this.keystorePass = keystorePass;
        this.keystoreUsage = KEYSTORE_USAGE;
        this.keystoreStorageType = KEYSTORE_STORAGE_TYPE;
        Path keystoreFile = Paths.get(this.keystoreFilename);
        if (!keystoreFile.toFile().canRead()) {
            throw new Exception(this.rb.getResourceString("error.readaccess", this.keystoreFilename));
        }
        if (!Files.exists(keystoreFile)) {
            throw new Exception(this.rb.getResourceString("error.filexists", this.keystoreFilename));
        }
        if (!keystoreFile.toFile().isFile()) {
            throw new Exception(this.rb.getResourceString("error.notafile", this.keystoreFilename));
        }
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        this.keystore = cryptoHelper.createKeyStoreInstance(this.keystoreStorageType);
        this.keystoreUtil.loadKeyStore(this.keystore, this.keystoreFilename, this.keystorePass);
    }

    @Override
    public void save() throws Exception {
        if (this.keystore == null) {
            //internal error, should not happen
            throw new Exception(this.rb.getResourceString("error.save.notloaded"));
        }
        this.keystoreUtil.saveKeyStore(this.keystore, this.keystorePass, this.keystoreFilename);
    }

    @Override
    public Key getKey(String alias) throws Exception {
        Key key = this.keystore.getKey(alias, this.keystorePass);
        return (key);
    }

    @Override
    public Certificate[] getCertificateChain(String alias) throws Exception {
        Certificate[] chain = this.keystore.getCertificateChain(alias);
        return (chain);
    }

    @Override
    public X509Certificate getCertificate(String alias) throws Exception {
        X509Certificate certificate = (X509Certificate) this.keystore.getCertificate(alias);
        return (certificate);
    }

    @Override
    public void renameEntry(String oldAlias, String newAlias, char[] keypairPass) throws Exception {
        KeyStoreUtil keystoreUtility = new KeyStoreUtil();
        keystoreUtility.renameEntry(this.keystore, oldAlias, newAlias, keypairPass);
    }

    @Override
    public KeyStore getKeystore() {
        return (this.keystore);
    }

    @Override
    public char[] getKeystorePass() {
        return (this.keystorePass);
    }

    @Override
    public void deleteEntry(String alias) throws Exception {
        if (this.keystore == null) {
            //internal error, should not happen
            throw new Exception(this.rb.getResourceString("error.delete.notloaded"));
        }
        this.keystore.deleteEntry(alias);
    }

    @Override
    public Map<String, Certificate> loadCertificatesFromKeystore() throws Exception {   
        //recreate keystore object
        this.keystoreUtil.loadKeyStore(this.keystore, this.keystoreFilename, this.keystorePass);
        Map<String, Certificate> certificateMap = this.keystoreUtil.getCertificatesFromKeystore(this.keystore);
        return (certificateMap);
    }

    @Override
    public boolean isKeyEntry(String alias) throws Exception {
        return (this.keystore.isKeyEntry(alias));
    }

    @Override
    public String getOriginalKeystoreFilename() {
        return (this.keystoreFilename);
    }

    @Override
    public boolean canWrite() {
        return (Files.isWritable(Paths.get(this.keystoreFilename)));
    }

    @Override
    public String getKeystoreStorageType() {
        return( this.keystoreStorageType);
    }
    
    @Override
    public int getKeystoreUsage() {
        return( this.keystoreUsage);
    }
}
