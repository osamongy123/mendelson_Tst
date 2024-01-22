//$Header: /as2/de/mendelson/util/uinotification/UINotification.java 28    11/05/22 9:45 Heller $
package de.mendelson.util.uinotification;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Main UI Notification
 *
 * @author S.Heller
 * @version $Revision: 28 $
 */
public class UINotification implements INotificationHandler {

    protected final static MendelsonMultiResolutionImage IMAGE_SUCCESS
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/uinotification/notification_ok.svg",
                    NotificationPanel.IMAGESIZE_ICON, NotificationPanel.IMAGESIZE_ICON * 2);
    protected final static MendelsonMultiResolutionImage IMAGE_ERROR
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/uinotification/notification_error.svg",
                    NotificationPanel.IMAGESIZE_ICON, NotificationPanel.IMAGESIZE_ICON * 2);
    protected final static MendelsonMultiResolutionImage IMAGE_WARNING
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/uinotification/notification_warning.svg",
                    NotificationPanel.IMAGESIZE_ICON, NotificationPanel.IMAGESIZE_ICON * 2);
    protected final static MendelsonMultiResolutionImage IMAGE_INFORMATION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/uinotification/notification_information.svg",
                    NotificationPanel.IMAGESIZE_ICON, NotificationPanel.IMAGESIZE_ICON * 2);

    public static final Color DEFAULT_COLOR_BACKGROUND_SUCCESS_LIGHT = NotificationPanel.DEFAULT_COLOR_BACKGROUND_SUCCESS_LIGHT;
    public static final Color DEFAULT_COLOR_BACKGROUND_SUCCESS_DARK = NotificationPanel.DEFAULT_COLOR_BACKGROUND_SUCCESS_DARK;
    public static final Color DEFAULT_COLOR_BACKGROUND_WARNING_LIGHT = NotificationPanel.DEFAULT_COLOR_BACKGROUND_WARNING_LIGHT;
    public static final Color DEFAULT_COLOR_BACKGROUND_WARNING_DARK = NotificationPanel.DEFAULT_COLOR_BACKGROUND_WARNING_DARK;
    public static final Color DEFAULT_COLOR_BACKGROUND_ERROR_LIGHT = NotificationPanel.DEFAULT_COLOR_BACKGROUND_ERROR_LIGHT;
    public static final Color DEFAULT_COLOR_BACKGROUND_ERROR_DARK = NotificationPanel.DEFAULT_COLOR_BACKGROUND_ERROR_DARK;
    public static final Color DEFAULT_COLOR_BACKGROUND_INFORMATION_LIGHT = NotificationPanel.DEFAULT_COLOR_BACKGROUND_INFORMATION_LIGHT;
    public static final Color DEFAULT_COLOR_BACKGROUND_INFORMATION_DARK = NotificationPanel.DEFAULT_COLOR_BACKGROUND_INFORMATION_DARK;
    public static final Color DEFAULT_COLOR_FOREGROUND_DETAILS = NotificationPanel.DEFAULT_COLOR_FOREGROUND_DETAILS;
    public static final Color DEFAULT_COLOR_FOREGROUND_TITLE = NotificationPanel.DEFAULT_COLOR_FOREGROUND_TITLE;

    public static final int START_POS_LEFT_LOWER = 1;
    public static final int START_POS_RIGHT_LOWER = 2;
    public static final int START_POS_LEFT_UPPER = 3;
    public static final int START_POS_RIGHT_UPPER = 4;

    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_ERROR = 3;
    public static final int TYPE_INFORMATION = 4;

    public static final int INTERACTION_TYPE_INTERNAL_STACKED_FRAMES = 1;
    public static final int INTERACTION_TYPE_MESSAGE_DIALOGS = 2;

    private Color backgroundSuccessLight = DEFAULT_COLOR_BACKGROUND_SUCCESS_LIGHT;
    private Color backgroundSuccessDark = DEFAULT_COLOR_BACKGROUND_SUCCESS_DARK;
    private Color backgroundWarningLight = DEFAULT_COLOR_BACKGROUND_WARNING_LIGHT;
    private Color backgroundWarningDark = DEFAULT_COLOR_BACKGROUND_WARNING_DARK;
    private Color backgroundErrorLight = DEFAULT_COLOR_BACKGROUND_ERROR_LIGHT;
    private Color backgroundErrorDark = DEFAULT_COLOR_BACKGROUND_ERROR_DARK;
    private Color backgroundInformationLight = DEFAULT_COLOR_BACKGROUND_INFORMATION_LIGHT;
    private Color backgroundInformationDark = DEFAULT_COLOR_BACKGROUND_INFORMATION_DARK;

    private Color foregroundDetails = DEFAULT_COLOR_FOREGROUND_DETAILS;
    private Color foregroundTitle = DEFAULT_COLOR_FOREGROUND_TITLE;

    private Color crossColor = Color.GRAY;
    private Color crossColorMouseOver = Color.WHITE;
    
    protected final static String UIMANAGER_KEY_FOREGROUND = "controlText";
    protected final static String UIMANAGER_KEY_BACKGROUND = "controlHighlight";
    protected final static String UIMANAGER_KEY_CROSS = "controlText";
    protected final static String UIMANAGER_KEY_CROSS_MOUSEOVER = "controlHighlight";

    /**
     * How long is a single notification frame visible?
     */
    public final static long DEFAULT_NOTIFICATION_DISPLAY_TIME_IN_MS = 4500;
    /**
     * Fade out time
     */
    public final static long DEFAULT_NOTIFICATION_DISPLAY_TIME_FADEOUT_IN_MS = 1000;
    /**
     * Fade in time
     */
    public final static long DEFAULT_NOTIFICATION_DISPLAY_TIME_FADEIN_IN_MS = 200;
    /**
     * At which opacity should each notification frame disappear?
     */
    public final static float VISIBLE_OPACITY_THRESHOLD = 0.3f;

    private JFrame anchorFrame = null;
    /**
     * keeps this as singleton
     */
    private static UINotification instance;
    private int startPosition = START_POS_RIGHT_LOWER;

    private int gapBetweenNotifications = 1;
    private int gapToLeftRight = 0;
    private int gapToUpperLower = 0;

    private long notificationDisplayTime = DEFAULT_NOTIFICATION_DISPLAY_TIME_IN_MS;
    private long notificationDisplayTimeFadeout = DEFAULT_NOTIFICATION_DISPLAY_TIME_FADEOUT_IN_MS;
    private long notificationDisplayTimeFadeIn = DEFAULT_NOTIFICATION_DISPLAY_TIME_FADEIN_IN_MS;

    private int notificationHeight = 80;
    private int notificationWidth = 400;

    private int interactionType = INTERACTION_TYPE_INTERNAL_STACKED_FRAMES;

    private final List<NotificationWindow> notificationList
            = Collections.synchronizedList(new ArrayList<NotificationWindow>());

    /**
     * Resourcebundle to localize the GUI
     */
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleUINotification.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Singleton for the whole application
     */
    public static synchronized UINotification instance() {
        if (instance == null) {
            instance = new UINotification();
        }
        return instance;
    }

    private UINotification() {
    }

    /**
     * Binds the notification panels to the passed frame. It is required to
     * initialize the notification system using this method first before using
     * it
     *
     * @param anchorFrame
     */
    public UINotification setAnchor(JFrame anchorFrame) {
        this.anchorFrame = anchorFrame;
        this.anchorFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                UINotification.this.notificationPositionsHaveChanged();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                UINotification.this.notificationPositionsHaveChanged();
            }
        });
        this.anchorFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowIconified(WindowEvent e) {
                UINotification.this.anchorWindowIconified();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                UINotification.this.anchorWindowDeIconified();
            }

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
                UINotification.this.anchorApplicationActivated();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                UINotification.this.anchorApplicationDeActivated();
            }
        });
        return (this);
    }

    /**
     * Allows to set gaps between the notifications, between the upper and lower
     * frame border and to the left/right frame border
     */
    public UINotification setGaps(int gapBetweenNotifications, int gapToLeftRight, int gapToUpperLower) {
        this.gapBetweenNotifications = gapBetweenNotifications;
        this.gapToLeftRight = gapToLeftRight;
        this.gapToUpperLower = gapToUpperLower;
        return (this);
    }

    /**
     * Redefines the used background colors for the panels - takes the default
     * colors defined in the UI manager and also sets the dark mode colors if
     * requested
     */
    public UINotification setAllColorsDefaultFromUIManager(boolean darkMode) {
        this.setForegroundColorsDefaultFromUIManager(darkMode);
        this.setBackgroundColorsDefaultFromUIManager(darkMode);
        this.setCrossColorsDefaultFromUIManager(darkMode);
        return (this);
    }

    /**
     * Redefines the used cross colors for the panels - takes the default colors
     * defined in the UI manager and also sets the dark mode colors if requested
     */
    public UINotification setCrossColorsDefaultFromUIManager(boolean darkMode) {
        if (UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_CROSS) != null) {
            this.crossColor = UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_CROSS);
        }
        if (UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_CROSS_MOUSEOVER) != null) {
            this.crossColorMouseOver = UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_CROSS_MOUSEOVER);
        }
        return (this);
    }

    /**
     * Redefines the used cross colors for the panels - takes the default colors
     * defined in the UI manager and also sets the dark mode colors if requested
     */
    public UINotification setCrossColors(Color crossColor, Color crossColorMouseOver) {
        this.crossColor = crossColor;
        this.crossColorMouseOver = crossColorMouseOver;
        return (this);
    }

    /**
     * Redefines the used background colors for the panels - takes the default
     * colors defined in the UI manager and also sets the dark mode colors if
     * requested
     */
    public UINotification setBackgroundColorsDefaultFromUIManager(boolean darkMode) {
        this.backgroundSuccessDark = new Color(0, 104, 55);
        if (UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_BACKGROUND) != null) {
            this.backgroundSuccessLight = UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_BACKGROUND);
            this.backgroundWarningLight = UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_BACKGROUND);
            this.backgroundErrorLight = UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_BACKGROUND);
            this.backgroundInformationLight = UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_BACKGROUND);
        }
        this.backgroundWarningDark = new Color(255, 176, 59);
        this.backgroundErrorDark = new Color(193, 39, 45);
        this.backgroundInformationDark = new Color(0, 113, 188);
        if (darkMode) {
            this.backgroundSuccessDark = ColorUtil.darkenColor(this.backgroundSuccessDark, 0.2f);
            this.backgroundInformationDark = ColorUtil.darkenColor(this.backgroundInformationDark, 0.2f);
            this.backgroundWarningDark = ColorUtil.darkenColor(this.backgroundWarningDark, 0.2f);
            this.backgroundErrorDark = ColorUtil.darkenColor(this.backgroundErrorDark, 0.2f);
        }
        return (this);
    }

    /**
     * Redefines the used foreground colors for the panels - takes the default
     * colors defined in the UI manager and also sets the dark mode colors if
     * requested
     */
    public UINotification setForegroundColorsDefaultFromUIManager(boolean darkMode) {
        if (UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_FOREGROUND) != null) {
            this.foregroundTitle = UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_FOREGROUND);
            this.foregroundDetails = UIManager.getLookAndFeelDefaults().getColor(UIMANAGER_KEY_FOREGROUND);
        }
        return (this);
    }

    /**
     * Redefines the used background colors for the panels
     */
    public UINotification setBackgroundColors(
            Color backgroundSuccessLight,
            Color backgroundSuccessDark,
            Color backgroundWarningLight,
            Color backgroundWarningDark,
            Color backgroundErrorLight,
            Color backgroundErrorDark,
            Color backgroundInformationLight,
            Color backgroundInformationDark) {
        this.backgroundSuccessLight = backgroundSuccessLight;
        this.backgroundSuccessDark = backgroundSuccessDark;
        this.backgroundInformationLight = backgroundInformationLight;
        this.backgroundInformationDark = backgroundInformationDark;
        this.backgroundWarningLight = backgroundWarningLight;
        this.backgroundWarningDark = backgroundWarningDark;
        this.backgroundErrorLight = backgroundErrorLight;
        this.backgroundErrorDark = backgroundErrorDark;
        return (this);
    }

    /**
     * Redefines the used foreground colors for the panels
     */
    public UINotification setForegroundColors(Color foregroundTitle, Color foregroundDetails) {
        this.foregroundTitle = foregroundTitle;
        this.foregroundDetails = foregroundDetails;
        return (this);
    }

    /**
     * Sets the size of a single notification. Changes the default values
     */
    public UINotification setSize(int width, int heigth) {
        this.notificationHeight = heigth;
        this.notificationWidth = width;
        return (this);
    }

    /**
     * Sets the full time to display a single notification and the time after it
     * should start to fade out. If fading out is not supported by the graphic
     * configuration this value is ignored and there is no fadeout
     *
     * @param notificationDisplayTime Fade in time of the notification
     * @param notificationDisplayTime Staying display time of a notification
     * @param notificationDisplayTimeFadeOut Fade out time of the display
     * @return
     */
    public UINotification setTiming(long notificationDisplayTimeFadeIn, long notificationDisplayTime, long notificationDisplayTimeFadeOut) {
        this.notificationDisplayTime = notificationDisplayTime;
        this.notificationDisplayTimeFadeout = notificationDisplayTimeFadeOut;
        this.notificationDisplayTimeFadeIn = notificationDisplayTimeFadeIn;
        return (this);
    }

    /**
     * Defines the start point of the notifications. If this is a lower position
     * the notifications are stacked up - else they are stacked down
     */
    public UINotification setStart(int START_POS) {
        this.startPosition = START_POS;
        if (this.startPosition != START_POS_LEFT_LOWER
                && startPosition != START_POS_RIGHT_UPPER
                && startPosition != START_POS_LEFT_UPPER
                && startPosition != START_POS_RIGHT_LOWER) {
            throw new IllegalArgumentException("UINotification: Illegal startposition");
        }
        return (this);
    }

    /**
     * Adds a new notification to the UI notification system. This is a new
     * panel that contains an image and a text and will disappear after some
     * time - notifications are stacked
     *
     * @param image The image to display. If this is null a default image for
     * the notification type is displayed (warning, error, ok)
     * @param NOTIFICATION_TYPE One of the notification types that are defined
     * in this class. The background color of the notification depends on the
     * type (green/yellow/red..). One of UINotification.TYPE_OK,
     * UINotification.TYPE_WARNING, UINotification.TYPE_ERROR,
     * UINotification.TYPE_INFORMATION
     * @param notificationDetails The text that is displayed. It is folded
     * automatically
     * @param notificationTitle The title of the notification - not folded -
     * means you have to ensure a short title. If this is null, the type OK,
     * WARNING, ERROR is displayed in the localized language of the current
     * locale
     * @throws IllegalArgumentException This exception is thrown if the
     * notification system is not initialized using
     * UINotification().instance().setAnchor( JFrame frame )
     */
    public void addNotification(MendelsonMultiResolutionImage image,
            final int NOTIFICATION_TYPE,
            String notificationTitle,
            String notificationDetails) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    _addNotification(image, NOTIFICATION_TYPE, notificationTitle, notificationDetails);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Adds a new notification to the UI notification system. This is a new
     * panel that contains an image and a text and will disappear after some
     * time - notifications are stacked
     *
     * @param image The image to display. If this is null a default image for
     * the notification type is displayed (warning, error, ok)
     * @param NOTIFICATION_TYPE One of the notification types that are defined
     * in this class. The background color of the notification depends on the
     * type (green/yellow/red..). One of UINotification.TYPE_OK,
     * UINotification.TYPE_WARNING, UINotification.TYPE_ERROR,
     * UINotification.TYPE_INFORMATION
     * @param notificationDetails The text that is displayed. It is folded
     * automatically
     * @param notificationTitle The title of the notification - not folded -
     * means you have to ensure a short title. If this is null, the type OK,
     * WARNING, ERROR is displayed in the localized language of the current
     * locale
     * @throws IllegalArgumentException This exception is thrown if the
     * notification system is not initialized using
     * UINotification().instance().setAnchor( JFrame frame )
     */
    private void _addNotification(MendelsonMultiResolutionImage image,
            final int NOTIFICATION_TYPE,
            String notificationTitle,
            String notificationDetails) throws IllegalArgumentException {
        //check if the notifcation system has been already initialized
        if (this.anchorFrame == null) {
            throw new IllegalArgumentException(
                    "UINotification: No anchor frame assigned to UINotification - please initialize once with "
                    + "UINotification().instance().setAnchor( JFrame frame )");
        }
        //display notification title that depends on the type of the notification if none is set
        if (notificationTitle == null) {
            if (NOTIFICATION_TYPE == UINotification.TYPE_SUCCESS) {
                notificationTitle = rb.getResourceString("title.ok");
            } else if (NOTIFICATION_TYPE == UINotification.TYPE_ERROR) {
                notificationTitle = rb.getResourceString("title.error");
            } else if (NOTIFICATION_TYPE == UINotification.TYPE_WARNING) {
                notificationTitle = rb.getResourceString("title.warning");
            } else if (NOTIFICATION_TYPE == UINotification.TYPE_INFORMATION) {
                notificationTitle = rb.getResourceString("title.information");
            } else {
                notificationTitle = "--";
            }
        }
        if (this.interactionType == INTERACTION_TYPE_INTERNAL_STACKED_FRAMES) {
            NotificationWindow notificationWindow = new NotificationWindow(
                    this.anchorFrame,
                    image,
                    NOTIFICATION_TYPE,
                    notificationTitle, notificationDetails,
                    new Rectangle(0, 0, this.notificationWidth, this.notificationHeight),
                    this,
                    this.notificationDisplayTimeFadeIn,
                    this.notificationDisplayTime,
                    this.notificationDisplayTimeFadeout
            )
                    .setBackgroundColors(
                            this.backgroundSuccessLight,
                            this.backgroundSuccessDark,
                            this.backgroundWarningLight,
                            this.backgroundWarningDark,
                            this.backgroundErrorLight,
                            this.backgroundErrorDark,
                            this.backgroundInformationLight,
                            this.backgroundInformationDark
                    )
                    .setForegroundColors(this.foregroundTitle, this.foregroundDetails)
                    .setCrossColors(this.crossColor, this.crossColorMouseOver);
            synchronized (this.notificationList) {
                this.notificationList.add(0, notificationWindow);
                this.notificationPositionsHaveChanged();
            }
            notificationWindow.setVisible(true);
        } else {
            int optionPaneMessageType = JOptionPane.INFORMATION_MESSAGE;
            if (NOTIFICATION_TYPE == UINotification.TYPE_ERROR) {
                optionPaneMessageType = JOptionPane.ERROR_MESSAGE;
            } else if (NOTIFICATION_TYPE == UINotification.TYPE_WARNING) {
                optionPaneMessageType = JOptionPane.WARNING_MESSAGE;
            }
            image = UINotification.getMultiresolutionImage(image, NOTIFICATION_TYPE);
            notificationDetails = this.foldString(notificationDetails, "\n", 80);
            JOptionPane.showMessageDialog(this.anchorFrame,
                    notificationDetails,
                    notificationTitle, optionPaneMessageType,
                    new ImageIcon(image.toMinResolution(32)));
        }
    }

    /**
     * Adds a new notification to the UI notification system. This is a
     * notification of an exception that occured in the UI
     */
    public void addNotification(Throwable e) {
        this.addNotification(
                null,
                UINotification.TYPE_ERROR,
                e.getClass().getSimpleName(),
                "[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
    }

    /**
     * Is called if the notifications changed their position either by deleting
     * them, an anchor frame move or a new notification
     */
    private void notificationPositionsHaveChanged() {
        synchronized (this.notificationList) {
            for (NotificationWindow frame : this.notificationList) {
                Point newPosition = this.computeNotificationPosition(frame);
                frame.setLocation(newPosition);
            }
        }
    }

    /**
     * The anchor turned to iconified state - the notifications should be
     * hidden, too
     *
     */
    private void anchorWindowIconified() {
        synchronized (this.notificationList) {
            for (NotificationWindow frame : this.notificationList) {
                frame.setVisible(false);
            }
        }
    }

    /**
     * The anchor turned to iconified state - the notifications should be
     * visible again, too
     *
     */
    private void anchorWindowDeIconified() {
        synchronized (this.notificationList) {
            for (NotificationWindow frame : this.notificationList) {
                frame.setVisible(true);
            }
        }
    }

    /**
     * The anchor application has been activated
     *
     */
    private void anchorApplicationActivated() {
        synchronized (this.notificationList) {
            for (NotificationWindow frame : this.notificationList) {
                frame.setVisible(true);
            }
        }
    }

    /**
     * The anchor window has been deactivated
     *
     */
    private void anchorApplicationDeActivated() {
        synchronized (this.notificationList) {
            for (NotificationWindow frame : this.notificationList) {
                frame.setVisible(false);
            }
        }
    }

    /**
     * Returns the Point where the notification should be located
     */
    public Point computeNotificationPosition(NotificationWindow notificationFrame) {
        synchronized (this.notificationList) {
            int index = this.notificationList.indexOf(notificationFrame);
            int x = 0;
            int y = 0;
            if (this.startPosition == START_POS_LEFT_LOWER
                    || this.startPosition == START_POS_LEFT_UPPER) {
                x = this.anchorFrame.getX() + this.gapToLeftRight;
            } else {
                x = this.anchorFrame.getX() + this.anchorFrame.getWidth() - this.notificationWidth
                        - this.gapToLeftRight;
            }
            if (this.startPosition == START_POS_LEFT_LOWER
                    || this.startPosition == START_POS_RIGHT_LOWER) {
                y = this.anchorFrame.getY() + this.anchorFrame.getHeight() - (index + 1) * this.notificationHeight
                        - this.gapToUpperLower - (index) * this.gapBetweenNotifications;
            } else {
                y = this.anchorFrame.getY() + this.gapToUpperLower + (index) * this.gapBetweenNotifications;
            }
            return (new Point(x, y));
        }

    }

    /**
     * Deletes a single notification from the internal notification list
     *
     * @param notificationFrame
     */
    @Override
    public void deleteNotification(NotificationWindow notificationFrame) {
        synchronized (this.notificationList) {
            this.notificationList.remove(notificationFrame);
            this.notificationPositionsHaveChanged();
        }
    }

    /**
     * @return the interactionType
     */
    public int getInteractionType() {
        return interactionType;
    }

    /**
     * @param interactionType Defines in which format notifications should be
     * displayed. One of
     * UINotificiation.INTERACTION_TYPE_INTERNAL_STACKED_FRAMES,
     * UINotification.INTERACTION_TYPE_MESSAGE_DIALOGS
     */
    public void setInteractionType(final int INTERACTION_TYPE) {
        this.interactionType = INTERACTION_TYPE;
    }

    public static MendelsonMultiResolutionImage getMultiresolutionImage(MendelsonMultiResolutionImage image, final int NOTIFICATION_TYPE) {
        //no image passed - take default image
        if (image == null) {
            image = UINotification.IMAGE_SUCCESS;
            if (NOTIFICATION_TYPE == UINotification.TYPE_WARNING) {
                image = UINotification.IMAGE_WARNING;
            } else if (NOTIFICATION_TYPE == UINotification.TYPE_ERROR) {
                image = UINotification.IMAGE_ERROR;
            } else if (NOTIFICATION_TYPE == UINotification.TYPE_INFORMATION) {
                image = UINotification.IMAGE_INFORMATION;
            }
        }
        return (image);
    }

    /**
     * Folds a string using the passed delimiter where the max line length is
     * the passed lineLenght
     *
     * @param source Source string to use
     * @param delimiter Delimiter to add at the folding point
     * @param lineLength Max line length of the result
     */
    private String foldString(String source, String delimiter, int lineLength) {
        if (source == null) {
            return ("null");
        }
        StringBuilder result = new StringBuilder();
        int linePos = 0;
        for (int i = 0; i < source.length(); i++) {
            char singleChar = source.charAt(i);
            if (singleChar == ' ' && linePos >= lineLength) {
                result.append(delimiter);
                linePos = 0;
            } else {
                result.append(singleChar);
                linePos++;
            }
        }
        return (result.toString());
    }

    /**Generates the "notification close" cross image in a requested color
     * 
     * @param size both height and width, its a square
     * @param color The paint color for the cross
     * @return 
     */
    protected static ImageIcon generateCrossImage(int size, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.createGraphics();
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        renderingHints.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC));
        renderingHints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));
        renderingHints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
        renderingHints.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY));
        renderingHints.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE));
        g.setRenderingHints(renderingHints);
        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, 0, size - 1, size - 1);
        g.drawLine(0, size - 1, size - 1, 0);
        g.dispose();
        return (new ImageIcon(image));
    }

}
