//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ExecuteMoveToPartner.java 10    24/08/22 14:57 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;

import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MDNAccessDB;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.sendorder.SendOrderSender;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.cert.CertificateManager;
import java.nio.file.Path;
import java.nio.file.Paths;
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
 * Allows to execute a postprocessing event. This is used to move the data to a
 * defined remote partner
 *
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class ExecuteMoveToPartner implements IProcessingExecution {

    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private MessageAccessDB messageAccess;
    private MDNAccessDB mdnAccess;
    private PartnerAccessDB partnerAccess;
    private CertificateManager certificateManagerEncSign;

    /**
     * Localize your GUI!
     */
    private MecResourceBundle rb = null;
    private IDBDriverManager dbDriverManager;

    public ExecuteMoveToPartner(IDBDriverManager dbDriverManager,
            CertificateManager certificateManagerEncSign) {
        this.dbDriverManager = dbDriverManager;
        this.certificateManagerEncSign = certificateManagerEncSign;
        this.messageAccess = new MessageAccessDB(dbDriverManager);
        this.mdnAccess = new MDNAccessDB(dbDriverManager);
        this.partnerAccess = new PartnerAccessDB(dbDriverManager);
        //Load resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleExecuteMoveToPartner.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Executes a post processing event: move file to directory
     */
    @Override
    public void executeProcess(ProcessingEvent event)
            throws Exception {
        //get all required values for this event
        AS2MessageInfo messageInfo = this.messageAccess.getLastMessageEntry(event.getMessageId());
        if (messageInfo == null) {
            throw new Exception(this.rb.getResourceString("messageid.nolonger.exist", event.getMessageId()));
        } else {
            if (event.getEventType() == ProcessingEvent.TYPE_SEND_FAILURE
                    || event.getEventType() == ProcessingEvent.TYPE_SEND_SUCCESS) {
                this.executeMoveToPartnerOnSend(event, messageInfo);
            } else {
                this.executeMoveToPartnerOnReceipt(event, messageInfo);
            }
        }
    }

    /**
     * Executes a move to dir command for an inbound AS2 message if this has
     * been defined in the partner settings
     */
    private void executeMoveToPartnerOnSend(ProcessingEvent event, AS2MessageInfo messageInfo) throws Exception {
        //do not execute anything for CEM messages
        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
            return;
        }
        Partner messageSender = this.partnerAccess.getPartner(messageInfo.getSenderId());
        Partner messageReceiver = this.partnerAccess.getPartner(messageInfo.getReceiverId());
        List<AS2Payload> payload = this.messageAccess.getPayload(messageInfo.getMessageId());
        String targetPartnerAS2Id = event.getParameter().get(0);
        Partner targetPartner = this.partnerAccess.getPartner(targetPartnerAS2Id);
        if (targetPartner == null) {
            throw new PostprocessingException(this.rb.getResourceString("targetpartner.does.not.exist", targetPartnerAS2Id),
                    messageSender, messageReceiver);
        }
        if (payload != null && payload.size() > 0) {
            this.logger.log(Level.INFO, this.rb.getResourceString("executing.send",
                    new Object[]{
                        messageSender.getName(),
                        messageReceiver.getName()
                    }), messageInfo);
            this.logger.log(Level.INFO, this.rb.getResourceString("executing.targetpartner",
                    targetPartner.toString()), messageInfo);
            for (AS2Payload singlePayload : payload) {
                if (singlePayload.getPayloadFilename() == null) {
                    this.logger.log(Level.WARNING, "executeMoveToPartnerOnSend: payload filename does not exist.", messageInfo);
                    continue;
                }
                String originalFilename = singlePayload.getOriginalFilename();
                Path sourceFile = Paths.get(singlePayload.getPayloadFilename());
                this.logger.log(Level.INFO, this.rb.getResourceString("executing.movetopartner",
                        new Object[]{
                            sourceFile.toAbsolutePath().toString(),
                            targetPartner.toString()
                        }), messageInfo);

                Path[] sendFiles = new Path[]{sourceFile};
                String[] originalFilenames = new String[]{originalFilename};
                String[] payloadContentTypes = new String[]{targetPartner.getContentType()};
                try {
                    SendOrderSender orderSender = new SendOrderSender(this.dbDriverManager);
                    orderSender.send(this.certificateManagerEncSign, messageSender,
                            targetPartner, sendFiles, originalFilenames, null,
                            targetPartner.getSubject(), payloadContentTypes);
                    this.logger.log(Level.INFO, this.rb.getResourceString("executing.movetopartner.success",
                            targetPartner.toString()), messageInfo);
                } catch (Exception e) {
                    throw new PostprocessingException(e.getMessage(), messageSender, messageReceiver);
                }
            }
        } else {
            throw new PostprocessingException("executeMoveToPartnerOnSend: No payload found for message " + messageInfo.getMessageId(),
                    messageSender, messageReceiver);
        }
    }

    /**
     * Executes a shell command for an inbound AS2 message if this has been
     * defined in the partner settings
     */
    private void executeMoveToPartnerOnReceipt(ProcessingEvent event, AS2MessageInfo messageInfo) throws Exception {
        //do not execute a command for CEM messages
        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
            return;
        }
        Partner messageSender = this.partnerAccess.getPartner(messageInfo.getSenderId());
        Partner messageReceiver = this.partnerAccess.getPartner(messageInfo.getReceiverId());
        List<AS2Payload> payload = this.messageAccess.getPayload(messageInfo.getMessageId());
        String targetPartnerAS2Id = event.getParameter().get(0);
        Partner targetPartner = this.partnerAccess.getPartner(targetPartnerAS2Id);
        if (targetPartner == null) {
            throw new PostprocessingException(
                    this.rb.getResourceString("targetpartner.does.not.exist", targetPartnerAS2Id),
                    messageSender, messageReceiver);
        }
        if (payload != null && payload.size() > 0) {
            this.logger.log(Level.INFO, this.rb.getResourceString("executing.receipt",
                    new Object[]{
                        messageSender.getName(),
                        messageReceiver.getName()
                    }), messageInfo);
            for (AS2Payload singlePayload : payload) {
                if (singlePayload.getPayloadFilename() == null) {
                    throw new PostprocessingException("executeMoveToPartnerOnReceipt: payload filename does not exist.",
                            messageSender, messageReceiver);
                }
                String originalFilename = singlePayload.getOriginalFilename();
                Path sourceFile = Paths.get(singlePayload.getPayloadFilename());
                this.logger.log(Level.INFO, this.rb.getResourceString("executing.movetopartner",
                        new Object[]{
                            sourceFile.toAbsolutePath().toString(),
                            targetPartner.toString()
                        }), messageInfo);
                try {
                    Path[] sendFiles = new Path[]{sourceFile};
                    String[] originalFilenames = new String[]{originalFilename};
                    String[] payloadContentTypes = new String[]{targetPartner.getContentType()};
                    SendOrderSender orderSender = new SendOrderSender(this.dbDriverManager);
                    orderSender.send(this.certificateManagerEncSign, messageReceiver,
                            targetPartner, sendFiles, originalFilenames, null,
                            targetPartner.getSubject(), payloadContentTypes);
                    this.logger.log(Level.INFO, this.rb.getResourceString("executing.movetopartner.success",
                            targetPartner.toString()), messageInfo);
                } catch (Exception e) {
                    throw new PostprocessingException(e.getMessage(), messageSender, messageReceiver);
                }
            }
        } else {
            throw new PostprocessingException("executeMoveToPartnerOnReceipt: No payload found for message "
                    + messageInfo.getMessageId(), messageSender, messageReceiver);
        }
    }
}
