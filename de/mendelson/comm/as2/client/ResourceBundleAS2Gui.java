//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2Gui.java 53    17/01/23 14:16 Heller $ 
package de.mendelson.comm.as2.client;

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
 * @author S.Heller
 * @version $Revision: 53 $
 */
public class ResourceBundleAS2Gui extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"menu.file", "File"},
        {"menu.file.exit", "Exit"},
        {"menu.file.partner", "Partner"},
        {"menu.file.datasheet", "Create communication datasheet"},
        {"menu.file.certificates", "Certificates"},
        {"menu.file.certificate", "Certificates"},
        {"menu.file.certificate.signcrypt", "Sign/Crypt"},
        {"menu.file.certificate.ssl", "TLS"},
        {"menu.file.cem", "Certificate Exchange Manager (CEM)"},
        {"menu.file.cemsend", "Exchange certificates with partners (CEM)"},
        {"menu.file.statistic", "Statistic"},
        {"menu.file.quota", "Quota"},
        {"menu.file.serverinfo", "Display HTTP server configuration"},
        {"menu.file.systemevents", "System events"},
        {"menu.file.searchinserverlog", "Search in serverlog"},        
        {"menu.file.preferences", "Preferences"},
        {"menu.file.send", "Send file to partner"},
        {"menu.file.resend", "Send as new transaction"},
        {"menu.file.resend.multiple", "Send as new transactions"},
        {"menu.file.migrate.hsqldb", "Migrate from HSQLDB"},
        {"menu.file.ha", "High Availability Instances"},
        {"menu.help", "Help"},
        {"menu.help.about", "About"},
        {"menu.help.supportrequest", "Support request"},
        {"menu.help.shop", "mendelson online shop"},
        {"menu.help.helpsystem", "Help system"},
        {"menu.help.forum", "Forum"},
        {"details", "Message details"},
        {"filter.showfinished", "Show finished"},
        {"filter.showpending", "Show pending"},
        {"filter.showstopped", "Show stopped"},
        {"filter.none", "-- None --"},
        {"filter.partner", "Partner restriction:"},
        {"filter.localstation", "Local station restriction:"},
        {"filter.direction", "Direction restriction:"},
        {"filter.direction.inbound", "Inbound"},
        {"filter.direction.outbound", "Outbound"},
        {"filter.use", "Use time filter" },
        {"filter.from", "From:" },
        {"filter.to", "To:" },
        {"filter", "Filter"},
        {"keyrefresh", "Reload keys"},
        {"configurecolumns", "Columns" },
        {"delete.msg", "Delete"},
        {"stoprefresh.msg", "Toggle refresh"},
        {"dialog.msg.delete.message", "Do you really want to delete the selected messages permanent?"},
        {"dialog.msg.delete.title", "Delete messages"},
        {"msg.delete.success.single", "{0} message has been deleted successfully" },
        {"msg.delete.success.multiple", "{0} messages have been deleted successfully" },
        {"welcome", "Welcome, {0}"},
        {"fatal.error", "Fatal error"},
        {"warning.refreshstopped", "The GUI refresh has been stopped."},
        {"tab.welcome", "News and updates"},
        {"tab.transactions", "Transactions"},
        {"new.version", "A new version is available. Click here to download it."},
        {"new.version.logentry.1", "A new version is available."},
        {"new.version.logentry.2", "Please visit {0} to download it."},        
        {"dbconnection.failed.message", "Unable to establish a DB connection to the AS2 server: {0}"},
        {"dbconnection.failed.title", "Unable to connect"},
        {"login.failed.client.incompatible.message", "The server reports that this client is incompatible. Please use the proper client version."},
        {"login.failed.client.incompatible.title", "Login rejected"},
        {"uploading.to.server", "Uploading to server"},
        {"refresh.overview", "Refreshing transaction list"},
        {"dialog.resend.message", "Do you really want to resend the data of the selected transaction?"},
        {"dialog.resend.message.multiple", "Do you really want to resend the data of the selected {0} transactions?"},
        {"dialog.resend.title", "Transaction resend"},
        {"logputput.disabled", "** The log output has been disabled **"},
        {"logputput.enabled", "** The log output has been enabled **"},
        {"resend.failed.nopayload", "Resend as new transaction failed: The selected transaction {0} has no payload."},
        {"server.answer.timeout.title", "Timeout in client-server connection" },
        {"server.answer.timeout.details", "The server does not answer in the defined timeframe on a request - high load?" },
    };
}