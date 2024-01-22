//$Header: /mendelson_business_integration/de/mendelson/util/TextOverlay.java 14    25.11.21 13:20 Heller $
package de.mendelson.util;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Overlay to add to a text field to enhance the UX - add a placeholder overlay
 * text for the empty field. The placeholder is visible as long as there is no
 * content in the field. The color of the overlay text is the disabled text of
 * the text field. It's possible to add an image which is placed left to the
 * text. The image is grayed out by default - means it's possible to pass a
 * colored image. If the length of the placeholder exceeds the under laying text
 * field the text field is resized to match the text size (this part does only
 * work if the user interface is part of the graphics hierarchy - else the
 * resize is just skipped)
 *
 * @author S.Heller
 * @version: $Revision: 14 $
 */
public class TextOverlay {

    private TextOverlay() {
    }

    public static void addTo(JTextField textField, String placeholderText) {
        addTo(textField, null, placeholderText);
    }

    public static void addTo(JTextField textField, MendelsonMultiResolutionImage placeholderImage) {
        addTo(textField, placeholderImage, null);
    }

    /**
     * Adds a placeholder to the passed text field. Because there is a listener
     * added to the text fields document you must not add another
     * javax.swing.text document to the text field later - else the overlay will
     * not be shown
     *
     * @param textField Text field to add the paceholder overlay to
     * @param placeholderImage Optional image to add to the placeholder, may be
     * null
     * @param placeholderText Text of the placeholder
     */
    public static void addTo(JTextField textField, MendelsonMultiResolutionImage placeholderImage, String placeholderText) {
        Container parentComponent = textField.getParent();
        if (!(parentComponent.getLayout() instanceof GridBagLayout)) {
            throw new RuntimeException(TextOverlay.class.getSimpleName() + " does work in Gridbaglayout Layoutmanager only");
        }
        final JLabel placeholderOverlay = new JLabel(placeholderText);
        placeholderOverlay.setFocusable(false);
        if (placeholderImage != null) {
            int imageHeight = textField.getPreferredSize().height - textField.getMargin().top * 2;
            ImageIcon placeholderIcon = new ImageIcon(placeholderImage.toMinResolution(imageHeight));
            placeholderOverlay.setIcon(ImageUtil.grayImage(placeholderIcon));
        }
        //UX setting: The placeholders font should be smaller than the standard font used in the password field
        Font placeholderFont = textField.getFont().deriveFont((float) textField.getFont().getSize() - 1);
        placeholderOverlay.setFont(placeholderFont);
        placeholderOverlay.setForeground(UIManager.getColor("FormattedTextField.inactiveForeground"));
        int placeholderMarginLeft = textField.getMargin().left * 2;
        int placeholderMarginRight = textField.getMargin().right * 2;
        Border placeholderMargin = new EmptyBorder(
                textField.getMargin().top,
                placeholderMarginLeft,
                textField.getMargin().bottom,
                placeholderMarginRight);
        placeholderOverlay.setBorder(placeholderMargin);
        //listen to the text fields document: If the field content is empty the overlay should be shown - else
        //it should be invisible
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                boolean shouldBeVisible = textField.getText().length() == 0
                        && textField.isEnabled() && textField.isEditable();
                placeholderOverlay.setVisible(shouldBeVisible);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                boolean shouldBeVisible = textField.getText().length() == 0
                        && textField.isEnabled() && textField.isEditable();
                placeholderOverlay.setVisible(shouldBeVisible);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                boolean shouldBeVisible = textField.getText().length() == 0
                        && textField.isEnabled() && textField.isEditable();
                placeholderOverlay.setVisible(shouldBeVisible);
            }
        });
        //do not show the text overlay placeholder if the textfield is disabled or not editable. 
        //As there is no special listener for the enabled/editable state of a JComponent just take the 
        //property change event and perform a string compare on the
        //property name. Hopefully this will not change in the future..
        textField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equalsIgnoreCase("enabled")) {
                    boolean enabled = Boolean.valueOf(evt.getNewValue().toString()).booleanValue();
                    placeholderOverlay.setVisible(enabled
                            && textField.isVisible()
                            && textField.getText().length() == 0
                    );
                } else if (evt.getPropertyName().equalsIgnoreCase("editable")) {
                    boolean editable = Boolean.valueOf(evt.getNewValue().toString()).booleanValue();
                    placeholderOverlay.setVisible(editable
                            && textField.isVisible()
                            && textField.getText().length() == 0
                    );
                }
            }
        });
        //Set the text cursor to the placeholder overlay - this is set to the underlaying
        //text field but will not trigger because it is below the overlay label
        placeholderOverlay.setCursor(textField.getCursor());
        final JLayeredPane layeredPane = new JLayeredPane();
        //do not display the whole layer if the text field turns invisible. 
        layeredPane.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                //dont show the placeholder if there is either text in it or the text field is disabled
                placeholderOverlay.setVisible(textField.isVisible()
                        && textField.getText().length() == 0
                        && textField.isEnabled()
                        && textField.isEditable());
                layeredPane.setVisible(textField.isVisible());
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                //dont show the placeholder if there is either text in it or the text field is disabled
                placeholderOverlay.setVisible(textField.isVisible()
                        && textField.getText().length() == 0
                        && textField.isEnabled()
                        && textField.isEditable());
                layeredPane.setVisible(textField.isVisible());
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                //dont show the placeholder if there is either text in it or the text field is disabled
                placeholderOverlay.setVisible(textField.isVisible()
                        && textField.getText().length() == 0
                        && textField.isEnabled()
                        && textField.isEditable());
                layeredPane.setVisible(textField.isVisible());
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //find out the width of the placeholder text. If it exceeds the text field length
                //the size of the text field must be recomputed. There is no guarantee that 
                //this will result in the required size as the real size is in the responsibility
                //of the layout manager. This will not set the absolute size - it will
                //just set recommendations (min size, preferred size)
                try {
                    AffineTransform affinetransform = new AffineTransform();
                    FontRenderContext fontRenderContext = new FontRenderContext(affinetransform, true, true);
                    int placeholderTextWidthInPixels = (int) (placeholderFont.getStringBounds(
                            placeholderText, fontRenderContext).getWidth());
                    if (placeholderTextWidthInPixels
                            > (textField.getPreferredSize().width
                            - placeholderMarginLeft
                            - placeholderMarginRight)) {
                        textField.setPreferredSize(
                                new Dimension(
                                        placeholderTextWidthInPixels
                                        + placeholderMarginLeft
                                        + placeholderMarginRight,
                                        textField.getPreferredSize().height));
                        textField.setMinimumSize(
                                new Dimension(
                                        placeholderTextWidthInPixels
                                        + placeholderMarginLeft
                                        + placeholderMarginRight,
                                        textField.getMinimumSize().height));
                    }
                } catch (Throwable e) {
                    //the underlaying text field if not part of the swing graphics hierarchy and
                    //the system is unable to compute the font size. In this seldom case just skip the resize
                }
                Dimension textFieldPreferredSize = textField.getPreferredSize();
                Dimension textFieldMinSize = textField.getMinimumSize();
                Dimension textFieldMaxSize = textField.getMaximumSize();
                GridBagLayout parentLayout = (GridBagLayout) parentComponent.getLayout();
                GridBagConstraints constraints = (GridBagConstraints) parentLayout.getConstraints(textField).clone();
                placeholderOverlay.setPreferredSize(textFieldPreferredSize);
                placeholderOverlay.setMinimumSize(textFieldMinSize);
                placeholderOverlay.setMaximumSize(textFieldMaxSize);
                parentComponent.getLayout().removeLayoutComponent(textField);
                layeredPane.setPreferredSize(textFieldPreferredSize);
                layeredPane.setMaximumSize(textFieldMaxSize);
                layeredPane.setMinimumSize(textFieldMinSize);
                layeredPane.setLayout(new SpringLayout());
                layeredPane.add(textField, JLayeredPane.DEFAULT_LAYER);
                //The palette layer is above the default layer
                layeredPane.add(placeholderOverlay, JLayeredPane.PALETTE_LAYER);
                SpringLayout springLayout = (SpringLayout) layeredPane.getLayout();
                springLayout.putConstraint(SpringLayout.WEST, textField, 0, SpringLayout.WEST, layeredPane);
                springLayout.putConstraint(SpringLayout.EAST, textField, 0, SpringLayout.EAST, layeredPane);
                springLayout.putConstraint(SpringLayout.NORTH, textField, 0, SpringLayout.NORTH, layeredPane);
                springLayout.putConstraint(SpringLayout.SOUTH, textField, 0, SpringLayout.SOUTH, layeredPane);
                springLayout.putConstraint(SpringLayout.WEST, placeholderOverlay, 0, SpringLayout.WEST, layeredPane);
                springLayout.putConstraint(SpringLayout.EAST, placeholderOverlay, 0, SpringLayout.EAST, layeredPane);
                springLayout.putConstraint(SpringLayout.NORTH, placeholderOverlay, 0, SpringLayout.NORTH, layeredPane);
                springLayout.putConstraint(SpringLayout.SOUTH, placeholderOverlay, 0, SpringLayout.SOUTH, layeredPane);
                parentComponent.add(layeredPane, constraints);
                parentComponent.invalidate();
                parentComponent.validate();
            }
        }
        );

    }

}
