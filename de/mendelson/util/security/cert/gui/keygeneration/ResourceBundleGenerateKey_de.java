//$Header: /as2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleGenerateKey_de.java 13    24/10/22 12:52 Heller $
package de.mendelson.util.security.cert.gui.keygeneration;
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
 * @version $Revision: 13 $
 */
public class ResourceBundleGenerateKey_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {

        {"title", "Schlüsselerstellung" },
        {"button.ok", "Ok" },
        {"button.cancel", "Abbruch" },
        {"label.keytype", "Schlüsseltyp" },
        {"label.keytype.help", "<HTML><strong>Schlüsseltyp</strong><br><br>"
            + "Dies ist der Algorithmus zum Erstellen des Schlüssels. Für die daraus resultierenden Schlüssel gibt es je nach Algorithmus vor- und Nachteile.<br>"
            + "Wir würden Ihnen Stand 2022 einen RSA Schlüssel mit der Schlüssellänge 2048 oder 4096 bit empfehlen."
            + "</HTML>"
        },
        {"label.signature", "Signatur" },
        {"label.signature.help", "<HTML><strong>Signatur</strong><br><br>"
            + "Dies ist der Signaturalgorithmus, mit dem der Schlüssel signiert ist. "
            + "Er wird für Integritätstests des Schlüssels selber benötigt. Dieser Parameter hat nichts mit den "
            + "Signaturfähigkeiten des Schlüssels zu tun - Sie können also zum Beispiel mit einem SHA-1 signierten "
            + "Schlüssel auch SHA-2 Signaturen erstellen "
            + "oder anders herum.<br>"
            + "Wir würden Ihnen Stand 2022 einen SHA-2 signierten Schlüssel empfehlen."
            + "</HTML>"
        },
        {"label.size", "Schlüssellänge" },
        {"label.size.help", "<HTML><strong>Schlüssellänge</strong><br><br>"
            + "Dies ist die Schlüssellänge des Schlüssels. Prinzipiell sind kryptographische Operationen mit "
            + "grösserer Schlüssellänge sicherer als kryptographische Operationen mit Schlüsseln kleinerer Schlüssellänge. "
            + "Der Nachteil grosser Schlüssellängen ist jedoch, dass kryptographische Operationen signifikant länger dauern, "
            + "was die Datenverarbeitung je nach Rechenleistung signifikant verlangsamen kann.<br>"
            + "Wir würden Ihnen Stand 2022 einen Schlüssel der Länge 2048 oder 4096 bit empfehlen."
            + "</HTML>"
        },
        {"label.commonname", "Common Name" },
        {"label.commonname.help", "<HTML><strong>Common Name</strong><br><br>"
            + "Dies ist der Name Ihrer Domain, wie es dem DNS Eintrag entspricht. Dieser Parameter ist wichtig für das Handshake einer TLS Verbindung. "
            + "Es ist möglich (aber nicht empfehlenswert!), hier eine IP Adresse einzugeben. Es ist ebenfalls möglich, "
            + "ein Wildcard Zertifikat zu erstellen, wenn Sie hier Teile der Domain durch * ersetzen. "
            + "Aber auch das ist nicht empfehlenswert, weil nicht alle Partner solche Schlüssel akzeptieren.<br>"
            + "Wenn Sie diesen Schlüssel als TLS Schlüssel verwenden möchten und dieser Eintrag auf eine nichtexistiente Domain verweist oder nicht Ihrer Domain entspricht, "
            + "sollten die meisten Systeme eingehende TLS Verbindungen abbrechen."
            + "</HTML>"
        },
        {"label.commonname.hint", "(Domain Name des Servers)" },
        {"label.organisationunit", "Organisation (Unit)" },
        {"label.organisationname", "Organisation (Name)" },
        {"label.locality", "Ort" },
        {"label.locality.hint", "(Stadt)" },
        {"label.state", "Land" },
        {"label.countrycode", "Ländercode" },
        {"label.countrycode.hint", "(2 Zeichen, ISO 3166)" },
        {"label.mailaddress", "Mail Adresse" },
        {"label.mailaddress.help", "<HTML><strong>Mail Adresse</strong><br><br>"
            + "Dies ist die Mailadresse, die mit dem Schlüssel verknüpft ist. Technisch ist "
            + "dieser Parameter uninteressant. Wenn Sie den Schlüssel jedoch beglaubigen lassen "
            + "möchten, dient diese Mailadresse in der Regel für die Kommunikation mit der CA. "
            + "Ausserdem sollte die Mailadresse sich auch auf der Domain des Servers befinden "
            + "und so etwas wie webmaster@domain oder ähnlichem entsprechen, "
            + "weil die meisten CAs somit prüfen, ob Sie im Besitz der zugehörigen Domain sind."
            + "</HTML>"
        },
        {"label.validity", "Gültigkeit in Tagen" },
        {"label.validity.help", "<HTML><strong>Gültigkeit in Tagen</strong><br><br>"
            + "Dieser Wert ist nur für self signed Schlüssel interessant. Im Falle einer Beglaubigung wird die CA diesen Wert überschreiben."
            + "</HTML>"
        },
        {"label.purpose", "Verwendungszweck / Schlüsselerweiterungen" },
        {"label.purpose.encsign", "Verschlüsselung und digitale Signatur" },
        {"label.purpose.ssl", "TLS/SSL" },
        {"label.subjectalternativenames", "Alternative Antragstellernamen" },        
        {"warning.mail.in.domain", "Die Mailadresse ist nicht Teil der Domain \"{0}\" (z.B. meinname@{0}).\nDies kann ein Problem sein, wenn der Schlüssel später beglaubigt werden soll."},
        {"warning.nonexisting.domain", "Die Domain \"{0}\" existiert nicht." },
        {"warning.invalid.mail", "Die Mail Adresse \"{0}\" ist ungültig." },
        {"button.reedit", "Überarbeiten" },
        {"button.ignore", "Warnungen ignorieren" },
        {"warning.title", "Mögliches Problem der Schlüsselparameter" },
        {"view.expert", "Experten Ansicht" },
        {"view.basic", "Standard Ansicht" },
        {"label.namedeccurve", "Kurve" },
        {"label.namedeccurve.help", "<HTML><strong>Kurve</strong><br><br>"
            + "Hiermit wählen Sie den Namen der EC Kurve aus, der für die Generation des Schlüssels verwendet werden soll. "
            + "Die gewünschte Schlüssellänge ist in der Regel Teil des Namens der Kurve, so hat zum Beispiel der Schlüssel der Kurve "
            + "\"BrainpoolP256r1\" eine Länge von 256bit. Die Stand 2022 am meisten verwendete Kurve (ca 75% aller EC Zertifikate im Internet verwenden sie) ist NIST P-256, die Sie hier unter dem "
            + "Namen \"Prime256v1\" finden. Sie ist Stand 2022 die Standardkurve von OpenSSL."
            + "</HTML>" },
    };
    
}