//$Header: /as2/de/mendelson/util/systemevents/ResourceBundleSystemEvent_de.java 27    24/02/22 17:04 Heller $
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
 * @version $Revision: 27 $
 */
public class ResourceBundleSystemEvent_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"type." + SystemEvent.TYPE_CERTIFICATE_ADD, "Zertifikat (hinzugefügt)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_ANY, "Zertifikat"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_DEL, "Zertifikat (gelöscht)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_EXCHANGE_ANY, "Zertifikataustausch"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_EXCHANGE_REQUEST_RECEIVED, "Zertifikataustausch (eingehende Anfrage)"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_EXPIRE, "Zertifikat läuft aus"},
        {"type." + SystemEvent.TYPE_CERTIFICATE_MODIFY, "Zertifikat (Alias verändert)"},
        {"type." + SystemEvent.TYPE_CONNECTIVITY_ANY, "Verbindung"},
        {"type." + SystemEvent.TYPE_CONNECTIVITY_TEST, "Verbindungstest"},
        {"type." + SystemEvent.TYPE_DATABASE_ANY, "Datenbank"},
        {"type." + SystemEvent.TYPE_DATABASE_CREATION, "Datenbankerstellung"},
        {"type." + SystemEvent.TYPE_DATABASE_UPDATE, "Datenbank (Update)"},
        {"type." + SystemEvent.TYPE_DATABASE_INITIALIZATION, "Datenbank (Initialisierung)"},
        {"type." + SystemEvent.TYPE_NOTIFICATION_ANY, "Benachrichtigung"},
        {"type." + SystemEvent.TYPE_NOTIFICATION_SEND_FAILED, "Benachrichtigung (Versand fehlgeschlagen)"},
        {"type." + SystemEvent.TYPE_NOTIFICATION_SEND_SUCCESS, "Benachrichtigung (Versand erfolgreich)"},
        {"type." + SystemEvent.TYPE_PARTNER_ADD, "Partner (hinzugefügt)"},
        {"type." + SystemEvent.TYPE_PARTNER_DEL, "Partner (gelöscht)"},
        {"type." + SystemEvent.TYPE_PARTNER_MODIFY, "Partner (modifiziert)"},
        {"type." + SystemEvent.TYPE_QUOTA_ANY, "Quota"},
        {"type." + SystemEvent.TYPE_QUOTA_RECEIVE_EXCEEDED, "Quota erreicht"},
        {"type." + SystemEvent.TYPE_QUOTA_SEND_EXCEEDED, "Quota erreicht"},
        {"type." + SystemEvent.TYPE_QUOTA_RECEIVE_EXCEEDED, "Quota erreicht"},
        {"type." + SystemEvent.TYPE_SERVER_CONFIGURATION_CHANGED, "Konfigurationsänderung"},
        {"type." + SystemEvent.TYPE_SERVER_CONFIGURATION_ANY, "Konfiguration"},
        {"type." + SystemEvent.TYPE_SERVER_CONFIGURATION_CHECK, "Konfigurationsprüfung"},
        {"type." + SystemEvent.TYPE_SERVER_COMPONENTS_ANY, "Server Komponente"},
        {"type." + SystemEvent.TYPE_MAIN_SERVER_RUNNING, "Server läuft"},
        {"type." + SystemEvent.TYPE_MAIN_SERVER_SHUTDOWN, "Server heruntergefahren"},
        {"type." + SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN, "Serverstart"},
        {"type." + SystemEvent.TYPE_DATABASE_SERVER_STARTUP_BEGIN, "DB Server startet"},
        {"type." + SystemEvent.TYPE_DATABASE_SERVER_RUNNING, "DB Server läuft"},
        {"type." + SystemEvent.TYPE_DATABASE_SERVER_SHUTDOWN, "DB Server heruntergefahren"},
        {"type." + SystemEvent.TYPE_HTTP_SERVER_STARTUP_BEGIN, "HTTP Server startet"},
        {"type." + SystemEvent.TYPE_HTTP_SERVER_RUNNING, "HTTP Server läuft"},
        {"type." + SystemEvent.TYPE_HTTP_SERVER_SHUTDOWN, "HTTP Server heruntergefahren"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_STARTUP_BEGIN, "TRFC Server startet"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_RUNNING, "TRFC Server läuft"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_SHUTDOWN, "TRFC Server heruntergefahren"},
        {"type." + SystemEvent.TYPE_TRFC_SERVER_STATE, "TRFC Server Status"},
        {"type." + SystemEvent.TYPE_SCHEDULER_SERVER_STARTUP_BEGIN, "Scheduler startet"},      
        {"type." + SystemEvent.TYPE_SCHEDULER_SERVER_RUNNING, "Scheduler läuft"},      
        {"type." + SystemEvent.TYPE_SCHEDULER_SERVER_SHUTDOWN, "Scheduler heruntergefahren"},      
        {"type." + SystemEvent.TYPE_TRANSACTION_ANY, "Transaktion"},
        {"type." + SystemEvent.TYPE_TRANSACTION_ERROR, "Transaktionsfehler"},
        {"type." + SystemEvent.TYPE_TRANSACTION_REJECTED_RESEND, "Transaktion (erneute Zustellung zurückgewiesen)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_DUPLICATE_MESSAGE, "Transaktion (doppelte Nachricht)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_DELETE, "Transaktion (löschen)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_CANCEL, "Transaktion (abbrechen)"},
        {"type." + SystemEvent.TYPE_TRANSACTION_RESEND, "Transaktion (erneut senden)"},
        {"type." + SystemEvent.TYPE_PROCESSING_ANY, "Datenverarbeitung"},
        {"type." + SystemEvent.TYPE_PRE_PROCESSING, "Vorverarbeitung"},
        {"type." + SystemEvent.TYPE_POST_PROCESSING, "Nachverarbeitung"},
        {"type." + SystemEvent.TYPE_LICENSE_ANY, "Lizenz"},
        {"type." + SystemEvent.TYPE_LICENSE_EXPIRE, "License Ablauf"},
        {"type." + SystemEvent.TYPE_LICENSE_UPDATE, "License Aktualisierung"},
        {"type." + SystemEvent.TYPE_FILE_OPERATION_ANY, "Dateioperation"},
        {"type." + SystemEvent.TYPE_FILE_DELETE, "Datei (löschen)"},
        {"type." + SystemEvent.TYPE_FILE_MOVE, "Datei (verschieben)"},
        {"type." + SystemEvent.TYPE_FILE_COPY, "Datei (kopieren)"},
        {"type." + SystemEvent.TYPE_MKDIR, "Verzeichnis erstellen"},
        {"type." + SystemEvent.TYPE_DIRECTORY_MONITORING_STATE_CHANGED, "Verzeichnisüberwachung (Status verändert)"},        
        {"type." + SystemEvent.TYPE_CLIENT_ANY, "Client"},
        {"type." + SystemEvent.TYPE_CLIENT_LOGIN_FAILURE, "Benutzeranmeldung (Fehlgeschlagen)"},
        {"type." + SystemEvent.TYPE_CLIENT_LOGIN_SUCCESS, "Benutzeranmeldung (Erfolg)"},
        {"type." + SystemEvent.TYPE_CLIENT_LOGOFF, "Benutzertrennung"},
        {"type." + SystemEvent.TYPE_OTHER, "Unspezifiziert"},
        {"type." + SystemEvent.TYPE_PORT_LISTENER, "Empfangsport"},
        {"origin." + SystemEvent.ORIGIN_SYSTEM, "System"},
        {"origin." + SystemEvent.ORIGIN_TRANSACTION, "Transaktion"},
        {"origin." + SystemEvent.ORIGIN_USER, "Benutzer"},
        {"severity." + SystemEvent.SEVERITY_ERROR, "Fehler"},
        {"severity." + SystemEvent.SEVERITY_WARNING, "Warnung"},
        {"severity." + SystemEvent.SEVERITY_INFO, "Info"},
        {"category." + SystemEvent.CATEGORY_LICENSE, "Aktivierung"},
        {"category." + SystemEvent.CATEGORY_CERTIFICATE, "Zertifikat"},
        {"category." + SystemEvent.CATEGORY_CONFIGURATION, "Konfiguration"},
        {"category." + SystemEvent.CATEGORY_CONNECTIVITY, "Verbindung"},
        {"category." + SystemEvent.CATEGORY_DATABASE, "Datenbank"},
        {"category." + SystemEvent.CATEGORY_NOTIFICATION, "Benachrichtigung"},
        {"category." + SystemEvent.CATEGORY_OTHER, "Andere"},
        {"category." + SystemEvent.CATEGORY_PROCESSING, "Datenverarbeitung"},
        {"category." + SystemEvent.CATEGORY_QUOTA, "Kontingent"},
        {"category." + SystemEvent.CATEGORY_SERVER_COMPONENTS, "Server Komponente"},
        {"category." + SystemEvent.CATEGORY_TRANSACTION, "Transaktion"},
        {"category." + SystemEvent.CATEGORY_FILE_OPERATION, "Dateioperation" },
        {"category." + SystemEvent.CATEGORY_CLIENT_OPERATION, "Client Operation" },
    };
}
