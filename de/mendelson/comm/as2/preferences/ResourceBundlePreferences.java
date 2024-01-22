//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferences.java 72    4/10/22 10:12 Heller $
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
 * @version $Revision: 72 $
 */
public class ResourceBundlePreferences extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        //preferences localized
        {PreferencesAS2.DIR_MSG, "Message storage"},
        {"button.ok", "Ok"},
        {"button.cancel", "Cancel"},
        {"button.modify", "Modify"},
        {"button.browse", "Browse"},
        {"filechooser.selectdir", "Select a directory to set"},
        {"title", "Preferences"},
        {"tab.language", "Client"},
        {"tab.dir", "Directories"},
        {"tab.security", "Security"},
        {"tab.proxy", "Proxy"},
        {"tab.misc", "Misc"},
        {"tab.maintenance", "Maintenance"},
        {"tab.notification", "Notification"},
        {"tab.interface", "Modules"},
        {"tab.log", "Log"},
        {"tab.connectivity", "Connectivity"},
        {"header.dirname", "Type"},
        {"header.dirvalue", "Dir"},
        {"label.language", "Language"},
        {"label.language.help", "<HTML><strong>Language</strong><br><br>"
            + "This is the display language of the client. If you run client and server in different processes "
            + "(which is recommended) the server language might be an other. "
            + "The language used in the log will always be the server language."
            + "</HTML>"},
        {"label.country", "Country/Region"},
        {"label.country.help", "<HTML><strong>Country/Region</strong><br><br>"
            + "This setting mainly just controls the date format that is used to display transaction dates etc in the client."
            + "</HTML>"},
        {"label.keystore.https.pass", "Keystore password (https send):"},
        {"label.keystore.pass", "Keystore password (encryption/signature):"},
        {"label.keystore.https", "Keystore (https send):"},
        {"label.keystore.encryptionsign", "Keystore (enc, sign):"},
        {"label.proxy.url", "Proxy URL:"},
        {"label.proxy.url.hint", "Proxy ip or domain"},
        {"label.proxy.port.hint", "Port"},
        {"label.proxy.user", "User:"},
        {"label.proxy.user.hint", "Proxy login user"},
        {"label.proxy.pass", "Password:"},
        {"label.proxy.pass.hint", "Proxy login password"},
        {"label.proxy.use", "Use a HTTP proxy for outgoing HTTP/HTTPs connections"},
        {"label.proxy.useauthentification", "Use proxy authentification"},
        {"filechooser.keystore", "Please select the keystore file (jks format)."},
        {"label.days", "days"},
        {"label.deletemsgolderthan", "Auto delete messages older than"},
        {"label.deletemsglog", "Inform in log and fire system event about auto deleted messages"},
        {"label.deletestatsolderthan", "Auto delete statistic data older than"},
        {"label.deletelogdirolderthan", "Auto delete log data older than"},
        {"label.asyncmdn.timeout", "Max waiting time for async MDN"},
        {"label.asyncmdn.timeout.help", "<HTML><strong>Max waiting time for async MDN</strong>"
            + "<br><br>The time the system will wait for an asynchronous MDN (message delivery notification) for a sent AS2 message before setting the transaction to failed state."
            + "</HTML>"},
        {"label.httpsend.timeout", "HTTP/S send timeout"},
        {"label.httpsend.timeout.help", "<HTML><strong>HTTP/S send timeout</strong><br><br>"
            + "This is the network connection timeout for outbound connections.<br>"
            + "If no connection to your partner system has been established after this time, the connection attempt will be "
            + "aborted and further connection attempts will be made later according to the retry settings."
            + "</HTML>"},
        {"label.min", "min"},
        {"receipt.subdir", "Create subdirectory for receipt messages per partner"},
        {"receipt.subdir.help", "<HTML><strong>Partner receipt subdirectory</strong><br><br>"
            + "Setup to either receive data from the partner in the directory <strong>&lt;localstation&gt;/inbox</strong>"
            + " or <strong>&lt;localstation&gt;/inbox/&lt;partnername&gt;</strong>."
            + "</HTML>"},
        //notification
        {"checkbox.notifycertexpire", "Notify certificate expire"},
        {"checkbox.notifytransactionerror", "Notify transaction errors"},
        {"checkbox.notifycem", "Notify certificate exchange (CEM) events"},
        {"checkbox.notifyfailure", "Notify system problems"},
        {"checkbox.notifyresend", "Notify rejected resends"},
        {"checkbox.notifyconnectionproblem", "Notify connection problems"},
        {"checkbox.notifypostprocessing", "Notify postprocessing problems"},
        {"button.testmail", "Send test mail"},
        {"label.mailhost", "Mail server host (SMTP)"},
        {"label.mailhost.hint", "IP or domain of server"},
        {"label.mailport", "Port"},
        {"label.mailport.help", "<HTML><strong>SMTP Port</strong><br><br>"
            + "Mainly this is one of the following values:<br>"
            + "<strong>25</strong> (Standard port)<br>"
            + "<strong>465</strong> (SSL port, outdated value)<br>"
            + "<strong>587</strong> (SSL port, standard port)<br>"
            + "<strong>2525</strong> (SSL port, non-standard)"
            + "</HTML>"},
        {"label.mailport.hint", "SMTP port"},
        {"label.mailaccount", "Mail server account"},
        {"label.mailpass", "Mail server password"},
        {"label.notificationmail", "Notification receiver address"},
        {"label.notificationmail.help", "<HTML><strong>Notification receiver address</strong><br><br>"
            + "The mail address of the receiver of the notification mail.<br>"
            + "If you would like to setup multiple receiver please enter a comma separated list of mail addresses."
            + "</HTML>"},
        {"label.replyto", "Replyto address"},
        {"label.smtpauthorization.header", "SMTP authorization"},
        {"label.smtpauthorization.credentials", "User/password credentials"},
        {"label.smtpauthorization.none", "None"},
        {"label.smtpauthorization.oauth2", "OAuth2"},
        {"label.smtpauthorization.user", "User"},
        {"label.smtpauthorization.user.hint", "SMTP servers user name"},
        {"label.smtpauthorization.pass", "Password"},
        {"label.smtpauthorization.pass.hint", "SMTP servers password"},
        {"label.security", "Connection security"},
        {"testmail.message.success", "Test mail sent successfully."},
        {"testmail.message.error", "Error sending test mail:\n{0}"},
        {"testmail.title", "Test mail send result"},
        {"testmail", "Test mail"},
        //interface
        {"label.showhttpheader", "Allow to configure the HTTP headers in the partner configuration"},
        {"label.showquota", "Allow to configure quota notification in the partner configuration"},
        {"label.cem", "Allow certificate exchange (CEM)"},
        {"label.outboundstatusfiles", "Write outbound transaction status files"},
        {"info.restart.client", "A client restart is required to make these changes valid!"},
        {"remotedir.select", "Select a directory on the server"},
        //retry
        {"label.retry.max", "Max number of connection retries"},
        {"label.retry.max.help", "<HTML><strong>Max number of connection retries</strong>"
            + "<br><br>This is the number of retries that is used to retry connections to a partner if a connection could not be established."
            + " The wait time between these retries could be set up in the property <strong>Wait time between connection retries</strong>."
            + "</HTML>"},
        {"label.retry.waittime", "Wait time between connection retries"},
        {"label.retry.waittime.help", "<HTML><strong>Wait time between connection retries</strong>"
            + "<br><br>This is the time in seconds the system will wait before reconnecting again to the partner. "
            + "A connection retry is only performed if it was impossible to establish a connection to a partner "
            + "(e.g. partner system down or infrastructure problem). The number of connection retries could be configured in the property "
            + "<strong>Max number of connection retries</strong>."
            + "</HTML>"},
        {"label.sec", "seconds"},
        {"keystore.hint", "<HTML><strong>Warning:</strong><br>Please only change these parameters if you want to integrate external keystores. "
            + "Changing the paths may cause problems during the update.</HTML>"},
        {"maintenancemultiplier.day", "day(s)"},
        {"maintenancemultiplier.hour", "hour(s)"},
        {"maintenancemultiplier.minute", "minute(s)"},
        {"label.logpollprocess", "Log poll process (Huge amount of entries - do not use in production)"},
        {"label.max.outboundconnections", "Max parallel outbound connections"},
        {"label.max.outboundconnections.help", "<HTML><strong>Max parallel outbound connections</strong><br><br>"
            + "This is the amount of maximal parallel outbound connections your system will open. This value is mainly available to save your partner system from beeing flooded by inbound connections from your side.</HTML>"},
        {"label.max.inboundconnections", "Max parallel inbound connections"},
        {"label.max.inboundconnections.help", "<HTML><strong>Max parallel inbound connections</strong><br><br>"
            + "This is the amount of maximal parallel inbound connections your system will allow. "
            + "This setting is passed on to the embedded HTTP server, "
            + "you must restart the AS2 server after a change."
            + "</HTML>"},
        {"event.preferences.modified.subject", "The server settings entry {0} has been modified"},
        {"event.preferences.modified.body", "Old value: {0}\nNew value: {1}"},
        {"event.notificationdata.modified.subject", "The notification data has been modified"},
        {"event.notificationdata.modified.body", "The notification data has been modified from \n\n{0}\n\nto\n\n{1}"},
        {"label.maxmailspermin", "Max number of notifications/min"},
        {"label.maxmailspermin.help", "<HTML><strong>Max number of notifications/min</strong><br><br>"
            + "To prevent too many mails you could summarize all notifications by setting up the max number of notifications that will be sent per minute."
            + "Using this functionality you will receive mails that contain multiple notifications."
            + "</HTML>"},
        {"systemmaintenance.deleteoldtransactions.help", "<HTML><strong>Delete old transactions</strong><br><br>This sets up the time range the transactions and related data (also temp files) will remain in the system and should be displayed in the transaction overview.<br>These settings will <strong>not</strong> touch your received data/files.<br>Even for deleted transactions the transaction log is still available via the \"log search\" functionality.</HTML>"},
        {"systemmaintenance.deleteoldstatistic.help", "<HTML><strong>Delete old statistic data</strong><br><br>The system collects compatibility data from the partner systems and can display this as statistics. This determines the time frame in which this data is kept.</HTML>" },
        {"systemmaintenance.deleteoldlogdirs.help", "<HTML><strong>Delete old log dirs</strong><br><br>Even if old transactions have been deleted, the transaction logs can still be traced via existing log files. This setting deletes these log files and also all files for system events that fall within the same time period.</HTML>" },
        {"label.colorblindness", "Enable support for color blindness"},
        {"warning.clientrestart.required", "Client settings have been changed - please restart the client to make them valid"},
        {"warning.serverrestart.required", "Please restart the server to make these changes valid"},
        {"label.darkmode", "Dark mode"},
        {"label.litemode", "Light mode"},
        {"label.trustallservercerts", "TLS: Trust all remote server end certificates" },
        {"label.trustallservercerts.help", "<HTML><strong>TLS: Trust all remote server end certificates</strong><br><br>"
            + "Normally it''s required to have all certificates of the trust chain of your partners AS2 systems in your TLS certificate manager. If you enable this option it is just required to "
            + "setup the root and intermediate certificates of your partners AS2 system into your TLS certificate manager. Once these certificates are available the system will trust any trusted end certificate based on them. "
            + "Please remember that this option only makes sense if your partner uses a trusted certificates - self signed certificates are accepted anyway."
            + "<br><br><strong>Warning:</strong> Enabling this "
            + "lowers the security level as man in the middle attacks are possible!"
            + "</HTML>"},
        {"label.stricthostcheck", "TLS: Strict host check" },
        {"label.stricthostcheck.help", "<HTML><strong>TLS: Strict host check</strong><br><br>"
            + "Sets whether to check if the common name (CN) of the remote certificate matches the remote host in the "
            + "case of an outgoing TLS connection. This check applies only to trusted certificates."
            + "</HTML>"},
        {"label.httpport", "HTTP listen port" },
        {"label.httpport.help", "<HTML><strong>HTTP listen port</strong><br><br>"
            + "This is the port for incoming unencrypted connections. This setting is passed on to the embedded HTTP server, "
            + "you must restart the AS2 server after a change.<br>"
            + "The port is part of the URL to which your partner must send AS2 messages. This is http://host:<strong>port</strong>/as2/HttpReceiver."
                + "</HTML>" 
        },
        {"label.httpsport", "HTTPS listen port" },
        {"label.httpsport.help", "<HTML><strong>HTTPS listen port</strong><br><br>"
            + "This is the port for incoming encrypted connections (TLS). This setting is passed on to the embedded HTTP server, "
            + "you must restart the AS2 server after a change.<br>"
            + "The port is part of the URL to which your partner must send AS2 messages. This is https://host:<strong>port</strong>/as2/HttpReceiver."
                + "</HTML>" 
        },
        {"embedded.httpconfig.not.available", "HTTP server not available or config file access problems" },
    };
}
