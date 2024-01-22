//$Header: /as2/de/mendelson/util/security/keygeneration/KeyGenerator.java 15    24/10/22 11:58 Heller $
package de.mendelson.util.security.keygeneration;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.spec.ECParameterSpec;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * This class allows to generate a private key
 *
 * @author S.Heller
 * @version $Revision: 15 $
 */
public class KeyGenerator {

    public static final String KEYALGORITHM_DSA = "DSA";
    public static final String KEYALGORITHM_RSA = "RSA";
    public static final String KEYALGORITHM_ECDSA = "ECDSA";

    /**
     * Creates a new instance of KeyGenerator
     */
    public KeyGenerator() {
    }

    /**
     * Generate a key pair.
     *
     * @param keyType The type of the key alg as defined in this class
     * @param keySize The length of the key
     */
    public KeyGenerationResult generateKeyPair(KeyGenerationValues generationValues) throws Exception {
        //generation keypair
        KeyPairGenerator keyPairGen = null;
        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        //intialize with DSA/RSA/etc
        keyPairGen = KeyPairGenerator.getInstance(generationValues.getKeyAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
        if (generationValues.getKeyAlgorithm().startsWith("EC")) {
            ECNamedCurveParameterSpec curveParams = ECNamedCurveTable.getParameterSpec(generationValues.getECNamedCurve());
            ECParameterSpec ecParameterSpec = new ECNamedCurveSpec(curveParams.getName(),
                    curveParams.getCurve(),
                    curveParams.getG(),
                    curveParams.getN());
            keyPairGen.initialize(ecParameterSpec, rand);
        } else {
            keyPairGen.initialize(generationValues.getKeySize(), rand);
        }
        KeyPair keyPair = keyPairGen.generateKeyPair();
        X509Certificate certificate = this.generateCertificate(generationValues, keyPair);
        KeyGenerationResult result = new KeyGenerationResult(keyPair, certificate);
        return (result);
    }

    private X509Certificate generateCertificate(KeyGenerationValues generationValues, KeyPair keyPair) throws Exception {
        SubjectPublicKeyInfo publicKeyInformation = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        ContentSigner signer = new JcaContentSignerBuilder(generationValues.getSignatureAlgorithm())
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build(keyPair.getPrivate());
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append("CN=").append(replace(generationValues.getCommonName(), ",", "\\,"));
        nameBuilder.append(",OU=").append(replace(generationValues.getOrganisationUnit(), ",", "\\,"));
        nameBuilder.append(",O=").append(replace(generationValues.getOrganisationName(), ",", "\\,"));
        nameBuilder.append(",L=").append(replace(generationValues.getLocalityName(), ",", "\\,"));
        nameBuilder.append(",ST=").append(replace(generationValues.getStateName(), ",", "\\,"));
        nameBuilder.append(",C=").append(replace(generationValues.getCountryCode(), ",", "\\,"));
        nameBuilder.append(",E=").append(replace(generationValues.getEmailAddress(), ",", "\\,"));
        X500Name issuerName = new X500Name(nameBuilder.toString());
        X500Name subjectName = new X500Name(nameBuilder.toString());
        Date startDate = new Date(System.currentTimeMillis());
        long duration = TimeUnit.DAYS.toMillis(generationValues.getKeyValidInDays());
        Date endDate = new Date(startDate.getTime() + duration);
        BigInteger serialNumber = new BigInteger(Long.toString(System.currentTimeMillis() / 1000));
        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(
                issuerName, serialNumber, startDate, endDate, subjectName, publicKeyInformation);
        //add a key extension if this is requested
        if (generationValues.getKeyExtension() != null) {
            certificateBuilder.addExtension(Extension.keyUsage, true, generationValues.getKeyExtension());
        }
        //add a extended key extension if this is requested
        if (generationValues.getExtendedKeyExtension() != null) {
            certificateBuilder.addExtension(Extension.extendedKeyUsage, false, generationValues.getExtendedKeyExtension());
        }
        if (!generationValues.getSubjectAlternativeNames().isEmpty()) {
            GeneralName[] generalNamesArray = new GeneralName[generationValues.getSubjectAlternativeNames().size()];
            generationValues.getSubjectAlternativeNames().toArray(generalNamesArray);
            certificateBuilder.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(generalNamesArray));
        }
        X509CertificateHolder certificateHolder = certificateBuilder.build(signer);
        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(certificateHolder);
        return (certificate);
    }

    /**
     * Replaces the string tag by the string replacement in the sourceString
     *
     * @param source Source string
     * @param tag	String that will be replaced
     * @param replacement String that will replace the tag
     * @return String that contains the replaced values
     */
    private static String replace(String source, String tag, String replacement) {
        if (source == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        while (true) {
            int index = source.indexOf(tag);
            if (index == -1) {
                buffer.append(source);
                return (buffer.toString());
            }
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + tag.length());
        }
    }

}
