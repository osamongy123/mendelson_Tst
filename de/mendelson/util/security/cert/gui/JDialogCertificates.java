//$Header: /as2/de/mendelson/util/security/cert/gui/JDialogCertificates.java 96    12/12/22 14:18 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.gui.keygeneration.JDialogGenerateKey;
import de.mendelson.util.LayoutManagerJToolbar;
import de.mendelson.util.MecFileChooser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.AllowModificationCallback;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.modulelock.LockClientInformation;
import de.mendelson.util.modulelock.ModuleLock;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateInUseChecker;
import de.mendelson.util.security.cert.KeyCopyHandler;
import de.mendelson.util.security.cert.KeystoreStorage;
import de.mendelson.util.security.cert.clientserver.RefreshKeystoreCertificates;
import de.mendelson.util.security.csr.CSRUtil;
import de.mendelson.util.security.csr.ResourceBundleCSR;
import de.mendelson.util.security.keygeneration.KeyGenerationResult;
import de.mendelson.util.security.keygeneration.KeyGenerationValues;
import de.mendelson.util.security.keygeneration.KeyGenerator;
import de.mendelson.util.uinotification.UINotification;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Certificate manager UI
 *
 * @author S.Heller
 * @version $Revision: 96 $
 */
public class JDialogCertificates extends JDialog implements ListSelectionListener {

    /**
     * Image size for the popup menus
     */
    protected static final int IMAGE_SIZE_POPUP = 18;
    protected static final int IMAGE_SIZE_MENUITEM = 18;
    protected static final int IMAGE_SIZE_TOOLBAR = 24;

    protected final static MendelsonMultiResolutionImage IMAGE_DELETE_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/delete.svg", IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_DELETE_EXPIRED_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/delete_expired.svg", IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_IMPORT_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/import.svg", IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_EXPORT_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/export.svg", IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_EDIT_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/edit.svg", IMAGE_SIZE_MENUITEM, 64);
    protected final static MendelsonMultiResolutionImage IMAGE_ADD_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/add.svg", IMAGE_SIZE_MENUITEM, IMAGE_SIZE_MENUITEM * 2);
    protected final static MendelsonMultiResolutionImage IMAGE_CA_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/ca.svg", IMAGE_SIZE_MENUITEM, IMAGE_SIZE_MENUITEM * 2);
    protected final static MendelsonMultiResolutionImage IMAGE_CERTIFICATE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/certificate.svg", IMAGE_SIZE_MENUITEM, IMAGE_SIZE_MENUITEM * 2);
    protected final static MendelsonMultiResolutionImage IMAGE_REFERENCE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/reference.svg", IMAGE_SIZE_MENUITEM, IMAGE_SIZE_MENUITEM * 2);
    protected final static MendelsonMultiResolutionImage IMAGE_KEYCOPY
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/keycopy.svg", IMAGE_SIZE_MENUITEM, IMAGE_SIZE_MENUITEM * 2);

    /**
     * Resource to localize the GUI
     */
    private final MecResourceBundle rb;
    private final MecResourceBundle rbCSR;
    private JPanelCertificates panelCertificates = null;
    private CertificateManager manager;
    private final Logger logger;
    private final GUIClient guiClient;
    private final String productName;
    private final List<AllowModificationCallback> allowModificationCallbackList = new ArrayList<AllowModificationCallback>();
    private final LockClientInformation lockKeeper;
    private final String moduleName;
    private Color colorOk = Color.green.darker().darker();
    private Color colorWarning = Color.red.darker();

    /**
     * Creates new form JDialogMessageMapping
     */
    public JDialogCertificates(JFrame parent, Logger logger, GUIClient guiClient,
            String title, String productName, boolean moduleLockedByAnotherClient,
            String moduleName, LockClientInformation lockKeeper) {
        super(parent, title, true);
        this.guiClient = guiClient;
        this.logger = logger;
        this.productName = productName;
        this.lockKeeper = lockKeeper;
        this.moduleName = moduleName;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificates.class.getName());
            this.rbCSR = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCSR.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.initComponents();
        this.colorOk = ColorUtil.getBestContrastColorAroundForeground(this.jLabelWarnings.getBackground(), colorOk);
        this.colorWarning = ColorUtil.getBestContrastColorAroundForeground(this.jLabelWarnings.getBackground(), colorWarning);
        this.jLabelWarnings.setForeground(colorWarning);
        this.setJMenuBar(this.jMenuBar);
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.panelCertificates = new JPanelCertificates(this.logger, this, this.guiClient, moduleName);
        this.panelCertificates.setButtons(this.jButtonEditCertificate, this.jButtonDeleteCertificate);
        this.panelCertificates.setMenuItems(this.jMenuItemFileEdit, this.jMenuItemFileDelete);
        //if no certificate is in the keystore a value should be displayed that shows this..
        this.jLabelTrustAnchorValue.setText("--");
        this.panelCertificates.setExternalDisplayComponents(this.jLabelTrustAnchorValue, this.jLabelWarnings);
        this.jPanelCertificatesMain.add(this.panelCertificates);
        this.jPanelModuleLockWarning.setVisible(moduleLockedByAnotherClient);
        this.jToolBar.setLayout(new LayoutManagerJToolbar());
        if (this.moduleName.equals(ModuleLock.MODULE_ENCSIGN_KEYSTORE)) {
            this.jMenuItemFileKeyCopy.setText(this.rb.getResourceString("button.keycopy",
                    this.rb.getResourceString("button.keycopy.tls")));
        } else {
            this.jMenuItemFileKeyCopy.setText(this.rb.getResourceString("button.keycopy",
                    this.rb.getResourceString("button.keycopy.signencrypt")));
        }
        this.jMenuItemFileKeyCopy.setEnabled(false);
        //bind del key to delete certificate
        ActionListener actionListenerDEL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!isOperationAllowed(false)) {
                    return;
                }
                panelCertificates.deleteSelectedCertificate();
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        this.getRootPane().registerKeyboardAction(actionListenerDEL, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Overwrite the designers icons by multi resolution icons
     */
    private void setMultiresolutionIcons() {
        this.jButtonAddCertificate.setIcon(new ImageIcon(IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_TOOLBAR)));
        this.jButtonExportCertificate.setIcon(new ImageIcon(IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_TOOLBAR)));
        this.jMenuItemImportCertificate.setIcon(new ImageIcon(IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemImportKeyJKS.setIcon(new ImageIcon(IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemImportKeyPEM.setIcon(new ImageIcon(IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemImportKeyPKCS12.setIcon(new ImageIcon(IMAGE_IMPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemExportCertificate.setIcon(new ImageIcon(IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemExportKeyPKCS12.setIcon(new ImageIcon(IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jButtonDeleteCertificate.setIcon(new ImageIcon(IMAGE_DELETE_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_TOOLBAR)));
        this.jButtonEditCertificate.setIcon(new ImageIcon(IMAGE_EDIT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_TOOLBAR)));
        this.jMenuItemFileDelete.setIcon(new ImageIcon(IMAGE_DELETE_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemFileDeleteAllExpired.setIcon(new ImageIcon(IMAGE_DELETE_EXPIRED_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemFileEdit.setIcon(new ImageIcon(IMAGE_EDIT_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemGenerateKey.setIcon(new ImageIcon(IMAGE_ADD_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemGenerateCSRInitial.setIcon(new ImageIcon(IMAGE_CA_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemGenerateCSRRenew.setIcon(new ImageIcon(IMAGE_CA_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemImportCSRResponseInitial.setIcon(new ImageIcon(IMAGE_CA_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemImportCSRResponseRenew.setIcon(new ImageIcon(IMAGE_CA_MULTIRESOLUTION.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemFileReference.setIcon(new ImageIcon(IMAGE_REFERENCE.toMinResolution(IMAGE_SIZE_MENUITEM)));
        this.jMenuItemFileKeyCopy.setIcon(new ImageIcon(IMAGE_KEYCOPY.toMinResolution(IMAGE_SIZE_MENUITEM)));
    }

    @Override
    public void setVisible(boolean flag) {
        if (flag) {
            this.setMultiresolutionIcons();
            this.setButtonState();
        }
        super.setVisible(flag);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JMenu getMenuImport() {
        return (this.jMenuImport);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JMenu getMenuExport() {
        //ensure to set the HiDPI icons before exporting the toolbar
        this.setMultiresolutionIcons();
        return (this.jMenuExport);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JMenu getMenuTools() {
        //ensure to set the HiDPI icons before exporting the toolbar
        this.setMultiresolutionIcons();
        return (this.jMenuTools);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JPanel getMainPanel() {
        //ensure to set the HiDPI icons before exporting the toolbar
        this.setMultiresolutionIcons();
        return (this.jPanelMain);
    }

    /**
     * Allows the integration of the components of this dialog into other
     * components
     *
     * @return
     */
    public JToolBar getToolbar() {
        //ensure to set the HiDPI icons before exporting the toolbar
        this.setMultiresolutionIcons();
        return (this.jToolBar);
    }

    /**
     * Adds a callback that is called if a user tries to modify the
     * configuration A modification will be prevented if one of the callbacks
     * does not allow it
     */
    public void addAllowModificationCallback(AllowModificationCallback callback) {
        this.allowModificationCallbackList.add(callback);
        this.panelCertificates.addAllowModificationCallback(callback);
    }

    /**
     * Set the handler to copy entries from one keystore manager to another. If
     * this is not passed the entry "key copy" in the UI will be greyed out
     *
     * @param handler
     */
    public void setKeyCopyHandler(KeyCopyHandler handler) {
        if (this.manager == null) {
            throw new IllegalArgumentException("JDialogCertificates: Please call initialize() before "
                    + "passing a key copy handler");
        }
        if( handler != null ){
            handler.setSource(this.manager);
        }
        this.panelCertificates.setKeyCopyHandler(handler);
        this.jMenuItemFileKeyCopy.setEnabled(handler != null);        
    }

    /**
     * Initializes the keystore gui
     */
    public void initialize(KeystoreStorage keystoreStorage) {
        this.manager = new CertificateManager(this.logger);
        this.setTitle(this.getTitle() + " | " + keystoreStorage.getKeystoreStorageType());
        this.manager.loadKeystoreCertificates(keystoreStorage);
        this.jTextFieldCertFileInfo.setText(keystoreStorage.getOriginalKeystoreFilename());
        this.panelCertificates.addKeystore(manager);
        this.setButtonState();
    }

    public void setSelectionByAlias(String selectedAlias) {
        this.panelCertificates.setSelectionByAlias(selectedAlias);
    }

    public void addCertificateInUseChecker(CertificateInUseChecker checker) {
        this.panelCertificates.addCertificateInUseChecker(checker);
    }

    /**
     * Checks if the operation is possible because the keystore is R/O and
     * displayes a message if not It's also possible to set the module into a
     * mode where modifications are not allowed - this will be displayed, too
     */
    private boolean isOperationAllowed(boolean silent) {
        for (AllowModificationCallback callback : this.allowModificationCallbackList) {
            boolean modificationAllowed = callback.allowModification(silent);
            if (!modificationAllowed) {
                return (false);
            }
        }
        boolean readWrite = true;
        readWrite = readWrite && this.manager.canWrite();
        if (!readWrite) {
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("keystore.readonly.title"),
                    this.rb.getResourceString("keystore.readonly.message"));
        }
        return (readWrite);
    }

    /**
     * Imports a certificate into the keystore
     */
    private void importCertificate() {
        if (!this.isOperationAllowed(false)) {
            return;
        }
        //take the main panel as anchor because it might be integrated in another swing program
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.jPanelMain);
        MecFileChooser chooser = new MecFileChooser(
                parent,
                this.rb.getResourceString("filechooser.certificate.import"));
        String importFilename = chooser.browseFilename();
        if (importFilename == null) {
            return;
        }
        JDialogInfoOnExternalCertificate infoDialog
                = new JDialogInfoOnExternalCertificate(parent, Paths.get(importFilename),
                        this.manager);
        infoDialog.setVisible(true);
        while (infoDialog.importPressed()) {
            //it is possible that there are more than a single certificate in the passed file (e.g. p7b). Get the index
            int selectedCertificateIndex = infoDialog.getCertificateIndex();
            InputStream inStream = null;
            try {
                KeyStoreUtil util = new KeyStoreUtil();
                inStream = Files.newInputStream(Paths.get(importFilename));
                List<X509Certificate> certList = util.readCertificates(inStream,
                        BouncyCastleProvider.PROVIDER_NAME);
                X509Certificate importCertificate = certList.get(selectedCertificateIndex);
                String proposedAlias = util.getProposalCertificateAliasForImport(importCertificate);
                String alias = JOptionPane.showInputDialog(this,
                        this.rb.getResourceString("certificate.import.alias"), proposedAlias);
                if (alias == null || alias.trim().length() == 0) {
                    return;
                }
                util.importX509Certificate(this.manager.getKeystore(), importFilename, alias, selectedCertificateIndex,
                        BouncyCastleProvider.PROVIDER_NAME);
                this.manager.saveKeystore();
                this.manager.rereadKeystoreCertificates();
                //inform the server that there are changes in the keystore
                RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
                JDialogCertificates.this.guiClient.sendAsync(signal);
                this.panelCertificates.refreshData();
                this.panelCertificates.certificateAdded(alias);
                KeystoreCertificate keystoreCertificate = this.manager.getKeystoreCertificate(alias);
                String messageKey = "certificate.import.success.message";
                if (keystoreCertificate.isCACertificate()) {
                    messageKey = "certificate.ca.import.success.message";
                }
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_SUCCESS,
                        this.rb.getResourceString("certificate.import.success.title"),
                        this.rb.getResourceString(messageKey, alias));
                //multiple certificates: show the import dialog again
                if (certList.size() > 1) {
                    infoDialog.setVisible(true);
                } else {
                    break;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_ERROR,
                        this.rb.getResourceString("certificate.import.error.title"),
                        this.rb.getResourceString("certificate.import.error.message", e.getMessage()));
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (Exception e) {
                        //nop
                    }
                }
            }
        }
    }

    /**
     * Imports a key in PEM format to the keystore
     */
    private void importPEMKey() {
        if (!isOperationAllowed(false)) {
            return;
        }
        //take the main panel as anchor because it might be integrated in another swing program
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.jPanelMain);
        JDialogImportKeyPEM dialog = new JDialogImportKeyPEM(parent, this.manager,
                this.manager.getKeystoreType());
        dialog.setVisible(true);
        try {
            this.manager.saveKeystore();
            //signal the server that there are changes in the keystore
            RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
            JDialogCertificates.this.guiClient.sendAsync(signal);
            this.panelCertificates.refreshData();
            this.panelCertificates.certificateAdded(dialog.getNewAlias());
        } catch (Throwable e) {
            this.logger.severe(e.getMessage());
        }
    }

    /**
     * Imports a key in PKCS12 format to the keystore
     */
    private void importPKCS12Key() {
        if (!isOperationAllowed(false)) {
            return;
        }
        //take the main panel as anchor because it might be integrated in another swing program
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.jPanelMain);
        JDialogImportKeyPKCS12 dialog = new JDialogImportKeyPKCS12(parent, this.logger, this.manager);
        dialog.setVisible(true);
        try {
            this.manager.saveKeystore();
            //signal the server that there are changes in the keystore
            RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
            JDialogCertificates.this.guiClient.sendAsync(signal);
            this.panelCertificates.refreshData();
            this.panelCertificates.certificateAdded(dialog.getNewAlias());
        } catch (Throwable e) {
            this.logger.severe(e.getMessage());
        }
    }

    /**
     * Imports a key in PKCS12 format to the keystore
     */
    private void importJKSKey() {
        if (!isOperationAllowed(false)) {
            return;
        }
        //take the main panel as anchor because it might be integrated in another swing program
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.jPanelMain);
        JDialogImportKeyJKS dialog = new JDialogImportKeyJKS(parent, this.logger, this.manager);
        dialog.setVisible(true);
        try {
            this.manager.saveKeystore();
            this.panelCertificates.refreshData();
            this.panelCertificates.certificateAdded(dialog.getNewAlias());
            //signal the server that there are changes in the keystore
            RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
            JDialogCertificates.this.guiClient.sendAsync(signal);
        } catch (Throwable e) {
            this.logger.severe(e.getMessage());
        }
    }

    private void generateCSR(boolean initial) {
        CSRUtil util = new CSRUtil();
        KeystoreCertificate selectedPrivateKey = this.panelCertificates.getSelectedCertificate();
        try {
            PKCS10CertificationRequest csr = util.generateCSR(this.manager, selectedPrivateKey.getAlias());
            String title = this.rbCSR.getResourceString("csr.title.renew");
            String storequestion = this.rbCSR.getResourceString("csr.message.storequestion.renew");
            String[] options = new String[]{
                this.rbCSR.getResourceString("csr.option.1.renew"),
                this.rbCSR.getResourceString("csr.option.2"),
                this.rbCSR.getResourceString("cancel"),};
            if (initial) {
                title = this.rbCSR.getResourceString("csr.title");
                storequestion = this.rbCSR.getResourceString("csr.message.storequestion");
                options = new String[]{
                    this.rbCSR.getResourceString("csr.option.1"),
                    this.rbCSR.getResourceString("csr.option.2"),
                    this.rbCSR.getResourceString("cancel"),};
            }
            //ask the user if the partner should be really deleted, all data is lost
            int requestValue = JOptionPane.showOptionDialog(this,
                    storequestion,
                    title, JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, options[2]);
            if (requestValue == 0) {
                this.buyKeyAtMendelson(csr);
            } else if (requestValue == 1) {
                //take the main panel as anchor because it might be integrated in another swing program
                JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.jPanelMain);
                MecFileChooser chooser = new MecFileChooser(parent,
                        this.rbCSR.getResourceString("label.selectcsrfile"));
                String outFilename = chooser.browseFilename();
                if (outFilename != null) {
                    Path outFile = Paths.get(outFilename);
                    util.storeCSRPEM(csr, outFile);
                    UINotification.instance().addNotification(null,
                            UINotification.TYPE_SUCCESS,
                            this.rbCSR.getResourceString("csr.generation.success.title"),
                            this.rbCSR.getResourceString("csr.generation.success.message",
                                    outFile.toAbsolutePath().toString()));
                }
            }
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
            e.printStackTrace();
            String errorDetails = "[" + e.getClass().getSimpleName() + "] " + e.getMessage();
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    this.rbCSR.getResourceString("csr.generation.failure.title"),
                    this.rbCSR.getResourceString("csr.generation.failure.message",
                            errorDetails));
        }
    }

    /**
     * Generates a body publisher for the java.net.http client that contains the
     * form data that should be transferred
     *
     * @param formDataMap A map that contains all form variables as key/value
     * pair
     * @return
     */
    private HttpRequest.BodyPublisher generateFormData(Map<Object, Object> formDataMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : formDataMap.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    private void buyKeyAtMendelson(PKCS10CertificationRequest csr) throws Exception {
        CSRUtil util = new CSRUtil();
        String csrStr = util.storeCSRPEM(csr);
        Map<Object, Object> formDataMap = new HashMap<>();
        formDataMap.put("csrpem", csrStr);
        formDataMap.put("source", this.productName);
        HttpClient client = HttpClient.newBuilder().
                followRedirects(Redirect.ALWAYS).build();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(this.generateFormData(formDataMap))
                .uri(URI.create("http://ca.mendelson-e-c.com/csr2session.php"))
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpURLConnection.HTTP_OK
                && statusCode != HttpURLConnection.HTTP_ACCEPTED) {
            throw new Exception(this.rbCSR.getResourceString("ca.connection.problem", String.valueOf(statusCode)));
        }
        String sessionId = response.body();
        URI uri = new URI("http://ca.mendelson-e-c.com?area=buy&stage=checkcsr&sid=" + sessionId);
        Desktop.getDesktop().browse(uri);
    }

    private void generateKeypair() {
        KeyGenerator generator = new KeyGenerator();
        try {
            //take the main panel as anchor because it might be integrated in another swing program
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.jPanelMain);
            JDialogGenerateKey dialog = new JDialogGenerateKey(parent, this.manager);
            dialog.setVisible(true);
            KeyGenerationValues values = dialog.getValues();
            if (values == null) {
                //user break
                return;
            }
            KeyGenerationResult result = generator.generateKeyPair(values);
            KeyStoreUtil util = new KeyStoreUtil();
            String alias = util.getProposalCertificateAliasForImport(result.getCertificate());
            alias = util.ensureUniqueAliasName(this.manager.getKeystore(), alias);
            this.manager.getKeystore().setKeyEntry(alias, result.getKeyPair().getPrivate(),
                    this.manager.getKeystorePass(), new X509Certificate[]{result.getCertificate()});
            this.manager.saveKeystore();
            this.manager.rereadKeystoreCertificates();
            //inform the server that there are changes in the keystore
            RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
            JDialogCertificates.this.guiClient.sendAsync(signal);
            this.panelCertificates.refreshData();
            this.panelCertificates.certificateAdded(alias);
        } catch (Throwable e) {
            String message = e.getClass().getName() + ": " + e.getMessage();
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("generatekey.error.title"),
                    this.rb.getResourceString("generatekey.error.message", message));
            e.printStackTrace();
        }
    }

    private void importCSRResponse(boolean renew) {
        CSRUtil util = new CSRUtil();
        KeystoreCertificate selectedCert = this.panelCertificates.getSelectedCertificate();
        String selectedCertAlias = selectedCert.getAlias();
        try {
            //take the main panel as anchor because it might be integrated in another swing program
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this.jPanelMain);
            MecFileChooser chooser = new MecFileChooser(parent,
                    this.rbCSR.getResourceString("label.selectcsrrepsonsefile"));
            String inFilename = chooser.browseFilename();
            if (inFilename != null) {
                if (renew) {
                    //clones the key entry and selects the new one
                    String newAlias = this.cloneSelectedCertificate();
                    if (newAlias != null) {
                        selectedCertAlias = newAlias;
                    } else {
                        throw new Exception("Processing failure: Unable to set new key alias");
                    }
                }
                util.importCSRReply(this.manager, selectedCertAlias, Paths.get(inFilename));
                this.manager.saveKeystore();
                this.manager.rereadKeystoreCertificates();
                //inform the server that there are changes in the keystore
                RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
                JDialogCertificates.this.guiClient.sendAsync(signal);
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_SUCCESS,
                        this.rbCSR.getResourceString("csrresponse.import.success.title"),
                        this.rbCSR.getResourceString("csrresponse.import.success.message"));
            }
        } catch (Throwable e) {
            this.logger.severe(e.getMessage());
            UINotification.instance().addNotification(null,
                    UINotification.TYPE_SUCCESS,
                    this.rbCSR.getResourceString("csrresponse.import.failure.title"),
                    this.rbCSR.getResourceString("csrresponse.import.failure.message", e.getMessage()));
        }
    }

    private void saveAndClose() {
        if (this.manager != null) {
            try {
                this.manager.saveKeystore();
            } catch (Throwable e) {
                e.printStackTrace();
                UINotification.instance().addNotification(e);
                return;
            }

        }
        this.setVisible(false);
        this.dispose();
    }

    public void setOkButtonVisible(boolean visible) {
        this.jPanelButton.setVisible(visible);
    }

    /**
     * Refreshes the menus etc
     */
    private void setButtonState() {
        if (this.panelCertificates != null) {
            //disable everything?
            boolean operationAllowed = this.isOperationAllowed(true);
            this.jButtonAddCertificate.setEnabled(operationAllowed);
            this.jButtonDeleteCertificate.setEnabled(operationAllowed);
            this.jButtonEditCertificate.setEnabled(operationAllowed);
            this.jMenuItemGenerateKey.setEnabled(operationAllowed);
            this.jMenuItemImportCSRResponseInitial.setEnabled(operationAllowed);
            this.jMenuItemImportCSRResponseRenew.setEnabled(operationAllowed);
            this.jMenuItemImportCertificate.setEnabled(operationAllowed);
            this.jMenuItemImportKeyJKS.setEnabled(operationAllowed);
            this.jMenuItemImportKeyPEM.setEnabled(operationAllowed);
            this.jMenuItemImportKeyPKCS12.setEnabled(operationAllowed);
            KeystoreCertificate selectedCert = this.panelCertificates.getSelectedCertificate();
            this.jMenuItemGenerateCSRInitial.setEnabled(selectedCert != null && selectedCert.getIsKeyPair()
                    && selectedCert.isSelfSigned());
            this.jMenuItemImportCSRResponseInitial.setEnabled(operationAllowed && selectedCert != null && selectedCert.getIsKeyPair()
                    && selectedCert.isSelfSigned());
            this.jMenuItemGenerateCSRRenew.setEnabled(selectedCert != null && selectedCert.getIsKeyPair()
                    && !selectedCert.isSelfSigned());
            this.jMenuItemImportCSRResponseRenew.setEnabled(operationAllowed && selectedCert != null && selectedCert.getIsKeyPair()
                    && !selectedCert.isSelfSigned());
            this.jMenuItemFileReference.setEnabled(selectedCert != null
                    && this.panelCertificates.isReferenceFunctionAvailable());
        }
    }

    private String cloneSelectedCertificate() throws Throwable {
        String newAlias = null;
        if (this.panelCertificates != null) {
            KeystoreCertificate selectedCert = this.panelCertificates.getSelectedCertificate();
            if (selectedCert != null && selectedCert.getIsKeyPair()) {
                KeyStoreUtil util = new KeyStoreUtil();
                newAlias = util.getProposalCertificateAliasForImport(selectedCert.getX509Certificate());
                newAlias = util.ensureUniqueAliasName(this.manager.getKeystore(), newAlias);
                PrivateKey privateKey = (PrivateKey) this.manager.getKey(selectedCert.getAlias());
                this.manager.getKeystore().setKeyEntry(newAlias, privateKey,
                        this.manager.getKeystorePass(), new X509Certificate[]{selectedCert.getX509Certificate()});
                this.manager.saveKeystore();
                this.manager.rereadKeystoreCertificates();
                this.panelCertificates.refreshData();
                this.panelCertificates.certificateAdded(newAlias);
            }
        }
        return (newAlias);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemFileEdit = new javax.swing.JMenuItem();
        jMenuItemFileReference = new javax.swing.JMenuItem();
        jMenuItemFileKeyCopy = new javax.swing.JMenuItem();
        jMenuItemFileDelete = new javax.swing.JMenuItem();
        jMenuItemFileDeleteAllExpired = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFileClose = new javax.swing.JMenuItem();
        jMenuImport = new javax.swing.JMenu();
        jMenuItemImportKeyPEM = new javax.swing.JMenuItem();
        jMenuItemImportKeyPKCS12 = new javax.swing.JMenuItem();
        jMenuItemImportKeyJKS = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemImportCertificate = new javax.swing.JMenuItem();
        jMenuExport = new javax.swing.JMenu();
        jMenuItemExportKeyPKCS12 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItemExportCertificate = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemGenerateKey = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemGenerateCSRInitial = new javax.swing.JMenuItem();
        jMenuItemImportCSRResponseInitial = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemGenerateCSRRenew = new javax.swing.JMenuItem();
        jMenuItemImportCSRResponseRenew = new javax.swing.JMenuItem();
        jToolBar = new javax.swing.JToolBar();
        jButtonAddCertificate = new javax.swing.JButton();
        jButtonExportCertificate = new javax.swing.JButton();
        jButtonEditCertificate = new javax.swing.JButton();
        jButtonDeleteCertificate = new javax.swing.JButton();
        jPanelMain = new javax.swing.JPanel();
        jPanelModuleLockWarning = new javax.swing.JPanel();
        jLabelModuleLockedWarning = new javax.swing.JLabel();
        jButtonModuleLockInfo = new javax.swing.JButton();
        jPanelCertificatesMain = new javax.swing.JPanel();
        jPanelButton = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jPanelStatusBar = new javax.swing.JPanel();
        jLabelKeystore = new javax.swing.JLabel();
        jTextFieldCertFileInfo = new javax.swing.JTextField();
        jLabelTrustAnchor = new javax.swing.JLabel();
        jLabelTrustAnchorValue = new javax.swing.JLabel();
        jLabelWarnings = new javax.swing.JLabel();
        jPanelSep1 = new javax.swing.JPanel();
        jSeparator6 = new javax.swing.JSeparator();
        jPanelSep2 = new javax.swing.JPanel();
        jSeparator7 = new javax.swing.JSeparator();

        jMenuFile.setText(this.rb.getResourceString( "menu.file"));

        jMenuItemFileEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileEdit.setText(this.rb.getResourceString( "button.edit"));
        jMenuItemFileEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileEditActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileEdit);

        jMenuItemFileReference.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileReference.setText(this.rb.getResourceString( "button.reference"));
        jMenuItemFileReference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileReferenceActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileReference);

        jMenuItemFileKeyCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileKeyCopy.setText(this.rb.getResourceString( "button.keycopy"));
        jMenuItemFileKeyCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileKeyCopyActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileKeyCopy);

        jMenuItemFileDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileDelete.setText(this.rb.getResourceString( "button.delete"));
        jMenuItemFileDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileDeleteActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileDelete);

        jMenuItemFileDeleteAllExpired.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemFileDeleteAllExpired.setText(this.rb.getResourceString( "button.delete.all.expired"));
        jMenuItemFileDeleteAllExpired.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileDeleteAllExpiredActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileDeleteAllExpired);
        jMenuFile.add(jSeparator3);

        jMenuItemFileClose.setText(this.rb.getResourceString( "menu.file.close"));
        jMenuItemFileClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileCloseActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemFileClose);

        jMenuBar.add(jMenuFile);

        jMenuImport.setText(this.rb.getResourceString( "menu.import" ));

        jMenuItemImportKeyPEM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportKeyPEM.setText(this.rb.getResourceString( "label.key.import.pem" ));
        jMenuItemImportKeyPEM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportKeyPEMActionPerformed(evt);
            }
        });
        jMenuImport.add(jMenuItemImportKeyPEM);

        jMenuItemImportKeyPKCS12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportKeyPKCS12.setText(this.rb.getResourceString( "label.key.import.pkcs12" ));
        jMenuItemImportKeyPKCS12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportKeyPKCS12ActionPerformed(evt);
            }
        });
        jMenuImport.add(jMenuItemImportKeyPKCS12);

        jMenuItemImportKeyJKS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportKeyJKS.setText(this.rb.getResourceString( "label.key.import.jks" ));
        jMenuItemImportKeyJKS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportKeyJKSActionPerformed(evt);
            }
        });
        jMenuImport.add(jMenuItemImportKeyJKS);
        jMenuImport.add(jSeparator1);

        jMenuItemImportCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportCertificate.setText(this.rb.getResourceString( "label.cert.import" ));
        jMenuItemImportCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportCertificateActionPerformed(evt);
            }
        });
        jMenuImport.add(jMenuItemImportCertificate);

        jMenuBar.add(jMenuImport);

        jMenuExport.setText(this.rb.getResourceString( "menu.export" ));

        jMenuItemExportKeyPKCS12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemExportKeyPKCS12.setText(this.rb.getResourceString( "label.key.export.pkcs12" ));
        jMenuItemExportKeyPKCS12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportKeyPKCS12ActionPerformed(evt);
            }
        });
        jMenuExport.add(jMenuItemExportKeyPKCS12);
        jMenuExport.add(jSeparator2);

        jMenuItemExportCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemExportCertificate.setText(this.rb.getResourceString( "label.cert.export" ));
        jMenuItemExportCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportCertificateActionPerformed(evt);
            }
        });
        jMenuExport.add(jMenuItemExportCertificate);

        jMenuBar.add(jMenuExport);

        jMenuTools.setText(this.rb.getResourceString( "menu.tools"));

        jMenuItemGenerateKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemGenerateKey.setText(this.rb.getResourceString( "menu.tools.generatekey"));
        jMenuItemGenerateKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateKeyActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemGenerateKey);
        jMenuTools.add(jSeparator4);

        jMenuItemGenerateCSRInitial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemGenerateCSRInitial.setText(this.rb.getResourceString( "menu.tools.generatecsr"));
        jMenuItemGenerateCSRInitial.setEnabled(false);
        jMenuItemGenerateCSRInitial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateCSRInitialActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemGenerateCSRInitial);

        jMenuItemImportCSRResponseInitial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportCSRResponseInitial.setText(this.rb.getResourceString( "menu.tools.importcsr"));
        jMenuItemImportCSRResponseInitial.setEnabled(false);
        jMenuItemImportCSRResponseInitial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportCSRResponseInitialActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemImportCSRResponseInitial);
        jMenuTools.add(jSeparator5);

        jMenuItemGenerateCSRRenew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemGenerateCSRRenew.setText(this.rb.getResourceString( "menu.tools.generatecsr.renew"));
        jMenuItemGenerateCSRRenew.setEnabled(false);
        jMenuItemGenerateCSRRenew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateCSRRenewActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemGenerateCSRRenew);

        jMenuItemImportCSRResponseRenew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemImportCSRResponseRenew.setText(this.rb.getResourceString( "menu.tools.importcsr.renew"));
        jMenuItemImportCSRResponseRenew.setEnabled(false);
        jMenuItemImportCSRResponseRenew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportCSRResponseRenewActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemImportCSRResponseRenew);

        jMenuBar.add(jMenuTools);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        jButtonAddCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image24x24.gif"))); // NOI18N
        jButtonAddCertificate.setText(this.rb.getResourceString( "button.newcertificate"));
        jButtonAddCertificate.setFocusable(false);
        jButtonAddCertificate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddCertificate.setMaximumSize(new java.awt.Dimension(99, 41));
        jButtonAddCertificate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddCertificateActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonAddCertificate);

        jButtonExportCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image24x24.gif"))); // NOI18N
        jButtonExportCertificate.setText(this.rb.getResourceString( "button.export"));
        jButtonExportCertificate.setFocusable(false);
        jButtonExportCertificate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportCertificate.setMaximumSize(new java.awt.Dimension(99, 41));
        jButtonExportCertificate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportCertificateActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonExportCertificate);

        jButtonEditCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image24x24.gif"))); // NOI18N
        jButtonEditCertificate.setText(this.rb.getResourceString( "button.edit"));
        jButtonEditCertificate.setFocusable(false);
        jButtonEditCertificate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEditCertificate.setMaximumSize(new java.awt.Dimension(99, 41));
        jButtonEditCertificate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonEditCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditCertificateActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonEditCertificate);

        jButtonDeleteCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image24x24.gif"))); // NOI18N
        jButtonDeleteCertificate.setText(this.rb.getResourceString( "button.delete"));
        jButtonDeleteCertificate.setFocusable(false);
        jButtonDeleteCertificate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDeleteCertificate.setMaximumSize(new java.awt.Dimension(99, 41));
        jButtonDeleteCertificate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDeleteCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteCertificateActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonDeleteCertificate);

        getContentPane().add(jToolBar, java.awt.BorderLayout.NORTH);

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jPanelModuleLockWarning.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 51, 0)));
        jPanelModuleLockWarning.setLayout(new java.awt.GridBagLayout());

        jLabelModuleLockedWarning.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelModuleLockedWarning.setForeground(new java.awt.Color(204, 51, 0));
        jLabelModuleLockedWarning.setText(this.rb.getResourceString( "module.locked"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelModuleLockWarning.add(jLabelModuleLockedWarning, gridBagConstraints);

        jButtonModuleLockInfo.setText("...");
        jButtonModuleLockInfo.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonModuleLockInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModuleLockInfoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelModuleLockWarning.add(jButtonModuleLockInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 9.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMain.add(jPanelModuleLockWarning, gridBagConstraints);

        jPanelCertificatesMain.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMain.add(jPanelCertificatesMain, gridBagConstraints);

        jPanelButton.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(this.rb.getResourceString( "button.ok" ));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButton.add(jButtonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanelMain.add(jPanelButton, gridBagConstraints);

        getContentPane().add(jPanelMain, java.awt.BorderLayout.CENTER);

        jPanelStatusBar.setLayout(new java.awt.GridBagLayout());

        jLabelKeystore.setText(this.rb.getResourceString( "label.keystore"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelStatusBar.add(jLabelKeystore, gridBagConstraints);

        jTextFieldCertFileInfo.setEditable(false);
        jTextFieldCertFileInfo.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        jPanelStatusBar.add(jTextFieldCertFileInfo, gridBagConstraints);

        jLabelTrustAnchor.setText(this.rb.getResourceString( "label.trustanchor"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 0);
        jPanelStatusBar.add(jLabelTrustAnchor, gridBagConstraints);

        jLabelTrustAnchorValue.setText("jLabelTrustAnchorValue");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        jPanelStatusBar.add(jLabelTrustAnchorValue, gridBagConstraints);

        jLabelWarnings.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelWarnings.setForeground(new java.awt.Color(0, 153, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 5);
        jPanelStatusBar.add(jLabelWarnings, gridBagConstraints);

        jPanelSep1.setLayout(new java.awt.GridBagLayout());

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
        jPanelSep1.add(jSeparator6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelStatusBar.add(jPanelSep1, gridBagConstraints);

        jPanelSep2.setLayout(new java.awt.GridBagLayout());

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 5);
        jPanelSep2.add(jSeparator7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelStatusBar.add(jPanelSep2, gridBagConstraints);

        getContentPane().add(jPanelStatusBar, java.awt.BorderLayout.SOUTH);

        setSize(new java.awt.Dimension(1035, 764));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemImportKeyJKSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportKeyJKSActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importJKSKey();
    }//GEN-LAST:event_jMenuItemImportKeyJKSActionPerformed

    private void jMenuItemExportKeyPKCS12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportKeyPKCS12ActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.exportPKCS12Key();
    }//GEN-LAST:event_jMenuItemExportKeyPKCS12ActionPerformed

    private void jMenuItemExportCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.exportSelectedCertificate();
    }//GEN-LAST:event_jMenuItemExportCertificateActionPerformed

    private void jMenuItemImportKeyPKCS12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportKeyPKCS12ActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importPKCS12Key();
    }//GEN-LAST:event_jMenuItemImportKeyPKCS12ActionPerformed

    private void jMenuItemImportCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importCertificate();
    }//GEN-LAST:event_jMenuItemImportCertificateActionPerformed

    private void jMenuItemImportKeyPEMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportKeyPEMActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importPEMKey();
    }//GEN-LAST:event_jMenuItemImportKeyPEMActionPerformed

    private void jButtonDeleteCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.deleteSelectedCertificate();
    }//GEN-LAST:event_jButtonDeleteCertificateActionPerformed

    private void jButtonEditCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.renameSelectedAlias();
    }//GEN-LAST:event_jButtonEditCertificateActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.saveAndClose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        this.dispose();
    }//GEN-LAST:event_closeDialog

    private void jMenuItemGenerateKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateKeyActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.generateKeypair();
    }//GEN-LAST:event_jMenuItemGenerateKeyActionPerformed

    private void jMenuItemGenerateCSRInitialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateCSRInitialActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.generateCSR(true);
    }//GEN-LAST:event_jMenuItemGenerateCSRInitialActionPerformed

    private void jMenuItemImportCSRResponseInitialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportCSRResponseInitialActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importCSRResponse(false);
        this.panelCertificates.refreshData();
        this.panelCertificates.displayTrustAnchor();
    }//GEN-LAST:event_jMenuItemImportCSRResponseInitialActionPerformed

    private void jMenuItemFileEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileEditActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.renameSelectedAlias();
    }//GEN-LAST:event_jMenuItemFileEditActionPerformed

    private void jMenuItemFileDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileDeleteActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.deleteSelectedCertificate();
    }//GEN-LAST:event_jMenuItemFileDeleteActionPerformed

    private void jMenuItemFileCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileCloseActionPerformed
        this.saveAndClose();
    }//GEN-LAST:event_jMenuItemFileCloseActionPerformed

    private void jMenuItemGenerateCSRRenewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateCSRRenewActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.generateCSR(false);
    }//GEN-LAST:event_jMenuItemGenerateCSRRenewActionPerformed

    private void jMenuItemImportCSRResponseRenewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportCSRResponseRenewActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importCSRResponse(true);
        this.panelCertificates.refreshData();
        this.panelCertificates.displayTrustAnchor();
    }//GEN-LAST:event_jMenuItemImportCSRResponseRenewActionPerformed

    private void jButtonAddCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.importCertificate();
    }//GEN-LAST:event_jButtonAddCertificateActionPerformed

    private void jButtonModuleLockInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModuleLockInfoActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        ModuleLock.displayDialogModuleLocked(parent, this.lockKeeper, this.moduleName);
    }//GEN-LAST:event_jButtonModuleLockInfoActionPerformed

    private void jButtonExportCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportCertificateActionPerformed
        if (!isOperationAllowed(false)) {
            return;
        }
        this.panelCertificates.exportSelectedCertificate();
    }//GEN-LAST:event_jButtonExportCertificateActionPerformed

    private void jMenuItemFileDeleteAllExpiredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileDeleteAllExpiredActionPerformed
        this.panelCertificates.deleteAllUnusedExpiredEntries();
    }//GEN-LAST:event_jMenuItemFileDeleteAllExpiredActionPerformed

    private void jMenuItemFileReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileReferenceActionPerformed
        this.panelCertificates.displayReference();
    }//GEN-LAST:event_jMenuItemFileReferenceActionPerformed

    private void jMenuItemFileKeyCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileKeyCopyActionPerformed
       this.panelCertificates.keycopy();
    }//GEN-LAST:event_jMenuItemFileKeyCopyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddCertificate;
    private javax.swing.JButton jButtonDeleteCertificate;
    private javax.swing.JButton jButtonEditCertificate;
    private javax.swing.JButton jButtonExportCertificate;
    private javax.swing.JButton jButtonModuleLockInfo;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelKeystore;
    private javax.swing.JLabel jLabelModuleLockedWarning;
    private javax.swing.JLabel jLabelTrustAnchor;
    private javax.swing.JLabel jLabelTrustAnchorValue;
    private javax.swing.JLabel jLabelWarnings;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuExport;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuImport;
    private javax.swing.JMenuItem jMenuItemExportCertificate;
    private javax.swing.JMenuItem jMenuItemExportKeyPKCS12;
    private javax.swing.JMenuItem jMenuItemFileClose;
    private javax.swing.JMenuItem jMenuItemFileDelete;
    private javax.swing.JMenuItem jMenuItemFileDeleteAllExpired;
    private javax.swing.JMenuItem jMenuItemFileEdit;
    private javax.swing.JMenuItem jMenuItemFileKeyCopy;
    private javax.swing.JMenuItem jMenuItemFileReference;
    private javax.swing.JMenuItem jMenuItemGenerateCSRInitial;
    private javax.swing.JMenuItem jMenuItemGenerateCSRRenew;
    private javax.swing.JMenuItem jMenuItemGenerateKey;
    private javax.swing.JMenuItem jMenuItemImportCSRResponseInitial;
    private javax.swing.JMenuItem jMenuItemImportCSRResponseRenew;
    private javax.swing.JMenuItem jMenuItemImportCertificate;
    private javax.swing.JMenuItem jMenuItemImportKeyJKS;
    private javax.swing.JMenuItem jMenuItemImportKeyPEM;
    private javax.swing.JMenuItem jMenuItemImportKeyPKCS12;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelCertificatesMain;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelModuleLockWarning;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelStatusBar;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTextField jTextFieldCertFileInfo;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables

    /**
     * Let this class listen to the underlaying table liste events, makes it a
     * ListSelectionListener
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.setButtonState();
    }

    /**
     * Sets the image size for the images of every popup menu of the certificate
     * manager
     *
     * @param imageSizePopup the imageSizePopup to set
     */
    public void setImageSizePopup(int imageSizePopup) {
        this.panelCertificates.setImageSizePopup(imageSizePopup);
    }
}
