//$Header: /as2/de/mendelson/util/log/panel/LogConsolePanel.java 17    18/05/22 12:16 Heller $
package de.mendelson.util.log.panel;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.log.ANSI;
import de.mendelson.util.log.JTextPaneLoggingHandler;
import de.mendelson.util.log.JTextPaneOutputStream;
import de.mendelson.util.log.LogFormatter;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * The frame system output/debug info is written to
 *
 * @author S.Heller
 * @version $Revision: 17 $
 */
public class LogConsolePanel extends JPanel implements ClipboardOwner {

    public static final String COLOR_BLACK = ANSI.COLOR_SYSTEM_BLACK;
    public static final String COLOR_LIGHT_BLUE = ANSI.COLOR_SYSTEM_BLUE_BRIGHT;
    public static final String COLOR_LIGHT_RED = ANSI.COLOR_SYSTEM_RED_BRIGHT;
    public static final String COLOR_LIGHT_CYAN = ANSI.COLOR_SYSTEM_CYAN_BRIGHT;
    public static final String COLOR_LIGHT_GREEN = ANSI.COLOR_SYSTEM_GREEN_BRIGHT;
    public static final String COLOR_LIGHT_GRAY = ANSI.COLOR_SYSTEM_GREY_BRIGHT;
    public static final String COLOR_LIGHT_PURPLE = ANSI.COLOR_SYSTEM_PURPLE_BRIGHT;
    public static final String COLOR_DARK_BLUE = ANSI.COLOR_SYSTEM_BLUE;
    public static final String COLOR_DARK_GRAY = ANSI.COLOR_SYSTEM_GREY;    
    public static final String COLOR_DARK_GREEN = ANSI.COLOR_SYSTEM_GREEN;
    public static final String COLOR_DARK_PURPLE = ANSI.COLOR_SYSTEM_PURPLE;
    public static final String COLOR_DARK_RED = ANSI.COLOR_SYSTEM_RED;
    public static final String COLOR_DARK_CYAN = ANSI.COLOR_SYSTEM_CYAN;
    public static final String COLOR_WHITE = ANSI.COLOR_SYSTEM_WHITE_BRIGHT;
    public static final String COLOR_DARK_YELLOW = ANSI.COLOR_SYSTEM_YELLOW;
    public static final String COLOR_LIGHT_YELLOW = ANSI.COLOR_SYSTEM_YELLOW;

    /**
     * PrintStream to write in, this is just a wrapper to the internal logger.
     */
    private PrintStream out = null;
    /**
     * Logger to log to
     */
    private Logger logger;
    /**
     * ResourceBundle to localize this GUI
     */
    private MecResourceBundle rb;
    private JTextPaneLoggingHandler handler;

    private static final MendelsonMultiResolutionImage IMAGE_DELESECT
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/log/panel/deselect.svg", 18, 36);
    private static final MendelsonMultiResolutionImage IMAGE_CLIPBOARD
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/log/panel/notes.svg", 18, 36);
    
    public LogConsolePanel(Logger logger, LogFormatter logFormatter) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleLogConsole.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.initComponents();
        this.setMultiresolutionIcons();
        this.initialize(logger, logFormatter);
    }

    public LogConsolePanel(Logger logger) {
        this(logger, new LogFormatter(LogFormatter.FORMAT_CONSOLE));
    }

    private void setMultiresolutionIcons() {
        this.jMenuItemClear.setIcon(new ImageIcon(IMAGE_DELESECT.toMinResolution(18)));
        this.jMenuItemCopyToClipBoard.setIcon(new ImageIcon(IMAGE_CLIPBOARD.toMinResolution(18)));
    }
    
    /**
     * Enables/disables the display log
     */
    public void setDisplayLog(boolean enable) {
        this.setDisplayLog(enable, null);
    }

    /**
     * Enables/disables the display log
     */
    public void setDisplayLog(boolean enable, String logMessage) {
        //log the new state before disabling the output
        if (!enable && logMessage != null) {
            this.logger.log(Level.FINER, logMessage);
        }
        this.handler.setEnabled(enable);
        //log the new state after enabling the output
        if (enable && logMessage != null) {
            this.logger.log(Level.FINER, logMessage);
        }
    }

    private void initialize(Logger logger, LogFormatter logFormatter) {
        this.logger = logger;
        this.logger.setUseParentHandlers(false);
        OutputStream logStream = new JTextPaneOutputStream(this.jTextPane);
        this.out = new PrintStream(logStream);
        this.handler = new JTextPaneLoggingHandler(this.jTextPane, logFormatter);
        this.setDefaultColors(this.handler);
        this.logger.addHandler(handler);
        this.jPopupMenu.setInvoker(this.jTextPane);
    }

    /**
     * Sets some default colors for the logger levels. Overwrite these colors
     * using the setColor method
     *
     */
    public void setDefaultColors(JTextPaneLoggingHandler handler) {
        handler.setColor(Level.SEVERE, COLOR_DARK_RED);
        handler.setColor(Level.WARNING, COLOR_DARK_BLUE);
        handler.setColor(Level.INFO, COLOR_BLACK);
        handler.setColor(Level.CONFIG, COLOR_DARK_GREEN);
        handler.setColor(Level.FINE, COLOR_LIGHT_GRAY);
        handler.setColor(Level.FINER, COLOR_LIGHT_GRAY);
        handler.setColor(Level.FINEST, COLOR_LIGHT_GRAY);
    }

    /**
     * Sets a special color for a special log level. Please use the class
     * constant values
     */
    public void setColor(Level level, String color) {
        this.handler.setColor(level, color);
    }

    /**
     * returns the PrintStream to write output data to
     */
    public PrintStream getPrintStream() {
        return (this.out);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPopupMenu = new javax.swing.JPopupMenu();
        jMenuItemClear = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemCopyToClipBoard = new javax.swing.JMenuItem();
        jScrollPane = new javax.swing.JScrollPane();
        jTextPane = new javax.swing.JTextPane();

        jMenuItemClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/log/panel/missing_image16x16.gif"))); // NOI18N
        jMenuItemClear.setText(this.rb.getResourceString( "label.clear" ));
        jMenuItemClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemClearActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemClear);
        jPopupMenu.add(jSeparator1);

        jMenuItemCopyToClipBoard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/log/panel/missing_image16x16.gif"))); // NOI18N
        jMenuItemCopyToClipBoard.setText(this.rb.getResourceString( "label.toclipboard" ));
        jMenuItemCopyToClipBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyToClipBoardActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemCopyToClipBoard);

        setLayout(new java.awt.GridBagLayout());

        jScrollPane.setPreferredSize(new java.awt.Dimension(300, 100));

        jTextPane.setEditable(false);
        jTextPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextPaneMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextPaneMouseReleased(evt);
            }
        });
        jScrollPane.setViewportView(jTextPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    private void jMenuItemCopyToClipBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopyToClipBoardActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(this.jTextPane.getText()), this);
    }//GEN-LAST:event_jMenuItemCopyToClipBoardActionPerformed

    private void jMenuItemClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClearActionPerformed
        this.jTextPane.setText("");
    }//GEN-LAST:event_jMenuItemClearActionPerformed

    private void jTextPaneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextPaneMousePressed
        //JavaDoc for MouseEvent.isPopupTrigger():
        //Note: Popup menus are triggered differently on different systems. 
        //Therefore, isPopupTrigger should be checked in both mousePressed and mouseReleased 
        //for proper cross-platform functionality.
        if (evt.isPopupTrigger() || evt.isMetaDown()) {
            this.jPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTextPaneMousePressed

    private void jTextPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextPaneMouseReleased
        //JavaDoc for MouseEvent.isPopupTrigger():
        //Note: Popup menus are triggered differently on different systems. 
        //Therefore, isPopupTrigger should be checked in both mousePressed and mouseReleased 
        //for proper cross-platform functionality.
        if (evt.isPopupTrigger() || evt.isMetaDown()) {
            this.jPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTextPaneMouseReleased

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {
        //Clipboard contents replaced, dont care!
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jMenuItemClear;
    private javax.swing.JMenuItem jMenuItemCopyToClipBoard;
    private javax.swing.JPopupMenu jPopupMenu;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextPane jTextPane;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the handler
     */
    public JTextPaneLoggingHandler getHandler() {
        return handler;
    }
}
