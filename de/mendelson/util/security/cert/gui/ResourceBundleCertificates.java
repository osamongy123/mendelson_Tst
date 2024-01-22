//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleCertificates.java 24    12/12/22 14:18 Heller $
package de.mendelson.util.security.cert.gui;
import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize gui entries
 * @author S.Heller
 * @version $Revision: 24 $
 */
public class ResourceBundleCertificates extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        
        {"display.ca.certs", "Show CA certificates" },        
        {"button.delete", "Delete key/certificate" },
        {"button.delete.all.expired", "Delete all expired keys/certificates" },
        {"button.edit", "Rename alias" },
        {"button.newkey", "Import key" },
        {"button.newcertificate", "Import certificate" },
        {"button.export", "Export certificate" },       
        {"button.reference", "Show usage" },       
        {"button.keycopy", "Copy entry to {0} manager" },       
        {"button.keycopy.tls", "TLS" },
        {"button.keycopy.signencrypt", "Encryption/Signature" },
        {"menu.file", "File" },        
        {"menu.file.close", "Close" },
        {"menu.import", "Import" },        
        {"menu.export", "Export" },
        {"menu.tools", "Tools" },
        {"menu.tools.generatekey", "Generate new key (self signed)" },
        {"menu.tools.generatecsr", "Trust certificate: Generate CSR to CA" },
        {"menu.tools.generatecsr.renew", "Renew certificate: Generate CSR to CA" },
        {"menu.tools.importcsr", "Trust certificate: Import CAs answer to CSR" },
        {"menu.tools.importcsr.renew", "Renew certificate: Import CAs answer to CSR" },
        {"label.selectcsrfile", "Please select the file where to store the request" },
        {"label.cert.import", "Import certificate (from your trading partner)" },
        {"label.cert.export", "Export certificate (for your trading partner)" },
        {"label.key.import.pem", "Import your own private key (from PEM)" },
        {"label.key.import.pkcs12", "Import your own private key (from PKCS#12)" },     
        {"label.key.import.jks", "Import your own private key (from JKS, JAVA keystore format)" },     
        {"label.key.export.pkcs12", "Export your own private key (PKCS#12) (for backup purpose only!)" },        
        {"label.keystore", "Keystore file:" },        
        {"title.signencrypt", "Avaliable certificates and keys (encryption, signature)" },
        {"title.ssl", "Avaliable certificates and keys (SSL/TLS)" },                
        {"button.ok", "Ok" },
        {"button.cancel", "Cancel" },
        {"filechooser.certificate.import", "Please select the certificate file for the import" },
        {"certificate.import.success.message", "The certificate has been imported successfully using the alias \"{0}\"" },
        {"certificate.ca.import.success.message", "The CA certificate has been imported successfully using the alias \"{0}\"." },
        {"certificate.import.success.title", "Success" },
        {"certificate.import.error.message", "There occured an error during the import process.\n{0}" },
        {"certificate.import.error.title", "Error" },
        {"certificate.import.alias", "Certificate alias to use:" },
        {"keystore.readonly.message", "The underlaying keystore is read-only.\nAny certificate write operation (modify/edit) is not possible in this state." },
        {"keystore.readonly.title", "Keystore r/o" },
        {"modifications.notalllowed.message", "Modifications are not possible"},
        {"generatekey.error.message", "{0}" },
        {"generatekey.error.title", "Error while key generation" },
        {"tab.info.basic", "Details" },
        {"tab.info.extension", "Extension" },
        {"tab.info.trustchain", "Trust chain" },        
        {"dialog.cert.delete.message", "Do you really want to delete the certificate with the alias \"{0}\"?"},
        {"dialog.cert.delete.title", "Delete certificate"},        
        {"title.cert.in.use", "Certificate is in use" },
        {"cert.delete.impossible", "Impossible to delete the entry, it''s in use by a partner. \nPlease use \"Show usage\" for details." },
        {"module.locked", "This certificate management is locked by another client, you are not allowed to commit your changes!" },
        {"label.trustanchor", "Trust anchor:" },
        {"warning.testkey", "Public mendelson test key - do not use in production!" },
        {"label.key.valid", "This key is valid" },
        {"label.key.invalid", "This key is valid" },
        {"label.cert.valid", "This certificate is valid" },
        {"label.cert.invalid", "This certificate is invalid" },
        {"warning.deleteallexpired.text", "Do you really want to delete {0} expired and unused entries?" },
        {"warning.deleteallexpired.title", "Delete all expired, unused keys/certificates" },
        {"warning.deleteallexpired.noneavailable.title", "None available" },
        {"warning.deleteallexpired.noneavailable.text", "There are no expired and unused entries available to delete" },
        {"success.deleteallexpired.title", "Delete expired, unused keys/certificates" },
        {"success.deleteallexpired.text", "{0} expired and unused keys/certificates have been removed" },
        {"warning.deleteallexpired.expired.but.used.title", "Used keys/certificates not deleted" },
        {"warning.deleteallexpired.expired.but.used.text", "{0} keys/certificates are expired but still in use - the system will keep them" },
        {"module.locked.title", "Module locked" },
        {"module.locked.text", "The module {0} is locked by another client ({1})." },
        {"keycopy.target.exists.title", "Entry exists in the target certifiicate manager"},
        {"keycopy.target.exists.text", "This entry does already exist (alias {0})."},
        {"keycopy.target.ro.title", "Target r/o" },
        {"keycopy.target.ro.text", "The underlaying keystore file for the target certificate manager is read/only." },
        {"keycopy.success.text", "The entry [{0}] has been copied successfully" },
    };
    
}