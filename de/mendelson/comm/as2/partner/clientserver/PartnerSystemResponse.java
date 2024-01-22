//$Header: /as2/de/mendelson/comm/as2/partner/clientserver/PartnerSystemResponse.java 5     5/08/22 9:58 Heller $
package de.mendelson.comm.as2.partner.clientserver;

import de.mendelson.comm.as2.partner.PartnerSystem;
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
 * @version $Revision: 5 $
 */
public class PartnerSystemResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private List<PartnerSystem> partnerSystemList = new ArrayList<PartnerSystem>();

    public PartnerSystemResponse(PartnerSystemRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Request partner system");
    }

    /**
     * @return the partnerSystem
     */
    public List<PartnerSystem> getPartnerSystems() {
        return this.partnerSystemList;
    }

    /**
     * @param partnerSystem the partnerSystem to set
     */
    public void addPartnerSystems(List<PartnerSystem> partnerSystems) {
        if (partnerSystems != null) {
            this.partnerSystemList.addAll(partnerSystems);
        }
    }

}
