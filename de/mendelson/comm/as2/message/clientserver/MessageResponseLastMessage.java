//$Header: /as2/de/mendelson/comm/as2/message/clientserver/MessageResponseLastMessage.java 3     6/22/18 4:21p Heller $
package de.mendelson.comm.as2.message.clientserver;

import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
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
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class MessageResponseLastMessage extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private AS2MessageInfo messageInfo = null;

    public MessageResponseLastMessage(MessageRequestLastMessage request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Message respond last message");
    }

    /**
     * @return the list
     */
    public AS2MessageInfo getInfo() {
        return messageInfo;
    }

    /**
     * @param list the list to set
     */
    public void setInfo(AS2MessageInfo messageInfo) {
        this.messageInfo = messageInfo;
    }

    
}
