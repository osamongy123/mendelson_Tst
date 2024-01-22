//$Header: /mec_as2/de/mendelson/comm/as2/preferences/PreferencesPanelSystemMaintenance.java 26    2/01/23 16:52 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.util.JTextFieldLimitDocument;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.preferences.PreferencesClient;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Panel to define the inbox settings
 *
 * @author S.Heller
 * @version: $Revision: 26 $
 */
public class PreferencesPanelSystemMaintenance extends PreferencesPanel {

    private final static MendelsonMultiResolutionImage ICON_MAINTENANCE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/maintenance.svg",
                    JDialogPreferences.IMAGE_HEIGHT, JDialogPreferences.IMAGE_HEIGHT * 2);

    /**
     * Localize the GUI
     */
    private static MecResourceBundle rb = null;
    /**
     * GUI prefs
     */
    private PreferencesClient preferences;

    /**
     * Creates new form PreferencesPanelDirectories
     */
    public PreferencesPanelSystemMaintenance(BaseClient baseClient) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.initComponents();
        this.initializeHelp();
        this.preferences = new PreferencesClient(baseClient);
        if (this.preferences.getBoolean(PreferencesAS2.COMMUNITY_EDITION)) {
            this.jCheckBoxDeleteStatsOlderThan.setVisible(false);
            this.jTextFieldDeleteStatsOlderThan.setVisible(false);
            this.jLabelDays2.setVisible(false);
            this.jPanelUIHelpDelStatistic.setVisible(false);
        }
        this.jComboBoxTimeUnit.addItem(new TimeUnitMaintenance(TimeUnitMaintenance.MULTIPLIER_DAY));
        this.jComboBoxTimeUnit.addItem(new TimeUnitMaintenance(TimeUnitMaintenance.MULTIPLIER_HOUR));
        this.jComboBoxTimeUnit.addItem(new TimeUnitMaintenance(TimeUnitMaintenance.MULTIPLIER_MINUTE));
        //set the max string length that could be entered to 5 - these are 00000 to 99999
        this.jTextFieldDeleteLogDirOlderThan.setDocument(new JTextFieldLimitDocument(5));
        this.jTextFieldDeleteMsgOlderThan.setDocument(new JTextFieldLimitDocument(5));
        this.jTextFieldDeleteStatsOlderThan.setDocument(new JTextFieldLimitDocument(5));        
    }

    private void initializeHelp(){
        this.jPanelUIHelpDelLogDirs.setToolTip( this.rb, "systemmaintenance.deleteoldlogdirs.help");
        this.jPanelUIHelpDelOldTransactions.setToolTip( this.rb, "systemmaintenance.deleteoldtransactions.help");
        this.jPanelUIHelpDelStatistic.setToolTip( this.rb, "systemmaintenance.deleteoldstatistic.help");
    }
    
    /**
     * Sets new preferences to this panel to changes/modify
     */
    @Override
    public void loadPreferences() {
        this.jCheckBoxDeleteMsgOlderThan.setSelected(this.preferences.getBoolean(PreferencesAS2.AUTO_MSG_DELETE));
        this.jTextFieldDeleteMsgOlderThan.setText(String.valueOf(this.preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN)));
        this.jComboBoxTimeUnit.setSelectedItem(new TimeUnitMaintenance(this.preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S)));
        if (!this.preferences.getBoolean(PreferencesAS2.COMMUNITY_EDITION)) {
            this.jCheckBoxDeleteStatsOlderThan.setSelected(this.preferences.getBoolean(PreferencesAS2.AUTO_STATS_DELETE));
            this.jTextFieldDeleteStatsOlderThan.setText(String.valueOf(this.preferences.getInt(PreferencesAS2.AUTO_STATS_DELETE_OLDERTHAN)));
        }
        this.jCheckBoxDeleteLogDirOlderThan.setSelected(this.preferences.getBoolean(PreferencesAS2.AUTO_LOGDIR_DELETE));
        this.jTextFieldDeleteLogDirOlderThan.setText(String.valueOf(this.preferences.getInt(PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN)));        
    }

    /**
     * Stores the GUI settings in the preferences
     */
    @Override
    public void savePreferences() {
        try {
            int olderThantransactions = Integer.valueOf(this.jTextFieldDeleteMsgOlderThan.getText()).intValue();
            //do not allow negative values or the 0
            if (olderThantransactions <= 0) {
                olderThantransactions = Integer.getInteger(this.preferences.getDefaultValue(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN)).intValue();
            }            
            this.preferences.putInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN, olderThantransactions);
            this.preferences.putInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S,
                    (int) ((TimeUnitMaintenance) this.jComboBoxTimeUnit.getSelectedItem()).getMultiplier());
            this.preferences.putBoolean(PreferencesAS2.AUTO_MSG_DELETE, this.jCheckBoxDeleteMsgOlderThan.isSelected());
            //stats auto delete capabilites
            if (!this.preferences.getBoolean(PreferencesAS2.COMMUNITY_EDITION)) {
                int olderThanStats = Integer.valueOf(this.jTextFieldDeleteStatsOlderThan.getText()).intValue();
                if (olderThanStats <= 0) {
                    olderThanStats = Integer.getInteger(this.preferences.getDefaultValue(PreferencesAS2.AUTO_STATS_DELETE_OLDERTHAN)).intValue();
                }
                this.preferences.putInt(PreferencesAS2.AUTO_STATS_DELETE_OLDERTHAN, olderThanStats);
                this.preferences.putBoolean(PreferencesAS2.AUTO_STATS_DELETE, this.jCheckBoxDeleteStatsOlderThan.isSelected());
            }
            //log dir delete settings
            int olderThanLogDir = Integer.valueOf(this.jTextFieldDeleteLogDirOlderThan.getText()).intValue();
            //do not allow negative values or the 0
            if (olderThanLogDir <= 0) {
                olderThanLogDir = Integer.getInteger(this.preferences.getDefaultValue(PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN)).intValue();
            }
            this.preferences.putInt(PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN, olderThanLogDir);
            this.preferences.putBoolean(PreferencesAS2.AUTO_LOGDIR_DELETE, this.jCheckBoxDeleteLogDirOlderThan.isSelected());
        } catch (Exception nop) {
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

        jPanelMargin = new javax.swing.JPanel();
        jPanelSpace = new javax.swing.JPanel();
        jCheckBoxDeleteMsgOlderThan = new javax.swing.JCheckBox();
        jTextFieldDeleteMsgOlderThan = new javax.swing.JTextField();
        jCheckBoxDeleteStatsOlderThan = new javax.swing.JCheckBox();
        jTextFieldDeleteStatsOlderThan = new javax.swing.JTextField();
        jLabelDays2 = new javax.swing.JLabel();
        jComboBoxTimeUnit = new javax.swing.JComboBox();
        jCheckBoxDeleteLogDirOlderThan = new javax.swing.JCheckBox();
        jTextFieldDeleteLogDirOlderThan = new javax.swing.JTextField();
        jLabelDays1 = new javax.swing.JLabel();
        jPanelUIHelpDelLogDirs = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelUIHelpDelStatistic = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelUIHelpDelOldTransactions = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelSpaceAbove = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jPanelMargin.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelMargin.add(jPanelSpace, gridBagConstraints);

        jCheckBoxDeleteMsgOlderThan.setText(this.rb.getResourceString( "label.deletemsgolderthan"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jCheckBoxDeleteMsgOlderThan, gridBagConstraints);

        jTextFieldDeleteMsgOlderThan.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldDeleteMsgOlderThan.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jTextFieldDeleteMsgOlderThan, gridBagConstraints);

        jCheckBoxDeleteStatsOlderThan.setText(this.rb.getResourceString( "label.deletestatsolderthan"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jCheckBoxDeleteStatsOlderThan, gridBagConstraints);

        jTextFieldDeleteStatsOlderThan.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldDeleteStatsOlderThan.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jTextFieldDeleteStatsOlderThan, gridBagConstraints);

        jLabelDays2.setText(this.rb.getResourceString( "label.days" ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelDays2, gridBagConstraints);

        jComboBoxTimeUnit.setMinimumSize(new java.awt.Dimension(100, 22));
        jComboBoxTimeUnit.setPreferredSize(new java.awt.Dimension(100, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jComboBoxTimeUnit, gridBagConstraints);

        jCheckBoxDeleteLogDirOlderThan.setText(this.rb.getResourceString( "label.deletelogdirolderthan"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jCheckBoxDeleteLogDirOlderThan, gridBagConstraints);

        jTextFieldDeleteLogDirOlderThan.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldDeleteLogDirOlderThan.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelMargin.add(jTextFieldDeleteLogDirOlderThan, gridBagConstraints);

        jLabelDays1.setText(this.rb.getResourceString( "label.days" ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelDays1, gridBagConstraints);

        jPanelUIHelpDelLogDirs.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        jPanelMargin.add(jPanelUIHelpDelLogDirs, gridBagConstraints);

        jPanelUIHelpDelStatistic.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        jPanelMargin.add(jPanelUIHelpDelStatistic, gridBagConstraints);

        jPanelUIHelpDelOldTransactions.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanelUIHelpDelOldTransactions.setTooltipWidth(250);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        jPanelMargin.add(jPanelUIHelpDelOldTransactions, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 0, 10);
        jPanelMargin.add(jPanelSpaceAbove, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelMargin, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxDeleteLogDirOlderThan;
    private javax.swing.JCheckBox jCheckBoxDeleteMsgOlderThan;
    private javax.swing.JCheckBox jCheckBoxDeleteStatsOlderThan;
    private javax.swing.JComboBox jComboBoxTimeUnit;
    private javax.swing.JLabel jLabelDays1;
    private javax.swing.JLabel jLabelDays2;
    private javax.swing.JPanel jPanelMargin;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpaceAbove;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpDelLogDirs;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpDelOldTransactions;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpDelStatistic;
    private javax.swing.JTextField jTextFieldDeleteLogDirOlderThan;
    private javax.swing.JTextField jTextFieldDeleteMsgOlderThan;
    private javax.swing.JTextField jTextFieldDeleteStatsOlderThan;
    // End of variables declaration//GEN-END:variables

    @Override
    public ImageIcon getIcon() {
        return (new ImageIcon(ICON_MAINTENANCE));
    }

    @Override
    public String getTabResource() {
        return ("tab.maintenance");
    }

    public static class TimeUnitMaintenance {

        public static final long MULTIPLIER_DAY = TimeUnit.DAYS.toSeconds(1);
        public static final long MULTIPLIER_HOUR = TimeUnit.HOURS.toSeconds(1);
        public static final long MULTIPLIER_MINUTE = TimeUnit.MINUTES.toSeconds(1);

        private final long MULTIPLIER;

        public TimeUnitMaintenance(final long MULTIPLIER) {
            this.MULTIPLIER = MULTIPLIER;
        }

        @Override
        public String toString() {
            if (this.getMultiplier() == MULTIPLIER_DAY) {
                return (rb.getResourceString("maintenancemultiplier.day"));
            } else if (this.getMultiplier() == MULTIPLIER_HOUR) {
                return (rb.getResourceString("maintenancemultiplier.hour"));
            } else if (this.getMultiplier() == MULTIPLIER_MINUTE) {
                return (rb.getResourceString("maintenancemultiplier.minute"));
            } else {
                return ("Unknown value " + this.getMultiplier());
            }
        }

        /**
         * Overwrite the equal method of object
         *
         * @param anObject object to compare
         */
        @Override
        public boolean equals(Object anObject) {
            if (anObject == this) {
                return (true);
            }
            if (anObject != null && anObject instanceof TimeUnitMaintenance) {
                TimeUnitMaintenance entry = (TimeUnitMaintenance) anObject;
                return (entry.getMultiplier() == this.getMultiplier());
            }
            return (false);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (int) (this.getMultiplier() ^ (this.getMultiplier() >>> 32));
            return hash;
        }

        /**
         * @return the set multiplier
         */
        public long getMultiplier() {
            return MULTIPLIER;
        }

    }

}
