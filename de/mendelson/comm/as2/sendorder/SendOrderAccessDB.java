//$Header: /as2/de/mendelson/comm/as2/sendorder/SendOrderAccessDB.java 28    9/12/22 13:44 Heller $
package de.mendelson.comm.as2.sendorder;

import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Accesses the queue for the internal send orders
 *
 * @author S.Heller
 * @version $Revision: 28 $
 */
public class SendOrderAccessDB {

    private final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private IDBDriverManager dbDriverManager;

    public SendOrderAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Deletes an entry in the database that contains a send order. Opens a new
     * connection to the DB
     *
     */
    public void delete(int dbId) {
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionName = "SendOrder_delete";
        Statement transactionStatement = null;
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockDELETE(transactionStatement,
                    new String[]{
                        "sendorder"
                    });
            this.delete(dbId, runtimeConnectionNoAutoCommit);
            this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
        } catch (Exception e) {
            try {
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
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

    }

    /**
     * Deletes an entry in the database that contains a send order
     *
     * @param dbId
     * @param connection A connection must be passed because it is possible to
     * run this in a transactional context.
     */
    private void delete(int dbId, Connection runtimeConnectionNoAutoCommit) throws Exception {
        if (dbId == -1) {
            return;
        }
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = runtimeConnectionNoAutoCommit.prepareStatement(
                    "DELETE FROM sendorder WHERE id=?");
            preparedStatement.setInt(1, dbId);
            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Reschedules an existing order
     */
    public void rescheduleOrder(SendOrder order, long nextExecutionTime) {
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionName = "SendOrder_reschedule";
        Statement transactionStatement = null;
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(
                    IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                    new String[]{
                        "sendorder"
                    });
            PreparedStatement statementUpdate = null;
            try {
                statementUpdate = runtimeConnectionNoAutoCommit.prepareStatement(
                        "UPDATE sendorder SET nextexecutiontime=?,sendorder=?,orderstate=? WHERE id=?");
                statementUpdate.setLong(1, nextExecutionTime);
                this.dbDriverManager.setObjectParameterAsJavaObject(statementUpdate, 2, order);
                statementUpdate.setInt(3, SendOrder.STATE_WAITING);
                //condition
                statementUpdate.setInt(4, order.getDbId());
                statementUpdate.executeUpdate();
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Throwable e) {
                try {
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statementUpdate);
            } finally {
                if (statementUpdate != null) {
                    try {
                        statementUpdate.close();
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
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
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Adds a new send order to the outbound queue
     */
    public void add(SendOrder order) {
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionName = "SendOrder_add";
        Statement transactionStatement = null;
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(
                    IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                    new String[]{
                        "sendorder"
                    });
            PreparedStatement statement = null;
            try {
                statement = runtimeConnectionNoAutoCommit.prepareStatement(
                        "INSERT INTO sendorder(scheduletime,nextexecutiontime,sendorder,orderstate)"
                        + "VALUES(?,?,?,?)");
                statement.setLong(1, System.currentTimeMillis());
                //execute as soon as possible
                statement.setLong(2, System.currentTimeMillis());
                this.dbDriverManager.setObjectParameterAsJavaObject(statement, 3, order);
                statement.setInt(4, SendOrder.STATE_WAITING);
                statement.executeUpdate();
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Throwable e) {
                try {
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
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
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * On a server start all the available transaction should be reset to the
     * wait state
     */
    public void resetAllToWaiting() {
        PreparedStatement statementUpdate = null;
        Connection runtimeConnectionNoAutoCommit = null;
        String transactionName = "SendOrder_resetAllToWaiting";
        Statement transactionStatement = null;
        try {
            runtimeConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                    new String[]{
                        "sendorder"
                    });
            statementUpdate = runtimeConnectionNoAutoCommit.prepareStatement("UPDATE sendorder SET orderstate=?");
            statementUpdate.setInt(1, SendOrder.STATE_WAITING);
            statementUpdate.executeUpdate();
            this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
        } catch (Exception e) {
            try {
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("SendOrderAccessDB.resetAllToWait: "
                    + "[" + e.getClass().getName() + "] " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statementUpdate);
        } finally {
            if (statementUpdate != null) {
                try {
                    statementUpdate.close();
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
     * Sets a new state to a send order
     */
    private void setState(int id, int orderState, Connection runtimeConnectionNoAutoCommit) throws Exception {
        PreparedStatement statementUpdate = null;
        try {
            statementUpdate = runtimeConnectionNoAutoCommit.prepareStatement(
                    "UPDATE sendorder SET orderstate=? WHERE id=?");
            statementUpdate.setInt(1, orderState);
            statementUpdate.setLong(2, id);
            statementUpdate.executeUpdate();
        } finally {
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
     * Returns the next n scheduled orders or an empty list if none exists. This
     * reads orders from the database queue and deletes them once picked up.
     * This is a transactional operation, the database table is locked
     */
    public List<SendOrder> getNext(int maxCount) {
        final List<SendOrder> sendOrderList = new ArrayList<SendOrder>();
        Statement transactionStatement = null;
        final String transactionName = "SendOrder_next";
        Connection runtimeConnectionNoAutoCommit = null;
        try {
            runtimeConnectionNoAutoCommit = dbDriverManager.getConnectionWithoutErrorHandling(
                    IDBDriverManager.DB_RUNTIME);
            runtimeConnectionNoAutoCommit.setAutoCommit(false);
            PreparedStatement preparedStatementSelect = null;
            ResultSet result = null;
            int count = 0;
            try {
                transactionStatement = runtimeConnectionNoAutoCommit.createStatement();
                dbDriverManager.startTransaction(transactionStatement, transactionName);
                dbDriverManager.setTableLockDELETE(transactionStatement,
                        new String[]{
                            "sendorder"
                        });
                preparedStatementSelect = runtimeConnectionNoAutoCommit.prepareStatement(
                        "SELECT * FROM sendorder WHERE orderstate=? "
                        + "AND nextexecutiontime <=? ORDER BY nextexecutiontime");
                preparedStatementSelect.setInt(1, SendOrder.STATE_WAITING);
                preparedStatementSelect.setLong(2, System.currentTimeMillis());
                result = preparedStatementSelect.executeQuery();
                while (result.next() && count < maxCount) {
                    Object orderObject = null;
                    try {
                        orderObject = this.dbDriverManager.readObjectStoredAsJavaObject(result, "sendorder");
                    } catch (Throwable invalidClassExeption) {
                        //nop
                    }
                    SendOrder order = null;
                    if (orderObject != null) {
                        if (orderObject instanceof SendOrder) {
                            //this happens if you read the serialized object from HSQLDB
                            order = (SendOrder) orderObject;
                            int id = result.getInt("id");
                            order.setDbId(id);
                            //do not let it pick up by any other node/process now: Set the state to processing
                            this.setState(id, SendOrder.STATE_PROCESSING, runtimeConnectionNoAutoCommit);
                            sendOrderList.add(order);
                            count++;
                        } else if (orderObject instanceof byte[]) {
                            //this happens if you read the serialized object from mySQL
                            ByteArrayInputStream memIn = new ByteArrayInputStream((byte[]) orderObject);
                            ObjectInput in = new ObjectInputStream(memIn);
                            SendOrder sendOrderObj = (SendOrder) in.readObject();
                            int id = result.getInt("id");
                            sendOrderObj.setDbId(id);
                            //do not let it pick up by any other node/process now: Set the state to processing
                            this.setState(id, SendOrder.STATE_PROCESSING, runtimeConnectionNoAutoCommit);
                            sendOrderList.add(sendOrderObj);
                            count++;
                        }
                    } else {
                        //delete the entry from the database, its from an older version or an invalid entry
                        int id = result.getInt("id");
                        this.delete(id, runtimeConnectionNoAutoCommit);
                        break;
                    }
                }
                //all ok - finish transaction
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Throwable e) {
                try {
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, preparedStatementSelect);
                //return empty list
                return (new ArrayList<SendOrder>());
            } finally {
                if (preparedStatementSelect != null) {
                    try {
                        preparedStatementSelect.close();
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
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
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
        return (sendOrderList);
    }
}
