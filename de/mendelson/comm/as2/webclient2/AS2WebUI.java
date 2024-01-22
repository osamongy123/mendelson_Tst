//$Header: /as2/de/mendelson/comm/as2/webclient2/AS2WebUI.java 69    22/11/22 8:41 Heller $
package de.mendelson.comm.as2.webclient2;

import com.vaadin.annotations.Theme;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.UI;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.database.DBDriverManagerHSQL;
import de.mendelson.comm.as2.database.DBDriverManagerMySQL;
import de.mendelson.comm.as2.database.DBDriverManagerOracleDB;
import de.mendelson.comm.as2.database.DBDriverManagerPostgreSQL;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.ResourceBundleAS2Message;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.AnonymousTextClient;
import de.mendelson.util.clientserver.about.ServerInfoRequest;
import de.mendelson.util.clientserver.about.ServerInfoResponse;
import de.mendelson.util.clientserver.user.User;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.message.MessageOverviewFilter;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.server.ServerPlugins;
import de.mendelson.util.clientserver.user.UserAccess;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.PBKDF2;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Main frame for the web interface
 *
 * @author S.Heller
 * @version $Revision: 69 $
 */
@Theme("valo")
public class AS2WebUI extends UI {

    public static final int ICON_SIZE_STANDARD = 20;
    public static final int ICON_SIZE_BUTTON = 24;

    private Button buttonDetails = null;
    /**
     * Format the date display
     */
    private VerticalLayout mainWindowLayout = new VerticalLayout();
    private Panel mainPanel = new Panel();
    private VerticalLayout mainPanelLayout = new VerticalLayout();
    private FileResource RESOURCE_IMAGE_IN;
    private FileResource RESOURCE_IMAGE_OUT;
    private FileResource RESOURCE_IMAGE_PENDING;
    private FileResource RESOURCE_IMAGE_STOPPED;
    private FileResource RESOURCE_IMAGE_FINISHED;
    private FileResource RESOURCE_IMAGE_ALL;
    private FileResource RESOURCE_IMAGE_LOCALSTATION;
    private FileResource RESOURCE_IMAGE_SINGLEPARTNER;
    private FileResource RESOURCE_IMAGE_USER;
    private FileResource RESOURCE_IMAGE_SUM_OF_MESSAGES_IN_SYSTEM;
    private final MecResourceBundle rbMessage;
    /**
     * Stores information about the browser
     */
    private WebBrowser browser = null;
    private User user = null;
    private Label labelUsername = new Label();
    private Grid<GridOverviewRow> overviewGrid = new Grid<GridOverviewRow>();
    /**
     * Footer
     */
    private Label footerTransactionSum = new Label();
    private Label footerTransactionOkSum = new Label();
    private Label footerTransactionPendingSum = new Label();
    private Label footerTransactionErrorSum = new Label();
    private Label footerTransactionSumAllInSystem = new Label();
    private Panel footerPanel;
    private GridLayout welcomeLayout = new GridLayout(2, 2);
    private final IDBDriverManager dbDriverManager;
    private ComboBox<PartnerEntry> comboboxFilterLocalstation = null;
    private ComboBox<PartnerEntry> comboboxFilterRemotestation = null;
    private CheckBox checkboxOk = null;
    private CheckBox checkboxPending = null;
    private CheckBox checkboxStopped = null;
    /**
     * Date/Time selection
     */
    private RadioButtonGroup<String> buttonGroupTimezone = null;
    private String buttonLabelTimezoneStrLocal = null;
    private String buttonLabelTimezoneStrServer = null;
    private String timezoneStrServer = null;
    private String timezoneStrLocal = null;
    private long timezoneOffsetInS = 0L;
    private boolean pluginActivatedHA = false;
    private boolean pluginActivatedPostgreSQL = false;
    private boolean pluginActivatedMySQL = false;
    private boolean pluginActivatedOracleDB = false;
    private boolean webinterfaceActivated = false;

    /**
     * This is the entry point of you application as denoted in your web.xml
     *
     */
    public AS2WebUI() {
        Locale.setDefault(Locale.UK);
        //load resource bundle
        try {
            this.rbMessage = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Message.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        AnonymousTextClient client = null;
        try {
            client = new AnonymousTextClient();
            client.setDisplayServerLogMessages(false);
            client.connect("localhost", AS2Server.CLIENTSERVER_COMM_PORT, 30000);
            ServerInfoResponse response = (ServerInfoResponse) client.sendSync(new ServerInfoRequest(), 30000);
            if (response.getException() != null) {
                throw response.getException();
            }
            this.webinterfaceActivated = response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                    != null && response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                            .contains(ServerPlugins.PLUGIN_WEBINTERFACE);
            this.pluginActivatedHA = response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                    != null && response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                            .contains(ServerPlugins.PLUGIN_HA);
            this.pluginActivatedPostgreSQL = response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                    != null && response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                            .contains(ServerPlugins.PLUGIN_POSTGRESQL);
            this.pluginActivatedMySQL = response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                    != null && response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                            .contains(ServerPlugins.PLUGIN_MYSQL);
            this.pluginActivatedOracleDB = response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                    != null && response.getProperties().getProperty(ServerInfoRequest.PLUGINS)
                            .contains(ServerPlugins.PLUGIN_ORACLE_DB);
            if (this.pluginActivatedPostgreSQL) {
                this.dbDriverManager = DBDriverManagerPostgreSQL.instance();
            } else if (this.pluginActivatedMySQL) {
                this.dbDriverManager = DBDriverManagerMySQL.instance();
            }else if (this.pluginActivatedOracleDB) {
                this.dbDriverManager = DBDriverManagerOracleDB.instance();
            } else {
                this.dbDriverManager = DBDriverManagerHSQL.instance();
            }
            this.dbDriverManager.setupConnectionPool();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        }
        this.labelUsername.setCaptionAsHtml(true);
    }

    /**
     * Init is invoked on application load (when a user accesses the application
     * for the first time).
     */
    @Override
    public void init(VaadinRequest request) {
        this.user = null;
        this.browser = Page.getCurrent().getWebBrowser();
        RESOURCE_IMAGE_IN = new FileResource(new File("/VAADIN/theme/mendelson/images/in.svg"));
        RESOURCE_IMAGE_OUT = new FileResource(new File("/VAADIN/theme/mendelson/images/out.svg"));
        RESOURCE_IMAGE_PENDING = new FileResource(new File("/VAADIN/theme/mendelson/images/state_pending.svg"));
        RESOURCE_IMAGE_STOPPED = new FileResource(new File("/VAADIN/theme/mendelson/images/state_stopped.svg"));
        RESOURCE_IMAGE_FINISHED = new FileResource(new File("/VAADIN/theme/mendelson/images/state_finished.svg"));
        RESOURCE_IMAGE_SUM_OF_MESSAGES_IN_SYSTEM = new FileResource(new File("/VAADIN/theme/mendelson/images/state_all_sum.svg"));
        RESOURCE_IMAGE_ALL = new FileResource(new File("/VAADIN/theme/mendelson/images/state_all.svg"));
        RESOURCE_IMAGE_LOCALSTATION = new FileResource(new File("/VAADIN/theme/mendelson/images/localstation.svg"));
        RESOURCE_IMAGE_SINGLEPARTNER = new FileResource(new File("/VAADIN/theme/mendelson/images/singlepartner.svg"));
        RESOURCE_IMAGE_USER = new FileResource(new File("/VAADIN/theme/mendelson/images/origin_user.svg"));
        try {
            this.checkRunningPermission();
        } catch (Exception e) {
            e.printStackTrace();
            new Notification("Fatal", e.getMessage(),
                    Notification.Type.ERROR_MESSAGE, true)
                    .show(Page.getCurrent());
        }
        //invisible so far: already generate the panel to switch later
        this.generateMainPanel();
        Panel logoPanel = this.generateLogoPanel();
        this.mainWindowLayout.addComponent(logoPanel);
        this.mainWindowLayout.setExpandRatio(logoPanel, 0f);
        this.generateWelcomePanel();
        this.mainWindowLayout.addComponentsAndExpand(this.welcomeLayout);
        this.mainWindowLayout.setExpandRatio(this.welcomeLayout, 1f);
        this.mainWindowLayout.setSizeFull();
        this.setContent(this.mainWindowLayout);
        Page.getCurrent().addBrowserWindowResizeListener(new Page.BrowserWindowResizeListener() {
            @Override
            public void browserWindowResized(Page.BrowserWindowResizeEvent event) {
                AS2WebUI.this.resizeGrid(event.getHeight());
            }
        });
        this.resizeGrid(Page.getCurrent().getBrowserWindowHeight());
    }

    /**
     * Checks if the web interface might run
     */
    private void checkRunningPermission() throws Exception {
        if (!this.webinterfaceActivated) {
            throw new Exception("This web interface license has not been activated so far. "
                    + "Please ask the mendelson support for an activation key.");
        }
        this.checkVersionMatch();
    }

    /**
     * Returns the version of this class
     */
    public static String getVersion() {
        String revision = "$Revision: 69 $";
        return (revision.substring(revision.indexOf(":") + 1,
                revision.lastIndexOf("$")).trim());
    }

    /**
     * Prevents to output any text that might contain JavaScript
     *
     * @param text
     * @return
     */
    public static final String replaceJavaScriptOutput(String text) {
        String pattern = "(\\<)(.*?)(\\>)";
        return (text.replaceAll(pattern, (char) 0xFF1C + "$2" + (char) 0xFF1E));
    }

    /**
     * The Grid requires a static valid how many rows to show. This should grow
     * or shrink if the browser is resized. This computes the possible values
     *
     * @param pageHeight
     */
    private void resizeGrid(int pageHeight) {
        int upperPart = 300;
        int lowerPart = 145;
        double headerRowHeight = 38;
        double bodyRowHeight = 38;
        double possibleGridHeight = pageHeight - upperPart - lowerPart - headerRowHeight;
        double possibleRows = possibleGridHeight / bodyRowHeight;
        //rescale the grid and never show less than 5 rows
        this.overviewGrid.setHeightByRows(Math.max(5, possibleRows));
    }

    private void generateMainPanel() {
        MenuBar menuBar = this.createMenuBar();
        this.mainPanelLayout.addComponent(menuBar);
        this.mainPanelLayout.setComponentAlignment(menuBar, Alignment.TOP_CENTER);
        HorizontalLayout buttonLayout = this.createButtonBar();
        this.mainPanelLayout.addComponent(buttonLayout);
        this.mainPanelLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
        this.overviewGrid = this.createOverviewGrid();
        this.overviewGrid.scrollToEnd();
        this.mainPanelLayout.addComponent(this.overviewGrid);
        this.mainPanelLayout.setComponentAlignment(this.overviewGrid, Alignment.MIDDLE_CENTER);
        this.footerPanel = this.generateFooter();
        this.mainPanelLayout.addComponent(this.footerPanel);
        this.mainPanelLayout.setComponentAlignment(this.footerPanel, Alignment.BOTTOM_CENTER);
        this.mainPanelLayout.setSpacing(true);
        this.mainPanel.setContent(this.mainPanelLayout);
        this.mainPanelLayout.setExpandRatio(this.overviewGrid, 1f);
    }

    private Panel generateFooter() {
        Panel panelFooter = new Panel();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(new MarginInfo(true, true, true, true));
        layout.setSpacing(true);
        layout.addComponent(this.footerTransactionSumAllInSystem);
        layout.addComponent(this.footerTransactionSum);
        layout.addComponent(this.footerTransactionOkSum);
        layout.addComponent(this.footerTransactionPendingSum);
        layout.addComponent(this.footerTransactionErrorSum);
        panelFooter.setContent(layout);
        return (panelFooter);
    }

    private Panel generateLogoPanel() {
        Panel logoPanel = new Panel();
        Embedded logoImage = new Embedded("", new ThemeResource("../mendelson/images/mendelson_banner.png"));
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setSpacing(false);
        logoLayout.setMargin(true);
        logoLayout.addComponent(logoImage);
        logoPanel.setContent(logoLayout);
        logoPanel.setHeight(110, Unit.PIXELS);
        return (logoPanel);
    }

    /**
     * Checks if the deployed web interface version matches the AS2 receipt
     * unit. A mismatch could happen if an update occurred
     */
    private void checkVersionMatch() throws Exception {
        AnonymousTextClient client = null;
        try {
            client = new AnonymousTextClient();
            client.setDisplayServerLogMessages(false);
            client.connect("localhost", AS2Server.CLIENTSERVER_COMM_PORT, 30000);
            ServerInfoResponse response = (ServerInfoResponse) client.sendSync(new ServerInfoRequest(), 30000);
            String remoteProductName = response.getProperties().getProperty(ServerInfoResponse.SERVER_PRODUCT_NAME);
            String remoteServerVersion = response.getProperties().getProperty(ServerInfoResponse.SERVER_VERSION);
            String remoteServerBuild = response.getProperties().getProperty(ServerInfoResponse.SERVER_BUILD);
            if (!remoteProductName.equals(AS2ServerVersion.getProductName())
                    || !remoteServerVersion.equals(AS2ServerVersion.getVersion())
                    || !remoteServerBuild.equals(AS2ServerVersion.getBuild())) {
                throw new Exception("Please update the mendelson AS2 web interface. "
                        + " This web interface is compatible to " + AS2ServerVersion.getFullProductName()
                        + " but the detected mendelson AS2 processing unit version is " + remoteProductName + " " + remoteServerVersion + " " + remoteServerBuild + ".");
            }
        } catch (Exception e) {
            StringBuilder builder = new StringBuilder();
            builder.append("Error connecting to mendelson AS2 processing unit: ");
            builder.append(e.getMessage());
            throw (e);
        } finally {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        }
    }

    private void displayMessageDetailsOfSelectedRow() {
        //ignore quick double clicks
        if (this.getWindows().isEmpty()) {
            Optional<GridOverviewRow> selection = this.overviewGrid.getSelectionModel().getFirstSelectedItem();
            if (!selection.isEmpty() && selection.isPresent()) {
                GridOverviewRow selectedRow = selection.get();
                if (selectedRow != null) {
                    AS2MessageInfo selectedInfo = selectedRow.getMessageInfo();
                    String displayTimezone = null;
                    //get the timezone string for the date/time transformation
                    if (this.buttonGroupTimezone.getSelectedItem().get().equals(this.buttonLabelTimezoneStrLocal)) {
                        displayTimezone = this.timezoneStrLocal;
                    } else {
                        displayTimezone = this.timezoneStrServer;
                    }
                    OkDialog dialog = new TransactionDetailsDialog(
                            selectedInfo, displayTimezone, this.browser.getLocale(), this.dbDriverManager);
                    dialog.init(false);
                    this.addWindow(dialog);
                }
            }
        }
    }

    /**
     * Generates the buttons and the filter comboboxes
     */
    private HorizontalLayout createButtonBar() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth(100f, Unit.PERCENTAGE);
        ThemeResource resourceRefresh = new ThemeResource("../mendelson/images/refresh_24x24.png");
        Button buttonRefresh = new Button("Refresh");
        buttonRefresh.setIcon(resourceRefresh);
        buttonRefresh.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_ICON_ALIGN_TOP);
        buttonRefresh.setEnabled(true);
        buttonRefresh.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                AS2WebUI.this.refreshOverviewTableData();
            }
        });
        buttonLayout.addComponent(buttonRefresh);
        buttonLayout.setComponentAlignment(buttonRefresh, Alignment.MIDDLE_CENTER);
        ThemeResource resourceDetails = new ThemeResource("../mendelson/images/messagedetails_24x24.png");
        this.buttonDetails = new Button("Details");
        this.buttonDetails.setIcon(resourceDetails);
        this.buttonDetails.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_ICON_ALIGN_TOP);
        this.buttonDetails.setEnabled(false);
        this.buttonDetails.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                AS2WebUI.this.displayMessageDetailsOfSelectedRow();
            }
        });
        buttonLayout.addComponent(this.buttonDetails);
        buttonLayout.setComponentAlignment(this.buttonDetails, Alignment.MIDDLE_CENTER);
        List<PartnerEntry> localStationList = new ArrayList<PartnerEntry>();
        List<PartnerEntry> remoteStationList = new ArrayList<PartnerEntry>();
        List<Partner> localStationsPartnerList = new ArrayList<Partner>();
        List<Partner> remoteStationsPartnerList = new ArrayList<Partner>();
        //load partner data
        try {
            PartnerAccessDB partnerAccess = new PartnerAccessDB(this.dbDriverManager);
            localStationsPartnerList.addAll(
                    partnerAccess.getLocalStations(PartnerAccessDB.DATA_COMPLETENESS_NAMES_AS2ID_TYPE));
            remoteStationsPartnerList.addAll(
                    partnerAccess.getNonLocalStations(PartnerAccessDB.DATA_COMPLETENESS_NAMES_AS2ID_TYPE));
        } catch (Exception e) {
            new Notification("Problem", e.getMessage(),
                    Notification.Type.WARNING_MESSAGE, true)
                    .show(Page.getCurrent());
        }
        this.comboboxFilterLocalstation = new ComboBox<PartnerEntry>();
        this.comboboxFilterLocalstation.setCaption(this.generateComboboxHTMLText("Local station", RESOURCE_IMAGE_LOCALSTATION,
                ICON_SIZE_STANDARD, ICON_SIZE_STANDARD));
        this.comboboxFilterLocalstation.setCaptionAsHtml(true);
        localStationList.add(new PartnerEntry(null));
        for (Partner partner : localStationsPartnerList) {
            localStationList.add(new PartnerEntry(partner));
        }
        this.comboboxFilterLocalstation.setItems(localStationList);
        this.comboboxFilterLocalstation.setTextInputAllowed(false);
        this.comboboxFilterLocalstation.setEmptySelectionAllowed(false);
        this.comboboxFilterLocalstation.setItemCaptionGenerator(PartnerEntry::toDisplay);
        this.comboboxFilterLocalstation.setSelectedItem(new PartnerEntry(null));
        buttonLayout.addComponent(this.comboboxFilterLocalstation);
        buttonLayout.setComponentAlignment(this.comboboxFilterLocalstation, Alignment.TOP_CENTER);

        this.comboboxFilterRemotestation = new ComboBox<PartnerEntry>();
        this.comboboxFilterRemotestation.setCaption(this.generateComboboxHTMLText("Remote station", RESOURCE_IMAGE_SINGLEPARTNER,
                ICON_SIZE_STANDARD, ICON_SIZE_STANDARD));
        this.comboboxFilterRemotestation.setCaptionAsHtml(true);
        remoteStationList.add(new PartnerEntry(null));
        for (Partner partner : remoteStationsPartnerList) {
            remoteStationList.add(new PartnerEntry(partner));
        }
        this.comboboxFilterRemotestation.setItems(remoteStationList);
        this.comboboxFilterRemotestation.setTextInputAllowed(false);
        this.comboboxFilterRemotestation.setEmptySelectionAllowed(false);
        this.comboboxFilterRemotestation.setItemCaptionGenerator(PartnerEntry::toDisplay);
        this.comboboxFilterRemotestation.setSelectedItem(new PartnerEntry(null));
        buttonLayout.addComponent(this.comboboxFilterRemotestation);
        buttonLayout.setComponentAlignment(this.comboboxFilterRemotestation, Alignment.TOP_CENTER);

        buttonLayout.addComponent(this.createStateSelection());
        buttonLayout.addComponent(this.createTimeSelection());

        this.labelUsername.setWidth(null);
        buttonLayout.addComponent(this.labelUsername);
        buttonLayout.setComponentAlignment(this.labelUsername, Alignment.TOP_RIGHT);
        buttonLayout.setExpandRatio(buttonRefresh, 0.0f);
        buttonLayout.setExpandRatio(this.buttonDetails, 0.0f);
        buttonLayout.setExpandRatio(this.labelUsername, 1.0f);
        return (buttonLayout);
    }

    /**
     * Computes the difference between the local and the server timezones in
     * seconds and returns it
     */
    private long computeTimezoneOffsetInS() {
        ZoneId zoneIdLocal = ZoneId.of(this.timezoneStrLocal);
        ZoneOffset zoneOffsetLocal = LocalDateTime.now().atZone(zoneIdLocal).getOffset();
        ZoneId zoneIdServer = ZoneId.of(this.timezoneStrServer);
        ZoneOffset zoneOffsetServer = LocalDateTime.now().atZone(zoneIdServer).getOffset();
        int zoneOffsetServerAsSeconds = zoneOffsetServer.getTotalSeconds();
        int zoneOffsetLocalAsSeconds = zoneOffsetLocal.getTotalSeconds();
        int differenceInSeconds = zoneOffsetLocalAsSeconds - zoneOffsetServerAsSeconds;
        return (differenceInSeconds);
    }

    /**
     * Returns the offset of a timezone given by its id. Will return something
     * like "-08:00"
     *
     * @param timezoneId
     * @return
     */
    private String getTimezoneOffsetAsString(String timezoneIdStr) {
        ZoneId zoneId = ZoneId.of(timezoneIdStr);
        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(zoneId);
        ZoneOffset zoneOffset = zonedDateTime.getOffset();
        String offsetStr = zoneOffset.getId().replaceAll("Z", "+00:00");
        return (offsetStr);
    }

    private Layout createTimeSelection() {
        HorizontalLayout layout = new HorizontalLayout();
        this.buttonGroupTimezone = new RadioButtonGroup<String>("Date/Time display");
        this.timezoneStrLocal = ZoneId.systemDefault().toString();
        this.timezoneStrServer = ZoneId.systemDefault().toString();
        if (this.browser != null && this.browser.getTimeZoneId() != null) {
            //not all browsers might support the time zone
            this.timezoneStrLocal = this.browser.getTimeZoneId();
        }
        this.buttonLabelTimezoneStrLocal = "Local [" + this.timezoneStrLocal
                + "; " + this.getTimezoneOffsetAsString(timezoneStrLocal) + "]";
        this.buttonLabelTimezoneStrServer = "AS2 server [" + this.timezoneStrServer
                + "; " + this.getTimezoneOffsetAsString(timezoneStrServer) + "]";
        this.timezoneOffsetInS = this.computeTimezoneOffsetInS();
        buttonGroupTimezone.setItems(this.buttonLabelTimezoneStrLocal, this.buttonLabelTimezoneStrServer);
        if (this.timezoneStrServer.equals(this.timezoneStrLocal)) {
            this.buttonGroupTimezone.setItemEnabledProvider(new SerializablePredicate<String>() {
                @Override
                public boolean test(String itemStr) {
                    return (itemStr.equals(AS2WebUI.this.buttonLabelTimezoneStrLocal));
                }
            });
        }
        buttonGroupTimezone.setSelectedItem(this.buttonLabelTimezoneStrLocal);
        layout.addComponent(buttonGroupTimezone);
        layout.setSpacing(true);
        return (layout);

    }

    private Layout createStateSelection() {
        int imageSize = 18;
        GridLayout layout = new GridLayout(2, 3);
        layout.setMargin(false);
        layout.setSpacing(true);
        layout.setWidth(300, Unit.PIXELS);
        this.checkboxOk = new CheckBox();
        this.checkboxOk.setCaption(this.generateCheckboxHTMLText("Display Ok", RESOURCE_IMAGE_FINISHED, imageSize, imageSize));
        this.checkboxOk.setCaptionAsHtml(true);
        this.checkboxOk.setValue(true);
        layout.addComponent(this.checkboxOk, 1, 0);
        this.checkboxPending = new CheckBox();
        this.checkboxPending.setCaption(this.generateCheckboxHTMLText("Display Pending", RESOURCE_IMAGE_PENDING, imageSize, imageSize));
        this.checkboxPending.setCaptionAsHtml(true);
        this.checkboxPending.setValue(true);
        layout.addComponent(this.checkboxPending, 1, 1);
        this.checkboxStopped = new CheckBox();
        this.checkboxStopped.setCaption(this.generateCheckboxHTMLText("Display Stopped", RESOURCE_IMAGE_STOPPED, imageSize, imageSize));
        this.checkboxStopped.setCaptionAsHtml(true);
        this.checkboxStopped.setValue(true);
        layout.addComponent(this.checkboxStopped, 1, 2);
        return (layout);
    }

    public void logout() {
        this.user = null;
        this.overviewGrid.setItems(new ArrayList<GridOverviewRow>());
        this.mainWindowLayout.removeComponent(this.mainPanel);
        this.mainWindowLayout.addComponentsAndExpand(this.welcomeLayout);
        this.mainWindowLayout.setExpandRatio(this.welcomeLayout, 1f);
        UI.getCurrent().getSession().close();
        UI.getCurrent().getPage().reload();
    }

    private MenuBar createMenuBar() {
        MenuBar.Command logoutCommand = new MenuBar.Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                logout();
            }
        };
        MenuBar.Command stateCommand = new MenuBar.Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                OkDialog dialog = new StateDialog();
                dialog.init(false);
                AS2WebUI.this.addWindow(dialog);
            }
        };
        MenuBar.Command haOverviewCommand = null;
        if (this.pluginActivatedHA) {
            haOverviewCommand = new MenuBar.Command() {

                @Override
                public void menuSelected(MenuItem selectedItem) {
                    OkDialog dialog = new HADialog(dbDriverManager);
                    dialog.init(false);
                    AS2WebUI.this.addWindow(dialog);
                }
            };
        }
        MenuBar.Command aboutCommand = new MenuBar.Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                OkDialog dialog = new AboutDialog();
                dialog.init(false);
                AS2WebUI.this.addWindow(dialog);
            }
        };
        MenuBar menubar = new MenuBar();
        MenuBar.MenuItem fileItem = menubar.addItem("AS2 server", null, null);
        fileItem.addItem("State", null, stateCommand);
        if (haOverviewCommand != null) {
            fileItem.addItem("HA Cluster overview", null, haOverviewCommand);
        }
        fileItem.addItem("Logout", null, logoutCommand);
        MenuBar.MenuItem helpItem = menubar.addItem("Help", null, null);
        helpItem.addItem("About", null, aboutCommand);
        menubar.setSizeFull();
        return (menubar);
    }

    /**
     * Connect to the database and load the data into a table
     */
    private Grid createOverviewGrid() {
        this.overviewGrid.setWidth(100, Unit.PERCENTAGE);
        this.overviewGrid.setHeightByRows(11);
        this.overviewGrid.setHeightMode(HeightMode.ROW);
        this.overviewGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        // Handle selection change.
        this.overviewGrid.addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChange(SelectionEvent event) {
                AS2WebUI.this.buttonDetails.setEnabled(true);
            }
        });
        this.overviewGrid.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(Grid.ItemClick event) {
                //check if there is a selection
                Optional<GridOverviewRow> selectedItem = overviewGrid.getSelectionModel().getFirstSelectedItem();
                if (selectedItem != null && !selectedItem.isEmpty()) {
                    //just display the row if this is no selection click but a display click
                    if (event.getItem().equals(selectedItem.get())) {
                        displayMessageDetailsOfSelectedRow();
                    }
                }
            }
        });
        //disallow empty selection
        ((SingleSelectionModel<GridOverviewRow>) this.overviewGrid.getSelectionModel()).setDeselectAllowed(false);
        this.overviewGrid.addColumn(GridOverviewRow::getStateIcon, new ComponentRenderer()).setWidth(55).setResizable(false);
        this.overviewGrid.addColumn(GridOverviewRow::getDirectionIcon, new ComponentRenderer()).setWidth(55).setResizable(false);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM,
                this.browser.getLocale());
        this.overviewGrid.addColumn(GridOverviewRow::getInitDate,
                new DateRenderer(dateFormat)).setCaption("Timestamp");
        this.overviewGrid.addColumn(GridOverviewRow::getLocalStation).setCaption("Local station");
        this.overviewGrid.addColumn(GridOverviewRow::getPartner).setCaption("Remote station");
        this.overviewGrid.addColumn(GridOverviewRow::getMessageId).setCaption("Message Id");
        this.overviewGrid.addColumn(GridOverviewRow::getPayload).setCaption("Payload");
        this.overviewGrid.addColumn(GridOverviewRow::getEncryption).setCaption("Encryption");
        this.overviewGrid.addColumn(GridOverviewRow::getSignature).setCaption("Signature");
        this.overviewGrid.addColumn(GridOverviewRow::getSyncAsync).setCaption("MDN");
        return (this.overviewGrid);
    }

    private void refreshOverviewTableData() {
        //always check if there is still a user available, parallel screens are possible
        if (this.user == null) {
            this.logout();
            return;
        }
        /**
         * Stores all partner ids and the corresponding partner objects
         */
        Map<String, Partner> partnerMap = new HashMap<String, Partner>();
        //load partner data
        try {
            PartnerAccessDB partnerAccess = new PartnerAccessDB(this.dbDriverManager);
            List<Partner> partner = partnerAccess.getAllPartner(PartnerAccessDB.DATA_COMPLETENESS_NAMES_AS2ID_TYPE);
            for (Partner singlePartner : partner) {
                partnerMap.put(singlePartner.getAS2Identification(), singlePartner);
            }
        } catch (Exception e) {
            new Notification("Problem", e.getMessage(),
                    Notification.Type.WARNING_MESSAGE, true)
                    .show(Page.getCurrent());
        }
        int displaySum = 0;
        int sumPending = 0;
        int sumOk = 0;
        int sumError = 0;
        int sumAllInSystem = 0;
        //load message data
        try {
            MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
            MessageOverviewFilter filter = new MessageOverviewFilter();
            sumAllInSystem = messageAccess.getMessageCount();
            //1000 transactions max
            filter.setLimit(1000);
            Optional<PartnerEntry> localStationFilter = this.comboboxFilterLocalstation.getSelectedItem();
            if (localStationFilter != null && !localStationFilter.isEmpty()) {
                Partner localStationFilterPartner = (localStationFilter.get()).getPartner();
                if (localStationFilterPartner != null) {
                    filter.setShowLocalStation(localStationFilterPartner);
                }
            }
            Optional<PartnerEntry> remoteStationFilter = this.comboboxFilterRemotestation.getSelectedItem();
            if (remoteStationFilter != null && !remoteStationFilter.isEmpty()) {
                Partner remoteStationFilterPartner = (remoteStationFilter.get()).getPartner();
                if (remoteStationFilterPartner != null) {
                    filter.setShowPartner(remoteStationFilterPartner);
                }
            }
            filter.setShowFinished(this.checkboxOk.getValue().booleanValue());
            filter.setShowPending(this.checkboxPending.getValue().booleanValue());
            filter.setShowStopped(this.checkboxStopped.getValue().booleanValue());
            List<AS2MessageInfo> info = messageAccess.getMessageOverview(filter);
            List<GridOverviewRow> displayList = new ArrayList<GridOverviewRow>();
            for (int i = 0; i < info.size(); i++) {
                AS2MessageInfo messageInfo = info.get(i);
                AS2Message message = new AS2Message(messageInfo);
                List<AS2Payload> payloads = messageAccess.getPayload(messageInfo.getMessageId());
                for (AS2Payload payload : payloads) {
                    message.addPayload(payload);
                }
                displaySum++;
                if (messageInfo.getState() == AS2Message.STATE_FINISHED) {
                    sumOk++;
                } else if (messageInfo.getState() == AS2Message.STATE_STOPPED) {
                    sumError++;
                } else {
                    sumPending++;
                }
                displayList.add(new GridOverviewRow(message, messageInfo, partnerMap));
            }
            this.overviewGrid.setItems(displayList);
            //refresh the footer
            this.refreshFooterLabel(this.footerTransactionSumAllInSystem, RESOURCE_IMAGE_SUM_OF_MESSAGES_IN_SYSTEM, sumAllInSystem);
            this.refreshFooterLabel(this.footerTransactionSum, RESOURCE_IMAGE_ALL, displaySum);
            this.refreshFooterLabel(this.footerTransactionErrorSum, RESOURCE_IMAGE_STOPPED, sumError);
            this.refreshFooterLabel(this.footerTransactionPendingSum, RESOURCE_IMAGE_PENDING, sumPending);
            this.refreshFooterLabel(this.footerTransactionOkSum, RESOURCE_IMAGE_FINISHED, sumOk);
        } catch (Exception e) {
            new Notification("Problem", "[" + e.getClass().getSimpleName() + "] " + e.getMessage(),
                    Notification.Type.WARNING_MESSAGE, true)
                    .show(Page.getCurrent());
        }
    }

    private Label generateImageLabel(FileResource resource) {
        Label label = new Label();
        label.setContentMode(ContentMode.HTML);
        label.setValue(this.generateImageLabelHTMLText(resource, ICON_SIZE_STANDARD, ICON_SIZE_STANDARD));
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

    private String generateCheckboxHTMLText(String text, FileResource resource, int width, int height) {
        return ("<div><img style=\"padding-right: 4px;margin-bottom: -3px;\" src=\"VAADIN/themes/mendelson/images/" + resource.getFilename()
                + "\" width=\"" + width + "\" height=\"" + height + "\"/>" + text + "</div>");
    }

    private String generateComboboxHTMLText(String text, FileResource resource, int width, int height) {
        return ("<div><img style=\"padding-right: 4px;margin-bottom: -3px;\" src=\"VAADIN/themes/mendelson/images/" + resource.getFilename()
                + "\" width=\"" + width + "\" height=\"" + height + "\"/>" + text + "</div>");
    }

    private void refreshFooterLabel(Label label, FileResource resource, int count) {
        label.setContentMode(ContentMode.HTML);
        label.setValue("<div><img style=\"padding-right: 6px;margin-bottom: -3px;\" src=\"VAADIN/themes/mendelson/images/" + resource.getFilename()
                + "\" width=\"" + ICON_SIZE_STANDARD + "\" height=\"" + ICON_SIZE_STANDARD + "\"/>" + String.valueOf(count) + "</div>");
    }

    private void generateWelcomePanel() {
        this.welcomeLayout.setMargin(new MarginInfo(false, true, true, false));
        this.welcomeLayout.setSpacing(true);
        Panel loginPanel = new Panel();
        VerticalLayout loginPanelLayout = new VerticalLayout();
        loginPanel.setContent(loginPanelLayout);
        LoginForm loginForm = new LoginForm();
        loginForm.addLoginListener(new LoginListener() {

            @Override
            public void onLogin(LoginEvent event) {
                try {
                    checkRunningPermission();
                    try {
                        String username = event.getLoginParameter("username");
                        String password = event.getLoginParameter("password");
                        UserAccess access = new UserAccess(Logger.getAnonymousLogger());
                        User foundUser = access.readUser(username);
                        if (foundUser == null
                                || foundUser.getPasswdCrypted() == null
                                || !PBKDF2.validatePassword(password, foundUser.getPasswdCrypted())
                                || !foundUser.getPermission(1).equals("FULL")) {
                            new Notification("Login failed",
                                    "Wrong credentials or no permission to access the system",
                                    Notification.Type.WARNING_MESSAGE, true)
                                    .show(Page.getCurrent());
                        } else {
                            //login accepted
                            AS2WebUI.this.user = foundUser;
                            AS2WebUI.this.labelUsername.setCaption(generateImageLabelHTMLTextWithPadding(RESOURCE_IMAGE_USER, 24, 24, -7)
                                    + AS2WebUI.this.user.getName());
                            AS2WebUI.this.mainWindowLayout.removeComponent(AS2WebUI.this.welcomeLayout);
                            AS2WebUI.this.mainWindowLayout.addComponentsAndExpand(AS2WebUI.this.mainPanel);
                            AS2WebUI.this.mainWindowLayout.setExpandRatio(AS2WebUI.this.mainPanel, 1f);
                            AS2WebUI.this.refreshOverviewTableData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Notification("Service not available",
                                "Login currently not possible - please try later",
                                Notification.Type.ERROR_MESSAGE, true)
                                .show(Page.getCurrent());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Notification("Fatal", e.getMessage(),
                            Notification.Type.ERROR_MESSAGE, true)
                            .show(Page.getCurrent());
                }
            }
        });
        loginPanelLayout.addComponent(loginForm);
        this.welcomeLayout.addComponent(loginPanel, 0, 0);
        RichTextArea welcomeTextArea = this.loadWelcomeTextComponent();
        if (welcomeTextArea != null) {
            Panel welcomePanel = new Panel();
            VerticalLayout welcomePanelLayout = new VerticalLayout();
            welcomePanelLayout.addComponent(welcomeTextArea);
            welcomePanel.setContent(welcomePanelLayout);
            welcomePanel.setWidth(1024, Unit.PIXELS);
            this.welcomeLayout.addComponent(welcomePanel, 1, 0, 1, 1);
            this.welcomeLayout.setComponentAlignment(welcomePanel, Alignment.TOP_RIGHT);
        }
        this.welcomeLayout.setComponentAlignment(loginPanel, Alignment.TOP_LEFT);
        this.welcomeLayout.setHeightFull();
    }

    /**
     * Loads a user specific text to display in the welcome screen or null if
     * the configuration files does not exist
     *
     * @return null if no configuration file exists
     */
    private RichTextArea loadWelcomeTextComponent() {
        Path welcomeFile = Paths.get("webas2_welcome.html");
        RichTextArea area = new RichTextArea();
        area.setSizeFull();
        String welcomeText = "";
        if (Files.exists(welcomeFile)) {
            if (Files.isDirectory(welcomeFile)) {
                welcomeText = "Warning: The configuration file " + welcomeFile.toAbsolutePath() + " is a directory. Should be a file.";
            } else if (!Files.isReadable(welcomeFile)) {
                welcomeText = "Warning: The configuration file " + welcomeFile.toAbsolutePath() + " could no be read by the current user.";
            } else {
                try {
                    welcomeText = new String(Files.readAllBytes(welcomeFile));
                } catch (Exception e) {
                    welcomeText = "Warning: Unable to process the configuration file " + welcomeFile.toAbsolutePath()
                            + " [" + e.getClass().getSimpleName() + "] " + e.getMessage();
                }
            }
        } else {
            return (null);
        }
        //add some inner padding the easy way..
        welcomeText = "<div style=\"padding: 10px\">" + welcomeText + "</div>";
        area.setValue(welcomeText);
        area.setReadOnly(true);
        return (area);
    }

    private final class GridOverviewRow {

        private final AS2MessageInfo messageInfo;
        private final Map<String, Partner> partnerMap;
        private final AS2Message message;

        public GridOverviewRow(AS2Message message, AS2MessageInfo messageInfo, Map<String, Partner> partnerMap) {
            this.messageInfo = messageInfo;
            this.partnerMap = partnerMap;
            this.message = message;
        }

        public Label getDirectionIcon() {
            FileResource directionIcon = null;
            if (this.messageInfo.getDirection() == AS2MessageInfo.DIRECTION_IN) {
                directionIcon = RESOURCE_IMAGE_IN;
            } else {
                directionIcon = RESOURCE_IMAGE_OUT;
            }
            return (generateImageLabel(directionIcon));
        }

        public Label getStateIcon() {
            FileResource stateIcon = RESOURCE_IMAGE_PENDING;
            if (this.messageInfo.getState() == AS2Message.STATE_FINISHED) {
                stateIcon = RESOURCE_IMAGE_FINISHED;
            } else if (this.messageInfo.getState() == AS2Message.STATE_STOPPED) {
                stateIcon = RESOURCE_IMAGE_STOPPED;
            }
            return (generateImageLabel(stateIcon));
        }

        public Date getInitDate() {
            if (AS2WebUI.this.buttonGroupTimezone.getSelectedItem().get().equals(AS2WebUI.this.buttonLabelTimezoneStrServer)) {
                return (this.messageInfo.getInitDate());
            } else {
                Instant localInstant = this.messageInfo.getInitDate().toInstant().plusSeconds(timezoneOffsetInS);
                return (new Date(localInstant.toEpochMilli()));
            }
        }

        public String getLocalStation() {
            String localStationStr = null;
            if (this.messageInfo.getDirection() != AS2MessageInfo.DIRECTION_IN) {
                String id = messageInfo.getSenderId();
                Partner sender = this.partnerMap.get(id);
                if (sender != null) {
                    localStationStr = sender.getName();
                } else {
                    localStationStr = id;
                }
            } else {
                String id = this.messageInfo.getReceiverId();
                Partner receiver = this.partnerMap.get(id);
                if (receiver != null) {
                    localStationStr = receiver.getName();
                } else {
                    localStationStr = id;
                }
            }
            //prevent JavaScript
            localStationStr = AS2WebUI.replaceJavaScriptOutput(localStationStr);
            return (localStationStr);
        }

        public String getPartner() {
            String partnerStr = null;
            if (this.messageInfo.getDirection() == AS2MessageInfo.DIRECTION_OUT) {
                String id = this.messageInfo.getReceiverId();
                Partner receiver = this.partnerMap.get(id);
                if (receiver != null) {
                    partnerStr = receiver.getName();
                } else {
                    partnerStr = id;
                }
            } else {
                String id = this.messageInfo.getSenderId();
                Partner sender = this.partnerMap.get(id);
                if (sender != null) {
                    partnerStr = sender.getName();
                } else {
                    partnerStr = id;
                }
            }
            //prevent JavaScript
            partnerStr = AS2WebUI.replaceJavaScriptOutput(partnerStr);
            return (partnerStr);
        }

        public String getMessageId() {
            String messageId = this.messageInfo.getMessageId();
            //prevent JavaScript
            messageId = AS2WebUI.replaceJavaScriptOutput(messageId);
            return (messageId);
        }

        public String getPayload() {
            String payload = null;
            if (this.message.getPayloadCount() == 0
                    || (this.message.getPayloadCount() == 1 && message.getPayload(0).getOriginalFilename() == null)) {
                payload = "--";
            } else if (this.message.getPayloadCount() == 1) {
                payload = this.message.getPayload(0).getOriginalFilename();
            } else {
                payload = "Number of attachments: " + String.valueOf(message.getPayloadCount());
            }
            return (payload);
        }

        public String getEncryption() {
            return (rbMessage.getResourceString("encryption." + this.messageInfo.getEncryptionType()));
        }

        public String getSignature() {
            return (rbMessage.getResourceString("signature." + messageInfo.getSignType()));
        }

        public String getSyncAsync() {
            return (this.messageInfo.requestsSyncMDN() ? "SYNC" : "ASYNC");
        }

        public AS2MessageInfo getMessageInfo() {
            return (this.messageInfo);
        }

    }

    /**
     * Entry for the filter combo boxes
     */
    private final class PartnerEntry {

        private Partner partner;

        public PartnerEntry(Partner partner) {
            this.partner = partner;
        }

        public String toDisplay() {
            if (this.partner == null) {
                return ("[ All ]");
            }
            return (this.partner.getName());
        }

        public Partner getPartner() {
            return (this.partner);
        }
    }

}
