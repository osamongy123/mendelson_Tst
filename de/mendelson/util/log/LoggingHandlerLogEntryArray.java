//$Header: /mec_as4/de/mendelson/util/log/LoggingHandlerLogEntryArray.java 7     7.01.21 13:49 Heller $
package de.mendelson.util.log;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Handler to log output to a StringBuilder
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class LoggingHandlerLogEntryArray extends Handler {

    private List<LogEntry> out;

    public LoggingHandlerLogEntryArray(List<LogEntry> out) {
        this.out = out;
    }

    /**
     * Set (or change) the character encoding used by this <tt>Handler</tt>.
     * <p>
     * The encoding should be set before any <tt>LogRecords</tt> are written to
     * the <tt>Handler</tt>.
     *
     * @param encoding The name of a supported character encoding. May be null,
     * to indicate the default platform encoding.
     * @exception SecurityException if a security manager exists and if the
     * caller does not have <tt>LoggingPermission("control")</tt>.
     * @exception UnsupportedEncodingException if the named encoding is not
     * supported.
     */
    @Override
    public void setEncoding(String encoding)
            throws SecurityException, java.io.UnsupportedEncodingException {
        super.setEncoding(encoding);
    }

    /**
     * Format and publish a LogRecord.
     *
     * @param record description of the log event
     */
    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        try {
            this.logMessage(record.getLevel(), record.getMillis(), record.getMessage(),
                    record.getParameters());
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
        }
    }

    /**
     * Check if this Handler would actually log a given LogRecord, depending of
     * the log level
     *
     * @param record a LogRecord
     * @return true if the LogRecord would be logged.
     *
     */
    @Override
    public boolean isLoggable(LogRecord record) {
        return super.isLoggable(record);
    }

    /**
     * Flush any buffered messages.
     */
    @Override
    public synchronized void flush() {
    }

    /**
     * Just flushes the current message
     */
    @Override
    public synchronized void close() throws SecurityException {
        this.flush();
    }

    /**
     * Finally logs the passed message
     */
    private synchronized void logMessage(Level level, long millis, String message, Object[] parameter) {
        LogEntry entry = new LogEntry(level, millis, message);
        this.out.add( entry );
    }

    public static class LogEntry implements Serializable{

        public static final long serialVersionUID = 1L;
        private Level level;
        private long millis;
        private String message;

        public LogEntry(Level level, long millis, String message) {
            this.level = level;
            this.millis = millis;
            this.message = message;
        }

        /**
         * @return the level
         */
        public Level getLevel() {
            return level;
        }

        /**
         * @param level the level to set
         */
        public void setLevel(Level level) {
            this.level = level;
        }

        /**
         * @return the millis
         */
        public long getMillis() {
            return millis;
        }

        /**
         * @param millis the millis to set
         */
        public void setMillis(long millis) {
            this.millis = millis;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }

        
        
    }

}
