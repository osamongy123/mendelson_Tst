package de.mendelson.util.passwordfield;

import de.mendelson.util.ImageUtil;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPasswordField;
import javax.swing.JToggleButton;
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
 * Overlay to add to a password field to allow a toggle of the password
 * visibility. It's also possible to add additional text to the empty field and
 * an additional icon. If the placeholder text width exceeds the password field
 * length (prefered and min) the password field size is recomputed to match the
 * placeholder text (this part does only work if the user interface is part of
 * the graphics hierarchy - else the resize is just skipped)
 *
 * @author S.Heller
 * @version: $Revision: 10 $
 */
public class PasswordOverlay {

    private static final MendelsonMultiResolutionImage IMAGE_EYE_MASKED
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/passwordfield/password_toggle.svg", 16,
                    48);
    private static final MendelsonMultiResolutionImage IMAGE_EYE_UNMASKED
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/passwordfield/password_toggle_nomask.svg", 16,
                    48);

    private PasswordOverlay() {
    }

    public static void addTo(JPasswordField passwordField) {
        addTo(passwordField, null, null);
    }

    public static void addTo(JPasswordField passwordField, String placeholderText) {
        addTo(passwordField, null, placeholderText);
    }

    private static ImageIcon generateEyeIconMasked(int imageHeight){
        ImageIcon iconEye = new ImageIcon(IMAGE_EYE_MASKED.toMinResolution(imageHeight));
        return( iconEye );
    }
    
    private static ImageIcon generateEyeIconMaskedLight(int imageHeight){
        ImageIcon iconEyeGreyed = ImageUtil.grayImage(generateEyeIconMasked(imageHeight));
        return( iconEyeGreyed );
    }
    
     private static ImageIcon generateEyeIconUnmasked(int imageHeight){
        ImageIcon iconEye = new ImageIcon(IMAGE_EYE_UNMASKED.toMinResolution(imageHeight));
        return( iconEye );
    }
    
    private static ImageIcon generateEyeIconUnmaskedLight(int imageHeight){
        ImageIcon iconEyeGreyed = ImageUtil.grayImage(generateEyeIconUnmasked(imageHeight));
        return( iconEyeGreyed );
    }
    
    /**
     * Adds a placeholder and a eye icon to the passed text field. Because there is a listener added to the password fields 
     * document you must not add another javax.swing.text document to the field later 
     * - else the overlay will not be shown
     * 
     * @param passwordField Password field to add the paceholder overlay to
     * @param placeholderImage Optional image to add to the placeholder, may be null
     * @param placeholderText Text of the placeholder
     */
    public static void addTo(JPasswordField passwordField, MendelsonMultiResolutionImage placeholderImage, String placeholderText) {
        Container parentComponent = passwordField.getParent();
        if (!(parentComponent.getLayout() instanceof GridBagLayout)) {
            throw new RuntimeException(PasswordOverlay.class.getSimpleName() + " does work in Gridbaglayout Layoutmanager only");
        }
        int imageHeight = passwordField.getPreferredSize().height - passwordField.getMargin().top * 2;
        final ImageIcon iconEyeMasked = generateEyeIconMasked(imageHeight);
        final ImageIcon iconEyeMaskedLight = generateEyeIconMaskedLight(imageHeight);
        final ImageIcon iconEyeUnmasked = generateEyeIconUnmasked(imageHeight);
        final ImageIcon iconEyeUnmaskedLight = generateEyeIconUnmaskedLight(imageHeight);
        final char echoChar = passwordField.getEchoChar();
        final JToggleButton jToggleButtonEye = new JToggleButton(iconEyeMasked);
        jToggleButtonEye.setPreferredSize(
                new Dimension(
                        passwordField.getPreferredSize().height,
                        passwordField.getPreferredSize().height));
        //Do not display the toggle button decoration
        jToggleButtonEye.setBorderPainted(false);
        jToggleButtonEye.setContentAreaFilled(false);
        jToggleButtonEye.setFocusPainted(false);
        passwordField.setMargin(new Insets(passwordField.getMargin().top,
                passwordField.getMargin().left,
                passwordField.getMargin().bottom,
                passwordField.getMargin().right + passwordField.getMargin().top * 2 + imageHeight
        ));
        //toggle the mask character if the user clicked on the eye
        jToggleButtonEye.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //ignore any action on the eye if the underlaying field is disabled
                if (!passwordField.isEnabled()) {
                    return;
                }
                if (jToggleButtonEye.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar(echoChar);
                }
                passwordField.requestFocusInWindow();
            }
        });
        //modify the eye color if the user moves on it with the mouse
        jToggleButtonEye.addMouseListener(new MouseListener() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if (jToggleButtonEye.isSelected()) {
                    jToggleButtonEye.setIcon(iconEyeUnmaskedLight);
                } else {
                    jToggleButtonEye.setIcon(iconEyeMaskedLight);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (jToggleButtonEye.isSelected()) {
                    jToggleButtonEye.setIcon(iconEyeUnmasked);
                } else {
                    jToggleButtonEye.setIcon(iconEyeMasked);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (jToggleButtonEye.isSelected()) {
                    jToggleButtonEye.setIcon(iconEyeUnmaskedLight);
                } else {
                    jToggleButtonEye.setIcon(iconEyeMaskedLight);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

        });
        //do not show the eye if the password field is disabled. As there is no special listener for the
        //enabled state of a JComponent just take the property change event and perform a string compare on the
        //property name. Hopefully this will not change in the future..
        passwordField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equalsIgnoreCase("enabled")) {
                    boolean enabled = Boolean.valueOf(evt.getNewValue().toString()).booleanValue();
                    if (!enabled) {
                        //do not keep the raw display in disabled state. Also fall back to masked icon
                        jToggleButtonEye.setSelected(false);
                        passwordField.setEchoChar(echoChar);
                        jToggleButtonEye.setIcon(iconEyeMasked);
                    }
                    jToggleButtonEye.setVisible(enabled);
                }
            }
        });
        //Generate the placeholder label
        final JLabel placeholderOverlay = new JLabel(placeholderText);
        placeholderOverlay.setFocusable(false);
        if (placeholderImage != null) {
            int imageHeightPlaceholder = passwordField.getPreferredSize().height - passwordField.getMargin().top * 2;
            ImageIcon placeholderIcon = new ImageIcon(placeholderImage.toMinResolution(imageHeightPlaceholder));
            placeholderOverlay.setIcon(ImageUtil.grayImage(placeholderIcon));
        }
        //UX setting: The placeholders font should be smaller than the standard font used in the password field
        Font placeholderFont = passwordField.getFont().deriveFont((float) passwordField.getFont().getSize() - 1);
        placeholderOverlay.setFont(placeholderFont);
        placeholderOverlay.setForeground(UIManager.getColor("FormattedTextField.inactiveForeground"));
        int placeholderMarginLeft = passwordField.getMargin().left * 2;
        Border placeholderMargin = new EmptyBorder(
                passwordField.getMargin().top,
                placeholderMarginLeft,
                passwordField.getMargin().bottom,
                0);
        placeholderOverlay.setBorder(placeholderMargin);
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (passwordField.getPassword().length > 0) {
                    placeholderOverlay.setVisible(false);
                } else {
                    placeholderOverlay.setVisible(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (passwordField.getPassword().length > 0) {
                    placeholderOverlay.setVisible(false);
                } else {
                    placeholderOverlay.setVisible(true);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (passwordField.getPassword().length > 0) {
                    placeholderOverlay.setVisible(false);
                } else {
                    placeholderOverlay.setVisible(true);
                }
            }
        });

        //Set the text cursor to the placeholder overlay - this is set to the underlaying
        //text field but will not trigger because it is below the overlay label
        placeholderOverlay.setCursor(passwordField.getCursor());
        final JLayeredPane layeredPane = new JLayeredPane();
        //do not display the whole layer if the password field turns invisible.
        //As the visible state is just a flag an anchestolistener on the parent structure
        //is required to be informed on the visible state
        layeredPane.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                placeholderOverlay.setVisible(passwordField.isVisible()
                        && passwordField.getPassword().length == 0);
                layeredPane.setVisible(passwordField.isVisible());
                jToggleButtonEye.setVisible(passwordField.isVisible() && passwordField.isEnabled());
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                placeholderOverlay.setVisible(passwordField.isVisible()
                        && passwordField.getPassword().length == 0);
                layeredPane.setVisible(passwordField.isVisible());
                jToggleButtonEye.setVisible(passwordField.isVisible() && passwordField.isEnabled());
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                placeholderOverlay.setVisible(passwordField.isVisible()
                        && passwordField.getPassword().length == 0);
                layeredPane.setVisible(passwordField.isVisible());
                jToggleButtonEye.setVisible(passwordField.isVisible() && passwordField.isEnabled());
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //find out the width of the placeholder text. If it exceeds the password field length
                //the size of the password field must be recomputed. It's not sure that this will
                //finally result in the requested size as resizing the components is in the
                //resposibility of the layout manager. This will not set the absolute size - it will
                //just set recommendations (min size, preferred size)
                try {
                    AffineTransform affinetransform = new AffineTransform();
                    FontRenderContext fontRenderContext = new FontRenderContext(affinetransform, true, true);
                    int placeholderTextWidthInPixels = (int) (placeholderFont.getStringBounds(
                            placeholderText, fontRenderContext)
                            .getWidth());
                    if (placeholderTextWidthInPixels
                            > (passwordField.getPreferredSize().width
                            - jToggleButtonEye.getPreferredSize().width
                            - placeholderMarginLeft
                            - jToggleButtonEye.getMargin().left)) {
                        passwordField.setPreferredSize(
                                new Dimension(
                                        placeholderTextWidthInPixels
                                        + jToggleButtonEye.getPreferredSize().width
                                        + placeholderMarginLeft
                                        + jToggleButtonEye.getMargin().left,
                                        passwordField.getPreferredSize().height));
                        passwordField.setMinimumSize(
                                new Dimension(
                                        placeholderTextWidthInPixels
                                        + jToggleButtonEye.getMinimumSize().width
                                        + placeholderMarginLeft
                                        + jToggleButtonEye.getMargin().left,
                                        passwordField.getMinimumSize().height));
                    }
                } catch (Throwable e) {
                    //the underlaying password field if not part of the swing graphics hierarchy and
                    //the system is unable to compute the font size. In this seldom case just skip the resize
                }
                Dimension passwordFieldPreferredSize = passwordField.getPreferredSize();
                Dimension passwordFieldMinSize = passwordField.getMinimumSize();
                Dimension passwordFieldMaxSize = passwordField.getMaximumSize();
                Dimension placeholderOverlayPreferredSize = new Dimension(
                        passwordField.getPreferredSize().width - jToggleButtonEye.getPreferredSize().width,
                        passwordField.getPreferredSize().height);
                Dimension placeholderOverlayMinSize = new Dimension(
                        passwordField.getMinimumSize().width - jToggleButtonEye.getMinimumSize().width,
                        passwordField.getMinimumSize().height);
                Dimension placeholderOverlayMaxSize = new Dimension(
                        passwordField.getMaximumSize().width - jToggleButtonEye.getMaximumSize().width,
                        passwordField.getMaximumSize().height);
                placeholderOverlay.setPreferredSize(placeholderOverlayPreferredSize);
                placeholderOverlay.setMinimumSize(placeholderOverlayMinSize);
                placeholderOverlay.setMaximumSize(placeholderOverlayMaxSize);
                GridBagLayout parentLayout = (GridBagLayout) parentComponent.getLayout();
                GridBagConstraints constraints = (GridBagConstraints) parentLayout.getConstraints(passwordField).clone();
                parentComponent.getLayout().removeLayoutComponent(passwordField);
                layeredPane.setPreferredSize(passwordFieldPreferredSize);
                layeredPane.setMaximumSize(passwordFieldMaxSize);
                layeredPane.setMinimumSize(passwordFieldMinSize);
                layeredPane.setLayout(new SpringLayout());
                layeredPane.add(passwordField, JLayeredPane.DEFAULT_LAYER);
                //The palette layer is above the default layer
                layeredPane.add(jToggleButtonEye, JLayeredPane.PALETTE_LAYER);
                layeredPane.add(placeholderOverlay, JLayeredPane.PALETTE_LAYER);
                SpringLayout springLayout = (SpringLayout) layeredPane.getLayout();
                springLayout.putConstraint(SpringLayout.WEST, passwordField, 0, SpringLayout.WEST, layeredPane);
                springLayout.putConstraint(SpringLayout.EAST, passwordField, 0, SpringLayout.EAST, layeredPane);
                springLayout.putConstraint(SpringLayout.NORTH, passwordField, 0, SpringLayout.NORTH, layeredPane);
                springLayout.putConstraint(SpringLayout.SOUTH, passwordField, 0, SpringLayout.SOUTH, layeredPane);
                springLayout.putConstraint(SpringLayout.EAST, jToggleButtonEye, 0, SpringLayout.EAST, layeredPane);
                springLayout.putConstraint(SpringLayout.NORTH, jToggleButtonEye, 0, SpringLayout.NORTH, layeredPane);
                springLayout.putConstraint(SpringLayout.SOUTH, jToggleButtonEye, 0, SpringLayout.SOUTH, layeredPane);
                springLayout.putConstraint(SpringLayout.WEST, placeholderOverlay, 0, SpringLayout.WEST, layeredPane);
                springLayout.putConstraint(SpringLayout.NORTH, placeholderOverlay, 0, SpringLayout.NORTH, layeredPane);
                springLayout.putConstraint(SpringLayout.SOUTH, placeholderOverlay, 0, SpringLayout.SOUTH, layeredPane);
                parentComponent.add(layeredPane, constraints);
                parentComponent.invalidate();
                parentComponent.validate();
            }
        });

    }

}
