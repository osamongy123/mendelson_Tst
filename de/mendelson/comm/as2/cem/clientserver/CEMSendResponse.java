//$Header: /as2/de/mendelson/comm/as2/cem/clientserver/CEMSendResponse.java 4     4/06/18 12:21p Heller $
package de.mendelson.comm.as2.cem.clientserver;

import de.mendelson.comm.as2.partner.Partner;
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
 * @version $Revision: 4 $
 */
public class CEMSendResponse extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    private List<Partner> informedPartner = null;

    public CEMSendResponse(CEMSendRequest request) {
        super(request);
    }

    @Override
    public String toString() {
        return ("Resonse to a CEM send request");
    }

    /**
     * @return the informed partner
     */
    public List<Partner> getInformedPartner() {
        if (this.informedPartner != null) {
            return (this.informedPartner);
        } else {
            return (new ArrayList<Partner>());
        }
    }

    /**
     * @param list the list to set
     */
    public void setInformedPartner(List<Partner> informedPartner) {
        this.informedPartner = informedPartner;
    }
}
