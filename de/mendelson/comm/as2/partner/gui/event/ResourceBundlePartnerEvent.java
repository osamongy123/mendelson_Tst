//$Header: /as2/de/mendelson/comm/as2/partner/gui/event/ResourceBundlePartnerEvent.java 6     4.01.21 9:48 Heller $
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
 * @version $Revision: 6 $
 */
public class ResourceBundlePartnerEvent extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"type." + PartnerEventInformation.TYPE_ON_RECEIPT, "on message receipt"},
        {"type." + PartnerEventInformation.TYPE_ON_SENDERROR, "on send error"},
        {"type." + PartnerEventInformation.TYPE_ON_SENDSUCCESS, "on send success"},
        {"title.select.process", "Please select a new process as event ({0})" },
        {"tab.newprocess", "Available postprocessing process types" },
        {"process.executeshell", "Execute shell command" },
        {"process.executeshell.description", "Execute a shell command or a batch script as postprocessing." },
        {"process.movetopartner", "Move to partner" },
        {"process.movetopartner.description", "Move the message to a partner for routing, e.g. from DMZ to ERP systems." },
        {"process.movetodirectory", "Move to directory" },
        {"process.movetodirectory.description", "Move the message to a defined directory" },
        {"button.ok", "Ok" },
        {"button.cancel", "Cancel" },
        {"title.configuration.shell", "Shell command setup [Partner {0}, {1}]"},
        {"title.configuration.movetodir", "Move message to directory [Partner {0}, {1}]"},
        {"title.configuration.movetopartner", "Move message to remote partner [Partner {0}, {1}]"},
        {"label.shell.info", "<HTML>Please setup the shell command that should be executed in this event. Please remember that this is OS specific, it is redirected to the default shell of your OS.</HTML>"},
        {"label.shell.command", "Command ({0}): "},
        {"shell.hint.replacement." + PartnerEventInformation.TYPE_ON_RECEIPT, "<HTML>The following variables will be replaced by system values in this command before it is executed:<br><i>$'{'filename}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'originalfilename}</i></HTML>"},
        {"shell.hint.replacement." + PartnerEventInformation.TYPE_ON_SENDERROR, "<HTML>The following variables will be replaced by system values in this command before it is executed:<br><i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
        {"shell.hint.replacement." + PartnerEventInformation.TYPE_ON_SENDSUCCESS, "<HTML>The following variables will be replaced by system values in this command before it is executed:<br><i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
        {"shell.hint.samples", "<HTML><strong>Samples</strong><br>Windows: <i>cmd /c move \"$'{'filename}\" \"c:\\mydir\"</i><br>Linux: <i>mv \"$'{'filename}\" \"~/mydir/\"</i></HTML>"},
        {"label.movetodir.info", "<HTML>Please setup the server side directory where the message should be moved to.</HTML>"},        
        {"label.movetodir.targetdir", "Target dir ({0}): "},
        {"label.movetodir.remotedir.select", "Please select the target directory on the server" },        
        {"label.movetopartner.info", "<HTML>Please select the remote partner the message should be forwarded to.</HTML>"},
        {"label.movetopartner", "Target partner: "},
        {"label.movetopartner.noroutingpartner", "<HTML>There is no remote partner available in the system to send messages to. Please add a partner to send messages to first.</HTML>"},
    };
    
}