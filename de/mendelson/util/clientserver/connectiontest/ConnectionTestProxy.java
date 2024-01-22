//$Header: /as2/de/mendelson/util/clientserver/connectiontest/ConnectionTestProxy.java 4     24/08/22 12:56 Heller $
package de.mendelson.util.clientserver.connectiontest;

import java.io.Serializable;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores the data for a SOCKS proxy. For a connection test the same proxy as
 * for the real connection should be used.
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ConnectionTestProxy implements Serializable {
    public static final long serialVersionUID = 1L;

    /**
     * By default the standard telnet port is used
     */
    private int port = 23;
    private String address = null;
    private String userName = null;
    private String password = null;

    public ConnectionTestProxy() {
    }

    
    /**Generates a proxy object*/
    public Proxy asProxy( int testType ) {
        Type proxyType = null;
        if( testType == ConnectionTest.CONNECTION_TEST_AS2){
            proxyType = Proxy.Type.HTTP;
        }else if( testType == ConnectionTest.CONNECTION_TEST_AS4){
            proxyType = Proxy.Type.HTTP;
        }else if( testType == ConnectionTest.CONNECTION_TEST_OFTP2){
            proxyType = Proxy.Type.SOCKS;
        }        
        SocketAddress socketAddress = new InetSocketAddress(this.getAddress(), this.getPort());
        Proxy proxy = new Proxy(proxyType, socketAddress);
        if( this.usesAuthentication()){
            Authenticator authenticator = new Authenticator(){                
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return (new PasswordAuthentication(getUserName(), getPassword().toCharArray()));
                }
            };
            Authenticator.setDefault(authenticator);
        }
        return( proxy );
    }

    public boolean usesAuthentication(){
        return( this.getUserName() != null && this.getPassword() != null);
    }
    
    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    

}
