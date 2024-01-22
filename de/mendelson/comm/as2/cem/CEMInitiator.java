//$Header: /as2/de/mendelson/comm/as2/cem/CEMInitiator.java 41    15/11/22 9:23 Heller $
package de.mendelson.comm.as2.cem;

import de.mendelson.comm.as2.cem.messages.EDIINTCertificateExchangeRequest;
import de.mendelson.comm.as2.cem.messages.EndEntity;
import de.mendelson.comm.as2.cem.messages.TradingPartnerInfo;
import de.mendelson.comm.as2.cem.messages.TrustRequest;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageCreation;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.message.UniqueId;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.partner.PartnerSystem;
import de.mendelson.comm.as2.partner.PartnerSystemAccessDB;
import de.mendelson.comm.as2.sendorder.SendOrder;
import de.mendelson.comm.as2.sendorder.SendOrderSender;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Initiates a CEM request
 *
 * @author S.Heller
 * @version $Revision: 41 $
 */
public class CEMInitiator {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    /**
     * Stores the certificates
     */
    private CertificateManager certificateManagerEncSign;
    private IDBDriverManager dbDriverManager;
    private PartnerAccessDB partnerAccess;
    private MecResourceBundle rb;

    /**
     * Creates new message I/O log and connects to localhost
     *
     * @param host host to connect to
     */
    public CEMInitiator(IDBDriverManager dbDriverManager, CertificateManager certificateManagerEncSign) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCEM.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.dbDriverManager = dbDriverManager;
        this.certificateManagerEncSign = certificateManagerEncSign;
        this.partnerAccess = new PartnerAccessDB(dbDriverManager);
    }

    /**
     * Returns a list of partners that have been informed by the requests
     */
    public List<Partner> sendRequests(Partner initiator, List<Partner> receiver,
            KeystoreCertificate certificate,
            boolean encryptionUsage, boolean signatureUsage, boolean sslUsage, Date respondByDate)
            throws Exception {
        //get all partner that are CEM enabled
        List<Partner> cemPartnerList = new ArrayList<Partner>();
        PartnerSystemAccessDB systemAccess = new PartnerSystemAccessDB(this.dbDriverManager);
        //check again if this partner supports CEM...
        for (Partner partner : receiver) {
            PartnerSystem partnerSystem = systemAccess.getPartnerSystem(partner);
            if (partnerSystem != null && partnerSystem.supportsCEM()) {
                cemPartnerList.add(partner);
            }
        }
        List<SendOrder> orderList = new ArrayList<SendOrder>();
        for (Partner singleReceiver : cemPartnerList) {
            SendOrder sendOrder = this.createRequest(initiator, singleReceiver, certificate, encryptionUsage, signatureUsage, sslUsage, respondByDate);
            orderList.add(sendOrder);
        }
        for (SendOrder order : orderList) {
            SendOrderSender orderSender
                    = new SendOrderSender(this.dbDriverManager);
            orderSender.send(order);
        }
        return (cemPartnerList);
    }

    /**
     * Sends the request to the partner
     */
    private SendOrder createRequest(Partner initiator, Partner receiver, KeystoreCertificate certificate,
            boolean encryptionUsage, boolean signatureUsage, boolean sslUsage, Date respondByDate)
            throws Exception {
        StringBuilder logPurpose = new StringBuilder();
        EDIINTCertificateExchangeRequest request = new EDIINTCertificateExchangeRequest();
        String requestId = UniqueId.createId();
        String requestContentId = UniqueId.createId();
        String certContentId = UniqueId.createId();
        request.setRequestId(requestId);
        TradingPartnerInfo partnerInfo = new TradingPartnerInfo();
        partnerInfo.setSenderAS2Id(initiator.getAS2Identification());
        request.setTradingPartnerInfo(partnerInfo);
        EndEntity endEntity = new EndEntity();
        endEntity.setContentId(certContentId);
        endEntity.setIssuerName(certificate.getIssuerDN());
        endEntity.setSerialNumber(certificate.getSerialNumberDEC());
        TrustRequest trustRequest = new TrustRequest();
        trustRequest.setResponseURL(initiator.getMdnURL());
        trustRequest.setRespondByDate(respondByDate);
        trustRequest.setCertUsageEncryption(encryptionUsage);
        if (encryptionUsage) {
            logPurpose.append("Encryption ");
        }
        trustRequest.setCertUsageSSL(sslUsage);
        if (sslUsage) {
            logPurpose.append("SSL/TLS ");
        }
        trustRequest.setCertUsageSignature(signatureUsage);
        if (signatureUsage) {
            logPurpose.append("Signature ");
        }
        trustRequest.setEndEntity(endEntity);
        request.addTrustRequest(trustRequest);
        //export the certificate to a file and create a payload
        Path certFile = this.exportCertificate(certificate, certContentId);
        AS2Payload[] payloads = new AS2Payload[2];
        Path descriptionFile = this.storeRequest(request);
        //build up the XML description as payload
        AS2Payload payloadXML = new AS2Payload();
        payloadXML.setPayloadFilename(descriptionFile.toAbsolutePath().toString());
        payloadXML.loadDataFromPayloadFile();
        payloadXML.setContentId(requestContentId);
        payloadXML.setContentType("application/ediint-cert-exchange+xml");
        payloads[0] = payloadXML;
        //build up the certificate as payload
        AS2Payload payloadCert = new AS2Payload();
        payloadCert.setPayloadFilename(certFile.toAbsolutePath().toString());
        payloadCert.loadDataFromPayloadFile();
        payloadCert.setContentId(certContentId);
        payloadCert.setContentType("application/pkcs7-mime; smime-type=certs-only");
        payloads[1] = payloadCert;
        //send the message
        AS2MessageCreation creation = new AS2MessageCreation(this.certificateManagerEncSign, this.certificateManagerEncSign);
        AS2Message message = creation.createMessage(initiator, receiver, payloads, AS2Message.MESSAGETYPE_CEM);
        SendOrder order = new SendOrder();
        order.setReceiver(receiver);
        order.setMessage(message);
        order.setSender(initiator);
        AS2MessageInfo messageInfo = (AS2MessageInfo) order.getMessage().getAS2Info();
        //enter the request to the CEM table in the db
        CEMAccessDB cemAccess = new CEMAccessDB(this.dbDriverManager);
        cemAccess.insertRequest((AS2MessageInfo) order.getMessage().getAS2Info(), initiator, order.getReceiver(), request);
        MessageAccessDB messageAccess
                = new MessageAccessDB(this.dbDriverManager);
        messageAccess.initializeOrUpdateMessage(messageInfo);
        this.logger.log(Level.INFO, this.rb.getResourceString("cem.created.request",
                new Object[]{
                    messageInfo.getMessageId(),
                    initiator.getName(),
                    receiver.getName(),
                    certificate.getIssuerDN(),
                    certificate.getSerialNumberDEC(),
                    logPurpose,}), messageInfo);
        return (order);
    }

    private Path exportCertificate(KeystoreCertificate certificate, String certContentId)
            throws Exception {
        KeyStoreUtil util = new KeyStoreUtil();
        String tempDir = System.getProperty("java.io.tmpdir");
        Path[] exportFile = util.exportX509CertificatePKCS7(this.certificateManagerEncSign.getKeystore(),
                certificate.getAlias(), tempDir + certContentId + ".p7c");
        return (exportFile[0]);
    }

    private Path storeRequest(EDIINTCertificateExchangeRequest request) throws Exception {
        Path descriptionFile = AS2Tools.createTempFile("request", ".xml");
        Writer writer = null;
        try {
            writer = Files.newBufferedWriter(descriptionFile, StandardCharsets.UTF_8);
            writer.write(request.toXML());
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        return (descriptionFile);
    }
}
