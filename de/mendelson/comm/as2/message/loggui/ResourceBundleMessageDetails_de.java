//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageDetails_de.java 25    1/09/22 14:19 Heller $
package de.mendelson.comm.as2.message.loggui;
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
 * @version $Revision: 25 $
 */
public class ResourceBundleMessageDetails_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        
        {"title", "Nachrichtendetails" },
        {"title.cem", "Nachrichtendetails des Zertifikataustausch (CEM)" },        
        {"transactionstate.ok.send", "<HTML>Die {0} Nachricht wurde erfolgreich zum Partner \"{1}\" gesendet - er hat eine entsprechende Bestätigung geschickt.</HTML>"},
        {"transactionstate.ok.receive", "<HTML>Die {0} Nachricht wurde erfolgreich vom Partner \"{1}\" empfangen. Eine entsprechende Bestätigung wurde an den Partner verschickt.</HTML>"},
        {"transactionstate.ok.details", "<HTML>Die Daten wurden übertragen und die Transaktion wurde erfolgreich abgeschlossen</HTML>" },
        {"transactionstate.error.unknown", "Ein unbekannter Fehler trat auf." },
        {"transactionstate.error.out", "<HTML>Sie haben die {0} Nachricht erfolgreich an Ihren Partner \"{1}\" übermittelt - er war aber nicht in der Lage, sie zu verarbeiten und antwortete mit dem Fehler [{2}]</HTML>" },
        {"transactionstate.error.in", "<HTML>Sie empfingen die {0} Nachricht erfolgreich von Ihrem Partner \"{1}\" - Ihr System war aber nicht in der Lage, sie zu verarbeiten und antwortete mit dem Fehler [{2}]</HTML>" },
        {"transactionstate.error.unknown-trading-partner", "<HTML>Sie und Ihr Partner haben unterschiedliche AS2 Kennungen für die beiden Partner der Übertragung in der Konfiguration. Die folgenden Kennungen wurden verwendet: \"{0}\" (Nachrichtensender), \"{1}\" (Nachrichtenempfänger)</HTML>" },
        {"transactionstate.error.authentication-failed", "<HTML>Der Nachrichtenempfänger konnte die Signatur des Senders in den Daten nicht erfolgreich prüfen. Dies ist meistens ein Konfigurationsproblem, da Sender und Empfänger hier das gleiche Zertifikat verwenden müssen. Bitte sehen Sie sich auch die MDN Details im Protokoll an - dieses könnte weitere Informationen enthalten.</HTML>" },
        {"transactionstate.error.decompression-failed", "<HTML>Der Nachrichtenempfänger konnte die empfangene Nachricht nicht dekomprimieren</HTML>" },
        {"transactionstate.error.insufficient-message-security", "<HTML>Der Nachrichtenempfänger erwartete einen höheren Sicherheitslevel für die empfangenen Daten (zum Beispiel verschlüsselte Daten anstelle von unverschlüsselten)</HTML>" },
        {"transactionstate.error.unexpected-processing-error", "<HTML>Dies ist eine sehr generische Fehlermeldung. Aus unbekanntem Grund konnte der Empfänger die Nachricht nicht verarbeiten.</HTML>" },
        {"transactionstate.error.decryption-failed", "<HTML>Der Nachrichtenempfänger konnte die Nachricht nicht entschlüsseln. Meistens ist das ein Konfigurationsproblem, verwendet der Sender das richtige Zertifikat zum Verschlüsseln?</HTML>" },
        {"transactionstate.error.connectionrefused", "<HTML>Sie haben versucht, das Partnersystem zu erreichen. Entweder schlug das fehl oder Ihr Partner hat nicht innerhalb der definierten Zeit mit einer Bestätigung geantwortet.</HTML>" },
        {"transactionstate.error.connectionrefused.details", "<HTML>Dies könnte ein Infrastrukturproblem sein, Ihr Partnersystem läuft gar nicht oder Sie haben die falsche Empfangs-URL in der Konfiguration eingegeben? Wenn die Daten übermittelt wurden und Ihr Partner sie nicht bestätigt hat, haben Sie eventuell das Zeitfenster für die Bestätigung zu klein gewählt?</HTML>" },
        {"transactionstate.error.asyncmdnsend", "<HTML>Eine Nachricht mit einer asynchronen MDN-Anforderung wurde empfangen und erfolgreich verarbeitet, aber Ihr System konnte die asynchrone MDN nicht zurücksenden oder sie wurde vom Partnersystem nicht akzeptiert.</HTML>" },
        {"transactionstate.error.asyncmdnsend.details", "<HTML>Der AS2-Message-Sender übermittelt die URL, an die er die MDN zurücksenden soll - entweder ist dieses System nicht erreichbar (Infrastrukturproblem oder das Partnersystem ist ausgefallen?) oder das Partnersystem hat die asynchrone MDN nicht akzeptiert und antwortete mit einem HTTP 400.</HTML>" },
        {"transactionstate.pending", "Diese Transaktion ist im Wartezustand." },
        {"transactiondetails.outbound.secure", "Dies ist eine ausgehende gesicherte Verbindung, Sie versenden Daten an den Partner \"{0}\"." },
        {"transactiondetails.outbound.insecure", "Dies ist eine ausgehende ungesicherte Verbindung, Sie versenden Daten an den Partner \"{0}\"." },
        {"transactiondetails.inbound.secure", "Dies ist eine eingehende gesicherte Verbindung, Sie empfangen Daten vom Partner \"{0}\"." },
        {"transactiondetails.inbound.insecure", "Dies ist eine eingehende ungesicherte Verbindung, Sie empfangen Daten vom Partner \"{0}\"." },
        {"transactiondetails.outbound.sync", " Sie empfangen die Bestätigung direkt als Antwort auf dem Rückkanal der ausgehenden Verbindung (synchrone MDN)." },
        {"transactiondetails.outbound.async", " Für die Bestätigung baut Ihr Partner eine neue Verbindung zu Ihnen auf (asynchrone MDN)." },
        {"transactiondetails.inbound.sync", " Sie senden die Bestätigung direkt als Antwort auf dem Rückkanal der eingehenden Verbindung (synchrone MDN)." },
        {"transactiondetails.inbound.async", " Sie senden die Bestätigung, indem Sie eine neue Verbindung zum Partner aufbauen (asynchrone MDN)." },        
        {"button.ok", "Ok" },
        {"header.timestamp", "Datum" },
        {"header.messageid", "Referenznummer" },
        {"message.raw.decrypted", "Übertragungsdaten (unverschlüsselt)" },         
        {"message.header", "Kopfdaten" },
        {"message.payload", "Nutzdaten" },
        {"message.payload.multiple", "Nutzdaten ({0})" },
        {"tab.log", "Log dieser Nachrichteninstanz" },
        {"header.encryption", "Verschlüsselung" },
        {"header.signature", "Digitale Signatur" },
        {"header.senderhost", "Sender" },
        {"header.useragent", "AS2 Server" },
    };
    
}