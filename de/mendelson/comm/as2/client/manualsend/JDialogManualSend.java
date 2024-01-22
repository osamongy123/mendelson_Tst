//$Header: /as2/de/mendelson/comm/as2/client/manualsend/JDialogManualSend.java 40    20/01/23 10:53 Heller $
package de.mendelson.comm.as2.client.manualsend;

import de.mendelson.comm.as2.client.AS2StatusBar;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.comm.as2.partner.gui.ListCellRendererPartner;
import de.mendelson.util.LockingGlassPane;
import de.mendelson.util.MecFileChooser;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.datatransfer.TransferClientWithProgress;
import de.mendelson.util.uinotification.UINotification;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
 * Dialog to send a file to a single partner
 *
 * @author S.Heller
 * @version $Revision: 40 $
 */
public class JDialogManualSend extends JDialog {

    private final static boolean MULTIPLE_FILES = false;

    /**
     * ResourceBundle to localize the GUI
     */
    private MecResourceBundle rb = null;
    private Logger logger = Logger.getLogger("de.mendelson.as2.client");
    private List<Partner> localStations = new ArrayList<Partner>();
    //DB connection for the partner access
    private BaseClient baseClient;
    private AS2StatusBar statusbar;
    private final MendelsonMultiResolutionImage IMAGE_MANUAL_SEND
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/client/send.svg", 32, 48);
    /**
     * String that is displayed while the client uploads data to the server to
     * send
     */
    private String uploadDisplay;

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param uploadDisplay String that is displayed while the client uploads
     * data to the server to send
     */
    public JDialogManualSend(JFrame parent, BaseClient baseClient,
            AS2StatusBar statusbar, String uploadDisplay) {
        super(parent, true);
        this.statusbar = statusbar;
        this.uploadDisplay = uploadDisplay;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleManualSend.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.baseClient = baseClient;
        this.setTitle(this.rb.getResourceString("title"));
        initComponents();
        TextOverlay.addTo(this.jTextFieldFilename1, rb.getResourceString("label.filename.hint"));
        this.setMultiresolutionIcons();
        this.jButtonBrowse2.setVisible(MULTIPLE_FILES);
        this.jTextFieldFilename2.setVisible(MULTIPLE_FILES);
        this.jLabelFilename2.setVisible(MULTIPLE_FILES);

        this.getRootPane().setDefaultButton(this.jButtonOk);
        //fill in data
        try {
            PartnerListResponse response = (PartnerListResponse) baseClient.sendSync(
                    new PartnerListRequest(PartnerListRequest.LIST_ALL,
                            PartnerListRequest.DATA_COMPLETENESS_NAME_AS2ID_TYPE));
            List<Partner> allPartnerList = response.getList();
            for (Partner partner : allPartnerList) {
                if (partner.isLocalStation()) {
                    this.localStations.add(partner);
                } else {
                    this.jComboBoxRemotePartner.addItem(partner);
                }
            }
        } catch (Exception e) {
            this.logger.severe("JDialogManualSend: " + e.getMessage());
            UINotification.instance().addNotification(
                    IMAGE_MANUAL_SEND,
                    UINotification.TYPE_WARNING,
                    JDialogManualSend.this.rb.getResourceString("title"),
                    "JDialogManualSend: " + e.getMessage());
        }
        //single local station? No need to select the sender
        if (this.localStations.size() == 1) {
            this.jLabelSender.setVisible(false);
            this.jComboBoxLocalStations.setVisible(false);
        } else {
            //add all local stations to the selection box
            this.jComboBoxLocalStations.removeAllItems();
            for (Partner localStation : this.localStations) {
                this.jComboBoxLocalStations.addItem(localStation);
            }
            this.jComboBoxLocalStations.setSelectedItem(0);
        }
        this.jComboBoxRemotePartner.setRenderer(new ListCellRendererPartner());
        this.jComboBoxLocalStations.setRenderer(new ListCellRendererPartner());

        this.setButtonState();
    }

    private void setMultiresolutionIcons() {
        this.jLabelIcon.setIcon(new ImageIcon(IMAGE_MANUAL_SEND.toMinResolution(32)));
    }

    /**
     * Lock the component: Add a glasspane that prevents any action on the UI
     */
    private void lock() {
        //init glasspane for first use
        if (!(this.getGlassPane() instanceof LockingGlassPane)) {
            this.setGlassPane(new LockingGlassPane());
        }
        this.getGlassPane().setVisible(true);
        this.getGlassPane().requestFocusInWindow();
    }

    /**
     * Unlock the component: remove the glass pane that prevents any action on
     * the UI
     */
    private void unlock() {
        getGlassPane().setVisible(false);
    }

    /**
     * Sets the ok and cancel buttons of this GUI
     */
    private void setButtonState() {
        if (this.jRadioButtonSendFile.isSelected()) {
            this.jButtonOk.setEnabled(this.jTextFieldFilename1.getText().length() > 0);
        } else {
            this.jButtonOk.setEnabled(true);
        }
        this.jTextFieldFilename1.setEditable(this.jRadioButtonSendFile.isSelected());
        this.jTextFieldFilename1.setEnabled(this.jRadioButtonSendFile.isSelected());
        this.jTextFieldFilename2.setEditable(this.jRadioButtonSendFile.isSelected());
        this.jTextFieldFilename2.setEnabled(this.jRadioButtonSendFile.isSelected());
        this.jButtonBrowse1.setEnabled(this.jRadioButtonSendFile.isSelected());
        this.jButtonBrowse2.setEnabled(this.jRadioButtonSendFile.isSelected());
    }

    /**
     * Sends the data to the server and initializes a send
     */
    public ManualSendResponse performSend() throws Throwable {
        Partner receiver = (Partner) this.jComboBoxRemotePartner.getSelectedItem();
        Partner sender = null;
        if (this.localStations.size() == 1) {
            sender = this.localStations.get(0);
        } else {
            sender = (Partner) this.jComboBoxLocalStations.getSelectedItem();
        }
        ManualSendRequest request = new ManualSendRequest();
        request.setResendMessageId(null);
        request.setReceiverAS2Id(receiver.getAS2Identification());
        request.setSenderAS2Id(sender.getAS2Identification());
        if (this.jRadioButtonSendTestMessage.isSelected()) {
            request.setSendTestdata(true);
        } else {
            //upload the file to the server to enqueue it there
            List<String> uploadHashs = new ArrayList<String>();
            List<Path> files = new ArrayList<Path>();
            files.add(Paths.get(this.jTextFieldFilename1.getText()));
            if (this.jTextFieldFilename2.isVisible() && this.jTextFieldFilename2.getText().trim().length() > 0) {
                files.add(Paths.get(this.jTextFieldFilename2.getText()));
            }
            for (Path uploadFile : files) {
                InputStream inStream = null;
                try {
                    //perform the upload to the server, chunked
                    TransferClientWithProgress transferClient = new TransferClientWithProgress(
                            this.baseClient,
                            this.statusbar.getProgressPanel());
                    inStream = Files.newInputStream(uploadFile);
                    String uploadHash = transferClient.uploadChunkedWithProgress(inStream, this.uploadDisplay,
                            (int) Files.size(uploadFile));
                    uploadHashs.add(uploadHash);
                    request.addFilename(uploadFile.getFileName().toString(), null);
                } finally {
                    if (inStream != null) {
                        inStream.close();
                    }
                }
            }
            request.setUploadHashs(uploadHashs);
        }
        ManualSendResponse response = null;
        response = (ManualSendResponse) baseClient.sendSyncWaitInfinite(request);
        if (response.getException() != null) {
            throw (response.getException());
        }
        return (response);
    }

    /**
     * Allows to resend an existing transaction by keeping the original filename
     *
     * @param resendMessageId
     * @param sender
     * @param receiver
     * @param dataFile
     * @param originalFilename
     * @return
     * @throws Throwable
     */
    public ManualSendResponse performResend(String resendMessageId, Partner sender, Partner receiver,
            Path dataFile, String originalFilename, String subject) throws Throwable {
        InputStream inStream = null;
        ManualSendResponse response = null;
        try {
            if (dataFile == null) {
                throw new FileNotFoundException();
            }
            TransferClientWithProgress transferClient = new TransferClientWithProgress(
                    this.baseClient,
                    this.statusbar.getProgressPanel());
            inStream = Files.newInputStream(dataFile);
            //perform the upload to the server, chunked
            String uploadHash = transferClient.uploadChunkedWithProgress(inStream, this.uploadDisplay,
                    (int) Files.size(dataFile));
            ManualSendRequest request = new ManualSendRequest();
            request.setResendMessageId(resendMessageId);
            request.setUploadHash(uploadHash);
            request.addFilename(originalFilename, null);
            request.setReceiverAS2Id(receiver.getAS2Identification());
            request.setSenderAS2Id(sender.getAS2Identification());
            request.setSubject(subject);
            response = (ManualSendResponse) transferClient.uploadWaitInfinite(request);
            if (response.getException() != null) {
                throw (response.getException());
            }
        } finally {
            if (inStream != null) {
                inStream.close();

            }
        }
        return (response);
    }

    private void okButtonPressed() {
        this.jButtonOk.setEnabled(false);
        this.jButtonCancel.setEnabled(false);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JDialogManualSend.this.lock();
                try {
                    //perform send has an own progress bar, no need to set one here. This is never a resend of an existing
                    //transaction, set the resendMessageId to null
                    JDialogManualSend.this.performSend();
                    //display success dialog
                    JDialogManualSend.this.unlock();
                    JDialogManualSend.this.setVisible(false);
                    UINotification.instance().addNotification(IMAGE_MANUAL_SEND,
                            UINotification.TYPE_SUCCESS,
                            JDialogManualSend.this.rb.getResourceString("title"),
                            JDialogManualSend.this.rb.getResourceString("send.success"));
                } catch (Throwable e) {
                    JDialogManualSend.this.logger.warning("Manual send: " + e.getMessage());
                    UINotification.instance().addNotification(
                            IMAGE_MANUAL_SEND,
                            UINotification.TYPE_WARNING,
                            JDialogManualSend.this.rb.getResourceString("title"),
                            "Manual send: " + e.getMessage());
                } finally {
                    JDialogManualSend.this.unlock();
                    JDialogManualSend.this.setVisible(false);
                    JDialogManualSend.this.dispose();
                }
            }
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(runnable);
        executor.shutdown();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        jPanelEdit = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jLabelFilename1 = new javax.swing.JLabel();
        jTextFieldFilename1 = new javax.swing.JTextField();
        jLabelRemotePartner = new javax.swing.JLabel();
        jComboBoxRemotePartner = new javax.swing.JComboBox();
        jButtonBrowse1 = new javax.swing.JButton();
        jComboBoxLocalStations = new javax.swing.JComboBox();
        jLabelSender = new javax.swing.JLabel();
        jLabelFilename2 = new javax.swing.JLabel();
        jTextFieldFilename2 = new javax.swing.JTextField();
        jButtonBrowse2 = new javax.swing.JButton();
        jPanelSpace = new javax.swing.JPanel();
        jRadioButtonSendFile = new javax.swing.JRadioButton();
        jRadioButtonSendTestMessage = new javax.swing.JRadioButton();
        jLabelSendTestdata = new javax.swing.JLabel();
        jPanelSpace4 = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/manualsend/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelEdit.add(jLabelIcon, gridBagConstraints);

        jLabelFilename1.setText(this.rb.getResourceString( "label.filename"));
        jLabelFilename1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelFilename1MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelFilename1, gridBagConstraints);

        jTextFieldFilename1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldFilename1MouseClicked(evt);
            }
        });
        jTextFieldFilename1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldFilename1KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldFilename1, gridBagConstraints);

        jLabelRemotePartner.setText(this.rb.getResourceString( "label.partner"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelRemotePartner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jComboBoxRemotePartner, gridBagConstraints);

        jButtonBrowse1.setText("..");
        jButtonBrowse1.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowse1.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowse1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonBrowse1MouseClicked(evt);
            }
        });
        jButtonBrowse1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowse1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowse1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jComboBoxLocalStations, gridBagConstraints);

        jLabelSender.setText(this.rb.getResourceString( "label.localstation"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelEdit.add(jLabelSender, gridBagConstraints);

        jLabelFilename2.setText(this.rb.getResourceString( "label.filename"));
        jLabelFilename2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelFilename2MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelFilename2, gridBagConstraints);

        jTextFieldFilename2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldFilename2MouseClicked(evt);
            }
        });
        jTextFieldFilename2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldFilename2KeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jTextFieldFilename2, gridBagConstraints);

        jButtonBrowse2.setText("..");
        jButtonBrowse2.setToolTipText(this.rb.getResourceString( "button.browse"));
        jButtonBrowse2.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonBrowse2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonBrowse2MouseClicked(evt);
            }
        });
        jButtonBrowse2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowse2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelEdit.add(jButtonBrowse2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelEdit.add(jPanelSpace, gridBagConstraints);

        buttonGroup.add(jRadioButtonSendFile);
        jRadioButtonSendFile.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButtonSendFileStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelEdit.add(jRadioButtonSendFile, gridBagConstraints);

        buttonGroup.add(jRadioButtonSendTestMessage);
        jRadioButtonSendTestMessage.setSelected(true);
        jRadioButtonSendTestMessage.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButtonSendTestMessageStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelEdit.add(jRadioButtonSendTestMessage, gridBagConstraints);

        jLabelSendTestdata.setText(this.rb.getResourceString( "label.testdata"));
        jLabelSendTestdata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSendTestdataMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelEdit.add(jLabelSendTestdata, gridBagConstraints);

        jPanelSpace4.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanelSpace4.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanelEdit.add(jPanelSpace4, gridBagConstraints);

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

        setSize(new java.awt.Dimension(492, 350));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBrowse1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowse1ActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        MecFileChooser chooser = new MecFileChooser(parent,
                this.rb.getResourceString("label.selectfile"));
        chooser.browseFilename(this.jTextFieldFilename1);
        this.setButtonState();
    }//GEN-LAST:event_jButtonBrowse1ActionPerformed

    private void jTextFieldFilename1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFilename1KeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldFilename1KeyReleased

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.okButtonPressed();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jTextFieldFilename2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFilename2KeyReleased
        this.setButtonState();
    }//GEN-LAST:event_jTextFieldFilename2KeyReleased

    private void jButtonBrowse2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowse2ActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        MecFileChooser chooser = new MecFileChooser(parent,
                this.rb.getResourceString("label.selectfile"));
        chooser.browseFilename(this.jTextFieldFilename2);
        this.setButtonState();
    }//GEN-LAST:event_jButtonBrowse2ActionPerformed

    private void jLabelFilename1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFilename1MouseClicked
        this.jRadioButtonSendFile.setSelected(true);
    }//GEN-LAST:event_jLabelFilename1MouseClicked

    private void jLabelFilename2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFilename2MouseClicked
        this.jRadioButtonSendFile.setSelected(true);
    }//GEN-LAST:event_jLabelFilename2MouseClicked

    private void jLabelSendTestdataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSendTestdataMouseClicked
        this.jRadioButtonSendTestMessage.setSelected(true);
    }//GEN-LAST:event_jLabelSendTestdataMouseClicked

    private void jRadioButtonSendTestMessageStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButtonSendTestMessageStateChanged
        this.setButtonState();
    }//GEN-LAST:event_jRadioButtonSendTestMessageStateChanged

    private void jRadioButtonSendFileStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButtonSendFileStateChanged
        this.setButtonState();
    }//GEN-LAST:event_jRadioButtonSendFileStateChanged

    private void jTextFieldFilename1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldFilename1MouseClicked
        this.jRadioButtonSendFile.setSelected(true);
    }//GEN-LAST:event_jTextFieldFilename1MouseClicked

    private void jButtonBrowse1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonBrowse1MouseClicked
        if (!this.jRadioButtonSendFile.isSelected()) {
            this.jRadioButtonSendFile.setSelected(true);
            this.jButtonBrowse1.doClick();
        }
    }//GEN-LAST:event_jButtonBrowse1MouseClicked

    private void jTextFieldFilename2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldFilename2MouseClicked
        this.jRadioButtonSendFile.setSelected(true);
    }//GEN-LAST:event_jTextFieldFilename2MouseClicked

    private void jButtonBrowse2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonBrowse2MouseClicked
        if (!this.jRadioButtonSendFile.isSelected()) {
            this.jRadioButtonSendFile.setSelected(true);
            this.jButtonBrowse2.doClick();
        }
    }//GEN-LAST:event_jButtonBrowse2MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JButton jButtonBrowse1;
    private javax.swing.JButton jButtonBrowse2;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JComboBox jComboBoxLocalStations;
    private javax.swing.JComboBox jComboBoxRemotePartner;
    private javax.swing.JLabel jLabelFilename1;
    private javax.swing.JLabel jLabelFilename2;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelRemotePartner;
    private javax.swing.JLabel jLabelSendTestdata;
    private javax.swing.JLabel jLabelSender;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace4;
    private javax.swing.JRadioButton jRadioButtonSendFile;
    private javax.swing.JRadioButton jRadioButtonSendTestMessage;
    private javax.swing.JTextField jTextFieldFilename1;
    private javax.swing.JTextField jTextFieldFilename2;
    // End of variables declaration//GEN-END:variables
}
