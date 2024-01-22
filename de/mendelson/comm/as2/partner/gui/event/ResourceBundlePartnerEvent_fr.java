//$Header: /as2/de/mendelson/comm/as2/partner/gui/event/ResourceBundlePartnerEvent_fr.java 3     4.01.21 9:48 Heller $
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
 * @version $Revision: 3 $
 */
public class ResourceBundlePartnerEvent_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"type." + PartnerEventInformation.TYPE_ON_RECEIPT, "à la réception"},
        {"type." + PartnerEventInformation.TYPE_ON_SENDERROR, "après l''envoi (erreur)"},
        {"type." + PartnerEventInformation.TYPE_ON_SENDSUCCESS, "après l'envoi (succès)"},
        {"title.select.process", "Veuillez sélectionner un nouveau processus comme événement ({0})" },
        {"tab.newprocess", "Processus disponibles pour le post-traitement" },
        {"process.executeshell", "Exécution d''un ordre d''obus" },
        {"process.executeshell.description", "Exécutez une commande ou un script shell pour le post-traitement des données." },
        {"process.movetopartner", "Transmission aux partenaires" },
        {"process.movetopartner.description", "Transmission à un partenaire, par exemple de la DMZ vers le système ERP" },
        {"process.movetodirectory", "Aller au répertoire" },
        {"process.movetodirectory.description", "Déplacer les données vers un autre répertoire" },
        {"button.ok", "Ok" },
        {"button.cancel", "Annuler" },
        {"title.configuration.shell", "Configuration de la commande shell [Partenaire {0}, {1}]"},
        {"title.configuration.movetodir", "Déplacer les messages vers le répertoire [Partenaire {0}, {1}]"},
        {"title.configuration.movetopartner", "Transmission de données à un partenaire [Partenaire {0}, {1}]"},
        {"label.shell.info", "<HTML>Veuillez configurer la commande shell à exécuter dans ce cas. N'oubliez pas que cette fonction est spécifique au système d'exploitation, elle redirigera vers le shell par défaut de votre système d'exploitation.</HTML>"},
        {"label.shell.command", "Commande ({0}): "},
        {"shell.hint.replacement." + PartnerEventInformation.TYPE_ON_RECEIPT, "<HTML>Les variables suivantes sont remplacées par des valeurs système dans cette commande avant qu''elle ne soit exécutée:<br><i>$'{'filename}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'originalfilename}</i></HTML>"},
        {"shell.hint.replacement." + PartnerEventInformation.TYPE_ON_SENDERROR, "<HTML>DLes variables suivantes sont remplacées par des valeurs système dans cette commande avant qu''elle ne soit exécutée:<br><i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
        {"shell.hint.replacement." + PartnerEventInformation.TYPE_ON_SENDSUCCESS, "<HTML>Les variables suivantes sont remplacées par des valeurs système dans cette commande avant qu''elle ne soit exécutée:<br><i>$'{'filename}, $'{'fullstoragefilename}, $'{'log}, $'{'subject},$'{'sender}, $'{'receiver}, $'{'messageid}, $'{'mdntext}, $'{'userdefinedid}</i></HTML>"},
        {"shell.hint.samples", "<HTML><strong>Exemples</strong><br>Windows: <i>cmd /c move \"$'{'filename}\" \"c:\\mydir\"</i><br>Linux: <i>mv \"$'{'filename}\" \"~/mydir/\"</i></HTML>"},
        {"label.movetodir.info", "<HTML>Veuillez configurer le répertoire côté serveur dans lequel le message doit être déplacé.</HTML>"},        
        {"label.movetodir.targetdir", "Répertoire cible ({0}): "},
        {"label.movetodir.remotedir.select", "Veuillez sélectionner le répertoire cible sur le serveur" },        
        {"label.movetopartner.info", "<HTML>Veuillez sélectionner le partenaire à distance auquel le message doit être transmis.</HTML>"},
        {"label.movetopartner", "Partenaires cibles: "},
        {"label.movetopartner.noroutingpartner", "<HTML>Il n''y a pas de partenaire à distance disponible dans le système auquel les messages peuvent être envoyés. Veuillez d''abord ajouter un partenaire auquel les messages doivent être envoyés.</HTML>"},
    };
    
}