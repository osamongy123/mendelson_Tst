//$Header: /as2/de/mendelson/comm/as2/preferences/ResourceBundlePreferences_de.java 75    17/11/22 15:09 Heller $
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
 * @version $Revision: 75 $
 */
public class ResourceBundlePreferences_de extends MecResourceBundle {

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
        {PreferencesAS2.DIR_MSG, "Nachrichtenverzeichnis"},
        {"button.ok", "Ok"},
        {"button.cancel", "Abbrechen"},
        {"button.modify", "Bearbeiten"},
        {"button.browse", "Durchsuchen"},
        {"filechooser.selectdir", "Bitte wählen Sie das zu setzene Verzeichnis"},
        {"title", "Einstellungen"},
        {"tab.language", "Client"},
        {"tab.dir", "Verzeichnisse"},
        {"tab.security", "Sicherheit"},
        {"tab.proxy", "Proxy"},
        {"tab.misc", "Allgemein"},
        {"tab.maintenance", "Systempflege"},
        {"tab.notification", "Benachrichtigung"},
        {"tab.interface", "Module"},
        {"tab.log", "Protokoll"},
        {"tab.connectivity", "Verbindungen"},
        {"header.dirname", "Typ"},
        {"header.dirvalue", "Verzeichnis"},
        {"label.language", "Sprache"},
        {"label.language.help", "<HTML><strong>Sprache</strong><br><br>"
            + "Dies ist die Anzeigesprache des Clients. Wenn Sie Client und Server in verschiedenen Prozessen laufen lassen "
            + "(was empfohlen wird), kann die Serversprache eine andere sein. "
            + "Die Sprache, die im Protokoll verwendet wird, ist immer die Sprache des Servers."
            + "</HTML>"},
        {"label.country", "Land/Region"},
        {"label.country.help", "<HTML><strong>Land/Region</strong><br><br>"
            + "Diese Einstellung steuert im Wesentlichen nur das Datumsformat, das für die Anzeige von Transaktionsdaten usw. im Client verwendet wird."
            + "</HTML>"},
        {"label.keystore.https.pass", "Keystore Passwort (zum Senden via Https):"},
        {"label.keystore.pass", "Keystore Password (Verschlüsselung/digitale Signatur):"},
        {"label.keystore.https", "Keystore (zum Senden via Https):"},
        {"label.keystore.encryptionsign", "Keystore( Verschl., Signatur):"},
        {"label.proxy.url", "Proxy URL:"},
        {"label.proxy.url.hint", "Proxy IP oder Domain"},
        {"label.proxy.port.hint", "Port"},
        {"label.proxy.user", "Benutzer:"},
        {"label.proxy.user.hint", "Proxy Login Benutzer"},
        {"label.proxy.pass", "Passwort:"},
        {"label.proxy.pass.hint", "Proxy Login Passwort"},
        {"label.proxy.use", "HTTP Proxy für ausgehende HTTP/HTTPs Verbindungen benutzen"},
        {"label.proxy.useauthentification", "Authentifizierung für Proxy benutzen"},
        {"filechooser.keystore", "Bitte wählen Sie die Keystore Datei (JKS Format)."},
        {"label.days", "Tage"},
        {"label.deletemsgolderthan", "Automatisches Löschen von Nachrichten, die älter sind als"},
        {"label.deletemsglog", "Automatisches Löschen von Dateien und Logeinträgen protokollieren"},
        {"label.deletestatsolderthan", "Automatisches Löschen von Statistikdaten, die älter sind als"},
        {"label.deletelogdirolderthan", "Automatisches Löschen von Protokolldaten, die älter sind als"},
        {"label.asyncmdn.timeout", "Maximale Wartezeit auf asynchrone MDNs"},
        {"label.asyncmdn.timeout.help", "<HTML><strong>Maximale Wartezeit auf asynchrone MDNs</strong>"
            + "<br><br>Die Zeit, die das System auf eine asynchrone MDN (Message Delivery Notification) für eine gesendete AS2 Nachricht wartet, bevor es die Transaktion in den Status \"fehlgeschlagen\" versetzt."
            + "</HTML>"},
        {"label.httpsend.timeout", "HTTP/S Sende-Timeout"},
        {"label.httpsend.timeout.help", "<HTML><strong>HTTP/S Sende-Timeout</strong><br><br>"
            + "Dies ist Wert für die Zeitüberschreitung der Netzwerkverbindung für ausgehende Verbindungen.<br>"
            + "Wenn nach dieser Zeit keine Verbindung zu Ihrem Partnersystem zustande gekommen ist, wird der Verbindungsversuch abgebrochen und es werden gegebenenfalls entsprechend "
            + "der Wiederholungseinstellungen später weitere Verbindungsversuche unternommen."
            + "</HTML>"},
        {"label.min", "min"},
        {"receipt.subdir", "Unterverzeichnisse pro Partner für Nachrichtenempfang anlegen"},
        {"receipt.subdir.help", "<HTML><strong>Unterverzeichnisse für Empfang</strong><br><br>"
            + "Stellt ein, ob Daten im Verzeichnis <strong>&lt;Lokale Station&gt;/inbox</strong>"
            + " oder <strong>&lt;Lokale Station&gt;/inbox/&lt;Partnername&gt;</strong> empfangen werden sollen."
            + "</HTML>"},
        //notification
        {"checkbox.notifycertexpire", "Vor dem Auslaufen von Zertifikaten"},
        {"checkbox.notifytransactionerror", "Nach Fehlern in Transaktionen"},
        {"checkbox.notifycem", "Ereignisse beim Zertifikataustausch (CEM)"},
        {"checkbox.notifyfailure", "Nach Systemproblemen"},
        {"checkbox.notifyresend", "Nach abgewiesenen Resends"},
        {"checkbox.notifyconnectionproblem", "Bei Verbindungsproblemen"},
        {"checkbox.notifypostprocessing", "Probleme bei der Nachbearbeitung"},
        {"button.testmail", "Sende Test Mail"},
        {"label.mailhost", "Mailserver (SMTP)"},
        {"label.mailhost.hint", "IP oder Domain des Servers"},
        {"label.mailport", "Port"},
        {"label.mailport.help", "<HTML><strong>SMTP Port</strong><br><br>"
            + "In der Regel ist es einer dieser Werte:<br>"
            + "<strong>25</strong> (Standard Port)<br>"
            + "<strong>465</strong> (SSL Port, überholter Wert)<br>"
            + "<strong>587</strong> (SSL Port, Standardwert)<br>"
            + "<strong>2525</strong> (SSL Port, alternativer Wert, kein Standard)"
            + "</HTML>"},
        {"label.mailport.hint", "SMTP Port"},
        {"label.mailaccount", "Mailserver Konto"},
        {"label.mailpass", "Mailserver Passwort"},
        {"label.notificationmail", "Benachrichtigungsempfänger Mailadresse"},
        {"label.notificationmail.help", "<HTML><strong>Benachrichtigungsempfänger Mailadresse</strong><br><br>"
            + "Die Mail Adresse des Empfängers der Benachrichtigung.<br>"
            + "Wenn die Benachrichtigung an mehrere Empfänger geschickt werden soll, geben Sie hier bitte eine kommaseparierte Liste von Empfangsadressen ein."
            + "</HTML>"},
        {"label.replyto", "Replyto Adresse"},
        {"label.smtpauthorization.header", "SMTP Autorisierung"},
        {"label.smtpauthorization.credentials", "Benutzer/Passwort"},
        {"label.smtpauthorization.none", "Keine"},
        {"label.smtpauthorization.oauth2", "OAuth2"},
        {"label.smtpauthorization.user", "Benutzer"},
        {"label.smtpauthorization.user.hint", "SMTP Server Benutzername"},
        {"label.smtpauthorization.pass", "Passwort"},
        {"label.smtpauthorization.pass.hint", "SMTP Server Passwort"},
        {"label.security", "Verbindungssicherheit"},
        {"testmail.message.success", "Eine Test-eMail wurde erfolgreich versandt."},
        {"testmail.message.error", "Fehler beim Senden der Test-eMail:\n{0}"},
        {"testmail.title", "Senden einer Test-eMail"},
        {"testmail", "Test Mail"},
        //interface
        {"label.showhttpheader", "Anzeige der HTTP Header Konfiguration bei den Partnereinstellungen"},
        {"label.showquota", "Anzeige der Benachrichtigungskonfiguration bei den Partnereinstellungen"},
        {"label.cem", "Zertifikataustausch erlauben (CEM)"},
        {"label.outboundstatusfiles", "Statusdateien für ausgehende Transaktionen schreiben"},
        {"info.restart.client", "Sie müssen den Client neu starten, damit diese Änderungen gültig werden!"},
        {"remotedir.select", "Verzeichnis auf dem Server wählen"},
        //retry
        {"label.retry.max", "Max Anzahl der Versuche zum Verbindungsaufbau"},
        {"label.retry.max.help", "<HTML><strong>Max Anzahl der Versuche zum Verbindungsaufbau</strong>"
            + "<br><br>Dies ist die Anzahl der Wiederholungsversuche, die verwendet werden, um Verbindungen "
            + "zu einem Partner zu wiederholen, wenn eine Verbindung nicht hergestellt werden konnte. Die "
            + "Wartezeit zwischen diesen Wiederholungsversuchen kann in der Eigenschaft <strong>Wartezeit zwischen Verbindungswiederholungen</strong> eingestellt werden."
            + "</HTML>"},
        {"label.retry.waittime", "Wartezeit zwischen Verbindungswiederholungen"},
        {"label.retry.waittime.help", "<HTML><strong>Wartezeit zwischen Verbindungswiederholungen</strong>"
            + "<br><br>Dies ist die Zeit in Sekunden, die das System wartet, bevor es erneut eine Verbindung "
            + "zum Partner herstellt. Ein erneuter Verbindungsversuch wird nur dann durchgeführt, wenn "
            + "es nicht möglich war, eine Verbindung zu einem Partner herzustellen (z.B. Ausfall des "
            + "Partnersystems oder Infrastrukturproblem). Die Anzahl der Verbindungswiederholungen kann "
            + "in der Eigenschaft <strong>Maximale Anzahl von Verbindungswiederholungen</strong> konfiguriert werden."
            + "</HTML>"},
        {"label.sec", "s"},
        {"keystore.hint", "<HTML><strong>Achtung:</strong><br>Bitte ändern Sie diese Parameter nur, wenn Sie externe Keystores "
            + "einbinden möchten. Mit veränderten Pfaden kann es zu Problemen beim Update kommen.</HTML>"},
        {"maintenancemultiplier.day", "Tag(e)"},
        {"maintenancemultiplier.hour", "Stunde(n)"},
        {"maintenancemultiplier.minute", "Minute(n)"},
        {"label.logpollprocess", "Informationen zum Pollprozess im Protokoll anzeigen (viele Einträge - nicht im Produktivbetrieb verwenden)"},
        {"label.max.outboundconnections", "Max ausgehende parallele Verbindungen"},
        {"label.max.outboundconnections.help", "<HTML><strong>Max ausgehende parallele Verbindungen</strong>"
            + "<br><br>Dies ist die Anzahl der maximalen parallelen ausgehenden Verbindungen, die Ihr "
            + "System öffnen wird. Dieser Wert dient hauptsächlich dazu, Ihr Partnersystem vor einer Überlastung "
            + "durch eingehende Verbindungen von Ihrer Seite zu schützen.</HTML>"},
        {"label.max.inboundconnections", "Max eingehende parallele Verbindungen"},
        {"label.max.inboundconnections.help", "<HTML><strong>Max eingehende parallele Verbindungen</strong>"
            + "<br><br>Dies ist die Anzahl der maximalen parallelen eingehenden Verbindungen, die zu Ihrem "
            + "System geöffnet werden dürfen. "
            + "Diese Einstellung wird an den eingebetteten HTTP Server weiter gegeben, "
            + "Sie müssen nach einer Änderung den AS2 Server neu starten."
            + "</HTML>"},
        {"event.preferences.modified.subject", "Der Wert {0} der Servereinstellungen wurde modifiziert"},
        {"event.preferences.modified.body", "Alter Wert: {0}\nNeuer Wert: {1}"},
        {"event.notificationdata.modified.subject", "Die Einstellungen zur Benachrichtigung wurden verändert"},
        {"event.notificationdata.modified.body", "Die Benachrichtigungsdaten wurden von\n\n{0}\n\nnach\n\n{1}\n\n verändert."},
        {"label.maxmailspermin", "Max Anzahl von Benachrichtigungen/min"},
        {"label.maxmailspermin.help", "<HTML><strong>Max Anzahl von Benachrichtigungen/min</strong><br><br>"
            + "Um zu viele Mails zu vermeiden, können Sie Benachrichtigungen zusammenfassen, indem Sie die maximale "
            + "Anzahl von Benachrichtigungen pro Minute festlegen. Mit dieser Funktion erhalten Sie Mails, die "
            + "mehrere Benachrichtigungen enthalten."
            + "</HTML>"},
        {"systemmaintenance.deleteoldtransactions.help", "<HTML><strong>Löschen alter Nachrichten</strong><br><br>Dies legt den Zeitrahmen fest, in dem die "
            + "Transaktionen und die zugehörigen Daten (z.B. temporäre Dateien) im System verbleiben und in der Transaktionsübersicht "
            + "angezeigt werden sollen.<br>Diese Einstellungen betreffen <strong>nicht</strong> Ihre empfangenen Daten/Dateien, diese "
            + "bleiben unberührt. Für gelöschte Transaktionen ist das Transaktionsprotokoll über die Funktionalität der Logsuche "
            + "weiterhin verfügbar."
            + "</HTML>"},
        {"systemmaintenance.deleteoldstatistic.help", "<HTML><strong>Löschen alter Statistikdaten</strong><br><br>Das System sammelt Kompatibilitätsdaten "
            + "der Partnersysteme und kann diese als Statistik darstellen. Dies legt den Zeitrahmen fest, in dem diese Daten vorgehalten werden.</HTML>"},
        {"systemmaintenance.deleteoldlogdirs.help", "<HTML><strong>Löschen alter Logverzeichnisse</strong><br><br>"
            + "Auch wenn alte Transaktionen gelöscht wurden, lassen sich die Vorgänge noch über bestehende Protokolldateien nachvollziehen. "
            + "Diese Einstellung löscht diese Protokolldateien und auch alle Dateien für Systemereignisse, die in den gleichen Zeitraum fallen.</HTML>"},
        {"label.colorblindness", "Unterstützung für Farbblindheit"},
        {"warning.clientrestart.required", "Die Client Einstellungen wurden geändert - bitte starten Sie den Client neu, damit sie gültig werden"},
        {"warning.serverrestart.required", "Bitte starten Sie den Server neu, damit diese Änderungen gültig werden."},
        {"label.darkmode", "Dunkler Modus"},
        {"label.litemode", "Heller Modus"},
        {"label.trustallservercerts", "TLS: Allen Endserverzertifikaten Ihrer AS2 Partner vertrauen"},
        {"label.trustallservercerts.help", "<HTML><strong>TLS: Allen Endserverzertifikaten Ihrer AS2 Partner vertrauen</strong><br><br>"
            + "Normalerweise ist es für TLS erforderlich, alle Zertifikate der Trust Chain des AS2 Systems Ihres Partners in Ihrem TLS Zertifikatsmanager "
            + "zu halten. Wenn Sie diese Option aktivieren, vertrauen Sie beim ausgehenden Verbindungsaufbau dem Endzertifikat Ihres Partnersystems, wenn Sie nur die zugehörigen Stamm- und Zwischenzertifikate im TLS Zertifikatsmanager vorhalten. "
            + "Bitte beachten Sie, dass diese Option nur sinnvoll ist, wenn Ihr Partner ein beglaubigtes Zertifikat verwendet - selbst signierte Zertifikate werden ohnehin akzeptiert."
            + "<br><br><strong>Warnung:</strong> Die Aktivierung dieser Option senkt das Sicherheitsniveau, da Man-in-the-Middle Angriffe möglich sind!" + "</HTML>"},
        {"label.stricthostcheck", "TLS: Strikte Prüfung des Hostnames"},
        {"label.stricthostcheck.help", "<HTML><strong>TLS: Strikte Prüfung des Hostnames</strong><br><br>"
            + "Hiermit stellen Sie ein, ob im Falle einer ausgehenden TLS Verbindung geprüft werden soll, ob der Common Name "
            + "(CN) des entfernten Zertifikats mit dem entfernten Host übereinstimmt. Diese Prüfung gilt nur für "
            + "beglaubigte Zertifikate."
            + "</HTML>"},
        {"label.httpport", "HTTP Eingangsport"},
        {"label.httpport.help", "<HTML><strong>HTTP Eingangsport</strong><br><br>"
            + "Dies ist der Port für eingehende unverschlüsselte Verbindungen. Diese Einstellung wird an den eingebetteten HTTP Server weiter gegeben, "
            + "Sie müssen nach einer Änderung den AS2 Server neu starten.<br>"
            + "Der Port ist Teil der URL, an die Ihr Partner AS2 Nachrichten senden muss. Dies ist http://Host:<strong>Port</strong>/as2/HttpReceiver."
            + "</HTML>"
        },
        {"label.httpsport", "HTTPS Eingangsport"},
        {"label.httpsport.help", "<HTML><strong>HTTPS Eingangsport</strong><br><br>"
            + "Dies ist der Port für eingehende verschlüsselte Verbindungen (TLS). "
            + "Diese Einstellung wird an den eingebetteten HTTP Server weiter gegeben, "
            + "Sie müssen nach einer Änderung den AS2 Server neu starten.<br>"
            + "Der Port ist Teil der URL, an die Ihr Partner AS2 Nachrichten senden muss. Dies ist https://Host:<strong>Port</strong>/as2/HttpReceiver."
            + "</HTML>"
        },
        {"embedded.httpconfig.not.available", "HTTP Server nicht verfügbar oder Zugriffsprobleme auf Konfigurationsdatei"},};

}
