//$Header: /as2/de/mendelson/comm/as2/webclient2/TransactionDetailsDialog.java 31    8/08/22 10:13 Heller $
package de.mendelson.comm.as2.webclient2;

import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.DateRenderer;
import de.mendelson.comm.as2.log.LogAccessDB;
import de.mendelson.comm.as2.log.LogEntry;
import de.mendelson.comm.as2.message.AS2Info;
import de.mendelson.comm.as2.message.AS2MDNInfo;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MDNAccessDB;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.message.ResourceBundleAS2Message;
import de.mendelson.comm.as2.message.loggui.ResourceBundleMessageDetails;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * The about dialog for the as2 server web ui
 *
 * @author S.Heller
 * @version $Revision: 31 $
 */
public class TransactionDetailsDialog extends OkDialog {

    private final String COLOR_RED = "#e74c3c";
    private final String COLOR_GREEN = "#27ae60";

    private IDBDriverManager dbDriverManager = null;
    private FileResource RESOURCE_IMAGE_IN;
    private FileResource RESOURCE_IMAGE_OUT;
    private FileResource RESOURCE_IMAGE_MESSAGE;
    private FileResource RESOURCE_IMAGE_SIGNAL_OK;
    private FileResource RESOURCE_IMAGE_SIGNAL_FAILURE;
    private FileResource RESOURCE_IMAGE_PENDING;
    private FileResource RESOURCE_IMAGE_STOPPED;
    private FileResource RESOURCE_IMAGE_FINISHED;
    private FileResource RESOURCE_IMAGE_LOCALSTATION;
    private FileResource RESOURCE_IMAGE_SINGLEPARTNER;
    private final MecResourceBundle rbMessage;
    private final MecResourceBundle rbMessageDetails;
    private Grid detailsGrid = null;
    private FilePanel rawMessageDecryptedPanel = null;
    private FilePanel messageHeaderPanel = null;
    private FilePanel[] payloadPanel = null;
    private List<AS2Payload> payload = new ArrayList<AS2Payload>();
    private TabSheet tabSheet = null;
    private Grid<GridDetailRow> grid = new Grid<GridDetailRow>();
    private AS2MessageInfo as2MessageInfo;
    private AS2MDNInfo as2MDNInfo;
    private Partner sender = null;
    private Partner receiver = null;
    private String timezoneStr;
    private Locale browserLocale;
    private String dispositionText = "--";

    /**
     *
     * @param configConnection
     * @param runtimeConnection
     * @param as2MessageInfo The AS2 message info to display
     * @param timezoneStr The timezone string as delivered by the browser/user
     * selection, e.g. "Europe/Berlin" or "America/New_York"
     * @browserLocale Locale of the browser - to format the date (e.g. 24h
     * format or 12h format)
     */
    public TransactionDetailsDialog(AS2MessageInfo as2MessageInfo,
            String timezoneStr, Locale browserLocale, IDBDriverManager dbDriverManager) {
        super(1400, 815, "");
        String messageId = as2MessageInfo.getMessageId();
        //prevent JavaScript
        String messageIdNoJavaScript = AS2WebUI.replaceJavaScriptOutput(messageId);
        super.setCaption("&nbsp;Transaction details (<strong>" + messageIdNoJavaScript + "</strong>)");
        super.setCaptionAsHtml(true);
        this.setResizable(true);
        this.setClosable(true);
        this.timezoneStr = timezoneStr;
        this.browserLocale = browserLocale;
        this.dbDriverManager = dbDriverManager;
        this.as2MessageInfo = as2MessageInfo;
        PartnerAccessDB partnerAccess = new PartnerAccessDB(dbDriverManager);
        this.sender = partnerAccess.getPartnerByAS2Id(as2MessageInfo.getSenderId(), PartnerAccessDB.DATA_COMPLETENESS_NAMES_AS2ID_TYPE);
        this.receiver = partnerAccess.getPartnerByAS2Id(as2MessageInfo.getReceiverId(), PartnerAccessDB.DATA_COMPLETENESS_NAMES_AS2ID_TYPE);
        try {
            MDNAccessDB mdnAccess = new MDNAccessDB(dbDriverManager);
            List<AS2MDNInfo> mdnInfoList = mdnAccess.getMDN(messageId);
            if (!mdnInfoList.isEmpty()) {
                AS2MDNInfo mdnInfo = mdnInfoList.get(0);
                if( mdnInfo.getDispositionState() != null ){
                    this.dispositionText = mdnInfo.getDispositionState();
                }
            }
        }catch (Exception e) {
            new Notification("Problem", "[" + e.getClass().getSimpleName() + "] " + e.getMessage(),
                    Notification.Type.WARNING_MESSAGE, true)
                    .show(Page.getCurrent());
        }
        this.setIcon(new ThemeResource("../mendelson/images/messagedetails_24x24.png"));
        //load resource bundle
        try {
            this.rbMessageDetails = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMessageDetails.class.getName(), Locale.ENGLISH);
            this.rbMessage = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Message.class.getName(), Locale.ENGLISH);
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }

    @Override
    public void init(boolean displayOkButton) {
        RESOURCE_IMAGE_IN = new FileResource(new File("/VAADIN/theme/mendelson/images/in.svg"));
        RESOURCE_IMAGE_OUT = new FileResource(new File("/VAADIN/theme/mendelson/images/out.svg"));
        RESOURCE_IMAGE_MESSAGE = new FileResource(new File("/VAADIN/theme/mendelson/images/message.svg"));
        RESOURCE_IMAGE_SIGNAL_OK = new FileResource(new File("/VAADIN/theme/mendelson/images/signal_ok.svg"));
        RESOURCE_IMAGE_SIGNAL_FAILURE = new FileResource(new File("/VAADIN/theme/mendelson/images/signal_failure.svg"));
        RESOURCE_IMAGE_PENDING = new FileResource(new File("/VAADIN/theme/mendelson/images/state_pending.svg"));
        RESOURCE_IMAGE_STOPPED = new FileResource(new File("/VAADIN/theme/mendelson/images/state_stopped.svg"));
        RESOURCE_IMAGE_FINISHED = new FileResource(new File("/VAADIN/theme/mendelson/images/state_finished.svg"));
        RESOURCE_IMAGE_LOCALSTATION = new FileResource(new File("/VAADIN/theme/mendelson/images/localstation.svg"));
        RESOURCE_IMAGE_SINGLEPARTNER = new FileResource(new File("/VAADIN/theme/mendelson/images/singlepartner.svg"));
        try {
            MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
            this.payload = messageAccess.getPayload(TransactionDetailsDialog.this.as2MessageInfo.getMessageId());
        } catch (Exception e) {
            new Notification("Problem", "["
                    + e.getClass().getSimpleName() + "] " + e.getMessage(),
                    Notification.Type.ERROR_MESSAGE, true)
                    .show(Page.getCurrent());
        } 
        super.init(displayOkButton);
        this.refreshTableContentAndSelectFirst();
    }

    private Date convertDateToDisplayTimezone(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = null;
        try {
            zoneId = ZoneId.of(this.timezoneStr);
        } catch (Exception e) {
            //DateTimeException - ignore it
        }
        if (zoneId != null) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return (Date.from(localDateTime.toInstant(zoneId.getRules().getOffset(localDateTime))));
        } else {
            //unknown new timezone - just return the original date
            return (Date.from(instant));
        }
    }

    /**
     * Could be overwritten, contains the content to display
     */
    @Override
    public AbstractComponent getContentPanel() {
        Panel panel = new Panel();
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(this.createTransactionStateOverview());
        this.detailsGrid = this.createDetailsGrid();
        layout.addComponent(this.detailsGrid);
        this.tabSheet = this.createTabSheet();
        layout.addComponent(this.tabSheet);
        panel.setContent(layout);
        return (panel);
    }

    /**
     * Creates a horizontal panel that contains information about the
     * transaction: State/sender/Receiver
     *
     * @return
     */
    private Panel createTransactionStateOverview() {
        HorizontalLayout firstLineLayout = new HorizontalLayout();
        firstLineLayout.setMargin(new MarginInfo(false, false, false, false));
        firstLineLayout.setSpacing(true);        
        HorizontalLayout secondLineLayout = new HorizontalLayout();
        secondLineLayout.setMargin(new MarginInfo(false, false, false, true));
        secondLineLayout.setSpacing(true);   
        //first line
        Label transactionStateImage = new Label();
        transactionStateImage.setCaptionAsHtml(true);
        FileResource stateResource = null;
        if (this.as2MessageInfo.getState() == AS2Message.STATE_FINISHED) {
            stateResource = RESOURCE_IMAGE_FINISHED;
        } else if (this.as2MessageInfo.getState() == AS2Message.STATE_PENDING) {
            stateResource = RESOURCE_IMAGE_PENDING;
        } else {
            stateResource = RESOURCE_IMAGE_STOPPED;
        }
        transactionStateImage.setCaption(this.generateImageLabelHTMLText(stateResource, 24, 24));
        firstLineLayout.addComponent(transactionStateImage);
        firstLineLayout.setComponentAlignment(transactionStateImage, Alignment.MIDDLE_LEFT);        
        Label labelSender = new Label();
        labelSender.setCaptionAsHtml(true);
        String senderDisplay = null;
        FileResource resourceSender = RESOURCE_IMAGE_SINGLEPARTNER;
        if (this.sender == null) {
            senderDisplay = this.as2MessageInfo.getSenderId();
        } else {
            senderDisplay = this.sender.getName();
            if (this.sender.isLocalStation()) {
                resourceSender = RESOURCE_IMAGE_LOCALSTATION;
            }
        }
        labelSender.setCaption(
                this.generateImageLabelHTMLTextWithPadding(resourceSender, 20, 20, -6)
                + "&nbsp;" + senderDisplay);
        firstLineLayout.addComponent(labelSender);
        firstLineLayout.setComponentAlignment(labelSender, Alignment.MIDDLE_LEFT);        
        Label labelArrow = new Label();
        labelArrow.setCaptionAsHtml(true);
        labelArrow.setCaption("&xrarr;");
        firstLineLayout.addComponent(labelArrow);
        firstLineLayout.setComponentAlignment(labelArrow, Alignment.MIDDLE_LEFT);  
        String receiverDisplay = null;
        FileResource resourceReceiver = RESOURCE_IMAGE_SINGLEPARTNER;
        if (this.receiver == null) {
            receiverDisplay = this.as2MessageInfo.getReceiverId();
        } else {
            receiverDisplay = this.receiver.getName();
            if (this.receiver.isLocalStation()) {
                resourceReceiver = RESOURCE_IMAGE_LOCALSTATION;
            }
        }
        Label labelReceiver = new Label();
        labelReceiver.setCaptionAsHtml(true);
        labelReceiver.setCaption(
                this.generateImageLabelHTMLTextWithPadding(
                        resourceReceiver, 20, 20, -6) + receiverDisplay);
        firstLineLayout.addComponent(labelReceiver);
        firstLineLayout.setComponentAlignment(labelReceiver, Alignment.MIDDLE_LEFT);   
        String colorGreen = "<span style=\"color:" + COLOR_GREEN + "\">";
        String colorRed = "<span style=\"color:" + COLOR_RED + "\">";
        String color = this.dispositionText.contains("error")?colorRed:colorGreen;
        Label labelDisposition = new Label();
        labelDisposition.setCaptionAsHtml(true);
        labelDisposition.setCaption( "<HTML><strong>" + color + "[" + this.dispositionText + "]</span></strong></HTML>");
        firstLineLayout.addComponent(labelDisposition);
        firstLineLayout.setComponentAlignment(labelDisposition, Alignment.MIDDLE_LEFT);        
        //second line
        Label labelDetails = new Label();
        labelDetails.setCaptionAsHtml(true);
        if (this.as2MessageInfo.getDirection() == AS2MessageInfo.DIRECTION_OUT) {
            if (this.as2MessageInfo.requestsSyncMDN()) {
                labelDetails.setCaption("<HTML>"
                        + "<i>"
                        + this.rbMessageDetails.getResourceString("transactiondetails.outbound.sync") + "</HTML>");
            } else {
                labelDetails.setCaption("<HTML>"
                        + "<i>"
                        + this.rbMessageDetails.getResourceString("transactiondetails.outbound.async") + "</HTML>");
            }
        } else {
            if (this.as2MessageInfo.requestsSyncMDN()) {
                labelDetails.setCaption("<HTML>"
                        + "<i>"
                        + this.rbMessageDetails.getResourceString("transactiondetails.inbound.sync") + "</i></HTML>");
            } else {
                labelDetails.setCaption("<HTML>"
                        + "<i>"
                        + this.rbMessageDetails.getResourceString("transactiondetails.inbound.async") + "</i></HTML>");
            }
        }
        secondLineLayout.addComponent(labelDetails);                      
        secondLineLayout.setComponentAlignment(labelDetails, Alignment.MIDDLE_LEFT);                          
        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(new MarginInfo(true, true, true, true));
        panelLayout.setSpacing(false);  
        panelLayout.addComponent(firstLineLayout);
        panelLayout.addComponent(secondLineLayout);
        Panel panel = new Panel();
        panel.setContent(panelLayout);
        return (panel);
    }

    private TabSheet createTabSheet() {
        TabSheet tabsheet = new TabSheet();
        RichTextArea logTextArea = this.createLogTab();
        //add a layout to the tabs for scroll bars
        VerticalLayout layoutscrollLog = new VerticalLayout(logTextArea);
        tabsheet.addTab(layoutscrollLog, "Log", null);
        this.rawMessageDecryptedPanel = this.createRawMessageDecryptedPanel();
        tabsheet.addTab(new VerticalLayout(this.rawMessageDecryptedPanel), "Raw message decrypted", null);
        this.messageHeaderPanel = this.createMessageHeaderPanel();
        tabsheet.addTab(new VerticalLayout(this.messageHeaderPanel), "Message header", null);
        this.payloadPanel = this.createPayloadPanel();
        for (int i = 0; i < this.payloadPanel.length; i++) {
            String tabTitle = "Payload";
            if (this.payloadPanel.length > 1) {
                tabTitle += " " + (i + 1);
            }
            tabsheet.addTab(new VerticalLayout(this.payloadPanel[i]), tabTitle, null);
        }
        tabsheet.setWidth(100, Unit.PERCENTAGE);
        tabsheet.setHeight(430, Unit.PIXELS);
        return (tabsheet);
    }

    private RichTextArea createLogTab() {
        DateFormat format = SimpleDateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.MEDIUM, this.browserLocale);
        RichTextArea textArea = new RichTextArea();
        textArea.setWidth(100, Unit.PERCENTAGE);
        textArea.setHeightUndefined();
        try {
            LogAccessDB logAccess = new LogAccessDB(this.dbDriverManager);
            List<LogEntry> entries = logAccess.getLog(this.as2MessageInfo.getMessageId());
            StringBuilder log = new StringBuilder();
            log.append("<HTML>");
            for (LogEntry entry : entries) {
                Date logDate = this.convertDateToDisplayTimezone(new Date(entry.getMillis()));
                log.append("<strong>[").append(format.format(logDate)).append("]</strong> ");
                if (entry.getLevel().equals(Level.SEVERE)) {
                    log.append("<span style=\"color:" + COLOR_RED + "\">");
                } else if (entry.getLevel().intValue() < Level.INFO.intValue()) {
                    log.append("<span style=\"color:" + COLOR_GREEN + "\">");
                }
                //prevent JavaScript
                log.append(AS2WebUI.replaceJavaScriptOutput(entry.getMessage()));
                if (entry.getLevel().equals(Level.SEVERE) || entry.getLevel().intValue() < Level.INFO.intValue()) {
                    log.append("</span>");
                }
                log.append("<br>");
            }
            log.append("</HTML>");
            textArea.setValue(log.toString());
            textArea.setReadOnly(true);
        } catch (Exception e) {
            new Notification("Problem", "["
                    + e.getClass().getSimpleName() + "] " + e.getMessage(),
                    Notification.Type.ERROR_MESSAGE, true)
                    .show(Page.getCurrent());
        }
        return (textArea);
    }

    private FilePanel createRawMessageDecryptedPanel() {
        FilePanel panel = new FilePanel();
        panel.setReadOnly(true);
        panel.setHeightUndefined();
        return (panel);
    }

    private FilePanel createMessageHeaderPanel() {
        FilePanel panel = new FilePanel();
        panel.setReadOnly(true);
        panel.setHeightUndefined();
        return (panel);
    }

    private FilePanel[] createPayloadPanel() {
        FilePanel[] panel = new FilePanel[this.payload.size()];
        for (int i = 0; i < panel.length; i++) {
            panel[i] = new FilePanel();
            panel[i].setReadOnly(true);
            panel[i].setHeightUndefined();
        }
        return (panel);
    }

    private Grid<GridDetailRow> createDetailsGrid() {
        this.grid.setWidth(100, Unit.PERCENTAGE);
        this.grid.setHeightByRows(2);
        this.grid.setHeightMode(HeightMode.ROW);
        this.grid.setSelectionMode(SelectionMode.SINGLE);

        // Handle selection change.
        this.grid.addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChange(SelectionEvent event) {
                Optional selectedItem = event.getFirstSelectedItem();
                if (selectedItem != null && !selectedItem.isEmpty() && selectedItem.isPresent()) {
                    try {
                        GridDetailRow selectionRow = (GridDetailRow) selectedItem.get();
                        AS2Info info = selectionRow.getAS2Info();
                        Path rawFile = null;
                        if (!info.isMDN()) {
                            AS2MessageInfo messageInfo = (AS2MessageInfo) info;
                            if (messageInfo.getRawFilenameDecrypted() != null) {
                                rawFile = Paths.get(messageInfo.getRawFilenameDecrypted());
                            } else if (messageInfo.getRawFilename() != null) {
                                rawFile = Paths.get(messageInfo.getRawFilename());
                            }
                        } else if (info.isMDN()) {
                            AS2MDNInfo mdnInfo = (AS2MDNInfo) info;
                            if (mdnInfo.getRawFilename() != null) {
                                rawFile = Paths.get(mdnInfo.getRawFilename());
                            }
                        }
                        TransactionDetailsDialog.this.rawMessageDecryptedPanel.displayFile(rawFile, false);
                        Path headerFile = null;
                        if (info.getHeaderFilename() != null) {
                            headerFile = Paths.get(info.getHeaderFilename());
                        }
                        TransactionDetailsDialog.this.messageHeaderPanel.displayFile(headerFile, false);

                        if (TransactionDetailsDialog.this.payload.size() > 0) {
                            for (int i = 0; i < TransactionDetailsDialog.this.payload.size(); i++) {
                                Path payloadFile = Paths.get(TransactionDetailsDialog.this.payload.get(i).getPayloadFilename());
                                TransactionDetailsDialog.this.payloadPanel[i].displayFile(payloadFile, true);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        new Notification("Problem", "[" + ex.getClass().getSimpleName() + "] " + ex.getMessage(),
                                Notification.Type.WARNING_MESSAGE, true)
                                .show(Page.getCurrent());
                    }
                }
            }
        });
        //disallow empty selection
        ((SingleSelectionModel<GridDetailRow>) grid.getSelectionModel()).setDeselectAllowed(false);
        this.grid.addStyleName("components-inside");
        this.grid.addColumn(GridDetailRow::getDirectionIcon, new ComponentRenderer()).setWidth(55).setResizable(false);
        this.grid.addColumn(GridDetailRow::getInitDate,
                new DateRenderer(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM,
                        this.browserLocale))).setCaption("Date");
        this.grid.addColumn(GridDetailRow::getStateIcon, new ComponentRenderer()).setWidth(55).setResizable(false);
        this.grid.addColumn(GridDetailRow::getSecurity).setCaption("Security");
        this.grid.addColumn(GridDetailRow::getSender).setCaption("Sender");
        this.grid.addColumn(GridDetailRow::getServer).setCaption("Server");
        return (this.grid);

    }

    /**
     * Refreshes the table content, AS2 message and the signal. Will also select
     * the first entry
     */
    private void refreshTableContentAndSelectFirst() {
        try {
            MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
            //add the content
            List<AS2Info> infoList = messageAccess.getMessageDetails(this.as2MessageInfo.getMessageId());
            List<GridDetailRow> displayList = new ArrayList<GridDetailRow>();
            for (AS2Info info : infoList) {
                displayList.add(new GridDetailRow(info));
            }
            this.grid.setItems(displayList);
            if (!displayList.isEmpty()) {
                this.grid.getSelectionModel().select(displayList.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Notification("Problem", "[" + e.getClass().getSimpleName() + "] " + e.getMessage(),
                    Notification.Type.WARNING_MESSAGE, true)
                    .show(Page.getCurrent());
        } 
    }

    private Label generateImageLabel(FileResource resource) {
        Label label = new Label();
        label.setContentMode(ContentMode.HTML);
        label.setIcon(resource);
        label.setValue(generateImageLabelHTMLText(resource, AS2WebUI.ICON_SIZE_STANDARD, AS2WebUI.ICON_SIZE_STANDARD));
        return (label);
    }

    private String generateImageLabelHTMLText(FileResource resource, int width, int height) {
        return ("<img src=\"VAADIN/themes/mendelson/images/" + resource.getFilename()
                + "\" width=\"" + width + "\" height=\"" + height + "\"/>");
    }

    private String generateImageLabelHTMLTextWithPadding(FileResource resource, int width, int height, int padding) {
        return ("<img style=\"padding-right: 4px;margin-bottom: " + padding + "px;\" src=\"VAADIN/themes/mendelson/images/" + resource.getFilename()
                + "\" width=\"" + width + "\" height=\"" + height + "\"/>");
    }

    private final class GridDetailRow {

        private final AS2Info as2Info;

        public GridDetailRow(AS2Info as2Info) {
            this.as2Info = as2Info;
        }

        public Label getDirectionIcon() {
            if (this.as2Info.getDirection() == AS2MessageInfo.DIRECTION_IN) {
                return (generateImageLabel(RESOURCE_IMAGE_IN));
            } else {
                return (generateImageLabel(RESOURCE_IMAGE_OUT));
            }
        }

        public Date getInitDate() {
            return (TransactionDetailsDialog.this.convertDateToDisplayTimezone(this.as2Info.getInitDate()));
        }

        public Label getStateIcon() {
            FileResource stateIcon = null;
            if (this.as2Info.isMDN()) {
                if (this.as2Info.getState() == AS2Message.STATE_FINISHED) {
                    stateIcon = RESOURCE_IMAGE_SIGNAL_OK;
                } else {
                    stateIcon = RESOURCE_IMAGE_SIGNAL_FAILURE;
                }
            } else {
                stateIcon = RESOURCE_IMAGE_MESSAGE;
            }
            return (generateImageLabel(stateIcon));
        }

        public String getSecurity() {
            StringBuilder builderSecurity = new StringBuilder();
            builderSecurity.append(rbMessage.getResourceString(
                    "signature." + this.as2Info.getSignType()));
            if (!this.as2Info.isMDN()) {
                builderSecurity.append("/");
                builderSecurity.append(rbMessage.getResourceString(
                        "encryption." + this.as2Info.getEncryptionType()));
            }
            return (builderSecurity.toString());
        }

        public String getSender() {
            String sender = "";
            if (this.as2Info.getSenderHost() != null) {
                sender = this.as2Info.getSenderHost();
            }
            sender = AS2WebUI.replaceJavaScriptOutput(sender);
            return (sender);
        }

        public String getServer() {
            String serversUserAgent = "";
            if (this.as2Info.getUserAgent() != null) {
                serversUserAgent = this.as2Info.getUserAgent();
            }
            serversUserAgent = AS2WebUI.replaceJavaScriptOutput(serversUserAgent);
            return (serversUserAgent);
        }

        public AS2Info getAS2Info() {
            return (this.as2Info);
        }

    }

}
