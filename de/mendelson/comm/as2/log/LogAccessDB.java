//$Header: /as2/de/mendelson/comm/as2/log/LogAccessDB.java 45    9/11/22 15:14 Heller $
package de.mendelson.comm.as2.log;

import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Access to the AS2 log that stores log messages for every transaction
 *
 * @author S.Heller
 * @version $Revision: 45 $
 */
public class LogAccessDB {

    private final int LEVEL_FINE = 3;
    private final int LEVEL_SEVERE = 2;
    private final int LEVEL_WARNING = 1;
    private final int LEVEL_INFO = 0;
    private final IDBDriverManager dbDriverManager;

    /**
     * Store the timestamps in the database in UTC to make the database portable
     * and to prevent daylight saving problems
     */
    private final Calendar calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    /**
     * @param host host to connect to
     */
    public LogAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    private int convertLevel(Level level) {
        if (level.equals(Level.WARNING)) {
            return (this.LEVEL_WARNING);
        }
        if (level.equals(Level.SEVERE)) {
            return (this.LEVEL_SEVERE);
        }
        if (level.equals(Level.FINE)) {
            return (this.LEVEL_FINE);
        }
        return (this.LEVEL_INFO);
    }

    private Level convertLevel(int level) {
        if (level == this.LEVEL_WARNING) {
            return (Level.WARNING);
        }
        if (level == this.LEVEL_SEVERE) {
            return (Level.SEVERE);
        }
        if (level == this.LEVEL_FINE) {
            return (Level.FINE);
        }
        return (Level.INFO);
    }

    /**
     * Adds a log line to the db - opens a new database connection first
     */
    public void logAsTransaction(Level level, long millis, String logMessage, String messageId) {
        if (logMessage == null) {
            return;
        }
        Connection runtimeConnectionNoAutoCommit = null;
        try {
            runtimeConnectionNoAutoCommit
                    = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            this.logAsTransaction(runtimeConnectionNoAutoCommit, level, millis, logMessage, messageId);
        } catch (SQLException e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } catch (Throwable e) {
            SystemEventManagerImplAS2.systemFailure(e);
        } finally {
            if (runtimeConnectionNoAutoCommit != null) {
                try {
                    runtimeConnectionNoAutoCommit.close();
                } catch (Throwable e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
    }

    /**
     * Adds a single log line to the db
     */
    private void logAsTransaction(Connection runtimeConnectionNoAutoCommit,
            Level level, long millis, String logMessage, String messageId) {
        if (logMessage == null) {
            return;
        }
        PreparedStatement statement = null;
        Statement transactionStatement = null;
        String transactionName = "LogAccessDB_logAsTransaction";
        try {
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO messagelog(timestamputc,messageid,loglevel,details)VALUES(?,?,?,?)");
            statement.setTimestamp(1, new Timestamp(millis), this.calendarUTC);
            statement.setString(2, messageId);
            statement.setInt(3, this.convertLevel(level));
            this.dbDriverManager.setTextParameterAsJavaObject(statement, 4, logMessage);
            statement.executeUpdate();
            this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
        } catch (SQLIntegrityConstraintViolationException e) {
            String errorMessage = "LogAccessDB.log "
                    + "(" + e.getClass().getSimpleName() + "): "
                    + " The system tries to store a log entry for the message id \"" + messageId
                    + "\", but this message seems not to exist in the system.\n"
                    + "The reason might be an unreferenced MDN or a bad inbound AS2 message structure.";
            SystemEvent event = new SystemEvent(SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_TRANSACTION, SystemEvent.TYPE_TRANSACTION_ANY);
            event.setBody(errorMessage + "\n\nLog message: \"" + logMessage + "\"");
            event.setSubject("Unreferenced MDN or bad message structure");
            SystemEventManagerImplAS2.newEvent(event);
            try {
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex);
            }
        } catch (Exception e) {
            try {
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex);
            }
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

    /**
     * Returns the whole log of a single instance
     */
    public List<LogEntry> getLog(String messageId) {
        List<LogEntry> list = new ArrayList<LogEntry>();
        Connection runtimeConnectionAutoCommit = null;
        try {
            runtimeConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            PreparedStatement statement = null;
            try {
                statement = runtimeConnectionAutoCommit.prepareStatement("SELECT * FROM messagelog WHERE messageid=? ORDER BY timestamputc");
                statement.setString(1, messageId);
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    LogEntry entry = new LogEntry();
                    entry.setLevel(this.convertLevel(result.getInt("loglevel")));
                    String detailsStr = this.dbDriverManager.readTextStoredAsJavaObject(result, "details");
                    if (detailsStr != null) {
                        entry.setMessage(detailsStr);
                    }
                    entry.setMessageId(messageId);
                    entry.setMillis(result.getTimestamp("timestamputc", this.calendarUTC).getTime());
                    list.add(entry);
                }
            } catch (Exception e) {
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
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
        return (list);
    }

    /**
     * Deletes all information from the table messagelog regarding the passed
     * message instance
     */
    public void deleteMessageLog(List<String> messageIds, Connection runtimeConnectionNoAutoCommit) throws Exception {
        PreparedStatement statement = null;
        try {
            if (messageIds != null && !messageIds.isEmpty()) {
                StringBuilder deleteQuery = new StringBuilder("DELETE FROM messagelog WHERE messageid IN (");
                for (int i = 0; i < messageIds.size(); i++) {
                    if (i > 0) {
                        deleteQuery.append(",");
                    }
                    deleteQuery.append("?");
                }
                deleteQuery.append(")");
                statement = runtimeConnectionNoAutoCommit.prepareStatement(deleteQuery.toString());
                for (int i = 0; i < messageIds.size(); i++) {
                    statement.setString(i + 1, messageIds.get(i));
                }
            } else {
                statement = runtimeConnectionNoAutoCommit.prepareStatement("DELETE FROM messagelog WHERE messageid IS NULL");
            }
            statement.execute();
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
     * Deletes all information from the table messagelog regarding the passed
     * message instance - opens a new db connection - transactional
     */
    public void deleteMessageLog(List<String> messageIds) {
        Statement statement = null;
        //a new connection to the database is required because the message storage contains several tables and all this has to be transactional
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionname = "Message_deleteLog";
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            statement = runtimeConnectionNoAutoCommit.createStatement();
            //start transaction
            this.dbDriverManager.startTransaction(statement, transactionname);
            this.dbDriverManager.setTableLockDELETE(statement,
                    new String[]{
                        "messagelog"});
            this.deleteMessageLog(messageIds, runtimeConnectionNoAutoCommit);
            //all ok - finish transaction and release all locks
            this.dbDriverManager.commitTransaction(statement, transactionname);
        } catch (Exception e) {
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
                    //nop
                }
            }
        }
    }

}
