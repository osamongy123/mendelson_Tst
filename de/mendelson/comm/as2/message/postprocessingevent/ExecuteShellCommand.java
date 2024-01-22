//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ExecuteShellCommand.java 15    24/08/22 14:57 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;

import de.mendelson.comm.as2.log.LogAccessDB;
import de.mendelson.comm.as2.log.LogEntry;
import de.mendelson.comm.as2.message.AS2LoggerOutputStream;
import de.mendelson.comm.as2.message.AS2MDNInfo;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MDNAccessDB;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.Exec;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.sql.Connection;
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
 * @version $Revision: 15 $
 */
public class ExecuteShellCommand implements IProcessingExecution {

    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private MessageAccessDB messageAccess;
    private MDNAccessDB mdnAccess;
    private PartnerAccessDB partnerAccess;
    private IDBDriverManager dbDriverManager;
    /**
     * Localize your GUI!
     */
    private MecResourceBundle rb = null;    

    public ExecuteShellCommand(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
        this.messageAccess = new MessageAccessDB(dbDriverManager);
        this.mdnAccess = new MDNAccessDB(dbDriverManager);
        this.partnerAccess = new PartnerAccessDB(dbDriverManager);
        //Load resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleExecuteShellCommand.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Executes a post processing shell command as defined in the partner events
     */
    @Override
    public void executeProcess(ProcessingEvent event) throws Exception {
        //get all required values for this event
        AS2MessageInfo messageInfo = this.messageAccess.getLastMessageEntry(event.getMessageId());
        if (messageInfo == null) {
            throw new Exception(this.rb.getResourceString("messageid.nolonger.exist", event.getMessageId()));
        }
        AS2MDNInfo mdnInfo = null;
        if (event.getMDNId() != null) {
            List<AS2MDNInfo> mdnInfoList = this.mdnAccess.getMDN(event.getMessageId());
            if (mdnInfoList != null && !mdnInfoList.isEmpty()) {
                mdnInfo = mdnInfoList.get(0);
            }
        }
        if (event.getEventType() == ProcessingEvent.TYPE_SEND_FAILURE
                || event.getEventType() == ProcessingEvent.TYPE_SEND_SUCCESS) {
            this.executeShellCommandOnSend(event, messageInfo, mdnInfo);
        } else {
            this.executeShellCommandOnReceipt(event, messageInfo);
        }
    }

    /**
     * Executes a shell command for an inbound AS2 message if this has been
     * defined in the partner settings
     */
    private void executeShellCommandOnSend(ProcessingEvent event, AS2MessageInfo messageInfo, AS2MDNInfo mdnInfo)
            throws Exception {
        //do not execute a command for CEM messages
        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
            return;
        }
        Partner messageSender = this.partnerAccess.getPartner(messageInfo.getSenderId());
        Partner messageReceiver = this.partnerAccess.getPartner(messageInfo.getReceiverId());
        List<AS2Payload> payload = this.messageAccess.getPayload(messageInfo.getMessageId());
        String rawCommand = event.getParameter().get(0);
        if (payload != null && payload.size() > 0) {
            this.logger.log(Level.INFO, this.rb.getResourceString("executing.send",
                    new Object[]{
                        messageSender.getName(),
                        messageReceiver.getName()
                    }), messageInfo);
            for (AS2Payload singlePayload : payload) {
                if (singlePayload.getPayloadFilename() == null) {
                    throw new PostprocessingException("executeShellCommandOnSend: payload filename does not exist.",
                            messageSender, messageReceiver);
                }
                String filename = singlePayload.getOriginalFilename();
                String command = this.replace(rawCommand, "${filename}", filename);
                command = this.replace(command, "${fullstoragefilename}", singlePayload.getPayloadFilename());
                command = this.replace(command, "${sender}", messageSender.getName());
                command = this.replace(command, "${receiver}", messageReceiver.getName());
                command = this.replace(command, "${messageid}", messageInfo.getMessageId());
                if (messageInfo.getSubject() != null) {
                    command = this.replace(command, "${subject}", messageInfo.getSubject());
                } else {
                    command = this.replace(command, "${subject}", "");
                }
                if (messageInfo.getUserdefinedId() != null) {
                    command = this.replace(command, "${userdefinedid}", messageInfo.getUserdefinedId());
                } else {
                    command = this.replace(command, "${userdefinedid}", "");
                }
                if (mdnInfo != null) {
                    command = this.replace(command, "${mdntext}", mdnInfo.getRemoteMDNText());
                } else {
                    command = this.replace(command, "${mdntext}", "");
                }
                //add log?
                if (command.contains("${log}")) {
                    try {
                        LogAccessDB logAccess = new LogAccessDB(this.dbDriverManager);
                        List<LogEntry> entries = logAccess.getLog(messageInfo.getMessageId());
                        StringBuilder logBuffer = new StringBuilder();
                        for (LogEntry logEntry : entries) {
                            logBuffer.append(logEntry.getMessage()).append("\\n");
                        }
                        //dont use single and double quotes, this is used in command line environment
                        String logText = this.replace(logBuffer.toString(), "\"", "");
                        logText = this.replace(logText, "'", "");
                        command = this.replace(command, "${log}", logText);
                    } catch (Exception e) {
                        throw new PostprocessingException(e.getMessage(), messageSender, messageReceiver);
                    }
                }
                this.logger.log(Level.INFO, this.rb.getResourceString("executing.command",
                        new Object[]{command}), messageInfo);
                int returnCode = 0;
                try {
                    Exec exec = new Exec();
                    returnCode = exec.start(command, new PrintStream(new AS2LoggerOutputStream(this.logger, messageInfo)),
                            new PrintStream(new AS2LoggerOutputStream(this.logger, messageInfo)));
                    this.logger.log(Level.INFO, this.rb.getResourceString("executed.command",
                            new Object[]{String.valueOf(returnCode)}), messageInfo);
                } catch (Exception e) {
                    throw new PostprocessingException(e.getMessage(), messageSender, messageReceiver);
                }
                if (returnCode != 0) {
                    throw new PostprocessingException(this.rb.getResourceString("executed.command",
                            new Object[]{String.valueOf(returnCode)}),
                            messageSender, messageReceiver);
                }
            }
        } else {
            throw new PostprocessingException("executeShellCommandOnSend: No payload found for message " + messageInfo.getMessageId(),
                    messageSender, messageReceiver);
        }
    }

    /**
     * Executes a shell command for an inbound AS2 message if this has been
     * defined in the partner settings
     */
    private void executeShellCommandOnReceipt(ProcessingEvent event, AS2MessageInfo messageInfo)
            throws Exception {
        //do not execute a command for CEM messages
        if (messageInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
            return;
        }
        Partner messageSender = this.partnerAccess.getPartner(messageInfo.getSenderId());
        Partner messageReceiver = this.partnerAccess.getPartner(messageInfo.getReceiverId());
        List<AS2Payload> payload = this.messageAccess.getPayload(messageInfo.getMessageId());
        String rawCommand = event.getParameter().get(0);
        if (payload != null) {
            this.logger.log(Level.INFO, this.rb.getResourceString("executing.receipt",
                    new Object[]{
                        messageSender.getName(),
                        messageReceiver.getName()
                    }), messageInfo);
            for (int i = 0; i < payload.size(); i++) {
                if (payload.get(i).getPayloadFilename() == null) {
                    continue;
                }
                String filename = payload.get(i).getPayloadFilename();
                String originalFilename = payload.get(i).getOriginalFilename();
                if (originalFilename == null) {
                    originalFilename = "NOT_TRANSMITTED";
                }
                rawCommand = this.replace(rawCommand, "${filename}",
                        Paths.get(filename).toAbsolutePath().toString());
                rawCommand = this.replace(rawCommand, "${sender}", messageSender.getName());
                rawCommand = this.replace(rawCommand, "${receiver}", messageReceiver.getName());
                rawCommand = this.replace(rawCommand, "${messageid}", messageInfo.getMessageId());
                if (messageInfo.getSubject() != null) {
                    rawCommand = this.replace(rawCommand, "${subject}", messageInfo.getSubject());
                } else {
                    rawCommand = this.replace(rawCommand, "${subject}", "");
                }
                rawCommand = this.replace(rawCommand, "${originalfilename}", originalFilename);
                this.logger.log(Level.INFO, this.rb.getResourceString("executing.command",
                        new Object[]{rawCommand}), messageInfo);
                Exec exec = new Exec();
                int returnCode = 0;
                try {
                    returnCode = exec.start(rawCommand, new PrintStream(new AS2LoggerOutputStream(this.logger, messageInfo)),
                            new PrintStream(new AS2LoggerOutputStream(this.logger, messageInfo)));
                    this.logger.log(Level.INFO, this.rb.getResourceString("executed.command",
                            new Object[]{String.valueOf(returnCode)}), messageInfo);
                } catch (Exception e) {
                    throw new PostprocessingException(e.getMessage(), messageSender, messageReceiver);
                }
                if (returnCode != 0) {
                    throw new PostprocessingException(this.rb.getResourceString("executed.command",
                            new Object[]{String.valueOf(returnCode)}),
                            messageSender, messageReceiver);
                }
            }
        } else {
            throw new PostprocessingException("executeShellCommandOnReceipt: No payload found for message " + messageInfo.getMessageId(),
                    messageSender, messageReceiver);
        }
    }

    /**
     * Replaces the string tag by the string replacement in the sourceString
     *
     * @param source Source string
     * @param tag	String that will be replaced
     * @param replacement String that will replace the tag
     * @return String that contains the replaced values
     */
    private String replace(String source, String tag, String replacement) {
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
