//$Header: /as2/de/mendelson/util/uinotification/NotificationWindow.java 21    10/05/22 13:18 Heller $package de.mendelson.util.uinotification;
package de.mendelson.util.uinotification;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Single notification panel
 *
 * @author S.Heller
 * @version $Revision: 21 $
 */
public class NotificationWindow extends JWindow implements MouseInputListener {

    /**
     * At which opacity should the frame disappear/appear?
     */
    private final static float VISIBLE_OPACITY_THRESHOLD = UINotification.VISIBLE_OPACITY_THRESHOLD;
    /**
     * The wait time per step of the internal fade out thread
     */
    private final long THREAD_WAIT_TIME_STEPS_IN_MS = 25;

    private final Runnable fadeout;
    private INotificationHandler notificationHandler;
    private boolean graphicSupportsTranslucentWindows = false;
    private boolean graphicSupportsShapedWindows = false;

    private final JFrame anchorFrame;
    private final NotificationPanel notificationPanel;
    private final JLabel closeCrossLabel;
    private final JPanel closeCrossPanel;
    private final JPanel notificationTypePanel;
    private final JPanel textPanel;

    private Color crossColor = Color.BLACK;
    private Color crossColorMouseOver = Color.WHITE;

    /**
     * @param anchorFrame Root frame for the notification position
     * @param image Image to display - there is a default if this is null which
     * depends on the notification type
     * @param NOTIFICATION_TYPE One of UINotification.TYPE_OK,
     * UINotification.TYPE_WARNING, UINotification.TYPE_ERROR
     * @param notificationTitle The title of the notification - not folded -
     * means you have to ensure a short title. If this is null, the type OK,
     * WARNING, ERROR is displayed in the localized language of the current
     * locale
     * @param notificationDetails Details of the notification - folded, means
     * this could be some longer if required
     * @param bounds
     * @param notificationHandler
     */
    public NotificationWindow(JFrame anchorFrame,
            MendelsonMultiResolutionImage image,
            final int NOTIFICATION_TYPE, String notificationTitle,
            String notificationDetails, Rectangle bounds,
            INotificationHandler notificationHandler,
            long notificationDisplayTimeFadeIn,
            long notificationDisplayTime,
            long notificationDisplayTimeFadeout
    ) {
        //do not block this window by any other window that is modal
        this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        this.anchorFrame = anchorFrame;
        //make this component invisible for the mouse
        this.addMouseListener(this);
        //make this component invisible for the mouse
        this.addMouseMotionListener(this);
        this.setAlwaysOnTop(anchorFrame.isActive());
        this.determineGraphicsCapabilities();
        if (this.graphicSupportsShapedWindows) {
            this.addComponentListener(new ComponentAdapter() {
                // Give the window a round rectangle shape.
                // If the window is resized, the shape is recalculated here.
                @Override
                public void componentResized(ComponentEvent e) {
                    RoundRectangle2D.Float shape = new RoundRectangle2D.Float(0, 0,
                            (int) getWidth(),
                            (int) getHeight(),
                            10f, 10f);
                    setShape(shape);
                    invalidate();
                    validate();
                }
            });
        }
        if (this.graphicSupportsTranslucentWindows) {
            this.setOpacity(1f);
        }
        this.setBounds(bounds);
        this.notificationHandler = notificationHandler;
        this.notificationPanel = new NotificationPanel(image, NOTIFICATION_TYPE,
                notificationTitle, notificationDetails, bounds, this.graphicSupportsShapedWindows);
        this.notificationTypePanel = this.notificationPanel.getNotificationTypePanel();
        this.textPanel = this.notificationPanel.getTextPanel();
        this.closeCrossPanel = this.notificationPanel.getCloseCrossPanel();
        this.closeCrossPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                NotificationWindow.this.setVisible(false);
                NotificationWindow.this.notificationHandler.deleteNotification(NotificationWindow.this);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Color backgroundColor = NotificationWindow.this.notificationTypePanel.getBackground();
                        NotificationWindow.this.closeCrossPanel.setBackground(backgroundColor);
                        Color bestCrossContrastColor = ColorUtil.getBestContrastColorAroundForeground(backgroundColor, crossColorMouseOver);
                        ImageIcon crossIcon = UINotification.generateCrossImage(NotificationPanel.IMAGESIZE_CLOSECROSS, bestCrossContrastColor);
                        NotificationWindow.this.closeCrossLabel.setIcon(crossIcon);
                    }
                });
            }

            @Override
            public void mouseExited(MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Color backgroundColor = NotificationWindow.this.textPanel.getBackground();
                        NotificationWindow.this.closeCrossPanel.setBackground(backgroundColor);
                        Color bestCrossContrastColor = ColorUtil.getBestContrastColorAroundForeground(backgroundColor, crossColor);
                        ImageIcon crossIcon = UINotification.generateCrossImage(NotificationPanel.IMAGESIZE_CLOSECROSS, bestCrossContrastColor);
                        NotificationWindow.this.closeCrossLabel.setIcon(crossIcon);
                    }
                });
            }
        });
        this.closeCrossLabel = this.notificationPanel.getCloseCrossLabel();
        this.closeCrossLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                NotificationWindow.this.setVisible(false);
                NotificationWindow.this.notificationHandler.deleteNotification(NotificationWindow.this);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Color backgroundColor = NotificationWindow.this.notificationTypePanel.getBackground();
                        NotificationWindow.this.closeCrossPanel.setBackground(backgroundColor);
                        Color bestCrossContrastColor = ColorUtil.getBestContrastColorAroundForeground(backgroundColor, crossColorMouseOver);
                        ImageIcon crossIcon = UINotification.generateCrossImage(NotificationPanel.IMAGESIZE_CLOSECROSS, bestCrossContrastColor);
                        NotificationWindow.this.closeCrossLabel.setIcon(crossIcon);
                    }
                });
            }

            @Override
            public void mouseExited(MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Color backgroundColor = NotificationWindow.this.textPanel.getBackground();
                        NotificationWindow.this.closeCrossPanel.setBackground(backgroundColor);
                        Color bestCrossContrastColor = ColorUtil.getBestContrastColorAroundForeground(backgroundColor, crossColor);
                        ImageIcon crossIcon = UINotification.generateCrossImage(NotificationPanel.IMAGESIZE_CLOSECROSS, bestCrossContrastColor);
                        NotificationWindow.this.closeCrossLabel.setIcon(crossIcon);
                    }
                });
            }
        });
        this.add(this.notificationPanel, BorderLayout.CENTER);

        this.fadeout = new Runnable() {
            float fadeInTime = (float) notificationDisplayTimeFadeIn;
            float fadeoutTime = (float) notificationDisplayTimeFadeout;
            float fullOpacityLoss = 1f - (float) VISIBLE_OPACITY_THRESHOLD;
            float fadeoutStepCount = (float) notificationDisplayTimeFadeout / (float) THREAD_WAIT_TIME_STEPS_IN_MS;
            float opacityLossPerStep = fullOpacityLoss / fadeoutStepCount;
            float fadeInStepCount = (float) notificationDisplayTimeFadeIn / (float) THREAD_WAIT_TIME_STEPS_IN_MS;
            float opacityGainPerStep = (1f - (float) (VISIBLE_OPACITY_THRESHOLD)) / fadeInStepCount;

            @Override
            public void run() {
                //fade in
                try {
                    if (NotificationWindow.this.graphicSupportsTranslucentWindows) {
                        NotificationWindow.this.setOpacity(VISIBLE_OPACITY_THRESHOLD);
                        while (NotificationWindow.this.getOpacity() < 1.0f) {
                            try {
                                Thread.sleep(THREAD_WAIT_TIME_STEPS_IN_MS);
                            } catch (InterruptedException e) {
                                //nop
                            }
                            float newOpacity = NotificationWindow.this.getOpacity() + opacityGainPerStep;
                            NotificationWindow.this.setOpacity(newOpacity);
                        }
                    } else {
                        //no transaprency - just delete the notification window
                        try {
                            Thread.sleep((long) fadeInTime);
                        } catch (InterruptedException e) {
                            //nop
                        }
                    }
                } catch (Exception e) {
                    //nop
                }
                //stay and display notification
                try {
                    Thread.sleep(notificationDisplayTime);
                } catch (InterruptedException e) {
                    //nop
                }
                try {
                    //Fade out notification
                    if (NotificationWindow.this.graphicSupportsTranslucentWindows) {
                        while (NotificationWindow.this.getOpacity() > VISIBLE_OPACITY_THRESHOLD) {
                            try {
                                Thread.sleep(THREAD_WAIT_TIME_STEPS_IN_MS);
                            } catch (InterruptedException e) {
                                //nop
                            }
                            float opacity = NotificationWindow.this.getOpacity();
                            if (opacity - opacityLossPerStep < VISIBLE_OPACITY_THRESHOLD) {
                                NotificationWindow.this.setOpacity(0);
                            } else {
                                NotificationWindow.this.setOpacity(opacity - opacityLossPerStep);
                            }
                        }
                    } else {
                        //no transaprency - just delete the notification window
                        try {
                            Thread.sleep((long) fadeoutTime);
                        } catch (InterruptedException e) {
                            //nop
                        }
                    }
                } finally {
                    NotificationWindow.this.notificationHandler.deleteNotification(NotificationWindow.this);
                    NotificationWindow.this.setVisible(false);
                    NotificationWindow.this.dispose();
                }
            }
        };
    }

    /**
     * Redefines the used background colors for the panels
     */
    public NotificationWindow setBackgroundColors(
            Color backgroundSuccessLight,
            Color backgroundSuccessDark,
            Color backgroundWarningLight,
            Color backgroundWarningDark,
            Color backgroundErrorLight,
            Color backgroundErrorDark,
            Color backgroundInformationLight,
            Color backgroundInformationDark) {
        this.notificationPanel.setBackgroundColors(
                backgroundSuccessLight,
                backgroundSuccessDark,
                backgroundWarningLight,
                backgroundWarningDark,
                backgroundErrorLight,
                backgroundErrorDark,
                backgroundInformationLight,
                backgroundInformationDark);
        return (this);
    }

    /**
     * Redefines the used foreground colors for the panels
     */
    public NotificationWindow setForegroundColors(Color foregroundTitle, Color foregroundDetails) {
        this.notificationPanel.setForegroundColors(foregroundTitle, foregroundDetails);
        return (this);
    }

    /**
     * Redefines the used cross colors for the panels
     */
    public NotificationWindow setCrossColors(Color crossColor, Color crossColorMouseOver) {
        this.notificationPanel.setCurrentCross(crossColor);
        this.crossColor = crossColor;
        this.crossColorMouseOver = crossColorMouseOver;
        return (this);
    }

    /**
     * Checks if transparent windows are possible, shapes etc..
     *
     */
    private void determineGraphicsCapabilities() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        //mainly this should be supported by every desktop system
        this.graphicSupportsTranslucentWindows = device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
        this.graphicSupportsShapedWindows = device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT);
    }

    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(this.fadeout);
            executor.shutdown();
        }
    }

    /**
     * Overwrite the equal method of object
     *
     * @param anObject object to compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof NotificationWindow && anObject == this) {
            return (true);
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.fadeout);
        return hash;
    }

    private void deliverMouseEventToUnderlayingComponent(MouseEvent e) {
        //on screen
        Point clickPoint = e.getLocationOnScreen();
        //convert click position from screen to relative position in the anchor frame
        SwingUtilities.convertPointFromScreen(clickPoint, this.anchorFrame);
        Component componentBelowClickPoint = SwingUtilities.getDeepestComponentAt(this.anchorFrame, clickPoint.x, clickPoint.y);
        if (componentBelowClickPoint != null) {
            //perform a mouse event in the component, e.g. a button click
            clickPoint = e.getLocationOnScreen();
            //convert from screenlocation to click position in component
            SwingUtilities.convertPointFromScreen(clickPoint, componentBelowClickPoint);
            //create mouse event and dispatch it to the underlaying component
            MouseEvent mouseEvent = new MouseEvent(componentBelowClickPoint, e.getID(), e.getWhen(),
                    e.getModifiersEx(), clickPoint.x, clickPoint.y, e.getClickCount(), e.isPopupTrigger(), e.getButton());
            componentBelowClickPoint.dispatchEvent(mouseEvent);
        }
    }

    /**
     * Makes this a mouse input listener
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        this.deliverMouseEventToUnderlayingComponent(e);
    }

    /**
     * Makes this a mouse input listener
     */
    @Override
    public void mousePressed(MouseEvent e) {
        this.deliverMouseEventToUnderlayingComponent(e);
    }

    /**
     * Makes this a mouse input listener
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        this.deliverMouseEventToUnderlayingComponent(e);
    }

    /**
     * Makes this a mouse input listener
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        this.deliverMouseEventToUnderlayingComponent(e);
    }

    /**
     * Makes this a mouse input listener
     */
    @Override
    public void mouseExited(MouseEvent e) {
        this.deliverMouseEventToUnderlayingComponent(e);
    }

    /**
     * Makes this a mouse input listener
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        this.deliverMouseEventToUnderlayingComponent(e);
    }

    /**
     * Makes this a mouse input listener
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        this.deliverMouseEventToUnderlayingComponent(e);
    }

}
