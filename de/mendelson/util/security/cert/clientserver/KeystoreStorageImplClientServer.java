//$Header: /oftp2/de/mendelson/util/security/cert/clientserver/KeystoreStorageImplClientServer.java 15    17.02.21 11:49 Heller $
package de.mendelson.util.security.cert.clientserver;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadRequestFile;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadResponseFile;
import de.mendelson.util.clientserver.clients.datatransfer.TransferClient;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.security.cert.KeystoreStorageImplFile;
import de.mendelson.util.security.cert.ResourceBundleKeystoreStorage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
 * Keystore storage implementation that relies on a client-server access
 * @author S.Heller
 * @version $Revision: 15 $
 */
public class KeystoreStorageImplClientServer implements KeystoreStorage {

    public static final int KEYSTORE_USAGE_SSL = KeystoreStorageImplFile.KEYSTORE_USAGE_SSL;
    public static final int KEYSTORE_USAGE_ENC_SIGN = KeystoreStorageImplFile.KEYSTORE_USAGE_ENC_SIGN;
    public static final String KEYSTORE_STORAGE_TYPE_JKS = BCCryptoHelper.KEYSTORE_JKS;
    public static final String KEYSTORE_STORAGE_TYPE_PKCS12 = BCCryptoHelper.KEYSTORE_PKCS12;
    
    private KeyStore keystore = null;
    private char[] keystorePass = null;
    private KeyStoreUtil keystoreUtil = new KeyStoreUtil();
    private BaseClient baseClient;
    private String originalFilename;
    private boolean readOnlyOnServer = false;
    private int keystoreUsage = KEYSTORE_USAGE_ENC_SIGN;
    private String keystoreStorageType = KEYSTORE_STORAGE_TYPE_PKCS12;
    private MecResourceBundle rb;

    /**
     * @param keystoreFilename
     * @param keystorePass
     * @param KEYSTORE_USAGE keystore type as defined in the class BCCryptoHelper
     */
    public KeystoreStorageImplClientServer(BaseClient baseClient, String filename, char[] keystorePass, final int KEYSTORE_USAGE,
            final String KEYSTORE_STORAGE_TYPE) throws Exception {
         //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleKeystoreStorage.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }       
        this.baseClient = baseClient;
        this.keystorePass = keystorePass;
        this.keystoreUsage = KEYSTORE_USAGE;
        this.keystoreStorageType = KEYSTORE_STORAGE_TYPE;
        this.loadKeystore(filename);
    }

    private void loadKeystore( String filename ) throws Exception{
        //request the keystore from the server
        DownloadRequestFile request = new DownloadRequestFile();
        request.setFilename(filename);
        DownloadResponseFile response = (DownloadResponseFile) baseClient.sendSync(request);
        if (response == null) {
            throw new Exception(this.rb.getResourceString("error.nodata"));
        }
        this.originalFilename = response.getFullFilename();
        byte[] keystoreBytes = response.getDataBytes();
        this.readOnlyOnServer = response.isReadOnly();
        if (keystoreBytes == null || keystoreBytes.length == 0) {
            throw new IllegalArgumentException(this.rb.getResourceString("error.empty"));
        }
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        this.keystore = cryptoHelper.createKeyStoreInstance(this.keystoreStorageType);
        //load the keystore data from the transferred byte array
        InputStream inStream = null;
        try {
            inStream = new ByteArrayInputStream(keystoreBytes);
            this.keystore.load(inStream, this.keystorePass);
        }
        catch( Exception e ){
            throw new Exception( this.rb.getResourceString( "keystore.read.failure",
                    e.getMessage()), e);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }
    }

    @Override
    public void save() throws Throwable {
        //write the current keystore object to a byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.keystore.store(out, this.keystorePass);
        out.flush();
        out.close();
        UploadRequestKeystore request = new UploadRequestKeystore(this.keystoreUsage, 
                this.keystoreStorageType);
        TransferClient transferClient = new TransferClient(this.baseClient);
        //send the full data file to the server, chunked
        ByteArrayInputStream inStream = new ByteArrayInputStream( out.toByteArray());
        String uploadHash = transferClient.uploadChunked(inStream);
        request.setUploadHash(uploadHash);
        inStream.close();
        request.setTargetFilename(this.originalFilename);
        UploadResponseKeystore response = (UploadResponseKeystore) this.baseClient.sendSync(request);
        if (response == null) {
            throw new Exception(this.rb.getResourceString("error.save"));
        } else if (response != null && response.getException() != null) {
            throw (response.getException());
        }
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
        return ((X509Certificate) this.keystore.getCertificate(alias));
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
        this.loadKeystore(this.originalFilename);
        Map<String, Certificate> certificateMap = this.keystoreUtil.getCertificatesFromKeystore(this.keystore);
        return (certificateMap);
    }

    @Override
    public boolean isKeyEntry(String alias) throws Exception {
        return (this.keystore.isKeyEntry(alias));
    }

    @Override
    public String getOriginalKeystoreFilename() {
        return (this.originalFilename);
    }

    @Override
    public boolean canWrite() {
        return (!this.readOnlyOnServer);
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
