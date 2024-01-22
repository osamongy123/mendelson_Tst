//$Header: /as2/de/mendelson/comm/as2/cem/clientserver/CEMDeleteRequest.java 2     4/06/18 12:21p Heller $
package de.mendelson.comm.as2.cem.clientserver;

import de.mendelson.comm.as2.cem.CEMEntry;
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
 * @version $Revision: 2 $
 */
public class CEMDeleteRequest extends ClientServerMessage implements Serializable {
    
    public static final long serialVersionUID = 1L;
    private CEMEntry entry = null;
    
    public CEMDeleteRequest(CEMEntry entry) {
        this.entry = entry;
    }


    @Override
    public String toString() {
        return ("Delete a single CEM entry");
    }

    /**
     * @return the entry
     */
    public CEMEntry getEntry() {
        return entry;
    }

}
