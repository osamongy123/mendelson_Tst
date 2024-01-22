//$Header: /as2/de/mendelson/comm/as2/preferences/PreferencesPanelNotification.java 51    14/10/22 9:36 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.client.AS2StatusBar;
import de.mendelson.comm.as2.clientserver.message.PerformNotificationTestRequest;
import de.mendelson.comm.as2.server.ServerPlugins;
import de.mendelson.util.JTextFieldLimitDocument;
import de.mendelson.util.systemevents.notification.NotificationData;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetRequest;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetResponse;
import de.mendelson.util.systemevents.notification.clientserver.NotificationSetMessage;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.oauth2.OAuth2Config;
import de.mendelson.util.oauth2.gui.JDialogOAuth2Config;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.systemevents.notification.NotificationDataImplAS2;
import de.mendelson.util.uinotification.UINotification;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Panel to define the directory preferences
 *
 * @author S.Heller
 * @version: $Revision: 51 $
 */
public class PreferencesPanelNotification extends PreferencesPanel {

    /**
     * Localize the GUI
     */
    private MecResourceBundle rb = null;
    private PreferencesClient preferences;
    private BaseClient baseClient;
    private boolean inInit = false;
    private AS2StatusBar statusbar;
    private Logger logger = Logger.getLogger("de.mendelson.as2.client");
    private OAuth2Config oauth2Config = null;

    private final static MendelsonMultiResolutionImage IMAGE_NOTIFICATION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/notification.svg",
                    JDialogPreferences.IMAGE_HEIGHT);
    private final static MendelsonMultiResolutionImage IMAGE_TESTCONNECTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/testconnection.svg", 24);
    private final static MendelsonMultiResolutionImage IMAGE_OAUTH2
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/oauth2/gui/oauth2.svg", 24);

    /**
     * Creates new form PreferencesPanelDirectories
     */
    public PreferencesPanelNotification(BaseClient baseClient, AS2StatusBar statusbar) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.baseClient = baseClient;
        this.statusbar = statusbar;
        this.preferences = new PreferencesClient(baseClient);
        this.initComponents();
        PasswordOverlay.addTo(this.jPasswordFieldSMTPPass,
                this.rb.getResourceString("label.smtpauthorization.pass.hint"));
        TextOverlay.addTo(this.jTextFieldSMTPUser,
                this.rb.getResourceString("label.smtpauthorization.user.hint"));
        TextOverlay.addTo(this.jTextFieldHost,
                this.rb.getResourceString("label.mailhost.hint"));
        TextOverlay.addTo(this.jTextFieldPort,
                this.rb.getResourceString("label.mailport.hint"));
        this.setMultiresolutionIcons();
        this.inInit = true;
        this.jComboBoxSecurity.removeAllItems();
        this.jComboBoxSecurity.addItem(new SecurityEntry(NotificationData.SECURITY_PLAIN));
        this.jComboBoxSecurity.addItem(new SecurityEntry(NotificationData.SECURITY_START_TLS));
        this.jComboBoxSecurity.addItem(new SecurityEntry(NotificationData.SECURITY_TLS));
        this.jTextFieldPort.setDocument(new JTextFieldLimitDocument(5));
        this.initializeHelp();
        this.inInit = false;
    }

    private void setMultiresolutionIcons() {
        this.jButtonSendTestMail.setIcon(new ImageIcon(IMAGE_TESTCONNECTION.toMinResolution(24)));
        this.jButtonOAuth2.setIcon(new ImageIcon(IMAGE_OAUTH2.toMinResolution(24)));
    }

    private void initializeHelp() {
        this.jPanelUIHelpMaxMailsPerMin.setToolTip(this.rb, "label.maxmailspermin.help");
        this.jPanelUIHelpSMTPPort.setToolTip(this.rb, "label.mailport.help");
    }

    /**
     * Sets new preferences to this panel to changes/modify
     */
    @Override
    public void loadPreferences() {
        this.inInit = true;
        if (!this.isPluginActivated(ServerPlugins.PLUGIN_OAUTH2)) {
            this.jRadioButtonAuthorizationOAuth2.setEnabled(false);
            this.jButtonOAuth2.setEnabled(false);
            this.jTextFieldOAuth2.setEnabled(false);
            this.jTextFieldOAuth2.setEditable(false);
        }
        NotificationDataImplAS2 data
                = (NotificationDataImplAS2) ((NotificationGetResponse) this.baseClient.sendSync(new NotificationGetRequest())).getData();
        this.jTextFieldHost.setText(data.getMailServer());
        this.jTextFieldNotificationMail.setText(data.getNotificationMail());
        this.jTextFieldPort.setText(String.valueOf(data.getMailServerPort()));
        this.jTextFieldReplyTo.setText(data.getReplyTo());
        this.jCheckBoxNotifyCert.setSelected(data.notifyCertExpire());
        this.jCheckBoxNotifyTransactionError.setSelected(data.notifyTransactionError());
        this.jCheckBoxNotifyCEM.setSelected(data.notifyCEM());
        this.jCheckBoxNotifySystemFailure.setSelected(data.notifySystemFailure());
        this.jCheckBoxNotifyResend.setSelected(data.notifyResendDetected());
        if (data.usesSMTPAuthCredentials()) {
            this.jRadioButtonAuthorizationCredentials.setSelected(true);
        } else if (data.usesSMTPAuthOAuth2()) {
            this.jRadioButtonAuthorizationOAuth2.setSelected(true);
        } else {
            this.jRadioButtonAuthorizationNone.setSelected(true);
        }
        this.jCheckBoxNotifyConnectionProblem.setSelected(data.notifyConnectionProblem());
        this.jCheckBoxNotifyPostprocessing.setSelected(data.notifyPostprocessingProblem());
        if (data.getSMTPUser() != null) {
            this.jTextFieldSMTPUser.setText(data.getSMTPUser());
        } else {
            this.jTextFieldSMTPUser.setText("");
        }
        if (data.getSMTPPass() != null) {
            this.jPasswordFieldSMTPPass.setText(String.valueOf(data.getSMTPPass()));
        } else {
            this.jPasswordFieldSMTPPass.setText("");
        }
        this.jComboBoxSecurity.setSelectedItem(new SecurityEntry(data.getConnectionSecurity()));
        this.jTextFieldMaxMailsPerMin.setText(String.valueOf(data.getMaxNotificationsPerMin()));
        this.oauth2Config = data.getOAuth2Config();
        this.displayCurrentOAuth2();
        this.inInit = false;
        this.setButtonState();
    }

    private void displayCurrentOAuth2() {
        if (this.oauth2Config == null) {
            this.jTextFieldOAuth2.setText("--");
        } else {
            this.jTextFieldOAuth2.setText(this.oauth2Config.toString());
        }
    }

    private void setButtonState() {
        this.jTextFieldSMTPUser.setEnabled(this.jRadioButtonAuthorizationCredentials.isSelected());
        this.jPasswordFieldSMTPPass.setEnabled(this.jRadioButtonAuthorizationCredentials.isSelected());
        this.jTextFieldOAuth2.setEnabled(this.jRadioButtonAuthorizationOAuth2.isSelected());
        this.jButtonSendTestMail.setEnabled(
                !this.jRadioButtonAuthorizationOAuth2.isSelected()
                || (this.jRadioButtonAuthorizationOAuth2.isSelected() && this.oauth2Config != null));
    }

    /**
     * Captures the gui settings, creates a notification data object from these
     * settings and returns this
     */
    private NotificationDataImplAS2 captureGUIData() {
        NotificationDataImplAS2 data = new NotificationDataImplAS2();
        data.setMailServer(this.jTextFieldHost.getText());
        try {
            data.setMailServerPort(Integer.valueOf(this.jTextFieldPort.getText()).intValue());
        } catch (NumberFormatException e) {
            //if there is nonsense in this field just take the default value of the object
        }
        data.setNotifyCertExpire(this.jCheckBoxNotifyCert.isSelected());
        data.setNotifyTransactionError(this.jCheckBoxNotifyTransactionError.isSelected());
        data.setNotifyCEM(this.jCheckBoxNotifyCEM.isSelected());
        data.setNotifySystemFailure(this.jCheckBoxNotifySystemFailure.isSelected());
        data.setNotificationMail(this.jTextFieldNotificationMail.getText());
        data.setUsesSMTPAuthCredentials(this.jRadioButtonAuthorizationCredentials.isSelected());
        data.setUsesSMTPAuthOAuth2(this.jRadioButtonAuthorizationOAuth2.isSelected());
        data.setOAuth2Config(this.oauth2Config);
        data.setSMTPUser(this.jTextFieldSMTPUser.getText());
        data.setSMTPPass(this.jPasswordFieldSMTPPass.getPassword());
        data.setReplyTo(this.jTextFieldReplyTo.getText());
        data.setNotifyResendDetected(this.jCheckBoxNotifyResend.isSelected());
        data.setNotifyConnectionProblem(this.jCheckBoxNotifyConnectionProblem.isSelected());
        data.setConnectionSecurity(((SecurityEntry) this.jComboBoxSecurity.getSelectedItem()).getValue());
        data.setNotifyPostprocessingProblem(this.jCheckBoxNotifyPostprocessing.isSelected());
        try {
            data.setMaxNotificationsPerMin(Integer.valueOf(this.jTextFieldMaxMailsPerMin.getText()).intValue());
        } catch (Exception e) {
            //nop, ignore
        }
        return (data);
    }

    /**
     * Capture the GUI values and store them in the database
     *
     */
    @Override
    public void savePreferences() {
        NotificationData data = this.captureGUIData();
        NotificationSetMessage message = new NotificationSetMessage();
        message.setData(data);
        this.baseClient.sendAsync(message);

    }

    private void sendTestMail() {
        final String uniqueId = this.getClass().getName() + ".sendTestMail." + System.currentTimeMillis();
        Runnable test = new Runnable() {

            @Override
            public void run() {
                try {
                    PreferencesPanelNotification.this.statusbar.startProgressIndeterminate(
                            PreferencesPanelNotification.this.rb.getResourceString("testmail"), uniqueId);
                    NotificationData data = PreferencesPanelNotification.this.captureGUIData();
                    PerformNotificationTestRequest message = new PerformNotificationTestRequest(data);
                    ClientServerResponse response = PreferencesPanelNotification.this.baseClient.sendSync(message);
                    PreferencesPanelNotification.this.statusbar.stopProgressIfExists(uniqueId);
                    if (response == null) {
                        UINotification.instance().addNotification(
                                null,
                                UINotification.TYPE_ERROR,
                                PreferencesPanelNotification.this.rb.getResourceString("testmail.title"),
                                PreferencesPanelNotification.this.rb.getResourceString("testmail.message.error", "Timeout")
                        );
                        return;
                    }
                    if (response.getException() != null) {
                        String body = PreferencesPanelNotification.this.rb.getResourceString("testmail.message.error",
                                response.getException().getMessage());
                        UINotification.instance().addNotification(
                                null,
                                UINotification.TYPE_ERROR,
                                PreferencesPanelNotification.this.rb.getResourceString("testmail.title"),
                                body
                        );
                    } else {
                        UINotification.instance().addNotification(
                                null,
                                UINotification.TYPE_SUCCESS,
                                PreferencesPanelNotification.this.rb.getResourceString("testmail.title"),
                                PreferencesPanelNotification.this.rb.getResourceString("testmail.message.success")
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UINotification.instance().addNotification(e);
                } finally {
                    PreferencesPanelNotification.this.statusbar.stopProgressIfExists(uniqueId);
                }
            }
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(test);
        executor.shutdown();
    }

    private void setupOAuth2() {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        OAuth2Config config = null;
        if (this.oauth2Config == null) {
            config = new OAuth2Config();
        } else {
            config = this.oauth2Config;
        }
        JDialogOAuth2Config dialog = new JDialogOAuth2Config(parentFrame,
                this.baseClient,
                config,
                AS2ServerVersion.getProductName(),
                JDialogOAuth2Config.DIALOG_TYPE_SMTP
        );
        dialog.setVisible(true);
        if (dialog.okPressed()) {
            this.oauth2Config = config;
        }
        dialog.dispose();
        this.displayCurrentOAuth2();
        this.setButtonState();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupAuthorization = new javax.swing.ButtonGroup();
        jPanelMargin = new javax.swing.JPanel();
        jPanelSpace = new javax.swing.JPanel();
        jLabelHost = new javax.swing.JLabel();
        jTextFieldHost = new javax.swing.JTextField();
        jLabelPort = new javax.swing.JLabel();
        jTextFieldPort = new javax.swing.JTextField();
        jTextFieldNotificationMail = new javax.swing.JTextField();
        jLabelReplyTo = new javax.swing.JLabel();
        jTextFieldReplyTo = new javax.swing.JTextField();
        jButtonSendTestMail = new javax.swing.JButton();
        jPanelSep = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jPasswordFieldSMTPPass = new javax.swing.JPasswordField();
        jTextFieldSMTPUser = new javax.swing.JTextField();
        jLabelUser = new javax.swing.JLabel();
        jLabelPass = new javax.swing.JLabel();
        jLabelSecurity = new javax.swing.JLabel();
        jComboBoxSecurity = new javax.swing.JComboBox();
        jTextFieldMaxMailsPerMin = new javax.swing.JTextField();
        jPanelNotificationSelection = new javax.swing.JPanel();
        jCheckBoxNotifyPostprocessing = new javax.swing.JCheckBox();
        jCheckBoxNotifyResend = new javax.swing.JCheckBox();
        jCheckBoxNotifySystemFailure = new javax.swing.JCheckBox();
        jCheckBoxNotifyConnectionProblem = new javax.swing.JCheckBox();
        jCheckBoxNotifyCEM = new javax.swing.JCheckBox();
        jCheckBoxNotifyTransactionError = new javax.swing.JCheckBox();
        jCheckBoxNotifyCert = new javax.swing.JCheckBox();
        jPanelMaxMailsPerMin = new javax.swing.JPanel();
        jLabelMaxMailsPerMain = new javax.swing.JLabel();
        jPanelUIHelpMaxMailsPerMin = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelSpacer = new javax.swing.JPanel();
        jPanelUIHelpSMTPPort = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelUIHelpLabelNotificationMailReceiver = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jRadioButtonAuthorizationNone = new javax.swing.JRadioButton();
        jRadioButtonAuthorizationCredentials = new javax.swing.JRadioButton();
        jRadioButtonAuthorizationOAuth2 = new javax.swing.JRadioButton();
        jLabelSMTPAuthorization = new javax.swing.JLabel();
        jPanelSpace3434 = new javax.swing.JPanel();
        jPanelOAuth2 = new javax.swing.JPanel();
        jTextFieldOAuth2 = new javax.swing.JTextField();
        jButtonOAuth2 = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jPanelMargin.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelMargin.add(jPanelSpace, gridBagConstraints);

        jLabelHost.setText(this.rb.getResourceString("label.mailhost"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMargin.add(jLabelHost, gridBagConstraints);

        jTextFieldHost.setMinimumSize(new java.awt.Dimension(180, 20));
        jTextFieldHost.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMargin.add(jTextFieldHost, gridBagConstraints);

        jLabelPort.setText(this.rb.getResourceString( "label.mailport"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelPort, gridBagConstraints);

        jTextFieldPort.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextFieldPort.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jTextFieldPort, gridBagConstraints);

        jTextFieldNotificationMail.setMinimumSize(new java.awt.Dimension(220, 20));
        jTextFieldNotificationMail.setPreferredSize(new java.awt.Dimension(220, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.gridwidth = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMargin.add(jTextFieldNotificationMail, gridBagConstraints);

        jLabelReplyTo.setText(this.rb.getResourceString( "label.replyto"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMargin.add(jLabelReplyTo, gridBagConstraints);

        jTextFieldReplyTo.setMinimumSize(new java.awt.Dimension(180, 20));
        jTextFieldReplyTo.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMargin.add(jTextFieldReplyTo, gridBagConstraints);

        jButtonSendTestMail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jButtonSendTestMail.setText(this.rb.getResourceString("button.testmail"));
        jButtonSendTestMail.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSendTestMail.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonSendTestMail.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSendTestMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendTestMailActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 14;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanelMargin.add(jButtonSendTestMail, gridBagConstraints);

        jPanelSep.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelSep.add(jSeparator1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 27;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanelMargin.add(jPanelSep, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 27;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanelMargin.add(jSeparator2, gridBagConstraints);

        jPasswordFieldSMTPPass.setMinimumSize(new java.awt.Dimension(180, 20));
        jPasswordFieldSMTPPass.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelMargin.add(jPasswordFieldSMTPPass, gridBagConstraints);

        jTextFieldSMTPUser.setMinimumSize(new java.awt.Dimension(180, 20));
        jTextFieldSMTPUser.setPreferredSize(new java.awt.Dimension(180, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelMargin.add(jTextFieldSMTPUser, gridBagConstraints);

        jLabelUser.setText(this.rb.getResourceString( "label.smtpauthorization.user"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelUser, gridBagConstraints);

        jLabelPass.setText(this.rb.getResourceString( "label.smtpauthorization.pass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelPass, gridBagConstraints);

        jLabelSecurity.setText(this.rb.getResourceString( "label.security"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMargin.add(jLabelSecurity, gridBagConstraints);

        jComboBoxSecurity.setMinimumSize(new java.awt.Dimension(100, 20));
        jComboBoxSecurity.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMargin.add(jComboBoxSecurity, gridBagConstraints);

        jTextFieldMaxMailsPerMin.setMinimumSize(new java.awt.Dimension(40, 20));
        jTextFieldMaxMailsPerMin.setPreferredSize(new java.awt.Dimension(40, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanelMargin.add(jTextFieldMaxMailsPerMin, gridBagConstraints);

        jPanelNotificationSelection.setLayout(new java.awt.GridBagLayout());

        jCheckBoxNotifyPostprocessing.setText(this.rb.getResourceString( "checkbox.notifypostprocessing"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jCheckBoxNotifyPostprocessing, gridBagConstraints);

        jCheckBoxNotifyResend.setText(this.rb.getResourceString( "checkbox.notifyresend"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jCheckBoxNotifyResend, gridBagConstraints);

        jCheckBoxNotifySystemFailure.setText(this.rb.getResourceString( "checkbox.notifyfailure"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jCheckBoxNotifySystemFailure, gridBagConstraints);

        jCheckBoxNotifyConnectionProblem.setText(this.rb.getResourceString( "checkbox.notifyconnectionproblem"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanelNotificationSelection.add(jCheckBoxNotifyConnectionProblem, gridBagConstraints);

        jCheckBoxNotifyCEM.setText(this.rb.getResourceString("checkbox.notifycem"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanelNotificationSelection.add(jCheckBoxNotifyCEM, gridBagConstraints);

        jCheckBoxNotifyTransactionError.setText(this.rb.getResourceString("checkbox.notifytransactionerror"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jCheckBoxNotifyTransactionError, gridBagConstraints);

        jCheckBoxNotifyCert.setText(this.rb.getResourceString( "checkbox.notifycertexpire"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jCheckBoxNotifyCert, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 22;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanelMargin.add(jPanelNotificationSelection, gridBagConstraints);

        jPanelMaxMailsPerMin.setLayout(new java.awt.GridBagLayout());

        jLabelMaxMailsPerMain.setText(this.rb.getResourceString("label.maxmailspermin"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMaxMailsPerMin.add(jLabelMaxMailsPerMain, gridBagConstraints);

        jPanelUIHelpMaxMailsPerMin.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        jPanelMaxMailsPerMin.add(jPanelUIHelpMaxMailsPerMin, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelMaxMailsPerMin.add(jPanelSpacer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanelMargin.add(jPanelMaxMailsPerMin, gridBagConstraints);

        jPanelUIHelpSMTPPort.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 6;
        jPanelMargin.add(jPanelUIHelpSMTPPort, gridBagConstraints);

        jPanelUIHelpLabelNotificationMailReceiver.setToolTipText(this.rb.getResourceString( "label.notificationmail.help"));
        jPanelUIHelpLabelNotificationMailReceiver.setText(this.rb.getResourceString( "label.notificationmail"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanelMargin.add(jPanelUIHelpLabelNotificationMailReceiver, gridBagConstraints);

        buttonGroupAuthorization.add(jRadioButtonAuthorizationNone);
        jRadioButtonAuthorizationNone.setText(this.rb.getResourceString("label.smtpauthorization.none"));
        jRadioButtonAuthorizationNone.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonAuthorizationNoneItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jRadioButtonAuthorizationNone, gridBagConstraints);

        buttonGroupAuthorization.add(jRadioButtonAuthorizationCredentials);
        jRadioButtonAuthorizationCredentials.setText(this.rb.getResourceString("label.smtpauthorization.credentials"));
        jRadioButtonAuthorizationCredentials.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonAuthorizationCredentialsItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jRadioButtonAuthorizationCredentials, gridBagConstraints);

        buttonGroupAuthorization.add(jRadioButtonAuthorizationOAuth2);
        jRadioButtonAuthorizationOAuth2.setText(this.rb.getResourceString("label.smtpauthorization.oauth2"));
        jRadioButtonAuthorizationOAuth2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonAuthorizationOAuth2ItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jRadioButtonAuthorizationOAuth2, gridBagConstraints);

        jLabelSMTPAuthorization.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabelSMTPAuthorization.setText(this.rb.getResourceString("label.smtpauthorization.header"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMargin.add(jLabelSMTPAuthorization, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 27;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jPanelSpace3434, gridBagConstraints);

        jPanelOAuth2.setLayout(new java.awt.GridBagLayout());

        jTextFieldOAuth2.setEditable(false);
        jTextFieldOAuth2.setText("--");
        jTextFieldOAuth2.setMinimumSize(new java.awt.Dimension(400, 22));
        jTextFieldOAuth2.setPreferredSize(new java.awt.Dimension(400, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelOAuth2.add(jTextFieldOAuth2, gridBagConstraints);

        jButtonOAuth2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jButtonOAuth2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonOAuth2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOAuth2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 4;
        jPanelOAuth2.add(jButtonOAuth2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelMargin.add(jPanelOAuth2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelMargin, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSendTestMailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendTestMailActionPerformed
        this.sendTestMail();
    }//GEN-LAST:event_jButtonSendTestMailActionPerformed

    private void jButtonOAuth2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOAuth2ActionPerformed
        this.setupOAuth2();
    }//GEN-LAST:event_jButtonOAuth2ActionPerformed

    private void jRadioButtonAuthorizationNoneItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonAuthorizationNoneItemStateChanged
        this.setButtonState();
    }//GEN-LAST:event_jRadioButtonAuthorizationNoneItemStateChanged

    private void jRadioButtonAuthorizationCredentialsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonAuthorizationCredentialsItemStateChanged
        this.setButtonState();
    }//GEN-LAST:event_jRadioButtonAuthorizationCredentialsItemStateChanged

    private void jRadioButtonAuthorizationOAuth2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonAuthorizationOAuth2ItemStateChanged
        this.setButtonState();
    }//GEN-LAST:event_jRadioButtonAuthorizationOAuth2ItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupAuthorization;
    private javax.swing.JButton jButtonOAuth2;
    private javax.swing.JButton jButtonSendTestMail;
    private javax.swing.JCheckBox jCheckBoxNotifyCEM;
    private javax.swing.JCheckBox jCheckBoxNotifyCert;
    private javax.swing.JCheckBox jCheckBoxNotifyConnectionProblem;
    private javax.swing.JCheckBox jCheckBoxNotifyPostprocessing;
    private javax.swing.JCheckBox jCheckBoxNotifyResend;
    private javax.swing.JCheckBox jCheckBoxNotifySystemFailure;
    private javax.swing.JCheckBox jCheckBoxNotifyTransactionError;
    private javax.swing.JComboBox jComboBoxSecurity;
    private javax.swing.JLabel jLabelHost;
    private javax.swing.JLabel jLabelMaxMailsPerMain;
    private javax.swing.JLabel jLabelPass;
    private javax.swing.JLabel jLabelPort;
    private javax.swing.JLabel jLabelReplyTo;
    private javax.swing.JLabel jLabelSMTPAuthorization;
    private javax.swing.JLabel jLabelSecurity;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JPanel jPanelMargin;
    private javax.swing.JPanel jPanelMaxMailsPerMin;
    private javax.swing.JPanel jPanelNotificationSelection;
    private javax.swing.JPanel jPanelOAuth2;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace3434;
    private javax.swing.JPanel jPanelSpacer;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelNotificationMailReceiver;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpMaxMailsPerMin;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpSMTPPort;
    private javax.swing.JPasswordField jPasswordFieldSMTPPass;
    private javax.swing.JRadioButton jRadioButtonAuthorizationCredentials;
    private javax.swing.JRadioButton jRadioButtonAuthorizationNone;
    private javax.swing.JRadioButton jRadioButtonAuthorizationOAuth2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextFieldHost;
    private javax.swing.JTextField jTextFieldMaxMailsPerMin;
    private javax.swing.JTextField jTextFieldNotificationMail;
    private javax.swing.JTextField jTextFieldOAuth2;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextField jTextFieldReplyTo;
    private javax.swing.JTextField jTextFieldSMTPUser;
    // End of variables declaration//GEN-END:variables

    @Override
    public ImageIcon getIcon() {
        return (new ImageIcon(IMAGE_NOTIFICATION));
    }

    @Override
    public String getTabResource() {
        return ("tab.notification");
    }

    private static class SecurityEntry {

        private int value = NotificationData.SECURITY_PLAIN;

        public SecurityEntry(int value) {
            this.value = value;
        }

        public int getDefaultPort() {
            if (this.value == NotificationData.SECURITY_TLS) {
                return (465);
            } else {
                return (25);
            }
        }

        @Override
        public String toString() {
            if (this.getValue() == NotificationData.SECURITY_PLAIN) {
                return ("--");
            } else if (this.getValue() == NotificationData.SECURITY_START_TLS) {
                return ("STARTTLS");
            } else {
                return ("TLS");
            }
        }

        /**
         * @return the value
         */
        public int getValue() {
            return value;
        }

        /**
         * Overwrite the equal method of object
         *
         * @param anObject object ot compare
         */
        @Override
        public boolean equals(Object anObject) {
            if (anObject == this) {
                return (true);
            }
            if (anObject != null && anObject instanceof SecurityEntry) {
                SecurityEntry entry = (SecurityEntry) anObject;
                return (entry.value == this.value);
            }
            return (false);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 29 * hash + this.value;
            return hash;
        }
    }
}
