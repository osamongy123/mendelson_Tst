//$Header: /as2/de/mendelson/comm/as2/partner/Partner.java 89    18/10/22 17:48 Heller $
package de.mendelson.comm.as2.partner;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.ResourceBundleAS2Message;
import de.mendelson.comm.as2.message.store.MessageStoreHandler;
import de.mendelson.comm.as2.partner.gui.ResourceBundlePartnerPanel;
import de.mendelson.comm.as2.send.HttpConnectionParameter;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.oauth2.OAuth2Config;
import de.mendelson.util.security.cert.KeystoreCertificate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores all information about a business partner
 *
 * @author S.Heller
 * @version $Revision: 89 $
 */
public class Partner implements Serializable, Comparable, Cloneable {

    public static final long serialVersionUID = 1L;
    //setup a much longer timeout than the default for the partner sync client-server requests 
    //- this should be reachable even if the system is under high load
    public static final long TIMEOUT_PARTNER_REQUEST = TimeUnit.MINUTES.toMillis(5);
    /**
     * Unique id in the database for this partner
     */
    private int dbId = -1;
    private boolean localStation = false;
    /**
     * Found identification in the message
     */
    private String as2Identification;
    /**
     * Name of the partner, defined in the PIP server
     */
    private String name;
    private PartnerCertificateInformationList partnerCertificateList = new PartnerCertificateInformationList();
    private int encryptionType = AS2Message.ENCRYPTION_3DES;
    private int signType = AS2Message.SIGNATURE_SHA1;
    private String email = "sender@as2server.com";
    private String url = this.getDefaultURL();
    private String subject = "AS2 message";
    private String contentType = "application/EDI-Consent";
    private String mdnURL = this.getDefaultURL();
    private boolean syncMDN = true;
    private boolean keepFilenameOnReceipt = false;
    private String[] pollIgnoreList = null;
    /**
     * Directory poll interval in seconds
     */
    private int pollInterval = 30;
    /**
     * Max number of files to pick up for a single poll process
     */
    private int maxPollFiles = 100;
    /**
     * Allows to disable the dir poll - the send orders have to be placed using
     * the command line interface if enabled
     */
    private boolean enableDirPoll = true;
    /**
     * Compression type for this partner, used if you send messages to the
     * partner
     */
    private int compressionType = AS2Message.COMPRESSION_NONE;
    /**
     * MDNs to this partner should be signed?
     */
    private boolean signedMDN = true;
    /**
     * Stores an async MDN HTTP authentication if requested
     */
    private HTTPAuthentication authenticationCredentialsAsyncMDN = new HTTPAuthentication();
    /**
     * Stores a send data HTTP authentication if requested
     */
    private HTTPAuthentication authenticationCredentialsMessage = new HTTPAuthentication();
    /**
     * Comment for a partner, this is not relevant for the as2 communication
     */
    private String comment = null;
    private int notifySend = 0;
    private int notifyReceive = 0;
    private int notifySendReceive = 0;
    private boolean notifySendEnabled = false;
    private boolean notifyReceiveEnabled = false;
    private boolean notifySendReceiveEnabled = false;
    private String contactCompany = null;
    private String contactAS2 = null;
    private int contentTransferEncoding = AS2Message.CONTENT_TRANSFER_ENCODING_BINARY;
    private PartnerEventInformation partnerEvents = new PartnerEventInformation();
    /**
     * Partner specific http headers
     */
    private final List<PartnerHttpHeader> httpHeader = Collections.synchronizedList(new ArrayList<PartnerHttpHeader>());
    /**
     * http protocol version for this partner
     */
    private String httpProtocolVersion = HttpConnectionParameter.HTTP_1_1;
    /**
     * An in-memory state that checks if the configuration of this partner is ok
     */
    private boolean configError = false;
    /**
     * Should be always set to true - see RFC 6211
     */
    private boolean useAlgorithmIdentifierProtectionAttribute = true;
    private boolean useOAuth2Message = false;
    private boolean useOAuth2MDN = false;
    private OAuth2Config oauth2Message = null;
    private OAuth2Config oauth2MDN = null;

    public Partner() {
    }

    /**
     * @return the configError
     */
    public boolean hasConfigError() {
        return configError;
    }

    /**
     * @param configError the configError to set
     */
    public void setConfigError(boolean configError) {
        this.configError = configError;
    }

    /**
     * Deletes http headers that contain only of a value but no key
     */
    public void deleteEmptyHttpHeader() {
        synchronized (this.httpHeader) {
            for (int i = this.httpHeader.size() - 1; i >= 0; i--) {
                PartnerHttpHeader header = this.httpHeader.get(i);
                if (header.getKey() == null || header.getKey().trim().length() == 0) {
                    this.httpHeader.remove(i);
                }
            }
        }
    }

    /**
     * Returns a list of file patterns that will be ignored by the dir poll
     * manager
     */
    public String[] getPollIgnoreList() {
        return (this.pollIgnoreList);
    }

    /**
     * Pass the poll ignore list to the partner
     */
    public void setPollIgnoreList(String[] pollIgnoreList) {
        this.pollIgnoreList = pollIgnoreList;
    }

    /**
     * Expected is a comma separated list of poll ignores
     */
    public void setPollIgnoreListString(String pollIgnoreStr) {
        if (pollIgnoreStr == null || pollIgnoreStr.trim().length() == 0) {
            this.pollIgnoreList = null;
        } else {
            StringTokenizer tokenizer = new StringTokenizer(pollIgnoreStr, ",");
            this.pollIgnoreList = new String[tokenizer.countTokens()];
            for (int i = 0; tokenizer.hasMoreTokens(); i++) {
                this.pollIgnoreList[i] = tokenizer.nextToken();
            }
        }
    }

    /**
     * Returns a String that contains a comma separated list of dir poll manager
     * ignore patterns
     */
    public String getPollIgnoreListAsString() {
        if (this.pollIgnoreList == null) {
            return (null);
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < this.pollIgnoreList.length; i++) {
            buffer.append(this.pollIgnoreList[i]);
            if (i + 1 < this.pollIgnoreList.length) {
                buffer.append(",");
            }
        }
        return (buffer.toString());
    }

    /**
     * Returns the default URL where to connect to
     */
    public String getDefaultURL() {
        return ("http://testas2.mendelson-e-c.com:8080/as2/HttpReceiver");
    }

    /**
     * Returns the path on the harddisk where the messages are stored in
     */
    public String getMessagePath(String absolutePathOnServerSideMessageDir, String serverSideFileSeparator) {
        StringBuilder messagePath = new StringBuilder();
        messagePath.append(absolutePathOnServerSideMessageDir);
        if (!messagePath.toString().endsWith(serverSideFileSeparator)) {
            messagePath.append(serverSideFileSeparator);
        }
        messagePath.append(MessageStoreHandler.convertToValidFilename(this.getName()));
        return (messagePath.toString());
    }

    public int getDBId() {
        return dbId;
    }

    public void setDBId(int dbId) {
        this.dbId = dbId;
    }

    public boolean isLocalStation() {
        return localStation;
    }

    public void setLocalStation(boolean localStation) {
        this.localStation = localStation;
    }

    public String getAS2Identification() {
        return as2Identification;
    }

    public void setAS2Identification(String as2Identification) {
        this.as2Identification = as2Identification;
    }

    public String getName() {
        if (this.name == null) {
            return (this.getAS2Identification());
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PartnerCertificateInformation getCertificateInformation(int category) {
        return (this.partnerCertificateList.getPartnerCertificate(category));
    }

    /**
     * Sets a single cert information to the partner, overwriting any existing
     * with the same status, priority and type
     */
    public void setCertificateInformation(PartnerCertificateInformation information) {
        this.partnerCertificateList.setCertificateInformation(information);
    }

    /**
     * Sets a new sign cert to this partner. This is a convenience method to set
     * a cert info of the category SIGN, overwriting prio 1 and set the new cert
     * to accepted
     */
    public void setSignFingerprintSHA1(String fingerprintSHA1) {
        PartnerCertificateInformation signInfo = new PartnerCertificateInformation(
                fingerprintSHA1,
                PartnerCertificateInformation.CATEGORY_SIGN);
        this.setCertificateInformation(signInfo);
    }

    /**
     * Sets a new encryption cert to this partner. This is a convenience method
     * to set a cert info of the category CRYPT, overwriting prio 1 and set the
     * new cert to accepted
     */
    public void setCryptFingerprintSHA1(String fingerprintSHA1) {
        PartnerCertificateInformation cryptInfo = new PartnerCertificateInformation(
                fingerprintSHA1,
                PartnerCertificateInformation.CATEGORY_CRYPT);
        this.setCertificateInformation(cryptInfo);
    }

    /**
     * Returns the alias used for signing messages. This returns the cert
     * category SIGN
     */
    public String getSignFingerprintSHA1() {
        PartnerCertificateInformation signInfo = this.getCertificateInformation(PartnerCertificateInformation.CATEGORY_SIGN);
        if (signInfo != null) {
            return (signInfo.getFingerprintSHA1());
        } else {
            return (null);
        }
    }

    /**
     * Returns the cert used for signing messages. This returns the cert
     * category SIGN
     */
    public String getCryptFingerprintSHA1() {
        PartnerCertificateInformation cryptInfo = this.getCertificateInformation(PartnerCertificateInformation.CATEGORY_CRYPT);
        if (cryptInfo != null) {
            return (cryptInfo.getFingerprintSHA1());
        } else {
            return (null);
        }
    }

    /**
     * Overwrite the existing cert list
     */
    public void setPartnerCertificateInformationList(PartnerCertificateInformationList list) {
        this.partnerCertificateList = list;
    }

    /**
     * Returns the existing cert list
     */
    public PartnerCertificateInformationList getPartnerCertificateInformationList() {
        return (this.partnerCertificateList);
    }

    public int getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(int encryptionType) {
        this.encryptionType = encryptionType;
    }

    public int getSignType() {
        return signType;
    }

    public void setSignType(int signType) {
        this.signType = signType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getMdnURL() {
        return mdnURL;
    }

    public void setMdnURL(String mdnURL) {
        this.mdnURL = mdnURL;
    }

    @Override
    public String toString() {
        if (this.name != null) {
            return (this.name);
        }
        return (this.as2Identification);
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
        if (anObject != null && anObject instanceof Partner) {
            Partner partner = (Partner) anObject;
            if (this.dbId != -1) {
                return (partner.dbId == this.dbId);
            } else {
                boolean nameMatch = partner.getName() != null && this.name != null && this.name.equals(partner.name);
                boolean as2idMatch = partner.getAS2Identification() != null && this.as2Identification != null && this.as2Identification.equals(partner.as2Identification);
                return (nameMatch && as2idMatch);
            }
        }
        return (false);
    }

    /**
     * Checks if two partner have the same content - this is slow
     */
    public static boolean hasSameContent(Partner partner1, Partner partner2, CertificateManager certmanagerEncSign) {
        String partner1Serialized = partner1.toXML(certmanagerEncSign, 0);
        String partner2Serialized = partner2.toXML(certmanagerEncSign, 0);
        return (partner1Serialized.equals(partner2Serialized));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.localStation ? 1 : 0);
        hash = 29 * hash + (this.as2Identification != null ? this.as2Identification.hashCode() : 0);
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + this.encryptionType;
        hash = 29 * hash + this.signType;
        return hash;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isSyncMDN() {
        return syncMDN;
    }

    public void setSyncMDN(boolean syncMDN) {
        this.syncMDN = syncMDN;
    }

    public int getPollInterval() {
        return (this.pollInterval);
    }

    public void setPollInterval(int pollInterval) {
        if (pollInterval > 0) {
            this.pollInterval = pollInterval;
        }
    }

    public int getCompressionType() {
        return (this.compressionType);
    }

    public void setCompressionType(int compressionType) {
        this.compressionType = compressionType;
    }

    public boolean isSignedMDN() {
        return signedMDN;
    }

    public void setSignedMDN(boolean signedMDN) {
        this.signedMDN = signedMDN;
    }

    public HTTPAuthentication getAuthenticationCredentialsAsyncMDN() {
        return authenticationCredentialsAsyncMDN;
    }

    public void setAuthenticationAsyncMDN(HTTPAuthentication authenticationCredentialsAsyncMDN) {
        this.authenticationCredentialsAsyncMDN = authenticationCredentialsAsyncMDN;
    }

    public HTTPAuthentication getAuthenticationCredentialsMessage() {
        return authenticationCredentialsMessage;
    }

    public void setAuthentication(HTTPAuthentication authenticationCredentialsMessage) {
        this.authenticationCredentialsMessage = authenticationCredentialsMessage;
    }

    public void setKeepOriginalFilenameOnReceipt(boolean keepFilenameOnReceipt) {
        this.keepFilenameOnReceipt = keepFilenameOnReceipt;
    }

    public boolean getKeepOriginalFilenameOnReceipt() {
        return (this.keepFilenameOnReceipt);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment == null || comment.length() == 0) {
            this.comment = null;
        } else {
            this.comment = comment;
        }
    }

    public int compare(Object one, Object two) {
        Partner obj1 = (Partner) one;
        Partner obj2 = (Partner) two;
        return (obj1.getName().compareToIgnoreCase(obj2.getName()));
    }

    @Override
    public int compareTo(Object obj) {
        Partner partner = (Partner) obj;
        return (this.name.compareToIgnoreCase(partner.name));
    }

    public int getNotifySend() {
        return notifySend;
    }

    public void setNotifySend(int notifySend) {
        this.notifySend = notifySend;
    }

    public int getNotifyReceive() {
        return notifyReceive;
    }

    public void setNotifyReceive(int notifyReceive) {
        this.notifyReceive = notifyReceive;
    }

    public int getNotifySendReceive() {
        return notifySendReceive;
    }

    public void setNotifySendReceive(int notifySendReceive) {
        this.notifySendReceive = notifySendReceive;
    }

    public boolean isNotifySendEnabled() {
        return notifySendEnabled;
    }

    public void setNotifySendEnabled(boolean notifySendEnabled) {
        this.notifySendEnabled = notifySendEnabled;
    }

    public boolean isNotifyReceiveEnabled() {
        return notifyReceiveEnabled;
    }

    public void setNotifyReceiveEnabled(boolean notifyReceiveEnabled) {
        this.notifyReceiveEnabled = notifyReceiveEnabled;
    }

    public boolean isNotifySendReceiveEnabled() {
        return notifySendReceiveEnabled;
    }

    public void setNotifySendReceiveEnabled(boolean notifySendReceiveEnabled) {
        this.notifySendReceiveEnabled = notifySendReceiveEnabled;
    }

    public String getDebugDisplay() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Name:\t\t").append(this.getName());
        buffer.append(" (local station: ").append(this.isLocalStation()).append(")\n");
        buffer.append("AS2 id:\t\t").append(this.getAS2Identification()).append("\n");
        buffer.append("Max poll files:\t\t").append(this.getMaxPollFiles()).append("\n");
        return (buffer.toString());
    }

    /**
     * @return the contentTransferEncoding
     */
    public int getContentTransferEncoding() {
        return contentTransferEncoding;
    }

    /**
     * @param contentTransferEncoding the contentTransferEncoding to set
     */
    public void setContentTransferEncoding(int contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    /**
     * Displays key data of this partner in a localized form - mainly for the
     * log
     */
    public String toDisplay(CertificateManager certificateManagerEncSign) {
        MecResourceBundle rbPartnerPanel = null;
        MecResourceBundle rbMessage = null;
        try {
            rbPartnerPanel = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePartnerPanel.class.getName());
            rbMessage = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Message.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            return ("Unable to resolve localization resource");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(rbPartnerPanel.getResourceString("label.name")).append(" ");
        builder.append(this.getName()).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.id")).append(" ");
        builder.append(this.getAS2Identification()).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.url")).append(" ");
        builder.append(this.getURL()).append("\n");
        String fingerPrintSign = this.getCertificateInformation(PartnerCertificateInformation.CATEGORY_SIGN).getFingerprintSHA1();
        String aliasSign = certificateManagerEncSign.getAliasByFingerprint(fingerPrintSign);
        if (aliasSign == null) {
            aliasSign = "--";
        }
        builder.append(rbPartnerPanel.getResourceString("label.signalias.cert")).append(" ");
        builder.append(aliasSign).append("\n");
        String fingerPrintCrypt = this.getCertificateInformation(PartnerCertificateInformation.CATEGORY_CRYPT).getFingerprintSHA1();
        String aliasCrypt = certificateManagerEncSign.getAliasByFingerprint(fingerPrintCrypt);
        if (aliasCrypt == null) {
            aliasCrypt = "--";
        }
        builder.append(rbPartnerPanel.getResourceString("label.cryptalias.cert")).append(" ");
        builder.append(aliasCrypt).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.signtype")).append(" ");
        builder.append(rbMessage.getResourceString("signature." + this.signType)).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.encryptiontype")).append(" ");
        builder.append(rbMessage.getResourceString("encryption." + this.encryptionType)).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.compression")).append(": ");
        builder.append(this.compressionType == AS2Message.COMPRESSION_ZLIB ? Boolean.toString(true) : Boolean.toString(false)).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.syncmdn")).append(": ");
        builder.append(Boolean.toString(this.syncMDN)).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.signedmdn")).append(": ");
        builder.append(Boolean.toString(this.signedMDN)).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.httpversion")).append(" ");
        builder.append(this.httpProtocolVersion).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.subject")).append(" ");
        builder.append(this.subject).append("\n");
        builder.append(rbPartnerPanel.getResourceString("label.contenttype")).append(" ");
        builder.append(this.contentType).append("\n");
        if (this.enableDirPoll) {
            builder.append(rbPartnerPanel.getResourceString("label.pollinterval")).append(" ");
            builder.append(this.pollInterval + "s").append("\n");
            builder.append(rbPartnerPanel.getResourceString("label.pollignore")).append(" ");
            if (this.pollIgnoreList != null) {
                for (String entry : this.pollIgnoreList) {
                    builder.append(entry + " ");
                }
            }
            builder.append("\n");
            builder.append(rbPartnerPanel.getResourceString("label.maxpollfiles")).append(" ");
            builder.append(String.valueOf(this.maxPollFiles)).append("\n");
        }
        if (this.authenticationCredentialsMessage.isEnabled()) {
            builder.append(rbPartnerPanel.getResourceString("label.httpauth.credentials.message")).append(" (Msg): ");
            builder.append(this.authenticationCredentialsMessage.getUser() + "/" 
                    + this.authenticationCredentialsMessage.getPassword()).append("\n");
        }
        if (this.authenticationCredentialsAsyncMDN.isEnabled()) {
            builder.append(rbPartnerPanel.getResourceString("label.httpauth.credentials.asyncmdn")).append(" (MDN): ");
            builder.append(this.authenticationCredentialsAsyncMDN.getUser() + "/" 
                    + this.authenticationCredentialsAsyncMDN.getPassword()).append("\n");
        }
        return (builder.toString());
    }

    /**
     * Serializes this partner to XML
     *
     * @param level level in the XML hierarchy for the xml beautifying
     */
    public String toXML(CertificateManager certmanagerEncSign, int level) {
        String offset = "";
        for (int i = 0; i < level; i++) {
            offset += "\t";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(offset).append("<partner>\n");
        builder.append(offset).append("\t<name>").append(this.toCDATA(this.name)).append("</name>\n");
        builder.append(offset).append("\t<as2ident>").append(this.toCDATA(this.as2Identification)).append("</as2ident>\n");
        builder.append( this.partnerEvents.toXML(level+1));        
        //partner comments:
        //comment, contactAS2, contactCompany
        if (this.comment != null) {
            builder.append(offset).append("\t<comment>").append(this.toCDATA(this.comment)).append("</comment>\n");
        }
        if (this.contactAS2 != null) {
            builder.append(offset).append("\t<commentcontact>").append(this.toCDATA(this.contactAS2)).append("</commentcontact>\n");
        }
        if (this.contactCompany != null) {
            builder.append(offset).append("\t<commentcompany>").append(this.toCDATA(this.contactCompany)).append("</commentcompany>\n");
        }
        builder.append(offset).append("\t<contenttype>").append(this.toCDATA(this.contentType)).append("</contenttype>\n");
        //no longer used but for compatibility of older versions: write down the crypt alias
        PartnerCertificateInformation cryptInfo = this.getCertificateInformation(PartnerCertificateInformation.CATEGORY_CRYPT);
        if (cryptInfo != null) {
            String cryptAlias = certmanagerEncSign.getAliasByFingerprint(this.getCryptFingerprintSHA1());
            builder.append(offset).append("\t<cryptalias>").append(this.toCDATA(cryptAlias)).append("</cryptalias>\n");
        }
        builder.append(offset).append("\t<email>").append(this.toCDATA(this.email)).append("</email>\n");
        builder.append(offset).append("\t<mdnurl>").append(this.toCDATA(this.mdnURL)).append("</mdnurl>\n");
        //no longer used but for compatibility of older versions: write down the sign alias
        String signAlias = certmanagerEncSign.getAliasByFingerprint(this.getSignFingerprintSHA1());
        if (signAlias != null) {
            builder.append(offset).append("\t<signalias>").append(this.toCDATA(signAlias)).append("</signalias>\n");
        }
        builder.append(offset).append("\t<subject>").append(this.toCDATA(this.subject)).append("</subject>\n");
        builder.append(offset).append("\t<url>").append(this.toCDATA(this.url)).append("</url>\n");
        builder.append(offset).append("\t<compression>").append(String.valueOf(this.compressionType)).append("</compression>\n");
        builder.append(offset).append("\t<transferencoding>").append(this.contentTransferEncoding).append("</transferencoding>\n");
        builder.append(offset).append("\t<encryptiontype>").append(String.valueOf(this.encryptionType)).append("</encryptiontype>\n");
        builder.append(offset).append("\t<keepfilename>").append(String.valueOf(this.keepFilenameOnReceipt)).append("</keepfilename>\n");
        builder.append(offset).append("\t<localstation>").append(String.valueOf(this.localStation)).append("</localstation>\n");
        builder.append(offset).append("\t<notifyreceive>").append(String.valueOf(this.notifyReceive)).append("</notifyreceive>\n");
        builder.append(offset).append("\t<notifyreceiveenabled>").append(String.valueOf(this.notifyReceiveEnabled)).append("</notifyreceiveenabled>\n");
        builder.append(offset).append("\t<notifysend>").append(String.valueOf(this.notifySend)).append("</notifysend>\n");
        builder.append(offset).append("\t<notifysendenabled>").append(String.valueOf(this.notifySendEnabled)).append("</notifysendenabled>\n");
        builder.append(offset).append("\t<notifysendreceiveenabled>").append(String.valueOf(this.notifySendReceiveEnabled)).append("</notifysendreceiveenabled>\n");
        builder.append(offset).append("\t<pollinterval>").append(String.valueOf(this.pollInterval)).append("</pollinterval>\n");
        if (this.pollIgnoreList != null) {
            builder.append(offset).append("\t<pollignorelist>").append(this.toCDATA(this.getPollIgnoreListAsString())).append("</pollignorelist>\n");
            builder.append(offset).append("\t<maxpollfiles>").append(String.valueOf(this.getMaxPollFiles())).append("</maxpollfiles>\n");
        }
        builder.append(offset).append("\t<signtype>").append(String.valueOf(this.signType)).append("</signtype>\n");
        builder.append(offset).append("\t<signedmdn>").append(String.valueOf(this.signedMDN)).append("</signedmdn>\n");
        builder.append(offset).append("\t<syncmdn>").append(String.valueOf(this.syncMDN)).append("</syncmdn>\n");
        builder.append(offset).append("\t<algorithmidentifierprotectionattribute>").append(String.valueOf(this.useAlgorithmIdentifierProtectionAttribute)).append("</algorithmidentifierprotectionattribute>\n");
        builder.append(offset).append("\t<enabledirpoll>").append(String.valueOf(this.isEnableDirPoll())).append("</enabledirpoll>\n");
        if (this.authenticationCredentialsMessage != null) {
            builder.append(this.authenticationCredentialsMessage.toXML(level + 1, "standard"));
        }
        if (this.authenticationCredentialsAsyncMDN != null) {
            builder.append(this.authenticationCredentialsAsyncMDN.toXML(level + 1, "asyncmdn"));
        }
        if( this.oauth2Message != null ){
            builder.append(this.oauth2Message.toXML(level + 1, "standard", this.useOAuth2Message));
        }
        if( this.oauth2MDN != null ){
            builder.append(this.oauth2MDN.toXML(level + 1, "asyncmdn", this.useOAuth2MDN));
        }
        synchronized (this.httpHeader) {
            if (this.httpHeader != null && this.httpHeader.size() > 0) {
                for (PartnerHttpHeader singleHeader : this.httpHeader) {
                    builder.append("\t<httpheader>\n");
                    builder.append("\t\t<key>").append(this.toCDATA(singleHeader.getKey())).append("</key>\n");
                    builder.append("\t\t<value>").append(this.toCDATA(singleHeader.getValue())).append("</value>\n");
                    builder.append("\t</httpheader>\n");
                }
            }
        }
        builder.append(offset).append("</partner>\n");
        return (builder.toString());
    }

    /**
     * Adds a CDATA indicator to XML data
     */
    private String toCDATA(String data) {
        return ("<![CDATA[" + data + "]]>");
    }

    /**
     * Deserializes a partner from an XML node
     *
     * @param manager The certificate manager to get the assigned certificates
     * from - may be null, then there is no certificate/key assigned to the
     * partner
     */
    public static Partner fromXML(CertificateManager manager, Element element) {
        Partner partner = new Partner();
        NodeList propertiesNodeList = element.getChildNodes();
        for (int i = 0; i < propertiesNodeList.getLength(); i++) {
            if (propertiesNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element property = (Element) propertiesNodeList.item(i);
                String key = property.getTagName();
                String value = property.getTextContent();
                if (key.equals("name")) {
                    partner.setName(value);
                } else if (key.equals("as2ident")) {
                    partner.setAS2Identification(value);
                } else if (key.equals("events")) {
                    PartnerEventInformation.fromXML( partner, property );
                }  else if (key.equals("comment")) {
                    partner.setComment(value);
                } else if (key.equals("commentcontact")) {
                    partner.setContactAS2(value);
                } else if (key.equals("commentcompany")) {
                    partner.setContactCompany(value);
                } else if (key.equals("contenttype")) {
                    partner.setContentType(value);
                } else if (key.equals("cryptalias")) {
                    if (manager != null) {
                        KeystoreCertificate certificate = manager.getKeystoreCertificate(value);
                        if (certificate != null) {
                            partner.setCryptFingerprintSHA1(manager.getKeystoreCertificate(value).getFingerPrintSHA1());
                        }
                    }
                } else if (key.equals("email")) {
                    partner.setEmail(value);
                } else if (key.equals("mdnurl")) {
                    partner.setMdnURL(value);
                } else if (key.equals("signalias")) {
                    if (manager != null) {
                        KeystoreCertificate certificate = manager.getKeystoreCertificate(value);
                        if (certificate != null) {
                            partner.setSignFingerprintSHA1(manager.getKeystoreCertificate(value).getFingerPrintSHA1());
                        }
                    }
                } else if (key.equals("subject")) {
                    partner.setSubject(value);
                } else if (key.equals("url")) {
                    partner.setURL(value);
                } else if (key.equals("compression")) {
                    partner.setCompressionType(Integer.valueOf(value).intValue());
                } else if (key.equals("transferencoding")) {
                    partner.setContentTransferEncoding(Integer.valueOf(value).intValue());
                } else if (key.equals("encryptiontype")) {
                    partner.setEncryptionType(Integer.valueOf(value).intValue());
                } else if (key.equals("keepfilename")) {
                    partner.setKeepOriginalFilenameOnReceipt(value.equalsIgnoreCase("true"));
                } else if (key.equals("localstation")) {
                    partner.setLocalStation(value.equalsIgnoreCase("true"));
                } else if (key.equals("notifyreceive")) {
                    partner.setNotifyReceive(Integer.valueOf(value).intValue());
                } else if (key.equals("notifyreceiveenabled")) {
                    partner.setNotifyReceiveEnabled(value.equalsIgnoreCase("true"));
                } else if (key.equals("notifysend")) {
                    partner.setNotifySend(Integer.valueOf(value).intValue());
                } else if (key.equals("notifysendenabled")) {
                    partner.setNotifySendEnabled(value.equalsIgnoreCase("true"));
                } else if (key.equals("notifysendreceiveenabled")) {
                    partner.setNotifySendReceiveEnabled(value.equalsIgnoreCase("true"));
                } else if (key.equals("pollinterval")) {
                    partner.setPollInterval(Integer.valueOf(value).intValue());
                } else if (key.equals("maxpollfiles")) {
                    partner.setMaxPollFiles(Integer.valueOf(value).intValue());
                } else if (key.equals("pollignorelist")) {
                    partner.setPollIgnoreListString(value);
                } else if (key.equals("signtype")) {
                    partner.setSignType(Integer.valueOf(value).intValue());
                } else if (key.equals("signedmdn")) {
                    partner.setSignedMDN(value.equalsIgnoreCase("true"));
                } else if (key.equals("syncmdn")) {
                    partner.setSyncMDN(value.equalsIgnoreCase("true"));
                } else if (key.equals("algorithmidentifierprotectionattribute")) {
                    partner.setUseAlgorithmIdentifierProtectionAttribute(value.equalsIgnoreCase("true"));
                } else if (key.equals("enabledirpoll")) {
                    partner.setEnableDirPoll(value.equalsIgnoreCase("true"));
                } else if (key.equals("httpheader")) {
                    NodeList httpHeaderNodeList = property.getChildNodes();
                    PartnerHttpHeader httpHeader = new PartnerHttpHeader();
                    for (int ii = 0; ii < httpHeaderNodeList.getLength(); ii++) {
                        if (httpHeaderNodeList.item(ii).getNodeType() == Node.ELEMENT_NODE) {
                            Element headerProperty = (Element) httpHeaderNodeList.item(ii);
                            String httpHeaderKey = headerProperty.getTagName();
                            String httpHeaderValue = headerProperty.getTextContent();
                            if (httpHeaderKey.equalsIgnoreCase("key")) {
                                httpHeader.setKey(httpHeaderValue);
                            } else if (httpHeaderKey.equalsIgnoreCase("value")) {
                                httpHeader.setValue(httpHeaderValue);
                            }
                        }
                    }
                    if (httpHeader.getKey() != null && httpHeader.getKey().trim().length() > 0
                            && httpHeader.getValue() != null && httpHeader.getValue().trim().length() > 0) {
                        partner.addHttpHeader(httpHeader);
                    }
                }
            }
        }
        //deserialize the HTTP authentications
        NodeList authenticationNodeList = element.getElementsByTagName("httpauthentication");
        for (int i = 0; i < authenticationNodeList.getLength(); i++) {
            Element authenticationElement = (Element) authenticationNodeList.item(i);
            if (!authenticationElement.hasAttribute("type")) {
                continue;
            }
            if (authenticationElement.getAttribute("type").equalsIgnoreCase("standard")) {
                partner.setAuthentication(HTTPAuthentication.fromXML(authenticationElement));
            } else if (authenticationElement.getAttribute("type").equalsIgnoreCase("asyncmdn")) {
                partner.setAuthenticationAsyncMDN(HTTPAuthentication.fromXML(authenticationElement));
            }
        }
        //deserialize the oauth2 authentications
        NodeList oauth2NodeList = element.getElementsByTagName("oauth2config");
        for (int i = 0; i < oauth2NodeList.getLength(); i++) {
            Element authenticationElement = (Element) oauth2NodeList.item(i);
            if (!authenticationElement.hasAttribute("type")) {
                continue;
            }
            if (authenticationElement.getAttribute("type").equalsIgnoreCase("standard")) {
                partner.setOAuth2Message(OAuth2Config.fromXML(authenticationElement));
                partner.setUseOAuth2Message(OAuth2Config.fromXMLIsEnabled(authenticationElement));
            } else if (authenticationElement.getAttribute("type").equalsIgnoreCase("asyncmdn")) {
                partner.setOAuth2MDN(OAuth2Config.fromXML(authenticationElement));
                partner.setUseOAuth2MDN(OAuth2Config.fromXMLIsEnabled(authenticationElement));
            }
        }
        return (partner);
    }

    /**
     * @return the httpHeader or null if it doesnt exist
     */
    public PartnerHttpHeader getHttpHeader(String key) {
        PartnerHttpHeader searchHeader = new PartnerHttpHeader();
        searchHeader.setKey(key);
        synchronized (this.httpHeader) {
            int index = this.httpHeader.indexOf(searchHeader);
            if (index >= 0) {
                return (this.httpHeader.get(index));
            }
        }
        return (null);
    }

    /**
     * Returns all http headers that are not listed in the passed list
     */
    public List<PartnerHttpHeader> getAllNonListedHttpHeader(List<String> keyList) {
        List<PartnerHttpHeader> nonListedHeaders = new ArrayList<PartnerHttpHeader>();
        synchronized (this.httpHeader) {
            for (PartnerHttpHeader testHeader : this.httpHeader) {
                if (!keyList.contains(testHeader.getKey())) {
                    nonListedHeaders.add(testHeader);
                }
            }
        }
        return (nonListedHeaders);
    }

    public void setHttpHeader(List<PartnerHttpHeader> list) {
        synchronized (this.httpHeader) {
            this.httpHeader.clear();
            this.httpHeader.addAll(list);
        }
    }

    /**
     * Returns all http headers of this partner
     */
    public List<PartnerHttpHeader> getHttpHeader() {
        List<PartnerHttpHeader> list = new ArrayList<PartnerHttpHeader>();
        synchronized (this.httpHeader) {
            list.addAll(this.httpHeader);
        }
        return (this.httpHeader);
    }

    /**
     * @param httpHeader the httpHeader to set
     */
    public void addHttpHeader(PartnerHttpHeader httpHeader) {
        synchronized (this.httpHeader) {
            this.httpHeader.add(httpHeader);
        }
    }

    /**
     * @return the httpProtocolVersion
     */
    public String getHttpProtocolVersion() {
        return httpProtocolVersion;
    }

    /**
     * @param httpProtocolVersion the httpProtocolVersion to set
     */
    public void setHttpProtocolVersion(String httpProtocolVersion) {
        if (httpProtocolVersion == null
                || (!httpProtocolVersion.equals(HttpConnectionParameter.HTTP_1_0)
                && !httpProtocolVersion.equals(HttpConnectionParameter.HTTP_1_1))) {
            throw new IllegalArgumentException("Partner.setHttpProtocolVersion(): Allowed values are \""
                    + HttpConnectionParameter.HTTP_1_0 + "\" and \"" + HttpConnectionParameter.HTTP_1_1 + "\".");
        }
        this.httpProtocolVersion = httpProtocolVersion;
    }

    /**
     * @return the maxPollFiles
     */
    public int getMaxPollFiles() {
        return maxPollFiles;
    }

    /**
     * @param maxPollFiles the maxPollFiles to set
     */
    public void setMaxPollFiles(int maxPollFiles) {
        this.maxPollFiles = maxPollFiles;
    }

    /**
     * Clone this object
     */
    @Override
    public Object clone() {
        try {
            Partner clonedPartner = (Partner) super.clone();            
            clonedPartner.partnerCertificateList = new PartnerCertificateInformationList();
            clonedPartner.setCertificateInformation(this.getCertificateInformation(PartnerCertificateInformation.CATEGORY_SIGN));
            clonedPartner.setCertificateInformation(this.getCertificateInformation(PartnerCertificateInformation.CATEGORY_CRYPT));
            clonedPartner.setCertificateInformation(this.getCertificateInformation(PartnerCertificateInformation.CATEGORY_SSL));
            return (clonedPartner);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return (null);
        }
    }

    /**
     * @return the contactAS2
     */
    public String getContactAS2() {
        return contactAS2;
    }

    /**
     * @param contactAS2 the contactAS2 to set
     */
    public void setContactAS2(String contactAS2) {
        this.contactAS2 = contactAS2;
    }

    /**
     * @return the contactCompany
     */
    public String getContactCompany() {
        return contactCompany;
    }

    /**
     * @param contactCompany the contactCompany to set
     */
    public void setContactCompany(String contactCompany) {
        this.contactCompany = contactCompany;
    }

    /**
     * @return the useAlgorithmIdentifierProtectionAttribute
     */
    public boolean getUseAlgorithmIdentifierProtectionAttribute() {
        return (this.useAlgorithmIdentifierProtectionAttribute);
    }

    /**
     * @param useAlgorithmIdentifierProtectionAttribute the
     * useAlgorithmIdentifierProtectionAttribute to set
     */
    public void setUseAlgorithmIdentifierProtectionAttribute(boolean useAlgorithmIdentifierProtectionAttribute) {
        this.useAlgorithmIdentifierProtectionAttribute = useAlgorithmIdentifierProtectionAttribute;
    }

    /**
     * @return the enableDirPoll
     */
    public boolean isEnableDirPoll() {
        return enableDirPoll;
    }

    /**
     * @param enableDirPoll the enableDirPoll to set
     */
    public void setEnableDirPoll(boolean enableDirPoll) {
        this.enableDirPoll = enableDirPoll;
    }

    /**
     * @return the partnerEventInfo
     */
    public PartnerEventInformation getPartnerEvents() {
        return partnerEvents;
    }

    /**
     * @return the useOAuth2Message
     */
    public boolean usesOAuth2Message() {
        return useOAuth2Message;
    }

    /**
     * @param useOAuth2Message the useOAuth2Message to set
     */
    public void setUseOAuth2Message(boolean useOAuth2Message) {
        this.useOAuth2Message = useOAuth2Message;
    }

    /**
     * @return the useOAuth2MDN
     */
    public boolean usesOAuth2MDN() {
        return useOAuth2MDN;
    }

    /**
     * @param useOAuth2MDN the useOAuth2MDN to set
     */
    public void setUseOAuth2MDN(boolean useOAuth2MDN) {
        this.useOAuth2MDN = useOAuth2MDN;
    }

    /**
     * @return the oauth2Message
     */
    public OAuth2Config getOAuth2Message() {
        return(this.oauth2Message);
    }

    /**
     * @param oauth2Message the oauth2Message to set
     */
    public void setOAuth2Message(OAuth2Config oauth2Message) {
        this.oauth2Message = oauth2Message;
    }

    /**
     * @return the oauth2MDN
     */
    public OAuth2Config getOAuth2MDN() {
        return( this.oauth2MDN);
    }

    /**
     * @param oauth2MDN the oauth2MDN to set
     */
    public void setOAuth2MDN(OAuth2Config oauth2MDN) {
        this.oauth2MDN = oauth2MDN;
    }

}
