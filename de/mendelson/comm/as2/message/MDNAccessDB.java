//$Header: /as2/de/mendelson/comm/as2/message/MDNAccessDB.java 33    29/11/22 10:07 Heller $
package de.mendelson.comm.as2.message;

import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Access MDN
 *
 * @author S.Heller
 * @version $Revision: 33 $
 */
public class MDNAccessDB {

    /**
     * Logger to log information to
     */
    private static final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final IDBDriverManager dbDriverManager;
    private final Calendar calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    /**
     * Creates new MDNAccessDB
     *
     * @param host host to connect to
     */
    public MDNAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Returns all overview rows from the database
     */
    public List<AS2MDNInfo> getMDN(String relatedMessageId) {
        List<AS2MDNInfo> messageList = new ArrayList<AS2MDNInfo>();
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            ResultSet result = null;
            PreparedStatement statement = null;
            try {
                statement = runtimeConnectionAutoCommit.prepareStatement(
                        "SELECT * FROM mdn WHERE relatedmessageid=? ORDER BY initdateutc ASC");
                statement.setString(1, relatedMessageId);
                result = statement.executeQuery();
                while (result.next()) {
                    AS2MDNInfo info = new AS2MDNInfo();
                    info.setMessageId(result.getString("messageid"));
                    info.setInitDate(result.getTimestamp("initdateutc", this.calendarUTC));
                    info.setDirection(result.getInt("direction"));
                    info.setRelatedMessageId(result.getString("relatedmessageid"));
                    info.setRawFilename(result.getString("rawfilename"));
                    info.setReceiverId(result.getString("receiverid"));
                    info.setSenderId(result.getString("senderid"));
                    info.setSignType(result.getInt("signature"));
                    info.setState(result.getInt("state"));
                    info.setHeaderFilename(result.getString("headerfilename"));
                    info.setSenderHost(result.getString("senderhost"));
                    info.setUserAgent(result.getString("useragent"));
                    info.setDispositionState(result.getString("dispositionstate"));
                    info.setRemoteMDNText(this.dbDriverManager.readTextStoredAsJavaObject(result, "mdntext"));
                    messageList.add(info);
                }
            } catch (Exception e) {
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
                return (null);
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
                    }
                }
            }
        } catch (Exception e) {
            this.logger.severe("MessageAccessDB.getMessageCount(state): " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (runtimeConnectionAutoCommit != null) {
                try {
                    runtimeConnectionAutoCommit.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
        return (messageList);
    }

    /**
     * Adds a MDN to the database - opens a new database connection because this
     * has to be transactional
     */
    public void initializeOrUpdateMDN(AS2MDNInfo info) {
        Statement statement = null;
        //a new connection to the database is required because the message storage contains several queries and all this has to be transactional
        Connection runtimeConnectionNoAutoCommit = null;
        String messageId = info.getRelatedMessageId();
        String transactionName = "MDN_init_update";
        //check if a related message exists        
        MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
        if (!messageAccess.messageIdExists(messageId)) {
            throw new RuntimeException("Unexpected MDN received. The inbound MDN references a message with the AS2 message id \"" + messageId + "\" that does not exist in the system.");
        }
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            statement = runtimeConnectionNoAutoCommit.createStatement();
            //start transaction
            this.dbDriverManager.startTransaction(statement, transactionName);
            this.dbDriverManager.setTableLockINSERTAndUPDATE(statement,
                    new String[]{"mdn"});
            int updatedEntries = this.updateMDN(info, runtimeConnectionNoAutoCommit);
            if (updatedEntries == 0) {
                this.initializeMDN(info, runtimeConnectionNoAutoCommit);
            }
            //all ok - finish transaction and release all locks
            this.dbDriverManager.commitTransaction(statement, transactionName);
        } catch (Throwable e) {
            try {
                //an error occured - rollback transaction and release all table locks
                this.dbDriverManager.rollbackTransaction(statement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (runtimeConnectionNoAutoCommit != null) {
                try {
                    runtimeConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Updates a MDN in the database and returns the number of modified rows. If
     * this is 0 a new insert of the MDN is recommended as it does not exist so
     * far
     */
    private int updateMDN(AS2MDNInfo info, Connection runtimeConnectionNoAutoCommit) throws Exception {
        PreparedStatement statement = null;
        int updatedEntries = 0;
        try {
            statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "UPDATE mdn SET rawfilename=?,receiverid=?,senderid=?,signature=?,state=?,headerfilename=?,"
                    + "mdntext=?,dispositionstate=? WHERE messageid=?");
            statement.setString(1, info.getRawFilename());
            statement.setString(2, info.getReceiverId());
            statement.setString(3, info.getSenderId());
            statement.setInt(4, info.getSignType());
            statement.setInt(5, info.getState());
            statement.setString(6, info.getHeaderFilename());
            this.dbDriverManager.setTextParameterAsJavaObject(statement, 7, info.getRemoteMDNText());
            statement.setString(8, info.getDispositionState());
            //condition
            statement.setString(9, info.getMessageId());
            updatedEntries = statement.executeUpdate();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
        return (updatedEntries);
    }

    /**
     * Checks if the MDN id does already exist in the database. In this case an
     * error occurred - a MDNs message id has to be unique
     */
    private void checkForUniqueMDNMessageId(AS2MDNInfo info, Connection runtimeConnectionNoAutoCommit) throws Exception {
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            //get SSL and sign certificates
            String query = "SELECT COUNT(1) AS counter FROM mdn WHERE messageid=?";
            statement = runtimeConnectionNoAutoCommit.prepareStatement(query);
            statement.setString(1, info.getMessageId());
            result = statement.executeQuery();
            if (result.next()) {
                if (result.getInt("counter") > 0) {
                    throw new RuntimeException("The received MDN with the message id "
                            + "\"" + info.getMessageId() + "\" does already exist in the system."
                            + " The message id of MDN must be unique, this MDN is related to the message "
                            + "\"" + info.getRelatedMessageId() + "\".");
                }
            }
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Adds a MDN to the database
     */
    private void initializeMDN(AS2MDNInfo info, Connection runtimeConnectionNoAutoCommit) throws Exception {
        this.checkForUniqueMDNMessageId(info, runtimeConnectionNoAutoCommit);
        PreparedStatement statement = null;
        try {
            statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO mdn(messageid,relatedmessageid,initdateutc,"
                    + "direction,rawfilename,receiverid,"
                    + "senderid,signature,state,headerfilename,"
                    + "senderhost,useragent,mdntext,dispositionstate)"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statement.setString(1, info.getMessageId());
            statement.setString(2, info.getRelatedMessageId());
            statement.setTimestamp(3, new java.sql.Timestamp(info.getInitDate().getTime()), this.calendarUTC);
            statement.setInt(4, info.getDirection());
            statement.setString(5, info.getRawFilename());
            statement.setString(6, info.getReceiverId());
            statement.setString(7, info.getSenderId());
            statement.setInt(8, info.getSignType());
            statement.setInt(9, info.getState());
            statement.setString(10, info.getHeaderFilename());
            statement.setString(11, info.getSenderHost());
            statement.setString(12, info.getUserAgent());
            this.dbDriverManager.setTextParameterAsJavaObject(statement, 13, info.getRemoteMDNText());
            statement.setString(14, info.getDispositionState());
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Returns all file names of files that could be deleted for a passed
     * message info
     */
    public List<String> getRawFilenamesToDelete(List<String> messageIds,
            Connection runtimeConnectionNoAutoCommit) throws Exception {
        List<String> filenameList = new ArrayList<String>();
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            StringBuilder query = new StringBuilder("SELECT rawfilename,headerfilename FROM mdn WHERE relatedmessageid IN (");
            for (int i = 0; i < messageIds.size(); i++) {
                if (i > 0) {
                    query.append(",");
                }
                query.append("?");
            }
            query.append(")");
            statement = runtimeConnectionNoAutoCommit.prepareStatement(query.toString());
            for (int i = 0; i < messageIds.size(); i++) {
                statement.setString(i + 1, messageIds.get(i));
            }
            result = statement.executeQuery();
            while (result.next()) {
                String rawFilename = result.getString("rawfilename");
                if (!result.wasNull()) {
                    filenameList.add(rawFilename);
                }
                String headerFilename = result.getString("headerfilename");
                if (!result.wasNull()) {
                    filenameList.add(headerFilename);
                }
            }
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
        return (filenameList);
    }
}
