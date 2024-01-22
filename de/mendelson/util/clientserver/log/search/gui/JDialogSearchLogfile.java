//$Header: /as2/de/mendelson/util/clientserver/log/search/gui/JDialogSearchLogfile.java 12    19/01/23 9:26 Heller $
package de.mendelson.util.clientserver.log.search.gui;

import de.mendelson.util.DateChooserUI;
import de.mendelson.util.IStatusBar;
import de.mendelson.util.LockingGlassPane;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.log.search.Logline;
import de.mendelson.util.clientserver.log.search.ServerSideLogfileFilter;
import de.mendelson.util.clientserver.log.search.ServerSideLogfileFilterImplAS2;
import de.mendelson.util.clientserver.log.search.ServerlogfileSearchRequest;
import de.mendelson.util.clientserver.log.search.ServerlogfileSearchResponse;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dialog to search on the server side in the log files
 *
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class JDialogSearchLogfile extends JDialog {

    private MecResourceBundle rb;
    private BaseClient baseClient;
    private IStatusBar statusBar;
    private Date currentStartDate = new Date();
    private Date currentEndDate = new Date();
    private DateFormat datetimeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private final MendelsonMultiResolutionImage IMAGE_MAGNIFYING_GLASS
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/clientserver/log/search/gui/magnifying_glass.svg", 24, 48);

    /**
     * Creates new form JDialogSearchLogfile
     */
    public JDialogSearchLogfile(JFrame parent, BaseClient baseClient, IStatusBar statusBar) {
        super(parent, true);
        //Load resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogSearchLogfile.class.getName());
        } //load up  resourcebundle        
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.statusBar = statusBar;
        this.baseClient = baseClient;
        initComponents();
        TextOverlay.addTo(this.jTextFieldSearchText, this.rb.getResourceString("textfield.preset"));
        this.setMultiresolutionIcons();
        this.setupDateChooser();
        //hide dialog on esc
        ActionListener actionListenerESC = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jButtonClose.doClick();
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane().registerKeyboardAction(actionListenerESC, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.getRootPane().setDefaultButton(this.jButtonSearch);
    }

    private void setMultiresolutionIcons() {
        this.jButtonSearch.setIcon(new ImageIcon(IMAGE_MAGNIFYING_GLASS.toMinResolution(24)));
    }

    /**
     * Allows to copy a transmission number to the clipboard which will be used
     * automatically in the search dialog
     */
    private String getCurrentDataFromClipboard() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            String result = (String) clipboard.getData(DataFlavor.stringFlavor);
            return (result);
        } catch (Exception e) {
        }
        return (null);
    }

    @Override
    public void setVisible(boolean flag) {
        if (flag) {
            String clipboardData = this.getCurrentDataFromClipboard();
            int maxLength = 100;
            if (clipboardData != null && clipboardData.contains("@") && clipboardData.length() <= maxLength) {
                this.jTextFieldSearchText.setText(clipboardData);
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jTextFieldSearchText.requestFocusInWindow();
                    jTextFieldSearchText.selectAll();
                }
            });
        }
        super.setVisible(flag);
    }

    /**
     * Defines the date chooser and the used colors
     */
    private void setupDateChooser() {
        this.jDateChooserStartDate.setUI(new DateChooserUI());
        this.jDateChooserStartDate.setLocale(Locale.getDefault());
        this.jDateChooserStartDate.setDate(this.currentStartDate);
        this.jDateChooserStartDate.getDateEditor().addPropertyChangeListener(
                new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName() != null && e.getPropertyName().startsWith("date")) {
                    currentStartDate = jDateChooserStartDate.getDate();
                }
            }
        });
        this.jDateChooserEndDate.setUI(new DateChooserUI());
        this.jDateChooserEndDate.setLocale(Locale.getDefault());
        this.jDateChooserEndDate.setDate(this.currentEndDate);
        this.jDateChooserEndDate.getDateEditor().addPropertyChangeListener(
                new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName() != null && e.getPropertyName().startsWith("date")) {
                    currentEndDate = jDateChooserEndDate.getDate();
                }
            }
        });
    }

    /**
     * The user modified the filter... capture the current values
     */
    private ServerSideLogfileFilter generateFilterFromGUI() {
        ServerSideLogfileFilterImplAS2 logFilter = new ServerSideLogfileFilterImplAS2();
        logFilter.setStartDate(this.jDateChooserStartDate.getDate().getTime());
        logFilter.setEndDate(this.jDateChooserEndDate.getDate().getTime());
        if (this.jRadioButtonMDNId.isSelected()) {
            logFilter.setMDNId(this.jTextFieldSearchText.getText());
        } else if (this.jRadioButtonMessageId.isSelected()) {
            logFilter.setMessageId(this.jTextFieldSearchText.getText());
        } else if (this.jRadioButtonUserdefinedId.isSelected()) {
            logFilter.setUserdefinedId(this.jTextFieldSearchText.getText());
        }
        return (logFilter);
    }

    /**
     * Perform a search on the server for events - by showing the search dialog
     * first and performing the action afterwards
     */
    private synchronized void performSearch() {
        final String uniqueId = this.getClass().getName() + ".performSearch." + System.currentTimeMillis();
        final ServerSideLogfileFilter filter = this.generateFilterFromGUI();
        final ServerlogfileSearchRequest request = new ServerlogfileSearchRequest(filter);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JDialogSearchLogfile.this.lock();
                //display wait indicator
                JDialogSearchLogfile.this.statusBar.startProgressIndeterminate(rb.getResourceString("label.search"), uniqueId);
                try {
                    ServerlogfileSearchResponse response = (ServerlogfileSearchResponse) baseClient.sendSync(request);
                    StringBuilder builder = new StringBuilder();
                    if (response.getException() != null) {
                        builder.append(rb.getResourceString("problem.serverside",
                                new Object[]{
                                    response.getException().getClass().getSimpleName(),
                                    response.getException().getMessage(),}));
                    } else {
                        List<Logline> resultList = response.getSearchResults();
                        for (Logline line : resultList) {
                            try {
                                long timestamp = Long.valueOf(line.getValue(Logline.KEY_MILLISECS));
                                builder.append("[");
                                builder.append(JDialogSearchLogfile.this.datetimeFormat.format(new Date(timestamp)));
                                builder.append("] ");
                            } catch (Exception e) {
                                //nop
                            }
                            builder.append(line.getValue(Logline.KEY_LOGMESSAGE));
                            builder.append(System.lineSeparator());
                        }
                        if (resultList.isEmpty()) {
                            if (((ServerSideLogfileFilterImplAS2) filter).getMessageId() != null) {
                                builder.append(rb.getResourceString("no.data.messageid",
                                        ((ServerSideLogfileFilterImplAS2) filter).getMessageId()));
                            }
                            if (((ServerSideLogfileFilterImplAS2) filter).getMDNId() != null) {
                                builder.append(rb.getResourceString("no.data.mdnid",
                                        ((ServerSideLogfileFilterImplAS2) filter).getMDNId()));
                            }
                            if (((ServerSideLogfileFilterImplAS2) filter).getUserdefinedId() != null) {
                                builder.append(rb.getResourceString("no.data.uid",
                                        ((ServerSideLogfileFilterImplAS2) filter).getUserdefinedId()));
                            }
                        }
                    }
                    JDialogSearchLogfile.this.jTextPaneResult.setText(builder.toString());
                } catch (Throwable e) {
                } finally {
                    JDialogSearchLogfile.this.unlock();
                    JDialogSearchLogfile.this.statusBar.stopProgressIfExists(uniqueId);
                }
            }
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(runnable);
        executor.shutdown();
    }

    /**
     * Lock the component: Add a glasspane that prevents any action on the UI
     */
    private void lock() {
        //init glasspane for first use
        if (!(this.getGlassPane() instanceof LockingGlassPane)) {
            this.setGlassPane(new LockingGlassPane());
        }
        this.getGlassPane().setVisible(true);
        this.getGlassPane().requestFocusInWindow();
    }

    /**
     * Unlock the component: remove the glasspane that prevents any action on
     * the UI
     */
    private void unlock() {
        getGlassPane().setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupSearchSelection = new javax.swing.ButtonGroup();
        jPanelSearch = new javax.swing.JPanel();
        jButtonSearch = new javax.swing.JButton();
        jLabelStartDate = new javax.swing.JLabel();
        jLabelEndDate = new javax.swing.JLabel();
        jDateChooserStartDate = new com.toedter.calendar.JDateChooser();
        jDateChooserEndDate = new com.toedter.calendar.JDateChooser();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jRadioButtonMessageId = new javax.swing.JRadioButton();
        jRadioButtonMDNId = new javax.swing.JRadioButton();
        jRadioButtonUserdefinedId = new javax.swing.JRadioButton();
        jTextFieldSearchText = new javax.swing.JTextField();
        jLabelDescription = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelResult = new javax.swing.JPanel();
        jScrollPaneResult = new javax.swing.JScrollPane();
        jTextPaneResult = new javax.swing.JTextPane();
        jPanelButton = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(rb.getResourceString( "title"));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelSearch.setLayout(new java.awt.GridBagLayout());

        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/clientserver/log/search/gui/missing_image24x24.gif"))); // NOI18N
        jButtonSearch.setText(this.rb.getResourceString( "label.search"));
        jButtonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSearch.setMargin(new java.awt.Insets(5, 14, 2, 14));
        jButtonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 17;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 15);
        jPanelSearch.add(jButtonSearch, gridBagConstraints);

        jLabelStartDate.setText(this.rb.getResourceString( "label.startdate"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelSearch.add(jLabelStartDate, gridBagConstraints);

        jLabelEndDate.setText(this.rb.getResourceString("label.enddate"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelSearch.add(jLabelEndDate, gridBagConstraints);

        jDateChooserStartDate.setMinimumSize(new java.awt.Dimension(130, 20));
        jDateChooserStartDate.setPreferredSize(new java.awt.Dimension(130, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSearch.add(jDateChooserStartDate, gridBagConstraints);

        jDateChooserEndDate.setMinimumSize(new java.awt.Dimension(130, 20));
        jDateChooserEndDate.setPreferredSize(new java.awt.Dimension(130, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSearch.add(jDateChooserEndDate, gridBagConstraints);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 15;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        jPanelSearch.add(jSeparator1, gridBagConstraints);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        jPanelSearch.add(jSeparator2, gridBagConstraints);

        buttonGroupSearchSelection.add(jRadioButtonMessageId);
        jRadioButtonMessageId.setSelected(true);
        jRadioButtonMessageId.setText(rb.getResourceString( "label.messageid"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSearch.add(jRadioButtonMessageId, gridBagConstraints);

        buttonGroupSearchSelection.add(jRadioButtonMDNId);
        jRadioButtonMDNId.setText(rb.getResourceString( "label.mdnid"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSearch.add(jRadioButtonMDNId, gridBagConstraints);

        buttonGroupSearchSelection.add(jRadioButtonUserdefinedId);
        jRadioButtonUserdefinedId.setText(rb.getResourceString( "label.uid"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSearch.add(jRadioButtonUserdefinedId, gridBagConstraints);

        jTextFieldSearchText.setText("mendelsonAS2@partnerAS2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelSearch.add(jTextFieldSearchText, gridBagConstraints);

        jLabelDescription.setText(rb.getResourceString( "label.info"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 15, 5);
        jPanelSearch.add(jLabelDescription, gridBagConstraints);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        jPanelSearch.add(jSeparator3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 15;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanelSearch, gridBagConstraints);

        jPanelResult.setLayout(new java.awt.GridBagLayout());

        jScrollPaneResult.setViewportView(jTextPaneResult);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelResult.add(jScrollPaneResult, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(jPanelResult, gridBagConstraints);

        jPanelButton.setLayout(new java.awt.GridBagLayout());

        jButtonClose.setText(rb.getResourceString( "button.close"));
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        jPanelButton.add(jButtonClose, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(jPanelButton, gridBagConstraints);

        setSize(new java.awt.Dimension(1035, 691));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        this.performSearch();
    }//GEN-LAST:event_jButtonSearchActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupSearchSelection;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonSearch;
    private com.toedter.calendar.JDateChooser jDateChooserEndDate;
    private com.toedter.calendar.JDateChooser jDateChooserStartDate;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JLabel jLabelEndDate;
    private javax.swing.JLabel jLabelStartDate;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelResult;
    private javax.swing.JPanel jPanelSearch;
    private javax.swing.JRadioButton jRadioButtonMDNId;
    private javax.swing.JRadioButton jRadioButtonMessageId;
    private javax.swing.JRadioButton jRadioButtonUserdefinedId;
    private javax.swing.JScrollPane jScrollPaneResult;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextFieldSearchText;
    private javax.swing.JTextPane jTextPaneResult;
    // End of variables declaration//GEN-END:variables
}
