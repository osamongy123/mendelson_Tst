//$Header: /as2/de/mendelson/util/clientserver/connectiontest/clientserver/ConnectionTestResponse.java 3     4/06/18 12:22p Heller $
package de.mendelson.util.clientserver.connectiontest.clientserver;

import de.mendelson.util.clientserver.connectiontest.ConnectionTestResult;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.log.LoggingHandlerLogEntryArray;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Msg for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ConnectionTestResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private ConnectionTestResult result = null;
    private List<LoggingHandlerLogEntryArray.LogEntry> logEntries = new ArrayList<LoggingHandlerLogEntryArray.LogEntry>();

    public ConnectionTestResponse(ConnectionTestRequest request) {
        super(request);
    }

    /**
     * @return the logEntries
     */
    public List<LoggingHandlerLogEntryArray.LogEntry> getLogEntries() {
        return logEntries;
    }

    /**
     * @param logEntries the logEntries to set
     */
    public void addLogEntries(List<LoggingHandlerLogEntryArray.LogEntry> logEntries) {
        this.logEntries.addAll(logEntries);
    }

    @Override
    public String toString() {
        return ("Connection test response");
    }

    /**
     * @return the result
     */
    public ConnectionTestResult getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(ConnectionTestResult result) {
        this.result = result;
    }

}
