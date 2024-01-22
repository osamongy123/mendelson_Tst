//$Header: /oftp2/de/mendelson/util/clientserver/log/search/ServerlogfileSearchResponse.java 1     5.12.18 12:14 Heller $
package de.mendelson.util.clientserver.log.search;

import de.mendelson.util.clientserver.messages.ClientServerResponse;
import java.io.Serializable;
import java.util.ArrayList;
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
public class ServerlogfileSearchResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private List<Logline> resultList = new ArrayList<Logline>();
      
    public ServerlogfileSearchResponse(ServerlogfileSearchRequest request) {
        super(request);
    }
    
    /**
     * @return the event result List
     */
    public List<Logline> getSearchResults() {
        return (this.resultList);
    }

    /**
     * @param eventList the eventList to set
     */
    public void setLoglineResultList(List<Logline> eventList) {
        this.resultList.addAll(eventList);
    }

    @Override
    public String toString() {
        return ("Search for server log file entries");
    }

    

}
