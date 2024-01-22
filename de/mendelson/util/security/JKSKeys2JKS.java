//$Header: /as2/de/mendelson/util/security/JKSKeys2JKS.java 2     26/09/22 10:19 Heller $
package de.mendelson.util.security;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Allows the conversion of a private key that is stored in a sun keystore to
 * the JKS format
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class JKSKeys2JKS {

    private Logger logger = null;

    private KeyStore targetKeyStore = null;
    private char[] keystorePass = null;

    public JKSKeys2JKS(Logger logger) {
        this.logger = logger;
    }

    /**
     * @param jksKeyStore JKS Keystore that contains the private key
     * @param jksKeyPassword JKS Key pass
     * @param alias alias of the key, used in both import and exported keystore
     */
    public void exportKey(KeyStore jksKeyStore, char[] jksKeyPassword, String alias) throws Exception {
        //extract key
        RSAPrivateCrtKey jksPrivateCrtKey = (RSAPrivateCrtKey) jksKeyStore.getKey(alias, jksKeyPassword);
        Certificate jksCert = jksKeyStore.getCertificate(alias);
        //Get Certificate Chain
        Certificate[] jksCerts = jksKeyStore.getCertificateChain(alias);
        if (jksPrivateCrtKey == null || jksCerts == null) {
            this.logger.severe("I didn't find a private key entry with the alias \"" + alias + "\" in the JKS keystore");
            return;
        }
        KeyStore pkcs12Keystore = this.targetKeyStore;
        if (pkcs12Keystore == null) {
            pkcs12Keystore = this.generateJKSKeyStore();
        }
        pkcs12Keystore.setKeyEntry(alias, jksPrivateCrtKey, this.keystorePass, jksCerts);
    }

    public void setTargetKeyStore(KeyStore keystore, char[] keystorePass) {
        this.targetKeyStore = keystore;
        this.keystorePass = keystorePass;
    }

    /**
     * Loads ore creates a keystore to import the keys to
     */
    private KeyStore generateJKSKeyStore() throws Exception {
        KeyStore keystore = KeyStore.getInstance(BCCryptoHelper.KEYSTORE_JKS, BouncyCastleProvider.PROVIDER_NAME);
        keystore.load(null, null);
        return (keystore);
    }

    /**
     * Saves the passed keystore
     *
     * @param keystorePass Password for the keystore
     * @param filename Filename where to save the keystore to
     */
    public void saveKeyStore(KeyStore keystore, char[] keystorePass,
            Path file) throws Exception {
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
}
