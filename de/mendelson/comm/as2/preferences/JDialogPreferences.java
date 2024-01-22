//$Header: /as2/de/mendelson/comm/as2/preferences/JDialogPreferences.java 48    5/10/22 10:10 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.ImageButtonBar;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.uinotification.UINotification;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dialog to configure a single partner
 *
 * @author S.Heller
 * @version $Revision: 48 $
 */
public class JDialogPreferences extends JDialog {

    public static final int IMAGE_HEIGHT = 28;

    private final static MendelsonMultiResolutionImage IMAGE_LANGUAGE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/language.svg", IMAGE_HEIGHT,
                    IMAGE_HEIGHT * 2);
    private final static MendelsonMultiResolutionImage IMAGE_COLORBLIND
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/color_blindness.svg", 20,
                    36, MendelsonMultiResolutionImage.SVGScalingOption.KEEP_HEIGHT);
    private final static MendelsonMultiResolutionImage IMAGE_DARKMODE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/darkmode.svg", 20);
    private final static MendelsonMultiResolutionImage IMAGE_LIGHTMODE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/lightmode.svg", 20);

    /**
     * ResourceBundle to localize the GUI
     */
    private MecResourceBundle rb = null;
    /**
     * The language should be stored in the client preferences, no client-server
     * comm required here
     */
    private PreferencesAS2 clientPreferences = new PreferencesAS2();
    /**
     * stores all available panels
     */
    private final List<PreferencesPanel> panelList = new ArrayList<PreferencesPanel>();
    private ImageButtonBar buttonBar;

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param parameter Parameter to edit, null for a new one
     * @param parameterList List of available parameter
     * @param activatedPlugins A String containing all activated plugins of the system
     */
    public JDialogPreferences(JFrame parent, List<PreferencesPanel> panelList, String selectedTab, String activatedPlugins) {
        super(parent, true);
        this.panelList.addAll( panelList );
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        initComponents();
        this.setMultiresolutionIcons();
        this.setupCountrySelection();
        this.setDarkModeRadio();
        ColorUtil.autoCorrectForegroundColor(this.jLabelLanguageInfo);
        if (this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("de")) {
            this.jRadioButtonLangDE.setSelected(true);
        } else if (this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("en")) {
            this.jRadioButtonLangEN.setSelected(true);
        } else if (this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("fr")) {
            this.jRadioButtonLangFR.setSelected(true);
        }
        String selectedCountryCode = this.clientPreferences.get(PreferencesAS2.COUNTRY).toUpperCase();
        this.jListCountry.setSelectedValue(new DisplayCountry(selectedCountryCode), true);
        boolean colorBlindness = this.clientPreferences.getBoolean(PreferencesAS2.COLOR_BLINDNESS);
        this.jCheckBoxColorBlindness.setSelected(colorBlindness);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        for (PreferencesPanel preferencePanel : this.panelList) {
            //initialize the panels
            preferencePanel.setActivatedPlugins(activatedPlugins);
            preferencePanel.loadPreferences();            
            //add the panels to the layout
            this.jPanelEdit.add(preferencePanel, gridBagConstraints);
        }
        this.buttonBar = new ImageButtonBar(ImageButtonBar.HORIZONTAL)
                .setPreferredButtonSize(85, 84);
        boolean selected = selectedTab == null;
        for (PreferencesPanel preferencePanel : this.panelList) {
            if (selectedTab != null && preferencePanel.getTabResource().equals(selectedTab)) {
                selected = true;
            }
            buttonBar.addButton(
                    preferencePanel.getIcon(),
                    this.rb.getResourceString(preferencePanel.getTabResource()),
                    new JComponent[]{preferencePanel},
                    selected);
            selected = false;
        }
        buttonBar.addButton(
                new ImageIcon(IMAGE_LANGUAGE.toMinResolution(IMAGE_HEIGHT)),
                this.rb.getResourceString("tab.language"),
                new JComponent[]{this.jPanelLanguage},
                false)
                .build();
        //add button bar
        this.jPanelButtonBar.setLayout(new BorderLayout());
        this.jPanelButtonBar.add(buttonBar, BorderLayout.CENTER);
        this.getRootPane().setDefaultButton(this.jButtonOk);

    }

    private void setMultiresolutionIcons() {
        this.jLabelIconBlind.setIcon(new ImageIcon(IMAGE_COLORBLIND.toMinResolution(20)));
        this.jLabelDarkMode.setIcon(new ImageIcon(IMAGE_DARKMODE.toMinResolution(20)));
        this.jLabelLightMode.setIcon(new ImageIcon(IMAGE_LIGHTMODE.toMinResolution(20)));
    }
    
    private void captureGUIValues() {
        boolean clientRestartRequired = false;
        if (this.jRadioButtonLangDE.isSelected()) {
            if (!this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("de")) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.LANGUAGE, "de");
        } else if (this.jRadioButtonLangEN.isSelected()) {
            if (!this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("en")) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.LANGUAGE, "en");
        } else if (this.jRadioButtonLangFR.isSelected()) {
            if (!this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("fr")) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.LANGUAGE, "fr");
        }
        if (this.jListCountry.getSelectedValue() != null) {
            String newCountryCode = this.jListCountry.getSelectedValue().getCountryCode();
            if (!this.clientPreferences.get(PreferencesAS2.COUNTRY).equals(newCountryCode)) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.COUNTRY, newCountryCode);
        }
        if (this.clientPreferences.getBoolean(PreferencesAS2.COLOR_BLINDNESS) != (this.jCheckBoxColorBlindness.isSelected())) {
            clientRestartRequired = true;
        }
        this.clientPreferences.putBoolean(PreferencesAS2.COLOR_BLINDNESS, this.jCheckBoxColorBlindness.isSelected());
        if ((this.jRadioButtonDarkMode.isSelected()
                && !this.clientPreferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT).equalsIgnoreCase("DARK"))
                || (this.jRadioButtonLiteMode.isSelected()
                && this.clientPreferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT).equalsIgnoreCase("DARK"))) {
            this.clientPreferences.put(PreferencesAS2.DISPLAY_MODE_CLIENT,
                    this.jRadioButtonDarkMode.isSelected() ? "DARK" : "LIGHT");
            clientRestartRequired = true;
        }
        if (clientRestartRequired) {
            UINotification.instance().addNotification(
                    PreferencesPanelMDN.IMAGE_PREFS,
                    UINotification.TYPE_INFORMATION,
                    this.rb.getResourceString("title"),
                    this.rb.getResourceString("warning.clientrestart.required"));
        }

    }

    /**
     * Fills in the available countries of the system into the list
     */
    private void setupCountrySelection() {
        Set<String> countryCodes = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2);
        DefaultListModel listModel = (DefaultListModel) this.jListCountry.getModel();
        listModel.clear();
        List<DisplayCountry> displayList = new ArrayList<DisplayCountry>();
        for (String countryCode : countryCodes) {
            displayList.add(new DisplayCountry(countryCode));
        }
        //sort german special chars the right way if the locale is german...
        Collections.sort(displayList);
        DisplayCountry[] countryArray = new DisplayCountry[displayList.size()];
        displayList.toArray(countryArray);
        this.jListCountry.setListData(countryArray);
    }

    private void setDarkModeRadio() {
        String displayMode = this.clientPreferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT);
        if (displayMode.equalsIgnoreCase("DARK")) {
            this.jRadioButtonDarkMode.setSelected(true);
        } else {
            this.jRadioButtonLiteMode.setSelected(true);
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

        buttonGroupLanguage = new javax.swing.ButtonGroup();
        buttonGroupDarkMode = new javax.swing.ButtonGroup();
        jPanelEdit = new javax.swing.JPanel();
        jPanelLanguage = new javax.swing.JPanel();
        jRadioButtonLangDE = new javax.swing.JRadioButton();
        jRadioButtonLangEN = new javax.swing.JRadioButton();
        jRadioButtonLangFR = new javax.swing.JRadioButton();
        jPanelSpace = new javax.swing.JPanel();
        jLabelLanguageInfo = new javax.swing.JLabel();
        jScrollPaneCountry = new javax.swing.JScrollPane();
        jListCountry = new javax.swing.JList<>();
        jPanelSpace44 = new javax.swing.JPanel();
        jCheckBoxColorBlindness = new javax.swing.JCheckBox();
        jPanelColorBlindness = new javax.swing.JPanel();
        jLabelIconBlind = new javax.swing.JLabel();
        jPanelDarkMode = new javax.swing.JPanel();
        jRadioButtonDarkMode = new javax.swing.JRadioButton();
        jRadioButtonLiteMode = new javax.swing.JRadioButton();
        jLabelDarkMode = new javax.swing.JLabel();
        jLabelLightMode = new javax.swing.JLabel();
        jPanelUIHelpLabel1 = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabel2 = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jPanelButtonBar = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(this.rb.getResourceString( "title"));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jPanelLanguage.setLayout(new java.awt.GridBagLayout());

        buttonGroupLanguage.add(jRadioButtonLangDE);
        jRadioButtonLangDE.setText("Deutsch");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 5, 5);
        jPanelLanguage.add(jRadioButtonLangDE, gridBagConstraints);

        buttonGroupLanguage.add(jRadioButtonLangEN);
        jRadioButtonLangEN.setText("English");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelLanguage.add(jRadioButtonLangEN, gridBagConstraints);

        buttonGroupLanguage.add(jRadioButtonLangFR);
        jRadioButtonLangFR.setText("Français");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelLanguage.add(jRadioButtonLangFR, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelLanguage.add(jPanelSpace, gridBagConstraints);

        jLabelLanguageInfo.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabelLanguageInfo.setForeground(new java.awt.Color(255, 51, 0));
        jLabelLanguageInfo.setText(this.rb.getResourceString("info.restart.client"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        jPanelLanguage.add(jLabelLanguageInfo, gridBagConstraints);

        jScrollPaneCountry.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPaneCountry.setMinimumSize(new java.awt.Dimension(275, 242));

        jListCountry.setModel(new DefaultListModel());
        jListCountry.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListCountry.setVisibleRowCount(15);
        jScrollPaneCountry.setViewportView(jListCountry);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 5, 5);
        jPanelLanguage.add(jScrollPaneCountry, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelLanguage.add(jPanelSpace44, gridBagConstraints);

        jCheckBoxColorBlindness.setText(this.rb.getResourceString( "label.colorblindness"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelLanguage.add(jCheckBoxColorBlindness, gridBagConstraints);

        jPanelColorBlindness.setLayout(new java.awt.GridBagLayout());

        jLabelIconBlind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelColorBlindness.add(jLabelIconBlind, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelLanguage.add(jPanelColorBlindness, gridBagConstraints);

        jPanelDarkMode.setLayout(new java.awt.GridBagLayout());

        buttonGroupDarkMode.add(jRadioButtonDarkMode);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelDarkMode.add(jRadioButtonDarkMode, gridBagConstraints);

        buttonGroupDarkMode.add(jRadioButtonLiteMode);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanelDarkMode.add(jRadioButtonLiteMode, gridBagConstraints);

        jLabelDarkMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jLabelDarkMode.setText(this.rb.getResourceString( "label.darkmode"));
        jLabelDarkMode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelDarkModeMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 20);
        jPanelDarkMode.add(jLabelDarkMode, gridBagConstraints);

        jLabelLightMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jLabelLightMode.setText(this.rb.getResourceString( "label.litemode"));
        jLabelLightMode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelLightModeMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDarkMode.add(jLabelLightMode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 20, 5);
        jPanelLanguage.add(jPanelDarkMode, gridBagConstraints);

        jPanelUIHelpLabel1.setToolTipText(this.rb.getResourceString( "label.country.help"));
        jPanelUIHelpLabel1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanelUIHelpLabel1.setText(this.rb.getResourceString( "label.country"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 45, 5, 0);
        jPanelLanguage.add(jPanelUIHelpLabel1, gridBagConstraints);

        jPanelUIHelpLabel2.setToolTipText(this.rb.getResourceString( "label.language.help"));
        jPanelUIHelpLabel2.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanelUIHelpLabel2.setText(this.rb.getResourceString( "label.language"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 0);
        jPanelLanguage.add(jPanelUIHelpLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelEdit.add(jPanelLanguage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtonBar, gridBagConstraints);

        setSize(new java.awt.Dimension(1084, 691));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        for (PreferencesPanel panel : this.panelList) {
            panel.savePreferences();
        }
        this.setVisible(false);
        this.captureGUIValues();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jLabelDarkModeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelDarkModeMouseClicked
        this.jRadioButtonDarkMode.setSelected(true);
    }//GEN-LAST:event_jLabelDarkModeMouseClicked

    private void jLabelLightModeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelLightModeMouseClicked
        this.jRadioButtonLiteMode.setSelected(true);
    }//GEN-LAST:event_jLabelLightModeMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupDarkMode;
    private javax.swing.ButtonGroup buttonGroupLanguage;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JCheckBox jCheckBoxColorBlindness;
    private javax.swing.JLabel jLabelDarkMode;
    private javax.swing.JLabel jLabelIconBlind;
    private javax.swing.JLabel jLabelLanguageInfo;
    private javax.swing.JLabel jLabelLightMode;
    private javax.swing.JList<DisplayCountry> jListCountry;
    private javax.swing.JPanel jPanelButtonBar;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelColorBlindness;
    private javax.swing.JPanel jPanelDarkMode;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelLanguage;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace44;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabel1;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabel2;
    private javax.swing.JRadioButton jRadioButtonDarkMode;
    private javax.swing.JRadioButton jRadioButtonLangDE;
    private javax.swing.JRadioButton jRadioButtonLangEN;
    private javax.swing.JRadioButton jRadioButtonLangFR;
    private javax.swing.JRadioButton jRadioButtonLiteMode;
    private javax.swing.JScrollPane jScrollPaneCountry;
    // End of variables declaration//GEN-END:variables

    private static class DisplayCountry implements Comparable<DisplayCountry> {

        private String countryCode;
        private String displayString;

        public DisplayCountry(String countryCode) {
            this.countryCode = countryCode.toUpperCase();
            Locale locale = new Locale(Locale.getDefault().getLanguage(), countryCode);
            this.displayString = locale.getDisplayCountry() + " (" + countryCode + ")";
        }

        @Override
        public String toString() {
            return (this.displayString);
        }

        @Override
        public boolean equals(Object anObject) {
            if (anObject == this) {
                return (true);
            }
            if (anObject != null && anObject instanceof DisplayCountry) {
                DisplayCountry entry = (DisplayCountry) anObject;
                return (entry.getCountryCode().equals(this.getCountryCode()));
            }
            return (false);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + Objects.hashCode(this.getCountryCode());
            return hash;
        }

        @Override
        public int compareTo(DisplayCountry displayCountry) {
            Collator collator = Collator.getInstance(Locale.getDefault());
            //include french and german special chars into the sort mechanism
            return (collator.compare(this.displayString, displayCountry.displayString));
        }

        /**
         * @return the countryCode
         */
        public String getCountryCode() {
            return countryCode;
        }
    }
}
