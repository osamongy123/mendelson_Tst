//$Header: /as2/de/mendelson/util/security/cert/CertificateInUseChecker.java 4     7/04/22 10:33 Heller $
package de.mendelson.util.security.cert;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Checks if a certificate is in use
 * @author S.Heller
 * @version $Revision: 4 $
 */
public interface CertificateInUseChecker {

    /**Returns an empty element if this certificate is not in use*/
    public CertificateInUseInfo checkUsed(KeystoreCertificate cert); 
    
}
