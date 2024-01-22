//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/PostprocessingException.java 2     10.09.20 12:57 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;

import de.mendelson.comm.as2.partner.Partner;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Exception that is thrown if a problem occured during the post processing
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class PostprocessingException extends Exception {

    
    private Partner sender;
    private Partner receiver;

    public PostprocessingException(String message, Partner sender, Partner receiver) {
        super( message );
        this.sender = sender;
        this.receiver = receiver;
    }
    
    /**
     * @return the sender
     */
    public Partner getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(Partner sender) {
        this.sender = sender;
    }

    /**
     * @return the receiver
     */
    public Partner getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(Partner receiver) {
        this.receiver = receiver;
    }

}
