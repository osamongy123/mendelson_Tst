//$Header: /as2/de/mendelson/comm/as2/client/ResourceBundleAS2Gui_de.java 54    17/01/23 14:16 Heller $
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
 * @version $Revision: 54 $
 */
public class ResourceBundleAS2Gui_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"menu.file", "Datei"},
        {"menu.file.exit", "Beenden"},
        {"menu.file.partner", "Partner"},
        {"menu.file.datasheet", "Datenblatt für Anbindung"},        
        {"menu.file.certificates", "Zertifikate"},
        {"menu.file.certificate", "Zertifikate"},
        {"menu.file.certificate.signcrypt", "Sign/Verschlüsselung"},
        {"menu.file.certificate.ssl", "TLS"},
        {"menu.file.cem", "Verwaltung Zertifikataustausch (CEM)"},
        {"menu.file.cemsend", "Zertifikate mit Partnern tauschen (CEM)"},
        {"menu.file.preferences", "Einstellungen"},
        {"menu.file.send", "Datei an Partner versenden"},
        {"menu.file.resend", "Als neue Transaktion versenden"},
        {"menu.file.resend.multiple", "Als neue Transaktionen versenden"},
        {"menu.file.migrate.hsqldb", "Von HSQLDB migrieren"},
        {"menu.file.ha", "Instanzen der Hochverfügbarkeit"},
        {"menu.file.statistic", "Statistik"},
        {"menu.file.quota", "Kontingente"},
        {"menu.file.serverinfo", "HTTP Server Konfiguration anzeigen"},
        {"menu.file.systemevents", "Systemereignisse"},
        {"menu.file.searchinserverlog", "Serverprotokoll durchsuchen"},        
        {"menu.help", "Hilfe"},
        {"menu.help.about", "Über"},
        {"menu.help.supportrequest", "Support Anfrage"},
        {"menu.help.shop", "mendelson Online Shop"},
        {"menu.help.helpsystem", "Hilfesystem"},
        {"menu.help.forum", "Forum"},
        {"details", "Nachrichtendetails"},
        {"filter.showfinished", "Fertige anzeigen"},
        {"filter.showpending", "Wartende anzeigen"},
        {"filter.showstopped", "Gestoppte anzeigen"},
        {"filter.none", "-- Keine --"},
        {"filter.partner", "Partnerbeschränkung:"},
        {"filter.localstation", "Beschränkung der lokalen Station:"},
        {"filter.direction", "Richtungsbeschränkung:"},
        {"filter.direction.inbound", "Eingehend"},
        {"filter.direction.outbound", "Ausgehend"},
        {"filter", "Filter"},
        {"filter.use", "Zeitliche Einschränkung" },
        {"filter.from", "Von:" },
        {"filter.to", "Bis:" },
        {"keyrefresh", "Zertifikate aktualisieren"},
        {"configurecolumns", "Spalten" },
        {"delete.msg", "Löschen"},
        {"dialog.msg.delete.message", "Wollen Sie die selektierten Nachrichten wirklich permanent löschen?"},
        {"dialog.msg.delete.title", "Löschen von Nachrichten"},
        {"msg.delete.success.single", "{0} Nachricht wurde gelöscht" },
        {"msg.delete.success.multiple", "{0} Nachrichten wurden gelöscht" },
        {"stoprefresh.msg", "Aktualisierung an/aus"},
        {"welcome", "Willkommen, {0}"},
        {"fatal.error", "Fehler"},
        {"warning.refreshstopped", "Die Aktualisierung der Oberfläche ist abgeschaltet."},
        {"tab.welcome", "News und Updates"},
        {"tab.transactions", "Transaktionen"},
        {"new.version", "Eine neue Version ist verfügbar. Hier klicken, um sie herunterzuladen."},
        {"new.version.logentry.1", "Eine neue Version ist verfügbar."},
        {"new.version.logentry.2", "Sie können Sie unter {0} herunterladen."}, 
        {"dbconnection.failed.message", "Es konnte keine Verbindung zum AS2 Datenbankserver hergestellt werden: {0}"},
        {"dbconnection.failed.title", "Keine Verbindung möglich"},
        {"login.failed.client.incompatible.message", "Der Server meldet, dass dieser Client nicht die richtige Version hat.\nBitte verwenden Sie den zum Server passenden Client."},
        {"login.failed.client.incompatible.title", "Login wurde zurückgewiesen"},
        {"uploading.to.server", "Übertrage zum Server"},
        {"refresh.overview", "Aktualisiere Transaktionsliste"},
        {"dialog.resend.message", "Wollen Sie die selektierte Transaktion wirklich erneut senden?"},
        {"dialog.resend.message.multiple", "Wollen Sie die {0} selektierten Transaktionen wirklich erneut senden?"},
        {"dialog.resend.title", "Daten erneut senden"},
        {"logputput.disabled", "** Die Protokollausgabe wurde unterdrückt **"},
        {"logputput.enabled", "** Die Protokollausgabe wurde aktiviert **"},
        {"resend.failed.nopayload", "Erneuter Versand als neue Transaktion ist fehlgeschlagen: Die selektierte Transaktion {0} hat keine Nutzdaten." },
        {"server.answer.timeout.title", "Zeitüberschreitung in der Client-Server Anbindung" },
        {"server.answer.timeout.details", "Der Server antwortet nicht im definierten Zeitrahmen - ist die Last zu hoch?" },
    };
}