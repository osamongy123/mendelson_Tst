//$Header: /as2/de/mendelson/util/systemevents/gui/JDialogSystemEvents.java 34    5/01/23 12:24 Heller $
package de.mendelson.util.systemevents.gui;

import com.toedter.calendar.JDateChooser;
import de.mendelson.util.DateChooserUI;
import de.mendelson.util.IStatusBar;
import de.mendelson.util.LockingGlassPane;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.systemevents.ResourceBundleSystemEvent;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.clientserver.SystemEventSearchRequest;
import de.mendelson.util.systemevents.clientserver.SystemEventSearchResponse;
import de.mendelson.util.systemevents.search.ServerSideEventFilter;
import de.mendelson.util.tables.TableCellRendererDate;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
public class JDialogSystemEvents extends JDialog implements ListSelectionListener {

    private static final MendelsonMultiResolutionImage IMAGE_MAGNIFYING_GLASS
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/systemevents/gui/magnifying_glass.svg", 24);
    private static final MendelsonMultiResolutionImage IMAGE_RESET_FILTER
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/systemevents/gui/refresh.svg", 24);
    private final BaseClient baseClient;
    private Date currentStartDate = new Date();
    private Date currentEndDate = new Date();
    private final MecResourceBundle rb;
    private final MecResourceBundle rbSystemEvent;
    private final IStatusBar statusBar;

    /**
     * Creates new form JDialogSystemEvents
     */
    public JDialogSystemEvents(JFrame parent, BaseClient baseClient, IStatusBar statusBar) {
        super(parent, true);
        //Load resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogSystemEvent.class.getName());
            this.rbSystemEvent = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleSystemEvent.class.getName());
        } //load up  resourcebundle        
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.statusBar = statusBar;
        this.baseClient = baseClient;
        initComponents();
        TextOverlay.addTo(this.jTextFieldFreeTextSearch,
                this.rb.getResourceString("label.freetext.hint"));
        this.setTitle(this.rb.getResourceString("title"));
        this.jTableSystemEvents.setRowHeight(TableModelSystemEvents.ROW_HEIGHT);
        this.jTableSystemEvents.getSelectionModel().addListSelectionListener(this);
        this.jTableSystemEvents.getTableHeader().setReorderingAllowed(false);
        //just put the country information into the format locale
        DateFormat localizedDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        this.jTableSystemEvents.setDefaultRenderer(Date.class, new TableCellRendererDate(localizedDateFormat));
        this.jTableSystemEvents.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //icon columns
        TableColumn column = this.jTableSystemEvents.getColumnModel().getColumn(0);
        column.setMaxWidth(TableModelSystemEvents.ROW_HEIGHT + this.jTableSystemEvents.getRowMargin() * 2);
        column.setResizable(false);
        column = this.jTableSystemEvents.getColumnModel().getColumn(1);
        column.setMaxWidth(TableModelSystemEvents.ROW_HEIGHT + this.jTableSystemEvents.getRowMargin() * 2);
        column.setResizable(false);
        this.setupDateChooser();
        this.setMultiresolutionIcons();
        //setup localized event label
        this.jLabelSeverityError.setText(this.rbSystemEvent.getResourceString("severity." + SystemEvent.SEVERITY_ERROR));
        this.jLabelSeverityWarning.setText(this.rbSystemEvent.getResourceString("severity." + SystemEvent.SEVERITY_WARNING));
        this.jLabelSeverityInfo.setText(this.rbSystemEvent.getResourceString("severity." + SystemEvent.SEVERITY_INFO));
        this.jLabelOriginSystem.setText(this.rbSystemEvent.getResourceString("origin." + SystemEvent.ORIGIN_SYSTEM));
        this.jLabelOriginTransaction.setText(this.rbSystemEvent.getResourceString("origin." + SystemEvent.ORIGIN_TRANSACTION));
        this.jLabelOriginUser.setText(this.rbSystemEvent.getResourceString("origin." + SystemEvent.ORIGIN_USER));
        List<UIEventCategory> categoryList = UIEventCategory.getAllSorted();
        // +1 because there is an "all" entry
        this.jComboBoxCategory.setMaximumRowCount(categoryList.size()+1);
        this.jComboBoxCategory.addItem(this.rb.getResourceString("category.all"));
        for (UIEventCategory category : categoryList) {
            this.jComboBoxCategory.addItem(category);
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
        this.getRootPane().setDefaultButton(this.jButtonSearch);
        this.jButtonSearch.requestFocusInWindow();
    }

    private void setMultiresolutionIcons() {
        this.jButtonSearch.setIcon(new ImageIcon(IMAGE_MAGNIFYING_GLASS.toMinResolution(24)));
        this.jButtonResetFilter.setIcon(new ImageIcon(IMAGE_RESET_FILTER.toMinResolution(24)));
        this.jLabelSeverityError.setIcon(new ImageIcon(SystemEvent.ICON_SEVERITY_ERROR_MULTIRESOLUTION.toMinResolution(18)));
        this.jLabelSeverityInfo.setIcon(new ImageIcon(SystemEvent.ICON_SEVERITY_INFO_MULTIRESOLUTION.toMinResolution(18)));
        this.jLabelSeverityWarning.setIcon(new ImageIcon(SystemEvent.ICON_SEVERITY_WARNING_MULTIRESOLUTION.toMinResolution(18)));
        this.jLabelOriginSystem.setIcon(new ImageIcon(SystemEvent.ICON_ORIGIN_SYSTEM_MULTIRESOLUTION.toMinResolution(18)));
        this.jLabelOriginTransaction.setIcon(new ImageIcon(SystemEvent.ICON_ORIGIN_TRANSACTION_MULTIRESOLUTION.toMinResolution(18)));
        this.jLabelOriginUser.setIcon(new ImageIcon(SystemEvent.ICON_ORIGIN_USER_MULTIRESOLUTION.toMinResolution(18)));
    }

    /**
     * Resets the filter to the default values
     */
    private void resetFilter() {
        this.currentStartDate = new Date();
        this.jDateChooserStartDate.setDate(this.currentStartDate);
        this.currentEndDate = new Date();
        this.jDateChooserEndDate.setDate(this.currentEndDate);
        this.jCheckBoxOriginSystem.setSelected(true);
        this.jCheckBoxOriginTransaction.setSelected(true);
        this.jCheckBoxOriginUser.setSelected(true);
        this.jCheckBoxSeverityError.setSelected(true);
        this.jCheckBoxSeverityInfo.setSelected(true);
        this.jCheckBoxSeverityWarning.setSelected(true);
        this.jTextFieldFreeTextSearch.setText("");
        this.jComboBoxCategory.setSelectedIndex(0);
    }

    /**
     * Defines the date chooser and the used colors
     */
    private void setupDateChooser() {
        this.jDateChooserStartDate.setUI(new DateChooserUI());
        this.jDateChooserStartDate.setLocale(Locale.getDefault());
        this.jDateChooserStartDate.setDate(this.currentStartDate);
        this.jDateChooserStartDate.getDateEditor().addPropertyChangeListener(
                new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName() != null && e.getPropertyName().startsWith("date")) {
                    currentStartDate = jDateChooserStartDate.getDate();
                }
            }
        });
        this.jDateChooserEndDate.setUI(new DateChooserUI());
        this.jDateChooserEndDate.setLocale(Locale.getDefault());
        this.jDateChooserEndDate.setDate(this.currentEndDate);
        this.jDateChooserEndDate.getDateEditor().addPropertyChangeListener(
                new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName() != null && e.getPropertyName().startsWith("date")) {
                    currentEndDate = jDateChooserEndDate.getDate();
                }
            }
        });
    }

    @Override
    public void setVisible(boolean visibleFlag) {
        if (visibleFlag) {
            //the date chose does not recognize daily date change..
            //reinitialize it if this dialog becomes visible
            GridBagLayout layout = (GridBagLayout) this.jPanelDateFilter.getLayout();
            GridBagConstraints gridStart = layout.getConstraints(this.jDateChooserStartDate);
            GridBagConstraints gridEnd = layout.getConstraints(this.jDateChooserEndDate);
            this.jPanelDateFilter.remove(this.jDateChooserStartDate);
            this.jDateChooserStartDate = new JDateChooser(this.currentStartDate);
            this.jPanelDateFilter.add(this.jDateChooserStartDate, gridStart);
            this.jPanelDateFilter.remove(this.jDateChooserEndDate);
            this.jDateChooserEndDate = new JDateChooser(this.currentEndDate);
            this.jPanelDateFilter.add(this.jDateChooserEndDate, gridEnd);
            this.setupDateChooser();
            this.performSearch();
        }
        super.setVisible(visibleFlag);
    }

    /**
     * The user modified the filter... capture the current values
     */
    private ServerSideEventFilter generateFilterFromGUI() {
        ServerSideEventFilter eventFilter = new ServerSideEventFilter();
        eventFilter.setAcceptSeverityError(this.jCheckBoxSeverityError.isSelected());
        eventFilter.setAcceptSeverityInfo(this.jCheckBoxSeverityInfo.isSelected());
        eventFilter.setAcceptSeverityWarning(this.jCheckBoxSeverityWarning.isSelected());
        eventFilter.setAcceptOriginSystem(this.jCheckBoxOriginSystem.isSelected());
        eventFilter.setAcceptOriginTransaction(this.jCheckBoxOriginTransaction.isSelected());
        eventFilter.setAcceptOriginUser(this.jCheckBoxOriginUser.isSelected());
        Object selectedCategoryObj = this.jComboBoxCategory.getSelectedItem();
        if (selectedCategoryObj == null || selectedCategoryObj instanceof String) {
            eventFilter.setAcceptCategory(-1);
        } else {
            UIEventCategory selectedCategory = (UIEventCategory) selectedCategoryObj;
            eventFilter.setAcceptCategory(selectedCategory.getCategoryValue());
        }
        eventFilter.setSearchEventid(this.jTextFieldFreeTextSearch.getText());
        eventFilter.setBodySearchText(this.jTextFieldFreeTextSearch.getText());
        eventFilter.setSubjectSearchText(this.jTextFieldFreeTextSearch.getText());
        eventFilter.setStartDate(this.jDateChooserStartDate.getDate().getTime());
        eventFilter.setEndDate(this.jDateChooserEndDate.getDate().getTime());
        return (eventFilter);
    }

    /**
     * No event for the selected day or no selection
     */
    private void displayNoSelection() {
        this.jPanelDisplaySingleSystemEventTodaysEvents.displayNoSelection();
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
     * Unlock the component: remove the glasspane that prevents any action on
     * the UI
     */
    private void unlock() {
        getGlassPane().setVisible(false);
    }

    /**
     * Perform a search on the server for events - by showing the search dialog
     * first and performing the action afterwards
     */
    private synchronized void performSearch() {
        final String uniqueId = this.getClass().getName() + ".performSearch." + System.currentTimeMillis();
        final SystemEventSearchRequest request = new SystemEventSearchRequest(this.generateFilterFromGUI());
        this.displayNoSelection();
        //this could take some time as indicies might be first created
        final long SEARCH_TIMEOUT = TimeUnit.MINUTES.toMillis(1);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JDialogSystemEvents.this.lock();
                //display wait indicator
                JDialogSystemEvents.this.statusBar.startProgressIndeterminate(rb.getResourceString("label.search"), uniqueId);
                try {
                    SystemEventSearchResponse response = (SystemEventSearchResponse) baseClient.sendSync(request, SEARCH_TIMEOUT);
                    List<SystemEvent> resultList = response.getSearchResults();
                    ((TableModelSystemEvents) jTableSystemEvents.getModel()).passNewData(resultList);
                    jPanelEmptyTable.setVisible(resultList.isEmpty());
                    if (!resultList.isEmpty()) {
                        jTableSystemEvents.getSelectionModel().setSelectionInterval(resultList.size() - 1, resultList.size() - 1);
                        scrollScrollPane(jScrollPaneTableEvents, SwingUtilities.BOTTOM);
                    }
                } catch (Throwable e) {
                } finally {
                    JDialogSystemEvents.this.unlock();
                    JDialogSystemEvents.this.statusBar.stopProgressIfExists(uniqueId);
                }
            }
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(runnable);
        executor.shutdown();
    }

    /**
     * Scrolls a passed scroll pane to a requested position
     */
    private void scrollScrollPane(JScrollPane scrollpane, int vertical) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                switch (vertical) {
                    case SwingConstants.TOP:
                        scrollpane.getVerticalScrollBar().setValue(0);
                        break;
                    case SwingConstants.CENTER:
                        scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
                        scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getValue() / 2);
                        break;
                    case SwingConstants.BOTTOM:
                        scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
                        break;
                }
            }
        });
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

        jPanelButton = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jPanelTodaysEvents = new javax.swing.JPanel();
        jPanelFilterEvents = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelDateFilter = new javax.swing.JPanel();
        jLabelStartDate = new javax.swing.JLabel();
        jDateChooserStartDate = new com.toedter.calendar.JDateChooser();
        jLabelEndDate = new javax.swing.JLabel();
        jDateChooserEndDate = new com.toedter.calendar.JDateChooser();
        jPanelSearchTextCategory = new javax.swing.JPanel();
        jLabelCategory = new javax.swing.JLabel();
        jLabelFreeText = new javax.swing.JLabel();
        jTextFieldFreeTextSearch = new javax.swing.JTextField();
        jComboBoxCategory = new javax.swing.JComboBox<>();
        jPanelButtons = new javax.swing.JPanel();
        jButtonResetFilter = new javax.swing.JButton();
        jButtonSearch = new javax.swing.JButton();
        jPanelSeverity = new javax.swing.JPanel();
        jLabelSeverityError = new javax.swing.JLabel();
        jLabelSeverityWarning = new javax.swing.JLabel();
        jLabelSeverityInfo = new javax.swing.JLabel();
        jCheckBoxSeverityInfo = new javax.swing.JCheckBox();
        jCheckBoxSeverityWarning = new javax.swing.JCheckBox();
        jCheckBoxSeverityError = new javax.swing.JCheckBox();
        jPanelOrigin = new javax.swing.JPanel();
        jCheckBoxOriginSystem = new javax.swing.JCheckBox();
        jLabelOriginSystem = new javax.swing.JLabel();
        jCheckBoxOriginUser = new javax.swing.JCheckBox();
        jLabelOriginUser = new javax.swing.JLabel();
        jCheckBoxOriginTransaction = new javax.swing.JCheckBox();
        jLabelOriginTransaction = new javax.swing.JLabel();
        jPanelMainEvents = new javax.swing.JPanel();
        jSplitPaneEvents = new javax.swing.JSplitPane();
        jPanelUpper = new javax.swing.JPanel();
        jPanelEmptyTable = new javax.swing.JPanel();
        jLabelEmptyTable = new javax.swing.JLabel();
        jScrollPaneTableEvents = new javax.swing.JScrollPane();
        jTableSystemEvents = new javax.swing.JTable();
        jPanelLower = new javax.swing.JPanel();
        jPanelDisplaySingleSystemEventTodaysEvents = new de.mendelson.util.systemevents.gui.JPanelDisplaySingleSystemEvent();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelButton.setLayout(new java.awt.GridBagLayout());

        jButtonClose.setText(this.rb.getResourceString( "label.close")
        );
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButton.add(jButtonClose, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelButton, gridBagConstraints);

        jPanelTodaysEvents.setLayout(new java.awt.GridBagLayout());

        jPanelFilterEvents.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelFilterEvents.setMinimumSize(new java.awt.Dimension(563, 145));
        jPanelFilterEvents.setPreferredSize(new java.awt.Dimension(658, 145));
        jPanelFilterEvents.setLayout(new java.awt.GridBagLayout());

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 15);
        jPanelFilterEvents.add(jSeparator1, gridBagConstraints);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 15);
        jPanelFilterEvents.add(jSeparator2, gridBagConstraints);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 15);
        jPanelFilterEvents.add(jSeparator3, gridBagConstraints);

        jPanelDateFilter.setMinimumSize(new java.awt.Dimension(206, 80));
        jPanelDateFilter.setPreferredSize(new java.awt.Dimension(220, 80));
        jPanelDateFilter.setLayout(new java.awt.GridBagLayout());

        jLabelStartDate.setText(this.rb.getResourceString( "label.startdate"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanelDateFilter.add(jLabelStartDate, gridBagConstraints);

        jDateChooserStartDate.setMinimumSize(new java.awt.Dimension(120, 20));
        jDateChooserStartDate.setPreferredSize(new java.awt.Dimension(120, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanelDateFilter.add(jDateChooserStartDate, gridBagConstraints);

        jLabelEndDate.setText(this.rb.getResourceString("label.enddate"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDateFilter.add(jLabelEndDate, gridBagConstraints);

        jDateChooserEndDate.setMinimumSize(new java.awt.Dimension(120, 20));
        jDateChooserEndDate.setPreferredSize(new java.awt.Dimension(120, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDateFilter.add(jDateChooserEndDate, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanelFilterEvents.add(jPanelDateFilter, gridBagConstraints);

        jPanelSearchTextCategory.setPreferredSize(new java.awt.Dimension(350, 100));
        jPanelSearchTextCategory.setLayout(new java.awt.GridBagLayout());

        jLabelCategory.setText(this.rb.getResourceString( "label.category"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanelSearchTextCategory.add(jLabelCategory, gridBagConstraints);

        jLabelFreeText.setText(this.rb.getResourceString( "label.freetext")
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanelSearchTextCategory.add(jLabelFreeText, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanelSearchTextCategory.add(jTextFieldFreeTextSearch, gridBagConstraints);

        jComboBoxCategory.setPreferredSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 5, 15);
        jPanelSearchTextCategory.add(jComboBoxCategory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanelFilterEvents.add(jPanelSearchTextCategory, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonResetFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/systemevents/gui/missing_image24x24.gif"))); // NOI18N
        jButtonResetFilter.setText(this.rb.getResourceString( "label.resetfilter"));
        jButtonResetFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonResetFilter.setMargin(new java.awt.Insets(5, 14, 2, 14));
        jButtonResetFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonResetFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetFilterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        jPanelButtons.add(jButtonResetFilter, gridBagConstraints);

        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/systemevents/gui/missing_image24x24.gif"))); // NOI18N
        jButtonSearch.setText(this.rb.getResourceString( "label.search"));
        jButtonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSearch.setMargin(new java.awt.Insets(5, 14, 2, 14));
        jButtonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 7, 10);
        jPanelButtons.add(jButtonSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 17;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelFilterEvents.add(jPanelButtons, gridBagConstraints);

        jPanelSeverity.setLayout(new java.awt.GridBagLayout());

        jLabelSeverityError.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/systemevents/gui/missing_image16x16.gif"))); // NOI18N
        jLabelSeverityError.setText("Error");
        jLabelSeverityError.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSeverityErrorMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        jPanelSeverity.add(jLabelSeverityError, gridBagConstraints);

        jLabelSeverityWarning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/systemevents/gui/missing_image16x16.gif"))); // NOI18N
        jLabelSeverityWarning.setText("Warning");
        jLabelSeverityWarning.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSeverityWarningMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 15);
        jPanelSeverity.add(jLabelSeverityWarning, gridBagConstraints);

        jLabelSeverityInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/systemevents/gui/missing_image16x16.gif"))); // NOI18N
        jLabelSeverityInfo.setText("Info");
        jLabelSeverityInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSeverityInfoMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 15);
        jPanelSeverity.add(jLabelSeverityInfo, gridBagConstraints);

        jCheckBoxSeverityInfo.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelSeverity.add(jCheckBoxSeverityInfo, gridBagConstraints);

        jCheckBoxSeverityWarning.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelSeverity.add(jCheckBoxSeverityWarning, gridBagConstraints);

        jCheckBoxSeverityError.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        jPanelSeverity.add(jCheckBoxSeverityError, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterEvents.add(jPanelSeverity, gridBagConstraints);

        jPanelOrigin.setLayout(new java.awt.GridBagLayout());

        jCheckBoxOriginSystem.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        jPanelOrigin.add(jCheckBoxOriginSystem, gridBagConstraints);

        jLabelOriginSystem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/systemevents/gui/missing_image16x16.gif"))); // NOI18N
        jLabelOriginSystem.setText("System");
        jLabelOriginSystem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelOriginSystemMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        jPanelOrigin.add(jLabelOriginSystem, gridBagConstraints);

        jCheckBoxOriginUser.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelOrigin.add(jCheckBoxOriginUser, gridBagConstraints);

        jLabelOriginUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/systemevents/gui/missing_image16x16.gif"))); // NOI18N
        jLabelOriginUser.setText("User");
        jLabelOriginUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelOriginUserMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 15);
        jPanelOrigin.add(jLabelOriginUser, gridBagConstraints);

        jCheckBoxOriginTransaction.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelOrigin.add(jCheckBoxOriginTransaction, gridBagConstraints);

        jLabelOriginTransaction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/systemevents/gui/missing_image16x16.gif"))); // NOI18N
        jLabelOriginTransaction.setText("Transaction");
        jLabelOriginTransaction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelOriginTransactionMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 15);
        jPanelOrigin.add(jLabelOriginTransaction, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelFilterEvents.add(jPanelOrigin, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelTodaysEvents.add(jPanelFilterEvents, gridBagConstraints);

        jPanelMainEvents.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelMainEvents.setLayout(new java.awt.GridBagLayout());

        jSplitPaneEvents.setDividerLocation(200);
        jSplitPaneEvents.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanelUpper.setLayout(new java.awt.GridBagLayout());

        jPanelEmptyTable.setLayout(new java.awt.GridBagLayout());

        jLabelEmptyTable.setText(this.rb.getResourceString( "no.data"));
        jPanelEmptyTable.add(jLabelEmptyTable, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelUpper.add(jPanelEmptyTable, gridBagConstraints);

        jTableSystemEvents.setModel(new TableModelSystemEvents());
        jTableSystemEvents.setShowHorizontalLines(false);
        jTableSystemEvents.setShowVerticalLines(false);
        jScrollPaneTableEvents.setViewportView(jTableSystemEvents);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelUpper.add(jScrollPaneTableEvents, gridBagConstraints);

        jSplitPaneEvents.setLeftComponent(jPanelUpper);

        jPanelLower.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelLower.add(jPanelDisplaySingleSystemEventTodaysEvents, gridBagConstraints);

        jSplitPaneEvents.setRightComponent(jPanelLower);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelMainEvents.add(jSplitPaneEvents, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelTodaysEvents.add(jPanelMainEvents, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelTodaysEvents, gridBagConstraints);

        setSize(new java.awt.Dimension(1213, 865));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        //do not dispose the UI as it is always available in the parent context and just switched between 
        //visible/hidden
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jLabelSeverityErrorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSeverityErrorMouseClicked
        this.jCheckBoxSeverityError.doClick();
    }//GEN-LAST:event_jLabelSeverityErrorMouseClicked

    private void jLabelSeverityWarningMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSeverityWarningMouseClicked
        this.jCheckBoxSeverityWarning.doClick();
    }//GEN-LAST:event_jLabelSeverityWarningMouseClicked

    private void jLabelSeverityInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSeverityInfoMouseClicked
        this.jCheckBoxSeverityInfo.doClick();
    }//GEN-LAST:event_jLabelSeverityInfoMouseClicked

    private void jLabelOriginSystemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelOriginSystemMouseClicked
        this.jCheckBoxOriginSystem.doClick();
    }//GEN-LAST:event_jLabelOriginSystemMouseClicked

    private void jLabelOriginUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelOriginUserMouseClicked
        this.jCheckBoxOriginUser.doClick();
    }//GEN-LAST:event_jLabelOriginUserMouseClicked

    private void jLabelOriginTransactionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelOriginTransactionMouseClicked
        this.jCheckBoxOriginTransaction.doClick();
    }//GEN-LAST:event_jLabelOriginTransactionMouseClicked

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        this.performSearch();
    }//GEN-LAST:event_jButtonSearchActionPerformed

    private void jButtonResetFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetFilterActionPerformed
        this.resetFilter();
    }//GEN-LAST:event_jButtonResetFilterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonResetFilter;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JCheckBox jCheckBoxOriginSystem;
    private javax.swing.JCheckBox jCheckBoxOriginTransaction;
    private javax.swing.JCheckBox jCheckBoxOriginUser;
    private javax.swing.JCheckBox jCheckBoxSeverityError;
    private javax.swing.JCheckBox jCheckBoxSeverityInfo;
    private javax.swing.JCheckBox jCheckBoxSeverityWarning;
    private javax.swing.JComboBox<Object> jComboBoxCategory;
    private com.toedter.calendar.JDateChooser jDateChooserEndDate;
    private com.toedter.calendar.JDateChooser jDateChooserStartDate;
    private javax.swing.JLabel jLabelCategory;
    private javax.swing.JLabel jLabelEmptyTable;
    private javax.swing.JLabel jLabelEndDate;
    private javax.swing.JLabel jLabelFreeText;
    private javax.swing.JLabel jLabelOriginSystem;
    private javax.swing.JLabel jLabelOriginTransaction;
    private javax.swing.JLabel jLabelOriginUser;
    private javax.swing.JLabel jLabelSeverityError;
    private javax.swing.JLabel jLabelSeverityInfo;
    private javax.swing.JLabel jLabelSeverityWarning;
    private javax.swing.JLabel jLabelStartDate;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelDateFilter;
    private de.mendelson.util.systemevents.gui.JPanelDisplaySingleSystemEvent jPanelDisplaySingleSystemEventTodaysEvents;
    private javax.swing.JPanel jPanelEmptyTable;
    private javax.swing.JPanel jPanelFilterEvents;
    private javax.swing.JPanel jPanelLower;
    private javax.swing.JPanel jPanelMainEvents;
    private javax.swing.JPanel jPanelOrigin;
    private javax.swing.JPanel jPanelSearchTextCategory;
    private javax.swing.JPanel jPanelSeverity;
    private javax.swing.JPanel jPanelTodaysEvents;
    private javax.swing.JPanel jPanelUpper;
    private javax.swing.JScrollPane jScrollPaneTableEvents;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSplitPane jSplitPaneEvents;
    private javax.swing.JTable jTableSystemEvents;
    private javax.swing.JTextField jTextFieldFreeTextSearch;
    // End of variables declaration//GEN-END:variables

    /**
     * Makes this a ListSelectionListener
     *
     * @param e
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedRow = this.jTableSystemEvents.getSelectedRow();
        if (selectedRow >= 0) {
            SystemEvent selectedEvent = ((TableModelSystemEvents) this.jTableSystemEvents.getModel()).getEventAt(selectedRow);
            this.jPanelDisplaySingleSystemEventTodaysEvents.displayEvent(selectedEvent);
        }
        if (this.jTableSystemEvents.getRowCount() == 0) {
            this.displayNoSelection();
        }
        this.jPanelEmptyTable.setVisible(this.jTableSystemEvents.getRowCount() == 0);
    }

}
