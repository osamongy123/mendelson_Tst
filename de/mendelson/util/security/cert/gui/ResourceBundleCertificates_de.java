//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleCertificates_de.java 25    12/12/22 14:18 Heller $
package de.mendelson.util.security.cert.gui;

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
public class ResourceBundleCertificates_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"display.ca.certs", "CA Zertifikate anzeigen"},
        {"button.delete", "Schlüssel/Zertifikat löschen"},
        {"button.delete.all.expired", "Alle abgelaufenen Schlüssel/Zertifikate löschen" },
        {"button.edit", "Alias umbenennen"},
        {"button.newkey", "Schlüssel importieren"},
        {"button.newcertificate", "Zertifikat importieren"},
        {"button.export", "Zertifikat exportieren"},
        {"button.reference", "Verwendung anzeigen" },     
        {"button.keycopy", "Zur {0} Verwaltung kopieren" },       
        {"button.keycopy.tls", "TLS" },
        {"button.keycopy.signencrypt", "Verschlüsselung/Signatur" },
        {"menu.file", "Datei" },        
        {"menu.file.close", "Beenden" },
        {"menu.import", "Import"},
        {"menu.export", "Export"},
        {"menu.tools", "Erweitert"},
        {"menu.tools.generatekey", "Neuen Schlüssel generieren (Self signed)"},
        {"menu.tools.generatecsr", "Zertifikat beglaubigen: CSR generieren (an CA)"},
        {"menu.tools.generatecsr.renew", "Zertifikat erneuern: CSR generieren (an CA)"},
        {"menu.tools.importcsr", "Zertifikat beglaubigen: Antwort der CA auf CSR importieren"},
        {"menu.tools.importcsr.renew", "Zertifikat erneuern: Antwort der CA auf CSR importieren"},
        {"label.selectcsrfile", "Bitte wählen Sie die Datei zum Speichern des CSR"},
        {"label.cert.import", "Zertifikat importieren (vom Partner)"},
        {"label.cert.export", "Zertifikat exportieren (für den Partner)"},
        {"label.key.import.pem", "Eigenen privaten Schlüssel importieren (von PEM)"},
        {"label.key.import.pkcs12", "Eigenen privaten Schlüssel importieren (von PKCS#12)"},
        {"label.key.import.jks", "Eigenen privaten Schlüssel importieren (von JKS, JAVA Keystore Format)"},
        {"label.key.export.pkcs12", "Schlüssel exportieren (PKCS#12) (nur für Backup Zwecke!)"},
        {"label.keystore", "Keystore Datei:"},
        {"title.signencrypt", "Verfügbare Schlüssel und Zertifikate (Verschlüsselung, Signaturen)"},
        {"title.ssl", "Verfügbare Schlüssel und Zertifikate (SSL/TLS)"},
        {"button.ok", "Ok"},
        {"button.cancel", "Abbrechen"},
        {"filechooser.certificate.import", "Bitte wählen Sie die Zertifikatdatei für den Import"},
        {"certificate.import.success.message", "Das Zertifikat wurde erfolgreich mit dem Alias \"{0}\" importiert."},
        {"certificate.ca.import.success.message", "Das CA Zertifikat wurde erfolgreich mit dem Alias \"{0}\" importiert."},
        {"certificate.import.success.title", "Erfolg"},
        {"certificate.import.error.message", "Es gab einen Fehler während des Imports:\n{0}"},
        {"certificate.import.error.title", "Fehler"},
        {"certificate.import.alias", "Alias für dieses Zertifikat:"},
        {"keystore.readonly.message", "Die zugrundeliegende Keystore Datei ist schreibgeschützt.\nEine Schreiboperation (Speichern/Verändern) kann nicht durchgeführt werden."},
        {"keystore.readonly.title", "Keystore schreibgeschützt - Bearbeiten nicht möglich"},
        {"modifications.notalllowed.message", "Modifikationen sind nicht möglich"},
        {"generatekey.error.message", "{0}"},
        {"generatekey.error.title", "Fehler bei der Schlüsselerstellung"},
        {"tab.info.basic", "Details"},
        {"tab.info.extension", "Erweiterungen"},
        {"tab.info.trustchain", "Zertifizierungspfad" },        
        {"dialog.cert.delete.message", "Wollen Sie wirklich das Zertifikat mit dem Alias \"{0}\" löschen?"},
        {"dialog.cert.delete.title", "Zertifikat löschen"},
        {"title.cert.in.use", "Zertifikat wird verwendet" },
        {"cert.delete.impossible", "Der Eintrag kann nicht gelöscht werden, er wird verwendet.\nBitte verwenden Sie \"Verwendung anzeigen\" für weitere Informationen." },
        {"module.locked", "Diese Zertifikatverwaltung wird aktuell exklusiv von einem anderen Client geöffnet, Sie können keine Änderungen vornehmen!" },
        {"label.trustanchor", "Trust anchor:" },
        {"warning.testkey", "Öffentlich verfügbarer mendelson Testschlüssel - nicht im produktiven Betrieb verwenden!" },
        {"label.key.valid", "Dieser Schlüssel ist gültig" },
        {"label.key.invalid", "Dieser Schlüssel ist ungültig" },
        {"label.cert.valid", "Dieses Zertifikat ist gültig" },
        {"label.cert.invalid", "Dieses Zertifikat ist ungültig" },
        {"warning.deleteallexpired.text", "Wollen Sie wirlich {0} abgelaufene und unbenutzte Einträge löschen?" },
        {"warning.deleteallexpired.title", "Abgelaufene und unbenutzte Schlüssel/Zertifkate löschen" },
        {"warning.deleteallexpired.noneavailable.title", "Keine verfügbar" },
        {"warning.deleteallexpired.noneavailable.text", "Es gibt keine abgelaufenen, unbenutzen Einträge" },
        {"success.deleteallexpired.title", "Abgelaufene und unbenutzte Zertifikate/Schlüssel löschen" },
        {"success.deleteallexpired.text", "{0} abgelaufene und unbenutzte Schlüssel/Zertifikate wurden gelöscht" },
        {"warning.deleteallexpired.expired.but.used.title", "Benutzte Schlüssel/Zertifikate" },
        {"warning.deleteallexpired.expired.but.used.text", "{0} abgelaufene Schlüssel/Zertifikate werden in der Konfiguration verwendet und daher nicht gelöscht " },
        {"module.locked.title", "Modul ist in Verwendung" },
        {"module.locked.text", "Das Modul {0} wird von einem anderen Client exklusiv verwendet ({1})." },
        {"keycopy.target.exists.title", "Eintrag exisitiert bereits im Ziel"},
        {"keycopy.target.exists.text", "Dieser Eintrag exisiert bereits in der Zielzertifikatverwaltung (Alias {0})."},
        {"keycopy.target.ro.title", "Ziel schreibgeschützt" },
        {"keycopy.target.ro.text", "Operation fehlgeschlagen - Schlüsseldatei des Ziels ist schreibgeschützt." },
        {"keycopy.success.text", "Der Eintrag [{0}] wurde erfolgreich kopiert" },
    };
}
