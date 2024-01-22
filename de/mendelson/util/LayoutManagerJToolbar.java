//$Header: /mendelson_business_integration/de/mendelson/util/LayoutManagerJToolbar.java 6     2/21/17 11:57a Heller $
package de.mendelson.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar.Separator;
import javax.swing.SwingConstants;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */

/**
 * Layout all buttons in a JToolbar with the same size usage: set this
 * LayoutManager to the JToolbar before it comes visible:
 * this.jToolBar.setLayout(new LayoutManagerJToolbar());
 *
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class LayoutManagerJToolbar implements LayoutManager, SwingConstants {

    private final int GAP = 2;

    public LayoutManagerJToolbar() {
    }

    private Dimension[] computeDimensions(Component children[]) {
        int maxWidth = 0;
        int maxHeight = 0;
        int visibleCount = 0;
        Dimension componentPreferredSize;
        for (int i = 0, childCount = children.length; i < childCount; i++) {
            if (children[i].isVisible()) {
                componentPreferredSize = children[i].getPreferredSize();
                maxWidth = Math.max(maxWidth, componentPreferredSize.width);
                maxHeight = Math.max(maxHeight, componentPreferredSize.height);
                visibleCount++;
            }
        }
        int usedWidth = maxWidth * visibleCount + GAP * (visibleCount - 1);
        int usedHeight = maxHeight;
        return (new Dimension[]{
            new Dimension(maxWidth, maxHeight),
            new Dimension(usedWidth, usedHeight)
        });
    }

    @Override
    public void layoutContainer(Container container) {
        Insets insets = container.getInsets();
        Component[] children = container.getComponents();
        Dimension dimension[] = computeDimensions(children);
        int maxWidth = dimension[0].width;
        int maxHeight = dimension[0].height;
        int currentX = insets.left;
        for (int i = 0, childCount = children.length; i < childCount; i++) {
            if (children[i].isVisible()) {
                if (children[i] instanceof JButton
                        || children[i] instanceof JToggleButton) {
                    children[i].setBounds(
                            currentX,
                            insets.top,
                            maxWidth,
                            maxHeight);
                    currentX += (maxWidth + GAP);
                } else if (children[i] instanceof Separator
                        || children[i] instanceof JSeparator) {
                    int width = insets.left + insets.right;
                    children[i].setBounds(
                            currentX,
                            insets.top,
                            width,
                            maxHeight);
                    currentX += width;
                } else if (children[i] instanceof JComboBox) {
                    JComboBox comboBox = (JComboBox)children[i];
                    comboBox.setFocusable(false);
                    children[i].setBounds(
                            currentX,
                            insets.top,
                            insets.left + insets.right + comboBox.getPreferredSize().width,
                            maxHeight);
                    currentX += (insets.left + insets.right + comboBox.getPreferredSize().width);
                }
            }
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container container) {
        return (preferredLayoutSize(container));
    }

    @Override
    public Dimension preferredLayoutSize(Container container) {
        Insets insets = container.getInsets();
        Component[] children = container.getComponents();
        Dimension dimension[] = computeDimensions(children);
        int usedWidth = dimension[1].width;
        int usedHeight = dimension[1].height;
        return (new Dimension(
                insets.left + usedWidth + insets.right,
                insets.top + usedHeight + insets.bottom));
    }

    @Override
    public void addLayoutComponent(String str, Component component) {
    }

    @Override
    public void removeLayoutComponent(Component component) {
    }

}
