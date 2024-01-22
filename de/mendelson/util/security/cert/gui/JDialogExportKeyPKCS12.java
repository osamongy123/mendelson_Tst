//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogExportKeyPKCS12.java 19    26/09/22 10:19 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.MecFileChooser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.JKSKeys2PKCS12;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.PKCS122PKCS12;
import de.mendelson.util.uinotification.UINotification;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Export a private key into a pkcs#12 keystore
 *
 * @author S.Heller
 * @version $Revision: 19 $
 */
public class JDialogExportKeyPKCS12 extends JDialog {

    /**
     * ResourceBundle to localize the GUI
     */
    private MecResourceBundle rb = null;
    private CertificateManager manager = null;
    private Logger logger = null;

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param manager Manager that handles the certificates
     */
    public JDialogExportKeyPKCS12(JFrame parent, Logger logger, CertificateManager manager,
            String selectedAlias) throws Exception {
        super(parent, true);
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleExportKeyPKCS12.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.logger = logger;
        this.setTitle(this.rb.getResourceString("title"));
        initComponents();
        PasswordOverlay.addTo(this.jPasswordFieldPassphrase, this.rb.getResourceString("label.keypass.hint"));
        TextOverlay.addTo(this.jTextFieldExportPKCS12File, this.rb.getResourceString("label.exportkey.hint"));
        this.jLabelIcon.setIcon(new ImageIcon(JDialogCertificates.IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(32)));
        this.manager = manager;
        this.populateAliasList(selectedAlias);
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.setButtonState();
    }

    private void populateAliasList(String preselection) throws Exception {
        this.jComboBoxAlias.removeAllItems();
        KeyStoreUtil util = new KeyStoreUtil();
        KeyStore sourceKeystore = this.manager.getKeystore();
        List<String> keyAliasesList = util.getKeyAliases(sourceKeystore);
        if (keyAliasesList.isEmpty()) {
            throw new Exception(this.rb.getResourceString("keystore.contains.nokeys"));
        } else {
            for (int i = 0; i < keyAliasesList.size(); i++) {
                this.jComboBoxAlias.addItem(keyAliasesList.get(i));
            }
            if (preselection != null) {
                this.jComboBoxAlias.setSelectedItem(preselection);
            } else {
                this.jComboBoxAlias.setSelectedItem(0);
            }
        }
    }

    /**
     * Sets the ok and cancel buttons of this GUI
     */
    private void setButtonState() {
        this.jButtonOk.setEnabled(this.jTextFieldExportPKCS12File.getText().length() > 0
                && this.jPasswordFieldPassphrase.getPassword().length > 0);
    }

    /**
     * Finally import the key
     */
    private void performExportFromPKCS12() {
        try {
            KeyStoreUtil util = new KeyStoreUtil();
            KeyStore sourceKeystore = this.manager.getKeystore();
            String selectedAlias = (String) this.jComboBoxAlias.getSelectedItem();
            KeyStore targetKeystore = KeyStore.getInstance(BCCryptoHelper.KEYSTORE_PKCS12, 
                    BouncyCastleProvider.PROVIDER_NAME);
            util.loadKeyStore(targetKeystore, this.jTextFieldExportPKCS12File.getText(),
                    this.jPasswordFieldPassphrase.getPassword());
            String keystoreFormat = this.manager.getKeystoreType();
            if (keystoreFormat.equals(BCCryptoHelper.KEYSTORE_PKCS12)) {
                PKCS122PKCS12 exporter = new PKCS122PKCS12(this.logger);
                exporter.setTargetKeyStore(targetKeystore, this.jPasswordFieldPassphrase.getPassword());
                exporter.importKey(sourceKeystore, selectedAlias);
                exporter.saveKeyStore(targetKeystore, this.jPasswordFieldPassphrase.getPassword(),
                        Paths.get(this.jTextFieldExportPKCS12File.getText()));
            } else if (keystoreFormat.equals(BCCryptoHelper.KEYSTORE_JKS)) {
                JKSKeys2PKCS12 exporter = new JKSKeys2PKCS12(this.logger);
                exporter.setTargetKeyStore(targetKeystore);
                exporter.exportKey(sourceKeystore, this.manager.getKeystorePass(), selectedAlias);
                exporter.saveKeyStore(targetKeystore, this.jPasswordFieldPassphrase.getPassword(),
                        Paths.get(this.jTextFieldExportPKCS12File.getText()));
            }
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_SUCCESS,
                    this.rb.getResourceString("key.export.success.title"),
                    this.rb.getResourceString("key.export.success.message")
            );
            this.logger.fine(this.rb.getResourceString("key.exported.to.file",
                    new Object[]{
                        selectedAlias,
                        Paths.get(this.jTextFieldExportPKCS12File.getText()).toAbsolutePath().toString()
                    }));
        } catch (Exception e) {
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("key.export.error.title"),
                    this.rb.getResourceString("key.export.error.message", e.getMessage()));
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
        jLabelExportPKCS12File = new javax.swing.JLabel();
        jTextFieldExportPKCS12File = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonBrowseExportFile = new javax.swing.JButton();
        jLabelPassphrase = new javax.swing.JLabel();
        jPasswordFieldPassphrase = new javax.swing.JPasswordField();
        jComboBoxAlias = new javax.swing.JComboBox();
        jLabelAlias = new javax.swing.JLabel();
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

        jLabelExportPKCS12File.setText(this.rb.getResourceString( "label.exportkey"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelExportPKCS12File, gridBagConstraints);

        jTextFieldExportPKCS12File.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldExportPKCS12FileKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldExportPKCS12File, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelEdit.add(jPanel3, gridBagConstraints);

        jButtonBrowseExportFile.setText("..");
        jButtonBrowseExportFile.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowseExportFile.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowseExportFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseExportFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowseExportFile, gridBagConstraints);

        jLabelPassphrase.setText(this.rb.getResourceString( "label.keypass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelPassphrase, gridBagConstraints);

        jPasswordFieldPassphrase.setMinimumSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPassphrase.setPreferredSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPassphrase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordFieldPassphraseKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jPasswordFieldPassphrase, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jComboBoxAlias, gridBagConstraints);

        jLabelAlias.setText(this.rb.getResourceString( "label.alias" ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelAlias, gridBagConstraints);

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

        setSize(new java.awt.Dimension(466, 296));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jPasswordFieldPassphraseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldPassphraseKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jPasswordFieldPassphraseKeyReleased

    private void jButtonBrowseExportFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseExportFileActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        MecFileChooser chooser = new MecFileChooser(
                parent,
                this.rb.getResourceString("filechooser.key.export"));
        chooser.browseFilename(this.jTextFieldExportPKCS12File);
    }//GEN-LAST:event_jButtonBrowseExportFileActionPerformed

    private void jTextFieldExportPKCS12FileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldExportPKCS12FileKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldExportPKCS12FileKeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible(false);
        this.performExportFromPKCS12();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseExportFile;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JComboBox jComboBoxAlias;
    private javax.swing.JLabel jLabelAlias;
    private javax.swing.JLabel jLabelExportPKCS12File;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelPassphrase;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPasswordField jPasswordFieldPassphrase;
    private javax.swing.JTextField jTextFieldExportPKCS12File;
    // End of variables declaration//GEN-END:variables
}
