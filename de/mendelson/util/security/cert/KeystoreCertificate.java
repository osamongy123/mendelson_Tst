//$Header: /as2/de/mendelson/util/security/cert/KeystoreCertificate.java 37    14/12/22 17:13 Heller $
package de.mendelson.util.security.cert;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Object that stores a single configuration certificate/key
 *
 * @author S.Heller
 * @version $Revision: 37 $
 */
public class KeystoreCertificate implements Comparable, Serializable {

    public static final long serialVersionUID = 1L;
    private String alias = "";
    private X509Certificate certificate = null;
    /**Private of public key*/
    private Key key = null;
    private boolean isKeyPair = false;
    private String infoText = "";
    //cache some data
    private byte[] fingerprintSHA1Bytes = null;
    private String fingerprintSHA1 = null;

    private static final Map<String, String> OID_MAP = new HashMap<String, String>();

    static {
        OID_MAP.put("1.3.6.1.5.5.7.3.2", "Client authentication");
        OID_MAP.put("1.3.6.1.5.5.7.3.1", "Webserver authentication");
        OID_MAP.put("1.3.6.1.5.5.7.3.5", "IPSec end system");
        OID_MAP.put("1.3.6.1.5.5.7.3.6", "IPSec tunnel");
        OID_MAP.put("1.3.6.1.5.5.7.3.3", "Code signing");
        OID_MAP.put("1.3.6.1.5.5.7.3.7", "IPSec user");
        OID_MAP.put("1.3.6.1.5.5.7.3.4", "Email protection");
        OID_MAP.put("1.3.6.1.5.5.7.3.8", "Timestamping");
        OID_MAP.put("2.16.840.1.113733.1.8.1", "Verisign Server Gated Crypto");
        //Netscape extended key usages
        OID_MAP.put("2.16.840.1.113730.4.1", "Netscape Server Gated Crypto");
        OID_MAP.put("2.16.840.1.113730.1.2", "Netscape base URL");
        OID_MAP.put("2.16.840.1.113730.1.8", "Netscape CA policy URL");
        OID_MAP.put("2.16.840.1.113730.1.4", "Netscape CA revocation URL");
        OID_MAP.put("2.16.840.1.113730.1.7", "Netscape cert renewal URL");
        OID_MAP.put("2.16.840.1.113730.2.5", "Netscape cert sequence");
        OID_MAP.put("2.16.840.1.113730.1.1", "Netscape cert type");
        OID_MAP.put("2.16.840.1.113730.1.13", "Netscape comment");
        OID_MAP.put("2.16.840.1.113730.1.3", "Netscape revocation URL");
        OID_MAP.put("2.16.840.1.113730.1.12", "Netscape SSL server name");
        //MS extended key usages
        OID_MAP.put("1.3.6.1.4.1.311.10.3.3", "Microsoft Server Gated Crypto");
        OID_MAP.put("1.3.6.1.4.1.311.20.2.2", "Smart card logon");
        OID_MAP.put("1.3.6.1.4.1.311.10.3.4", "Encrypting filesystem");
        OID_MAP.put("1.3.6.1.4.1.311.10.3.12", "Document signing");
        OID_MAP.put("1.3.6.1.4.1.311.21.5", "CA encryption certificate");
        OID_MAP.put("1.3.6.1.4.1.311.10.3.1", "Microsoft trust list signing");
        OID_MAP.put("1.3.6.1.4.1.311.10.3.4.1", "File recovery");
        OID_MAP.put("1.3.6.1.4.1.311.10.3.11", "Key recovery");
        OID_MAP.put("1.3.6.1.4.1.311.10.3.10", "Qualified subordination");
        OID_MAP.put("1.3.6.1.4.1.311.10.3.9", "Root list signer");
    }

    /**
     * The SHA-1 fingerprints of the public available mendelson test keys
     */
    public static final String[] TEST_KEYS_FINGERPRINTS_SHA1 = new String[]{
        "6D:9A:2C:79:02:0B:F1:6B:20:78:E4:A3:BE:DF:93:DD:2A:AD:B7:40", //key2
        "3D:A0:27:42:4D:92:6D:04:BB:74:66:1D:48:3E:61:6A:46:2A:05:B7", //key1
        "08:FF:33:83:DF:8B:2F:9F:40:BB:F7:88:FE:FD:9C:15:40:E4:FE:C6", //key4
        "DC:99:5A:83:60:A4:37:C4:30:3B:10:AC:31:4E:D9:21:16:61:36:77" //key3  
    };

    public KeystoreCertificate() {
    }

    /**
     * Returns the extension value "extended key usage", OID 2.5.29.37
     *
     */
    public List<String> getExtendedKeyUsage() {
        List<String> extendedKeyUsage = new ArrayList<String>();
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.37");
        if (extensionValue == null) {
            return (extendedKeyUsage);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            ASN1Sequence asn1Sequence = (ASN1Sequence) ASN1Primitive.fromByteArray(octedBytes);
            for (int i = 0; i < asn1Sequence.size(); i++) {
                String oid = (asn1Sequence.getObjectAt(i).toASN1Primitive().toString());
                if (OID_MAP.containsKey(oid)) {
                    extendedKeyUsage.add(OID_MAP.get(oid));
                } else {
                    extendedKeyUsage.add(oid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (extendedKeyUsage);
    }

    /**
     * OID 2.5.29.35 - Authority Key Identifier This extension may be used
     * either as a certificate or CRL extension. It identifies the public key to
     * be used to verify the signature on this certificate or CRL. It enables
     * distinct keys used by the same CA to be distinguished (e.g., as key
     * updating occurs).
     *
     * @return
     */
    public List<String> getAuthorityKeyIdentifier() {
        List<String> authorityKeyIdentifierList = new ArrayList<String>();
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.35");
        if (extensionValue == null) {
            //there is no such extension: return empty list
            return (authorityKeyIdentifierList);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            ASN1Sequence asn1Sequence = (ASN1Sequence) ASN1Primitive.fromByteArray(octedBytes);
            for (int i = 0, len = asn1Sequence.size(); i < len; i++) {
                if (asn1Sequence.getObjectAt(i) instanceof DERTaggedObject) {
                    DERTaggedObject derTagObj = (DERTaggedObject) asn1Sequence.getObjectAt(i);
                    if (derTagObj.getTagNo() == 0) {
                        DEROctetString octetStr = (DEROctetString) derTagObj.getObject();
                        byte[] identifier = octetStr.getOctets();
                        authorityKeyIdentifierList.add("[Key identifier] " + byteArrayToHexStr(identifier));
                    } else if (derTagObj.getTagNo() == 2) {
                        DEROctetString octetStr = (DEROctetString) derTagObj.getObject();
                        byte[] identifier = octetStr.getOctets();
                        authorityKeyIdentifierList.add("[Serial] " + byteArrayToHexStr(identifier));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (authorityKeyIdentifierList);
    }

    /**
     * OID 2.5.29.14 - Subject Key Identifier This extension identifies the
     * public key being certified. It enables distinct keys used by the same
     * subject to be differentiated (e.g., as key updating occurs).
     *
     * @return
     */
    public List<String> getSubjectKeyIdentifier() {
        List<String> subjectKeyIdentifierList = new ArrayList<String>();
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.14");
        if (extensionValue == null) {
            //there is no such extension: return empty list
            return (subjectKeyIdentifierList);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            DEROctetString octetStr = (DEROctetString) ASN1Primitive.fromByteArray(octedBytes);
            byte[] identifier = octetStr.getOctets();
            subjectKeyIdentifierList.add(byteArrayToHexStr(identifier));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (subjectKeyIdentifierList);
    }

    /**
     * Returns the key usages of this cert, OID 2.5.29.15
     */
    public List<String> getKeyUsages() {
        List<String> keyUsages = new ArrayList<String>();
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.15");
        if (extensionValue == null) {
            //there is no such extension: return empty list
            return (keyUsages);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            //bit encoded values for the key usage
            int val = KeyUsage.getInstance(ASN1Primitive.fromByteArray(octedBytes)).getPadBits();
            //bit 0
            if ((val & KeyUsage.digitalSignature) == KeyUsage.digitalSignature) {
                keyUsages.add("Digital signature");
            }
            //bit 1
            if ((val & KeyUsage.nonRepudiation) == KeyUsage.nonRepudiation) {
                keyUsages.add("Non repudiation");
            }
            //bit 2
            if ((val & KeyUsage.keyEncipherment) == KeyUsage.keyEncipherment) {
                keyUsages.add("Key encipherment");
            }
            //bit 3
            if ((val & KeyUsage.dataEncipherment) == KeyUsage.dataEncipherment) {
                keyUsages.add("Data encipherment");
            }
            //bit 4
            if ((val & KeyUsage.keyAgreement) == KeyUsage.keyAgreement) {
                keyUsages.add("Key agreement");
            }
            //bit 5
            if ((val & KeyUsage.keyCertSign) == KeyUsage.keyCertSign) {
                keyUsages.add("Key certificate signing");
            }
            //bit6
            if ((val & KeyUsage.cRLSign) == KeyUsage.cRLSign) {
                keyUsages.add("CRL signing");
            }
            if ((val & KeyUsage.decipherOnly) == KeyUsage.decipherOnly) {
                keyUsages.add("Decipher");
            }

            if ((val & KeyUsage.encipherOnly) == KeyUsage.encipherOnly) {
                keyUsages.add("Encipher");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (keyUsages);
    }

    /**
     * In fact whenever we say key we mean a pair of numbers comprising the key;
     * a key number to use in the raising of powers and another number that is
     * the modulus of the arithmetic to be used for the work.
     *
     * @return
     */
    public BigInteger getModulus() {
        PublicKey key = this.certificate.getPublicKey();
        if (key instanceof RSAPublicKey) {
            RSAPublicKey rsaKey = (RSAPublicKey) key;
            return (rsaKey.getModulus());
        }
        return (BigInteger.ZERO);
    }

    /**
     * In fact whenever we say key we mean a pair of numbers comprising the key;
     * a key number to use in the raising of powers and another number that is
     * the modulus of the arithmetic to be used for the work.
     *
     * @return
     */
    public BigInteger getPublicExponent() {
        PublicKey key = this.certificate.getPublicKey();
        if (key instanceof RSAPublicKey) {
            RSAPublicKey rsaKey = (RSAPublicKey) key;
            return (rsaKey.getPublicExponent());
        }
        return (BigInteger.ZERO);
    }

    /**
     * Returns the subject alternative name of this cert, OID 2.5.29.17
     */
    public List<String> getSubjectAlternativeNames() {
        List<String> alternativeNames = new ArrayList<String>();
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.17");
        if (extensionValue == null) {
            return (alternativeNames);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            GeneralName[] names = (GeneralNames.getInstance(ASN1Primitive.fromByteArray(octedBytes))).getNames();
            for (GeneralName name : names) {
                ASN1Encodable encodable = name.getName();

                //IP addresses are sometimes stored as DEROctetString which would result in a single hex value on display
                // - this has to be decoded for the display
                if (encodable instanceof DEROctetString && name.getTagNo() == GeneralName.iPAddress) {
                    DEROctetString str = (DEROctetString) encodable;
                    StringBuilder decStr = new StringBuilder();
                    byte[] octets = str.getOctets();
                    for (byte octet : octets) {
                        if (decStr.length() > 0) {
                            decStr.append(".");
                        }
                        decStr.append((int) (octet & 0xFF));
                    }
                    alternativeNames.add(decStr + " (" + generalNameTagNoToString(name) + ")");
                } else {
                    alternativeNames.add(((ASN1Encodable) name.getName()).toString()
                            + " (" + generalNameTagNoToString(name) + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (alternativeNames);
    }

    /**
     * Converts the tag no of a general name to a human readable value
     */
    public static final String generalNameTagNoToString(int tagNo) {
        if (tagNo == GeneralName.dNSName) {
            return ("DNS");
        }
        if (tagNo == GeneralName.directoryName) {
            return ("Directory");
        }
        if (tagNo == GeneralName.ediPartyName) {
            return ("EDI party");
        }
        if (tagNo == GeneralName.iPAddress) {
            return ("IP");
        }
        if (tagNo == GeneralName.otherName) {
            return ("Other name");
        }
        if (tagNo == GeneralName.registeredID) {
            return ("Registered ID");
        }
        if (tagNo == GeneralName.rfc822Name) {
            return ("Mail (RFC822)");
        }
        if (tagNo == GeneralName.uniformResourceIdentifier) {
            return ("URI");
        }
        if (tagNo == GeneralName.x400Address) {
            return ("x.400");
        }
        return ("");
    }

    /**
     * Converts the tag no of a general name to a human readable value
     */
    public static final String generalNameTagNoToString(GeneralName name) {
        return (generalNameTagNoToString(name.getTagNo()));
    }

    /**
     * Get extension values for CRL Distribution Points as a string list or an
     * empty list if an exception occured or the extension doesnt exist OID
     * 2.5.29.31
     */
    public List<String> getCrlDistributionURLs() {
        List<String> ulrList = new ArrayList<String>();
        //CRL destribution points has OID 2.5.29.31
        byte[] extensionValue = this.certificate.getExtensionValue("2.5.29.31");
        if (extensionValue == null) {
            return (ulrList);
        }
        try {
            byte[] octedBytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets();
            CRLDistPoint distPoint = CRLDistPoint.getInstance(ASN1Primitive.fromByteArray(octedBytes));
            DistributionPoint[] points = distPoint.getDistributionPoints();
            for (DistributionPoint point : points) {
                DistributionPointName distributionPointName = point.getDistributionPoint();
                if (distributionPointName != null) {
                    if (distributionPointName.getType() == DistributionPointName.FULL_NAME) {
                        GeneralNames generalNames = (GeneralNames) distributionPointName.getName();
                        for (GeneralName generalName : generalNames.getNames()) {
                            //generalName.getTagNo() is GeneralName.uniformResourceIdentifier in this case
                            ulrList.add(((ASN1String) generalName.getName()).getString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            //nop
        }
        return (ulrList);
    }

    /**
     * Returns the enwrapped certificate version
     */
    public int getVersion() {
        return (this.certificate.getVersion());
    }

    public String getSigAlgName() {
        return (this.certificate.getSigAlgName());
    }

    public String getSigAlgOID() {
        return (this.certificate.getSigAlgOID());
    }

    public String getPublicKeyAlgorithm() {
        PublicKey key = this.certificate.getPublicKey();
        return (key.getAlgorithm());
    }

    /**
     * Valid date start
     */
    public Date getNotBefore() {
        return (this.certificate.getNotBefore());
    }

    /**
     * Valid date end
     */
    public Date getNotAfter() {
        return (this.certificate.getNotAfter());
    }

    public String getSubjectDN() {
        return (this.certificate.getSubjectDN().toString());
    }

    public String getIssuerDN() {
        return (this.certificate.getIssuerDN().toString());
    }

    /**
     * Returns the serial number as decimal
     */
    public String getSerialNumberDEC() {
        return (this.certificate.getSerialNumber().toString());
    }

    /**
     * Returns the serial number as decimal
     */
    public String getSerialNumberHEX() {
        return (this.certificate.getSerialNumber().toString(16).toUpperCase());
    }

    public void setAlias(String alias) {
        if (alias == null) {
            alias = "";
        }
        this.alias = alias;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
        this.computeInfoText();
    }
    
    public void setKey(Key key) {
        this.key = key;
    }

    public void setIsKeyPair(boolean isKeyPair) {
        this.isKeyPair = isKeyPair;
    }

    public X509Certificate getX509Certificate() {
        return (this.certificate);
    }

    public boolean getIsKeyPair() {
        return (this.isKeyPair);
    }

    /**Returns the private key of the entry - or null if it is not set*/
    public Key getKey(){
        return( this.key );
    }
    
    
    /**
     * KeyUsage extension, (OID = 2.5.29.15). The key usage extension defines
     * the purpose (e.g., encipherment, signature, certificate signing) of the
     * key contained in the certificate. The ASN.1 definition for this is:
     *
     * KeyUsage ::= BIT STRING { digitalSignature (0), nonRepudiation (1),
     * keyEncipherment (2), dataEncipherment (3), keyAgreement (4), keyCertSign
     * (5), --> true ONLY for CAs cRLSign (6), encipherOnly (7), decipherOnly
     * (8) }
     *
     * @return
     */
    public boolean isCACertificate() {
        boolean[] keyUsage = this.certificate.getKeyUsage();
        if (keyUsage != null) {
            return (keyUsage[5]);
        } else {
            return (false);
        }
    }

    public boolean isEndUserCertificate() {
        return (!this.isCACertificate());
    }

    /**
     * This method seems not to be reliable for all certificates - sometimes the
     * Root certificates could not be identified using it. The method
     * isCACertificate works for sure - but does only identify if a certificate
     * is an end user certificate or not.
     *
     * @return
     */
    public boolean isRootCertificate() {
        return (this.isSelfSigned() && this.certificate.getBasicConstraints() != -1);
    }

    public boolean isSelfSigned() {
        X500Principal subject = this.certificate.getSubjectX500Principal();
        X500Principal issuer = this.certificate.getIssuerX500Principal();
        return (subject.equals(issuer));
    }

    public String getAlias() {
        return (this.alias);
    }

    /**
     * If the public key could not be obtained by unknown reason this will
     * return 0
     *
     * @return
     */
    public int getPublicKeyLength() {
        PublicKey key = this.certificate.getPublicKey();
        if (key instanceof RSAPublicKey) {
            RSAPublicKey rsaKey = (RSAPublicKey) key;
            return (rsaKey.getModulus().bitLength());
        } else if (key instanceof DSAPublicKey) {
            DSAPublicKey dsaKey = (DSAPublicKey) key;
            return (dsaKey.getParams().getP().bitLength());
        } else if (key instanceof ECPublicKey) {
            ECPublicKey ecKey = (ECPublicKey) key;
            return (ecKey.getParams().getOrder().bitLength());
        }
        return (0);
    }

    public byte[] getFingerPrintBytesSHA1() {
        if( this.fingerprintSHA1Bytes == null ){
            this.fingerprintSHA1Bytes = this.getFingerPrintBytes("SHA1");
        }
        return( this.fingerprintSHA1Bytes);
    }

    public byte[] getFingerPrintBytesMD5() {
        return (this.getFingerPrintBytes("MD5"));
    }

    public byte[] getFingerPrintBytesSHA256() {
        return (this.getFingerPrintBytes("SHA-256"));
    }

    public String getFingerPrintSHA1() {
        if( this.fingerprintSHA1 == null ){
            this.fingerprintSHA1 = this.getFingerPrint("SHA1");
        }
        return( this.fingerprintSHA1);
    }

    public String getFingerPrintMD5() {
        return (this.getFingerPrint("MD5"));
    }

    public String getFingerPrintSHA256() {
        return (this.getFingerPrint("SHA-256"));
    }

    /**
     * Deserializes a fingerprint string to a byte array It is assumed that the
     * fingerprint string has the format hex:hex:hex
     */
    public static byte[] fingerprintStrToBytes(String fingerprintStr) {
        if (fingerprintStr == null || !fingerprintStr.contains(":")) {
            throw new IllegalArgumentException("KeystoreCertificate.fingerprintStrToBytes: The certificate fingerprint \"" + fingerprintStr + "\" is not a valid fingerprint");
        }
        String[] token = fingerprintStr.split(":");
        byte[] bytes = new byte[token.length];
        for (int i = 0; i < token.length; i++) {
            while (token[i].length() < 2) {
                token[i] = "0" + token[i];
            }
            bytes[i] = fromHexString(token[i])[0];
        }
        return (bytes);
    }

    private static byte[] fromHexString(final String encoded) {
        if ((encoded.length() % 2) != 0) {
            throw new IllegalArgumentException("KeystoreCertificate.fromHexString: Input string must contain an even number of characters");
        }
        final byte result[] = new byte[encoded.length() / 2];
        final char enc[] = encoded.toCharArray();
        try {
            for (int i = 0; i < enc.length; i += 2) {
                StringBuilder curr = new StringBuilder(2);
                curr.append(enc[i]).append(enc[i + 1]);
                result[i / 2] = (byte) Integer.parseInt(curr.toString(), 16);
            }
            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("KeystoreCertificate.fromHexString: Input string must contain hex values, found: \"" + encoded + "\"");
        }
    }

    public static String byteArrayToHexStr(byte[] byteArray) {
        StringBuilder hextStringBuffer = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if (i > 0) {
                hextStringBuffer.append(":");
            }
            String singleByte = Integer.toHexString(byteArray[i] & 0xFF).toUpperCase();
            if (singleByte.length() == 0) {
                hextStringBuffer.append("00");
            } else if (singleByte.length() == 1) {
                hextStringBuffer.append("0");
            }
            hextStringBuffer.append(singleByte);
        }
        return hextStringBuffer.toString();
    }

    /**
     * Serializes a fingerprint string from a byte array to a String It is
     * assumed that the fingerprint string has the format hex:hex:hex
     */
    public static String fingerprintBytesToStr(byte[] fingerprintBytes) {
        return (byteArrayToHexStr(fingerprintBytes));
    }

    /**
     * @param digest to create the hash value, please use SHA1 or MD5 only
     *
     */
    private byte[] getFingerPrintBytes(String digest) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(digest);
            return (messageDigest.digest(this.certificate.getEncoded()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns a fingerprint string that returns the fingerprint using the
     * format n:n:n
     *
     * @param digest to create the hash value, please use SHA1 or MD5 only
     *
     */
    private String getFingerPrint(String digest) {
        return (fingerprintBytesToStr(this.getFingerPrintBytes(digest)));
    }

    /**
     * Returns the cert path for this certificate as it exists in the keystore
     *
     * @return null if no cert path could be found All used methods are not
     * thread safe
     */
    public synchronized PKIXCertPathBuilderResult 
        getPKIXCertPathBuilderResult(KeyStore keystore, List<X509Certificate> certificateList) {
        X509Certificate embeddedCertificate = this.getX509Certificate();
        try {
            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(embeddedCertificate);
            boolean selected = selector.match(embeddedCertificate);
            if (!selected) {
                return (null);
            }
            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", BouncyCastleProvider.PROVIDER_NAME);
            PKIXBuilderParameters pkixParameter = new PKIXBuilderParameters(keystore, selector);
            pkixParameter.setRevocationEnabled(false);
            //a value of 5 does not work for some certificates in Bouncycastle. 3 means Anchor + 3 certificate 
            //which should be fine
            pkixParameter.setMaxPathLength(3);
            CertStoreParameters params = new CollectionCertStoreParameters(certificateList);
            CertStore intermediateCertStore = CertStore.getInstance("Collection", params,
                    BouncyCastleProvider.PROVIDER_NAME);
            pkixParameter.addCertStore(intermediateCertStore);
            PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder.build(pkixParameter);
            return (result);
        } catch (KeyStoreException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidAlgorithmParameterException e) {
        } catch (CertPathBuilderException e) {
        } catch (Throwable e) {
        }
        return (null);
    }

    /**
     * Validates the certificate and returns the trust anchor certificate if the
     * cert path is valid and the full path could be validated
     *
     * @return null if the certificate could not be trusted or an other failure
     * like nosuchalg exception etc occurs
     */
    public X509Certificate validateCertPath(KeyStore keystore, List<X509Certificate> certificateList) {
        CertPath certPath = this.getPKIXCertPathBuilderResult(keystore, certificateList).getCertPath();
        if (certPath == null) {
            return (null);
        }
        try {
            // Validator params
            PKIXParameters params = new PKIXParameters(keystore);
            // Disable CRL checking since we are not supplying any CRLs
            params.setRevocationEnabled(false);
            //use BC here else PKCS#12 is not supported as keystore
            CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX",
                    BouncyCastleProvider.PROVIDER_NAME);
            CertPathValidatorResult result = certPathValidator.validate(certPath, params);
            // Get the CA used to validate this path
            PKIXCertPathValidatorResult pkixResult = (PKIXCertPathValidatorResult) result;
            TrustAnchor ta = pkixResult.getTrustAnchor();
            X509Certificate taCert = ta.getTrustedCert();
            return (taCert);
        } catch (NoSuchProviderException e) {
        } catch (KeyStoreException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidAlgorithmParameterException e) {
        } catch (CertPathValidatorException e) {
            // Validation failed
        }
        return (null);
    }

    @Override
    public String toString() {
        return (this.alias);
    }

    @Override
    public int compareTo(Object object) {
        if (this.equals(object)) {
            return (0);
        }
        KeystoreCertificate otherCert = (KeystoreCertificate) object;
        return (this.alias.toUpperCase().compareTo(otherCert.alias.toUpperCase()));
    }

    private void computeInfoText() {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        StringBuilder infoTextBuilder = new StringBuilder();
        infoTextBuilder.append("Version: ").append(this.getVersion());
        if (this.isRootCertificate()) {
            infoTextBuilder.append(" (Root certificate)");
        }
        infoTextBuilder.append("\n");
        infoTextBuilder.append("Subject: ").append(this.getSubjectDN()).append("\n");
        infoTextBuilder.append("Issuer: ").append(this.getIssuerDN()).append("\n");
        infoTextBuilder.append("Serial (dec): ").append(this.getSerialNumberDEC()).append("\n");
        infoTextBuilder.append("Serial (hex): ").append(this.getSerialNumberHEX()).append("\n");
        infoTextBuilder.append("Valid from: ").append(format.format(this.getNotBefore())).append("\n");
        infoTextBuilder.append("Valid until: ").append(format.format(this.getNotAfter())).append("\n");
        infoTextBuilder.append("Public key: ");
        int publicKeyLength = this.getPublicKeyLength();
        infoTextBuilder.append(String.valueOf(publicKeyLength));
        infoTextBuilder.append(" ").append(this.getPublicKeyAlgorithm()).append("\n");
        infoTextBuilder.append("Signature algorithm: ").append(this.getSigAlgName()).append(" (OID ")
                .append(this.getSigAlgOID()).append(")\n");
        if (this.getPublicKeyAlgorithm().startsWith("EC")) {
            try {
                String oid = this.getCurveOID(this.getX509Certificate().getPublicKey());
                String name = this.getCurveName(this.getX509Certificate().getPublicKey());
                infoTextBuilder
                        .append("Named Curve: ")
                        .append(name).append( " [OID ")
                        .append(oid).append("]\n");
            } catch (Exception ignore) {
                infoTextBuilder
                        .append("Named Curve:  Unknown\n");
            }
        }
        try {
            infoTextBuilder.append("Fingerprint (MD5): ").append(this.getFingerPrintMD5()).append("\n");
            infoTextBuilder.append("Fingerprint (SHA-1): ").append(this.getFingerPrintSHA1()).append("\n");
            infoTextBuilder.append("Fingerprint (SHA-256): ").append(this.getFingerPrintSHA256()).append("\n");
        } catch (Exception e) {
            infoTextBuilder.append("Fingerprint processing failed: ").append(e.getMessage());
        }
        this.infoText = infoTextBuilder.toString();
    }

    /**
     * Returns the curve OID if this is a EC key/certificate
     */
    public String getCurveOID(Key key) throws Exception {
        if (key instanceof ECPublicKey) {
            AlgorithmParameters params = AlgorithmParameters.getInstance("EC");
            params.init(((ECKey) key).getParams());
            return params.getParameterSpec(ECGenParameterSpec.class).getName();
        }
        return ("");
    }

    /**
     * Returns the curve OID if this is a EC key/certificate
     */
    public String getCurveName(Key key) throws Exception {
        if (key instanceof ECPublicKey) {
            ECParameterSpec params = ((ECPublicKey) key).getParams();
            org.bouncycastle.jce.spec.ECParameterSpec spec = EC5Util.convertSpec(params);          
            Enumeration names = ECNamedCurveTable.getNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement().toString();
                X9ECParameters possibleMatch = ECNamedCurveTable.getByName(name);
                if (possibleMatch != null) {
                    if (spec.getN().equals(possibleMatch.getN())
                            && spec.getH().equals(possibleMatch.getH())
                            && spec.getCurve().equals(possibleMatch.getCurve())
                            && spec.getG().equals(possibleMatch.getG())) {
                        return name;
                    }
                }
            }
        }
        return( "");
    }

    /**
     * Returns a string that contains information about the certificate
     */
    public String getInfo() {
        return (this.infoText);
    }

    /**
     * Returns some information about the certificate extensions
     */
    public String getInfoExtension() {
        StringBuilder extensionText = new StringBuilder();
        List<String> crl = this.getCrlDistributionURLs();
        for (int i = 0; i < crl.size(); i++) {
            extensionText.append("CRL distribution[").append(String.valueOf(i + 1)).append("]: ").append(crl.get(i)).append("\n");
        }
        List<String> alternativeNames = this.getSubjectAlternativeNames();
        if (!alternativeNames.isEmpty()) {
            extensionText.append("Subject alternative name: ").append(this.convertListToString(alternativeNames)).append("\n");
        }
        List<String> keyUsages = this.getKeyUsages();
        if (!keyUsages.isEmpty()) {
            extensionText.append("Key usage: ").append(this.convertListToString(keyUsages)).append("\n");
        }
        List<String> extkeyUsages = this.getExtendedKeyUsage();
        if (!extkeyUsages.isEmpty()) {
            extensionText.append("Extended key usage: ").append(this.convertListToString(extkeyUsages)).append("\n");
        }
        List<String> authorityKeyIdentifier = this.getAuthorityKeyIdentifier();
        if (!authorityKeyIdentifier.isEmpty()) {
            extensionText.append("Authority key identifier: ").append(this.convertListToString(authorityKeyIdentifier)).append("\n");
        }
        List<String> subjectKeyIdentifier = this.getSubjectKeyIdentifier();
        if (!subjectKeyIdentifier.isEmpty()) {
            extensionText.append("Subject key identifier: ").append(this.convertListToString(subjectKeyIdentifier)).append("\n");
        }
        return (extensionText.toString());
    }

    /**
     * Converts the arraylist content to a comma separated string
     */
    private String convertListToString(Collection<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String value : list) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
        return (builder.toString());
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
        if (anObject != null && anObject instanceof KeystoreCertificate) {
            KeystoreCertificate cert = (KeystoreCertificate) anObject;
            String otherFingerPrint = null;
            String ownFingerPrint = null;
            try {
                otherFingerPrint = cert.getFingerPrintSHA1();
                ownFingerPrint = this.getFingerPrintSHA1();
                return (otherFingerPrint.equals(ownFingerPrint));
            } catch (Exception e) {
                //unable to obtain the finger print. Use the serial number and the dates.
                return (cert.getIssuerDN().equals(this.getIssuerDN())
                        && cert.getNotAfter().equals(this.getNotAfter())
                        && cert.getNotBefore().equals(this.getNotBefore()));
            }
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.alias != null ? this.alias.hashCode() : 0);
        hash = 97 * hash + (this.certificate != null ? this.certificate.hashCode() : 0);
        hash = 97 * hash + (this.isKeyPair ? 1 : 0);
        return hash;
    }
    /**
     * '3D:A0:27:42:4D:92:6D:04:BB:74:66:1D:48:3E:61:6A:46:2A:05:B7'
     */
//    public static final void main(String[] args) {
//        byte[] test = new byte[]{
//            (byte) 0x00, (byte) 0x3D, (byte)0x04 , (byte) 0xA0, (byte) 0x92,
//            (byte) 0x6D, (byte) 0x6D, (byte) 0x04, (byte) 0xBB, (byte) 0x74,
//            (byte) 0x66, (byte) 0x1D, (byte) 0x48, (byte) 0x3E, (byte) 0x61,
//            (byte) 0x6A, (byte) 0x46, (byte) 0x05, (byte) 0xB7, (byte) 0x42,
//        };
//        String str = KeystoreCertificate.fingerprintBytesToStr(test);
//        byte[] testbytes = KeystoreCertificate.fingerprintStrToBytes(str);
//        boolean areequal = Arrays.equals(test, testbytes);
//        System.out.println(areequal);
//    }
}
