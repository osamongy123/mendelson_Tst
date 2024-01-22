//$Header: /as4/de/mendelson/util/log/ConsoleHandlerStdout.java 2     8/04/22 10:35 Heller $
package de.mendelson.util.log;

import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.LogRecord;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Mainly a copy of the ConsoleHandler - but this writes to stdout where the
 * ConsoleHandler writes to stderr. This class makes sense if you can only view
 * the stdout stream in the console - like this is realized in the install4j
 * installers with the option -console
 *
 * @author S.Heller
 */
public class ConsoleHandlerStdout extends StreamHandler {

    public ConsoleHandlerStdout() {
        // configure with specific defaults for ConsoleHandler
        super(System.out, new SimpleFormatter());
    }
    
    /**
     * Publish a {@code LogRecord}.
     * <p>
     * The logging request was made initially to a {@code Logger} object,
     * which initialized the {@code LogRecord} and forwarded it here.
     *
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    /**
     * Override {@code StreamHandler.close} to do a flush but not
     * to close the output stream.  That is, we do <b>not</b>
     * close {@code System.err}.
     */
    @Override
    public void close() {
        flush();
    }
}
