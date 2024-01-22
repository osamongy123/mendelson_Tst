//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogExportCertificate.java 21    18/11/22 15:00 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.MecFileChooser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.uinotification.UINotification;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertPath;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Dialog to configure a single partner
 *
 * @author S.Heller
 * @version $Revision: 21 $
 */
public class JDialogExportCertificate extends JDialog {

    /**
     * ResourceBundle to localize the GUI
     */
    private MecResourceBundle rb = null;
    protected static final String PEM = "PEM";
    protected static final String DER = "DER";
    protected static final String PKCS7 = "PKCS#7";
    protected static final String SSH2 = "SSH2";
    private CertificateManager manager = null;
    private Logger logger = Logger.getAnonymousLogger();

    /**
     * @param manager Manages all certificates
     */
    public JDialogExportCertificate(JFrame parent, CertificateManager manager,
            String selectedAlias, Logger logger) {
        super(parent, true);
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleExportCertificate.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.setTitle(this.rb.getResourceString("title"));
        initComponents();
        TextOverlay.addTo(jTextFieldExportFile,
                this.rb.getResourceString("label.exportfile.hint"));
        this.jLabelIcon.setIcon(new ImageIcon(JDialogCertificates.IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(32)));
        this.manager = manager;
        this.getRootPane().setDefaultButton(this.jButtonOk);
        //fill data into comboboxes
        this.jComboBoxExportFormat.addItem(new ExportFormat(DER));
        this.jComboBoxExportFormat.addItem(new ExportFormat(PEM));
        this.jComboBoxExportFormat.addItem(new ExportFormat(PKCS7));
        this.jComboBoxExportFormat.addItem(new ExportFormat(SSH2));
        List<KeystoreCertificate> list = this.manager.getKeyStoreCertificateList();
        for (KeystoreCertificate cert : list) {
            this.jComboBoxAlias.addItem(cert.getAlias());
        }
        this.jComboBoxAlias.setSelectedItem(selectedAlias);
        this.setButtonState();
    }

    /**
     * Sets the ok and cancel buttons of this GUI
     */
    private void setButtonState() {
        this.jButtonOk.setEnabled(this.jTextFieldExportFile.getText().length() > 0);
    }

    /**
     * Compute the whole trust chain for pkcs#7 export
     */
    private List<X509Certificate> computeTrustChain(String alias) {
        KeystoreCertificate certificate = this.manager.getKeystoreCertificate(alias);
        PKIXCertPathBuilderResult result = certificate.getPKIXCertPathBuilderResult(this.manager.getKeystore(), this.manager.getX509CertificateList());
        List<X509Certificate> list = new ArrayList<X509Certificate>();
        //self signed?
        if (result == null) {
            //it's a self signed certificate: return it without any CA/intermediate certs
            list.add(certificate.getX509Certificate());
        } else {
            //trusted cert
            CertPath path = result.getCertPath();
            for (Object cert : path.getCertificates()) {
                list.add(0, (X509Certificate) cert);
            }
            X509Certificate anchorCertX509 = list.get(0);
            boolean trustChainComplete = false;
            while (!trustChainComplete) {
                KeystoreCertificate keyCertAnchor = null;
                //find out the keystore cert of the anchor
                for (KeystoreCertificate keyCert : this.manager.getKeyStoreCertificateList()) {
                    if (keyCert.getX509Certificate().equals(anchorCertX509)) {
                        keyCertAnchor = keyCert;
                        break;
                    }
                }
                if (keyCertAnchor != null) {
                    //check if the anchor has another anchor as intermediates certificate may have the attribute "CA:true", too
                    result = keyCertAnchor.getPKIXCertPathBuilderResult(this.manager.getKeystore(), this.manager.getX509CertificateList());
                    anchorCertX509 = result.getTrustAnchor().getTrustedCert();
                    if (!keyCertAnchor.getX509Certificate().equals(anchorCertX509)) {
                        list.add(0, anchorCertX509);
                    } else {
                        trustChainComplete = true;
                    }
                } else {
                    trustChainComplete = true;
                }
            }
        }
        return (list);
    }

    /**
     * Finally exports the certificate
     */
    private void performExport() {
        KeyStoreUtil util = new KeyStoreUtil();
        try {
            String alias = this.jComboBoxAlias.getSelectedItem().toString();
            String exportFilename = this.jTextFieldExportFile.getText();
            ExportFormat exportFormat = (ExportFormat) this.jComboBoxExportFormat.getSelectedItem();
            if (exportFormat.getType().equals(PEM)) {
                if (!exportFilename.toLowerCase().endsWith(".cer")) {
                    exportFilename += ".cer";
                }
                util.exportX509CertificatePEM(
                        this.manager.getKeystore(), alias, exportFilename);
            } else if (exportFormat.getType().equals(DER)) {
                if (!exportFilename.toLowerCase().endsWith(".cer")) {
                    exportFilename += ".cer";
                }
                util.exportX509CertificateDER(this.manager.getKeystore(), alias, exportFilename);
            } else if (exportFormat.getType().equals(PKCS7)) {
                if (!exportFilename.toLowerCase().endsWith(".p7b")) {
                    exportFilename += ".p7b";
                }
                List<X509Certificate> list = this.computeTrustChain(alias);
                X509Certificate[] certArray = new X509Certificate[list.size()];
                list.toArray(certArray);
                Path[] files = util.exportX509CertificatePKCS7(certArray, exportFilename);
            } else if (exportFormat.getType().equals(SSH2)) {
                if (!exportFilename.toLowerCase().endsWith(".pub")) {
                    exportFilename += ".pub";
                }
                util.exportPublicKeySSH2(this.manager.getPublicKey(alias), exportFilename);
            }
            String exportFilenameDisplay = Paths.get(exportFilename).toAbsolutePath().toString();
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_SUCCESS,
                    this.rb.getResourceString("certificate.export.success.title"),
                    this.rb.getResourceString("certificate.export.success.message", 
                            exportFilenameDisplay));
        } catch (Throwable e) {
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("certificate.export.error.title"),
                    this.rb.getResourceString("certificate.export.error.message",
                            "[" + e.getClass().getSimpleName() + "] "
                            + e.getMessage()));
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
        jLabelExportFile = new javax.swing.JLabel();
        jTextFieldExportFile = new javax.swing.JTextField();
        jLabelExportEncoding = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jComboBoxExportFormat = new javax.swing.JComboBox();
        jLabelAlias = new javax.swing.JLabel();
        jComboBoxAlias = new javax.swing.JComboBox();
        jButtonBrowse = new javax.swing.JButton();
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

        jLabelExportFile.setText(this.rb.getResourceString( "label.exportfile"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelExportFile, gridBagConstraints);

        jTextFieldExportFile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldExportFileKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jTextFieldExportFile, gridBagConstraints);

        jLabelExportEncoding.setText(this.rb.getResourceString( "label.exportformat"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelExportEncoding, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelEdit.add(jPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jComboBoxExportFormat, gridBagConstraints);

        jLabelAlias.setText(this.rb.getResourceString( "label.alias"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelAlias, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jComboBoxAlias, gridBagConstraints);

        jButtonBrowse.setText("..");
        jButtonBrowse.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowse.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowse, gridBagConstraints);

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

        setSize(new java.awt.Dimension(445, 278));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        MecFileChooser chooser = new MecFileChooser(parent, this.rb.getResourceString("filechooser.certificate.export"));
        chooser.browseFilename(this.jTextFieldExportFile);
        this.setButtonState();
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    private void jTextFieldExportFileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldExportFileKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldExportFileKeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible(false);
        this.performExport();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JComboBox jComboBoxAlias;
    private javax.swing.JComboBox jComboBoxExportFormat;
    private javax.swing.JLabel jLabelAlias;
    private javax.swing.JLabel jLabelExportEncoding;
    private javax.swing.JLabel jLabelExportFile;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JTextField jTextFieldExportFile;
    // End of variables declaration//GEN-END:variables

    public static class ExportFormat {

        private String type;
        private MecResourceBundle rb;

        public ExportFormat(String type) {
            this.type = type;
            //load resource bundle
            try {
                this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                        ResourceBundleExportCertificate.class.getName());
            } catch (MissingResourceException e) {
                throw new RuntimeException("Oops..resource bundle "
                        + e.getClassName() + " not found.");
            }
        }

        @Override
        public String toString() {
            return (this.rb.getResourceString(type));
        }

        /**
         * Overwrite the equal method of object
         *
         * @param anObject object to compare
         */
        @Override
        public boolean equals(Object anObject) {
            if (anObject == this) {
                return (true);
            }
            if (anObject != null && anObject instanceof ExportFormat) {
                ExportFormat exportFormat = (ExportFormat) anObject;
                return (exportFormat.getType().equals(this.getType()));
            }
            return (false);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + (this.getType() != null ? this.getType().hashCode() : 0);
            return hash;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }
    }
}
