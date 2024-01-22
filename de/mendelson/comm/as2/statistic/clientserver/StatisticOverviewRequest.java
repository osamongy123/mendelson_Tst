//$Header: /as2/de/mendelson/comm/as2/statistic/clientserver/StatisticOverviewRequest.java 2     4/06/18 12:21p Heller $
package de.mendelson.comm.as2.statistic.clientserver;

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
public class StatisticOverviewRequest extends ClientServerMessage implements Serializable {
    
    public static final long serialVersionUID = 1L;
    private String as2Identification;
    
    public StatisticOverviewRequest(String as2Identification) {
        this.as2Identification = as2Identification;
    }

    @Override
    public String toString() {
        return ("List statistic overview");
    }

    /**
     * @return the as2Identification
     */
    public String getAS2Identification() {
        return as2Identification;
    }

}
