//$Header: /as2/de/mendelson/util/clientserver/ServerHelloMessage.java 1     24/02/22 12:55 Heller $
package de.mendelson.util.clientserver;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Message that is sent from the server to the client once the client logged in
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class ServerHelloMessage implements Serializable{

    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARNING = 3;
    public static final int LEVEL_SEVERE = 4;
    
    public static final long serialVersionUID = 1L;
    private final String message;    
    private int level = LEVEL_DEBUG;
    
    /**Creates a server hello message with the level LEVEL_DEBUG*/
    public ServerHelloMessage(String message) {
        this.message = message;
    }

    /**Creates a server hello message with a passed level
     * @param LEVEL one of LEVEL_DEBUG,
     * LEVEL_INFO, LEVEL_WARNING, LEVEL_SEVERE
     */
    public ServerHelloMessage(String message, final int LEVEL) {
        this.message = message;
        this.level = LEVEL;
    }
    
    /**
     * @return the level - one of LEVEL_DEBUG,
     * LEVEL_INFO, LEVEL_WARNING, LEVEL_SEVERE
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set, one of LEVEL_DEBUG,
     * LEVEL_INFO, LEVEL_WARNING, LEVEL_SEVERE
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    
    

}
