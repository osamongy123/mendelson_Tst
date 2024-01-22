//$Header: /as4/de/mendelson/util/security/cert/DefaultKeyCopyHandler.java 2     13/12/22 13:23 Heller $
package de.mendelson.util.security.cert;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.modulelock.LockClientInformation;
import de.mendelson.util.modulelock.message.ModuleLockRequest;
import de.mendelson.util.modulelock.message.ModuleLockResponse;
import de.mendelson.util.security.cert.clientserver.KeystoreStorageImplClientServer;
import de.mendelson.util.security.cert.gui.ResourceBundleCertificates;
import de.mendelson.util.uinotification.UINotification;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * This is the standard key copy handler that allows to copy keys/certificates
 * to the other keystore manager of the system
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class DefaultKeyCopyHandler implements KeyCopyHandler {

    private final BaseClient baseClient;
    private final String serversideTargetKeystoreFilename;
    private final char[] serversideTargetKeystorePass;
    private final String targetModuleLockId;
    private final int targetKeystoreUsage;
    private final String targetKeystoreType;
    private final MecResourceBundle rb;
    private CertificateManager certificateManagerSource = null;

    /**
     *
     * @param baseClient
     * @param serversideTargetKeystoreFilename The filename of the target
     * keystore on the server
     * @param serversideTargetKeystorePass The password of the target keystore
     * on the server
     * @param targetKeystoreUsage What purpose has the target keystore, e.g.
     * KeystoreStorageImplClientServer.KEYSTORE_USAGE_ENC_SIGN
     * @param targetKeystoreType What format has the target keystore, e.g.
     * KeystoreStorageImplClientServer.KEYSTORE_STORAGE_TYPE_PKCS12
     * @param targetModuleLockId The module lock String required to lock the
     * access to the target keystore, e.g. ModuleLock.MODULE_ENCSIGN_KEYSTORE.
     */
    public DefaultKeyCopyHandler(BaseClient baseClient, String serversideTargetKeystoreFilename,
            char[] serversideTargetKeystorePass,
            int targetKeystoreUsage, String targetKeystoreType,
            String targetModuleLockId) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificates.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.baseClient = baseClient;
        this.targetKeystoreUsage = targetKeystoreUsage;
        this.targetKeystoreType = targetKeystoreType;
        this.serversideTargetKeystorePass = serversideTargetKeystorePass;
        this.serversideTargetKeystoreFilename = serversideTargetKeystoreFilename;
        this.targetModuleLockId = targetModuleLockId;
    }

    @Override
    public void copyEntry(KeystoreCertificate sourceCertificate) throws Throwable {
        ModuleLockRequest lockRequest;
        ModuleLockResponse lockResponse;
        try {
            //try to lock the target keystore module for write access
            lockRequest = new ModuleLockRequest(this.targetModuleLockId, ModuleLockRequest.TYPE_SET);
            lockResponse = (ModuleLockResponse) this.baseClient.sendSync(lockRequest);
            boolean hasLock = lockResponse.wasSuccessful();
            if (!hasLock) {
                LockClientInformation lockKeeper = lockResponse.getLockKeeper();
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        this.rb.getResourceString("module.locked.title"),
                        this.rb.getResourceString("module.locked.text",
                                new Object[]{
                                    this.targetModuleLockId,
                                    lockKeeper.getClientIP()
                                }));
                return;
            }
            //load the target keystore
            KeystoreStorage targetKeystoreStorage = new KeystoreStorageImplClientServer(
                    this.baseClient, this.serversideTargetKeystoreFilename,
                    this.serversideTargetKeystorePass,
                    this.targetKeystoreUsage,
                    this.targetKeystoreType
            );
            CertificateManager certificateManagerTarget = new CertificateManager(Logger.getAnonymousLogger());
            certificateManagerTarget.loadKeystoreCertificates(targetKeystoreStorage);
            //check if the entry does already exist in the target keystore
            String fingerprint = sourceCertificate.getFingerPrintSHA1();
            if( certificateManagerTarget.getKeystoreCertificateByFingerprintSHA1(fingerprint) != null ){
                KeystoreCertificate targetCert = certificateManagerTarget.getKeystoreCertificateByFingerprintSHA1(fingerprint);
                String existingTargetAlias = targetCert.getAlias();
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        this.rb.getResourceString("keycopy.target.exists.title"),
                        this.rb.getResourceString("keycopy.target.exists.text",
                                new Object[]{
                                    existingTargetAlias
                                }));
                return;
            }
            if( !certificateManagerTarget.canWrite()){
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        this.rb.getResourceString("keycopy.target.ro.title"),
                        this.rb.getResourceString("keycopy.target.ro.text"));
                return;
            }
            //find a working key alias
            String newAlias = sourceCertificate.getAlias();
            int counter = 0;
            Map<String,Certificate> certMap = targetKeystoreStorage.loadCertificatesFromKeystore();            
            while( certMap.containsKey(newAlias) ){
                counter++;
                newAlias = sourceCertificate.getAlias() + "_" + counter;
            }
            if( sourceCertificate.getIsKeyPair()){
                //this saves the underlaying keystore
                certificateManagerTarget.setKeyEntry(newAlias, 
                        this.certificateManagerSource.getKey(sourceCertificate.getAlias()),
                        this.certificateManagerSource.getCertificateChain(sourceCertificate.getAlias()));
            }else{
                certificateManagerTarget.addCertificate(newAlias, sourceCertificate.getX509Certificate());
            }            
            UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_SUCCESS,
                        null,
                        this.rb.getResourceString("keycopy.success.text",
                                sourceCertificate.getAlias()));
        } finally {
            //release the target module lock
            lockRequest = new ModuleLockRequest(targetModuleLockId, ModuleLockRequest.TYPE_RELEASE);
            lockResponse = (ModuleLockResponse) this.baseClient.sendSync(lockRequest);
        }
    }

    @Override
    public void setSource(CertificateManager certificateManagerSource) {
        this.certificateManagerSource = certificateManagerSource;
    }
}
