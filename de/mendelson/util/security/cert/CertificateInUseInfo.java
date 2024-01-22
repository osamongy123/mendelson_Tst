//$Header: /as2/de/mendelson/util/security/cert/CertificateInUseInfo.java 3     29/08/22 15:20 Heller $
package de.mendelson.util.security.cert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Contains information about the use of a certificate
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class CertificateInUseInfo implements Serializable{

    public static final long serialVersionUID = 1L;
    public static final int PARTNER_REMOTE = 1;
    public static final int PARTNER_GATEWAY = 2;
    public static final int PARTNER_ROUTED = 3;
    public static final int PARTNER_LOCALSTATION = 4;
    public static final int PARTNER_LOCALSTATION_VIRTUAL = 5;

    private List<SingleCertificateInUseInfo> singleUsageList = new ArrayList<SingleCertificateInUseInfo>();
    private final String fingerprintSHA1;

    public CertificateInUseInfo(String fingerprintSHA1) {
        this.fingerprintSHA1 = fingerprintSHA1;
    }

    public boolean isEmpty(){
        return( this.singleUsageList.isEmpty() );
    }
    
    public List<SingleCertificateInUseInfo> getUsageList(){
        List<SingleCertificateInUseInfo> list = new ArrayList<SingleCertificateInUseInfo>();
        list.addAll(this.singleUsageList);
        return( list );
    }
    
    
    public void addUsage(final int PARTNER_TYPE, String partnerName, String details) {
        SingleCertificateInUseInfo info = new SingleCertificateInUseInfo(PARTNER_TYPE, partnerName, details);
        this.singleUsageList.add(info);
    }

    /**
     * @return the message
     */
    public String getMessageAsText() {
        StringBuilder builder = new StringBuilder();
        for (SingleCertificateInUseInfo info : this.singleUsageList) {
            builder.append(info.getPartnerName())
                    .append(" (")
                    .append(info.getDetails())
                    .append(")")
                    .append("\n");
        }
        return (builder.toString());
    }

    /**
     * @return the fingerprintSHA1 of the certificate this info object is for
     */
    public String getFingerprintSHA1() {
        return fingerprintSHA1;
    }

    /**
     * Overwrite the equal method of object
     *
     * @param anObject object to compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof CertificateInUseInfo) {
            CertificateInUseInfo info = (CertificateInUseInfo) anObject;
            return (info.fingerprintSHA1.equals(this.fingerprintSHA1));
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.singleUsageList);
        hash = 67 * hash + Objects.hashCode(this.fingerprintSHA1);
        return hash;
    }
    
    
    public static class SingleCertificateInUseInfo implements Serializable{

        public static final long serialVersionUID = 1L;
        private final int TYPE;
        private final String partnerName;
        private final String details;

        public SingleCertificateInUseInfo(final int PARTNER_TYPE, String partnerName, String details) {
            this.TYPE = PARTNER_TYPE;
            this.details = details;
            this.partnerName = partnerName;
        }

        /**
         * @return the TYPE
         */
        public int getType() {
            return TYPE;
        }

        /**
         * @return the partnerName
         */
        public String getPartnerName() {
            return partnerName;
        }

        /**
         * @return the details
         */
        public String getDetails() {
            return details;
        }
    }

}
