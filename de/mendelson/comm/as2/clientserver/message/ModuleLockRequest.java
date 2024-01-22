//$Header: /as2/de/mendelson/comm/as2/clientserver/message/ModuleLockRequest.java 2     26.05.14 17:59 Heller $
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
 * @version $Revision: 2 $
 */
public class ModuleLockRequest extends ClientServerMessage implements Serializable{
        
    public static final int TYPE_SET = 1;
    public static final int TYPE_RELEASE = 2;
    public static final int TYPE_REFRESH = 3;
    public static final int TYPE_LOCK_INFO = 4;
    
    private String moduleName;
    private int type = TYPE_SET;

    public ModuleLockRequest( String moduleName, int type ){
        this.moduleName = moduleName;
        this.type = type;
    }

    @Override
    public String toString(){
        return( "Module lock request" );
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @return the moduleName
     */
    public String getModuleName() {
        return moduleName;
    }
    
    
}
