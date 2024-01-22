//$Header: /oftp2/de/mendelson/util/security/cert/gui/JDialogImportKeyPEM.java 13    24.09.21 10:37 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.MecFileChooser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.PEMKeys2Keystore;
import de.mendelson.util.uinotification.UINotification;
import java.nio.file.Paths;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


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
 * @version $Revision: 13 $
 */
public class JDialogImportKeyPEM extends JDialog {

    /**
     * ResourceBundle to localize the GUI
     */
    private MecResourceBundle rb = null;
    private CertificateManager manager = null;
    private String newAlias = null;
    private String keystoreType;

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param manager Manager that handles the certificates
     */
    public JDialogImportKeyPEM(JFrame parent, CertificateManager manager, String keystoreType) {
        super(parent, true);
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleImportKeyPEM.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.setTitle(this.rb.getResourceString("title"));
        this.keystoreType = keystoreType;
        initComponents();
        PasswordOverlay.addTo(this.jPasswordFieldPEMPassphrase,
                this.rb.getResourceString( "label.keypass.hint"));
        TextOverlay.addTo( this.jTextFieldNewAlias, this.rb.getResourceString( "label.alias.hint"));
        TextOverlay.addTo( this.jTextFieldImportCert, this.rb.getResourceString( "label.importcert.hint"));
        TextOverlay.addTo(this.jTextFieldImportKeyFile, this.rb.getResourceString( "label.importkey.hint"));
        this.jLabelIcon.setIcon(new ImageIcon(JDialogCertificates.IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(32)));
        this.manager = manager;
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.setButtonState();
    }

    public String getNewAlias() {
        return (this.newAlias);
    }

    /**
     * Sets the ok and cancel buttons of this GUI
     */
    private void setButtonState() {
        this.jButtonOk.setEnabled(this.jTextFieldImportCert.getText().length() > 0
                && this.jTextFieldImportKeyFile.getText().length() > 0
                && this.jTextFieldNewAlias.getText().length() > 0);
    }

    /**
     * Finally import the key
     */
    private void performImport() {
        try {
            PEMKeys2Keystore importer;
            if (this.keystoreType.equals(BCCryptoHelper.KEYSTORE_PKCS12)) {
                importer = new PEMKeys2Keystore(Logger.getLogger("de.mendelson.as2.client"), BCCryptoHelper.KEYSTORE_PKCS12);
            } else if (this.keystoreType.equals(BCCryptoHelper.KEYSTORE_JKS)) {
                importer = new PEMKeys2Keystore(Logger.getLogger("de.mendelson.as2.client"), BCCryptoHelper.KEYSTORE_JKS);
            } else {
                throw new IllegalArgumentException("JDialogImportKeyPEM:performImport: Unsupported keystore type " + this.keystoreType);
            }
            importer.setTargetKeyStore(this.manager.getKeystore(), this.manager.getKeystorePass());
            importer.importKey(Paths.get(this.jTextFieldImportKeyFile.getText()),
                    this.jPasswordFieldPEMPassphrase.getPassword(),
                    this.manager.getKeystorePass(),
                    Paths.get(this.jTextFieldImportCert.getText()),
                    this.jTextFieldNewAlias.getText());
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_SUCCESS,
                    this.rb.getResourceString("key.import.success.title"),
                    this.rb.getResourceString("key.import.success.message"));
        } catch (Exception e) {
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("key.import.error.title"),
                    this.rb.getResourceString("key.import.error.message", e.getMessage()));
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
        jLabelImportPEMFile = new javax.swing.JLabel();
        jTextFieldImportKeyFile = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonBrowseImportPEMFile = new javax.swing.JButton();
        jLabelImportCert = new javax.swing.JLabel();
        jTextFieldImportCert = new javax.swing.JTextField();
        jButtonBrowseImportCert = new javax.swing.JButton();
        jLabelImportPEMPassphrase = new javax.swing.JLabel();
        jPasswordFieldPEMPassphrase = new javax.swing.JPasswordField();
        jLabelNewAlias = new javax.swing.JLabel();
        jTextFieldNewAlias = new javax.swing.JTextField();
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

        jLabelImportPEMFile.setText(this.rb.getResourceString( "label.importkey"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelImportPEMFile, gridBagConstraints);

        jTextFieldImportKeyFile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldImportKeyFileKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldImportKeyFile, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelEdit.add(jPanel3, gridBagConstraints);

        jButtonBrowseImportPEMFile.setText("..");
        jButtonBrowseImportPEMFile.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowseImportPEMFile.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowseImportPEMFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseImportPEMFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowseImportPEMFile, gridBagConstraints);

        jLabelImportCert.setText(this.rb.getResourceString( "label.importcert"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelImportCert, gridBagConstraints);

        jTextFieldImportCert.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldImportCertKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldImportCert, gridBagConstraints);

        jButtonBrowseImportCert.setText("..");
        jButtonBrowseImportCert.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowseImportCert.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowseImportCert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseImportCertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowseImportCert, gridBagConstraints);

        jLabelImportPEMPassphrase.setText(this.rb.getResourceString( "label.keypass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelImportPEMPassphrase, gridBagConstraints);

        jPasswordFieldPEMPassphrase.setMinimumSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPEMPassphrase.setPreferredSize(new java.awt.Dimension(150, 20));
        jPasswordFieldPEMPassphrase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordFieldPEMPassphraseKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jPasswordFieldPEMPassphrase, gridBagConstraints);

        jLabelNewAlias.setText(this.rb.getResourceString( "label.alias"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelNewAlias, gridBagConstraints);

        jTextFieldNewAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNewAliasKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldNewAlias, gridBagConstraints);

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

        setSize(new java.awt.Dimension(545, 346));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBrowseImportCertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseImportCertActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        MecFileChooser chooser = new MecFileChooser(parent, this.rb.getResourceString("filechooser.cert.import"));
        chooser.browseFilename(this.jTextFieldImportCert);
    }//GEN-LAST:event_jButtonBrowseImportCertActionPerformed

    private void jTextFieldNewAliasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNewAliasKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldNewAliasKeyReleased

    private void jTextFieldImportCertKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldImportCertKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldImportCertKeyReleased

    private void jPasswordFieldPEMPassphraseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldPEMPassphraseKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jPasswordFieldPEMPassphraseKeyReleased

    private void jButtonBrowseImportPEMFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseImportPEMFileActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        MecFileChooser chooser = new MecFileChooser(parent, this.rb.getResourceString("filechooser.key.import"));
        chooser.browseFilename(this.jTextFieldImportKeyFile);
    }//GEN-LAST:event_jButtonBrowseImportPEMFileActionPerformed

    private void jTextFieldImportKeyFileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldImportKeyFileKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldImportKeyFileKeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.newAlias = this.jTextFieldNewAlias.getText();
        this.setVisible(false);
        this.performImport();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseImportCert;
    private javax.swing.JButton jButtonBrowseImportPEMFile;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelImportCert;
    private javax.swing.JLabel jLabelImportPEMFile;
    private javax.swing.JLabel jLabelImportPEMPassphrase;
    private javax.swing.JLabel jLabelNewAlias;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPasswordField jPasswordFieldPEMPassphrase;
    private javax.swing.JTextField jTextFieldImportCert;
    private javax.swing.JTextField jTextFieldImportKeyFile;
    private javax.swing.JTextField jTextFieldNewAlias;
    // End of variables declaration//GEN-END:variables
}
