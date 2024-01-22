//$Header: /as2/de/mendelson/comm/as2/cem/gui/ResourceBundleCEMOverview_de.java 7     6/21/18 12:48p Heller $
package de.mendelson.comm.as2.cem.gui;
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
 * @version $Revision: 7 $
 */
public class ResourceBundleCEMOverview_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Verwaltung Zertifikataustausch" },
        {"button.sendcem", "Neuer Austausch" },
        {"button.requestdetails", "Details der Anfrage" },
        {"button.responsedetails", "Details der Antwort" },
        {"button.exit", "Schliessen" },
        {"button.cancel", "Abbrechen" },
        {"button.refresh", "Aktualisieren" },
        {"button.remove", "Löschen" },
        {"header.state", "Antwort" },
        {"header.category", "Benutzt für" },
        {"header.requestdate", "Anfragedatum" },
        {"header.initiator", "Von" },
        {"header.receiver", "An" },
        {"label.certificate", "Zertifikat:"},
        {"header.alias", "Zertifikat"},
        {"header.activity", "Systemaktivität" },
        {"activity.waitingforprocessing", "Warte auf Verarbeitung" },
        {"activity.waitingforanswer", "Warte auf Antwort" },
        {"activity.waitingfordate", "Warte bis zum Aktivierungdatum ({0})" },
        {"activity.activated", "Keine - Aktiviert am {0}" },
        {"activity.none", "Keine" },
        {"tab.certificate", "Zertifikatinformation" },
        {"tab.reasonforrejection", "Ablehnungsbegründung" },
    };
    
}