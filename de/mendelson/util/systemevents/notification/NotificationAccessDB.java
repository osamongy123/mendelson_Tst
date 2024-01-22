//$Header: /as2/de/mendelson/util/systemevents/notification/NotificationAccessDB.java 5     28/07/22 13:06 Heller $
package de.mendelson.util.systemevents.notification;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface for all notification access implementations
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public interface NotificationAccessDB {

    /**
     * Reads the notification data from the db, there is only one available
     */
    public NotificationData getNotificationData();

    /**
     * Inserts a new notification date entry into the database
     */
    public void updateNotification(NotificationData data);
        
}
