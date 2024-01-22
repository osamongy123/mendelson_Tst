//$Header: /oftp2/de/mendelson/util/clientserver/messages/LoginState.java 21    24/08/22 17:33 Heller $
package de.mendelson.util.clientserver.messages;

import de.mendelson.util.clientserver.ServerHelloMessage;
import de.mendelson.util.clientserver.user.User;
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
 * @version $Revision: 21 $
 */
public class LoginState extends ClientServerResponse implements Serializable {

    public static final long serialVersionUID = 1L;
    public static final int STATE_AUTHENTICATION_SUCCESS = 1;
    public static final int STATE_AUTHENTICATION_FAILURE = 2;
    public static final int STATE_AUTHENTICATION_FAILURE_PASSWORD_REQUIRED = 3;
    public static final int STATE_INCOMPATIBLE_CLIENT = 4;
    public static final int STATE_REJECTED = 5;
    
    /**
     * Stores the users rights, this is returned by the server
     */
    private User user;
    private int state = STATE_AUTHENTICATION_FAILURE;
    private String stateDetails = null;
    private List<ServerHelloMessage> serverHelloMessages = null;

    public LoginState(LoginRequest request) {
        super(request);
    }

    public int getState() {
        return state;
    }

    public String getPermission(Integer index) {
        if (this.user == null) {
            return ("");
        }
        return (this.user.getPermission(index));
    }

    public void setState(int state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the serverHelloMessage
     */
    public List<ServerHelloMessage> getServerHelloMessages() {
        return this.serverHelloMessages;
    }

    /**
     * @param serverHelloMessage the serverHelloMessage to set
     */
    public void setServerHelloMessages(List<ServerHelloMessage> serverHelloMessages) {
        this.serverHelloMessages = serverHelloMessages;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.user != null) {
            builder.append("Login state for user ");
            builder.append(this.user.getName());
            builder.append(": ");
        } else {
            builder.append("Login state: ");
        }
        if (this.state == STATE_AUTHENTICATION_FAILURE) {
            builder.append("FAILURE");
        } else if (this.state == STATE_AUTHENTICATION_SUCCESS) {
            builder.append("AUTHENTICATION SUCCESS");
        } else if (this.state == STATE_AUTHENTICATION_FAILURE_PASSWORD_REQUIRED) {
            builder.append("PASSWORD_REQUIRED");
        }  else if (this.state == STATE_INCOMPATIBLE_CLIENT) {
            builder.append("INCOMPATIBLE CLIENT");
        } else if (this.state == STATE_REJECTED) {
            builder.append("REJECTED");
        } else {
            builder.append("UNKNOWN");
        }
        return (builder.toString());
    }

    /**
     * @return the stateDetails
     */
    public String getStateDetails() {
        return stateDetails;
    }

    /**
     * @param stateDetails the stateDetails to set
     */
    public void setStateDetails(String stateDetails) {
        this.stateDetails = stateDetails;
    }

}
