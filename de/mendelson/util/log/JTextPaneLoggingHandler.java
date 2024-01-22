//$Header: /as2/de/mendelson/util/log/JTextPaneLoggingHandler.java 36    24/08/22 12:56 Heller $
package de.mendelson.util.log;

import de.mendelson.util.ColorUtil;
import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Handler to log logger data to a swing text component
 *
 * @author S.Heller
 * @version $Revision: 36 $
 */
public class JTextPaneLoggingHandler extends Handler {

    /**
     * The max number of bytes that are displayed. If the content exceeds this
     * there is data removed at the start
     */
    private final long MAX_BUFFER_SIZE = 30000;
    private JTextPane jTextPane = null;
    private final Style currentStyle;
    private boolean bold = false;
    private boolean underline = false;
    private boolean italic = false;
    //allows to enable/disable the logging output
    private boolean enabled = true;
    private Color defaultForegroundColor = UIManager.getColor("TextPane.foreground");
    private Color defaultBackgroundColor = UIManager.getColor("TextPane.background");
    private final Map<Color, Color> highContrastColorCache = new ConcurrentHashMap<Color, Color>();
    private final LogFormatter formatter;

    /**
     * Stores the logging colors for the logging levels as ANSI
     */
    private final Map<Level, String> colorMapANSI = new ConcurrentHashMap<Level, String>();

    public JTextPaneLoggingHandler(JTextPane jTextPane, LogFormatter formatter) {
        this.setFormatter(formatter);
        this.formatter = formatter;
        //set default colors, these could be overwritten using the setColor method
        this.setColor(Level.WARNING, ANSI.COLOR_SYSTEM_BLUE);
        this.setColor(Level.SEVERE, ANSI.COLOR_SYSTEM_RED);
        this.setColor(Level.INFO, ANSI.COLOR_SYSTEM_BLACK);
        this.setColor(Level.FINE, ANSI.COLOR_SYSTEM_GREEN);
        this.setColor(Level.FINER, ANSI.COLOR_SYSTEM_GREY);
        this.setColor(Level.FINEST, ANSI.COLOR_SYSTEM_GREY_BRIGHT);        
        this.jTextPane = jTextPane;
        StyleContext context = StyleContext.getDefaultStyleContext();
        this.currentStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
        if (this.defaultBackgroundColor == null) {
            this.defaultBackgroundColor = jTextPane.getBackground();
        }
        if (this.defaultBackgroundColor == null) {
            this.defaultBackgroundColor = Color.WHITE;
        }
        if (this.defaultForegroundColor == null) {
            this.defaultForegroundColor = jTextPane.getForeground();
        }
        if (this.defaultForegroundColor == null) {
            this.defaultForegroundColor
                    = ColorUtil.getBestContrastColorAroundForeground(
                            this.defaultBackgroundColor, Color.BLACK);
        }
        this.resetStyle();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets a color for the log levels. The color is a constant of the class
     * ANSI
     *
     */
    public void setColor(Level loglevel, final String ANSI_COLOR) {
        this.colorMapANSI.put(loglevel, ANSI_COLOR);
        this.formatter.setColor(loglevel, ANSI_COLOR);
    }

    /**Takes the colors from the given logging handler
     * - useful if there are multiple logging handlers in the product that should use always
     * the same colors
     */
    public void setColorsFrom( JTextPaneLoggingHandler otherHandler ){
        this.setColor(Level.CONFIG, otherHandler.getColorAsANSI(Level.CONFIG));
        this.setColor(Level.FINE, otherHandler.getColorAsANSI(Level.FINE));
        this.setColor(Level.FINER, otherHandler.getColorAsANSI(Level.FINER));
        this.setColor(Level.FINEST, otherHandler.getColorAsANSI(Level.FINEST));
        this.setColor(Level.INFO, otherHandler.getColorAsANSI(Level.INFO));
        this.setColor(Level.SEVERE, otherHandler.getColorAsANSI(Level.SEVERE));
        this.setColor(Level.WARNING, otherHandler.getColorAsANSI(Level.WARNING));
    }
    
    
    /**
     * Returns the current set color for the passed log level. May return null
     * if no color is defined for the passed level
     */
    public String getColorAsANSI(Level loglevel) {
        if (colorMapANSI.containsKey(loglevel)) {
            return (this.colorMapANSI.get(loglevel));
        }
        return (null);
    }

    private void performANSICommand(String command) {
        switch (command) {
            case ANSI.BOLD:
                this.toggleStyleBold();
                break;
            case ANSI.ITALIC:
                this.toggleStyleItalic();
                break;
            case ANSI.UNDERLINE:
                this.toggleStyleUnderline();
                break;
            case ANSI.RESET:
                this.resetStyle();
                break;
            default:
                if (ANSI.isColorSequence(command)) {
                    this.setStyleForegroundAutoCorrected(command);
                }
                break;
        }
    }

    /**
     * Appends a message to the output area. ANSI codes will be decoded first.
     */
    public void messageDecode(String message) {
        // quick checks to speed things up
        if (message.contains(ANSI.CSI)) {
            boolean inSequence = false;
            int pos = 0;
            while (true) {
                if (!inSequence) {
                    int nextSequenceStartIndex = message.indexOf(ANSI.CSI, pos);
                    if (nextSequenceStartIndex < 0) {
                        //there is no other sequence
                        this.messageDecodeWrite(new StringBuilder(message.substring(pos)));
                        break;
                    } else {
                        //there is another sequence
                        this.messageDecodeWrite(new StringBuilder(message.substring(pos, nextSequenceStartIndex)));
                        pos = nextSequenceStartIndex;
                        inSequence = true;
                    }
                } else {
                    int nextSequenceEndIndex = message.indexOf('m', pos);
                    if (nextSequenceEndIndex <= 0) {
                        //looks like an error, the sequence has no end marker - just bail out
                        break;
                    } else {
                        if( nextSequenceEndIndex == message.length()-1 ){
                            this.performANSICommand(message.substring(pos));
                            inSequence = false;
                            break;
                        }else{
                            this.performANSICommand(message.substring(pos, nextSequenceEndIndex+1));
                            pos = nextSequenceEndIndex+1;
                            inSequence = false;
                        }
                    }
                }
            }
        } else {            
            messageDecodeWrite(new StringBuilder(message));
        }
    }

    /**
     * Appends the current buffer's contents to the output area while decoding a
     * message. The StringBuffer will be setLength(0) afterwards.
     */
    private void messageDecodeWrite(StringBuilder buffer) {
        final StyledDocument document = (StyledDocument) this.jTextPane.getDocument();
        synchronized (document) {
            try {
                long documentLength = document.getLength();
                long oversize = (documentLength + buffer.length()) - this.MAX_BUFFER_SIZE;
                if (oversize > 0) {
                    if (documentLength >= oversize) {
                        document.remove(0, (int) oversize);
                    } else {
                        document.remove(0, (int) documentLength);
                        if (buffer.length() > this.MAX_BUFFER_SIZE) {
                            buffer.delete(0, (int) (buffer.length() - this.MAX_BUFFER_SIZE));
                        }
                    }
                }
                document.insertString(document.getLength(), buffer.toString(), this.currentStyle);

            } catch (Throwable ex) {
                if (ex instanceof Exception) {
                    reportError(null, (Exception) ex, ErrorManager.WRITE_FAILURE);
                }
            }
        }
        buffer.setLength(0);
        //scroll to the last line, enqueue into the swing paint queue
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (document) {
                        int documentLength = document.getLength();
                        if (documentLength <= MAX_BUFFER_SIZE) {
                            JTextPaneLoggingHandler.this.jTextPane.setCaretPosition(documentLength);
                        }
                    }
                } catch (Error e) {
                    //ignore
                }
            }
        });
    }

    /**
     * Reset all style attributes to plain text.
     */
    private void resetStyle() {
        this.setStyleBold(false);
        this.setStyleItalic(false);
        this.setStyleUnderline(false);
        this.setStyleForeground(this.defaultForegroundColor);
    }

    /**
     * Enable or disable boldface mode for subsequent messages.
     */
    public void setStyleBold(boolean bold) {
        synchronized (this.currentStyle) {
            this.currentStyle.removeAttribute(StyleConstants.Bold);
            if (bold) {
                this.currentStyle.addAttribute(StyleConstants.Bold, Boolean.TRUE);
            }
            this.bold = bold;
        }
    }

    /**
     * Enable or disable italic mode for subsequent messages.
     */
    public void setStyleItalic(boolean italic) {
        synchronized (this.currentStyle) {
            this.currentStyle.removeAttribute(StyleConstants.Italic);
            if (italic) {
                this.currentStyle.addAttribute(StyleConstants.Italic, Boolean.TRUE);
            }
            this.italic = italic;
        }
    }

    /**
     * Toggle boldface mode for subsequent messages.
     */
    private void toggleStyleBold() {
        this.setStyleBold(!bold);
    }

    /**
     * Enable or disable underline mode for subsequent messages.
     */
    private void setStyleUnderline(boolean underline) {
        synchronized (this.currentStyle) {
            this.currentStyle.removeAttribute(StyleConstants.Underline);
            if (underline) {
                this.currentStyle.addAttribute(StyleConstants.Underline, Boolean.TRUE);
            }
            this.underline = underline;
        }
    }

    /**
     * Toggle underline mode for subsequent messages.
     */
    private void toggleStyleItalic() {
        this.setStyleItalic(!this.italic);
    }

    /**
     * Toggle underline mode for subsequent messages.
     */
    private void toggleStyleUnderline() {
        this.setStyleUnderline(!this.underline);
    }

    /**
     * Set foreground for subsequent messages.
     */
    private void setStyleForegroundAutoCorrected(String ansiSequence) {
        Color foregroundColor = ANSI.toColor(ansiSequence);
        //auto correct for higher contrast, cache this lookup
        if (!this.highContrastColorCache.containsKey(foregroundColor)) {
            this.highContrastColorCache.put(foregroundColor, ColorUtil.getBestContrastColorAroundForeground(
                    this.defaultBackgroundColor, foregroundColor));
        }
        this.setStyleForeground(this.highContrastColorCache.get(foregroundColor));
    }

    /**
     * Set foreground for subsequent messages.
     */
    private void setStyleForeground(Color foregroundColor) {
        synchronized (this.currentStyle) {
            this.currentStyle.removeAttribute(StyleConstants.Foreground);
            this.currentStyle.addAttribute(StyleConstants.Foreground, foregroundColor);
        }
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
        if (!isLoggable(record) || !this.enabled) {
            return;
        }
        String message;
        try {
            message = this.getFormatter().format(record);
        } catch (Throwable ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            if (ex instanceof Exception) {
                reportError(null, (Exception) ex, ErrorManager.FORMAT_FAILURE);
            }
            return;
        }
        try {
            this.messageDecode(message);
        } catch (Throwable ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            if (ex instanceof Exception) {
                reportError(null, (Exception) ex, ErrorManager.WRITE_FAILURE);
            }
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
     * @return the maxBuffersize
     */
    public long getMaxBuffersize() {
        return MAX_BUFFER_SIZE;
    }

    /**
     * @return the defaultForegroundColor, this is already corrected for best
     * contrast if dark mode etc is used
     */
    public Color getDefaultForegroundColor() {
        return (this.defaultForegroundColor);
    }

}
