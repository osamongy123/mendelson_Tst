//$Header: /as2/de/mendelson/util/security/cert/KeyCopyHandler.java 1     12/12/22 14:18 Heller $
package de.mendelson.util.security.cert;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Allows to copy a key/certificate between multiple keystore managers of a system
 * @author S.Heller
 * @version $Revision: 1 $
 */
public interface KeyCopyHandler {

    /**Copy the passed entry to another keystore manager*/
    public void copyEntry(KeystoreCertificate cert) throws Throwable; 
    
    public void setSource(CertificateManager source);
    
}
