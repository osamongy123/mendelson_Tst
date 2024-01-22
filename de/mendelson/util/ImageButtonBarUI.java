//$Header: /mendelson_business_integration/de/mendelson/util/ImageButtonBarUI.java 6     19.12.19 13:11 Heller $
package de.mendelson.util;

import com.l2fprod.common.swing.plaf.basic.BasicButtonBarUI;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonBarUI;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * UI LAF for the button bar
 *
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class ImageButtonBarUI extends BasicButtonBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new BlueishButtonBarUI();
    }

    @Override
    protected void installDefaults() {
        
        
     
        Border border = this.bar.getBorder();
        if (border == null || border instanceof javax.swing.plaf.UIResource) {
            this.bar.setBorder(new BorderUIResource(
                    new CompoundBorder(
                            BorderFactory.createLineBorder(
                                    UIManager.getColor("Panel.background")),
                            BorderFactory.createEmptyBorder(1, 1, 1, 1))));
        }

        Color color = this.bar.getBackground();
        if (color == null || color instanceof ColorUIResource) {
            this.bar.setOpaque(true);
            //defaults to white in standard Windows theme
            this.bar.setBackground(new ColorUIResource(
                    UIManager.getLookAndFeelDefaults().getColor("List.background")));
        }
    }

    @Override
    public void installButtonBarUI(AbstractButton button) {
        button.setUI(new ImageButtonBarButtonUI());
        button.setHorizontalTextPosition(0);
        button.setVerticalTextPosition(3);
        button.setOpaque(false);
    }

    private static class ImageButtonBarButtonUI extends BasicButtonUI {

        private final Color BACKGROUND_COLOR_HOVER;
        private final Color BORDER_COLOR_HOVER;
        private final Color FOREGROUND_COLOR_HOVER;
        private final Color BACKGROUND_COLOR_SELECTED;
        private final Color BORDER_COLOR_SELECTED;
        private final Color FOREGROUND_COLOR_SELECTED;

        public ImageButtonBarButtonUI(){
            super();     
            Color selectionColor = UIManager.getLookAndFeelDefaults().getColor("List.selectionBackground");            
            if( selectionColor == null ){
                //fallback to a blue color
                selectionColor = new Color(193, 210, 238);
            }              
            Color buttonForegroundColor = UIManager.getLookAndFeelDefaults().getColor("Button.foreground"); 
            if( buttonForegroundColor == null ){
                buttonForegroundColor = Color.BLACK;
            }
            BACKGROUND_COLOR_SELECTED = new Color( 
                    selectionColor.getRed(),
                    selectionColor.getGreen(),
                    selectionColor.getBlue(), 100);
            FOREGROUND_COLOR_SELECTED = ColorUtil.getBestContrastColorAroundForeground(
                    BACKGROUND_COLOR_SELECTED, buttonForegroundColor);
            FOREGROUND_COLOR_HOVER = ColorUtil.getBestContrastColorAroundForeground(
                    BACKGROUND_COLOR_SELECTED, buttonForegroundColor);
            BORDER_COLOR_SELECTED = BACKGROUND_COLOR_SELECTED.darker();
            //alpha range is 0..255
            BACKGROUND_COLOR_HOVER = new Color( 
                    selectionColor.getRed(),
                    selectionColor.getGreen(),
                    selectionColor.getBlue(), 70);
            BORDER_COLOR_HOVER = BACKGROUND_COLOR_HOVER.darker();            
        }
        
        
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            AbstractButton button = (AbstractButton) c;
            button.setRolloverEnabled(true);
            button.setBorder(
                    BorderFactory.createEmptyBorder(5, 3, 3, 3)
            );
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            if (button.getModel().isRollover() || button.getModel().isArmed() || button.getModel().isSelected()) {
                Color oldColor = g.getColor();
                if (button.getModel().isSelected()) {
                    g.setColor(BACKGROUND_COLOR_SELECTED);
                    button.setForeground(FOREGROUND_COLOR_SELECTED);
                } else {
                    g.setColor(BACKGROUND_COLOR_HOVER);
                    button.setForeground(FOREGROUND_COLOR_HOVER);
                }
                g.fillRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
                if (button.getModel().isSelected()) {
                    g.setColor(BORDER_COLOR_SELECTED);
                } else {
                    g.setColor(BORDER_COLOR_HOVER);
                }
                g.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
                g.setColor(oldColor);
            }else{
                button.setForeground(UIManager.getLookAndFeelDefaults().getColor("Button.foreground")); 
            }
            super.paint(g, c);
        }
    }
}
