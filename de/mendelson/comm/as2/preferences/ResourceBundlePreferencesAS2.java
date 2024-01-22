//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferencesAS2.java 9     2/08/22 15:37 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class ResourceBundlePreferencesAS2 extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"TRUE", "enabled" },
        {"FALSE", "disabled" },
        {"set.to", "set to" },
        {"setting.updated", "Setting updated" },
        {"notification.setting.updated", "The notification settings have been changed." },
        //preferences localized
        {PreferencesAS2.ASYNC_MDN_TIMEOUT, "Timeout for async MDN in min"},
        {PreferencesAS2.AUTH_PROXY_PASS, "HTTP Proxy credentials (password)"},
        {PreferencesAS2.AUTH_PROXY_USE, "Use HTTP proxy credentials"},
        {PreferencesAS2.AUTH_PROXY_USER, "HTTP Proxy credentials (user)"},
        {PreferencesAS2.AUTO_LOGDIR_DELETE, "Automatically cleanup log dir"},
        {PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN, "Automatically cleanup log dir older than"},
        {PreferencesAS2.AUTO_MSG_DELETE, "Automatically cleanup old transactions"},
        {PreferencesAS2.AUTO_MSG_DELETE_LOG, "Log entry for cleanup of old transactions"},
        {PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN, "Cleanup transactions older than"},
        {PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S, "Timeunit: Cleanup transactions older than"},
        {PreferencesAS2.AUTO_STATS_DELETE, "Automatically delete statistic data"},
        {PreferencesAS2.AUTO_STATS_DELETE_OLDERTHAN, "Delete statistic data (older than)"},
        {PreferencesAS2.CEM, "CEM verwenden"},
        {PreferencesAS2.COLOR_BLINDNESS, "Support for color blindness"},
        {PreferencesAS2.COMMUNITY_EDITION, "Community edition"},
        {PreferencesAS2.CONNECTION_RETRY_WAIT_TIME_IN_S, "Connection retry all n sec"},
        {PreferencesAS2.COUNTRY, "Clients country"},
        {PreferencesAS2.DATASHEET_RECEIPT_URL, "Datasheet URL"},
        {PreferencesAS2.DIR_MSG, "Base directory for messages"},
        {PreferencesAS2.HTTP_SEND_TIMEOUT, "Send timeout (HTTP/S)"},
        {PreferencesAS2.KEYSTORE, "Internal Sign/Encrypt keystore file"},
        {PreferencesAS2.KEYSTORE_HTTPS_SEND, "Internal TLS keystore file"},
        {PreferencesAS2.KEYSTORE_HTTPS_SEND_PASS, "Internal TLS keystore file (password)"},
        {PreferencesAS2.KEYSTORE_PASS, "Internal Sign/Encrypt keystore file (password)"},
        {PreferencesAS2.LANGUAGE, "Client language"},
        {PreferencesAS2.LAST_UPDATE_CHECK, "Last update check (unix time)"},
        {PreferencesAS2.LOG_POLL_PROCESS, "Display poll process in log"},
        {PreferencesAS2.MAX_CONNECTION_RETRY_COUNT, "Number of connection retries"},
        {PreferencesAS2.MAX_OUTBOUND_CONNECTIONS, "Max parallel outbound connections"},
        {PreferencesAS2.MAX_INBOUND_CONNECTIONS, "Max parallel inbound connections"},  
        {PreferencesAS2.PROXY_HOST, "HTTP Proxy host"},
        {PreferencesAS2.PROXY_PORT, "HTTP Proxy port"},
        {PreferencesAS2.PROXY_USE, "Use HTTP proxy"},
        {PreferencesAS2.RECEIPT_PARTNER_SUBDIR, "Use subdirectory per partner"},
        {PreferencesAS2.SHOW_HTTPHEADER_IN_PARTNER_CONFIG, "Display HTTP header config in partner management"},
        {PreferencesAS2.SHOW_QUOTA_NOTIFICATION_IN_PARTNER_CONFIG, "Display quota in partner management"},
        {PreferencesAS2.WRITE_OUTBOUND_STATUS_FILE, "Write a status file for each transaction"},      
        {PreferencesAS2.TLS_TRUST_ALL_REMOTE_SERVER_CERTIFICATES, "(TLS) Trust all remote server certificates"},   
        {PreferencesAS2.TLS_STRICT_HOST_CHECK, "(TLS) Strict host check"},   
        {PreferencesAS2.HTTPS_LISTEN_PORT, "HTTPS Listen port"},
        {PreferencesAS2.HTTP_LISTEN_PORT, "HTTP Listen port"},          
    };
}
