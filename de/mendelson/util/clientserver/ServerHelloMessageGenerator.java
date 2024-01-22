//$Header: /as2/de/mendelson/util/clientserver/ServerHelloMessageGenerator.java 2     24/02/22 14:20 Heller $
package de.mendelson.util.clientserver;

import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface that generates a server hello message that could be displayed in the client once
 * the client connects to the server
 * @author S.Heller
 * @version $Revision: 2 $
 */
public interface ServerHelloMessageGenerator {

    public List<ServerHelloMessage> generateServerHelloMessages();
    
}
