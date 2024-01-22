//$Header: /as2/de/mendelson/util/systemevents/ResourceBundleSystemEventFilenames.java 25    24/02/22 17:04 Heller $
package de.mendelson.util.systemevents;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize the mendelson products
 *
 * @author S.Heller
 * @version $Revision: 25 $
 */
public class ResourceBundleSystemEventFilenames extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"type." + SystemEvent.TYPE_CERTIFICATE_ADD, "Certificate (add)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_ANY, "Certificate"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_DEL, "Certificate (delete)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_EXCHANGE_ANY, "Certificate (exchange)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_EXCHANGE_REQUEST_RECEIVED, "Certificate (inbound exchange request)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_EXPIRE, "Certificate (expire)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_MODIFY, "Certificate (modify)"},
        {"type." + SystemEvent.TYPE_CONNECTIVITY_ANY, "Connectivity"},
        {"type." + SystemEvent.TYPE_CONNECTIVITY_TEST, "Connection test"},
        {"type." + SystemEvent.TYPE_DATABASE_ANY, "Database"},
        {"type." + SystemEvent.TYPE_DATABASE_CREATION, "Database (creation)"},
        {"type." + SystemEvent.TYPE_DATABASE_UPDATE, "Database (update)"},
        {"type." + SystemEvent.TYPE_DATABASE_INITIALIZATION, "Database (initialization)"},
        {"type." + SystemEvent.TYPE_NOTIFICATION_ANY, "Notification"},
        {"type." + SystemEvent.TYPE_NOTIFICATION_SEND_FAILED, "Notification send (failed)"},
        {"type." + SystemEvent.TYPE_NOTIFICATION_SEND_SUCCESS, "Notification send (success)"},
        {"type." + SystemEvent.TYPE_PARTNER_ADD, "Partner (add)"},
        {"type." + SystemEvent.TYPE_PARTNER_DEL, "Partner (delete)"},
        {"type." + SystemEvent.TYPE_PARTNER_MODIFY, "Partner (modify)"},
        {"type." + SystemEvent.TYPE_QUOTA_ANY, "Quota"},
        {"type." + SystemEvent.TYPE_QUOTA_RECEIVE_EXCEEDED, "Quota exceeded"},
        {"type." + SystemEvent.TYPE_QUOTA_SEND_EXCEEDED, "Quota exceeded"},
        {"type." + SystemEvent.TYPE_QUOTA_RECEIVE_EXCEEDED, "Quota exceeded"},
        {"type." + SystemEvent.TYPE_SERVER_CONFIGURATION_CHANGED, "Configuration changed"},
        {"type." + SystemEvent.TYPE_SERVER_CONFIGURATION_ANY, "Configuration"},
        {"type." + SystemEvent.TYPE_SERVER_CONFIGURATION_CHECK, "Configuration check"},
        {"type." + SystemEvent.TYPE_SERVER_COMPONENTS_ANY, "Server component"},
        {"type." + SystemEvent.TYPE_MAIN_SERVER_RUNNING, "Server is running"},
        {"type." + SystemEvent.TYPE_MAIN_SERVER_SHUTDOWN, "Server shutdown"},
        {"type." + SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN, "Server startup"},
        {"type." + SystemEvent.TYPE_DATABASE_SERVER_STARTUP_BEGIN, "DB server startup"},
        {"type." + SystemEvent.TYPE_DATABASE_SERVER_RUNNING, "DB server is running"},
        {"type." + SystemEvent.TYPE_DATABASE_SERVER_SHUTDOWN, "DB server shutdown"},
        {"type." + SystemEvent.TYPE_HTTP_SERVER_STARTUP_BEGIN, "HTTP server startup"},
        {"type." + SystemEvent.TYPE_HTTP_SERVER_RUNNING, "HTTP server is running"},
        {"type." + SystemEvent.TYPE_HTTP_SERVER_SHUTDOWN, "HTTP server shutdown"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_STARTUP_BEGIN, "TRFC server startup"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_RUNNING, "TRFC server is running"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_SHUTDOWN, "TRFC server shutdown"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_STATE, "TRFC server state"},        
        {"type." + SystemEvent.TYPE_SCHEDULER_SERVER_STARTUP_BEGIN, "Scheduler server startup"},      
        {"type." + SystemEvent.TYPE_SCHEDULER_SERVER_RUNNING, "Scheduler server running"},      
        {"type." + SystemEvent.TYPE_SCHEDULER_SERVER_SHUTDOWN, "Scheduler server shutdown"},      
        {"type." + SystemEvent.TYPE_TRANSACTION_ANY, "Transaction"},
        {"type." + SystemEvent.TYPE_TRANSACTION_ERROR, "Transaction (error)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_REJECTED_RESEND, "Transaction (rejected resend)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_DUPLICATE_MESSAGE, "Transaction (duplicate message)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_DELETE, "Transaction (delete)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_CANCEL, "Transaction (cancel)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_RESEND, "Transaction (resend)"},
        {"type." + SystemEvent.TYPE_PROCESSING_ANY, "Data processing"},
        {"type." + SystemEvent.TYPE_PRE_PROCESSING, "Preprocessing"},
        {"type." + SystemEvent.TYPE_POST_PROCESSING, "Postprocessing"},
        {"type." + SystemEvent.TYPE_LICENSE_ANY, "License"},
        {"type." + SystemEvent.TYPE_LICENSE_EXPIRE, "License expire"},
        {"type." + SystemEvent.TYPE_LICENSE_UPDATE, "License update"},
        {"type." + SystemEvent.TYPE_FILE_OPERATION_ANY, "File operation"},
        {"type." + SystemEvent.TYPE_FILE_DELETE, "File (delete)"},
        {"type." + SystemEvent.TYPE_FILE_MOVE, "File (move)"},
        {"type." + SystemEvent.TYPE_FILE_COPY, "File (copy)"},
        {"type." + SystemEvent.TYPE_MKDIR, "Create dir"},
        {"type." + SystemEvent.TYPE_DIRECTORY_MONITORING_STATE_CHANGED, "Directory monitoring state changed"},
        {"type." + SystemEvent.TYPE_CLIENT_ANY, "Client (any)"},
        {"type." + SystemEvent.TYPE_CLIENT_LOGIN_FAILURE, "Client login (failure)"},
        {"type." + SystemEvent.TYPE_CLIENT_LOGIN_SUCCESS, "Client login (success)"},
        {"type." + SystemEvent.TYPE_CLIENT_LOGOFF, "Client logoff"},
        {"type." + SystemEvent.TYPE_OTHER, "Other"},
        {"type." + SystemEvent.TYPE_PORT_LISTENER, "Port listener"},
        {"origin." + SystemEvent.ORIGIN_SYSTEM, "System" },
        {"origin." + SystemEvent.ORIGIN_TRANSACTION, "Transaction" },
        {"origin." + SystemEvent.ORIGIN_USER, "User" },
        {"severity." + SystemEvent.SEVERITY_ERROR, "Error"},
        {"severity." + SystemEvent.SEVERITY_WARNING, "Warning"},
        {"severity." + SystemEvent.SEVERITY_INFO, "Info"},        
        {"category." + SystemEvent.CATEGORY_CLIENT_OPERATION, "Client operation" },
    };
}
