//$Header: /as2/de/mendelson/util/security/BCCryptoHelper.java 123   26/09/22 10:19 Heller $
package de.mendelson.util.security;

import de.mendelson.util.security.cert.KeystoreCertificate;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSCompressedDataParser;
import org.bouncycastle.cms.CMSCompressedDataStreamGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedDataParser;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.CMSSignedGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyAgreeEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyAgreeRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyAgreeRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.ZlibCompressor;
import org.bouncycastle.cms.jcajce.ZlibExpanderProvider;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Utility class to handle bouncycastle cryptography
 *
 * @author S.Heller
 * @version $Revision: 123 $
 */
public class BCCryptoHelper {

    public static final String ALGORITHM_3DES = "3des";
    public static final String ALGORITHM_DES = "des";
    public static final String ALGORITHM_RC2 = "rc2";
    public static final String ALGORITHM_RC4 = "rc4";
    public static final String ALGORITHM_AES_128 = "aes128";
    public static final String ALGORITHM_AES_192 = "aes192";
    public static final String ALGORITHM_AES_256 = "aes256";
    /**
     * AES 128 with PKCS#1v2.1 RSAES_OAEP key encryption using SHA-256 as hash
     * algorithm, MGF1 as mask generation function
     */
    public static final String ALGORITHM_AES_128_RSAES_OAEP = "aes128-rsaes-oaep";
    /**
     * AES 192 with PKCS#1v2.1 RSAES_OAEP key encryption using SHA-256 as hash
     * algorithm, MGF1 as mask generation function
     */
    public static final String ALGORITHM_AES_192_RSAES_OAEP = "aes192-rsaes-oaep";
    /**
     * AES 256 with PKCS#1v2.1 RSAES_OAEP key encryption using SHA-256 as hash
     * algorithm, MGF1 as mask generation function
     */
    public static final String ALGORITHM_AES_256_RSAES_OAEP = "aes256-rsaes-oaep";

    public static final String ALGORITHM_MD5 = "md5";
    public static final String ALGORITHM_SHA1 = "sha1";
    public static final String ALGORITHM_SHA224 = "sha-224";
    public static final String ALGORITHM_SHA256 = "sha-256";
    public static final String ALGORITHM_SHA384 = "sha-384";
    public static final String ALGORITHM_SHA512 = "sha-512";
    public static final String ALGORITHM_SHA3_224 = "sha3-224";
    public static final String ALGORITHM_SHA3_256 = "sha3-256";
    public static final String ALGORITHM_SHA3_384 = "sha3-384";
    public static final String ALGORITHM_SHA3_512 = "sha3-512";
    /**
     * PKCS#1v2.1 RSASSA-PSS signature scheme using SHA-1 as hash algorithm,
     * MGF1 as mask generation function
     */
    public static final String ALGORITHM_SHA_1_RSASSA_PSS = "sha1-rsassa-pss";
    /**
     * PKCS#1v2.1 RSASSA-PSS signature scheme using SHA-224 as hash algorithm,
     * MGF1 as mask generation function
     */
    public static final String ALGORITHM_SHA_224_RSASSA_PSS = "sha-224-rsassa-pss";
    /**
     * PKCS#1v2.1 RSASSA-PSS signature scheme using SHA-256 as hash algorithm,
     * MGF1 as mask generation function
     */
    public static final String ALGORITHM_SHA_256_RSASSA_PSS = "sha-256-rsassa-pss";
    /**
     * PKCS#1v2.1 RSASSA-PSS signature scheme using SHA-384 as hash algorithm,
     * MGF1 as mask generation function
     */
    public static final String ALGORITHM_SHA_384_RSASSA_PSS = "sha-384-rsassa-pss";
    /**
     * PKCS#1v2.1 RSASSA-PSS signature scheme using SHA-512 as hash algorithm,
     * MGF1 as mask generation function
     */
    public static final String ALGORITHM_SHA_512_RSASSA_PSS = "sha-512-rsassa-pss";
    /**
     * PKCS#1v2.1 RSASSA-PSS signature scheme using SHA-3-224 as hash algorithm,
     * MGF1 as mask generation function SHA3-224withRSAandMGF1
     */
    public static final String ALGORITHM_SHA3_224_RSASSA_PSS = "sha3-224-rsassa-pss";
    /**
     * PKCS#1v2.1 RSASSA-PSS signature scheme using SHA-3-256 as hash algorithm,
     * MGF1 as mask generation function SHA3-256withRSAandMGF1
     */
    public static final String ALGORITHM_SHA3_256_RSASSA_PSS = "sha3-256-rsassa-pss";
    /**
     * PKCS#1v2.1 RSASSA-PSS signature scheme using SHA-3-384 as hash algorithm,
     * MGF1 as mask generation function SHA3-384withRSAandMGF1
     */
    public static final String ALGORITHM_SHA3_384_RSASSA_PSS = "sha3-384-rsassa-pss";
    /**
     * PKCS#1v2.1 RSASSA-PSS signature scheme using SHA-3-512 as hash algorithm,
     * MGF1 as mask generation function SHA3-512withRSAandMGF1
     */
    public static final String ALGORITHM_SHA3_512_RSASSA_PSS = "sha3-512-rsassa-pss";
    public static final String ALGORITHM_IDEA = "idea";
    public static final String ALGORITHM_CAST5 = "cast5";
    public static final String KEYSTORE_PKCS12 = "PKCS12";
    public static final String KEYSTORE_JKS = "JKS";

    public BCCryptoHelper() {
    }

    public boolean isEncrypted(MimeBodyPart part) throws MessagingException {
        if (part == null) {
            throw new MessagingException("Part is absent");
        }
        ContentType contentType = new ContentType(part.getContentType());
        String baseType = contentType.getBaseType().toLowerCase();
        if (baseType.equalsIgnoreCase("application/pkcs7-mime")) {
            String smimeType = contentType.getParameter("smime-type");
            return smimeType != null && smimeType.equalsIgnoreCase("enveloped-data");
        } else {
            return false;
        }
    }

    /**
     * Performs an encryption with a 192 bit key, this will fail if the
     * unlimited strength policy files have not been installed for the VM This
     * method will throw an exception if there is a general security problem,
     * e.g. the provider is not found
     *
     * @return true if the VM is patched, false if the VM is not patched
     */
    public boolean performUnlimitedStrengthJurisdictionPolicyTest() {
        byte[] data = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        SecretKeySpec key192 = new SecretKeySpec(
                new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                    0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                    0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17
                },
                "Blowfish");
        try {
            Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key192);
            cipher.doFinal(data);
        } catch (Exception e) {
            return (false);
        }
        return (true);
    }

    /**
     * Returns the signed part of this container if it exists, else null. If the
     * container itself is signed it is returned. You could use this method if
     * you are not sure if the main container of a message is the signed part or
     * if there are some unused MIME wrappers around it that embedd the signed
     * part
     */
    public Part getSignedEmbeddedPart(Part part) throws MessagingException, IOException {
        if (part == null) {
            throw new MessagingException("Part is absent");
        }
        if (part.isMimeType("multipart/signed")) {
            return (part);
        }
        if (part.isMimeType("multipart/*")) {
            Multipart multiPart = (Multipart) part.getContent();
            int count = multiPart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = multiPart.getBodyPart(i);
                Part signedEmbeddedPart = this.getSignedEmbeddedPart(bodyPart);
                if (signedEmbeddedPart != null) {
                    return (signedEmbeddedPart);
                }
            }
        }
        return (null);
    }

    /**
     * Checks if two mics in the format <base64>, <digest> are equal
     *
     * @param mic1
     * @param mic2
     * @return
     */
    public boolean micIsEqual(String mic1, String mic2) {
        DigestInputStream inStream1 = null;
        DigestInputStream inStream2 = null;
        try {
            mic1 = mic1.trim();
            mic2 = mic2.trim();
            if (mic1.equals(mic2)) {
                return (true);
            }
            //parse the mics
            int index1 = mic1.lastIndexOf(',');
            int index2 = mic2.lastIndexOf(',');
            String digest1 = mic1.substring(index1 + 1).trim();
            String digest2 = mic2.substring(index2 + 1).trim();
            String oid1 = this.convertAlgorithmNameToOID(digest1);
            String oid2 = this.convertAlgorithmNameToOID(digest2);
            String hashbase641 = mic1.substring(0, index1);
            String hashbase642 = mic2.substring(0, index2);
            byte[] bytes1 = Base64.decode(hashbase641);
            byte[] bytes2 = Base64.decode(hashbase642);
            inStream1 = new DigestInputStream(new ByteArrayInputStream(bytes1),
                    MessageDigest.getInstance(oid1, BouncyCastleProvider.PROVIDER_NAME));
            byte[] bytesHashValue1 = inStream1.readAllBytes();
            inStream2 = new DigestInputStream(new ByteArrayInputStream(bytes2),
                    MessageDigest.getInstance(oid2, BouncyCastleProvider.PROVIDER_NAME));
            byte[] bytesHashValue2 = inStream2.readAllBytes();
            if (bytesHashValue1.length != bytesHashValue2.length) {
                return (false);
            }
            for (int i = 0; i < bytesHashValue1.length; i++) {
                if (bytesHashValue1[i] != bytesHashValue2[i]) {
                    return (false);
                }
            }
            return (true);
        } catch (Exception e) {
            e.printStackTrace();
            return (false);
        } finally {
            if (inStream1 != null) {
                try {
                    inStream1.close();
                } catch (Exception e) {
                    //nop
                }
            }
            if (inStream2 != null) {
                try {
                    inStream2.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
    }

    /**
     * Displays a bundle of byte arrays as hex string, for debug purpose only
     */
    private String toHexDisplay(byte[] data) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            result.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
            result.append(" ");
        }
        return result.toString();
    }

    /**
     * Calculates the hash value for a passed byte array, base 64 encoded
     *
     * @param digestAlgOID digest OID algorithm, e.g. "1.3.14.3.2.26"
     */
    public String calculateMIC(InputStream dataStream, String digestAlgOID) throws GeneralSecurityException, MessagingException, IOException {
        if (dataStream == null) {
            throw new GeneralSecurityException("calculateMIC: Unable to calculate MIC - processed data is absent");
        }
        MessageDigest messageDigest = MessageDigest.getInstance(digestAlgOID, BouncyCastleProvider.PROVIDER_NAME);
        DigestInputStream digestInputStream = null;
        byte mic[] = null;
        try {
            digestInputStream = new DigestInputStream(dataStream, messageDigest);
            //perform filter operation
            for (byte buf[] = new byte[4096]; digestInputStream.read(buf) >= 0;) {
            }
            mic = digestInputStream.getMessageDigest().digest();
        } finally {
            if (digestInputStream != null) {
                digestInputStream.close();
            }
        }
        String micString = new String(Base64.encode(mic));
        return (micString);
    }

    /**
     * Calculates the hash value for a passed body part, base 64 encoded
     *
     * @param digestAlgOID digest OID algorithm, e.g. "1.3.14.3.2.26"
     */
    public String calculateMIC(Part part, String digestAlgOID) throws GeneralSecurityException, MessagingException, IOException {
        if (part == null) {
            throw new GeneralSecurityException("calculateMIC: Unable to calculate MIC - MIME part is absent");
        }
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        //writeTo is ok here instead of part.getDataHandler().getInputStream() because the headers are required, too
        part.writeTo(bOut);
        bOut.flush();
        bOut.close();
        byte data[] = bOut.toByteArray();
        return (this.calculateMIC(new ByteArrayInputStream(data), digestAlgOID));
    }

    /**
     * Decrypt a MIME body part
     */
    public MimeBodyPart decrypt(MimeBodyPart part, Certificate cert, Key key) 
            throws GeneralSecurityException, MessagingException, CMSException, IOException, SMIMEException {
        if (!this.isEncrypted(part)) {
            throw new GeneralSecurityException("decrypt: Unable to decrypt - Content-Type indicates data isn't encrypted");
        }
        X509Certificate x509Cert = this.castCertificate(cert);
        SMIMEEnveloped envelope = new SMIMEEnveloped(part);
        RecipientId recipientId = new JceKeyTransRecipientId(x509Cert);
        RecipientInformation recipient = envelope.getRecipientInfos().get(recipientId);
        if (recipient == null) {
            throw new GeneralSecurityException("decrypt: Unable to decrypt data - wrong key used to decrypt the data.");
        } else {
            MimeBodyPart bodyPart = SMIMEUtil.toMimeBodyPart(
                    recipient.getContentStream(new JceKeyTransEnvelopedRecipient(this.getPrivateKey(key))
                            .setProvider(BouncyCastleProvider.PROVIDER_NAME)));
            return (bodyPart);
        }
    }

    public void deinitialize() {
    }

    /**
     * @param algorith a algorithm alias name, e.g. "3des", wil be translated
     * into the right IOD number internal
     */
    public MimeBodyPart encrypt(MimeMessage part, Certificate cert, String algorithm) throws Exception {
        X509Certificate x509Cert = castCertificate(cert);
        String encAlgOID = this.convertAlgorithmNameToOID(algorithm);
        SMIMEEnvelopedGenerator generator = new SMIMEEnvelopedGenerator();
        generator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(x509Cert).setProvider(BouncyCastleProvider.PROVIDER_NAME));
        if (part == null) {
            throw new GeneralSecurityException("encrypt: Part is absent");
        }
        MimeBodyPart encData = generator.generate(part, new JceCMSContentEncryptorBuilder(new ASN1ObjectIdentifier(encAlgOID)).setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
        return encData;
    }

    /**
     * @param algorith a algorithm alias name, e.g. "3des", will be translated
     * into the right IOD number internal
     */
    public MimeBodyPart encrypt(MimeBodyPart part, Certificate cert, String algorithm) throws Exception {
        X509Certificate x509Cert = castCertificate(cert);
        String encAlgOID = this.convertAlgorithmNameToOID(algorithm);
        SMIMEEnvelopedGenerator generator = new SMIMEEnvelopedGenerator();
        generator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(x509Cert).setProvider(BouncyCastleProvider.PROVIDER_NAME));
        if (part == null) {
            throw new GeneralSecurityException("encrypt: Part is absent");
        }
        MimeBodyPart encData = generator.generate(part, new JceCMSContentEncryptorBuilder(new ASN1ObjectIdentifier(encAlgOID)).setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
        return encData;
    }

    public void initialize() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
        //set BC properties to deal with incorrects certificates RSA structures
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "true");
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
        mc.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
        mc.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
        mc.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
        mc.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
        CommandMap.setDefaultCommandMap(mc);
        //As of JavaMail 1.4.1 and later caching was introduced for Multipart objects,
        //this can cause some issues for signature verification as occasionally the cache does not produce exactly
        //the same message as was read in.
        System.setProperty("mail.mime.cachemultipart", "false");
    }

    /**
     * Create a pkcs7-signature of the passed content and returns it
     *
     * @param chain certificate chain, chain[0] is the signers certificate
     * itself
     * @param embeddOriginalData Indicates if the original data should be
     * embedded in the signature
     *
     */
    public byte[] sign(byte[] content, Certificate[] chain, Key key, String digest,
            boolean embeddOriginalData) throws Exception {
        X509Certificate x509Cert = this.castCertificate(chain[0]);
        PrivateKey privKey = this.getPrivateKey(key);
        CMSSignedDataGenerator signedDataGenerator = new CMSSignedDataGenerator();
        //add dont know
        ASN1EncodableVector signedAttributes = new ASN1EncodableVector();
        SMIMECapabilityVector caps = new SMIMECapabilityVector();
        caps.addCapability(SMIMECapability.dES_EDE3_CBC);
        caps.addCapability(SMIMECapability.rC2_CBC, 128);
        caps.addCapability(SMIMECapability.dES_CBC);
        signedAttributes.add(new SMIMECapabilitiesAttribute(caps));
        boolean isECKey = x509Cert.getPublicKey().getAlgorithm().equals("EC");
        String algorithm = this.getSignAlgorithmByInternalDigestName(digest, isECKey);
        signedDataGenerator.addSignerInfoGenerator(
                this.createSignerInfoGenerator(signedAttributes, privKey, x509Cert, algorithm, true)
        );
        //add cert store
        List<Certificate> certList = Arrays.asList(chain);
        Store certStore = new JcaCertStore(certList);
        signedDataGenerator.addCertificates(certStore);
        if (content == null) {
            throw new Exception("sign: content is absent");
        }
        CMSTypedData processable = new CMSProcessableByteArray(content);
        CMSSignedData signatureData = signedDataGenerator.generate(processable, embeddOriginalData);
        return (signatureData.getEncoded());
    }

    /**
     * Create a pkcs7-signature of the passed content and returns it, without
     * embedding the original data in the signature
     *
     * @param chain certificate chain, chain[0] is the signers certificate
     * itself
     *
     */
    public byte[] sign(byte[] content, Certificate[] chain, Key key, String digest) throws Exception {
        return (this.sign(content, chain, key, digest, false));
    }

    /**
     * Internal helper method - returns the signature algorithm by the internal
     * name of it
     *
     * @param digest
     * @param isECKey
     * @return
     * @throws Exception
     */
    private String getSignAlgorithmByInternalDigestName(String digest, boolean isECKey) throws Exception {
        String algorithm = null;
        if (digest.equalsIgnoreCase(ALGORITHM_SHA1)) {
            if (isECKey) {
                algorithm = "SHA1withECDSA";
            } else {
                algorithm = "SHA1withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA224)) {
            if (isECKey) {
                algorithm = "SHA224withECDSA";
            } else {
                algorithm = "SHA224withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA256)) {
            if (isECKey) {
                algorithm = "SHA256withECDSA";
            } else {
                algorithm = "SHA256withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA384)) {
            if (isECKey) {
                algorithm = "SHA384withECDSA";
            } else {
                algorithm = "SHA384withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA512)) {
            if (isECKey) {
                algorithm = "SHA512withECDSA";
            } else {
                algorithm = "SHA512withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_MD5)) {
            if (isECKey) {
                algorithm = "MD5withECDSA";
            } else {
                algorithm = "MD5withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA_1_RSASSA_PSS)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA1withRSAandMGF1";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA_224_RSASSA_PSS)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA224withRSAandMGF1";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA_384_RSASSA_PSS)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA384withRSAandMGF1";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA_256_RSASSA_PSS)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA256withRSAandMGF1";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA_512_RSASSA_PSS)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA512withRSAandMGF1";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA3_224)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                //algorithm = "SHA3-224withRSA";
                //rsassa-pkcs1-v1-5-with-sha3-224 has OID 2.16.840.1.101.3.4.3.13
                algorithm = "SHA3-224withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA3_256)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA3-256withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA3_384)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA3-384withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA3_512)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA3-512withRSA";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA3_224_RSASSA_PSS)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA3-224withRSAandMGF1";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA3_256_RSASSA_PSS)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA3-256withRSAandMGF1";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA3_384_RSASSA_PSS)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA3-384withRSAandMGF1";
            }
        } else if (digest.equalsIgnoreCase(ALGORITHM_SHA3_512_RSASSA_PSS)) {
            if (isECKey) {
                throw new Exception("sign: Signing digest " + digest + " is not supported for non RSA keys.");
            } else {
                algorithm = "SHA3-512withRSAandMGF1";
            }
        } else {
            throw new Exception("sign: Signing digest " + digest + " is not supported.");
        }
        return (algorithm);
    }

    /**
     * @param chain certificate chain, chain[0] is the signers certificate
     * itself Signs the data using S/MIME 3.1 - don't use if for S/MIME 3.2 or
     * higher
     * @param useAlgorithmIdentifierProtectionAttribute should be set to true,
     * see RFC 6211
     */
    public MimeMultipart sign(MimeBodyPart body, Certificate[] chain, Key key, String digest,
            boolean useAlgorithmIdentifierProtectionAttribute) throws Exception {
        X509Certificate x509Cert = this.castCertificate(chain[0]);
        PrivateKey privKey = this.getPrivateKey(key);
        //call this generator with a S/MIME 3.1 compatible constructor as it defaults to RFC 5751 (other micalg values)
        SMIMESignedGenerator signedDataGenerator = new SMIMESignedGenerator(SMIMESignedGenerator.RFC3851_MICALGS);
        //add dont know
        ASN1EncodableVector signedAttributes = new ASN1EncodableVector();
        SMIMECapabilityVector caps = new SMIMECapabilityVector();
        caps.addCapability(SMIMECapability.dES_EDE3_CBC);
        caps.addCapability(SMIMECapability.rC2_CBC, 128);
        caps.addCapability(SMIMECapability.dES_CBC);
        signedAttributes.add(new SMIMECapabilitiesAttribute(caps));
        boolean isECKey = x509Cert.getPublicKey().getAlgorithm().equals("EC");
        String algorithm = this.getSignAlgorithmByInternalDigestName(digest, isECKey);
        signedDataGenerator.addSignerInfoGenerator(
                this.createSignerInfoGenerator(signedAttributes, privKey, x509Cert, algorithm,
                        useAlgorithmIdentifierProtectionAttribute)
        );
        //add cert store
        List<Certificate> certList = Arrays.asList(chain);
        Store certStore = new JcaCertStore(certList);
        signedDataGenerator.addCertificates(certStore);
        MimeMultipart signedPart = signedDataGenerator.generate(body);
        return (signedPart);
    }

    /**
     * Generates a signer info generator for the SMIME signature
     *
     * @param algorithmName e.g "SHA1withRSA"
     * @param privateKey Key to use to sign
     * @param useAlgorithmIdentifierProtectionAttribute should be set to true,
     * see RFC 6211
     *
     */
    private SignerInfoGenerator createSignerInfoGenerator(ASN1EncodableVector signedAttributes, PrivateKey privateKey, X509Certificate x509Cert,
            String algorithmName, boolean useAlgorithmIdentifierProtectionAttribute)
            throws Exception {
        AttributeTable attributeTable = new AttributeTable(signedAttributes);
        SignerInfoGenerator signatureGenerator
                = new JcaSimpleSignerInfoGeneratorBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).setSignedAttributeGenerator(
                        attributeTable).build(algorithmName, privateKey, x509Cert);
        if (!useAlgorithmIdentifierProtectionAttribute) {
            //remove the Algorithm Identifier Protection Attribute - WARNING this is an operation that makes the signature useless
            final CMSAttributeTableGenerator tableGenerator = signatureGenerator.getSignedAttributeTableGenerator();
            signatureGenerator = new SignerInfoGenerator(signatureGenerator, new DefaultSignedAttributeTableGenerator() {
                @Override
                public AttributeTable getAttributes(Map parameters) {
                    AttributeTable attributeTable = tableGenerator.getAttributes(parameters);
                    //debug: display the used IODs for the signature attributes       
                    //Algorithm Identifier Protection Attribute is OID 1.2.840.113549.1.9.52
                    //                    ASN1EncodableVector vector = attributeTable.toASN1EncodableVector();
                    //                    for (int i = 0; i < vector.size(); i++) {
                    //                        System.out.println(vector.get(i).toASN1Primitive());
                    //                    }
                    //                    System.out.println("---");
                    attributeTable = attributeTable.remove(CMSAttributes.cmsAlgorithmProtect);
                    //                    vector = attributeTable.toASN1EncodableVector();
                    //                    for (int i = 0; i < vector.size(); i++) {
                    //                        System.out.println(vector.get(i).toASN1Primitive());
                    //                    }
                    return (attributeTable);
                }
            }, signatureGenerator.getUnsignedAttributeTableGenerator());
        }
        return (signatureGenerator);
    }

    /**
     * @param chain certificate chain, chain[0] is the signers certificate
     * itself Signs the data using S/MIME 3.1 - don't use if for S/MIME 3.2 or
     * higher
     * @param useAlgorithmIdentifierProtectionAttribute should be set to true,
     * see RFC 6211
     */
    public MimeMultipart sign(MimeMessage message, Certificate[] chain, Key key, String digest,
            boolean useAlgorithmIdentifierProtectionAttribute) throws Exception {
        if (message == null) {
            throw new Exception("Message sign: The MIME message to sign is absent");
        }
        X509Certificate x509Cert = this.castCertificate(chain[0]);
        PrivateKey privKey = this.getPrivateKey(key);
        SMIMESignedGenerator signedDataGenerator = new SMIMESignedGenerator(SMIMESignedGenerator.RFC3851_MICALGS);
        ASN1EncodableVector signedAttributes = new ASN1EncodableVector();
        SMIMECapabilityVector caps = new SMIMECapabilityVector();
        caps.addCapability(SMIMECapability.dES_EDE3_CBC);
        caps.addCapability(SMIMECapability.rC2_CBC, 128);
        caps.addCapability(SMIMECapability.dES_CBC);
        signedAttributes.add(new SMIMECapabilitiesAttribute(caps));
        boolean isECKey = x509Cert.getPublicKey().getAlgorithm().equals("EC");
        String algorithm = this.getSignAlgorithmByInternalDigestName(digest, isECKey);
        signedDataGenerator.addSignerInfoGenerator(
                this.createSignerInfoGenerator(signedAttributes, privKey, x509Cert, algorithm,
                        useAlgorithmIdentifierProtectionAttribute)
        );
        //add cert store
        List<Certificate> certList = Arrays.asList(chain);
        Store certStore = new JcaCertStore(certList);
        signedDataGenerator.addCertificates(certStore);
        MimeMultipart multipart = signedDataGenerator.generate(message);
        return (multipart);
    }

    /**
     * @param chain certificate chain, chain[0] is the signers certificate
     * itself
     * @param useAlgorithmIdentifierProtectionAttribute should be set to true,
     * see RFC 6211
     */
    public MimeMessage signToMessage(MimeMessage message, Certificate[] chain, Key key, String digest,
            boolean useAlgorithmIdentifierProtectionAttribute) throws Exception {
        MimeMultipart multipart = this.sign(message, chain, key, digest, useAlgorithmIdentifierProtectionAttribute);
        MimeMessage signedMessage = new MimeMessage(Session.getInstance(System.getProperties(), null));
        signedMessage.setContent(multipart, multipart.getContentType());
        signedMessage.saveChanges();
        return (signedMessage);
    }

    /**
     * Returns the digest OID algorithm from a signature that signs the passed
     * message part The return value for sha1 is e.g. "1.3.14.3.2.26".
     */
    public String getDigestAlgOIDFromSignature(Part part) throws Exception {
        if (part == null) {
            throw new GeneralSecurityException("getDigestAlgOIDFromSignature: Part is absent");
        }
        if (part.isMimeType("multipart/signed")) {
            MimeMultipart signedMultiPart = null;
            if (part.getContent() instanceof MimeMultipart) {
                signedMultiPart = (MimeMultipart) part.getContent();
            } else {
                //assuming it is an inputstream now
                signedMultiPart = new MimeMultipart(new ByteArrayDataSource((InputStream) part.getContent(), part.getContentType()));
            }
            SMIMESigned signed = new SMIMESigned(signedMultiPart);
            SignerInformationStore signerStore = signed.getSignerInfos();
            Collection<SignerInformation> signerCollection = signerStore.getSigners();
            for (SignerInformation signerInfo : signerCollection) {
                return (signerInfo.getDigestAlgOID());
            }
            throw new GeneralSecurityException("getDigestAlgOIDFromSignature: Unable to identify signature algorithm.");
        }
        throw new GeneralSecurityException("Content-Type indicates data isn't signed");
    }

    /**
     * Returns the encryption OID algorithm from a signature that signs the
     * passed message part The return value for RSASSA-PSS is for example
     * 1.2.840.113549.1.1.10
     */
    public String getEncryptionAlgOIDFromSignature(Part part) throws Exception {
        if (part == null) {
            throw new GeneralSecurityException("getDigestAlgOIDFromSignature: Part is absent");
        }
        if (part.isMimeType("multipart/signed")) {
            MimeMultipart signedMultiPart = null;
            if (part.getContent() instanceof MimeMultipart) {
                signedMultiPart = (MimeMultipart) part.getContent();
            } else {
                //assuming it is an inputstream now
                signedMultiPart = new MimeMultipart(new ByteArrayDataSource((InputStream) part.getContent(), part.getContentType()));
            }
            SMIMESigned signed = new SMIMESigned(signedMultiPart);
            SignerInformationStore signerStore = signed.getSignerInfos();
            Collection<SignerInformation> signerCollection = signerStore.getSigners();
            for(SignerInformation signerInfo:signerCollection){
                return (signerInfo.getEncryptionAlgOID());
            }
            throw new GeneralSecurityException("getEncryptionAlgOIDFromSignature: Unable to identify encryption algorithm.");
        }
        throw new GeneralSecurityException("Content-Type indicates data isn't signed");
    }

    /**
     * Returns the digest OID algorithm from a pkcs7 signature The return value
     * for sha1 is e.g. "1.3.14.3.2.26".
     */
    public String getDigestAlgOIDFromSignature(byte[] signature) throws Exception {
        if (signature == null) {
            throw new GeneralSecurityException("getDigestAlgOIDFromSignature: Signature is absent");
        }
        CMSSignedData signedData = new CMSSignedData(signature);
        SignerInformationStore signers = signedData.getSignerInfos();
        Collection<SignerInformation> signerCollection = signers.getSigners();
        for (SignerInformation signerInfo : signerCollection) {
            return (signerInfo.getDigestAlgOID());
        }
        throw new GeneralSecurityException("getDigestAlgOIDFromSignature: Unable to identify signature algorithm.");
    }

    /**
     * Returns the digest OID algorithm from a signature. The return value for
     * sha1 is e.g. "1.3.14.3.2.26".
     */
    public String getDigestAlgOIDFromSignature(InputStream signed, Certificate cert) throws Exception {
        CMSSignedDataParser parser = new CMSSignedDataParser(new JcaDigestCalculatorProviderBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(), signed);
        parser.getSignedContent().drain();
        SignerInformationStore signers = parser.getSignerInfos();
        Collection<SignerInformation> signerCollection = signers.getSigners();
        X509CertificateHolder certHolder = new X509CertificateHolder(cert.getEncoded());
        SignerInformationVerifier verifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(certHolder);
        for (SignerInformation signerInformation : signerCollection) {
            boolean verified = signerInformation.verify(verifier);
            if (verified) {
                return (signerInformation.getDigestAlgOID());
            }
        }
        throw new GeneralSecurityException("getDigestAlgOIDFromSignature: Unable to identify signature algorithm.");
    }

    /**
     * Verifies a signature of a passed content against the passed certificate
     */
    public boolean verify(byte[] content, byte[] signature, Certificate cert) throws Exception {
        if (content == null) {
            throw new GeneralSecurityException("Signature verification: The content is absent");
        }
        if (signature == null) {
            throw new GeneralSecurityException("Signature verification: The signature is absent");
        }
        if (signature.length == 0) {
            throw new Exception("Signature verification: The signature length is 0");
        }
        X509CertificateHolder certHolder = new X509CertificateHolder(cert.getEncoded());
        CMSTypedStream signedContent = new CMSTypedStream(new ByteArrayInputStream(content));
        CMSSignedDataParser dataParser = new CMSSignedDataParser(new BcDigestCalculatorProvider(),
                signedContent, new ByteArrayInputStream(signature));
        dataParser.getSignedContent().drain();
        SignerInformationStore signers = dataParser.getSignerInfos();
        Collection<SignerInformation> signerCollection = signers.getSigners();
        SignerInformationVerifier signerInfoVerifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(certHolder);
        boolean verified = false;
        for (SignerInformation signerInformation : signerCollection) {
            if (!verified) {
                try {
                    verified = signerInformation.verify(signerInfoVerifier);
                } catch (CMSException e) {
                    //check if the cause is an invalidkeyexception - this means that the verification has been
                    //performed on EC signed data using a RSA key or verce visa
                    Throwable cause = e.getCause();
                    while (cause != null) {
                        if (cause instanceof InvalidKeyException) {
                            if (cert.getPublicKey().getAlgorithm().equals("EC")) {
                                throw new Exception("You tried to verify a signature using a EC certificate that requires a RSA certificate. Please verify your security settings - you have set up the wrong certificate for the signature verification.");
                            } else {
                                throw new Exception("You tried to verify a signature using a RSA certificate that requires a EC certificate. Please verify your security settings - you have set up the wrong certificate for the signature verification.");
                            }
                        }
                        cause = cause.getCause();
                    }
                    throw e;
                }
            }
            if (verified) {
                break;
            }
        }
        return (verified);
    }

    /**
     * Verifies a signature against the passed certificate
     *
     * @param encoding one of 7bit quoted-printable base64 8bit binary
     */
    public MimeBodyPart verify(Part part, Certificate cert) throws Exception {
        return (this.verify(part, null, cert, false));
    }

    /**
     * Verifies a signature against the passed certificate
     *
     * @param contentTransferEncoding one of 7bit quoted-printable base64 8bit
     * binary
     * @param ignoreSignatureVerificationError Performs the signature
     * verification but do not raise an error if it fails - or do raise an error
     */
    public MimeBodyPart verify(Part part, String contentTransferEncoding, Certificate cert, boolean ignoreSignatureVerificationError) throws Exception {
        if (part == null) {
            throw new GeneralSecurityException("Signature verification failed: The MIME part is absent");
        }
        if (part.isMimeType("multipart/signed")) {
            MimeMultipart signedMultiPart = (MimeMultipart) part.getContent();
            //possible encoding: 7bit quoted-printable base64 8bit binary
            SMIMESigned signed = null;
            if (contentTransferEncoding == null) {
                //the default encoding in BC is 7bit but the default content transfer encoding in AS2 is binary.
                signed = new SMIMESigned(signedMultiPart, "binary");
            } else {
                signed = new SMIMESigned(signedMultiPart, contentTransferEncoding);
            }
            if (!ignoreSignatureVerificationError) {
                //perform the signature verification - this will be successful or not
                X509Certificate x509Cert = this.castCertificate(cert);
                X509CertificateHolder certHolder = new X509CertificateHolder(cert.getEncoded());
                SignerInformationVerifier verifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(certHolder);
                SignerInformationStore signerStore = signed.getSignerInfos();
                Collection<SignerInformation> signerCollection = signerStore.getSigners();
                for (SignerInformation signerInfo : signerCollection) {
                    //System.out.println("DEBUG Found digest algorithm: " + signerInfo.getDigestAlgorithmID().getAlgorithm().getId());                    
                    //System.out.println("DEBUG Found encryption algorithm: " + signerInfo.toASN1Structure().getDigestEncryptionAlgorithm().getAlgorithm().getId());
                    if (!signerInfo.verify(verifier)) {
                        StringBuilder signatureCertInfo = new StringBuilder();
                        //try to gain more information about the problem
                        if (signerInfo.getSID() != null) {
                            if (signerInfo.getSID().getSerialNumber() != null) {
                                signatureCertInfo.append("Serial number (DEC): ");
                                signatureCertInfo.append(signerInfo.getSID().getSerialNumber());
                            }
                            if (signerInfo.getSID().getIssuer() != null) {
                                if (signatureCertInfo.length() > 0) {
                                    signatureCertInfo.append("\n");
                                }
                                signatureCertInfo.append("Issuer: ");
                                signatureCertInfo.append(signerInfo.getSID().getIssuer().toString());
                            }
                        }
                        if (signatureCertInfo.length() > 0) {
                            signatureCertInfo.insert(0, "Signature certificate information:\n");
                        }
                        StringBuilder checkCertInfo = new StringBuilder();
                        KeystoreCertificate certificate = new KeystoreCertificate();
                        certificate.setCertificate(x509Cert);
                        checkCertInfo.append("Verification certificate information:\n");
                        checkCertInfo.append("Serial number (DEC): ");
                        checkCertInfo.append(certificate.getSerialNumberDEC());
                        checkCertInfo.append("\n");
                        checkCertInfo.append("Serial number (HEX): ");
                        checkCertInfo.append(certificate.getSerialNumberHEX());
                        checkCertInfo.append("\n");
                        checkCertInfo.append("Finger print (SHA-1): ");
                        checkCertInfo.append(certificate.getFingerPrintSHA1());
                        checkCertInfo.append("\n");
                        checkCertInfo.append("Valid from: ");
                        checkCertInfo.append(DateFormat.getDateInstance(DateFormat.SHORT).format(certificate.getNotBefore()));
                        checkCertInfo.append("\n");
                        checkCertInfo.append("Valid to: ");
                        checkCertInfo.append(DateFormat.getDateInstance(DateFormat.SHORT).format(certificate.getNotAfter()));
                        checkCertInfo.append("\n");
                        checkCertInfo.append("Issuer: ");
                        checkCertInfo.append(x509Cert.getIssuerX500Principal().toString());
                        StringBuilder message = new StringBuilder("Verification failed");
                        message.append("\n\n");
                        message.append(signatureCertInfo);
                        message.append("\n\n");
                        message.append(checkCertInfo);
                        throw new SignatureException(message.toString());
                    }
                }
            }
            return signed.getContent();
        } else {
            throw new GeneralSecurityException("Content-Type indicates data isn't signed");
        }
    }

    private X509Certificate castCertificate(Certificate cert) throws GeneralSecurityException {
        if (cert == null) {
            throw new GeneralSecurityException("castCertificate: Certificate is absent");
        }
        if (!(cert instanceof X509Certificate)) {
            throw new GeneralSecurityException("castCertificate: Certificate must be an instance of X509Certificate");
        } else {
            return (X509Certificate) cert;
        }
    }

    private PrivateKey getPrivateKey(Key key) throws GeneralSecurityException {
        if (key == null) {
            throw new GeneralSecurityException("getPrivateKey: Key is absent");
        }
        if (!(key instanceof PrivateKey)) {
            throw new GeneralSecurityException("getPrivateKey: Key must implement PrivateKey interface");
        } else {
            return (PrivateKey) key;
        }
    }

    /**
     * Converts the passed algorithm or OID
     */
    public String convertAlgorithmNameToOID(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NoSuchAlgorithmException("convertAlgorithmNameToOID: Unable to proceed - Algorithm is absent");
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_MD5)) {
            return (CMSSignedGenerator.DIGEST_MD5);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_SHA1)) {
            return (CMSSignedGenerator.DIGEST_SHA1);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_SHA224) || algorithm.equalsIgnoreCase("sha224")) {
            return (CMSSignedGenerator.DIGEST_SHA224);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_SHA256) || algorithm.equalsIgnoreCase("sha256")) {
            return (CMSSignedGenerator.DIGEST_SHA256);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_SHA384) || algorithm.equalsIgnoreCase("sha384")) {
            return (CMSSignedGenerator.DIGEST_SHA384);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_SHA512) || algorithm.equalsIgnoreCase("sha512")) {
            return (CMSSignedGenerator.DIGEST_SHA512);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_SHA3_224)) {
            return (NISTObjectIdentifiers.id_sha3_224.getId());
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_SHA3_256)) {
            return (NISTObjectIdentifiers.id_sha3_256.getId());
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_SHA3_384)) {
            return (NISTObjectIdentifiers.id_sha3_384.getId());
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_SHA3_512)) {
            return (NISTObjectIdentifiers.id_sha3_512.getId());
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_3DES)) {
            return ("1.2.840.113549.3.7");
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_DES)) {
            return ("1.3.14.3.2.7");
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_CAST5)) {
            return (CMSEnvelopedDataGenerator.CAST5_CBC);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_IDEA)) {
            return (CMSEnvelopedDataGenerator.IDEA_CBC);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_RC2)) {
            return (CMSEnvelopedDataGenerator.RC2_CBC);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_RC4)) {
            return ("1.2.840.113549.3.4");
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_AES_128)) {
            return (CMSEnvelopedDataGenerator.AES128_CBC);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_AES_192)) {
            return (CMSEnvelopedDataGenerator.AES192_CBC);
        } else if (algorithm.equalsIgnoreCase(ALGORITHM_AES_256)) {
            return (CMSEnvelopedDataGenerator.AES256_CBC);
        } else {
            throw new NoSuchAlgorithmException("Unsupported algorithm: " + algorithm);
        }
    }

    /**
     * Converts the passed algorithm or OID
     */
    public String convertOIDToAlgorithmName(String oid) throws NoSuchAlgorithmException {
        if (oid == null) {
            throw new NoSuchAlgorithmException("convertOIDToAlgorithmName: OID is absent");
        } else if (oid.equalsIgnoreCase(CMSSignedGenerator.DIGEST_MD5)) {
            return (ALGORITHM_MD5);
        } else if (oid.equalsIgnoreCase(CMSSignedGenerator.DIGEST_SHA1)) {
            return (ALGORITHM_SHA1);
        } else if (oid.equalsIgnoreCase(CMSSignedGenerator.DIGEST_SHA224)) {
            return (ALGORITHM_SHA224);
        } else if (oid.equalsIgnoreCase(CMSSignedGenerator.DIGEST_SHA256)) {
            return (ALGORITHM_SHA256);
        } else if (oid.equalsIgnoreCase(CMSSignedGenerator.DIGEST_SHA384)) {
            return (ALGORITHM_SHA384);
        } else if (oid.equalsIgnoreCase(CMSSignedGenerator.DIGEST_SHA512)) {
            return (ALGORITHM_SHA512);
        } else if (oid.equalsIgnoreCase(NISTObjectIdentifiers.id_sha3_224.getId())) {
            return (ALGORITHM_SHA3_224);
        } else if (oid.equalsIgnoreCase(NISTObjectIdentifiers.id_sha3_256.getId())) {
            return (ALGORITHM_SHA3_256);
        } else if (oid.equalsIgnoreCase(NISTObjectIdentifiers.id_sha3_384.getId())) {
            return (ALGORITHM_SHA3_384);
        } else if (oid.equalsIgnoreCase(NISTObjectIdentifiers.id_sha3_512.getId())) {
            return (ALGORITHM_SHA3_512);
        } else if (oid.equalsIgnoreCase(CMSEnvelopedDataGenerator.CAST5_CBC)) {
            return (ALGORITHM_CAST5);
        } else if (oid.equalsIgnoreCase(CMSEnvelopedDataGenerator.DES_EDE3_CBC)) {
            return (ALGORITHM_3DES);
        } else if (oid.equalsIgnoreCase("1.3.14.3.2.7")) {
            return (ALGORITHM_DES);
        } else if (oid.equalsIgnoreCase(CMSEnvelopedDataGenerator.IDEA_CBC)) {
            return (ALGORITHM_IDEA);
        } else if (oid.equalsIgnoreCase(CMSEnvelopedDataGenerator.RC2_CBC)) {
            return (ALGORITHM_RC2);
        } else if (oid.equalsIgnoreCase("1.2.840.113549.3.4")) {
            return (ALGORITHM_RC4);
        } else if (oid.equalsIgnoreCase(CMSEnvelopedDataGenerator.AES128_CBC)) {
            return (ALGORITHM_AES_128);
        } else if (oid.equalsIgnoreCase(CMSEnvelopedDataGenerator.AES192_CBC)) {
            return (ALGORITHM_AES_192);
        } else if (oid.equalsIgnoreCase(CMSEnvelopedDataGenerator.AES256_CBC)) {
            return (ALGORITHM_AES_256);
        } else {
            throw new NoSuchAlgorithmException("Unsupported algorithm: OID " + oid);
        }
    }

    /**
     *
     * @param type Keystore type which should be one of the class constants
     * @return
     * @throws java.security.KeyStoreException
     * @throws java.security.NoSuchProviderException
     */
    public KeyStore createKeyStoreInstance(String type) throws KeyStoreException, NoSuchProviderException {
        if (type.equals(KEYSTORE_PKCS12)) {
            return KeyStore.getInstance(type, BouncyCastleProvider.PROVIDER_NAME);
        } else {
            return KeyStore.getInstance(type);
        }
    }

    /**
     * returns a CMS encrypted byte array
     */
    public byte[] encryptCMS(byte[] data, final String ALGORITHM_NAME, Certificate cert) throws Exception {
        ByteArrayInputStream dataMem = new ByteArrayInputStream(data);
        ByteArrayOutputStream encryptedMem = new ByteArrayOutputStream();
        this.encryptCMS(dataMem, encryptedMem, ALGORITHM_NAME, cert, true);
        dataMem.close();
        encryptedMem.close();
        return (encryptedMem.toByteArray());
    }

    /**
     * Generates a CMSEnvelopedDataStreamGenerator to encrypt a data stream.
     * This method checks if the passed certificate is a EC certificate or a
     * DSA/RSA certificate. For different certificates there are different ways
     * to create the data stream generator.
     *
     * @param cert
     * @return
     */
    public CMSEnvelopedDataStreamGenerator generateCMSEnvelopedDataStreamGenerator(Certificate cert, AlgorithmIdentifier keyTransportScheme) throws Exception {
        CMSEnvelopedDataStreamGenerator dataStreamGenerator = new CMSEnvelopedDataStreamGenerator();
        String algorithm = cert.getPublicKey().getAlgorithm();
        if (algorithm.equalsIgnoreCase("EC")) {
            //RFC 5753 Use of Elliptic Curve Cryptography (ECC) Algorithms
            //in Cryptographic Message Syntax (CMS)
            //3.2.1. Fields of KeyAgreeRecipientInfo
            KeyPair keypair = this.generateECKeypair(cert);
            JceKeyAgreeRecipientInfoGenerator recipientGenerator
                    = new JceKeyAgreeRecipientInfoGenerator(CMSAlgorithm.ECMQV_SHA1KDF, keypair.getPrivate(), keypair.getPublic(),
                            CMSAlgorithm.AES128_WRAP).setProvider(BouncyCastleProvider.PROVIDER_NAME);
            recipientGenerator.addRecipient((X509Certificate) cert);
            dataStreamGenerator.addRecipientInfoGenerator(recipientGenerator);
        } else {
            X509Certificate x509Cert = this.castCertificate(cert);
            if (keyTransportScheme != null) {
                dataStreamGenerator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(x509Cert, keyTransportScheme).setProvider(BouncyCastleProvider.PROVIDER_NAME));
            } else {
                dataStreamGenerator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(x509Cert).setProvider(BouncyCastleProvider.PROVIDER_NAME));
            }
        }
        return (dataStreamGenerator);
    }

    /**
     * Encrypts data to a stream
     */
    public void encryptCMS(InputStream rawStream, OutputStream encryptedStream,
            final String ALGORITHM_NAME, Certificate cert, boolean inMemory) throws Exception {
        CMSEnvelopedDataStreamGenerator dataStreamGenerator = this.generateCMSEnvelopedDataStreamGenerator(cert, null);
        String oid = this.convertAlgorithmNameToOID(ALGORITHM_NAME);
        if (inMemory) {
            ByteArrayOutputStream memBuffer = new ByteArrayOutputStream();
            OutputStream cmsEnveloped = null;
            try {
                ASN1ObjectIdentifier objectIdentifier = new ASN1ObjectIdentifier(oid);
                OutputEncryptor outputEncryptor = new JceCMSContentEncryptorBuilder(objectIdentifier).build();
                cmsEnveloped = dataStreamGenerator.open(memBuffer, outputEncryptor);
                rawStream.transferTo(cmsEnveloped);
            } finally {
                if (cmsEnveloped != null) {
                    cmsEnveloped.flush();
                    cmsEnveloped.close();
                }
            }
            encryptedStream.write(memBuffer.toByteArray());
        } else {
            Path tempFile = Files.createTempFile("encrypt", ".temp");
            OutputStream fileBuffer = null;
            OutputStream cmsEnveloped = null;
            try {
                fileBuffer = Files.newOutputStream(tempFile);
                ASN1ObjectIdentifier objectIdentifier = new ASN1ObjectIdentifier(oid);
                OutputEncryptor outputEncryptor = new JceCMSContentEncryptorBuilder(objectIdentifier).build();
                cmsEnveloped = dataStreamGenerator.open(fileBuffer, outputEncryptor);
                rawStream.transferTo(cmsEnveloped);
            } finally {
                if (cmsEnveloped != null) {
                    cmsEnveloped.flush();
                    cmsEnveloped.close();
                }
                if (fileBuffer != null) {
                    fileBuffer.flush();
                    fileBuffer.close();
                }
            }
            InputStream fileIn = null;
            try {
                fileIn = Files.newInputStream(tempFile);
                fileIn.transferTo(encryptedStream);
            } finally {
                if (fileIn != null) {
                    fileIn.close();
                }
            }
            try {
                Files.delete(tempFile);
            } catch (IOException e) {
                //nop
            }
        }
    }

    /**
     * Generates a key pair on a special EC
     */
    private KeyPair generateECKeypair(Certificate cert) throws Exception {
        if (!cert.getPublicKey().getAlgorithm().equals("EC")) {
            throw new IllegalArgumentException("BCCryptoHelper.generateECKeypair requires a certificate where the public keys algorithm is \"EC\"");
        }
        ECPublicKey ecPublicKey = (ECPublicKey) cert.getPublicKey();
        ECParameterSpec ecSpec = ecPublicKey.getParameters();
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
        keyGenerator.initialize(ecSpec, new SecureRandom());
        KeyPair keyPair = keyGenerator.generateKeyPair();
        return (keyPair);
    }

    /**
     * Decrypts a formerly encrypted byte array
     */
    public byte[] decryptCMS(byte[] encrypted, Certificate cert, Key key) throws Exception {
        ByteArrayInputStream encryptedMem = new ByteArrayInputStream(encrypted);
        ByteArrayOutputStream decryptedMem = new ByteArrayOutputStream();
        this.decryptCMS(encryptedMem, decryptedMem, cert, key);
        encryptedMem.close();
        decryptedMem.close();
        return (decryptedMem.toByteArray());
    }

    /**
     * Decrypts a formerly encrypted stream. An exception will be thrown if
     * decryption is not possible
     */
    public void decryptCMS(InputStream encrypted, OutputStream decrypted, Certificate certificateReceiver, Key key) throws Exception {
        BufferedInputStream bufferedEncrypted = new BufferedInputStream(encrypted);
        BufferedOutputStream bufferedDecrypted = new BufferedOutputStream(decrypted);
        X509Certificate x509Cert = this.castCertificate(certificateReceiver);
        CMSEnvelopedDataParser parser = new CMSEnvelopedDataParser(bufferedEncrypted);
        RecipientId recipientId = null;
        boolean isECKey = certificateReceiver.getPublicKey().getAlgorithm().equals("EC");
        if (isECKey) {
            recipientId = new JceKeyAgreeRecipientId(x509Cert);
        } else {
            recipientId = new JceKeyTransRecipientId(x509Cert);
        }
        RecipientInformation recipient = parser.getRecipientInfos().get(recipientId);
        if (recipient != null) {
            CMSTypedStream cmsEncrypted = null;
            if (isECKey) {
                cmsEncrypted = recipient.getContentStream(
                        new JceKeyAgreeEnvelopedRecipient(this.getPrivateKey(key)).setProvider(BouncyCastleProvider.PROVIDER_NAME));
            } else {
                cmsEncrypted = recipient.getContentStream(
                        new JceKeyTransEnvelopedRecipient(this.getPrivateKey(key)).setProvider(BouncyCastleProvider.PROVIDER_NAME));
            }
            InputStream encryptedContent = cmsEncrypted.getContentStream();
            encryptedContent.transferTo(bufferedDecrypted);
            bufferedDecrypted.flush();
        } else {
            throw new GeneralSecurityException("Wrong key used to decrypt the data.");
        }
    }

    /**
     * Uncompresses a data stream
     */
    public void uncompressCMS(InputStream compressed, OutputStream uncompressed) throws Exception {
        CMSCompressedDataParser compressedParser = new CMSCompressedDataParser(new BufferedInputStream(compressed));
        compressedParser.getContent(new ZlibExpanderProvider()).getContentStream().transferTo(uncompressed);
        uncompressed.flush();
    }

    /**
     * Compress a data stream
     */
    public void compressCMS(InputStream uncompressed, OutputStream compressed, boolean inMemory) throws Exception {
        //streamed compression does not work without a stream buffer in bc 1.45 and before
        CMSCompressedDataStreamGenerator generator = new CMSCompressedDataStreamGenerator();
        if (inMemory) {
            ByteArrayOutputStream memBuffer = new ByteArrayOutputStream();
            OutputStream cOut = generator.open(memBuffer, new ZlibCompressor());
            uncompressed.transferTo(cOut);
            cOut.flush();
            cOut.close();
            compressed.write(memBuffer.toByteArray());
        } else {
            File tempFile = File.createTempFile("compress", ".temp");
            OutputStream fileBuffer = null;
            try {
                fileBuffer = Files.newOutputStream(tempFile.toPath());
                OutputStream cOut = generator.open(fileBuffer, new ZlibCompressor());
                uncompressed.transferTo(cOut);
                cOut.flush();
                cOut.close();
            } finally {
                if (fileBuffer != null) {
                    fileBuffer.flush();
                    fileBuffer.close();
                }
            }
            InputStream fileIn = null;
            try {
                fileIn = Files.newInputStream(tempFile.toPath());
                fileIn.transferTo(compressed);
            } finally {
                if (fileIn != null) {
                    fileIn.close();
                }
            }
            try {
                Files.delete(tempFile.toPath());
            } catch (IOException e) {
                //nop
            }
        }
    }

    public void signCMS(InputStream unsigned, OutputStream signed, final String ALGORITHM_NAME, Certificate signCert, Key signKey,
            boolean inMemory) throws Exception {
        CMSSignedDataStreamGenerator generator = new CMSSignedDataStreamGenerator();
        PrivateKey signPrivKey = this.getPrivateKey(signKey);
        ContentSigner contentSigner = new JcaContentSignerBuilder(ALGORITHM_NAME).setProvider(BouncyCastleProvider.PROVIDER_NAME).build(signPrivKey);
        generator.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(
                        new JcaDigestCalculatorProviderBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build())
                        .build(contentSigner, new X509CertificateHolder(signCert.getEncoded())));
        if (inMemory) {
            ByteArrayOutputStream memBuffer = new ByteArrayOutputStream();
            OutputStream signedOut = generator.open(memBuffer, true);
            unsigned.transferTo(signedOut);
            signedOut.flush();
            signedOut.close();
            signed.write(memBuffer.toByteArray());
        } else {
            File tempFile = File.createTempFile("sign", ".temp");
            OutputStream fileBuffer = null;
            OutputStream signedOut = null;
            try {
                fileBuffer = Files.newOutputStream(tempFile.toPath());
                signedOut = generator.open(fileBuffer, true);
                unsigned.transferTo(signedOut);
            } finally {
                if (signedOut != null) {
                    signedOut.flush();
                    signedOut.close();
                }
                if (fileBuffer != null) {
                    fileBuffer.flush();
                    fileBuffer.close();
                }
            }
            InputStream fileIn = null;
            try {
                fileIn = Files.newInputStream(tempFile.toPath());
                fileIn.transferTo(signed);
            } finally {
                if (fileIn != null) {
                    fileIn.close();
                }
            }
            try {
                Files.delete(tempFile.toPath());
            } catch (IOException e) {
                //nop
            }
        }
    }

    public boolean verifySignatureCMS(InputStream signed, Certificate cert) throws Exception {
        CMSSignedDataParser parser = new CMSSignedDataParser(
                new JcaDigestCalculatorProviderBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(), signed);
        parser.getSignedContent().drain();
        SignerInformationStore signers = parser.getSignerInfos();
        Collection signerCollection = signers.getSigners();
        Iterator it = signerCollection.iterator();
        boolean verified = false;
        X509CertificateHolder certHolder = new X509CertificateHolder(cert.getEncoded());
        SignerInformationVerifier verifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(certHolder);
        while (it.hasNext()) {
            SignerInformation signerInformation = (SignerInformation) it.next();
            if (!verified) {
                verified = signerInformation.verify(verifier);
            }
            if (verified) {
                break;
            }
        }
        return (verified);
    }

    public void removeSignatureCMS(InputStream signed, OutputStream unsigned, Certificate cert) throws Exception {
        CMSSignedDataParser parser = new CMSSignedDataParser(new JcaDigestCalculatorProviderBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(), signed);
        InputStream signedContent = parser.getSignedContent().getContentStream();
        signedContent.transferTo(unsigned);
        unsigned.flush();
    }

    /**
     * Generates a hash of a passed input stream
     */
    public byte[] generateFileHash(MessageDigest digest, InputStream in) throws IOException {
        BufferedInputStream inStream = new BufferedInputStream(in);
        byte[] buffer = new byte[4096];
        int sizeRead = -1;
        while ((sizeRead = inStream.read(buffer)) != -1) {
            digest.update(buffer, 0, sizeRead);
        }
        inStream.close();
        byte[] hash = null;
        hash = digest.digest();
        return hash;
    }

//    public static final void main( String[] args ){
//        Security.addProvider(BouncyCastleProvider.PROVIDER_NAME);
//        System.out.println( CMSSignedGenerator.DIGEST_MD5);
//    }
}
