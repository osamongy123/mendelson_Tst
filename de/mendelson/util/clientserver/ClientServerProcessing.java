//$Header: /as2/de/mendelson/util/clientserver/ClientServerProcessing.java 2     7.01.11 15:43 Heller $
package de.mendelson.util.clientserver;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import org.apache.mina.core.session.IoSession;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface for the server processing. All user defined requests that the server should perform 
 * are defined in this interface
 * 
 * @author S.Heller
 * @version $Revision: 2 $
 */
public interface ClientServerProcessing {

    /**Should return if the message could be processed*/
    public boolean process(IoSession session, ClientServerMessage object);
}
