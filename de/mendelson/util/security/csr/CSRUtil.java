//$Header: /as2/de/mendelson/util/security/csr/CSRUtil.java 16    26/09/22 10:19 Heller $
package de.mendelson.util.security.csr;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Handles csr related activities on a certificate
 *
 * @author S.Heller
 * @version $Revision: 16 $
 */
public class CSRUtil {

    private MecResourceBundle rb;

    public CSRUtil() {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCSRUtil.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    private ASN1Primitive toDERObject(byte[] data) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        ASN1InputStream asnInputStream = new ASN1InputStream(inStream);

        return asnInputStream.readObject();
    }

    private List<GeneralName> getSubjectAlternativeNames(X509Certificate certificate) throws Exception {
        List<GeneralName> namesList = new ArrayList<GeneralName>();
        //Each entry is a List whose first entry is an Integer (the name type, 0-8) and whose second entry is a String or a 
        //byte array (the name, in string or ASN.1 DER encoded form, respectively).
        Collection<List<?>> parsedNamesList = certificate.getSubjectAlternativeNames();
        //nothing found -> return empty list
        if (parsedNamesList == null) {
            return (namesList);
        }
        for (List list : parsedNamesList) {
            if (list.size() == 2) {
                int tagNo = ((Integer) list.get(0)).intValue();
                if (list.get(1) instanceof byte[]) {
                    GeneralName newName = new GeneralName(tagNo, this.toDERObject((byte[]) list.get(1)));
                    namesList.add(newName);
                } else if (list.get(1) instanceof String) {
                    GeneralName newName = new GeneralName(tagNo, list.get(1).toString());
                    namesList.add(newName);
                }
            }
        }
        return (namesList);
    }

    /**
     * Generates a PKCS10 CertificationRequest. The passed private key must not
     * be trusted
     */
    public PKCS10CertificationRequest createCSR(String dn, PrivateKey key, X509Certificate cert) throws Exception {
        X500Name x500DNName = new X500Name(dn);
        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
        AsymmetricKeyParameter privateKeyAsymKeyParam = PrivateKeyFactory.createKey(key.getEncoded());
        ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(privateKeyAsymKeyParam);
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(cert.getPublicKey().getEncoded());
        PKCS10CertificationRequestBuilder builder = new PKCS10CertificationRequestBuilder(x500DNName, subPubKeyInfo);
        /*
         * Add SubjectAlternativeNames (SANs) using the ExtensionsGenerator
         */
        List<GeneralName> sanList = this.getSubjectAlternativeNames(cert);
        if (!sanList.isEmpty()) {
            ExtensionsGenerator extGen = new ExtensionsGenerator();
            GeneralName[] sanArray = new GeneralName[sanList.size()];
            sanList.toArray(sanArray);
            GeneralNames subjectAltNames = new GeneralNames(sanArray);
            extGen.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
            builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());
        }
        PKCS10CertificationRequest csr = builder.build(sigGen);
        ContentVerifierProvider verifier = new JcaContentVerifierProviderBuilder()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME).build(cert);
        boolean verified = csr.isSignatureValid(verifier);
        if (!verified) {
            throw new Exception(this.rb.getResourceString("verification.failed"));
        }
        return (csr);
    }

    /**
     * Generates a PKCS10 CertificationRequest. The passed private key must not
     * be trusted
     */
    public PKCS10CertificationRequest generateCSR(CertificateManager manager, String privateKeyAlias) throws Exception {
        PrivateKey key = manager.getPrivateKey(privateKeyAlias);
        KeyStoreUtil keystoreUtil = new KeyStoreUtil();
        Certificate[] certchain = manager.getCertificateChain(privateKeyAlias);
        X509Certificate[] x509Certchain = new X509Certificate[certchain.length];
        for (int i = 0; i < certchain.length; i++) {
            x509Certchain[i] = (X509Certificate) certchain[i];
        }
        x509Certchain = keystoreUtil.orderX509CertChain(x509Certchain);
        //get the subject alternate names
        X509Certificate endCert = x509Certchain[0];
        PKCS10CertificationRequest csr = this.createCSR(endCert.getSubjectDN().toString(), key, endCert);
        return (csr);
    }

    /**
     * Writes the CSR to a string
     */
    public String storeCSRPEM(PKCS10CertificationRequest csr) throws Exception {
        JcaPEMWriter pemWriter = null;
        StringWriter stringWriter = new StringWriter();
        try {
            pemWriter = new JcaPEMWriter(stringWriter);
            pemWriter.writeObject(csr);
            pemWriter.flush();
        } finally {
            if (pemWriter != null) {
                pemWriter.close();
            }
        }
        return (stringWriter.toString());
    }

    /**
     * Writes a csr to a file, PEM encoded
     */
    public void storeCSRPEM(PKCS10CertificationRequest csr, Path outFile) throws Exception {
        JcaPEMWriter pemWriter = null;
        try {
            pemWriter = new JcaPEMWriter(Files.newBufferedWriter(outFile));
            pemWriter.writeObject(csr);
            pemWriter.flush();
        } finally {
            if (pemWriter != null) {
                pemWriter.close();
            }
        }
    }

    /**
     * Imports the answer of the CA which looks like a certificate. The patched
     * certificate will be updated with the cert chain that is included in the
     * returned signed certificate.
     *
     */
    public boolean importCSRReply(CertificateManager manager, String alias, Path csrResponseFile) throws Throwable {
        PrivateKey key = manager.getPrivateKey(alias);
        PublicKey publicKey = manager.getPublicKey(alias);
        // Load certificates found in the PEM(!) encoded answer
        List<X509Certificate> responseCertList = new ArrayList<X509Certificate>();
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(csrResponseFile);
            for (Certificate responseCert : CertificateFactory.getInstance("X509").generateCertificates(inputStream)) {
                responseCertList.add((X509Certificate) responseCert);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        if (responseCertList.isEmpty()) {
            throw new Exception(this.rb.getResourceString("no.certificates.in.reply"));
        }
        PublicKey responsePublicKey = responseCertList.get(responseCertList.size() - 1).getPublicKey();
        if (!publicKey.equals(responsePublicKey)) {
            throw new Exception(this.rb.getResourceString("response.public.key.does.not.match"));
        }
        List<X509Certificate> newCerts;
        if (responseCertList.size() == 1) {
            // Reply has only one certificate
            newCerts = this.buildNewTrustChain(manager, responseCertList.get(0));
        } else {
            // Reply has a chain of certificates
            newCerts = this.validateReply(responseCertList);
        }
        if (newCerts != null) {
            manager.setKeyEntry(alias, key, newCerts.toArray(new X509Certificate[newCerts.size()]));
            return true;
        } else {
            return false;
        }
    }

    private List<X509Certificate> buildNewTrustChain(CertificateManager manager, X509Certificate certReply)
            throws Exception {
        Map<X500Principal, List<X509Certificate>> knownCerts = manager.getIssuerCertificateMap();
        LinkedList<X509Certificate> newTrustChain = new LinkedList<X509Certificate>();
        this.buildNewTrustChainRecursive(manager, certReply, newTrustChain, knownCerts);
        return (newTrustChain);
    }

    /**
     * Builds a new certificate chain from the answer
     */
    private void buildNewTrustChainRecursive(CertificateManager manager, X509Certificate certificate, LinkedList<X509Certificate> newTrustChain,
            Map<X500Principal, List<X509Certificate>> availableCertificates) throws Exception {
        X500Principal subject = certificate.getSubjectX500Principal();
        X500Principal issuer = certificate.getIssuerX500Principal();
        // Check if the certificate is a root certificate (i.e. was issued by the same Principal that
        // is present in the subject)
        if (subject.equals(issuer)) {
            newTrustChain.addFirst(certificate);
            return;
        }
        // Get the list of known certificates of the certificate's issuer
        List<X509Certificate> issuerCerts = availableCertificates.get(issuer);
        if (issuerCerts == null || issuerCerts.isEmpty()) {
            // A certificate is in the chain that is missing in the available certificates -> has to be imported first
            throw new Exception(this.rb.getResourceString("missing.cert.in.trustchain", issuer));
        }
        for (X509Certificate issuerCert : issuerCerts) {
            PublicKey publickey = issuerCert.getPublicKey();
            // Verify the certificate with the specified public key
            certificate.verify(publickey);
            this.buildNewTrustChainRecursive(manager, issuerCert, newTrustChain, availableCertificates);
        }
        newTrustChain.addFirst(certificate);
    }

    /**
     * Validates chain in certification reply, and returns the ordered elements
     * of the chain (with user certificate first, and root certificate last in
     * the array).
     *
     * @param alias the alias name
     * @param userCert the user certificate of the alias
     * @param replyCerts the chain provided in the reply
     */
    private List<X509Certificate> validateReply(List<X509Certificate> replyCerts) throws Exception {
        // order the certs in the reply (bottom-up).
        X509Certificate tmpCert = null;
        Principal issuer = replyCerts.get(0).getIssuerDN();
        for (int i = 1; i < replyCerts.size(); i++) {
            // find a cert in the reply whose "subject" is the same as the
            // given "issuer"
            int j;
            for (j = i; j < replyCerts.size(); j++) {
                Principal subject = replyCerts.get(j).getSubjectDN();
                if (subject.equals(issuer)) {
                    tmpCert = replyCerts.get(i);
                    replyCerts.set(i, replyCerts.get(j));
                    replyCerts.set(j, tmpCert);
                    issuer = replyCerts.get(i).getIssuerDN();
                    break;
                }
            }
            if (j == replyCerts.size()) {
                throw new Exception(this.rb.getResourceString("response.chain.incomplete"));
            }
        }
        // now verify each cert in the ordered chain
        for (int i = 0; i < replyCerts.size(); i++) {
            PublicKey pubKey = replyCerts.get(i + 1).getPublicKey();
            try {
                replyCerts.get(i).verify(pubKey);
            } catch (Exception e) {
                throw new Exception(this.rb.getResourceString("response.verification.failed", e.getMessage()));
            }
        }
        return replyCerts;
    }
}
