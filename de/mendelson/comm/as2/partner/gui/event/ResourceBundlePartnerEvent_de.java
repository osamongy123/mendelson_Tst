//$Header: /as2/de/mendelson/comm/as2/partner/gui/event/ResourceBundlePartnerEvent_de.java 4     4.01.21 9:48 Heller $
package de.mendelson.comm.as2.partner.gui.event;
import de.mendelson.comm.as2.partner.PartnerEventInformation;
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
 * @version $Revision: 4 $
 */
public class ResourceBundlePartnerEvent_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"type." + PartnerEventInformation.TYPE_ON_RECEIPT, "nach Empfang"},
        {"type." + PartnerEventInformation.TYPE_ON_SENDERROR, "nach Versand (Fehler)"},
        {"type." + PartnerEventInformation.TYPE_ON_SENDSUCCESS, "nach Versand (Erfolg)"},
        {"title.select.process", "Bitte wählen Sie einen neuen Prozess als Ereignis ({0})" },
        {"tab.newprocess", "Verfügbare Prozesse zur Nachbearbeitung" },
        {"process.executeshell", "Ausführung eines Shell Kommandos" },
        {"process.executeshell.description", "Führen Sie ein Shell Kommando oder ein Skript aus, um Daten nachzubearbeiten." },
        {"process.movetopartner", "Weiterleitung an Partner" },
        {"process.movetopartner.description", "Weiterleitung an einen Partner, zum Beispiel von der DMZ zum ERP System." },
        {"process.movetodirectory", "In Verzeichnis verschieben" },
        {"process.movetodirectory.description", "Verschieben Sie die Daten in ein anderes Verzeichnis" },
        {"button.ok", "Ok" },
        {"button.cancel", "Abbruch" },
        {"title.configuration.shell", "Shell Kommando Konfiguration [Partner {0}, {1}]"},
        {"title.configuration.movetodir", "Nachrichten in Verzeichnis verschieben [Partner {0}, {1}]"},
        {"title.configuration.movetopartner", "Weiterleitung von Daten an einen Partner [Partner {0}, {1}]"},
        {"label.shell.info", "<HTML>Bitte richten Sie den Shell-Befehl ein, der in diesem Fall ausgeführt werden soll. Bitte denken Sie daran, dass dies betriebssystemspezifisch ist, es wird auf die Standard-Shell Ihres Betriebssystems umgeleitet.</HTML>"},
        {"label.shell.command", "Kommando ({0}): "},
        {"shell.hint.replacement." + PartnerEventInformation.TYPE_ON_RECEIPT, "<HTML>Die folgenden Variablen werden in diesem Befehl durch Systemwerte ersetzt, bevor er ausgeführt wird:<br><i>$'{'filename}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'originalfilename}</i></HTML>"},
        {"shell.hint.replacement." + PartnerEventInformation.TYPE_ON_SENDERROR, "<HTML>Die folgenden Variablen werden in diesem Befehl durch Systemwerte ersetzt, bevor er ausgeführt wird:<br><i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
        {"shell.hint.replacement." + PartnerEventInformation.TYPE_ON_SENDSUCCESS, "<HTML>Die folgenden Variablen werden in diesem Befehl durch Systemwerte ersetzt, bevor er ausgeführt wird:<br><i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
        {"shell.hint.samples", "<HTML><strong>Beispiele</strong><br>Windows: <i>cmd /c move \"$'{'filename}\" \"c:\\zielverzeichnis\"</i><br>Linux: <i>mv \"$'{'filename}\" \"~/zielverzeichnis/\"</i></HTML>"},
        {"label.movetodir.info", "<HTML>Bitte richten Sie das serverseitige Verzeichnis ein, in das die Nachricht verschoben werden soll.</HTML>"},        
        {"label.movetodir.targetdir", "Zielverzeichnis ({0}): "},
        {"label.movetodir.remotedir.select", "Bitte wählen Sie das Zielverzeichnis auf dem Server" },        
        {"label.movetopartner.info", "<HTML>Bitte wählen Sie den entfernten Partner, an den die Nachricht weitergeleitet werden soll.</HTML>"},
        {"label.movetopartner", "Zielpartner: "},
        {"label.movetopartner.noroutingpartner", "<HTML>Es ist kein entfernter Partner im System verfügbar, an den Nachrichten gesendet werden können. Bitte fügen Sie zunächst einen Partner hinzu, an den Nachrichten gesendet werden sollen.</HTML>"},
    };
    
}