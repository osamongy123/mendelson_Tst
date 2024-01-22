//$Header: /as2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleDialogSubjectAlternativeNames_de.java 3     4/06/18 1:35p Hel $
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
 * ResourceBundle to localize the converter IDE internal editor frame - if you want to
 * localize eagle to your language, please contact us: localize@mendelson.de
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleDialogSubjectAlternativeNames_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {    
        {"title", "Alternative Antragstellernamen verwalten" },
        {"info", "Mit Hilfe dieses Dialogs können Sie die alternativen Antragstellernamen für die Generierungsprozess des Schlüssels verwalten (subject alternative name). Diese Werte sind eine Erweiterung des x.509 Zertifikats. Wenn Ihr Partner das unterstützt, können Sie hier zum Beispiel zusätzliche Domains für Ihren Schlüssel eintragen. Im OFTP2 kann es ja nach Partner notwendig sein, einige Felder mit Kenndaten zu füllen, zum Beispiel die Odette Id Ihres Systems als URL im Format \"oftp://OdetteId\" und erneut Ihre Domain im Feld DNS-Name." },
        {"button.ok", "Ok" },
        {"button.cancel", "Abbruch" },
        {"label.add", "Hinzufügen" },
        {"label.del", "Löschen" },
        {"header.name", "Typ" },
        {"header.value", "Wert" },
    };
    
}