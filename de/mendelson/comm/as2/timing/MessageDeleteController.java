//$Header: /as2/de/mendelson/comm/as2/timing/MessageDeleteController.java 48    25/11/22 11:50 Heller $
package de.mendelson.comm.as2.timing;

import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.log.LogAccessDB;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.preferences.ResourceBundlePreferences;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Controls the timed deletion of AS2 entries from the log
 *
 * @author S.Heller
 * @version $Revision: 48 $
 */
public class MessageDeleteController {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private PreferencesAS2 preferences = new PreferencesAS2();
    private MessageDeleteThread deleteThread;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new NamedThreadFactory("old-transactions-housekeeping"));
    private ClientServer clientserver = null;
    private MecResourceBundle rb = null;
    private MecResourceBundle rbTime = null;
    private LogAccessDB logAccess;
    private IDBDriverManager dbDriverManager;
    private MessageAccessDB messageAccess;

    public MessageDeleteController(ClientServer clientserver, IDBDriverManager dbDriverManager) {
        this.clientserver = clientserver;
        this.dbDriverManager = dbDriverManager;
        this.logAccess = new LogAccessDB(dbDriverManager);
        this.messageAccess = new MessageAccessDB(this.dbDriverManager);
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMessageDeleteController.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        try {
            this.rbTime = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Starts the embedded task that guards the log
     */
    public void startAutoDeleteControl() {
        this.deleteThread = new MessageDeleteThread();
        this.scheduledExecutor.scheduleWithFixedDelay(this.deleteThread, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Deletes a sub list of the whole delete order from the log
     *
     * @param infoList
     * @param broadcastRefresh
     * @param deleteLog
     */
    private void deleteMessageFromLogPart(List<AS2MessageInfo> infoList, boolean broadcastRefresh, StringBuilder deleteLog,
            Connection runtimeConnectionNoAutoCommit) throws Exception {
        List<String> messageIdList = new ArrayList<String>();
        for (AS2MessageInfo messageInfo : infoList) {
            messageIdList.add(messageInfo.getMessageId());
        }
        this.logAccess.deleteMessageLog(messageIdList, runtimeConnectionNoAutoCommit);
        //delete all raw files from the disk
        List<String> rawfilenames = this.messageAccess.getRawFilenamesToDelete(messageIdList, runtimeConnectionNoAutoCommit);
        if (rawfilenames != null && !rawfilenames.isEmpty()) {
            for (String rawfilename : rawfilenames) {
                try {
                    Files.delete(Paths.get(rawfilename));
                    deleteLog.append("[" + rb.getResourceString("delete.ok") + "]: ");
                } catch (NoSuchFileException e) {
                    deleteLog.append("[" + rb.getResourceString("delete.skipped")
                            + "]: (" + e.getClass().getSimpleName() + ")  " + e.getMessage());
                } catch (Exception e) {
                    deleteLog.append("[" + rb.getResourceString("delete.failed")
                            + "]: (" + e.getClass().getSimpleName() + ")  " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_FILE_OPERATION_ANY);
                    new File(rawfilename).deleteOnExit();
                }
                deleteLog.append("  ");
                deleteLog.append(Paths.get(rawfilename).toAbsolutePath().toString());
                deleteLog.append(System.lineSeparator());
            }
        }
        this.messageAccess.deleteMessages(messageIdList, runtimeConnectionNoAutoCommit);
        if (broadcastRefresh && this.clientserver != null) {
            this.clientserver.broadcastToClients(new RefreshClientMessageOverviewList());
        }
    }

    /**
     * Deletes a message entry from the log. Clears all files. Main entry point
     * for a delete operation
     */
    public void deleteMessagesFromLog(List<AS2MessageInfo> infoList, boolean broadcastRefresh, StringBuilder deleteLog) {
        int bulkSize = 10;
        //a new connection to the database is required because the message storage contains several tables and all this has to be transactional
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionname = "Message_deleteFromLog";
        Statement transactionStatement = null;
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            //start transaction
            this.dbDriverManager.startTransaction(transactionStatement, transactionname);
            //lock tables for delete
            this.dbDriverManager.setTableLockDELETE(transactionStatement,
                    new String[]{
                        "messages",
                        "messagelog",
                        "mdn",
                        "payload"
                    });
            List<AS2MessageInfo> subList = new ArrayList<AS2MessageInfo>();
            for (int i = 0; i < infoList.size(); i++) {
                subList.add(infoList.get(i));
                if (i != 0 && i % bulkSize == 0) {
                    this.deleteMessageFromLogPart(subList, broadcastRefresh, deleteLog, runtimeConnectionNoAutoCommit);
                    subList.clear();
                }
            }
            if (!subList.isEmpty()) {
                this.deleteMessageFromLogPart(subList, broadcastRefresh, deleteLog, runtimeConnectionNoAutoCommit);
            }
            //all ok - commit
            this.dbDriverManager.commitTransaction(transactionStatement, transactionname);
        } catch (Exception e) {
            try {
                //an error occured - rollback transaction and release all table locks
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("MessageAccessDB.deleteMessagesFromLog: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    //nop
                }
            }
            if (runtimeConnectionNoAutoCommit != null) {
                try {
                    runtimeConnectionNoAutoCommit.close();
                } catch (Exception ex) {
                    //nop
                }
            }
        }

    }

    public class MessageDeleteThread implements Runnable {

        public MessageDeleteThread() {
        }

        @Override
        public void run() {
            try {
                if (preferences.getBoolean(PreferencesAS2.AUTO_MSG_DELETE)) {
                    MessageAccessDB messageAccess = new MessageAccessDB(dbDriverManager);
                    try {
                        long olderThan = System.currentTimeMillis()
                                - TimeUnit.SECONDS.toMillis(
                                        preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN)
                                        * preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S));
                        List<AS2MessageInfo> overviewList = messageAccess.getMessagesOlderThan(olderThan, -1);
                        if (overviewList != null) {
                            List<AS2MessageInfo> deleteList = new ArrayList<AS2MessageInfo>();
                            for (AS2MessageInfo messageInfo : overviewList) {
                                if (messageInfo.getState() == AS2Message.STATE_FINISHED || messageInfo.getState() == AS2Message.STATE_STOPPED) {
                                    if (preferences.getBoolean(PreferencesAS2.AUTO_MSG_DELETE_LOG)) {
                                        String timeUnit = "";
                                        if (preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S) == TimeUnit.DAYS.toSeconds(1)) {
                                            timeUnit = rbTime.getResourceString("maintenancemultiplier.day");
                                        } else if (preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S) == TimeUnit.HOURS.toSeconds(1)) {
                                            timeUnit = rbTime.getResourceString("maintenancemultiplier.hour");
                                        } else if (preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S) == TimeUnit.MINUTES.toSeconds(1)) {
                                            timeUnit = rbTime.getResourceString("maintenancemultiplier.minute");
                                        }
                                        logger.fine(rb.getResourceString("autodelete",
                                                new Object[]{
                                                    messageInfo.getMessageId(),
                                                    String.valueOf(preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN)),
                                                    timeUnit
                                                }));
                                    }
                                    deleteList.add(messageInfo);
                                }
                            }
                            StringBuilder deletedTransactionLog = new StringBuilder();
                            deleteMessagesFromLog(deleteList, true, deletedTransactionLog);
                            //fire system event if automatic log deletes should be loged
                            if (preferences.getBoolean(PreferencesAS2.AUTO_MSG_DELETE_LOG)) {
                                this.fireSystemEventTransactionsDeletedByMaintenance(olderThan, deleteList,
                                        deletedTransactionLog);
                            }
                        }
                    } catch (Throwable e) {
                        logger.severe("MessageDeleteThread: [" + e.getClass().getSimpleName() + "]:" + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
                    }
                }
            } catch (Throwable e) {
                //final try/catch - this thead must not stop!
                logger.severe("MessageDeleteThread: [" + e.getClass().getSimpleName() + "]:" + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
            }
        }

        /**
         * Fire a system event that the system maintenance process has deleted
         * transactions
         */
        private void fireSystemEventTransactionsDeletedByMaintenance(long olderThan, List<AS2MessageInfo> deletedTransactionList,
                StringBuilder transactionDeleteLog) {
            //Do not fire an event if there are no deleted transactions
            if (deletedTransactionList.isEmpty()) {
                return;
            }
            DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            SystemEvent event = new SystemEvent(
                    SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_TRANSACTION_DELETE);
            event.setSubject(rb.getResourceString("transaction.deleted.system"));
            StringBuilder builder = new StringBuilder();
            builder.append(rb.getResourceString("transaction.delete.setting.olderthan", dateFormat.format(new Date(olderThan))));
            builder.append(System.lineSeparator()).append(System.lineSeparator());
            for (int i = 0; i < deletedTransactionList.size(); i++) {
                AS2MessageInfo singleInfo = deletedTransactionList.get(i);
                builder.append("[");
                builder.append(rb.getResourceString("transaction.deleted.transactiondate",
                        dateFormat.format(singleInfo.getInitDate())));
                builder.append("] (");
                builder.append(singleInfo.getSenderId());
                builder.append(" --> ");
                builder.append(singleInfo.getReceiverId());
                builder.append(") ");
                builder.append(singleInfo.getMessageId());
                builder.append(System.lineSeparator());
            }
            builder.append("---").append(System.lineSeparator());
            builder.append(transactionDeleteLog);
            event.setBody(builder.toString());
            SystemEventManagerImplAS2.newEvent(event);
        }

    }
}
