//$Header: /converteride/de/mendelson/util/ImageButtonBar.java 14    16/05/22 12:55 Heller $
package de.mendelson.util;

import com.l2fprod.common.swing.JButtonBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

/**
 * Panel that contains image buttons and could be used to group panels. Use it
 * the following way: 1.initialize it 2.Add panels to it by addButton()
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
public class ImageButtonBar extends JPanel {

    public static final int VERTICAL = JButtonBar.VERTICAL;
    public static final int HORIZONTAL = JButtonBar.HORIZONTAL;
    private JButtonBar bar;
    private final List<ImageButtonComponent> componentsList
            = Collections.synchronizedList(new ArrayList<ImageButtonComponent>());
    private final Map<String, JToggleButton> internalButtonMap
            = new ConcurrentHashMap<String, JToggleButton>();

    private int preferredButtonWidth = -1;
    private int preferredButtonHeight = -1;

    public ImageButtonBar(final int DIRECTION) {
        if (DIRECTION != VERTICAL && DIRECTION != HORIZONTAL) {
            throw new IllegalArgumentException("ImageButtonBar: unsupported direction");
        }
        this.bar = new JButtonBar(DIRECTION);
        bar.setUI(new ImageButtonBarUI());
    }

    public ImageButtonBar setPreferredButtonSize(int width, int height) {
        this.preferredButtonHeight = height;
        this.preferredButtonWidth = width;
        return( this );
    }

    /**
     * Allows to select a button of the button bar that has been given an
     * internal name before. If the button with the name does not exist nothing
     * happens
     */
    public void setSelectedButton(String internalButtonName) {
        if (this.internalButtonMap.containsKey(internalButtonName)) {
            JToggleButton button = this.internalButtonMap.get(internalButtonName);
            button.doClick();
        }
    }

    /**
     * Add a panel that is controlled by the passed icon
     *
     * @param icon Icon to display
     * @param text Text that is assigned to the icon
     * @param panel Assigned component
     * @param initialSelected selects the component initial if set
     * @param internalName Name that should be used for the button if you would
     * like to select it later using the setSelectedButton method, might be null
     */
    public ImageButtonBar addButton(ImageIcon icon, String text, String internalName, JComponent[] components, boolean initialSelected) {
        synchronized (this.componentsList) {
            ImageButtonComponent imageButton = new ImageButtonComponent(icon, text, internalName, components, initialSelected);
            this.componentsList.add(imageButton);
        }
        return( this );
    }

    /**
     * Add a panel that is controlled by the passed icon
     *
     * @param icon Icon to display
     * @param text Text that is assigned to the icon
     * @param panel Assigned component
     * @param initialSelected selects the component initial if set
     */
    public ImageButtonBar addButton(ImageIcon icon, String text, JComponent[] components, boolean initialSelected) {
        return( this.addButton(icon, text, null, components, initialSelected));
    }

    /**
     * Add a panel that is controlled by the passed icon
     *
     * @param icon Icon to display
     * @param text Text that is assigned to the icon (and displayed below)
     * @param panel Assigned component - this is shown once the button is selected
     * @param initialSelected selects the component initial if set
     */
    public ImageButtonBar addButton(ImageIcon icon, String text, JComponent component, boolean initialSelected) {
        return( this.addButton(icon, text, null, new JComponent[]{component}, initialSelected));
    }

    /**
     * Computes the min width from all buttons - prevents a shortening of the
     * button text
     */
    private int computeButtonMinWidth() {
        int gapX = 5;
        int buttonMinWidth = this.preferredButtonWidth;
        synchronized (this.componentsList) {
            for (ImageButtonComponent component : this.componentsList) {
                JToggleButton button = new JToggleButton(component.getText());
                int textWidth = button.getFontMetrics(button.getFont()).stringWidth(button.getText());
                buttonMinWidth = Math.max(buttonMinWidth, textWidth + (2 * gapX));
                if (component.getIcon() != null) {
                    int iconWidth = component.getIcon().getIconWidth() + (2 * gapX);
                    buttonMinWidth = Math.max(buttonMinWidth, iconWidth);
                }
            }
        }
        return (buttonMinWidth);
    }

    public ImageButtonBar build() {
        int buttonMinWidth = this.computeButtonMinWidth();
        ButtonGroup buttonGroup = new ButtonGroup();
        synchronized (this.componentsList) {
            for (ImageButtonComponent imageButtonComponent : this.componentsList) {
                Action action = new ImageButtonBarAbstractActionImpl(imageButtonComponent, this.componentsList);
                JToggleButton button = new JToggleButton(action);
                button.setHorizontalAlignment(SwingConstants.CENTER);
                button.setVerticalAlignment(SwingConstants.CENTER);
                button.setHorizontalTextPosition(SwingConstants.CENTER);
                button.setVerticalTextPosition(SwingConstants.BOTTOM);
                if (this.preferredButtonHeight != -1 && this.preferredButtonWidth != -1) {
                    button.setPreferredSize(new Dimension(buttonMinWidth, this.preferredButtonHeight));
                }
                this.bar.add(button);
                buttonGroup.add(button);
                for (JComponent component : imageButtonComponent.getComponents()) {
                    component.setVisible(imageButtonComponent.getInitialSelected());
                }
                button.setSelected(imageButtonComponent.getInitialSelected());
                //allow to select the component by name
                if (imageButtonComponent.getInternalName() != null) {
                    this.internalButtonMap.put(imageButtonComponent.getInternalName(), button);
                }
            }
        }
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        this.add(this.bar, BorderLayout.CENTER);
        return( this );
    }

    private final static class ImageButtonComponent {

        private final ImageIcon icon;
        private final String text;
        private final JComponent[] components;
        private boolean initialSelected = false;
        private String internalName = null;

        public ImageButtonComponent(ImageIcon icon, String text, String internalName,
                JComponent[] components, boolean initialSelected) {
            this.icon = icon;
            this.text = text;
            this.components = components;
            this.initialSelected = initialSelected;
            this.internalName = internalName;
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
            if (anObject != null && anObject instanceof ImageButtonComponent) {
                ImageButtonComponent otherComponent = (ImageButtonComponent) anObject;
                boolean componentsAreEqual = this.components.length == otherComponent.components.length;
                for (int i = 0; componentsAreEqual && i < this.components.length; i++) {
                    componentsAreEqual = componentsAreEqual && this.components[i].equals(otherComponent.components[i]);
                }
                return (this.text.equals(otherComponent.text) && this.icon.equals(otherComponent.icon) && componentsAreEqual);
            }
            return (false);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + (this.icon != null ? this.icon.hashCode() : 0);
            hash = 59 * hash + (this.text != null ? this.text.hashCode() : 0);
            hash = 59 * hash + Arrays.deepHashCode(this.components);
            hash = 59 * hash + (this.initialSelected ? 1 : 0);
            return hash;
        }

        public JComponent[] getComponents() {
            return this.components;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        public String getText() {
            return text;
        }

        public boolean getInitialSelected() {
            return initialSelected;
        }

        public void setInitialSelected(boolean initialSelected) {
            this.initialSelected = initialSelected;
        }

        public void setVisible(boolean flag) {
            for (JComponent component : this.components) {
                component.setVisible(flag);
            }

        }

        /**
         * @return the internalName
         */
        public String getInternalName() {
            return internalName;
        }
    }

    private static class ImageButtonBarAbstractActionImpl extends AbstractAction {

        private ImageButtonComponent ownComponents;
        private List<ImageButtonComponent> componentList;

        public ImageButtonBarAbstractActionImpl(ImageButtonComponent ownComponents, List<ImageButtonComponent> componentList) {
            super(ownComponents.getText(), ownComponents.getIcon());
            this.ownComponents = ownComponents;
            this.componentList = componentList;
        }

        /**
         * Invisible all added panels but own panel
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            for (ImageButtonComponent singleComponents : this.componentList) {
                singleComponents.setVisible(ownComponents.equals(singleComponents));
            }
        }
    }
}
