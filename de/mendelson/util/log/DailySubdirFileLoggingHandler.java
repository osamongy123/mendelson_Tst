//$Header: /as2/de/mendelson/util/log/DailySubdirFileLoggingHandler.java 15    1/06/22 14:49 Heller $
package de.mendelson.util.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * Handler to log logger data to a file in a daily subdirectory. A log dir and a
 * log file name could be passed to this class, the log will be written to
 * logDir/yyMMdd/logfilename Sample: logger.addHandler( new
 * DailySubdirFileLoggingHandler(Paths.get("mylogdir"), "mylogfile.log") );
 *
 * @author S.Heller
 * @version $Revision: 15 $
 */
public class DailySubdirFileLoggingHandler extends Handler {

    private boolean doneHeader;
    private BufferedWriter writer = null;
    private final DateFormat logDateFormat = new SimpleDateFormat("yyyyMMdd");
    private final Path logDir;
    private final String logfileName;
    //stores the actual log file name that is used to write log to
    private String actualLogFilename = null;
    private long maxLogFileSize = -1;

    public DailySubdirFileLoggingHandler(Path logDir, String logfileName, LogFormatter logFormatter) {
        this.logDir = logDir;
        this.logfileName = logfileName;
        this.setFormatter(logFormatter);
    }
    /**
     * Sets the size of a single log file in bytes
     */
    public void setMaxLogFileSize(long maxLogFileSize) {
        this.maxLogFileSize = maxLogFileSize;
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
        String msg;
        int rawMessageLength = 0;
        try {
            msg = this.getFormatter().format(record);
            String rawMessage = this.getFormatter().formatMessage(record);
            if (rawMessage != null) {
                rawMessageLength = rawMessage.length();
            }
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
        try {
            if (!doneHeader) {
                this.logMessage(record.getLevel(), this.getFormatter().getHead(this), rawMessageLength);
                doneHeader = true;
            }
            this.logMessage(record.getLevel(), msg, rawMessageLength);
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

    private Path getFullLogDir() {
        StringBuilder path = new StringBuilder();
        path.append(this.logDir.toAbsolutePath().toString());
        path.append(FileSystems.getDefault().getSeparator());
        path.append(this.logDateFormat.format(new Date()));
        return (Paths.get(path.toString()));
    }

    private String generateNewLogFileName(Path fullLogDir) {
        Path newLogFile = Paths.get(fullLogDir.toAbsolutePath().toString(),
                this.logfileName);
        if (this.maxLogFileSize == -1) {
            return (newLogFile.toAbsolutePath().toString());
        }
        int counter = 1;
        try {
            while (Files.exists(newLogFile) && Files.size(newLogFile) > this.maxLogFileSize) {
                newLogFile = Paths.get(fullLogDir.toAbsolutePath().toString(),                        
                        this.logfileName + "." + counter);
                counter++;
            }
        } catch (IOException e) {
            //nop
        }
        return (newLogFile.toAbsolutePath().toString());
    }

    /**
     * Finally logs the passed message to the text component and sets the canvas
     * pos
     */
    private synchronized void logMessage(Level level, String message, int rawMessageLength) {
        Path fullLogDir = this.getFullLogDir();
        String newLogFilename = this.generateNewLogFileName(fullLogDir);
        //check if the loggers output stream is still valid        
        if (this.writer == null || this.actualLogFilename == null || !newLogFilename.equals(this.actualLogFilename)
                || !Files.exists(Paths.get(newLogFilename))) {
            if (this.writer != null) {
                try {
                    //close existing writer
                    this.writer.flush();
                    this.writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!Files.exists(fullLogDir)) {
                try {
                    Files.createDirectories(fullLogDir);
                } catch (IOException e) {
                    //nop
                }
            }
            try {
                //open a new log file - append to existing and create if it does not exist so far
                this.writer = Files.newBufferedWriter(Paths.get(newLogFilename), 
                        StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                this.actualLogFilename = newLogFilename;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            this.writer.write(message);
            this.writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static final void main(String[] args) {
//        Logger fileLogger = Logger.getLogger("test");
//        fileLogger.setUseParentHandlers(false);
//        DailySubdirFileLoggingHandler logHandler = new DailySubdirFileLoggingHandler(new File("c:/temp"), "serverlog.log");
//        logHandler.setMaxLogFileSize(100);
//        fileLogger.addHandler(logHandler);
//        fileLogger.setLevel(Level.ALL);
//        for (int i = 0; i < 100; i++) {
//            fileLogger.log(Level.INFO, "This is a test " + i);
//        }
//    }
}
