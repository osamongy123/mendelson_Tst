//$Header: /as2/de/mendelson/comm/as2/timing/MDNReceiptController.java 37    5/08/22 9:18 Heller $
package de.mendelson.comm.as2.timing;

import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.message.postprocessingevent.ProcessingEvent;
import de.mendelson.comm.as2.message.store.MessageStoreHandler;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
 * Controls the timed deletion of as2 entries from the log
 *
 * @author S.Heller
 * @version $Revision: 37 $
 */
public class MDNReceiptController {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private PreferencesAS2 preferences = new PreferencesAS2();
    private MDNCheckThread checkThread;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
        new NamedThreadFactory("mdn-receipt-control"));
    /**
     * server for client-server communication
     */
    private ClientServer clientserver = null;
    private MecResourceBundle rb = null;
    private IDBDriverManager dbDriverManager;

    public MDNReceiptController(ClientServer clientserver, IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
        this.clientserver = clientserver;
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMDNReceipt.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Starts the embedded task that guards the log
     */
    public void startMDNCheck() {
        this.checkThread = new MDNCheckThread();
        this.scheduledExecutor.scheduleWithFixedDelay(this.checkThread, 1, 1, TimeUnit.MINUTES);
    }

    public class MDNCheckThread implements Runnable {

        //wait this time between checks
        private MessageAccessDB messageAccess;

        public MDNCheckThread() {
            try {
                this.messageAccess = new MessageAccessDB(dbDriverManager);
            } catch (Exception e) {
                logger.severe("MDNCheckThread: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        }

        @Override
        public void run() {
            try {
                long olderThan = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(preferences.getInt(PreferencesAS2.ASYNC_MDN_TIMEOUT));
                List<AS2MessageInfo> overviewList = this.messageAccess.getMessagesSendOlderThan(olderThan);
                if (overviewList != null) {
                    for (AS2MessageInfo messageInfo : overviewList) {
                        try {
                            logger.log(Level.SEVERE, rb.getResourceString("expired"), messageInfo);
                            //a message id may have more then one entry if the sender implemented a resend mechanism
                            messageAccess.setMessageState(messageInfo.getMessageId(), AS2Message.STATE_PENDING, AS2Message.STATE_STOPPED);
                            messageInfo.setState(AS2Message.STATE_STOPPED);
                            ProcessingEvent.enqueueEventIfRequired(dbDriverManager, messageInfo, null);
                            //write status file
                            MessageStoreHandler handler = new MessageStoreHandler(dbDriverManager);
                            handler.writeOutboundStatusFile(messageInfo);
                        } catch (Exception e) {
                            //this thread MUST not stop on any error!
                            logger.severe(Thread.currentThread().getName() + ": " + e.getMessage());
                            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
                        }
                    }
                    if (overviewList.size() > 0) {
                        clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
                    }
                }
            } catch (Throwable e) {
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
            }
        }
    }
}
