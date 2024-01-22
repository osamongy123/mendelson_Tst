//$Header: /mec_as2/de/mendelson/comm/as2/ha/clientserver/ServerInstanceHAListResponse.java 1     2/02/22 15:13 Heller $
package de.mendelson.comm.as2.ha.clientserver;

import de.mendelson.comm.as2.ha.ServerInstanceHA;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.Serializable;
import java.util.List;
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
public class ServerInstanceHAListResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private List<ServerInstanceHA> list = null;

    public ServerInstanceHAListResponse(ServerInstanceHAListRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("List HA instances");
    }

    /**
     * @return the list
     */
    public List<ServerInstanceHA> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<ServerInstanceHA> list) {
        this.list = list;
    }
}
