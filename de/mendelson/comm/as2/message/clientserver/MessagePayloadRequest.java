//$Header: /as2/de/mendelson/comm/as2/message/clientserver/MessagePayloadRequest.java 2     4/06/18 12:21p Heller $
package de.mendelson.comm.as2.message.clientserver;

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
 * @version $Revision: 2 $
 */
public class MessagePayloadRequest extends ClientServerMessage implements Serializable{

    public static final long serialVersionUID = 1L;
    private String messageId = null;

    public MessagePayloadRequest(String messageId){
        this.messageId = messageId;
    }

    @Override
    public String toString(){
        return( "Message payload request" );
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }
  
}
