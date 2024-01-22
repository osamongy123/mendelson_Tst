//$Header: /as2/de/mendelson/util/clientserver/messages/LoginRequired.java 3     4/06/18 12:22p Heller $
package de.mendelson.util.clientserver.messages;

import de.mendelson.util.clientserver.user.User;
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
public class LoginRequired extends ClientServerMessage implements Serializable {

    public static final long serialVersionUID = 1L;
    private User user;

    @Override
    public String toString() {
        return ("Login required.");
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
}
