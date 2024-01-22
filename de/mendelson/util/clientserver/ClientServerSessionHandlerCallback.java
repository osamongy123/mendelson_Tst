//$Header: /mendelson_business_integration/de/mendelson/util/clientserver/ClientServerSessionHandlerCallback.java 1     9/16/15 12:29p Heller $
package de.mendelson.util.clientserver;

import org.apache.mina.core.session.IoSession;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Client side protocol handler callback interface
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public interface ClientServerSessionHandlerCallback {

    public void clientDisconnected(IoSession session);

    public void clientLoggedIn(IoSession session);

}
