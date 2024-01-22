//$Header: /as2/de/mendelson/comm/as2/statistic/clientserver/StatisticDetailResponse.java 2     4/06/18 12:21p Heller $
package de.mendelson.comm.as2.statistic.clientserver;

import de.mendelson.comm.as2.statistic.StatisticDetailEntry;
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
 * @version $Revision: 2 $
 */
public class StatisticDetailResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private List<StatisticDetailEntry > list = null;

    public StatisticDetailResponse(StatisticDetailRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("List statistic details");
    }

    /**
     * @return the list
     */
    public List<StatisticDetailEntry > getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<StatisticDetailEntry > list) {
        this.list = list;
    }
}
