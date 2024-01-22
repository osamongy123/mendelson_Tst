//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessageParser_de.java 46    26.08.21 11:30 Heller $
package de.mendelson.comm.as2.message;

import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 * @author S.Heller
 * @version $Revision: 46 $
 */
public class ResourceBundleAS2MessageParser_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {        
        {"mdn.answerto", "Die eingegangene Empfangsbestätigung (MDN) mit der Nachrichtennummer \"{0}\" ist die Antwort auf die ausgegangene AS2 Nachricht \"{1}\"."},
        {"mdn.state", "Status der eingegangenen Empfangsbestätigung (MDN) ist [{0}]."},
        {"mdn.details", "Details der eingegangenen Empfangsbestätigung (MDN) von {0}: \"{1}\""},
        {"mdn.incoming", "Eingegangene Übertragung ist eine Empfangsbestätigung (MDN)."},
        {"mdn.incoming.ha", "Eingegangene Übertragung ist eine Empfangsbestätigung (MDN), verarbeitet von [{0}]." }, 
        {"mdn.incoming.relationship", "Eingegangene Übertragung ist eine Empfangsbestätigung (MDN) [{0}]" },
        {"mdn.incoming.relationship.ha", "Eingegangene Übertragung ist eine Empfangsbestätigung (MDN) [{0}], verarbeitet von [{1}]." }, 
        {"msg.incoming", "Eingehende Übertragung ist eine AS2 Nachricht [{0}], Rohdatengrösse: {1}"},
        {"msg.incoming.ha", "Eingehende Übertragung ist eine AS2 Nachricht [{0}], Rohdatengrösse: {1}, verarbeitet von [{2}]." }, 
        {"msg.incoming.identproblem", "Eingehende Übertragung ist eine AS2 Nachricht. Sie wurde nicht verarbeitet, weil es ein Problem mit der Partneridentifikation gab." },   
        {"msg.already.processed", "Eine Nachricht mit der Nachrichtennummer [{0}] wurde bereits verarbeitet" },
        {"mdn.signed", "Empfangsbestätigung (MDN) ist digital signiert ({0})."},
        {"mdn.unsigned.error", "Eingegangene Empfangsbestätigung (MDN) ist entgegen der Partnerkonfiguration \"{0}\" NICHT digital signiert."},
        {"mdn.signed.error", "Eingegangene Empfangsbestätigung (MDN) ist entgegen der Partnerkonfiguration \"{0}\" digital signiert."},
        {"msg.signed", "Eingegangene AS2 Nachricht ist digital signiert."},
        {"msg.encrypted", "Eingegangene AS2 Nachricht ist verschlüsselt."},
        {"msg.notencrypted", "Eingegangene AS2 Nachricht ist nicht verschlüsselt."},
        {"msg.notsigned", "Eingegangene AS2 Nachricht ist nicht digital signiert."},
        {"mdn.notsigned", "Eingegangene Empfangsbestätigung (MDN) ist nicht digital signiert."},
        {"message.signature.ok", "Digitale Signatur der eingegangenen AS2 Nachricht wurde erfolgreich überprüft."},
        {"mdn.signature.ok", "Digitale Signatur der eingegangenen MDN wurde erfolgreich überprüft."},
        {"message.signature.failure", "Überprüfung der digitalen Signatur der eingegangenen AS2 Nachricht schlug fehl - {0}"},
        {"mdn.signature.failure", "Überprüfung der digitalen Signatur der eingegangenen MDN schlug fehl - {0}"},
        {"message.signature.using.alias", "Benutze das Zertifikat \"{0}\" zum Überprüfen der digitalen Signatur der eingegangenen AS2 Nachricht."},
        {"mdn.signature.using.alias", "Benutze das Zertifikat \"{0}\" zum Überprüfen der digitalen Signatur der eingegangenen MDN."},
        {"decryption.done.alias", "Die Daten der eingegangenen AS2 Nachricht wurden mit Hilfe des Schlüssels \"{0}\" entschlüsselt, der Verschlüsselungsalgorithmus war \"{1}\", der Schlüsselverschlüsselungsalgorithmus war \"{2}\"."},
        {"mdn.unexpected.messageid", "Die eingegangene Empfangsbestätigung (MDN) referenziert eine AS2 Nachricht der Referenznummer \"{0}\", die nicht existert."},
        {"mdn.unexpected.messageid", "Die eingegangene Empfangsbestätigung (MDN) referenziert die AS2 Nachricht der Referenznummer \"{0}\", die keine MDN erwartet."},
        {"data.compressed.expanded", "Die komprimierten Nutzdaten der eingegangenen AS2 Nachricht wurden von {0} auf {1} expandiert."},
        {"found.attachments", "Es wurden {0} Anhänge mit Nutzdaten in der AS2 Nachricht gefunden."},
        {"decryption.inforequired", "Zum Entschlüsseln der eingegangenen AS2 Nachricht ist ein Schlüssel mit folgenden Parametern notwendig:\n{0}"},
        {"decryption.infoassigned", "Zum Entschlüsseln der eingegangenen AS2 Nachricht wurde ein Schlüssel mit folgenden Parametern benutzt (Alias \"{0}\"):\n{1}"},
        {"signature.analyzed.digest", "Für die digitale Signatur wurde vom Sender der Algorithmus \"{0}\" verwendet."},
        {"signature.analyzed.digest.failed", "Das System konnte den Signaturalgorithmus der eingehenden AS2 Nachricht nicht herausfinden." },
        {"filename.extraction.error", "Extrahieren des Originaldateinamens der eingegangenen AS2 Nachricht ist nicht möglich: \"{0}\", wird ignoriert."},
        {"contentmic.match", "Der Message Integrity Code (MIC) stimmt mit der gesandten AS2 Nachricht überein."},
        {"contentmic.failure", "Der Message Integrity Code (MIC) stimmt nicht mit der gesandten AS2 Nachricht überein (erwartet: {0}, erhalten: {1})."},
        {"found.cem", "Die eingegangene Nachricht ist eine Anfrage für einen Zertifikataustausch (CEM)."},
        {"data.unable.to.process.content.transfer.encoding", "Es sind Daten empfangen worden, die nicht verarbeitet werden konnten, weil sie Fehler enthalten. Das Content Transfer Encoding \"{0}\" ist unbekannt."},
        {"original.filename.found", "Der originale Dateiname wurde vom Sender als \"{0}\" übertragen." },
        {"original.filename.undefined", "Der Originaldateiname wurde nicht übertragen." },
        {"data.not.compressed", "Die eingegangenen AS2 Daten sind unkomprimiert." },
    };
}
