//$Header: /as2/de/mendelson/util/security/cert/CertificateManager.java 51    16/12/22 13:33 Heller $
package de.mendelson.util.security.cert;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.Base64;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.logging.Logger;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Helper class to store
 *
 * @author S.Heller
 * @version $Revision: 51 $
 */
public class CertificateManager {

    private Logger logger = null;
    private final List<KeystoreCertificate> keyStoreCertificateList
            = Collections.synchronizedList(new ArrayList<KeystoreCertificate>());
    private final Map<String, KeystoreCertificate> fingerprintCertificateMap
            = new ConcurrentHashMap<String, KeystoreCertificate>();
    private final Map<String, KeystoreCertificate> aliasCertificateMap
            = new ConcurrentHashMap<String, KeystoreCertificate>();
    private final MecResourceBundle rb;
    private KeystoreStorage storage = null;

    public CertificateManager(Logger logger) {
        this.logger = logger;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificateManager.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Returns the cert alias that is assigned to the cert/key of the passed
     * fingerprint (SHA1)
     */
    public String getAliasByFingerprint(byte[] fingerprintSHA1) {
        KeystoreCertificate cert = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        //fingerprint not found
        if (cert == null) {
            return (null);
        } else {
            return (cert.getAlias());
        }
    }

    /**
     * Returns the cert alias that is assigned to the cert/key of the passed
     * fingerprint (SHA1)
     */
    public String getAliasByFingerprint(String fingerprintSHA1) {
        KeystoreCertificate cert = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        //fingerprint not found
        if (cert == null) {
            return (null);
        } else {
            return (cert.getAlias());
        }
    }

    /**
     * Returns the certificate chain for a special alias
     */
    public Certificate[] getCertificateChain(String alias) throws Exception {
        Certificate[] chain = this.storage.getCertificateChain(alias);
        return (chain);
    }

    /**
     * Returns the X509 certificate assigned to the passed alias
     */
    public X509Certificate getX509Certificate(String alias) throws Exception {
        KeystoreCertificate certificate = this.aliasCertificateMap.get(alias);
        if (certificate == null) {
            throw new Exception(this.rb.getResourceString("alias.notfound", alias));
        }
        return (certificate.getX509Certificate());
    }

    /**
     * Returns the list of available X509 certificates
     */
    public List<X509Certificate> getX509CertificateList() {
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                certList.add(cert.getX509Certificate());
            }
        }
        return (certList);
    }

    /**
     * Returns the private key for an alias. If the assigned certificate does
     * not contain a private key an exception is thrown
     */
    public PrivateKey getPrivateKey(String alias) throws Exception {
        KeystoreCertificate certificate = this.aliasCertificateMap.get(alias);
        if (certificate == null) {
            throw new Exception(this.rb.getResourceString("alias.notfound", alias));
        }
        PrivateKey key = (PrivateKey) certificate.getKey();
        if (key == null) {
            throw new Exception(this.rb.getResourceString("alias.hasno.privatekey", alias));
        }
        return (key);
    }

    /**
     * Returns the public key for an alias.
     */
    public PublicKey getPublicKey(String alias) throws Exception {
        return (this.storage.getCertificate(alias).getPublicKey());
    }

    /**
     * Returns the private key for a passed public key. If the passed public key
     * does not exist an exception is thrown
     *
     * @param publicKey
     * @return
     */
    public PrivateKey getPrivateKeyByPublicKey(PublicKey publicKey) throws Exception {
        try {
            String lookupKeyEncoded = Base64.encode(publicKey.getEncoded());
            synchronized (this.keyStoreCertificateList) {
                for (KeystoreCertificate keystoreCertificate : this.keyStoreCertificateList) {
                    if (keystoreCertificate.getIsKeyPair()) {
                        String foundKeyEncoded = Base64.encode(keystoreCertificate.getX509Certificate().getPublicKey().getEncoded());
                        if (foundKeyEncoded.equals(lookupKeyEncoded)) {
                            return (this.getPrivateKey(keystoreCertificate.getAlias()));
                        }
                    }
                }
            }
            throw new Exception();
        } catch (Exception e) {
            throw new Exception("The private key for the passed public key does not exist.");
        }
    }

    /**
     * Returns the private key for a passed fingerprint (SHA1). If the assigned
     * certificate does not contain a private key an exception is thrown
     */
    public PrivateKey getPrivateKeyByFingerprintSHA1(byte[] fingerprintStrSHA1) throws Exception {
        //this will always return the private key if there is a public and a private key entry with the same serial in
        //the keystore
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintStrSHA1);
        return (this.getPrivateKey(certificate.getAlias()));
    }

    /**
     * Returns the public key for a passed fingerprint (SHA1). If the assigned
     * certificate does not contain a public key an exception is thrown
     */
    public PublicKey getPublicKeyByFingerprintSHA1(byte[] fingerprintStrSHA1) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintStrSHA1);
        return (this.getPublicKey(certificate.getAlias()));
    }

    /**
     * Returns the private key for a passed fingerprint (SHA1). If the assigned
     * certificate does not contain a private key an exception is thrown
     */
    public PrivateKey getPrivateKeyByFingerprintSHA1(String fingerprintStrSHA1) throws Exception {
        //this will always return the private key if there is a public and a private key entry with the same serial in
        //the keystore
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintStrSHA1);
        if (certificate == null) {
            throw new Exception(this.rb.getResourceString("certificate.not.found.fingerprint",
                    fingerprintStrSHA1));
        }
        return (this.getPrivateKey(certificate.getAlias()));
    }

    /**
     * Returns the public key for a passed fingerprint (SHA1). If the assigned
     * certificate does not contain a public key an exception is thrown
     */
    public PublicKey getPublicKeyByFingerprintSHA1(String fingerprintStrSHA1) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintStrSHA1);
        return (this.getPublicKey(certificate.getAlias()));
    }

    /**
     * Returns the public key or the private key for an alias.
     */
    public Key getKey(String alias) throws Exception {
        KeystoreCertificate certificate = this.aliasCertificateMap.get(alias);
        if (certificate == null) {
            throw new Exception(this.rb.getResourceString("alias.notfound", alias));
        }
        Key key = certificate.getKey();
        if (key == null) {
            throw new Exception(this.rb.getResourceString("alias.hasno.key", alias));
        } else {
            return (key);
        }
    }

    /**
     * Stores the manages keystore
     */
    public void saveKeystore() throws Throwable {
        this.storage.save();
        this.rereadKeystoreCertificates();
    }

    /**
     * Deletes an entry from the actual keystore
     */
    public void deleteKeystoreEntry(String alias) throws Throwable {
        this.storage.deleteEntry(alias);
        this.saveKeystore();
    }

    /**
     * Renames an entry in the underlaying keystore. Please remember that
     * PKCS#12 contains no key pair password, pass null in this case
     *
     */
    public void renameAlias(String oldAlias, String newAlias, char[] keypairPass) throws Throwable {
        this.storage.renameEntry(oldAlias, newAlias, keypairPass);
        this.saveKeystore();
    }

    /**
     * Refreshes the cached certificate data. This is an expensive operation as
     * it reads and analyzes all certificates/keys from the underlaying keystore
     */
    public void rereadKeystoreCertificates() throws Exception {
        synchronized (this.keyStoreCertificateList) {
            Map<String, Certificate> newCertificateMap = this.storage.loadCertificatesFromKeystore();
            this.keyStoreCertificateList.clear();
            for (String alias : newCertificateMap.keySet()) {
                KeystoreCertificate certificate = new KeystoreCertificate();
                certificate.setAlias(alias);
                X509Certificate foundCertificate = (X509Certificate) newCertificateMap.get(alias);
                certificate.setCertificate(foundCertificate);
                try {
                    boolean isKeyPair = this.getKeystore().isKeyEntry(alias);
                    certificate.setIsKeyPair(isKeyPair);
                    if (isKeyPair) {
                        certificate.setKey(this.storage.getKey(alias));
                    }
                } catch (Exception e) {
                    //no problem, thats what we wanted to know
                    certificate.setIsKeyPair(false);
                    if (this.logger != null) {
                        this.logger.warning(e.getMessage());
                    }
                }
                this.keyStoreCertificateList.add(certificate);
            }
            synchronized (this.fingerprintCertificateMap) {
                this.fingerprintCertificateMap.clear();
                for (KeystoreCertificate certificate : this.keyStoreCertificateList) {
                    String fingerprint = certificate.getFingerPrintSHA1();
                    //it could happen that a cert and a key with the same fingerprint are in the keystore.
                    //the key has priority in this case
                    KeystoreCertificate existingCertificate = this.fingerprintCertificateMap.get(fingerprint);
                    if (existingCertificate == null) {
                        this.fingerprintCertificateMap.put(fingerprint, certificate);
                    } else if (!existingCertificate.getIsKeyPair()) {
                        this.fingerprintCertificateMap.put(fingerprint, certificate);
                    }
                }
            }
            synchronized (this.aliasCertificateMap) {
                this.aliasCertificateMap.clear();
                for (KeystoreCertificate certificate : this.keyStoreCertificateList) {
                    String alias = certificate.getAlias();
                    this.aliasCertificateMap.put(alias, certificate);
                }
            }
        }
    }

    /**
     * Adds a single certificate and saves the underlaying keystore
     */
    public void addCertificate(String alias, X509Certificate x509Certificate) throws Throwable {
        this.getKeystore().setCertificateEntry(alias, x509Certificate);
        this.saveKeystore();
    }

    /**
     * Called from external if the certificate storage has been changed. This
     * method calls the normal rereadkeystore method but logs the step
     */
    public void rereadKeystoreCertificatesLogged() {
        try {
            this.rereadKeystoreCertificates();
            if (this.logger != null) {
                this.logger.fine(this.rb.getResourceString("keystore.reloaded"));
            }
        } catch (Exception e) {
            if (this.logger != null) {
                this.logger.warning(this.rb.getResourceString("keystore.read.failure",
                        new Object[]{e.getMessage()}));
            }
        }
    }

    /**
     * Wrapper function for the underlaying keystore storage implementation
     */
    public boolean canWrite() {
        return (this.storage.canWrite());
    }

    /**
     * Wrapper function for the underlaying keystore storage implementation.
     * This is one of BCCryptoHelper.KEYSTORE_JKS or
     * BCCryptoHelper.KEYSTORE_PKCS12
     */
    public String getKeystoreType() {
        return (this.storage.getKeystoreStorageType());
    }

    /**
     * Reads the certificates of the actual key store
     *
     * @param type keystore typ as defined in the class BCCryptoHelper
     */
    public void loadKeystoreCertificates(KeystoreStorage storage) {
        this.storage = storage;
        try {
            this.rereadKeystoreCertificates();
        } catch (Exception e) {
            if (this.logger != null) {
                this.logger.warning(this.rb.getResourceString("keystore.read.failure",
                        new Object[]{e.getMessage()}));
            }
        }
    }

    /**
     * Reads the certificates of the actual key store
     *
     * @param type keystore typ as defined in the class BCCryptoHelper
     */
    public void loadKeystoreCertificatesWithException(KeystoreStorage storage) throws Exception {
        this.storage = storage;
        this.rereadKeystoreCertificates();
    }

    /**
     * returns null if the alias does not exist
     */
    public KeystoreCertificate getKeystoreCertificate(String alias) {
        KeystoreCertificate certificate = this.aliasCertificateMap.get(alias);
        return (certificate);
    }

    public KeystoreCertificate getKeystoreCertificateBySubjectDNNonNull(String subjectDN, String additionalInfo) throws Exception {
        KeystoreCertificate foundCert = null;
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                if (cert.getSubjectDN().equals(subjectDN)) {
                    //no entry found so far: always store the found one
                    if (foundCert == null) {
                        foundCert = cert;
                    } else {
                        //entry already found: overwrite it only if the found entry is a key
                        if (cert.getIsKeyPair()) {
                            foundCert = cert;
                        }
                    }
                }
            }
            if (foundCert == null) {
                throw new Exception(this.rb.getResourceString("certificate.not.found.subjectdn.withinfo",
                        new Object[]{subjectDN, additionalInfo}));
            } else {
                return (foundCert);
            }
        }
    }

    public KeystoreCertificate getKeystoreCertificateBySubjectKeyIdentifierNonNull(byte[] skiBytes, String additionalInfo) throws Exception {
        KeystoreCertificate foundCert = this.getKeystoreCertificateBySubjectKeyIdentifier(skiBytes);
        if (foundCert == null) {
            throw new Exception(this.rb.getResourceString("certificate.not.found.ski.withinfo",
                    new Object[]{KeystoreCertificate.byteArrayToHexStr(skiBytes), additionalInfo}));
        } else {
            return (foundCert);
        }

    }

    public KeystoreCertificate getKeystoreCertificateBySubjectKeyIdentifier(byte[] skiBytes) {
        String skiHex = KeystoreCertificate.byteArrayToHexStr(skiBytes);
        //it could happen that a cert and a key with the same fingerprint are in the keystore.
        //Always return the key in this case.
        KeystoreCertificate foundCert = null;
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                List<String> certificateSKIList = cert.getSubjectKeyIdentifier();
                for (String foundSKI : certificateSKIList) {
                    if (foundSKI.equals(skiHex)) {
                        //no entry found so far: always store the found one
                        if (foundCert == null) {
                            foundCert = cert;
                        } else {
                            //entry already found: overwrite it only if the found entry is a key
                            if (cert.getIsKeyPair()) {
                                foundCert = cert;
                            }
                        }
                    }
                }
            }
        }
        return (foundCert);
    }

    /**
     * returns null if a certificate with the issuerDN and the serial does not
     * exist
     */
    public KeystoreCertificate getKeystoreCertificateByIssuerDNAndSerial(String issuerDN, String serialDEC) {
        return (this.getKeystoreCertificateByIssuerAndSerial(new X500Principal(issuerDN), serialDEC));
    }

    /**
     * returns null if a certificate with the issuerDN and the serial does not
     * exist
     */
    public KeystoreCertificate getKeystoreCertificateByIssuerAndSerial(X500Principal issuer, String serialDEC) {
        //it could happen that a cert and a key with the same fingerprint are in the keystore.
        //Always return the key in this case.
        KeystoreCertificate foundCert = null;
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                if (cert.getSerialNumberDEC().equals(serialDEC) && cert.getX509Certificate().getIssuerX500Principal().equals(issuer)) {
                    //no entry found so far: always store the found one
                    if (foundCert == null) {
                        foundCert = cert;
                    } else {
                        //entry already found: overwrite it only if the found entry is a key
                        if (cert.getIsKeyPair()) {
                            foundCert = cert;
                        }
                    }
                }
            }
        }
        return (foundCert);
    }

    /**
     * Throws an exception if the requested certificate does not exist in the
     * keystore - this contains the additional info Str
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1NonNull(String fingerprintSHA1, String additionalInfo) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        if (certificate == null) {
            throw new Exception(this.rb.getResourceString("certificate.not.found.fingerprint.withinfo",
                    new Object[]{fingerprintSHA1, additionalInfo}));
        } else {
            return (certificate);
        }
    }

    /**
     * Throws an exception if the requested certificate does not exist in the
     * keystore
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1NonNull(String fingerprintSHA1) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        if (certificate == null) {
            throw new Exception(this.rb.getResourceString("certificate.not.found.fingerprint", fingerprintSHA1));
        } else {
            return (certificate);
        }
    }

    /**
     * Throws an exception if the requested certificate does not exist in the
     * keystore
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1NonNull(byte[] fingerprintSHA1) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        if (certificate == null) {
            throw new Exception(this.rb.getResourceString("certificate.not.found.fingerprint",
                    KeystoreCertificate.fingerprintBytesToStr(fingerprintSHA1)));
        } else {
            return (certificate);
        }
    }

    /**
     * Throws an exception if the requested certificate does not exist in the
     * keystore- this contains the additional info Str
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1NonNull(byte[] fingerprintSHA1, String additionalInfo) throws Exception {
        KeystoreCertificate certificate = this.getKeystoreCertificateByFingerprintSHA1(fingerprintSHA1);
        if (certificate == null) {
            throw new Exception(this.rb.getResourceString("certificate.not.found.fingerprint.withinfo",
                    new Object[]{KeystoreCertificate.fingerprintBytesToStr(fingerprintSHA1), additionalInfo}));
        } else {
            return (certificate);
        }
    }

    private boolean issuerIsEqual(String issuer1, String issuer2) {
        final String[] compareList = new String[]{
            "C", "O", "OU", "CN", "ST", "L", "E"
        };
        try {
            LdapName name1 = new LdapName(issuer1);
            LdapName name2 = new LdapName(issuer2);
            for (Rdn rdn1 : name1.getRdns()) {
                for (Rdn rdn2 : name2.getRdns()) {
                    for (String compareType : compareList) {
                        if (rdn1.getType().equalsIgnoreCase(compareType)
                                && rdn2.getType().equalsIgnoreCase(compareType)) {
                            String value1 = rdn1.getValue().toString();
                            String value2 = rdn2.getValue().toString();
                            if (!value1.equals(value2)) {
                                return (false);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return (false);
        }
        return (true);
    }

    /**
     * Tries to find a certificate with the related issuer/serial and throws an
     * exception if it does not exist in the certificate manager
     *
     * @param issuer
     * @param serial
     * @param additionalInfo
     * @return
     * @throws Exception
     */
    public KeystoreCertificate getKeystoreCertificateByIssuerSerialNonNull(String issuer, BigInteger serial,
            String additionalInfo) throws Exception {
        KeystoreCertificate foundCert = null;
        synchronized (this.keyStoreCertificateList) {
            for (KeystoreCertificate cert : this.keyStoreCertificateList) {
                String foundIssuerDN = cert.getIssuerDN();
                BigInteger foundSerial = cert.getX509Certificate().getSerialNumber();
                if (this.issuerIsEqual(issuer, foundIssuerDN) && foundSerial.equals(serial)) {
                    //no entry found so far: always store the found one
                    if (foundCert == null) {
                        foundCert = cert;
                    } else {
                        //entry already found: overwrite it only if the found entry is a key
                        if (cert.getIsKeyPair()) {
                            foundCert = cert;
                        }
                    }
                }
            }
        }
        if (foundCert == null) {
            String serialHex = serial.toString(16);
            throw new Exception(this.rb.getResourceString("certificate.not.found.issuerserial.withinfo",
                    new Object[]{issuer, serial.toString() + " (dec), " + serialHex + " (hex)", additionalInfo}));
        } else {
            return (foundCert);
        }
    }

    /**
     * returns null if the fingerprint does not exist
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1(byte[] fingerprintSHA1) {
        if (fingerprintSHA1 == null) {
            return (null);
        }
        String fingerprintSHA1Str = KeystoreCertificate.byteArrayToHexStr(fingerprintSHA1);
        KeystoreCertificate foundCert = this.fingerprintCertificateMap.get(fingerprintSHA1Str);
        return (foundCert);
    }

    /**
     * returns null if the fingerprint does not exist
     */
    public KeystoreCertificate getKeystoreCertificateByFingerprintSHA1(String fingerprintSHA1) {
        //just return null if the fingerprint string is invalid in any case
        if (fingerprintSHA1 == null || fingerprintSHA1.trim().length() == 0 || !fingerprintSHA1.contains(":")) {
            return (null);
        }
        KeystoreCertificate foundCert = this.fingerprintCertificateMap.get(fingerprintSHA1);
        return (foundCert);
    }

    /**
     * Returns a list of certificates, sorted by their name
     */
    public List<KeystoreCertificate> getKeyStoreCertificateList() {
        List<KeystoreCertificate> newList = new ArrayList<KeystoreCertificate>();
        synchronized (this.keyStoreCertificateList) {
            newList.addAll(this.keyStoreCertificateList);
        }
        Collections.sort(newList);
        return (newList);
    }

    /**
     * Passes a logger to the certificate manager. There will be no logging if
     * no logger has been passed
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public char[] getKeystorePass() {
        return this.storage.getKeystorePass();
    }

    public KeyStore getKeystore() {
        return (this.storage.getKeystore());
    }

    /**
     * Returns a map with issuer as key and the available certs as value
     */
    public Map<X500Principal, List<X509Certificate>> getIssuerCertificateMap() throws Exception {
        Map<X500Principal, List<X509Certificate>> map = new HashMap<X500Principal, List<X509Certificate>>();
        synchronized (this.keyStoreCertificateList) {
            List<KeystoreCertificate> certList = this.getKeyStoreCertificateList();
            for (KeystoreCertificate keystoreCertificate : certList) {
                X509Certificate foundCert = (X509Certificate) keystoreCertificate.getX509Certificate();
                if (foundCert != null) {
                    X500Principal subjectDN = foundCert.getSubjectX500Principal();
                    List<X509Certificate> foundCertList = map.get(subjectDN);
                    if (foundCertList == null) {
                        foundCertList = new ArrayList<X509Certificate>();
                        foundCertList.add(foundCert);
                    } else {
                        if (!foundCertList.contains(foundCert)) {
                            foundCertList.add(foundCert);
                        }
                    }
                    map.put(subjectDN, foundCertList);
                }
            }
            return map;
        }
    }

    /**
     * Adds a new Key entry and saves the underlaying keystore
     */
    public void setKeyEntry(String alias, Key key, Certificate[] chain) throws Throwable {
        this.getKeystore().setKeyEntry(alias, key, this.getKeystorePass(), chain);
        this.saveKeystore();
    }

//    public static final void main(String[] args) {
//        String dn1 = "CN=mend, OU=mendelson-e-commerce GmbH, O=mendelson-e-commerce GmbH, L=Berlin, ST=Berlin, C=DE, EMAILADDRESS=rosettanet@mendelson.de";
//        String dn2 = "E=rosettanet@mendelson.de,C=DE,ST=Berlin,L=Berlin,O=mendelson-e-commerce GmbH,OU=mendelson-e-commerce GmbH,CN=mend";
//        System.out.println(CertificateManager.issuerDNIsEqual(dn1, dn2));
//    }
}
