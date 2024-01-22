//$Header: /as2/de/mendelson/util/uinotification/NotificationPanel.java 18    10/05/22 12:57 Heller $
package de.mendelson.util.uinotification;

import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Panel that contains the notification information
 *
 * @author S.Heller
 * @version $Revision: 18 $
 */
public class NotificationPanel extends JPanel {

    protected static final Color DEFAULT_COLOR_BACKGROUND_SUCCESS_LIGHT = Color.WHITE;
    protected static final Color DEFAULT_COLOR_BACKGROUND_SUCCESS_DARK = new Color(0, 104, 55);
    protected static final Color DEFAULT_COLOR_BACKGROUND_WARNING_LIGHT = Color.WHITE;
    protected static final Color DEFAULT_COLOR_BACKGROUND_WARNING_DARK = new Color(255, 176, 59);
    protected static final Color DEFAULT_COLOR_BACKGROUND_ERROR_LIGHT = Color.WHITE;
    protected static final Color DEFAULT_COLOR_BACKGROUND_ERROR_DARK = new Color(193, 39, 45);
    protected static final Color DEFAULT_COLOR_BACKGROUND_INFORMATION_LIGHT = Color.WHITE;
    protected static final Color DEFAULT_COLOR_BACKGROUND_INFORMATION_DARK = new Color(0, 113, 188);

    protected static final Color DEFAULT_COLOR_FOREGROUND_TITLE = Color.GRAY;
    protected static final Color DEFAULT_COLOR_FOREGROUND_DETAILS = Color.GRAY;

    private int notificationType = UINotification.TYPE_SUCCESS;

    protected final static int IMAGESIZE_CLOSECROSS = 13;
    protected final static int IMAGESIZE_ICON = 34;

    private MendelsonMultiResolutionImage image;
    /**
     * Will cut the details text in any case if the length exceeds this value
     */
    private final int MAX_NOTIFICATION_DETAILS_TEXT_LENGTH = 150;

    /**
     * Indicates if the graphic unit supports user defined shapes
     */
    private boolean graphicSupportsShapedWindows = false;
    
    /**
     * @param image Image to display - there is a default if this is null
     * @param NOTIFICATION_TYPE One of UINotification.TYPE_OK,
     * UINotification.TYPE_WARNING, UINotification.TYPE_ERROR
     * @param notificationTitle The title of the notification - not folded -
     * means you have to ensure a short title. If this is null, the type OK,
     * WARNING, ERROR is displayed in the localized language of the current
     * locale
     * @param notificationDetails Details of the notification - folded, means
     * this could be some longer if required. If the length of the details
     * exceeds the defined MAX_NOTIFICATION_DETAILS_LENGTH it is simply cut off
     */
    public NotificationPanel(MendelsonMultiResolutionImage image,
            final int NOTIFICATION_TYPE, String notificationTitle, String notificationDetails,
            Rectangle bounds, boolean graphicSupportsShapedWindows) {
        this.notificationType = NOTIFICATION_TYPE;
        this.image = image;
        this.graphicSupportsShapedWindows = graphicSupportsShapedWindows;
        initComponents();
        this.setMultiresolutionIcons();
        //setOpaque(false) means background of parent will be painted first
        //setOpaque(true) means The component agrees to paint all of the pixels contained within 
        //its rectangular bounds. This is done by painting the background before doing the custom 
        //painting of the component.
        this.jPanelSpace4.setOpaque(false);
        this.jPanelSpace5.setOpaque(false);
        this.jPanelIcon.setOpaque(true);
        this.jPanelText.setOpaque(true);
        this.jPanelNotificationTypeBar.setOpaque(true);
        if (this.graphicSupportsShapedWindows) {
            this.jPanelNotificationTypeBar.setType(ShapedPanel.TYPE_ROUNDED_EDGES_LEFT);
            this.jPanelCross.setType(ShapedPanel.TYPE_ROUNDED_EDGES_RIGHT);
        }else{
            this.jPanelNotificationTypeBar.setType(ShapedPanel.TYPE_NO_ROUNDED_EDGES);
            this.jPanelCross.setType(ShapedPanel.TYPE_NO_ROUNDED_EDGES);
        }
        this.setBackgroundColors(
                DEFAULT_COLOR_BACKGROUND_SUCCESS_LIGHT,
                DEFAULT_COLOR_BACKGROUND_SUCCESS_DARK,
                DEFAULT_COLOR_BACKGROUND_WARNING_LIGHT,
                DEFAULT_COLOR_BACKGROUND_WARNING_DARK,
                DEFAULT_COLOR_BACKGROUND_ERROR_LIGHT,
                DEFAULT_COLOR_BACKGROUND_ERROR_DARK,
                DEFAULT_COLOR_BACKGROUND_INFORMATION_LIGHT,
                DEFAULT_COLOR_BACKGROUND_INFORMATION_DARK);
        this.jLabelNotificationTitle.setText(notificationTitle);
        if (notificationDetails != null) {
            if (notificationDetails.length() > MAX_NOTIFICATION_DETAILS_TEXT_LENGTH) {
                notificationDetails = notificationDetails.substring(0, MAX_NOTIFICATION_DETAILS_TEXT_LENGTH - 1);
            }
            this.jLabelNotificationDetails.setText("<HTML>" + notificationDetails + "</HTML>");
        } else {
            this.jLabelNotificationDetails.setText("");
        }
        this.jLabelNotificationDetails.setForeground(DEFAULT_COLOR_FOREGROUND_DETAILS);
        this.jLabelNotificationTitle.setForeground(DEFAULT_COLOR_FOREGROUND_TITLE);
    }

    private void setMultiresolutionIcons() {
        this.image = UINotification.getMultiresolutionImage(this.image, this.notificationType);
        this.jLabelIcon.setIcon(new ImageIcon(this.image.toMinResolution(IMAGESIZE_ICON)));
        this.setCurrentCross(DEFAULT_COLOR_FOREGROUND_DETAILS);
    }

    protected JLabel getCloseCrossLabel() {
        return (this.jLabelCross);
    }

    protected JPanel getCloseCrossPanel() {
        return (this.jPanelCross);
    }

    protected JPanel getIconPanel() {
        return (this.jPanelIcon);
    }

    protected JPanel getNotificationTypePanel() {
        return (this.jPanelNotificationTypeBar);
    }

    protected JPanel getTextPanel() {
        return (this.jPanelText);
    }

    /**
     * Redefines the used background colors for the panels
     */
    public void setBackgroundColors(
            Color backgroundSuccessLight,
            Color backgroundSuccessDark,
            Color backgroundWarningLight,
            Color backgroundWarningDark,
            Color backgroundErrorLight,
            Color backgroundErrorDark,
            Color backgroundInformationLight,
            Color backgroundInformationDark) {
        if (this.notificationType == UINotification.TYPE_SUCCESS) {
            this.jPanelNotificationTypeBar.setBackground(backgroundSuccessDark);
            this.jPanelIcon.setBackground(backgroundSuccessLight);
            this.jPanelText.setBackground(backgroundSuccessLight);
            this.jPanelCross.setBackground(backgroundSuccessLight);
        } else if (this.notificationType == UINotification.TYPE_WARNING) {
            this.jPanelNotificationTypeBar.setBackground(backgroundWarningDark);
            this.jPanelIcon.setBackground(backgroundWarningLight);
            this.jPanelText.setBackground(backgroundWarningLight);
            this.jPanelCross.setBackground(backgroundWarningLight);
        } else if (this.notificationType == UINotification.TYPE_ERROR) {
            this.jPanelNotificationTypeBar.setBackground(backgroundErrorDark);
            this.jPanelIcon.setBackground(backgroundErrorLight);
            this.jPanelText.setBackground(backgroundErrorLight);
            this.jPanelCross.setBackground(backgroundErrorLight);
        } else if (this.notificationType == UINotification.TYPE_INFORMATION) {
            this.jPanelNotificationTypeBar.setBackground(backgroundInformationDark);
            this.jPanelIcon.setBackground(backgroundInformationLight);
            this.jPanelText.setBackground(backgroundInformationLight);
            this.jPanelCross.setBackground(backgroundInformationLight);
        }
    }

    /**
     * Refreshes the current cross image
     */
    protected void setCurrentCross(Color crossColor) {
        this.jLabelCross.setIcon(UINotification.generateCrossImage(IMAGESIZE_CLOSECROSS, crossColor));
    }

    /**
     * Redefines the used foreground colors for the panels
     */
    public void setForegroundColors(Color foregroundTitle, Color foregroundDetails) {
        this.jLabelNotificationTitle.setForeground(foregroundTitle);
        this.jLabelNotificationDetails.setForeground(foregroundDetails);
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

        jPanelNotificationTypeBar = new de.mendelson.util.uinotification.ShapedPanel();
        jPanelIcon = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jPanelText = new javax.swing.JPanel();
        jLabelNotificationDetails = new javax.swing.JLabel();
        jLabelNotificationTitle = new javax.swing.JLabel();
        jLabelSpace1 = new javax.swing.JLabel();
        jLabelSpace2 = new javax.swing.JLabel();
        jLabelSpace3 = new javax.swing.JLabel();
        jPanelSpace4 = new javax.swing.JPanel();
        jPanelCross = new de.mendelson.util.uinotification.ShapedPanel();
        jLabelCross = new javax.swing.JLabel();
        jPanelSpace5 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(222, 231, 251));
        setLayout(new java.awt.GridBagLayout());

        jPanelNotificationTypeBar.setMinimumSize(new java.awt.Dimension(15, 10));
        jPanelNotificationTypeBar.setPreferredSize(new java.awt.Dimension(15, 10));

        javax.swing.GroupLayout jPanelNotificationTypeBarLayout = new javax.swing.GroupLayout(jPanelNotificationTypeBar);
        jPanelNotificationTypeBar.setLayout(jPanelNotificationTypeBarLayout);
        jPanelNotificationTypeBarLayout.setHorizontalGroup(
            jPanelNotificationTypeBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelNotificationTypeBarLayout.setVerticalGroup(
            jPanelNotificationTypeBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanelNotificationTypeBar, gridBagConstraints);

        jPanelIcon.setFocusable(false);
        jPanelIcon.setMinimumSize(new java.awt.Dimension(55, 42));
        jPanelIcon.setPreferredSize(new java.awt.Dimension(55, 42));
        jPanelIcon.setLayout(new java.awt.GridBagLayout());

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/uinotification/missing_image32x32.gif"))); // NOI18N
        jLabelIcon.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        jPanelIcon.add(jLabelIcon, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jPanelIcon, gridBagConstraints);

        jPanelText.setFocusable(false);
        jPanelText.setLayout(new java.awt.GridBagLayout());

        jLabelNotificationDetails.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelNotificationDetails.setText("Notification details");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanelText.add(jLabelNotificationDetails, gridBagConstraints);

        jLabelNotificationTitle.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabelNotificationTitle.setText("Title");
        jLabelNotificationTitle.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanelText.add(jLabelNotificationTitle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 5);
        jPanelText.add(jLabelSpace1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelText.add(jLabelSpace2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelText.add(jLabelSpace3, gridBagConstraints);

        jPanelSpace4.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSpace4.setPreferredSize(new java.awt.Dimension(0, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelText.add(jPanelSpace4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanelText, gridBagConstraints);

        jPanelCross.setFocusable(false);
        jPanelCross.setMinimumSize(new java.awt.Dimension(30, 38));
        jPanelCross.setPreferredSize(new java.awt.Dimension(30, 38));
        jPanelCross.setLayout(new java.awt.GridBagLayout());

        jLabelCross.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/uinotification/missing_image16x16.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        jPanelCross.add(jLabelCross, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelCross.add(jPanelSpace5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weighty = 1.0;
        add(jPanelCross, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelCross;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelNotificationDetails;
    private javax.swing.JLabel jLabelNotificationTitle;
    private javax.swing.JLabel jLabelSpace1;
    private javax.swing.JLabel jLabelSpace2;
    private javax.swing.JLabel jLabelSpace3;
    private de.mendelson.util.uinotification.ShapedPanel jPanelCross;
    private javax.swing.JPanel jPanelIcon;
    private de.mendelson.util.uinotification.ShapedPanel jPanelNotificationTypeBar;
    private javax.swing.JPanel jPanelSpace4;
    private javax.swing.JPanel jPanelSpace5;
    private javax.swing.JPanel jPanelText;
    // End of variables declaration//GEN-END:variables
}
