//$Header: /as2/de/mendelson/util/security/keygeneration/KeyGenerationValues.java 10    21/10/22 17:12 Heller $
package de.mendelson.util.security.keygeneration;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyUsage;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Stores key values for the generation process
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class KeyGenerationValues {

    public static final String KEYALGORITHM_DSA = KeyGenerator.KEYALGORITHM_DSA;
    public static final String KEYALGORITHM_RSA = KeyGenerator.KEYALGORITHM_RSA;
    public static final String KEYALGORITHM_ECDSA = KeyGenerator.KEYALGORITHM_ECDSA;
    public static final String SIGNATUREALGORITHM_SHA256_WITH_RSA = "SHA256WithRSA";

    private String keyAlgorithm = KEYALGORITHM_RSA;
    private int keySize = 2048;
    private String commonName = "subdomain.yourdomain.com";
    private String organisationUnit = "Your company department";
    private String organisationName = "Your company";
    private String localityName = "Locality name";
    private String stateName = "State name";
    private String countryCode = "DE";
    private String emailAddress = "webmaster@mailaddressondomain.to";
    private int keyValidInDays = 365;
    private String signatureAlgorithm = "SHA256WithRSA";
    private KeyUsage keyExtension = null;
    private ExtendedKeyUsage extendedKeyExtension = null;
    private List<GeneralName> subjectAlternativeNames = new ArrayList<GeneralName>();
    private String ecNamedCurve = null;

    /**
     * @return RSA/DSA etc
     */
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * @param keyAlgorithm the keyType to set
     */
    public void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
    }

    /**
     * @return the keySize
     */
    public int getKeySize() {
        return keySize;
    }

    /**
     * @param keySize the keySize to set
     */
    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    /**
     * @return the commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @param commonName the commonName to set
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * @return the organisationUnit
     */
    public String getOrganisationUnit() {
        return organisationUnit;
    }

    /**
     * @param organisationUnit the organisationUnit to set
     */
    public void setOrganisationUnit(String organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

    /**
     * @return the organisationName
     */
    public String getOrganisationName() {
        return organisationName;
    }

    /**
     * @param organisationName the organisationName to set
     */
    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    /**
     * @return the localityName
     */
    public String getLocalityName() {
        return localityName;
    }

    /**
     * @param localityName the localityName to set
     */
    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    /**
     * @return the stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the keyValidInDays
     */
    public int getKeyValidInDays() {
        return keyValidInDays;
    }

    /**
     * @param keyValidInDays the keyValidInDays to set
     */
    public void setKeyValidInDays(int keyValidInDays) {
        this.keyValidInDays = keyValidInDays;
    }

    /**
     * @return the signatureAlgorithm
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /**
     * @param signatureAlgorithm the signatureAlgorithm to set
     */
    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    /**
     * @return the keyExtension
     */
    public KeyUsage getKeyExtension() {
        return keyExtension;
    }

    /**
     * @param keyExtension the keyExtension to set
     */
    public void setKeyExtension(KeyUsage keyExtension) {
        this.keyExtension = keyExtension;
    }

    /**
     * @return the extendedKeyExtension
     */
    public ExtendedKeyUsage getExtendedKeyExtension() {
        return extendedKeyExtension;
    }

    /**
     * @param extendedKeyExtension the extendedKeyExtension to set
     */
    public void setExtendedKeyExtension(ExtendedKeyUsage extendedKeyExtension) {
        this.extendedKeyExtension = extendedKeyExtension;
    }
    
    /**
     * @return the subjectAlternativeName
     */
    public List<GeneralName> getSubjectAlternativeNames() {
        return this.subjectAlternativeNames;
    }

    /**
     * @param subjectAlternativeName the subjectAlternativeName to add
     */
    public void addSubjectAlternativeName(GeneralName name) {
        this.subjectAlternativeNames.add( name );
    }

    /**
     * @return the ecNamedCurve
     */
    public String getECNamedCurve() {
        return ecNamedCurve;
    }

    /**
     * @param ecNamedCurve the ecNamedCurve to set
     */
    public void setECNamedCurve(String ecNamedCurve) {
        this.ecNamedCurve = ecNamedCurve;
    }
}
