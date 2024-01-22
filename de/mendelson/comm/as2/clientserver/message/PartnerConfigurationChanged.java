//$Header: /as2/de/mendelson/comm/as2/clientserver/message/PartnerConfigurationChanged.java 3     4/06/18 12:21p Heller $
package de.mendelson.comm.as2.clientserver.message;

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
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class PartnerConfigurationChanged extends ClientServerMessage implements Serializable{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public String toString(){
        return( "Partner configuration changed on client notification" );
    }
    
    
}
