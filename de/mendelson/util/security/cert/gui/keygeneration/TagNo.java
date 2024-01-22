//$Header: /as2/de/mendelson/util/security/cert/gui/keygeneration/TagNo.java 2     6/09/17 11:11a Heller $
package de.mendelson.util.security.cert.gui.keygeneration;

import de.mendelson.util.security.cert.KeystoreCertificate;
import org.bouncycastle.asn1.x509.GeneralName;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores the tag No entry of the General Name
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class TagNo {

    private int value = GeneralName.otherName;
    public static final String DNS_NAME = KeystoreCertificate.generalNameTagNoToString(GeneralName.dNSName);
    public static final String DIR_NAME = KeystoreCertificate.generalNameTagNoToString(GeneralName.directoryName);
    public static final String IP_ADDRESS = KeystoreCertificate.generalNameTagNoToString(GeneralName.iPAddress);
    public static final String REGISTERED_ID = KeystoreCertificate.generalNameTagNoToString(GeneralName.registeredID);
    public static final String RFC822_NAME = KeystoreCertificate.generalNameTagNoToString(GeneralName.rfc822Name);
    public static final String URI = KeystoreCertificate.generalNameTagNoToString(GeneralName.uniformResourceIdentifier);

    //the following values are available in the API but seem not to be valid as
    //General Name during the key generation:
    //public static final String EDI_PARTY_NAME = "EDI party name";
    //public static final String X400 = "x.400 address";
    public TagNo(int value) {
        this.value = value;
    }

    public TagNo(String valueStr) {
        this.value = stringToInt(valueStr);
    }

    public static int stringToInt(String valueStr) {
        if( valueStr.equals(DNS_NAME)){
            return( GeneralName.dNSName);            
        }else if( valueStr.equals(IP_ADDRESS)){
            return( GeneralName.iPAddress);            
        }else if( valueStr.equals(REGISTERED_ID)){
            return( GeneralName.registeredID);            
        }else if( valueStr.equals(RFC822_NAME)){
            return( GeneralName.rfc822Name);            
        }else if( valueStr.equals(URI)){
            return( GeneralName.uniformResourceIdentifier);            
        }else if( valueStr.equals(DIR_NAME)){
            return( GeneralName.directoryName);            
        }
        throw new IllegalArgumentException("Unknown value " + valueStr + " to process a General Name");        
    }

    public static String intValueToString(int tagNo) {
        switch (tagNo) {
            case GeneralName.dNSName:
                return (DNS_NAME);
            case GeneralName.directoryName:
                return (DIR_NAME);
            case GeneralName.iPAddress:
                return (IP_ADDRESS);
            case GeneralName.registeredID:
                return (REGISTERED_ID);
            case GeneralName.rfc822Name:
                return (RFC822_NAME);
            case GeneralName.uniformResourceIdentifier:
                return (URI);
            //case GeneralName.x400Address:
            //    return (X400);
            //case GeneralName.ediPartyName:
            //    return (EDI_PARTY_NAME);
            default:
                throw new IllegalArgumentException("Unknown value " + tagNo + " to process a General Name");
        }
    }

    @Override
    public String toString() {
        return (intValueToString(this.value));
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * Overwrite the equal method of object
     *
     * @param anObject object ot compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof TagNo) {
            TagNo entry = (TagNo) anObject;
            return (this.value == entry.getValue());
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.value;
        return hash;
    }

}
