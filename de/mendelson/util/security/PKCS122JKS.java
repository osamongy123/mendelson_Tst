//$Header: /as2/de/mendelson/util/security/PKCS122JKS.java 4     26/09/22 10:19 Heller $
package de.mendelson.util.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PasswordFinder;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * This class allows to import a key that exist in pkcs#12 keystore into an
 * other JKS keystore
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class PKCS122JKS implements PasswordFinder {

    private Logger logger = Logger.getAnonymousLogger();
    /**
     * Keystore to use, if this is not set a new one will be created
     */
    private KeyStore keystore = null;
    /**
     * Default pass for a new created keystore, overwrite this by using the
     * setKeyStore() method
     */
    private char[] keystorePass = "test".toCharArray();

    /**
     * Creates a new instance of PEMUtil
     *
     * @param logger Logger to log the information to
     */
    public PKCS122JKS(Logger logger) {
        this.logger = logger;
        //forget it to work without BC at this point, the SUN JCE provider
        //could not handle pcks12
        // performs "Security.addProvider(new BouncyCastleProvider());" and adds some BC related system properties
        new BCCryptoHelper().initialize();
    }

    /**
     * @param importKeystoreStream Stream that contains a keystore in pkcs12
     * format
     */
    public void importKey(KeyStore sourceKeyStore, String alias) throws Exception {
        if (sourceKeyStore.isKeyEntry(alias)) {
            Key importKey = sourceKeyStore.getKey(alias, new char[]{});
            Certificate[] certs = sourceKeyStore.getCertificateChain(alias);
            if (certs == null || certs.length == 0) {
                throw new Exception("JKS import: private key with alias " + alias + " does not contain a certificate.");
            }
            KeyStore store = this.keystore;
            if (store == null) {
                store = this.generateKeyStore();
            }
            store.setKeyEntry(alias, importKey, this.keystorePass, certs);
        } else {
            throw new Exception("JKS import: keystore doesn't contain a private key with alias " + alias);
        }
    }

    /**
     * @param importKeystoreStream Stream that contains a keystore in pkcs12
     * format
     */
    public void importKey(InputStream sourceKeystoreStream, char[] sourceKeypass,
            String alias) throws Exception {
        //open keystore
        KeyStore sourceKeystore = KeyStore.getInstance(BCCryptoHelper.KEYSTORE_PKCS12, 
                BouncyCastleProvider.PROVIDER_NAME);
        sourceKeystore.load(sourceKeystoreStream, sourceKeypass);
        this.importKey(sourceKeystore, alias);
    }

    /**
     * Loads ore creates a keystore to import the keys to
     */
    private KeyStore generateKeyStore() throws Exception {
        //do not remove the BC paramter, SUN cannot handle the format proper
        KeyStore localKeystore = KeyStore.getInstance(BCCryptoHelper.KEYSTORE_JKS, 
                BouncyCastleProvider.PROVIDER_NAME);
        localKeystore.load(null, null);
        return (localKeystore);
    }

    /**
     * Sets an already existing keystore to this class. Without an existing
     * keystore a new one is created
     */
    public void setTargetKeyStore(KeyStore keystore, char[] keystorePass) {
        this.keystore = keystore;
        this.keystorePass = keystorePass;
    }

    /**
     * Saves the passed keystore
     *
     * @param keystorePass Password for the keystore
     * @param filename Filename where to save the keystore to
     */
    public void saveKeyStore(KeyStore keystore, char[] keystorePass, Path file) throws Exception {
        OutputStream out = null;
        try {
            out = Files.newOutputStream(file);
            keystore.store(out, keystorePass);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * makes this a PasswordFinder
     */
    @Override
    public char[] getPassword() {
        return (this.keystorePass);
    }
}
