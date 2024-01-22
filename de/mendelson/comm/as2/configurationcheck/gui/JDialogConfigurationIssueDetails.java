//$Header: /as2/de/mendelson/comm/as2/configurationcheck/gui/JDialogConfigurationIssueDetails.java 11    24/11/22 10:22 Heller $
package de.mendelson.comm.as2.configurationcheck.gui;

import de.mendelson.comm.as2.client.ModuleStarter;
import de.mendelson.comm.as2.configurationcheck.ConfigurationIssue;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.ColorUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.Color;
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * This is a dialog that contains additional information about a single issue
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class JDialogConfigurationIssueDetails extends JDialog {

    private final static MendelsonMultiResolutionImage IMAGE_WARNING_SIGN
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/configurationcheck/gui/warning_sign.svg", 38, 60);

    private ModuleStarter moduleStarter;
    private List<ConfigurationIssue> issueList;
    private int currentIssueIndex;
    private MecResourceBundle rbIssueDetails;

    /**
     * Creates new form JDialogConfigurationIssueDetails
     */
    public JDialogConfigurationIssueDetails(Frame parent, ModuleStarter moduleStarter, List<ConfigurationIssue> issueList,
            int issueIndex) {
        super(parent, true);
        //load resource bundle
        try {
            this.rbIssueDetails = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleConfigurationIssueDetails.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.issueList = issueList;
        this.currentIssueIndex = issueIndex;
        this.moduleStarter = moduleStarter;
        initComponents();
        this.jLabelIcon.setIcon(new ImageIcon(IMAGE_WARNING_SIGN));
        this.jLabelIssueShortDescription.setForeground(Color.BLACK);
        this.displayCurrentIssue();
        this.getRootPane().setDefaultButton(this.jButtonClose);
        //bail out on ESC
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        jButtonClose.doClick();
                    }
                }
                return false;
            }
        });
    }

    private void displayCurrentIssue() {
        ConfigurationIssue selectedIssue = this.issueList.get(this.currentIssueIndex);
        this.setTitle(this.rbIssueDetails.getResourceString("title",
                new Object[]{
                    String.valueOf(this.currentIssueIndex + 1),
                    String.valueOf(this.issueList.size())
                }));
        String shortDescription = selectedIssue.getSubject();
        if( selectedIssue.getDetails() != null && selectedIssue.getDetails().trim().length() > 0 ){
            shortDescription = shortDescription + " (" + selectedIssue.getDetails() + ")";
        }
        String longDescription = selectedIssue.getHintAsHTML();
        this.jLabelIssueShortDescription.setText("<HTML>" + AS2Tools.fold(shortDescription, "<br>", 80) + "</HTML>");
        this.jLabelIssueLongDescription.setText(longDescription);
        this.setButtonState();
    }

    private void setButtonState() {
        this.jButtonNextIssue.setVisible(this.issueList.size() > 1);
        ConfigurationIssue selectedIssue = this.issueList.get(this.currentIssueIndex);
        this.jButtonJumpToIssue.setEnabled(selectedIssue.hasJumpTargetInUI());
        switch (selectedIssue.getIssueId()) {
            case ConfigurationIssue.CERTIFICATE_EXPIRED_ENC_SIGN:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.keystore"));
                break;
            case ConfigurationIssue.CERTIFICATE_EXPIRED_TLS:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.keystore"));
                break;
            case ConfigurationIssue.NO_KEY_IN_TLS_KEYSTORE:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.keystore"));
                break;
            case ConfigurationIssue.MULTIPLE_KEYS_IN_TLS_KEYSTORE:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.keystore"));
                break;
            case ConfigurationIssue.USE_OF_TEST_KEYS_IN_TLS:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.keystore"));
                break;
            case ConfigurationIssue.HUGE_AMOUNT_OF_TRANSACTIONS_NO_AUTO_DELETE:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.config"));
                break;
            case ConfigurationIssue.NO_OUTBOUND_CONNECTIONS_ALLOWED:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.config"));
                break;
            case ConfigurationIssue.CERTIFICATE_MISSING_ENC_REMOTE_PARTNER:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.partner"));
                break;
            case ConfigurationIssue.CERTIFICATE_MISSING_SIGN_REMOTE_PARTNER:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.partner"));
                break;
            case ConfigurationIssue.KEY_MISSING_ENC_LOCAL_STATION:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.partner"));
                break;
            case ConfigurationIssue.KEY_MISSING_SIGN_LOCAL_STATION:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.partner"));
                break;
            case ConfigurationIssue.DIFFERENT_KEYSTORES_TLS:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.config"));
                break;    
            default:
                this.jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString("button.jumpto.generic"));
                break;
        }
    }

    private void jumpToIssue() {
        this.setVisible(false);
        ConfigurationIssue selectedIssue = this.issueList.get(this.currentIssueIndex);
        switch (selectedIssue.getIssueId()) {
            case ConfigurationIssue.CERTIFICATE_EXPIRED_ENC_SIGN:
                this.moduleStarter.displayCertificateManagerEncSign(selectedIssue.getDetails());
                break;
            case ConfigurationIssue.CERTIFICATE_EXPIRED_TLS:
                this.moduleStarter.displayCertificateManagerSSL(selectedIssue.getDetails());
                break;
            case ConfigurationIssue.NO_KEY_IN_TLS_KEYSTORE:
                this.moduleStarter.displayCertificateManagerSSL(null);
                break;
            case ConfigurationIssue.MULTIPLE_KEYS_IN_TLS_KEYSTORE:
                this.moduleStarter.displayCertificateManagerSSL(null);
                break;
            case ConfigurationIssue.USE_OF_TEST_KEYS_IN_TLS:
                this.moduleStarter.displayCertificateManagerSSL(selectedIssue.getDetails());
                break;
            case ConfigurationIssue.HUGE_AMOUNT_OF_TRANSACTIONS_NO_AUTO_DELETE:
                this.moduleStarter.displayPreferences("tab.maintenance");
                break;
            case ConfigurationIssue.NO_OUTBOUND_CONNECTIONS_ALLOWED:
                this.moduleStarter.displayPreferences("tab.misc");
                break;
            case ConfigurationIssue.CERTIFICATE_MISSING_ENC_REMOTE_PARTNER:
                this.moduleStarter.displayPartnerManager(selectedIssue.getDetails());
                break;
            case ConfigurationIssue.CERTIFICATE_MISSING_SIGN_REMOTE_PARTNER:
                this.moduleStarter.displayPartnerManager(selectedIssue.getDetails());
                break;
            case ConfigurationIssue.KEY_MISSING_ENC_LOCAL_STATION:
                this.moduleStarter.displayPartnerManager(selectedIssue.getDetails());
                break;
            case ConfigurationIssue.KEY_MISSING_SIGN_LOCAL_STATION:
                this.moduleStarter.displayPartnerManager(selectedIssue.getDetails());
                break;
            case ConfigurationIssue.DIFFERENT_KEYSTORES_TLS:
                this.moduleStarter.displayPreferences("tab.security");
                break;    
        }
    }

    private void displayNextIssue() {
        this.currentIssueIndex++;
        if (this.currentIssueIndex > this.issueList.size() - 1) {
            this.currentIssueIndex = 0;
        }
        this.displayCurrentIssue();
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

        jPanelMargin = new javax.swing.JPanel();
        jPanelMain = new javax.swing.JPanel();
        jPanelShortDescription = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jLabelIssueShortDescription = new javax.swing.JLabel();
        jPanelLongDescription = new javax.swing.JPanel();
        jLabelIssueLongDescription = new javax.swing.JLabel();
        jPanelButtons = new javax.swing.JPanel();
        jPanelButton2 = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jButtonJumpToIssue = new javax.swing.JButton();
        jPanelButton3 = new javax.swing.JPanel();
        jButtonNextIssue = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelMargin.setLayout(new java.awt.GridBagLayout());

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jPanelShortDescription.setBackground(new java.awt.Color(255, 255, 255));
        jPanelShortDescription.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanelShortDescription.setLayout(new java.awt.GridBagLayout());

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/configurationcheck/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelShortDescription.add(jLabelIcon, gridBagConstraints);

        jLabelIssueShortDescription.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelIssueShortDescription.setText("<Short description>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        jPanelShortDescription.add(jLabelIssueShortDescription, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMain.add(jPanelShortDescription, gridBagConstraints);

        jPanelLongDescription.setLayout(new java.awt.GridBagLayout());

        jLabelIssueLongDescription.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabelIssueLongDescription.setText("<Long description>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelLongDescription.add(jLabelIssueLongDescription, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMain.add(jPanelLongDescription, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMargin.add(jPanelMain, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelMargin, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jPanelButton2.setLayout(new java.awt.GridBagLayout());

        jButtonClose.setText(this.rbIssueDetails.getResourceString( "button.close"));
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButton2.add(jButtonClose, gridBagConstraints);

        jButtonJumpToIssue.setText(this.rbIssueDetails.getResourceString( "button.jumpto.generic"));
        jButtonJumpToIssue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonJumpToIssueActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButton2.add(jButtonJumpToIssue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelButtons.add(jPanelButton2, gridBagConstraints);

        jPanelButton3.setLayout(new java.awt.GridBagLayout());

        jButtonNextIssue.setText(this.rbIssueDetails.getResourceString( "button.next"));
        jButtonNextIssue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextIssueActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButton3.add(jButtonNextIssue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanelButtons.add(jPanelButton3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);

        setSize(new java.awt.Dimension(706, 429));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonJumpToIssueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonJumpToIssueActionPerformed
        this.jumpToIssue();
    }//GEN-LAST:event_jButtonJumpToIssueActionPerformed

    private void jButtonNextIssueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextIssueActionPerformed
        this.displayNextIssue();
    }//GEN-LAST:event_jButtonNextIssueActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonJumpToIssue;
    private javax.swing.JButton jButtonNextIssue;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelIssueLongDescription;
    private javax.swing.JLabel jLabelIssueShortDescription;
    private javax.swing.JPanel jPanelButton2;
    private javax.swing.JPanel jPanelButton3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelLongDescription;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelMargin;
    private javax.swing.JPanel jPanelShortDescription;
    // End of variables declaration//GEN-END:variables
}
