//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/OpacityIcon.java 2     16.11.18 14:00 Heller $
package de.mendelson.util.clientserver.clients.filesystemview;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Allows to display Icons with a given opacity
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class OpacityIcon implements Icon {

    private Icon icon = null;
    private float opacity = 1f;

    /**
     *
     * @param icon icon to enwrapp
     * @param opacity 0f (invisible) to 1f (standard)
     */
    public OpacityIcon(Icon icon, float opacity) {
        this.icon = icon;
        this.opacity = opacity;
    }

    /**
     *
     * @param opacity 0f (invisible) to 1f (standard)
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    /**
     * Draws the enwrapped icon
     *
     * @param component The component to which the icon is painted
     * @param g the graphics context
     * @param x upper left x
     * @param y upper left y
     */
    @Override
    public void paintIcon(Component component, Graphics g, int x, int y) {
        if (this.icon != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.SrcAtop.derive(opacity));
            this.icon.paintIcon(component, g2, x, y);
            g2.dispose();
        }
    }

    /**
     * Returns the enwrapped icons key data
     */
    @Override
    public int getIconWidth() {
        if (this.icon != null) {
            return (this.icon.getIconWidth());
        } else {
            return (0);
        }
    }

    /**
     * Returns the enwrapped icons key data
     */
    @Override
    public int getIconHeight() {
        if (this.icon != null) {
            return (this.icon.getIconHeight());
        } else {
            return (0);
        }
    }

}
