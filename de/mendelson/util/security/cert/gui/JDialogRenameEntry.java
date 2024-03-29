//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogRenameEntry.java 12    15/12/22 10:27 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.uinotification.UINotification;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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
 * Dialog to configure a single partner
 *
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class JDialogRenameEntry extends JDialog {

    private final MecResourceBundle rb;
    private final CertificateManager manager;
    private final String oldAlias;
    private String newAlias = null;
    private final String keystoreType;

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param parameter Parameter to edit, null for a new one
     * @param parameterList List of available parameter
     */
    public JDialogRenameEntry(JFrame parent, CertificateManager manager,
            String oldAlias, String keystoreType, char[] keystorepass) {
        super(parent, true);
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleRenameEntry.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.keystoreType = keystoreType;
        this.setTitle(this.rb.getResourceString("title", oldAlias));
        initComponents();
        TextOverlay.addTo(this.jTextFieldNewAlias, this.rb.getResourceString("label.newalias.hint"));
        PasswordOverlay.addTo(this.jPasswordFieldKeyPairPass);
        this.manager = manager;
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.oldAlias = oldAlias;
        //PKCS#12 has no key pair passwords
        this.jLabelKeyPairPass.setEnabled(this.keystoreType.equals(BCCryptoHelper.KEYSTORE_JKS));
        this.jPasswordFieldKeyPairPass.setEditable(this.keystoreType.equals(BCCryptoHelper.KEYSTORE_JKS));
        this.jPasswordFieldKeyPairPass.setEnabled(this.keystoreType.equals(BCCryptoHelper.KEYSTORE_JKS));
        if (this.keystoreType.equals(BCCryptoHelper.KEYSTORE_JKS)) {
            this.jPasswordFieldKeyPairPass.setText(new String(keystorepass));
        }
        //request the focus on the new alias field
        this.jTextFieldNewAlias.setText(oldAlias);
        this.jTextFieldNewAlias.requestFocusInWindow();
        this.jTextFieldNewAlias.selectAll();
        this.jLabelIcon.setIcon(new ImageIcon(JDialogCertificates.IMAGE_EDIT_MULTIRESOLUTION.toMinResolution(32)));
        this.setButtonState();
    }

    /**
     * Sets the ok and cancel buttons of this GUI
     */
    private void setButtonState() {
        String newAliasTmp = this.jTextFieldNewAlias.getText();
        this.jButtonOk.setEnabled(
                newAliasTmp.length() > 0
                && !this.oldAlias.equals(newAliasTmp));
    }

    private boolean aliasDoesAlreadyExist(String newAlias) {
        List<KeystoreCertificate> list = this.manager.getKeyStoreCertificateList();
        for (KeystoreCertificate certificate : list) {
            if (certificate.getAlias().equalsIgnoreCase(newAlias.trim())) {
                return (true);
            }
        }
        return (false);
    }

    public String getNewAlias() {
        return (this.newAlias);
    }

    /**
     * Performs the rename process
     */
    private void performRename() {
        try {
            char[] keypairPass = null;
            if (this.keystoreType.equals(BCCryptoHelper.KEYSTORE_JKS)) {
                keypairPass = this.jPasswordFieldKeyPairPass.getPassword();
            }
            this.manager.renameAlias(this.oldAlias, this.jTextFieldNewAlias.getText(), keypairPass);
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
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
        jLabelNewAlias = new javax.swing.JLabel();
        jTextFieldNewAlias = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabelKeyPairPass = new javax.swing.JLabel();
        jPasswordFieldKeyPairPass = new javax.swing.JPasswordField();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 20, 10);
        jPanelEdit.add(jLabelIcon, gridBagConstraints);

        jLabelNewAlias.setText(this.rb.getResourceString( "label.newalias"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelNewAlias, gridBagConstraints);

        jTextFieldNewAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNewAliasKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        jPanelEdit.add(jTextFieldNewAlias, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelEdit.add(jPanel3, gridBagConstraints);

        jLabelKeyPairPass.setText(this.rb.getResourceString( "label.keypairpass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelKeyPairPass, gridBagConstraints);

        jPasswordFieldKeyPairPass.setMinimumSize(new java.awt.Dimension(150, 20));
        jPasswordFieldKeyPairPass.setPreferredSize(new java.awt.Dimension(150, 20));
        jPasswordFieldKeyPairPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordFieldKeyPairPassKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        jPanelEdit.add(jPasswordFieldKeyPairPass, gridBagConstraints);

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

        jButtonCancel.setText(this.rb.getResourceString( "button.cancel" ));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);

        setSize(new java.awt.Dimension(449, 276));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldNewAliasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNewAliasKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldNewAliasKeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.newAlias = this.oldAlias;
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        String newAliasTemp = this.jTextFieldNewAlias.getText();
        if (!this.aliasDoesAlreadyExist(newAliasTemp)) {
            this.performRename();
            this.newAlias = this.jTextFieldNewAlias.getText();
            this.setVisible(false);
        } else {
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("alias.exists.title"),
                    this.rb.getResourceString("alias.exists.message", newAliasTemp));
        }
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jPasswordFieldKeyPairPassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldKeyPairPassKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jPasswordFieldKeyPairPassKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelKeyPairPass;
    private javax.swing.JLabel jLabelNewAlias;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPasswordField jPasswordFieldKeyPairPass;
    private javax.swing.JTextField jTextFieldNewAlias;
    // End of variables declaration//GEN-END:variables
}
