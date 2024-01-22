//$Header: /mec_as2/de/mendelson/comm/as2/server/ServerPlugins.java 6     23/01/23 13:13 Heller $
package de.mendelson.comm.as2.server;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.logging.Logger;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Not implemented in the community edition
 *
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class ServerPlugins implements Serializable {

    public static final long serialVersionUID = 1L;
    public static final String PLUGIN_POSTGRESQL = "PostgreSQL";
    public static final String PLUGIN_HA = "HA";
    public static final String PLUGIN_JAVA_API = "Java_API";
    public static final String PLUGIN_MYSQL = "MySQL";
    public static final String PLUGIN_REST_API = "REST_API";
    public static final String PLUGIN_WEBINTERFACE = "Webinterface";
    public static final String PLUGIN_ORACLE_DB = "OracleDB";
    public static final String PLUGIN_OAUTH2 = "OAUTH2";

    public ServerPlugins() {
    }

    public void displayActivationState(Logger logger) {
    }

    public void setStartPlugins(boolean a) {
    }

    public void setActivated(final String a, boolean b) {
    }

    public boolean isActivated(final String a) {
        return( a.equals( PLUGIN_WEBINTERFACE));
    }

    public String getVersion(final String a) {
        return ("--");
    }

    public String getStartedPluginsAsString() {
        return (PLUGIN_WEBINTERFACE);
    }

    /**
     * @return the licensee
     */
    public String getLicensee() {
        return( "Community edition");
    }

    public LocalDateTime getLicenseExpireDate() {
        return( LocalDateTime.now().plusYears(999));
    }
        
    public String getLicenseExpireDateAsString() {
        return( "00000000" );
    }
        
    public boolean licenseWillExpire() {
        return (false);
    }
        
    public long getLicenseExpiresInDays() {        
        return (999);
    }
    
}
