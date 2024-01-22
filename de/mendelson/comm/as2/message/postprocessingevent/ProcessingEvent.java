//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ProcessingEvent.java 12    5/08/22 9:18 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;

import de.mendelson.comm.as2.message.AS2MDNInfo;
import de.mendelson.comm.as2.message.AS2Message;
import java.sql.Connection;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.partner.PartnerEventInformation;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Object that stores a post processing event that is defined in the partner
 * panel
 *
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class ProcessingEvent implements Serializable {

    

    public static final long serialVersionUID = 1L;

    public static final int TYPE_SEND_SUCCESS = 1;
    public static final int TYPE_SEND_FAILURE = 2;
    public static final int TYPE_RECEIPT_SUCCESS = 3;
    
    public static final int PROCESS_EXECUTE_SHELL = 1;
    public static final int PROCESS_MOVE_TO_PARTNER = 2;
    public static final int PROCESS_MOVE_TO_DIR = 3;
    
    
    public static MecResourceBundle rb;
    static{
        //Load resourcebundle
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleProcessingEvent.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    
    private static Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    /**
     * Related message/mdn id
     */
    private String messageId;
    private String mdnId;
    /**
     * Date of this message
     */
    private long initDate;
    /**
     * Command to execute
     */
    private List<String> parameter = new ArrayList<String>();
    private int eventType = -1;
    private int processType = -1;

    public ProcessingEvent(final int EVENT_TYPE, final int PROCESS_TYPE, String messageId, String mdnId, List<String> parameter, long initDate) {
        this.eventType = EVENT_TYPE;
        this.processType = PROCESS_TYPE;
        this.messageId = messageId;
        this.mdnId = mdnId;
        this.parameter.addAll( parameter );
        this.initDate = initDate;
    }

    public ProcessingEvent(final int EVENT_TYPE, final int PROCESS_TYPE, String messageId, String mdnId, List<String> parameter) {
        this(EVENT_TYPE, PROCESS_TYPE, messageId, mdnId, parameter, System.currentTimeMillis());
    }

    public static String getLocalizedProcessType( final int PROCESS_TYPE ){
        return( rb.getResourceString("processtype." + PROCESS_TYPE));
    }
    
    public static String getLocalizedEventType( final int EVENT_TYPE ){
        return( rb.getResourceString("eventtype." + EVENT_TYPE));
    }
    
    
    /**
     * Enqueue an event if it should be executed for the passed message/MDN id combination
     */
    public static void enqueueEventIfRequired(IDBDriverManager dbDriverManager,             
            AS2MessageInfo messageInfo, AS2MDNInfo mdnInfo) {
        PartnerAccessDB partnerAccess = new PartnerAccessDB(dbDriverManager);
        Partner messageSender = partnerAccess.getPartner(messageInfo.getSenderId());
        Partner messageReceiver = partnerAccess.getPartner(messageInfo.getReceiverId());
        PartnerEventInformation receiverEvents = messageReceiver.getPartnerEvents();
        PartnerEventInformation senderEvents = messageSender.getPartnerEvents();
        int eventType = -1;
        int processType = -1;
        List<String> parameter = new ArrayList<String>();
        if (messageSender.isLocalStation()) {
            if (messageInfo.getState() == AS2Message.STATE_STOPPED) {
                eventType = TYPE_SEND_FAILURE;
                processType = receiverEvents.getProcess(eventType);
                if (receiverEvents.useOnSenderror()
                        && receiverEvents.hasParameter(eventType)) {
                    parameter = receiverEvents.getParameter(eventType);
                }
            } else {
                eventType = TYPE_SEND_SUCCESS;
                processType = receiverEvents.getProcess(eventType);
                if (receiverEvents.useOnSendsuccess()
                        && receiverEvents.hasParameter(eventType)) {
                    parameter = receiverEvents.getParameter(eventType);
                }
            }
        } else {
            eventType = TYPE_RECEIPT_SUCCESS;
            processType = senderEvents.getProcess(eventType);
            if (senderEvents.useOnReceipt()
                    && senderEvents.hasParameter(eventType)) {
                parameter = senderEvents.getParameter(eventType);
            }
        }
        if (!parameter.isEmpty()) {
            String messageId = messageInfo.getMessageId();
            String mdnId = null;
            if (mdnInfo != null) {
                mdnId = mdnInfo.getMessageId();
            }
            ProcessingEvent event = new ProcessingEvent(eventType, processType, messageId, mdnId, parameter);
            ProcessingEventAccessDB processingEventDB = new ProcessingEventAccessDB(
                    dbDriverManager);
            processingEventDB.addEventToExecute(event);
            logger.log( Level.INFO, rb.getResourceString( "event.enqueued",
                    rb.getResourceString("processtype." + event.getProcessType())), messageInfo );
        }
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @return the initDate
     */
    public long getInitDate() {
        return initDate;
    }

    /**
     * @return the command
     */
    public List<String> getParameter() {
        return( this.parameter );
    }

    /**
     * @return the type
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * @return the mdnId
     */
    public String getMDNId() {
        return mdnId;
    }

    /**
     * @return the processType
     */
    public int getProcessType() {
        return processType;
    }
    
}
