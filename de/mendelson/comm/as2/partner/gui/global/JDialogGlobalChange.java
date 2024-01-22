//$Header: /as2/de/mendelson/comm/as2/partner/gui/global/JDialogGlobalChange.java 3     26/08/22 14:26 Heller $
package de.mendelson.comm.as2.partner.gui.global;

import de.mendelson.comm.as2.client.AS2Gui;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.preferences.PreferencesPanelMDN;
import de.mendelson.util.JTextFieldLimitDocument;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.uinotification.UINotification;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
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
 * Allows to select a partner and sends a certificate to him via your mail
 * application
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class JDialogGlobalChange extends JDialog {

    private final static MendelsonMultiResolutionImage IMAGE_SET
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/partner/gui/global/set_profile_defaults.svg",
                    AS2Gui.IMAGE_SIZE_TOOLBAR);
    private final static MendelsonMultiResolutionImage IMAGE_PARTNER_GROUP
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/partner/gui/global/partner_group.svg",
                    AS2Gui.IMAGE_SIZE_DIALOG);

    private final MecResourceBundle rb;
    private Logger logger = Logger.getLogger("de.mendelson.as2.client");
    private List<Partner> partnerList;

    public JDialogGlobalChange(JFrame parent, List<Partner> partnerList) {
        super(parent, true);
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleGlobalChange.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        initComponents();
        this.setMultiresolutionIcons();
        this.partnerList = partnerList;
        this.jTextFieldMaxPollFiles.setDocument(new JTextFieldLimitDocument(4));
        this.jTextFieldMaxPollFiles.setText("100");
        this.jTextFieldPollInterval.setDocument(new JTextFieldLimitDocument(5));
        this.jTextFieldPollInterval.setText("30");
    }

    private void setMultiresolutionIcons() {
        this.jLabelIcon.setIcon(new ImageIcon(IMAGE_PARTNER_GROUP.toMinResolution(AS2Gui.IMAGE_SIZE_DIALOG)));
        this.jButtonSetPollEnabled.setIcon(new ImageIcon(IMAGE_SET.toMinResolution(AS2Gui.IMAGE_SIZE_TOOLBAR)));
        this.jButtonSetPollInterval.setIcon(new ImageIcon(IMAGE_SET.toMinResolution(AS2Gui.IMAGE_SIZE_TOOLBAR)));
        this.jButtonSetMaxPollFiles.setIcon(new ImageIcon(IMAGE_SET.toMinResolution(AS2Gui.IMAGE_SIZE_TOOLBAR)));
    }

    private void performChangeErrorNotification() {
            UINotification.instance().addNotification(
                    IMAGE_SET,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("title"),
                    this.rb.getResourceString("partnersetting.notchanged"));
    }
    
    
    private void performChangeNotification(int changeCount) {
        if (changeCount > 0) {
            UINotification.instance().addNotification(
                    IMAGE_SET,
                    UINotification.TYPE_SUCCESS,
                    this.rb.getResourceString("title"),
                    this.rb.getResourceString("partnersetting.changed", String.valueOf(changeCount)));
        } else {
            UINotification.instance().addNotification(
                    IMAGE_SET,
                    UINotification.TYPE_WARNING,
                    this.rb.getResourceString("title"),
                    this.rb.getResourceString("partnersetting.changed", String.valueOf(changeCount)));
        }
    }

    private void setPollToAllPartner() {
        boolean performPoll = this.jCheckBoxSetPollEnabled.isSelected();
        int changeCount = 0;
        for (Partner partner : this.partnerList) {
            if (partner.isLocalStation()) {
                continue;
            }
            if (partner.isEnableDirPoll() != performPoll) {
                changeCount++;
            }
            partner.setEnableDirPoll(performPoll);
        }
        this.performChangeNotification(changeCount);
    }

    private void setMaxPollFilesToAllPartner() {
        int maxPollFiles = 100;
        try {
            maxPollFiles = Integer.valueOf(this.jTextFieldMaxPollFiles.getText());
            if( maxPollFiles <= 0){
                throw new Exception();
            }
        } catch (Exception e) {
            this.performChangeErrorNotification();
            return;
        }
        int changeCount = 0;
        for (Partner partner : this.partnerList) {
            if (partner.isLocalStation()) {
                continue;
            }
            if (partner.getMaxPollFiles() != maxPollFiles) {
                changeCount++;
            }
            partner.setMaxPollFiles(maxPollFiles);
        }
        this.performChangeNotification(changeCount);
    }

    private void setPollIntervalInSToAllPartner() {
        int pollIntervalInS = 30;
        try {
            pollIntervalInS = Integer.valueOf(this.jTextFieldPollInterval.getText());
            if( pollIntervalInS <= 0){
                throw new Exception();
            }
        } catch (Exception e) {
            this.performChangeErrorNotification();
            return;
        }
        int changeCount = 0;
        for (Partner partner : this.partnerList) {
            if (partner.isLocalStation()) {
                continue;
            }
            if (partner.getPollInterval() != pollIntervalInS) {
                changeCount++;
            }
            partner.setPollInterval(pollIntervalInS);
        }
        this.performChangeNotification(changeCount);
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
        jLabelIcon = new javax.swing.JLabel();
        jCheckBoxSetPollEnabled = new javax.swing.JCheckBox();
        jButtonSetPollEnabled = new javax.swing.JButton();
        jPanelSpace = new javax.swing.JPanel();
        jLabelInfo = new javax.swing.JLabel();
        jLabelPollInterval = new javax.swing.JLabel();
        jTextFieldPollInterval = new javax.swing.JTextField();
        jLabelSeconds = new javax.swing.JLabel();
        jButtonSetPollInterval = new javax.swing.JButton();
        jPanelHorizontalSpace = new javax.swing.JPanel();
        jLabelPollFiles = new javax.swing.JLabel();
        jTextFieldMaxPollFiles = new javax.swing.JTextField();
        jButtonSetMaxPollFiles = new javax.swing.JButton();
        jPanelHorizontalSpace2 = new javax.swing.JPanel();
        jPanelButton = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(this.rb.getResourceString( "title"));
        setModal(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/cem/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMain.add(jLabelIcon, gridBagConstraints);

        jCheckBoxSetPollEnabled.setText(this.rb.getResourceString("label.dirpoll"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMain.add(jCheckBoxSetPollEnabled, gridBagConstraints);

        jButtonSetPollEnabled.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/partner/gui/global/missing_image24x24.gif"))); // NOI18N
        jButtonSetPollEnabled.setText(this.rb.getResourceString( "button.set")
        );
        jButtonSetPollEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetPollEnabledActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 20);
        jPanelMain.add(jButtonSetPollEnabled, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelMain.add(jPanelSpace, gridBagConstraints);

        jLabelInfo.setText(this.rb.getResourceString( "info.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMain.add(jLabelInfo, gridBagConstraints);

        jLabelPollInterval.setText(this.rb.getResourceString("label.pollinterval"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        jPanelMain.add(jLabelPollInterval, gridBagConstraints);

        jTextFieldPollInterval.setText("30");
        jTextFieldPollInterval.setMinimumSize(new java.awt.Dimension(40, 22));
        jTextFieldPollInterval.setPreferredSize(new java.awt.Dimension(40, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelMain.add(jTextFieldPollInterval, gridBagConstraints);

        jLabelSeconds.setText("s");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMain.add(jLabelSeconds, gridBagConstraints);

        jButtonSetPollInterval.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/partner/gui/global/missing_image24x24.gif"))); // NOI18N
        jButtonSetPollInterval.setText(this.rb.getResourceString( "button.set"));
        jButtonSetPollInterval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetPollIntervalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 20);
        jPanelMain.add(jButtonSetPollInterval, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMain.add(jPanelHorizontalSpace, gridBagConstraints);

        jLabelPollFiles.setText(this.rb.getResourceString("label.maxpollfiles"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        jPanelMain.add(jLabelPollFiles, gridBagConstraints);

        jTextFieldMaxPollFiles.setText("50");
        jTextFieldMaxPollFiles.setMinimumSize(new java.awt.Dimension(40, 22));
        jTextFieldMaxPollFiles.setPreferredSize(new java.awt.Dimension(40, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelMain.add(jTextFieldMaxPollFiles, gridBagConstraints);

        jButtonSetMaxPollFiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/partner/gui/global/missing_image24x24.gif"))); // NOI18N
        jButtonSetMaxPollFiles.setText(this.rb.getResourceString( "button.set"));
        jButtonSetMaxPollFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetMaxPollFilesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 20);
        jPanelMain.add(jButtonSetMaxPollFiles, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        jPanelMain.add(jPanelHorizontalSpace2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelMain, gridBagConstraints);

        jPanelButton.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(this.rb.getResourceString( "button.ok"));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButton.add(jButtonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButton, gridBagConstraints);

        setSize(new java.awt.Dimension(557, 446));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonSetPollEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetPollEnabledActionPerformed
        this.setPollToAllPartner();
    }//GEN-LAST:event_jButtonSetPollEnabledActionPerformed

    private void jButtonSetPollIntervalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetPollIntervalActionPerformed
        this.setPollIntervalInSToAllPartner();
    }//GEN-LAST:event_jButtonSetPollIntervalActionPerformed

    private void jButtonSetMaxPollFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetMaxPollFilesActionPerformed
        this.setMaxPollFilesToAllPartner();
    }//GEN-LAST:event_jButtonSetMaxPollFilesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonSetMaxPollFiles;
    private javax.swing.JButton jButtonSetPollEnabled;
    private javax.swing.JButton jButtonSetPollInterval;
    private javax.swing.JCheckBox jCheckBoxSetPollEnabled;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelPollFiles;
    private javax.swing.JLabel jLabelPollInterval;
    private javax.swing.JLabel jLabelSeconds;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelHorizontalSpace;
    private javax.swing.JPanel jPanelHorizontalSpace2;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JTextField jTextFieldMaxPollFiles;
    private javax.swing.JTextField jTextFieldPollInterval;
    // End of variables declaration//GEN-END:variables
}
