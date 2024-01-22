//$Header: /as2/de/mendelson/comm/as2/partner/CertificateUsedByPartnerChecker.java 7     29/08/22 15:20 Heller $
package de.mendelson.comm.as2.partner;

import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.security.cert.CertificateInUseChecker;
import de.mendelson.util.security.cert.CertificateInUseInfo;
import de.mendelson.util.security.cert.KeystoreCertificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Checks if a certificate is in use by a partner
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class CertificateUsedByPartnerChecker implements CertificateInUseChecker {

    private final MecResourceBundle rb;
    private final BaseClient baseClient;
    private final Map<String, CertificateInUseInfo> infoMap = new ConcurrentHashMap<String, CertificateInUseInfo>();

    public CertificateUsedByPartnerChecker(BaseClient baseClient) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificateUsedByPartnerChecker.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.baseClient = baseClient;
        this.loadCertificateInUseInformation();
    }

    /**
     * Loads all the used certificates and their usage into the info Map
     */
    private void loadCertificateInUseInformation() {
        PartnerListResponse response = (PartnerListResponse) this.baseClient.sendSync(
                new PartnerListRequest(PartnerListRequest.LIST_ALL), Partner.TIMEOUT_PARTNER_REQUEST);
        List<Partner> partnerList = response.getList();
        //build up list of all fingerprints that are in use
        List<String> fingerprintList = new ArrayList<String>();
        for (Partner singlePartner : partnerList) {
            String cryptFingerprint = singlePartner.getCryptFingerprintSHA1();
            if (!fingerprintList.contains(cryptFingerprint)) {
                fingerprintList.add(cryptFingerprint);
            }
            String signFingerprint = singlePartner.getSignFingerprintSHA1();
            if (!fingerprintList.contains(signFingerprint)) {
                fingerprintList.add(signFingerprint);
            }
        }
        for (String fingerPrintSHA1 : fingerprintList) {
            CertificateInUseInfo info = new CertificateInUseInfo(fingerPrintSHA1);
            for (Partner singlePartner : partnerList) {
                String cryptFingerprint = singlePartner.getCryptFingerprintSHA1();
                String signFingerprint = singlePartner.getSignFingerprintSHA1();
                int partnerType = CertificateInUseInfo.PARTNER_REMOTE;
                if (singlePartner.isLocalStation()) {
                    partnerType = CertificateInUseInfo.PARTNER_LOCALSTATION;
                }
                if (fingerPrintSHA1.equals(cryptFingerprint)) {
                    info.addUsage(partnerType,
                            singlePartner.getName(),
                            this.rb.getResourceString("used.crypt"));
                }
                if (fingerPrintSHA1.equals(signFingerprint)) {
                    info.addUsage(partnerType,
                            singlePartner.getName(),
                            this.rb.getResourceString("used.sign"));
                }
            }
            this.infoMap.put(fingerPrintSHA1, info);
        }

    }

    /**
     * Checks if and for which cryptographic operation a passed certificated is
     * used in the product. Will return detailed information about the usage
     */
    @Override
    public CertificateInUseInfo checkUsed(KeystoreCertificate certificate) {
        String fingerPrintSHA1 = certificate.getFingerPrintSHA1();
        if( this.infoMap.containsKey(fingerPrintSHA1)){
            return( this.infoMap.get(fingerPrintSHA1));
        }else{
            return( new CertificateInUseInfo(fingerPrintSHA1));
        }
    }

}
