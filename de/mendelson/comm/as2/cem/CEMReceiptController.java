//$Header: /as2/de/mendelson/comm/as2/cem/CEMReceiptController.java 75    26/09/22 10:36 Heller $
package de.mendelson.comm.as2.cem;

import de.mendelson.comm.as2.AS2Exception;
import de.mendelson.comm.as2.cem.messages.CertificateReference;
import de.mendelson.comm.as2.cem.messages.EDIINTCertificateExchangeRequest;
import de.mendelson.comm.as2.cem.messages.EDIINTCertificateExchangeResponse;
import de.mendelson.comm.as2.cem.messages.TradingPartnerInfo;
import de.mendelson.comm.as2.cem.messages.TrustRequest;
import de.mendelson.comm.as2.cem.messages.TrustResponse;
import de.mendelson.comm.as2.clientserver.message.RefreshClientCEMDisplay;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageCreation;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.sendorder.SendOrder;
import de.mendelson.comm.as2.sendorder.SendOrderSender;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.XPathHelper;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.KeystoreStorageImplFile;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Task that checks for inbound CEM requests and performs the required steps
 * like
 *
 * @author S.Heller
 * @version $Revision: 75 $
 */
public class CEMReceiptController {

    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private IDBDriverManager dbDriverManager;
    private CertificateManager certificateManagerEncSign;
    private MecResourceBundle rb;
    private PreferencesAS2 preferences = new PreferencesAS2();
    private ClientServer clientServer;

    public static final String KEYSTORE_TYPE_SSL = "SSL";
    public static final String KEYSTORE_TYPE_ENC_SIGN = "ENC_SIGN";

    public CEMReceiptController(ClientServer clientServer,
            IDBDriverManager dbDriverManager,
            CertificateManager certificateManagerEncSign) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCEM.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.dbDriverManager = dbDriverManager;
        this.certificateManagerEncSign = certificateManagerEncSign;
        this.clientServer = clientServer;
    }

    /**
     * Checks a XML file against a W3C Schema and throws an exception if
     * anything happens
     */
    private void checkAgainstSchema(AS2Message message, Path schemaFile, byte[] xmlData) throws Exception {
        //create a new W3C Schema instance
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(schemaFile.toFile());
        Validator validator = schema.newValidator();
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        ByteArrayInputStream memIn = new ByteArrayInputStream(xmlData);
        DOMSource source = new DOMSource(builder.parse(memIn));
        validator.validate(source);
        AS2MessageInfo info = (AS2MessageInfo) message.getAS2Info();
        this.logger.log(Level.INFO, this.rb.getResourceString("cem.validated.schema"), info);
        memIn.close();
    }

    /**
     * Checks an inbound CEM and throws an error if anything goes wrong. This
     * has to be done before the MDN is sent. It will parse the xml description
     * and see if all attachments are referenced etc
     */
    public void checkInboundCEM(AS2Message message) throws AS2Exception {
        AS2MessageInfo info = (AS2MessageInfo) message.getAS2Info();
        try {
            //check if a description file is part of the request. For compatibility reasons the server
            //is checking the payload by its content type only if the number of payloads are > 1. If there is
            //only one payload it is assumed that the ONE payload is the description.
            AS2Payload description = this.getPayloadByContentType(message.getPayloads(), "ediint-cert-exchange+xml");
            if (description == null) {
                if (message.getPayloadCount() == 1) {
                    description = message.getPayload(0);
                } else {
                    //do not localize, this will appear in the MDN
                    throw new Exception("CEM is in wrong structure: missing ediint-cert-exchange xml.");
                }
            }
            Path cemSchema = Paths.get("cem.xsd");
            this.checkAgainstSchema(message, cemSchema, description.getData());
            //parse the XML data to check if the content is in right structure and all attachments are present
            ByteArrayInputStream inStream = new ByteArrayInputStream(description.getData());
            XPathHelper helper = new XPathHelper(inStream);
            helper.addNamespace("x", "urn:ietf:params:xml:ns:ediintcertificateexchange");
            helper.addNamespace("ds", "http://www.w3.org/2000/09/xmldsig#");
            if (helper.getNodeCount("/x:EDIINTCertificateExchangeRequest") == 1) {
                this.logger.log(Level.INFO, this.rb.getResourceString("cemtype.request"), info);
                EDIINTCertificateExchangeRequest request = EDIINTCertificateExchangeRequest.parse(description.getData());
                if (!request.getTradingPartnerInfo().getSenderAS2Id().equals(info.getSenderId())) {
                    //do not localize, will be returned in an MDN
                    throw new Exception("CEM request sender AS2 id (" + request.getTradingPartnerInfo().getSenderAS2Id() + ") is not the same as the message sender AS2 id (" + info.getSenderId() + ")");
                }
                //check if all referenced content ids are available as attachments
                List<TrustRequest> requestList = request.getTrustRequestList();
                for (TrustRequest trustRequest : requestList) {
                    String contentId = trustRequest.getEndEntity().getContentId();
                    AS2Payload referencedPayload = this.getPayloadByContentId(message.getPayloads(), contentId);
                    if (referencedPayload == null) {
                        throw new Exception("The CEM request references an attached certificate with the content id " + contentId + " which is not part of the message.");
                    }
                }
            } else if (helper.getNodeCount("/x:EDIINTCertificateExchangeResponse") == 1) {
                this.logger.log(Level.INFO, this.rb.getResourceString("cemtype.response"), info);
                EDIINTCertificateExchangeResponse response = EDIINTCertificateExchangeResponse.parse(description.getData());
                CEMAccessDB cemAccess = new CEMAccessDB(
                        this.dbDriverManager);
                if (!cemAccess.requestExists(response.getRequestId())) {
                    //do not loalize, will be returned in an MDN
                    throw new Exception("Related CEM request with requestId " + response.getRequestId() + " does not exist.");
                } else {
                    this.logger.log(Level.INFO, this.rb.getResourceString("cem.response.relatedrequest.found",
                            new Object[]{response.getRequestId()}), info);
                }
                if (!response.getTradingPartnerInfo().getSenderAS2Id().equals(info.getSenderId())) {
                    //do not loalize, will be returned in an MDN
                    throw new Exception("CEM request sender AS2 id (" + response.getTradingPartnerInfo().getSenderAS2Id() + ") is not the same as the message sender AS2 id (" + info.getSenderId() + ")");
                }
            } else {
                //no idea what this request is about
                throw new AS2Exception(AS2Exception.PROCESSING_ERROR, "The inbound CEM message is neither a certificate request or a certificate response - unable to process it", message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AS2Exception(AS2Exception.PROCESSING_ERROR, e.getMessage(), message);
        }
    }

    /**
     * Gets an inbound CEM and processes it
     */
    public void processInboundCEM(AS2MessageInfo messageInfo) throws Throwable {
        List<AS2Payload> payloads = this.getPayloads(messageInfo);
        AS2Payload description = this.getPayloadByContentType(payloads, "ediint-cert-exchange+xml");
        //check if it is a request or a response
        ByteArrayInputStream inStream = new ByteArrayInputStream(description.getData());
        XPathHelper helper = new XPathHelper(inStream);
        helper.addNamespace("x", "urn:ietf:params:xml:ns:ediintcertificateexchange");
        helper.addNamespace("ds", "http://www.w3.org/2000/09/xmldsig#");
        if (helper.getNodeCount("/x:EDIINTCertificateExchangeRequest") == 1) {
            this.processInboundCEMRequest(messageInfo, payloads, description);
        } else if (helper.getNodeCount("/x:EDIINTCertificateExchangeResponse") == 1) {
            this.processInboundCEMResponse(messageInfo, description);
        }
    }

    /**
     * Processes an inbound CEM request and answers a CEM response
     */
    private void processInboundCEMRequest(AS2MessageInfo info, List<AS2Payload> payloads, AS2Payload description) throws Throwable {
        PartnerAccessDB partnerAccess
                = new PartnerAccessDB(this.dbDriverManager);
        Partner initiator = partnerAccess.getPartner(info.getSenderId());
        Partner receiver = partnerAccess.getPartner(info.getReceiverId());
        EDIINTCertificateExchangeRequest request = EDIINTCertificateExchangeRequest.parse(description.getData());
        //auto import the attached certificates into the right keystore: SSL to the SSL keystore,
        //encryption and signature to the enc/singnature keystore
        List<TrustResponse> trustResponses = this.importCertificates(initiator, info, request, payloads);
        EDIINTCertificateExchangeResponse response = new EDIINTCertificateExchangeResponse();
        response.setRequestId(request.getRequestId());
        TradingPartnerInfo partnerInfo = new TradingPartnerInfo();
        partnerInfo.setSenderAS2Id(info.getReceiverId());
        response.setTradingPartnerInfo(partnerInfo);
        for (TrustResponse trustResponse : trustResponses) {
            response.addTrustResponse(trustResponse);
        }
        //enter the request to the CEM table in the db
        CEMAccessDB cemAccess = new CEMAccessDB(this.dbDriverManager);
        cemAccess.insertRequest(info, initiator, receiver, request);
        if (this.clientServer != null) {
            this.clientServer.broadcastToClients(new RefreshClientCEMDisplay());
        }
        //do not insert any new certificate and reject the request if the CEM is disabled in the
        //preferences
        boolean cemEnabled = this.preferences.getBoolean(PreferencesAS2.CEM);
        if (!cemEnabled) {
            AS2Message errorMessage = new AS2Message(info);
            //the user has disabled the CEM support
            throw new AS2Exception(AS2Exception.PROCESSING_ERROR, "The CEM support of this AS2 system is disabled in the system configuration by the user",
                    errorMessage);
        }
        //now send the response and insert the response data into the database
        this.sendResponse(info, info.getReceiverId(), info.getSenderId(), response);
        //send a CEM notification if this is requested in the config
        SystemEventManagerImplAS2 manager = new SystemEventManagerImplAS2();
        try {
            manager.newEventCEMRequestReceived(initiator, request.getRequestId());
        } catch (Exception e) {
            logger.severe("CEMReceiptController: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e);
        }
    }

    /**
     * Processes the inbound CEM response
     */
    private void processInboundCEMResponse(AS2MessageInfo info, AS2Payload description) throws Exception {
        EDIINTCertificateExchangeResponse response = EDIINTCertificateExchangeResponse.parse(description.getData());
        //insert the response into the database
        CEMAccessDB cemAccess = new CEMAccessDB(
                this.dbDriverManager);
        //insert the request data into the certificate database
        PartnerAccessDB partnerAccess
                = new PartnerAccessDB(this.dbDriverManager);
        Partner receiver = partnerAccess.getPartner(info.getSenderId());
        Partner initiator = partnerAccess.getPartner(info.getReceiverId());
        cemAccess.insertResponse(info, initiator, receiver, response);
        if (this.clientServer != null) {
            this.clientServer.broadcastToClients(new RefreshClientCEMDisplay());
        }
    }

    /**
     * Returns the payloads that are assigned to the passed message info
     */
    private List<AS2Payload> getPayloads(AS2MessageInfo info) throws Exception {
        MessageAccessDB messageAccess
                = new MessageAccessDB(this.dbDriverManager);
        List<AS2Payload> payloads = messageAccess.getPayload(info.getMessageId());
        for (AS2Payload payload : payloads) {
            payload.loadDataFromPayloadFile();
        }
        return (payloads);
    }

    /**
     * Checks the content types of the passed payload and returns the first
     * found payload
     */
    private AS2Payload getPayloadByContentType(List<AS2Payload> payloads, String contentType) {
        for (AS2Payload payload : payloads) {
            if (payload.getContentType() != null && contentType != null
                    && payload.getContentType().toLowerCase().contains(contentType.toLowerCase())) {
                return (payload);
            }
        }
        return (null);
    }

    /**
     * Checks the content ids of the passed payload and returns the first found
     * payload
     */
    private AS2Payload getPayloadByContentId(List<AS2Payload> payloads, String contentId) {
        for (AS2Payload payload : payloads) {
            if (payload.getContentId() != null && contentId != null
                    && payload.getContentId().toLowerCase().contains(contentId.toLowerCase())) {
                return (payload);
            }
        }
        return (null);
    }

    /**
     * Sends the respose of a CEM and inserts the response into the database
     */
    private void sendResponse(AS2MessageInfo requestInfo, String senderId, String receiverId, EDIINTCertificateExchangeResponse response) throws Exception {
        PartnerAccessDB partnerAccess
                = new PartnerAccessDB(this.dbDriverManager);
        Partner sender = partnerAccess.getPartner(senderId);
        Partner receiver = partnerAccess.getPartner(receiverId);
        AS2MessageCreation creation = new AS2MessageCreation(this.certificateManagerEncSign, this.certificateManagerEncSign);
        //store the payload
        Path payloadFile = AS2Tools.createTempFile("AS2Response", ".xml");
        Writer writer = null;
        try {
            writer = Files.newBufferedWriter(payloadFile, StandardCharsets.UTF_8);
            writer.write(response.toXML());
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        AS2Payload descriptionXML = new AS2Payload();
        descriptionXML.setContentType("application/ediint-cert-exchange+xml");
        descriptionXML.setPayloadFilename(payloadFile.toAbsolutePath().toString());
        descriptionXML.loadDataFromPayloadFile();
        AS2Message message = creation.createMessage(sender, receiver, new AS2Payload[]{descriptionXML}, AS2Message.MESSAGETYPE_CEM);
        this.logger.log(Level.INFO, this.rb.getResourceString("cem.response.prepared",
                new Object[]{
                    response.getRequestId()
                }), requestInfo);
        SendOrder order = new SendOrder();
        order.setReceiver(receiver);
        order.setMessage(message);
        order.setSender(sender);
        SendOrderSender orderSender = new SendOrderSender(this.dbDriverManager);
        orderSender.send(order);
        CEMAccessDB cemAccess = new CEMAccessDB(this.dbDriverManager);
        cemAccess.insertResponse((AS2MessageInfo) message.getAS2Info(), receiver, sender, response);
        if (this.clientServer != null) {
            this.clientServer.broadcastToClients(new RefreshClientCEMDisplay());
        }
    }

    /**
     * Imports a single certificate in the keystore that is wrapped by the
     * passed certificate manager Returns if the import has been skipped (it
     * already exists) or if the import has been performed
     */
    private boolean importSingleCertificate(AS2MessageInfo info, CertificateManager certificateManager, Certificate certificateToImport,
            final String keystoreType) throws Throwable {
        KeyStoreUtil util = new KeyStoreUtil();
        X509Certificate certificate = util.convertToX509Certificate(certificateToImport);
        boolean imported = false;
        this.logger.log(Level.INFO, this.rb.getResourceString("transmitted.certificate.info",
                new Object[]{
                    certificate.getIssuerDN().toString(),
                    certificate.getSerialNumber().toString(),}), info);
        //check if the cert already exists
        String importAlias = util.getCertificateAlias(certificateManager.getKeystore(), certificate);
        if (importAlias != null) {
            this.logger.log(Level.WARNING, this.rb.getResourceString(keystoreType + ".cert.already.imported",
                    new Object[]{
                        importAlias
                    }), info);
        } else {
            //import the new alias
            importAlias = util.importX509Certificate(certificateManager.getKeystore(), certificate, BouncyCastleProvider.PROVIDER_NAME);
            certificateManager.saveKeystore();
            this.logger.log(Level.FINE, this.rb.getResourceString(keystoreType + ".cert.imported.success",
                    new Object[]{
                        importAlias
                    }), info);
            imported = true;
        }
        certificateManager.rereadKeystoreCertificates();
        return (imported);
    }

    /**
     * Auto imports the CEM request certificates into the encryption/sign
     * keystore if they dont exist so far
     */
    private List<TrustResponse> importCertificates(Partner initiator, AS2MessageInfo info, 
            EDIINTCertificateExchangeRequest request, List<AS2Payload> payloads) throws Throwable {
        KeyStoreUtil util = new KeyStoreUtil();
        List<TrustResponse> trustResponseList = new ArrayList<TrustResponse>();
        List<TrustRequest> trustRequestList = request.getTrustRequestList();
        //log some information about the inbound trust request
        this.logger.log(Level.INFO, this.rb.getResourceString("cem.structure.info",
                new Object[]{
                    String.valueOf(trustRequestList.size())
                }), info);
        //do reject the request if the CEM is disabled in the
        //preferences
        boolean cemEnabled = this.preferences.getBoolean(PreferencesAS2.CEM);
        int trustRequestCounter = 0;
        for (TrustRequest trustRequest : trustRequestList) {
            trustRequestCounter++;
            this.logger.log(Level.INFO,
                    this.rb.getResourceString("trustrequest.working.on",
                            new Object[]{
                                String.valueOf(trustRequestCounter)
                            }), info);
            //read certificates from the payloads
            AS2Payload certPayload = this.getPayloadByContentId(payloads, trustRequest.getEndEntity().getContentId());
            InputStream inStream = null;
            Collection<? extends Certificate> certList = null;
            try {
                inStream = Files.newInputStream(Paths.get(certPayload.getPayloadFilename()));
                certList = util.readCertificates(inStream, BouncyCastleProvider.PROVIDER_NAME);
            } finally {
                if (inStream != null) {
                    inStream.close();
                }
            }
            this.logger.log(Level.INFO,
                    this.rb.getResourceString("trustrequest.certificates.found",
                            new Object[]{
                                String.valueOf(certList.size())
                            }), info);
            TrustResponse trustResponse = new TrustResponse();
            if (cemEnabled) {
                trustResponse.setState(TrustResponse.STATUS_ACCEPTED_STR);
                try {
                    for (Certificate cert : certList) {
                        //import the cert into the encryption/signature keystore
                        if (trustRequest.isCertUsageEncryption() || trustRequest.isCertUsageSignature()) {
                            boolean imported = this.importSingleCertificate(info, this.certificateManagerEncSign, cert, KEYSTORE_TYPE_ENC_SIGN);
                            //notify the user that a sign/encrypt certificate has been imported from the desired partner
                            if (imported) {
                                String importAlias = util.getCertificateAlias(certificateManagerEncSign.getKeystore(), util.convertToX509Certificate(cert));
                                SystemEventManagerImplAS2 manager = new SystemEventManagerImplAS2();
                                manager.newEventEncSignCertificateAddedByCEM(initiator, certificateManagerEncSign.getKeystoreCertificate(importAlias));
                            }
                        }
                        if (trustRequest.isCertUsageSSL()) {
                            //import the certificate into the SSL keystore
                            CertificateManager certificateManagerSSL = new CertificateManager(this.logger);
                            String keystoreFile
                                    = Paths.get(this.preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND)).toAbsolutePath().toString();
                            KeystoreStorageImplFile storage = new KeystoreStorageImplFile(keystoreFile,
                                    this.preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND_PASS).toCharArray(),
                                    KeystoreStorageImplFile.KEYSTORE_USAGE_SSL,
                                    KeystoreStorageImplFile.KEYSTORE_STORAGE_TYPE_JKS
                            );
                            certificateManagerSSL.loadKeystoreCertificates(storage);
                            boolean imported = this.importSingleCertificate(info, certificateManagerSSL, cert, KEYSTORE_TYPE_SSL);
                            //notify the user that a SSL/TLS certificate has been imported from the desired partner
                            if (imported) {
                                String importAlias = util.getCertificateAlias(certificateManagerSSL.getKeystore(), util.convertToX509Certificate(cert));
                                SystemEventManagerImplAS2 manager = new SystemEventManagerImplAS2();
                                manager.newEventSSLCertificateAddedByCEM(initiator, certificateManagerSSL.getKeystoreCertificate(importAlias));
                            }
                        }
                    }
                    this.logger.log(Level.WARNING,
                            this.rb.getResourceString("trustrequest.accepted"), info);
                } catch (Exception e) {
                    //if the import fails the trust response state should be set to REJECTED with an error message
                    trustResponse.setState(TrustResponse.STATUS_REJECTED_STR);
                    this.logger.log(Level.WARNING,
                            this.rb.getResourceString("trustrequest.rejected"), info);
                    String rejectionReason = "Failure in certificate import process: " + e.getMessage();
                    this.logger.warning(rejectionReason);
                    trustResponse.setReasonForRejection(rejectionReason);
                }
            } else {
                //cem is disabled
                trustResponse.setState(TrustResponse.STATUS_REJECTED_STR);
                this.logger.log(Level.WARNING,
                        this.rb.getResourceString("trustrequest.rejected"), info);
                //do not localize the reason, its part of the response
                trustResponse.setReasonForRejection("CEM has been disabled in the system settings.");
            }
            //ensure to really return the same issuer name as requested if the other side performs a simple string compare on it
            CertificateReference certificateReference = new CertificateReference();
            certificateReference.setCertficiate(trustRequest.getEndEntity().getIssuerName(), trustRequest.getEndEntity().getSerialNumber());
            trustResponse.setCertificateReference(certificateReference);
            trustResponseList.add(trustResponse);
        }
        return (trustResponseList);
    }
}
