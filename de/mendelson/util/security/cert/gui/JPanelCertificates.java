//$Header: /as2/de/mendelson/util/security/cert/gui/JPanelCertificates.java 56    12/12/22 14:18 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.AllowModificationCallback;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.modulelock.ModuleLock;
import de.mendelson.util.security.cert.CertificateInUseChecker;
import de.mendelson.util.security.cert.CertificateInUseInfo;
import de.mendelson.util.security.cert.CertificateInUseInfo.SingleCertificateInUseInfo;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeyCopyHandler;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.TableModelCertificates;
import de.mendelson.util.security.cert.clientserver.RefreshKeystoreCertificates;
import de.mendelson.util.tables.JTableColumnResizer;
import de.mendelson.util.tables.PersistentTableRowSorter;
import de.mendelson.util.tables.TableCellRendererDate;
import de.mendelson.util.uinotification.UINotification;
import java.awt.Color;
import java.security.cert.CertPath;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Panel to configure the Certificates
 *
 * @author S.Heller
 * @version $Revision: 56 $
 */
public class JPanelCertificates extends JPanel implements ListSelectionListener, PopupMenuListener {

    private final Logger logger;
    /**
     * Title used to render sub dialogs
     */
    private String title = null;
    private JButton editButton = null;
    private JButton deleteButton = null;
    private JMenuItem itemEdit = null;
    private JMenuItem itemDelete = null;
    private CertificateManager manager = null;
    private String keystoreType;
    private MecResourceBundle rb = null;
    public static final ImageIcon ICON_CERTIFICATE_ROOT
            = new ImageIcon(TableModelCertificates.IMAGE_ROOT_MULTIRESOLUTION.toMinResolution(16));
    public static final ImageIcon ICON_CERTIFICATE_UNTRUSTED
            = new ImageIcon(TableModelCertificates.IMAGE_UNTRUSTED_MULTIRESOLUTION.toMinResolution(16));
    private final List<CertificateInUseChecker> inUseCheckerList
            = Collections.synchronizedList(new ArrayList<CertificateInUseChecker>());
    private List<AllowModificationCallback> allowModificationCallbackList = new ArrayList<AllowModificationCallback>();
    private KeyCopyHandler keyCopyHandler = null;
    /**
     * Image size for the popup menus
     */
    private int imageSizePopup = JDialogCertificates.IMAGE_SIZE_POPUP;
    private Color colorOk = Color.green.darker().darker();
    private Color colorWarning = Color.red.darker();
    /**
     * Allows to set an external label to display the trust anchor information
     * in
     */
    private JLabel jLabelTrustAnchorValueAlternate = null;
    private JLabel jLabelWarnings = null;
    private final String moduleName;

    private final GUIClient guiClient;

    /**
     * Creates new form JPanelPartnerConfig
     *
     * @param moduleName The module name of this instance. As this Panel may be
     * used in TLS and ENC/Sign environment and there is a unique key required
     * for persistent settings this String should help
     */
    public JPanelCertificates(Logger logger, ListSelectionListener additionalListener,
            GUIClient guiClient, String moduleName) {
        if (moduleName == null) {
            moduleName = "";
        }
        this.moduleName = moduleName;
        this.logger = logger;
        this.guiClient = guiClient;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificates.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        initComponents();
        //add row sorter
        RowSorter<TableModel> sorter = new PersistentTableRowSorter<TableModel>(this.jTable.getModel(),
                this.getClass().getName() + "_" + this.moduleName);
        this.jTable.setRowHeight(TableModelCertificates.ROW_HEIGHT);
        this.jTable.setRowSorter(sorter);
        this.jTable.getTableHeader().setReorderingAllowed(false);
        this.jTable.getColumnModel().getColumn(0).setMaxWidth(TableModelCertificates.ROW_HEIGHT + this.jTable.getRowMargin() * 2);
        this.jTable.getColumnModel().getColumn(1).setMaxWidth(TableModelCertificates.ROW_HEIGHT + this.jTable.getRowMargin() * 2);
        this.jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jTable.getSelectionModel().addListSelectionListener(additionalListener);
        this.jTable.getSelectionModel().addListSelectionListener(this);
        this.jTable.setDefaultRenderer(Date.class, new TableCellRendererDate(DateFormat.getDateInstance(DateFormat.SHORT)));
        this.jPopupMenu.setInvoker(this.jScrollPaneTable);
        this.jPopupMenu.addPopupMenuListener(this);
        this.setMultiresolutionIcons();
    }

    /**
     * Overwrite the designers icons by multi resolution icons
     */
    private void setMultiresolutionIcons() {
        this.jMenuItemPopupDeleteEntry.setIcon(new ImageIcon(JDialogCertificates.IMAGE_DELETE_MULTIRESOLUTION.toMinResolution(this.imageSizePopup)));
        this.jMenuItemPopupExportCert.setIcon(new ImageIcon(JDialogCertificates.IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(this.imageSizePopup)));
        this.jMenuItemPopupExportKey.setIcon(new ImageIcon(JDialogCertificates.IMAGE_EXPORT_MULTIRESOLUTION.toMinResolution(this.imageSizePopup)));
        this.jMenuItemPopupRenameAlias.setIcon(new ImageIcon(JDialogCertificates.IMAGE_EDIT_MULTIRESOLUTION.toMinResolution(this.imageSizePopup)));
        this.jMenuItemPopupReference.setIcon(new ImageIcon(JDialogCertificates.IMAGE_REFERENCE.toMinResolution(this.imageSizePopup)));
        this.jMenuItemPopupKeyCopy.setIcon(new ImageIcon(JDialogCertificates.IMAGE_KEYCOPY.toMinResolution(this.imageSizePopup)));
    }

    /**
     * Will delete all entries that are red in the current certificate list
     */
    protected void deleteAllUnusedExpiredEntries() {
        List<KeystoreCertificate> certificateList = ((TableModelCertificates) this.jTable.getModel()).getCurrentCertificateList();
        List<KeystoreCertificate> expiredList = new ArrayList<KeystoreCertificate>();
        List<KeystoreCertificate> expiredButUsedList = new ArrayList<KeystoreCertificate>();
        Date currentDate = new Date();
        for (KeystoreCertificate certificate : certificateList) {
            if (certificate.getNotAfter().before(currentDate)) {
                //this is an expired certificate but perhaps it is in use - then it should not be deleted
                synchronized (this.inUseCheckerList) {
                    for (CertificateInUseChecker checker : this.inUseCheckerList) {
                        CertificateInUseInfo info = checker.checkUsed(certificate);
                        if (info.isEmpty()) {
                            //unused expired certificate
                            expiredList.add(certificate);
                        } else {
                            //used expired certificate - has to have the same state as an unexpired certificate 
                            //for this process
                            expiredButUsedList.add(certificate);
                        }
                    }
                }
            }
        }
        if (!expiredButUsedList.isEmpty()) {
            UINotification.instance().addNotification(JDialogCertificates.IMAGE_CERTIFICATE,
                    UINotification.TYPE_WARNING,
                    this.rb.getResourceString("warning.deleteallexpired.expired.but.used.title"),
                    this.rb.getResourceString("warning.deleteallexpired.expired.but.used.text",
                            String.valueOf(expiredButUsedList.size()))
            );
        }
        if (!expiredList.isEmpty()) {
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
            int requestValue = JOptionPane.showConfirmDialog(parent,
                    this.rb.getResourceString("warning.deleteallexpired.text", String.valueOf(expiredList.size())),
                    this.rb.getResourceString("warning.deleteallexpired.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (requestValue == JOptionPane.YES_OPTION) {
                for (KeystoreCertificate certToDelete : expiredList) {
                    try {
                        this.manager.deleteKeystoreEntry(certToDelete.getAlias());
                    } catch (Throwable e) {
                        UINotification.instance().addNotification(e);
                    }
                }
                try {
                    this.manager.saveKeystore();
                    RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
                    JPanelCertificates.this.guiClient.sendAsync(signal);
                    this.refreshData();
                    UINotification.instance().addNotification(JDialogCertificates.IMAGE_CERTIFICATE,
                            UINotification.TYPE_SUCCESS,
                            this.rb.getResourceString("success.deleteallexpired.title"),
                            this.rb.getResourceString("success.deleteallexpired.text", String.valueOf(expiredList.size())));
                } catch (Throwable e) {
                    UINotification.instance().addNotification(e);
                }
            }
        } else {
            UINotification.instance().addNotification(JDialogCertificates.IMAGE_CERTIFICATE,
                    UINotification.TYPE_ERROR,
                    this.rb.getResourceString("warning.deleteallexpired.noneavailable.title"),
                    this.rb.getResourceString("warning.deleteallexpired.noneavailable.text"));
        }
    }

    /**
     * Allows to setup an external alternate label where the trust label
     * information is displayed in. Calling this will invisible the local label
     *
     * @param jLabelTrustAnchorValueAlternate
     */
    protected void setExternalDisplayComponents(JLabel jLabelTrustAnchorValueAlternate, JLabel jLabelWarnings) {
        this.jLabelTrustAnchor.setVisible(false);
        this.jLabelTrustAnchorValue.setVisible(false);
        this.jLabelTrustAnchorValueAlternate = jLabelTrustAnchorValueAlternate;
        this.jLabelWarnings = jLabelWarnings;
        //adjust the warning and ok colors to keep contrast to the passed label
        if (this.jLabelWarnings != null) {
            this.colorOk = ColorUtil.getBestContrastColorAroundForeground(this.jLabelWarnings.getBackground(), colorOk);
            this.colorWarning = ColorUtil.getBestContrastColorAroundForeground(this.jLabelWarnings.getBackground(), colorWarning);
        }
    }

    /**
     * Adds a callback that is called if a user tries to modify the
     * configuration A modification will be prevented if one of the callbacks
     * does not allow it
     */
    protected void addAllowModificationCallback(AllowModificationCallback callback) {
        this.allowModificationCallbackList.add(callback);
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

    public void addKeystore(CertificateManager manager) {
        this.manager = manager;
        this.keystoreType = manager.getKeystoreType();
        if (this.moduleName.equals(ModuleLock.MODULE_ENCSIGN_KEYSTORE)) {
            this.jMenuItemPopupKeyCopy.setText(this.rb.getResourceString("button.keycopy",
                    this.rb.getResourceString("button.keycopy.tls")));
        } else {
            this.jMenuItemPopupKeyCopy.setText(this.rb.getResourceString("button.keycopy",
                    this.rb.getResourceString("button.keycopy.signencrypt")));
        }
        this.refreshData();
        JTableColumnResizer.adjustColumnWidthByContent(this.jTable);
        if (this.jTable.getRowCount() > 0) {
            this.jTable.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    private boolean keystoreIsReadonly() {
        return (!this.manager.canWrite());
    }

    /**
     * Adds the functionality to check if a certificate is in use and should not
     * be deleted
     *
     * @param checker
     */
    protected void addCertificateInUseChecker(CertificateInUseChecker checker) {
        synchronized (this.inUseCheckerList) {
            this.inUseCheckerList.add(checker);
            ((TableModelCertificates) this.jTable.getModel()).addCertificateInUseChecker(checker);
        }
    }

    protected void setSelectionByAlias(String selectedAlias) {
        if (selectedAlias != null) {
            for (int i = 0; i < ((TableModelCertificates) this.jTable.getModel()).getRowCount(); i++) {
                KeystoreCertificate certificate = ((TableModelCertificates) this.jTable.getModel()).getParameter(i);
                if (certificate.getAlias().equals(selectedAlias)) {
                    this.jTable.getSelectionModel().setSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    /**
     * Returns a single certificate of a row of the embedded table
     */
    protected KeystoreCertificate getSelectedCertificate() {
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow < 0) {
            return (null);
        }
        return (((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow));
    }

    /**
     * Returns the actual selected row
     */
    protected int getSelectedRow() {
        return (this.jTable.getSelectedRow());
    }

    protected void refreshData() {
        //try to keep the mark
        int selectedRow = this.jTable.getSelectedRow();
        String selectedAlias = null;
        if (selectedRow >= 0) {
            selectedAlias = (((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow)).getAlias();
        }
        if (this.jCheckBoxShowCACertificates.isSelected()) {
            //show all certificates
            ((TableModelCertificates) this.jTable.getModel()).setNewData(this.manager.getKeyStoreCertificateList());
        } else {
            List<KeystoreCertificate> keystoreCertList = new ArrayList<KeystoreCertificate>();
            List<KeystoreCertificate> keystoreCertListAll = this.manager.getKeyStoreCertificateList();
            //do not show the CA certificates
            for (KeystoreCertificate cert : keystoreCertListAll) {
                if (!cert.isCACertificate()) {
                    keystoreCertList.add(cert);
                }
            }
            ((TableModelCertificates) this.jTable.getModel()).setNewData(keystoreCertList);
        }
        for (int i = 0, rowCount = this.jTable.getRowCount(); i < rowCount; i++) {
            KeystoreCertificate cert = ((TableModelCertificates) this.jTable.getModel()).getParameter(i);
            if (cert.getAlias().equals(selectedAlias)) {
                this.jTable.getSelectionModel().setSelectionInterval(i, i);
                break;
            }
        }
    }

    /**
     * Lets this gui refresh the table
     */
    protected void certificateDeleted(int lastRow) {
        //last row? dec
        if (lastRow > this.jTable.getRowCount() - 1 && lastRow != 0) {
            lastRow--;
        }
        if (this.jTable.getRowCount() > 0) {
            this.jTable.getSelectionModel().setSelectionInterval(lastRow, lastRow);
        }
    }

    /**
     * Lets this gui refresh the table
     */
    protected void certificateAdded(String newAlias) {
        if (newAlias == null) {
            return;
        }
        for (int i = 0, rowCount = this.jTable.getRowCount(); i < rowCount; i++) {
            KeystoreCertificate cert = ((TableModelCertificates) this.jTable.getModel()).getParameter(i);
            if (cert.getAlias().equals(newAlias)) {
                this.jTable.getSelectionModel().setSelectionInterval(i, i);
                break;
            }
        }
    }

    /**
     * Lets this gui refresh the table
     */
    protected void certificateRenamedTo(String newAlias) {
        if (newAlias == null) {
            return;
        }
        for (int i = 0, rowCount = this.jTable.getRowCount(); i < rowCount; i++) {
            KeystoreCertificate cert = ((TableModelCertificates) this.jTable.getModel()).getParameter(i);
            if (cert.getAlias().equals(newAlias)) {
                this.jTable.getSelectionModel().setSelectionInterval(i, i);
                break;
            }
        }
    }

    /**
     * Allows the GUI to control the passed buttons
     */
    protected void setButtons(JButton editButton, JButton deleteButton) {
        this.editButton = editButton;
        this.deleteButton = deleteButton;
        this.setButtonState();
    }

    protected void setMenuItems(JMenuItem itemEdit, JMenuItem itemDelete) {
        this.itemEdit = itemEdit;
        this.itemDelete = itemDelete;
        this.setButtonState();
    }

    /**
     * Control the state of the panels buttons
     */
    private void setButtonState() {
        if (this.deleteButton != null) {
            this.deleteButton.setEnabled(this.jTable.getSelectedRow() >= 0);
        }
        if (this.editButton != null) {
            this.editButton.setEnabled(this.jTable.getSelectedRow() >= 0);
        }
        if (this.itemEdit != null) {
            this.itemEdit.setEnabled(this.jTable.getSelectedRow() >= 0);
        }
        if (this.itemDelete != null) {
            this.itemDelete.setEnabled(this.jTable.getSelectedRow() >= 0);
        }
    }

    /**
     * Makes this a ListSelectionListener
     */
    @Override
    public synchronized void valueChanged(ListSelectionEvent listSelectionEvent) {
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow >= 0) {
            final KeystoreCertificate certificate = ((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow);
            String infoText = certificate.getInfo();
            String extensionText = certificate.getInfoExtension();
            this.jEditorPaneInfo.setText(infoText);
            this.jTextAreaInfoExtension.setText(extensionText);
            List<KeystoreCertificate> trustChain = JPanelCertificates.this.computeTrustChain(certificate.getAlias());
            this.jTreeTrustChain.buildTree(trustChain);
            this.displayTrustAnchor();
            this.displayWarnings(certificate);
        }
        this.setButtonState();
    }

    /**
     * Displays warnings if a related label is set
     */
    private void displayWarnings(KeystoreCertificate certificate) {
        if (this.jLabelWarnings == null) {
            return;
        }
        this.jLabelWarnings.setText("");
        this.jLabelWarnings.setForeground(this.colorOk);
        boolean problem = false;
        for (String fingerprint : KeystoreCertificate.TEST_KEYS_FINGERPRINTS_SHA1) {
            if (fingerprint.equalsIgnoreCase(certificate.getFingerPrintSHA1())) {
                this.jLabelWarnings.setForeground(colorWarning);
                this.jLabelWarnings.setText(this.rb.getResourceString("warning.testkey"));
                problem = true;
                break;
            }
        }
        if (!problem) {
            try {
                certificate.getX509Certificate().checkValidity();
                if (certificate.getIsKeyPair()) {
                    this.jLabelWarnings.setText(this.rb.getResourceString("label.key.valid"));
                } else {
                    this.jLabelWarnings.setText(this.rb.getResourceString("label.cert.valid"));
                }
            } catch (Exception e) {
                //there is a problem...turn label color to red
                this.jLabelWarnings.setForeground(this.colorWarning);
                if (certificate.getIsKeyPair()) {
                    this.jLabelWarnings.setText(this.rb.getResourceString("label.key.invalid"));
                } else {
                    this.jLabelWarnings.setText(this.rb.getResourceString("label.cert.invalid"));
                }
            }
        }
    }

    /**
     * Compute the whole trust chain for pkcs#7 export
     */
    private List<KeystoreCertificate> computeTrustChain(String alias) {
        KeystoreCertificate certificate = this.manager.getKeystoreCertificate(alias);
        PKIXCertPathBuilderResult result = certificate.getPKIXCertPathBuilderResult(this.manager.getKeystore(),
                this.manager.getX509CertificateList());
        List<KeystoreCertificate> list = new ArrayList<KeystoreCertificate>();
        //self signed?
        if (result == null) {
            //it's a self signed certificate: return it without any CA/intermediate certs
            list.add(certificate);
        } else {
            //trusted cert
            CertPath certPath = result.getCertPath();
            for (Object cert : certPath.getCertificates()) {
                X509Certificate workingCert = (X509Certificate) cert;
                for (KeystoreCertificate availableKeystoreCert : this.manager.getKeyStoreCertificateList()) {
                    if (workingCert.equals(availableKeystoreCert.getX509Certificate())) {
                        list.add(0, availableKeystoreCert);
                    }
                }
            }
            X509Certificate anchorCertX509 = null;
            boolean trustChainComplete = false;
            if (list.isEmpty()) {
                anchorCertX509 = result.getTrustAnchor().getTrustedCert();
                KeystoreCertificate anchorKeystoreCertificate = new KeystoreCertificate();
                anchorKeystoreCertificate.setCertificate(anchorCertX509);
                list.add(anchorKeystoreCertificate);
                trustChainComplete = true;
            } else {
                anchorCertX509 = list.get(0).getX509Certificate();
            }
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
                    if (result != null) {
                        anchorCertX509 = result.getTrustAnchor().getTrustedCert();
                        if (!keyCertAnchor.getX509Certificate().equals(anchorCertX509)) {
                            for (KeystoreCertificate availableKeystoreCert : this.manager.getKeyStoreCertificateList()) {
                                if (anchorCertX509.equals(availableKeystoreCert.getX509Certificate())) {
                                    list.add(0, availableKeystoreCert);
                                }
                            }
                        } else {
                            trustChainComplete = true;
                        }
                    } else {
                        trustChainComplete = true;
                    }
                } else {
                    trustChainComplete = true;
                }
            }
            //if a certificate is imported two times into the keystore it will occure two or more times in a row in this list and
            //this will confuse the cert path display
            // - the following code will remove the certificates if they are two times in a row in the list
            KeystoreCertificate selectedCertificate = null;
            for (KeystoreCertificate cert : list) {
                if (cert.getAlias().equals(alias) && cert.getFingerPrintSHA1().equals(list.get(list.size() - 1).getFingerPrintSHA1())) {
                    selectedCertificate = cert;
                }
            }
            KeystoreCertificate lastCheckedCert = null;
            boolean repeatLoop = true;
            while (repeatLoop && list.size() > 1) {
                int deleteIndex = -1;
                for (int i = 0; i < list.size(); i++) {
                    KeystoreCertificate singleCert = list.get(i);
                    if (lastCheckedCert == null) {
                        lastCheckedCert = singleCert;
                    } else {
                        if (lastCheckedCert.getFingerPrintSHA1().equals(singleCert.getFingerPrintSHA1())) {
                            deleteIndex = i;
                            lastCheckedCert = null;
                            break;
                        }
                        lastCheckedCert = singleCert;
                    }
                }
                if (deleteIndex != -1) {
                    list.remove(deleteIndex);
                } else {
                    repeatLoop = false;
                }
            }
            //ensure that the selectedCertificate is always the last one in the path - it might be the same cert with an other
            //alias, too after this delete algorithm
            if (selectedCertificate != null) {
                list.remove(list.size() - 1);
                list.add(selectedCertificate);
            }
        }
        return (list);
    }

    protected void displayTrustAnchor() {
        JLabel usedDisplayLabel = this.jLabelTrustAnchorValue;
        if (this.jLabelTrustAnchorValueAlternate != null) {
            usedDisplayLabel = this.jLabelTrustAnchorValueAlternate;
        }
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                KeystoreCertificate certificate = ((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow);
                if (certificate.isRootCertificate()) {
                    usedDisplayLabel.setIcon(ICON_CERTIFICATE_ROOT);
                    usedDisplayLabel.setText("Root certificate");
                } else if (certificate.isSelfSigned()) {
                    //figure out the icon used to render the cert entry in the table
                    usedDisplayLabel.setIcon(((TableModelCertificates) this.jTable.getModel())
                            .getUnmodifiedIconForCertificate(certificate));
                    usedDisplayLabel.setText("Self signed");
                } else {
                    PKIXCertPathBuilderResult result = certificate.getPKIXCertPathBuilderResult(this.manager.getKeystore(), this.manager.getX509CertificateList());
                    if (result == null) {
                        usedDisplayLabel.setIcon(ICON_CERTIFICATE_UNTRUSTED);
                        usedDisplayLabel.setText("Untrusted");
                    } else {
                        TrustAnchor anchor = result.getTrustAnchor();
                        if (anchor == null) {
                            usedDisplayLabel.setIcon(ICON_CERTIFICATE_UNTRUSTED);
                            usedDisplayLabel.setText("Untrusted");
                        } else {
                            List<KeystoreCertificate> trustPath = this.computeTrustChain(certificate.getAlias());
                            //found a root in the cert path
                            usedDisplayLabel.setIcon(ICON_CERTIFICATE_ROOT);
                            usedDisplayLabel.setText(trustPath.get(0).getAlias());
                        }
                    }
                }
            } catch (Exception e) {
                usedDisplayLabel.setIcon(null);
                usedDisplayLabel.setText("--");
            }
        } else {
            usedDisplayLabel.setIcon(null);
            usedDisplayLabel.setText("--");
        }
    }

    protected void performDeleteParameter() {
        try {
            KeystoreCertificate certificate = ((TableModelCertificates) this.jTable.getModel()).getParameter(this.jTable.getSelectedRow());
            this.manager.deleteKeystoreEntry(certificate.getAlias());
            this.manager.saveKeystore();
            int selectedRow = this.jTable.getSelectedRow();
            this.refreshData();
            if (this.jTable.getRowCount() - 1 >= selectedRow) {
                this.jTable.getSelectionModel().setSelectionInterval(
                        selectedRow, selectedRow);
            } else if (this.jTable.getRowCount() > 0) {
                this.jTable.getSelectionModel().setSelectionInterval(
                        this.jTable.getRowCount() - 1, this.jTable.getRowCount() - 1);
            }
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected void renameSelectedAlias() {
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        String oldAlias = selectedCertificate.getAlias();
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        JDialogRenameEntry dialog = new JDialogRenameEntry(parent, this.manager, oldAlias,
                this.keystoreType, this.manager.getKeystorePass());
        dialog.setVisible(true);
        String newAlias = dialog.getNewAlias();
        dialog.dispose();
        try {
            this.manager.saveKeystore();
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
        //signal the server that there are changes in the keystore
        RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
        JPanelCertificates.this.guiClient.sendAsync(signal);
        this.refreshData();
        this.certificateRenamedTo(newAlias);
    }

    protected void deleteSelectedCertificate() {
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        boolean inUse = false;
        synchronized (this.inUseCheckerList) {
            for (CertificateInUseChecker checker : this.inUseCheckerList) {
                CertificateInUseInfo singleInfo = checker.checkUsed(selectedCertificate);
                if (!singleInfo.isEmpty()) {
                    inUse = true;
                }
            }
        }
        if (inUse) {
            UINotification.instance().addNotification(JDialogCertificates.IMAGE_DELETE_MULTIRESOLUTION,
                    UINotification.TYPE_WARNING,
                    this.rb.getResourceString("title.cert.in.use"),
                    this.rb.getResourceString("cert.delete.impossible"));
            return;
        }
        //ask the user if the cert should be really deleted, all data is lost
        int requestValue = JOptionPane.showConfirmDialog(
                this, this.rb.getResourceString("dialog.cert.delete.message", selectedCertificate.getAlias()),
                this.rb.getResourceString("dialog.cert.delete.title"),
                JOptionPane.YES_NO_OPTION);
        if (requestValue != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int selectedRow = this.getSelectedRow();
            this.manager.deleteKeystoreEntry(selectedCertificate.getAlias());
            this.manager.saveKeystore();
            RefreshKeystoreCertificates signal = new RefreshKeystoreCertificates();
            JPanelCertificates.this.guiClient.sendAsync(signal);
            this.refreshData();
            this.certificateDeleted(selectedRow);
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
    }

    /**
     * Exports a selected certificate
     */
    protected void exportSelectedCertificate() {
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        if (selectedCertificate != null) {
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
            JDialogExportCertificate dialog = new JDialogExportCertificate(parent, this.manager,
                    selectedCertificate.getAlias(), this.logger);
            dialog.setVisible(true);
        }
    }

    /**
     * Exports a key to a pkcs12 keystore
     */
    protected void exportPKCS12Key() {
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        String preselectionAlias = selectedCertificate == null ? null : selectedCertificate.getAlias();
        try {
            JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
            JDialogExportKeyPKCS12 dialog = new JDialogExportKeyPKCS12(parent, this.logger, this.manager,
                    preselectionAlias);
            dialog.setVisible(true);
        } catch (Exception e) {
            UINotification.instance().addNotification(e);
        }

    }

    /**
     * Displays a dialog that shows information about the usage of the selected
     * certificate
     */
    protected void displayReference() {
        List<SingleCertificateInUseInfo> infoList = new ArrayList<SingleCertificateInUseInfo>();
        KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
        synchronized (this.inUseCheckerList) {
            for (CertificateInUseChecker checker : this.inUseCheckerList) {
                CertificateInUseInfo info = checker.checkUsed(selectedCertificate);
                if (!info.isEmpty()) {
                    infoList.addAll(info.getUsageList());
                }
            }
        }
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        JDialogCertificateReference dialog = new JDialogCertificateReference(parent, infoList, selectedCertificate);
        dialog.setVisible(true);
    }

    /**
     * Checks if a reference functionality has been attached to the certificate
     * manager. This is only the case if a CertificateInUseChecker has been
     * attached. This should be the case for enc/signature manager only as in
     * the TLS manager there is no partner assigned to a certificate/key
     */
    protected boolean isReferenceFunctionAvailable() {
        synchronized (this.inUseCheckerList) {
            return (!this.inUseCheckerList.isEmpty());
        }
    }

    public void keycopy() {
        if (this.keyCopyHandler != null) {
            try {
                int selectedRow = this.jTable.getSelectedRow();
                if (selectedRow >= 0) {
                    KeystoreCertificate cert = ((TableModelCertificates) this.jTable.getModel()).getParameter(selectedRow);
                    this.keyCopyHandler.copyEntry(cert);
                }
            } catch (Throwable e) {
                UINotification.instance().addNotification(e);
            }
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

        jPopupMenu = new javax.swing.JPopupMenu();
        jMenuItemPopupExportKey = new javax.swing.JMenuItem();
        jMenuItemPopupExportCert = new javax.swing.JMenuItem();
        jMenuItemPopupRenameAlias = new javax.swing.JMenuItem();
        jMenuItemPopupReference = new javax.swing.JMenuItem();
        jMenuItemPopupKeyCopy = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPopupDeleteEntry = new javax.swing.JMenuItem();
        jSplitPane = new javax.swing.JSplitPane();
        jScrollPaneTable = new javax.swing.JScrollPane();
        jTable = new de.mendelson.util.tables.JTableSortable();
        jTabbedPaneInfo = new javax.swing.JTabbedPane();
        jScrollPaneInfo = new javax.swing.JScrollPane();
        jEditorPaneInfo = new javax.swing.JEditorPane();
        jScrollPaneInfoExtension = new javax.swing.JScrollPane();
        jTextAreaInfoExtension = new javax.swing.JTextArea();
        jScrollPaneTrustchain = new javax.swing.JScrollPane();
        jTreeTrustChain = new de.mendelson.util.security.cert.gui.JTreeTrustChain();
        jCheckBoxShowCACertificates = new javax.swing.JCheckBox();
        jLabelTrustAnchor = new javax.swing.JLabel();
        jLabelTrustAnchorValue = new javax.swing.JLabel();

        jMenuItemPopupExportKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupExportKey.setText(this.rb.getResourceString("label.key.export.pkcs12"));
        jMenuItemPopupExportKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupExportKeyActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupExportKey);

        jMenuItemPopupExportCert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupExportCert.setText(this.rb.getResourceString("label.cert.export"));
        jMenuItemPopupExportCert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupExportCertActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupExportCert);

        jMenuItemPopupRenameAlias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupRenameAlias.setText(this.rb.getResourceString("button.edit"));
        jMenuItemPopupRenameAlias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupRenameAliasActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupRenameAlias);

        jMenuItemPopupReference.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupReference.setText(this.rb.getResourceString("button.reference"));
        jMenuItemPopupReference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupReferenceActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupReference);

        jMenuItemPopupKeyCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupKeyCopy.setText(this.rb.getResourceString("button.keycopy"));
        jMenuItemPopupKeyCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupKeyCopyActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupKeyCopy);
        jPopupMenu.add(jSeparator1);

        jMenuItemPopupDeleteEntry.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/missing_image16x16.gif"))); // NOI18N
        jMenuItemPopupDeleteEntry.setText(this.rb.getResourceString( "button.delete"));
        jMenuItemPopupDeleteEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPopupDeleteEntryActionPerformed(evt);
            }
        });
        jPopupMenu.add(jMenuItemPopupDeleteEntry);

        setLayout(new java.awt.GridBagLayout());

        jSplitPane.setDividerLocation(200);
        jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTable.setModel(new TableModelCertificates());
        jTable.setDoubleBuffered(true);
        jTable.setShowHorizontalLines(false);
        jTable.setShowVerticalLines(false);
        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMouseClicked(evt);
            }
        });
        jScrollPaneTable.setViewportView(jTable);

        jSplitPane.setLeftComponent(jScrollPaneTable);

        jEditorPaneInfo.setEditable(false);
        jEditorPaneInfo.setDoubleBuffered(true);
        jScrollPaneInfo.setViewportView(jEditorPaneInfo);

        jTabbedPaneInfo.addTab(this.rb.getResourceString( "tab.info.basic" ), jScrollPaneInfo);

        jTextAreaInfoExtension.setEditable(false);
        jTextAreaInfoExtension.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextAreaInfoExtension.setLineWrap(true);
        jTextAreaInfoExtension.setWrapStyleWord(true);
        jTextAreaInfoExtension.setDoubleBuffered(true);
        jScrollPaneInfoExtension.setViewportView(jTextAreaInfoExtension);

        jTabbedPaneInfo.addTab(this.rb.getResourceString( "tab.info.extension" ), jScrollPaneInfoExtension);

        jTreeTrustChain.setDoubleBuffered(true);
        jScrollPaneTrustchain.setViewportView(jTreeTrustChain);

        jTabbedPaneInfo.addTab(this.rb.getResourceString( "tab.info.trustchain" ), jScrollPaneTrustchain);

        jSplitPane.setRightComponent(jTabbedPaneInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane, gridBagConstraints);

        jCheckBoxShowCACertificates.setText(this.rb.getResourceString( "display.ca.certs"));
        jCheckBoxShowCACertificates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxShowCACertificatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        add(jCheckBoxShowCACertificates, gridBagConstraints);

        jLabelTrustAnchor.setText(this.rb.getResourceString( "label.trustanchor"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(jLabelTrustAnchor, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabelTrustAnchorValue, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void jCheckBoxShowCACertificatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxShowCACertificatesActionPerformed
    this.refreshData();
}//GEN-LAST:event_jCheckBoxShowCACertificatesActionPerformed

private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
    if (evt.isPopupTrigger() || evt.isMetaDown()) {
        if (this.jTable.getSelectedRowCount() == 0) {
            return;
        }
        this.jPopupMenu.show(evt.getComponent(), evt.getX(),
                evt.getY());
    }
}//GEN-LAST:event_jTableMouseClicked

private void jMenuItemPopupDeleteEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupDeleteEntryActionPerformed
    if (!this.isOperationAllowed(false)) {
        return;
    }
    this.deleteSelectedCertificate();
}//GEN-LAST:event_jMenuItemPopupDeleteEntryActionPerformed

private void jMenuItemPopupRenameAliasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupRenameAliasActionPerformed
    if (!this.isOperationAllowed(false)) {
        return;
    }
    this.renameSelectedAlias();
}//GEN-LAST:event_jMenuItemPopupRenameAliasActionPerformed

private void jMenuItemPopupExportKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupExportKeyActionPerformed
    if (!this.isOperationAllowed(false)) {
        return;
    }
    this.exportPKCS12Key();
}//GEN-LAST:event_jMenuItemPopupExportKeyActionPerformed

private void jMenuItemPopupExportCertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupExportCertActionPerformed
    if (!this.isOperationAllowed(false)) {
        return;
    }
    this.exportSelectedCertificate();
}//GEN-LAST:event_jMenuItemPopupExportCertActionPerformed

    private void jMenuItemPopupReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupReferenceActionPerformed
        if (!this.isOperationAllowed(false)) {
            return;
        }
        this.displayReference();
    }//GEN-LAST:event_jMenuItemPopupReferenceActionPerformed

    private void jMenuItemPopupKeyCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPopupKeyCopyActionPerformed
        this.keycopy();
    }//GEN-LAST:event_jMenuItemPopupKeyCopyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxShowCACertificates;
    private javax.swing.JEditorPane jEditorPaneInfo;
    private javax.swing.JLabel jLabelTrustAnchor;
    private javax.swing.JLabel jLabelTrustAnchorValue;
    private javax.swing.JMenuItem jMenuItemPopupDeleteEntry;
    private javax.swing.JMenuItem jMenuItemPopupExportCert;
    private javax.swing.JMenuItem jMenuItemPopupExportKey;
    private javax.swing.JMenuItem jMenuItemPopupKeyCopy;
    private javax.swing.JMenuItem jMenuItemPopupReference;
    private javax.swing.JMenuItem jMenuItemPopupRenameAlias;
    private javax.swing.JPopupMenu jPopupMenu;
    private javax.swing.JScrollPane jScrollPaneInfo;
    private javax.swing.JScrollPane jScrollPaneInfoExtension;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JScrollPane jScrollPaneTrustchain;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JTabbedPane jTabbedPaneInfo;
    private de.mendelson.util.tables.JTableSortable jTable;
    private javax.swing.JTextArea jTextAreaInfoExtension;
    private de.mendelson.util.security.cert.gui.JTreeTrustChain jTreeTrustChain;
    // End of variables declaration//GEN-END:variables

    /**
     * Makes this a popup menu listener
     */
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        if (e.getSource() == this.jPopupMenu) {
            boolean operationAllowed = this.isOperationAllowed(true);
            KeystoreCertificate selectedCertificate = this.getSelectedCertificate();
            this.jMenuItemPopupExportCert.setEnabled(selectedCertificate != null);
            this.jMenuItemPopupExportKey.setEnabled(selectedCertificate != null
                    && selectedCertificate.getIsKeyPair());
            this.jMenuItemPopupDeleteEntry.setEnabled(operationAllowed && !this.keystoreIsReadonly());
            this.jMenuItemPopupRenameAlias.setEnabled(operationAllowed && !this.keystoreIsReadonly());
            synchronized (this.inUseCheckerList) {
                this.jMenuItemPopupReference.setEnabled(selectedCertificate != null
                        && !this.inUseCheckerList.isEmpty()
                );
            }
            this.jMenuItemPopupKeyCopy.setEnabled(this.keyCopyHandler != null);
        }
    }

    /**
     * Makes this a popup menu listener
     */
    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    /**
     * Makes this a popup menu listener
     */
    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }

    /**
     * Sets the image size for the images of every popup menu of the certificate
     * manager
     *
     * @param imageSizePopup the imageSizePopup to set
     */
    public void setImageSizePopup(int imageSizePopup) {
        this.imageSizePopup = imageSizePopup;
        this.setMultiresolutionIcons();
    }

    /**
     * Allows to copy a selected entry of the keystore manager to another
     * keystore manager of the system
     *
     * @return the keyCopyHandler
     */
    public KeyCopyHandler getKeyCopyHandler() {
        return keyCopyHandler;
    }

    /**
     * Allows to copy a selected entry of the keystore manager to another
     * keystore manager of the system
     *
     * @param keyCopyHandler the keyCopyHandler to set
     */
    public void setKeyCopyHandler(KeyCopyHandler keyCopyHandler) {
        this.keyCopyHandler = keyCopyHandler;
    }
}
