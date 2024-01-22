//$Header: /as2/de/mendelson/comm/as2/partner/gui/ResourceBundlePartnerConfig_fr.java 11    26/08/22 14:17 Heller $
package de.mendelson.comm.as2.partner.gui;
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
 * @author E.Pailleau
 * @version $Revision: 11 $
 */
public class ResourceBundlePartnerConfig_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Configuration des partenaires" },
        {"button.ok", "Valider" },
        {"button.cancel", "Annuler" },
        {"button.new", "Nouveau" },
        {"button.delete", "Supprimer" },
        {"button.globalchange", "Global" },
        {"nolocalstation.message", "Un partenaire au moins doit être défini comme station locale." },
        {"nolocalstation.title", "Aucune station locale" },
        {"localstation.noprivatekey.message", "Merci d''affecter une clef privée à une des stations locale." },
        {"localstation.noprivatekey.title", "Aucune clef affectée" },
        {"dialog.partner.delete.message", "Vous êtes sur le point de supprimer le partenaire \"{0}\" de la configuration.\nToute les données concernant le partenaire \"{0}\" seront perdues.\n\nVoulez-vous vraiment supprimer le partenaire \"{0}\"?" },
        {"dialog.partner.delete.title", "Suppression de partenaire" },
        {"dialog.partner.deletedir.message","L'associé \"{0}\" a été supprimé. L'annuaire être à la base\n\"{1}\"\ndevrait-il être supprimé sur le disque dur, aussi?"},
        {"dialog.partner.deletedir.title", "Annuaire de message d'associé de suppression" },
        {"dialog.partner.renamedir.message", "L'associé \"{0}\" a été retitré à \"{1}\". L'annuaire être à la base \"{2}\" devrait-il être retitré sur le disque dur, aussi?" },
        {"dialog.partner.renamedir.title", "Retitrez l'annuaire de message d'associé" },
        {"directory.rename.failure", "Incapable de retitrer \"{0}\" à \"{1}\"." },
        {"directory.rename.success", "L'annuaire \"{0}\" a été retitré à \"{1}\"." },
        {"directory.delete.failure", "Incapable de supprimer \"{0}\": [\"{1}\"]" },
        {"directory.delete.success", "L'annuaire \"{0}\" a été supprimé." },
        {"saving", "Enregistrement..." },
        {"module.locked", "La gestion des partenaires est verrouillé par un autre client, vous n''êtes pas autorisé à valider vos modifications!" },
        {"event.partner.deleted.subject", "Le partenaire {0} a été supprimé de la gestion des partenaires par l''utilisateur." },
        {"event.partner.deleted.body", "Données du partenaire supprimé:\n\n{0}" },
        {"event.partner.deleted.subject", "Le partenaire {0} a été supprimé de la gestion des partenaires par l''utilisateur." },
        {"event.partner.deleted.body", "Données du partenaire supprimé:\n\n{0}" },
        {"event.partner.added.subject", "Le partenaire {0} a été ajouté par l'utilisateur à la gestion des partenaires." },
        {"event.partner.added.body", "Données du nouveau partenaire:\n\n{0}" },
        {"event.partner.modified.subject", "Le partenaire {0} a été modifié par l''utilisateur" },
        {"event.partner.modified.body", "Données antérieures du partenaire:\n\n{0}\n\nDonnées sur les nouveaux partenaires:\n\n{1}" },        
    };
    
}
