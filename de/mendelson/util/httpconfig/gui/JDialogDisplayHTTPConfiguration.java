//$Header: /as2/de/mendelson/util/httpconfig/gui/JDialogDisplayHTTPConfiguration.java 14    2/05/22 10:55 Heller $
package de.mendelson.util.httpconfig.gui;

import de.mendelson.util.IStatusBar;
import de.mendelson.util.LockingGlassPane;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.httpconfig.clientserver.DisplayHTTPServerConfigurationRequest;
import de.mendelson.util.httpconfig.clientserver.DisplayHTTPServerConfigurationResponse;
import de.mendelson.util.uinotification.UINotification;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dialog to send a file to a single partner
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
public class JDialogDisplayHTTPConfiguration extends JDialog {

    /**
     * ResourceBundle to localize the GUI
     */
    private MecResourceBundle rb = null;
    private BaseClient baseClient;
    private IStatusBar statusbar;
    private final MendelsonMultiResolutionImage ICON_PORTS
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/httpconfig/gui/ports.svg", 32, 64);

    /**
     * Creates new form JDialogPartnerConfig
     *
     */
    public JDialogDisplayHTTPConfiguration(JFrame parent, BaseClient baseClient,
            IStatusBar statusbar) {
        super(parent, true);
        this.statusbar = statusbar;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDisplayHTTPConfiguration.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.baseClient = baseClient;
        this.setTitle(this.rb.getResourceString("title"));
        initComponents();
        this.jLabelIcon.setIcon(new ImageIcon(ICON_PORTS));
    }

    /**
     * Lock the component: Add a glasspane that prevents any action on the UI
     */
    protected void lock() {
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
    protected void unlock() {
        getGlassPane().setVisible(false);
    }

    public void initialize() {
        final String uniqueId = this.getClass().getName() + ".initialize." + System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    JDialogDisplayHTTPConfiguration.this.lock();
                    //display wait indicator
                    JDialogDisplayHTTPConfiguration.this.statusbar.startProgressIndeterminate(
                            JDialogDisplayHTTPConfiguration.this.rb.getResourceString("reading.configuration"), uniqueId);
                    DisplayHTTPServerConfigurationResponse response
                            = (DisplayHTTPServerConfigurationResponse) JDialogDisplayHTTPConfiguration.this.baseClient.sendSyncWaitInfinite(new DisplayHTTPServerConfigurationRequest());
                    JDialogDisplayHTTPConfiguration.this.jTextAreaMisc.setText(response.getMiscConfigurationText());
                    String embeddedJettyServerVersion = response.getEmbeddedJettyServerVersion();
                    if (embeddedJettyServerVersion == null) {
                        embeddedJettyServerVersion = "--";
                    }
                    String httpServerConfigFile = response.getHttpServerConfigFile();
                    if (httpServerConfigFile == null) {
                        httpServerConfigFile = "--";
                    }
                    String httpServerUserConfigFile = response.getHTTPServerUserConfigFile();
                    if (httpServerUserConfigFile == null) {
                        httpServerUserConfigFile = "--";
                    }
                    JDialogDisplayHTTPConfiguration.this.jLabelConfigFileInfo.setText("<HTML>"
                            + JDialogDisplayHTTPConfiguration.this.rb.getResourceString("label.info.configfile",
                                    new Object[]{
                                        embeddedJettyServerVersion,
                                        "<strong>" + httpServerConfigFile + "</strong>",
                                        "<strong>" + httpServerUserConfigFile + "</strong>"
                                        })
                            + "</HTML>");
                    if (!response.isEmbeddedHTTPServerStarted()) {
                        JDialogDisplayHTTPConfiguration.this.jTextAreaMisc.setText(
                                JDialogDisplayHTTPConfiguration.this.rb.getResourceString("no.embedded.httpserver"));
                        JDialogDisplayHTTPConfiguration.this.jTextAreaCipher.setText(
                                JDialogDisplayHTTPConfiguration.this.rb.getResourceString("no.embedded.httpserver"));
                        JDialogDisplayHTTPConfiguration.this.jTextAreaProtocols.setText(
                                JDialogDisplayHTTPConfiguration.this.rb.getResourceString("no.embedded.httpserver"));
                    } else if (response.isSSLEnabled()) {
                        JDialogDisplayHTTPConfiguration.this.jTextAreaCipher.setText(response.getCipherConfigurationText());
                        JDialogDisplayHTTPConfiguration.this.jTextAreaProtocols.setText(response.getProtocolConfigurationText());
                    } else {
                        JDialogDisplayHTTPConfiguration.this.jTextAreaCipher.setText(
                                JDialogDisplayHTTPConfiguration.this.rb.getResourceString("no.ssl.enabled",
                                        response.getHttpServerConfigFile()));
                        JDialogDisplayHTTPConfiguration.this.jTextAreaProtocols.setText(
                                JDialogDisplayHTTPConfiguration.this.rb.getResourceString("no.ssl.enabled",
                                        response.getHttpServerConfigFile()));
                    }
                } catch (Exception e) {
                    JDialogDisplayHTTPConfiguration.this.unlock();
                    JDialogDisplayHTTPConfiguration.this.statusbar.stopProgressIfExists(uniqueId);
                    UINotification.instance().addNotification(e);
                } finally {
                    JDialogDisplayHTTPConfiguration.this.unlock();
                    JDialogDisplayHTTPConfiguration.this.statusbar.stopProgressIfExists(uniqueId);
                }
            }
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(runnable);
        executor.shutdown();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelEdit = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jPanelSpace = new javax.swing.JPanel();
        jTabbedPaneConfig = new javax.swing.JTabbedPane();
        jScrollPaneConfigMisc = new javax.swing.JScrollPane();
        jTextAreaMisc = new javax.swing.JTextArea();
        jScrollPaneCipher = new javax.swing.JScrollPane();
        jTextAreaCipher = new javax.swing.JTextArea();
        jScrollPaneProtocols = new javax.swing.JScrollPane();
        jTextAreaProtocols = new javax.swing.JTextArea();
        jLabelConfigFileInfo = new javax.swing.JLabel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/httpconfig/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 10, 10);
        jPanelEdit.add(jLabelIcon, gridBagConstraints);

        jPanelSpace.setLayout(new java.awt.GridBagLayout());

        jTextAreaMisc.setEditable(false);
        jTextAreaMisc.setColumns(20);
        jTextAreaMisc.setRows(5);
        jScrollPaneConfigMisc.setViewportView(jTextAreaMisc);

        jTabbedPaneConfig.addTab(this.rb.getResourceString( "tab.misc")
            , jScrollPaneConfigMisc);

        jTextAreaCipher.setEditable(false);
        jTextAreaCipher.setColumns(20);
        jTextAreaCipher.setRows(5);
        jScrollPaneCipher.setViewportView(jTextAreaCipher);

        jTabbedPaneConfig.addTab(this.rb.getResourceString( "tab.cipher"), jScrollPaneCipher);

        jTextAreaProtocols.setEditable(false);
        jTextAreaProtocols.setColumns(20);
        jTextAreaProtocols.setRows(5);
        jScrollPaneProtocols.setViewportView(jTextAreaProtocols);

        jTabbedPaneConfig.addTab(this.rb.getResourceString( "tab.protocols"), jScrollPaneProtocols);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelSpace.add(jTabbedPaneConfig, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jPanelSpace, gridBagConstraints);

        jLabelConfigFileInfo.setText("<ConfigFileInfo>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 10, 10);
        jPanelEdit.add(jLabelConfigFileInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelEdit, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(this.rb.getResourceString( "button.ok" ));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);

        setSize(new java.awt.Dimension(857, 610));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelConfigFileInfo;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JScrollPane jScrollPaneCipher;
    private javax.swing.JScrollPane jScrollPaneConfigMisc;
    private javax.swing.JScrollPane jScrollPaneProtocols;
    private javax.swing.JTabbedPane jTabbedPaneConfig;
    private javax.swing.JTextArea jTextAreaCipher;
    private javax.swing.JTextArea jTextAreaMisc;
    private javax.swing.JTextArea jTextAreaProtocols;
    // End of variables declaration//GEN-END:variables
}
