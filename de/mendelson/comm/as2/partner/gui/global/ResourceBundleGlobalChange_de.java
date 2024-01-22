//$Header: /as2/de/mendelson/comm/as2/partner/gui/global/ResourceBundleGlobalChange_de.java 1     26/08/22 14:11 Heller $
package de.mendelson.comm.as2.partner.gui.global;
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
 * @version $Revision: 1 $
 */
public class ResourceBundleGlobalChange_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Globale Änderungen an allen Partnern" },
        {"button.ok", "Schliessen" }, 
        {"button.set", "Setzen" }, 
        {"partnersetting.changed", "Einstellungen wurden geändert für {0} Partner." },  
        {"partnersetting.notchanged", "Einstellungen wurden nicht geändert - fehlerhafter Wert" },  
        {"info.text", "<HTML>Mit Hilfe dieses Dialog können Sie Parameter aller Partner "
            + "gleichzeitig auf definierte Werte setzen. Wenn Sie \"Setzen\" gedrückt haben, "
            + "wird der jeweilige Wert für <strong>ALLE</strong> Partner überschrieben.</HTML>" },
        {"label.dirpoll", "Verzeichnispoll für alle Partner durchführen" },
        {"label.maxpollfiles", "Maximale Anzahl von Dateien aller Partner pro Pollvorgang" },
        {"label.pollinterval", "Verzeichnis Pollintervall aller Partner" },
    };
    
}