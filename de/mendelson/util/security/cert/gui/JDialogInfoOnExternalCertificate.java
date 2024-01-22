//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogInfoOnExternalCertificate.java 21    26/09/22 10:19 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.TableModelCertificates;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dialog to display information about certificates - either in an external file
 * or a passed object
 *
 * @author S.Heller
 * @version $Revision: 21 $
 */
public class JDialogInfoOnExternalCertificate extends JDialog {

    private Color colorOk = Color.green.darker().darker();
    private Color colorWarning = Color.red.darker();
    /**
     * ResourceBundle to localize the GUI
     */
    private MecResourceBundle rb = null;
    private boolean certificateIsOk = true;
    private boolean importPressed = false;
    private List<String> infoTextList = new ArrayList<String>();
    private int certificateIndex = 0;
    private CertificateManager certificateManager;
    private List<X509Certificate> certList = new ArrayList<X509Certificate>();

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param parameter Parameter to edit, null for a new one
     * @param parameterList List of available parameter
     */
    @Deprecated (since="2019")
    public JDialogInfoOnExternalCertificate(JFrame parent, File certFile, CertificateManager certificateManager) {
        this( parent, certFile.toPath(), certificateManager);
    }
    
    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param parameter Parameter to edit, null for a new one
     * @param parameterList List of available parameter
     */
    public JDialogInfoOnExternalCertificate(JFrame parent, Path certFile, CertificateManager certificateManager) {
        super(parent, true);
        this.certificateManager = certificateManager;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleInfoOnExternalCertificate.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        initComponents();
        colorOk = ColorUtil.getBestContrastColorAroundForeground(this.jLabelAliasExistsIndicator.getBackground(), colorOk);
        colorWarning = ColorUtil.getBestContrastColorAroundForeground(this.jLabelAliasExistsIndicator.getBackground(), colorWarning);
        this.setMultiresolutionIcons();
        this.getRootPane().setDefaultButton(this.jButtonImport);
        this.infoTextList = this.loadCertsFromFileAndGetInfo(certFile, this.certList);
        if (this.infoTextList.size() == 1) {
            this.setTitle(this.rb.getResourceString("title.single"));
        } else {
            this.setTitle(this.rb.getResourceString("title.multiple"));
        }
        this.displayCertificateInformationAndSetButtonState(this.certList);
        //hide dialog on esc
        ActionListener actionListenerESC = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jButtonCancel.doClick();
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane().registerKeyboardAction(actionListenerESC, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param parameter Parameter to edit, null for a new one
     * @param parameterList List of available parameter
     */
    public JDialogInfoOnExternalCertificate(JFrame parent, List<X509Certificate> certs, CertificateManager certificateManager) {
        super(parent, true);
        this.certificateManager = certificateManager;
        this.certList.addAll(certs);
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleInfoOnExternalCertificate.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        initComponents();
        colorOk = ColorUtil.getBestContrastColorAroundForeground(this.jLabelAliasExistsIndicator.getBackground(), colorOk);
        colorWarning = ColorUtil.getBestContrastColorAroundForeground(this.jLabelAliasExistsIndicator.getBackground(), colorWarning);
        this.setMultiresolutionIcons();
        this.getRootPane().setDefaultButton(this.jButtonImport);
        this.infoTextList = this.getInfo(certs);
        if (this.infoTextList.size() == 1) {
            this.setTitle(this.rb.getResourceString("title.single"));
        } else {
            this.setTitle(this.rb.getResourceString("title.multiple"));
        }
        this.displayCertificateInformationAndSetButtonState(certs);
    }
    
    private void setMultiresolutionIcons() {
        this.jLabelIcon.setIcon(new ImageIcon(TableModelCertificates.IMAGE_CERTIFICATE_MULTIRESOLUTION.toMinResolution(32)));
    }
    
    
    private void displayCertificateInformationAndSetButtonState(List<X509Certificate> certList) {
        this.jTextAreaInfo.setText(this.infoTextList.get(this.getCertificateIndex()));
        if (this.infoTextList.size() > 1) {
            this.jLabelIcon.setText(this.rb.getResourceString("certinfo.index",
                    new Object[]{String.valueOf(this.getCertificateIndex() + 1),
                        String.valueOf(this.infoTextList.size())}));
        }
        boolean certificateAlreadyImported = false;
        if (this.certificateIsOk) {
            KeystoreCertificate certificate = new KeystoreCertificate();
            certificate.setCertificate(certList.get(this.getCertificateIndex()));
            String fingerprintSHA1 = certificate.getFingerPrintSHA1();
            String foundAlias = this.certificateManager.getAliasByFingerprint(fingerprintSHA1);
            if (foundAlias != null) {
                this.jLabelAliasExistsIndicator.setForeground(this.colorWarning);
                this.jLabelAliasExistsIndicator.setText(this.rb.getResourceString("certificate.exists", foundAlias));
                certificateAlreadyImported = true;
            } else {
                this.jLabelAliasExistsIndicator.setForeground(this.colorOk);
                this.jLabelAliasExistsIndicator.setText(this.rb.getResourceString("certificate.doesnot.exist"));
            }
        } else {
            this.jLabelAliasExistsIndicator.setForeground(this.colorWarning);
            this.jLabelAliasExistsIndicator.setText(this.rb.getResourceString("no.certificate"));
        }
        this.jButtonImport.setEnabled(this.certificateIsOk && !certificateAlreadyImported);
        this.jButtonIndexUp.setVisible(this.infoTextList.size() > 1);
        this.jButtonIndexDown.setVisible(this.infoTextList.size() > 1);
        this.jButtonIndexDown.setEnabled(this.getCertificateIndex() > 0);
        this.jButtonIndexUp.setEnabled(this.getCertificateIndex() < this.infoTextList.size() - 1);
    }

    private List<String> getInfo(List<X509Certificate> certList) {
        List<String> infoList = new ArrayList<String>();
        for (int i = 0; i < certList.size(); i++) {
            StringBuilder infoText = new StringBuilder();
            X509Certificate cert = (X509Certificate) certList.get(i);
            KeystoreCertificate keystoreCert = new KeystoreCertificate();
            keystoreCert.setCertificate(cert);
            infoText.append(keystoreCert.getInfo());
            infoList.add(infoText.toString());
        }
        return (infoList);
    }

    private List<String> loadCertsFromFileAndGetInfo(Path certFile, List<X509Certificate> certListToFill) {
        List<String> infoList = null;
        InputStream inStream = null;
        try {
            inStream = Files.newInputStream(certFile);
            KeyStoreUtil util = new KeyStoreUtil();
            List<X509Certificate> certList = util.readCertificates(inStream, 
                    BouncyCastleProvider.PROVIDER_NAME);
            certListToFill.addAll(certList);
            infoList = this.getInfo(certList);
            //add file info to info text
            StringBuilder fileInfoText = new StringBuilder();
            fileInfoText.append(this.rb.getResourceString("certinfo.certfile", certFile.toAbsolutePath().toString()));
            fileInfoText.append("\n---\n");
            for (int i = 0; i < infoList.size(); i++) {
                infoList.set(i, fileInfoText.toString() + infoList.get(i));
            }
        } catch (Exception e) {
            this.certificateIsOk = false;
            if (infoList != null) {
                infoList.clear();
            } else {
                infoList = new ArrayList<String>();
            }
            infoList.add(e.getMessage());
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (Exception e) {
                //nop
            }
        }
        return (infoList);
    }

    public boolean importPressed() {
        return (this.importPressed);
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
        jScrollPaneInfo = new javax.swing.JScrollPane();
        jTextAreaInfo = new javax.swing.JTextArea();
        jPanelHeader = new javax.swing.JPanel();
        jButtonIndexDown = new javax.swing.JButton();
        jButtonIndexUp = new javax.swing.JButton();
        jLabelIcon = new javax.swing.JLabel();
        jLabelAliasExistsIndicator = new javax.swing.JLabel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonImport = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jTextAreaInfo.setEditable(false);
        jTextAreaInfo.setColumns(20);
        jTextAreaInfo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextAreaInfo.setRows(5);
        jScrollPaneInfo.setViewportView(jTextAreaInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 19);
        jPanelEdit.add(jScrollPaneInfo, gridBagConstraints);

        jPanelHeader.setLayout(new java.awt.GridBagLayout());

        jButtonIndexDown.setText("<<");
        jButtonIndexDown.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jButtonIndexDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIndexDownActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHeader.add(jButtonIndexDown, gridBagConstraints);

        jButtonIndexUp.setText(">>");
        jButtonIndexUp.setMargin(new java.awt.Insets(10, 10, 10, 10));
        jButtonIndexUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIndexUpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 20);
        jPanelHeader.add(jButtonIndexUp, gridBagConstraints);

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelHeader.add(jLabelIcon, gridBagConstraints);

        jLabelAliasExistsIndicator.setText("<Alias (if already imported)>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 5);
        jPanelHeader.add(jLabelAliasExistsIndicator, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelEdit.add(jPanelHeader, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelEdit, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonImport.setText(this.rb.getResourceString( "button.ok" ));
        jButtonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonImport, gridBagConstraints);

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

        setSize(new java.awt.Dimension(632, 476));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.importPressed = false;
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImportActionPerformed
        this.importPressed = true;
        this.setVisible(false);
    }//GEN-LAST:event_jButtonImportActionPerformed

    private void jButtonIndexDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIndexDownActionPerformed
        if (this.getCertificateIndex() > 0) {
            this.certificateIndex--;
            this.displayCertificateInformationAndSetButtonState(this.certList);
        }
    }//GEN-LAST:event_jButtonIndexDownActionPerformed

    private void jButtonIndexUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIndexUpActionPerformed
        if (this.getCertificateIndex() < this.infoTextList.size() - 1) {
            this.certificateIndex++;
            this.displayCertificateInformationAndSetButtonState(this.certList);
        }
    }//GEN-LAST:event_jButtonIndexUpActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonImport;
    private javax.swing.JButton jButtonIndexDown;
    private javax.swing.JButton jButtonIndexUp;
    private javax.swing.JLabel jLabelAliasExistsIndicator;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JScrollPane jScrollPaneInfo;
    private javax.swing.JTextArea jTextAreaInfo;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the certificateIndex
     */
    public int getCertificateIndex() {
        return certificateIndex;
    }

    @Override
    public void setVisible(boolean flag) {
        //reinitialize some settings if this dialog is just revisibled
        if (flag) {
            this.importPressed = false;
            if( !this.certList.isEmpty()){
                this.displayCertificateInformationAndSetButtonState(this.certList);
            }
        }
        super.setVisible(flag);
    }
}
