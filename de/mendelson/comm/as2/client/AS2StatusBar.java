//$Header: /as2/de/mendelson/comm/as2/client/AS2StatusBar.java 33    17/02/22 11:31 Heller $
package de.mendelson.comm.as2.client;

import de.mendelson.comm.as2.configurationcheck.gui.JDialogIssuesList;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.clientserver.message.ConfigurationCheckRequest;
import de.mendelson.comm.as2.clientserver.message.ConfigurationCheckResponse;
import de.mendelson.util.IStatusBar;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.ProgressPanel;
import de.mendelson.util.clientserver.BaseClient;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Status bar for the AS2 GUI
 *
 * @author S.Heller
 * @version $Revision: 33 $
 */
public class AS2StatusBar extends JPanel implements IStatusBar {

    private final static int ICON_HEIGHT = 18;

    private MecResourceBundle rb;
    public static final MendelsonMultiResolutionImage IMAGE_WARNING
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/warning_sign.svg", ICON_HEIGHT, ICON_HEIGHT * 2);
    public static final MendelsonMultiResolutionImage IMAGE_PENDING
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_pending.svg", ICON_HEIGHT, ICON_HEIGHT * 2);
    public static final MendelsonMultiResolutionImage IMAGE_STOPPED
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_stopped.svg", ICON_HEIGHT, ICON_HEIGHT * 2);
    public static final MendelsonMultiResolutionImage IMAGE_FINISHED
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_finished.svg", ICON_HEIGHT, ICON_HEIGHT * 2);
    public static final MendelsonMultiResolutionImage IMAGE_SERVED
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_all.svg", ICON_HEIGHT, ICON_HEIGHT * 2);
    public static final MendelsonMultiResolutionImage IMAGE_ALL
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_all_sum.svg", ICON_HEIGHT, ICON_HEIGHT * 2);
    public static final MendelsonMultiResolutionImage IMAGE_ALL_SELECTED
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/client/state_allselected.svg", ICON_HEIGHT, ICON_HEIGHT * 2);
    private ModuleStarter moduleStarter;
    private BaseClient baseClient = null;
    private ConfigurationCheckThread checkThread = null;
    private final ScheduledExecutorService configurationCheckRefreshExecutor = Executors.newSingleThreadScheduledExecutor(
        new NamedThreadFactory("client-configuration-check"));

    /**
     * Creates new form AS2StatusBar
     */
    public AS2StatusBar() {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2StatusBar.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        initComponents();
        this.setMultiresolutionIcons();
    }

    private void setMultiresolutionIcons() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelTransactionsFailure.setIcon(new ImageIcon(IMAGE_STOPPED.toMinResolution(ICON_HEIGHT)));
                jLabelTransactionsOk.setIcon(new ImageIcon(IMAGE_FINISHED.toMinResolution(ICON_HEIGHT)));
                jLabelTransactionsPending.setIcon(new ImageIcon(IMAGE_PENDING.toMinResolution(ICON_HEIGHT)));
                jLabelTransactionsServed.setIcon(new ImageIcon(IMAGE_SERVED.toMinResolution(ICON_HEIGHT)));
                jLabelTransactionsAll.setIcon(new ImageIcon(IMAGE_ALL.toMinResolution(ICON_HEIGHT)));
                jLabelTransactionsSelected.setIcon(new ImageIcon(IMAGE_ALL_SELECTED.toMinResolution(ICON_HEIGHT)));
            }

        });

    }

    public void initialize(BaseClient baseClient, ModuleStarter moduleStarter) {
        this.baseClient = baseClient;
        this.moduleStarter = moduleStarter;
    }

    public void startConfigurationChecker() {
        if (this.baseClient == null) {
            throw new IllegalArgumentException("Status bar: Please pass the base client to the status bar before starting the config checker.");
        }
        this.checkThread = new ConfigurationCheckThread();
        this.configurationCheckRefreshExecutor.scheduleWithFixedDelay(this.checkThread, 1, 30, TimeUnit.SECONDS);
    }

    public void setTransactionCount(int countAll, int countServed, int countOk, int countPending, int countFailed, int countSelected) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelTransactionsAll.setText(String.valueOf(countAll));
                jLabelTransactionsServed.setText(String.valueOf(countServed));
                jLabelTransactionsOk.setText(String.valueOf(countOk));
                jLabelTransactionsPending.setText(String.valueOf(countPending));
                jLabelTransactionsFailure.setText(String.valueOf(countFailed));
                jLabelTransactionsSelected.setText(String.valueOf(countSelected));
            }
        });

    }

    public void setConnectedHost(String host) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelHost.setText(AS2ServerVersion.getProductName() + "@" + host);
            }
        });
    }

    public void setSelectedTransactionCount(int countSelected) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelTransactionsSelected.setText(String.valueOf(countSelected));
            }
        });
    }

    @Override
    public void startProgressIndeterminate(String progressDetails, String uniqueId) {
        this.progressPanel.startProgressIndeterminate(progressDetails, uniqueId);
    }

    @Override
    public void stopProgressIfExists(String uniqueId) {
        this.progressPanel.stopProgressIfExists(uniqueId);
    }

    public ProgressPanel getProgressPanel() {
        return (this.progressPanel);
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

        jPanelMain = new javax.swing.JPanel();
        jPanelTransactionCount = new javax.swing.JPanel();
        jLabelTransactionsAll = new javax.swing.JLabel();
        jLabelTransactionsServed = new javax.swing.JLabel();
        jLabelTransactionsOk = new javax.swing.JLabel();
        jLabelTransactionsPending = new javax.swing.JLabel();
        jLabelTransactionsFailure = new javax.swing.JLabel();
        jLabelTransactionsSelected = new javax.swing.JLabel();
        jPanelSep1 = new javax.swing.JPanel();
        jSeparator6 = new javax.swing.JSeparator();
        jPanelSep2 = new javax.swing.JPanel();
        jSeparator7 = new javax.swing.JSeparator();
        jPanelSep3 = new javax.swing.JPanel();
        jSeparator8 = new javax.swing.JSeparator();
        jPanelSep4 = new javax.swing.JPanel();
        jSeparator9 = new javax.swing.JSeparator();
        jPanelSep5 = new javax.swing.JPanel();
        jSeparator10 = new javax.swing.JSeparator();
        jPanelSep8 = new javax.swing.JPanel();
        jSeparator13 = new javax.swing.JSeparator();
        jPanelEmpty = new javax.swing.JPanel();
        jLabelConfigurationIssue = new javax.swing.JLabel();
        jLabelHost = new javax.swing.JLabel();
        progressPanel = new de.mendelson.util.ProgressPanel();
        jPanelSep6 = new javax.swing.JPanel();
        jSeparator11 = new javax.swing.JSeparator();
        jPanelSep7 = new javax.swing.JPanel();
        jSeparator12 = new javax.swing.JSeparator();

        setFocusable(false);
        setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        setRequestFocusEnabled(false);
        setVerifyInputWhenFocusTarget(false);
        setLayout(new java.awt.GridBagLayout());

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jPanelTransactionCount.setLayout(new java.awt.GridBagLayout());

        jLabelTransactionsAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsAll.setText("0");
        jLabelTransactionsAll.setToolTipText(this.rb.getResourceString( "count.all.available"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsAll, gridBagConstraints);

        jLabelTransactionsServed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsServed.setText("0");
        jLabelTransactionsServed.setToolTipText(this.rb.getResourceString( "count.all.served"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsServed, gridBagConstraints);

        jLabelTransactionsOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsOk.setText("0");
        jLabelTransactionsOk.setToolTipText(this.rb.getResourceString( "count.ok"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsOk, gridBagConstraints);

        jLabelTransactionsPending.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsPending.setText("0");
        jLabelTransactionsPending.setToolTipText(this.rb.getResourceString( "count.pending"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsPending, gridBagConstraints);

        jLabelTransactionsFailure.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsFailure.setText("0");
        jLabelTransactionsFailure.setToolTipText(this.rb.getResourceString( "count.failure"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsFailure, gridBagConstraints);

        jLabelTransactionsSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/missing_image16x16.gif"))); // NOI18N
        jLabelTransactionsSelected.setText("0");
        jLabelTransactionsSelected.setToolTipText(this.rb.getResourceString( "count.selected"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelTransactionCount.add(jLabelTransactionsSelected, gridBagConstraints);

        jPanelSep1.setLayout(new java.awt.GridBagLayout());

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanelSep1.add(jSeparator6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep1, gridBagConstraints);

        jPanelSep2.setLayout(new java.awt.GridBagLayout());

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanelSep2.add(jSeparator7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep2, gridBagConstraints);

        jPanelSep3.setLayout(new java.awt.GridBagLayout());

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanelSep3.add(jSeparator8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep3, gridBagConstraints);

        jPanelSep4.setLayout(new java.awt.GridBagLayout());

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanelSep4.add(jSeparator9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep4, gridBagConstraints);

        jPanelSep5.setLayout(new java.awt.GridBagLayout());

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanelSep5.add(jSeparator10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep5, gridBagConstraints);

        jPanelSep8.setLayout(new java.awt.GridBagLayout());

        jSeparator13.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanelSep8.add(jSeparator13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelTransactionCount.add(jPanelSep8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        jPanelMain.add(jPanelTransactionCount, gridBagConstraints);

        jPanelEmpty.setPreferredSize(new java.awt.Dimension(125, 20));
        jPanelEmpty.setLayout(new java.awt.GridBagLayout());

        jLabelConfigurationIssue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelConfigurationIssue.setText("Not connected");
        jLabelConfigurationIssue.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabelConfigurationIssue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelConfigurationIssueMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelConfigurationIssueMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelEmpty.add(jLabelConfigurationIssue, gridBagConstraints);

        jLabelHost.setText("Not connected");
        jLabelHost.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabelHost.setMaximumSize(new java.awt.Dimension(1000, 16));
        jLabelHost.setMinimumSize(new java.awt.Dimension(100, 16));
        jLabelHost.setPreferredSize(new java.awt.Dimension(200, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanelEmpty.add(jLabelHost, gridBagConstraints);

        progressPanel.setMaximumSize(new java.awt.Dimension(2147483647, 16));
        progressPanel.setMinimumSize(new java.awt.Dimension(200, 12));
        progressPanel.setPreferredSize(new java.awt.Dimension(200, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelEmpty.add(progressPanel, gridBagConstraints);

        jPanelSep6.setLayout(new java.awt.GridBagLayout());

        jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanelSep6.add(jSeparator11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelEmpty.add(jPanelSep6, gridBagConstraints);

        jPanelSep7.setLayout(new java.awt.GridBagLayout());

        jSeparator12.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanelSep7.add(jSeparator12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelEmpty.add(jPanelSep7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanelMain.add(jPanelEmpty, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        add(jPanelMain, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabelConfigurationIssueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelConfigurationIssueMouseClicked
        if (evt.getClickCount() == 2) {
            //double clicked on the issue panel
        }
    }//GEN-LAST:event_jLabelConfigurationIssueMouseClicked

    private void jLabelConfigurationIssueMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelConfigurationIssueMouseEntered
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        JDialogIssuesList dialog = new JDialogIssuesList(parent, this.baseClient,
                this.jLabelConfigurationIssue.getLocationOnScreen(), this.moduleStarter);
        dialog.setVisible(true);
    }//GEN-LAST:event_jLabelConfigurationIssueMouseEntered

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelConfigurationIssue;
    private javax.swing.JLabel jLabelHost;
    private javax.swing.JLabel jLabelTransactionsAll;
    private javax.swing.JLabel jLabelTransactionsFailure;
    private javax.swing.JLabel jLabelTransactionsOk;
    private javax.swing.JLabel jLabelTransactionsPending;
    private javax.swing.JLabel jLabelTransactionsSelected;
    private javax.swing.JLabel jLabelTransactionsServed;
    private javax.swing.JPanel jPanelEmpty;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSep4;
    private javax.swing.JPanel jPanelSep5;
    private javax.swing.JPanel jPanelSep6;
    private javax.swing.JPanel jPanelSep7;
    private javax.swing.JPanel jPanelSep8;
    private javax.swing.JPanel jPanelTransactionCount;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private de.mendelson.util.ProgressPanel progressPanel;
    // End of variables declaration//GEN-END:variables

    public class ConfigurationCheckThread implements Runnable {

        public ConfigurationCheckThread() {
        }

        @Override
        public void run() {
            try {
                ConfigurationCheckResponse response = (ConfigurationCheckResponse) baseClient.sendSync(new ConfigurationCheckRequest());
                final int issueCount = response.getIssues().size();
                if (issueCount == 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            jLabelConfigurationIssue.setIcon(null);
                            String text = rb.getResourceString("no.configuration.issues");
                            jLabelConfigurationIssue.setText(text);
                            int labelWidth = computeStringWidth(text) + 10;
                            jLabelConfigurationIssue.setPreferredSize(new Dimension(labelWidth, ICON_HEIGHT));
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String text;
                            if (issueCount > 1) {
                                text = rb.getResourceString("configuration.issue.multiple", String.valueOf(issueCount));
                            } else {
                                text = rb.getResourceString("configuration.issue.single", String.valueOf(issueCount));
                            }
                            jLabelConfigurationIssue.setText(text);
                            jLabelConfigurationIssue.setIcon(new ImageIcon(IMAGE_WARNING.toMinResolution(ICON_HEIGHT)));
                            //contents with some gap result in the label width
                            final int labelWidth = computeStringWidth(text)
                                    + new ImageIcon(IMAGE_WARNING.toMinResolution(ICON_HEIGHT)).getIconWidth() + 10;
                            jLabelConfigurationIssue.setPreferredSize(new Dimension(labelWidth, ICON_HEIGHT));
                        }
                    });
                }
            } catch (Throwable e) {
                //nop
            }
        }

        /**
         * Compute the width of the content up to the actual cursor position not
         * been found on the OS
         */
        private int computeStringWidth(String text) {
            Graphics2D g = (Graphics2D) jLabelConfigurationIssue.getGraphics();
            FontMetrics metrics = g.getFontMetrics(jLabelConfigurationIssue.getFont());
            return ((int) Math.ceil(metrics.getStringBounds(text, g).getWidth()));
        }

    }

}
