//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogImportKeyPKCS12.java 16    26/09/22 10:19 Heller $
package de.mendelson.util.security.cert.gui;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.MecFileChooser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.PKCS122JKS;
import de.mendelson.util.security.PKCS122PKCS12;
import de.mendelson.util.uinotification.UINotification;
import java.security.KeyStore;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
 * Dialog to configure a single partner
 * @author S.Heller
 * @version $Revision: 16 $
 */
public class JDialogImportKeyPKCS12 extends JDialog {
    
    /**ResourceBundle to localize the GUI*/
    private MecResourceBundle rb;    
    private final CertificateManager manager;    
    private String newAlias = null;
    private final Logger logger;
    
    /** Creates new form JDialogPartnerConfig
     *@param manager Manager that handles the certificates
     */
    public JDialogImportKeyPKCS12(JFrame parent, Logger logger, CertificateManager manager ) {
        super( parent, true );
        //load resource bundle
        try{
            this.rb = (MecResourceBundle)ResourceBundle.getBundle(
                    ResourceBundleImportKeyPKCS12.class.getName());
        } catch ( MissingResourceException e ) {
            throw new RuntimeException( "Oops..resource bundle "
                    + e.getClassName() + " not found." );
        }
        this.setTitle( this.rb.getResourceString( "title" ));
        initComponents();
        PasswordOverlay.addTo(this.jPasswordFieldPassphrase,
                this.rb.getResourceString( "label.keypass.hint"));
        TextOverlay.addTo(this.jTextFieldImportPKCS12File, this.rb.getResourceString( "label.importkey.hint"));
        this.jLabelIcon.setIcon( new ImageIcon(JDialogCertificates.IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(32)));
        this.manager = manager;
        this.logger = logger;
        this.getRootPane().setDefaultButton( this.jButtonOk );
        this.setButtonState();
    }
    
    public String getNewAlias(){
        return( this.newAlias );
    }
    
    /**Sets the ok and cancel buttons of this GUI*/
    private void setButtonState(){
        this.jButtonOk.setEnabled( this.jTextFieldImportPKCS12File.getText().length() > 0
                && this.jPasswordFieldPassphrase.getPassword().length > 0 );
    }
    
    /**Finally import the key*/
    private void performImport(){
        try{
            KeyStoreUtil util = new KeyStoreUtil();            
            KeyStore sourceKeystore = KeyStore.getInstance( BCCryptoHelper.KEYSTORE_PKCS12, 
                    BouncyCastleProvider.PROVIDER_NAME );            
            util.loadKeyStore( sourceKeystore, this.jTextFieldImportPKCS12File.getText(), 
                    this.jPasswordFieldPassphrase.getPassword());
            List<String> keyAliasesList = util.getKeyAliases(sourceKeystore);
            String selectedAlias = null;
            if( keyAliasesList.isEmpty() ){
                throw new Exception( this.rb.getResourceString( "keystore.contains.nokeys" ));
            }else if( keyAliasesList.size() == 1 ){
                selectedAlias = keyAliasesList.get(0);
            }else{
                //multiple keys available
                Object[] aliasArray = new Object[keyAliasesList.size()];
                for( int i = 0; i < keyAliasesList.size(); i++ ){
                    aliasArray[i] = keyAliasesList.get(i);
                }
                JFrame parent = (JFrame)SwingUtilities.getAncestorOfClass( JFrame.class, this );
                Object selectedAliasObject = JOptionPane.showInputDialog( parent, 
                        this.rb.getResourceString( "multiple.keys.message" ),
                        this.rb.getResourceString( "multiple.keys.title" ), JOptionPane.QUESTION_MESSAGE,
                        null, aliasArray, aliasArray[0] );
                //user break
                if( selectedAliasObject == null ){
                    return;
                }
                selectedAlias = selectedAliasObject.toString();
            }
            if( this.manager.getKeystoreType().equals(BCCryptoHelper.KEYSTORE_PKCS12)){
                PKCS122PKCS12 importer = new PKCS122PKCS12(this.logger);
                importer.setTargetKeyStore( this.manager.getKeystore(), this.manager.getKeystorePass() );
                importer.importKey( sourceKeystore, selectedAlias );
            }else if( this.manager.getKeystoreType().equals(BCCryptoHelper.KEYSTORE_JKS)){
                PKCS122JKS importer = new PKCS122JKS(this.logger);
                importer.setTargetKeyStore( this.manager.getKeystore(), this.manager.getKeystorePass() );
                importer.importKey( sourceKeystore, selectedAlias );
            }
            this.newAlias = selectedAlias;
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_SUCCESS,
                    this.rb.getResourceString("key.import.success.title"),
                    this.rb.getResourceString("key.import.success.message"));
        } catch( Exception e ){
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("key.import.error.title"),
                    this.rb.getResourceString("key.import.error.message", e.getMessage()));            
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelEdit = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jLabelImportPKCS12File = new javax.swing.JLabel();
        jTextFieldImportPKCS12File = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButtonBrowseImportFile = new javax.swing.JButton();
        jLabelImportPEMPassphrase = new javax.swing.JLabel();
        jPasswordFieldPassphrase = new javax.swing.JPasswordField();
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

        jLabelImportPKCS12File.setText(this.rb.getResourceString( "label.importkey"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelImportPKCS12File, gridBagConstraints);

        jTextFieldImportPKCS12File.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldImportPKCS12FileKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldImportPKCS12File, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelEdit.add(jPanel3, gridBagConstraints);

        jButtonBrowseImportFile.setText("..");
        jButtonBrowseImportFile.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowseImportFile.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowseImportFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseImportFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowseImportFile, gridBagConstraints);

        jLabelImportPEMPassphrase.setText(this.rb.getResourceString( "label.keypass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelImportPEMPassphrase, gridBagConstraints);

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

        setSize(new java.awt.Dimension(415, 259));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    private void jPasswordFieldPassphraseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordFieldPassphraseKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jPasswordFieldPassphraseKeyReleased
    
    private void jButtonBrowseImportFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseImportFileActionPerformed
        JFrame parent = (JFrame)SwingUtilities.getAncestorOfClass( JFrame.class, this );
        MecFileChooser chooser = new MecFileChooser(
                parent,
                this.rb.getResourceString( "filechooser.key.import" ));
        chooser.browseFilename( this.jTextFieldImportPKCS12File );
    }//GEN-LAST:event_jButtonBrowseImportFileActionPerformed
    
    private void jTextFieldImportPKCS12FileKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldImportPKCS12FileKeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldImportPKCS12FileKeyReleased
    
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible( false );
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed
    
    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible( false );
        this.performImport();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseImportFile;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelImportPEMPassphrase;
    private javax.swing.JLabel jLabelImportPKCS12File;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPasswordField jPasswordFieldPassphrase;
    private javax.swing.JTextField jTextFieldImportPKCS12File;
    // End of variables declaration//GEN-END:variables
    
}
