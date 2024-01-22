package de.mendelson.util.clientserver.connectiontest.gui;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.clientserver.connectiontest.ConnectionTestResult;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.connectiontest.ConnectionTest;
import de.mendelson.util.log.JTextPaneLoggingHandler;
import de.mendelson.util.log.LogFormatter;
import de.mendelson.util.log.LoggingHandlerLogEntryArray;
import de.mendelson.util.security.KeyStoreUtil;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.gui.JDialogInfoOnExternalCertificate;
import de.mendelson.util.security.cert.gui.ResourceBundleCertificates;
import de.mendelson.util.uinotification.UINotification;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
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
 * Dialog to display the test result of a connection test
 *
 * @author S.Heller
 * @version $Revision: 32 $
 */
public class JDialogConnectionTestResult extends JDialog {

    public static final int CONNECTION_TEST_OFTP2 = ConnectionTest.CONNECTION_TEST_OFTP2;
    public static final int CONNECTION_TEST_AS2 = ConnectionTest.CONNECTION_TEST_AS2;
    public static final int CONNECTION_TEST_AS4 = ConnectionTest.CONNECTION_TEST_AS4;

    private ConnectionTestResult result;
    private MecResourceBundle rb = null;
    private CertificateManager certManagerSSL;
    private MecResourceBundle rbCerts;

    private static final MendelsonMultiResolutionImage IMAGE_CONNECTIONTEST
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/clientserver/connectiontest/gui/testconnection.svg", 16);
    private static final MendelsonMultiResolutionImage IMAGE_IMPORT
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/clientserver/connectiontest/gui/import.svg", 24);
    private static final MendelsonMultiResolutionImage IMAGE_LOCALSTATION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/clientserver/connectiontest/gui/localstation.svg", 24);
    private static final MendelsonMultiResolutionImage IMAGE_REMOTE_PARTNER
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/clientserver/connectiontest/gui/singlepartner.svg", 24);
    private static final MendelsonMultiResolutionImage IMAGE_GATEWAY_PARTNER
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/clientserver/connectiontest/gui/singlepartner_gateway.svg", 24);

    private final String TEXT_SECURE_LOCK = "<html>&#128274;</html>";
    private final String TEXT_INSECURE_LOCK = "<html>&#128275;</html>";

    /**
     * Creates new form JDialogTestResult
     */
    public JDialogConnectionTestResult(JFrame parent,
            final int CONNECTION_TYPE_TEST,
            List<LoggingHandlerLogEntryArray.LogEntry> logEntries,
            ConnectionTestResult result,
            CertificateManager certManagerEncSign,
            CertificateManager certManagerSSL) {
        super(parent, true);
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogConnectionTestResult.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        try {
            this.rbCerts = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificates.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        initComponents();
        this.result = result;
        this.setMultiresolutionIcons();
        this.initializePartnerPanel();
        Color errorColor = Color.red.darker();
        Color okColor = Color.green.darker().darker();
        Color labelBackground = this.jLabelConnectionState.getBackground();
        errorColor = ColorUtil.getBestContrastColorAroundForeground(labelBackground, errorColor);
        okColor = ColorUtil.getBestContrastColorAroundForeground(labelBackground, okColor);
        this.jLabelRemoteOFTPService.setVisible(false);
        this.jLabelOFTPServiceState.setVisible(false);
        if (CONNECTION_TYPE_TEST == CONNECTION_TEST_OFTP2) {
            this.jLabelRemoteOFTPService.setVisible(true);
            this.jLabelOFTPServiceState.setVisible(true);
        }
        this.certManagerSSL = certManagerSSL;
        this.setTitle(this.rb.getResourceString("title"));
        if (result.wasSSLTest()) {
            this.jLabelHeader.setText(this.rb.getResourceString("header.ssl", result.getTestedRemoteAddress()));
            this.jLabelPartnerLock.setText(TEXT_SECURE_LOCK);
        } else {
            this.jLabelHeader.setText(this.rb.getResourceString("header.plain", result.getTestedRemoteAddress()));
            this.jLabelPartnerLock.setText(TEXT_INSECURE_LOCK);
        }
        if (result.isConnectionIsPossible()) {
            this.jLabelConnectionState.setForeground(okColor);
            this.jLabelConnectionState.setText(this.rb.getResourceString("OK"));
        } else {
            this.jLabelConnectionState.setForeground(errorColor);
            this.jLabelConnectionState.setText(this.rb.getResourceString("FAILED"));
        }
        if (!result.wasSSLTest()) {
            this.jButtonImportCertificates.setEnabled(false);
            this.jLabelRemoteCertificatesAvailableLocal.setEnabled(false);
            this.jLabelCertificateState.setEnabled(false);
            this.jLabelCertificateState.setText(this.rb.getResourceString("no.certificate.plain"));
        } else {
            if (result.getFoundCertificates() == null || result.getFoundCertificates().length == 0) {
                this.jLabelCertificateState.setForeground(errorColor);
                this.jLabelCertificateState.setText(this.rb.getResourceString("FAILED"));
                this.jButtonImportCertificates.setEnabled(false);
            } else if (result.getFoundCertificates() != null && result.getFoundCertificates().length > 0
                    && this.remoteCertificatesAreAvailable(certManagerSSL, result.getFoundCertificates())) {
                this.jLabelCertificateState.setForeground(okColor);
                this.jLabelCertificateState.setText(this.rb.getResourceString("AVAILABLE"));
                this.jButtonImportCertificates.setEnabled(true);
            } else {
                this.jLabelCertificateState.setForeground(errorColor);
                this.jLabelCertificateState.setText(this.rb.getResourceString("NOT_AVAILABLE"));
                this.jButtonImportCertificates.setEnabled(true);
            }
        }
        if (result.isOftpServiceFound()) {
            this.jLabelOFTPServiceState.setForeground(okColor);
            this.jLabelOFTPServiceState.setText(this.rb.getResourceString("OK"));
        } else {
            this.jLabelOFTPServiceState.setForeground(errorColor);
            this.jLabelOFTPServiceState.setText(this.rb.getResourceString("FAILED"));
        }
        //display the log if there is any
        Logger testLogger = Logger.getAnonymousLogger();
        testLogger.setUseParentHandlers(false);
        JTextPaneLoggingHandler handler = new JTextPaneLoggingHandler(this.jTextPaneLog,
                new LogFormatter(LogFormatter.FORMAT_CONSOLE_COLORED));
        testLogger.setLevel(Level.ALL);
        testLogger.addHandler(handler);
        testLogger.log(Level.FINE,
                this.rb.getResourceString("description." + CONNECTION_TYPE_TEST,
                        new Object[]{
                            result.getTestedRemoteAddress().getHostString(),
                            String.valueOf(result.getTestedRemoteAddress().getPort())
                        }));
        testLogger.log(Level.INFO, "");
        testLogger.log(Level.INFO, "");
        for (LoggingHandlerLogEntryArray.LogEntry logEntry : logEntries) {
            testLogger.log(logEntry.getLevel(), logEntry.getMessage());
        }
        //log some technical information - the ciphers
        if (result.wasSSLTest()) {
            String cipher = result.getUsedCipherSuite();
            //SSL_NULL_WITH_NULL_NULL is the inital cipher - if it is still the selected then a successful handshake did not happen
            if (cipher != null && !cipher.equals("SSL_NULL_WITH_NULL_NULL")) {
                testLogger.log(Level.FINEST, this.rb.getResourceString("used.cipher", cipher));
            }
        }
        //hide dialog on esc
        ActionListener actionListenerESC = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jButtonClose.doClick();
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane().registerKeyboardAction(actionListenerESC, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void setMultiresolutionIcons() {
        this.setIconImages(IMAGE_CONNECTIONTEST.getResolutionVariants());
        this.jButtonImportCertificates.setIcon(new ImageIcon(IMAGE_IMPORT.toMinResolution(24)));
        //Its possible to not display the partner panel
        if (this.result.getSenderName() != null) {
            this.jLabelPartnerSenderImage.setIcon(new ImageIcon(IMAGE_LOCALSTATION.toMinResolution(28)));
            if (this.result.getPartnerRole() == ConnectionTest.PARTNER_ROLE_GATEWAY_PARTNER) {
                this.jLabelPartnerReceiverImage.setIcon(new ImageIcon(IMAGE_GATEWAY_PARTNER.toMinResolution(28)));
            } else {
                this.jLabelPartnerReceiverImage.setIcon(new ImageIcon(IMAGE_REMOTE_PARTNER.toMinResolution(28)));
            }
        }
    }

    private void initializePartnerPanel() {
        if (this.result.getSenderName() == null) {
            this.jPanelPartnerDisplay.setVisible(false);
        } else {
            String senderName 
                    = this.calculateMaxPartnerName(this.result.getSenderName(), 
                            this.jLabelPartnerSenderName.getFontMetrics(this.jLabelPartnerSenderName.getFont()), 
                            (int)this.jPanelPartnerSender.getPreferredSize().getWidth());
            String receiverName 
                    = this.calculateMaxPartnerName(this.result.getReceiverName(), 
                            this.jLabelPartnerReceiverName.getFontMetrics(this.jLabelPartnerReceiverName.getFont()), 
                            (int)this.jPanelPartnerReceiver.getPreferredSize().getWidth());            
            this.jLabelPartnerSenderName.setText(senderName);
            this.jLabelPartnerReceiverName.setText(receiverName);
        }
    }

    /**Calculates the String that should be displayed as partner name in the dialog. If the width is too long
     * it is shortened
     * @param partnerName
     * @param fontMetrics
     * @param panelWidth
     * @return 
     */
    private String calculateMaxPartnerName(String partnerName, FontMetrics fontMetrics, int panelWidth) {  
        int textWidth = fontMetrics.stringWidth(partnerName);
        if (textWidth > panelWidth * 0.75) {
            String newName = "";
            for (int i = 0; i < partnerName.length(); i++) {                
                textWidth = fontMetrics.stringWidth(newName+"[..]");
                if (textWidth > panelWidth * 0.75) {
                    break;
                }
                newName += partnerName.charAt(i);
            }
            return( newName+"[..]");
        }else{
            return( partnerName );
        }
    }

    /**
     * Checks if the downloaded certificates are available in the local TLS
     * keystore
     *
     */
    private boolean remoteCertificatesAreAvailable(CertificateManager certManagerSSL,
            X509Certificate[] remoteCertificates) {
        boolean exists = true;
        for (X509Certificate remoteCertificate : remoteCertificates) {
            KeystoreCertificate testCert = new KeystoreCertificate();
            testCert.setCertificate(remoteCertificate);
            String fingerPrint = testCert.getFingerPrintSHA1();
            if (certManagerSSL.getKeystoreCertificateByFingerprintSHA1(fingerPrint) == null) {
                exists = false;
                break;
            }
        }
        return (exists);
    }

    private void viewCertificates() {
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.addAll(Arrays.asList(this.result.getFoundCertificates()));
        JDialogInfoOnExternalCertificate infoDialog = new JDialogInfoOnExternalCertificate(parent, certList, this.certManagerSSL);
        infoDialog.setVisible(true);
        //user pressed cancel: bail out
        while (infoDialog.importPressed()) {
            int selectedCertificateIndex = infoDialog.getCertificateIndex();
            KeyStoreUtil util = new KeyStoreUtil();
            X509Certificate importCertificate = certList.get(selectedCertificateIndex);
            try {
                String alias = util.importX509Certificate(this.certManagerSSL.getKeystore(), importCertificate, 
                        BouncyCastleProvider.PROVIDER_NAME);
                this.certManagerSSL.saveKeystore();
                this.certManagerSSL.rereadKeystoreCertificates();
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_SUCCESS,
                        this.rbCerts.getResourceString("certificate.import.success.title"),
                        this.rbCerts.getResourceString("certificate.import.success.message", alias));
            } catch (Throwable e) {
                e.printStackTrace();
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_ERROR,
                        this.rbCerts.getResourceString("certificate.import.error.title"),
                        this.rbCerts.getResourceString("certificate.import.error.message", e.getMessage()));
            }
            if (certList.size() > 1) {
                infoDialog.setVisible(true);
            } else {
                break;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane = new javax.swing.JSplitPane();
        jPanelOverview = new javax.swing.JPanel();
        jLabelHeader = new javax.swing.JLabel();
        jLabelIPConnection = new javax.swing.JLabel();
        jLabelRemoteCertificatesAvailableLocal = new javax.swing.JLabel();
        jLabelRemoteOFTPService = new javax.swing.JLabel();
        jLabelConnectionState = new javax.swing.JLabel();
        jLabelCertificateState = new javax.swing.JLabel();
        jLabelOFTPServiceState = new javax.swing.JLabel();
        jPanelSpace = new javax.swing.JPanel();
        jButtonImportCertificates = new javax.swing.JButton();
        jPanelPartnerDisplay = new javax.swing.JPanel();
        jLabelPartnerArrow = new javax.swing.JLabel();
        jLabelPartnerLock = new javax.swing.JLabel();
        jPanelPartnerSender = new javax.swing.JPanel();
        jLabelPartnerSenderImage = new javax.swing.JLabel();
        jLabelPartnerSenderName = new javax.swing.JLabel();
        jPanelPartnerReceiver = new javax.swing.JPanel();
        jLabelPartnerReceiverImage = new javax.swing.JLabel();
        jLabelPartnerReceiverName = new javax.swing.JLabel();
        jPanelLog = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        jTextPaneLog = new javax.swing.JTextPane();
        jPanelButton = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jSplitPane.setBorder(null);
        jSplitPane.setDividerLocation(270);
        jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanelOverview.setLayout(new java.awt.GridBagLayout());

        jLabelHeader.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabelHeader.setText("<Header with URL>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 10, 5);
        jPanelOverview.add(jLabelHeader, gridBagConstraints);

        jLabelIPConnection.setText(this.rb.getResourceString("label.connection.established"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 20, 5);
        jPanelOverview.add(jLabelIPConnection, gridBagConstraints);

        jLabelRemoteCertificatesAvailableLocal.setText(this.rb.getResourceString("label.certificates.available.local"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 20, 5);
        jPanelOverview.add(jLabelRemoteCertificatesAvailableLocal, gridBagConstraints);

        jLabelRemoteOFTPService.setText(this.rb.getResourceString("label.running.oftpservice"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 20, 5);
        jPanelOverview.add(jLabelRemoteOFTPService, gridBagConstraints);

        jLabelConnectionState.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelConnectionState.setText("<STATE>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 5);
        jPanelOverview.add(jLabelConnectionState, gridBagConstraints);

        jLabelCertificateState.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelCertificateState.setText("<STATE>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 5);
        jPanelOverview.add(jLabelCertificateState, gridBagConstraints);

        jLabelOFTPServiceState.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelOFTPServiceState.setText("<STATE>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 5);
        jPanelOverview.add(jLabelOFTPServiceState, gridBagConstraints);

        jPanelSpace.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanelSpace.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelOverview.add(jPanelSpace, gridBagConstraints);

        jButtonImportCertificates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/clientserver/connectiontest/gui/missing_image24x24.gif"))); // NOI18N
        jButtonImportCertificates.setText(this.rb.getResourceString( "button.viewcert"));
        jButtonImportCertificates.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonImportCertificates.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jButtonImportCertificates.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonImportCertificates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportCertificatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        jPanelOverview.add(jButtonImportCertificates, gridBagConstraints);

        jPanelPartnerDisplay.setLayout(new java.awt.GridBagLayout());

        jLabelPartnerArrow.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelPartnerArrow.setText("<html>&#10230;</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(-5, 10, 15, 10);
        jPanelPartnerDisplay.add(jLabelPartnerArrow, gridBagConstraints);

        jLabelPartnerLock.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        jLabelPartnerLock.setText("<html>&#128275;</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, -5, 0);
        jPanelPartnerDisplay.add(jLabelPartnerLock, gridBagConstraints);

        jPanelPartnerSender.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelPartnerSender.setMinimumSize(new java.awt.Dimension(120, 70));
        jPanelPartnerSender.setPreferredSize(new java.awt.Dimension(120, 70));
        jPanelPartnerSender.setLayout(new java.awt.GridBagLayout());

        jLabelPartnerSenderImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/clientserver/connectiontest/gui/missing_image24x24.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        jPanelPartnerSender.add(jLabelPartnerSenderImage, gridBagConstraints);

        jLabelPartnerSenderName.setText("<sender>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanelPartnerSender.add(jLabelPartnerSenderName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        jPanelPartnerDisplay.add(jPanelPartnerSender, gridBagConstraints);

        jPanelPartnerReceiver.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelPartnerReceiver.setMinimumSize(new java.awt.Dimension(120, 70));
        jPanelPartnerReceiver.setPreferredSize(new java.awt.Dimension(120, 70));
        jPanelPartnerReceiver.setLayout(new java.awt.GridBagLayout());

        jLabelPartnerReceiverImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/clientserver/connectiontest/gui/missing_image24x24.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        jPanelPartnerReceiver.add(jLabelPartnerReceiverImage, gridBagConstraints);

        jLabelPartnerReceiverName.setText("<receiver>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanelPartnerReceiver.add(jLabelPartnerReceiverName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        jPanelPartnerDisplay.add(jPanelPartnerReceiver, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        jPanelOverview.add(jPanelPartnerDisplay, gridBagConstraints);

        jSplitPane.setLeftComponent(jPanelOverview);

        jPanelLog.setLayout(new java.awt.GridBagLayout());

        jTextPaneLog.setEditable(false);
        jScrollPane.setViewportView(jTextPaneLog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelLog.add(jScrollPane, gridBagConstraints);

        jSplitPane.setRightComponent(jPanelLog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jSplitPane, gridBagConstraints);

        jPanelButton.setLayout(new java.awt.GridBagLayout());

        jButtonClose.setText(this.rb.getResourceString( "button.close"));
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelButton.add(jButtonClose, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelButton, gridBagConstraints);

        setSize(new java.awt.Dimension(964, 712));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonImportCertificatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImportCertificatesActionPerformed
        this.viewCertificates();
    }//GEN-LAST:event_jButtonImportCertificatesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonImportCertificates;
    private javax.swing.JLabel jLabelCertificateState;
    private javax.swing.JLabel jLabelConnectionState;
    private javax.swing.JLabel jLabelHeader;
    private javax.swing.JLabel jLabelIPConnection;
    private javax.swing.JLabel jLabelOFTPServiceState;
    private javax.swing.JLabel jLabelPartnerArrow;
    private javax.swing.JLabel jLabelPartnerLock;
    private javax.swing.JLabel jLabelPartnerReceiverImage;
    private javax.swing.JLabel jLabelPartnerReceiverName;
    private javax.swing.JLabel jLabelPartnerSenderImage;
    private javax.swing.JLabel jLabelPartnerSenderName;
    private javax.swing.JLabel jLabelRemoteCertificatesAvailableLocal;
    private javax.swing.JLabel jLabelRemoteOFTPService;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelLog;
    private javax.swing.JPanel jPanelOverview;
    private javax.swing.JPanel jPanelPartnerDisplay;
    private javax.swing.JPanel jPanelPartnerReceiver;
    private javax.swing.JPanel jPanelPartnerSender;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JTextPane jTextPaneLog;
    // End of variables declaration//GEN-END:variables
}
