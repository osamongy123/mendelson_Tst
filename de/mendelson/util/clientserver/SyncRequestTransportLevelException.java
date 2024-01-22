//$Header: /as2/de/mendelson/util/clientserver/SyncRequestTransportLevelException.java 1     12.01.11 17:54 Heller $
package de.mendelson.util.clientserver;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Exception that is thrown if a sync request failed on the transport level
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class SyncRequestTransportLevelException extends Exception{
    
    @Override
    public String getMessage(){
        return( "Sync request failed.");
    }

}
