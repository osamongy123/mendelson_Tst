//$Header: /as2/de/mendelson/comm/as2/clientserver/message/PerformNotificationTestRequest.java 3     19.09.18 11:23 Heller $
package de.mendelson.comm.as2.clientserver.message;

import de.mendelson.util.systemevents.notification.NotificationData;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.Serializable;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Msg for the client server protocol
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class PerformNotificationTestRequest extends ClientServerMessage implements Serializable{
    
    public static final long serialVersionUID = 1L;
    private NotificationData notificationData;
    
    public PerformNotificationTestRequest(NotificationData notificationData){
        this.notificationData = notificationData;
    }
    
    @Override
    public String toString(){
        return( "Perform notification test" );
    }

    /**
     * @return the data
     */
    public NotificationData getNotificationData() {
        return notificationData;
    }

    
}
