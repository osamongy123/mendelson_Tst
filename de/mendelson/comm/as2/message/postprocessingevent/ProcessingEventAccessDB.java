//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ProcessingEventAccessDB.java 11    9/11/22 15:15 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.Base64;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.hsqldb.types.Types;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Access the event queue for the partner related event processing (post processing)
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class ProcessingEventAccessDB {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private IDBDriverManager dbDriverManager = null;

    /**
     * Creates new message I/O log and connects to localhost
     *
     * @param host host to connect to
     */
    public ProcessingEventAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Returns the next event that should be executed in the processing queue of
     * NULL if none exists
     * If an event is returned it is deleted in the queue
     */
    public ProcessingEvent getNextEventToExecuteAsTransaction(Connection runtimeConnectionNoAutoCommit) {
        ResultSet result = null;
        PreparedStatement statementSelect = null;
        PreparedStatement statementDelete = null;
        PreparedStatement debugStatement = null;
        ProcessingEvent event = null;
        Statement transactionStatement = null;
        String transactionName = "ProcessingEvent_next";
        try {
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            //begin transaction - lock database table
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockExclusive(transactionStatement, 
                    new String[]{"processingeventqueue"});
            statementSelect = runtimeConnectionNoAutoCommit.prepareStatement(
                    "SELECT * FROM processingeventqueue WHERE initdate < ? ORDER BY initdate ASC");
            statementSelect.setLong(1, System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(15));
            debugStatement = statementSelect;
            result = statementSelect.executeQuery();
            if (result.next()) {
                int eventType = result.getInt("eventtype");                
                int processType = result.getInt("processtype");    
                long initDate = result.getLong("initdate");
                List<String> parameter = this.deserializeList(result.getString("parameterlist"));
                String relatedMessageId = result.getString("messageid");
                String relatedMDNId = result.getString("mdnid");
                if( result.wasNull()){
                    relatedMDNId = null;
                }
                event = new ProcessingEvent(eventType, processType, relatedMessageId, relatedMDNId, parameter, initDate);
                statementDelete = runtimeConnectionNoAutoCommit.prepareStatement("DELETE FROM processingeventqueue WHERE messageid=?");
                debugStatement = statementDelete;
                statementDelete.setString(1, relatedMessageId);
                statementDelete.executeUpdate();
            }
            //all ok - finish transaction
            this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            return (event);
        } catch (Exception e) {
            try {
                //cancel transaction - something went wrong
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                this.logger.severe("ProcessingEventAccessDB.getNext: " + ex.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("ProcessingEventAccessDB.getNextEventToExecute: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, debugStatement);            
            return (null);
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
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statementSelect);
                }
            }
            if (statementDelete != null) {
                try {
                    statementDelete.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statementDelete);
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
     * Adds an Event to the database
     */
    public void addEventToExecute(ProcessingEvent event) {
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionName = "PostProcessing_addEvent";
        PreparedStatement statement = null;
        Statement transactionStatement = null;
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            statement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO processingeventqueue("
                    + "eventtype,processtype,initdate,parameterlist,messageid,mdnid)"
                    + "VALUES(?,?,?,?,?,?)");
            statement.setInt(1, event.getEventType());
            statement.setInt(2, event.getProcessType());
            statement.setLong(3, event.getInitDate());
            statement.setString(4, this.serializeList(event.getParameter()));
            statement.setString(5, event.getMessageId());
            if( event.getMDNId() == null){
                statement.setNull(6, Types.VARCHAR);
            }else{
                statement.setString(6, event.getMDNId());
            }
            statement.executeUpdate();
            this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
        } catch (Exception e) {
            try {
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("ProcessingEventAccessDB.addEventToExecute: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    this.logger.severe("ProcessingEventAccessDB.addEventToExecute: " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
                }
            }
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
    }

    /**Serializes a list to a single string
     * 
     * @param list
     * @return 
     */
    private String serializeList( List<String> list){
        StringBuilder builder = new StringBuilder();
        for( String entry:list){
            if( builder.length() > 0 ){
                builder.append( " ");
            }
            builder.append(Base64.encode(entry.getBytes(StandardCharsets.UTF_8)));
        }
        return( builder.toString());
    }
    
    /**Serializes a list to a single string
     * 
     * @param list
     * @return 
     */
    private List<String> deserializeList( String entry){
        List<String> list = new ArrayList<String>();
        if( entry == null ){
            return( list );
        }
        String[] entryArray = entry.split(" ");
        for( String singleEntry:entryArray){
            byte[] decodedBytes = Base64.decode(singleEntry);
            //base64 decoding failed...return empty parameters
            if( decodedBytes == null ){
                return( new ArrayList<String>());
            }
            list.add( new String(decodedBytes, StandardCharsets.UTF_8));
        }
        return( list );
    }
    
}
