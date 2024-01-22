//$Header: /mec_as2/de/mendelson/comm/as2/partner/clientserver/PartnerSystemRequest.java 3     18.12.20 14:25 Heller $
package de.mendelson.comm.as2.partner.clientserver;

import de.mendelson.comm.as2.partner.Partner;
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
 * @version $Revision: 3 $
 */
public class PartnerSystemRequest extends ClientServerMessage implements Serializable {

    public static final long serialVersionUID = 1L;
    
    public static final int TYPE_LIST_ALL = 1;
    public static final int TYPE_LIST_SINGLE = 2;
    
    private Partner partner = null;
    private int type = TYPE_LIST_ALL;

    public PartnerSystemRequest(final int TYPE) {
        this.type = TYPE;
    }

    @Override
    public String toString() {
        return ("Request partner system");
    }

    /**
     * @return the partner
     */
    public Partner getPartner() {
        return partner;
    }

    /**
     * @param partner the partner to set if the type is TYPE_LIST_SINGLE
     */
    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }
}
