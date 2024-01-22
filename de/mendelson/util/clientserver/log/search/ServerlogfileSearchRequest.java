//$Header: /oftp2/de/mendelson/util/clientserver/log/search/ServerlogfileSearchRequest.java 1     5.12.18 12:14 Heller $
package de.mendelson.util.clientserver.log.search;

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
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class ServerlogfileSearchRequest extends ClientServerMessage implements Serializable {

    public static final long serialVersionUID = 1L;
    private ServerSideLogfileFilter filter;
    
    public ServerlogfileSearchRequest(ServerSideLogfileFilter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return ("Search for log file entries");
    }

    /**
     * @return the search filter
     */
    public ServerSideLogfileFilter getFilter() {
        return( this.filter );
    }

    
}
