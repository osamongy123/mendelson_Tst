//$Header: /as2/de/mendelson/comm/as2/message/loggui/DialogMessageDetails.java 64    1/09/22 14:11 Heller $
package de.mendelson.comm.as2.message.loggui;

import de.mendelson.comm.as2.AS2Exception;
import de.mendelson.comm.as2.log.LogEntry;
import de.mendelson.comm.as2.message.AS2Info;
import de.mendelson.comm.as2.message.AS2MDNInfo;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.clientserver.MessageDetailRequest;
import de.mendelson.comm.as2.message.clientserver.MessageDetailResponse;
import de.mendelson.comm.as2.message.clientserver.MessageLogRequest;
import de.mendelson.comm.as2.message.clientserver.MessageLogResponse;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.util.ColorUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.log.JTextPaneLoggingHandler;
import de.mendelson.util.log.LogFormatter;
import de.mendelson.util.tables.JTableColumnResizer;
import de.mendelson.util.uinotification.UINotification;
import java.awt.Color;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dialog to show the details of a transaction
 *
 * @author S.Heller
 * @version $Revision: 64 $
 */
public class DialogMessageDetails extends JDialog implements ListSelectionListener {

    public static final ImageIcon ICON_LOCALSTATION
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/localstation.svg", 24, 48));
    public static final ImageIcon ICON_REMOTEPARTNER
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/singlepartner.svg", 24, 48));
    public static final ImageIcon ICON_PENDING
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/state_pending.svg", 15, 48));
    public static final ImageIcon ICON_STOPPED
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/state_stopped.svg", 15, 48));
    public static final ImageIcon ICON_FINISHED
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/state_finished.svg", 15, 48));
    public static final ImageIcon OVERVIEWSTATE_OUTBOUND_OK
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/comm_ok_outbound.svg", 170, 230));
    public static final ImageIcon OVERVIEWSTATE_OUTBOUND_FAILED
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/comm_failed_outbound.svg", 170, 230));
    public static final ImageIcon OVERVIEWSTATE_INBOUND_FAILED
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/comm_failed_inbound.svg", 170, 230));
    public static final ImageIcon OVERVIEWSTATE_INBOUND_OK
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/comm_ok_inbound.svg", 170, 230));
    public static final ImageIcon OVERVIEWSTATE_OUTBOUND_CONN_FAILED
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/comm_failed_outbound_conn.svg", 170, 230));
    public static final ImageIcon OVERVIEWSTATE_INBOUND_ANSWER_FAILED
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/comm_failed_inbound_answer.svg", 170, 230));
    public static final ImageIcon OVERVIEWSTATE_PENDING
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/comm_pending.svg", 170, 230));

    private final String TEXT_SECURE_LOCK = "<html>&#128274;</html>";
    private final String TEXT_INSECURE_LOCK = "<html>&#128275;</html>";

    private final static Logger logger = Logger.getLogger("de.mendelson.as2.client");
    /**
     * Localize the GUI
     */
    private final MecResourceBundle rb;
    /**
     * Stores information about the message
     */
    private final AS2MessageInfo overviewInfo;
    /**
     * Stores the payloads
     */
    private final List<AS2Payload> payloadList;
    private final JPanelFileDisplay jPanelFileDisplayRaw;
    private final JPanelFileDisplay jPanelFileDisplayHeader;
    private final JPanelFileDisplay[] jPanelFileDisplayPayload;
    private final BaseClient baseClient;
    private Color colorRed = Color.RED.darker();
    private Color colorYellow = Color.YELLOW.darker().darker();
    private Color colorGreen = Color.GREEN.darker().darker();

    /**
     * Creates new form AboutDialog
     */
    public DialogMessageDetails(JFrame parent, BaseClient baseClient, AS2MessageInfo overviewInfo,
            List<AS2Payload> payloadList, JTextPaneLoggingHandler handler) {
        super(parent, true);
        this.baseClient = baseClient;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMessageDetails.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.jPanelFileDisplayRaw = new JPanelFileDisplay(baseClient);
        this.jPanelFileDisplayHeader = new JPanelFileDisplay(baseClient);
        this.payloadList = payloadList;
        this.overviewInfo = overviewInfo;
        if (overviewInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
            this.setTitle(this.rb.getResourceString("title.cem"));
        } else {
            this.setTitle(this.rb.getResourceString("title"));
        }
        this.initComponents();
        this.colorRed = ColorUtil.getBestContrastColorAroundForeground(
                this.jLabelTransactionStateDetails.getBackground(), colorRed);
        this.colorGreen = ColorUtil.getBestContrastColorAroundForeground(
                this.jLabelTransactionStateDetails.getBackground(), colorGreen);
        this.colorYellow = ColorUtil.getBestContrastColorAroundForeground(
                this.jLabelTransactionStateDetails.getBackground(), colorYellow);
        this.populateTransactionOverviewPanel(overviewInfo);
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.jTableMessageDetails.setRowHeight(TableModelMessageDetails.ROW_HEIGHT);
        this.jTableMessageDetails.getTableHeader().setReorderingAllowed(false);
        //first icon
        TableColumn column = this.jTableMessageDetails.getColumnModel().getColumn(0);
        column.setMaxWidth(TableModelMessageDetails.ROW_HEIGHT + this.jTableMessageDetails.getRowMargin() * 2);
        column.setResizable(false);
        column = this.jTableMessageDetails.getColumnModel().getColumn(2);
        column.setMaxWidth(TableModelMessageDetails.ROW_HEIGHT + this.jTableMessageDetails.getRowMargin() * 2);
        column.setResizable(false);
        this.displayData(overviewInfo);
        this.jTabbedPane.addTab(this.rb.getResourceString("message.raw.decrypted"), jPanelFileDisplayRaw);
        this.jTabbedPane.addTab(this.rb.getResourceString("message.header"), jPanelFileDisplayHeader);
        this.jPanelFileDisplayPayload = new JPanelFileDisplay[payloadList.size()];
        for (int i = 0; i < this.payloadList.size(); i++) {
            this.jPanelFileDisplayPayload[i] = new JPanelFileDisplay(baseClient);
            if (payloadList.size() == 1) {
                this.jTabbedPane.addTab(this.rb.getResourceString("message.payload"), this.jPanelFileDisplayPayload[0]);
            } else {
                this.jTabbedPane.addTab(this.rb.getResourceString("message.payload.multiple",
                        String.valueOf(i + 1)), this.jPanelFileDisplayPayload[i]);
            }
        }
        this.jTableMessageDetails.getSelectionModel().addListSelectionListener(this);
        this.displayProcessLog(handler);
        JTableColumnResizer.adjustColumnWidthByContent(this.jTableMessageDetails);
        this.jTableMessageDetails.getSelectionModel().setSelectionInterval(0, 0);
    }

    /**
     * Displays overview information about the transaction
     */
    private void populateTransactionOverviewPanel(AS2MessageInfo overviewInfo) {
        String messageTypeStr = "AS2";
        if (overviewInfo.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
            messageTypeStr = "CEM";
        }
        //get all partner from server - just to display the icons. No full partner
        //information is required
        PartnerListRequest partnerRequest
                = new PartnerListRequest(
                        PartnerListRequest.LIST_BY_AS2_ID, PartnerListRequest.DATA_COMPLETENESS_NAME_AS2ID_TYPE);
        partnerRequest.setAdditionalListOptionStr(overviewInfo.getSenderId());
        PartnerListResponse partnerResponse = (PartnerListResponse) this.baseClient.sendSync(partnerRequest);
        List<Partner> partnerList = partnerResponse.getList();
        Partner sender = null;
        if (!partnerList.isEmpty()) {
            sender = partnerList.get(0);
        }
        partnerRequest
                = new PartnerListRequest(PartnerListRequest.LIST_BY_AS2_ID, PartnerListRequest.DATA_COMPLETENESS_NAME_AS2ID_TYPE);
        partnerRequest.setAdditionalListOptionStr(overviewInfo.getReceiverId());
        partnerResponse = (PartnerListResponse) this.baseClient.sendSync(partnerRequest);
        partnerList = partnerResponse.getList();
        Partner receiver = null;
        if (!partnerList.isEmpty()) {
            receiver = partnerList.get(0);
        }
        this.jLabelTransactionStateDetails.setVisible(false);
        this.jLabelAS2TransmissionSender.setIcon(this.getIcon(sender));
        this.jLabelAS2TransmissionReceiver.setIcon(this.getIcon(receiver));
        if (sender == null) {
            this.jLabelAS2TransmissionSender.setText(overviewInfo.getSenderId());
        } else {
            this.jLabelAS2TransmissionSender.setText(sender.getName());
        }
        if (receiver == null) {
            this.jLabelAS2TransmissionReceiver.setText(overviewInfo.getReceiverId());
        } else {
            this.jLabelAS2TransmissionReceiver.setText(receiver.getName());
        }
        if (overviewInfo.usesTLS()) {
            this.jLabelTLSIcon.setText(TEXT_SECURE_LOCK);
        } else {
            this.jLabelTLSIcon.setText(TEXT_INSECURE_LOCK);
        }
        //display some general transaction details
        StringBuilder transactionDetailsText = new StringBuilder();
        if (overviewInfo.getDirection() == AS2MessageInfo.DIRECTION_OUT) {
            if (overviewInfo.usesTLS()) {
                transactionDetailsText.append(this.rb.getResourceString("transactiondetails.outbound.secure",
                        this.jLabelAS2TransmissionReceiver.getText()));
            } else {
                transactionDetailsText.append(this.rb.getResourceString("transactiondetails.outbound.insecure",
                        this.jLabelAS2TransmissionReceiver.getText()));
            }
            if (overviewInfo.requestsSyncMDN()) {
                transactionDetailsText.append(this.rb.getResourceString("transactiondetails.outbound.sync"));
            } else {
                transactionDetailsText.append(this.rb.getResourceString("transactiondetails.outbound.async"));
            }
        } else {
            if (overviewInfo.usesTLS()) {
                transactionDetailsText.append(this.rb.getResourceString("transactiondetails.inbound.secure",
                        this.jLabelAS2TransmissionReceiver.getText()));
            } else {
                transactionDetailsText.append(this.rb.getResourceString("transactiondetails.inbound.insecure",
                        this.jLabelAS2TransmissionReceiver.getText()));
            }
            if (overviewInfo.requestsSyncMDN()) {
                transactionDetailsText.append(this.rb.getResourceString("transactiondetails.inbound.sync"));
            } else {
                transactionDetailsText.append(this.rb.getResourceString("transactiondetails.inbound.async"));
            }
        }
        this.jLabelTransmissionDescription.setText("<HTML>" + transactionDetailsText.toString() + "</HTML>");

        this.jLabelTransactionStateDetails.setVisible(false);
        List<AS2Info> transactionDetails = null;
        try {
            transactionDetails = ((MessageDetailResponse) this.baseClient.sendSync(new MessageDetailRequest(overviewInfo.getMessageId()))).getList();
        } catch (Exception e) {
        }
        if (overviewInfo.getState() == AS2Message.STATE_STOPPED) {
            this.jLabelTransactionStateGeneral.setForeground(this.colorRed);
            this.jLabelTransactionStateDetails.setForeground(this.colorRed);
            if (transactionDetails == null) {
                this.jLabelTransactionStateDetails.setVisible(false);
            } else {
                if (transactionDetails.size() < 2) {
                    this.jLabelStateOverviewImage.setIcon(OVERVIEWSTATE_OUTBOUND_CONN_FAILED);
                    this.jLabelTransactionStateGeneral.setText(this.rb.getResourceString("transactionstate.error.connectionrefused"));
                    this.jLabelTransactionStateDetails.setVisible(true);
                    this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.error.connectionrefused.details"));
                } else {
                    //get last MDN info
                    AS2MDNInfo mdnInfo = null;
                    for (int i = transactionDetails.size() - 1; i > 0; i--) {
                        AS2Info info = transactionDetails.get(i);
                        if (info.isMDN()) {
                            mdnInfo = (AS2MDNInfo) info;
                            break;
                        }
                    }
                    if (mdnInfo != null) {
                        String dispositionState = mdnInfo.getDispositionState();
                        if (dispositionState == null) {
                            dispositionState = "Unknown";
                        }
                        if (overviewInfo.getDirection() == AS2MessageInfo.DIRECTION_OUT) {
                            this.jLabelStateOverviewImage.setIcon(OVERVIEWSTATE_OUTBOUND_FAILED);
                            this.jLabelTransactionStateGeneral.setText(this.rb.getResourceString("transactionstate.error.out",
                                    new Object[]{
                                        messageTypeStr,
                                        this.jLabelAS2TransmissionReceiver.getText(),
                                        dispositionState}));
                        } else {
                            this.jLabelStateOverviewImage.setIcon(OVERVIEWSTATE_INBOUND_FAILED);
                            this.jLabelTransactionStateGeneral.setText(this.rb.getResourceString("transactionstate.error.in",
                                    new Object[]{
                                        messageTypeStr,
                                        this.jLabelAS2TransmissionReceiver.getText(),
                                        dispositionState
                                    }));
                            //special: If the transaction direction was inbound and the transaction state is stopped anyway but the MDN state
                            //is processed and the MDN was async then there is a connection problem sending the async MDN or the async MDN has been
                            //rejected with a HTTP 400 by the partner
                            if (mdnInfo.getState() == AS2Message.STATE_FINISHED && !overviewInfo.requestsSyncMDN()) {
                                this.jLabelStateOverviewImage.setIcon(OVERVIEWSTATE_INBOUND_ANSWER_FAILED);
                                this.jLabelTransactionStateGeneral.setText(this.rb.getResourceString("transactionstate.error.asyncmdnsend"));
                                this.jLabelTransactionStateDetails.setVisible(true);
                                this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.error.asyncmdnsend.details"));
                            }
                        }
                        //get some more details
                        if (dispositionState.contains(AS2Exception.UNKNOWN_TRADING_PARTNER_ERROR)) {
                            this.jLabelTransactionStateDetails.setVisible(true);
                            this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.error.unknown-trading-partner",
                                    new Object[]{
                                        overviewInfo.getSenderId(),
                                        overviewInfo.getReceiverId(),}));
                        } else if (dispositionState.contains(AS2Exception.AUTHENTIFICATION_ERROR)) {
                            this.jLabelTransactionStateDetails.setVisible(true);
                            this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.error.authentication-failed"));
                        } else if (dispositionState.contains(AS2Exception.DECOMPRESSSION_ERROR)) {
                            this.jLabelTransactionStateDetails.setVisible(true);
                            this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.error.decompression-failed"));
                        } else if (dispositionState.contains(AS2Exception.INSUFFICIENT_SECURITY_ERROR)) {
                            this.jLabelTransactionStateDetails.setVisible(true);
                            this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.error.insufficient-message-security"));
                        } else if (dispositionState.contains(AS2Exception.PROCESSING_ERROR)) {
                            this.jLabelTransactionStateDetails.setVisible(true);
                            this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.error.unexpected-processing-error"));
                        } else if (dispositionState.contains(AS2Exception.DECRYPTION_ERROR)) {
                            this.jLabelTransactionStateDetails.setVisible(true);
                            this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.error.decryption-failed"));
                        }
                    } else {
                        this.jLabelStateOverviewImage.setIcon(null);
                        this.jLabelTransactionStateGeneral.setText(this.rb.getResourceString("transactionstate.error.unknown"));
                        this.jLabelTransactionStateDetails.setVisible(false);
                    }

                }
            }
        } else if (overviewInfo.getState() == AS2Message.STATE_FINISHED) {
            this.jLabelTransactionStateGeneral.setForeground(this.colorGreen);
            this.jLabelTransactionStateDetails.setForeground(this.colorGreen);
            if (overviewInfo.getDirection() == AS2MessageInfo.DIRECTION_OUT) {
                this.jLabelStateOverviewImage.setIcon(OVERVIEWSTATE_OUTBOUND_OK);
                this.jLabelTransactionStateGeneral.setText(this.rb.getResourceString("transactionstate.ok.send",
                        new Object[]{
                            messageTypeStr,
                            this.jLabelAS2TransmissionReceiver.getText()
                        }
                ));
                this.jLabelTransactionStateDetails.setVisible(true);
                this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.ok.details"));
            } else {
                this.jLabelStateOverviewImage.setIcon(OVERVIEWSTATE_INBOUND_OK);
                this.jLabelTransactionStateGeneral.setText(this.rb.getResourceString("transactionstate.ok.receive",
                        new Object[]{
                            messageTypeStr,
                            this.jLabelAS2TransmissionReceiver.getText(),}));
                this.jLabelTransactionStateDetails.setVisible(true);
                this.jLabelTransactionStateDetails.setText(this.rb.getResourceString("transactionstate.ok.details"));
            }
        } else if (overviewInfo.getState() == AS2Message.STATE_PENDING) {
            this.jLabelStateOverviewImage.setIcon(OVERVIEWSTATE_PENDING);
            this.jLabelTransactionStateGeneral.setForeground(this.colorYellow);
            this.jLabelTransactionStateGeneral.setText(this.rb.getResourceString("transactionstate.pending"));
            this.jLabelTransactionStateDetails.setVisible(false);
        }
    }

    /**
     * Returns the partner icon to display for sender and receiver
     */
    private ImageIcon getIcon(Partner partner) {
        if (partner != null && partner.isLocalStation()) {
            return (ICON_LOCALSTATION);
        }
        return (this.ICON_REMOTEPARTNER);
    }

    /**
     * Displays the message details log
     *
     * @param overviewHandler Logging handler for the main logging panel - this
     * is just needed for the colors
     */
    private void displayProcessLog(JTextPaneLoggingHandler overviewHandler) {
        Logger detailsLogger = Logger.getAnonymousLogger();
        detailsLogger.setUseParentHandlers(false);
        detailsLogger.setLevel(Level.ALL);
        JTextPaneLoggingHandler detailsHandler = new JTextPaneLoggingHandler(this.jTextPaneLog,
                new LogFormatter(LogFormatter.FORMAT_CONSOLE_COLORED));
        detailsHandler.setLevel(Level.ALL);
        detailsLogger.addHandler(detailsHandler);
        detailsHandler.setColorsFrom(overviewHandler);
        MessageLogResponse logResponse = (MessageLogResponse) this.baseClient.sendSync(new MessageLogRequest(overviewInfo.getMessageId()));
        if (logResponse != null) {
            if (logResponse.getException() != null) {
                UINotification.instance().addNotification(logResponse.getException());
            } else {
                List<LogEntry> entries = logResponse.getList();
                for (LogEntry logEntry : entries) {
                    LogRecord logRecord = new LogRecord(logEntry.getLevel(), logEntry.getMessage());
                    logRecord.setInstant(new Date(logEntry.getMillis()).toInstant());
                    detailsLogger.log(logRecord);
                }
            }
        }
    }

    /**
     * Displays all messages that contain to the passed overview object
     */
    private void displayData(AS2MessageInfo overviewRow) {
        try {
            List<AS2Info> details = ((MessageDetailResponse) this.baseClient.sendSync(new MessageDetailRequest(overviewRow.getMessageId()))).getList();
            ((TableModelMessageDetails) this.jTableMessageDetails.getModel()).passNewData(details);
        } catch (Exception e) {
            UINotification.instance().addNotification(e);
        }
    }

    /**
     * ListSelectionListener
     */
    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        int selectedRow = this.jTableMessageDetails.getSelectedRow();
        if (selectedRow >= 0) {
            AS2Info info = ((TableModelMessageDetails) this.jTableMessageDetails.getModel()).getRow(selectedRow);
            String rawFileName = null;
            if (!info.isMDN()) {
                AS2MessageInfo messageInfo = (AS2MessageInfo) info;
                if (messageInfo.getRawFilenameDecrypted() != null) {
                    rawFileName = messageInfo.getRawFilenameDecrypted();
                } else if (messageInfo.getRawFilename() != null) {
                    rawFileName = messageInfo.getRawFilename();
                }
            } else {
                if (info.getRawFilename() != null) {
                    rawFileName = info.getRawFilename();
                }
            }
            this.jPanelFileDisplayRaw.displayFile(rawFileName, false);
            String headerFilename = null;
            if (info.getHeaderFilename() != null) {
                headerFilename = info.getHeaderFilename();
            }
            this.jPanelFileDisplayHeader.displayFile(headerFilename, false);
            try {
                if (this.payloadList.size() > 0) {
                    for (int i = 0; i < payloadList.size(); i++) {
                        String payloadFilename = this.payloadList.get(i).getPayloadFilename();
                        this.jPanelFileDisplayPayload[i].displayFile(payloadFilename, true);
                    }
                }
            } catch (Exception e) {
                //nop
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

        jPanelMain = new javax.swing.JPanel();
        jPanelHeader = new javax.swing.JPanel();
        jLabelAS2TransmissionSender = new javax.swing.JLabel();
        jLabelAS2TransmissionReceiver = new javax.swing.JLabel();
        jLabelTransactionStateGeneral = new javax.swing.JLabel();
        jLabelTransactionStateDetails = new javax.swing.JLabel();
        jPanelSpace = new javax.swing.JPanel();
        jLabelTransmissionDescription = new javax.swing.JLabel();
        jPanelSep = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanelSep2 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelOverviewImage = new javax.swing.JPanel();
        jLabelStateOverviewImage = new javax.swing.JLabel();
        jPanelVerticalSpace1 = new javax.swing.JPanel();
        jPanelArrow = new javax.swing.JPanel();
        jLabelArrow = new javax.swing.JLabel();
        jLabelTLSIcon = new javax.swing.JLabel();
        jPanelVerticalSpace2 = new javax.swing.JPanel();
        jPanelSpaceHorizontal1 = new javax.swing.JPanel();
        jPanelSpaceHorizontal2 = new javax.swing.JPanel();
        jPanelInfo = new javax.swing.JPanel();
        jSplitPane = new javax.swing.JSplitPane();
        jScrollPaneList = new javax.swing.JScrollPane();
        jTableMessageDetails = new javax.swing.JTable();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelProcessLog = new javax.swing.JPanel();
        jScrollPaneLog = new javax.swing.JScrollPane();
        jTextPaneLog = new javax.swing.JTextPane();
        jPanelButton = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jPanelHeader.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelHeader.setLayout(new java.awt.GridBagLayout());

        jLabelAS2TransmissionSender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/message/loggui/missing_image24x24.gif"))); // NOI18N
        jLabelAS2TransmissionSender.setText("Sender");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHeader.add(jLabelAS2TransmissionSender, gridBagConstraints);

        jLabelAS2TransmissionReceiver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/message/loggui/missing_image24x24.gif"))); // NOI18N
        jLabelAS2TransmissionReceiver.setText("Receiver");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHeader.add(jLabelAS2TransmissionReceiver, gridBagConstraints);

        jLabelTransactionStateGeneral.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelTransactionStateGeneral.setText("<General transaction state>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelHeader.add(jLabelTransactionStateGeneral, gridBagConstraints);

        jLabelTransactionStateDetails.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelTransactionStateDetails.setForeground(new java.awt.Color(0, 153, 0));
        jLabelTransactionStateDetails.setText("<Transaction state details>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanelHeader.add(jLabelTransactionStateDetails, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelHeader.add(jPanelSpace, gridBagConstraints);

        jLabelTransmissionDescription.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelTransmissionDescription.setText("<Transmission description>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 5);
        jPanelHeader.add(jLabelTransmissionDescription, gridBagConstraints);

        jPanelSep.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelSep.add(jSeparator1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelHeader.add(jPanelSep, gridBagConstraints);

        jPanelSep2.setLayout(new java.awt.GridBagLayout());

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelSep2.add(jSeparator2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelHeader.add(jPanelSep2, gridBagConstraints);

        jPanelOverviewImage.setLayout(new java.awt.GridBagLayout());

        jLabelStateOverviewImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/message/loggui/comm_ok_outbound.png"))); // NOI18N
        jLabelStateOverviewImage.setMaximumSize(new java.awt.Dimension(170, 90));
        jLabelStateOverviewImage.setMinimumSize(new java.awt.Dimension(170, 90));
        jLabelStateOverviewImage.setPreferredSize(new java.awt.Dimension(170, 90));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelOverviewImage.add(jLabelStateOverviewImage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelHeader.add(jPanelOverviewImage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanelHeader.add(jPanelVerticalSpace1, gridBagConstraints);

        jPanelArrow.setLayout(new java.awt.GridBagLayout());

        jLabelArrow.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelArrow.setText("<HTML>&#x27F6;</HTML>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(-5, 0, 0, 0);
        jPanelArrow.add(jLabelArrow, gridBagConstraints);

        jLabelTLSIcon.setText("<html>&#128274;</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, -5, 0);
        jPanelArrow.add(jLabelTLSIcon, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanelHeader.add(jPanelArrow, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanelHeader.add(jPanelVerticalSpace2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 5);
        jPanelHeader.add(jPanelSpaceHorizontal1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 5);
        jPanelHeader.add(jPanelSpaceHorizontal2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMain.add(jPanelHeader, gridBagConstraints);

        jPanelInfo.setLayout(new java.awt.GridBagLayout());

        jSplitPane.setDividerLocation(120);
        jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTableMessageDetails.setModel(new TableModelMessageDetails());
        jTableMessageDetails.setShowHorizontalLines(false);
        jTableMessageDetails.setShowVerticalLines(false);
        jScrollPaneList.setViewportView(jTableMessageDetails);

        jSplitPane.setLeftComponent(jScrollPaneList);

        jPanelProcessLog.setLayout(new java.awt.GridBagLayout());

        jTextPaneLog.setEditable(false);
        jScrollPaneLog.setViewportView(jTextPaneLog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelProcessLog.add(jScrollPaneLog, gridBagConstraints);

        jTabbedPane.addTab(this.rb.getResourceString( "tab.log"), jPanelProcessLog);

        jSplitPane.setRightComponent(jTabbedPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelInfo.add(jSplitPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMain.add(jPanelInfo, gridBagConstraints);

        jPanelButton.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonOk.setText(this.rb.getResourceString( "button.ok" ));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        jPanelButton.add(jButtonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanelMain.add(jPanelButton, gridBagConstraints);

        getContentPane().add(jPanelMain, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(1155, 784));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_closeDialog
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelAS2TransmissionReceiver;
    private javax.swing.JLabel jLabelAS2TransmissionSender;
    private javax.swing.JLabel jLabelArrow;
    private javax.swing.JLabel jLabelStateOverviewImage;
    private javax.swing.JLabel jLabelTLSIcon;
    private javax.swing.JLabel jLabelTransactionStateDetails;
    private javax.swing.JLabel jLabelTransactionStateGeneral;
    private javax.swing.JLabel jLabelTransmissionDescription;
    private javax.swing.JPanel jPanelArrow;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelInfo;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelOverviewImage;
    private javax.swing.JPanel jPanelProcessLog;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpaceHorizontal1;
    private javax.swing.JPanel jPanelSpaceHorizontal2;
    private javax.swing.JPanel jPanelVerticalSpace1;
    private javax.swing.JPanel jPanelVerticalSpace2;
    private javax.swing.JScrollPane jScrollPaneList;
    private javax.swing.JScrollPane jScrollPaneLog;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTableMessageDetails;
    private javax.swing.JTextPane jTextPaneLog;
    // End of variables declaration//GEN-END:variables
}
