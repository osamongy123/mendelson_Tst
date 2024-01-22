//$Header: /as2/de/mendelson/comm/as2/message/MessageAccessDB.java 142   9/12/22 14:22 Heller $
package de.mendelson.comm.as2.message;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.statistic.ServerInteroperabilityAccessDB;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Implementation of a server log for the as2 server database
 *
 * @author S.Heller
 * @version $Revision: 142 $
 */
public class MessageAccessDB {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final Calendar calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private final IDBDriverManager dbDriverManager;

    /**
     * Creates new message I/O log and connects to localhost
     *
     * @param host host to connect to
     */
    public MessageAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Returns the number of transmissions in the system of a special state
     */
    public int getMessageCount(int state) {
        int counter = 0;
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            PreparedStatement statement = null;
            ResultSet result = null;
            try {
                statement = runtimeConnectionAutoCommit.prepareStatement(
                        "SELECT COUNT(1) AS messagecount FROM messages WHERE state=?");
                statement.setInt(1, state);
                result = statement.executeQuery();
                if (result.next()) {
                    counter = result.getInt("messagecount");
                }
            } catch (Exception e) {
                this.logger.severe("MessageAccessDB.getMessageCount(state): " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
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
        return (counter);
    }

    /**
     * Returns the number of transmissions in the system
     */
    public int getMessageCount() {
        int counter = 0;
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            PreparedStatement statement = null;
            ResultSet result = null;
            try {
                statement = runtimeConnectionAutoCommit.prepareStatement("SELECT COUNT(1) AS messagecount FROM messages");
                result = statement.executeQuery();
                if (result.next()) {
                    counter = result.getInt("messagecount");
                }
            } catch (Exception e) {
                this.logger.severe("getMessageCount: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
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
        } catch (Exception e) {
            this.logger.severe("MessageAccessDB.getMessageCount: " + e.getMessage());
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
        return (counter);
    }

    /**
     * Returns the state of the latest passed message. Will return pending state
     * if the messageid does not exist.
     *
     * @return One of AS2Message.STATE_PENDING, AS2Message.STATE_FINISHED,
     * AS2Message.STATE_STOPPED
     */
    public int getMessageState(String messageId) {
        int state = AS2Message.STATE_PENDING;
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            PreparedStatement statement = null;
            ResultSet result = null;
            try {
                //desc because the latest message should be first in resultset
                statement = runtimeConnectionAutoCommit.prepareStatement(
                        "SELECT state FROM messages WHERE messageid=? ORDER BY initdateutc DESC");
                statement.setString(1, messageId);
                result = statement.executeQuery();
                if (result.next()) {
                    state = result.getInt("state");
                }
            } catch (Exception e) {
                this.logger.severe("getMessageState: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
            } finally {
                try {
                    if (result != null) {
                        result.close();
                    }
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        } catch (Exception e) {
            this.logger.severe("MessageAccessDB.getMessageState: " + e.getMessage());
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
        return (state);
    }

    public void setMessageState(String messageId, int fromState, int toState) {
        Connection runtimeConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        String transactionName = "MessageAccessDB_setMessageState";
        try {
            runtimeConnectionNoAutoCommit
                    = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            try {
                this.setMessageState(runtimeConnectionNoAutoCommit,
                        messageId, fromState, toState);
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Exception e) {
                try {
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Throwable e) {
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
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
    }

    /**
     * Sets the corresponding message status to the new value. This will change
     * the state in any case without any check
     *
     * @param state one of the states defined in the class AS2Message
     */
    private void setMessageState(Connection runtimeConnectionNoAutoCommit,
            String messageId, int fromState, int toState) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "UPDATE messages SET state=? WHERE state=? AND messageid=?");
            statement.setInt(1, toState);
            statement.setInt(2, fromState);
            statement.setString(3, messageId);
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
        //A transaction has been stopped. This is worth a system event because a notification might be triggered
        //for such an event
        if (toState == AS2Message.STATE_STOPPED) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SystemEventManagerImplAS2 eventManager = new SystemEventManagerImplAS2();
                    try {
                        eventManager.newEventTransactionError(messageId, dbDriverManager);
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.systemFailure(e);
                    }
                }
            };
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(runnable);
            executor.shutdown();
        }
    }

    /**
     * Sets the corresponding message status to the new value. This will have
     * only effects if the actual message state is "pending". "Stopped" and
     * "finished" are states that MUST not be changed.
     *
     * @param newState one of the states defined in the class AS2Message
     */
    public void setMessageState(String messageId, int newState) {
        int oldState = this.getMessageState(messageId);
        //keep red state and keep green state - only the pending state may be changed
        if (oldState != AS2Message.STATE_PENDING) {
            return;
        }
        this.setMessageState(messageId, oldState, newState);
        //store the entry in the interoperability statistic
        ServerInteroperabilityAccessDB statisticAccess
                = new ServerInteroperabilityAccessDB(this.dbDriverManager);
        List<AS2MessageInfo> overviewList = this.getMessageOverview(messageId);
        statisticAccess.addEntry(messageId, newState, overviewList);
    }

    /**
     * Returns information about the payload of a special message
     */
    public List<AS2Payload> getPayload(String messageId) {
        List<AS2Payload> payloadList = new ArrayList<AS2Payload>();
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            ResultSet result = null;
            PreparedStatement statement = null;
            try {
                statement = runtimeConnectionAutoCommit.prepareStatement("SELECT * FROM payload WHERE messageid=?");
                statement.setString(1, messageId);
                result = statement.executeQuery();
                while (result.next()) {
                    AS2Payload payload = new AS2Payload();
                    payload.setPayloadFilename(result.getString("payloadfilename"));
                    payload.setOriginalFilename(result.getString("originalfilename"));
                    payload.setContentId(result.getString("contentid"));
                    payload.setContentType(result.getString("contenttype"));
                    payloadList.add(payload);
                }
            } catch (Exception e) {
                this.logger.severe("MessageAccessDB.getPayload: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
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
        } catch (Exception e) {
            this.logger.severe("MessageAccessDB.getPayload: " + e.getMessage());
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
        return (payloadList);
    }

    /**
     * Returns all detail rows from the datase
     */
    public List<AS2Info> getMessageDetails(String messageId) {
        List<AS2Info> messageList = new ArrayList<AS2Info>();
        messageList.addAll(this.getMessageOverview(messageId));
        MDNAccessDB mdnAccess = new MDNAccessDB(this.dbDriverManager);
        messageList.addAll(mdnAccess.getMDN(messageId));
        return (messageList);
    }

    /**
     * Checks if a passed message id exists
     */
    public boolean messageIdExists(String messageId) {
        AS2MessageInfo info = this.getLastMessageEntry(messageId);
        return (info != null);
    }

    public String getLastMessageIdByUserdefinedId(String userdefinedId) {
        if (userdefinedId == null) {
            return (null);
        }
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            ResultSet result = null;
            PreparedStatement statement = null;
            try {
                //desc because we need the latest
                statement = runtimeConnectionAutoCommit.prepareStatement(
                        "SELECT messageid FROM messages WHERE userdefinedid=? ORDER BY initdateutc DESC");
                statement.setString(1, userdefinedId);
                result = statement.executeQuery();
                if (result.next()) {
                    return (result.getString("messageid"));
                }
            } catch (Exception e) {
                this.logger.severe("MessageAccessDB.getMessageIdByUserdefinedId: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
                return (null);
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessageIdByUserdefinedId: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessageIdByUserdefinedId: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
            }
        } catch (Exception e) {
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
        return (null);
    }

    /**
     * Reads information about a specific messageid from the data base, returns
     * the latest message of this id
     */
    public AS2MessageInfo getLastMessageEntry(String messageId) {
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            ResultSet result = null;
            PreparedStatement statement = null;
            try {
                //desc because we need the latest
                statement = runtimeConnectionAutoCommit.prepareStatement("SELECT * FROM messages WHERE messageid=? ORDER BY initdateutc DESC");
                statement.setString(1, messageId);
                result = statement.executeQuery();
                if (result.next()) {
                    AS2MessageInfo info = new AS2MessageInfo();
                    info.setInitDate(result.getTimestamp("initdateutc", this.calendarUTC));
                    info.setEncryptionType(result.getInt("encryption"));
                    info.setDirection(result.getInt("direction"));
                    info.setMessageType(result.getInt("messagetype"));
                    info.setMessageId(result.getString("messageid"));
                    info.setRawFilename(result.getString("rawfilename"));
                    info.setReceiverId(result.getString("receiverid"));
                    info.setSenderId(result.getString("senderid"));
                    info.setSignType(result.getInt("signature"));
                    info.setState(result.getInt("state"));
                    info.setRequestsSyncMDN(result.getInt("syncmdn") == 1);
                    info.setHeaderFilename(result.getString("headerfilename"));
                    info.setRawFilenameDecrypted(result.getString("rawdecryptedfilename"));
                    info.setSenderHost(result.getString("senderhost"));
                    info.setUserAgent(result.getString("useragent"));
                    info.setReceivedContentMIC(result.getString("contentmic"));
                    info.setCompressionType(result.getInt("msgcompression"));
                    info.setAsyncMDNURL(result.getString("asyncmdnurl"));
                    info.setSubject(result.getString("msgsubject"));
                    info.setResendCounter(result.getInt("resendcounter"));
                    info.setUserdefinedId(result.getString("userdefinedid"));
                    info.setUsesTLS(result.getInt("secureconnection") == 1);
                    return (info);
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
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (runtimeConnectionAutoCommit != null) {
                try {
                    runtimeConnectionAutoCommit.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
        return (null);
    }

    /**
     * Returns all overview rows from the database - opens a new connection
     */
    public List<AS2MessageInfo> getMessageOverview(String messageId) {
        List<AS2MessageInfo> messageList = new ArrayList<AS2MessageInfo>();
        ResultSet result = null;
        PreparedStatement selectStatement = null;
        Connection runtimeConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        String transactionname = "Message_getOverview";
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionname);
            String query = "SELECT * FROM messages WHERE messageid=? ORDER BY initdateutc ASC";
            selectStatement = runtimeConnectionNoAutoCommit.prepareStatement(query);
            selectStatement.setString(1, messageId);
            result = selectStatement.executeQuery();
            while (result.next()) {
                AS2MessageInfo info = new AS2MessageInfo();
                info.setInitDate(result.getTimestamp("initdateutc", this.calendarUTC));
                info.setEncryptionType(result.getInt("encryption"));
                info.setDirection(result.getInt("direction"));
                info.setMessageType(result.getInt("messagetype"));
                info.setMessageId(result.getString("messageid"));
                info.setRawFilename(result.getString("rawfilename"));
                info.setReceiverId(result.getString("receiverid"));
                info.setSenderId(result.getString("senderid"));
                info.setSignType(result.getInt("signature"));
                info.setState(result.getInt("state"));
                info.setRequestsSyncMDN(result.getInt("syncmdn") == 1);
                info.setHeaderFilename(result.getString("headerfilename"));
                info.setRawFilenameDecrypted(result.getString("rawdecryptedfilename"));
                info.setSenderHost(result.getString("senderhost"));
                info.setUserAgent(result.getString("useragent"));
                info.setReceivedContentMIC(result.getString("contentmic"));
                info.setCompressionType(result.getInt("msgcompression"));
                info.setAsyncMDNURL(result.getString("asyncmdnurl"));
                info.setSubject(result.getString("msgsubject"));
                info.setResendCounter(result.getInt("resendcounter"));
                info.setUserdefinedId(result.getString("userdefinedid"));
                info.setUsesTLS(result.getInt("secureconnection") == 1);
                messageList.add(info);
            }
            this.dbDriverManager.commitTransaction(transactionStatement, transactionname);
        } catch (Exception e) {
            try {
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("MessageAccessDB.getMessageOverview(messageid): " + e.getMessage());
            e.printStackTrace();
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, selectStatement);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Exception e) {
                    this.logger.severe("MessageAccessDB.getMessageOverview(messageid): " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (selectStatement != null) {
                try {
                    selectStatement.close();
                } catch (Exception e) {
                    this.logger.severe("MessageAccessDB.getMessageOverview(messageid): " + e.getMessage());
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
        return (messageList);
    }

    /**
     * Returns all overview rows from the datase
     */
    public List<AS2MessageInfo> getMessageOverview(MessageOverviewFilter filter) {
        List<AS2MessageInfo> messageList = new ArrayList<AS2MessageInfo>();
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            ResultSet result = null;
            PreparedStatement statement = null;
            try {
                List<Object> parameterList = new ArrayList<Object>();
                StringBuilder queryCondition = new StringBuilder();
                if (filter.getShowPartner() != null) {
                    Partner partner = filter.getShowPartner();
                    if (queryCondition.length() == 0) {
                        queryCondition.append(" WHERE");
                    } else {
                        queryCondition.append(" AND");
                    }
                    queryCondition.append("(senderid=? OR receiverid=?)");
                    parameterList.add(partner.getAS2Identification());
                    parameterList.add(partner.getAS2Identification());
                }
                if (filter.getShowLocalStation() != null) {
                    Partner localStation = filter.getShowLocalStation();
                    if (queryCondition.length() == 0) {
                        queryCondition.append(" WHERE");
                    } else {
                        queryCondition.append(" AND");
                    }
                    queryCondition.append("(senderid=? OR receiverid=?)");
                    parameterList.add(localStation.getAS2Identification());
                    parameterList.add(localStation.getAS2Identification());
                }
                if (!filter.isShowFinished()) {
                    if (queryCondition.length() == 0) {
                        queryCondition.append(" WHERE");
                    } else {
                        queryCondition.append(" AND");
                    }
                    queryCondition.append(" state <>?");
                    parameterList.add(Integer.valueOf(AS2Message.STATE_FINISHED));
                }
                if (!filter.isShowPending()) {
                    if (queryCondition.length() == 0) {
                        queryCondition.append(" WHERE");
                    } else {
                        queryCondition.append(" AND");
                    }
                    queryCondition.append(" state <>?");
                    parameterList.add(Integer.valueOf(AS2Message.STATE_PENDING));
                }
                if (!filter.isShowStopped()) {
                    if (queryCondition.length() == 0) {
                        queryCondition.append(" WHERE");
                    } else {
                        queryCondition.append(" AND");
                    }
                    queryCondition.append(" state <>?");
                    parameterList.add(Integer.valueOf(AS2Message.STATE_STOPPED));
                }
                if (filter.getShowDirection() != MessageOverviewFilter.DIRECTION_ALL) {
                    if (queryCondition.length() == 0) {
                        queryCondition.append(" WHERE");
                    } else {
                        queryCondition.append(" AND");
                    }
                    queryCondition.append(" direction=?");
                    parameterList.add(Integer.valueOf(filter.getShowDirection()));
                }
                if (filter.getShowMessageType() != MessageOverviewFilter.MESSAGETYPE_ALL) {
                    if (queryCondition.length() == 0) {
                        queryCondition.append(" WHERE");
                    } else {
                        queryCondition.append(" AND");
                    }
                    queryCondition.append(" messagetype=?");
                    parameterList.add(Integer.valueOf(filter.getShowMessageType()));
                }
                if (filter.getUserdefinedId() != null) {
                    if (queryCondition.length() == 0) {
                        queryCondition.append(" WHERE");
                    } else {
                        queryCondition.append(" AND");
                    }
                    queryCondition.append(" userdefinedid=?");
                    parameterList.add(filter.getUserdefinedId());
                }
                boolean useTimeFilter = filter.getStartTime() != 0L && filter.getEndTime() != 0L;
                if (useTimeFilter) {
                    if (queryCondition.length() == 0) {
                        queryCondition.append(" WHERE");
                    } else {
                        queryCondition.append(" AND");
                    }
                    queryCondition.append(" CAST(initdateutc AS DATE)>=? AND CAST(initdateutc AS DATE)<=?");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(filter.getStartTime());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    parameterList.add(new Timestamp(calendar.getTimeInMillis()));
                    calendar.setTimeInMillis(filter.getEndTime());
                    calendar.add(Calendar.DAY_OF_YEAR, 0);
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 999);
                    parameterList.add(new Timestamp(calendar.getTimeInMillis()));
                }
                //Hint: This is the wrong order! It should be ordered using "ASC". But the HSQLDB LIMIT clause
                //just takes the n first rows of the result set and returns them. Means the first n results are taken now 
                //in the wrong order and then the returned list of transactions is built in the wrong order again 
                //(add every row to the pos 0 of the list)
                //- then the result is as if the LIMIT has been taken from the other side of the result set
                String query = "SELECT * FROM messages" + queryCondition.toString()
                        + " ORDER BY initdateutc DESC";
                if (!useTimeFilter) {
                    //do NOT use the limit if a time filter is set as the user want to see all transactions in range
                    query = this.dbDriverManager.addLimitToQuery(query, filter.getLimit());
                }
                statement = runtimeConnectionAutoCommit.prepareStatement(query);
                for (int i = 0; i < parameterList.size(); i++) {
                    if (parameterList.get(i) instanceof Integer) {
                        statement.setInt(i + 1, ((Integer) parameterList.get(i)).intValue());
                    } else if (parameterList.get(i) instanceof Timestamp) {
                        statement.setTimestamp(i + 1, (Timestamp) parameterList.get(i));
                    } else {
                        statement.setString(i + 1, (String) parameterList.get(i));
                    }
                }
                result = statement.executeQuery();
                while (result.next()) {
                    AS2MessageInfo info = new AS2MessageInfo();
                    info.setInitDate(result.getTimestamp("initdateutc", this.calendarUTC));
                    info.setEncryptionType(result.getInt("encryption"));
                    info.setDirection(result.getInt("direction"));
                    info.setMessageType(result.getInt("messagetype"));
                    info.setMessageId(result.getString("messageid"));
                    info.setRawFilename(result.getString("rawfilename"));
                    info.setReceiverId(result.getString("receiverid"));
                    info.setSenderId(result.getString("senderid"));
                    info.setSignType(result.getInt("signature"));
                    info.setState(result.getInt("state"));
                    info.setRequestsSyncMDN(result.getInt("syncmdn") == 1);
                    info.setHeaderFilename(result.getString("headerfilename"));
                    info.setRawFilenameDecrypted(result.getString("rawdecryptedfilename"));
                    info.setSenderHost(result.getString("senderhost"));
                    info.setUserAgent(result.getString("useragent"));
                    info.setReceivedContentMIC(result.getString("contentmic"));
                    info.setCompressionType(result.getInt("msgcompression"));
                    info.setAsyncMDNURL(result.getString("asyncmdnurl"));
                    info.setSubject(result.getString("msgsubject"));
                    info.setResendCounter(result.getInt("resendcounter"));
                    info.setUserdefinedId(result.getString("userdefinedid"));
                    info.setUsesTLS(result.getInt("secureconnection") == 1);
                    //change the order of the list. This is required because of the LIMIT clause of HSQLDB
                    messageList.add(0, info);
                }
            } catch (Exception e) {
                this.logger.severe("MessageAccessDB.getMessageOverview(filter): " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessageOverview(filter): " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessageOverview(filter): " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
            }
        } catch (Exception e) {
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
     * Returns all file names of files that could be deleted for a passed
     * message info
     */
    public List<String> getRawFilenamesToDelete(List<String> messageIds,
            Connection runtimeConnectionNoAutoCommit) throws Exception {
        List<String> filenameList = new ArrayList<String>();
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            StringBuilder query = new StringBuilder("SELECT rawfilename,rawdecryptedfilename,headerfilename "
                    + "FROM messages WHERE messageid IN (");
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
                String rawFilenameDecrypted = result.getString("rawdecryptedfilename");
                if (!result.wasNull()) {
                    filenameList.add(rawFilenameDecrypted);
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
                    this.logger.severe("MessageAccessDB.getRawFilenamesToDelete: " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    this.logger.severe("MessageAccessDB.getRawFilenamesToDelete: " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
        MDNAccessDB mdnAccess = new MDNAccessDB(this.dbDriverManager);
        filenameList.addAll(mdnAccess.getRawFilenamesToDelete(messageIds, runtimeConnectionNoAutoCommit));
        return (filenameList);
    }

    /**
     * Deletes messages and MDNs of the passed id - transactional Returns the
     * number of deleted messages. Means if the returned value is 0 none of the
     * passed message ids did exist in the database
     */
    public int deleteMessages(List<String> messageIds, Connection runtimeConnectionNoAutoCommit) throws Exception {
        PreparedStatement mdnDeleteStatement = null;
        PreparedStatement payload1DeleteStatement = null;
        PreparedStatement payload2DeleteStatement = null;
        PreparedStatement message1DeleteStatement = null;
        PreparedStatement message2DeleteStatement = null;
        try {
            if (messageIds != null && !messageIds.isEmpty()) {
                StringBuilder deleteQuery = new StringBuilder("DELETE FROM mdn WHERE relatedmessageid IN (");
                for (int i = 0; i < messageIds.size(); i++) {
                    if (i > 0) {
                        deleteQuery.append(",");
                    }
                    deleteQuery.append("?");
                }
                deleteQuery.append(")");
                mdnDeleteStatement = runtimeConnectionNoAutoCommit.prepareStatement(deleteQuery.toString());
                for (int i = 0; i < messageIds.size(); i++) {
                    mdnDeleteStatement.setString(i + 1, messageIds.get(i));
                }
                mdnDeleteStatement.executeUpdate();
                deleteQuery = new StringBuilder("DELETE FROM payload WHERE messageid IN (");
                for (int i = 0; i < messageIds.size(); i++) {
                    if (i > 0) {
                        deleteQuery.append(",");
                    }
                    deleteQuery.append("?");
                }
                deleteQuery.append(")");
                payload1DeleteStatement = runtimeConnectionNoAutoCommit.prepareStatement(deleteQuery.toString());
                for (int i = 0; i < messageIds.size(); i++) {
                    payload1DeleteStatement.setString(i + 1, messageIds.get(i));
                }
                payload1DeleteStatement.executeUpdate();
                deleteQuery = new StringBuilder("DELETE FROM messages WHERE messageid IN (");
                for (int i = 0; i < messageIds.size(); i++) {
                    if (i > 0) {
                        deleteQuery.append(",");
                    }
                    deleteQuery.append("?");
                }
                deleteQuery.append(")");
                message1DeleteStatement = runtimeConnectionNoAutoCommit.prepareStatement(deleteQuery.toString());
                for (int i = 0; i < messageIds.size(); i++) {
                    message1DeleteStatement.setString(i + 1, messageIds.get(i));
                }
                int deletedMessages = message1DeleteStatement.executeUpdate();
                return (deletedMessages);
            } else {
                payload2DeleteStatement = runtimeConnectionNoAutoCommit.prepareStatement("DELETE FROM payload WHERE messageid IS NULL");
                payload2DeleteStatement.executeUpdate();
                message2DeleteStatement = runtimeConnectionNoAutoCommit.prepareStatement("DELETE FROM messages WHERE messageid IS NULL");
                int deletedMessages = message2DeleteStatement.executeUpdate();
                return (deletedMessages);
            }
        } finally {
            if (mdnDeleteStatement != null) {
                try {
                    mdnDeleteStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (payload1DeleteStatement != null) {
                try {
                    payload1DeleteStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (payload2DeleteStatement != null) {
                try {
                    payload1DeleteStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (message1DeleteStatement != null) {
                try {
                    message1DeleteStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (message2DeleteStatement != null) {
                try {
                    message2DeleteStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Deletes messages and MDNs of the passed id by opening a new database
     * connection - transactional Returns the number of deleted messages. If non
     * of the messages ids exists 0 will be returned
     */
    public int deleteMessages(List<String> messageIds) {
        Statement transactionStatement = null;
        //a new connection to the database is required because the message storage contains several tables and all this has to be transactional
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionname = "Message_delete";
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            //start transaction
            this.dbDriverManager.startTransaction(transactionStatement, transactionname);
            //lock tables
            this.dbDriverManager.setTableLockDELETE(transactionStatement,
                    new String[]{
                        "mdn",
                        "payload",
                        "messages",});
            int deletedMessages = this.deleteMessages(messageIds, runtimeConnectionNoAutoCommit);
            //all ok - finish transaction and release all locks
            this.dbDriverManager.commitTransaction(transactionStatement, transactionname);
            return (deletedMessages);
        } catch (Exception e) {
            try {
                //an error occured - rollback transaction and release all table locks
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("MessageAccessDB.deleteMessage: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
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
        return (0);
    }

    /**
     * Deletes messages and MDNs of the passed id
     */
    public void deleteMessagesByInfo(List<AS2MessageInfo> infoList) {
        List<String> messageIdList = new ArrayList<String>();
        for (AS2MessageInfo messageInfo : infoList) {
            messageIdList.add(messageInfo.getMessageId());
        }
        this.deleteMessages(messageIdList);
    }

    public void setMessageSendDate(AS2MessageInfo info) {
        Connection runtimeConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        String transactionName = "MessageAccessDB_setMessageSendDate";
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            try {
                this.setMessageSendDate(runtimeConnectionNoAutoCommit, info);
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Exception e) {
                try {
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
            if (runtimeConnectionNoAutoCommit != null) {
                try {
                    runtimeConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
    }

    /**
     * Updates a message entry in the database, only the "senddateutc" col
     */
    private void setMessageSendDate(Connection runtimeConnectionNoAutoCommit, AS2MessageInfo info) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "UPDATE messages SET senddateutc=? WHERE messageid=?");
            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()), this.calendarUTC);
            //WHERE
            statement.setString(2, info.getMessageId());
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

    public void updateFilenames(AS2MessageInfo info) {
        Connection runtimeConnectionNoAutoCommit = null;
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            this.updateFilenamesAsTransaction(runtimeConnectionNoAutoCommit, info);
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e);
        } finally {
            if (runtimeConnectionNoAutoCommit != null) {
                try {
                    runtimeConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
    }

    /**
     * Updates a message entry in the database, only the filenames
     */
    private void updateFilenamesAsTransaction(Connection runtimeConnectionNoAutoCommit, AS2MessageInfo info) {
        PreparedStatement statement = null;
        Statement transactionStatement = null;
        String transactionName = "MessageAccessDB_updateFilenames";
        try {
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "UPDATE messages SET rawfilename=?,headerfilename=?,rawdecryptedfilename=? WHERE messageid=?");
            statement.setString(1, info.getRawFilename());
            statement.setString(2, info.getHeaderFilename());
            statement.setString(3, info.getRawFilenameDecrypted());
            //WHERE
            statement.setString(4, info.getMessageId());
            statement.executeUpdate();
            this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
        } catch (Throwable e) {
            try {
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("MessageAccessDB.updateFilenames: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    public void updateSubject(AS2MessageInfo info) {
        Connection runtimeConnectionNoAutoCommit = null;
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            this.updateSubjectAsTransaction(runtimeConnectionNoAutoCommit, info);
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e);
        } finally {
            if (runtimeConnectionNoAutoCommit != null) {
                try {
                    runtimeConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
    }

    /**
     * Updates the subject of a message
     */
    private void updateSubjectAsTransaction(Connection runtimeConnectionNoAutoCommit, AS2MessageInfo info) {
        PreparedStatement statement = null;
        Statement transactionStatement = null;
        String transactionName = "MessageAccessDB_updateSubject";
        try {
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "UPDATE messages SET msgsubject=? WHERE messageid=?");
            statement.setString(1, info.getSubject());
            //condition
            statement.setString(2, info.getMessageId());
            statement.executeUpdate();
            this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
        } catch (Exception e) {
            try {
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("MessageAccessDB.updateSubject: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    this.logger.severe("MessageAccessDB.updateSubject: " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    public void incResendCounter(String messageId) {
        Connection runtimeConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        String transactionName = "MessageAccessDB_incResendCounter";
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                    new String[]{"messages"});
            try {
                this.incResendCounter(runtimeConnectionNoAutoCommit, messageId);
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Throwable e) {
                try {
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                this.logger.severe("MessageAccessDB.incResendCounter: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.systemFailure(e);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (runtimeConnectionNoAutoCommit != null) {
                try {
                    runtimeConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
    }

    private void incResendCounter(Connection runtimeConnectionNoAutoCommit, String messageId) throws Exception {
        PreparedStatement statementUpdate = null;
        PreparedStatement statementSelect = null;
        ResultSet result = null;
        try {
            int currentCounter = 0;
            statementSelect = runtimeConnectionNoAutoCommit.prepareStatement(
                    "SELECT resendcounter FROM messages WHERE messageId=?");
            statementSelect.setString(1, messageId);
            result = statementSelect.executeQuery();
            if (result.next()) {
                currentCounter = result.getInt("resendcounter");
            }
            statementUpdate = runtimeConnectionNoAutoCommit.prepareStatement(
                    "UPDATE messages SET resendcounter=? WHERE messageid=?");
            statementUpdate.setInt(1, currentCounter + 1);
            statementUpdate.setString(2, messageId);
            statementUpdate.executeUpdate();
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (statementSelect != null) {
                try {
                    statementSelect.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (statementUpdate != null) {
                try {
                    statementUpdate.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Establishes a new DB connection, writes the payload and original
     * filenames to the database, deleting all entries first (only if a payload
     * has been passed)
     */
    public void insertPayloads(String messageId, List<AS2Payload> payloadList) {
        Statement transactionStatement = null;
        //a new connection to the database is required because the message storage contains several tables and all this has to be transactional
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionName = "Message_insertPayload";
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            //start transaction
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            //get table lock - as insertPayloads contains a delete this is the lock level
            this.dbDriverManager.setTableLockDELETE(transactionStatement,
                    new String[]{"payload"});
            this.insertPayloads(messageId, payloadList, runtimeConnectionNoAutoCommit);
            //all ok - finish transaction and release all locks
            this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
        } catch (Throwable e) {
            try {
                //an error occured - rollback transaction and release all table locks
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("MessageAccessDB.insertPayloads: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (runtimeConnectionNoAutoCommit != null) {
                try {
                    runtimeConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
    }

    /**
     * Writes the payload and original filenames to the database, deleting all
     * entries first (only if a payload has been passed)
     */
    private void insertPayloads(String messageId, List<AS2Payload> payloadList,
            Connection runtimeConnectionNoAutoCommit) throws Exception {
        if (payloadList == null || payloadList.isEmpty()) {
            return;
        }
        PreparedStatement statementDelete = null;
        try {
            statementDelete = runtimeConnectionNoAutoCommit.prepareStatement("DELETE FROM payload WHERE messageid=?");
            statementDelete.setString(1, messageId);
            statementDelete.executeUpdate();
            for (int i = 0; i < payloadList.size(); i++) {
                PreparedStatement statementInsert = null;
                try {
                    //insert
                    statementInsert = runtimeConnectionNoAutoCommit.prepareStatement(
                            "INSERT INTO payload(messageid,originalfilename,payloadfilename,contentid,contenttype)"
                            + "VALUES(?,?,?,?,?)");
                    AS2Payload payload = payloadList.get(i);
                    statementInsert.setString(1, messageId);
                    statementInsert.setString(2, payload.getOriginalFilename());
                    statementInsert.setString(3, payload.getPayloadFilename());
                    statementInsert.setString(4, payload.getContentId());
                    statementInsert.setString(5, payload.getContentType());
                    statementInsert.executeUpdate();
                } finally {
                    if (statementInsert != null) {
                        try {
                            statementInsert.close();
                        } catch (Exception e) {
                            this.logger.severe("MessageAccessDB.insertPayload: " + e.getMessage());
                            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                        }
                    }
                }
            }
        } finally {
            if (statementDelete != null) {
                try {
                    statementDelete.close();
                } catch (Exception e) {
                    this.logger.severe("MessageAccessDB.insertPayload: " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Initializes or updates a messages in the database. If the message id
     * already exists it is updated
     *
     * @param message
     */
    public void initializeOrUpdateMessage(AS2MessageInfo info) {
        Statement transactionStatement = null;
        //a new connection to the database is required because the message storage contains several tables and all this has to be transactional
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionName = "MessageAccessDB_initializeOrUpdateMessage";
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockDELETE(transactionStatement,
                    new String[]{"payload", "messages"});
            try {
                int updatedMessageCount = this.updateMessage(info, runtimeConnectionNoAutoCommit);
                if (updatedMessageCount == 0) {
                    this.initializeMessage(info, runtimeConnectionNoAutoCommit);
                }
                //all ok - finish transaction and release all locks
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Throwable e) {
                try {
                    //an error occured - rollback transaction and release all table locks
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Throwable e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (runtimeConnectionNoAutoCommit != null) {
                try {
                    runtimeConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
    }

    /**
     * Initializes a messages in the database.
     */
    private void initializeMessage(AS2MessageInfo info, Connection runtimeConnectionNoAutoCommit) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO messages(initdateutc,encryption,direction,messageid,rawfilename,receiverid,senderid,"
                    + "signature,state,syncmdn,headerfilename,rawdecryptedfilename,senderhost,useragent,"
                    + "contentmic,msgcompression,messagetype,asyncmdnurl,msgsubject,userdefinedid,"
                    + "secureconnection)VALUES("
                    + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setTimestamp(1, new java.sql.Timestamp(info.getInitDate().getTime()), this.calendarUTC);
            preparedStatement.setInt(2, info.getEncryptionType());
            preparedStatement.setInt(3, info.getDirection());
            preparedStatement.setString(4, info.getMessageId());
            preparedStatement.setString(5, info.getRawFilename());
            preparedStatement.setString(6, info.getReceiverId());
            preparedStatement.setString(7, info.getSenderId());
            preparedStatement.setInt(8, info.getSignType());
            preparedStatement.setInt(9, info.getState());
            preparedStatement.setInt(10, info.requestsSyncMDN() ? 1 : 0);
            preparedStatement.setString(11, info.getHeaderFilename());
            preparedStatement.setString(12, info.getRawFilenameDecrypted());
            preparedStatement.setString(13, info.getSenderHost());
            preparedStatement.setString(14, info.getUserAgent());
            preparedStatement.setString(15, info.getReceivedContentMIC());
            preparedStatement.setInt(16, info.getCompressionType());
            preparedStatement.setInt(17, info.getMessageType());
            preparedStatement.setString(18, info.getAsyncMDNURL());
            preparedStatement.setString(19, info.getSubject());
            if (info.getUserdefinedId() != null) {
                preparedStatement.setString(20, info.getUserdefinedId());
            } else {
                preparedStatement.setNull(20, Types.VARCHAR);
            }
            preparedStatement.setInt(21, info.usesTLS() ? 1 : 0);
            preparedStatement.executeUpdate();
            //insert payload and inc transaction counter
            AS2Message message = new AS2Message(info);
            this.insertPayloads(info.getMessageId(), message.getPayloads(), runtimeConnectionNoAutoCommit);
            AS2Server.incTransactionCounter();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Exception e) {
                    this.logger.severe("MessageAccessDB.initializeMessage: " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Updates an existing message in the database and returns a value != 0 if
     * this was successful - means the message id did already exist and has been
     * updated
     */
    private int updateMessage(AS2MessageInfo info, Connection runtimeConnectionNoAutoCommit) throws Exception {
        PreparedStatement preparedStatement = null;
        int updatedEntries = 0;
        try {
            preparedStatement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "UPDATE messages SET encryption=?,direction=?,rawfilename=?,receiverid=?,"
                    + "senderid=?,signature=?,state=?,syncmdn=?,headerfilename=?,useragent=?,"
                    + "rawdecryptedfilename=?,senderhost=?,"
                    + "contentmic=?,msgcompression=?,messagetype=?,asyncmdnurl=?,msgsubject=?,userdefinedid=?,"
                    + "secureconnection=?"
                    + " WHERE messageid=?");
            preparedStatement.setInt(1, info.getEncryptionType());
            preparedStatement.setInt(2, info.getDirection());
            preparedStatement.setString(3, info.getRawFilename());
            preparedStatement.setString(4, info.getReceiverId());
            preparedStatement.setString(5, info.getSenderId());
            preparedStatement.setInt(6, info.getSignType());
            preparedStatement.setInt(7, info.getState());
            preparedStatement.setInt(8, info.requestsSyncMDN() ? 1 : 0);
            preparedStatement.setString(9, info.getHeaderFilename());
            preparedStatement.setString(10, info.getUserAgent());
            preparedStatement.setString(11, info.getRawFilenameDecrypted());
            preparedStatement.setString(12, info.getSenderHost());
            preparedStatement.setString(13, info.getReceivedContentMIC());
            preparedStatement.setInt(14, info.getCompressionType());
            preparedStatement.setInt(15, info.getMessageType());
            preparedStatement.setString(16, info.getAsyncMDNURL());
            preparedStatement.setString(17, info.getSubject());
            if (info.getUserdefinedId() != null) {
                preparedStatement.setString(18, info.getUserdefinedId());
            } else {
                preparedStatement.setNull(18, Types.VARCHAR);
            }
            preparedStatement.setInt(19, info.usesTLS() ? 1 : 0);
            //condition
            preparedStatement.setString(20, info.getMessageId());
            updatedEntries = preparedStatement.executeUpdate();
            if (updatedEntries > 0) {
                //insert payload and inc transaction counter
                AS2Message message = new AS2Message(info);
                this.insertPayloads(info.getMessageId(), message.getPayloads(), runtimeConnectionNoAutoCommit);
            }
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Exception e) {
                    this.logger.severe("MessageAccessDB.updateMessage: " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
        return (updatedEntries);
    }

    /**
     * Returns a list of all messages that are older than the passed timestamp
     *
     * @param state pass -1 for any state else only messages of the requested
     * state are returned
     */
    public List<AS2MessageInfo> getMessagesSendOlderThan(long yourCurrentTimezoneTime) {
        List<AS2MessageInfo> messageList = new ArrayList<AS2MessageInfo>();
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            ResultSet result = null;
            PreparedStatement statement = null;
            try {
                String query = "SELECT * FROM messages WHERE (senddateutc IS NOT NULL) AND senddateutc < ? AND state=?";
                statement = runtimeConnectionAutoCommit.prepareStatement(query);
                statement.setTimestamp(1, new java.sql.Timestamp(yourCurrentTimezoneTime), this.calendarUTC);
                statement.setInt(2, AS2Message.STATE_PENDING);
                result = statement.executeQuery();
                while (result.next()) {
                    AS2MessageInfo info = new AS2MessageInfo();
                    info.setInitDate(result.getTimestamp("initdateutc", this.calendarUTC));
                    info.setEncryptionType(result.getInt("encryption"));
                    info.setDirection(result.getInt("direction"));
                    info.setMessageType(result.getInt("messagetype"));
                    info.setMessageId(result.getString("messageid"));
                    info.setRawFilename(result.getString("rawfilename"));
                    info.setReceiverId(result.getString("receiverid"));
                    info.setSenderId(result.getString("senderid"));
                    info.setSignType(result.getInt("signature"));
                    info.setState(result.getInt("state"));
                    info.setRequestsSyncMDN(result.getInt("syncmdn") == 1);
                    info.setHeaderFilename(result.getString("headerfilename"));
                    info.setRawFilenameDecrypted(result.getString("rawdecryptedfilename"));
                    info.setSenderHost(result.getString("senderhost"));
                    info.setUserAgent(result.getString("useragent"));
                    info.setReceivedContentMIC(result.getString("contentmic"));
                    info.setCompressionType(result.getInt("msgcompression"));
                    info.setAsyncMDNURL(result.getString("asyncmdnurl"));
                    info.setSubject(result.getString("msgsubject"));
                    info.setResendCounter(result.getInt("resendcounter"));
                    info.setUserdefinedId(result.getString("userdefinedid"));
                    info.setUsesTLS(result.getInt("secureconnection") == 1);
                    messageList.add(info);
                }
            } catch (Exception e) {
                this.logger.severe("MessageAccessDB.getMessagesSendOlderThan: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessagesSendOlderThan: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessagesSendOlderThan: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
            }
        } catch (Exception e) {
            this.logger.severe("MessageAccessDB.getMessagesSendOlderThan: " + e.getMessage());
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
     * Returns a list of all messages that are older than the passed timestamp
     *
     * @param state pass -1 for any state else only messages of the requested
     * state are returned
     */
    public List<AS2MessageInfo> getMessagesOlderThan(long initTimestamp, int state) {
        List<AS2MessageInfo> messageList = new ArrayList<AS2MessageInfo>();
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            ResultSet result = null;
            PreparedStatement statement = null;
            try {
                String query = "SELECT * FROM messages WHERE initdateutc < ?";
                if (state != -1) {
                    query = query + " AND state=" + state;
                }
                statement = runtimeConnectionAutoCommit.prepareStatement(query);
                statement.setTimestamp(1, new java.sql.Timestamp(initTimestamp), this.calendarUTC);
                result = statement.executeQuery();
                while (result.next()) {
                    AS2MessageInfo info = new AS2MessageInfo();
                    info.setInitDate(result.getTimestamp("initdateutc"));
                    info.setEncryptionType(result.getInt("encryption"));
                    info.setDirection(result.getInt("direction"));
                    info.setMessageType(result.getInt("messagetype"));
                    info.setMessageId(result.getString("messageid"));
                    info.setRawFilename(result.getString("rawfilename"));
                    info.setReceiverId(result.getString("receiverid"));
                    info.setSenderId(result.getString("senderid"));
                    info.setSignType(result.getInt("signature"));
                    info.setState(result.getInt("state"));
                    info.setRequestsSyncMDN(result.getInt("syncmdn") == 1);
                    info.setHeaderFilename(result.getString("headerfilename"));
                    info.setRawFilenameDecrypted(result.getString("rawdecryptedfilename"));
                    info.setSenderHost(result.getString("senderhost"));
                    info.setUserAgent(result.getString("useragent"));
                    info.setReceivedContentMIC(result.getString("contentmic"));
                    info.setCompressionType(result.getInt("msgcompression"));
                    info.setAsyncMDNURL(result.getString("asyncmdnurl"));
                    info.setSubject(result.getString("msgsubject"));
                    info.setResendCounter(result.getInt("resendcounter"));
                    info.setUserdefinedId(result.getString("userdefinedid"));
                    info.setUsesTLS(result.getInt("secureconnection") == 1);
                    messageList.add(info);
                }
            } catch (Exception e) {
                this.logger.severe("MessageAccessDB.getMessagesOlderThan: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessagesOlderThan: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessagesOlderThan: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
            }
        } catch (Exception e) {
            this.logger.severe("MessageAccessDB.getMessagesOlderThan: " + e.getMessage());
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
     * Returns a list of all messages that are younger than the passed timestamp
     *
     * @param state pass -1 for any state else only messages of the requested
     * state are returned
     */
    public List<AS2MessageInfo> getMessagesYoungerThan(long initTimestamp, int state) {
        List<AS2MessageInfo> messageList = new ArrayList<AS2MessageInfo>();
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            ResultSet result = null;
            PreparedStatement statement = null;
            try {
                String query = "SELECT * FROM messages WHERE initdateutc > ?";
                if (state != -1) {
                    query = query + " AND state=" + state;
                }
                statement = runtimeConnectionAutoCommit.prepareStatement(query);
                statement.setTimestamp(1, new java.sql.Timestamp(initTimestamp), this.calendarUTC);
                result = statement.executeQuery();
                while (result.next()) {
                    AS2MessageInfo info = new AS2MessageInfo();
                    info.setInitDate(result.getTimestamp("initdateutc"));
                    info.setEncryptionType(result.getInt("encryption"));
                    info.setDirection(result.getInt("direction"));
                    info.setMessageType(result.getInt("messagetype"));
                    info.setMessageId(result.getString("messageid"));
                    info.setRawFilename(result.getString("rawfilename"));
                    info.setReceiverId(result.getString("receiverid"));
                    info.setSenderId(result.getString("senderid"));
                    info.setSignType(result.getInt("signature"));
                    info.setState(result.getInt("state"));
                    info.setRequestsSyncMDN(result.getInt("syncmdn") == 1);
                    info.setHeaderFilename(result.getString("headerfilename"));
                    info.setRawFilenameDecrypted(result.getString("rawdecryptedfilename"));
                    info.setSenderHost(result.getString("senderhost"));
                    info.setUserAgent(result.getString("useragent"));
                    info.setReceivedContentMIC(result.getString("contentmic"));
                    info.setCompressionType(result.getInt("msgcompression"));
                    info.setAsyncMDNURL(result.getString("asyncmdnurl"));
                    info.setSubject(result.getString("msgsubject"));
                    info.setResendCounter(result.getInt("resendcounter"));
                    info.setUserdefinedId(result.getString("userdefinedid"));
                    info.setUsesTLS(result.getInt("secureconnection") == 1);
                    messageList.add(info);
                }
            } catch (Exception e) {
                this.logger.severe("MessageAccessDB.getMessagesYoungerThan: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessagesYoungerThan: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        this.logger.severe("MessageAccessDB.getMessagesYoungerThan: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
            }
        } catch (Exception e) {
            this.logger.severe("MessageAccessDB.getMessagesYoungerThan: " + e.getMessage());
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

}
