//$Header: /as2/de/mendelson/util/systemevents/notification/NotificationData.java 10    7/10/22 10:37 Heller $
package de.mendelson.util.systemevents.notification;

import de.mendelson.util.oauth2.OAuth2Config;
import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores the notification data for the mendelson products
 * @author S.Heller
 * @version $Revision: 10 $
 */
public abstract class NotificationData implements Serializable{

    public static final long serialVersionUID = 1L;
    
    public static final int SECURITY_PLAIN = 0;
    public static final int SECURITY_START_TLS = 1;
    public static final int SECURITY_TLS = 2;
    
    public abstract String getMailServer();
    public abstract int getMailServerPort();
    public abstract int getConnectionSecurity();
    public abstract boolean usesSMTPAuthCredentials();
    public abstract boolean usesSMTPAuthOAuth2();
    public abstract String getSMTPUser();
    public abstract char[] getSMTPPass();
    public abstract String getReplyTo();
    public abstract String getNotificationMail();
    public abstract int getMaxNotificationsPerMin();
    public abstract OAuth2Config getOAuth2Config();
    
}
