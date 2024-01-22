//$Header: /oftp2/de/mendelson/util/balloontip/JPanelUIHelp.java 11    14/06/22 10:05 Heller $
package de.mendelson.util.balloontip;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Help panel that is bound to a tooltip and could be used in the user interface
 * to explain details direct in the UI
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class JPanelUIHelp extends JPanel {

    private final MendelsonMultiResolutionImage IMAGE_HELP
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/balloontip/help.svg", 8, 48);

    private final int GAP_X = 10;
    private int maxTooltipWidth = 200;

    private Color imageColor = Color.decode("#0071bc");
    private Color balloontipBackground = Color.LIGHT_GRAY;
    private Color balloontipForeground = Color.BLACK;
    private Color balloontipBorder = Color.DARK_GRAY;
    private boolean customColorsUsed = false;
    private BalloonToolTip balloonTip = null;
    private String originalTooltipText = null;

    /**
     * Creates new form JPanelUIHelp
     */
    public JPanelUIHelp() {
        initComponents();
        this.imageColor = ColorUtil.getBestContrastColorAroundForeground(
                this.getBackground(), this.imageColor);
        this.setMultiresolutionIcons();
        //this is a just bad code - but there seems no way to modify a single tooltip timing,
        //no idea to solve this without this hack
        final int defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
        final int defaultDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
        final int defaultReshowDelay = ToolTipManager.sharedInstance().getReshowDelay();
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                ToolTipManager.sharedInstance().setInitialDelay(200);
                ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
                ToolTipManager.sharedInstance().setReshowDelay(0);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ToolTipManager.sharedInstance().setInitialDelay(defaultInitialDelay);
                ToolTipManager.sharedInstance().setDismissDelay(defaultDismissDelay);
                ToolTipManager.sharedInstance().setReshowDelay(defaultReshowDelay);
            }
        });
    }

    private void setMultiresolutionIcons() {
        int imageSize = Math.min(this.getPreferredSize().height, this.getPreferredSize().width);
        ImageIcon icon = new ImageIcon(IMAGE_HELP.toMinResolution((int) (imageSize * 0.7f)));
        this.jLabelImage.setIcon(icon);
    }

    @Override
    public void setEnabled( boolean enabled ){
        super.setEnabled( enabled );
        this.jLabelImage.setEnabled(enabled);
    }
    
    /**
     * Modifies the tooltip text if it is in HTML format: adds a width
     */
    private String addParameterToTooltipText(String tooltipText, int width) {
        StringBuilder tipText = new StringBuilder();
        if (tooltipText != null
                && tooltipText.toUpperCase().startsWith("<HTML>")
                && tooltipText.toUpperCase().endsWith("</HTML>")) {
            tipText.append("<HTML>");
            tipText.append("<p style=\"width:" + width + "px;\">");
            tipText.append(tooltipText.substring(6, tooltipText.length() - 7));
            tipText.append("</p><HTML>");
            return (tipText.toString());
        } else {
            return (tooltipText);
        }
    }

    @Override
    public void setToolTipText(String tooltipText) {
        this.originalTooltipText = tooltipText;
        super.setToolTipText(this.addParameterToTooltipText(this.originalTooltipText, this.maxTooltipWidth));
    }

    /**
     * Sets up the tooltip text for the component
     */
    public void setToolTip(MecResourceBundle rb, String resourceKey) {
        this.setToolTipText(rb.getResourceString(resourceKey));
    }

    /**
     * Sets the max tooltip width in pixel
     */
    public void setTooltipWidth(int maxTooltipWidth) {
        this.maxTooltipWidth = maxTooltipWidth;
        this.setToolTipText(this.originalTooltipText);
    }

    /**
     * Returns the current set max width of the tooltip
     */
    public int getTooltipWidth() {
        return (this.maxTooltipWidth);
    }
    
    /**
     * Sets the colors of the all the components used in here
     */
    public void setColors(
            Color balloontipBackground,
            Color balloontipForeground,
            Color balloontipBorder) {
        this.customColorsUsed = true;
        this.balloontipBackground = balloontipBackground;
        this.balloontipForeground = balloontipForeground;
        this.balloontipBorder = balloontipBorder;
        this.setMultiresolutionIcons();
        if (this.balloonTip != null) {
            this.balloonTip.setColors(
                    this.balloontipBackground,
                    this.balloontipForeground,
                    this.balloontipBorder);
        }
    }

    /**
     * Sets the color of the image
     */
    public void setImageColor(Color imageColor) {
        this.imageColor = imageColor;
    }

    /**
     * Rescale the icon - means the icon size could be set by the preferred size
     * of the widget
     */
    @Override
    public void setPreferredSize(Dimension dimension) {
        super.setPreferredSize(dimension);
        this.setMultiresolutionIcons();
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        int x = this.getWidth() + GAP_X;
        int y = 0;
        if (this.balloonTip == null) {
            BalloonToolTip tempTip = new BalloonToolTip();
            tempTip.setTipText(this.addParameterToTooltipText(
                    this.originalTooltipText, this.maxTooltipWidth));
            y = this.jLabelImage.getHeight() / 2 - tempTip.getPreferredSize().height / 2;
        } else {
            y = this.jLabelImage.getHeight() / 2 - this.balloonTip.getPreferredSize().height / 2;
        }
        return new Point(x, y);
    }

    @Override
    public JToolTip createToolTip() {
        BalloonToolTip tooltip = new BalloonToolTip();
        tooltip.setComponent(this);
        if (this.customColorsUsed) {
            tooltip.setColors(
                    this.balloontipBackground,
                    this.balloontipForeground,
                    this.balloontipBorder);
        }
        this.balloonTip = tooltip;
        return tooltip;
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

        jLabelImage = new javax.swing.JLabel();
        jPanelSpacer = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jLabelImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/balloontip/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(jLabelImage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(jPanelSpacer, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JPanel jPanelSpacer;
    // End of variables declaration//GEN-END:variables
}
