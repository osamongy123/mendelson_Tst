//$Header: /as2/de/mendelson/util/uinotification/INotificationHandler.java 2     28.01.20 14:26 Heller $package de.mendelson.util.uinotification;
package de.mendelson.util.uinotification;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface to handle requests of the notification frames
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public interface INotificationHandler {
    
    public void deleteNotification(NotificationWindow notificationFrame);
}
