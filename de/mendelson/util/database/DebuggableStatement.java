//$Header: /mec_oftp2/de/mendelson/util/database/DebuggableStatement.java 6     2/02/22 10:16 Heller $
package de.mendelson.util.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Database statement that could be debugged
 *
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class DebuggableStatement implements Statement {

    private Statement statement;
    private Logger connectionLogger = null;
    private String connectionName = "Unknown connection";
    private DebuggableConnection connection;
    /**
     * Counter for the unique query ids
     */
    private final static AtomicLong currentId = new AtomicLong(0);

    public DebuggableStatement(DebuggableConnection connection, Statement statement, Logger connectionLogger, String connectionName) {
        this.connection = connection;
        this.statement = statement;
        this.connectionLogger = connectionLogger;
        if (connectionName != null) {
            this.connectionName = connectionName;
        }
    }

    public DebuggableStatement(DebuggableConnection connection, Statement statement) {
        this(connection, statement, null, null);
    }

    /**
     * ConnectionName this statement has been created from - will be "Unknown
     * connection" if none has been set
     */
    public String getConnectionName() {
        return (this.connectionName);
    }

    @Override
    public void addBatch(String str) throws SQLException {
        this.statement.addBatch(str);
    }

    @Override
    public void cancel() throws SQLException {
        this.statement.cancel();
    }

    @Override
    public void clearBatch() throws SQLException {
        this.statement.clearBatch();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.statement.clearWarnings();
    }

    @Override
    public void close() throws SQLException {
        this.statement.close();
    }

    @Override
    public boolean execute(String str) throws SQLException {
        String uniqueQueryName = null;
        if (this.connectionLogger != null) {
            uniqueQueryName = createId();
            this.connectionLogger.info("[" + this.connectionName + "] [execute query " + uniqueQueryName + "] " + str);
        }
        try {
            boolean returnValue = this.statement.execute(str);
            return (returnValue);
        } catch (SQLException e) {
            if (this.connectionLogger != null) {
                String errorMessage = "[" + this.connectionName + "] [Problem in " + uniqueQueryName + "] "
                        + e.getClass().getSimpleName()
                        + " SQLState: " + e.getSQLState()
                        + " - " + e.getMessage();
                errorMessage = errorMessage.replace("\n", "; ");
                this.connectionLogger.info(errorMessage);
            }
            throw e;
        }
    }

    @Override
    public boolean execute(String str, int i) throws SQLException {
        return (this.statement.execute(str, i));
    }

    @Override
    public boolean execute(String str, int[] values) throws SQLException {
        return (this.statement.execute(str, values));
    }

    @Override
    public boolean execute(String str, String[] str1) throws SQLException {
        return (this.statement.execute(str, str1));
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return (this.statement.executeBatch());
    }

    @Override
    public ResultSet executeQuery(String str) throws SQLException {
        String uniqueQueryName = null;
        if (this.connectionLogger != null) {
            uniqueQueryName = createId();
            this.connectionLogger.info("[" + this.connectionName + "] [execute query " + uniqueQueryName + "] " + str);
        }
        try {
            ResultSet result = this.statement.executeQuery(str);
            return (result);
        } catch (SQLException e) {
            if (this.connectionLogger != null) {
                String errorMessage = "[" + this.connectionName + "] [Problem in " + uniqueQueryName + "] "
                        + e.getClass().getSimpleName()
                        + " SQLState: " + e.getSQLState()
                        + " - " + e.getMessage();
                errorMessage = errorMessage.replace("\n", "; ");
                this.connectionLogger.info(errorMessage);
            }
            throw e;
        }
    }

    @Override
    public int executeUpdate(String str) throws SQLException {
        String uniqueQueryName = null;
        if (this.connectionLogger != null) {
            uniqueQueryName = createId();
            this.connectionLogger.info("[" + this.connectionName + "] [execute update query " + uniqueQueryName + "] " + str);
        }
        try {
            int resultInt = this.statement.executeUpdate(str);
            return (resultInt);

        } catch (SQLException e) {
            if (this.connectionLogger != null) {
                String errorMessage = "[" + this.connectionName + "] [Problem in " + uniqueQueryName + "] "
                        + e.getClass().getSimpleName()
                        + " SQLState: " + e.getSQLState()
                        + " - " + e.getMessage();
                errorMessage = errorMessage.replace("\n", "; ");
                this.connectionLogger.info(errorMessage);
            }
            throw e;
        }
    }

    @Override
    public int executeUpdate(String str, int i) throws SQLException {
        return (this.statement.executeUpdate(str, i));
    }

    @Override
    public int executeUpdate(String str, int[] values) throws SQLException {
        return (this.statement.executeUpdate(str, values));
    }

    @Override
    public int executeUpdate(String str, String[] str1) throws SQLException {
        return (this.statement.executeUpdate(str, str1));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return (this.connection);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return (this.statement.getFetchDirection());
    }

    @Override
    public int getFetchSize() throws SQLException {
        return (this.statement.getFetchSize());
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return (this.statement.getGeneratedKeys());
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return (this.statement.getMaxFieldSize());
    }

    @Override
    public int getMaxRows() throws SQLException {
        return (this.statement.getMaxRows());
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return (this.statement.getMoreResults());
    }

    @Override
    public boolean getMoreResults(int param) throws SQLException {
        return (this.statement.getMoreResults(param));
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return (this.statement.getQueryTimeout());
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return (this.statement.getResultSet());
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return (this.statement.getResultSetConcurrency());
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return (this.statement.getResultSetHoldability());
    }

    @Override
    public int getResultSetType() throws SQLException {
        return (this.statement.getResultSetType());
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return (this.statement.getUpdateCount());
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return (this.statement.getWarnings());
    }

    @Override
    public void setCursorName(String str) throws SQLException {
        this.statement.setCursorName(str);
    }

    @Override
    public void setEscapeProcessing(boolean param) throws SQLException {
        this.statement.setEscapeProcessing(param);
    }

    @Override
    public void setFetchDirection(int param) throws SQLException {
        this.statement.setFetchDirection(param);
    }

    @Override
    public void setFetchSize(int param) throws SQLException {
        this.statement.setFetchSize(param);
    }

    @Override
    public void setMaxFieldSize(int param) throws SQLException {
        this.statement.setMaxFieldSize(param);
    }

    @Override
    public void setMaxRows(int param) throws SQLException {
        this.statement.setMaxRows(param);
    }

    @Override
    public void setQueryTimeout(int param) throws SQLException {
        this.statement.setQueryTimeout(param);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return (this.statement.isClosed());
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        this.statement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return (this.statement.isPoolable());
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (this.statement.unwrap(iface));
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (this.statement.isWrapperFor(iface));
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Creates a new id in the format yyyyMMddHHmm-nn
     */
    private static String createId() {
        StringBuilder idBuffer = new StringBuilder()
                .append("STATEMENT-")
                .append(currentId.getAndIncrement());
        return (idBuffer.toString());
    }
}
