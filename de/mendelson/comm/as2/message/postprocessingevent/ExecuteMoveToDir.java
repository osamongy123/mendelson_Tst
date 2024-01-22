//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ExecuteMoveToDir.java 11    24/08/22 14:57 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;

import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
 * Allows to execute a shell command. This is used to execute a shell command on
 * message receipt
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class ExecuteMoveToDir implements IProcessingExecution {

    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private MessageAccessDB messageAccess;
    private PartnerAccessDB partnerAccess;
    /**
     * Localize your GUI!
     */
    private MecResourceBundle rb = null;

    public ExecuteMoveToDir(IDBDriverManager dbDriverManager) {
        this.messageAccess = new MessageAccessDB(dbDriverManager);
        this.partnerAccess = new PartnerAccessDB(dbDriverManager);
        //Load resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleExecuteMoveToDir.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Executes a post processing event: move file to directory
     */
    @Override
    public void executeProcess(ProcessingEvent event) throws Exception {
        //get all required values for this event
        AS2MessageInfo messageInfo = this.messageAccess.getLastMessageEntry(event.getMessageId());
        if (messageInfo == null) {
            throw new Exception(this.rb.getResourceString("messageid.nolonger.exist", event.getMessageId()));
        } else {
            if (event.getEventType() == ProcessingEvent.TYPE_SEND_FAILURE
                    || event.getEventType() == ProcessingEvent.TYPE_SEND_SUCCESS) {
                this.executeMoveToDirOnSend(event, messageInfo);
            } else {
                this.executeMoveToDirOnReceipt(event, messageInfo);
            }
        }
    }

    /**
     * Executes a move to dir command for an inbound AS2 message if this has
     * been defined in the partner settings
     */
    private void executeMoveToDirOnSend(ProcessingEvent event, AS2MessageInfo messageInfo) throws Exception {
        //do not execute anything for CEM messages
        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
            return;
        }
        Partner messageSender = this.partnerAccess.getPartner(messageInfo.getSenderId());
        Partner messageReceiver = this.partnerAccess.getPartner(messageInfo.getReceiverId());
        List<AS2Payload> payload = this.messageAccess.getPayload(messageInfo.getMessageId());
        String targetDirStr = event.getParameter().get(0);
        if (payload != null && payload.size() > 0) {
            this.logger.log(Level.INFO, this.rb.getResourceString("executing.send",
                    new Object[]{
                        messageSender.getName(),
                        messageReceiver.getName()
                    }), messageInfo);
            this.logger.log(Level.INFO, this.rb.getResourceString("executing.targetdir",
                    Paths.get(targetDirStr).toAbsolutePath().toString()), messageInfo);
            for (AS2Payload singlePayload : payload) {
                if (singlePayload.getPayloadFilename() == null) {
                    throw new PostprocessingException("executeMoveToDirOnSend: payload filename does not exist.",
                            messageSender, messageReceiver);
                }
                try {
                    String originalFilename = singlePayload.getOriginalFilename();
                    Path sourceFile = Paths.get(singlePayload.getPayloadFilename());
                    Path targetFile = Paths.get(targetDirStr, originalFilename);
                    this.logger.log(Level.INFO, this.rb.getResourceString("executing.movetodir",
                            new Object[]{
                                sourceFile.toAbsolutePath().toString(),
                                targetFile.toAbsolutePath().toString()
                            }), messageInfo);
                    Files.move(sourceFile, targetFile,
                            StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING);
                    this.logger.log(Level.INFO, this.rb.getResourceString("executing.movetodir.success"), messageInfo);
                } catch (Exception e) {
                    throw new PostprocessingException(e.getMessage(), messageSender, messageReceiver);
                }
            }
        } else {
            throw new PostprocessingException("executeMoveToDirOnSend: No payload found for message "
                    + messageInfo.getMessageId(),
                    messageSender, messageReceiver);
        }
    }

    /**
     * Executes a shell command for an inbound AS2 message if this has been
     * defined in the partner settings
     */
    private void executeMoveToDirOnReceipt(ProcessingEvent event, AS2MessageInfo messageInfo) throws Exception {
        //do not execute a command for CEM messages
        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
            return;
        }
        Partner messageSender = this.partnerAccess.getPartner(messageInfo.getSenderId());
        Partner messageReceiver = this.partnerAccess.getPartner(messageInfo.getReceiverId());
        List<AS2Payload> payload = this.messageAccess.getPayload(messageInfo.getMessageId());
        String targetDirStr = event.getParameter().get(0);
        if (payload != null && payload.size() > 0) {
            this.logger.log(Level.INFO, this.rb.getResourceString("executing.receipt",
                    new Object[]{
                        messageSender.getName(),
                        messageReceiver.getName()
                    }), messageInfo);
            this.logger.log(Level.INFO, this.rb.getResourceString("executing.targetdir",
                    Paths.get(targetDirStr).toAbsolutePath().toString()), messageInfo);
            for (AS2Payload singlePayload : payload) {
                if (singlePayload.getPayloadFilename() == null) {
                    throw new PostprocessingException("executeMoveToDirOnReceipt: payload filename does not exist.",
                            messageSender, messageReceiver);
                }
                String filename = singlePayload.getPayloadFilename();
                String originalFilename = singlePayload.getOriginalFilename();
                if (originalFilename == null) {
                    originalFilename = filename;
                }
                Path sourceFile = Paths.get(singlePayload.getPayloadFilename());
                Path targetFile = Paths.get(targetDirStr, originalFilename);
                this.logger.log(Level.INFO, this.rb.getResourceString("executing.movetodir",
                        new Object[]{
                            sourceFile.toAbsolutePath().toString(),
                            targetFile.toAbsolutePath().toString()
                        }), messageInfo);
                try {
                    Files.move(sourceFile, targetFile,
                            StandardCopyOption.ATOMIC_MOVE,
                            StandardCopyOption.REPLACE_EXISTING);
                    this.logger.log(Level.INFO, this.rb.getResourceString("executing.movetodir.success"), messageInfo);
                } catch (Exception e) {
                    throw new PostprocessingException(e.getMessage(), messageSender, messageReceiver);
                }
            }
        } else {
            throw new PostprocessingException("executeMoveToDirOnReceipt: No payload found for message " + messageInfo.getMessageId(),
                    messageSender, messageReceiver);
        }
    }
}
